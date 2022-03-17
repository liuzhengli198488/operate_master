package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.gys.common.constant.CommonConstant;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.kylin.RowMapper;
import com.gys.entity.*;
import com.gys.mapper.GaiaSdReplenishComparedDMapper;
import com.gys.service.ReplenishDiffSumService;
import com.gys.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  @author SunJiaNan
 *  @version 1.0
 *  @date 2021/11/25 10:13
 *  @description 门店补货公式汇总报表
 */
@Service
@Slf4j
public class ReplenishDiffSumServiceImpl implements ReplenishDiffSumService {

    @Autowired
    private GaiaSdReplenishComparedDMapper replenishComparedDMapper;
    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;
    @Autowired
    private CosUtils cosUtils;


    @Override
    public JsonResult getReplenishDiffSumList(ReplenishDiffSumInData inData) {
        String type = inData.getType();         //报表类型  1-日报 2-周报 3-月报
        String beginDate = inData.getBeginDate();
        String endDate = inData.getEndDate();
        //校验参数
        if(ValidateUtil.isEmpty(type)) {
            throw new BusinessException("提示：请选择报表类型！");
        }
        if(!"1".equals(type) && !"2".equals(type) && !"3".equals(type)) {
            throw new BusinessException("提示：报表类型错误，请重新确认！");
        }
        if(ValidateUtil.isEmpty(beginDate)) {
            throw new BusinessException("提示：请选择起始日期！");
        }
        if(ValidateUtil.isEmpty(endDate)) {
            throw new BusinessException("提示：请选择结束日期！");
        }
        //计算起始日期与结束日期相差天数
        long diffDays = DateUtil.getDiffByDate(beginDate, endDate);
        if(diffDays > 366) {
            throw new BusinessException("提示：选择日期范围不能超过一年！");
        }
        inData.setBeginDate(DateUtil.dateStrFormatExt(beginDate));
        inData.setEndDate(DateUtil.dateStrFormatExt(endDate));

        //获取地区列表
        List<AreaOutData> areaList = replenishComparedDMapper.getAreaList();
        //将list转为map  key为地区编号 value为地区名称
        Map<String, String> areaNameMap = areaList.stream().collect(Collectors.toMap(AreaOutData::getAreaId, AreaOutData::getAreaName));
        //获取加盟商列表
        List<GaiaFranchisee> clientList = replenishComparedDMapper.getAllClient();
        //将list转为map  key为加盟商编号 value为加盟商名称
        Map<String, String> clientMap = clientList.stream().collect(Collectors.toMap(GaiaFranchisee::getClient, GaiaFranchisee::getFrancName));

        String queryStr = null;
        //获取查询sql  报表类型  1-日报 2-周报 3-月报
        if("1".equals(type)) {
            queryStr = getQuerySqlByDateForKylin(inData);
        } else {
            queryStr = getQuerySqlByWeekOrMonthForKylin(inData);
        }
        //queryStr = queryStr + " ORDER BY resultFirst.dateStr, resultFirst.provId, resultFirst.cityId, resultFirst.client, resultFirst.patternType DESC ";     //排序

        log.info("执行补货公式差异sql语句：" + queryStr);
        List<ReplenishDiffSumKylinOutData> replenishDiffSumListForKylin = kylinJdbcTemplate.query(queryStr, RowMapper.getDefault(ReplenishDiffSumKylinOutData.class));   //查询麒麟数据
        log.info("执行补货公式差异查询结果-kylin：" + JSONObject.toJSONString(replenishDiffSumListForKylin));
        //List<ReplenishDiffSumOutData> replenishDiffSumListForMysql = replenishComparedDMapper.getReplenishDiffSumList(inData);      //查询mysql数据  弃用
        //log.info("执行补货公式差异查询结果-mysql：" + JSONObject.toJSONString(replenishDiffSumListForMysql));

        //出参列表
        List<ReplenishDiffSumOutData> replenishDiffSumList = new ArrayList<>(replenishDiffSumListForKylin.size());
        ReplenishDiffSumOutData tempObj = null;
        //处理数据 加盟商 省名称 市名称
        for(ReplenishDiffSumKylinOutData replenishDiffSum : replenishDiffSumListForKylin) {
            //当类型为日报时，将yyyyMMdd 处理为yyyy年MM月dd日
            if("1".equals(type)) {
                String dateStr = replenishDiffSum.getDateStr();           //日期
                String formatDateStr = DateUtil.dateStrFormat(dateStr);   //将日期格式格式化为自定义的类型
                replenishDiffSum.setDateStr(formatDateStr);
            }

            String client = replenishDiffSum.getClient();    //加盟商编号
            String provId = replenishDiffSum.getProvId();    //省编号
            String cityId = replenishDiffSum.getCityId();    //市编号
            replenishDiffSum.setClientName((ValidateUtil.isNotEmpty(clientMap.get(client))) ? clientMap.get(client) : "");
            replenishDiffSum.setProvName((ValidateUtil.isNotEmpty(areaNameMap.get(provId))) ? areaNameMap.get(provId) : "");
            replenishDiffSum.setCityName((ValidateUtil.isNotEmpty(areaNameMap.get(cityId))) ? areaNameMap.get(cityId) : "");

            //创建临时对象 进行属性拷贝 添加到新list中
            tempObj = new ReplenishDiffSumOutData();
            BeanUtil.copyProperties(replenishDiffSum, tempObj);
            replenishDiffSumList.add(tempObj);
        }
        //计算汇总
        ReplenishDiffSumTotalOutData total = getTotalInfo(replenishDiffSumList);
        //出参
        Map<String, Object> result = new HashMap<>(2);
        result.put("list", replenishDiffSumList);
        result.put("listNum", total);
        return JsonResult.success(result, "success");
    }


    /**
     * 查询语句-日报
     * @param inData
     * @return
     */
    private String getQuerySqlByDateForKylin(ReplenishDiffSumInData inData) {
        //获取查询条件
        String queryConditionStr = getQueryCondition(inData);
        //构建查询
        StringBuilder queryBuilder = new StringBuilder("");
        queryBuilder.append(" SELECT ");
        queryBuilder.append(" resultFirst.dateStr, ");
        queryBuilder.append(" resultFirst.provId, ");
        queryBuilder.append(" resultFirst.cityId, ");
        queryBuilder.append(" resultFirst.client, ");
        queryBuilder.append(" resultFirst.patternType, ");
        queryBuilder.append(" resultSecond.replenishStoreCount replenishStoreCount, ");
        queryBuilder.append(" ROUND(CAST(resultFirst.oldReplenishProCount AS DECIMAL) / CAST(resultSecond.replenishStoreCount AS DECIMAL ), 0) singleReplenishCount, ");
        queryBuilder.append(" ROUND( CAST(( resultFirst.addCount + resultFirst.equalCount + resultFirst.editCount )AS DECIMAL ) / CAST(resultSecond.replenishStoreCount AS DECIMAL ), 0 ) singleActualReplenishCount, ");
        queryBuilder.append(" resultFirst.oldReplenishProCount oldReplenishProCount, ");
        queryBuilder.append(" resultFirst.addCount addCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.addCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) addProportion, ");
        queryBuilder.append(" resultFirst.deleteCount deleteCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.deleteCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) deleteProportion, ");
        queryBuilder.append(" resultFirst.equalCount equalCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.equalCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) equalProportion, ");
        queryBuilder.append(" resultFirst.editCount editCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.editCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) editProportion, ");
        queryBuilder.append(" ROUND(resultFirst.retailSaleAmt / 10000, 2)  retailSaleAmt, ");
        queryBuilder.append(" ROUND(resultFirst.actualretailSaleAmt / 10000, 2)  actualretailSaleAmt, ");
        queryBuilder.append(" ROUND((resultFirst.retailSaleAmt - resultFirst.actualretailSaleAmt) / 10000, 2)  diffAmt, ");
        queryBuilder.append(" ROUND(resultFirst.costAmt / 10000, 2) costAmt, ");
        queryBuilder.append(" ROUND(resultFirst.actualCostAmt / 10000, 2)  actualCostAmt, ");
        queryBuilder.append(" ROUND((resultFirst.costAmt - resultFirst.actualCostAmt) / 10000, 2) diffCostAmt ");
        queryBuilder.append(" FROM ( SELECT resultData.dateStr, resultData.provId, resultData.cityId, resultData.client, resultData.patternType, COUNT( resultData.proId ) oldReplenishProCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.addCount IS NULL THEN 0 ELSE resultData.addCount END ) addCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.deleteCount IS NULL THEN 0 ELSE resultData.deleteCount END ) deleteCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.equalCount IS NULL THEN 0 ELSE resultData.equalCount END ) equalCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.editCount IS NULL THEN 0 ELSE resultData.editCount END ) editCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.retailSaleAmt IS NULL THEN 0 ELSE resultData.retailSaleAmt END ) retailSaleAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.actualretailSaleAmt IS NULL THEN 0 ELSE resultData.actualretailSaleAmt END ) actualretailSaleAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.costAmt IS NULL THEN 0 ELSE resultData.costAmt END ) costAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.actualCostAmt IS NULL THEN 0 ELSE resultData.actualCostAmt END ) actualCostAmt ");
        queryBuilder.append(" FROM ( SELECT computeData.dateStr, computeData.provId, computeData.cityId, computeData.client, ");
        queryBuilder.append(" CASE WHEN computeData.patternType = '0' THEN '正常补货' WHEN computeData.patternType = '1' THEN '紧急补货' WHEN computeData.patternType = '2' THEN '铺货' WHEN computeData.patternType = '3' THEN '互调' WHEN computeData.patternType = '4' THEN '直配' ELSE computeData.patternType END patternType, ");
        queryBuilder.append(" computeData.brId, computeData.voucherId, computeData.proId,  ");
        queryBuilder.append(" SUM( computeData.addCount ) addCount, SUM( computeData.deleteCount ) deleteCount, SUM( computeData.equalCount ) equalCount, SUM( computeData.editCount ) editCount, ");
        queryBuilder.append(" SUM( computeData.proposeQty ) proposeQty, SUM( computeData.needQty ) needQty, ");
        queryBuilder.append(" SUM( computeData.normalPrice ) * SUM( computeData.proposeQty ) retailSaleAmt, ");
        queryBuilder.append(" SUM( computeData.normalPrice ) * SUM( computeData.needQty ) actualretailSaleAmt, ");
        queryBuilder.append(" SUM( computeData.movPrice ) * SUM( computeData.taxValue ) * SUM( computeData.proposeQty ) costAmt, ");
        queryBuilder.append(" SUM( computeData.movPrice ) * SUM( computeData.taxValue ) * SUM( computeData.needQty ) actualCostAmt  ");
        queryBuilder.append(" FROM ( SELECT baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType, baseData.brId, baseData.voucherId, baseData.proId, ");
        queryBuilder.append(" SUM( baseData.addCount ) addCount, SUM( baseData.deleteCount ) deleteCount, SUM( baseData.equalCount ) equalCount, SUM( baseData.editCount ) editCount, SUM( baseData.proposeQty ) proposeQty, ");
        queryBuilder.append(" SUM( baseData.needQty ) needQty, SUM( baseData.normalPrice ) normalPrice, SUM( baseData.movPrice ) movPrice, SUM( baseData.taxValue ) taxValue ");
        queryBuilder.append(" FROM ( SELECT rcd.GSRD_DATE dateStr, fran.FRANC_PROV provId, fran.FRANC_CITY cityId, rcd.CLIENT client, rh.GSRH_PATTERN patternType, rcd.GSRD_BR_ID brId, rcd.GSRD_VOUCHER_ID voucherId, ");
        queryBuilder.append(" rcd.GSRD_PRO_ID proId, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'A' THEN 1 ELSE 0 END addCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'D' THEN 1 ELSE 0 END deleteCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS IS NULL OR rcd.GSRD_STATUS = '' THEN 1 ELSE 0 END equalCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'M' THEN 1 ELSE 0 END editCount, ");
        queryBuilder.append(" SUM( rcd.GSRD_PROPOSE_QTY ) proposeQty, SUM( rcd.GSRD_NEED_QTY ) needQty, SUM( pp.GSPP_PRICE_NORMAL ) normalPrice, cost.MAT_MOV_PRICE movPrice, cost.TAX_CODE_VALUE taxValue ");
        queryBuilder.append(" FROM GAIA_SD_REPLENISH_COMPARED_D rcd LEFT JOIN GAIA_SD_REPLENISH_ORI_D rod ON ( rcd.CLIENT = rod.CLIENT AND rcd.GSRD_BR_ID = rod.GSRD_BR_ID AND rcd.GSRD_VOUCHER_ID = rod.GSRD_VOUCHER_ID AND rcd.GSRD_PRO_ID = rod.GSRD_PRO_ID AND rcd.GSRD_DATE = rod.GSRD_DATE) ");
        queryBuilder.append(" LEFT JOIN GAIA_SD_PRODUCT_PRICE pp ON ( rcd.CLIENT = pp.CLIENT AND rcd.GSRD_BR_ID = pp.GSPP_BR_ID AND rcd.GSRD_PRO_ID = pp.GSPP_PRO_ID ) ");
        queryBuilder.append(" LEFT JOIN GAIA_SD_REPLENISH_H rh ON ( rcd.CLIENT = rh.CLIENT AND rcd.GSRD_BR_ID = rh.GSRH_BR_ID AND rcd.GSRD_VOUCHER_ID = rh.GSRH_VOUCHER_ID AND rcd.GSRD_DATE = rh.GSRH_DATE ) ");
        queryBuilder.append(" LEFT JOIN ( ");
        queryBuilder.append(" SELECT ass.CLIENT, ass.MAT_PRO_CODE, SUM( ass.MAT_MOV_PRICE ) AS MAT_MOV_PRICE, ( 1 + cast( REPLACE ( tax.TAX_CODE_VALUE, '%', '' ) AS DECIMAL ) / 100 ) TAX_CODE_VALUE ");
        queryBuilder.append(" FROM GAIA_MATERIAL_ASSESS ass INNER JOIN GAIA_PRODUCT_BUSINESS pb ON ( ass.CLIENT = pb.CLIENT AND ass.MAT_ASSESS_SITE = pb.PRO_SITE AND ass.MAT_PRO_CODE = pb.PRO_SELF_CODE ) ");
        queryBuilder.append(" LEFT JOIN GAIA_TAX_CODE tax ON ( tax.TAX_CODE = pb.PRO_INPUT_TAX ) INNER JOIN GAIA_DC_DATA dc ON ( ass.CLIENT = dc.CLIENT AND ass.MAT_ASSESS_SITE = dc.DC_CODE )  ");
        queryBuilder.append(" WHERE tax.TAX_CODE_CLASS = '1' GROUP BY ass.CLIENT, ass.MAT_PRO_CODE, tax.TAX_CODE_VALUE  ");
        queryBuilder.append(" ) cost ON ( rcd.CLIENT = cost.CLIENT AND rcd.GSRD_PRO_ID = cost.MAT_PRO_CODE ) LEFT JOIN GAIA_FRANCHISEE fran ON ( rcd.CLIENT = fran.CLIENT )  ");
        queryBuilder.append(" LEFT JOIN GAIA_CAL_DT dt ON ( rcd.GSRD_DATE = dt.GCD_DATE ) ");
        queryBuilder.append(queryConditionStr);         //拼接条件
        queryBuilder.append(" GROUP BY rcd.GSRD_DATE, fran.FRANC_PROV, fran.FRANC_CITY, rcd.CLIENT, rh.GSRH_PATTERN, rcd.GSRD_BR_ID, rcd.GSRD_VOUCHER_ID, rcd.GSRD_PRO_ID, rcd.GSRD_STATUS, cost.MAT_MOV_PRICE, cost.TAX_CODE_VALUE ");
        queryBuilder.append(" ) baseData  ");
        queryBuilder.append(" GROUP BY baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType, baseData.brId, baseData.voucherId, baseData.proId ) computeData ");
        queryBuilder.append(" GROUP BY computeData.dateStr, computeData.provId, computeData.cityId, computeData.client, computeData.patternType, computeData.brId, computeData.voucherId, computeData.proId ) resultData ");
        queryBuilder.append(" GROUP BY resultData.dateStr, resultData.provId, resultData.cityId, resultData.client, resultData.patternType ) resultFirst ");
        queryBuilder.append(" LEFT JOIN ( SELECT baseData.dateStr dateStr, ");
        queryBuilder.append(" baseData.provId provId, baseData.cityId cityId, baseData.client client, baseData.patternType patternType, SUM(baseData.replenishStoreCount) replenishStoreCount ");
        queryBuilder.append(" FROM (SELECT tmp.dateStr dateStr, tmp.provId provId, tmp.cityId cityId, tmp.client client, tmp.patternType patternType, COUNT(tmp.brId) replenishStoreCount ");
        queryBuilder.append(" FROM ( SELECT rcd.GSRD_DATE dateStr, fran.FRANC_PROV provId, fran.FRANC_CITY cityId, rcd.CLIENT client, rcd.GSRD_BR_ID brId, ");
        queryBuilder.append(" CASE WHEN rh.GSRH_PATTERN = '0' THEN '正常补货' WHEN rh.GSRH_PATTERN = '1' THEN '紧急补货' WHEN rh.GSRH_PATTERN = '2' THEN '铺货' WHEN rh.GSRH_PATTERN = '3' THEN '互调' WHEN rh.GSRH_PATTERN = '4' THEN '直配' ELSE rh.GSRH_PATTERN END patternType ");
        queryBuilder.append(" FROM GAIA_SD_REPLENISH_COMPARED_D rcd LEFT JOIN GAIA_SD_REPLENISH_H rh ON ( rcd.CLIENT = rh.CLIENT AND rcd.GSRD_BR_ID = rh.GSRH_BR_ID AND rcd.GSRD_VOUCHER_ID = rh.GSRH_VOUCHER_ID AND rcd.GSRD_DATE = rh.GSRH_DATE ) ");
        queryBuilder.append(" LEFT JOIN GAIA_FRANCHISEE fran ON ( rcd.CLIENT = fran.CLIENT ) ");
        queryBuilder.append(" LEFT JOIN GAIA_CAL_DT dt ON ( rcd.GSRD_DATE = dt.GCD_DATE ) ");
        queryBuilder.append(queryConditionStr);         //拼接条件
        queryBuilder.append(" GROUP BY rcd.GSRD_DATE, fran.FRANC_PROV, fran.FRANC_CITY, rcd.CLIENT, rcd.GSRD_BR_ID, rh.GSRH_PATTERN ");
        queryBuilder.append(" ) tmp GROUP BY tmp.dateStr, tmp.provId, tmp.cityId, tmp.client, tmp.patternType ");
        queryBuilder.append(" ) baseData GROUP BY baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType ");
        queryBuilder.append(" ) resultSecond ON ( resultFirst.dateStr = resultSecond.dateStr AND resultFirst.provId = resultSecond.provId AND resultFirst.cityId = resultSecond.cityId AND resultFirst.client = resultSecond.client ");
        queryBuilder.append(" AND resultFirst.patternType = resultSecond.patternType ) ");
        return queryBuilder.toString();
    }



    /**
     * 查询语句-周报，月报
     * @param inData
     * @return
     */
    private String getQuerySqlByWeekOrMonthForKylin(ReplenishDiffSumInData inData) {
        //报表类型    1-日报 2-周报 3-月报
        String type = inData.getType();
        //获取查询条件
        String queryConditionStr = getQueryCondition(inData);
        //获取分组条件
        String[] groupArr = getGroupByType(type);

        //构建查询
        StringBuilder queryBuilder = new StringBuilder("");
        queryBuilder.append(" SELECT ");
        queryBuilder.append(" resultFirst.dateStr, ");
        queryBuilder.append(" resultFirst.provId, ");
        queryBuilder.append(" resultFirst.cityId, ");
        queryBuilder.append(" resultFirst.client, ");
        queryBuilder.append(" resultFirst.patternType, ");
        queryBuilder.append(" resultSecond.replenishStoreCount replenishStoreCount, ");
        queryBuilder.append(" ROUND(CAST(resultFirst.oldReplenishProCount AS DECIMAL) / CAST(resultSecond.replenishStoreCount AS DECIMAL ), 0) singleReplenishCount, ");
        queryBuilder.append(" ROUND( CAST(( resultFirst.addCount + resultFirst.equalCount + resultFirst.editCount )AS DECIMAL ) / CAST(resultSecond.replenishStoreCount AS DECIMAL ), 0 ) singleActualReplenishCount, ");
        queryBuilder.append(" resultFirst.oldReplenishProCount oldReplenishProCount, ");
        queryBuilder.append(" resultFirst.addCount addCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.addCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) addProportion, ");
        queryBuilder.append(" resultFirst.deleteCount deleteCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.deleteCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) deleteProportion, ");
        queryBuilder.append(" resultFirst.equalCount equalCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.equalCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) equalProportion, ");
        queryBuilder.append(" resultFirst.editCount editCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.editCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) editProportion, ");
        queryBuilder.append(" ROUND(resultFirst.retailSaleAmt / 10000, 2)  retailSaleAmt, ");
        queryBuilder.append(" ROUND(resultFirst.actualretailSaleAmt / 10000, 2)  actualretailSaleAmt, ");
        queryBuilder.append(" ROUND((resultFirst.retailSaleAmt - resultFirst.actualretailSaleAmt) / 10000, 2)  diffAmt, ");
        queryBuilder.append(" ROUND(resultFirst.costAmt / 10000, 2) costAmt, ");
        queryBuilder.append(" ROUND(resultFirst.actualCostAmt / 10000, 2)  actualCostAmt, ");
        queryBuilder.append(" ROUND((resultFirst.costAmt - resultFirst.actualCostAmt) / 10000, 2) diffCostAmt ");
        queryBuilder.append(" FROM ( SELECT resultData.dateStr, resultData.provId, resultData.cityId, resultData.client, resultData.patternType, COUNT( resultData.proId ) oldReplenishProCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.addCount IS NULL THEN 0 ELSE resultData.addCount END ) addCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.deleteCount IS NULL THEN 0 ELSE resultData.deleteCount END ) deleteCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.equalCount IS NULL THEN 0 ELSE resultData.equalCount END ) equalCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.editCount IS NULL THEN 0 ELSE resultData.editCount END ) editCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.retailSaleAmt IS NULL THEN 0 ELSE resultData.retailSaleAmt END ) retailSaleAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.actualretailSaleAmt IS NULL THEN 0 ELSE resultData.actualretailSaleAmt END ) actualretailSaleAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.costAmt IS NULL THEN 0 ELSE resultData.costAmt END ) costAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.actualCostAmt IS NULL THEN 0 ELSE resultData.actualCostAmt END ) actualCostAmt ");
        queryBuilder.append(" FROM ( SELECT computeData.dateStr, computeData.provId, computeData.cityId, computeData.client, ");
        queryBuilder.append(" CASE WHEN computeData.patternType = '0' THEN '正常补货' WHEN computeData.patternType = '1' THEN '紧急补货' WHEN computeData.patternType = '2' THEN '铺货' WHEN computeData.patternType = '3' THEN '互调' WHEN computeData.patternType = '4' THEN '直配' ELSE computeData.patternType END patternType, ");
        queryBuilder.append(" computeData.brId, computeData.voucherId, computeData.proId,  ");
        queryBuilder.append(" SUM( computeData.addCount ) addCount, SUM( computeData.deleteCount ) deleteCount, SUM( computeData.equalCount ) equalCount, SUM( computeData.editCount ) editCount, ");
        queryBuilder.append(" SUM( computeData.proposeQty ) proposeQty, SUM( computeData.needQty ) needQty, ");
        queryBuilder.append(" SUM( computeData.normalPrice ) * SUM( computeData.proposeQty ) retailSaleAmt, ");
        queryBuilder.append(" SUM( computeData.normalPrice ) * SUM( computeData.needQty ) actualretailSaleAmt, ");
        queryBuilder.append(" SUM( computeData.movPrice ) * SUM( computeData.taxValue ) * SUM( computeData.proposeQty ) costAmt, ");
        queryBuilder.append(" SUM( computeData.movPrice ) * SUM( computeData.taxValue ) * SUM( computeData.needQty ) actualCostAmt  ");
        queryBuilder.append(" FROM ( SELECT baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType, baseData.brId, baseData.voucherId, baseData.proId, ");
        queryBuilder.append(" SUM( baseData.addCount ) addCount, SUM( baseData.deleteCount ) deleteCount, SUM( baseData.equalCount ) equalCount, SUM( baseData.editCount ) editCount, SUM( baseData.proposeQty ) proposeQty, ");
        queryBuilder.append(" SUM( baseData.needQty ) needQty, SUM( baseData.normalPrice ) normalPrice, SUM( baseData.movPrice ) movPrice, SUM( baseData.taxValue ) taxValue ");
        queryBuilder.append(" FROM ( SELECT ");
        queryBuilder.append(groupArr[0]);     //dt.GCD_YEAR + '年' + dt.GCD_WEEK + '周' dateStr,
        queryBuilder.append(" fran.FRANC_PROV provId, fran.FRANC_CITY cityId, rcd.CLIENT client, rh.GSRH_PATTERN patternType, rcd.GSRD_BR_ID brId, rcd.GSRD_VOUCHER_ID voucherId, rcd.GSRD_PRO_ID proId, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'A' THEN 1 ELSE 0 END addCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'D' THEN 1 ELSE 0 END deleteCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS IS NULL OR rcd.GSRD_STATUS = '' THEN 1 ELSE 0 END equalCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'M' THEN 1 ELSE 0 END editCount, ");
        queryBuilder.append(" SUM( rcd.GSRD_PROPOSE_QTY ) proposeQty, SUM( rcd.GSRD_NEED_QTY ) needQty, SUM( pp.GSPP_PRICE_NORMAL ) normalPrice, cost.MAT_MOV_PRICE movPrice, cost.TAX_CODE_VALUE taxValue ");
        queryBuilder.append(" FROM GAIA_SD_REPLENISH_COMPARED_D rcd LEFT JOIN GAIA_SD_REPLENISH_ORI_D rod ON ( rcd.CLIENT = rod.CLIENT AND rcd.GSRD_BR_ID = rod.GSRD_BR_ID AND rcd.GSRD_VOUCHER_ID = rod.GSRD_VOUCHER_ID AND rcd.GSRD_PRO_ID = rod.GSRD_PRO_ID AND rcd.GSRD_DATE = rod.GSRD_DATE) ");
        queryBuilder.append(" LEFT JOIN GAIA_SD_PRODUCT_PRICE pp ON ( rcd.CLIENT = pp.CLIENT AND rcd.GSRD_BR_ID = pp.GSPP_BR_ID AND rcd.GSRD_PRO_ID = pp.GSPP_PRO_ID ) ");
        queryBuilder.append(" LEFT JOIN GAIA_SD_REPLENISH_H rh ON ( rcd.CLIENT = rh.CLIENT AND rcd.GSRD_BR_ID = rh.GSRH_BR_ID AND rcd.GSRD_VOUCHER_ID = rh.GSRH_VOUCHER_ID AND rcd.GSRD_DATE = rh.GSRH_DATE ) ");
        queryBuilder.append(" LEFT JOIN ( ");
        queryBuilder.append(" SELECT ass.CLIENT, ass.MAT_PRO_CODE, SUM( ass.MAT_MOV_PRICE ) AS MAT_MOV_PRICE, ( 1 + cast( REPLACE ( tax.TAX_CODE_VALUE, '%', '' ) AS DECIMAL ) / 100 ) TAX_CODE_VALUE ");
        queryBuilder.append(" FROM GAIA_MATERIAL_ASSESS ass INNER JOIN GAIA_PRODUCT_BUSINESS pb ON ( ass.CLIENT = pb.CLIENT AND ass.MAT_ASSESS_SITE = pb.PRO_SITE AND ass.MAT_PRO_CODE = pb.PRO_SELF_CODE ) ");
        queryBuilder.append(" LEFT JOIN GAIA_TAX_CODE tax ON ( tax.TAX_CODE = pb.PRO_INPUT_TAX ) INNER JOIN GAIA_DC_DATA dc ON ( ass.CLIENT = dc.CLIENT AND ass.MAT_ASSESS_SITE = dc.DC_CODE )  ");
        queryBuilder.append(" WHERE tax.TAX_CODE_CLASS = '1' GROUP BY ass.CLIENT, ass.MAT_PRO_CODE, tax.TAX_CODE_VALUE  ");
        queryBuilder.append(" ) cost ON ( rcd.CLIENT = cost.CLIENT AND rcd.GSRD_PRO_ID = cost.MAT_PRO_CODE ) LEFT JOIN GAIA_FRANCHISEE fran ON ( rcd.CLIENT = fran.CLIENT )  ");
        queryBuilder.append(" LEFT JOIN GAIA_CAL_DT dt ON ( rcd.GSRD_DATE = dt.GCD_DATE )  ");
        queryBuilder.append(queryConditionStr);         //拼接条件
        queryBuilder.append(" GROUP BY dateStr, fran.FRANC_PROV, fran.FRANC_CITY, rcd.CLIENT, rh.GSRH_PATTERN, rcd.GSRD_BR_ID, rcd.GSRD_VOUCHER_ID, rcd.GSRD_PRO_ID, rcd.GSRD_STATUS, cost.MAT_MOV_PRICE, cost.TAX_CODE_VALUE ");
        queryBuilder.append(" ) baseData  ");
        queryBuilder.append(" GROUP BY baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType, baseData.brId, baseData.voucherId, baseData.proId ) computeData ");
        queryBuilder.append(" GROUP BY computeData.dateStr, computeData.provId, computeData.cityId, computeData.client, computeData.patternType, computeData.brId, computeData.voucherId, computeData.proId ) resultData ");
        queryBuilder.append(" GROUP BY resultData.dateStr, resultData.provId, resultData.cityId, resultData.client, resultData.patternType ) resultFirst ");
        queryBuilder.append(" LEFT JOIN ( SELECT ");
        queryBuilder.append(groupArr[1]);       //baseData.years + '年' + baseData.weeks + '周' dateStr,
        queryBuilder.append(" baseData.provId provId, baseData.cityId cityId, baseData.client client, baseData.patternType patternType, SUM(baseData.replenishStoreCount) replenishStoreCount ");
        queryBuilder.append(" FROM ( SELECT tmp.years years, ");
        queryBuilder.append(groupArr[2]);      //tmp.weeks weeks,
        queryBuilder.append(" tmp.provId provId, tmp.cityId cityId, tmp.client client, tmp.patternType patternType, COUNT( tmp.brId ) replenishStoreCount ");
        queryBuilder.append(" FROM ( SELECT dt.GCD_YEAR years, ");
        queryBuilder.append(groupArr[3]);      //dt.GCD_YEAR, dt.GCD_WEEK,
        queryBuilder.append(" rcd.GSRD_DATE dates, fran.FRANC_PROV provId, fran.FRANC_CITY cityId, rcd.CLIENT client, rcd.GSRD_BR_ID brId, ");
        queryBuilder.append(" CASE WHEN rh.GSRH_PATTERN = '0' THEN '正常补货' WHEN rh.GSRH_PATTERN = '1' THEN '紧急补货' WHEN rh.GSRH_PATTERN = '2' THEN '铺货' WHEN rh.GSRH_PATTERN = '3' THEN '互调' WHEN rh.GSRH_PATTERN = '4' THEN '直配' ELSE rh.GSRH_PATTERN END patternType ");
        queryBuilder.append(" FROM GAIA_SD_REPLENISH_COMPARED_D rcd LEFT JOIN GAIA_SD_REPLENISH_H rh ON ( rcd.CLIENT = rh.CLIENT AND rcd.GSRD_BR_ID = rh.GSRH_BR_ID AND rcd.GSRD_VOUCHER_ID = rh.GSRH_VOUCHER_ID AND rcd.GSRD_DATE = rh.GSRH_DATE ) ");
        queryBuilder.append(" LEFT JOIN GAIA_FRANCHISEE fran ON ( rcd.CLIENT = fran.CLIENT ) ");
        queryBuilder.append(" LEFT JOIN GAIA_CAL_DT dt ON ( rcd.GSRD_DATE = dt.GCD_DATE ) ");
        queryBuilder.append(queryConditionStr);         //拼接条件
        queryBuilder.append(" GROUP BY ");
        queryBuilder.append(groupArr[4]);               //dt.GCD_YEAR, dt.GCD_WEEK,
        queryBuilder.append(" rcd.GSRD_DATE, fran.FRANC_PROV, fran.FRANC_CITY, rcd.CLIENT, rcd.GSRD_BR_ID, rh.GSRH_PATTERN ");
        queryBuilder.append(" ) tmp GROUP BY tmp.years, ");
        queryBuilder.append(groupArr[5]);               //tmp.weeks,
        queryBuilder.append(" tmp.provId, tmp.cityId, tmp.client, tmp.patternType ) baseData GROUP BY dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType ");
        queryBuilder.append(" ) resultSecond ON ( resultFirst.dateStr = resultSecond.dateStr AND resultFirst.provId = resultSecond.provId AND resultFirst.cityId = resultSecond.cityId AND resultFirst.client = resultSecond.client ");
        queryBuilder.append(" AND resultFirst.patternType = resultSecond.patternType ) ");
        return queryBuilder.toString();
    }



    /**
     * 获取查询条件
     * @param inData
     * @return
     */
    private String getQueryCondition(ReplenishDiffSumInData inData) {
        String beginDate = inData.getBeginDate();           //起始日期
        String endDate = inData.getEndDate();               //结束日期
        List<String> clientList = inData.getClientList();   //加盟商列表
        List<String> provinceList = inData.getProvinceList();   //省列表
        List<String> cityList = inData.getCityList();           //市列表
        List<String> patternList = inData.getPatternList();     //补货方式

        //条件
        StringBuilder queryCondition = new StringBuilder("");
        //起始日期 结束日期
        queryCondition.append(" WHERE ");
        queryCondition.append(" rcd.GSRD_DATE >= ");
        queryCondition.append("'");
        queryCondition.append(beginDate);
        queryCondition.append("'");
        queryCondition.append(" AND rcd.GSRD_DATE <= ");
        queryCondition.append("'");
        queryCondition.append(endDate);
        queryCondition.append("'");

        //如果加盟商列表不为空 则拼接条件
        if(ValidateUtil.isNotEmpty(clientList)) {
            queryCondition.append(" AND rcd.CLIENT IN ");
            String queryIndexCondition = getQueryIndexCondition(clientList);    //获取 IN 条件括号中的内容 追加到条件中
            queryCondition.append(queryIndexCondition);
        }
        //如果省列表不为空  则拼接条件
        if(ValidateUtil.isNotEmpty(provinceList)) {
            queryCondition.append(" AND fran.FRANC_PROV IN ");
            String queryIndexCondition = getQueryIndexCondition(provinceList);  //获取 IN 条件括号中的内容 追加到条件中
            queryCondition.append(queryIndexCondition);
        }
        //如果市列表不为空  则拼接条件
        if(ValidateUtil.isNotEmpty(cityList)) {
            queryCondition.append(" AND fran.FRANC_CITY IN ");
            String queryIndexCondition = getQueryIndexCondition(cityList);     //获取 IN 条件括号中的内容 追加到条件中
            queryCondition.append(queryIndexCondition);
        }
        //如果补货方式列表不为空  则拼接条件
        if(ValidateUtil.isNotEmpty(patternList)) {
            queryCondition.append(" AND rh.GSRH_PATTERN IN ");
            String queryIndexCondition = getQueryIndexCondition(patternList);   //获取 IN 条件括号中的内容 追加到条件中
            queryCondition.append(queryIndexCondition);
        }
        return queryCondition.toString();
    }



    /**
     * 处理in条件
     * @param indexConditionList
     * @return
     */
    private String getQueryIndexCondition(List<String> indexConditionList) {
        //in条件
        StringBuilder queryIndexCondition = new StringBuilder("");
        queryIndexCondition.append(" ( ");
        //遍历拼接sql
        for(int i = 0; i < indexConditionList.size(); i++) {
            String indexCondition = indexConditionList.get(i);
            queryIndexCondition.append("'");
            queryIndexCondition.append(indexCondition);

            //判断是否为最后一次拼接 如果是最后一次拼接 则不拼接逗号  否则每次循环拼接逗号
            if(i != indexConditionList.size() - 1) {
                queryIndexCondition.append("',");
            } else {
                queryIndexCondition.append("'");
            }

        }
        queryIndexCondition.append(" ) ");
        return queryIndexCondition.toString();
    }


    /**
     * 根据报表类型获取分组条件
     * @param type  2-周报 3-月报
     * @return
     */
    private String[] getGroupByType(String type) {
        String[] arr = new String[10];
        //2-周报
        if("2".equals(type)) {
            arr[0] = " dt.GCD_YEAR + '年' + dt.GCD_WEEK + '周' dateStr, ";
            arr[1] = " baseData.years + '年' + baseData.weeks + '周' dateStr, ";
            arr[2] = " tmp.weeks weeks, ";
            arr[3] = " dt.GCD_WEEK weeks, ";
            arr[4] = " dt.GCD_YEAR, dt.GCD_WEEK, ";
            arr[5] = " tmp.weeks, ";
        }

        //3-月报
        if("3".equals(type)) {
            arr[0] = " dt.GCD_YEAR + '年' + dt.GCD_MONTH + '月' dateStr, ";
            arr[1] = " baseData.years + '年' + baseData.months + '月' dateStr, ";
            arr[2] = " tmp.months months, ";
            arr[3] = " dt.GCD_MONTH months, ";
            arr[4] = " dt.GCD_YEAR, dt.GCD_MONTH, ";
            arr[5] = " tmp.months, ";
        }
        return arr;
    }


    /**
     * 计算汇总数据
     * @return
     */
    private ReplenishDiffSumTotalOutData getTotalInfo(List<ReplenishDiffSumOutData> replenishDiffSumList) {
        //补货门店数
        BigDecimal replenishStoreCount = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getReplenishStoreCount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //原单品项数
        BigDecimal oldReplenishProCount = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getOldReplenishProCount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //新增品项数
        BigDecimal addCount = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getAddCount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //删除品项数
        BigDecimal deleteCount = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getDeleteCount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //一致品项数
        BigDecimal equalCount = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getEqualCount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //修改品项数
        BigDecimal editCount = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getEditCount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //补货公式零售额
        BigDecimal retailSaleAmt = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getRetailSaleAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        //实际零售额
        BigDecimal actualRetailSaleAmt = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getActualRetailSaleAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        //零售额差异
        BigDecimal diffAmt = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getDiffAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        //补货公式成本额
        BigDecimal costAmt = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getCostAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        //实际成本额
        BigDecimal actualCostAmt = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getActualCostAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        //成本额差异
        BigDecimal diffCostAmt = replenishDiffSumList.stream().map(ReplenishDiffSumOutData::getDiffCostAmt).reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal ONE_HUNDRED = new BigDecimal(100);     //100
        final BigDecimal ZERO = BigDecimal.ZERO;                    //0
        final String suffix = "%";    //百分比后缀

        ReplenishDiffSumTotalOutData total = new ReplenishDiffSumTotalOutData();
        total.setReplenishStoreCount(replenishStoreCount);      //补货门店数
        total.setOldReplenishProCount(oldReplenishProCount);    //原单品项数
        total.setAddCount(addCount);                  //新增品项数
        total.setDeleteCount(deleteCount);            //删除品项数
        total.setEqualCount(equalCount);              //一致品项数
        total.setEditCount(editCount);                //修改品项数
        total.setRetailSaleAmt(retailSaleAmt);        //补货公式零售额
        total.setActualRetailSaleAmt(actualRetailSaleAmt);    //实际零售额
        total.setDiffAmt(diffAmt);               //零售额差异
        total.setCostAmt(costAmt);               //补货公式成本额
        total.setActualCostAmt(actualCostAmt);   //实际成本额
        total.setDiffCostAmt(diffCostAmt);       //成本额差异

        //判断补货门店数是否不为0  如果不为0 则做除法计算
        if(ZERO.compareTo(replenishStoreCount) != 0) {
            total.setSingleReplenishCount(oldReplenishProCount.divide(replenishStoreCount, 0, BigDecimal.ROUND_HALF_UP));            //单店单次原补货品项
            total.setSingleActualReplenishCount((addCount.add(equalCount).add(editCount)).divide(replenishStoreCount, 0, BigDecimal.ROUND_HALF_UP));   //单店单次实际补货品项
        } else {
            total.setSingleReplenishCount(ZERO);            //单店单次原补货品项
            total.setSingleActualReplenishCount(ZERO);      //单店单次实际补货品项
        }
        //判断原单品项数是否不为0  如果不为0 则做除法计算
        if(ZERO.compareTo(oldReplenishProCount) != 0) {
            total.setAddProportion(addCount.divide(oldReplenishProCount, 5, BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP) + suffix);          //新增品项数占比
            total.setDeleteProportion(deleteCount.divide(oldReplenishProCount, 5, BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP) + suffix);    //删除品项数占比
            total.setEqualProportion(equalCount.divide(oldReplenishProCount, 5, BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP) + suffix);      //一致品项数占比
            total.setEditProportion(editCount.divide(oldReplenishProCount, 5, BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP) + suffix);        //修改品项数占比
        } else {
            total.setAddProportion(ZERO.toString() + suffix);       //新增品项数占比
            total.setDeleteProportion(ZERO.toString() + suffix);    //删除品项数占比
            total.setEqualProportion(ZERO.toString() + suffix);     //一致品项数占比
            total.setEditProportion(ZERO.toString() + suffix);      //修改品项数占比
        }
        return total;
    }


    @Override
    public JsonResult exportReplenishDiffSum(List<ReplenishDiffSumOutData> replenishDiffSumList) {
        if(ValidateUtil.isEmpty(replenishDiffSumList)) {
            throw new BusinessException("提示：导出数据为空，无法导出！");
        }

        final String suffix = "%";    //百分比后缀
        //组装导出内容
        List<List<Object>> dataList = new ArrayList<>(replenishDiffSumList.size());
        for(ReplenishDiffSumOutData replenishDiffSum : replenishDiffSumList) {
            //每行数据
            List<Object> lineList = new ArrayList<>();
            //日期
            lineList.add(replenishDiffSum.getDateStr());
            //省
            lineList.add(replenishDiffSum.getProvName());
            //市
            lineList.add(replenishDiffSum.getCityName());
            //客户
            lineList.add(replenishDiffSum.getClient());
            //客户名称
            lineList.add(replenishDiffSum.getClientName());
            //补货方式
            lineList.add(replenishDiffSum.getPatternType());
            //补货门店数
            lineList.add(replenishDiffSum.getReplenishStoreCount());
            //单店单次原补货品项
            lineList.add(replenishDiffSum.getSingleReplenishCount());
            //单店单次实际补货品项
            lineList.add(replenishDiffSum.getSingleActualReplenishCount());
            //原单品项数
            lineList.add(replenishDiffSum.getOldReplenishProCount());
            //新增品项数
            lineList.add(replenishDiffSum.getAddCount());
            //新增占比
            BigDecimal addProportion = replenishDiffSum.getAddProportion();
            lineList.add((ValidateUtil.isNotEmpty(addProportion)) ? addProportion.toString() + suffix : "");
            //删除品项数
            lineList.add(replenishDiffSum.getDeleteCount());
            //删除占比
            BigDecimal deleteProportion = replenishDiffSum.getDeleteProportion();
            lineList.add((ValidateUtil.isNotEmpty(deleteProportion)) ? deleteProportion.toString() + suffix : "");
            //一致品项数
            lineList.add(replenishDiffSum.getEqualCount());
            //一致占比
            BigDecimal equalProportion = replenishDiffSum.getEqualProportion();
            lineList.add((ValidateUtil.isNotEmpty(equalProportion)) ? equalProportion.toString() + suffix : "");
            //修改品项数
            lineList.add(replenishDiffSum.getEditCount());
            //修改占比
            BigDecimal editProportion = replenishDiffSum.getEditProportion();
            lineList.add((ValidateUtil.isNotEmpty(editProportion)) ? editProportion.toString() + suffix : "");
            //补货公式零售额
            lineList.add(replenishDiffSum.getRetailSaleAmt());
            //实际零售额
            lineList.add(replenishDiffSum.getActualRetailSaleAmt());
            //零售额差异
            lineList.add(replenishDiffSum.getDiffAmt());
            //补货公式成本额
            lineList.add(replenishDiffSum.getCostAmt());
            //实际成本额
            lineList.add(replenishDiffSum.getActualCostAmt());
            //成本额差异
            lineList.add(replenishDiffSum.getDiffCostAmt());
            dataList.add(lineList);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HSSFWorkbook workbook = ExcelUtils.exportExcel2(
                new ArrayList<String[]>() {{
                    add(CommonConstant.REPLENISH_DIFF_SUM_EXPORT_HEAD);        //表头
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(dataList);    //数据
                }},
                new ArrayList<String>() {{
                    add(CommonConstant.REPLENISH_DIFF_SUM_SHEET_NAME);     //sheet名
                }});

        JsonResult uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = CommonConstant.REPLENISH_DIFF_SUM_SHEET_NAME + "-" + CommonUtil.getyyyyMMdd() + ".xls";     //文件名
            uploadResult = cosUtils.uploadFileNew(bos, fileName);      //上传腾讯云
            bos.flush();
        } catch (IOException e) {
            log.error("导出文件失败:{}",e.getMessage(), e);
            throw new BusinessException("导出文件失败！");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭流异常:{}",e.getMessage(), e);
                throw new BusinessException("关闭流异常！");
            }
        }
        return uploadResult;
    }



    /**
     * 查询语句
     * @param inData
     * @return
     */
    /*private String getQuerySqlByKylin(ReplenishDiffSumInData inData) {
        //报表类型    1-日报 2-周报 3-月报
        String type = inData.getType();
        //获取查询条件
        String queryConditionStr = getQueryCondition(inData);
        //获取分组条件
        String[] groupArr = getGroupByType(type);

        //构建查询
        StringBuilder queryBuilder = new StringBuilder("");
        queryBuilder.append(" SELECT ");
        queryBuilder.append(" resultFirst.dateStr, ");
        queryBuilder.append(" resultFirst.provId, ");
        queryBuilder.append(" resultFirst.cityId, ");
        queryBuilder.append(" resultFirst.client, ");
        queryBuilder.append(" resultFirst.patternType, ");
        queryBuilder.append(" resultSecond.replenishStoreCount replenishStoreCount, ");
        queryBuilder.append(" ROUND(CAST(resultFirst.oldReplenishProCount AS DECIMAL) / CAST(resultSecond.replenishStoreCount AS DECIMAL ), 0) singleReplenishCount, ");
        queryBuilder.append(" ROUND( CAST(( resultFirst.addCount + resultFirst.equalCount + resultFirst.editCount )AS DECIMAL ) / CAST(resultSecond.replenishStoreCount AS DECIMAL ), 0 ) singleActualReplenishCount, ");
        queryBuilder.append(" resultFirst.oldReplenishProCount oldReplenishProCount, ");
        queryBuilder.append(" resultFirst.addCount addCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.addCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) addProportion, ");
        queryBuilder.append(" resultFirst.deleteCount deleteCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.deleteCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) deleteProportion, ");
        queryBuilder.append(" resultFirst.equalCount equalCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.equalCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) equalProportion, ");
        queryBuilder.append(" resultFirst.editCount editCount, ");
        queryBuilder.append(" ROUND((CAST(resultFirst.editCount AS DECIMAL) / CAST(resultFirst.oldReplenishProCount AS DECIMAL)) * 100, 2) editProportion, ");
        queryBuilder.append(" ROUND(resultFirst.retailSaleAmt / 10000, 2)  retailSaleAmt, ");
        queryBuilder.append(" ROUND(resultFirst.actualretailSaleAmt / 10000, 2)  actualretailSaleAmt, ");
        queryBuilder.append(" ROUND((resultFirst.retailSaleAmt - resultFirst.actualretailSaleAmt) / 10000, 2)  diffAmt, ");
        queryBuilder.append(" ROUND(resultFirst.costAmt / 10000, 2) costAmt, ");
        queryBuilder.append(" ROUND(resultFirst.actualCostAmt / 10000, 2)  actualCostAmt, ");
        queryBuilder.append(" ROUND((resultFirst.costAmt - resultFirst.actualCostAmt) / 10000, 2) diffCostAmt ");
        queryBuilder.append(" FROM ( SELECT   ");
        queryBuilder.append(groupArr[0]);          //分组条件
        queryBuilder.append(" resultData.provId, resultData.cityId, resultData.client, resultData.patternType, ");
        queryBuilder.append(" COUNT( resultData.proId ) oldReplenishProCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.addCount IS NULL THEN 0 ELSE resultData.addCount END ) addCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.deleteCount IS NULL THEN 0 ELSE resultData.deleteCount END ) deleteCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.equalCount IS NULL THEN 0 ELSE resultData.equalCount END ) equalCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.editCount IS NULL THEN 0 ELSE resultData.editCount END ) editCount, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.retailSaleAmt IS NULL THEN 0 ELSE resultData.retailSaleAmt END ) retailSaleAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.actualretailSaleAmt IS NULL THEN 0 ELSE resultData.actualretailSaleAmt END ) actualretailSaleAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.costAmt IS NULL THEN 0 ELSE resultData.costAmt END ) costAmt, ");
        queryBuilder.append(" SUM( CASE WHEN resultData.actualCostAmt IS NULL THEN 0 ELSE resultData.actualCostAmt END ) actualCostAmt ");
        queryBuilder.append(" FROM ( SELECT computeData.dateStr, computeData.provId, computeData.cityId, computeData.client, ");
        queryBuilder.append(" CASE WHEN computeData.patternType = '0' THEN '正常补货' WHEN computeData.patternType = '1' THEN '紧急补货' WHEN computeData.patternType = '2' THEN '铺货' WHEN computeData.patternType = '3' THEN '互调' WHEN computeData.patternType = '4' THEN '直配' ELSE computeData.patternType END patternType, ");
        queryBuilder.append(" computeData.brId, computeData.voucherId, computeData.proId,  ");
        queryBuilder.append(" SUM( computeData.addCount ) addCount, SUM( computeData.deleteCount ) deleteCount, SUM( computeData.equalCount ) equalCount, SUM( computeData.editCount ) editCount, ");
        queryBuilder.append(" SUM( computeData.proposeQty ) proposeQty, SUM( computeData.needQty ) needQty, ");
        queryBuilder.append(" SUM( computeData.normalPrice ) * SUM( computeData.proposeQty ) retailSaleAmt, ");
        queryBuilder.append(" SUM( computeData.normalPrice ) * SUM( computeData.needQty ) actualretailSaleAmt, ");
        queryBuilder.append(" SUM( computeData.movPrice ) * SUM( computeData.taxValue ) * SUM( computeData.proposeQty ) costAmt, ");
        queryBuilder.append(" SUM( computeData.movPrice ) * SUM( computeData.taxValue ) * SUM( computeData.needQty ) actualCostAmt  ");
        queryBuilder.append(" FROM ( SELECT baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType, baseData.brId, baseData.voucherId, baseData.proId, ");
        queryBuilder.append(" SUM( baseData.addCount ) addCount, SUM( baseData.deleteCount ) deleteCount, SUM( baseData.equalCount ) equalCount, SUM( baseData.editCount ) editCount, SUM( baseData.proposeQty ) proposeQty, ");
        queryBuilder.append(" SUM( baseData.needQty ) needQty, SUM( baseData.normalPrice ) normalPrice, SUM( baseData.movPrice ) movPrice, SUM( baseData.taxValue ) taxValue ");
        queryBuilder.append(" FROM ( SELECT rcd.GSRD_DATE dateStr, fran.FRANC_PROV provId, fran.FRANC_CITY cityId, rcd.CLIENT client, rh.GSRH_PATTERN patternType, rcd.GSRD_BR_ID brId, rcd.GSRD_VOUCHER_ID voucherId, ");
        queryBuilder.append(" rcd.GSRD_PRO_ID proId, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'A' THEN 1 ELSE 0 END addCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'D' THEN 1 ELSE 0 END deleteCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS IS NULL OR rcd.GSRD_STATUS = '' THEN 1 ELSE 0 END equalCount, ");
        queryBuilder.append(" CASE WHEN rcd.GSRD_STATUS = 'M' THEN 1 ELSE 0 END editCount, ");
        queryBuilder.append(" SUM( rcd.GSRD_PROPOSE_QTY ) proposeQty, SUM( rcd.GSRD_NEED_QTY ) needQty, SUM( pp.GSPP_PRICE_NORMAL ) normalPrice, cost.MAT_MOV_PRICE movPrice, cost.TAX_CODE_VALUE taxValue ");
        queryBuilder.append(" FROM GAIA_SD_REPLENISH_COMPARED_D rcd LEFT JOIN GAIA_SD_REPLENISH_ORI_D rod ON ( rcd.CLIENT = rod.CLIENT AND rcd.GSRD_BR_ID = rod.GSRD_BR_ID AND rcd.GSRD_VOUCHER_ID = rod.GSRD_VOUCHER_ID AND rcd.GSRD_PRO_ID = rod.GSRD_PRO_ID AND rcd.GSRD_DATE = rod.GSRD_DATE) ");
        queryBuilder.append(" LEFT JOIN GAIA_SD_PRODUCT_PRICE pp ON ( rcd.CLIENT = pp.CLIENT AND rcd.GSRD_BR_ID = pp.GSPP_BR_ID AND rcd.GSRD_PRO_ID = pp.GSPP_PRO_ID ) ");
        queryBuilder.append(" LEFT JOIN GAIA_SD_REPLENISH_H rh ON ( rcd.CLIENT = rh.CLIENT AND rcd.GSRD_BR_ID = rh.GSRH_BR_ID AND rcd.GSRD_VOUCHER_ID = rh.GSRH_VOUCHER_ID AND rcd.GSRD_DATE = rh.GSRH_DATE ) ");
        queryBuilder.append(" LEFT JOIN ( ");
        queryBuilder.append(" SELECT ass.CLIENT, ass.MAT_PRO_CODE, SUM( ass.MAT_MOV_PRICE ) AS MAT_MOV_PRICE, ( 1 + cast( REPLACE ( tax.TAX_CODE_VALUE, '%', '' ) AS DECIMAL ) / 100 ) TAX_CODE_VALUE ");
        queryBuilder.append(" FROM GAIA_MATERIAL_ASSESS ass INNER JOIN GAIA_PRODUCT_BUSINESS pb ON ( ass.CLIENT = pb.CLIENT AND ass.MAT_ASSESS_SITE = pb.PRO_SITE AND ass.MAT_PRO_CODE = pb.PRO_SELF_CODE ) ");
        queryBuilder.append(" LEFT JOIN GAIA_TAX_CODE tax ON ( tax.TAX_CODE = pb.PRO_INPUT_TAX ) INNER JOIN GAIA_DC_DATA dc ON ( ass.CLIENT = dc.CLIENT AND ass.MAT_ASSESS_SITE = dc.DC_CODE )  ");
        queryBuilder.append(" WHERE tax.TAX_CODE_CLASS = '1' GROUP BY ass.CLIENT, ass.MAT_PRO_CODE, tax.TAX_CODE_VALUE  ");
        queryBuilder.append(" ) cost ON ( rcd.CLIENT = cost.CLIENT AND rcd.GSRD_PRO_ID = cost.MAT_PRO_CODE ) LEFT JOIN GAIA_FRANCHISEE fran ON ( rcd.CLIENT = fran.CLIENT )  ");
        queryBuilder.append(queryConditionStr);         //拼接条件
        queryBuilder.append(" GROUP BY rcd.GSRD_DATE, fran.FRANC_PROV, fran.FRANC_CITY, rcd.CLIENT, rh.GSRH_PATTERN, rcd.GSRD_BR_ID, rcd.GSRD_VOUCHER_ID, rcd.GSRD_PRO_ID, rcd.GSRD_STATUS, cost.MAT_MOV_PRICE, cost.TAX_CODE_VALUE ");
        queryBuilder.append(" ) baseData  ");
        queryBuilder.append(" GROUP BY baseData.dateStr, baseData.provId, baseData.cityId, baseData.client, baseData.patternType, baseData.brId, baseData.voucherId, baseData.proId ) computeData ");
        queryBuilder.append(" GROUP BY computeData.dateStr, computeData.provId, computeData.cityId, computeData.client, computeData.patternType, computeData.brId, computeData.voucherId, computeData.proId ) resultData ");
        queryBuilder.append(" LEFT JOIN GAIA_CAL_DT dt ON ( resultData.dateStr = REPLACE ( CAST( dt.GCD_DATE AS VARCHAR ), '-', '' ) ) GROUP BY ");
        queryBuilder.append(groupArr[1]);          //分组条件
        queryBuilder.append(" resultData.provId, resultData.cityId, resultData.client, resultData.patternType ) resultFirst ");
        queryBuilder.append(" LEFT JOIN ( SELECT ");
        queryBuilder.append(groupArr[0]);          //分组条件
        queryBuilder.append(" baseData.provId provId, baseData.cityId cityId, baseData.client client, baseData.patternType patternType, SUM(baseData.replenishStoreCount) replenishStoreCount ");
        queryBuilder.append(" FROM ( SELECT rcd.GSRD_DATE dateStr, fran.FRANC_PROV provId, fran.FRANC_CITY cityId, rcd.CLIENT client, ");
        queryBuilder.append(" CASE WHEN rh.GSRH_PATTERN = '0' THEN '正常补货' WHEN rh.GSRH_PATTERN = '1' THEN '紧急补货' WHEN rh.GSRH_PATTERN = '2' THEN '铺货' WHEN rh.GSRH_PATTERN = '3' THEN '互调' WHEN rh.GSRH_PATTERN = '4' THEN '直配' ELSE rh.GSRH_PATTERN END patternType, ");
        queryBuilder.append(" COUNT( DISTINCT rcd.GSRD_BR_ID ) replenishStoreCount  ");
        queryBuilder.append(" FROM GAIA_SD_REPLENISH_COMPARED_D rcd LEFT JOIN GAIA_SD_REPLENISH_H rh ON ( rcd.CLIENT = rh.CLIENT AND rcd.GSRD_BR_ID = rh.GSRH_BR_ID AND rcd.GSRD_VOUCHER_ID = rh.GSRH_VOUCHER_ID AND rcd.GSRD_DATE = rh.GSRH_DATE ) ");
        queryBuilder.append(" LEFT JOIN GAIA_FRANCHISEE fran ON ( rcd.CLIENT = fran.CLIENT )  ");
        queryBuilder.append(queryConditionStr);         //拼接条件
        queryBuilder.append(" GROUP BY rcd.GSRD_DATE, fran.FRANC_PROV, fran.FRANC_CITY, rcd.CLIENT, rh.GSRH_PATTERN ) baseData");
        queryBuilder.append(" LEFT JOIN GAIA_CAL_DT dt ON ( baseData.dateStr = REPLACE ( CAST( dt.GCD_DATE AS VARCHAR ), '-', '' )) GROUP BY");
        queryBuilder.append(groupArr[1]);           //分组条件
        queryBuilder.append(" baseData.provId, baseData.cityId, baseData.client, baseData.patternType ");
        queryBuilder.append(" ) resultSecond ON ( resultFirst.dateStr = resultSecond.dateStr AND resultFirst.provId = resultSecond.provId AND resultFirst.cityId = resultSecond.cityId AND resultFirst.client = resultSecond.client ");
        queryBuilder.append(" AND resultFirst.patternType = resultSecond.patternType ) ");
        return queryBuilder.toString();
    }*/


    /**
     * 根据报表类型获取分组条件
     * @param type  1-日报 2-周报 3-月报
     * @return
     */
    /*private String[] getGroupByType(String type) {
        String[] arr = new String[2];

        //1-日报
        if("1".equals(type)) {
            arr[0] = " REPLACE( CAST( dt.GCD_DATE AS VARCHAR ), '-', '' ) dateStr, ";
            arr[1] = " dt.GCD_DATE, ";
        }

        //2-周报
        if("2".equals(type)) {
            arr[0] = " dt.GCD_YEAR + '年' + dt.GCD_WEEK + '周' dateStr, ";
            arr[1] = " dt.GCD_YEAR, dt.GCD_WEEK, ";
        }

        //3-月报
        if("3".equals(type)) {
            arr[0] = " dt.GCD_YEAR + '年' + dt.GCD_MONTH + '月' dateStr, ";
            arr[1] = " dt.GCD_YEAR, dt.GCD_MONTH, ";
        }
        return arr;
    }*/
}

package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Splitter;
import com.gys.common.data.*;
import com.gys.common.kylin.RowMapper;
import com.gys.entity.*;
import com.gys.entity.data.MonthPushMoney.*;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV5OutData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV5TotalOutData;
import com.gys.entity.data.TichengProplanStoN;
import com.gys.entity.data.commissionplan.CommissionSummaryDetailDTO;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.entity.data.salesSummary.UserCommissionSummaryDetail;
import com.gys.mapper.*;
import com.gys.service.TiChengCaculateService;
import com.gys.util.BigDecimalUtil;
import com.gys.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import tk.mybatis.mapper.weekend.Weekend;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: flynn
 * @date: 2021年11月17日 下午7:20
 */
@Slf4j
@SuppressWarnings("all")
public class TiChangProCalculateServiceImpl extends AbstractTiChangCalcServiceImpl implements TiChengCaculateService {

    private GaiaTichengPlanZMapper tichengPlanZMapper;

    private TichengProplanBasicMapper tichengProplanBasicMapper;

    private TichengProplanStoNMapper tichengProplanStoMapper;

    private TichengProplanSettingMapper tichengProplanSettingMapper;

    private TichengProplanProNMapper tichengProplanProNMapper;

    private UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper;

    private JdbcTemplate kylinJdbcTemplate;

    public TiChangProCalculateServiceImpl(GaiaTichengPlanZMapper tichengPlanZMapper,
                                          TichengProplanBasicMapper tichengProplanBasicMapper,
                                          TichengProplanStoNMapper tichengProplanStoMapper,
                                          TichengProplanSettingMapper tichengProplanSettingMapper,
                                          TichengProplanProNMapper tichengProplanProNMapper,
                                          JdbcTemplate kylinJdbcTemplate) {
        this.tichengPlanZMapper = tichengPlanZMapper;
        this.tichengProplanBasicMapper = tichengProplanBasicMapper;
        this.tichengProplanStoMapper = tichengProplanStoMapper;
        this.tichengProplanSettingMapper = tichengProplanSettingMapper;
        this.tichengProplanProNMapper = tichengProplanProNMapper;
        this.kylinJdbcTemplate = kylinJdbcTemplate;
    }

    public TiChangProCalculateServiceImpl(GaiaTichengPlanZMapper tichengPlanZMapper,
                                          TichengProplanBasicMapper tichengProplanBasicMapper,
                                          TichengProplanStoNMapper tichengProplanStoMapper,
                                          TichengProplanSettingMapper tichengProplanSettingMapper,
                                          TichengProplanProNMapper tichengProplanProNMapper,
                                          JdbcTemplate kylinJdbcTemplate,
                                          UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper) {
        this.tichengPlanZMapper = tichengPlanZMapper;
        this.tichengProplanBasicMapper = tichengProplanBasicMapper;
        this.tichengProplanStoMapper = tichengProplanStoMapper;
        this.tichengProplanSettingMapper = tichengProplanSettingMapper;
        this.tichengProplanProNMapper = tichengProplanProNMapper;
        this.kylinJdbcTemplate = kylinJdbcTemplate;
        this.userCommissionSummaryDetailMapper = userCommissionSummaryDetailMapper;
        super.tichengPlanZMapper = tichengPlanZMapper;
        super.userCommissionSummaryDetailMapper = userCommissionSummaryDetailMapper;
        super.kylinJdbcTemplate = kylinJdbcTemplate;
    }


    private List<Map<String, Object>> decorateCaculatePlanForAllProduct(List<Map<String, Object>> kylinData, EmpSaleDetailInData inData) {
        List<Map<String, Object>> resList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(kylinData)) {
            resList = kylinData;
        }
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        inData.setToday(today);
        List<EmpSaleDetailResVo> toDayAllEmpSaleDetailList = new ArrayList<>();
        if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(inData.getEndDate().replaceAll("-", "")) >= Integer.parseInt(today)) {
            toDayAllEmpSaleDetailList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
        }


        if (CollectionUtil.isNotEmpty(kylinData)) {
            if (CollectionUtil.isNotEmpty(toDayAllEmpSaleDetailList)) {
                Map<String, Object> data = kylinData.get(0);
                BigDecimal amtToTal = BigDecimal.ZERO;
                BigDecimal ysAmtToTal = BigDecimal.ZERO;
                BigDecimal grossProfitAmtToTal = BigDecimal.ZERO;
                BigDecimal zkAmtToTal = BigDecimal.ZERO;
                BigDecimal costAmtToTal = BigDecimal.ZERO;
                for (EmpSaleDetailResVo resVo : toDayAllEmpSaleDetailList) {
                    String amt = resVo.getAmt();
                    amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
                    String ysAmt = resVo.getYsAmt();
                    ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
                    String grossProfitAmt = resVo.getGrossProfitAmt();
                    grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
                    String zkAmt = resVo.getZkAmt();
                    zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
                    String costAmt = resVo.getCostAmt();
                    costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
                }
                String days = String.valueOf(data.get("days"));
                Integer finalDays = 1;
                if (StrUtil.isNotBlank(days)) {
                    finalDays = Integer.parseInt(days) + finalDays;
                }
                data.put("days", finalDays.toString());

                String amt = String.valueOf(data.get("amt"));
                if (StrUtil.isNotBlank(amt)) {
                    amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
                }
                data.put("amt", amtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                String ysAmt = String.valueOf(data.get("ysAmt"));
                if (StrUtil.isNotBlank(ysAmt)) {
                    ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
                }
                data.put("ysAmt", ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                String grossProfitAmt = String.valueOf(data.get("grossProfitAmt"));
                if (StrUtil.isNotBlank(grossProfitAmt)) {
                    grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
                }
                data.put("grossProfitAmt", grossProfitAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.put("grossProfitRate", amtToTal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : (ysAmtToTal.subtract(costAmtToTal)).divide(ysAmtToTal, 4, RoundingMode.HALF_UP).toPlainString());

                String zkAmt = String.valueOf(data.get("zkAmt"));
                if (StrUtil.isNotBlank(zkAmt)) {
                    zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
                }
                data.put("zkAmt", zkAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                String costAmt = String.valueOf(data.get("costAmt"));
                if (StrUtil.isNotBlank(costAmt)) {
                    costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
                }
                data.put("costAmt", costAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.put("amtAvg", finalDays == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : ysAmtToTal.divide(new BigDecimal(finalDays), 4, RoundingMode.HALF_UP).toPlainString());//只有一天的情况
                resList.add(data);
            } else {
                resList = kylinData;
            }

        } else {
            //kylin数据为空
            if (CollectionUtil.isNotEmpty(toDayAllEmpSaleDetailList)) {
                Map<String, Object> data = new HashMap<>();
                BigDecimal amtToTal = BigDecimal.ZERO;
                BigDecimal ysAmtToTal = BigDecimal.ZERO;
                BigDecimal grossProfitAmtToTal = BigDecimal.ZERO;
                BigDecimal zkAmtToTal = BigDecimal.ZERO;
                BigDecimal costAmtToTal = BigDecimal.ZERO;
                for (EmpSaleDetailResVo resVo : toDayAllEmpSaleDetailList) {
                    String amt = resVo.getAmt();
                    amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
                    String ysAmt = resVo.getYsAmt();
                    ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
                    String grossProfitAmt = resVo.getGrossProfitAmt();
                    grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
                    String zkAmt = resVo.getZkAmt();
                    zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
                    String costAmt = resVo.getCostAmt();
                    costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
                }
//                String days = String.valueOf(data.get("days"));
                Integer finalDays = 1;
//                if (StrUtil.isNotBlank(days)) {
//                    finalDays = Integer.parseInt(days) + finalDays;
//                }
                data.put("days", finalDays.toString());

//                String amt = String.valueOf(data.get("amt"));
//                if (StrUtil.isNotBlank(amt)) {
//                    amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
//                }
                data.put("amt", amtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

//                String ysAmt = String.valueOf(data.get("ysAmt"));
//                if (StrUtil.isNotBlank(ysAmt)) {
//                    ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
//                }
                data.put("ysAmt", ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
//
//                String grossProfitAmt = String.valueOf(data.get("grossProfitAmt"));
//                if (StrUtil.isNotBlank(grossProfitAmt)) {
//                    grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
//                }
                data.put("grossProfitAmt", grossProfitAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.put("grossProfitRate", amtToTal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : (ysAmtToTal.subtract(costAmtToTal)).divide(ysAmtToTal, 4, RoundingMode.HALF_UP).toPlainString());

//                String zkAmt = String.valueOf(data.get("zkAmt"));
//                if (StrUtil.isNotBlank(zkAmt)) {
//                    zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
//                }
                data.put("zkAmt", zkAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

//                String costAmt = String.valueOf(data.get("costAmt"));
//                if (StrUtil.isNotBlank(costAmt)) {
//                    costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
//                }
                data.put("costAmt", costAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.put("amtAvg", finalDays == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : ysAmtToTal.divide(new BigDecimal(finalDays), 4, RoundingMode.HALF_UP).toPlainString());//只有一天的情况
                resList.add(data);
            }
        }
        return resList;
    }

    private List<Map<String, Object>> caculatePlanForAllProduct(String startDate, String endDate, String
            client, List<String> stoCodes) {
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        String todayKy = DateUtil.format(new Date(), "yyyy-MM-dd");
        List<Map<String, Object>> resList = new ArrayList<>();
        //  2、取单店日均，确定销售级别
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 ) as ysAmt, ") //应收金额
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 实收额
                .append(" round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),4) as grossProfitAmt, ")//毛利额
//                .append(" round((sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ))/ sum(GSSD_MOV_PRICE),2) as  grossProfitRate, ")//毛利率
                .append(" ( CASE WHEN sum( GSSD_AMT )= 0 THEN 0 ELSE round(( sum( GSSD_AMT ) - sum( GSSD_MOV_PRICE ))/ sum( GSSD_AMT ), 4 ) END ) AS grossProfitRate, ")//毛利率
                .append(" round(sum( GSSD_ZK_AMT ),2) as zkAmt, ")//折扣金额
                .append(" round(sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )),2) as zkl, ")//折扣率
                .append(" round(sum(GSSD_MOV_PRICE),4) AS costAmt, ")//成本额
                .append(" count(distinct GSSD_DATE) as days, ") //    销售天数
                .append(" round(SUM(GSSD_AMT)/COUNT(distinct GSSD_KL_DATE_BR),2) as amtAvg ") //  本店日均销售额
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + client + "'");
        if (ObjectUtil.isNotEmpty(stoCodes)) {
            stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(stoCodes));
        }
        if (Integer.parseInt(startDate.replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(endDate.replaceAll("-", "")) >= Integer.parseInt(today)) {
            stoLvBuilder.append(" AND D.GSSD_DATE NOT IN ('").append(todayKy).append("')");
        }
        stoLvBuilder.append(" GROUP BY D.CLIENT ");
//        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());
        if (CollectionUtil.isNotEmpty(stoLvData)) {
            resList = stoLvData;
        }
        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(stoCodes);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        resList = decorateCaculatePlanForAllProduct(resList, query);
        return resList;
    }


    private List<Map<String, Object>> decorateCaculatePlanForRuleOrigin(List<Map<String, Object>> kylinData, EmpSaleDetailInData inData, List<String> proCodes) {
        List<Map<String, Object>> resList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(kylinData)) {
            resList = kylinData;
        }
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(inData.getEndDate().replaceAll("-", "")) >= Integer.parseInt(today)) {
            List<EmpSaleDetailResVo> toDayAllEmpSaleDetailList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
            //处理一下数据，过滤掉需要过滤的数据
            for (EmpSaleDetailResVo resVo : toDayAllEmpSaleDetailList) {
                //过滤剔除商品
                if (!proCodes.contains(resVo.getProSelfCode())) {
                    continue;
                }
                Map<String, Object> resObject = new HashMap<>();
                //其余商品需要加入kylin数据合并成最终数据
                resObject.put("CLIENTID", inData.getClient());
                resObject.put("BRID", resVo.getStoCode());
                resObject.put("BRNAME", resVo.getStoName());
                resObject.put("BILLNO", resVo.getGssdBillNo());
                resObject.put("PROCODE", resVo.getProSelfCode());
                resObject.put("SERIAL", resVo.getSerial());
                resObject.put("QYT", resVo.getQyt());
                resObject.put("AMT", resVo.getAmt());
                resObject.put("YSAMT", resVo.getYsAmt());
                resObject.put("GROSSPROFITAMT", resVo.getGrossProfitAmt());
                resObject.put("GROSSPROFITRATE", resVo.getGrossProfitRate());
                resObject.put("ZKAMT", resVo.getZkAmt());
                resObject.put("ZKL", StrUtil.isNotBlank(resVo.getZkl()) ? new BigDecimal(resVo.getZkl()) : BigDecimal.ZERO);
                resObject.put("COSTAMT", resVo.getCostAmt());
                resObject.put("realTime", "true");
                resList.add(resObject);
            }
        }
        return resList;
    }


    private List<Map<String, Object>> caculatePlanForRuleOrigin(String startDate, String endDate, String
            client, List<String> stoCodes, List<String> proCodes) {
        List<Map<String, Object>> resList = new ArrayList<>();
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        String todayKy = DateUtil.format(new Date(), "yyyy-MM-dd");
        //  2、取单笔单品提成销售
        StringBuilder saleBuilder = new StringBuilder().append("SELECT d.CLIENT clientId,d.GSSD_BR_ID as brId,")
                .append(" S.STO_SHORT_NAME as brName,")
                .append(" GSSD_BILL_NO billNo,GSSD_PRO_ID proCode, ")
                .append(" d.GSSD_SERIAL as serial, ")
                .append(" round( sum( GSSD_QTY ), 2 ) AS qyt,")
                .append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 ) as ysAmt, ")
                .append(" round(sum(GSSD_AMT),2) as amt,")
                .append(" ROUND(round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),4), 2) as grossProfitAmt,")
//                .append(" round((sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ))/ sum(GSSD_MOV_PRICE),4) as  grossProfitRate,")
                .append(" ROUND(case sum(GSSD_AMT) when 0 then 0 else (ROUND(round(sum(GSSD_AMT),2) - round(sum" +
                        "(GSSD_MOV_PRICE),4), 2)) / sum(GSSD_AMT) end, 4) as  grossProfitRate,")
                // 查询出折扣率
//                .append(" round((case when sum(GSSD_AMT)=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end),4) as zkl,")
                .append(" round((case when (sum(GSSD_AMT) + sum(GSSD_ZK_AMT)) = 0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end),4) as zkl,")
                .append(" round(sum(GSSD_MOV_PRICE),4) AS costAmt,")
                .append(" round(sum( GSSD_ZK_AMT ),2) as zkAmt,");

        // 计算毛利额(汇总应收金额 - 加点后金额（加点后未税成本）- 加点后税金（加点后成本税额）)
        saleBuilder.append(" round(sum(GSSD_AMT)-sum(GSSD_MOV_PRICE),4) as saleGrossProfit ");
        // 查询出折扣率
//        saleBuilder.append(" ABS(case when min( GSSD_PRC1 ) = 0 or sum( GSSD_QTY )=0 then 0 else round(SUM( GSSD_AMT )/(min( GSSD_PRC1 )* sum( GSSD_QTY )), 4 ) end ) as ZKL ");
        saleBuilder.append(" from GAIA_SD_SALE_D d ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON d.CLIENT = S.CLIENT AND d.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE d.GSSD_DATE >= '" + startDate + "'")
                .append(" AND d.GSSD_DATE <= '" + endDate + "'")
                .append(" AND d.CLIENT= '" + client + "'");
        if (ObjectUtil.isNotEmpty(stoCodes)) {
            saleBuilder.append(" AND d.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(stoCodes));
        }
        if (Integer.parseInt(startDate.replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(endDate.replaceAll("-", "")) >= Integer.parseInt(today)) {
            saleBuilder.append(" AND D.GSSD_DATE NOT IN ('").append(todayKy).append("')");
        }
        if (ObjectUtil.isNotEmpty(proCodes)) {
            saleBuilder.append(" AND d.GSSD_PRO_ID IN ")
                    .append(CommonUtil.queryByBatch(proCodes));
        }

        saleBuilder.append(" GROUP BY d.CLIENT,d.GSSD_BR_ID,S.STO_SHORT_NAME,d.GSSD_BILL_NO,d.GSSD_PRO_ID,d.GSSD_SERIAL ");
//        log.info("sql统计数据：{查询小票中参与提成商品销售信息}:" + saleBuilder.toString());
        List<Map<String, Object>> saleData = kylinJdbcTemplate.queryForList(saleBuilder.toString());

        if (CollectionUtil.isNotEmpty(saleData)) {
            resList = saleData;
        }

        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(stoCodes);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        resList = decorateCaculatePlanForRuleOrigin(resList, query, proCodes);

        return resList;
    }


    private List<Map<String, Object>> caculatePlanForRule(String startDate, String endDate, String
            client, List<String> stoCodes, TichengProplanSetting
                                                                  setting, List<String> rejectClass, List<String> rejectPro) {
        //  2、取单笔单品提成销售
        StringBuilder saleBuilder = new StringBuilder().append("SELECT d.CLIENT clientId,d.GSSD_BR_ID as brId,")
                .append(" S.STO_SHORT_NAME as brName,")
                .append(" GSSD_BILL_NO billNo,GSSD_PRO_ID proCode,sum(GSSD_QTY) saleQty,sum(GSSD_AMT) saleAmt,min(GSSD_PRC1) proPrice,d.GSSD_SALER_ID salerId, ");

        // 计算毛利额(汇总应收金额 - 加点后金额（加点后未税成本）- 加点后税金（加点后成本税额）)
        saleBuilder.append("round(sum(GSSD_AMT)-sum(GSSD_MOV_PRICE),2) as saleGrossProfit");
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(setting.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(setting.getPlanRejectDiscountRate())) {
            saleBuilder.append("  , ABS(case when min( GSSD_PRC1 ) = 0 or sum( GSSD_QTY )=0 then 0 else round(SUM( GSSD_AMT )/(min( GSSD_PRC1 )* sum( GSSD_QTY )), 4 ) end ) as ZKL ");
        }

        saleBuilder.append(" from GAIA_SD_SALE_D d ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON d.CLIENT = S.CLIENT AND d.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE d.GSSD_DATE >= '" + startDate + "'")
                .append(" AND d.GSSD_DATE <= '" + endDate + "'")
                .append(" AND d.CLIENT= '" + client + "'");
        if (ObjectUtil.isNotEmpty(stoCodes)) {
            saleBuilder.append(" AND d.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(stoCodes));
        }
//        if (ObjectUtil.isNotEmpty(proCodes)) {
//            saleBuilder.append(" AND d.GSSD_PRO_ID IN ")
//                    .append(CommonUtil.queryByBatch(proCodes));
//        }
        saleBuilder.append(" GROUP BY d.CLIENT,d.GSSD_BR_ID,S.STO_SHORT_NAME,d.GSSD_BILL_NO,d.GSSD_PRO_ID,d.GSSD_SALER_ID ");
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(setting.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(setting.getPlanRejectDiscountRate())) {
            switch (setting.getPlanRejectDiscountRateSymbol()) {
                case "=":
                    setting.setPlanRejectDiscountRateSymbol("!=");
                    break;
                case ">":
                    setting.setPlanRejectDiscountRateSymbol("<=");
                    break;
                case ">=":
                    setting.setPlanRejectDiscountRateSymbol("<");
                    break;
                case "<":
                    setting.setPlanRejectDiscountRateSymbol(">=");
                    break;
                case "<=":
                    setting.setPlanRejectDiscountRateSymbol(">");
                    break;
            }
            saleBuilder.append("having ZKL").append(setting.getPlanRejectDiscountRateSymbol()).append(setting.getPlanRejectDiscountRate());
        }
        saleBuilder.append(" ORDER BY d.GSSD_BR_ID ");
//        log.info("sql统计数据：{查询小票中参与提成商品销售信息}:" + saleBuilder.toString());
        List<ProductSaleAmt> saleData = kylinJdbcTemplate.query(saleBuilder.toString(), RowMapper.getDefault(ProductSaleAmt.class));
        return null;
    }

    private String tranceRejectDiscountRateSymbol(String symbol) {
        String resStr = "";
        switch (symbol) {
            case "=":
                resStr = "!=";
                break;
            case ">":
                resStr = "<=";
                break;
            case ">=":
                resStr = "<";
                break;
            case "<":
                resStr = ">=";
                break;
            case "<=":
                resStr = ">";
                break;
        }
        return resStr;
    }

    private boolean caculateSymbol(BigDecimal left, BigDecimal rigth, String symbol) {
        left = left.multiply(BigDecimalUtil.ONE_HUNDRED);
        boolean resFlag = false;
        switch (symbol) {
            case "=":
                if (left.compareTo(rigth) == 0) {
                    resFlag = true;
                }
                break;
            case "<=":
                if (left.compareTo(rigth) <= 0) {
                    resFlag = true;
                }
                break;
            case "<":
                if (left.compareTo(rigth) < 0) {
                    resFlag = true;
                }
                break;
            case ">=":
                if (left.compareTo(rigth) >= 0) {
                    resFlag = true;
                }
                break;
            case ">":
                if (left.compareTo(rigth) > 0) {
                    resFlag = true;
                }
                break;
        }
        return resFlag;
    }

    private Map<String, Object> handleProSettingValue(TichengProplanProN proV3, TiChenProRes proRes) {
        Map<String, Object> resultMap = new HashMap<>();

        //计算提成以单张小票作为维度，dbData数据已经满足需求

        BigDecimal qyc = BigDecimalUtil.toBigDecimal(proRes.getQyt());
        BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(proRes.getYsAmt());
        BigDecimal amt = BigDecimalUtil.toBigDecimal(proRes.getAmt());
        BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(proRes.getGrossProfitAmt());
        BigDecimal baseAmount = BigDecimal.ZERO;
        //提成方式 1 按零售额提成 2 按销售额提成  3按毛利率提成
        if ("1".equals(proV3.getPliantPercentageType())) {
            baseAmount = ysAmt;
        } else if ("2".equals(proV3.getPliantPercentageType())) {
            baseAmount = amt;
        } else if ("3".equals(proV3.getPliantPercentageType())) {
            baseAmount = grossProfitAmt;
        }
        BigDecimal tiTotal = BigDecimalUtil.format(handleProTiAmount(proV3, baseAmount, qyc), 4);
        resultMap.put("tiTotal", tiTotal);
        return resultMap;
    }

    /**
     * 基于实际数据库级别尽心实时的销售天数统计
     *
     * @param condition
     * @return
     */
    private List<SaleDateNumRes> handleSaleDateNumByCondition(SaleDateNumQueryCondition condition) {
        List<SaleDateNumRes> resList = new ArrayList<>();
        List<SaleDateNumRes> saleDayList = tichengPlanZMapper.selecrSaleDaysByCondition(condition);
        if (CollectionUtil.isNotEmpty(saleDayList)) {
            resList = saleDayList;
        }
        return resList;
    }

    @Override
    public PageInfo caculatePlanOptimize(PushMoneyByStoreV5InData inData) {
        //所有的子方案的集合
        List<PushMoneyByStoreV5TotalOutData> outDatas = new ArrayList<>();

        //  1.查询当前商品提成计划
        TichengProplanBasic basicQuery = new TichengProplanBasic();
        basicQuery.setClient(inData.getClient());
        basicQuery.setId(inData.getPlanId());
        //  提成主表
        TichengProplanBasic basic = tichengProplanBasicMapper.selectOne(basicQuery);

        // 获取方案的开始结束时间
        String planStartDate = basic.getPlanStartDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        String planEndDate = basic.getPlanEndDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        // 获取用户选择的开始结束时间
        String userChooseStartDate = inData.getUserChooseStartDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        String userChooseEndDate = inData.getUserChooseEndDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);

        String maxDate = com.gys.util.DateUtil.getMaxDate(userChooseStartDate, planStartDate);
        String startDate = com.gys.util.DateUtil.dateConvert(maxDate);
        String minDate = com.gys.util.DateUtil.getMinDate(userChooseEndDate, planEndDate);
        String endDate = com.gys.util.DateUtil.dateConvert(minDate);

        TichengProplanStoN proplanStoQuery = new TichengProplanStoN();
        proplanStoQuery.setClient(inData.getClient());
        proplanStoQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStos = tichengProplanStoMapper.select(proplanStoQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStos)) {
            for (TichengProplanStoN sto : tichengProplanStos) {
                tichengSto.add(sto.getStoCode());
            }
        }
        List<CommonVo> storeCommons = tichengProplanBasicMapper.selectStoreByStoreCodes(tichengSto, inData.getClient());

        List<String> querySto = new ArrayList<>(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        Map<String, Map<String, Object>> settingMap = new HashMap<>();
        //计算每一个子方案，到最后进行按需汇总,此处最终的数据是settingig 对应的所有剔除折扣率之后的数据
        Map<String, List<Map<String, Object>>> settingIdDataListMap = new HashMap<>();
        //查询多个提成配置，及多个子方案
        TichengProplanSetting tichengProplanSettingQuery = new TichengProplanSetting();
        tichengProplanSettingQuery.setClient(inData.getClient());
        tichengProplanSettingQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan())) {
            //显示子方案的时候，是分别计算的
            if (StrUtil.isNotBlank(inData.getSettingId())) {
                tichengProplanSettingQuery.setId(Long.parseLong(inData.getSettingId()));
            }
        }
        tichengProplanSettingQuery.setDeleteFlag("0");

        List<TichengProplanSetting> tichengProplanSettings = tichengProplanSettingMapper.select(tichengProplanSettingQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanSettings)) {
            for (TichengProplanSetting setting : tichengProplanSettings) {
                Map<String, Object> singleSettingMap = new HashMap<>();
                //查询每个提成配置下的对应设置
                Long settingId = setting.getId();
                //  查询每个提成配置下商品提成商品
                TichengProplanProN tichengProplanProQuery = new TichengProplanProN();
                tichengProplanProQuery.setClient(inData.getClient());
                tichengProplanProQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
                tichengProplanProQuery.setSettingId(Integer.parseInt(settingId + ""));
                tichengProplanProQuery.setDeleteFlag("0");
                List<TichengProplanProN> tichengProplanPros = tichengProplanProNMapper.select(tichengProplanProQuery);
                //  查询每个提成配置下商品提成商品
                List<String> proCodes = new ArrayList<>();
                if (CollUtil.isNotEmpty(tichengProplanPros)) {
                    for (int i = 0; i < tichengProplanPros.size(); i++) {
                        proCodes.add(tichengProplanPros.get(i).getProCode());
                    }
                }
                singleSettingMap.put("settingMainInfo", setting);
                singleSettingMap.put("settingProCodes", proCodes);
                singleSettingMap.put("settingProSetting", tichengProplanPros);
                settingMap.put(settingId + "", singleSettingMap);
            }
        }

        PushMoneyByStoreV5OutData outTotalDataWithOutRule = new PushMoneyByStoreV5OutData();
        //先查询方案设置无关需要加载的数据--此统计为加盟商级别
        List<Map<String, Object>> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto);
        outTotalDataWithOutRule.setPlanName(basic.getPlanName());
        outTotalDataWithOutRule.setPlanId(inData.getPlanId());
        outTotalDataWithOutRule.setStartDate(planStartDate);
        outTotalDataWithOutRule.setEndtDate(planEndDate);

        if ("1".equals(basic.getPlanStatus())) {
            outTotalDataWithOutRule.setStatus("1");
        } else if ("2".equals(basic.getDeleteFlag())) {
            outTotalDataWithOutRule.setStatus("2");
        }
        outTotalDataWithOutRule.setType("单品提成");
        outTotalDataWithOutRule.setTypeValue("1");
        if (CollectionUtil.isNotEmpty(stoLvData)) {
            //正常应该是一条
            Map<String, Object> clientDataStatic = stoLvData.get(0);
            outTotalDataWithOutRule.setDays(BigDecimalUtil.toBigDecimal(clientDataStatic.get("days")));
            outTotalDataWithOutRule.setCostAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("costAmt")));
            outTotalDataWithOutRule.setYsAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("ysAmt")));
            outTotalDataWithOutRule.setAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("amt")));
            outTotalDataWithOutRule.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("grossProfitAmt")));
            outTotalDataWithOutRule.setGrossProfitRate(BigDecimalUtil.toBigDecimal(clientDataStatic.get("grossProfitRate")));
            outTotalDataWithOutRule.setZkAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("zkAmt")));
            outTotalDataWithOutRule.setZkRate(outTotalDataWithOutRule.getYsAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : outTotalDataWithOutRule.getZkAmt().divide(outTotalDataWithOutRule.getYsAmt(), 4, RoundingMode.HALF_UP));
        } else {
            outTotalDataWithOutRule.setDays(BigDecimal.ZERO);
            outTotalDataWithOutRule.setCostAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setYsAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setGrossProfitAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setGrossProfitRate(BigDecimal.ZERO);
            outTotalDataWithOutRule.setZkAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setZkRate(BigDecimal.ZERO);
        }

        if (CollectionUtil.isNotEmpty(settingMap)) {
            List<String> allProCodes = new ArrayList<>();
            //处理所有需要进入统计的所有procode
            for (String settingId : settingMap.keySet()) {
                Map<String, Object> settingInfoMap = settingMap.get(settingId);
                List<String> proCodes = (List<String>) settingInfoMap.get("settingProCodes");
                if (CollectionUtil.isNotEmpty(proCodes)) {
                    proCodes.forEach(x -> {
                        if (!allProCodes.contains(x)) {
                            allProCodes.add(x);
                        }
                    });
                }
            }

            // 获取的只有提成的数据
            String nowDate = LocalDate.now().toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
            boolean searchHistory = assertInTimeRange(startDate, endDate, nowDate);
            List<Map<String, Object>> saleDataOrigin = doGetCommissionDetail(startDate, endDate, inData.getClient(),
                    querySto, allProCodes, searchHistory, String.valueOf(inData.getPlanId()));
            for (String settingId : settingMap.keySet()) {
                settingIdDataListMap.put(settingId, saleDataOrigin.stream().filter(item -> settingId.equals(item.get("subPlanId"))).collect(Collectors.toList()));
            }
        }

        //settingMap 中有每个子方案的配置信息   settingIdDataListMap中是分组之后所有满足实际配置条件的数据
        //根据以上两个map进行提成数据的计算,汇总到每个子方案，用于后期勾选子方案展示时一把带出
        for (String settingId : settingMap.keySet()) {
            PushMoneyByStoreV5TotalOutData outData = new PushMoneyByStoreV5TotalOutData();//每个子方案最终结果
            BeanUtils.copyProperties(outTotalDataWithOutRule, outData);//复制通用数据
            Map<String, Object> settingInfoMap = settingMap.get(settingId);
            TichengProplanSetting settingMainInfo = (TichengProplanSetting) settingInfoMap.get("settingMainInfo");
            outData.setCPlanName(settingMainInfo.getCPlanName());

            //单个提成设置的主信息
            Map<String, TichengProplanProN> proCodeProSettingMap = new HashMap<>();
            List<TichengProplanProN> settingProSetting = (List<TichengProplanProN>) settingInfoMap.get("settingProSetting");
            if (CollectionUtil.isNotEmpty(settingProSetting)) {
                settingProSetting.forEach(x -> {
                    proCodeProSettingMap.put(x.getProCode(), x);
                });
            }

            // 获取有提成金额的商品
            List<Map<String, Object>> dataList = settingIdDataListMap.get(settingId);

            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
            BigDecimal tcAmtTotal = BigDecimal.ZERO;
            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal tiTotal = BigDecimal.ZERO;

            // 此处颗粒度为商品
            if (CollectionUtil.isNotEmpty(dataList)) {
                for (Map<String, Object> dbData : dataList) {
                    TiChenProRes proRes = new TiChenProRes();
                    BigDecimal qyt = BigDecimal.ZERO;
                    BigDecimal costAmt = BigDecimal.ZERO;
                    BigDecimal ysAmt = BigDecimal.ZERO;
                    BigDecimal amt = BigDecimal.ZERO;
                    BigDecimal grossProfitAmt = BigDecimal.ZERO;
                    String procode = (String) dbData.get("PROCODE");
                    proRes.setProId(procode);
                    if (dbData.get("qyt") == null) {
                        qyt = BigDecimalUtil.toBigDecimal(dbData.get("QYT"));
                    } else {
                        qyt = BigDecimalUtil.toBigDecimal(dbData.get("qyt"));
                    }
                    proRes.setQyt(qyt.toPlainString());
                    if (dbData.get("costAmt") == null) {
                        tcCostAmtTotal = tcCostAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("COSTAMT")));
                        costAmt = BigDecimalUtil.toBigDecimal(dbData.get("COSTAMT"));
                    } else {
                        tcCostAmtTotal = tcCostAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("costAmt")));
                        costAmt = BigDecimalUtil.toBigDecimal(dbData.get("costAmt"));
                    }
                    proRes.setCostAmt(costAmt.toPlainString());
                    if (dbData.get("ysAmt") == null) {
                        tcYsAmtTotal = tcYsAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("YSAMT")));
                        ysAmt = BigDecimalUtil.toBigDecimal(dbData.get("YSAMT"));
                    } else {
                        tcYsAmtTotal = tcYsAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("ysAmt")));
                        ysAmt = BigDecimalUtil.toBigDecimal(dbData.get("ysAmt"));
                    }
                    proRes.setYsAmt(ysAmt.toPlainString());
                    if (dbData.get("amt") == null) {
                        tcAmtTotal = tcAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("AMT")));
                        amt = BigDecimalUtil.toBigDecimal(dbData.get("AMT"));
                    } else {
                        tcAmtTotal = tcAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("amt")));
                        amt = BigDecimalUtil.toBigDecimal(dbData.get("amt"));
                    }
                    proRes.setAmt(amt.toPlainString());
                    if (dbData.get("grossProfitAmt") == null) {
                        tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("GROSSPROFITAMT")));
                        grossProfitAmt = BigDecimalUtil.toBigDecimal(dbData.get("GROSSPROFITAMT"));
                    } else {
                        tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("grossProfitAmt")));
                        grossProfitAmt = BigDecimalUtil.toBigDecimal(dbData.get("grossProfitAmt"));
                    }
                    proRes.setGrossProfitAmt(grossProfitAmt.toPlainString());
                    BigDecimal perTi;
                    if (MapUtils.getBooleanValue(dbData, "realTime", false)) {
                        // 实时数据计算提成
                        TichengProplanProN tichengProplanProN = proCodeProSettingMap.get(procode);
                        Map<String, Object> tichengDataMap = handleProSettingValue(tichengProplanProN, proRes);
                        perTi = (BigDecimal) tichengDataMap.get("tiTotal");
                    } else {
                        // 不是实时数据直接获取提成金额
                        perTi = BigDecimalUtil.toBigDecimal(dbData.get("commissionAmt"));
                    }
                    tiTotal = tiTotal.add(BigDecimalUtil.format(perTi, 4));
                }
            }

            outData.setStoreCodes(storeCommons);
            outData.setStoNum(storeCommons.size());
            outData.setCPlanName(settingMainInfo.getCPlanName());
            outData.setTcCostAmt(tcCostAmtTotal);
            outData.setTcYsAmt(tcYsAmtTotal);
            outData.setTcAmt(tcAmtTotal);
            outData.setTcGrossProfitAmt(tcGrossProfitAmtTotal);
            outData.setTcGrossProfitRate(tcAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));
            outData.setTiTotal(tiTotal);
            outData.setDeductionWageAmtRate(outData.getAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outData.getAmt(), 4, RoundingMode.HALF_UP));//提成销售比
            outData.setDeductionWageGrossProfitRate(outData.getGrossProfitAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outData.getGrossProfitAmt(), 4, RoundingMode.HALF_UP));
            //处理按照规则汇总的数据
            outDatas.add(outData);
        }

        PageInfo pageInfo = new PageInfo();
        if (CollUtil.isNotEmpty(outDatas)) {
            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
            BigDecimal tcAmtTotal = BigDecimal.ZERO;
            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal tiTotal = BigDecimal.ZERO;
            // 集合列的数据汇总
            PushMoneyByStoreV5TotalOutData outTotal = new PushMoneyByStoreV5TotalOutData();
            BeanUtils.copyProperties(outDatas.get(0), outTotal);
            outTotal.setStartDate(basic.getPlanStartDate());
            outTotal.setEndtDate(basic.getPlanEndDate());
            for (PushMoneyByStoreV5TotalOutData storeData : outDatas) {
                tcCostAmtTotal = tcCostAmtTotal.add(storeData.getTcCostAmt() == null ? BigDecimal.ZERO : storeData.getTcCostAmt());
                tcYsAmtTotal = tcYsAmtTotal.add(storeData.getTcYsAmt() == null ? BigDecimal.ZERO : storeData.getTcYsAmt());
                tcAmtTotal = tcAmtTotal.add(storeData.getTcAmt() == null ? BigDecimal.ZERO : storeData.getTcAmt());
                tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(storeData.getTcGrossProfitAmt() == null ? BigDecimal.ZERO : storeData.getTcGrossProfitAmt());
                tiTotal = tiTotal.add(storeData.getTiTotal() == null ? BigDecimal.ZERO : storeData.getTiTotal());
                outTotal.setTiTotal(tiTotal);//提成金额
            }
            outTotal.setTcCostAmt(tcCostAmtTotal.setScale(4, RoundingMode.HALF_UP));
            outTotal.setTcYsAmt(tcYsAmtTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setTcAmt(tcAmtTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setTcGrossProfitAmt(tcGrossProfitAmtTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setTcGrossProfitRate(tcAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));
            outTotal.setTiTotal(tiTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setDeductionWageAmtRate(outTotal.getAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outTotal.getAmt(), 4, RoundingMode.HALF_UP));//提成销售比
            outTotal.setDeductionWageGrossProfitRate(outTotal.getGrossProfitAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outTotal.getGrossProfitAmt(), 4, RoundingMode.HALF_UP));

            if ("1".equals(inData.getIfShowZplan())) {
                //表示展示子方案
                outDatas.forEach(x -> {
                    x.setTcCostAmt(x.getTcCostAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTcYsAmt(x.getTcYsAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTcAmt(x.getTcAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTcGrossProfitAmt(x.getTcGrossProfitAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTiTotal(x.getTiTotal().setScale(2, RoundingMode.HALF_UP));
                });
                pageInfo.setList(outDatas);
            } else {
                pageInfo = new PageInfo(new ArrayList(Arrays.asList(outTotal)), outTotal);//合并一条
            }
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    private List<Map<String, Object>> doGetCommissionDetail(String startDate,
                                                            String endDate,
                                                            String client,
                                                            List<String> querySto,
                                                            List<String> proCodes,
                                                            boolean searchHistory,
                                                            String planId) {
        List<Map<String, Object>> resList = new ArrayList<>();
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        List<Map<String, Object>> saleData = null;

        if (searchHistory) {
            CommissionSummaryDetailDTO commissionSummaryDetailDTO = new CommissionSummaryDetailDTO();
            commissionSummaryDetailDTO.setClient(client);
            commissionSummaryDetailDTO.setStartDate(startDate);
            commissionSummaryDetailDTO.setEndDate(endDate);
            commissionSummaryDetailDTO.setStoCodes(querySto);
            commissionSummaryDetailDTO.setPlanId(planId);
            // 销售提成
            commissionSummaryDetailDTO.setType("1");
            saleData = userCommissionSummaryDetailMapper.selectCommissionDetailByConditionWithMap(commissionSummaryDetailDTO);
        }

        if (CollectionUtil.isNotEmpty(saleData)) {
            resList = saleData;
        }

        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(querySto);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        resList = decorateCaculatePlanForRuleOrigin(resList, query, proCodes);
        return resList;
    }

    @Override
    public PageInfo caculatePlan(PushMoneyByStoreV5InData inData) {
        //所有的子方案的集合
        List<PushMoneyByStoreV5TotalOutData> outDatas = new ArrayList<>();

        //  1.查询当前商品提成计划
        TichengProplanBasic basicQuery = new TichengProplanBasic();
        basicQuery.setClient(inData.getClient());
        basicQuery.setId(inData.getPlanId());
        //  提成主表
        TichengProplanBasic basic = tichengProplanBasicMapper.selectOne(basicQuery);
        String startDate = DateUtil.format(DateUtil.parse(basic.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseStartDate().replaceAll("-", "")) >= Integer.parseInt(basic.getPlanStartDate().replaceAll("-", ""))) {
            startDate = DateUtil.format(DateUtil.parse(inData.getUserChooseStartDate()), DatePattern.NORM_DATE_PATTERN);
        }
        String endDate = DateUtil.format(DateUtil.parse(basic.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseEndDate().replaceAll("-", "")) <= Integer.parseInt(basic.getPlanEndDate().replaceAll("-", ""))) {
            endDate = DateUtil.format(DateUtil.parse(inData.getUserChooseEndDate()), DatePattern.NORM_DATE_PATTERN);
        }
        TichengProplanStoN proplanStoQuery = new TichengProplanStoN();
        proplanStoQuery.setClient(inData.getClient());
        proplanStoQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<CommonVo> storeCodes = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStos = tichengProplanStoMapper.select(proplanStoQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStos)) {
            for (TichengProplanStoN sto : tichengProplanStos) {
                tichengSto.add(sto.getStoCode());
            }
        }
        List<CommonVo> storeCommons = tichengProplanBasicMapper.selectStoreByStoreCodes(tichengSto, inData.getClient());


//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        Map<String, Map<String, Object>> settingMap = new HashMap<>();
        //计算每一个子方案，到最后进行按需汇总,此处最终的数据是settingig 对应的所有剔除折扣率之后的数据
        Map<String, List<Map<String, Object>>> settingIdDataListMap = new HashMap<>();
        //查询多个提成配置，及多个子方案
        TichengProplanSetting tichengProplanSettingQuery = new TichengProplanSetting();
        tichengProplanSettingQuery.setClient(inData.getClient());
        tichengProplanSettingQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan())) {
            //显示子方案的时候，是分别计算的
            if (StrUtil.isNotBlank(inData.getSettingId())) {
                tichengProplanSettingQuery.setId(Long.parseLong(inData.getSettingId()));
            }
        }
        tichengProplanSettingQuery.setDeleteFlag("0");

        List<TichengProplanSetting> tichengProplanSettings = tichengProplanSettingMapper.select(tichengProplanSettingQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanSettings)) {
            for (TichengProplanSetting setting : tichengProplanSettings) {
                Map<String, Object> singleSettingMap = new HashMap<>();
                //查询每个提成配置下的对应设置
                Long settingId = setting.getId();
                //  查询每个提成配置下商品提成商品
                List<String> proCodes = new ArrayList<>();
                TichengProplanProN tichengProplanProQuery = new TichengProplanProN();
                tichengProplanProQuery.setClient(inData.getClient());
                tichengProplanProQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
                tichengProplanProQuery.setSettingId(Integer.parseInt(settingId + ""));
                tichengProplanProQuery.setDeleteFlag("0");
                List<TichengProplanProN> tichengProplanPros = tichengProplanProNMapper.select(tichengProplanProQuery);
                if (CollUtil.isNotEmpty(tichengProplanPros)) {
                    for (int i = 0; i < tichengProplanPros.size(); i++) {
                        proCodes.add(tichengProplanPros.get(i).getProCode());
                    }
                }
                singleSettingMap.put("settingMainInfo", setting);
                singleSettingMap.put("settingProCodes", proCodes);
                singleSettingMap.put("settingProSetting", tichengProplanPros);
                settingMap.put(settingId + "", singleSettingMap);
            }
        }

//        //开始计算逻辑
//        SaleDateNumQueryCondition saleDateNumQueryCondition = new SaleDateNumQueryCondition();
//        saleDateNumQueryCondition.setClient(inData.getClient());
//        saleDateNumQueryCondition.setStartDate(startDate.replaceAll("-", ""));
//        saleDateNumQueryCondition.setEndDate(endDate.replaceAll("-", ""));
//        saleDateNumQueryCondition.setStoCodes(querySto);
//        saleDateNumQueryCondition.setType(null);
//        List<SaleDateNumRes> resList = handleSaleDateNumByCondition(saleDateNumQueryCondition);
//        String saleDays = "0";
//        if (CollectionUtil.isNotEmpty(resList)) {
//            //此处肯定是一条
//            saleDays = resList.get(0).getDays();
//        }


        PushMoneyByStoreV5OutData outTotalDataWithOutRule = new PushMoneyByStoreV5OutData();
        //先查询方案设置无关需要加载的数据--此统计为加盟商级别
        List<Map<String, Object>> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto);
        outTotalDataWithOutRule.setPlanName(basic.getPlanName());
        outTotalDataWithOutRule.setPlanId(inData.getPlanId());
        outTotalDataWithOutRule.setStartDate(basic.getPlanStartDate());

        //此处更改需求，需要处理用户选择时间与方案截止时间那个最小，取最小值
        startDate = DateUtil.format(DateUtil.parse(basic.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseStartDate()) >= Integer.parseInt(startDate.replaceAll("-", ""))) {
            startDate = DateUtil.format(DateUtil.parse(inData.getUserChooseStartDate()), DatePattern.NORM_DATE_PATTERN);
            outTotalDataWithOutRule.setStartDate(inData.getUserChooseStartDate());
        } else {
            outTotalDataWithOutRule.setStartDate(basic.getPlanStartDate());
        }
        endDate = DateUtil.format(DateUtil.parse(basic.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseEndDate()) <= Integer.parseInt(endDate.replaceAll("-", ""))) {
            endDate = DateUtil.format(DateUtil.parse(inData.getUserChooseEndDate()), DatePattern.NORM_DATE_PATTERN);
            outTotalDataWithOutRule.setEndtDate(inData.getUserChooseEndDate());
        } else {
            outTotalDataWithOutRule.setEndtDate(basic.getPlanEndDate());
        }
        if ("1".equals(basic.getPlanStatus())) {
            outTotalDataWithOutRule.setStatus("1");
        } else if ("2".equals(basic.getDeleteFlag())) {
            outTotalDataWithOutRule.setStatus("2");
        }
        outTotalDataWithOutRule.setType("单品提成");
        outTotalDataWithOutRule.setTypeValue("1");
        if (CollectionUtil.isNotEmpty(stoLvData)) {
            //正常应该是一条
            Map<String, Object> clientDataStatic = stoLvData.get(0);
            outTotalDataWithOutRule.setDays(BigDecimalUtil.toBigDecimal(clientDataStatic.get("days")));
            outTotalDataWithOutRule.setCostAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("costAmt")));
            outTotalDataWithOutRule.setYsAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("ysAmt")));
            outTotalDataWithOutRule.setAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("amt")));
            outTotalDataWithOutRule.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("grossProfitAmt")));
            outTotalDataWithOutRule.setGrossProfitRate(BigDecimalUtil.toBigDecimal(clientDataStatic.get("grossProfitRate")));
            outTotalDataWithOutRule.setZkAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.get("zkAmt")));
            outTotalDataWithOutRule.setZkRate(outTotalDataWithOutRule.getYsAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : outTotalDataWithOutRule.getZkAmt().divide(outTotalDataWithOutRule.getYsAmt(), 4, RoundingMode.HALF_UP));
        } else {
            outTotalDataWithOutRule.setDays(BigDecimal.ZERO);
            outTotalDataWithOutRule.setCostAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setYsAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setGrossProfitAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setGrossProfitRate(BigDecimal.ZERO);
            outTotalDataWithOutRule.setZkAmt(BigDecimal.ZERO);
            outTotalDataWithOutRule.setZkRate(BigDecimal.ZERO);
        }

        List<Map<String, Object>> saleDataOrigin = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(settingMap)) {
            List<String> allProCodes = new ArrayList<>();
            //处理所有需要进入统计的所有procode
            for (String settingId : settingMap.keySet()) {
                Map<String, Object> settingInfoMap = settingMap.get(settingId);
                List<String> proCodes = (List<String>) settingInfoMap.get("settingProCodes");
                if (CollectionUtil.isNotEmpty(proCodes)) {
                    proCodes.forEach(x -> {
                        if (!allProCodes.contains(x)) {
                            allProCodes.add(x);
                        }
                    });
                }
            }
            //考虑到性能问题，查询一把复合条件的数据之后进行数据的过滤，不然会产生多次查询
            saleDataOrigin = caculatePlanForRuleOrigin(startDate, endDate, inData.getClient(), querySto, allProCodes);
            for (String settingId : settingMap.keySet()) {
                Map<String, Object> settingInfoMap = settingMap.get(settingId);
                //单个提成设置的主信息
                TichengProplanSetting settingMainInfo = (TichengProplanSetting) settingInfoMap.get("settingMainInfo");
                //用户选择的商品编码
                List<String> proCodes = (List<String>) settingInfoMap.get("settingProCodes");
                //处理原始数据源
                if (CollectionUtil.isNotEmpty(saleDataOrigin)) {
                    List<Map<String, Object>> singleSettingSaleDataList = new ArrayList<>();
                    //处理每个子方案中的对应数据范围
                    for (Map<String, Object> x : saleDataOrigin) {
                        String procode = (String) x.get("PROCODE");
                        BigDecimal zkl = (BigDecimal) x.get("ZKL");
                        double grossProfitRate = MapUtils.getDouble(x, "GROSSPROFITRATE", 0.0);
                        double grossProfitAmt = MapUtils.getDouble(x, "GROSSPROFITAMT", 0.0);
                        // 负毛利率不参与提成
                        if ("0".equals(settingMainInfo.getPlanIfNegative())) {
                            if (grossProfitRate < 0) {
                                continue;
                            }
                        }
                        if (proCodes.contains(procode)) {
                            if (StrUtil.isNotBlank(settingMainInfo.getPlanRejectDiscountRateSymbol()) && StrUtil.isNotBlank(settingMainInfo.getPlanRejectDiscountRate())) {
//                                String symbol = tranceRejectDiscountRateSymbol(settingMainInfo.getPlanRejectDiscountRateSymbol());
                                if (!caculateSymbol(zkl, new BigDecimal(settingMainInfo.getPlanRejectDiscountRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), settingMainInfo.getPlanRejectDiscountRateSymbol())) {
                                    //只有不满足剔除条件的情况下才会删除，caculateSymbol为true的情况下是满足，不能添加，!表示不满足剔除条件，进行添加
                                    singleSettingSaleDataList.add(x);
                                }
                            } else {
                                singleSettingSaleDataList.add(x);
                            }
                        }
                    }
                    settingIdDataListMap.put(settingId, singleSettingSaleDataList);
                }
            }

        }

        //settingMap 中有每个子方案的配置信息   settingIdDataListMap中是分组之后所有满足实际配置条件的数据
        //根据以上两个map进行提成数据的计算,汇总到每个子方案，用于后期勾选子方案展示时一把带出
        for (String settingId : settingMap.keySet()) {
            PushMoneyByStoreV5TotalOutData outData = new PushMoneyByStoreV5TotalOutData();//每个子方案最终结果
            BeanUtils.copyProperties(outTotalDataWithOutRule, outData);//复制通用数据
            Map<String, Object> settingInfoMap = settingMap.get(settingId);
            TichengProplanSetting settingMainInfo = (TichengProplanSetting) settingInfoMap.get("settingMainInfo");
            outData.setCPlanName(settingMainInfo.getCPlanName());
            //单个提成设置的主信息
            Map<String, TichengProplanProN> proCodeProSettingMap = new HashMap<>();
            List<TichengProplanProN> settingProSetting = (List<TichengProplanProN>) settingInfoMap.get("settingProSetting");
            if (CollectionUtil.isNotEmpty(settingProSetting)) {
                settingProSetting.forEach(x -> {
                    proCodeProSettingMap.put(x.getProCode(), x);
                });
            }

            List<Map<String, Object>> dataList = settingIdDataListMap.get(settingId);
            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
            BigDecimal tcAmtTotal = BigDecimal.ZERO;
            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal tiTotal = BigDecimal.ZERO;

            //此处颗粒度为商品
            if (CollectionUtil.isNotEmpty(dataList)) {
                for (Map<String, Object> dbData : dataList) {
                    TiChenProRes proRes = new TiChenProRes();
                    BigDecimal qyt = BigDecimal.ZERO;
                    BigDecimal costAmt = BigDecimal.ZERO;
                    BigDecimal ysAmt = BigDecimal.ZERO;
                    BigDecimal amt = BigDecimal.ZERO;
                    BigDecimal grossProfitAmt = BigDecimal.ZERO;
                    String procode = (String) dbData.get("PROCODE");
                    proRes.setProId(procode);
                    if (dbData.get("qyt") == null) {
                        qyt = BigDecimalUtil.toBigDecimal(dbData.get("QYT"));
                    } else {
                        qyt = BigDecimalUtil.toBigDecimal(dbData.get("qyt"));
                    }
                    proRes.setQyt(qyt.toPlainString());
                    if (dbData.get("costAmt") == null) {
                        tcCostAmtTotal = tcCostAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("COSTAMT")));
                        costAmt = BigDecimalUtil.toBigDecimal(dbData.get("COSTAMT"));
                    } else {
                        tcCostAmtTotal = tcCostAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("costAmt")));
                        costAmt = BigDecimalUtil.toBigDecimal(dbData.get("costAmt"));
                    }
                    proRes.setCostAmt(costAmt.toPlainString());
                    if (dbData.get("ysamt") == null) {
                        tcYsAmtTotal = tcYsAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("YSAMT")));
                        ysAmt = BigDecimalUtil.toBigDecimal(dbData.get("YSAMT"));
                    } else {
                        tcYsAmtTotal = tcYsAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("ysamt")));
                        ysAmt = BigDecimalUtil.toBigDecimal(dbData.get("ysamt"));
                    }
                    proRes.setYsAmt(ysAmt.toPlainString());
                    if (dbData.get("amt") == null) {
                        tcAmtTotal = tcAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("AMT")));
                        amt = BigDecimalUtil.toBigDecimal(dbData.get("AMT"));
                    } else {
                        tcAmtTotal = tcAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("amt")));
                        amt = BigDecimalUtil.toBigDecimal(dbData.get("amt"));
                    }
                    proRes.setAmt(amt.toPlainString());
                    if (dbData.get("grossprofitamt") == null) {
                        tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("GROSSPROFITAMT")));
                        grossProfitAmt = BigDecimalUtil.toBigDecimal(dbData.get("GROSSPROFITAMT"));
                    } else {
                        tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(BigDecimalUtil.toBigDecimal(dbData.get("grossprofitamt")));
                        grossProfitAmt = BigDecimalUtil.toBigDecimal(dbData.get("grossprofitamt"));
                    }
                    proRes.setGrossProfitAmt(grossProfitAmt.toPlainString());
                    //理论上一定能取到相应的配置信息
                    TichengProplanProN tichengProplanProN = proCodeProSettingMap.get(procode);
                    Map<String, Object> tichengDataMap = handleProSettingValue(tichengProplanProN, proRes);
                    BigDecimal perTi = (BigDecimal) tichengDataMap.get("tiTotal");
                    tiTotal = tiTotal.add(BigDecimalUtil.format(perTi, 4));
                }

            }
            outData.setStoreCodes(storeCommons);
            outData.setStoNum(storeCommons.size());
            outData.setCPlanName(settingMainInfo.getCPlanName());
            outData.setTcCostAmt(tcCostAmtTotal);
            outData.setTcYsAmt(tcYsAmtTotal);
            outData.setTcAmt(tcAmtTotal);
            outData.setTcGrossProfitAmt(tcGrossProfitAmtTotal);
            outData.setTcGrossProfitRate(tcAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));
            outData.setTiTotal(tiTotal);
            outData.setDeductionWageAmtRate(outData.getAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outData.getAmt(), 4, RoundingMode.HALF_UP));//提成销售比
            outData.setDeductionWageGrossProfitRate(outData.getGrossProfitAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outData.getGrossProfitAmt(), 4, RoundingMode.HALF_UP));
            //处理按照规则汇总的数据
            outDatas.add(outData);
        }
//        PageInfo pageInfo = new PageInfo();
//        if (CollUtil.isNotEmpty(outDatas)) {
//            pageInfo.setList(outDatas);
//        }
//        return pageInfo;
//
        PageInfo pageInfo = new PageInfo();
        if (CollUtil.isNotEmpty(outDatas)) {
            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
            BigDecimal tcAmtTotal = BigDecimal.ZERO;
            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal tiTotal = BigDecimal.ZERO;
            // 集合列的数据汇总
            PushMoneyByStoreV5TotalOutData outTotal = new PushMoneyByStoreV5TotalOutData();
            BeanUtils.copyProperties(outDatas.get(0), outTotal);
            outTotal.setStartDate(basic.getPlanStartDate());
            outTotal.setEndtDate(basic.getPlanEndDate());
            for (PushMoneyByStoreV5TotalOutData storeData : outDatas) {
                tcCostAmtTotal = tcCostAmtTotal.add(storeData.getTcCostAmt() == null ? BigDecimal.ZERO : storeData.getTcCostAmt());
                tcYsAmtTotal = tcYsAmtTotal.add(storeData.getTcYsAmt() == null ? BigDecimal.ZERO : storeData.getTcYsAmt());
                tcAmtTotal = tcAmtTotal.add(storeData.getTcAmt() == null ? BigDecimal.ZERO : storeData.getTcAmt());
                tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(storeData.getTcGrossProfitAmt() == null ? BigDecimal.ZERO : storeData.getTcGrossProfitAmt());
                tiTotal = tiTotal.add(storeData.getTiTotal() == null ? BigDecimal.ZERO : storeData.getTiTotal());
                outTotal.setTiTotal(tiTotal);//提成金额
            }
            outTotal.setTcCostAmt(tcCostAmtTotal.setScale(4, RoundingMode.HALF_UP));
            outTotal.setTcYsAmt(tcYsAmtTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setTcAmt(tcAmtTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setTcGrossProfitAmt(tcGrossProfitAmtTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setTcGrossProfitRate(tcAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));
            outTotal.setTiTotal(tiTotal.setScale(2, RoundingMode.HALF_UP));
            outTotal.setDeductionWageAmtRate(outTotal.getAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outTotal.getAmt(), 4, RoundingMode.HALF_UP));//提成销售比
            outTotal.setDeductionWageGrossProfitRate(outTotal.getGrossProfitAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(outTotal.getGrossProfitAmt(), 4, RoundingMode.HALF_UP));

            if ("1".equals(inData.getIfShowZplan())) {
                //表示展示子方案
                outDatas.forEach(x -> {
                    x.setTcCostAmt(x.getTcCostAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTcYsAmt(x.getTcYsAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTcAmt(x.getTcAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTcGrossProfitAmt(x.getTcGrossProfitAmt().setScale(2, RoundingMode.HALF_UP));
                    x.setTiTotal(x.getTiTotal().setScale(2, RoundingMode.HALF_UP));
                });
                pageInfo.setList(outDatas);
//                pageInfo = new PageInfo(outDatas, outTotal);
            } else {
                pageInfo = new PageInfo(new ArrayList(Arrays.asList(outTotal)), outTotal);//合并一条
            }
//            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<StoreCommissionSummary> calcStoreCommissionSummaryOptimize(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        PageHelper.clearPage();
        // 获取单品提成方案列表
        List<StoreCommissionSummary> proCommissionMainPlans = tichengProplanProNMapper.selectProCommissionMainPlan(storeCommissionSummaryDO);

        String client = storeCommissionSummaryDO.getClient();
        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        // 是否员工提成汇总
        final boolean isSaleManSummary = 2 == summaryType;

        String startDate = storeCommissionSummaryDO.getStartDate();
        String endDate = storeCommissionSummaryDO.getEndDate();
        List<String> saleNames = storeCommissionSummaryDO.getSaleName();
        List<StoreCommissionSummary> tempProductCommissionSummaries = new ArrayList<>();

        LocalDate nowLocalDate = LocalDate.now();
        String nowDate = nowLocalDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        EmpSaleDetailInData inData = new EmpSaleDetailInData();
        inData.setClient(client);

        // 门店名称
        List<StoreSimpleInfoWithPlan> proCommissionStores = tichengProplanStoMapper.selectStoreByPlanIdAndClient(client, null);
        Map<String, String> storeSimpleInfoMap = proCommissionStores.stream()
                .collect(Collectors.toMap(StoreSimpleInfoWithPlan::getStoCode, StoreSimpleInfoWithPlan::getStoShortName, (oldValue, newValue) -> newValue));

        // 姓名
        Map<String, String> userNameMap = getUserNameMap(client, isSaleManSummary);

        List<StoreCommissionSummary> result = new ArrayList<>();

        // 1. 主方案遍历，获取每个方案对应时间范围内所有的成本额，应收金额等
        for (StoreCommissionSummary proCommissionMainPlan : proCommissionMainPlans) {

            String planId = String.valueOf(proCommissionMainPlan.getPlanId());

            // 2. 子方案遍历，获取每个子方案的提成金额
            //查询多个提成配置，及多个子方案
            TichengProplanSetting tichengProplanSettingQuery = new TichengProplanSetting();
            tichengProplanSettingQuery.setClient(client);
            tichengProplanSettingQuery.setPid(Long.parseLong(planId));
            tichengProplanSettingQuery.setCPlanName(storeCommissionSummaryDO.getSubPlanName());
            tichengProplanSettingQuery.setDeleteFlag("0");
            List<TichengProplanSetting> tichengProplanSettings = tichengProplanSettingMapper.selectAllByCondition(tichengProplanSettingQuery);
            // 没有子方案直接返回空
            if (CollectionUtil.isEmpty(tichengProplanSettings)) {
                continue;
            }

            // 获取主方案的销售汇总， 根据加盟商，门店，营业员，销售时间过滤
            List<StoreCommissionSummary> proMainPlanSaleDatas = new ArrayList<>();

            // 存放历史实时的提成明细
            List<StoreCommissionSummary> proCommissionSummaryDetails = new ArrayList<>();

            // 主方案销售数据处理
            boolean flag = commonPreDealWithCalcCommission(storeCommissionSummaryDO, proCommissionMainPlan,
                    proMainPlanSaleDatas, proCommissionSummaryDetails, startDate, endDate,
                    planId, nowDate, nowLocalDate);

            if (CollectionUtil.isEmpty(proMainPlanSaleDatas)) {
                continue;
            }

            // 单品提成方案
            Weekend<TichengProplanProN> tichengProplanProNWeekend = new Weekend<>(TichengProplanProN.class);
            tichengProplanProNWeekend.weekendCriteria()
                    .andEqualTo(TichengProplanProN::getClient, storeCommissionSummaryDO.getClient())
                    .andEqualTo(TichengProplanProN::getPid, planId)
                    .andEqualTo(TichengProplanProN::getDeleteFlag, "0");
            List<TichengProplanProN> commissionProPlan = tichengProplanProNMapper.selectByExample(tichengProplanProNWeekend);
            Map<String, List<TichengProplanProN>> commissionProPlanMap = commissionProPlan.stream()
                    .collect(Collectors.groupingBy(item -> item.getClient() + "_" + item.getPid() + "_" + item.getProCode() + "_" + item.getSettingId()));

            // 子方案名称
            Map<String, String> subPlanNameMap = tichengProplanSettings.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()),
                    TichengProplanSetting::getCPlanName, (oldVal, newVal) -> newVal));

            List<StoreCommissionSummary> proMainPlanSaleDatasContainSubPlan = new ArrayList<>();

            for (TichengProplanSetting tichengProplanSetting : tichengProplanSettings) {
                String settingId = tichengProplanSetting.getId() + "";

                String planRejectDiscountRateSymbol = tichengProplanSetting.getPlanRejectDiscountRateSymbol();
                String planRejectDiscountRate = tichengProplanSetting.getPlanRejectDiscountRate();
                String planIfNegative = tichengProplanSetting.getPlanIfNegative();

                // 子方案设置
                for (StoreCommissionSummary proMainPlanSaleData : proMainPlanSaleDatas) {
                    proMainPlanSaleData.setPlanId(Integer.parseInt(settingId));
                    proMainPlanSaleData.setSubPlanId(settingId);
                    String saleDate = ObjectUtil.defaultIfNull(proMainPlanSaleData.getSaleDate(), "").toString().replaceAll("-"
                            , "");
                    proMainPlanSaleData.setSaleDateStr(saleDate);
                    proMainPlanSaleDatasContainSubPlan.add(proMainPlanSaleData);
                }

                // 对于实时数据需要根据子方案计算提成金额

                // 如果查询时间大于等于当天
                if (flag) {

                    List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();

                    // 获取当天实时数据
                    List<EmpSaleDetailResVo> dbList = doGetRealTime(inData, isSaleManSummary, stoCodes, Integer.parseInt(planId), saleNames, nowDate);

                    for (EmpSaleDetailResVo detailResVo : dbList) {
                        // 属性设置到新的对象
                        StoreCommissionSummary temp = preSetAttr(detailResVo, storeSimpleInfoMap, planId, settingId);

                        temp.setPlanStartDate(proCommissionMainPlan.getPlanStartDate());
                        temp.setPlanEndDate(proCommissionMainPlan.getPlanEndDate());
                        BigDecimal amt = BigDecimalUtil.toBigDecimal(temp.getAmt());
                        BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(temp.getZkAmt());
                        BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(temp.getYsAmt());
                        // 折扣率
                        temp.setZkl(BigDecimalUtil.divide(zkAmt, ysAmt, 4));
                        // 毛利率
                        temp.setGrossProfitRate(BigDecimalUtil.divide(temp.getGrossProfitAmt(), amt, 4));
                        BigDecimal zkl = temp.getZkl();

                        // 如果设置了剔除折扣率
                        if (StrUtil.isAllNotBlank(planRejectDiscountRateSymbol, planRejectDiscountRate)) {
                            if (caculateSymbol(BigDecimalUtil.toBigDecimal(zkl), BigDecimalUtil.toBigDecimal(planRejectDiscountRate), planRejectDiscountRateSymbol)) {
                                continue;
                            }
                        }

                        // 负毛利率不参与提成
                        if ("0".equals(planIfNegative)) {
                            BigDecimal grossProfitAmt = BigDecimalUtil.format(temp.getGrossProfitAmt());
                            BigDecimal grossProfitRate = BigDecimalUtil.format(temp.getGrossProfitRate());
                            if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                                continue;
                            }
                        }

                        String proId = temp.getProId();
                        List<TichengProplanProN> commissionsProPlan = commissionProPlanMap.get(client + "_" + planId + "_" + proId + "_" + settingId);
                        if (CollectionUtil.isEmpty(commissionsProPlan)) {
                            continue;
                        }
                        TichengProplanProN tichengProplanProN = commissionsProPlan.get(0);
                        BigDecimal qyt = BigDecimalUtil.toBigDecimal(temp.getQyt());
                        BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(temp.getGrossProfitAmt());

                        // 计算提成金额
                        BigDecimal commissionAmt = calcCommissionTotal(tichengProplanProN, qyt, ysAmt, amt, grossProfitAmt);
                        temp.setCommissionAmt(commissionAmt);
                        // 提成商品成本额
                        temp.setCommissionCostAmt(BigDecimalUtil.toBigDecimal(temp.getCostAmt()));
                        // 提成商品销售额 销售额 = 应收金额 - 折扣金额
                        temp.setCommissionSales(BigDecimalUtil.toBigDecimal(temp.getAmt()));
                        // 提成商品毛利额 提成商品实收金额 - 提成商品成本金额
                        temp.setCommissionGrossProfitAmt(grossProfitAmt);
                        proCommissionSummaryDetails.add(temp);
                    }
                }
            }

            if (CollectionUtil.isEmpty(proCommissionSummaryDetails)) {
                continue;
            }
            result.addAll(dealResult(proCommissionSummaryDetails, storeCommissionSummaryDO, proMainPlanSaleDatasContainSubPlan,
                    subPlanNameMap, storeSimpleInfoMap, userNameMap, proCommissionMainPlan, 2));
        }
        return result;
    }

    @Deprecated
    @Override
    public List<StoreCommissionSummary> calcStoreCommissionSummary(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        PageHelper.clearPage();
        // 获取单品提成方案列表
        List<StoreCommissionSummary> selectProductCommissionPlans = tichengProplanProNMapper.selectProductCommissionPlan(storeCommissionSummaryDO);

        String client = storeCommissionSummaryDO.getClient();
        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        // 是否员工提成汇总
        final boolean isSaleManSummary = 2 == summaryType;
        Map<String, String> userNameMap = null;
        // 获取营业员姓名
        if (isSaleManSummary) {
            List<Map<String, String>> maps = tichengProplanBasicMapper.selectUserName(client);
            userNameMap = new HashMap<>();
            for (Map<String, String> map : maps) {
                userNameMap.put(map.get("userId"), map.get("userName"));
            }
        }

        String startDate = storeCommissionSummaryDO.getStartDate();
        String endDate = storeCommissionSummaryDO.getEndDate();
        List<String> saleNames = storeCommissionSummaryDO.getSaleName();
        List<StoreCommissionSummary> tempProductCommissionSummaries = new ArrayList<>();

        Boolean showSubPlan = storeCommissionSummaryDO.getShowSubPlan();
        LocalDate nowLocalDate = LocalDate.now();
        String nowDate = nowLocalDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        EmpSaleDetailInData inData = new EmpSaleDetailInData();
        inData.setClient(client);

        // 门店名称
        List<StoreSimpleInfoWithPlan> proCommissionStores = tichengProplanStoMapper.selectStoreByPlanIdAndClient(client, null);
        Map<String, String> storeSimpleInfoMap = proCommissionStores.stream()
                .collect(Collectors.toMap(StoreSimpleInfoWithPlan::getStoCode, StoreSimpleInfoWithPlan::getStoShortName, (oldValue, newValue) -> newValue));

        for (StoreCommissionSummary productCommissionPlan : selectProductCommissionPlans) {
            boolean flag = false;
            String tempEndDate = endDate;
            // 如果查询时间大于等于当天
            if (Integer.parseInt(endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
                // 把时间重置为昨天
                storeCommissionSummaryDO.setEndDate(nowLocalDate.minusDays(1).toString());
                tempEndDate = storeCommissionSummaryDO.getEndDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
                flag = true;
            }

            transferSearchDateRange(storeCommissionSummaryDO, startDate, tempEndDate, productCommissionPlan);

            // 如果比较后的结束时间大于方案结束时间 flag置为 false
            if (Integer.parseInt(productCommissionPlan.getPlanEndDate()) < Integer.parseInt(nowDate)) {
                flag = false;
            }

            String stoCode = productCommissionPlan.getStoCode();
            storeCommissionSummaryDO.setStoCodes(Splitter.on(CharUtil.COMMA).splitToList(stoCode));

            // 如果开始时间大于结束时间不查
            if (assertInTimeRange(storeCommissionSummaryDO.getStartDate(), storeCommissionSummaryDO.getEndDate(), nowDate)) {
                // 每个商品的记录, 包括没有提成的商品
                List<StoreCommissionSummary> storeCommissionSummaries = generateSqlAndResult(storeCommissionSummaryDO, isSaleManSummary);
                if (CollectionUtil.isNotEmpty(storeCommissionSummaries)) {
                    // 单品提成方案下商品数据
                    for (StoreCommissionSummary storeCommissionSummary : storeCommissionSummaries) {
                        StoreCommissionSummary temp = new StoreCommissionSummary();
                        BeanUtil.copyProperties(storeCommissionSummary, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        BeanUtil.copyProperties(productCommissionPlan, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        if (MapUtil.isNotEmpty(userNameMap)) {
                            temp.setSaleManName(userNameMap.get(temp.getSaleManCode()));
                        }
                        temp.setStoCode(storeCommissionSummary.getStoCode());
                        temp.setStoName(storeSimpleInfoMap.get(temp.getStoCode()));
                        tempProductCommissionSummaries.add(temp);
                    }
                }
            }

            // 如果查询时间大于等于当天
            if (flag) {
                // 获取当天实时数据
                inData.setPlanId(productCommissionPlan.getPlanId());
                inData.setToday(nowDate);
                inData.setStoArr(storeCommissionSummaryDO.getStoCodes());
                if (isSaleManSummary) {
                    inData.setNameSearchType("1");
                    inData.setNameSearchIdList(saleNames);
                }
                List<EmpSaleDetailResVo> dbList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
                renderCommissionDataWithToday(productCommissionPlan, dbList, userNameMap, storeSimpleInfoMap, tempProductCommissionSummaries);
            }
        }

        if (CollectionUtil.isNotEmpty(tempProductCommissionSummaries)) {
            List<Integer> planIds = selectProductCommissionPlans.stream().map(StoreCommissionSummary::getPlanId).distinct().collect(Collectors.toList());
            // 单品提成方案
            Weekend<TichengProplanProN> tichengProplanProNWeekend = new Weekend<>(TichengProplanProN.class);
            tichengProplanProNWeekend.weekendCriteria()
                    .andEqualTo(TichengProplanProN::getClient, storeCommissionSummaryDO.getClient())
                    .andIn(TichengProplanProN::getPid, planIds)
                    .andEqualTo(TichengProplanProN::getDeleteFlag, "0");
            List<TichengProplanProN> commissionProPlan = tichengProplanProNMapper.selectByExample(tichengProplanProNWeekend);
            Map<String, List<TichengProplanProN>> commissionProPlanMap = commissionProPlan.stream()
                    .collect(Collectors.groupingBy(item -> item.getClient() + "_" + item.getPid() + "_" + item.getProCode() + "_" + item.getSettingId()));

            Map<String, HashSet<String>> subPlanNum = new HashMap<>();
            // 合计销售天数
            Set<String> totalSaleDays = new HashSet<>();
            Iterator<StoreCommissionSummary> iterator = tempProductCommissionSummaries.iterator();
            while (iterator.hasNext()) {
                StoreCommissionSummary storeCommissionSummary = iterator.next();
                totalSaleDays.add(storeCommissionSummary.getSaleDate().toString());
                Integer planId = storeCommissionSummary.getPlanId();
                String proId = storeCommissionSummary.getProId();
                String subPlanId = storeCommissionSummary.getSubPlanId();

                if (subPlanNum.containsKey(String.valueOf(planId))) {
                    HashSet<String> subPlanIdSet = subPlanNum.get(String.valueOf(planId));
                    subPlanIdSet.add(subPlanId);
                    subPlanNum.put(String.valueOf(planId), subPlanIdSet);
                } else {
                    HashSet<String> subPlanIdSet = new HashSet<>();
                    subPlanIdSet.add(subPlanId);
                    subPlanNum.put(String.valueOf(planId), subPlanIdSet);
                }

                storeCommissionSummary.setPlanType(1);
                storeCommissionSummary.setPlanTypeName("单品提成");
                BigDecimal amt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getAmt());
                BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getZkAmt());
                BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getYsAmt());
                if (!storeCommissionSummary.getRealTime()) {
                    // 不是实时数据计算
                    // 折扣率
                    storeCommissionSummary.setZkl(BigDecimalUtil.divide(zkAmt, ysAmt, 4));
                    // 毛利率
                    storeCommissionSummary.setGrossProfitRate(BigDecimalUtil.divide(storeCommissionSummary.getGrossProfitAmt(), amt, 4));
                }

                BigDecimal zkl = storeCommissionSummary.getZkl();

                String planRejectDiscountRateSymbol = storeCommissionSummary.getPlanRejectDiscountRateSymbol();
                String planRejectDiscountRate = storeCommissionSummary.getPlanRejectDiscountRate();
                String planIfNegative = storeCommissionSummary.getPlanIfNegative();

                boolean flag = true;
                // 如果设置了剔除折扣率
                if (StrUtil.isAllNotBlank(planRejectDiscountRateSymbol, planRejectDiscountRate)) {
                    if (caculateSymbol(BigDecimalUtil.toBigDecimal(zkl), BigDecimalUtil.toBigDecimal(planRejectDiscountRate), planRejectDiscountRateSymbol)) {
                        flag = false;
                    }
                }

                // 负毛利率不参与提成
                if ("0".equals(planIfNegative)) {
                    BigDecimal grossProfitRate = BigDecimalUtil.format(storeCommissionSummary.getGrossProfitRate());
                    if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                        flag = false;
                    }
                }

                Integer settingId = storeCommissionSummary.getSettingId();
                List<TichengProplanProN> commissionsProPlan = commissionProPlanMap.get(client + "_" + planId + "_" + proId + "_" + settingId);
                if (CollectionUtil.isNotEmpty(commissionsProPlan)) {
                    TichengProplanProN tichengProplanProN = commissionsProPlan.get(0);
                    if (flag) {
                        BigDecimal qyt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getQyt());
                        BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getGrossProfitAmt());

                        // 计算提成金额
                        BigDecimal commissionAmt = calcCommissionTotal(tichengProplanProN, qyt, ysAmt, amt, grossProfitAmt);
                        storeCommissionSummary.setCommissionAmt(commissionAmt);
                        // 提成商品成本额
                        storeCommissionSummary.setCommissionCostAmt(BigDecimalUtil.toBigDecimal(storeCommissionSummary.getCostAmt()));
                        // 提成商品销售额 销售额 = 应收金额 - 折扣金额
                        storeCommissionSummary.setCommissionSales(BigDecimalUtil.toBigDecimal(storeCommissionSummary.getAmt()));
                        // 提成商品毛利额 提成商品实收金额 - 提成商品成本金额
                        storeCommissionSummary.setCommissionGrossProfitAmt(grossProfitAmt);
                    }
                }
            }
            // 是否日期汇总
            final boolean isSaleDateSummary = 2 == storeCommissionSummaryDO.getDisplayGranularity();
            Boolean showStore = storeCommissionSummaryDO.getShowStore();
            Map<String, Boolean> costAmtShowConfigMap = storeCommissionSummaryDO.getCostAmtShowConfigMap();
            return groupingByStore(tempProductCommissionSummaries, showSubPlan, showStore, isSaleManSummary,
                    isSaleDateSummary, totalSaleDays, 2, subPlanNum, costAmtShowConfigMap);
        }
        return new ArrayList<>();
    }

    private boolean handleEmpSaleDetailRejectDiscountRateSymbol(String symbol, BigDecimal left, BigDecimal right) {
        boolean flag = true;
        switch (symbol) {
            case "=":
                if (left.compareTo(right) == 0) {
                    flag = true;
                } else {
                    flag = false;
                }
                break;
            case ">":
                if (left.compareTo(right) > 0) {
                    flag = true;
                } else {
                    flag = false;
                }
                break;
            case ">=":
                if (left.compareTo(right) >= 0) {
                    flag = true;
                } else {
                    flag = false;
                }
                break;
            case "<":
                if (left.compareTo(right) < 0) {
                    flag = true;
                } else {
                    flag = false;
                }
                break;
            case "<=":
                if (left.compareTo(right) <= 0) {
                    flag = true;
                } else {
                    flag = false;
                }
                break;
        }
        return flag;
    }


    private ProLevelTiAmountRes handleProLevelTiAmount(BigDecimal qycSet, BigDecimal tichengAmtSet, BigDecimal tichengRateSet, BigDecimal baseAmount, BigDecimal qyc) {
        ProLevelTiAmountRes res = new ProLevelTiAmountRes();
        boolean resFlag = false;
        BigDecimal tiTotal = BigDecimal.ZERO;
        //倍数
        Integer multiple = 0;
        //余数
        if (qycSet != null && qyc.compareTo(qycSet) >= 0) {
            multiple = qyc.intValue() / qycSet.intValue();
            if (tichengAmtSet != null) {
                resFlag = false;
                if (tichengAmtSet != null) {
                    tiTotal = BigDecimalUtil.format(tichengAmtSet.multiply(BigDecimalUtil.toBigDecimal(multiple)), 4);
                }
            }
            res.setResFlag(resFlag);
            res.setTiTotal(tiTotal);
        } else {
            res.setResFlag(false);
            res.setTiTotal(tiTotal);
        }
        return res;
    }

    /**
     * 计算下一层级的余数
     *
     * @return
     */
    private Integer handleProLevelNextRemainder(BigDecimal qycSet, BigDecimal qyc) {
        Integer res = 0;
        //没有设置的情况下或者实际小于设置的情况下，直接返回实际销售数量
        if (qycSet == null || (qycSet != null && qyc.compareTo(qycSet) < 0)) {
            return qyc.intValue();
        }
        res = qyc.intValue() % qycSet.intValue();
        return res;
    }


    /**
     * 根据单品设置规则断定是按照比例提成还是按照固定金额提成模式
     * true表示按照比例提成
     *
     * @param proN
     * @return
     */
    private boolean getCaculateMode(TichengProplanProN proN) {
        boolean res = false;
        if (proN.getTichengRate() != null) {
            res = true;
        }
        if (proN.getTichengRate2() != null) {
            res = true;
        }
        if (proN.getTichengRate3() != null) {
            res = true;
        }
        return res;
    }


    private Integer getCaculateLev(TichengProplanProN proN, BigDecimal qyc) {
        Integer res = 0;
        if (proN.getSaleQty3() != null && qyc.compareTo(proN.getSaleQty3()) >= 0) {
            res = 3;
        }
        if (proN.getSaleQty2() != null && qyc.compareTo(proN.getSaleQty2()) >= 0) {
            res = 2;
        }
        if (proN.getSaleQty() != null && qyc.compareTo(proN.getSaleQty()) >= 0) {
            res = 1;
        }
        return res;
    }


    /**
     * 新版单品提成计算提成金额规则，
     * 计算逻辑根据实际销售数量qyc 命中对应设定段，余数再命中剩余段位,
     * 设置提成金额*除法结果作为单阶段的提成金额值
     *
     * @param proN
     * @param baseAmount
     * @param qyc
     * @return
     */
    private BigDecimal handleProTiAmount(TichengProplanProN proN, BigDecimal baseAmount, BigDecimal qyc) {
        BigDecimal tiToal = BigDecimal.ZERO;
        if (proN == null) {
            return tiToal;
        }
        BigDecimal saleQty1 = proN.getSaleQty();//一段设置数量
        BigDecimal saleQty2 = proN.getSaleQty2();//二段设置
        BigDecimal saleQty3 = proN.getSaleQty3();//三段设置

        //合法性校验,如果都没设置，直接进行过滤
        if (saleQty1 == null && saleQty2 == null && saleQty3 == null) {
            return tiToal;
        }

        //假设saleQty1<saleQty2<saleQty3

        //按照设定，目前三个级别中只能选择固定金额/或者比例模式提成的一种模式设定
        //按照比例的情况下,命中最高比例，按总额*比例进行最终提成金额计算即可
        //按照固定金额情况下，梯度计算
        if (getCaculateMode(proN)) {
            BigDecimal tiTotal = BigDecimal.ZERO;
            //表示按比例提成，直接进行计算返回结果即可
            Integer caculateLev = getCaculateLev(proN, qyc);
            if (caculateLev == 3) {
                if (proN.getTichengRate3() != null) {
                    tiTotal = baseAmount.multiply(proN.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                }
            } else if (caculateLev == 2) {
                if (proN.getTichengRate2() != null) {
                    tiTotal = baseAmount.multiply(proN.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                }
            } else if (caculateLev == 1) {
                if (proN.getTichengRate() != null) {
                    tiTotal = baseAmount.multiply(proN.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                }
            }
            return tiTotal;
        }


        //处理第三级别,进入到这边说明设置是按照固定金额进行提成
        ProLevelTiAmountRes res3 = handleProLevelTiAmount(saleQty3, proN.getTichengAmt3(), proN.getTichengRate3(), baseAmount, qyc);
        Integer remainder3 = handleProLevelNextRemainder(saleQty3, qyc);//取得3级计算的余数

        //累加三级计算结果
        tiToal = tiToal.add(res3.getTiTotal());
        //如果remainder3不是0还要进行2级计算
        if (remainder3 != 0) {
            ProLevelTiAmountRes res2 = handleProLevelTiAmount(saleQty2, proN.getTichengAmt2(), proN.getTichengRate2(), baseAmount, BigDecimalUtil.toBigDecimal(remainder3));
            Integer remainder2 = handleProLevelNextRemainder(saleQty2, BigDecimalUtil.toBigDecimal(remainder3));//取得2级计算的余数
            if (res2.isResFlag()) {
                //表示直接触发比例提成
                return res2.getTiTotal();
            } else {
                //累加2级计算结果
                tiToal = tiToal.add(res2.getTiTotal());
                if (remainder2 != 0) {
                    //如果remainder2不是0还要进行1级计算
                    ProLevelTiAmountRes res1 = handleProLevelTiAmount(saleQty1, proN.getTichengAmt(), proN.getTichengRate(), baseAmount, BigDecimalUtil.toBigDecimal(remainder2));
                    Integer remainder1 = handleProLevelNextRemainder(saleQty1, BigDecimalUtil.toBigDecimal(remainder2));//取得1级计算的余数
                    if (res1.isResFlag()) {
                        //表示直接触发比例提成
                        return res1.getTiTotal();
                    } else {
                        //累加2级计算结果
                        tiToal = tiToal.add(res1.getTiTotal());
                        //一级计算之后，这个时候剩下的余数，按照目前规则的计算规则，将不会参与提成计算，此时直接将目前的计算结果进行直接返回即可
                        return tiToal;
                    }
                } else {
                    //为0表示没有余数，计算结束
                    return tiToal;
                }
            }
        } else {
            //为0表示没有余数，计算结束
            return tiToal;
        }
    }


    private BigDecimal handleEmpSaleDetailTiTotal(TichengProplanSetting setting, EmpSaleDetailResVo
            detailResVo, Map<String, TichengProplanProN> proSettingMap) {
        TichengProplanProN proV3 = proSettingMap.get(detailResVo.getProSelfCode());
        BigDecimal tiToal = BigDecimal.ZERO;
        BigDecimal baseAmount = BigDecimal.ZERO;
        BigDecimal qyc = new BigDecimal(detailResVo.getQyt());

        //提成方式 1 按零售额提成 2 按销售额提成  3按毛利率提成
        if ("1".equals(setting.getPliantPercentageType())) {
            baseAmount = new BigDecimal(detailResVo.getYsAmt());
        } else if ("2".equals(setting.getPliantPercentageType())) {
            baseAmount = new BigDecimal(detailResVo.getAmt());
        } else if ("3".equals(setting.getPliantPercentageType())) {
            baseAmount = new BigDecimal(detailResVo.getGrossProfitAmt());
        }
        tiToal = BigDecimalUtil.format(handleProTiAmount(proV3, baseAmount, qyc), 4);
        return tiToal;
    }

    @Override
    public PageInfo empSaleDetailList(EmpSaleDetailInData inData,
                                      List<EmpSaleDetailResVo> empSaleDetailResVoList,
                                      Map<String, GaiaProductBusiness> productBusinessInfoMap,
                                      Map<String, Object> userIdNameMap,
                                      Map<String, GaiaProductClass> productClassMap,
                                      Map<String, Boolean> costAmtShowConfigMap) {
        PageInfo pageInfo = new PageInfo();

        List<EmpSaleDetailResVo> resList = new ArrayList<>();
        //  1.查询当前商品提成计划

        TichengProplanBasic basicQuery = new TichengProplanBasic();
        basicQuery.setClient(inData.getClient());
        basicQuery.setId(inData.getPlanId());
        //  提成主表
        TichengProplanBasic basic = tichengProplanBasicMapper.selectOne(basicQuery);
        String startDate = DateUtil.format(DateUtil.parse(basic.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(DateUtil.parse(basic.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

        TichengProplanStoN proplanStoQuery = new TichengProplanStoN();
        proplanStoQuery.setClient(inData.getClient());
        proplanStoQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStos = tichengProplanStoMapper.select(proplanStoQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStos)) {
            for (TichengProplanStoN sto : tichengProplanStos) {
                tichengSto.add(sto.getStoCode());
            }
        }
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        Map<String, Map<String, Object>> settingMap = new HashMap<>();
        //计算每一个子方案，到最后进行按需汇总,此处最终的数据是settingig 对应的所有剔除折扣率之后的数据
        Map<String, List<EmpSaleDetailResVo>> settingIdResListMap = new HashMap<>();
//        Map<String, List<Map<String, Object>>> settingIdDataListMap = new HashMap<>();
        //查询多个提成配置，及多个子方案
        TichengProplanSetting tichengProplanSettingQuery = new TichengProplanSetting();
        tichengProplanSettingQuery.setClient(inData.getClient());
        tichengProplanSettingQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
//        tichengProplanSettingQuery.setId(Long.parseLong(inData.getSettingId() + ""));
        tichengProplanSettingQuery.setDeleteFlag("0");
        List<TichengProplanSetting> tichengProplanSettings = tichengProplanSettingMapper.select(tichengProplanSettingQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanSettings)) {
            for (TichengProplanSetting setting : tichengProplanSettings) {
                Map<String, Object> singleSettingMap = new HashMap<>();
                //查询每个提成配置下的对应设置
                Long settingId = setting.getId();
                //  查询每个提成配置下商品提成商品
                List<String> proCodes = new ArrayList<>();
                TichengProplanProN tichengProplanProQuery = new TichengProplanProN();
                tichengProplanProQuery.setClient(inData.getClient());
                tichengProplanProQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
                tichengProplanProQuery.setSettingId(Integer.parseInt(settingId + ""));
                tichengProplanProQuery.setDeleteFlag("0");
                List<TichengProplanProN> tichengProplanPros = tichengProplanProNMapper.select(tichengProplanProQuery);
                if (CollUtil.isNotEmpty(tichengProplanPros)) {
                    for (int i = 0; i < tichengProplanPros.size(); i++) {
                        proCodes.add(tichengProplanPros.get(i).getProCode());
                    }
                }
                singleSettingMap.put("settingMainInfo", setting);
                singleSettingMap.put("settingProCodes", proCodes);
                singleSettingMap.put("settingProSetting", tichengProplanPros);
                settingMap.put(settingId + "", singleSettingMap);
            }
        }

        inData.setStoArr(querySto);
        if (CollectionUtil.isNotEmpty(settingMap)) {
            for (String settingId : settingMap.keySet()) {
                if (!settingId.equals(inData.getSettingId() + "")) {
                    continue;
                }
                Map<String, Object> settingInfoMap = settingMap.get(settingId);
                TichengProplanSetting setting = (TichengProplanSetting) settingInfoMap.get("settingMainInfo");
                List<String> proCodes = (List<String>) settingInfoMap.get("settingProCodes");
                List<TichengProplanProN> tichengProplanPros = (List<TichengProplanProN>) settingInfoMap.get("settingProSetting");
                Map<String, TichengProplanProN> proSettingMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(tichengProplanPros)) {
                    tichengProplanPros.forEach(x -> {
                        proSettingMap.put(x.getProCode(), x);
                    });
                }

                //循环计算每个提成子方案的实际情况
                if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {
                    List<EmpSaleDetailResVo> outDatas = new ArrayList<>();
                    for (EmpSaleDetailResVo detailResVo : empSaleDetailResVoList) {
                        BigDecimal tiTotal = BigDecimal.ZERO;
                        EmpSaleDetailResVo out = new EmpSaleDetailResVo();
                        //获取商品主信息
//                        GaiaProductBusiness gaiaProductBusiness = productBusinessInfoMap.get(detailResVo.getStoCode() + "-" + detailResVo.getProSelfCode());
                        if (!proCodes.contains(detailResVo.getProSelfCode())) {
//                            tiTotal = BigDecimal.ZERO;
                            continue;
                        } else {
                            //剔除折扣率
                            if (StringUtils.isNotBlank(setting.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotBlank(setting.getPlanRejectDiscountRate())) {
                                BigDecimal planRejectDiscountRate = new BigDecimal(setting.getPlanRejectDiscountRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                                if (handleEmpSaleDetailRejectDiscountRateSymbol(setting.getPlanRejectDiscountRateSymbol(), new BigDecimal(detailResVo.getZkl()), planRejectDiscountRate)) {
//                                    tiTotal = BigDecimal.ZERO;
                                    continue;
                                } else {
                                    //加入处理
                                    BigDecimal ti = handleEmpSaleDetailTiTotal(setting, detailResVo, proSettingMap);
                                    tiTotal = tiTotal.add(ti);
                                }
                            } else {
                                //加入处理
                                BigDecimal ti = handleEmpSaleDetailTiTotal(setting, detailResVo, proSettingMap);
                                tiTotal = tiTotal.add(ti);
                            }
                        }
                        if ("0".equals(setting.getPlanIfNegative())) {
                            // 负毛利率不参与提成
                            String grossProfitRate = detailResVo.getGrossProfitRate();
                            if (BigDecimalUtil.toBigDecimal(grossProfitRate).compareTo(BigDecimal.ZERO) < 0) {
//                                tiTotal = BigDecimal.ZERO;
                                continue;
                            }
                        }
                        String stoCode = detailResVo.getStoCode();
                        out.setPlanId(String.valueOf(basic.getId()));
                        out.setPlanName(basic.getPlanName());
                        out.setStartDate(basic.getPlanStartDate());
                        out.setEndDate(basic.getPlanEndDate());
                        out.setType("2");

                        out.setCPlanId(settingId);
                        out.setCPlanName(setting.getCPlanName());
                        out.setSalerId(detailResVo.getSalerId());
                        out.setSalerName(userIdNameMap.get(detailResVo.getSalerId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getSalerId()));
                        out.setStoCode(stoCode);
                        out.setStoName(detailResVo.getStoName());
                        out.setSaleDate(detailResVo.getSaleDate());
                        out.setGssdBillNo(detailResVo.getGssdBillNo());
                        out.setProSelfCode(detailResVo.getProSelfCode());
                        out.setSerial(detailResVo.getSerial());
                        out.setProName(detailResVo.getProName());
                        out.setProCommonName(detailResVo.getProCommonName());
                        out.setProSpecs(detailResVo.getProSpecs());
                        out.setProUnit(detailResVo.getProUnit());
                        out.setFactoryName(detailResVo.getFactoryName());
                        out.setProLsj(detailResVo.getProLsj());
                        out.setBatBatchNo(detailResVo.getBatBatchNo());
                        out.setBatExpiryDate(detailResVo.getBatExpiryDate());
                        out.setGssdValidDate(detailResVo.getGssdValidDate());
//                out.setValidDateDays();
                        out.setQyt(detailResVo.getQyt());
                        out.setCostAmt(detailResVo.getCostAmt());
                        out.setYsAmt(detailResVo.getYsAmt());
                        out.setAmt(detailResVo.getAmt());
                        out.setGrossProfitAmt(detailResVo.getGrossProfitAmt());
                        out.setGrossProfitRate(new BigDecimal(detailResVo.getGrossProfitRate()).toPlainString());
                        out.setZkAmt(detailResVo.getZkAmt());
                        out.setZkl(new BigDecimal(detailResVo.getZkl()).toPlainString());
                        out.setTiTotal(tiTotal.setScale(4, RoundingMode.HALF_UP).toPlainString());

                        out.setDeductionWageAmtRate(new BigDecimal(detailResVo.getAmt()).compareTo(BigDecimal.ZERO) == 0 ? "0" : tiTotal.divide(new BigDecimal(detailResVo.getAmt()), 4, RoundingMode.HALF_UP).toPlainString());
                        //new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO)==0?BigDecimal.ZERO:tiTotal.divide(new BigDecimal(detailResVo.getGrossProfitAmt()),2,RoundingMode.HALF_UP
                        out.setDeductionWageGrossProfitRate(new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO) == 0 ? "0" : tiTotal.divide(new BigDecimal(detailResVo.getGrossProfitAmt()), 4, RoundingMode.HALF_UP).toPlainString());
                        out.setGsshEmpId(detailResVo.getGsshEmpId());
                        out.setGsshEmpName(userIdNameMap.get(detailResVo.getGsshEmpId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getGsshEmpId()));
                        out.setDoctorId(detailResVo.getDoctorId());
                        out.setDoctorName(userIdNameMap.get(detailResVo.getDoctorId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getDoctorId()));
                        if (StrUtil.isNotBlank(detailResVo.getProClassCode())) {
                            GaiaProductClass gaiaProductClass = productClassMap.get(detailResVo.getProClassCode());
                            out.setProClassCode(gaiaProductClass.getProClassCode() + "-" + gaiaProductClass.getProClassName());
                            if (gaiaProductClass != null) {
                                out.setProMidClassCode(gaiaProductClass.getProMidClassCode() + "-" + gaiaProductClass.getProMidClassName());
                                out.setProBigClassCode(gaiaProductClass.getProBigClassCode() + "-" + gaiaProductClass.getProBigClassName());
                            }
                        }
                        out.setProSlaeClass(detailResVo.getProSlaeClass());
                        out.setProPosition(detailResVo.getProPosition());
                        out.setBatSupplierSalesman(detailResVo.getBatSupplierSalesman());
                        out.setBatSupplierCode(detailResVo.getBatSupplierCode());
                        out.setBatSupplierName(detailResVo.getBatSupplierName());

                        checkIsShowCostAmt(out, costAmtShowConfigMap, stoCode);

                        if (tiTotal.compareTo(BigDecimal.ZERO) != 0) {
                            outDatas.add(out);
                        } else if (tiTotal.compareTo(BigDecimal.ZERO) == 0 && StrUtil.isNotBlank(detailResVo.getGrossProfitAmt()) && new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO) == 0) {
                            outDatas.add(out);
                        }
                    }
                    settingIdResListMap.put(settingId, outDatas);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(settingIdResListMap)) {
            for (String settingId : settingIdResListMap.keySet()) {
                List<EmpSaleDetailResVo> empSaleDetailResVoList1 = settingIdResListMap.get(settingId);
                resList.addAll(empSaleDetailResVoList1);
            }
        }
        pageInfo.setList(resList);
        return pageInfo;
    }


    @Override
    public PageInfo empSaleDetailListOptimize(EmpSaleDetailInData inData,
                                              List<EmpSaleDetailResVo> userCommissionSummaryDetails,
                                              List<EmpSaleDetailResVo> empSaleDetailResVoList,
                                              Map<String, GaiaProductBusiness> productBusinessInfoMap,
                                              Map<String, Object> userIdNameMap,
                                              Map<String, GaiaProductClass> productClassMap,
                                              Map<String, Boolean> costAmtShowConfigMap) {
        PageInfo pageInfo = new PageInfo();

        List<EmpSaleDetailResVo> resList = new ArrayList<>();
        //  1.查询当前商品提成计划

        TichengProplanBasic basicQuery = new TichengProplanBasic();
        basicQuery.setClient(inData.getClient());
        basicQuery.setId(inData.getPlanId());
        //  提成主表
        TichengProplanBasic basic = tichengProplanBasicMapper.selectOne(basicQuery);
        String startDate = DateUtil.format(DateUtil.parse(basic.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(DateUtil.parse(basic.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

        TichengProplanStoN proplanStoQuery = new TichengProplanStoN();
        proplanStoQuery.setClient(inData.getClient());
        proplanStoQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStos = tichengProplanStoMapper.select(proplanStoQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStos)) {
            for (TichengProplanStoN sto : tichengProplanStos) {
                tichengSto.add(sto.getStoCode());
            }
        }
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        Map<String, Map<String, Object>> settingMap = new HashMap<>();
        //计算每一个子方案，到最后进行按需汇总,此处最终的数据是settingig 对应的所有剔除折扣率之后的数据
        Map<String, List<EmpSaleDetailResVo>> settingIdResListMap = new HashMap<>();
//        Map<String, List<Map<String, Object>>> settingIdDataListMap = new HashMap<>();
        //查询多个提成配置，及多个子方案
        TichengProplanSetting tichengProplanSettingQuery = new TichengProplanSetting();
        tichengProplanSettingQuery.setClient(inData.getClient());
        tichengProplanSettingQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
//        tichengProplanSettingQuery.setId(Long.parseLong(inData.getSettingId() + ""));
        tichengProplanSettingQuery.setDeleteFlag("0");
        List<TichengProplanSetting> tichengProplanSettings = tichengProplanSettingMapper.select(tichengProplanSettingQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanSettings)) {
            for (TichengProplanSetting setting : tichengProplanSettings) {
                Map<String, Object> singleSettingMap = new HashMap<>();
                //查询每个提成配置下的对应设置
                Long settingId = setting.getId();
                //  查询每个提成配置下商品提成商品
                List<String> proCodes = new ArrayList<>();
                TichengProplanProN tichengProplanProQuery = new TichengProplanProN();
                tichengProplanProQuery.setClient(inData.getClient());
                tichengProplanProQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
                tichengProplanProQuery.setSettingId(Integer.parseInt(settingId + ""));
                tichengProplanProQuery.setDeleteFlag("0");
                List<TichengProplanProN> tichengProplanPros = tichengProplanProNMapper.select(tichengProplanProQuery);
                if (CollUtil.isNotEmpty(tichengProplanPros)) {
                    for (int i = 0; i < tichengProplanPros.size(); i++) {
                        proCodes.add(tichengProplanPros.get(i).getProCode());
                    }
                }
                singleSettingMap.put("settingMainInfo", setting);
                singleSettingMap.put("settingProCodes", proCodes);
                singleSettingMap.put("settingProSetting", tichengProplanPros);
                settingMap.put(settingId + "", singleSettingMap);
            }
        }

        inData.setStoArr(querySto);
        if (CollectionUtil.isNotEmpty(settingMap)) {
            for (String settingId : settingMap.keySet()) {
                if (!settingId.equals(inData.getSettingId() + "")) {
                    continue;
                }
                Map<String, Object> settingInfoMap = settingMap.get(settingId);
                TichengProplanSetting setting = (TichengProplanSetting) settingInfoMap.get("settingMainInfo");
                List<String> proCodes = (List<String>) settingInfoMap.get("settingProCodes");
                List<TichengProplanProN> tichengProplanPros = (List<TichengProplanProN>) settingInfoMap.get("settingProSetting");
                Map<String, TichengProplanProN> proSettingMap = new HashMap<>();
                if (CollectionUtil.isNotEmpty(tichengProplanPros)) {
                    tichengProplanPros.forEach(x -> {
                        proSettingMap.put(x.getProCode(), x);
                    });
                }

                //循环计算每个提成子方案的实际情况
                if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {
                    List<EmpSaleDetailResVo> outDatas = new ArrayList<>();
                    for (EmpSaleDetailResVo detailResVo : empSaleDetailResVoList) {
                        BigDecimal tiTotal = BigDecimal.ZERO;
                        EmpSaleDetailResVo out = new EmpSaleDetailResVo();
                        //获取商品主信息
//                        GaiaProductBusiness gaiaProductBusiness = productBusinessInfoMap.get(detailResVo.getStoCode() + "-" + detailResVo.getProSelfCode());
                        if (!proCodes.contains(detailResVo.getProSelfCode())) {
//                            tiTotal = BigDecimal.ZERO;
                            continue;
                        } else {
                            //剔除折扣率
                            if (StringUtils.isNotBlank(setting.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotBlank(setting.getPlanRejectDiscountRate())) {
                                BigDecimal planRejectDiscountRate = new BigDecimal(setting.getPlanRejectDiscountRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                                if (handleEmpSaleDetailRejectDiscountRateSymbol(setting.getPlanRejectDiscountRateSymbol(), new BigDecimal(detailResVo.getZkl()), planRejectDiscountRate)) {
//                                    tiTotal = BigDecimal.ZERO;
                                    continue;
                                } else {
                                    //加入处理
                                    BigDecimal ti = handleEmpSaleDetailTiTotal(setting, detailResVo, proSettingMap);
                                    tiTotal = tiTotal.add(ti);
                                }
                            } else {
                                //加入处理
                                BigDecimal ti = handleEmpSaleDetailTiTotal(setting, detailResVo, proSettingMap);
                                tiTotal = tiTotal.add(ti);
                            }
                        }
                        if ("0".equals(setting.getPlanIfNegative())) {
                            // 负毛利率不参与提成
                            String grossProfitRate = detailResVo.getGrossProfitRate();
                            if (BigDecimalUtil.toBigDecimal(grossProfitRate).compareTo(BigDecimal.ZERO) < 0) {
//                                tiTotal = BigDecimal.ZERO;
                                continue;
                            }
                        }
                        String stoCode = detailResVo.getStoCode();
                        out.setPlanId(String.valueOf(basic.getId()));
                        out.setPlanName(basic.getPlanName());
                        out.setStartDate(basic.getPlanStartDate());
                        out.setEndDate(basic.getPlanEndDate());
                        out.setType("2");

                        out.setCPlanId(settingId);
                        out.setCPlanName(setting.getCPlanName());
                        out.setSalerId(detailResVo.getSalerId());
                        out.setSalerName(userIdNameMap.get(detailResVo.getSalerId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getSalerId()));
                        out.setStoCode(stoCode);
                        out.setStoName(detailResVo.getStoName());
                        out.setSaleDate(detailResVo.getSaleDate());
                        out.setGssdBillNo(detailResVo.getGssdBillNo());
                        out.setProSelfCode(detailResVo.getProSelfCode());
                        out.setSerial(detailResVo.getSerial());
                        out.setProName(detailResVo.getProName());
                        out.setProCommonName(detailResVo.getProCommonName());
                        out.setProSpecs(detailResVo.getProSpecs());
                        out.setProUnit(detailResVo.getProUnit());
                        out.setFactoryName(detailResVo.getFactoryName());
                        out.setProLsj(detailResVo.getProLsj());
                        out.setBatBatchNo(detailResVo.getBatBatchNo());
                        out.setBatExpiryDate(detailResVo.getBatExpiryDate());
                        out.setGssdValidDate(detailResVo.getGssdValidDate());
//                out.setValidDateDays();
                        out.setQyt(detailResVo.getQyt());
                        out.setCostAmt(detailResVo.getCostAmt());
                        out.setYsAmt(detailResVo.getYsAmt());
                        out.setAmt(detailResVo.getAmt());
                        out.setGrossProfitAmt(detailResVo.getGrossProfitAmt());
                        out.setGrossProfitRate(new BigDecimal(detailResVo.getGrossProfitRate()).toPlainString());
                        out.setZkAmt(detailResVo.getZkAmt());
                        out.setZkl(new BigDecimal(detailResVo.getZkl()).toPlainString());
                        out.setTiTotal(tiTotal.setScale(4, RoundingMode.HALF_UP).toPlainString());

                        out.setDeductionWageAmtRate(new BigDecimal(detailResVo.getAmt()).compareTo(BigDecimal.ZERO) == 0 ? "0" : tiTotal.divide(new BigDecimal(detailResVo.getAmt()), 4, RoundingMode.HALF_UP).toPlainString());
                        //new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO)==0?BigDecimal.ZERO:tiTotal.divide(new BigDecimal(detailResVo.getGrossProfitAmt()),2,RoundingMode.HALF_UP
                        out.setDeductionWageGrossProfitRate(new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO) == 0 ? "0" : tiTotal.divide(new BigDecimal(detailResVo.getGrossProfitAmt()), 4, RoundingMode.HALF_UP).toPlainString());
                        out.setGsshEmpId(detailResVo.getGsshEmpId());
                        out.setGsshEmpName(userIdNameMap.get(detailResVo.getGsshEmpId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getGsshEmpId()));
                        out.setDoctorId(detailResVo.getDoctorId());
                        out.setDoctorName(userIdNameMap.get(detailResVo.getDoctorId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getDoctorId()));
                        if (StrUtil.isNotBlank(detailResVo.getProClassCode())) {
                            GaiaProductClass gaiaProductClass = productClassMap.get(detailResVo.getProClassCode());
                            out.setProClassCode(gaiaProductClass.getProClassCode() + "-" + gaiaProductClass.getProClassName());
                            if (gaiaProductClass != null) {
                                out.setProMidClassCode(gaiaProductClass.getProMidClassCode() + "-" + gaiaProductClass.getProMidClassName());
                                out.setProBigClassCode(gaiaProductClass.getProBigClassCode() + "-" + gaiaProductClass.getProBigClassName());
                            }
                        }
                        out.setProSlaeClass(detailResVo.getProSlaeClass());
                        out.setProPosition(detailResVo.getProPosition());
                        out.setBatSupplierSalesman(detailResVo.getBatSupplierSalesman());
                        out.setBatSupplierCode(detailResVo.getBatSupplierCode());
                        out.setBatSupplierName(detailResVo.getBatSupplierName());

                        checkIsShowCostAmt(out, costAmtShowConfigMap, stoCode);

                        if (tiTotal.compareTo(BigDecimal.ZERO) != 0) {
                            outDatas.add(out);
                        } else if (tiTotal.compareTo(BigDecimal.ZERO) == 0 && StrUtil.isNotBlank(detailResVo.getGrossProfitAmt()) && new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO) == 0) {
                            outDatas.add(out);
                        }
                    }

                    for (EmpSaleDetailResVo userCommissionSummaryDetail : userCommissionSummaryDetails) {
                        // 验证是否显示金额
                        checkIsShowCostAmt(userCommissionSummaryDetail,
                                costAmtShowConfigMap,
                                userCommissionSummaryDetail.getStoCode());
                    }
                    // 历史提成数据添加
                    outDatas.addAll(userCommissionSummaryDetails);

                    settingIdResListMap.put(settingId, outDatas);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(settingIdResListMap)) {
            for (String settingId : settingIdResListMap.keySet()) {
                List<EmpSaleDetailResVo> empSaleDetailResVoList1 = settingIdResListMap.get(settingId);
                resList.addAll(empSaleDetailResVoList1);
            }
        }
        pageInfo.setList(resList);
        return pageInfo;
    }


    /**
     * @param commissionAmt        提仓金额
     * @param pliantPercentageType 提成级别
     * @param ysAmt                应收金额
     * @param amt                  实收金额
     * @param grossProfitAmt       毛利额
     * @param commissionRate       提成比例
     * @return BigDecimal
     */
    private BigDecimal calcCommissionAmt(BigDecimal commissionAmt, String pliantPercentageType, BigDecimal ysAmt,
                                         BigDecimal amt, BigDecimal grossProfitAmt, BigDecimal commissionRate) {
        BigDecimal commissionAmtRes;
        if (ObjectUtil.isNotEmpty(commissionAmt)) {
            commissionAmtRes = commissionAmt;
        } else {
            BigDecimal d = BigDecimal.ZERO;
            if ("1".equals(pliantPercentageType)) {
                d = ysAmt.multiply(commissionRate.divide(BigDecimalUtil.ONE_HUNDRED, 4, BigDecimal.ROUND_HALF_UP));
            } else if ("2".equals(pliantPercentageType)) {
                d = amt.multiply(commissionRate.divide(BigDecimalUtil.ONE_HUNDRED, 4, BigDecimal.ROUND_HALF_UP));
            } else if ("3".equals(pliantPercentageType)) {
                d = grossProfitAmt.multiply(commissionRate.divide(BigDecimalUtil.ONE_HUNDRED, 4, BigDecimal.ROUND_HALF_UP));
            }
            commissionAmtRes = d;
        }
        return commissionAmtRes;
    }


    /**
     * 计算提成金额
     *
     * @param tichengProplanProN 提成方案
     * @param qyt                数量
     * @param ysAmt              应收金额
     * @param amt                实收金额
     * @param grossProfitAmt     毛利额
     * @return BigDecimal
     */
    private BigDecimal calcCommissionTotal(TichengProplanProN tichengProplanProN, BigDecimal qyt, BigDecimal
            ysAmt, BigDecimal amt, BigDecimal grossProfitAmt) {
        String pliantPercentageType = tichengProplanProN.getPliantPercentageType();
        BigDecimal baseAmount = BigDecimal.ZERO;
        //提成方式 1 按零售额提成 2 按销售额提成  3按毛利率提成
        if ("1".equals(pliantPercentageType)) {
            baseAmount = ysAmt;
        } else if ("2".equals(pliantPercentageType)) {
            baseAmount = amt;
        } else if ("3".equals(pliantPercentageType)) {
            baseAmount = grossProfitAmt;
        }
        return BigDecimalUtil.format(handleProTiAmount(tichengProplanProN, baseAmount, qyt), 4);
    }

    /**
     * 单品提成汇总sql
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param isSaleManSummary         isSaleManSummary
     * @return List<StoreCommissionSummary>
     */
    public List<StoreCommissionSummary> generateSqlAndResult(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                             final boolean isSaleManSummary) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("\nSELECT\n" +
                "\tgssd.CLIENT client,\n" +
                "\tgssd.GSSD_BR_ID stoCode,\n" +
                "\tgssd.GSSD_SERIAL,\n" +
                "\tgssd.GSSD_DATE saleDate,\n");
        if (isSaleManSummary) {
            // 员工提成汇总
            sqlSb.append("\tgssd.GSSD_SALER_ID saleManCode,\n");
        }
        sqlSb.append("\tgssd.GSSD_PRO_ID proId,\n" +
                "\tSUM(gssd.GSSD_QTY) qyt,\n" +
                "\tROUND(SUM(gssd.GSSD_MOV_PRICE), 4 ) costAmt,\n" +
                "\tROUND((SUM(gssd.GSSD_AMT) + SUM(gssd.GSSD_ZK_AMT)), 2) ysAmt,\n" +
                "\tROUND(SUM(gssd.GSSD_AMT), 2) amt,\n" +
                "\tROUND(ROUND(SUM(gssd.GSSD_AMT), 2) - ROUND(SUM(gssd.GSSD_MOV_PRICE), 4), 2) grossProfitAmt,\n" +
                "\tROUND(SUM(gssd.GSSD_ZK_AMT), 2) zkAmt\n" +
                "FROM GAIA_SD_SALE_D gssd \n");

        sqlSb.append("INNER JOIN GAIA_STORE_DATA gsd ON gssd.CLIENT = gsd.CLIENT AND gssd.GSSD_BR_ID = gsd.STO_CODE \n");
        sqlSb.append("WHERE gssd.CLIENT = '");
        sqlSb.append(storeCommissionSummaryDO.getClient()).append("'\n");

        if (isSaleManSummary) {
            // 员工提成汇总增加营业员筛选
            List<String> saleNames = storeCommissionSummaryDO.getSaleName();
            sqlSb.append("AND gssd.GSSD_SALER_ID !='' AND gssd.GSSD_SALER_ID IS NOT NULL\n");
            if (CollectionUtil.isNotEmpty(saleNames)) {
                sqlSb.append(" AND gssd.GSSD_SALER_ID IN ")
                        .append(CommonUtil.queryByBatch(saleNames));
            }
        }

        String startDate = storeCommissionSummaryDO.getStartDate();
        if (StringUtils.isNotBlank(startDate)) {
            sqlSb.append("AND gssd.GSSD_DATE >= '").append(startDate).append("'\n");
        }
        String endDate = storeCommissionSummaryDO.getEndDate();
        if (StringUtils.isNotBlank(endDate)) {
            sqlSb.append("AND gssd.GSSD_DATE <= '").append(endDate).append("'\n");
        }

        List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
        if (ObjectUtil.isNotEmpty(stoCodes)) {
            sqlSb.append("AND gssd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(stoCodes))
                    .append("\n");
        }

        sqlSb.append("GROUP BY gssd.CLIENT,gssd.GSSD_BR_ID,gssd.GSSD_BILL_NO,gssd.GSSD_PRO_ID,gssd.GSSD_DATE,gssd.GSSD_SERIAL");
        if (isSaleManSummary) {
            // 员工提成汇总
            sqlSb.append(",gssd.GSSD_SALER_ID");
        }
        sqlSb.append("\nORDER BY gssd.GSSD_BR_ID,gssd.GSSD_DATE");
        log.warn("单品提成 sql: {}", sqlSb.toString());
        return kylinJdbcTemplate.query(sqlSb.toString(), BeanPropertyRowMapper.newInstance(StoreCommissionSummary.class));
    }


}


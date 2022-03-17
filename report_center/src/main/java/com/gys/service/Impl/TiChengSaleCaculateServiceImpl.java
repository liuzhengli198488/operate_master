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
import com.gys.common.data.CommonVo;
import com.gys.common.data.EmpSaleDetailResVo;
import com.gys.common.data.PageInfo;
import com.gys.common.data.StoreSimpleInfoWithPlan;
import com.gys.common.exception.BusinessException;
import com.gys.entity.*;
import com.gys.entity.data.MonthPushMoney.*;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV5OutData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV5TotalOutData;
import com.gys.entity.data.commissionplan.CommissionSummaryDetailDTO;
import com.gys.entity.data.commissionplan.StoreAvgAmt;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.mapper.*;
import com.gys.service.TiChengCaculateService;
import com.gys.util.BigDecimalUtil;
import com.gys.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import tk.mybatis.mapper.entity.Example;
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
public class TiChengSaleCaculateServiceImpl extends AbstractTiChangCalcServiceImpl implements TiChengCaculateService {

    private GaiaTichengPlanZMapper tichengPlanZMapper;

    private GaiaTichengSaleplanZMapper tichengSaleplanZMapper;

    private GaiaTichengSaleplanStoMapper tichengSaleplanStoMapper;

    private GaiaTichengSaleplanMMapper tichengSaleplanMMapper;

    private GaiaTichengRejectClassMapper tichengRejectClassMapper;

    private GaiaTichengRejectProMapper tichengRejectProMapper;

    private GaiaProductBusinessMapper gaiaProductBusinessMapper;

    private UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper;

    private JdbcTemplate kylinJdbcTemplate;

    public TiChengSaleCaculateServiceImpl(GaiaTichengPlanZMapper tichengPlanZMapper, GaiaTichengSaleplanZMapper tichengSaleplanZMapper,
                                          GaiaTichengSaleplanStoMapper tichengSaleplanStoMapper, GaiaTichengSaleplanMMapper tichengSaleplanMMapper,
                                          GaiaTichengRejectClassMapper tichengRejectClassMapper, GaiaTichengRejectProMapper tichengRejectProMapper,
                                          JdbcTemplate kylinJdbcTemplate,
                                          GaiaProductBusinessMapper gaiaProductBusinessMapper) {
        this.tichengPlanZMapper = tichengPlanZMapper;
        this.tichengSaleplanZMapper = tichengSaleplanZMapper;
        this.tichengSaleplanStoMapper = tichengSaleplanStoMapper;
        this.tichengSaleplanMMapper = tichengSaleplanMMapper;
        this.tichengRejectClassMapper = tichengRejectClassMapper;
        this.tichengRejectProMapper = tichengRejectProMapper;
        this.kylinJdbcTemplate = kylinJdbcTemplate;
        this.gaiaProductBusinessMapper = gaiaProductBusinessMapper;
    }

    public TiChengSaleCaculateServiceImpl(GaiaTichengPlanZMapper tichengPlanZMapper, GaiaTichengSaleplanZMapper tichengSaleplanZMapper,
                                          GaiaTichengSaleplanStoMapper tichengSaleplanStoMapper, GaiaTichengSaleplanMMapper tichengSaleplanMMapper,
                                          GaiaTichengRejectClassMapper tichengRejectClassMapper, GaiaTichengRejectProMapper tichengRejectProMapper,
                                          JdbcTemplate kylinJdbcTemplate,
                                          GaiaProductBusinessMapper gaiaProductBusinessMapper,
                                          UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper) {
        this.tichengPlanZMapper = tichengPlanZMapper;
        this.tichengSaleplanZMapper = tichengSaleplanZMapper;
        this.tichengSaleplanStoMapper = tichengSaleplanStoMapper;
        this.tichengSaleplanMMapper = tichengSaleplanMMapper;
        this.tichengRejectClassMapper = tichengRejectClassMapper;
        this.tichengRejectProMapper = tichengRejectProMapper;
        this.kylinJdbcTemplate = kylinJdbcTemplate;
        this.gaiaProductBusinessMapper = gaiaProductBusinessMapper;
        this.userCommissionSummaryDetailMapper = userCommissionSummaryDetailMapper;
        super.tichengPlanZMapper = tichengPlanZMapper;
        super.userCommissionSummaryDetailMapper = userCommissionSummaryDetailMapper;
        super.kylinJdbcTemplate = kylinJdbcTemplate;
    }

    private boolean handleDecorateCaculatePlanForRulePlanRejectDiscountRateSymbol(String symbol, BigDecimal left, BigDecimal right) {
        boolean res = false;
        switch (symbol) {
            case "=":
                res = left.compareTo(right) == 0;
                break;
            case ">":
                res = left.compareTo(right) == 1;
                break;
            case ">=":
                res = left.compareTo(right) >= 0;
                break;
            case "<":
                res = left.compareTo(right) == -1;
                break;
            case "<=":
                res = left.compareTo(right) <= 0;
                break;
        }
        return res;
    }


    private List<TiChenProRes> decorateCaculatePlanForRule(List<TiChenProRes> kylinData, EmpSaleDetailInData inData, GaiaTichengSaleplanZ tichengZ, List<String> rejectPro) {
        List<TiChenProRes> resList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(kylinData)) {
            resList = kylinData;
        }
        //因为是门店级别的数据，需要处理
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(inData.getEndDate().replaceAll("-", "")) >= Integer.parseInt(today)) {
            List<EmpSaleDetailResVo> toDayAllEmpSaleDetailList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
            //处理一下数据，过滤掉需要过滤的数据
            for (EmpSaleDetailResVo resVo : toDayAllEmpSaleDetailList) {
                TiChenProRes resObject = new TiChenProRes();
                //过滤折扣率
                String zkl = resVo.getZkl();
                // 如果设置了剔除折扣率
                if (StringUtils.isNotEmpty(tichengZ.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(tichengZ.getPlanRejectDiscountRate())) {
                    if (handleDecorateCaculatePlanForRulePlanRejectDiscountRateSymbol(tichengZ.getPlanRejectDiscountRateSymbol(), new BigDecimal(zkl).multiply(new BigDecimal(100)), new BigDecimal(tichengZ.getPlanRejectDiscountRate()))) {
                        continue;
                    }
                }
                //过滤商品分类
                if (CollectionUtil.isNotEmpty(inData.getRejectProClass())) {
                    if (inData.getRejectProClass().contains(resVo.getProClassCode())) {
                        continue;
                    }
                }
                //过滤商品
                if (CollectionUtil.isNotEmpty(rejectPro) && rejectPro.contains(resVo.getProSelfCode())) {
                    continue;
                }
                if ("0".equals(tichengZ.getPlanAmtWay())) {
                    resObject.setBaseAmount(resVo.getAmt());
                } else if ("1".equals(tichengZ.getPlanAmtWay())) {
                    resObject.setBaseAmount(BigDecimalUtil.toBigDecimal(resVo.getAmt()).subtract(BigDecimalUtil.toBigDecimal(resVo.getCostAmt())).setScale(4, RoundingMode.HALF_UP).toPlainString());
                }
                //  毛利率: 0.商品毛利率 = 零售价*数量 -成本额  1.销售毛利率 = 实收金额-成本额
                if ("0".equals(tichengZ.getPlanRateWay())) {
                    if (StrUtil.isNotBlank(resVo.getYsAmt()) && new BigDecimal(resVo.getYsAmt()).compareTo(BigDecimal.ZERO) != 0) {
                        resObject.setGrossProfitRate((BigDecimalUtil.toBigDecimal(resVo.getYsAmt()).subtract(BigDecimalUtil.toBigDecimal(resVo.getCostAmt()))).divide(BigDecimalUtil.toBigDecimal(resVo.getYsAmt()), 4, RoundingMode.HALF_UP).toPlainString());
                    }
                } else if ("1".equals(tichengZ.getPlanRateWay())) {
                    if (BigDecimalUtil.toBigDecimal(resVo.getAmt()).compareTo(BigDecimal.ZERO) == 0) {
                        resObject.setGrossProfitRate(BigDecimal.ZERO.toPlainString());
                    } else {
                        resObject.setGrossProfitRate((BigDecimalUtil.toBigDecimal(resVo.getAmt()).subtract(BigDecimalUtil.toBigDecimal(resVo.getCostAmt()))).divide(BigDecimalUtil.toBigDecimal(resVo.getAmt()), 4, RoundingMode.HALF_UP).toPlainString());
                    }
                }
                //其余商品需要加入kylin数据合并成最终数据
                resObject.setBrId(resVo.getStoCode());
                resObject.setBrName(resVo.getStoCode());
                resObject.setBillNo(resVo.getGssdBillNo());
                resObject.setProId(resVo.getProSelfCode());
                resObject.setSerial(resVo.getSerial());
                resObject.setQyt(resVo.getQyt());
                resObject.setAmt(resVo.getAmt());
                resObject.setYsAmt(resVo.getYsAmt());
                resObject.setGrossProfitAmt(resVo.getGrossProfitAmt());
//                resObject.setGrossProfitRate(resVo.getGrossProfitRate());
                resObject.setZkAmt(resVo.getZkAmt());
                resObject.setZkl(StrUtil.isNotBlank(resVo.getZkl()) ? new BigDecimal(resVo.getZkl()).setScale(4, RoundingMode.HALF_UP).toPlainString() : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP).toPlainString());
                resObject.setCostAmt(resVo.getCostAmt());
                resObject.setRealTime("true");
                resList.add(resObject);
            }
        }
        return resList;
    }

    private List<TiChenSTORes> decorateCaculatePlanForAllProduct(List<TiChenSTORes> kylinData, EmpSaleDetailInData inData) {
        List<TiChenSTORes> resList = new ArrayList<>();
        //因为是门店级别的数据，需要处理
        Map<String, List<EmpSaleDetailResVo>> empSaleDetailResVoMap = new HashMap<>();

        String today = DateUtil.format(new Date(), "yyyyMMdd");
        if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(inData.getEndDate().replaceAll("-", "")) >= Integer.parseInt(today)) {
            List<EmpSaleDetailResVo> toDayAllEmpSaleDetailList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
            if (CollectionUtil.isNotEmpty(toDayAllEmpSaleDetailList)) {
                for (EmpSaleDetailResVo resVo : toDayAllEmpSaleDetailList) {
                    String key = resVo.getStoCode();
                    if (empSaleDetailResVoMap.containsKey(key)) {
                        List<EmpSaleDetailResVo> stoData = empSaleDetailResVoMap.get(key);
                        stoData.add(resVo);
                        empSaleDetailResVoMap.put(key, stoData);
                    } else {
                        List<EmpSaleDetailResVo> stoData = new ArrayList<>();
                        stoData.add(resVo);
                        empSaleDetailResVoMap.put(key, stoData);
                    }
                }
            }

        }
        if (CollectionUtil.isNotEmpty(kylinData)) {
            if (CollectionUtil.isNotEmpty(empSaleDetailResVoMap)) {
                //当天有数据的情况
                //门店级别数据，进行处理
                for (TiChenSTORes data : kylinData) {
                    String stoCode = data.getBrId();
                    BigDecimal amtToTal = BigDecimal.ZERO;
                    BigDecimal ysAmtToTal = BigDecimal.ZERO;
                    BigDecimal grossProfitAmtToTal = BigDecimal.ZERO;
                    BigDecimal zkAmtToTal = BigDecimal.ZERO;
                    BigDecimal costAmtToTal = BigDecimal.ZERO;
                    List<EmpSaleDetailResVo> empSaleDetailResVoList = empSaleDetailResVoMap.get(stoCode);
                    if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {
                        for (EmpSaleDetailResVo resVo : empSaleDetailResVoList) {
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
                        data.setBrId(stoCode);
                        data.setBrName(empSaleDetailResVoList.get(0).getStoName());
                        String days = data.getDays();
                        Integer finalDays = 1;
                        if (StrUtil.isNotBlank(days)) {
                            finalDays = Integer.parseInt(days) + finalDays;
                        }
                        data.setDays(finalDays.toString());
                        String amt = String.valueOf(data.getAmt());
                        if (StrUtil.isNotBlank(amt)) {
                            amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
                        }
                        data.setAmt(amtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        String ysAmt = String.valueOf(data.getYsAmt());
                        if (StrUtil.isNotBlank(ysAmt)) {
                            ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
                        }
                        data.setYsAmt(ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        String grossProfitAmt = String.valueOf(data.getGrossProfitAmt());
                        if (StrUtil.isNotBlank(grossProfitAmt)) {
                            grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
                        }
                        data.setGrossProfitAmt(grossProfitAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                        data.setGrossProfitRate(amtToTal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : (ysAmtToTal.subtract(costAmtToTal)).divide(ysAmtToTal, 4, RoundingMode.HALF_UP).toPlainString());

                        String zkAmt = String.valueOf(data.getZkAmt());
                        if (StrUtil.isNotBlank(zkAmt)) {
                            zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
                        }
                        data.setZkAmt(zkAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                        String costAmt = String.valueOf(data.getCostAmt());
                        if (StrUtil.isNotBlank(costAmt)) {
                            costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
                        }
                        data.setCostAmt(costAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        data.setAmtAvg(finalDays == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : ysAmtToTal.divide(new BigDecimal(finalDays), 4, RoundingMode.HALF_UP).toPlainString());
                    }
                    resList.add(data);
                }
            } else {
                //当天没有数据.直接返回lylin数据
                resList = kylinData;
                return resList;
            }
        } else {
//            kylin没有数据的情况
            if (CollectionUtil.isNotEmpty(empSaleDetailResVoMap)) {
                for (String stoCode : empSaleDetailResVoMap.keySet()) {
                    TiChenSTORes resObject = new TiChenSTORes();
                    BigDecimal amtToTal = BigDecimal.ZERO;
                    BigDecimal ysAmtToTal = BigDecimal.ZERO;
                    BigDecimal grossProfitAmtToTal = BigDecimal.ZERO;
                    BigDecimal zkAmtToTal = BigDecimal.ZERO;
                    BigDecimal costAmtToTal = BigDecimal.ZERO;
                    List<EmpSaleDetailResVo> empSaleDetailResVoList = empSaleDetailResVoMap.get(stoCode);
                    if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {
                        for (EmpSaleDetailResVo resVo : empSaleDetailResVoList) {
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
                        resObject.setBrId(stoCode);
                        resObject.setBrName(empSaleDetailResVoList.get(0).getStoName());
                        resObject.setDays("1");
                        resObject.setAmt(amtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        resObject.setYsAmt(ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        resObject.setGrossProfitAmt(grossProfitAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        resObject.setGrossProfitRate(amtToTal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : (ysAmtToTal.subtract(costAmtToTal).divide(ysAmtToTal, 4, RoundingMode.HALF_UP)).toPlainString());
                        resObject.setZkAmt(zkAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        resObject.setCostAmt(costAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                        resObject.setAmtAvg(ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                    resList.add(resObject);
                }
            }
        }

        return resList;
    }

    private List<TiChenSTORes> caculatePlanForAllProduct(String startDate, String endDate, String client, List<String> stoCodes, String planIfNegative) {
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        String todayKy = DateUtil.format(new Date(), "yyyy-MM-dd");
        List<TiChenSTORes> resList = new ArrayList<>();
        //  2、取单店日均，确定销售级别
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 ) as ysAmt, ") //应收金额
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 实收额
                .append(" round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),2) as grossProfitAmt, ")//毛利额
                .append(" ( CASE WHEN sum( GSSD_AMT )= 0 THEN 0 ELSE round(( sum( GSSD_AMT ) - sum( GSSD_MOV_PRICE ))/ sum( GSSD_AMT ), 4 ) END ) AS grossProfitRate, ")//毛利率
                .append(" round(sum( GSSD_ZK_AMT ),2) as zkAmt, ")//折扣金额
//                .append(" round(sum( GSSD_ZK_AMT )/sum( GSSD_AMT ),2) as zkl, ")//折扣率
                .append("  round((case when sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end),4) as zkl, ")//折扣率
                .append(" round(sum(GSSD_MOV_PRICE),2) AS costAmt, ")//成本额
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
        stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME ");
        stoLvBuilder.append(" ORDER BY D.GSSD_BR_ID ");
//        -----------
//        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<TiChenSTORes> stoLvData = kylinJdbcTemplate.query(stoLvBuilder.toString(), BeanPropertyRowMapper.newInstance(TiChenSTORes.class));
//        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString(), BeanPropertyRowMapper.newInstance(TiChenSTORes));
        if (CollectionUtil.isNotEmpty(stoLvData)) {
            resList = stoLvData;
        }

        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(stoCodes);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setPlanIfNegative(planIfNegative);
        resList = decorateCaculatePlanForAllProduct(resList, query);
        return resList;
    }

    private List<TiChenProRes> doGetCommissionDetail(String startDate,
                                                     String endDate,
                                                     String client,
                                                     List<String> stoCodes,
                                                     GaiaTichengSaleplanZ tichengZ,
                                                     List<String> rejectClass,
                                                     List<String> rejectPro,
                                                     String planIfNegative,
                                                     boolean searchHistory,
                                                     String planId) {
        List<TiChenProRes> resList = new ArrayList<>();
        GaiaTichengSaleplanZ queryPlan = new GaiaTichengSaleplanZ();
        BeanUtils.copyProperties(tichengZ, queryPlan);

        List<TiChenProRes> stoLvData = new ArrayList<>();
        if (searchHistory) {
            CommissionSummaryDetailDTO commissionSummaryDetailDTO = new CommissionSummaryDetailDTO();
            commissionSummaryDetailDTO.setClient(client);
            commissionSummaryDetailDTO.setStartDate(startDate);
            commissionSummaryDetailDTO.setEndDate(endDate);
            commissionSummaryDetailDTO.setStoCodes(stoCodes);
            commissionSummaryDetailDTO.setPlanId(planId);
            // 销售提成
            commissionSummaryDetailDTO.setType("1");
            stoLvData = userCommissionSummaryDetailMapper.selectCommissionDetailByConditionWithTiChenProRes(commissionSummaryDetailDTO);
        }

        if (CollectionUtil.isNotEmpty(stoLvData)) {
            resList = stoLvData;
        }
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(stoCodes);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setPlanIfNegative(planIfNegative);
        query.setRejectProClass(rejectClass);
        resList = decorateCaculatePlanForRule(resList, query, queryPlan, rejectPro);
        return resList;
    }

    private List<TiChenProRes> caculatePlanForRule(String startDate,
                                                   String endDate,
                                                   String client,
                                                   List<String> stoCodes,
                                                   GaiaTichengSaleplanZ tichengZ,
                                                   List<String> rejectClass,
                                                   List<String> rejectPro,
                                                   String planIfNegative) {
        List<TiChenProRes> resList = new ArrayList<>();
        GaiaTichengSaleplanZ queryPlan = new GaiaTichengSaleplanZ();
        BeanUtils.copyProperties(tichengZ, queryPlan);
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        String todayKy = DateUtil.format(new Date(), "yyyy-MM-dd");
        //  2、取单店日均，确定销售级别
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" D.GSSD_BILL_NO AS GSSD_BILL_NO, ")
                .append(" D.GSSD_PRO_ID, ")
                .append(" D.GSSD_SERIAL, ")
                .append(" round(sum(GSSD_MOV_PRICE), 4) as costAmt, ") //成本额
                .append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 ) as ysAmt, ") //零售价
                .append(" round( sum( GSSD_AMT ), 2 ) AS amt, ") //应收金额
                .append(" round(round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),4), 2 ) as grossProfitAmt, ") //毛利额
                .append(" round(sum( GSSD_ZK_AMT ),2) as zkAmt ");//折扣额
//                .append(" round((case when (sum( GSSD_AMT ) + sum( GSSD_ZK_AMT ))=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end),2) as zkl "); //折扣率

        //  提成的基础数额 提成方式: 0 按销售额提成 1 按毛利额提成
        if ("0".equals(tichengZ.getPlanAmtWay())) {
            stoLvBuilder.append(", round(sum(GSSD_AMT),2) as baseAmount  "); //  销售额
        } else if ("1".equals(tichengZ.getPlanAmtWay())) {
            stoLvBuilder.append(", round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),2) as baseAmount "); //  毛利额
        }
        //  毛利率: 0.商品毛利率 = 零售价*数量 -成本额  1.销售毛利率 = 实收金额-成本额
        if ("0".equals(tichengZ.getPlanRateWay())) {
            stoLvBuilder.append(" ,( CASE WHEN (sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ))= 0 THEN 0 ELSE round( (sum( GSSD_AMT )+ sum( GSSD_ZK_AMT )- sum(GSSD_MOV_PRICE))/(sum( GSSD_AMT )+ sum( GSSD_ZK_AMT )), 4 ) END ) AS grossProfitRate "); //商品毛利率
        } else if ("1".equals(tichengZ.getPlanRateWay())) {
//            stoLvBuilder.append(" ,round( (sum( GSSD_AMT )- sum(GSSD_MOV_PRICE))/(sum( GSSD_AMT )), 2 ) as  grossProfitRate ");//    销售毛利率
            stoLvBuilder.append(" ,( CASE WHEN sum( GSSD_AMT )= 0 THEN 0 ELSE round(( sum( GSSD_AMT ) - sum( GSSD_MOV_PRICE ))/ sum( GSSD_AMT ), 4 ) END ) AS grossProfitRate ");//    销售毛利率
        }
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(tichengZ.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(tichengZ.getPlanRejectDiscountRate())) {
//            stoLvBuilder.append(" , round((case when sum(GSSD_AMT)=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end),2) as zkl ");
            stoLvBuilder.append(" , round((case when (sum( GSSD_AMT ) + sum( GSSD_ZK_AMT ))=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end),2) as zkl ");

        }
        stoLvBuilder
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_PRODUCT_BUSINESS  B ON B.CLIENT = D.CLIENT AND B.PRO_SITE = GSSD_BR_ID AND B.PRO_SELF_CODE = D.GSSD_PRO_ID ")
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
        if ("0".equals(tichengZ.getPlanIfNegative())) {
            stoLvBuilder.append(" AND D.GSSD_KL_NEGATIVE_MARGIN >= '0' ");  //   (<='0')  不含负毛利，(<='1') 或者此系数不写为含负毛利
        }
        if (ObjectUtil.isNotEmpty(rejectClass)) {
            stoLvBuilder.append(" AND B.PRO_CLASS NOT IN ")
                    .append(CommonUtil.queryByBatch(rejectClass));  //  剔除的商品分类
        }
        if (ObjectUtil.isNotEmpty(rejectPro)) {
            stoLvBuilder.append(" AND D.GSSD_PRO_ID NOT IN ")
                    .append(CommonUtil.queryByBatch(rejectPro));  //  剔除的商品
        }
        stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME,D.GSSD_BILL_NO,D.GSSD_PRO_ID,D.GSSD_SERIAL ");
//        if ("1".equals(tichengZ.getPlanRateWay())) {
//            stoLvBuilder.append(",GSSD_KL_MARGIN_AREA ");
//        }
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(tichengZ.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(tichengZ.getPlanRejectDiscountRate())) {
            switch (tichengZ.getPlanRejectDiscountRateSymbol()) {
                case "=":
                    tichengZ.setPlanRejectDiscountRateSymbol("!=");
                    break;
                case ">":
                    tichengZ.setPlanRejectDiscountRateSymbol("<=");
                    break;
                case ">=":
                    tichengZ.setPlanRejectDiscountRateSymbol("<");
                    break;
                case "<":
                    tichengZ.setPlanRejectDiscountRateSymbol(">=");
                    break;
                case "<=":
                    tichengZ.setPlanRejectDiscountRateSymbol(">");
                    break;
            }
            stoLvBuilder.append("having ZKL").append(tichengZ.getPlanRejectDiscountRateSymbol()).append(new BigDecimal(tichengZ.getPlanRejectDiscountRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }
//        stoLvBuilder.append(" ORDER BY D.GSSD_BR_ID ");
//        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<TiChenProRes> stoLvData = kylinJdbcTemplate.query(stoLvBuilder.toString(), BeanPropertyRowMapper.newInstance(TiChenProRes.class));
        if (CollectionUtil.isNotEmpty(stoLvData)) {
            resList = stoLvData;
        }
        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(stoCodes);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setPlanIfNegative(planIfNegative);
        query.setRejectProClass(rejectClass);
        resList = decorateCaculatePlanForRule(resList, query, queryPlan, rejectPro);

        return resList;
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
        PageInfo pageInfo = new PageInfo();
        List<PushMoneyByStoreV5OutData> outDatas = new ArrayList<>();
        GaiaTichengSaleplanZ tichengSalePlanZQuery = new GaiaTichengSaleplanZ();
        tichengSalePlanZQuery.setClient(inData.getClient());
        tichengSalePlanZQuery.setId(inData.getPlanId());
        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.select(tichengSalePlanZQuery);
        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
            throw new BusinessException("销售提成设置不正确,请检查设置!");
        }
        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
        String startDate = null;
        String endDate = null;
        //此处更改需求，需要处理用户选择时间与方案截止时间那个最小，取最小值
        endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseEndDate()) <= Integer.parseInt(endDate.replaceAll("-", ""))) {
            endDate = DateUtil.format(DateUtil.parse(inData.getUserChooseEndDate()), DatePattern.NORM_DATE_PATTERN);
        }
        startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseStartDate()) >= Integer.parseInt(startDate.replaceAll("-", ""))) {
            startDate = DateUtil.format(DateUtil.parse(inData.getUserChooseStartDate()), DatePattern.NORM_DATE_PATTERN);
        }

        //  提成门店表
        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getPlanId());

        List<CommonVo> storeCommons = tichengPlanZMapper.selectStoreByStoreCodes(tichengSto, inData.getClient());

        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        //  提成规则表
        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getPlanId()).andEqualTo("deleteFlag", "0");
        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);

        //  剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getPlanId());
        //  剔除商品
        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);//此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码

        //按门店级别进行汇总，查询销售方案涉及到时间段内的涉及到的所有商品的数据（销售天数/成本额/应收金额/实收金额/毛利额/毛利率/折扣金额/折扣率/日均销售）
        List<TiChenSTORes> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto, tichengZ.getPlanIfNegative());

        List<TiChenClientRes> stoLvDataByClient = caculatePlanForAllProductByClient(startDate, endDate, inData.getClient(), querySto);

        // 查询提成商品
        String nowDate = LocalDate.now().toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        boolean searchHistory = assertInTimeRange(startDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY),
                endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY), nowDate);

        List<TiChenProRes> saleDatas = doGetCommissionDetail(startDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY),
                endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY), inData.getClient(), querySto,
                tichengZ, rejectClass, rejectPro, tichengZ.getPlanIfNegative(),
                searchHistory, String.valueOf(inData.getPlanId()));

        //  3.按照销售提成设置，根据门店日均 把毛利率区间分给各个门店,根据门店日均销售来进行分配，会存在多个分配规则
        if (ObjectUtil.isNotEmpty(stoLvData)) {
            Map<String, Map> roleGroups = new HashMap<>();
            for (TiChenSTORes stoLv : stoLvData) {
                Map<String, Object> map = new HashMap<>();
                List<GaiaTichengSaleplanM> roles = new ArrayList<>();
                BigDecimal amtAvg = BigDecimalUtil.toBigDecimal(stoLv.getAmtAvg());
                if (ObjectUtil.isNotEmpty(amtAvg) || ObjectUtil.isNotEmpty(tichengMs)) {
                    for (GaiaTichengSaleplanM tichengM : tichengMs) {
                        if (amtAvg.compareTo(tichengM.getMinDailySaleAmt()) > 0
                                && amtAvg.compareTo(tichengM.getMaxDailySaleAmt()) <= 0) {
                            roles.add(tichengM);
                        }
                    }
                }
                map.put("brId", stoLv.getBrId());//门店编号
                map.put("brName", stoLv.getBrName());//门店名称
                map.put("days", stoLv.getDays());//门店销售天数
                map.put("amt", stoLv.getAmt());//实收金额
                map.put("ysAmt", stoLv.getYsAmt());//销售额
                map.put("grossProfitAmt", stoLv.getGrossProfitAmt());//毛利额
                map.put("grossProfitRate", stoLv.getGrossProfitRate());//毛利率
                map.put("zkAmt", stoLv.getZkAmt());//折扣金额
                map.put("costAmt", stoLv.getCostAmt());//成本额
                map.put("amtAvg", stoLv.getAmtAvg());//门店日销售额
                map.put("roles", roles);
                roleGroups.put(stoLv.getBrId(), map);
            }

            String planAmtWay = tichengZ.getPlanAmtWay();


            stoLvData.forEach(item -> {
                PushMoneyByStoreV5OutData outData = new PushMoneyByStoreV5OutData();
                String brId = item.getBrId();
                //  算提成
                Map roleBySto = roleGroups.get(brId);
                outData.setPlanName(tichengZ.getPlanName());
                outData.setPlanId(inData.getPlanId());
                outData.setStartDate(tichengZ.getPlanStartDate());
                outData.setEndtDate(tichengZ.getPlanEndDate());
                if ("1".equals(tichengZ.getPlanStatus())) {
                    outData.setStatus("1");
                } else if ("2".equals(tichengZ.getDeleteFlag())) {
                    outData.setStatus("2");
                }
                outData.setStoreCodes(tichengSto);//适用门店
                outData.setType("销售提成");
                outData.setTypeValue("1");
                outData.setStoCode(String.valueOf(roleBySto.get("brId")));
                outData.setStoName(String.valueOf(roleBySto.get("brName")));
                //设置
                outData.setDays(BigDecimalUtil.toBigDecimal(roleBySto.get("days")));
                outData.setCostAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("costAmt")));
                outData.setYsAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("ysAmt")));
                outData.setAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("amt")));
                outData.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("grossProfitAmt")));
                outData.setZkAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("zkAmt")));


                BigDecimal tcPrices = BigDecimal.ZERO;//提成金额
                BigDecimal tcCostAmt = BigDecimal.ZERO;
                BigDecimal tcYsAmt = BigDecimal.ZERO;
                BigDecimal tcAmt = BigDecimal.ZERO;
                BigDecimal tcGrossProfitAmt = BigDecimal.ZERO;
                if (ObjectUtil.isNotEmpty(tichengMs)) {
                    List<GaiaTichengSaleplanM> role2 = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");

                    //筛选出对应门店和营业员的数据
                    if (CollectionUtil.isNotEmpty(saleDatas)) {
                        List<TiChenProRes> saleData = saleDatas.stream().filter(s -> StrUtil.isNotBlank(s.getBrId()) && s.getBrId().equals(brId)).collect(Collectors.toList());
                        for (TiChenProRes saleD : saleData) {
                            BigDecimal baseAmount = BigDecimal.ZERO;
                            BigDecimal grossProfitRate = BigDecimalUtil.toBigDecimal(saleD.getGrossProfitRate());
                            if ("0".equals(planAmtWay)) {
                                baseAmount = BigDecimalUtil.toBigDecimal(saleD.getAmt());
                            } else if ("1".equals(planAmtWay)) {
                                baseAmount = BigDecimalUtil.toBigDecimal(saleD.getGrossProfitAmt());
                            }

                            if (Boolean.valueOf(saleD.getRealTime())) {
                                // 实时数据
                                for (GaiaTichengSaleplanM tichengM : role2) {
                                    if (saleD.getBrId().equals(item.getBrId())
                                            && grossProfitRate.multiply(new BigDecimal(100)).compareTo(tichengM.getMinProMll()) > 0
                                            && grossProfitRate.multiply(new BigDecimal(100)).compareTo(tichengM.getMaxProMll()) <= 0) {
                                        tcCostAmt = tcCostAmt.add(BigDecimalUtil.toBigDecimal(saleD.getCostAmt()));
                                        tcYsAmt = tcYsAmt.add(BigDecimalUtil.toBigDecimal(saleD.getYsAmt()));
                                        tcAmt = tcAmt.add(BigDecimalUtil.toBigDecimal(saleD.getAmt()));
                                        tcGrossProfitAmt = tcGrossProfitAmt.add(BigDecimalUtil.toBigDecimal(saleD.getGrossProfitAmt()));
                                        // 如果是实时数据则计算提成金额
                                        tcPrices = tcPrices.add(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengM.getTichengScale().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
                                        break;
                                    }
                                }
                            } else {
                                // 历史提成
                                tcCostAmt = tcCostAmt.add(BigDecimalUtil.toBigDecimal(saleD.getCostAmt()));
                                tcYsAmt = tcYsAmt.add(BigDecimalUtil.toBigDecimal(saleD.getYsAmt()));
                                tcAmt = tcAmt.add(BigDecimalUtil.toBigDecimal(saleD.getAmt()));
                                tcGrossProfitAmt = tcGrossProfitAmt.add(BigDecimalUtil.toBigDecimal(saleD.getGrossProfitAmt()));
                                // 不是实时数据直接获取
                                tcPrices = tcPrices.add(BigDecimalUtil.toBigDecimal(saleD.getCommissionAmt()));
                            }
                        }
                    }

                }
                outData.setTcCostAmt(tcCostAmt);
                outData.setTcYsAmt(tcYsAmt);
                outData.setTcAmt(tcAmt);
                outData.setTcGrossProfitAmt(tcGrossProfitAmt);
                outData.setTiTotal(tcPrices);//提成金额
                outDatas.add(outData);
            });
        }

//        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            TiChenClientRes clientDataStatic = stoLvDataByClient.get(0);
            // 集合列的数据汇总
            PushMoneyByStoreV5TotalOutData outTotal = new PushMoneyByStoreV5TotalOutData();
            PushMoneyByStoreV5OutData first = outDatas.get(0);
            BeanUtils.copyProperties(first, outTotal);
            outTotal.setStoNum(tichengSto.size());
            outTotal.setClient(inData.getClient());
            outTotal.setCPlanName("默认");
            outTotal.setDays(BigDecimalUtil.toBigDecimal(clientDataStatic.getDays()));
            BigDecimal costAmtTotal = BigDecimal.ZERO;
            BigDecimal ysAmtTotal = BigDecimal.ZERO;
            BigDecimal amtTotal = BigDecimal.ZERO;
            BigDecimal grossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal zkAmtTotal = BigDecimal.ZERO;
            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
            BigDecimal tcAmtTotal = BigDecimal.ZERO;
            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal tiTotal = BigDecimal.ZERO;
            for (PushMoneyByStoreV5OutData storeData : outDatas) {
                costAmtTotal = costAmtTotal.add(storeData.getCostAmt());
                ysAmtTotal = ysAmtTotal.add(storeData.getYsAmt());
                amtTotal = amtTotal.add(storeData.getAmt());
                grossProfitAmtTotal = grossProfitAmtTotal.add(storeData.getGrossProfitAmt());
                zkAmtTotal = zkAmtTotal.add(storeData.getZkAmt());
                tcCostAmtTotal = tcCostAmtTotal.add(storeData.getTcCostAmt());
                tcYsAmtTotal = tcYsAmtTotal.add(storeData.getTcYsAmt());
                tcAmtTotal = tcAmtTotal.add(storeData.getTcAmt());
                tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(storeData.getTcGrossProfitAmt());
                tiTotal = tiTotal.add(storeData.getTiTotal());
            }
            outTotal.setStoreCodes(storeCommons);

            //此处舍弃累计计算，使用统一从加盟商级别进行获取，保持与单品提成一样
            outTotal.setCostAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getCostAmt()));
            outTotal.setYsAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getYsAmt()));
            outTotal.setAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getAmt()));
            outTotal.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getGrossProfitAmt()));
            outTotal.setGrossProfitRate(BigDecimalUtil.toBigDecimal(clientDataStatic.getGrossProfitRate()));
            outTotal.setZkAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getZkAmt()));
            outTotal.setZkRate(outTotal.getYsAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : outTotal.getZkAmt().divide(outTotal.getYsAmt(), 4, RoundingMode.HALF_UP));


            outTotal.setTcCostAmt(tcCostAmtTotal.setScale(4, RoundingMode.HALF_UP));//提成商品成本额
            outTotal.setTcYsAmt(tcYsAmtTotal.setScale(2, RoundingMode.HALF_UP));//提成商品应收销售额
            outTotal.setTcAmt(tcAmtTotal.setScale(2, RoundingMode.HALF_UP));//提成商品销售额
            outTotal.setTcGrossProfitAmt(tcGrossProfitAmtTotal.setScale(2, RoundingMode.HALF_UP));//提成商品毛利额
            outTotal.setTcGrossProfitRate(tcGrossProfitAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));//提成商品毛利率
            outTotal.setTiTotal(tiTotal.setScale(4, RoundingMode.HALF_UP));
            outTotal.setDeductionWageAmtRate(amtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(amtTotal, 4, RoundingMode.HALF_UP));//提成销售比
            outTotal.setDeductionWageGrossProfitRate(grossProfitAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(grossProfitAmtTotal, 4, RoundingMode.HALF_UP));
            pageInfo.setList(Arrays.asList(outTotal));
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;

    }


    @Override
    public PageInfo caculatePlan(PushMoneyByStoreV5InData inData) {
        PageInfo pageInfo = new PageInfo();
        List<PushMoneyByStoreV5OutData> outDatas = new ArrayList<>();
        //  1.查询当前销售计划
        Map queryMap = new HashMap();                       // queryMap没用上
        queryMap.put("client", inData.getClient());
        queryMap.put("planId", inData.getPlanId());
        GaiaTichengSaleplanZ tichengSalePlanZQuery = new GaiaTichengSaleplanZ();
        tichengSalePlanZQuery.setClient(inData.getClient());
        tichengSalePlanZQuery.setId(inData.getPlanId());
        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.select(tichengSalePlanZQuery);
        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
            throw new BusinessException("销售提成设置不正确,请检查设置!");
        }
        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
        String startDate = null;
        String endDate = null;
        startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        //此处更改需求，需要处理用户选择时间与方案截止时间那个最小，取最小值
        endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseEndDate()) <= Integer.parseInt(endDate.replaceAll("-", ""))) {
            endDate = DateUtil.format(DateUtil.parse(inData.getUserChooseEndDate()), DatePattern.NORM_DATE_PATTERN);
        }
        startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        if (Integer.parseInt(inData.getUserChooseStartDate()) >= Integer.parseInt(startDate.replaceAll("-", ""))) {
            startDate = DateUtil.format(DateUtil.parse(inData.getUserChooseStartDate()), DatePattern.NORM_DATE_PATTERN);
        }

        //  提成门店表
        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getPlanId());


        List<CommonVo> storeCommons = tichengPlanZMapper.selectStoreByStoreCodes(tichengSto, inData.getClient());

//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }
        //  提成规则表
        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getPlanId()).andEqualTo("deleteFlag", "0");
        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);


        //  剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getPlanId());
        //  剔除商品
        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);//此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码

        //按门店级别进行汇总，查询销售方案涉及到时间段内的涉及到的所有商品的数据（销售天数/成本额/应收金额/实收金额/毛利额/毛利率/折扣金额/折扣率/日均销售）
        List<TiChenSTORes> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto, tichengZ.getPlanIfNegative());

        List<TiChenClientRes> stoLvDataByClient = caculatePlanForAllProductByClient(startDate, endDate, inData.getClient(), querySto);
//        Map<String, GaiaTichengSaleplanM> stoTichengM = getStoTichengM(stoLvData, tichengMs);

        List<TiChenProRes> saleDatas = caculatePlanForRule(startDate, endDate, inData.getClient(), querySto, tichengZ, rejectClass, rejectPro, tichengZ.getPlanIfNegative());

        String planIfNegative = tichengZ.getPlanIfNegative();

        //  3.按照销售提成设置，根据门店日均 把毛利率区间分给各个门店,根据门店日均销售来进行分配，会存在多个分配规则
        if (ObjectUtil.isNotEmpty(stoLvData)) {
            Map<String, Map> roleGroups = new HashMap<>();
            for (TiChenSTORes stoLv : stoLvData) {
                Map<String, Object> map = new HashMap<>();
                List<GaiaTichengSaleplanM> roles = new ArrayList<>();
                BigDecimal amtAvg = BigDecimalUtil.toBigDecimal(stoLv.getAmtAvg());
                if (ObjectUtil.isNotEmpty(amtAvg) || ObjectUtil.isNotEmpty(tichengMs)) {
                    for (GaiaTichengSaleplanM tichengM : tichengMs) {
                        if (amtAvg.compareTo(tichengM.getMinDailySaleAmt()) > 0
                                && amtAvg.compareTo(tichengM.getMaxDailySaleAmt()) <= 0) {
                            roles.add(tichengM);
                        }
                    }
                }
                map.put("brId", stoLv.getBrId());//门店编号
                map.put("brName", stoLv.getBrName());//门店名称
                map.put("days", stoLv.getDays());//门店销售天数
                map.put("amt", stoLv.getAmt());//实收金额
                map.put("ysAmt", stoLv.getYsAmt());//销售额
                map.put("grossProfitAmt", stoLv.getGrossProfitAmt());//毛利额
                map.put("grossProfitRate", stoLv.getGrossProfitRate());//毛利率
                map.put("zkAmt", stoLv.getZkAmt());//折扣金额
                map.put("costAmt", stoLv.getCostAmt());//成本额
                map.put("amtAvg", stoLv.getAmtAvg());//门店日销售额
                map.put("roles", roles);
                roleGroups.put(stoLv.getBrId(), map);
            }

            stoLvData.forEach(item -> {
                PushMoneyByStoreV5OutData outData = new PushMoneyByStoreV5OutData();
                String brId = item.getBrId();
                //  算提成
                Map roleBySto = roleGroups.get(brId);
                outData.setPlanName(tichengZ.getPlanName());
                outData.setPlanId(inData.getPlanId());
                outData.setStartDate(tichengZ.getPlanStartDate());
                outData.setEndtDate(tichengZ.getPlanEndDate());
                if ("1".equals(tichengZ.getPlanStatus())) {
                    outData.setStatus("1");
                } else if ("2".equals(tichengZ.getDeleteFlag())) {
                    outData.setStatus("2");
                }
                outData.setStoreCodes(tichengSto);//适用门店
                outData.setType("销售提成");
                outData.setTypeValue("1");
                outData.setStoCode(String.valueOf(roleBySto.get("brId")));
                outData.setStoName(String.valueOf(roleBySto.get("brName")));
                //设置
                outData.setDays(BigDecimalUtil.toBigDecimal(roleBySto.get("days")));
                outData.setCostAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("costAmt")));
                outData.setYsAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("ysAmt")));
                outData.setAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("amt")));
                outData.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("grossProfitAmt")));
                outData.setZkAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("zkAmt")));


                BigDecimal tcPrices = BigDecimal.ZERO;//提成金额
                BigDecimal tcCostAmt = BigDecimal.ZERO;
                BigDecimal tcYsAmt = BigDecimal.ZERO;
                BigDecimal tcAmt = BigDecimal.ZERO;
                BigDecimal tcGrossProfitAmt = BigDecimal.ZERO;
                if (ObjectUtil.isNotEmpty(tichengMs)) {
                    List<GaiaTichengSaleplanM> role2 = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");

                    //筛选出对应门店和营业员的数据
                    if (CollectionUtil.isNotEmpty(saleDatas)) {
                        List<TiChenProRes> saleData = saleDatas.stream().filter(s -> StrUtil.isNotBlank(s.getBrId()) && s.getBrId().equals(brId)).collect(Collectors.toList());
                        for (TiChenProRes saleD : saleData) {
                            BigDecimal baseAmount = BigDecimalUtil.toBigDecimal(saleD.getBaseAmount());
                            BigDecimal grossProfitRate = BigDecimalUtil.toBigDecimal(saleD.getGrossProfitRate());

                            if (StrUtil.isNotBlank(planIfNegative) && "0".equals(planIfNegative)) {
                                if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                                    continue;
                                }
                            }

                            for (GaiaTichengSaleplanM tichengM : role2) {
                                if (saleD.getBrId().equals(item.getBrId())
                                        && grossProfitRate.multiply(new BigDecimal(100)).compareTo(tichengM.getMinProMll()) > 0
                                        && grossProfitRate.multiply(new BigDecimal(100)).compareTo(tichengM.getMaxProMll()) <= 0) {
                                    tcCostAmt = tcCostAmt.add(BigDecimalUtil.toBigDecimal(saleD.getCostAmt()));
                                    tcYsAmt = tcYsAmt.add(BigDecimalUtil.toBigDecimal(saleD.getYsAmt()));
                                    tcAmt = tcAmt.add(BigDecimalUtil.toBigDecimal(saleD.getAmt()));
                                    tcGrossProfitAmt = tcGrossProfitAmt.add(BigDecimalUtil.toBigDecimal(saleD.getGrossProfitAmt()));
                                    tcPrices = tcPrices.add(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengM.getTichengScale().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
                                    // 理论上要求只能命中同一条明细
                                    break;
                                }
                            }
                        }
                    }

                }
                outData.setTcCostAmt(tcCostAmt);
                outData.setTcYsAmt(tcYsAmt);
                outData.setTcAmt(tcAmt);
                outData.setTcGrossProfitAmt(tcGrossProfitAmt);
                outData.setTiTotal(tcPrices);//提成金额
                outDatas.add(outData);
            });
        }

//        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            TiChenClientRes clientDataStatic = stoLvDataByClient.get(0);
            // 集合列的数据汇总
            PushMoneyByStoreV5TotalOutData outTotal = new PushMoneyByStoreV5TotalOutData();
            PushMoneyByStoreV5OutData first = outDatas.get(0);
            BeanUtils.copyProperties(first, outTotal);
            outTotal.setStoNum(tichengSto.size());
            outTotal.setClient(inData.getClient());
            outTotal.setCPlanName("默认");
            outTotal.setDays(BigDecimalUtil.toBigDecimal(clientDataStatic.getDays()));
            BigDecimal costAmtTotal = BigDecimal.ZERO;
            BigDecimal ysAmtTotal = BigDecimal.ZERO;
            BigDecimal amtTotal = BigDecimal.ZERO;
            BigDecimal grossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal zkAmtTotal = BigDecimal.ZERO;
            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
            BigDecimal tcAmtTotal = BigDecimal.ZERO;
            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
            BigDecimal tiTotal = BigDecimal.ZERO;
            for (PushMoneyByStoreV5OutData storeData : outDatas) {
                costAmtTotal = costAmtTotal.add(storeData.getCostAmt());
                ysAmtTotal = ysAmtTotal.add(storeData.getYsAmt());
                amtTotal = amtTotal.add(storeData.getAmt());
                grossProfitAmtTotal = grossProfitAmtTotal.add(storeData.getGrossProfitAmt());
                zkAmtTotal = zkAmtTotal.add(storeData.getZkAmt());
                tcCostAmtTotal = tcCostAmtTotal.add(storeData.getTcCostAmt());
                tcYsAmtTotal = tcYsAmtTotal.add(storeData.getTcYsAmt());
                tcAmtTotal = tcAmtTotal.add(storeData.getTcAmt());
                tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(storeData.getTcGrossProfitAmt());
                tiTotal = tiTotal.add(storeData.getTiTotal());
            }
            outTotal.setStoreCodes(storeCommons);

            //此处舍弃累计计算，使用统一从加盟商级别进行获取，保持与单品提成一样
            outTotal.setCostAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getCostAmt()));
            outTotal.setYsAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getYsAmt()));
            outTotal.setAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getAmt()));
            outTotal.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getGrossProfitAmt()));
            outTotal.setGrossProfitRate(BigDecimalUtil.toBigDecimal(clientDataStatic.getGrossProfitRate()));
            outTotal.setZkAmt(BigDecimalUtil.toBigDecimal(clientDataStatic.getZkAmt()));
            outTotal.setZkRate(outTotal.getYsAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : outTotal.getZkAmt().divide(outTotal.getYsAmt(), 4, RoundingMode.HALF_UP));


            outTotal.setTcCostAmt(tcCostAmtTotal.setScale(4, RoundingMode.HALF_UP));//提成商品成本额
            outTotal.setTcYsAmt(tcYsAmtTotal.setScale(2, RoundingMode.HALF_UP));//提成商品应收销售额
            outTotal.setTcAmt(tcAmtTotal.setScale(2, RoundingMode.HALF_UP));//提成商品销售额
            outTotal.setTcGrossProfitAmt(tcGrossProfitAmtTotal.setScale(2, RoundingMode.HALF_UP));//提成商品毛利额
            outTotal.setTcGrossProfitRate(tcGrossProfitAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));//提成商品毛利率
            outTotal.setTiTotal(tiTotal.setScale(4, RoundingMode.HALF_UP));
            outTotal.setDeductionWageAmtRate(amtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(amtTotal, 4, RoundingMode.HALF_UP));//提成销售比
            outTotal.setDeductionWageGrossProfitRate(grossProfitAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(grossProfitAmtTotal, 4, RoundingMode.HALF_UP));
            pageInfo.setList(Arrays.asList(outTotal));
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;

    }

    private List<TiChenClientRes> decorateCaculatePlanForAllProducttByClient(List<TiChenClientRes> kylinData, EmpSaleDetailInData inData) {
        List<TiChenClientRes> resList = new ArrayList<>();
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
                TiChenClientRes data = kylinData.get(0);
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
                String days = String.valueOf(data.getDays());
                Integer finalDays = 1;
                if (StrUtil.isNotBlank(days)) {
                    finalDays = Integer.parseInt(days) + finalDays;
                }
                data.setDays(finalDays.toString());

                String amt = String.valueOf(data.getAmt());
                if (StrUtil.isNotBlank(amt)) {
                    amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
                }
                data.setAmt(amtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                String ysAmt = String.valueOf(data.getYsAmt());
                if (StrUtil.isNotBlank(ysAmt)) {
                    ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
                }
                data.setYsAmt(ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                String grossProfitAmt = String.valueOf(data.getGrossProfitAmt());
                if (StrUtil.isNotBlank(grossProfitAmt)) {
                    grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
                }
                data.setGrossProfitAmt(grossProfitAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.setGrossProfitRate(amtToTal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : (ysAmtToTal.subtract(costAmtToTal)).divide(ysAmtToTal, 4, RoundingMode.HALF_UP).toPlainString());


                String zkAmt = String.valueOf(data.getZkAmt());
                if (StrUtil.isNotBlank(zkAmt)) {
                    zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
                }
                data.setZkAmt(zkAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                String costAmt = String.valueOf(data.getCostAmt());
                if (StrUtil.isNotBlank(costAmt)) {
                    costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
                }
                data.setCostAmt(costAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.setAmtAvg(finalDays == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : ysAmtToTal.divide(new BigDecimal(finalDays), 4, RoundingMode.HALF_UP).toPlainString());
                resList = new ArrayList<>(Arrays.asList(data));
            } else {
                resList = kylinData;
            }

        } else {
            //kylin数据为空
            if (CollectionUtil.isNotEmpty(toDayAllEmpSaleDetailList)) {
                TiChenClientRes data = new TiChenClientRes();
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

                Integer finalDays = 1;
//                if (data.get("days") != null) {
//                    String days = String.valueOf(data.get("days"));
//                    if (StrUtil.isNotBlank(days)) {
//                        finalDays = Integer.parseInt(days) + finalDays;
//                    }
//                }
                data.setDays(finalDays.toString());

//                String amt = String.valueOf(data.get("amt"));
//                if (StrUtil.isNotBlank(amt)) {
//                    amtToTal = amtToTal.add(BigDecimalUtil.toBigDecimal(amt));
//                }
                data.setAmt(amtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

//                String ysAmt = String.valueOf(data.get("ysAmt"));
//                if (StrUtil.isNotBlank(ysAmt)) {
//                    ysAmtToTal = ysAmtToTal.add(BigDecimalUtil.toBigDecimal(ysAmt));
//                }
                data.setYsAmt(ysAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

//                String grossProfitAmt = String.valueOf(data.get("grossProfitAmt"));
//                if (StrUtil.isNotBlank(grossProfitAmt)) {
//                    grossProfitAmtToTal = grossProfitAmtToTal.add(BigDecimalUtil.toBigDecimal(grossProfitAmt));
//                }
                data.setGrossProfitAmt(grossProfitAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

                data.setGrossProfitRate(amtToTal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : (ysAmtToTal.subtract(costAmtToTal)).divide(ysAmtToTal, 4, RoundingMode.HALF_UP).toPlainString());

//                String zkAmt = String.valueOf(data.get("zkAmt"));
//                if (StrUtil.isNotBlank(zkAmt)) {
//                    zkAmtToTal = zkAmtToTal.add(BigDecimalUtil.toBigDecimal(zkAmt));
//                }
                data.setZkAmt(zkAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());

//                String costAmt = String.valueOf(data.get("costAmt"));
//                if (StrUtil.isNotBlank(costAmt)) {
//                    costAmtToTal = costAmtToTal.add(BigDecimalUtil.toBigDecimal(costAmt));
//                }
                data.setCostAmt(costAmtToTal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                data.setAmtAvg(finalDays == 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() : ysAmtToTal.divide(new BigDecimal(finalDays), 4, RoundingMode.HALF_UP).toPlainString());
                resList.add(data);
            }
        }
        return resList;
    }

    private List<TiChenClientRes> caculatePlanForAllProductByClient(String startDate, String endDate, String client, List<String> stoCodes) {
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        String todayKy = DateUtil.format(new Date(), "yyyy-MM-dd");
        List<TiChenClientRes> resList = new ArrayList<>();
        //  2、取单店日均，确定销售级别
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 ) as ysAmt, ") //应收金额
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 实收额
                .append(" round(round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),4),2) as grossProfitAmt, ")//毛利额
//                .append(" round((sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ))/ sum(GSSD_MOV_PRICE),2) as  grossProfitRate, ")//毛利率
                .append(" ( CASE WHEN sum( GSSD_AMT )= 0 THEN 0 ELSE round(( sum( GSSD_AMT ) - sum( GSSD_MOV_PRICE ))/ sum( GSSD_AMT ), 4 ) END ) AS grossProfitRate, ")//毛利率
                .append(" round(sum( GSSD_ZK_AMT ),2) as zkAmt, ")//折扣金额
                .append(" ROUND((case when (sum( GSSD_AMT ) + sum( GSSD_ZK_AMT ))=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end), 2) as zkl, ")//折扣率
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
        List<TiChenClientRes> stoLvData = kylinJdbcTemplate.query(stoLvBuilder.toString(), BeanPropertyRowMapper.newInstance(TiChenClientRes.class));
        if (CollectionUtil.isNotEmpty(stoLvData)) {
            resList = stoLvData;
        }
        EmpSaleDetailInData query = new EmpSaleDetailInData();
        query.setClient(client);
        query.setToday(today);
        query.setStoArr(stoCodes);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        resList = decorateCaculatePlanForAllProducttByClient(resList, query);
        return resList;
    }


//    @Override
//    public PageInfo caculatePlan(PushMoneyByStoreV5InData inData) {
//
//        List<PushMoneyByStoreV5OutData> outDatas = new ArrayList<>();
//        //  1.查询当前销售计划
//        Map queryMap = new HashMap();                       // queryMap没用上
//        queryMap.put("client", inData.getClient());
//        queryMap.put("planId", inData.getPlanId());
//        GaiaTichengSaleplanZ tichengSalePlanZQuery = new GaiaTichengSaleplanZ();
//        tichengSalePlanZQuery.setClient(inData.getClient());
//        tichengSalePlanZQuery.setId(inData.getPlanId());
//        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.select(tichengSalePlanZQuery);
//        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
//            throw new BusinessException("销售提成设置不正确,请检查设置!");
//        }
//        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
//        String startDate = null;
//        String endDate = null;
//        startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
//        endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
//
//        //  提成门店表
//        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getPlanId());
////        List<String> tichengSto = Arrays.asList("10001");
//        List<String> querySto = new ArrayList<>();
//        querySto.addAll(tichengSto);
//        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
//            //  设置的门店和筛选的门店求交集
//            querySto.retainAll(inData.getStoArr());
//        }
//        //  提成规则表
//        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
//        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getPlanId()).andEqualTo("deleteFlag", "0");
//        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);
//
//
//        //  剔除的商品分类
//        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getPlanId());
//        //  剔除商品
//        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);//此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码
//
//        //按门店级别进行汇总，查询销售方案涉及到时间段内的涉及到的所有商品的数据（销售天数/成本额/应收金额/实收金额/毛利额/毛利率/折扣金额/折扣率）
//        List<Map<String, Object>> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto);
//
//
//        //  查询总的销售天数
//        StringBuilder saleDaysBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
//                .append(" count(distinct D.GSSD_DATE) as days ") //    销售天数
//                .append(" FROM GAIA_SD_SALE_D as D ")
//                .append(" WHERE 1=1 ")
//                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
//                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
//                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
//        saleDaysBuilder.append(" GROUP BY D.CLIENT ");
//        Map<String, Object> saleDaysData = new HashMap<>();
//        try{
//            saleDaysData = kylinJdbcTemplate.queryForMap(saleDaysBuilder.toString());
//        }catch (Exception e){
//            System.out.println("没有查询到实际数据");
//        }
//
////        Map<String, Object> saleDaysData = kylinJdbcTemplate.queryForMap(saleDaysBuilder.toString());
//
//        //查询按照提成设置规则过滤后的实际销售方案
//        List<Map<String, Object>> saleDatas = caculatePlanForRule(startDate, endDate, inData.getClient(), querySto, tichengZ, rejectClass, rejectPro);
//
//        Map<String, Map> brRuleDataMap = new HashMap<>();
//        if (CollectionUtil.isNotEmpty(saleDatas)) {
//            for (Map<String, Object> singleProInfo : saleDatas) {
//                String brId = String.valueOf(singleProInfo.get("brId"));
//                if (!brRuleDataMap.containsKey(brId)) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("brId", String.valueOf(singleProInfo.get("brId")));//门店编号
//                    map.put("brName", String.valueOf(singleProInfo.get("brName")));//门店名称
//                    map.put("amt", new BigDecimal(String.valueOf(singleProInfo.get("amt"))));//实收金额
//                    map.put("ysAmt", new BigDecimal(String.valueOf(singleProInfo.get("ysamt"))));//销售额
//                    map.put("costAmt", new BigDecimal(String.valueOf(singleProInfo.get("costamt"))));//成本额
//                    map.put("grossProfitAmt", new BigDecimal(String.valueOf(singleProInfo.get("grossprofitamt"))));//毛利额
//                    map.put("zkAmt", new BigDecimal(String.valueOf(singleProInfo.get("zkamt"))));//折扣金额
//                    map.put("baseAmount", new BigDecimal(String.valueOf(singleProInfo.get("baseamount"))));//动态值
////                    map.put("grossProfitRate", String.valueOf(singleProInfo.get("grossprofitrate")));//动态值
////                    map.put("zkl", String.valueOf(singleProInfo.get("zkl")));//折扣率，针对商品
//                    brRuleDataMap.put(brId, map);
//                } else {
//                    Map<String, Object> map = brRuleDataMap.get(brId);
//                    String amtNow = String.valueOf(singleProInfo.get("amt"));
//                    BigDecimal amtOld = (BigDecimal) map.get("amt");
//                    map.put("amt", new BigDecimal(amtNow).add(amtOld));//实收金额
//
//                    String ysAmtNow = String.valueOf(singleProInfo.get("ysamt"));
//                    BigDecimal ysAmtOld = (BigDecimal) map.get("ysAmt");
//                    map.put("ysAmt", new BigDecimal(ysAmtNow).add(ysAmtOld));//销售额
//
//                    String costAmtNow = String.valueOf(singleProInfo.get("costamt"));
//                    BigDecimal costAmtOld = (BigDecimal) map.get("costAmt");
//                    map.put("costAmt", new BigDecimal(costAmtNow).add(costAmtOld));//成本额
//
//                    String grossProfitAmtNow = String.valueOf(singleProInfo.get("grossprofitamt"));
//                    BigDecimal grossProfitAmtOld = (BigDecimal) map.get("grossProfitAmt");
//                    map.put("grossProfitAmt", new BigDecimal(grossProfitAmtNow).add(grossProfitAmtOld));//毛利额
//
//                    String zkAmtNow = String.valueOf(singleProInfo.get("zkamt"));
//                    BigDecimal zkAmtOld = (BigDecimal) map.get("zkAmt");
//                    map.put("zkAmt", new BigDecimal(zkAmtNow).add(zkAmtOld));//折扣金额
//
//                    String baseAmountNow = String.valueOf(singleProInfo.get("baseamount"));
//                    BigDecimal baseAmountOld = (BigDecimal) map.get("baseAmount");
//                    map.put("baseAmount", new BigDecimal(baseAmountNow).add(baseAmountOld));//动态值
//
////                    map.put("grossProfitRate", String.valueOf(singleProInfo.get("grossprofitrate")));//动态值
////                    map.put("zkl", String.valueOf(singleProInfo.get("zkl")));//折扣率，针对商品
//                    brRuleDataMap.put(brId, map);
//                }
//            }
//
//        }
//
//        //  3.按照销售提成设置，根据门店日均 把毛利率区间分给各个门店
//        if (ObjectUtil.isNotEmpty(stoLvData)) {
//            Map<String, Map> roleGroups = new HashMap<>();
//            for (Map stoLv : stoLvData) {
//                Map<String, Object> map = new HashMap<>();
//                List<GaiaTichengSaleplanM> roles = new ArrayList<>();
//                BigDecimal amtAvg = BigDecimalUtil.toBigDecimal(stoLv.get("amtAvg"));
//                if (ObjectUtil.isNotEmpty(amtAvg) || ObjectUtil.isNotEmpty(tichengMs)) {
//                    for (GaiaTichengSaleplanM tichengM : tichengMs) {
//                        if (amtAvg.compareTo(tichengM.getMinDailySaleAmt()) == 1
//                                && amtAvg.compareTo(tichengM.getMaxDailySaleAmt()) < 1) {
//                            roles.add(tichengM);
//                        }
//                    }
//                }
//                map.put("brId", String.valueOf(stoLv.get("brId")));//门店编号
//                map.put("brName", String.valueOf(stoLv.get("brName")));//门店名称
//                map.put("days", String.valueOf(stoLv.get("days")));//门店销售天数
//                map.put("amt", String.valueOf(stoLv.get("amt")));//实收金额
//                map.put("ysAmt", String.valueOf(stoLv.get("ysamt")));//销售额
//                map.put("grossProfitAmt", String.valueOf(stoLv.get("grossprofitamt")));//毛利额
//                map.put("grossProfitRate", String.valueOf(stoLv.get("grossprofitrate")));//毛利率
//                map.put("zkAmt", String.valueOf(stoLv.get("zkamt")));//折扣金额
//                map.put("costAmt", String.valueOf(stoLv.get("costamt")));//成本额
//                map.put("amtAvg", String.valueOf(stoLv.get("amtAvg")));//门店日销售额
//                map.put("roles", roles);
//                roleGroups.put(String.valueOf(stoLv.get("brId")), map);
//            }
//            stoLvData.forEach(item -> {
//                PushMoneyByStoreV5OutData outData = new PushMoneyByStoreV5OutData();
//                String brId = (String) item.get("brId");
//                System.err.println(item.get("brId"));
//                //  算提成
//                Map roleBySto = roleGroups.get(item.get("brId"));
//                outData.setPlanName(tichengZ.getPlanName());
//                outData.setPlanId(inData.getPlanId());
//                outData.setStartDate(tichengZ.getPlanStartDate());
//                outData.setEndtDate(tichengZ.getPlanEndDate());
//                if ("1".equals(tichengZ.getPlanStatus())) {
//                    outData.setStatus("1");
//                } else if ("2".equals(tichengZ.getDeleteFlag())) {
//                    outData.setStatus("2");
//                }
//                outData.setStoreCodes(tichengSto);//适用门店
//                outData.setType("销售提成");
//                outData.setTypeValue("1");
//                outData.setStoCode(String.valueOf(roleBySto.get("brId")));
//                outData.setStoName(String.valueOf(roleBySto.get("brName")));
//                //设置
//                outData.setDays(BigDecimalUtil.toBigDecimal(roleBySto.get("days")));
//                outData.setCostAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("costAmt")));
//                outData.setYsAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("ysAmt")));
//                outData.setAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("amt")));
//                outData.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("grossProfitAmt")));
//                outData.setZkAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("zkAmt")));
//
//
//                Map ruleSaleDateMap = brRuleDataMap.get(brId);
//                outData.setTcCostAmt(BigDecimalUtil.toBigDecimal(ruleSaleDateMap.get("costAmt")));//提成商品成本额
//                outData.setTcAmt(BigDecimalUtil.toBigDecimal(ruleSaleDateMap.get("amt")));//提成商品销售额
//                outData.setTcYsAmt(BigDecimalUtil.toBigDecimal(ruleSaleDateMap.get("ysAmt")));//提成商品应收销售额
//                outData.setTcGrossProfitAmt(BigDecimalUtil.toBigDecimal(ruleSaleDateMap.get("grossProfitAmt")));//提成商品销售额
//                BigDecimal tcPrices = BigDecimal.ZERO;//提成金额
//                if (ObjectUtil.isNotEmpty(tichengMs)) {
//                    List<GaiaTichengSaleplanM> role2 = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");
//
//                    //筛选出对应门店和营业员的数据
//                    List<Map> saleData = saleDatas.stream().filter(s -> s.get("brId").equals(item.get("brId"))).collect(Collectors.toList());
//
//                    for (Map saleD : saleData) {
//                        BigDecimal baseAmount = BigDecimalUtil.toBigDecimal(saleD.get("baseAmount"));
//                        BigDecimal grossProfitRate = BigDecimalUtil.toBigDecimal(saleD.get("grossProfitRate"));
//                        //提成方式: 0 按销售额提成 1 按毛利额提成
//                        if ("0".equals(tichengZ.getPlanAmtWay())) {
//                            baseAmount = BigDecimalUtil.toBigDecimal(saleD.get("ysAmt"));
//                        } else if ("1".equals(tichengZ.getPlanAmtWay())) {
//                            baseAmount = BigDecimalUtil.toBigDecimal(saleD.get("grossProfitAmt"));
//                        }
//                        //提成方式:0 按商品毛利率  1 按销售毛利率
//                        //商品毛利率=（应收金额-成本额）/应收金额*100%
//                        //销售毛利率=（实收金额-成本额）/实收金额*100%
//                        if ("0".equals(tichengZ.getPlanRateWay())) {
//                            BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(saleD.get("ysAmt"));
//                            if (ysAmt.compareTo(BigDecimal.ZERO) == 0) {
//                                grossProfitRate = BigDecimal.ZERO;
//                            } else {
//                                BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(saleD.get("grossProfitAmt"));
//                                grossProfitRate = grossProfitAmt.divide(ysAmt, 4, BigDecimal.ROUND_HALF_UP);
//                            }
//                        } else if ("1".equals(tichengZ.getPlanRateWay())) {
//                            BigDecimal amt = BigDecimalUtil.toBigDecimal(saleD.get("amt"));
//                            if (amt.compareTo(BigDecimal.ZERO) == 0) {
//                                grossProfitRate = BigDecimal.ZERO;
//                            } else {
//                                BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(saleD.get("grossProfitAmt"));
//                                grossProfitRate = grossProfitAmt.divide(amt, 4, BigDecimal.ROUND_HALF_UP);
//                            }
//                        }
//                        //退单情况下，毛利率可能存在负数
////                        if(grossProfitRate.doubleValue()<0){
////                            grossProfitRate = new BigDecimal(Math.abs(grossProfitRate.doubleValue()));
////                        }
//                        for (GaiaTichengSaleplanM tichengM : role2) {
//                            if (saleD.get("brId").equals(item.get("brId"))
//                                    && grossProfitRate.multiply(new BigDecimal(100)).compareTo(tichengM.getMinProMll()) == 1
//                                    && grossProfitRate.multiply(new BigDecimal(100)).compareTo(tichengM.getMaxProMll()) < 1) {
//
//                                tcPrices = tcPrices.add(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengM.getTichengScale().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
//                                // 理论上要求只能命中同一条明细
//                                break;
//                            }
//                        }
//                    }
//                }
//                outData.setTiTotal(tcPrices);//提成金额
////                outData.setDeductionWage(BigDecimalUtil.divide(tcPrices, 100, 4));
////                outData.setDeductionWageRate(BigDecimalUtil.divide(outData.getDeductionWage(), roleBySto.get("amt"), 4));
//                outDatas.add(outData);
//            });
//
////                // 遍历map
////            stop = System.currentTimeMillis();
////            long s3 = TimeUnit.MILLISECONDS.toMillis(stop - start);
////            log.info("时间s3:" + String.valueOf(s3));
//        }
//
//        PageInfo pageInfo;
//        if (CollUtil.isNotEmpty(outDatas)) {
//            // 集合列的数据汇总
//            PushMoneyByStoreV5TotalOutData outTotal = new PushMoneyByStoreV5TotalOutData();
//            PushMoneyByStoreV5OutData first = outDatas.get(0);
//            BeanUtils.copyProperties(first, outTotal);
//            outTotal.setStoNum(tichengSto.size());
//            outTotal.setClient(inData.getClient());
//            outTotal.setZPlanName("默认");
//            outTotal.setDays(BigDecimalUtil.toBigDecimal(saleDaysData.get("days")));
//            BigDecimal costAmtTotal = BigDecimal.ZERO;
//            BigDecimal ysAmtTotal = BigDecimal.ZERO;
//            BigDecimal amtTotal = BigDecimal.ZERO;
//            BigDecimal grossProfitAmtTotal = BigDecimal.ZERO;
//            BigDecimal zkAmtTotal = BigDecimal.ZERO;
//            BigDecimal tcCostAmtTotal = BigDecimal.ZERO;
//            BigDecimal tcYsAmtTotal = BigDecimal.ZERO;
//            BigDecimal tcAmtTotal = BigDecimal.ZERO;
//            BigDecimal tcGrossProfitAmtTotal = BigDecimal.ZERO;
//            BigDecimal tiTotal = BigDecimal.ZERO;
//            for (PushMoneyByStoreV5OutData storeData : outDatas) {
//                costAmtTotal = costAmtTotal.add(storeData.getAmt());
//                ysAmtTotal = ysAmtTotal.add(storeData.getYsAmt());
//                amtTotal = amtTotal.add(storeData.getAmt());
//                grossProfitAmtTotal = grossProfitAmtTotal.add(storeData.getGrossProfitAmt());
//                zkAmtTotal = zkAmtTotal.add(storeData.getZkAmt());
//                tcCostAmtTotal = tcCostAmtTotal.add(storeData.getTcCostAmt());
//                tcYsAmtTotal = tcYsAmtTotal.add(storeData.getTcYsAmt());
//                tcAmtTotal = tcAmtTotal.add(storeData.getTcAmt());
//                tcGrossProfitAmtTotal = tcGrossProfitAmtTotal.add(storeData.getTcGrossProfitAmt());
//
//                tiTotal = tiTotal.add(storeData.getTiTotal());
//                outTotal.setTiTotal(tiTotal);//提成金额
//
//            }
//            outTotal.setCostAmt(costAmtTotal);//成本额
//            outTotal.setYsAmt(ysAmtTotal);//应收金额
//            outTotal.setAmt(amtTotal);//实收金额
//            outTotal.setGrossProfitAmt(grossProfitAmtTotal);//毛利额
//            outTotal.setGrossProfitRate(amtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : grossProfitAmtTotal.divide(amtTotal, 4, RoundingMode.HALF_UP));//毛利率
//            outTotal.setZkAmt(zkAmtTotal);//折扣金额
//            outTotal.setZkRate(ysAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : zkAmtTotal.divide(ysAmtTotal, 4, RoundingMode.HALF_UP));//折扣率
//            outTotal.setTcCostAmt(tcCostAmtTotal);//提成商品成本额
//            outTotal.setTcYsAmt(tcYsAmtTotal);//提成商品应收销售额
//            outTotal.setTcAmt(tcAmtTotal);//提成商品销售额
//            outTotal.setTcGrossProfitAmt(tcGrossProfitAmtTotal);//提成商品毛利额
//            outTotal.setTcGrossProfitRate(tcGrossProfitAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tcGrossProfitAmtTotal.divide(tcAmtTotal, 4, RoundingMode.HALF_UP));//提成商品毛利率
//            outTotal.setTiTotal(tiTotal);
//            outTotal.setDeductionWageAmtRate(amtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(amtTotal, 4, RoundingMode.HALF_UP));//提成销售比
//            outTotal.setDeductionWageGrossProfitRate(grossProfitAmtTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : tiTotal.divide(grossProfitAmtTotal, 4, RoundingMode.HALF_UP));
////            for (PushMoneyByStoreV5OutData storeData : outDatas) {
////                outTotal.setAmt(BigDecimalUtil.add(storeData.getAmt(), outTotal.getAmt()));
////                if (ObjectUtil.isNotEmpty(saleDaysData) && StringUtils.isNotEmpty(String.valueOf(saleDaysData.get("days")))) {
////                    outTotal.setDays(BigDecimalUtil.toBigDecimal(saleDaysData.get("days")));
////                }
////                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
////            }
////            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
////                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
////            }
//            pageInfo = new PageInfo(new ArrayList(Arrays.asList(outTotal)), outTotal);
//        } else {
//            pageInfo = new PageInfo();
//        }
//        return pageInfo;
//
//    }

    @Override
    public List<StoreCommissionSummary> calcStoreCommissionSummaryOptimize(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        PageHelper.clearPage();

        String client = storeCommissionSummaryDO.getClient();
        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        // 是否员工提成汇总
        final boolean isSaleManSummary = 2 == summaryType;

        // 获取销售提成方案
        List<StoreCommissionSummary> selectSaleCommissionPlan = tichengSaleplanZMapper.selectSaleCommissionSummaryOptimize(storeCommissionSummaryDO);

        String startDate = storeCommissionSummaryDO.getStartDate();
        String endDate = storeCommissionSummaryDO.getEndDate();

        List<StoreCommissionSummary> tempProductCommissionSummaries = new ArrayList<>();
        Map<String, List<String>> rejectProMap = new HashMap<>();
        Map<String, List<String>> rejectClassMap = new HashMap<>();

        LocalDate nowLocalDate = LocalDate.now();
        String nowDate = nowLocalDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        EmpSaleDetailInData inData = new EmpSaleDetailInData();
        inData.setClient(client);

        // 门店名称
        List<StoreSimpleInfoWithPlan> saleCommissionStores = tichengSaleplanStoMapper.selectStoreByPlanIdAndClient(client, null);
        Map<String, String> storeSimpleInfoMap = saleCommissionStores.stream()
                .collect(Collectors.toMap(StoreSimpleInfoWithPlan::getStoCode, StoreSimpleInfoWithPlan::getStoShortName, (oldValue, newValue) -> newValue));

        // 姓名
        Map<String, String> userNameMap = getUserNameMap(client, isSaleManSummary);

        List<StoreCommissionSummary> result = new ArrayList<>();

        // map缓存门店日均销售额
        Map<String, BigDecimal> storeAvgAmtMap = new HashMap<>();

        // 1. 销售提成主方案，兼容单品提成默认只有一个子方案
        for (StoreCommissionSummary saleCommissionMainPlan : selectSaleCommissionPlan) {
            // 负毛利率商品是否不参与销售提成 0 是 1 否
            String planIfNegative = saleCommissionMainPlan.getPlanIfNegative();
            String planAmtWay = saleCommissionMainPlan.getPlanAmtWay();
            String planId = String.valueOf(saleCommissionMainPlan.getPlanId());
            String planRejectDiscountRateSymbol = saleCommissionMainPlan.getPlanRejectDiscountRateSymbol();
            String planRejectDiscountRate = saleCommissionMainPlan.getPlanRejectDiscountRate();

            // 获取主方案的销售汇总， 根据加盟商，门店，营业员，销售时间过滤
            List<StoreCommissionSummary> saleMainPlanSaleDatas = new ArrayList<>();

            // 存放历史喝实时的提成明细
            List<StoreCommissionSummary> saleCommissionSummaryDetails = new ArrayList<>();

            // 主方案销售数据处理
            boolean flag = commonPreDealWithCalcCommission(storeCommissionSummaryDO, saleCommissionMainPlan,
                    saleMainPlanSaleDatas, saleCommissionSummaryDetails, startDate, endDate,
                    planId, nowDate, nowLocalDate);

            if (CollectionUtil.isEmpty(saleMainPlanSaleDatas)) {
                continue;
            }

            // 获取各个房子门店日均销售额
            doGetStoreAvgAmt(storeCommissionSummaryDO, client, startDate,
                    nowDate, nowLocalDate, flag, planId, storeAvgAmtMap);

            // 获取剔除的商品，商品分类
            doGetFilterCondition(planId, client, saleCommissionMainPlan, rejectClassMap, rejectProMap);

            if (flag) {

                List<Integer> planIds = selectSaleCommissionPlan.stream()
                        .map(StoreCommissionSummary::getPlanId)
                        .distinct().collect(Collectors.toList());
                //  提成规则表
                Weekend<GaiaTichengSaleplanM> commissionRuleWeekend = new Weekend<>(GaiaTichengSaleplanM.class);
                commissionRuleWeekend.weekendCriteria()
                        .andEqualTo(GaiaTichengSaleplanM::getClient, client)
                        .andIn(GaiaTichengSaleplanM::getPid, planIds)
                        .andEqualTo(GaiaTichengSaleplanM::getDeleteFlag, "0");
                List<GaiaTichengSaleplanM> commissionRules = tichengSaleplanMMapper.selectByExample(commissionRuleWeekend);
                // 提成方案规则缓存
                Map<String, List<GaiaTichengSaleplanM>> commissionPlanRuleMap = commissionRules.stream()
                        .collect(Collectors.groupingBy(item -> String.valueOf(item.getPid())));

                List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
                List<String> saleNames = storeCommissionSummaryDO.getSaleName();

                // 获取当天实时数据
                List<EmpSaleDetailResVo> dbList = doGetRealTime(inData, isSaleManSummary, stoCodes,
                        Integer.parseInt(planId), saleNames, nowDate);

                for (EmpSaleDetailResVo detailResVo : dbList) {
                    // 属性设置到新的对象
                    StoreCommissionSummary temp = preSetAttr(detailResVo, storeSimpleInfoMap, planId, "");

                    temp.setPlanStartDate(saleCommissionMainPlan.getPlanStartDate());
                    temp.setPlanEndDate(saleCommissionMainPlan.getPlanEndDate());
                    BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(temp.getZkAmt());
                    BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(temp.getYsAmt());
                    temp.setPlanAmtWay(planAmtWay);

                    // 计算毛利率
                    BigDecimal grossProfitRate = calcGrossProfitRate(temp);
                    temp.setGrossProfitRate(grossProfitRate);
                    // 计算折扣率
                    temp.setZkl(calcDiscountRate(ysAmt, zkAmt));
                    if (StringUtils.isNotEmpty(planRejectDiscountRateSymbol) && StringUtils.isNotEmpty(planRejectDiscountRate)) {
                        BigDecimal zkl = temp.getZkl();
                        // 过滤不在折扣率范围之内的数据
                        if (filterNotInRange(planRejectDiscountRateSymbol, planRejectDiscountRate, zkl)) {
                            continue;
                        }
                    }
                    List<String> rejectPros = rejectProMap.get(String.valueOf(planId));
                    if (CollectionUtil.isNotEmpty(rejectPros) && rejectPros.contains(temp.getProId())) {
                        continue;

                    }
                    List<String> rejectClass = rejectClassMap.get(String.valueOf(planId));
                    if (CollectionUtil.isNotEmpty(rejectClass) && rejectClass.contains(temp.getProClassCode())) {
                        continue;
                    }
                    List<GaiaTichengSaleplanM> gaiaTichengSaleplanMS = commissionPlanRuleMap.get(String.valueOf(temp.getPlanId()));
                    if (CollectionUtil.isNotEmpty(gaiaTichengSaleplanMS)) {
                        // 门店日均销售额
                        BigDecimal amtAvg =
                                BigDecimalUtil.toBigDecimal(storeAvgAmtMap.get(planId + temp.getStoCode()));
                        BigDecimal amt = BigDecimalUtil.toBigDecimal(temp.getAmt());
                        Optional<GaiaTichengSaleplanM> first = gaiaTichengSaleplanMS.stream()
                                .filter(item ->
                                        amtAvg.compareTo(item.getMinDailySaleAmt()) > 0 &&
                                                amtAvg.compareTo(item.getMaxDailySaleAmt()) <= 0 &&
                                                grossProfitRate.multiply(new BigDecimal(100)).compareTo(item.getMinProMll()) > 0 &&
                                                grossProfitRate.multiply(new BigDecimal(100)).compareTo(item.getMaxProMll()) <= 0).findFirst();
                        if (first.isPresent()) {
                            BigDecimal tichengScale = first.get().getTichengScale();
                            BigDecimal baseAmount = BigDecimal.ZERO;
                            //提成方式: 0 按销售额提成 1 按毛利额提成
                            if ("0".equals(planAmtWay)) {
                                baseAmount = amt;
                            } else if ("1".equals(planAmtWay)) {
                                baseAmount = BigDecimalUtil.toBigDecimal(temp.getGrossProfitAmt());
                            }
                            // 提成金额
                            temp.setCommissionAmt(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengScale.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
                            // 提成商品成本额
                            temp.setCommissionCostAmt(BigDecimalUtil.toBigDecimal(temp.getCostAmt()));
                            // 提成商品销售额 销售额 = 应收金额 - 折扣金额 (即提成商品实收金额)
                            temp.setCommissionSales(BigDecimalUtil.toBigDecimal(temp.getAmt()));
                            // 提成商品毛利额 提成商品实收金额 - 提成商品成本金额
                            temp.setCommissionGrossProfitAmt(temp.getCommissionSales().subtract(temp.getCommissionCostAmt()));
                            saleCommissionSummaryDetails.add(temp);
                        }
                    }
                }
            }
            if (CollectionUtil.isEmpty(saleCommissionSummaryDetails)) {
                continue;
            }

            for (StoreCommissionSummary saleMainPlanSaleData : saleMainPlanSaleDatas) {
                saleMainPlanSaleData.setPlanId(Integer.parseInt(planId));
                saleMainPlanSaleData.setSubPlanId("");
                String saleDate = ObjectUtil.defaultIfNull(saleMainPlanSaleData.getSaleDate(), "").toString().replaceAll("-"
                        , "");
                saleMainPlanSaleData.setSaleDateStr(saleDate);
            }
            Boolean showSubPlan = storeCommissionSummaryDO.getShowSubPlan();
            result.addAll(dealResult(saleCommissionSummaryDetails, storeCommissionSummaryDO, saleMainPlanSaleDatas,
                    null, storeSimpleInfoMap, userNameMap, saleCommissionMainPlan, 1));
        }
        return result;
    }

    private void doGetFilterCondition(String planId, String client,
                                      StoreCommissionSummary saleCommissionMainPlan,
                                      Map<String, List<String>> rejectClassMap,
                                      Map<String, List<String>> rejectProMap) {

        // 剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(Integer.parseInt(planId));
        if (rejectClassMap.containsKey(planId)) {
            rejectClass.addAll(CollectionUtil.defaultIfEmpty(rejectClassMap.get("planId"), new ArrayList<>()));
            rejectClassMap.put(planId, rejectClass);
        } else {
            rejectClassMap.put(planId, rejectClass);
        }

        // 查询要剔除的商品 此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码
        GaiaTichengSaleplanZ commissionSalePlan = new GaiaTichengSaleplanZ();
        String planStartDate = saleCommissionMainPlan.getPlanStartDate();
        String planEndDate = saleCommissionMainPlan.getPlanEndDate();
        commissionSalePlan.setId(Integer.parseInt(planId));
        commissionSalePlan.setClient(client);
        commissionSalePlan.setPlanStartDate(planStartDate);
        commissionSalePlan.setPlanEndDate(planEndDate);
        List<String> rejectPro = tichengRejectProMapper.selectPro(commissionSalePlan);
        if (rejectProMap.containsKey(String.valueOf(planId))) {
            rejectPro.addAll(CollectionUtil.defaultIfEmpty(rejectProMap.get("planId"), new ArrayList<>()));
            rejectProMap.put(String.valueOf(planId), rejectPro);
        } else {
            rejectProMap.put(String.valueOf(planId), rejectPro);
        }
    }

    private void doGetStoreAvgAmt(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                  String client, String startDate, String nowDate,
                                  LocalDate nowLocalDate, boolean flag, String planId,
                                  Map<String, BigDecimal> storeAvgAmtMap) {

        // 计算门店日均销售额
        List<StoreAvgAmt> storeAvgAmt;
        // 如果开始时间大于等于当天
        if (Integer.parseInt(startDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
            // 直接查数据库
            List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
            storeAvgAmt = tichengSaleplanZMapper.selectAvgAmtByStore(client, nowDate, stoCodes);
        } else {
            // 结束时间大于等于当天从数据库查
            if (Integer.parseInt(storeCommissionSummaryDO.getEndDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
                // 从麒麟查询当天之前的数据
                storeCommissionSummaryDO.setEndDate(nowLocalDate.minusDays(1).toString());
                storeAvgAmt = calcAvgAmtByStore(storeCommissionSummaryDO);
                List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
                if (flag) {
                    // 从数据库查当天的数据
                    storeAvgAmt.addAll(tichengSaleplanZMapper.selectAvgAmtByStore(client, nowDate, stoCodes));
                }
            } else {
                storeAvgAmt = calcAvgAmtByStore(storeCommissionSummaryDO);
            }
        }

        Map<String, List<StoreAvgAmt>> storeAvgAmtCollectMap = storeAvgAmt.stream().collect(Collectors.groupingBy(StoreAvgAmt::getStoCode));
        for (Map.Entry<String, List<StoreAvgAmt>> entry : storeAvgAmtCollectMap.entrySet()) {
            List<StoreAvgAmt> value = entry.getValue();
            BigDecimal total = value.stream().map(StoreAvgAmt::getAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            Integer reduce = value.stream().map(StoreAvgAmt::getKlDate).filter(Objects::nonNull).reduce(0, Integer::sum);
            storeAvgAmtMap.put(planId + entry.getKey(), BigDecimalUtil.divide(total, reduce, 2));
        }
    }


    @Deprecated
    @Override
    public List<StoreCommissionSummary> calcStoreCommissionSummary(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        PageHelper.clearPage();
        // 销售提成计算
        String client = storeCommissionSummaryDO.getClient();

        // 获取销售提成方案
        List<StoreCommissionSummary> selectSaleCommissionPlan = tichengSaleplanZMapper.selectSaleCommissionSummary(storeCommissionSummaryDO);

        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        // 是否员工提成汇总
        final boolean isSaleManSummary = 2 == summaryType;

        Map<String, String> userNameMap = null;
        // 获取营业员姓名
        if (isSaleManSummary) {
            List<Map<String, String>> maps = tichengSaleplanZMapper.selectUserName(client);
            userNameMap = new HashMap<>();
            for (Map<String, String> map : maps) {
                userNameMap.put(map.get("userId"), map.get("userName"));
            }
        }
        String startDate = storeCommissionSummaryDO.getStartDate();
        String endDate = storeCommissionSummaryDO.getEndDate();
        List<String> saleNames = storeCommissionSummaryDO.getSaleName();
        List<StoreCommissionSummary> tempProductCommissionSummaries = new ArrayList<>();
        Map<String, List<String>> rejectProMap = new HashMap<>();
        Map<String, List<String>> rejectClassMap = new HashMap<>();

        LocalDate nowLocalDate = LocalDate.now();
        String nowDate = nowLocalDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        EmpSaleDetailInData inData = new EmpSaleDetailInData();
        inData.setClient(client);

        // 门店名称
        List<StoreSimpleInfoWithPlan> saleCommissionStores = tichengSaleplanStoMapper.selectStoreByPlanIdAndClient(client, null);
        Map<String, String> storeSimpleInfoMap = saleCommissionStores.stream()
                .collect(Collectors.toMap(StoreSimpleInfoWithPlan::getStoCode, StoreSimpleInfoWithPlan::getStoShortName, (oldValue, newValue) -> newValue));

        // map缓存门店日均销售额
        Map<String, BigDecimal> storeAvgAmtMap = new HashMap<>();

        for (StoreCommissionSummary saleCommissionPlan : selectSaleCommissionPlan) {
            // 负毛利率商品是否不参与销售提成 0 是 1 否
            String planIfNegative = saleCommissionPlan.getPlanIfNegative();

            Integer planId = saleCommissionPlan.getPlanId();
            String planRejectDiscountRateSymbol = saleCommissionPlan.getPlanRejectDiscountRateSymbol();
            String planRejectDiscountRate = saleCommissionPlan.getPlanRejectDiscountRate();

            // 剔除的商品分类
            List<String> rejectClass = tichengRejectClassMapper.selectClass(planId);
            if (rejectClassMap.containsKey(String.valueOf(planId))) {
                rejectClass.addAll(CollectionUtil.defaultIfEmpty(rejectClassMap.get("planId"), new ArrayList<>()));
                rejectClassMap.put(String.valueOf(planId), rejectClass);
            } else {
                rejectClassMap.put(String.valueOf(planId), rejectClass);
            }

            // 查询要剔除的商品 此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码
            GaiaTichengSaleplanZ commissionSalePlan = new GaiaTichengSaleplanZ();
            String planStartDate = saleCommissionPlan.getPlanStartDate();
            String planEndDate = saleCommissionPlan.getPlanEndDate();
            commissionSalePlan.setId(planId);
            commissionSalePlan.setClient(client);
            commissionSalePlan.setPlanStartDate(planStartDate);
            commissionSalePlan.setPlanEndDate(planEndDate);
            List<String> rejectPro = tichengRejectProMapper.selectPro(commissionSalePlan);
            if (rejectProMap.containsKey(String.valueOf(planId))) {
                rejectPro.addAll(CollectionUtil.defaultIfEmpty(rejectProMap.get("planId"), new ArrayList<>()));
                rejectProMap.put(String.valueOf(planId), rejectPro);
            } else {
                rejectProMap.put(String.valueOf(planId), rejectPro);
            }
            String tempEndDate = endDate;
            boolean flag = false;
            // 如果查询时间大于等于当天
            if (Integer.parseInt(endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
                // 把时间重置为昨天
                storeCommissionSummaryDO.setEndDate(nowLocalDate.minusDays(1).toString());
                tempEndDate = storeCommissionSummaryDO.getEndDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
                flag = true;
            }

            transferSearchDateRange(storeCommissionSummaryDO, startDate, tempEndDate, saleCommissionPlan);

            // 如果比较后的结束时间大于方案结束时间 flag置为 false
            if (Integer.parseInt(planEndDate) < Integer.parseInt(nowDate)) {
                flag = false;
            }

            String stoCode = saleCommissionPlan.getStoCode();
            storeCommissionSummaryDO.setStoCodes(Splitter.on(CharUtil.COMMA).splitToList(stoCode));

            // 如果开始时间大于结束时间不查
            if (assertInTimeRange(storeCommissionSummaryDO.getStartDate(), storeCommissionSummaryDO.getEndDate(), nowDate)) {
                // 销售提成方案下商品数据，不包括剔除的
                List<StoreCommissionSummary> storeCommissionSummaries = generateSqlAndResult(storeCommissionSummaryDO, null, null, planIfNegative, planRejectDiscountRateSymbol, planRejectDiscountRate, false);
                if (CollectionUtil.isNotEmpty(storeCommissionSummaries)) {
                    for (StoreCommissionSummary storeCommissionSummary : storeCommissionSummaries) {
                        StoreCommissionSummary temp = new StoreCommissionSummary();
                        BeanUtil.copyProperties(storeCommissionSummary, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        BeanUtil.copyProperties(saleCommissionPlan, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
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
                inData.setPlanId(planId);
                inData.setToday(nowDate);
                inData.setStoArr(storeCommissionSummaryDO.getStoCodes());
                if (isSaleManSummary) {
                    inData.setNameSearchType("1");
                    inData.setNameSearchIdList(saleNames);
                }
                List<EmpSaleDetailResVo> dbList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
                renderCommissionDataWithToday(saleCommissionPlan, dbList, userNameMap, storeSimpleInfoMap, tempProductCommissionSummaries);
            }

            // 计算门店日均销售额
            List<StoreAvgAmt> storeAvgAmt;
            // 如果开始时间大于等于当天
            if (Integer.parseInt(startDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
                // 直接查数据库
                List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
                storeAvgAmt = tichengSaleplanZMapper.selectAvgAmtByStore(client, nowDate, stoCodes);
            } else {
                // 结束时间大于等于当天从数据库查
                if (Integer.parseInt(endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
                    // 从麒麟查询当天之前的数据
                    storeCommissionSummaryDO.setEndDate(nowLocalDate.minusDays(1).toString());
                    storeAvgAmt = calcAvgAmtByStore(storeCommissionSummaryDO);
                    List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
                    if (flag) {
                        // 从数据库查当天的数据
                        storeAvgAmt.addAll(tichengSaleplanZMapper.selectAvgAmtByStore(client, nowDate, stoCodes));
                    }
                } else {
                    storeAvgAmt = calcAvgAmtByStore(storeCommissionSummaryDO);
                }
            }

            Map<String, List<StoreAvgAmt>> storeAvgAmtCollectMap = storeAvgAmt.stream().collect(Collectors.groupingBy(StoreAvgAmt::getStoCode));
            for (Map.Entry<String, List<StoreAvgAmt>> entry : storeAvgAmtCollectMap.entrySet()) {
                List<StoreAvgAmt> value = entry.getValue();
                BigDecimal total = value.stream().map(StoreAvgAmt::getAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                Integer reduce = value.stream().map(StoreAvgAmt::getKlDate).filter(Objects::nonNull).reduce(0, Integer::sum);
                storeAvgAmtMap.put(planId + entry.getKey(), BigDecimalUtil.divide(total, reduce, 2));
            }
        }

        if (CollectionUtil.isNotEmpty(tempProductCommissionSummaries)) {

            List<Integer> planIds = selectSaleCommissionPlan.stream()
                    .map(StoreCommissionSummary::getPlanId)
                    .distinct().collect(Collectors.toList());
            //  提成规则表
            Weekend<GaiaTichengSaleplanM> commissionRuleWeekend = new Weekend<>(GaiaTichengSaleplanM.class);
            commissionRuleWeekend.weekendCriteria()
                    .andEqualTo(GaiaTichengSaleplanM::getClient, client)
                    .andIn(GaiaTichengSaleplanM::getPid, planIds)
                    .andEqualTo(GaiaTichengSaleplanM::getDeleteFlag, "0");
            List<GaiaTichengSaleplanM> commissionRules = tichengSaleplanMMapper.selectByExample(commissionRuleWeekend);
            // 提成方案规则缓存
            Map<String, List<GaiaTichengSaleplanM>> commissionPlanRuleMap = commissionRules.stream().collect(Collectors.groupingBy(item -> String.valueOf(item.getPid())));

            // 合计销售天数
            Set<String> totalSaleDays = new HashSet<>();
            Iterator<StoreCommissionSummary> iterator = tempProductCommissionSummaries.iterator();
            while (iterator.hasNext()) {
                StoreCommissionSummary storeCommissionSummary = iterator.next();
                Integer planId = storeCommissionSummary.getPlanId();
                totalSaleDays.add(storeCommissionSummary.getSaleDate().toString());
                // 如果设置了剔除折扣率
                String planRejectDiscountRateSymbol = storeCommissionSummary.getPlanRejectDiscountRateSymbol();
                String planRejectDiscountRate = storeCommissionSummary.getPlanRejectDiscountRate();

                BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getZkAmt());
                BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getYsAmt());

                // 计算毛利率
                BigDecimal grossProfitRate = calcGrossProfitRate(storeCommissionSummary);
                if (!storeCommissionSummary.getRealTime()) {
                    // 不是实时数据计算
                    // 计算毛利率
                    storeCommissionSummary.setGrossProfitRate(grossProfitRate);
                    // 计算折扣率
                    storeCommissionSummary.setZkl(calcDiscountRate(ysAmt, zkAmt));
                }
                if (StringUtils.isNotEmpty(planRejectDiscountRateSymbol) && StringUtils.isNotEmpty(planRejectDiscountRate)) {
                    BigDecimal zkl = storeCommissionSummary.getZkl();
                    // 过滤不在折扣率范围之内的数据
                    if (filterNotInRange(planRejectDiscountRateSymbol, planRejectDiscountRate, zkl)) {
                        continue;
                    }
                }
                List<String> rejectPros = rejectProMap.get(String.valueOf(planId));
                if (CollectionUtil.isNotEmpty(rejectPros) && rejectPros.contains(storeCommissionSummary.getProId())) {
                    continue;

                }
                List<String> rejectClass = rejectClassMap.get(String.valueOf(planId));
                if (CollectionUtil.isNotEmpty(rejectClass) && rejectClass.contains(storeCommissionSummary.getProClassCode())) {
                    continue;

                }
                List<GaiaTichengSaleplanM> gaiaTichengSaleplanMS = commissionPlanRuleMap.get(String.valueOf(storeCommissionSummary.getPlanId()));
                if (CollectionUtil.isNotEmpty(gaiaTichengSaleplanMS)) {
                    // 门店日均销售额
                    BigDecimal amtAvg =
                            BigDecimalUtil.toBigDecimal(storeAvgAmtMap.get(planId + storeCommissionSummary.getStoCode()));
                    BigDecimal amt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getAmt());
                    Optional<GaiaTichengSaleplanM> first = gaiaTichengSaleplanMS.stream()
                            .filter(item ->
                                    amtAvg.compareTo(item.getMinDailySaleAmt()) > 0 &&
                                            amtAvg.compareTo(item.getMaxDailySaleAmt()) <= 0 &&
                                            grossProfitRate.multiply(new BigDecimal(100)).compareTo(item.getMinProMll()) > 0 &&
                                            grossProfitRate.multiply(new BigDecimal(100)).compareTo(item.getMaxProMll()) <= 0).findFirst();
                    if (StrUtil.isNotBlank(storeCommissionSummary.getPlanIfNegative()) && "0".equals(storeCommissionSummary.getPlanIfNegative())) {
                        if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                            continue;
                        }
                    }
                    if (first.isPresent()) {
                        BigDecimal tichengScale = first.get().getTichengScale();
                        String planAmtWay = storeCommissionSummary.getPlanAmtWay();
                        BigDecimal baseAmount = BigDecimal.ZERO;
                        //提成方式: 0 按销售额提成 1 按毛利额提成
                        if ("0".equals(planAmtWay)) {
                            baseAmount = amt;
                        } else if ("1".equals(planAmtWay)) {
                            baseAmount = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getGrossProfitAmt());
                        }
                        // 提成金额
                        storeCommissionSummary.setCommissionAmt(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengScale.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
                        // 提成商品成本额
                        storeCommissionSummary.setCommissionCostAmt(BigDecimalUtil.toBigDecimal(storeCommissionSummary.getCostAmt()));
                        // 提成商品销售额 销售额 = 应收金额 - 折扣金额 (即提成商品实收金额)
                        storeCommissionSummary.setCommissionSales(BigDecimalUtil.toBigDecimal(storeCommissionSummary.getAmt()));
                        // 提成商品毛利额 提成商品实收金额 - 提成商品成本金额
                        storeCommissionSummary.setCommissionGrossProfitAmt(storeCommissionSummary.getCommissionSales().subtract(storeCommissionSummary.getCommissionCostAmt()));
                    }
                }
            }
            // 是否日期汇总
            final boolean isSaleDateSummary = 2 == storeCommissionSummaryDO.getDisplayGranularity();
            Boolean showSubPlan = storeCommissionSummaryDO.getShowSubPlan();
            Boolean showStore = storeCommissionSummaryDO.getShowStore();
            Map<String, Boolean> costAmtShowConfigMap = storeCommissionSummaryDO.getCostAmtShowConfigMap();
            return groupingByStore(tempProductCommissionSummaries, showSubPlan, showStore, isSaleManSummary,
                    isSaleDateSummary, totalSaleDays, 1, null, costAmtShowConfigMap);
        }
        return new ArrayList<>();
    }

    /**
     * 计算门店日均销售额
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return List<StoreAvgAmt>
     */
    private List<StoreAvgAmt> calcAvgAmtByStore(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        String sbSql = "SELECT\n" +
                "\tgssd.GSSD_BR_ID stoCode,\n" +
                "\tSUM(gssd.GSSD_AMT) amt,\n" +
                "\tCOUNT(DISTINCT GSSD_KL_DATE_BR) klDate\n" +
                "FROM GAIA_SD_SALE_D gssd\n" +
                "INNER JOIN GAIA_PRODUCT_BUSINESS gpb ON gpb.CLIENT = gssd.CLIENT AND gpb.PRO_SITE = gssd.GSSD_BR_ID AND gpb.PRO_SELF_CODE = gssd.GSSD_PRO_ID \n" +
                "WHERE gssd.CLIENT = '%s' \n" +
                "AND gssd.GSSD_BR_ID !='' AND gssd.GSSD_BR_ID IS NOT NULL\n" +
                "AND gssd.GSSD_DATE >= '%s'\n" +
                "AND gssd.GSSD_DATE <= '%s'\n" +
                "GROUP BY gssd.CLIENT, gssd.GSSD_BR_ID";
        String sql = String.format(sbSql, storeCommissionSummaryDO.getClient(), storeCommissionSummaryDO.getStartDate(), storeCommissionSummaryDO.getEndDate());
        return kylinJdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(StoreAvgAmt.class));
    }

    /**
     * 计算毛利率
     *
     * @param storeCommissionSummary storeCommissionSummary
     * @return BigDecimal
     */
    private BigDecimal calcGrossProfitRate(StoreCommissionSummary storeCommissionSummary) {
        String planRateWay = storeCommissionSummary.getPlanRateWay();
        BigDecimal grossProfitRate = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getGrossProfitRate());
        //提成方式:0 按商品毛利率  1 按销售毛利率
        //商品毛利率=（应收金额-成本额）/应收金额*100%
        //销售毛利率=（实收金额-成本额）/实收金额*100%
        if ("0".equals(planRateWay)) {
            BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getYsAmt());
            if (ysAmt.compareTo(BigDecimal.ZERO) == 0) {
                grossProfitRate = BigDecimal.ZERO;
            } else {
                BigDecimal costAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getCostAmt());
                grossProfitRate = ysAmt.subtract(costAmt).divide(ysAmt, 4, BigDecimal.ROUND_HALF_UP);
            }
        } else if ("1".equals(planRateWay)) {
            BigDecimal amt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getAmt());
            if (amt.compareTo(BigDecimal.ZERO) == 0) {
                grossProfitRate = BigDecimal.ZERO;
            } else {
                BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(storeCommissionSummary.getGrossProfitAmt());
                grossProfitRate = grossProfitAmt.divide(amt, 4, BigDecimal.ROUND_HALF_UP);
            }
        }
        return grossProfitRate;
    }

    /**
     * 计算折扣率
     *
     * @param ysAmt 应收金额
     * @param zkAmt 折扣金额
     * @return 折扣率
     */
    private BigDecimal calcDiscountRate(BigDecimal ysAmt, BigDecimal zkAmt) {
        if (ysAmt.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimalUtil.divide(zkAmt, ysAmt, 4);
        }
    }


    /**
     * 销售提成根据门店日均销售额，命中几个规则
     *
     * @param stoLvData
     * @param tichengMs
     * @return
     */
    private Map<String, Map> getStoTichengM(List<TiChenSTORes> stoLvData, List<GaiaTichengSaleplanM> tichengMs) {
        Map<String, Map> roleGroups = new HashMap<>();
        if (ObjectUtil.isNotEmpty(stoLvData)) {
            for (TiChenSTORes stoLv : stoLvData) {
                Map<String, Object> map = new HashMap<>();
                List<GaiaTichengSaleplanM> roles = new ArrayList<>();
                BigDecimal amtAvg = BigDecimalUtil.toBigDecimal(stoLv.getAmtAvg());
                if (ObjectUtil.isNotEmpty(amtAvg) || ObjectUtil.isNotEmpty(tichengMs)) {
                    for (GaiaTichengSaleplanM tichengM : tichengMs) {
                        if (tichengM.getMinDailySaleAmt() == null || tichengM.getMaxDailySaleAmt() == null) {
                            continue;
                        }
                        if (amtAvg.compareTo(tichengM.getMinDailySaleAmt()) > 0
                                && amtAvg.compareTo(tichengM.getMaxDailySaleAmt()) <= 0) {
                            roles.add(tichengM);
                        }
                    }
                }
                map.put("brId", stoLv.getBrId());//门店编号
                map.put("brName", stoLv.getBrName());//门店名称
                map.put("days", stoLv.getDays());//门店销售天数
                map.put("amt", stoLv.getAmt());//实收金额
                map.put("ysAmt", stoLv.getYsAmt());//销售额
                map.put("grossProfitAmt", stoLv.getGrossProfitAmt());//毛利额
                map.put("grossProfitRate", stoLv.getGrossProfitRate());//毛利率
                map.put("zkAmt", stoLv.getZkAmt());//折扣金额
                map.put("costAmt", stoLv.getCostAmt());//成本额
                map.put("amtAvg", stoLv.getAmtAvg());//门店日销售额
                map.put("roles", roles);
                roleGroups.put(stoLv.getBrId(), map);
            }

        }
        return roleGroups;
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

    private Boolean handleEmpSaleDetailLeagal(GaiaTichengSaleplanZ tichengZ, EmpSaleDetailResVo detailResVo, List<GaiaTichengSaleplanM> saleplanMS) {
        Boolean resFlag = false;
        BigDecimal tiToal = BigDecimal.ZERO;
        BigDecimal baseAmount = BigDecimal.ZERO;
        BigDecimal grossProfitRate = BigDecimal.ZERO;
        //提成方式: 0 按销售额提成 1 按毛利额提成
        if ("0".equals(tichengZ.getPlanAmtWay())) {
            baseAmount = new BigDecimal(detailResVo.getAmt());
        } else if ("1".equals(tichengZ.getPlanAmtWay())) {
            BigDecimal amt = BigDecimalUtil.toBigDecimal(detailResVo.getAmt());
            BigDecimal costAmt = BigDecimalUtil.toBigDecimal(detailResVo.getCostAmt());
            baseAmount = amt.subtract(costAmt);
        }
        //提成方式:0 按商品毛利率  1 按销售毛利率
        //商品毛利率=（应收金额-成本额）/应收金额*100%
        //销售毛利率=（实收金额-成本额）/实收金额*100%
        BigDecimal ysAmt = new BigDecimal(detailResVo.getYsAmt());
        BigDecimal amt = new BigDecimal(detailResVo.getAmt());
        BigDecimal costAmt = new BigDecimal(detailResVo.getCostAmt());
        if ("0".equals(tichengZ.getPlanRateWay())) {
            if (ysAmt.compareTo(BigDecimal.ZERO) == 0) {
                grossProfitRate = BigDecimal.ZERO;
            } else {
                BigDecimal grossProfitAmt = ysAmt.subtract(costAmt);
                grossProfitRate = grossProfitAmt.divide(ysAmt, 4, BigDecimal.ROUND_HALF_UP);
            }
        } else if ("1".equals(tichengZ.getPlanRateWay())) {
            if (amt.compareTo(BigDecimal.ZERO) == 0) {
                grossProfitRate = BigDecimal.ZERO;
            } else {
                BigDecimal grossProfitAmt = amt.subtract(costAmt);
                grossProfitRate = grossProfitAmt.divide(amt, 4, BigDecimal.ROUND_HALF_UP);
            }
        }
        GaiaTichengSaleplanM finalSaleplanM = null;
        //对每个商品进行过滤，只有满足毛利率符合的情况下才进行计算
        if (CollectionUtil.isNotEmpty(saleplanMS)) {
            for (GaiaTichengSaleplanM saleplanM : saleplanMS) {
                if (grossProfitRate.multiply(new BigDecimal(100)).compareTo(saleplanM.getMinProMll()) > 0
                        && grossProfitRate.multiply(new BigDecimal(100)).compareTo(saleplanM.getMaxProMll()) <= 0) {
                    finalSaleplanM = saleplanM;
                    // 理论上要求只能命中同一条明细
                    break;
                }
            }
        }
        if (finalSaleplanM != null) {
            //表示触发提成
            resFlag = true;
        }
        return resFlag;
    }

    private BigDecimal handleEmpSaleDetailTiTotal(GaiaTichengSaleplanZ tichengZ, EmpSaleDetailResVo detailResVo, List<GaiaTichengSaleplanM> saleplanMS) {
        BigDecimal tiToal = BigDecimal.ZERO;
        BigDecimal baseAmount = BigDecimal.ZERO;
        BigDecimal grossProfitRate = BigDecimal.ZERO;
        //提成方式: 0 按销售额提成 1 按毛利额提成
        if ("0".equals(tichengZ.getPlanAmtWay())) {
            baseAmount = new BigDecimal(detailResVo.getAmt());
        } else if ("1".equals(tichengZ.getPlanAmtWay())) {
//            BigDecimal amt = BigDecimalUtil.toBigDecimal(detailResVo.getAmt());
//            BigDecimal costAmt = BigDecimalUtil.toBigDecimal(detailResVo.getCostAmt());
            baseAmount = BigDecimalUtil.toBigDecimal(detailResVo.getGrossProfitAmt()); //amt.subtract(costAmt);
        }
        //提成方式:0 按商品毛利率  1 按销售毛利率
        //商品毛利率=（应收金额-成本额）/应收金额*100%
        //销售毛利率=（实收金额-成本额）/实收金额*100%
        BigDecimal ysAmt = new BigDecimal(detailResVo.getYsAmt());
        BigDecimal amt = new BigDecimal(detailResVo.getAmt());
        BigDecimal costAmt = StrUtil.isBlank(detailResVo.getCostAmt()) ? BigDecimal.ZERO : new BigDecimal(detailResVo.getCostAmt());
        if ("0".equals(tichengZ.getPlanRateWay())) {
            if (ysAmt.compareTo(BigDecimal.ZERO) == 0) {
                grossProfitRate = BigDecimal.ZERO;
            } else {
//                BigDecimal grossProfitAmt = ysAmt.subtract(costAmt);
                grossProfitRate = ysAmt.subtract(costAmt).divide(ysAmt, 4, BigDecimal.ROUND_HALF_UP);
            }
        } else if ("1".equals(tichengZ.getPlanRateWay())) {
            if (amt.compareTo(BigDecimal.ZERO) == 0) {
                grossProfitRate = BigDecimal.ZERO;
            } else {
                BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(detailResVo.getGrossProfitAmt()); // amt.subtract(costAmt);
                grossProfitRate = grossProfitAmt.divide(amt, 4, BigDecimal.ROUND_HALF_UP);
            }
        }
        GaiaTichengSaleplanM finalSaleplanM = null;
        //对每个商品进行过滤，只有满足毛利率符合的情况下才进行计算
        if (CollectionUtil.isNotEmpty(saleplanMS)) {
            for (GaiaTichengSaleplanM saleplanM : saleplanMS) {
                if (grossProfitRate.multiply(new BigDecimal(100)).compareTo(saleplanM.getMinProMll()) > 0
                        && grossProfitRate.multiply(new BigDecimal(100)).compareTo(saleplanM.getMaxProMll()) <= 0) {
                    finalSaleplanM = saleplanM;
                    // 理论上要求只能命中同一条明细
                    break;
                }
            }
        }
        if (finalSaleplanM != null) {
            tiToal = tiToal.add(baseAmount.multiply(finalSaleplanM.getTichengScale().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));
            if (StrUtil.isNotBlank(tichengZ.getPlanIfNegative()) && "0".equals(tichengZ.getPlanIfNegative())) {
                if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                    tiToal = BigDecimal.ZERO;
                }
            }
        }
        return BigDecimalUtil.format(tiToal, 4);
    }


    @Override
    public PageInfo empSaleDetailList(EmpSaleDetailInData inData,
                                      List<EmpSaleDetailResVo> empSaleDetailResVoList,
                                      Map<String, GaiaProductBusiness> productBusinessInfoMap,
                                      Map<String, Object> userIdNameMap,
                                      Map<String, GaiaProductClass> productClassMap,
                                      Map<String, Boolean> costAmtShowConfigMap) {
        PageInfo pageInfo = new PageInfo();
        List<EmpSaleDetailResVo> outDatas = new ArrayList<>();
        //  1.查询当前销售计划
        Map queryMap = new HashMap();                       // queryMap没用上
        queryMap.put("client", inData.getClient());
        queryMap.put("planId", inData.getPlanId());
        GaiaTichengSaleplanZ tichengSalePlanZQuery = new GaiaTichengSaleplanZ();
        tichengSalePlanZQuery.setClient(inData.getClient());
        tichengSalePlanZQuery.setId(inData.getPlanId());
        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.select(tichengSalePlanZQuery);
        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
            throw new BusinessException("销售提成设置不正确,请检查设置!");
        }
        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
        String startDate = null;
        String endDate = null;
        startDate = DateUtil.format(DateUtil.parse(inData.getStartDate()), DatePattern.NORM_DATE_PATTERN);
        endDate = DateUtil.format(DateUtil.parse(inData.getEndDate()), DatePattern.NORM_DATE_PATTERN);

        //  提成门店表
        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getPlanId());
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }
        //  提成规则表
        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getPlanId()).andEqualTo("deleteFlag", "0");
        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);

        //  剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getPlanId());
        //  剔除商品
        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);//此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码

        //按门店级别进行汇总，查询销售方案涉及到时间段内的涉及到的所有商品的数据（销售天数/成本额/应收金额/实收金额/毛利额/毛利率/折扣金额/折扣率/日均销售）
        List<TiChenSTORes> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto, inData.getPlanIfNegative());


        Map<String, Map> stoTichengMs = getStoTichengM(stoLvData, tichengMs);


        //计算提成金额
        if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {

            for (EmpSaleDetailResVo detailResVo : empSaleDetailResVoList) {
                //过滤毛利率是负数的情况
                if ("0".equals(tichengZ.getPlanIfNegative())) {
                    String grossProfitRate = detailResVo.getGrossProfitRate();
                    if (StrUtil.isNotBlank(grossProfitRate) && grossProfitRate.contains("-")) {
                        continue;
                    }
                }
                BigDecimal tiTotal = BigDecimal.ZERO;
                EmpSaleDetailResVo out = new EmpSaleDetailResVo();
                String stoCode = detailResVo.getStoCode();
                Map roleBySto = stoTichengMs.get(stoCode);
                if (CollectionUtil.isNotEmpty(roleBySto)) {
                    //获取已经命中的规则（达成门店人均销售的规则）
                    List<GaiaTichengSaleplanM> saleplanMS = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");


                    //只有符合门店日均的才需要计算提成
                    if (CollectionUtil.isNotEmpty(saleplanMS)) {
                        //剔除商品
                        if (rejectPro.contains(detailResVo.getProSelfCode())) {
//                            tiTotal = BigDecimal.ZERO;
                            continue;
                        } else {
                            //剔除商品分类
                            if (rejectClass.contains(detailResVo.getProClassCode())) {
                                continue;
//                                tiTotal = BigDecimal.ZERO;
                            } else {
                                //剔除折扣率
                                if (StringUtils.isNotBlank(tichengZ.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotBlank(tichengZ.getPlanRejectDiscountRate())) {
                                    BigDecimal planRejectDiscountRate = new BigDecimal(tichengZ.getPlanRejectDiscountRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                                    if (handleEmpSaleDetailRejectDiscountRateSymbol(tichengZ.getPlanRejectDiscountRateSymbol(), new BigDecimal(detailResVo.getZkl()), planRejectDiscountRate)) {
//                                        tiTotal = BigDecimal.ZERO;
                                        continue;
                                    } else {
                                        if (!handleEmpSaleDetailLeagal(tichengZ, detailResVo, saleplanMS)) {
                                            continue;
                                        } else {
                                            //加入处理
                                            BigDecimal ti = handleEmpSaleDetailTiTotal(tichengZ, detailResVo, saleplanMS);
                                            tiTotal = tiTotal.add(ti);
                                        }
                                    }

                                } else {
                                    if (!handleEmpSaleDetailLeagal(tichengZ, detailResVo, saleplanMS)) {
                                        continue;
                                    } else {
                                        //加入处理
                                        BigDecimal ti = handleEmpSaleDetailTiTotal(tichengZ, detailResVo, saleplanMS);
                                        tiTotal = tiTotal.add(ti);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.err.println(stoCode);
                }
                //组装明细数据
                out.setPlanId(tichengZ.getId().toString());
                out.setPlanName(tichengZ.getPlanName());
                out.setStartDate(tichengZ.getPlanStartDate());
                out.setEndDate(tichengZ.getPlanEndDate());
                out.setType("1");
                out.setCPlanName("默认");
                out.setSalerId(detailResVo.getSalerId());
                out.setSalerName(userIdNameMap.get(detailResVo.getSalerId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getSalerId()));
                out.setStoCode(detailResVo.getStoCode());
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
                out.setTiTotal(tiTotal.toPlainString());

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

                //调整为只有有提成才需要展示
                if (tiTotal.compareTo(BigDecimal.ZERO) != 0) {
                    outDatas.add(out);
                } else if (tiTotal.compareTo(BigDecimal.ZERO) == 0 && StrUtil.isNotBlank(detailResVo.getGrossProfitAmt()) && new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO) == 0) {
                    outDatas.add(out);
                }
            }
        }
        pageInfo.setList(outDatas);
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
        List<EmpSaleDetailResVo> outDatas = new ArrayList<>();
        //  1.查询当前销售计划
        Map queryMap = new HashMap();                       // queryMap没用上
        queryMap.put("client", inData.getClient());
        queryMap.put("planId", inData.getPlanId());
        GaiaTichengSaleplanZ tichengSalePlanZQuery = new GaiaTichengSaleplanZ();
        tichengSalePlanZQuery.setClient(inData.getClient());
        tichengSalePlanZQuery.setId(inData.getPlanId());
        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.select(tichengSalePlanZQuery);
        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
            throw new BusinessException("销售提成设置不正确,请检查设置!");
        }
        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
        String startDate = null;
        String endDate = null;
        startDate = DateUtil.format(DateUtil.parse(inData.getStartDate()), DatePattern.NORM_DATE_PATTERN);
        endDate = DateUtil.format(DateUtil.parse(inData.getEndDate()), DatePattern.NORM_DATE_PATTERN);

        //  提成门店表
        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getPlanId());
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }
        //  提成规则表
        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getPlanId()).andEqualTo("deleteFlag", "0");
        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);

        //  剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getPlanId());
        //  剔除商品
        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);//此处已经包括了同期单品提成设置中设置的不参与销售提成的商品编码

        //按门店级别进行汇总，查询销售方案涉及到时间段内的涉及到的所有商品的数据（销售天数/成本额/应收金额/实收金额/毛利额/毛利率/折扣金额/折扣率/日均销售）
        List<TiChenSTORes> stoLvData = caculatePlanForAllProduct(startDate, endDate, inData.getClient(), querySto, inData.getPlanIfNegative());


        Map<String, Map> stoTichengMs = getStoTichengM(stoLvData, tichengMs);


        //计算提成金额
        if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {

            for (EmpSaleDetailResVo detailResVo : empSaleDetailResVoList) {
                //过滤毛利率是负数的情况
                if ("0".equals(tichengZ.getPlanIfNegative())) {
                    String grossProfitRate = detailResVo.getGrossProfitRate();
                    if (StrUtil.isNotBlank(grossProfitRate) && grossProfitRate.contains("-")) {
                        continue;
                    }
                }
                BigDecimal tiTotal = BigDecimal.ZERO;
                EmpSaleDetailResVo out = new EmpSaleDetailResVo();
                String stoCode = detailResVo.getStoCode();
                Map roleBySto = stoTichengMs.get(stoCode);
                if (CollectionUtil.isNotEmpty(roleBySto)) {
                    //获取已经命中的规则（达成门店人均销售的规则）
                    List<GaiaTichengSaleplanM> saleplanMS = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");


                    //只有符合门店日均的才需要计算提成
                    if (CollectionUtil.isNotEmpty(saleplanMS)) {
                        //剔除商品
                        if (rejectPro.contains(detailResVo.getProSelfCode())) {
//                            tiTotal = BigDecimal.ZERO;
                            continue;
                        } else {
                            //剔除商品分类
                            if (rejectClass.contains(detailResVo.getProClassCode())) {
                                continue;
//                                tiTotal = BigDecimal.ZERO;
                            } else {
                                //剔除折扣率
                                if (StringUtils.isNotBlank(tichengZ.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotBlank(tichengZ.getPlanRejectDiscountRate())) {
                                    BigDecimal planRejectDiscountRate = new BigDecimal(tichengZ.getPlanRejectDiscountRate()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                                    if (handleEmpSaleDetailRejectDiscountRateSymbol(tichengZ.getPlanRejectDiscountRateSymbol(), new BigDecimal(detailResVo.getZkl()), planRejectDiscountRate)) {
//                                        tiTotal = BigDecimal.ZERO;
                                        continue;
                                    } else {
                                        if (!handleEmpSaleDetailLeagal(tichengZ, detailResVo, saleplanMS)) {
                                            continue;
                                        } else {
                                            //加入处理
                                            BigDecimal ti = handleEmpSaleDetailTiTotal(tichengZ, detailResVo, saleplanMS);
                                            tiTotal = tiTotal.add(ti);
                                        }
                                    }

                                } else {
                                    if (!handleEmpSaleDetailLeagal(tichengZ, detailResVo, saleplanMS)) {
                                        continue;
                                    } else {
                                        //加入处理
                                        BigDecimal ti = handleEmpSaleDetailTiTotal(tichengZ, detailResVo, saleplanMS);
                                        tiTotal = tiTotal.add(ti);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.err.println(stoCode);
                }
                //组装明细数据
                out.setPlanId(tichengZ.getId().toString());
                out.setPlanName(tichengZ.getPlanName());
                out.setStartDate(tichengZ.getPlanStartDate());
                out.setEndDate(tichengZ.getPlanEndDate());
                out.setType("1");
                out.setCPlanName("默认");
                out.setSalerId(detailResVo.getSalerId());
                out.setSalerName(userIdNameMap.get(detailResVo.getSalerId()) == null ? "" : (String) userIdNameMap.get(detailResVo.getSalerId()));
                out.setStoCode(detailResVo.getStoCode());
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
                out.setTiTotal(tiTotal.toPlainString());

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

                //调整为只有有提成才需要展示
                if (tiTotal.compareTo(BigDecimal.ZERO) != 0) {
                    outDatas.add(out);
                } else if (tiTotal.compareTo(BigDecimal.ZERO) == 0 && StrUtil.isNotBlank(detailResVo.getGrossProfitAmt()) && new BigDecimal(detailResVo.getGrossProfitAmt()).compareTo(BigDecimal.ZERO) == 0) {
                    outDatas.add(out);
                }

            }
        }

        if (CollectionUtil.isNotEmpty(userCommissionSummaryDetails)) {
            for (EmpSaleDetailResVo userCommissionSummaryDetail : userCommissionSummaryDetails) {
                // 验证是否显示金额
                checkIsShowCostAmt(userCommissionSummaryDetail,
                        costAmtShowConfigMap,
                        userCommissionSummaryDetail.getStoCode());
            }
            // 历史提成数据添加
            outDatas.addAll(userCommissionSummaryDetails);
        }
        pageInfo.setList(outDatas);
        return pageInfo;
    }


    private boolean filterNotInRange(String planRejectDiscountRateSymbol, String planRejectDiscountRate, BigDecimal zkl) {
        if (zkl == null) {
            zkl = BigDecimal.ZERO;
        }
        zkl = zkl.multiply(BigDecimalUtil.ONE_HUNDRED);
        BigDecimal planRejectDiscountRateBd = new BigDecimal(planRejectDiscountRate);
        switch (planRejectDiscountRateSymbol) {
            case "=":
                return zkl.compareTo(planRejectDiscountRateBd) == 0;
            case ">":
                return zkl.compareTo(planRejectDiscountRateBd) > 0;
            case ">=":
                return zkl.compareTo(planRejectDiscountRateBd) >= 0;
            case "<":
                return zkl.compareTo(planRejectDiscountRateBd) < 0;
            case "<=":
                return zkl.compareTo(planRejectDiscountRateBd) <= 0;
            default:
                return false;
        }
    }

    /**
     * 销售提成汇总sql
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param rejectClass              剔除商品分类
     * @param rejectPro                剔除商品
     * @param planIfNegative           负毛利率商品是否不参与销售提成 0 是 1 否
     * @param onlyWeedOutPro           只查询剔除的商品
     * @return List<StoreCommissionSummary>
     */
    public List<StoreCommissionSummary> generateSqlAndResult(StoreCommissionSummaryDO storeCommissionSummaryDO, List<String> rejectClass,
                                                             List<String> rejectPro, String planIfNegative, String planRejectDiscountRateSymbol,
                                                             String planRejectDiscountRate, boolean onlyWeedOutPro) {
        StringBuilder sqlSb = new StringBuilder();
        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        sqlSb.append("\nSELECT\n" +
                "\tgssd.CLIENT client,\n" +
                "\tgssd.GSSD_BR_ID stoCode,\n" +
                "\tgssd.GSSD_DATE saleDate,\n" +
                "\tgssd.GSSD_SERIAL,\n" +
                "\tMAX(gpb.PRO_CLASS) proClassCode,\n" +
                "\tgssd.GSSD_PRO_ID proId,\n");
        if (2 == summaryType) {
            // 员工提成汇总
            sqlSb.append("\tgssd.GSSD_SALER_ID saleManCode,\n");
        }
        // 如果设置了剔除折扣率
//        if (StringUtils.isNotEmpty(planRejectDiscountRateSymbol) && StringUtils.isNotEmpty(planRejectDiscountRate)) {
        sqlSb.append("ROUND((case when (sum( GSSD_AMT ) + sum( GSSD_ZK_AMT ))=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end), 2) as zkl,\n ");
//        }
        sqlSb.append("\tROUND(SUM(gssd.GSSD_MOV_PRICE), 4) costAmt,\n" +
                "\tROUND((SUM(gssd.GSSD_AMT) + SUM(gssd.GSSD_ZK_AMT)), 2) ysAmt,\n" +
                "\tROUND(SUM(gssd.GSSD_AMT), 2) amt,\n" +
                "\tROUND(ROUND(SUM(gssd.GSSD_AMT), 2 ) - ROUND(SUM(gssd.GSSD_MOV_PRICE), 4), 2) grossProfitAmt,\n" +
                "\tROUND(SUM(gssd.GSSD_ZK_AMT), 2) zkAmt,\n" +
                "\tCOUNT(DISTINCT GSSD_KL_DATE_BR) klDates\n" +
                "FROM GAIA_SD_SALE_D gssd\n" +
                "INNER JOIN GAIA_STORE_DATA gsd ON gssd.CLIENT = gsd.CLIENT AND gssd.GSSD_BR_ID = gsd.STO_CODE \n" +
                "INNER JOIN GAIA_PRODUCT_BUSINESS gpb ON gpb.CLIENT = gssd.CLIENT AND gpb.PRO_SITE = gssd.GSSD_BR_ID AND gpb.PRO_SELF_CODE = gssd.GSSD_PRO_ID \n");

        sqlSb.append("WHERE gssd.CLIENT = '");
        sqlSb.append(storeCommissionSummaryDO.getClient()).append("'\n");

        if (!onlyWeedOutPro) {
            if ("0".equals(planIfNegative)) {
                sqlSb.append("AND gssd.GSSD_KL_NEGATIVE_MARGIN >= '0'\n");
            }
        }

        if (2 == summaryType) {
            // 员工提成汇总增加营业员筛选
            List<String> saleNames = storeCommissionSummaryDO.getSaleName();
            sqlSb.append("AND gssd.GSSD_SALER_ID !='' AND gssd.GSSD_SALER_ID IS NOT NULL\n");
            if (CollectionUtil.isNotEmpty(saleNames)) {
                sqlSb.append("AND gssd.GSSD_SALER_ID IN ")
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
        // 门店
        List<String> stoCodes = storeCommissionSummaryDO.getStoCodes();
        if (ObjectUtil.isNotEmpty(stoCodes)) {
            sqlSb.append("AND gssd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(stoCodes));
        }

        if (ObjectUtil.isNotEmpty(rejectClass)) {
            sqlSb.append("\nAND gpb.PRO_CLASS NOT IN ")
                    .append(CommonUtil.queryByBatch(rejectClass));  //  剔除的商品分类
        }
        if (ObjectUtil.isNotEmpty(rejectPro)) {
            sqlSb.append("\nAND gssd.GSSD_PRO_ID NOT IN ")
                    .append(CommonUtil.queryByBatch(rejectPro));  //  剔除的商品
        }

        sqlSb.append("GROUP BY gssd.CLIENT, gssd.GSSD_BR_ID,gssd.GSSD_BILL_NO,gssd.GSSD_PRO_ID,gssd.GSSD_DATE,gssd.GSSD_SERIAL");
        if (2 == summaryType) {
            // 员工提成汇总
            sqlSb.append(",gssd.GSSD_SALER_ID");
        }
        // 如果设置了剔除折扣率
//        if (StringUtils.isNotEmpty(planRejectDiscountRateSymbol) && StringUtils.isNotEmpty(planRejectDiscountRate)) {
//            String symbol;
//            switch (planRejectDiscountRateSymbol) {
//                case "=":
//                    symbol = "!=";
//                    break;
//                case ">":
//                    symbol = "<=";
//                    break;
//                case ">=":
//                    symbol = "<";
//                    break;
//                case "<":
//                    symbol = ">=";
//                    break;
//                case "<=":
//                    symbol = ">";
//                    break;
//                default:
//                    throw new IllegalStateException("Unexpected value: " + planRejectDiscountRateSymbol);
//            }
//            if (StringUtils.isNotBlank(symbol)) {
//                sqlSb.append("\nhaving ZKL").append(symbol).append(new BigDecimal(planRejectDiscountRate).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
//            }
//        }
        sqlSb.append("\nORDER BY gssd.GSSD_BR_ID");
        log.warn("销售提成 sql: {}", sqlSb.toString());
        return kylinJdbcTemplate.query(sqlSb.toString(), BeanPropertyRowMapper.newInstance(StoreCommissionSummary.class));
    }

}


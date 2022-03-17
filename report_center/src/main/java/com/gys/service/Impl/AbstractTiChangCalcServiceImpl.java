package com.gys.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Splitter;
import com.gys.common.data.*;
import com.gys.entity.data.MonthPushMoney.*;
import com.gys.entity.data.commissionplan.CommissionSummaryDetailDTO;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.mapper.*;
import com.gys.util.BigDecimalUtil;
import com.gys.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
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
public abstract class AbstractTiChangCalcServiceImpl {

    protected JdbcTemplate kylinJdbcTemplate;

    protected GaiaTichengPlanZMapper tichengPlanZMapper;

    protected UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper;

    public boolean commonPreDealWithCalcCommission(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                   StoreCommissionSummary commissionMainPlan,
                                                   List<StoreCommissionSummary> mainPlanSaleDatas,
                                                   List<StoreCommissionSummary> saleCommissionSummaryDetails,
                                                   String startDate,
                                                   String endDate,
                                                   String planId,
                                                   String nowDate,
                                                   LocalDate nowLocalDate) {
        String client = storeCommissionSummaryDO.getClient();

        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        // 是否员工提成汇总
        final boolean isSaleManSummary = 2 == summaryType;
        // 是否日期汇总
        final boolean isSaleDateSummary = 2 == storeCommissionSummaryDO.getDisplayGranularity();
        // 是否展示门店
        final boolean showStore = storeCommissionSummaryDO.getShowStore();

        boolean flag = false;
        String tempEndDate = endDate;
        // 如果查询时间大于等于当天
        if (Integer.parseInt(endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) >= Integer.parseInt(nowDate)) {
            // 把时间重置为昨天
            storeCommissionSummaryDO.setEndDate(nowLocalDate.minusDays(1).toString());
            tempEndDate = storeCommissionSummaryDO.getEndDate().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
            flag = true;
        }

        transferSearchDateRange(storeCommissionSummaryDO, startDate, tempEndDate, commissionMainPlan);

        String stoCode = commissionMainPlan.getStoCode();
        storeCommissionSummaryDO.setStoCodes(Splitter.on(CharUtil.COMMA).splitToList(stoCode));

        // 如果开始时间大于结束时间不查
        if (assertInTimeRange(storeCommissionSummaryDO.getStartDate(), storeCommissionSummaryDO.getEndDate(), nowDate)) {
            CommissionSummaryDetailDTO commissionSummaryDetailDTO = new CommissionSummaryDetailDTO();
            commissionSummaryDetailDTO.setClient(client);
            commissionSummaryDetailDTO.setStartDate(storeCommissionSummaryDO.getStartDate()
                    .replaceAll(StrUtil.DASHED, StrUtil.EMPTY));
            commissionSummaryDetailDTO.setEndDate(storeCommissionSummaryDO.getEndDate()
                    .replaceAll(StrUtil.DASHED, StrUtil.EMPTY));
            commissionSummaryDetailDTO.setStoCodes(storeCommissionSummaryDO.getStoCodes());
            commissionSummaryDetailDTO.setPlanId(planId);
            // 查询各个子方案的历史提成明细
            saleCommissionSummaryDetails.addAll(userCommissionSummaryDetailMapper
                    .selectCommissionDetailByConditionWithStoreCommissionSummary(commissionSummaryDetailDTO));
        }

        // 如果比较后的结束时间大于方案结束时间 flag置为 false
        if (Integer.parseInt(commissionMainPlan.getPlanEndDate()) < Integer.parseInt(nowDate)) {
            flag = false;
        }

        // 获取主方案的销售汇总， 根据加盟商，门店，营业员，销售时间过滤
        mainPlanSaleDatas.addAll(generateSqlAndResultOptimize(
                storeCommissionSummaryDO,
                isSaleManSummary,
                showStore,
                isSaleDateSummary));

        if (flag) {
            // 获取实时的主方案销售汇总
            mainPlanSaleDatas.addAll(doGetRealTimeMainPlanSummary(client, nowDate, isSaleManSummary,
                    showStore, isSaleDateSummary));
        }
        return flag;
    }

    /**
     * 获取实时的主方案销售汇总
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param isSaleManSummary         isSaleManSummary
     * @param showStore                showStore
     * @param isSaleDateSummary        isSaleDateSummary
     * @return List<StoreCommissionSummary>
     */
    protected List<StoreCommissionSummary> doGetRealTimeMainPlanSummary(String client,
                                                                        String nowDate,
                                                                        boolean isSaleManSummary,
                                                                        boolean showStore,
                                                                        boolean isSaleDateSummary) {
        List<StoreCommissionSummary> result = new ArrayList<>();
        result.addAll(tichengPlanZMapper.getRealTimeSaleSummary(client, nowDate, isSaleManSummary,
                showStore, isSaleDateSummary));
        return result;
    }

    /**
     * 判断是否在查询 kylin 数据的时间范围内
     *
     * @param tempStartDate tempStartDate
     * @param tempEndDate   tempEndDate
     * @param nowDate       nowDate
     * @return boolean
     */
    protected boolean assertInTimeRange(final String tempStartDate, final String tempEndDate, String nowDate) {
        return Integer.parseInt(tempStartDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) <= Integer.parseInt(tempEndDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY))
                && Integer.parseInt(tempEndDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) < Integer.parseInt(nowDate);
    }


    /**
     * 设置每个方法查询销售单时间范围
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param startDate                startDate
     * @param endDate                  endDate
     * @param productCommissionPlan    productCommissionPlan
     */
    private void transferSearchDateRange(StoreCommissionSummaryDO storeCommissionSummaryDO, String startDate,
                                         String endDate, StoreCommissionSummary productCommissionPlan) {
        storeCommissionSummaryDO.setStoCode(productCommissionPlan.getStoCode());
        String maxDate = com.gys.util.DateUtil.getMaxDate(startDate, productCommissionPlan.getPlanStartDate());
        storeCommissionSummaryDO.setStartDate(com.gys.util.DateUtil.dateConvert(maxDate));
        String minDate = com.gys.util.DateUtil.getMinDate(endDate, productCommissionPlan.getPlanEndDate());
        storeCommissionSummaryDO.setEndDate(com.gys.util.DateUtil.dateConvert(minDate));
    }

    /**
     * 生成提成分组的key
     *
     * @param storeCommissionSummary storeCommissionSummary
     * @param showSubPlan            是否显示子方案
     * @param showStore              是否显示门店
     * @param isSaleManSummary       是否是员工提成汇总
     * @param isSaleDateSummary      是否显示销售日期
     * @return String
     */
    private String generateKey(StoreCommissionSummary storeCommissionSummary,
                               boolean showSubPlan, boolean showStore,
                               boolean isSaleManSummary, boolean isSaleDateSummary) {
        String plan = storeCommissionSummary.getPlanId() + "";
        if (isSaleManSummary) {
            if (showSubPlan) {
                plan = storeCommissionSummary.getSubPlanId();
            }
            String stoCode = "";
            if (showStore) {
                stoCode = storeCommissionSummary.getStoCode();
            }
            // 员工提成汇总
            if (isSaleDateSummary) {
                return plan + "_" + storeCommissionSummary.getSaleDateStr() + "_" + storeCommissionSummary.getSaleManCode() + "_" + stoCode;
            } else {
                return plan + "_" + storeCommissionSummary.getSaleManCode() + "_" + stoCode;
            }
        } else {
            // 门店提成汇总
            if (showSubPlan) {
                // 开启子方案
                plan = storeCommissionSummary.getSubPlanId();
            }
            if (isSaleDateSummary) {
                return plan + "_" + storeCommissionSummary.getStoCode() + "_" + storeCommissionSummary.getSaleDateStr();
            } else {
                return plan + "_" + storeCommissionSummary.getStoCode();
            }
        }
    }

    /**
     * 处理最终结果集
     *
     * @param commissionSummaries      commissionSummaries
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param mainPlanSaleDatas        mainPlanSaleDatas
     * @param subPlanNameMap           subPlanNameMap
     * @param storeSimpleInfoMap       storeSimpleInfoMap
     * @param userNameMap              userNameMap
     * @param commissionMainPlan       commissionMainPlan
     * @param commissionType           commissionType
     * @return List<StoreCommissionSummary>
     */
    public List<StoreCommissionSummary> dealResult(List<StoreCommissionSummary> commissionSummaries,
                                                   StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                   List<StoreCommissionSummary> mainPlanSaleDatas,
                                                   Map<String, String> subPlanNameMap,
                                                   Map<String, String> storeSimpleInfoMap,
                                                   Map<String, String> userNameMap,
                                                   StoreCommissionSummary commissionMainPlan,
                                                   final Integer commissionType) {
        // 子方案 ， 门店， 日期， 营业员四个维度
        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        // 是否员工提成汇总
        final boolean isSaleManSummary = 2 == summaryType;
        // 是否日期汇总
        boolean isSaleDateSummary = 2 == storeCommissionSummaryDO.getDisplayGranularity();
        // 是否展示门店
        Boolean showStore = storeCommissionSummaryDO.getShowStore();
        // 是否展示子方案
        Boolean showSubPlan = storeCommissionSummaryDO.getShowSubPlan();

        Map<String, Boolean> costAmtShowConfigMap = storeCommissionSummaryDO.getCostAmtShowConfigMap();

        List<StoreCommissionSummary> storeCommissionSummariesResult = new ArrayList<>();

        Map<String, List<StoreCommissionSummary>> collect = commissionSummaries.stream()
                .collect(Collectors.groupingBy(item -> generateKey(item, showSubPlan, showStore, isSaleManSummary, isSaleDateSummary)));

        Integer planId = commissionMainPlan.getPlanId();
        String planName = commissionMainPlan.getPlanName();
        String planStartDate = commissionMainPlan.getPlanStartDate();
        String planEndDate = commissionMainPlan.getPlanEndDate();

        Map<String, List<StoreCommissionSummary>> mainPlanSaleDataMap = mainPlanSaleDatas.stream()
                .collect(Collectors.groupingBy(item -> generateKey(item, showSubPlan, showStore, isSaleManSummary, isSaleDateSummary)));

        Set<Map.Entry<String, List<StoreCommissionSummary>>> entries = mainPlanSaleDataMap.entrySet();

        // 根据门店，销售日期，子方案，营业员分组
        for (Map.Entry<String, List<StoreCommissionSummary>> entry : entries) {
            String key = entry.getKey();
            List<StoreCommissionSummary> value = entry.getValue();
            if (CollectionUtil.isEmpty(value)) {
                continue;
            }
            StoreCommissionSummary temp = value.get(0);

            StoreCommissionSummary commissionSummary = value.stream()
                    .reduce(new StoreCommissionSummary(), StoreCommissionSummary::summary, StoreCommissionSummary::summary);

            // 设置方案
            commissionSummary.setPlanId(planId);
            commissionSummary.setPlanName(planName);
            commissionSummary.setPlanStartDate(planStartDate);
            commissionSummary.setPlanEndDate(planEndDate);
            String stoCode = ObjectUtil.defaultIfNull(temp.getStoCode(), "");
            String saleDate = ObjectUtil.defaultIfNull(temp.getSaleDate(), "").toString().replaceAll("-"
                    , "");
            String saleManCode = ObjectUtil.defaultIfNull(temp.getSaleManCode(), "");

            commissionSummary.setStoCode(stoCode);
            commissionSummary.setSaleDateStr(saleDate);
            commissionSummary.setSaleManCode(saleManCode);

            // 提成汇总
            List<StoreCommissionSummary> commissionValue = collect.get(key);
            if (CollectionUtil.isEmpty(commissionValue)) {
                continue;
            }

            StoreCommissionSummary reduce = commissionValue.stream()
                    .reduce(new StoreCommissionSummary(), StoreCommissionSummary::summary, StoreCommissionSummary::summary);

            if (commissionType == 1) {
                commissionSummary.setPlanTypeName("销售提成");
            } else {
                commissionSummary.setPlanTypeName("单品提成");
            }
            commissionSummary.setPlanType(commissionType);

            if (showStore) {
                commissionSummary.setStoName(storeSimpleInfoMap.get(commissionSummary.getStoCode()));
            }

            if (isSaleManSummary) {
                commissionSummary.setSaleManName(userNameMap.get(saleManCode));
            }

            BigDecimal commissionGrossProfitAmt = reduce.getGrossProfitAmt();
            BigDecimal commissionAmt = BigDecimalUtil.toBigDecimal(reduce.getCommissionAmt());

            commissionSummary.setCommissionCostAmt(BigDecimalUtil.format(reduce.getCostAmt(), 4));
            commissionSummary.setCommissionSales(BigDecimalUtil.format(reduce.getAmt(), 2));
            commissionSummary.setCommissionGrossProfitAmt(BigDecimalUtil.format(commissionGrossProfitAmt, 4));
            commissionSummary.setCommissionAmt(BigDecimalUtil.format(commissionAmt, 4));
            // 提成商品毛利率 提成商品毛利额/提成商品实收金额
            commissionSummary.setCommissionGrossProfitRate(BigDecimalUtil.divide(commissionGrossProfitAmt, commissionSummary.getCommissionSales(), 4));
            // 提成销售比 提成金额/实收金额
            commissionSummary.setCommissionSalesRatio(BigDecimalUtil.divide(commissionAmt, reduce.getYsAmt(), 4));
            // 提成毛利比 提成金额/毛利额
            commissionSummary.setCommissionGrossProfitRatio(BigDecimalUtil.divide(commissionAmt, commissionGrossProfitAmt, 4));

            if (2 == commissionType) {
                commissionSummary.setSubPlanName(subPlanNameMap.get(commissionSummary.getSubPlanId()));
            } else {
                commissionSummary.setSubPlanName("默认");
            }

            BigDecimal amt = commissionSummary.getAmt();
            BigDecimal costAmt = commissionSummary.getCostAmt();
            BigDecimal ysAmt = commissionSummary.getYsAmt();
            BigDecimal grossProfitAmt = commissionSummary.getGrossProfitAmt();
            BigDecimal zkAmt = commissionSummary.getZkAmt();

            // 设置主方案的 成本额，应收金额，实收金额，折扣金额，毛利额
            commissionSummary.setCostAmt(BigDecimalUtil.format(costAmt, 2));
            commissionSummary.setYsAmt(BigDecimalUtil.format(ysAmt, 2));
            commissionSummary.setAmt(BigDecimalUtil.format(amt, 2));
            commissionSummary.setGrossProfitAmt(BigDecimalUtil.format(grossProfitAmt, 2));
            commissionSummary.setZkAmt(BigDecimalUtil.format(zkAmt, 2));
            // 折扣率
            commissionSummary.setZkl(BigDecimalUtil.divide(zkAmt, ysAmt, 4));
            // 毛利率
            commissionSummary.setGrossProfitRate(BigDecimalUtil.divide(grossProfitAmt, amt, 4));

            // 门店提成 或者 员工提成打开门店显示时判断
            if (MapUtil.isNotEmpty(costAmtShowConfigMap)) {
                if (costAmtShowConfigMap.getOrDefault(commissionSummary.getStoCode(), true)) {
                    // 不显示成本，毛利，毛利率
                    commissionSummary.setCostAmt(null);
                    commissionSummary.setGrossProfitAmt(null);
                    commissionSummary.setGrossProfitRate(null);
                    commissionSummary.setNotShowAmt(true);
                } else {
                    commissionSummary.setNotShowAmt(false);
                }
            }
            storeCommissionSummariesResult.add(commissionSummary);
        }

//        StoreCommissionSummary summary = mainPlanSaleDatas.get(0);
//
//        BigDecimal amt = summary.getAmt();
//        BigDecimal costAmt = summary.getCostAmt();
//        BigDecimal ysAmt = summary.getYsAmt();
//        BigDecimal grossProfitAmt = summary.getGrossProfitAmt();
//        BigDecimal zkAmt = summary.getZkAmt();
//        // 最终结果集
//        for (StoreCommissionSummary commissionSummary : storeCommissionSummaries) {
//            // 设置方案
//            commissionSummary.setPlanId(planId);
//            commissionSummary.setPlanName(planName);
//
//            if (2 == commissionType) {
//                commissionSummary.setSubPlanName(subPlanNameMap.get(commissionSummary.getSubPlanId()));
//            } else {
//                commissionSummary.setSubPlanName("默认");
//            }
//
//            // 设置主方案的 成本额，应收金额，实收金额，折扣金额，毛利额
//            commissionSummary.setCostAmt(BigDecimalUtil.format(costAmt, 2));
//            commissionSummary.setYsAmt(BigDecimalUtil.format(ysAmt, 2));
//            commissionSummary.setAmt(BigDecimalUtil.format(amt, 2));
//            commissionSummary.setGrossProfitAmt(BigDecimalUtil.format(grossProfitAmt, 2));
//            commissionSummary.setZkAmt(BigDecimalUtil.format(zkAmt, 2));
//            // 折扣率
//            commissionSummary.setZkl(BigDecimalUtil.divide(zkAmt, ysAmt, 4));
//            // 毛利率
//            commissionSummary.setGrossProfitRate(BigDecimalUtil.divide(grossProfitAmt, amt, 4));
//
//            // 销售天数
//            commissionSummary.setSaleDays(1);
//
//            // 门店提成 或者 员工提成打开门店显示时判断
//            if (MapUtil.isNotEmpty(costAmtShowConfigMap)) {
//                if (costAmtShowConfigMap.getOrDefault(commissionSummary.getStoCode(), true)) {
//                    // 不显示成本，毛利，毛利率
//                    commissionSummary.setCostAmt(null);
//                    commissionSummary.setGrossProfitAmt(null);
//                    commissionSummary.setGrossProfitRate(null);
//                    commissionSummary.setNotShowAmt(true);
//                } else {
//                    commissionSummary.setNotShowAmt(false);
//                }
//            }
//        }
        return storeCommissionSummariesResult;
    }

    public Map<String, String> getUserNameMap(String client, boolean isSaleManSummary) {
        Map<String, String> userNameMap = null;
        // 获取营业员姓名
        if (isSaleManSummary) {
            List<Map<String, String>> maps = userCommissionSummaryDetailMapper.selectUserName(client);
            userNameMap = new HashMap<>();
            for (Map<String, String> map : maps) {
                userNameMap.put(map.get("userId"), map.get("userName"));
            }
        }
        return userNameMap;
    }

    public StoreCommissionSummary preSetAttr(EmpSaleDetailResVo detailResVo, Map<String, String> storeSimpleInfoMap,
                                             String planId, String settingId) {
        StoreCommissionSummary temp = new StoreCommissionSummary();
        temp.setSaleManCode(detailResVo.getSalerId());
        temp.setSaleDateStr(com.gys.util.DateUtil.dateConvert(detailResVo.getSaleDate()));
        temp.setProId(detailResVo.getProSelfCode());
        temp.setCostAmt(BigDecimalUtil.toBigDecimal(detailResVo.getCostAmt()));
        temp.setYsAmt(BigDecimalUtil.toBigDecimal(detailResVo.getYsAmt()));
        temp.setAmt(BigDecimalUtil.toBigDecimal(detailResVo.getAmt()));
        temp.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(detailResVo.getGrossProfitAmt()));
        temp.setZkAmt(BigDecimalUtil.toBigDecimal(detailResVo.getZkAmt()));
        temp.setQyt(detailResVo.getQyt());
        temp.setGrossProfitRate(BigDecimalUtil.toBigDecimal(detailResVo.getGrossProfitRate()));
        temp.setZkl(BigDecimalUtil.toBigDecimal(detailResVo.getZkl()));
        temp.setStoCode(detailResVo.getStoCode());
        temp.setPlanId(Integer.parseInt(planId));
        temp.setSubPlanId(settingId);
        return temp;
    }

    public List<EmpSaleDetailResVo> doGetRealTime(EmpSaleDetailInData inData, boolean isSaleManSummary,
                                                  List<String> stoCodes, int planId, List<String> saleNames,
                                                  String nowDate) {
        inData.setPlanId(planId);
        inData.setToday(nowDate);
        inData.setStoArr(stoCodes);
        if (isSaleManSummary) {
            inData.setNameSearchType("1");
            inData.setNameSearchIdList(saleNames);
        }
        return tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
    }

    public List<StoreCommissionSummary> generateSqlAndResultOptimize(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                                     final boolean isSaleManSummary,
                                                                     final boolean showStore,
                                                                     final boolean isSaleDateSummary) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("\nSELECT\n" +
                "\tgssd.CLIENT client,\n");
        if (isSaleManSummary) {
            // 员工提成汇总
            sqlSb.append("\tgssd.GSSD_SALER_ID saleManCode,\n");
        }
        if (isSaleDateSummary) {
            // 展示日期
            sqlSb.append("\tgssd.GSSD_DATE saleDate,\n");
        }
        if (showStore) {
            // 展示门店
            sqlSb.append("\tgssd.GSSD_BR_ID stoCode,\n");
        }

        sqlSb.append(
                "\tCOUNT(DISTINCT gssd.GSSD_DATE) saleDays,\n" +
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

        sqlSb.append("GROUP BY gssd.CLIENT");
        if (storeCommissionSummaryDO.getShowStore()) {
            sqlSb.append(",gssd.GSSD_BR_ID");
        }
        if (isSaleDateSummary) {
            sqlSb.append(",gssd.GSSD_DATE");
        }
        if (isSaleManSummary) {
            // 员工提成汇总
            sqlSb.append(",gssd.GSSD_SALER_ID");
        }
        log.warn("提成 sql: {}", sqlSb.toString());
        return kylinJdbcTemplate.query(sqlSb.toString(), BeanPropertyRowMapper.newInstance(StoreCommissionSummary.class));
    }


}
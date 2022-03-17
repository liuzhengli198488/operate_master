package com.gys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.data.EmpSaleDetailResVo;
import com.gys.common.data.PageInfo;
import com.gys.entity.GaiaProductBusiness;
import com.gys.entity.GaiaProductClass;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.MonthPushMoney.PushMoneyByStoreV5InData;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.entity.data.salesSummary.UserCommissionSummaryDetail;
import com.gys.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年11月17日 下午7:18
 */
public interface TiChengCaculateService {

    @Deprecated
    PageInfo caculatePlan(PushMoneyByStoreV5InData inData);

    /**
     * 方案提成汇总查询优化
     *
     * @param inData inData
     * @return PageInfo
     */
    PageInfo caculatePlanOptimize(PushMoneyByStoreV5InData inData);

    /**
     * 计算门店提成汇总
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return List<StoreCommissionSummary>
     */
    List<StoreCommissionSummary> calcStoreCommissionSummary(StoreCommissionSummaryDO storeCommissionSummaryDO);

    /**
     * 计算门店提成汇总优化
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return List<StoreCommissionSummary>
     */
    List<StoreCommissionSummary> calcStoreCommissionSummaryOptimize(StoreCommissionSummaryDO storeCommissionSummaryDO);

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
    default String generateKey(StoreCommissionSummary storeCommissionSummary,
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
                return plan + "_" + storeCommissionSummary.getSaleDate() + "_" + storeCommissionSummary.getSaleManCode() + "_" + stoCode;
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
                return plan + "_" + storeCommissionSummary.getStoCode() + "_" + storeCommissionSummary.getSaleDate();
            } else {
                return plan + "_" + storeCommissionSummary.getStoCode();
            }
        }
    }

    /**
     * 根据方案，门店，日期等 条件分组
     *
     * @param tempProductCommissionSummaries 单个商品汇总统计
     * @param showSubPlan                    是否显示子方案
     * @param showStore                      是否显示门店
     * @param isSaleManSummary               是否按营业员分组
     * @param isSaleDateSummary              是否按日期分组
     * @param totalSaleDays                  totalSaleDays
     * @param commissionType                 提成类型 1：销售提成 2：单品提成
     * @param subPlanNum                     subPlanNum
     * @param costAmtShowConfigMap           costAmtShowConfigMap
     * @return List<StoreCommissionSummary>
     */
    default List<StoreCommissionSummary> groupingByStore(List<StoreCommissionSummary> tempProductCommissionSummaries,
                                                         boolean showSubPlan, boolean showStore,
                                                         final boolean isSaleManSummary, final boolean isSaleDateSummary,
                                                         final Set<String> totalSaleDays, final Integer commissionType,
                                                         Map<String, HashSet<String>> subPlanNum,
                                                         Map<String, Boolean> costAmtShowConfigMap) {
        // 分组
        Map<String, List<StoreCommissionSummary>> collect = tempProductCommissionSummaries.stream()
                .collect(Collectors.groupingBy(item -> generateKey(item, showSubPlan, showStore, isSaleManSummary, isSaleDateSummary)));

        List<StoreCommissionSummary> storeCommissionSummariesResult = new ArrayList<>();
        for (Map.Entry<String, List<StoreCommissionSummary>> entry : collect.entrySet()) {
            List<StoreCommissionSummary> value = entry.getValue();
            StoreCommissionSummary storeCommissionSummary = value.get(0);
            // 汇总
            StoreCommissionSummary reduce = value.stream()
                    .reduce(new StoreCommissionSummary(), StoreCommissionSummary::summary, StoreCommissionSummary::summary);
            if (isSaleDateSummary) {
                storeCommissionSummary.setSaleDays(1);
            } else {
                storeCommissionSummary.setSaleDays((int) value.stream().map(StoreCommissionSummary::getSaleDate).distinct().count());
            }

            if (commissionType == 1) {
                storeCommissionSummary.setPlanTypeName("销售提成");
            } else {
                storeCommissionSummary.setPlanTypeName("单品提成");
            }
            storeCommissionSummary.setPlanType(commissionType);

            BigDecimal amt = BigDecimalUtil.toBigDecimal(reduce.getAmt());
            BigDecimal costAmt = BigDecimalUtil.toBigDecimal(reduce.getCostAmt());
            BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(reduce.getYsAmt());
            BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(reduce.getZkAmt());
            BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(reduce.getGrossProfitAmt());
            storeCommissionSummary.setCostAmt(BigDecimalUtil.format(costAmt, 2));
            storeCommissionSummary.setYsAmt(BigDecimalUtil.format(ysAmt, 2));
            storeCommissionSummary.setAmt(BigDecimalUtil.format(amt, 2));
            storeCommissionSummary.setGrossProfitAmt(BigDecimalUtil.format(grossProfitAmt, 2));
            storeCommissionSummary.setZkAmt(BigDecimalUtil.format(zkAmt, 2));

            if (!showSubPlan && subPlanNum != null) {
                HashSet<String> strings = subPlanNum.get(String.valueOf(storeCommissionSummary.getPlanId()));
                if (CollectionUtil.isNotEmpty(strings)) {
                    int size = strings.size();
                    storeCommissionSummary.setCostAmt(BigDecimalUtil.divide(costAmt, size, 2));
                    storeCommissionSummary.setYsAmt(BigDecimalUtil.divide(ysAmt, size, 2));
                    storeCommissionSummary.setAmt(BigDecimalUtil.divide(amt, size, 2));
                    storeCommissionSummary.setGrossProfitAmt(BigDecimalUtil.divide(grossProfitAmt, size, 2));
                    storeCommissionSummary.setZkAmt(BigDecimalUtil.divide(zkAmt, size, 2));
                }
            }

            BigDecimal commissionAmt = BigDecimalUtil.toBigDecimal(reduce.getCommissionAmt());
            // 折扣率
            storeCommissionSummary.setZkl(BigDecimalUtil.divide(zkAmt, ysAmt, 4));
            // 毛利率
            storeCommissionSummary.setGrossProfitRate(BigDecimalUtil.divide(grossProfitAmt, amt, 4));
            storeCommissionSummary.setCommissionCostAmt(BigDecimalUtil.format(reduce.getCommissionCostAmt(), 4));
            storeCommissionSummary.setCommissionSales(BigDecimalUtil.format(reduce.getCommissionSales(), 2));
            storeCommissionSummary.setCommissionGrossProfitAmt(BigDecimalUtil.format(reduce.getCommissionGrossProfitAmt(), 4));
            storeCommissionSummary.setCommissionAmt(BigDecimalUtil.format(commissionAmt, 4));
            // 提成商品毛利率 提成商品毛利额/提成商品实收金额
            storeCommissionSummary.setCommissionGrossProfitRate(BigDecimalUtil.divide(reduce.getCommissionGrossProfitAmt(), reduce.getCommissionSales(), 4));
            // 提成销售比 提成金额/实收金额
            storeCommissionSummary.setCommissionSalesRatio(BigDecimalUtil.divide(commissionAmt, amt, 4));
            // 提成毛利比 提成金额/毛利额
            storeCommissionSummary.setCommissionGrossProfitRatio(BigDecimalUtil.divide(commissionAmt, grossProfitAmt, 4));
            storeCommissionSummary.setTotalSaleDays(totalSaleDays);

            // 门店提成 或者 员工提成打开门店显示时判断
            if (MapUtil.isNotEmpty(costAmtShowConfigMap)) {
                if (costAmtShowConfigMap.getOrDefault(storeCommissionSummary.getStoCode(), true)) {
                    // 不显示成本，毛利，毛利率
                    storeCommissionSummary.setCostAmt(null);
                    storeCommissionSummary.setGrossProfitAmt(null);
                    storeCommissionSummary.setGrossProfitRate(null);
                    storeCommissionSummary.setNotShowAmt(true);
                } else {
                    storeCommissionSummary.setNotShowAmt(false);
                }
            }
            storeCommissionSummariesResult.add(storeCommissionSummary);
        }
        return storeCommissionSummariesResult;
    }


    /**
     * 设置每个方法查询销售单时间范围
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param startDate                startDate
     * @param endDate                  endDate
     * @param productCommissionPlan    productCommissionPlan
     */
    default void transferSearchDateRange(StoreCommissionSummaryDO storeCommissionSummaryDO, String startDate,
                                         String endDate, StoreCommissionSummary productCommissionPlan) {
        storeCommissionSummaryDO.setStoCode(productCommissionPlan.getStoCode());
        String maxDate = com.gys.util.DateUtil.getMaxDate(startDate, productCommissionPlan.getPlanStartDate());
        storeCommissionSummaryDO.setStartDate(com.gys.util.DateUtil.dateConvert(maxDate));
        String minDate = com.gys.util.DateUtil.getMinDate(endDate, productCommissionPlan.getPlanEndDate());
        storeCommissionSummaryDO.setEndDate(com.gys.util.DateUtil.dateConvert(minDate));
    }

    /**
     * 获取当天提成数据
     *
     * @param saleCommissionPlan             saleCommissionPlan
     * @param dbList                         dbList
     * @param userNameMap                    userNameMap
     * @param storeSimpleInfoMap             storeSimpleInfoMap
     * @param tempProductCommissionSummaries tempProductCommissionSummaries
     */
    default void renderCommissionDataWithToday(StoreCommissionSummary saleCommissionPlan, List<EmpSaleDetailResVo> dbList,
                                               Map<String, String> userNameMap, Map<String, String> storeSimpleInfoMap,
                                               List<StoreCommissionSummary> tempProductCommissionSummaries) {
        if (CollectionUtil.isNotEmpty(dbList)) {
            for (EmpSaleDetailResVo detailResVo : dbList) {
                StoreCommissionSummary temp = new StoreCommissionSummary();
                BeanUtil.copyProperties(saleCommissionPlan, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                temp.setRealTime(true);
                temp.setSaleManCode(detailResVo.getSalerId());
                temp.setSaleDate(LocalDate.parse(com.gys.util.DateUtil.dateConvert(detailResVo.getSaleDate())));
                temp.setProId(detailResVo.getProSelfCode());
                temp.setCostAmt(BigDecimalUtil.toBigDecimal(detailResVo.getCostAmt()));
                temp.setYsAmt(BigDecimalUtil.toBigDecimal(detailResVo.getYsAmt()));
                temp.setAmt(BigDecimalUtil.toBigDecimal(detailResVo.getAmt()));
                temp.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(detailResVo.getGrossProfitAmt()));
                temp.setZkAmt(BigDecimalUtil.toBigDecimal(detailResVo.getZkAmt()));
                temp.setQyt(detailResVo.getQyt());
                temp.setGrossProfitRate(BigDecimalUtil.toBigDecimal(detailResVo.getGrossProfitRate()));
                temp.setZkl(BigDecimalUtil.toBigDecimal(detailResVo.getZkl()));
                if (MapUtil.isNotEmpty(userNameMap)) {
                    temp.setSaleManName(userNameMap.get(temp.getSaleManCode()));
                }
                temp.setStoCode(detailResVo.getStoCode());
                temp.setStoName(storeSimpleInfoMap.get(temp.getStoCode()));
                tempProductCommissionSummaries.add(temp);
            }
        }
    }

    /**
     * 计算员工提成明细
     *
     * @return
     */
    PageInfo empSaleDetailList(EmpSaleDetailInData inData,
                               List<EmpSaleDetailResVo> empSaleDetailResVoList,
                               Map<String, GaiaProductBusiness> productBusinessInfoMap,
                               Map<String, Object> userIdNameMap,
                               Map<String, GaiaProductClass> productClassMap,
                               Map<String, Boolean> costAmtShowConfigMap);


    /**
     * 计算员工提成明细优化
     *
     * @param inData                       inData
     * @param userCommissionSummaryDetails userCommissionSummaryDetails
     * @param empSaleDetailResVoList       empSaleDetailResVoList
     * @param productBusinessInfoMap       productBusinessInfoMap
     * @param userIdNameMap                userIdNameMap
     * @param productClassMap              productClassMap
     * @param costAmtShowConfigMap         costAmtShowConfigMap
     * @return PageInfo
     */
    PageInfo empSaleDetailListOptimize(EmpSaleDetailInData inData,
                                       List<EmpSaleDetailResVo> userCommissionSummaryDetails,
                                       List<EmpSaleDetailResVo> empSaleDetailResVoList,
                                       Map<String, GaiaProductBusiness> productBusinessInfoMap,
                                       Map<String, Object> userIdNameMap,
                                       Map<String, GaiaProductClass> productClassMap,
                                       Map<String, Boolean> costAmtShowConfigMap);


    /**
     * 验证是否显示成本 毛利 毛利率
     *
     * @param out                  out
     * @param costAmtShowConfigMap costAmtShowConfigMap
     * @param stoCode              stoCode
     */
    default void checkIsShowCostAmt(EmpSaleDetailResVo out, Map<String, Boolean> costAmtShowConfigMap, String stoCode) {
        if (MapUtil.isNotEmpty(costAmtShowConfigMap)) {
            if (costAmtShowConfigMap.getOrDefault(stoCode, true)) {
                out.setNotShowAmt(true);
                // 不显示成本 毛利 毛利率
                out.setCostAmt(null);
                out.setGrossProfitAmt(null);
                out.setGrossProfitRate(null);
            } else {
                out.setNotShowAmt(false);
            }
        }
    }

}


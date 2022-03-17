package com.gys.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.data.*;
import com.gys.entity.TichengProplanProN;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.entity.data.commissionplan.app.AppCommissionSummary;
import com.gys.util.BigDecimalUtil;
import com.gys.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wu mao yin
 * @Description: app个人销售报表提成计算
 * @date 2022/1/17 10:20
 */
public interface PersonalSalesReportCommissionCalc {

    /**
     * 获取当天提成数据
     *
     * @param saleCommissionPlan      saleCommissionPlan
     * @param dbList                  dbList
     * @param tempCommissionSummaries tempCommissionSummaries
     */
    default void renderCommissionDataWithToday(StoreCommissionSummary saleCommissionPlan, List<EmpSaleDetailResVo> dbList,
                                               List<AppCommissionSummary> tempCommissionSummaries) {
        if (CollectionUtil.isNotEmpty(dbList)) {
            for (EmpSaleDetailResVo detailResVo : dbList) {
                AppCommissionSummary temp = new AppCommissionSummary();
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
                temp.setBillNo(detailResVo.getGssdBillNo());
                temp.setStoCode(detailResVo.getStoCode());
                tempCommissionSummaries.add(temp);
            }
        }
    }

    /**
     * 比较剔除折扣率范围
     *
     * @param left   left
     * @param right  right
     * @param symbol symbol
     * @return boolean
     */
    default boolean calcSymbol(BigDecimal left, BigDecimal right, String symbol) {
        left = left.multiply(BigDecimalUtil.ONE_HUNDRED);
        boolean resFlag = false;
        switch (symbol) {
            case "=":
                if (left.compareTo(right) == 0) {
                    resFlag = true;
                }
                break;
            case "<=":
                if (left.compareTo(right) <= 0) {
                    resFlag = true;
                }
                break;
            case "<":
                if (left.compareTo(right) < 0) {
                    resFlag = true;
                }
                break;
            case ">=":
                if (left.compareTo(right) >= 0) {
                    resFlag = true;
                }
                break;
            case ">":
                if (left.compareTo(right) > 0) {
                    resFlag = true;
                }
                break;
            default:
                break;
        }
        return resFlag;
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
    default BigDecimal calcCommissionTotal(TichengProplanProN tichengProplanProN, BigDecimal qyt, BigDecimal
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
     * 根据方案，门店，日期等 条件分组
     *
     * @param tempProductCommissionSummaries 单个商品汇总统计
     * @param subPlanNum                     subPlanNum
     * @return List<StoreCommissionSummary>
     */
    default Map<String, AppCommissionSummary> groupingByStore(List<AppCommissionSummary> tempProductCommissionSummaries,
                                                              Map<String, HashSet<String>> subPlanNum) {
        // 分组
        Map<String, List<AppCommissionSummary>> collect = tempProductCommissionSummaries.stream()
                .collect(Collectors.groupingBy(AppCommissionSummary::getTimeType));
        Map<String, AppCommissionSummary> res = new HashMap<>();
        for (Map.Entry<String, List<AppCommissionSummary>> entry : collect.entrySet()) {
            List<AppCommissionSummary> value = entry.getValue();
            AppCommissionSummary appCommissionSummary = value.get(0);
            // 汇总
            StoreCommissionSummary reduce = value.stream()
                    .reduce(new AppCommissionSummary(), StoreCommissionSummary::summary, StoreCommissionSummary::summary);
            BigDecimal commissionAmt = BigDecimalUtil.toBigDecimal(reduce.getCommissionAmt());

            BigDecimal amt = BigDecimalUtil.toBigDecimal(reduce.getAmt());
            appCommissionSummary.setAmt(amt);

            if (subPlanNum != null) {
                HashSet<String> strings = subPlanNum.get(String.valueOf(appCommissionSummary.getPlanId()));
                if (CollectionUtil.isNotEmpty(strings)) {
                    int size = strings.size();
                    appCommissionSummary.setAmt(BigDecimalUtil.divide(amt, size, 2));
                }
            }

            // 交易次数 去重
            long payCount = value.stream().map(AppCommissionSummary::getBillNo)
                    .filter(Objects::nonNull).distinct().count();
            appCommissionSummary.setPayCount((int) payCount);
            appCommissionSummary.setCustomerPrice(BigDecimalUtil.divide(amt, payCount, 2));
            // 提成
            appCommissionSummary.setCommissionAmt(BigDecimalUtil.format(commissionAmt, 4));
            res.put(entry.getKey(), appCommissionSummary);
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
    default BigDecimal handleProTiAmount(TichengProplanProN proN, BigDecimal baseAmount, BigDecimal qyc) {
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

    default ProLevelTiAmountRes handleProLevelTiAmount(BigDecimal qycSet, BigDecimal tichengAmtSet, BigDecimal tichengRateSet, BigDecimal baseAmount, BigDecimal qyc) {
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
    default Integer handleProLevelNextRemainder(BigDecimal qycSet, BigDecimal qyc) {
        Integer res = 0;
        //没有设置的情况下或者实际小于设置的情况下，直接返回实际销售数量
        if (qycSet == null || (qycSet != null && qyc.compareTo(qycSet) < 0)) {
            return qyc.intValue();
        }
        res = qyc.intValue() % qycSet.intValue();
        return res;
    }

    default Integer getCaculateLev(TichengProplanProN proN, BigDecimal qyc) {
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


    default boolean getCaculateMode(TichengProplanProN proN) {
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

    /**
     * 拼接单品提成sql
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return String
     */
    default String generateProCommissionSqlAndResult(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("\nSELECT\n" +
                "\tgssd.CLIENT client,\n" +
                "\tgssd.GSSD_BR_ID stoCode,\n" +
                "\tgssd.GSSD_SERIAL,\n" +
                "\tgssd.GSSD_DATE saleDate,\n" +
                "\tgssd.GSSD_PRO_ID proId,\n" +
                "\tgssd.GSSD_BILL_NO gssdBillNo,\n" +
                "\tSUM(gssd.GSSD_QTY) qyt,\n" +
                "\tROUND(SUM(gssd.GSSD_MOV_PRICE), 4 ) costAmt,\n" +
                "\tROUND((SUM(gssd.GSSD_AMT) + SUM(gssd.GSSD_ZK_AMT)), 2) ysAmt,\n" +
                "\tROUND(SUM(gssd.GSSD_AMT), 2) amt,\n" +
                "\tROUND(ROUND(SUM(gssd.GSSD_AMT), 2) - ROUND(SUM(gssd.GSSD_MOV_PRICE), 4), 2) grossProfitAmt,\n" +
                "\tROUND(SUM(gssd.GSSD_ZK_AMT), 2) zkAmt\n");
        sqlSb.append("FROM GAIA_SD_SALE_D gssd \n" +
                "WHERE gssd.CLIENT = '");
        sqlSb.append(storeCommissionSummaryDO.getClient()).append("'\n");
        sqlSb.append("AND gssd.GSSD_SALER_ID = '");
        sqlSb.append(storeCommissionSummaryDO.getUserId()).append("'\n");
        sqlSb.append("AND gssd.GSSD_DATE >= '").append(storeCommissionSummaryDO.getStartDate()).append("'\n");
        sqlSb.append("AND gssd.GSSD_DATE <= '").append(storeCommissionSummaryDO.getEndDate()).append("'\n");
        sqlSb.append("AND gssd.GSSD_BR_ID IN ").append(CommonUtil.queryByBatch(storeCommissionSummaryDO.getStoCodes())).append("\n");
        sqlSb.append("GROUP BY gssd.CLIENT,gssd.GSSD_BR_ID,gssd.GSSD_BILL_NO,gssd.GSSD_PRO_ID,gssd.GSSD_DATE,gssd.GSSD_SERIAL");
        System.out.println("单品提成 sql: " + sqlSb.toString());
        return sqlSb.toString();
    }

    /**
     * 销售提成汇总sql
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param planIfNegative           负毛利率商品是否不参与销售提成 0 是 1 否
     * @return List<StoreCommissionSummary>
     */
    default String generateSaleCommissionSqlAndResult(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                      String planIfNegative) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("\nSELECT\n" +
                "\tgssd.CLIENT client,\n" +
                "\tgssd.GSSD_BR_ID stoCode,\n" +
                "\tgssd.GSSD_DATE saleDate,\n" +
                "\tgssd.GSSD_SERIAL,\n" +
                "\tMAX(gpb.PRO_CLASS) proClassCode,\n" +
                "\tgssd.GSSD_PRO_ID proId,\n");
        sqlSb.append("ROUND((case when (sum( GSSD_AMT ) + sum( GSSD_ZK_AMT ))=0 then 0 else sum( GSSD_ZK_AMT )/(sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) end), 2) as zkl,\n ");
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
        if ("0".equals(planIfNegative)) {
            sqlSb.append("AND gssd.GSSD_KL_NEGATIVE_MARGIN >= '0'\n");
        }
        sqlSb.append("AND gssd.GSSD_SALER_ID ='").append(storeCommissionSummaryDO.getUserId()).append("'\n");
        sqlSb.append("AND gssd.GSSD_DATE >= '").append(storeCommissionSummaryDO.getStartDate()).append("'\n");
        sqlSb.append("AND gssd.GSSD_DATE <= '").append(storeCommissionSummaryDO.getEndDate()).append("'\n");
        sqlSb.append("AND gssd.GSSD_BR_ID IN ").append(CommonUtil.queryByBatch(storeCommissionSummaryDO.getStoCodes()));
        sqlSb.append("GROUP BY gssd.CLIENT, gssd.GSSD_BR_ID,gssd.GSSD_BILL_NO,gssd.GSSD_PRO_ID,gssd.GSSD_DATE,gssd.GSSD_SERIAL");
        sqlSb.append("\nORDER BY gssd.GSSD_BR_ID");
        System.out.println("销售提成 sql: " + sqlSb.toString());
        return sqlSb.toString();
    }

    default boolean filterNotInRange(String planRejectDiscountRateSymbol, String planRejectDiscountRate, BigDecimal zkl) {
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
     * 计算折扣率
     *
     * @param ysAmt 应收金额
     * @param zkAmt 折扣金额
     * @return 折扣率
     */
    default BigDecimal calcDiscountRate(BigDecimal ysAmt, BigDecimal zkAmt) {
        if (ysAmt.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return BigDecimalUtil.divide(zkAmt, ysAmt, 4);
        }
    }

    /**
     * 计算毛利率
     *
     * @param storeCommissionSummary storeCommissionSummary
     * @return BigDecimal
     */
    default BigDecimal calcGrossProfitRate(StoreCommissionSummary storeCommissionSummary) {
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
     * 判断是否在查询 kylin 数据的时间范围内
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param nowDate                  nowDate
     * @return boolean
     */
    default boolean assertInTimeRange(StoreCommissionSummaryDO storeCommissionSummaryDO, String nowDate) {
        final String tempStartDate = storeCommissionSummaryDO.getStartDate();
        final String tempEndDate = storeCommissionSummaryDO.getEndDate();
        return Integer.parseInt(tempStartDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) <= Integer.parseInt(tempEndDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY))
                && Integer.parseInt(tempEndDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY)) < Integer.parseInt(nowDate);
    }

    /**
     * 比较时间范围
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

}

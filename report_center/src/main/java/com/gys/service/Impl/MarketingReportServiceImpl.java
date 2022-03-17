package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gys.common.data.JsonResult;
import com.gys.common.data.SalesRankOutData;
import com.gys.common.exception.BusinessException;
import com.gys.common.kylin.RowMapper;
import com.gys.entity.data.marketing.MarketingInDate;
import com.gys.entity.data.marketing.MarketingReportListOutData;
import com.gys.entity.data.marketing.MarketingReportOutData;
import com.gys.entity.data.marketing.StartAndEndDayInData;
import com.gys.mapper.AppReportMapper;
import com.gys.mapper.MarketingReportMapper;
import com.gys.service.MarketingReportService;
import com.gys.util.CalendarUtil;
import com.gys.util.CommonUtil;
import com.gys.util.DateUtil;
import com.gys.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


@Service
@Slf4j
public class MarketingReportServiceImpl implements MarketingReportService {

    @Autowired
    private MarketingReportMapper marketingReportMapper;
    @Autowired
    private AppReportMapper appReportMapper;
    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;

    @Override
    public JsonResult getMonthReport(StartAndEndDayInData inData) {
        //获取年
        Integer year = inData.getYear();
        //获取月
        Integer month = inData.getMonth();
        if(ObjectUtil.isEmpty(year) || ObjectUtil.isEmpty(month)) {
            throw new BusinessException("提示：查询年月不能为空");
        }
        Map<String, String> dateTimeMap = appReportMapper.selectDateTime(year, month, null);
        if(CollUtil.isEmpty(dateTimeMap)) {
            throw new BusinessException("提示：查询日期有误,请重试");
        }
        String queryDate = CommonUtil.parseWebDate(dateTimeMap.get("maxDate"));
        inData.setQueryDate(queryDate);

        //查询 本月活动次数  本月推送次数  本月开展次数
        MarketingReportOutData monthReport = new MarketingReportOutData();
        monthReport.setTotalCount(marketingReportMapper.getTotalCount(inData));
        monthReport.setPushCount(marketingReportMapper.getPushCount(inData));
        monthReport.setDevCount(marketingReportMapper.getDevCount(inData));
        monthReport.setStoreCount(marketingReportMapper.getStoreCount(inData));
        monthReport.setSaleDayCount(marketingReportMapper.getSaleDayCount(inData));

        //获取本月最后一天日期
        boolean lastDayOfMonthFlag = CalendarUtil.isLastDay(queryDate);
        //判断当前日期是否为本月最后一天  如果是最后一天，则按整月来查询
        if(lastDayOfMonthFlag) {
            //获取上月最后一天日期
            String lastMonthLastDay = CalendarUtil.getLastMonthLastDay(queryDate);
            //获取上年同月最后一天日期
            String lastYearThisMonthLastDay = CalendarUtil.getLastYearThisMonthLastDay(queryDate);
            inData.setLastMonthLastDay(lastMonthLastDay);
            inData.setLastYearThisMonthLastDay(lastYearThisMonthLastDay);
        } else {
            //如果不是最后一天，上个月则查询相同天数，上年本月也查询相同天数
            //获取上月本日  注意：如果没有上月没有本日的话，会返回上月最大天数
            String lastMonthThisDay = CalendarUtil.getLastMonthThisDay(queryDate);
            //获取上年本月本日的日期
            String lastYearThisMonthThisDay = CalendarUtil.getLastYearThisMonthThisDay(queryDate);
            inData.setLastMonthLastDay(lastMonthThisDay);
            inData.setLastYearThisMonthLastDay(lastYearThisMonthThisDay);
        }
        List<MarketingReportListOutData> saleAmountList = marketingReportMapper.getSaleAmountList(inData);
        //如果集合中有null元素，则移除集合中所有为null的元素
        if(saleAmountList.contains(null)) {
            saleAmountList.removeAll(Collections.singleton(null));
        }

        if(CollUtil.isNotEmpty(saleAmountList)) {
            for(MarketingReportListOutData marketingReport : saleAmountList) {
                MarketingReportListOutData saleAmount = marketingReportMapper.getSaleAmount(inData.getClientId(), inData.getBrId(),
                        marketingReport.getActivityBeginDate(), marketingReport.getActivityEndDate());
                if(ObjectUtil.isNotEmpty(saleAmount)) {
                    marketingReport.setThisMonthAmt(saleAmount.getThisMonthAmt());
                    marketingReport.setLastMonthAmt(saleAmount.getLastMonthAmt());
                    marketingReport.setLastMonthProportion(saleAmount.getLastMonthProportion());
                    marketingReport.setLastYearAmt(saleAmount.getLastYearAmt());
                    marketingReport.setLastYearProportion(saleAmount.getLastYearProportion());
                }
            }
        }
        //组装参数返回
        Map<String, Object> resultMap = new HashMap<>(5);
        resultMap.put("activityCount", monthReport);
        resultMap.put("activityAmountList", saleAmountList);
        return JsonResult.success(resultMap, "success");
    }

    @Override
    public Object getMarketingInfoByPlanCode(MarketingInDate inData) {
        MarketingReportOutData out = marketingReportMapper.getMarketingPlan(inData);
        if (ObjectUtil.isEmpty(out)) {
            throw new BusinessException("未查询到该营销活动！");
        }
        //本期
        StringBuilder sql = getMarketingSql(inData, out.getGsphBeginDate(),out.getGsphEndDate(),inData.getClientId());
        log.info("<APP营销报告><营销报告信息><查询麒麟-本期sql> :" + sql);
        List<SalesRankOutData> outDataList = kylinJdbcTemplate.query(sql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(outDataList)) {
            SalesRankOutData bqSales = outDataList.get(0);
            bqSales.setGrossRate(Util.divide(Util.checkNull(bqSales.getGross()), Util.checkNull(bqSales.getSalesAmt())).doubleValue());
            bqSales.setPersonPrice(Util.divide(Util.checkNull(bqSales.getSalesAmt()), Util.checkNull(bqSales.getBillQty())).doubleValue());
            out.setBqSales(bqSales);
        }
        //上期
        StringBuilder sqSql = getMarketingSql(inData, out.getSqStartTime(),out.getSqEndTime(),inData.getClientId());
        log.info("<APP营销报告><营销报告信息><查询麒麟-上期sqSql> :" + sqSql);
        List<SalesRankOutData> sqDataList = kylinJdbcTemplate.query(sqSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(sqDataList)) {
            SalesRankOutData sqSales = sqDataList.get(0);
            sqSales.setGrossRate(Util.divide(Util.checkNull(sqSales.getGross()), Util.checkNull(sqSales.getSalesAmt())).doubleValue());
            sqSales.setPersonPrice(Util.divide(Util.checkNull(sqSales.getSalesAmt()), Util.checkNull(sqSales.getBillQty())).doubleValue());
            out.setSqSales(sqSales);
        }
        //同期
        StringBuilder tqSql = getMarketingSql(inData, out.getTqStartTime(),out.getTqEndTime(),inData.getClientId());
        log.info("<APP营销报告><营销报告信息><查询麒麟-同期tqSql> :" + tqSql);
        List<SalesRankOutData> tqDataList = kylinJdbcTemplate.query(tqSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(tqDataList)) {
            SalesRankOutData tqSales = tqDataList.get(0);
            tqSales.setGrossRate(Util.divide(Util.checkNull(tqSales.getGross()), Util.checkNull(tqSales.getSalesAmt())).doubleValue());
            tqSales.setPersonPrice(Util.divide(Util.checkNull(tqSales.getSalesAmt()), Util.checkNull(tqSales.getBillQty())).doubleValue());
            out.setTqSales(tqSales);
        }
        return out;
    }

    @Override
    public Object listMarketingStores(MarketingInDate inData) {
        List<HashMap<String, Object>> storeList = marketingReportMapper.listMarketingStores(inData);
        return storeList;
    }

    @Override
    public Object listVIPInfoByPlanCode(MarketingInDate inData) {
        MarketingReportOutData out = marketingReportMapper.getMarketingPlan(inData);
        if (ObjectUtil.isEmpty(out)) {
            throw new BusinessException("未查询到该营销活动！");
        }
        StringBuilder head = new StringBuilder("");
        return null;
    }

    @Override
    public Object listMidTOP(MarketingInDate inData) {
        MarketingReportOutData out = marketingReportMapper.getMarketingPlan(inData);
        if (ObjectUtil.isEmpty(out)) {
            throw new BusinessException("未查询到该营销活动！");
        }
        StringBuilder midTopSql = getMidTopSql(inData, out);
        log.info("<APP营销报告><中类top><查询麒麟-sql> :" + midTopSql);
        List<SalesRankOutData> midTopDataList = kylinJdbcTemplate.query(midTopSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(midTopDataList)) {
            for (SalesRankOutData salesRankOutData : midTopDataList) {
                salesRankOutData.setMidClassName(salesRankOutData.getMidClassCode() + "-" + salesRankOutData.getMidClassName());
                salesRankOutData.setSalesAmtRate(Util.divide(Util.checkNull(salesRankOutData.getTopSalesAmt()),Util.checkNull(salesRankOutData.getSalesAmt())).doubleValue());
            }
        }
        return midTopDataList;
    }

    @Override
    public Object listSalesTop(MarketingInDate inData) {
        MarketingReportOutData out = marketingReportMapper.getMarketingPlan(inData);
        if (ObjectUtil.isEmpty(out)) {
            throw new BusinessException("未查询到该营销活动！");
        }
        StringBuilder salesSql = getSalesTopSql(inData, out);
        log.info("<APP营销报告><销售额top><查询麒麟-sql> :" + salesSql);
        List<SalesRankOutData> salesDataList = kylinJdbcTemplate.query(salesSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(salesDataList)) {
            for (SalesRankOutData salesRankOutData : salesDataList) {
                salesRankOutData.setSalesAmtRate(Util.divide(Util.checkNull(salesRankOutData.getTopSalesAmt()),Util.checkNull(salesRankOutData.getSalesAmt())).doubleValue());
            }
        }
        return salesDataList;
    }

    @Override
    public Object listSalesQtyTop(MarketingInDate inData) {
        MarketingReportOutData out = marketingReportMapper.getMarketingPlan(inData);
        if (ObjectUtil.isEmpty(out)) {
            throw new BusinessException("未查询到该营销活动！");
        }
        StringBuilder salesQtySql = getSalesQtySql(inData, out);
        log.info("<APP营销报告><销售量top><查询麒麟-sql> :" + salesQtySql);
        List<SalesRankOutData> salesDataList = kylinJdbcTemplate.query(salesQtySql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(salesDataList)) {
            for (SalesRankOutData salesRankOutData : salesDataList) {
                salesRankOutData.setSalesAmtRate(Util.divide(Util.checkNull(salesRankOutData.getTopSalesAmt()),Util.checkNull(salesRankOutData.getSalesAmt())).doubleValue());
            }
        }
        return salesDataList;
    }

    private StringBuilder getSalesQtySql(MarketingInDate inData, MarketingReportOutData out) {
        StringBuilder head = new StringBuilder("select t1.CLIENT client,t1.salesQty,t1.salesAmt,t2.topSalesAmt,t2.PRO_SELF_CODE proSelfCode,t2.PRO_NAME proName \n" +
                " from (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_QTY ) salesQty,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " c.FRANC_NAME francName\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                " LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                " LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                " where a.GSSD_DATE >= '" + out.getGsphBeginDate() + "' AND a.GSSD_DATE <= '" + out.getGsphEndDate() + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder t1 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " c.FRANC_NAME)t1\n" +
                "LEFT JOIN (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_QTY ) topSalesQty,\n" +
                " sum( a.GSSD_AMT ) topSalesAmt,\n" +
                " e.PRO_SELF_CODE,\n" +
                " e.PRO_NAME\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS"+
                " where a.GSSD_DATE >= '" + out.getGsphBeginDate() + "' AND a.GSSD_DATE <= '" + out.getGsphEndDate() + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder end = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " e.PRO_SELF_CODE,\n" +
                " e.PRO_NAME)t2 on t1.CLIENT = t2.CLIENT\n" +
                " order by t2.topSalesQty desc limit " + inData.getQty());
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            head.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t1.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
        }
        StringBuilder salesQtySql = head.append(t1).append(end);
        return salesQtySql;
    }

    @Override
    public Object listGoodsInfoByPlanCode(MarketingInDate inData) {
        MarketingReportOutData out = marketingReportMapper.getMarketingPlan(inData);
        if (ObjectUtil.isEmpty(out)) {
            throw new BusinessException("未查询到该营销活动！");
        }
        StringBuilder promotionSql = getPromotionSql(inData, out,out.getGsphBeginDate(),out.getGsphEndDate());
        log.info("<APP营销报告><促销-本期><查询麒麟-sql> :" + promotionSql);
        List<SalesRankOutData> promotionDataList = kylinJdbcTemplate.query(promotionSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(promotionDataList)) {
            SalesRankOutData bqSales = promotionDataList.get(0);
            out.setPromotionalSalesRate(Util.divide(Util.checkNull(bqSales.getCxAmt()), Util.checkNull(bqSales.getSalesAmt())).doubleValue());
            out.setUnPromotionalSalesRate(BigDecimal.ONE.subtract(Util.checkNull(out.getPromotionalSalesRate())).doubleValue());
            out.setPromotionalBillRate(Util.divide(Util.checkNull(bqSales.getCxBillQty()), Util.checkNull(bqSales.getBillQty())).doubleValue());
            out.setUnPromotionalBillRate(BigDecimal.ONE.subtract(Util.checkNull(out.getPromotionalBillRate())).doubleValue());
            out.setBqSales(bqSales);
        }
        StringBuilder promotionSqSql = getPromotionSql(inData, out,out.getGsphBeginDate(),out.getGsphEndDate());
        log.info("<APP营销报告><促销-上期><查询麒麟-sql> :" + promotionSql);
        List<SalesRankOutData> sqPromotionDataList = kylinJdbcTemplate.query(promotionSqSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(sqPromotionDataList)) {
            SalesRankOutData sqSales = sqPromotionDataList.get(0);
            out.setSqSales(sqSales);
        }
        StringBuilder promotionTqSql = getPromotionSql(inData, out,out.getGsphBeginDate(),out.getGsphEndDate());
        log.info("<APP营销报告><促销-同期><查询麒麟-sql> :" + promotionSql);
        List<SalesRankOutData> tqPromotionDataList = kylinJdbcTemplate.query(promotionTqSql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        if (!CollectionUtils.isEmpty(tqPromotionDataList)) {
            SalesRankOutData tqSales = tqPromotionDataList.get(0);
            out.setTqSales(tqSales);
        }
        return out;
    }

    private StringBuilder getPromotionSql(MarketingInDate inData, MarketingReportOutData out,String startTime,String endTime) {
        StringBuilder head = new StringBuilder("select t1.salesAmt,t2.salesAmt cxAmt ,t3.billNoCount billQty, t4.billNoCount cxBillQty\n" +
                "from (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_QTY ) salesQty,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " c.FRANC_NAME francName\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                " LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                " LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE " +
                " where a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder t1 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " c.FRANC_NAME)t1 \n" +
                "LEFT JOIN (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_QTY ) salesQty,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " c.FRANC_NAME francName\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                " LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                " LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE " +
                " where a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "'  and a.CLIENT = '" + inData.getClientId() + "' and a.GSSD_PM_ACTIVITY_ID ='"+ out.getGsphVoucherId()+"' ");
        StringBuilder t2 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " c.FRANC_NAME)t2 on t1.CLIENT = t2.CLIENT\n" +
                " LEFT JOIN ( SELECT\n" +
                "  count( 1 ) billNoCount,\n" +
                "  t.CLIENT\n" +
                "  FROM\n" +
                "  (\n" +
                "  SELECT\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  a.CLIENT,\n" +
                "  a.GSSD_DATE,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GSSD_BR_ID \n" +
                "  FROM\n" +
                "  GAIA_SD_SALE_D a\n" +
                "  INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "  LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "  INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "  AND a.GSSD_BR_ID = d.STO_CODE\n" +
                "  INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "  AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "  AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "  LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                "  LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                "  LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE " +
                " where a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder t3 = new StringBuilder("GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  a.GSSD_DATE,\n" +
                "  GSSD_BR_ID \n" +
                "  ) t \n" +
                "  GROUP BY\n" +
                "  t.CLIENT\n" +
                "  )t3 on t1.CLIENT = t3.CLIENT\n" +
                "\tLEFT JOIN ( SELECT\n" +
                "  count( 1 ) billNoCount,\n" +
                "  t.CLIENT\n" +
                "  FROM\n" +
                "  (\n" +
                "  SELECT\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  a.CLIENT,\n" +
                "  a.GSSD_DATE,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GSSD_BR_ID \n" +
                "  FROM\n" +
                "  GAIA_SD_SALE_D a\n" +
                "  INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "  LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "  INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "  AND a.GSSD_BR_ID = d.STO_CODE\n" +
                "  INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "  AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "  AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "  LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                "  LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                "  LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE " +
                " where a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "'  and a.CLIENT = '" + inData.getClientId() + "' and a.GSSD_PM_ACTIVITY_ID ='"+ out.getGsphVoucherId()+"' ");
        StringBuilder end = new StringBuilder("GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  a.GSSD_DATE,\n" +
                "  GSSD_BR_ID \n" +
                "  ) t \n" +
                "  GROUP BY\n" +
                "  t.CLIENT\n" +
                "  )t4 on t1.CLIENT = t4.CLIENT");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            head.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t1.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t2.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t3.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
        }
        StringBuilder promotionSql = head.append(t1).append(t2).append(t3).append(end);
        return promotionSql;
    }

    private StringBuilder getSalesTopSql(MarketingInDate inData, MarketingReportOutData out) {
        StringBuilder head = new StringBuilder("select t1.CLIENT client,t1.salesAmt,t2.topSalesAmt,t2.PRO_SELF_CODE proSelfCode,t2.PRO_NAME proName \n" +
                " from (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " c.FRANC_NAME francName\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                " LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                " LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                " where a.GSSD_DATE >= '" + out.getGsphBeginDate() + "' AND a.GSSD_DATE <= '" + out.getGsphEndDate() + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder t1 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " c.FRANC_NAME)t1\n" +
                "LEFT JOIN (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_AMT ) topSalesAmt,\n" +
                " e.PRO_SELF_CODE,\n" +
                " e.PRO_NAME\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS"+
                " where a.GSSD_DATE >= '" + out.getGsphBeginDate() + "' AND a.GSSD_DATE <= '" + out.getGsphEndDate() + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder end = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " e.PRO_SELF_CODE,\n" +
                " e.PRO_NAME)t2 on t1.CLIENT = t2.CLIENT\n" +
                " order by t2.topSalesAmt desc limit " + inData.getQty());
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            head.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t1.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
        }
        return head.append(t1).append(end);
    }

    private StringBuilder getMidTopSql(MarketingInDate inData, MarketingReportOutData out) {
        StringBuilder head = new StringBuilder("select t1.CLIENT client,t1.salesAmt,t2.topSalesAmt,t2.PRO_MID_CLASS_CODE midClassCode,\n" +
                " t2.PRO_MID_CLASS_NAME midClassName from (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " c.FRANC_NAME francName\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                " LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                " LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                " where a.GSSD_DATE >= '" + out.getGsphBeginDate() + "' AND a.GSSD_DATE <= '" + out.getGsphEndDate() + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder t1 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " c.FRANC_NAME)t1\n" +
                "LEFT JOIN (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_AMT ) topSalesAmt,\n" +
                " g.PRO_MID_CLASS_CODE,\n" +
                " g.PRO_MID_CLASS_NAME\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS"+
                " where a.GSSD_DATE >= '" + out.getGsphBeginDate() + "' AND a.GSSD_DATE <= '" + out.getGsphEndDate() + "'  and a.CLIENT = '" + inData.getClientId() + "'");
        StringBuilder end = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " g.PRO_MID_CLASS_CODE,\n" +
                " g.PRO_MID_CLASS_NAME)t2 on t1.CLIENT = t2.CLIENT\n" +
                " order by t2.topSalesAmt desc limit " + inData.getQty());
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            head.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t1.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
        }
        StringBuilder sql = head.append(t1).append(end);
        return sql;
    }

    private StringBuilder getMarketingSql(MarketingInDate inData, String startTime, String endTime ,String client) {
        StringBuilder head = new StringBuilder("select t1.salesAmt,t1.CLIENT,(t1.salesAmt - t1.GSSD_MOV_PRICE)gross,t2.billNoCount billQty from (SELECT\n" +
                " a.CLIENT,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " a.CLIENT clientId,\n" +
                " c.FRANC_NAME francName\n" +
                "FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = d.STO_CODE\n" +
                " INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                " AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                " AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                " LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                " LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                "WHERE a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "'  and a.CLIENT = '" + client + "'");
        StringBuilder t1 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " c.FRANC_NAME)t1\n" +
                " LEFT JOIN (  SELECT\n" +
                "  count( 1 ) billNoCount,\n" +
                "  t.CLIENT\n" +
                "  FROM\n" +
                "  (\n" +
                "  SELECT\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  a.CLIENT,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GSSD_BR_ID \n" +
                "  FROM\n" +
                "  GAIA_SD_SALE_D a\n" +
                "  INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "  LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "  INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "  AND a.GSSD_BR_ID = d.STO_CODE\n" +
                "  INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "  AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "  AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "  LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                "  LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                "  LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                "  where a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "'  and a.CLIENT = '" + client + "'");
        StringBuilder end = new StringBuilder("GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  GSSD_BR_ID \n" +
                "  ) t \n" +
                "  GROUP BY\n" +
                "  t.CLIENT) t2 on t1.CLIENT = t2.CLIENT");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            head.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
            t1.append(" and a.GSSD_BR_ID ='" + inData.getStoCode() + "'");
        }
        StringBuilder sql = head.append(t1).append(end);
        return sql;
    }


}

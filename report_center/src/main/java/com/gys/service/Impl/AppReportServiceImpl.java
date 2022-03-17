package com.gys.service.Impl;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.gys.common.data.*;
import com.gys.common.kylin.RowMapper;
import com.gys.mapper.AppReportMapper;
import com.gys.service.AppReportService;
import com.gys.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppReportServiceImpl implements AppReportService {
    @Resource
    private AppReportMapper appReportMapper;
    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;

    @Override
    public List<String> selectYearlist() {
        return appReportMapper.selectYearList();
    }

    @Override
    public List<Map<String, String>> selectWeekOrMonthListByYear(Map<String, String> inData) {
        List<Map<String, String>> list = new ArrayList<>();
        if ("1".equals(inData.get("type"))){
            list = appReportMapper.selectMonthListByYear(inData.get("year"));
        }
        if ("2".equals(inData.get("type"))){
            list = appReportMapper.selectWeekListByYear(inData.get("year"));
        }
        return list;
    }

    @Override
    public List<ReportOutData> ingredientClassList(ReportInData inData) {
        String startDate = "";
        String endDate = "";
        Integer month = null;
        Integer week = null;
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }else if("3".equals(inData.getChooseType())){
            if ("1".equals(inData.getWeekOrMonth())) {
                month = Integer.valueOf(inData.getNumber());
                week = 0;
            }
            if ("2".equals(inData.getWeekOrMonth())){
                week = Integer.valueOf(inData.getNumber());
                month = 0;
            }
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(inData.getYear(),month,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }else if("4".equals(inData.getChooseType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
            int year = calendar.get(Calendar.YEAR) - 1;
            week = calendar.get(Calendar.WEEK_OF_YEAR);
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }
        List<ReportOutData> list = appReportMapper.ingredientClassList(inData.getClientId(), inData.getStoCode(), startDate, endDate, inData.getCategoryClass());
        List<ReportOutData> proCountList = appReportMapper.ingredientProCountClassList(inData.getClientId(), inData.getStoCode(), startDate, endDate, inData.getCategoryClass());
        for (ReportOutData data : list) {
            for (ReportOutData proCount : proCountList) {
                if ("2".equals(inData.getCategoryClass())&& data.getMidClassCode().equals(proCount.getMidClassCode())) {
                    data.setProCount(proCount.getProCount());
                    break;
                } else if ("1".equals(inData.getCategoryClass())&& data.getBigClassCode().equals(proCount.getBigClassCode())) {
                    data.setProCount(proCount.getProCount());
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public List<ReportOutData> categoryClassList(ReportInData inData) {
        String startDate = "";
        String endDate = "";
        Integer month = null;
        Integer week = null;
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }else if("3".equals(inData.getChooseType())){
            if ("1".equals(inData.getWeekOrMonth())) {
                month = Integer.valueOf(inData.getNumber());
                week = 0;
            }
            if ("2".equals(inData.getWeekOrMonth())){
                week = Integer.valueOf(inData.getNumber());
                month = 0;
            }
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(inData.getYear(),month,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }else if("4".equals(inData.getChooseType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
            int year = calendar.get(Calendar.YEAR) - 1;
            week = calendar.get(Calendar.WEEK_OF_YEAR);
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }
        StringBuilder builder = new StringBuilder().append("SELECT x.CLIENT,");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append("x.GSSD_BR_ID as stoCode,x.STO_NAME as stoName,");
        }
        builder.append("CONCAT( x.bigClass, x.bigClassname ) AS bigClass,");
        if("2".equals(inData.getCategoryClass())){
            builder.append("CONCAT( x.midClass, x.midClassname ) AS midClass,");
        }
        builder.append("x.totalAmt,x.totalCost,ROUND(x.totalProfit,2) totalProfit,")
                .append("(CASE WHEN x.TotalAmt <> 0 THEN Round(x.TotalProfit*100/x.TotalAmt,2) ELSE 0 END) as totalProfitRate,y.allAmt,")
                .append("(CASE WHEN y.allAmt <> 0 THEN Round( x.TotalProfit * 100 / y.allAmt, 2 ) ELSE 0 END) as contributionRate,x.proCount")
                .append(" FROM ( SELECT a.CLIENT,");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append("a.GSSD_BR_ID,c.STO_NAME,");
        }
        builder.append("d.PRO_BIG_CLASS_CODE AS bigClass,d.PRO_BIG_CLASS_NAME AS bigClassname,");
        if("2".equals(inData.getCategoryClass())){
            builder.append("d.PRO_MID_CLASS_CODE AS midClass,d.PRO_MID_CLASS_NAME AS midClassname,");
        }
        builder.append("SUM( a.GSSD_AMT ) AS totalAmt,SUM( a.GSSD_MOV_PRICE ) AS totalCost,")
                .append("SUM( a.GSSD_AMT ) - sum( a.GSSD_MOV_PRICE ) AS totalProfit,f.proCount")
                .append(" FROM GAIA_SD_SALE_D a")
                .append(" INNER JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.PRO_SITE AND a.GSSD_PRO_ID = b.PRO_SELF_CODE")
                .append(" INNER JOIN GAIA_STORE_DATA c ON a.CLIENT = c.CLIENT AND a.GSSD_BR_ID = c.STO_CODE")
                .append(" INNER JOIN GAIA_PRODUCT_CLASS d ON b.PRO_CLASS = d.PRO_CLASS_CODE")
                .append(" INNER JOIN (select e.CLIENT,e.PRO_BIG_CLASS_CODE,");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append("e.GSSD_BR_ID,");
        }
        if("2".equals(inData.getCategoryClass())) {
            builder.append("e.PRO_MID_CLASS_CODE,");
        }
        builder.append("count(e.GSSD_PRO_ID) proCount from (SELECT a.CLIENT,d.PRO_BIG_CLASS_CODE,");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            builder.append("a.GSSD_BR_ID,");
        }
        if("2".equals(inData.getCategoryClass())) {
            builder.append("d.PRO_MID_CLASS_CODE,");
        }
        builder.append("a.GSSD_PRO_ID")
                .append(" FROM GAIA_SD_SALE_D a")
                .append(" INNER JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.PRO_SITE AND a.GSSD_PRO_ID = b.PRO_SELF_CODE")
                .append(" INNER JOIN GAIA_PRODUCT_CLASS d ON b.PRO_CLASS = d.PRO_CLASS_CODE")
                .append(" WHERE a.GSSD_DATE >= '" +startDate+"'" )
                .append(" AND a.GSSD_DATE <= '" + endDate + "'")
                .append(" AND a.CLIENT = '" + inData.getClientId() + "'");
                if(ObjectUtil.isNotEmpty(inData.getStoCode())){
                    builder.append(" and a.GSSD_BR_ID = '" + inData.getStoCode() +"'");
                }
        builder.append(" GROUP BY a.CLIENT,d.PRO_BIG_CLASS_CODE,");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append("a.GSSD_BR_ID,");
        }
        if("2".equals(inData.getCategoryClass())) {
            builder.append("d.PRO_MID_CLASS_CODE,");
        }
        builder.append("a.GSSD_PRO_ID) e GROUP BY e.CLIENT,e.PRO_BIG_CLASS_CODE ");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(",e.GSSD_BR_ID");
        }
        if("2".equals(inData.getCategoryClass())) {
            builder.append(",e.PRO_MID_CLASS_CODE");
        }
        builder.append(" ) f ON f.CLIENT = a.CLIENT AND d.PRO_BIG_CLASS_CODE = f.PRO_BIG_CLASS_CODE");
        if (ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" AND f.GSSD_BR_ID = a.GSSD_BR_ID");
        }
        if("2".equals(inData.getCategoryClass())) {
            builder.append(" AND d.PRO_MID_CLASS_CODE = f.PRO_MID_CLASS_CODE");
        }
        builder.append(" WHERE a.GSSD_DATE >= '" +startDate+"'" )
                .append(" AND a.GSSD_DATE <= '" + endDate + "'")
                .append(" AND a.CLIENT = '" + inData.getClientId() + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and a.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY a.CLIENT,d.PRO_BIG_CLASS_CODE,d.PRO_BIG_CLASS_NAME,");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append("a.GSSD_BR_ID,c.STO_NAME,");
        }
        if("2".equals(inData.getCategoryClass())) {
            builder.append(" d.PRO_MID_CLASS_CODE,d.PRO_MID_CLASS_NAME,");
        }
        builder.append(" f.proCount ORDER BY SUM( a.GSSD_AMT ) - sum( a.GSSD_MOV_PRICE ) DESC ")
                .append(" Limit 10 ) x,")
                .append(" (SELECT CLIENT,SUM( GSSD_AMT ) allAmt");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(",GSSD_BR_ID");
        }
        builder.append(" FROM GAIA_SD_SALE_D  WHERE CLIENT = '" + inData.getClientId() + "'")
                .append(" AND GSSD_DATE >= '" +startDate+"'" )
                .append(" AND GSSD_DATE <= '" + endDate + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY CLIENT");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(", GSSD_BR_ID ");
        }
        builder.append(" ) y WHERE x.CLIENT = y.CLIENT ");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" AND x.GSSD_BR_ID = y.GSSD_BR_ID");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<ReportOutData> reportOutData = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ReportOutData.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(reportOutData));
        if (ObjectUtil.isNotEmpty(reportOutData)){
            for (ReportOutData outData : reportOutData) {
                outData.setContributionRate(outData.getContributionRate() + "%");
                outData.setTotalProfitRate(outData.getTotalProfitRate() + "%");
            }
        }
        return reportOutData;
    }

    @Override
    public List<CompClassOutData> compClassList(ReportInData inData) {
        String startDate = "";
        String endDate = "";
        Integer month = null;
        Integer week = null;
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }else if("3".equals(inData.getChooseType())){
            if ("1".equals(inData.getWeekOrMonth())) {
                month = Integer.valueOf(inData.getNumber());
                week = 0;
            }
            if ("2".equals(inData.getWeekOrMonth())){
                week = Integer.valueOf(inData.getNumber());
                month = 0;
            }
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(inData.getYear(),month,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }else if("4".equals(inData.getChooseType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
            int year = calendar.get(Calendar.YEAR) - 1;
            week = calendar.get(Calendar.WEEK_OF_YEAR);
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }
        StringBuilder builder = new StringBuilder().append("SELECT b.PRO_COMPCLASS proCompClass,")
                .append("( CASE b.PRO_COMPCLASS WHEN NULL THEN '其他' WHEN '' THEN '其他' ELSE b.PRO_COMPCLASS_NAME END ) proCompClassName,")
                .append(" d.proCount,sum( a.GSSD_AMT ) gssdAmt,ROUND((SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE )),2) AS grossProfitAmt,")
                .append(" (CASE WHEN SUM( a.GSSD_AMT ) <> 0 THEN Round(( SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ) ) / SUM( a.GSSD_AMT ) * 100,2) ELSE 0 END)  AS grossProfitRate ")
                .append(" FROM GAIA_SD_SALE_D a INNER JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.PRO_SITE AND a.GSSD_PRO_ID = b.PRO_SELF_CODE ")
                .append(" INNER JOIN GAIA_STORE_DATA c ON a.CLIENT = c.CLIENT AND a.GSSD_BR_ID = c.STO_CODE ")
                .append(" INNER JOIN ( SELECT count( GSSD_PRO_ID ) proCount, PRO_COMPCLASS,PRO_COMPCLASS_NAME ")
                .append(" FROM ( SELECT sa.GSSD_PRO_ID, pb.PRO_COMPCLASS, pb.PRO_COMPCLASS_NAME ")
                .append(" FROM GAIA_SD_SALE_D sa INNER JOIN GAIA_PRODUCT_BUSINESS pb ON sa.CLIENT = pb.CLIENT AND sa.GSSD_BR_ID = pb.PRO_SITE ")
                .append(" AND sa.GSSD_PRO_ID = pb.PRO_SELF_CODE WHERE sa.GSSD_DATE >= '"+startDate+"' AND sa.GSSD_DATE <= '"+endDate+"'")
                .append(" AND sa.CLIENT = '" + inData.getClientId() + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and sa.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY sa.GSSD_PRO_ID, pb.PRO_COMPCLASS,pb.PRO_COMPCLASS_NAME) sc ")
                .append("GROUP BY PRO_COMPCLASS,PRO_COMPCLASS_NAME ) d ON d.PRO_COMPCLASS = b.PRO_COMPCLASS ")
                .append(" WHERE a.GSSD_DATE >= '"+startDate+"' AND a.GSSD_DATE <= '"+endDate+"'  AND a.CLIENT = '" + inData.getClientId() + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and b.PRO_COMPCLASS !='N999999' and a.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY b.PRO_COMPCLASS, b.PRO_COMPCLASS_NAME, d.proCount ")
                .append(" ORDER BY sum( a.GSSD_AMT ) DESC ");
        if ("1".equals(inData.getType())) {
            builder.append(" LIMIT 10");
        }else{
            builder.append(" LIMIT 50");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<CompClassOutData> compClassOutData = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(CompClassOutData.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(compClassOutData));
        //获取
        for (CompClassOutData outData : compClassOutData) {
            outData.setGrossProfitRate(outData.getGrossProfitRate() + "%");
        }

        return compClassOutData;
    }

    @Override
    public PageInfo<SaleBreedOutData> saleBreedList(ReportInData inData) {
        String startDate = "";
        String endDate = "";
        Integer month = null;
        Integer week = null;
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }else if("3".equals(inData.getChooseType())){
            if ("1".equals(inData.getWeekOrMonth())) {
                month = Integer.valueOf(inData.getNumber());
                week = 0;
            }
            if ("2".equals(inData.getWeekOrMonth())){
                week = Integer.valueOf(inData.getNumber());
                month = 0;
            }
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(inData.getYear(),month,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }else if("4".equals(inData.getChooseType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
            int year = calendar.get(Calendar.YEAR) - 1;
            week = calendar.get(Calendar.WEEK_OF_YEAR);
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }
        StringBuilder builder = new StringBuilder().append("SELECT a.GSSD_PRO_ID proCode,b.PRO_NAME proName,sum( a.GSSD_QTY ) gssdQty,ROUND(sum( a.GSSD_AMT ),2) gssdAmt,")
                .append(" b.PRO_SPECS proSpecs,'' factoryName,")
                .append(" ROUND( SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ), 2 ) AS grossProfitAmt,")
                .append(" (CASE WHEN SUM( a.GSSD_AMT ) <> 0 THEN ROUND(( SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ) ) / SUM( a.GSSD_AMT ) * 100,2 ) ELSE 0 END) AS grossProfitRate  FROM")
                .append(" GAIA_SD_SALE_D a ")
                .append(" INNER JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT ")
                .append(" AND a.GSSD_BR_ID = b.PRO_SITE ")
                .append(" AND a.GSSD_PRO_ID = b.PRO_SELF_CODE")
                .append(" INNER JOIN GAIA_STORE_DATA c ON a.CLIENT = c.CLIENT ")
                .append(" AND a.GSSD_BR_ID = c.STO_CODE WHERE")
                .append(" a.GSSD_DATE >= '"+startDate+"' ")
                .append(" AND a.GSSD_DATE <= '"+endDate+"' ")
                .append(" AND a.CLIENT = '" + inData.getClientId() + "' " );
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and a.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append("GROUP BY a.GSSD_PRO_ID,b.PRO_NAME,b.PRO_SPECS ,b.PRO_SPECS")
                .append(" ORDER BY sum( a.GSSD_AMT ) DESC ");

           builder.append(" LIMIT "+inData.getPageSize());
        int num = (inData.getPageNum()-1) * inData.getPageSize();
        builder.append("  offset  " + num);

        log.info("sql统计数据：{}", builder.toString());
        List<SaleBreedOutData> saleBreedOutData = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(SaleBreedOutData.class));

        PageInfo pageInfo;

        System.err.println();
       //获取proCode 10个
        List<String> proCodeList = saleBreedOutData.stream().map(SaleBreedOutData::getProCode).collect(Collectors.toList());
        //根据inData.getClientId(),inData.getStoCode() ,List<proCode > 查mysql，select proCode,生产企业==》list
        List<SaleBreedOutData> saleBreedOutDataList = appReportMapper.listSaleBreedOutData(proCodeList,inData.getClientId(),"");
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(saleBreedOutData));
        for (SaleBreedOutData outData : saleBreedOutData) {
            outData.setGrossProfitRate(outData.getGrossProfitRate() + "%");
            for (SaleBreedOutData data :saleBreedOutDataList) {
                if(outData.getProCode().equals(data.getProCode())){
                    outData.setFactoryName(data.getFactoryName());
                }
            }
        }
        pageInfo = new PageInfo(saleBreedOutData);

        return pageInfo;
    }

    @Override
    public List<OverStockOutData> overStockList(ReportInData inData) {
//        String startDate = "";
//        String endDate = "";
//        Integer month = null;
//        Integer week = null;
//        if("1".equals(inData.getChooseType())){
//            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
//            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
//        }else if ("2".equals(inData.getChooseType())){
//            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
//            startDate = timeStr[0];
//            endDate = timeStr[1];
//        }else if("3".equals(inData.getChooseType())){
//            if ("1".equals(inData.getWeekOrMonth())) {
//                month = Integer.valueOf(inData.getNumber());
//                week = 0;
//            }
//            if ("2".equals(inData.getWeekOrMonth())){
//                week = Integer.valueOf(inData.getNumber());
//                month = 0;
//            }
//            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(inData.getYear(),month,week);
//            startDate = startAndEndDate.get("minDate");
//            endDate = startAndEndDate.get("maxDate");
//        }else if("4".equals(inData.getChooseType())){
//            Calendar calendar = Calendar.getInstance();
//            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
//            int year = calendar.get(Calendar.YEAR) - 1;
//            week = calendar.get(Calendar.WEEK_OF_YEAR);
//            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
//            startDate = startAndEndDate.get("minDate");
//            endDate = startAndEndDate.get("maxDate");
//        }
//        inData.setStartDate(startDate);
//        inData.setEndDate(endDate);
        List<OverStockOutData> overStockOutData = appReportMapper.selectOverStockList(inData);
        return overStockOutData;
    }

    @Override
    public RelevancyOutData selectRelevancyTotal(ReportInData inData) {
        String startDate = "";
        String endDate = "";
        Integer month = null;
        Integer week = null;
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }else if("3".equals(inData.getChooseType())){
            if ("1".equals(inData.getWeekOrMonth())) {
                month = Integer.valueOf(inData.getNumber());
                week = 0;
            }
            if ("2".equals(inData.getWeekOrMonth())){
                week = Integer.valueOf(inData.getNumber());
                month = 0;
            }
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(inData.getYear(),month,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }else if("4".equals(inData.getChooseType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
            int year = calendar.get(Calendar.YEAR) - 1;
            week = calendar.get(Calendar.WEEK_OF_YEAR);
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
            startDate = startAndEndDate.get("minDate");
            endDate = startAndEndDate.get("maxDate");
        }
        inData.setStartDate(startDate);
        inData.setEndDate(endDate);
        RelevancyOutData relevancyOutData = appReportMapper.selectRelevancyTotal(inData);
        return relevancyOutData;
    }

    @Override
    public CompClassRelevancyTotal selectCompClassRelevancyList(ReportInData inData) {
        CompClassRelevancyTotal result = new CompClassRelevancyTotal();
        String startDate = "";
        String endDate = "";
        // 上周/月起止时间
        String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
        startDate = timeStr[0];
        endDate = timeStr[1];
        inData.setStartDate(startDate);
        inData.setEndDate(endDate);
        BigDecimal billCountTotal = BigDecimal.ZERO;
        //上周/月销售成分前十
        List<CompClassOutData> compClassOutDataBefore = selectCompClassTenLine(inData);
        List<CompClassTopTen> compClassTopTenList = new ArrayList<>();
        for (CompClassOutData classOutData : compClassOutDataBefore) {
            inData.setCompClass(classOutData.getProCompClass());
            List<CompClassRelevancyOutData> classRelevancyOutDataList = appReportMapper.selectCompClassRelevancyOutData(inData);
            if (classRelevancyOutDataList != null && classRelevancyOutDataList.size() > 0){
                String compClass = classOutData.getProCompClass();
                CompClassTopTen compClassTopTen = new CompClassTopTen();
                compClassTopTen.setFirstBillCount(classRelevancyOutDataList.get(0).getBillCount());
                compClassTopTen.setFirstProCompClass(compClass);
                compClassTopTen.setFirstProCompClassName(classOutData.getProCompClassName());
                BigDecimal a = BigDecimal.ZERO;
                List<CompClassRelevancyOutData> list = new ArrayList<>();
                billCountTotal = billCountTotal.add(classRelevancyOutDataList.get(0).getBillCount());
                for (CompClassRelevancyOutData outData : classRelevancyOutDataList) {
                    if (!compClass.equals(outData.getProCompClass())){
                        String relevancyRate = "0.00%";
                        if (!"0".equals(compClassTopTen.getFirstBillCount())) {
                            relevancyRate = outData.getBillCount().divide(compClassTopTen.getFirstBillCount(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) + "%";
                        }
                        outData.setRelevancyRate(relevancyRate);
                        list.add(outData);
                        a = a.add(outData.getGssdAmt()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    }
                }
                compClassTopTen.setFirstAmt(a);
                if (list.size() > 0){
                    compClassTopTen.setCompClassRelevancyOutDataList(list);
                }
                compClassTopTenList.add(compClassTopTen);
            }

        }
        if(compClassTopTenList.size() > 0){
            List<CompClassTopTen> classTopTenList = compClassTopTenList.stream()
                    .sorted(Comparator.comparing(CompClassTopTen::getFirstBillCount).reversed().thenComparing(CompClassTopTen::getFirstAmt)).collect(Collectors.toList());
            result.setCompClassTopTenList(classTopTenList);
        }
        // 本周/月起止时间
        startDate = getTimesWeekmorning(inData.getWeekOrMonth());
        endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        inData.setStartDate(startDate);
        inData.setEndDate(endDate);
        // 本周/月销售成分前十
        SumNowReportOutData sumNowReportOutData = appReportMapper.sumNowReport(inData);
        String content = "";
        if (ObjectUtil.isNotEmpty(sumNowReportOutData)){
            if("1".equals(inData.getWeekOrMonth())){
                content = "本月成功关联次数"+sumNowReportOutData.getBillCount()+",比上月环比"+(sumNowReportOutData.getBillCount().subtract(billCountTotal))+",成功关联销售额"
                        + sumNowReportOutData.getGssdAmt() + ",成功关联毛利额" + sumNowReportOutData.getGrossProfitAmt();
            }else{
                content += "本周成功关联次数"+sumNowReportOutData.getBillCount()+",比上周环比"+(sumNowReportOutData.getBillCount().subtract(billCountTotal))+",成功关联销售额"
                        + sumNowReportOutData.getGssdAmt() + ",成功关联毛利额" + sumNowReportOutData.getGrossProfitAmt();
            }
        }

        result.setCompClassRelevancyContent(content);
        return result;
    }

    // 获得本周一0点时间
    public static String getTimesWeekmorning(String type) {
        Calendar cal = Calendar.getInstance();
        String startDate = "";
        if ("1".equals(type)){
            int month = cal.get(Calendar.MONTH) + 1;
            startDate = cal.get(Calendar.YEAR) + "-" + (month < 10 ? "0" + month : month) + "-01";
        }else {
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDate = sdf.format(cal.getTime());
        }
        return startDate;
    }
    public static String getYearMonthByMonth(String type) {
        String time = "";
        String startDate = "";
        String endDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        if ("2".equals(type)) {
            //获取上一周的起止时间：
            cal.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
            cal.add(Calendar.DATE, -1 * 7);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            startDate = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1 * 7);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            endDate = sdf.format(cal.getTime());
        }else {
            //获取上一个月的起止时间：
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            startDate = sdf.format(cal.getTime());
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 0);
            endDate = sdf.format(cal.getTime());
        }
        time = startDate + "," + endDate;
        return time;
    }

    public List<CompClassOutData> selectCompClassTen(ReportInData inData){
        String startDate = "";
        String endDate = "";
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }
        StringBuilder builder = new StringBuilder().append("SELECT b.PRO_COMPCLASS proCompClass,")
                .append("( CASE b.PRO_COMPCLASS WHEN NULL THEN '其他' WHEN '' THEN '其他' ELSE b.PRO_COMPCLASS_NAME END ) proCompClassName,")
                .append(" d.proCount,sum( a.GSSD_AMT ) gssdAmt,SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ) AS grossProfitAmt,")
                .append(" (CASE WHEN SUM( a.GSSD_AMT ) <> 0 THEN Round(( SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ) ) / SUM( a.GSSD_AMT ) * 100,2) ELSE 0 END)  AS grossProfitRate ")
                .append(" FROM GAIA_SD_SALE_D a INNER JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.PRO_SITE AND a.GSSD_PRO_ID = b.PRO_SELF_CODE ")
                .append(" INNER JOIN GAIA_STORE_DATA c ON a.CLIENT = c.CLIENT AND a.GSSD_BR_ID = c.STO_CODE ")
                .append(" INNER JOIN ( SELECT count( GSSD_PRO_ID ) proCount, PRO_COMPCLASS,PRO_COMPCLASS_NAME ")
                .append(" FROM ( SELECT sa.GSSD_PRO_ID, pb.PRO_COMPCLASS, pb.PRO_COMPCLASS_NAME ")
                .append(" FROM GAIA_SD_SALE_D sa INNER JOIN GAIA_PRODUCT_BUSINESS pb ON sa.CLIENT = pb.CLIENT AND sa.GSSD_BR_ID = pb.PRO_SITE ")
                .append(" AND sa.GSSD_PRO_ID = pb.PRO_SELF_CODE WHERE sa.GSSD_DATE >= '"+startDate+"' AND sa.GSSD_DATE <= '"+endDate+"'")
                .append(" AND sa.CLIENT = '" + inData.getClientId() + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and sa.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY sa.GSSD_PRO_ID, pb.PRO_COMPCLASS,pb.PRO_COMPCLASS_NAME) sc ")
                .append("GROUP BY PRO_COMPCLASS,PRO_COMPCLASS_NAME ) d ON d.PRO_COMPCLASS = b.PRO_COMPCLASS ")
                .append(" WHERE a.GSSD_DATE >= '"+startDate+"' AND a.GSSD_DATE <= '"+endDate+"'  AND a.CLIENT = '" + inData.getClientId() + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and b.PRO_COMPCLASS !='N999999' and a.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY b.PRO_COMPCLASS, b.PRO_COMPCLASS_NAME, d.proCount ORDER BY sum( a.GSSD_AMT ) DESC LIMIT 10");
        log.info("sql统计数据：{}", builder.toString());
        List<CompClassOutData> compClassOutData = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(CompClassOutData.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(compClassOutData));
        for (CompClassOutData outData : compClassOutData) {
            outData.setGrossProfitRate(outData.getGrossProfitRate() + "%");
        }
        return compClassOutData;
    }

    public List<CompClassOutData> selectCompClassTenLine(ReportInData inData){
        String startDate = "";
        String endDate = "";
        if("1".equals(inData.getChooseType())){
            startDate = getTimesWeekmorning(inData.getWeekOrMonth());
            endDate = CommonUtil.parseyyyyMMdd(CommonUtil.getyyyyMMdd());
        }else if ("2".equals(inData.getChooseType())){
            String[] timeStr = getYearMonthByMonth(inData.getWeekOrMonth()).split(",");
            startDate = timeStr[0];
            endDate = timeStr[1];
        }
        StringBuilder builder = new StringBuilder().append("SELECT b.PRO_COMPCLASS proCompClass,")
                .append("( CASE b.PRO_COMPCLASS WHEN NULL THEN '其他' WHEN '' THEN '其他' ELSE b.PRO_COMPCLASS_NAME END ) proCompClassName,")
                .append(" d.proCount,sum( a.GSSD_AMT ) gssdAmt,SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ) AS grossProfitAmt,")
                .append(" (CASE WHEN SUM( a.GSSD_AMT ) <> 0 THEN Round(( SUM( a.GSSD_AMT ) - SUM( a.GSSD_MOV_PRICE ) ) / SUM( a.GSSD_AMT ) * 100,2) ELSE 0 END)  AS grossProfitRate ")
                .append(" FROM GAIA_SD_SALE_D a INNER JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.PRO_SITE AND a.GSSD_PRO_ID = b.PRO_SELF_CODE ")
                .append(" INNER JOIN GAIA_STORE_DATA c ON a.CLIENT = c.CLIENT AND a.GSSD_BR_ID = c.STO_CODE ")
                .append(" INNER JOIN ( SELECT count( GSSD_PRO_ID ) proCount, PRO_COMPCLASS,PRO_COMPCLASS_NAME ")
                .append(" FROM ( SELECT sa.GSSD_PRO_ID, pb.PRO_COMPCLASS, pb.PRO_COMPCLASS_NAME ")
                .append(" FROM GAIA_SD_SALE_D sa INNER JOIN GAIA_PRODUCT_BUSINESS pb ON sa.CLIENT = pb.CLIENT AND sa.GSSD_BR_ID = pb.PRO_SITE ")
                .append(" AND sa.GSSD_PRO_ID = pb.PRO_SELF_CODE WHERE sa.GSSD_DATE >= '"+startDate+"' AND sa.GSSD_DATE <= '"+endDate+"'")
                .append(" AND sa.CLIENT = '" + inData.getClientId() + "'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and sa.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" GROUP BY sa.GSSD_PRO_ID, pb.PRO_COMPCLASS,pb.PRO_COMPCLASS_NAME) sc ")
                .append("GROUP BY PRO_COMPCLASS,PRO_COMPCLASS_NAME ) d ON d.PRO_COMPCLASS = b.PRO_COMPCLASS ")
                .append(" WHERE a.GSSD_DATE >= '"+startDate+"' AND a.GSSD_DATE <= '"+endDate+"'  AND a.CLIENT = '" + inData.getClientId() + "' and b.PRO_COMPCLASS !='N999999'");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())){
            builder.append(" and a.GSSD_BR_ID = '" + inData.getStoCode() +"'");
        }
        builder.append(" AND a.GSSD_BILL_NO in (SELECT GSSD_BILL_NO FROM (SELECT GSSD_BILL_NO,count( GSSD_PRO_ID ) proCount,sum(GSSD_MOV_PRICE) GSSD_MOV_PRICE ");
        builder.append(" FROM (SELECT sa.GSSD_PRO_ID,sa.GSSD_BILL_NO,sum( sa.GSSD_MOV_PRICE ) GSSD_MOV_PRICE FROM GAIA_SD_SALE_D sa ");
        builder.append(" WHERE sa.GSSD_DATE >= '"+startDate+"' AND sa.GSSD_DATE <= '"+endDate+"' AND sa.CLIENT = '" + inData.getClientId() + "' ");
        if(ObjectUtil.isNotEmpty(inData.getStoCode())) {
            builder.append(" AND sa.GSSD_BR_ID = '" + inData.getStoCode() + "' ");
        }
        builder.append(" GROUP BY sa.GSSD_BILL_NO,sa.GSSD_PRO_ID) sc GROUP BY  GSSD_BILL_NO) a WHERE proCount > 1 AND GSSD_MOV_PRICE >= 0 )");
        builder.append(" GROUP BY b.PRO_COMPCLASS, b.PRO_COMPCLASS_NAME, d.proCount ORDER BY sum( a.GSSD_AMT ) DESC LIMIT 10");
        log.info("sql统计数据：{}", builder.toString());
        List<CompClassOutData> compClassOutData = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(CompClassOutData.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(compClassOutData));
        for (CompClassOutData outData : compClassOutData) {
            outData.setGrossProfitRate(outData.getGrossProfitRate() + "%");
        }
        return compClassOutData;
    }

}

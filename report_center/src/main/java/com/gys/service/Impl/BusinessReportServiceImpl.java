package com.gys.service.Impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.gys.common.exception.BusinessException;
import com.gys.common.kylin.RowMapper;
import com.gys.common.response.Result;
import com.gys.entity.data.businessReport.*;
import com.gys.mapper.BusinessReportMapper;
import com.gys.service.BusinessReportService;
import com.gys.util.CosUtils;
import com.gys.util.DateUtil;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BusinessReportServiceImpl implements BusinessReportService {

    @Autowired
    private BusinessReportMapper businessReportMapper;
    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;
    @Resource
    public CosUtils cosUtils;

    @Override
    public BusinessReportResponse businessSalesReport(BusinessReportRequest inData) {
        //上月
        String lastMonth = DateUtil.getLastMonth(inData.getDate());
        //去年同期
        String lastYearCurrentDate = DateUtil.getLastYearCurrentDate(inData.getDate());

        //门店数  和  销售天数
        BusinessReportResponse response = businessReportMapper.getSalesQtyAndSalesDay(inData);
        //整体销售
        //本月销售，毛利，毛利率
        OverallSales overallSales = businessReportMapper.getAmtAndGrossAndGrossRate(inData.getClient(), inData.getStoCode(), inData.getDate());
        overallSales = ObjectUtil.isNotEmpty(overallSales) ? overallSales : new OverallSales();
        //上月销售，毛利，毛利率
        OverallSales lastOverallSales = businessReportMapper.getAmtAndGrossAndGrossRate(inData.getClient(), inData.getStoCode(), lastMonth);
        //同期销售，毛利，毛利率
        OverallSales lastYearOverallSales = businessReportMapper.getAmtAndGrossAndGrossRate(inData.getClient(), inData.getStoCode(), lastYearCurrentDate);
        //整体销售
        setOverallSales(overallSales, lastOverallSales, lastYearOverallSales);
        //获取计划营业额、计划毛利额、计划毛利率
        OverallSalesPlanData salesPlanData =   businessReportMapper.getPlanData(inData.getClient(), inData.getStoCode(),inData.getDate());
        //获取销售达成率 毛利达成率 毛利率达成率
        setSalesPlanData(overallSales,salesPlanData);
        response.setOverallSales(overallSales);
        //单店日均
        SingleStoreInfo singleStoreInfo = getSingleStoreInfo(response, overallSales, lastOverallSales, lastYearOverallSales);
        response.setSingleInfo(singleStoreInfo);
        //会员销售
        //本月
        VIPInfo vipInfo = businessReportMapper.getVIPInfo(inData.getClient(), inData.getStoCode(), inData.getDate());
        //上月
        VIPInfo lastVipInfo = businessReportMapper.getVIPInfo(inData.getClient(), inData.getStoCode(), lastMonth);
        //去年同期
        VIPInfo lastYearVipInfo = businessReportMapper.getVIPInfo(inData.getClient(), inData.getStoCode(), lastYearCurrentDate);
        //本月会员销售额
        VIPInfo vip = getVipInfo(overallSales, lastOverallSales, lastYearOverallSales, vipInfo, lastVipInfo, lastYearVipInfo);
        response.setVipInfo(vip);
        //医保销售
        //本月
        MedicalInsuranceSales medicalSales = businessReportMapper.getMedicalSalesInfo(inData.getClient(), inData.getStoCode(), inData.getDate());
        //上月
        MedicalInsuranceSales lastMedicalSales = businessReportMapper.getMedicalSalesInfo(inData.getClient(), inData.getStoCode(), lastMonth);
        //去年同期
        MedicalInsuranceSales lastYearMedicalSales = businessReportMapper.getMedicalSalesInfo(inData.getClient(), inData.getStoCode(), lastYearCurrentDate);
        MedicalInsuranceSales medical = getMedicalInsuranceSales(overallSales, lastOverallSales, lastYearOverallSales, medicalSales, lastMedicalSales, lastYearMedicalSales);
        response.setMedicalInfo(medical);
        return response;
    }

    /**
     * 获取销售达成率 毛利达成率 毛利率达成率
     * 销售达成率=本月销售额/计划营业额，百分比格式，
     * 毛利达成率=本月毛利额/计划毛利额，百分比格式，
     * 毛利率达成率=本月毛利率/计划毛利率，百分比格式
     */
    private void setSalesPlanData(OverallSales overallSales, OverallSalesPlanData salesPlanData) {
        if (Objects.isNull(salesPlanData)) {
            overallSales.setSalesAmtRate(BigDecimal.ZERO);
            overallSales.setGrossRate(BigDecimal.ZERO);
            overallSales.setGRateR(BigDecimal.ZERO);
        } else {
            //本月销售额
            BigDecimal salesAmt =getAmt(overallSales.getSalesAmt());
            //销售达成率
            overallSales.setSalesAmtRate(getAmtBigDecimal(salesAmt,salesPlanData.getPlanDailyPayAmt()));
            //本月毛利额
            BigDecimal gross =getAmt(overallSales.getGross());
            //本月毛利达成率
            overallSales.setGrossRate(getAmtBigDecimal(gross,salesPlanData.getPlanGrossProfit()));
            //本月毛利率
            BigDecimal gRate = getAmt(overallSales.getGRate().multiply(new BigDecimal("100")));
            //本月毛利达成率
            overallSales.setGRateR(getAmtBigDecimal(gRate,salesPlanData.getPlanGrossMargin()));
        }
    }

    private BigDecimal getAmt(BigDecimal amt) {
        return Objects.isNull(amt) ? BigDecimal.ZERO : amt.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    /**
     * @param amt 除数
     * @param planDailyPayAmt 被除数
     * @return
     */
    private BigDecimal getAmtBigDecimal(BigDecimal amt, BigDecimal planDailyPayAmt) {
        return planDailyPayAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : amt.divide(planDailyPayAmt, 4, BigDecimal.ROUND_HALF_UP);
    }
    @Override
    public BusinessReportResponse listMonthSalesInfo(BusinessReportRequest inData) {
        if (StringUtils.isEmpty(inData.getDate())) {
            throw new BusinessException("请选择查询日期！");
        }
        String thisYear = inData.getDate().substring(0, 4);
        String lastYear = DateUtil.getLastYearCurrentDate(inData.getDate()).substring(0, 4);
        List<MonthData> thisYearList = businessReportMapper.listMonthSalesInfo(thisYear, inData.getClient(), inData.getStoCode());
        List<MonthData> lastYearList = businessReportMapper.listMonthSalesInfo(lastYear, inData.getClient(), inData.getStoCode());
        List<String> allThisYear = getAllYear(thisYear);
        List<String> allLastYear = getAllYear(lastYear);
        List<MonthData> newThisList = getNewList(allThisYear, thisYearList);
        List<MonthData> newLastList = getNewList(allLastYear, lastYearList);
        BusinessReportResponse response = new BusinessReportResponse();
        response.setThisYearList(newThisList);
        response.setLastYearList(newLastList);
        return response;
    }

    private List<MonthData> getNewList(List<String> allThisYear, List<MonthData> thisYearList) {
        List<MonthData> thisYearData = new ArrayList<>();
        for (int i = 0; i < allThisYear.size(); i++) {
            MonthData monthData1 = new MonthData();
            monthData1.setDate((i + 1) + "月");
            if (CollectionUtil.isNotEmpty(thisYearList)) {
                for (MonthData monthData : thisYearList) {
                    if (allThisYear.get(i).equals(monthData.getDate())) {
                        monthData1.setSalesAmt(ObjectUtil.isNotEmpty(monthData.getSalesAmt()) ? monthData.getSalesAmt().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setTransactionQty(ObjectUtil.isNotEmpty(monthData.getTransactionQty()) ? monthData.getTransactionQty().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setGross(ObjectUtil.isNotEmpty(monthData.getGross()) ? monthData.getGross().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setGRate(ObjectUtil.isNotEmpty(monthData.getGRate()) ? monthData.getGRate().multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setSalesDay(ObjectUtil.isNotEmpty(monthData.getSalesDay()) ? monthData.getSalesDay().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setPrice(ObjectUtil.isNotEmpty(monthData.getPrice()) ? monthData.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setSingleTransactionQty(ObjectUtil.isNotEmpty(monthData.getSingleTransactionQty()) ? monthData.getSingleTransactionQty().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setSingleSalesAmt(ObjectUtil.isNotEmpty(monthData.getSingleSalesAmt()) ? monthData.getSingleSalesAmt().setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setVipRate(ObjectUtil.isNotEmpty(monthData.getVipRate()) ? monthData.getVipRate().multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setTransactionQtyRate(ObjectUtil.isNotEmpty(monthData.getTransactionQtyRate()) ? monthData.getTransactionQtyRate().multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
                        monthData1.setMedicalRate(ObjectUtil.isNotEmpty(monthData.getMedicalRate()) ? monthData.getMedicalRate().multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

                        break;
                    }
                }
            }
            thisYearData.add(monthData1);
        }
        return thisYearData;
    }

    private List<String> getAllYear(String thisYear) {
        List<String> allYear = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                allYear.add(thisYear + "0" + i);
            } else {
                allYear.add(thisYear + i);
            }
        }
        return allYear;
    }

    @Override
    public BusinessReportResponse listSingleSalesInfo(BusinessReportRequest inData) {
        if (StringUtils.isEmpty(inData.getDate())) {
            throw new BusinessException("请选择查询日期！");
        }
        BusinessReportResponse response = new BusinessReportResponse();
        String thisYear = inData.getDate().substring(0, 4);
        String lastYear = DateUtil.getLastYearCurrentDate(inData.getDate()).substring(0, 4);
        //今年单店日均
        List<MonthData> thisYearList = getMonthData(inData, thisYear);
        //去年单店日均
        List<MonthData> lastYearList = getMonthData(inData, lastYear);
        List<String> allThisYear = getAllYear(thisYear);
        List<String> allLastYear = getAllYear(lastYear);
        List<MonthData> newThisList = getNewList(allThisYear, thisYearList);
        List<MonthData> newLastList = getNewList(allLastYear, lastYearList);
        response.setThisYearList(newThisList);
        response.setLastYearList(newLastList);
        return response;
    }

    @Override
    public BusinessReportResponse listVipSalesInfo(BusinessReportRequest inData) {
        BusinessReportResponse response = new BusinessReportResponse();
        String thisYear = inData.getDate().substring(0, 4);
        String lastYear = DateUtil.getLastYearCurrentDate(inData.getDate()).substring(0, 4);
        List<MonthData> thisVipYearList = businessReportMapper.listVipSalesInfo(thisYear, inData.getClient(), inData.getStoCode());
        List<MonthData> lastVipYearList = businessReportMapper.listVipSalesInfo(lastYear, inData.getClient(), inData.getStoCode());
        List<String> allThisYear = getAllYear(thisYear);
        List<String> allLastYear = getAllYear(lastYear);
        List<MonthData> newThisList = getNewList(allThisYear, thisVipYearList);
        List<MonthData> newLastList = getNewList(allLastYear, lastVipYearList);
        response.setThisYearList(newThisList);
        response.setLastYearList(newLastList);
        return response;
    }

    @Override
    public BusinessReportResponse listMedicalSalesInfo(BusinessReportRequest inData) {
        BusinessReportResponse response = new BusinessReportResponse();
        String thisYear = inData.getDate().substring(0, 4);
        String lastYear = DateUtil.getLastYearCurrentDate(inData.getDate()).substring(0, 4);
        List<MonthData> thisVipYearList = businessReportMapper.listMedicalSalesInfo(thisYear, inData.getClient(), inData.getStoCode());
        List<MonthData> lastVipYearList = businessReportMapper.listMedicalSalesInfo(lastYear, inData.getClient(), inData.getStoCode());
        List<String> allThisYear = getAllYear(thisYear);
        List<String> allLastYear = getAllYear(lastYear);
        List<MonthData> newThisList = getNewList(allThisYear, thisVipYearList);
        List<MonthData> newLastList = getNewList(allLastYear, lastVipYearList);
        response.setThisYearList(newThisList);
        response.setLastYearList(newLastList);
        return response;
    }

    @Override
    public BusinessReportResponse listMonthSalesInfoByKL(BusinessReportRequest inData) {
        BusinessReportResponse response = getBusinessReportResponse(inData);
        return response;
    }

    private BusinessReportResponse getBusinessReportResponse(BusinessReportRequest inData) {
        String thisYear = inData.getDate().substring(0, 4);
        String lastYear = DateUtil.getLastYearCurrentDate(inData.getDate()).substring(0, 4);
        String thisStartDate = thisYear + "-01-01";
        String thisEndDate = thisYear + "-12-31";
        String lastStartDate = lastYear + "-01-01";
        String lastEndDate = lastYear + "-12-31";
        List<MonthData> thisYearDate = new ArrayList<>();
        List<MonthData> lastYearDate = new ArrayList<>();

        getSum(inData, thisYear, thisStartDate, thisEndDate, thisYearDate);
        getSum(inData, lastYear, lastStartDate, lastEndDate, lastYearDate);
        BusinessReportResponse response = new BusinessReportResponse();
        response.setLastYearList(lastYearDate);
        response.setThisYearList(thisYearDate);
        return response;
    }

    @Override
    public BusinessReportResponse listSingleSalesInfoByKL(BusinessReportRequest inData) {
        BusinessReportResponse response = getBusinessReportResponse(inData);
        //今年销售额
        List<MonthData> thisYearList = response.getThisYearList();
        //去年销售额
        List<MonthData> lastYearList = response.getLastYearList();
        String thisYear = inData.getDate().substring(0, 4);
        String lastYear = DateUtil.getLastYearCurrentDate(inData.getDate()).substring(0, 4);
        List<MonthData> thisYearSalesDayList = businessReportMapper.listSalesDay(thisYear, inData.getClient(), inData.getStoCode());
        List<MonthData> lastYearSalesDayList = businessReportMapper.listSalesDay(lastYear, inData.getClient(), inData.getStoCode());
        setSingle(thisYearList, thisYearSalesDayList);
        setSingle(lastYearList, lastYearSalesDayList);
        response.setThisYearList(thisYearList);
        response.setLastYearList(lastYearList);
        return response;
    }

    @Override
    public SalesReportInfo listCompanyWeeklySalesReport(BusinessReportRequest inData) {
        SalesReportInfo res = new SalesReportInfo();
        //客户类型
        if (CollectionUtil.isNotEmpty(inData.getFrancTypeList())) {
            List<String> francTypeList = inData.getFrancTypeList();
            for (String francType : francTypeList) {
                if ("1".equals(francType)) {
                    inData.setFrancType1("1");
                }
                if ("2".equals(francType)) {
                    inData.setFrancType2("1");
                }
                if ("3".equals(francType)) {
                    inData.setFrancType3("1");
                }
            }

        }
        //成分分类
        if (CollectionUtil.isNotEmpty(inData.getProCompCodeList())) {
            List<List<String>> proCompCodeList = inData.getProCompCodeList();
            String proCompCode = "'";
            for (int i = 0; i < proCompCodeList.size(); i++) {
                if (i == proCompCodeList.size() - 1) {
                    proCompCode = proCompCode + proCompCodeList.get(i).get(3) + "'";
                } else {
                    proCompCode = proCompCode + proCompCodeList.get(i).get(3) + "','";
                }
            }
            inData.setProCompCode(proCompCode);
        }
        //客户
        if (CollectionUtil.isNotEmpty(inData.getClientList())) {
            List<String> clientList = inData.getClientList();
            String clientStr = "'";
            for (int i = 0; i < clientList.size(); i++) {
                if (i == clientList.size() - 1) {
                    clientStr = clientStr + clientList.get(i) + "'";
                } else {
                    clientStr = clientStr + clientList.get(i) + "','";
                }
            }
            inData.setClientStr(clientStr);
        }
        //门店属性
        if (CollectionUtil.isNotEmpty(inData.getStoAttributeList())) {
            List<String> stoAttributeList = inData.getStoAttributeList();
            String stoAttributeStr = "'";
            for (int i = 0; i < stoAttributeList.size(); i++) {
                if (i == stoAttributeList.size() - 1) {
                    stoAttributeStr = stoAttributeStr + stoAttributeList.get(i) + "'";
                } else {
                    stoAttributeStr = stoAttributeStr + stoAttributeList.get(i) + "','";
                }
            }
            inData.setStoAttribute(stoAttributeStr);
        }
        //店型
        if (CollectionUtil.isNotEmpty(inData.getGsstVersionList())) {
            List<String> gsstVersionList = inData.getGsstVersionList();
            String versionStr = "'";
            for (int i = 0; i < gsstVersionList.size(); i++) {
                if (i == gsstVersionList.size() - 1) {
                    versionStr = versionStr + gsstVersionList.get(i) + "'";
                } else {
                    versionStr = versionStr + gsstVersionList.get(i) + "','";
                }
            }
            inData.setGsstVersionStr(versionStr);
        }
        HashMap<String, String> thisParam = new HashMap<>();
        HashMap<String, String> lastParam = new HashMap<>();
        HashMap<String, String> lastYearParam = new HashMap<>();
        if ("week".equals(inData.getWeekOrMonth())) {
            Integer[] yearList = {inData.getStartYear(), inData.getEndYear()};
            Integer[] weekList = {inData.getStartWeek(), inData.getEndWeek()};
            thisParam = businessReportMapper.getStartTimeAndEndTime(Arrays.asList(yearList), Arrays.asList(weekList), null);
            Integer lastEndWeek = inData.getEndWeek() - 1;
            Integer lastStartWeek = inData.getStartWeek() - 1;
            List<Integer> yearListHB = new ArrayList<>();
            List<Integer> weekListHB = new ArrayList<>();
            //计算环比的开始，结束时间。
            if (0 == lastEndWeek) {
                WeeklySalesReportInfo param = new WeeklySalesReportInfo();
                int y = inData.getEndYear() - 1;
                param.setY(String.valueOf(y));
                HashMap<String, Integer> map = businessReportMapper.getMaxWeek(param);
                yearListHB.add(y);
                weekListHB.add(map.get("w"));
            } else {
                yearListHB.add(inData.getEndYear());
                weekListHB.add(lastEndWeek);
            }
            if (0 == lastStartWeek) {
                WeeklySalesReportInfo param = new WeeklySalesReportInfo();
                int y = inData.getStartYear() - 1;
                param.setY(String.valueOf(y));
                HashMap<String, Integer> map = businessReportMapper.getMaxWeek(param);
                yearListHB.add(y);
                weekListHB.add(map.get("w"));
            } else {
                yearListHB.add(inData.getStartYear());
                weekListHB.add(lastStartWeek);
            }
            lastParam = businessReportMapper.getStartTimeAndEndTime(yearListHB, weekListHB, null);
            //计算同比的开始，结束时间。
            Integer[] yearListTB = {inData.getEndYear() - 1, inData.getStartYear() - 1};
            Integer[] weekListTB = {inData.getStartWeek(), inData.getEndWeek()};
            lastYearParam = businessReportMapper.getStartTimeAndEndTime(Arrays.asList(yearListTB), Arrays.asList(weekListTB), null);
        } else {
            Integer[] yearList = {inData.getStartYear(), inData.getEndYear()};
            Integer[] monthList = {inData.getStartMonth(), inData.getEndMonth()};
            thisParam = businessReportMapper.getStartTimeAndEndTime(Arrays.asList(yearList), null, Arrays.asList(monthList));
            int lastEndMonth = inData.getEndMonth() - 1;
            int lastStartMonth = inData.getStartMonth() - 1;
            List<Integer> yearListHB = new ArrayList<>();
            List<Integer> monthListHB = new ArrayList<>();
            //计算环比的开始，结束时间。
            if (0 == lastEndMonth) {
                int y = inData.getEndYear() - 1;
                yearListHB.add(y);
                monthListHB.add(12);
            } else {
                yearListHB.add(inData.getEndYear());
                monthListHB.add(lastEndMonth);
            }
            if (0 == lastStartMonth) {
                int y = inData.getStartYear() - 1;
                yearListHB.add(y);
                monthListHB.add(12);
            } else {
                yearListHB.add(inData.getStartYear());
                if (lastStartMonth < 10) {
                    monthListHB.add(lastStartMonth);
                } else {
                    monthListHB.add(lastStartMonth);
                }
            }
            lastParam = businessReportMapper.getStartTimeAndEndTime(yearListHB, null, monthListHB);
            //计算同比的开始，结束时间。
            Integer[] yearListTB = {inData.getEndYear() - 1, inData.getStartYear() - 1};
            Integer[] monthListTB = {inData.getStartMonth(), inData.getEndMonth()};
            lastYearParam = businessReportMapper.getStartTimeAndEndTime(Arrays.asList(yearListTB), null, Arrays.asList(monthListTB));

        }
        switch (inData.getReportType()) {
            //公司销售周报
            case "ENT":
                //本期数据
                inData.setStartDate(thisParam.get("startDate"));
                inData.setEndDate(thisParam.get("endDate"));
                List<WeeklySalesReportInfo> thisReportData = getWeeklyEntSalesReportInfos(inData);
                //上期数据
                inData.setStartDate(lastParam.get("startDate"));
                inData.setEndDate(lastParam.get("endDate"));
                List<WeeklySalesReportInfo> lastReportData = getWeeklyEntSalesReportInfos(inData);
                //同期数据
                inData.setStartDate(lastYearParam.get("startDate"));
                inData.setEndDate(lastYearParam.get("endDate"));
                List<WeeklySalesReportInfo> lastYearReportData = getWeeklyEntSalesReportInfos(inData);
                //组装数据

                //周报表
                if ("week".equals(inData.getWeekOrMonth())) {
                    //公司维度
                    if ("1".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String week = thisReportDatum.getW();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastWeek = Integer.valueOf(week) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(week) - 1) : String.valueOf(Integer.valueOf(week) - 1);
                            thisReportDatum.setWeekNumber(year + week);
                            //关联毛利率
                            thisReportDatum.setAssociateGrossRate(divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getAssociateSalesAmt())).doubleValue());
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("1".equals(thisReportDatum.getW())) {
                                    HashMap<String, Integer> map = businessReportMapper.getMaxWeek(thisReportDatum);
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && map.get("w").toString().equals(lastReportDatum.getW()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastWeek.equals(lastReportDatum.getW()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && week.equals(lastYearReportDatum.getW()) && lastYearReportDatum.getClientId().equals(thisReportDatum.getClientId())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }

                    }
                    //门店维度
                    if ("2".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String week = thisReportDatum.getW();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastWeek = Integer.valueOf(week) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(week) - 1) : String.valueOf(Integer.valueOf(week) - 1);
                            thisReportDatum.setWeekNumber(year + week);
                            //关联毛利率
                            thisReportDatum.setAssociateGrossRate(divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getAssociateSalesAmt())).doubleValue());
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("1".equals(thisReportDatum.getW())) {
                                    HashMap<String, Integer> map = businessReportMapper.getMaxWeek(thisReportDatum);
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && map.get("w").toString().equals(lastReportDatum.getW()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId()) && lastReportDatum.getGssdBrId().equals(thisReportDatum.getGssdBrId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastWeek.equals(lastReportDatum.getW()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId()) && lastReportDatum.getGssdBrId().equals(thisReportDatum.getGssdBrId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && week.equals(lastYearReportDatum.getW()) && lastYearReportDatum.getClientId().equals(thisReportDatum.getClientId()) && lastYearReportDatum.getGssdBrId().equals(thisReportDatum.getGssdBrId())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }

                    }
                    //省级维度
                    if ("3".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String week = thisReportDatum.getW();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastWeek = Integer.valueOf(week) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(week) - 1) : String.valueOf(Integer.valueOf(week) - 1);
                            thisReportDatum.setWeekNumber(year + week);
                            //关联毛利率
                            thisReportDatum.setAssociateGrossRate(divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getAssociateSalesAmt())).doubleValue());
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("1".equals(thisReportDatum.getW())) {
                                    HashMap<String, Integer> map = businessReportMapper.getMaxWeek(thisReportDatum);
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && map.get("w").toString().equals(lastReportDatum.getW()) && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastWeek.equals(lastReportDatum.getW()) && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && week.equals(lastYearReportDatum.getW()) && lastYearReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastYearReportDatum.getProvinceName())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }
                    }
                    //市级维度
                    if ("4".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String week = thisReportDatum.getW();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastWeek = Integer.valueOf(week) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(week) - 1) : String.valueOf(Integer.valueOf(week) - 1);
                            thisReportDatum.setWeekNumber(year + week);
                            //关联毛利率
                            thisReportDatum.setAssociateGrossRate(divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getAssociateSalesAmt())).doubleValue());
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("1".equals(thisReportDatum.getW())) {
                                    HashMap<String, Integer> map = businessReportMapper.getMaxWeek(thisReportDatum);
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && map.get("w").toString().equals(lastReportDatum.getW())
                                            && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())
                                            && lastReportDatum.getCity().equals(thisReportDatum.getCity()) && lastReportDatum.getCityName().equals(thisReportDatum.getCityName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastWeek.equals(lastReportDatum.getW())
                                            && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())
                                            && lastReportDatum.getCity().equals(thisReportDatum.getCity()) && lastReportDatum.getCityName().equals(thisReportDatum.getCityName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && week.equals(lastYearReportDatum.getW()) && lastYearReportDatum.getProvince().equals(thisReportDatum.getProvince())
                                        && thisReportDatum.getProvinceName().equals(lastYearReportDatum.getProvinceName()) && lastYearReportDatum.getCity().equals(thisReportDatum.getCity())
                                        && lastYearReportDatum.getCityName().equals(thisReportDatum.getCityName())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }
                    }

                } else {
                    //月度报表
                    //公司维度
                    if ("1".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String month = thisReportDatum.getM();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            Integer lastMonth = Integer.valueOf(month) - 1;
                            thisReportDatum.setWeekNumber(year + month);
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("0".equals(lastMonth)) {
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && "12".equals(lastReportDatum.getM()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastMonth == Integer.valueOf(lastReportDatum.getM()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && month.equals(lastYearReportDatum.getM()) && lastYearReportDatum.getClientId().equals(thisReportDatum.getClientId())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }

                    }
                    //门店维度
                    if ("2".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String month = thisReportDatum.getM();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastMonth = Integer.valueOf(month) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(month) - 1) : String.valueOf(Integer.valueOf(month) - 1);
                            thisReportDatum.setWeekNumber(year + month);
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("0".equals(lastMonth)) {
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && "12".equals(lastReportDatum.getM()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId()) && lastReportDatum.getGssdBrId().equals(thisReportDatum.getGssdBrId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastMonth.equals(lastReportDatum.getM()) && lastReportDatum.getClientId().equals(thisReportDatum.getClientId()) && lastReportDatum.getGssdBrId().equals(thisReportDatum.getGssdBrId())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && lastMonth.equals(lastYearReportDatum.getM()) && lastYearReportDatum.getClientId().equals(thisReportDatum.getClientId()) && lastYearReportDatum.getGssdBrId().equals(thisReportDatum.getGssdBrId())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }

                    }
                    //省级维度
                    if ("3".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String month = thisReportDatum.getM();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastMonth = Integer.valueOf(month) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(month) - 1) : String.valueOf(Integer.valueOf(month) - 1);
                            thisReportDatum.setWeekNumber(year + month);
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("0".equals(lastMonth)) {
                                    HashMap<String, Integer> map = businessReportMapper.getMaxWeek(thisReportDatum);
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && "12".equals(lastReportDatum.getM()) && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastMonth.equals(lastReportDatum.getM()) && thisReportDatum.getProvince().equals(lastReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && lastMonth.equals(lastYearReportDatum.getM()) && lastYearReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastYearReportDatum.getProvinceName())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }
                    }
                    //市级维度
                    if ("4".equals(inData.getDimension())) {
                        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
                            String year = thisReportDatum.getY();
                            String month = thisReportDatum.getM();
                            String lastYear = String.valueOf(Integer.valueOf(year) - 1);
                            String lastMonth = Integer.valueOf(month) - 1 < 10 ? "0" + String.valueOf(Integer.valueOf(month) - 1) : String.valueOf(Integer.valueOf(month) - 1);
                            thisReportDatum.setWeekNumber(year + month);
                            //医保销售占比
                            thisReportDatum.setMedicalSalesAmtRate(divide(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //毛利率
                            thisReportDatum.setGrossRate(divide(checkNull(thisReportDatum.getGross()), checkNull(thisReportDatum.getSalesAmt())).doubleValue());
                            //关联毛利占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getGross()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateGross())) {
                                Double associateGrossPercentage = divide(checkNull(thisReportDatum.getAssociateGross()), checkNull(thisReportDatum.getGross())).doubleValue();
                                thisReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
                            }
                            //关联销售占比
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(thisReportDatum.getAssociateSalesAmt())) {
                                Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
                                thisReportDatum.setAssociateSalesRate(associateSalesRate);
                            }

                            //关联率
                            if (ObjectUtil.isNotEmpty(thisReportDatum.getTotalQty()) && ObjectUtil.isNotEmpty(thisReportDatum.getSuccessAssociate())) {
                                Double associateRate = divide(checkNull(thisReportDatum.getSuccessAssociate()), checkNull(thisReportDatum.getTotalQty())).doubleValue();
                                thisReportDatum.setAssociateRate(associateRate);
                            }
                            for (WeeklySalesReportInfo lastReportDatum : lastReportData) {
                                //如果是一年的第一周
                                if ("0".equals(lastMonth)) {
                                    HashMap<String, Integer> map = businessReportMapper.getMaxWeek(thisReportDatum);
                                    //计算环比
                                    if (lastYear.equals(lastReportDatum.getY()) && "12".equals(lastReportDatum.getM())
                                            && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())
                                            && lastReportDatum.getCity().equals(thisReportDatum.getCity()) && lastReportDatum.getCityName().equals(thisReportDatum.getCityName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                } else {
                                    //计算环比
                                    if (year.equals(lastReportDatum.getY()) && lastMonth.equals(lastReportDatum.getM())
                                            && lastReportDatum.getProvince().equals(thisReportDatum.getProvince()) && thisReportDatum.getProvinceName().equals(lastReportDatum.getProvinceName())
                                            && lastReportDatum.getCity().equals(thisReportDatum.getCity()) && lastReportDatum.getCityName().equals(thisReportDatum.getCityName())) {
                                        setEntReportHB(thisReportDatum, lastReportDatum);
                                    }
                                }
                            }
                            for (WeeklySalesReportInfo lastYearReportDatum : lastYearReportData) {
                                //计算同比
                                if (lastYear.equals(lastYearReportDatum.getY()) && lastMonth.equals(lastYearReportDatum.getM()) && lastYearReportDatum.getProvince().equals(thisReportDatum.getProvince())
                                        && thisReportDatum.getProvinceName().equals(lastYearReportDatum.getProvinceName()) && lastYearReportDatum.getCity().equals(thisReportDatum.getCity())
                                        && lastYearReportDatum.getCityName().equals(thisReportDatum.getCityName())) {
                                    setEntReportTB(thisReportDatum, lastYearReportDatum);
                                }
                            }
                        }
                    }
                }
                res.setList(thisReportData);
                inData.setStartDate(thisParam.get("startDate"));
                inData.setEndDate(thisParam.get("endDate"));
                setTotal(res, thisReportData, inData);
                return res;
            //品类销售周报
            case "CLASS":
                inData.setStartDate(thisParam.get("startDate"));
                inData.setEndDate(thisParam.get("endDate"));
                List<WeeklySalesReportInfo> thisReportList = getClassReort(inData);
                inData.setStartDate(lastParam.get("startDate"));
                inData.setEndDate(lastParam.get("endDate"));
                List<WeeklySalesReportInfo> lastMonthReportList = getClassReort(inData);
                inData.setStartDate(lastYearParam.get("startDate"));
                inData.setEndDate(lastYearParam.get("endDate"));
                List<WeeklySalesReportInfo> lastYearReportList = getClassReort(inData);
                for (WeeklySalesReportInfo thisReport : thisReportList) {
                    if ("未匹配".equals(thisReport.getProCompBigName())) {
                        thisReport.setProCompBigName("其他");
                    }
                    // grossProfitRate,
                    thisReport.setGrossProfitRate(divide(checkNull(thisReport.getGross()), checkNull(thisReport.getTotalSalesAmt())).doubleValue());
                    // "ROUND(cast(t2.salesGoodsQty as double) /cast(t8.inventoryItemQty as double),2) movableSalesRate,
                    thisReport.setMovableSalesRate(divide(checkNull(thisReport.getSalesGoodsQty()),checkNull(thisReport.getInventoryItemQty())).doubleValue());
                    //ROUND(cast(t2.totalBillCount as double) / cast(t6.totalSalesDay as double),0) averageDailyTransactionQty,
                    thisReport.setAverageDailyTransactionQty(divide(checkNull(thisReport.getTotalBillCount()),checkNull(thisReport.getTotalSalesDay())).doubleValue());
                    //ROUND(t2.salesAmt / t3.totalSalesAmt,6) salesRate
                    thisReport.setSalesRate(divide(checkNull(thisReport.getSalesAmt()),checkNull(thisReport.getTotalSalesAmt())).doubleValue());
                    //ROUND(( t2.salesQty / t2.totalBillCount ),6) customerQty
                    thisReport.setCustomerQty(divide(checkNull(thisReport.getSalesQty()),checkNull(thisReport.getTotalBillCount())).doubleValue());
                    //
                    thisReport.setCustomerItemQty(divide(checkNull(thisReport.getSalesItemQty()), checkNull(thisReport.getTotalBillCount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

                    thisReport.setGrossRate(divide(checkNull(thisReport.getGross()), checkNull(thisReport.getSalesAmt())).doubleValue());
                    //日均销售额
                    thisReport.setAverageDailySalesAmt(divide(checkNull(thisReport.getSalesAmt()), checkNull(thisReport.getTotalSalesDay())).doubleValue());
                    //日均毛利额
                    thisReport.setAverageDailyGross(divide(checkNull(thisReport.getGross()), checkNull(thisReport.getTotalSalesDay())).doubleValue());
                    //周转天数
                    thisReport.setTurnoverDays(divide(checkNull(thisReport.getInventoryAmt()).multiply(checkNull(thisReport.getSalesDayQty())), checkNull(thisReport.getSalesAmt()).subtract(checkNull(thisReport.getGross()))).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //
                    thisReport.setSingleAveragePrice(divide(thisReport.getSalesAmt(), thisReport.getSalesQty()).doubleValue());
                    //计算环比
                    for (WeeklySalesReportInfo lastMonth : lastMonthReportList) {
                        lastMonth.setSingleAveragePrice(divide(lastMonth.getSalesAmt(), lastMonth.getSalesQty()).doubleValue());

                        lastMonth.setGrossProfitRate(divide(checkNull(lastMonth.getGross()),checkNull(lastMonth.getTotalSalesAmt())).doubleValue());

                        lastMonth.setMovableSalesRate(divide(checkNull(lastMonth.getSalesGoodsQty()),checkNull(lastMonth.getInventoryItemQty())).doubleValue());

                        lastMonth.setAverageDailyTransactionQty(divide(checkNull(lastMonth.getTotalBillCount()),checkNull(lastMonth.getTotalSalesDay())).doubleValue());

                        lastMonth.setSalesRate(divide(checkNull(lastMonth.getSalesAmt()),checkNull(lastMonth.getTotalSalesAmt())).doubleValue());

                        lastMonth.setCustomerQty(divide(checkNull(lastMonth.getSalesQty()),checkNull(lastMonth.getTotalBillCount())).doubleValue());

                        lastMonth.setCustomerItemQty(divide(checkNull(lastMonth.getSalesItemQty()), checkNull(lastMonth.getTotalBillCount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                        //上期日均销售额
                        lastMonth.setAverageDailySalesAmt(divide(checkNull(lastMonth.getSalesAmt()), checkNull(lastMonth.getTotalSalesDay())).doubleValue());
                        //上期日均毛利额
                        lastMonth.setAverageDailyGross(divide(checkNull(lastMonth.getGross()), checkNull(lastMonth.getTotalSalesDay())).doubleValue());
                        //上期 周转天数
                        lastMonth.setTurnoverDays(divide(checkNull(thisReport.getInventoryAmt()).multiply(checkNull(thisReport.getSalesDayQty())), checkNull(thisReport.getSalesAmt()).subtract(checkNull(thisReport.getGross()))).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
                        if (thisReport.getClientId().equals(lastMonth.getClientId()) && thisReport.getProCompBigCode().equals(lastMonth.getProCompBigCode())) {
                            lastMonth.setGrossRate(divide(checkNull(lastMonth.getGross()), checkNull(lastMonth.getSalesAmt())).doubleValue());
                            //动销门店数环比
                            BigDecimal salesStoresQtyHB = subtract(checkNull(thisReport.getSalesStoresQty()), checkNull(lastMonth.getSalesStoresQty()));
                            thisReport.setSalesStoresQtyHB(salesStoresQtyHB.doubleValue());
                            //日均销售额环比
                            BigDecimal averageDailySalesAmtHB = subtract(checkNull(thisReport.getAverageDailySalesAmt()), checkNull(lastMonth.getAverageDailySalesAmt()));
                            thisReport.setAverageDailySalesAmtHB(averageDailySalesAmtHB.doubleValue());
                            //日均毛利额同比
                            BigDecimal averageDailyGrossHB = subtract(checkNull(thisReport.getAverageDailyGross()), checkNull(lastMonth.getAverageDailyGross()));
                            thisReport.setAverageDailyGrossHB(averageDailyGrossHB.doubleValue());
                            //动销天数环比
                            BigDecimal salesDayQtyHB = subtract(checkNull(thisReport.getSalesDayQty()), checkNull(lastMonth.getSalesDayQty()));
                            thisReport.setSalesDayQtyHB(salesDayQtyHB.doubleValue());
                            //动销品项环比
                            BigDecimal salesGoodsQtyHB = subtract(checkNull(thisReport.getSalesGoodsQty()), checkNull(lastMonth.getSalesGoodsQty()));
                            thisReport.setSalesGoodsQtyHB(salesGoodsQtyHB.doubleValue());
                            //销量环比
                            BigDecimal salesQtyHB = subtract(checkNull(thisReport.getSalesQty()), checkNull(lastMonth.getSalesQty()));
                            thisReport.setSalesQtyHB(salesQtyHB.doubleValue());
                            //销售额环比
                            BigDecimal salesAmtHB = subtract(checkNull(thisReport.getSalesAmt()), checkNull(lastMonth.getSalesAmt()));
                            thisReport.setSalesAmtHB(salesAmtHB.doubleValue());
                            //销售额环比率
                            BigDecimal salesAmtHBRate = chainComparison(checkNull(thisReport.getSalesAmt()), checkNull(lastMonth.getSalesAmt()));
                            thisReport.setSalesAmtHBRate(salesAmtHBRate.doubleValue());
                            //销售占比环比
                            BigDecimal salesRateHB = subtract(checkNull(thisReport.getSalesRate()), checkNull(lastMonth.getSalesRate()));
                            thisReport.setSalesRateHB(salesRateHB.doubleValue());
                            //毛利额环比
                            BigDecimal grossHB = subtract(checkNull(thisReport.getGross()), checkNull(lastMonth.getGross()));
                            thisReport.setGrossHB(grossHB.doubleValue());
                            //毛利额环比率
                            BigDecimal grossHBRate = chainComparison(checkNull(thisReport.getGross()), checkNull(lastMonth.getGross()));
                            thisReport.setGrossHBRate(grossHBRate.doubleValue());
                            //毛利率环比
                            BigDecimal grossRateHB = subtract(checkNull(thisReport.getGrossRate()), checkNull(lastMonth.getGrossRate()));
                            thisReport.setGrossRateHB(grossRateHB.doubleValue());
                            //毛利贡献率环比
                            BigDecimal grossProfitRateHB = subtract(checkNull(thisReport.getGrossProfitRate()), checkNull(lastMonth.getGrossProfitRate()));
                            thisReport.setGrossProfitRateHB(grossProfitRateHB.doubleValue());
                            //总交易次数环比
                            BigDecimal totalBillCountHB = subtract(checkNull(thisReport.getTotalBillCount()), checkNull(lastMonth.getTotalBillCount()));
                            thisReport.setTotalBillCountHB(totalBillCountHB.doubleValue());
                            //日均交易次数环比
                            BigDecimal averageDailyTransactionQtyHB = subtract(checkNull(thisReport.getAverageDailyTransactionQty()), checkNull(lastMonth.getAverageDailyTransactionQty()));
                            thisReport.setAverageDailyTransactionQtyHB(averageDailyTransactionQtyHB.doubleValue());
                            //客单价环比
                            BigDecimal customerPriceHB = subtract(checkNull(thisReport.getCustomerPrice()), checkNull(lastMonth.getCustomerPrice()));
                            thisReport.setCustomerPriceHB(customerPriceHB.doubleValue());
                            //单品平均销售价环比
                            BigDecimal singleAveragePriceHB = subtract(checkNull(thisReport.getSingleAveragePrice()), checkNull(lastMonth.getSingleAveragePrice()));
                            thisReport.setSingleAveragePriceHB(singleAveragePriceHB.doubleValue());
                            //客单品项数环比
                            BigDecimal customerItemQtyHB = subtract(checkNull(thisReport.getCustomerItemQty()), checkNull(lastMonth.getCustomerItemQty()));
                            thisReport.setCustomerItemQtyHB(customerItemQtyHB.doubleValue());
                            //客单销量环比
                            BigDecimal customerQtyHB = subtract(checkNull(thisReport.getCustomerQty()), checkNull(lastMonth.getCustomerQty()));
                            thisReport.setCustomerQtyHB(customerQtyHB.doubleValue());
                            //总销售天数环比
                            BigDecimal totalSalesDayHB = subtract(checkNull(thisReport.getTotalSalesDay()), checkNull(lastMonth.getTotalSalesDay()));
                            thisReport.setTotalSalesDayHB(totalSalesDayHB.doubleValue());
                        }
                    }

                    //计算同比
                    for (WeeklySalesReportInfo lastYear : lastYearReportList) {
                        lastYear.setSingleAveragePrice(divide(lastYear.getSalesAmt(), lastYear.getSalesQty()).doubleValue());

                        lastYear.setGrossProfitRate(divide(checkNull(lastYear.getGross()),checkNull(lastYear.getTotalSalesAmt())).doubleValue());

                        lastYear.setMovableSalesRate(divide(checkNull(lastYear.getSalesGoodsQty()),checkNull(lastYear.getInventoryItemQty())).doubleValue());

                        lastYear.setAverageDailyTransactionQty(divide(checkNull(lastYear.getTotalBillCount()),checkNull(lastYear.getTotalSalesDay())).doubleValue());

                        lastYear.setSalesRate(divide(checkNull(lastYear.getSalesAmt()),checkNull(lastYear.getTotalSalesAmt())).doubleValue());

                        lastYear.setCustomerQty(divide(checkNull(lastYear.getSalesQty()),checkNull(lastYear.getTotalBillCount())).doubleValue());

                        lastYear.setCustomerItemQty(divide(checkNull(lastYear.getSalesItemQty()), checkNull(lastYear.getTotalBillCount())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                        //同期日均销售额
                        lastYear.setAverageDailySalesAmt(divide(checkNull(lastYear.getSalesAmt()), checkNull(lastYear.getTotalSalesDay())).doubleValue());
                        //同期日均毛利额
                        lastYear.setAverageDailyGross(divide(checkNull(lastYear.getGross()), checkNull(lastYear.getTotalSalesDay())).doubleValue());
                        //同期周转天数
                        lastYear.setTurnoverDays(divide(checkNull(lastYear.getInventoryAmt()).multiply(checkNull(lastYear.getSalesDayQty())), checkNull(lastYear.getSalesAmt()).subtract(checkNull(lastYear.getGross()))).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
                        if (thisReport.getClientId().equals(lastYear.getClientId()) && thisReport.getProCompBigCode().equals(lastYear.getProCompBigCode())) {
                            lastYear.setGrossRate(divide(checkNull(lastYear.getGross()), checkNull(lastYear.getSalesAmt())).doubleValue());
                            //动销门店数同比
                            BigDecimal salesStoresQtyTB = subtract(checkNull(thisReport.getSalesStoresQty()), checkNull(lastYear.getSalesStoresQty()));
                            thisReport.setSalesStoresQtyTB(salesStoresQtyTB.doubleValue());
                            //日均销售额同比
                            BigDecimal averageDailySalesAmtTB = subtract(checkNull(thisReport.getAverageDailySalesAmt()), checkNull(lastYear.getAverageDailySalesAmt()));
                            thisReport.setAverageDailySalesAmtTB(averageDailySalesAmtTB.doubleValue());
                            //日均毛利额同比
                            BigDecimal averageDailyGrossTB = subtract(checkNull(thisReport.getAverageDailyGross()), checkNull(lastYear.getAverageDailyGross()));
                            thisReport.setAverageDailyGrossTB(averageDailyGrossTB.doubleValue());
                            //动销天数环比
                            BigDecimal salesDayQtyTB = subtract(checkNull(thisReport.getSalesDayQty()), checkNull(lastYear.getSalesDayQty()));
                            thisReport.setSalesDayQtyTB(salesDayQtyTB.doubleValue());
                            //动销品项环比
                            BigDecimal salesGoodsQtyTB = subtract(checkNull(thisReport.getSalesGoodsQty()), checkNull(lastYear.getSalesGoodsQty()));
                            thisReport.setSalesGoodsQtyTB(salesGoodsQtyTB.doubleValue());
                            //销量环比
                            BigDecimal salesQtyTB = subtract(checkNull(thisReport.getSalesQty()), checkNull(lastYear.getSalesQty()));
                            thisReport.setSalesQtyTB(salesQtyTB.doubleValue());
                            //销售额环比
                            BigDecimal salesAmtTB = subtract(checkNull(thisReport.getSalesAmt()), checkNull(lastYear.getSalesAmt()));
                            thisReport.setSalesAmtTB(salesAmtTB.doubleValue());
                            //销售额环比率
                            BigDecimal salesAmtTBRate = chainComparison(checkNull(thisReport.getSalesAmt()), checkNull(lastYear.getSalesAmt()));
                            thisReport.setSalesAmtTBRate(salesAmtTBRate.doubleValue());
                            //销售占比环比
                            BigDecimal salesRateTB = subtract(checkNull(thisReport.getSalesRate()), checkNull(lastYear.getSalesRate()));
                            thisReport.setSalesRateTB(salesRateTB.doubleValue());
                            //毛利额环比
                            BigDecimal grossTB = subtract(checkNull(thisReport.getGross()), checkNull(lastYear.getGross()));
                            thisReport.setGrossTB(grossTB.doubleValue());
                            //毛利额环比率
                            BigDecimal grossTBRate = chainComparison(checkNull(thisReport.getGross()), checkNull(lastYear.getGross()));
                            thisReport.setGrossTBRate(grossTBRate.doubleValue());
                            //毛利率环比
                            BigDecimal grossRateTB = subtract(checkNull(thisReport.getGrossRate()), checkNull(lastYear.getGrossRate()));
                            thisReport.setGrossRateTB(grossRateTB.doubleValue());
                            //毛利贡献率环比
                            BigDecimal grossProfitRateTB = subtract(checkNull(thisReport.getGrossProfitRate()), checkNull(lastYear.getGrossProfitRate()));
                            thisReport.setGrossProfitRateTB(grossProfitRateTB.doubleValue());
                            //总交易次数环比
                            BigDecimal totalBillCountTB = subtract(checkNull(thisReport.getTotalBillCount()), checkNull(lastYear.getTotalBillCount()));
                            thisReport.setTotalBillCountTB(totalBillCountTB.doubleValue());
                            //日均交易次数环比
                            BigDecimal averageDailyTransactionQtyTB = subtract(checkNull(thisReport.getAverageDailyTransactionQty()), checkNull(lastYear.getAverageDailyTransactionQty()));
                            thisReport.setAverageDailyTransactionQtyTB(averageDailyTransactionQtyTB.doubleValue());
                            //客单价环比
                            BigDecimal customerPriceTB = subtract(checkNull(thisReport.getCustomerPrice()), checkNull(lastYear.getCustomerPrice()));
                            thisReport.setCustomerPriceTB(customerPriceTB.doubleValue());
                            //单品平均销售价环比
                            BigDecimal singleAveragePriceTB = subtract(checkNull(thisReport.getSingleAveragePrice()), checkNull(lastYear.getSingleAveragePrice()));
                            thisReport.setSingleAveragePriceTB(singleAveragePriceTB.doubleValue());
                            //客单品项数环比
                            BigDecimal customerItemQtyTB = subtract(checkNull(thisReport.getCustomerItemQty()), checkNull(lastYear.getCustomerItemQty()));
                            thisReport.setCustomerItemQtyTB(customerItemQtyTB.doubleValue());
                            //客单销量环比
                            BigDecimal customerQtyTB = subtract(checkNull(thisReport.getCustomerQty()), checkNull(lastYear.getCustomerQty()));
                            thisReport.setCustomerQtyTB(customerQtyTB.doubleValue());
                            //总销售天数环比
                            BigDecimal totalSalesDayTB = subtract(checkNull(thisReport.getTotalSalesDay()), checkNull(lastYear.getTotalSalesDay()));
                            thisReport.setTotalSalesDayTB(totalSalesDayTB.doubleValue());
                        }
                    }
                }
                res.setList(thisReportList);
                //汇总
                inData.setStartDate(thisParam.get("startDate"));
                inData.setEndDate(thisParam.get("endDate"));
                setTotal(res, thisReportList, inData);
                return res;

        }
        return res;
    }

    private void setTotal(SalesReportInfo res, List<WeeklySalesReportInfo> thisReportData, BusinessReportRequest inData) {
        StringBuilder head = new StringBuilder("select count(1) salesStoresQty from ( SELECT\n" +
                " a.CLIENT,\n" +
                " a.GSSD_BR_ID\n" +
                "\n" +
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
                "WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'  ");
        StringBuilder h1 = new StringBuilder("GROUP BY\n" +
                " a.CLIENT,\n" +
                " d.STO_NAME,\n" +
                " c.FRANC_NAME,\n" +
                " a.GSSD_BR_ID\n" +
                " ) aa ");
        StringBuilder head2 = new StringBuilder("select count(1) salesDayQty from ( SELECT\n" +
                "a.GSSD_DATE\n" +
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
                "WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'  ");
        StringBuilder h2 = new StringBuilder("GROUP BY\n" +
                "a.GSSD_DATE\n" +
                " ) aa ");
        StringBuilder head3 = new StringBuilder("select count(1) totalSalesDay  from ( SELECT\n" +
                " a.CLIENT,a.GSSD_DATE\n" +
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
                "WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'  ");
        StringBuilder h3 = new StringBuilder("GROUP BY\n" +
                "a.GSSD_DATE,a.CLIENT,a.GSSD_BR_ID)aa ");

        //查询条件 成分大类
        if (StringUtils.isNotBlank(inData.getProCompBigCode())) {
            head.append(" and g.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            head2.append(" and g.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            head3.append(" and g.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
        }
        //查询条件 成分中类
        if (StringUtils.isNotBlank(inData.getProCompMidCode())) {
            head.append(" and g.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            head2.append(" and g.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            head3.append(" and g.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
        }
        //查询条件 成分小类
        if (StringUtils.isNotBlank(inData.getProCompLitCode())) {
            head.append(" and g.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            head2.append(" and g.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            head3.append(" and g.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
        }
        //查询条件 成分分类
        if (StringUtils.isNotBlank(inData.getProCompCode())) {
            head.append(" and g.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            head2.append(" and g.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            head3.append(" and g.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
        }
        //查询条件 加盟商
        if (StringUtils.isNotBlank(inData.getClientStr())) {
            head.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            head2.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            head3.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
        }
        //查询条件 单体店
        if (StringUtils.isNotBlank(inData.getFrancType1()) ) {
            head.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            head2.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            head3.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            //查询条件 连锁公司
            if (StringUtils.isNotBlank(inData.getFrancType2())) {
                head.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                head2.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                head3.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
            }
            //查询条件 批发公司
            if (StringUtils.isNotBlank(inData.getFrancType3())) {
                head.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                head2.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                head3.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
            }

            head.append(" ) " );
            head2.append(" ) " );
            head3.append(" ) " );
        } else if (StringUtils.isBlank(inData.getFrancType1()) && StringUtils.isNotBlank(inData.getFrancType2())) {
            //查询条件 连锁公司
            if (StringUtils.isNotBlank(inData.getFrancType2())) {
                head.append(" and (c.FRANC_TYPE2 = " + inData.getFrancType2());
                head2.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                head3.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());

                //查询条件 批发公司
                if (StringUtils.isNotBlank(inData.getFrancType3())) {
                    head.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    head2.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    head3.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                }
            }
            head.append(" ) " );
            head2.append(" ) " );
            head3.append(" ) " );
        } else if (StringUtils.isBlank(inData.getFrancType1()) && StringUtils.isBlank(inData.getFrancType2()) && StringUtils.isNotBlank(inData.getFrancType3())) {
            head.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            head2.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            head3.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
        }


        //查询条件 门店
        if (CollectionUtil.isNotEmpty(inData.getStoCodeList())) {
            List<String> stoCodeList = inData.getStoCodeList();
            head.append(" AND ");
            head2.append(" AND ");
            head3.append(" AND ");
            for (int i = 0; i < stoCodeList.size(); i++) {
                String[] arr = stoCodeList.get(i).split("-");
                if (i == stoCodeList.size() - 1) {
                    head.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    head2.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    head3.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                } else {
                    head.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    head2.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    head3.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                }
            }
        }
        //查询条件 店型
        if (StringUtils.isNotBlank(inData.getGsstVersionStr())) {
            head.append(" and d.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            head2.append(" and d.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            head3.append(" and d.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");

        }
        StringBuilder totalInfo = head.append(h1);
        log.info("totalInfo统计数据：{}", totalInfo.toString());
        List<WeeklySalesReportInfo> reportOutData = kylinJdbcTemplate.query(totalInfo.toString(), RowMapper.getDefault(WeeklySalesReportInfo.class));
        log.info("合计数据返回结果:{}", JSONObject.toJSONString(reportOutData));
        StringBuilder salesDayQtyInfo = head2.append(h2);
        log.info("salesDayQtyInfo统计数据：{}", salesDayQtyInfo.toString());
        List<WeeklySalesReportInfo> salesDayQtyList = kylinJdbcTemplate.query(salesDayQtyInfo.toString(), RowMapper.getDefault(WeeklySalesReportInfo.class));
        log.info("合计数据返回结果:{}", JSONObject.toJSONString(salesDayQtyList));
        StringBuilder totalDayQtyInfo = head3.append(h3);
        log.info("salesDayQtyInfo统计数据：{}", totalDayQtyInfo.toString());
        List<WeeklySalesReportInfo> totalDayQtyList = kylinJdbcTemplate.query(totalDayQtyInfo.toString(), RowMapper.getDefault(WeeklySalesReportInfo.class));
        log.info("合计数据返回结果:{}", JSONObject.toJSONString(totalDayQtyList));
        //汇总
        WeeklySalesReportInfo total = new WeeklySalesReportInfo();
        BigDecimal salesStoresQty = ObjectUtil.isNotEmpty(reportOutData) ? checkNull(reportOutData.get(0).getSalesStoresQty()) : BigDecimal.ZERO;
        BigDecimal salesDayQty = ObjectUtil.isNotEmpty(salesDayQtyList) ? checkNull(salesDayQtyList.get(0).getSalesDayQty()) : BigDecimal.ZERO;
        BigDecimal totalSalesDay = ObjectUtil.isNotEmpty(totalDayQtyList) ? checkNull(totalDayQtyList.get(0).getTotalSalesDay()) : BigDecimal.ZERO;
        BigDecimal salesAmt = BigDecimal.ZERO;
        BigDecimal averageDailySalesAmt = BigDecimal.ZERO;
        BigDecimal gross = BigDecimal.ZERO;
        BigDecimal averageDailyGross = BigDecimal.ZERO;
        BigDecimal grossRate = BigDecimal.ZERO;
        BigDecimal inventoryItemQty = BigDecimal.ZERO;
        BigDecimal salesGoodsQty = BigDecimal.ZERO;
        BigDecimal salesQty = BigDecimal.ZERO;
        BigDecimal salesRate = BigDecimal.ZERO;
        BigDecimal grossProfitRate = BigDecimal.ZERO;
        BigDecimal HQty1 = BigDecimal.ZERO;
        BigDecimal HYGSalesAmt = BigDecimal.ZERO;
        BigDecimal HYGGross = BigDecimal.ZERO;
        BigDecimal inventoryAmt = BigDecimal.ZERO;
        BigDecimal turnoverDays = BigDecimal.ZERO;
        BigDecimal totalBillCount = BigDecimal.ZERO;
        BigDecimal averageDailyTransactionQty = BigDecimal.ZERO;
        BigDecimal customerPrice = BigDecimal.ZERO;
        BigDecimal singleAveragePrice = BigDecimal.ZERO;
        BigDecimal customerQty = BigDecimal.ZERO;
        BigDecimal customerItemQty = BigDecimal.ZERO;
        BigDecimal medicalSalesAmt = BigDecimal.ZERO;
        BigDecimal medicalSalesAmtRate = BigDecimal.ZERO;
        BigDecimal associateSalesAmt = BigDecimal.ZERO;
        BigDecimal manualQty = BigDecimal.ZERO;
        BigDecimal autoQty = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal successQty = BigDecimal.ZERO;
        BigDecimal associateGross = BigDecimal.ZERO;
        BigDecimal successAssociate = BigDecimal.ZERO;
        BigDecimal salesItemQty = BigDecimal.ZERO;
        for (WeeklySalesReportInfo thisReportDatum : thisReportData) {
            //销售额
            salesAmt = salesAmt.add(checkNull(thisReportDatum.getSalesAmt()));
            salesItemQty = salesItemQty.add(checkNull(thisReportDatum.getSalesItemQty()));
            //毛利额
            gross = gross.add(checkNull(thisReportDatum.getGross()));
            //日均毛利
            averageDailyGross = averageDailyGross.add(checkNull(thisReportDatum.getAverageDailyGross()));
            //库存品项
            inventoryItemQty = inventoryItemQty.add(checkNull(thisReportDatum.getInventoryItemQty()));
            //动销品项
            salesGoodsQty = salesGoodsQty.add(checkNull(thisReportDatum.getSalesGoodsQty()));
            //销量
            salesQty = salesQty.add(checkNull(thisReportDatum.getSalesQty()));
            //销售占比
            salesRate = BigDecimal.ONE;
            //缺货品项数
            HQty1 = HQty1.add(checkNull(thisReportDatum.getHQty1()));
            //缺货品项预估销售额
            HYGSalesAmt = HYGSalesAmt.add(checkNull(thisReportDatum.getHYGSalesAmt()));
            //缺货品项预估毛利额
            HYGGross = HYGGross.add(checkNull(thisReportDatum.getHYGGross()));
            //库存成本
            inventoryAmt = inventoryAmt.add(checkNull(thisReportDatum.getInventoryAmt()));
            //总交易次数
            totalBillCount = totalBillCount.add(checkNull(thisReportDatum.getTotalBillCount()));
            //医保销售额
            medicalSalesAmt = medicalSalesAmt.add(checkNull(thisReportDatum.getMedicalSalesAmt()));
            //关联销售额
            associateSalesAmt = associateSalesAmt.add(checkNull(thisReportDatum.getAssociateSalesAmt()));
            //关联手动弹出次数
            manualQty = manualQty.add(checkNull(thisReportDatum.getManualQty()));
            //关联自动弹出次数
            autoQty = autoQty.add(checkNull(thisReportDatum.getAutoQty()));
            //关联总弹出次数
            totalQty = totalQty.add(checkNull(thisReportDatum.getTotalQty()));
            //成功关联次数
            successQty = successQty.add(checkNull(thisReportDatum.getSuccessQty()));
            successAssociate = successAssociate.add(checkNull(thisReportDatum.getSuccessAssociate()));
            //关联毛利额
            associateGross = associateGross.add(checkNull(thisReportDatum.getAssociateGross()));
        }
        //日均销售额
        averageDailySalesAmt = divide(checkNull(salesAmt), checkNull(totalSalesDay));
        //毛利率
        grossRate = divide(checkNull(gross), checkNull(salesAmt));
        //毛利贡献率
        grossProfitRate = divide(checkNull(gross), checkNull(salesAmt));
        //日均交易次数
        averageDailyTransactionQty = divide(checkNull(totalBillCount), checkNull(totalSalesDay));
        //客单价
        customerPrice = divide(checkNull(salesAmt), checkNull(totalBillCount));
        //单品平均销售价
        singleAveragePrice = divide(checkNull(salesAmt), checkNull(salesQty));
        //客单品项数
        customerItemQty = divide(checkNull(salesItemQty), checkNull(totalBillCount));
        //客单销量
        customerQty = divide(checkNull(salesQty), checkNull(totalBillCount));
        //日均毛利额
        averageDailyGross = divide(checkNull(gross), checkNull(totalSalesDay));
        //医保销售占比
        medicalSalesAmtRate = divide(checkNull(medicalSalesAmt), checkNull(salesAmt));


        total.setAssociateGross(associateGross.doubleValue());
        total.setSuccessAssociate(successAssociate.doubleValue());
        total.setSuccessQty(successQty.doubleValue());
        total.setTotalQty(totalQty.doubleValue());
        total.setAutoQty(autoQty.doubleValue());
        total.setMedicalSalesAmt(medicalSalesAmt.doubleValue());
        total.setManualQty(manualQty.doubleValue());
        total.setAssociateSalesAmt(associateSalesAmt.doubleValue());
        total.setAverageDailyGross(averageDailyGross.doubleValue());
        total.setMedicalSalesAmtRate(medicalSalesAmtRate.doubleValue());
        total.setCustomerQty(customerQty.doubleValue());
        total.setCustomerItemQty(customerItemQty.doubleValue());
        total.setSingleAveragePrice(singleAveragePrice.doubleValue());
        total.setCustomerPrice(customerPrice.doubleValue());
        total.setAverageDailyTransactionQty(averageDailyTransactionQty.doubleValue());
        total.setGrossProfitRate(grossProfitRate.doubleValue());
        total.setSalesStoresQty(salesStoresQty.doubleValue());
        total.setSalesDayQty(salesDayQty.doubleValue());
        total.setTotalSalesDay(totalSalesDay.doubleValue());
        total.setSalesAmt(salesAmt.doubleValue());
        total.setGross(gross.doubleValue());
        total.setAverageDailyGross(averageDailyGross.doubleValue());
        total.setAverageDailySalesAmt(averageDailySalesAmt.doubleValue());
        total.setGrossRate(grossRate.doubleValue());
        total.setTotalBillCount(totalBillCount.doubleValue());
        total.setInventoryAmt(inventoryAmt.doubleValue());
        total.setHYGGross(HYGGross.doubleValue());
        total.setHQty1(HQty1.doubleValue());
        total.setSalesRate(salesRate.doubleValue());
        total.setSalesQty(salesQty.doubleValue());
        total.setSalesGoodsQty(salesGoodsQty.doubleValue());
        total.setInventoryItemQty(inventoryItemQty.doubleValue());
        total.setTurnoverDays(divide(checkNull(total.getInventoryAmt()).multiply(checkNull(total.getSalesDayQty())), checkNull(total.getSalesAmt()).subtract(checkNull(total.getGross()))).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setTotal(total);
    }

    @Override
    public List<HashMap<String, String>> listBigCode(BusinessReportRequest inData) {
        return businessReportMapper.listBigCode(inData);
    }

    @Override
    public List<HashMap<String, String>> listClientOrStores(BusinessReportRequest inData) {
        //加盟商类型
        if (ObjectUtil.isNotEmpty(inData.getFrancTypeList())) {
            List<String> francTypeList = inData.getFrancTypeList();
            inData.setFrancTypeFlag("1");
            for (String franceType : francTypeList) {
                if ("1".equals(franceType)) {
                    inData.setFrancType1("1");
                }
                if ("2".equals(franceType)) {
                    inData.setFrancType2("1");
                }
                if ("3".equals(franceType)) {
                    inData.setFrancType3("1");
                }
            }
        }
        return businessReportMapper.listClientOrStores(inData);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public List<GoodsClassResponseDTO> listClassCode(BusinessReportRequest inData) {
        List<GoodsClassResponseDTO> classList = businessReportMapper.getClassList();
        // 第一级
        List<GoodsClassResponseDTO> bigClassList = classList.stream()
                .filter(distinctByKey(GoodsClassResponseDTO::getProCompBigCode))
                .map(item -> {
                    GoodsClassResponseDTO temp = new GoodsClassResponseDTO();
                    temp.setProCompCode(item.getProCompBigCode());
                    temp.setProCompName(item.getProCompBigName());
                    return temp;
                })
                .collect(Collectors.toList());

        // 第二级
        bigClassList.stream().forEach(item -> {
            List<GoodsClassResponseDTO> midClassList = classList.stream()
                    .filter(itemClass -> itemClass.getProCompBigCode().equals(item.getProCompCode()))
                    .filter(distinctByKey(GoodsClassResponseDTO::getProCompMidCode))
                    .map(itemClass -> {
                        GoodsClassResponseDTO temp = new GoodsClassResponseDTO();
                        temp.setProCompCode(itemClass.getProCompMidCode());
                        temp.setProCompName(itemClass.getProCompMidName());
                        return temp;
                    })
                    .collect(Collectors.toList());
            item.setList(midClassList);

            // 第三级
            midClassList.stream().forEach(item1 -> {
                List<GoodsClassResponseDTO> litClassList = classList.stream()
                        .filter(item2 -> item2.getProCompMidCode().equals(item1.getProCompCode()))
                        .filter(distinctByKey(GoodsClassResponseDTO::getProCompLitCode))
                        .map(item2 -> {
                            GoodsClassResponseDTO temp = new GoodsClassResponseDTO();
                            temp.setProCompCode(item2.getProCompLitCode());
                            temp.setProCompName(item2.getProCompLitName());
                            return temp;
                        })
                        .collect(Collectors.toList());
                item1.setList(litClassList);
                //第四级
                litClassList.forEach(item4 -> {
                    List<GoodsClassResponseDTO> lastClassList = classList.stream()
                            .filter(item3 -> item3.getProCompLitCode().equals(item4.getProCompCode()))
                            .map(item3 -> {
                                GoodsClassResponseDTO temp = new GoodsClassResponseDTO();
                                temp.setProCompCode(item3.getProCompCode());
                                temp.setProCompName(item3.getProCompName());
                                return temp;
                            })
                            .collect(Collectors.toList());
                    item4.setList(lastClassList);
                });


            });
        });
        return bigClassList;
    }

    @Override
    public List<HashMap<String, String>> listStoreType(BusinessReportRequest inData) {
        return businessReportMapper.listStoreType();
    }

    @Override
    public Object exportWeeklySalesReport(BusinessReportRequest inData) throws IOException {
        SalesReportInfo salesReportInfo = this.listCompanyWeeklySalesReport(inData);
        List<WeeklySalesReportInfo> outData = salesReportInfo.getList();
        if (CollectionUtil.isNotEmpty(outData)) {
            for (WeeklySalesReportInfo outDatum : outData) {
                if ("month".equals(inData.getWeekOrMonth())) {
                    outDatum.setWeekNumber(outDatum.getY()+"年"+outDatum.getM()+"月");
                } else if ("week".equals(inData.getWeekOrMonth())) {
                    outDatum.setWeekNumber(outDatum.getY()+"年"+outDatum.getW()+"周");
                }
            }
        }
        outData.add(salesReportInfo.getTotal());
        String fileName = "销售周报";
        if (outData.size() > 0) {
            CsvFileInfo csvInfo = null;
            // 导出
            // byte数据
            List<Short> type = new ArrayList<>();
            type.add((short) 0);
            //公司销售周报-公司维度
            if ("1".equals(inData.getDimension()) && "ENT".equals(inData.getReportType())) {
                type.add((short) 6);
                type.add((short) 1);
                type.add((short) 7);
                type.add((short) 8);
                type.add((short) 9);
                csvInfo = CsvClient.getCsvByte(outData, fileName, type);
            } else if ("2".equals(inData.getDimension()) && "ENT".equals(inData.getReportType())) {
                type.add((short) 6);
                type.add((short) 2);
                type.add((short) 7);
                type.add((short) 8);
                csvInfo = CsvClient.getCsvByte(outData, fileName, type);
            } else if ("3".equals(inData.getDimension()) && "ENT".equals(inData.getReportType())) {
                type.add((short) 7);
                type.add((short) 3);
                csvInfo = CsvClient.getCsvByte(outData, fileName, type);
            } else if ("4".equals(inData.getDimension()) && "ENT".equals(inData.getReportType())) {
                type.add((short) 4);
                type.add((short) 7);
                type.add((short) 8);
                type.add((short) 9);
                csvInfo = CsvClient.getCsvByte(outData, fileName, type);
            } else if ("1".equals(inData.getDimension()) && "CLASS".equals(inData.getReportType())) {
                type.add((short) 5);
                type.add((short) 6);
                type.add((short) 8);
                type.add((short) 9);
                csvInfo = CsvClient.getCsvByte(outData, fileName, type);
            } else {
                return Result.error("导出失败");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result result = null;
            try {
                bos.write(csvInfo.getFileContent());
                result = cosUtils.uploadFile(bos, csvInfo.getFileName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bos.flush();
                bos.close();
            }
            return result;

        } else {
            throw new BusinessException("提示：没有查询到数据,请修改查询条件!");
        }
    }

    @Override
    public Object listWeek(BusinessReportRequest inData) {
        return businessReportMapper.listWeek(inData);
    }

    @Override
    public Object getNowWeekNum(BusinessReportRequest inData) {
        return businessReportMapper.getNowWeekNum();
    }

    private List<WeeklySalesReportInfo> getClassReort(BusinessReportRequest inData) {
        StringBuilder head1 = new StringBuilder().append("select t1.CLIENT clientId,t1.francName,COALESCE(t1.PRO_COMP_BIG_CODE,'') proCompBigCode,t1.PRO_COMP_BIG_NAME proCompBigName,\n" +
                "t7.salesStoresQty,t7.salesDayQty,t2.salesGoodsQty,t2.salesCost,t2.salesQty,t2.salesAmt,t2.totalBillCount,\n" +
                "t2.customerPrice,t2.gross,\n" +
                "t8.inventoryItemQty,ROUND(( t2.salesQty / t2.totalBillCount ),6) customerQty,\n" +
//                "ROUND(t2.gross / t3.totalSalesAmt,6) grossProfitRate,\n" +
                "t4.HYGGross,\n" +
                "t4.HYGSalesAmt,\n" +
                "t4.HQty1,\n" +
                "t1.inventoryQty,\n" +
                "t1.inventoryAmt,\n" +
//                "ROUND(cast(t2.salesGoodsQty as double) /cast(t8.inventoryItemQty as double),2) movableSalesRate,\n" +
//                "ROUND(cast(t2.totalBillCount as double) / cast(t6.totalSalesDay as double),0) averageDailyTransactionQty,\n" +
//                "ROUND(t2.salesAmt / t3.totalSalesAmt,6) salesRate,\n" +
//                "ROUND(t5.salesItemQty / t2.totalBillCount,6) customerItemQty,\n" +
                "t5.salesItemQty,\n" +
                "t6.totalSalesDay " +
                "FROM\n" +
                "\t(");
        StringBuilder t11 = new StringBuilder().append("select aa.CLIENT,aa.francName,aa.PRO_COMP_BIG_CODE,aa.PRO_COMP_BIG_NAME ,(sum(aa.WM_KCSL) + sum(aa.GSSB_QTY))inventoryQty,sum(aa.kccb)inventoryAmt\n" +
                "from (\n" +
                "SELECT\n" +
                "        a.CLIENT,g.FRANC_NAME francName,\n" +
                "        d.PRO_COMP_BIG_CODE,d.PRO_COMP_BIG_NAME,\n" +
                "        a.WM_SP_BM spbm,\n" +
                "        sum(a.WM_KCSL)WM_KCSL ,0 GSSB_QTY,\n" +
                "        sum(a.WM_KCSL) * (sum(e.MAT_TOTAL_AMT)+SUM(e.MAT_RATE_AMT))/sum(e.MAT_TOTAL_QTY) kccb\n" +
                "FROM\n" +
                "        GAIA_WMS_KUCEN a\n" +
                "        inner JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT \n" +
                "        AND a.WM_SP_BM = b.PRO_SELF_CODE \n" +
                "        AND a.PRO_SITE = b.PRO_SITE\n" +
                "        inner JOIN GAIA_DC_DATA c ON c.CLIENT = a.CLIENT \n" +
                "        AND a.PRO_SITE = c.DC_CODE\n" +
                "        LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS d ON d.PRO_COMP_CODE = b.PRO_COMPCLASS \n" +
                "        inner JOIN V_GAIA_MATERIAL_ASSESS e on a.CLIENT = e.CLIENT and e.MAT_PRO_CODE = a.WM_SP_BM and e.MAT_ASSESS_SITE = a.PRO_SITE\n" +
                "        inner JOIN GAIA_FRANCHISEE g on g.CLIENT = a.CLIENT " +
                " where 1=1 ");
        StringBuilder t12 = new StringBuilder().append("GROUP BY a.WM_SP_BM,a.CLIENT,d.PRO_COMP_BIG_CODE,d.PRO_COMP_BIG_NAME,g.FRANC_NAME\n" +
                "        \n" +
                "        union all\n" +
                "        SELECT\n" +
                "        a.CLIENT,g.FRANC_NAME francName,\n" +
                "        d.PRO_COMP_BIG_CODE,d.PRO_COMP_BIG_NAME,\n" +
                "         a.GSSB_PRO_ID  spbm,\n" +
                "         0 WM_KCSL,sum(a.GSSB_QTY)GSSB_QTY,\n" +
                "         case when sum(e.MAT_TOTAL_QTY) >0 then sum(a.GSSB_QTY) * (sum(e.MAT_ADD_AMT) +sum(e.MAT_ADD_TAX))/sum(e.MAT_TOTAL_QTY)\n" +
                "         when sum(e.MAT_TOTAL_QTY) <= 0 then sum(a.GSSB_QTY) * sum(e.MAT_MOV_PRICE) end kccb\n" +
                "FROM\n" +
                "        GAIA_SD_STOCK_BATCH a\n" +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT \n" +
                "        AND a.GSSB_PRO_ID = b.PRO_SELF_CODE \n" +
                "        AND a.GSSB_BR_ID = b.PRO_SITE\n" +
                "        INNER JOIN GAIA_STORE_DATA c ON c.CLIENT = a.CLIENT \n" +
                "        AND a.GSSB_BR_ID = c.STO_CODE\n" +
                "        LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS d ON d.PRO_COMP_CODE = b.PRO_COMPCLASS \n" +
                "        LEFT JOIN V_GAIA_MATERIAL_ASSESS e on a.CLIENT = e.CLIENT and e.MAT_PRO_CODE = a.GSSB_PRO_ID and e.MAT_ASSESS_SITE = a.GSSB_BR_ID\n" +
                "        inner JOIN GAIA_FRANCHISEE g on g.CLIENT = a.CLIENT\n" +
                "WHERE 1 = 1 ");
        StringBuilder t2 = new StringBuilder().append("GROUP BY         a.CLIENT, d.PRO_COMP_BIG_CODE,d.PRO_COMP_BIG_NAME,a.GSSB_PRO_ID,g.FRANC_NAME\n" +
                "        )aa\n" +
                "        GROUP BY aa.CLIENT,aa.PRO_COMP_BIG_CODE,aa.PRO_COMP_BIG_NAME,aa.francName) t1\n" +
                "\tLEFT JOIN ( select a.CLIENT,\n" +
                " c.FRANC_NAME francName,\n" +
                " e.PRO_COMP_BIG_CODE,\n" +
                " e.PRO_COMP_BIG_NAME, \n" +
                " count(DISTINCT a.GSSD_PRO_ID) salesGoodsQty,\n" +
                " sum(a.GSSD_MOV_PRICE)salesCost,\n" +
                " sum(a.GSSD_QTY) salesQty,\n" +
                " sum(a.GSSD_AMT) salesAmt,\n" +
                " count(DISTINCT a.GSSD_BILL_NO) totalBillCount,\n" +
                " sum(a.GSSD_AMT)/count(DISTINCT a.GSSD_BILL_NO) customerPrice,\n" +
                " sum( a.GSSD_AMT )-sum(a.GSSD_MOV_PRICE) gross\n" +
                " from\n" +
                " GAIA_SD_SALE_D a\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " inner join GAIA_CAL_DT g on g.GCD_DATE = a.GSSD_DATE\n" +
                " inner JOIN GAIA_PRODUCT_BUSINESS d on d.CLIENT = a.CLIENT and d.PRO_SITE = a.GSSD_BR_ID  and d.PRO_SELF_CODE = a.GSSD_PRO_ID\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS e on e.PRO_COMP_CODE = d.PRO_COMPCLASS\n" +
                " inner JOIN GAIA_STORE_DATA h ON a.GSSD_BR_ID  = h.STO_CODE AND h.CLIENT = a.CLIENT\n" +
                " left join GAIA_AREA b on b.AREA_ID = h.STO_CITY\n" +
                " left join GAIA_AREA f on f.AREA_ID = h.STO_PROVINCE" +
                " where a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'");
        StringBuilder t3 = new StringBuilder().append("  GROUP BY a.CLIENT,c.FRANC_NAME,e.PRO_COMP_BIG_NAME,\n" +
                "\te.PRO_COMP_BIG_CODE )t2 on t1.CLIENT = t2.CLIENT and t2.PRO_COMP_BIG_CODE = t1.PRO_COMP_BIG_CODE\n" +
                "LEFT JOIN (\n" +
                "  SELECT\n" +
                "  a.CLIENT,\n" +
                "  SUM( a.GSSD_AMT ) totalSalesAmt\n" +
                " FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " inner join GAIA_CAL_DT g on g.GCD_DATE = a.GSSD_DATE\n" +
                " inner JOIN GAIA_PRODUCT_BUSINESS d on d.CLIENT = a.CLIENT and d.PRO_SITE = a.GSSD_BR_ID  and d.PRO_SELF_CODE = a.GSSD_PRO_ID\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS e on e.PRO_COMP_CODE = d.PRO_COMPCLASS\n" +
                " inner JOIN GAIA_STORE_DATA h ON a.GSSD_BR_ID  = h.STO_CODE AND h.CLIENT = a.CLIENT\n" +
                " left join GAIA_AREA b on b.AREA_ID = h.STO_CITY\n" +
                " left join GAIA_AREA f on f.AREA_ID = h.STO_PROVINCE\n" +
                " WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'");
        StringBuilder t4 = new StringBuilder().append(" GROUP BY\n" +
                " a.CLIENT\n" +
                " ) t3 on t3.CLIENT = t1.CLIENT\n" +
                " LEFT JOIN (select a.CLIENT, d.PRO_COMP_BIG_CODE ,\n" +
                "          count( DISTINCT a.GSS_PRO_ID ) HQty1,\n" +
                "                sum( c.GSSD_AMT ) HYGSalesAmt,\n" +
                "          sum(c.GSSD_AMT) - sum(c.GSSD_MOV_PRICE) HYGGross\n" +
                "from GAIA_SD_STOCK a\n" +
                "LEFT JOIN GAIA_PRODUCT_BUSINESS b on a.CLIENT = b.CLIENT and a.GSS_BR_ID = b.PRO_SITE and a.GSS_PRO_ID = b.PRO_SELF_CODE\n" +
                "LEFT JOIN GAIA_SD_SALE_D c on c.CLIENT = a.CLIENT  and a.GSS_BR_ID  = c.GSSD_BR_ID AND a.GSS_PRO_ID = c.GSSD_PRO_ID \n" +
                "LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS d on d.PRO_COMP_CODE = b.PRO_COMPCLASS\n" +
                "        LEFT JOIN GAIA_FRANCHISEE j on j.CLIENT = a.CLIENT\n" +
                "        LEFT JOIN GAIA_STORE_DATA k ON a.GSS_BR_ID  = k.STO_CODE AND k.CLIENT = a.CLIENT\n" +
                "where b.PRO_POSITION NOT IN ('T','D') \n" +
                "AND (b.PRO_NO_PURCHASE is null or b.PRO_NO_PURCHASE != '1')" +
                "and c.GSSD_DATE >= TIMESTAMPADD(DAY, -90, date '" + inData.getStartDate() + "') AND c.GSSD_DATE <= '" + inData.getEndDate() + "'");
        StringBuilder t5 = new StringBuilder().append(" GROUP BY a.CLIENT,d.PRO_COMP_BIG_CODE)t4 on t4.CLIENT = t1.CLIENT and t1.PRO_COMP_BIG_CODE = t4.PRO_COMP_BIG_CODE\n" +
                "LEFT JOIN (select aa.CLIENT,aa.PRO_COMP_BIG_CODE,sum(aa.kdps)salesItemQty from (SELECT\n" +
                " a.CLIENT,e.PRO_COMP_BIG_CODE,\n" +
                "   count(DISTINCT a.GSSD_PRO_ID) kdps\n" +
                " FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " inner join GAIA_CAL_DT g on g.GCD_DATE = a.GSSD_DATE\n" +
                " inner JOIN GAIA_PRODUCT_BUSINESS d on d.CLIENT = a.CLIENT and d.PRO_SITE = a.GSSD_BR_ID  and d.PRO_SELF_CODE = a.GSSD_PRO_ID\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS e on e.PRO_COMP_CODE = d.PRO_COMPCLASS\n" +
                " inner JOIN GAIA_STORE_DATA h ON a.GSSD_BR_ID  = h.STO_CODE AND h.CLIENT = a.CLIENT\n" +
                " left join GAIA_AREA b on b.AREA_ID = h.STO_CITY\n" +
                " left join GAIA_AREA f on f.AREA_ID = h.STO_PROVINCE\n" +
                " WHERE  a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'");
        StringBuilder t6 = new StringBuilder().append(" GROUP BY\n" +
                " a.CLIENT ,a.GSSD_BR_ID,a.GSSD_BILL_NO,e.PRO_COMP_BIG_CODE)aa group by aa.CLIENT,aa.PRO_COMP_BIG_CODE)t5 on t5.CLIENT = t1.CLIENT and t5.PRO_COMP_BIG_CODE = t1.PRO_COMP_BIG_CODE \n" +
                " LEFT JOIN (select aa.CLIENT,sum(aa.xsts)totalSalesDay from (SELECT\n" +
                " a.CLIENT,\n" +
                "   count(DISTINCT a.GSSD_DATE) xsts\n" +
                " FROM\n" +
                " GAIA_SD_SALE_D a\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " inner join GAIA_CAL_DT g on g.GCD_DATE = a.GSSD_DATE\n" +
                " inner JOIN GAIA_PRODUCT_BUSINESS d on d.CLIENT = a.CLIENT and d.PRO_SITE = a.GSSD_BR_ID  and d.PRO_SELF_CODE = a.GSSD_PRO_ID\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS e on e.PRO_COMP_CODE = d.PRO_COMPCLASS\n" +
                " inner JOIN GAIA_STORE_DATA h ON a.GSSD_BR_ID  = h.STO_CODE AND h.CLIENT = a.CLIENT\n" +
                " left join GAIA_AREA b on b.AREA_ID = h.STO_CITY\n" +
                " left join GAIA_AREA f on f.AREA_ID = h.STO_PROVINCE\n" +
                " WHERE  a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'");
        StringBuilder t7 = new StringBuilder().append(" GROUP BY " +
                "           a.CLIENT ,a.GSSD_BR_ID)aa GROUP BY aa.CLIENT)t6 on t6.CLIENT = t1.CLIENT" +
                " left join (select a.CLIENT,\n" +
                " c.FRANC_NAME francName, \n" +
                " count(DISTINCT a.GSSD_DATE) salesDayQty,\n" +
                " count(DISTINCT a.GSSD_BR_ID) salesStoresQty\n" +
                " from\n" +
                " GAIA_SD_SALE_D a\n" +
                " LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                " inner join GAIA_CAL_DT g on g.GCD_DATE = a.GSSD_DATE\n" +
                " inner JOIN GAIA_PRODUCT_BUSINESS d on d.CLIENT = a.CLIENT and d.PRO_SITE = a.GSSD_BR_ID  and d.PRO_SELF_CODE = a.GSSD_PRO_ID\n" +
                " LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS e on e.PRO_COMP_CODE = d.PRO_COMPCLASS\n" +
                " inner JOIN GAIA_STORE_DATA h ON a.GSSD_BR_ID  = h.STO_CODE AND h.CLIENT = a.CLIENT\n" +
                " left join GAIA_AREA b on b.AREA_ID = h.STO_CITY\n" +
                " left join GAIA_AREA f on f.AREA_ID = h.STO_PROVINCE\n" +
                " where a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "'");
        StringBuilder t81 = new StringBuilder().append("GROUP BY a.CLIENT,c.FRANC_NAME) t7 on t7.CLIENT = t1.CLIENT  \n" +
                " left join (select bb.CLIENT,bb.PRO_COMP_BIG_CODE,count(1)inventoryItemQty from (select aa.CLIENT,aa.francName,aa.PRO_COMP_BIG_CODE \n" +
                "from (\n" +
                "SELECT\n" +
                "        a.CLIENT,g.FRANC_NAME francName,\n" +
                "        d.PRO_COMP_BIG_CODE,\n" +
                "        a.WM_SP_BM spbm,\n" +
                "        sum(a.WM_KCSL)WM_KCSL ,0 GSSB_QTY,\n" +
                "        sum(a.WM_KCSL) * (sum(e.MAT_TOTAL_AMT)+SUM(e.MAT_RATE_AMT))/sum(e.MAT_TOTAL_QTY) kccb\n" +
                "FROM\n" +
                "        GAIA_WMS_KUCEN a\n" +
                "        inner JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT \n" +
                "        AND a.WM_SP_BM = b.PRO_SELF_CODE \n" +
                "        AND a.PRO_SITE = b.PRO_SITE\n" +
                "        inner JOIN GAIA_DC_DATA c ON c.CLIENT = a.CLIENT \n" +
                "        AND a.PRO_SITE = c.DC_CODE\n" +
                "        LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS d ON d.PRO_COMP_CODE = b.PRO_COMPCLASS \n" +
                "        inner JOIN V_GAIA_MATERIAL_ASSESS e on a.CLIENT = e.CLIENT and e.MAT_PRO_CODE = a.WM_SP_BM and e.MAT_ASSESS_SITE = a.PRO_SITE\n" +
                "        inner JOIN GAIA_FRANCHISEE g on g.CLIENT = a.CLIENT  \n" +
                "        where 1=1 ");
        StringBuilder t82 = new StringBuilder().append("GROUP BY a.WM_SP_BM,a.CLIENT,d.PRO_COMP_BIG_CODE,g.FRANC_NAME\n" +
                "        \n" +
                "        union all\n" +
                "        SELECT\n" +
                "        a.CLIENT,g.FRANC_NAME francName,\n" +
                "        d.PRO_COMP_BIG_CODE,\n" +
                "         a.GSSB_PRO_ID  spbm,\n" +
                "         0 WM_KCSL,sum(a.GSSB_QTY)GSSB_QTY,\n" +
                "         case when sum(e.MAT_TOTAL_QTY) >0 then sum(a.GSSB_QTY) * (sum(e.MAT_ADD_AMT) +sum(e.MAT_ADD_TAX))/sum(e.MAT_TOTAL_QTY)\n" +
                "         when sum(e.MAT_TOTAL_QTY) <= 0 then sum(a.GSSB_QTY) * sum(e.MAT_MOV_PRICE) end kccb\n" +
                "FROM\n" +
                "        GAIA_SD_STOCK_BATCH a\n" +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS b ON a.CLIENT = b.CLIENT \n" +
                "        AND a.GSSB_PRO_ID = b.PRO_SELF_CODE \n" +
                "        AND a.GSSB_BR_ID = b.PRO_SITE\n" +
                "        INNER JOIN GAIA_STORE_DATA c ON c.CLIENT = a.CLIENT \n" +
                "        AND a.GSSB_BR_ID = c.STO_CODE\n" +
                "        LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS d ON d.PRO_COMP_CODE = b.PRO_COMPCLASS \n" +
                "        LEFT JOIN V_GAIA_MATERIAL_ASSESS e on a.CLIENT = e.CLIENT and e.MAT_PRO_CODE = a.GSSB_PRO_ID and e.MAT_ASSESS_SITE = a.GSSB_BR_ID\n" +
                "        inner JOIN GAIA_FRANCHISEE g on g.CLIENT = a.CLIENT\n" +
                "WHERE 1 = 1 ");
        StringBuilder t83 = new StringBuilder().append("");
        //查询条件 成分大类
        if (StringUtils.isNotBlank(inData.getProCompBigCode())) {
            t2.append(" and e.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t11.append(" and d.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t12.append(" and d.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t3.append(" and e.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t4.append(" and d.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t5.append(" and e.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t6.append(" and e.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t7.append(" and e.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t81.append(" and d.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t82.append(" and d.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");

        }
        //查询条件 成分中类
        if (StringUtils.isNotBlank(inData.getProCompMidCode())) {
            t2.append(" and e.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t11.append(" and d.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t12.append(" and d.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t3.append(" and e.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t4.append(" and d.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t5.append(" and e.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t6.append(" and e.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t7.append(" and e.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t81.append(" and d.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t82.append(" and d.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");

        }
        //查询条件 成分小类
        if (StringUtils.isNotBlank(inData.getProCompLitCode())) {
            t2.append(" and e.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t11.append(" and d.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t12.append(" and d.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t3.append(" and e.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t4.append(" and d.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t5.append(" and e.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t6.append(" and e.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t7.append(" and e.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t81.append(" and d.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t82.append(" and d.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");

        }
        //查询条件 成分分类
        if (StringUtils.isNotBlank(inData.getProCompCode())) {
            t2.append(" and e.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t11.append(" and d.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t12.append(" and d.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t3.append(" and e.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t4.append(" and d.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t5.append(" and e.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t6.append(" and e.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t7.append(" and e.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t81.append(" and d.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t82.append(" and d.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");

        }
        //查询条件 加盟商
        if (StringUtils.isNotBlank(inData.getClientStr())) {
            t2.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t11.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t12.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t3.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t4.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t5.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t6.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t7.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t81.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t82.append(" and a.CLIENT in (" + inData.getClientStr() + ")");

        }
        if (StringUtils.isNotBlank(inData.getFrancType1()) ) {
            t2.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t11.append(" and ( g.FRANC_TYPE1 = " + inData.getFrancType1());
            t12.append(" and (g.FRANC_TYPE1 = " + inData.getFrancType1());
            t3.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t4.append(" and ( j.FRANC_TYPE1 = " + inData.getFrancType1());
            t5.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t6.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t7.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t81.append(" and ( g.FRANC_TYPE1 = " + inData.getFrancType1());
            t82.append(" and ( g.FRANC_TYPE1 = " + inData.getFrancType1());
            //查询条件 连锁公司
            if (StringUtils.isNotBlank(inData.getFrancType2())) {
                t2.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t11.append(" or g.FRANC_TYPE2 = " + inData.getFrancType2());
                t12.append(" or g.FRANC_TYPE2 = " + inData.getFrancType2());
                t3.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t4.append(" or j.FRANC_TYPE2 = " + inData.getFrancType2());
                t5.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t6.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t7.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t81.append(" or g.FRANC_TYPE2 = " + inData.getFrancType2());
                t82.append(" or g.FRANC_TYPE2 = " + inData.getFrancType2());
            }
            //查询条件 批发公司
            if (StringUtils.isNotBlank(inData.getFrancType3())) {
                t2.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t11.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                t12.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                t3.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t4.append(" or j.FRANC_TYPE3 = " + inData.getFrancType3());
                t5.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t6.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t7.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t81.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                t82.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
            }
            t2.append(" ) ");
            t11.append(" ) ");
            t12.append(" ) ");
            t3.append(" ) ");
            t4.append(" ) ");
            t5.append(" ) ");
            t6.append(" ) ");
            t7.append(" ) ");
            t81.append(" ) ");
            t82.append(" ) ");
        } else if (StringUtils.isBlank(inData.getFrancType1()) && StringUtils.isNotBlank(inData.getFrancType2())) {
            //查询条件 连锁公司
            if (StringUtils.isNotBlank(inData.getFrancType2())) {
                t2.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t11.append(" and ( g.FRANC_TYPE2 = " + inData.getFrancType2());
                t12.append(" and ( g.FRANC_TYPE2 = " + inData.getFrancType2());
                t3.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t4.append(" and ( j.FRANC_TYPE2 = " + inData.getFrancType2());
                t5.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t6.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t7.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t81.append(" and ( g.FRANC_TYPE2 = " + inData.getFrancType2());
                t82.append(" and ( g.FRANC_TYPE2 = " + inData.getFrancType2());

                //查询条件 批发公司
                if (StringUtils.isNotBlank(inData.getFrancType3())) {
                    t2.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t11.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                    t12.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                    t3.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t4.append(" or j.FRANC_TYPE3 = " + inData.getFrancType3());
                    t5.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t6.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t7.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t81.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                    t82.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                }
            }
            t2.append(" ) ");
            t11.append(" ) ");
            t12.append(" ) ");
            t3.append(" ) ");
            t4.append(" ) ");
            t5.append(" ) ");
            t6.append(" ) ");
            t7.append(" ) ");
            t81.append(" ) ");
            t82.append(" ) ");
        } else if (StringUtils.isBlank(inData.getFrancType1()) && StringUtils.isBlank(inData.getFrancType2()) && StringUtils.isNotBlank(inData.getFrancType3())) {
            t2.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t11.append(" and g.FRANC_TYPE3 = " + inData.getFrancType3());
            t12.append(" and g.FRANC_TYPE3 = " + inData.getFrancType3());
            t3.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t4.append(" and j.FRANC_TYPE3 = " + inData.getFrancType3());
            t5.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t6.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t7.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t81.append(" and g.FRANC_TYPE3 = " + inData.getFrancType3());
            t82.append(" and g.FRANC_TYPE3 = " + inData.getFrancType3());
        }
        //查询条件 门店
        if (CollectionUtil.isNotEmpty(inData.getStoCodeList())) {
            List<String> stoCodeList = inData.getStoCodeList();
            t11.append(" AND ");
            t12.append(" AND ");
            t2.append(" AND ");
            t3.append(" AND ");
            t4.append(" AND ");
            t5.append(" AND ");
            t6.append(" AND ");
            t7.append(" AND ");
            t81.append(" AND ");
            t82.append(" AND ");
            for (int i = 0; i < stoCodeList.size(); i++) {
                String[] arr = stoCodeList.get(i).split("-");
                if (i == stoCodeList.size() - 1) {
                    t2.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t11.append("(a.CLIENT = '" + arr[0] + "' AND a.PRO_SITE = '" + arr[1] + "')");
                    t12.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSB_BR_ID = '" + arr[1] + "')");
                    t3.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t4.append("(a.CLIENT = '" + arr[0] + "' AND a.GSS_BR_ID = '" + arr[1] + "')");
                    t5.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t6.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t7.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t81.append("(a.CLIENT = '" + arr[0] + "' AND a.PRO_SITE = '" + arr[1] + "')");
                    t82.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSB_BR_ID = '" + arr[1] + "')");
                } else {
                    t2.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t11.append("(a.CLIENT = '" + arr[0] + "' AND a.PRO_SITE = '" + arr[1] + "') or ");
                    t12.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSB_BR_ID = '" + arr[1] + "') or ");
                    t3.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t4.append("(a.CLIENT = '" + arr[0] + "' AND a.GSS_BR_ID = '" + arr[1] + "') or ");
                    t5.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t6.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t7.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t81.append("(a.CLIENT = '" + arr[0] + "' AND a.PRO_SITE = '" + arr[1] + "') or ");
                    t82.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSB_BR_ID = '" + arr[1] + "') or ");
                }
            }
        }
        //查询条件 店型
        if (StringUtils.isNotBlank(inData.getGsstVersionStr())) {
            t2.append(" and h.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t12.append(" and c.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t3.append(" and h.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t4.append(" and k.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t5.append(" and h.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t6.append(" and h.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t7.append(" and h.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t82.append(" and c.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");

        }
        StringBuilder end = new StringBuilder().append("  GROUP BY         a.CLIENT, d.PRO_COMP_BIG_CODE,a.GSSB_PRO_ID,g.FRANC_NAME\n" +
                "        )aa\n" +
                "        GROUP BY aa.CLIENT,aa.PRO_COMP_BIG_CODE,aa.francName,aa.spbm)bb group by bb.CLIENT,bb.PRO_COMP_BIG_CODE,bb.francName) t8 on t8.CLIENT = t1.CLIENT and t8.PRO_COMP_BIG_CODE = t1.PRO_COMP_BIG_CODE ");
        StringBuilder order = new StringBuilder().append(" order by t1.CLIENT,t1.PRO_COMP_BIG_CODE ");
        StringBuilder sql1 = head1.append(t11).append(t12).append(t2).append(t3).append(t4).append(t5).append(t6).append(t7).append(t81).append(t82).append(end).append(order);
        log.info("sql1统计数据：{}", sql1.toString());
        List<WeeklySalesReportInfo> reportOutData = kylinJdbcTemplate.query(sql1.toString(), RowMapper.getDefault(WeeklySalesReportInfo.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(reportOutData));
        return reportOutData;
    }

    private void setEntReportTB(WeeklySalesReportInfo thisReportDatum, WeeklySalesReportInfo lastYearReportDatum) {
        //关联毛利率
        lastYearReportDatum.setAssociateGrossRate(divide(checkNull(lastYearReportDatum.getAssociateGross()), checkNull(lastYearReportDatum.getAssociateSalesAmt())).doubleValue());
        lastYearReportDatum.setMedicalSalesAmtRate(divide(checkNull(lastYearReportDatum.getMedicalSalesAmt()), checkNull(lastYearReportDatum.getSalesAmt())).doubleValue());
        //计算毛利率
        lastYearReportDatum.setGrossRate(divide(checkNull(lastYearReportDatum.getGross()), checkNull(lastYearReportDatum.getSalesAmt())).doubleValue());
        //关联毛利占比
        if (ObjectUtil.isNotEmpty(lastYearReportDatum.getGross()) && ObjectUtil.isNotEmpty(lastYearReportDatum.getAssociateGross())) {
            Double associateGrossPercentage = divide(checkNull(lastYearReportDatum.getAssociateGross()), checkNull(lastYearReportDatum.getGross())).doubleValue();
            lastYearReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
        }
        //关联销售占比
        if (ObjectUtil.isNotEmpty(lastYearReportDatum.getSalesAmt()) && ObjectUtil.isNotEmpty(lastYearReportDatum.getAssociateSalesAmt())) {
            Double associateSalesRate = divide(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(thisReportDatum.getSalesAmt())).doubleValue();
            lastYearReportDatum.setAssociateSalesRate(associateSalesRate);
        }

        //动销门店数同比
        Double salesStoresQtyTB = subtract(checkNull(thisReportDatum.getSalesStoresQty()), checkNull(lastYearReportDatum.getSalesStoresQty())).doubleValue();
        thisReportDatum.setSalesStoresQtyTB(salesStoresQtyTB);
        //动销天数同比
        Double salesDayQtyTB = subtract(checkNull(thisReportDatum.getSalesDayQty()), checkNull(lastYearReportDatum.getSalesDayQty())).doubleValue();
        thisReportDatum.setSalesDayQtyTB(salesDayQtyTB);
        //销售总天数同比
        Double totalSalesDayTB = subtract(checkNull(thisReportDatum.getTotalSalesDay()), checkNull(lastYearReportDatum.getTotalSalesDay())).doubleValue();
        thisReportDatum.setTotalSalesDayTB(totalSalesDayTB);
        //销售额同比
        Double salesAmtTB = subtract(checkNull(thisReportDatum.getSalesAmt()), checkNull(lastYearReportDatum.getSalesAmt())).doubleValue();
        thisReportDatum.setSalesAmtTB(salesAmtTB);
        //销售额同比率
        Double salesAmtTBRate = chainComparison(checkNull(thisReportDatum.getSalesAmt()), checkNull(lastYearReportDatum.getSalesAmt())).doubleValue();
        thisReportDatum.setSalesAmtTBRate(salesAmtTBRate);
        //日均销售额同比
        Double averageDailySalesAmtTB = subtract(checkNull(thisReportDatum.getAverageDailySalesAmt()), checkNull(lastYearReportDatum.getAverageDailySalesAmt())).doubleValue();
        thisReportDatum.setAverageDailySalesAmtTB(averageDailySalesAmtTB);
        //毛利额同比
        Double grossTB = subtract(checkNull(thisReportDatum.getGross()), checkNull(lastYearReportDatum.getGross())).doubleValue();
        thisReportDatum.setGrossTB(grossTB);
        //毛利额同比率
        Double grossTBRate = chainComparison(checkNull(thisReportDatum.getGross()), checkNull(lastYearReportDatum.getGross())).doubleValue();
        thisReportDatum.setGrossTBRate(grossTBRate);
        //日均毛利额同比
        Double averageDailyGrossTB = subtract(checkNull(thisReportDatum.getAverageDailyGross()), checkNull(lastYearReportDatum.getAverageDailyGross())).doubleValue();
        thisReportDatum.setAverageDailyGrossTB(averageDailyGrossTB);
        //毛利率同比
        Double grossRateTB = subtract(checkNull(thisReportDatum.getGrossRate()), checkNull(lastYearReportDatum.getGrossRate())).doubleValue();
        thisReportDatum.setGrossRateTB(grossRateTB);
        //日均交易次数同比
        Double averageDailyTransactionQtyTB = subtract(checkNull(thisReportDatum.getAverageDailyTransactionQty()), checkNull(lastYearReportDatum.getAverageDailyTransactionQty())).doubleValue();
        thisReportDatum.setAverageDailyTransactionQtyTB(averageDailyTransactionQtyTB);
        //客单价同比
        Double customerPriceTB = subtract(checkNull(thisReportDatum.getCustomerPrice()), checkNull(lastYearReportDatum.getCustomerPrice())).doubleValue();
        thisReportDatum.setCustomerPriceTB(customerPriceTB);
        //医保销售额同比
        Double medicalSalesAmtTB = subtract(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(lastYearReportDatum.getMedicalSalesAmt())).doubleValue();
        thisReportDatum.setMedicalSalesAmtTB(medicalSalesAmtTB);
        //医保销售占比同比
        Double medicalSalesAmtRateTB = subtract(checkNull(thisReportDatum.getMedicalSalesAmtRate()), checkNull(lastYearReportDatum.getMedicalSalesAmtRate())).doubleValue();
        thisReportDatum.setMedicalSalesAmtRateTB(medicalSalesAmtRateTB);
        //关联销售额同比
        Double associateSalesAmtTB = subtract(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(lastYearReportDatum.getAssociateSalesAmt())).doubleValue();
        thisReportDatum.setAssociateSalesAmtTB(associateSalesAmtTB);
        //关联销售占比同比
        Double associateSalesRateTB = subtract(checkNull(thisReportDatum.getAssociateSalesRate()), checkNull(lastYearReportDatum.getAssociateSalesRate())).doubleValue();
        thisReportDatum.setAssociateSalesRateTB(associateSalesRateTB);
        //关联毛利额同比
        Double associateGrossTB = subtract(checkNull(thisReportDatum.getAssociateGross()), checkNull(lastYearReportDatum.getAssociateGross())).doubleValue();
        thisReportDatum.setAssociateGrossTB(associateGrossTB);
        //关联毛利占比同比
        Double associateGrossPercentageTB = subtract(checkNull(thisReportDatum.getAssociateGrossPercentage()), checkNull(lastYearReportDatum.getAssociateGrossPercentage())).doubleValue();
        thisReportDatum.setAssociateGrossPercentageTB(associateGrossPercentageTB);
        //关联毛利率同比
        Double associateGrossRateTB = subtract(checkNull(thisReportDatum.getAssociateGrossRate()), checkNull(lastYearReportDatum.getAssociateGrossRate())).doubleValue();
        thisReportDatum.setAssociateGrossRateTB(associateGrossRateTB);
    }

    private void setEntReportHB(WeeklySalesReportInfo thisReportDatum, WeeklySalesReportInfo lastReportDatum) {
        //关联毛利率
        lastReportDatum.setAssociateGrossRate(divide(checkNull(lastReportDatum.getAssociateGross()), checkNull(lastReportDatum.getAssociateSalesAmt())).doubleValue());
        //医保销售占比
        lastReportDatum.setMedicalSalesAmtRate(divide(checkNull(lastReportDatum.getMedicalSalesAmt()), checkNull(lastReportDatum.getSalesAmt())).doubleValue());
        //计算上期毛利率
        lastReportDatum.setGrossRate(divide(checkNull(lastReportDatum.getGross()), checkNull(lastReportDatum.getSalesAmt())).doubleValue());
        //计算上期关联毛利占比
        Double associateGrossPercentage = ObjectUtil.isNotEmpty(lastReportDatum.getGross()) && checkNull(lastReportDatum.getGross()).compareTo(BigDecimal.ZERO) != 0 ? divide(lastReportDatum.getAssociateGross(), lastReportDatum.getGross()).doubleValue() : BigDecimal.ZERO.doubleValue();
        lastReportDatum.setAssociateGrossPercentage(associateGrossPercentage);
        //计算上期关联销售占比
        Double associateSalesRate = ObjectUtil.isNotEmpty(lastReportDatum.getSalesAmt()) && checkNull(lastReportDatum.getSalesAmt()).compareTo(BigDecimal.ZERO) != 0 ? divide(lastReportDatum.getAssociateSalesAmt(), lastReportDatum.getSalesAmt()).doubleValue() : BigDecimal.ZERO.doubleValue();
        lastReportDatum.setAssociateSalesRate(associateSalesRate);

        //动销门店数环比
        Double salesStoresQtyHB = subtract(checkNull(thisReportDatum.getSalesStoresQty()), checkNull(lastReportDatum.getSalesStoresQty())).doubleValue();
        thisReportDatum.setSalesStoresQtyHB(salesStoresQtyHB);
        //动销天数环比
        Double salesDayQtyHB = subtract(checkNull(thisReportDatum.getSalesDayQty()), checkNull(lastReportDatum.getSalesDayQty())).doubleValue();
        thisReportDatum.setSalesDayQtyHB(salesDayQtyHB);
        //销售总天数环比
        Double totalSalesDayHB = subtract(checkNull(thisReportDatum.getTotalSalesDay()), checkNull(lastReportDatum.getTotalSalesDay())).doubleValue();
        thisReportDatum.setTotalSalesDayHB(totalSalesDayHB);
        //销售额环比
        Double salesAmtHB = subtract(checkNull(thisReportDatum.getSalesAmt()), checkNull(lastReportDatum.getSalesAmt())).doubleValue();
        thisReportDatum.setSalesAmtHB(salesAmtHB);
        //销售额环比率
        Double salesAmtHBRate = chainComparison(checkNull(thisReportDatum.getSalesAmt()), checkNull(lastReportDatum.getSalesAmt())).doubleValue();
        thisReportDatum.setSalesAmtHBRate(salesAmtHBRate);
        //日均销售额环比
        Double averageDailySalesAmtHB = subtract(checkNull(thisReportDatum.getAverageDailySalesAmt()), checkNull(lastReportDatum.getAverageDailySalesAmt())).doubleValue();
        thisReportDatum.setAverageDailySalesAmtHB(averageDailySalesAmtHB);
        //毛利额环比
        Double grossHB = subtract(checkNull(thisReportDatum.getGross()), checkNull(lastReportDatum.getGross())).doubleValue();
        thisReportDatum.setGrossHB(grossHB);
        //毛利额环比率
        Double grossHBRate = chainComparison(checkNull(thisReportDatum.getGross()), checkNull(lastReportDatum.getGross())).doubleValue();
        thisReportDatum.setGrossHBRate(grossHBRate);
        //日均毛利额环比
        Double averageDailyGrossHB = subtract(checkNull(thisReportDatum.getAverageDailyGross()), checkNull(lastReportDatum.getAverageDailyGross())).doubleValue();
        thisReportDatum.setAverageDailyGrossHB(averageDailyGrossHB);
        //毛利率环比
        Double grossRateHB = subtract(checkNull(thisReportDatum.getGrossRate()), checkNull(lastReportDatum.getGrossRate())).doubleValue();
        thisReportDatum.setGrossRateHB(grossRateHB);
        //日均交易次数环比
        Double averageDailyTransactionQtyHB = subtract(checkNull(thisReportDatum.getAverageDailyTransactionQty()), checkNull(lastReportDatum.getAverageDailyTransactionQty())).doubleValue();
        thisReportDatum.setAverageDailyTransactionQtyHB(averageDailyTransactionQtyHB);
        //客单价环比
        Double customerPriceHB = subtract(checkNull(thisReportDatum.getCustomerPrice()), checkNull(lastReportDatum.getCustomerPrice())).doubleValue();
        thisReportDatum.setCustomerPriceHB(customerPriceHB);
        //医保销售额环比
        Double medicalSalesAmtHB = subtract(checkNull(thisReportDatum.getMedicalSalesAmt()), checkNull(lastReportDatum.getMedicalSalesAmt())).doubleValue();
        thisReportDatum.setMedicalSalesAmtHB(medicalSalesAmtHB);
        //医保销售占比环比
        Double medicalSalesAmtRateHB = subtract(checkNull(thisReportDatum.getMedicalSalesAmtRate()), checkNull(lastReportDatum.getMedicalSalesAmtRate())).doubleValue();
        thisReportDatum.setMedicalSalesAmtRateHB(medicalSalesAmtRateHB);
        //关联销售额环比
        Double associateSalesAmtHB = subtract(checkNull(thisReportDatum.getAssociateSalesAmt()), checkNull(lastReportDatum.getAssociateSalesAmt())).doubleValue();
        thisReportDatum.setAssociateSalesAmtHB(associateSalesAmtHB);
        //关联销售占比环比
        Double associateSalesRateHB = subtract(checkNull(thisReportDatum.getAssociateSalesRate()), checkNull(lastReportDatum.getAssociateSalesRate())).doubleValue();
        thisReportDatum.setAssociateSalesRateHB(associateSalesRateHB);
        //关联毛利额环比
        Double associateGrossHB = subtract(checkNull(thisReportDatum.getAssociateGross()), checkNull(lastReportDatum.getAssociateGross())).doubleValue();
        thisReportDatum.setAssociateGrossHB(associateGrossHB);
        //关联毛利占比环比
        Double associateGrossPercentageHB = subtract(checkNull(thisReportDatum.getAssociateGrossPercentage()), checkNull(lastReportDatum.getAssociateGrossPercentage())).doubleValue();
        thisReportDatum.setAssociateGrossPercentageHB(associateGrossPercentageHB);
        //关联毛利率环比
        Double associateGrossRateHB = subtract(checkNull(thisReportDatum.getAssociateGrossRate()), checkNull(lastReportDatum.getAssociateGrossRate())).doubleValue();
        thisReportDatum.setAssociateGrossRateHB(associateGrossRateHB);
    }

    private List<WeeklySalesReportInfo> getWeeklyEntSalesReportInfos(BusinessReportRequest inData) {
        //销售_商品_品类_门店_日期_加盟商_地区 （）
        StringBuilder head1 = new StringBuilder().append("SELECT t1.GCD_YEAR y,\n" +
                " sum( t1.salesAmt ) salesAmt,\n" +
                " sum( t3.billNoCount ) totalBillCount,\n" +
                " t2.salesStoresQty salesStoresQty,\n" +
                " t2.salesDayQty,\n" +
                " count( t1.GSSD_DATE ) totalSalesDay,\n" +
                " sum( t1.salesAmt )- sum( t1.GSSD_MOV_PRICE ) gross,\n" +
                //        " sum( t1.salesAmt )- sum( t1.GSSD_MOV_PRICE ))/ sum( t1.salesAmt ) grossRate,\n" +
                " sum( t4.medicalSalesAmt ) medicalSalesAmt,\n" +
                " COALESCE ( sum( t5.autoQty ), 0 ) autoQty,\n" +
                " COALESCE ( sum( t6.manualQty ), 0 ) manualQty,(\n" +
                " COALESCE ( sum( t5.autoQty ), 0 )+ COALESCE ( sum( t6.manualQty ), 0 )) totalQty,\n" +
                " ROUND(sum( t1.salesAmt )/ count( t1.GSSD_DATE ),6) averageDailySalesAmt,\n" +
                " ROUND((sum( t1.salesAmt )- sum( t1.GSSD_MOV_PRICE ))/ count( t1.GSSD_DATE ),6) averageDailyGross,\n" +
                " ROUND((cast( sum( t3.billNoCount ) AS DOUBLE ) / count( t1.GSSD_DATE )),6) averageDailyTransactionQty,\n" +
                " ROUND(sum( t1.salesAmt ) / sum( t3.billNoCount ),6) customerPrice,\n" +
                "  sum( t7.successQty ) successAssociate,\n" +
                " COALESCE ( sum( t7.associateSalesAmt ), 0 ) associateSalesAmt,\n" +
//                " ROUND(sum( t7.associateSalesAmt )/ sum( t1.salesAmt ),6) associateSalesRate,\n" +
                " sum( t7.associateGross ) associateGross");
        StringBuilder t1 = new StringBuilder().append(" FROM\n" +
                "(SELECT\n" +
                " a.GSSD_DATE,\n" +
                " a.CLIENT,\n" +
                " a.GSSD_BR_ID,\n" +
                " b.GCD_YEAR,d.STO_NAME stoName,\n" +
                " sum( a.GSSD_AMT ) salesAmt,\n" +
                " sum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                " b.GCD_WEEK,\n" +
                " a.CLIENT clientId,\n" +
                " c.FRANC_NAME francName,\n" +
                " i.AREA_ID city,\n" +
                " h.AREA_ID province,\n" +
                " i.AREA_NAME provinceName,\n" +
                " h.AREA_NAME cityName,\n" +
                " b.GCD_MONTH \n" +
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
                "WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "' ");
        StringBuilder t211 = new StringBuilder().append(" GROUP BY\n" +
                " b.GCD_YEAR,\n" +
                " b.GCD_WEEK,\n" +
                " b.GCD_MONTH,\n" +
                " a.CLIENT,\n" +
                " d.STO_NAME,\n" +
                " c.FRANC_NAME,\n" +
                " a.GSSD_BR_ID,\n" +
                " a.GSSD_DATE,\n" +
                " h.AREA_ID,\n" +
                " i.AREA_ID,\n" +
                " h.AREA_NAME,\n" +
                " i.AREA_NAME \n" +
                " ) t1\n" +
                "LEFT JOIN (\n" +
                "  SELECT\n" +
                "    GCD_YEAR,\n" +
                "    a.CLIENT,\n" +
                "    count( DISTINCT GCD_DATE ) salesDayQty,\n");
        if ("2".equals(inData.getDimension())) {
            t211.append("    GSSD_BR_ID, \n");
        }
        StringBuilder t21 = new StringBuilder().append(
                "    count( DISTINCT GSSD_BR_ID ) salesStoresQty \n" +
                        "  FROM\n" +
                        "    GAIA_SD_SALE_D a\n" +
                        "    INNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                        "    LEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                        "    INNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                        "    AND a.GSSD_BR_ID = d.STO_CODE\n" +
                        "    INNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                        "    AND a.GSSD_BR_ID = e.PRO_SITE \n" +
                        "    AND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                        "    LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS g ON g.PRO_COMP_CODE = e.PRO_COMPCLASS\n" +
                        "    LEFT JOIN GAIA_AREA h ON h.AREA_ID = d.STO_CITY\n" +
                        "    LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                        "  WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "' ");
        StringBuilder t311 = new StringBuilder().append(" GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  GCD_YEAR\n");
        if ("2".equals(inData.getDimension())) {
            t311.append("  ,GSSD_BR_ID \n");
        }

        StringBuilder t312 = new StringBuilder().append("  ) t2 ON t1.GCD_YEAR = t2.GCD_YEAR \n" +
                "  AND t1.CLIENT = t2.CLIENT \n");

        if ("week".equals(inData.getWeekOrMonth())) {
            //周
            t211.append("    GCD_WEEK,\n");
            t311.append("  ,GCD_WEEK\n");
            t312.append("  AND t1.GCD_WEEK = t2.GCD_WEEK \n");
        } else {
            //月
            t211.append("    GCD_MONTH,\n");
            t311.append("  ,GCD_MONTH\n");
            t312.append("  AND t1.GCD_MONTH = t2.GCD_MONTH \n");
        }
        if ("2".equals(inData.getDimension())) {
            t312.append("  AND t1.GSSD_BR_ID = t2.GSSD_BR_ID\n");
        }
        StringBuilder t31 = new StringBuilder().append(
                "  LEFT JOIN (\n" +
                        "  SELECT\n" +
                        "  count( 1 ) billNoCount,\n" +
                        "  t.CLIENT,\n" +
                        "  t.GCD_WEEK,\n" +
                        "  t.GCD_YEAR,\n" +
                        "  GSSD_BR_ID,\n" +
                        "  t.GSSD_DATE \n" +
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
                        "  LEFT JOIN GAIA_AREA i ON i.AREA_ID = d.STO_PROVINCE \n" +
                        "  WHERE a.GSSD_DATE >= '" + inData.getStartDate() + "' AND a.GSSD_DATE <= '" + inData.getEndDate() + "' ");
        StringBuilder t41 = new StringBuilder().append(" GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  GSSD_BILL_NO,\n" +
                "  GCD_YEAR,\n" +
                "  GCD_WEEK,\n" +
                "  a.GSSD_DATE,\n" +
                "  GSSD_BR_ID \n" +
                "  ) t \n" +
                "  GROUP BY\n" +
                "  t.CLIENT,\n" +
                "  t.GCD_WEEK,\n" +
                "  t.GCD_YEAR,\n" +
                "  GSSD_BR_ID,\n" +
                "  t.GSSD_DATE \n" +
                "  ) t3 ON t1.CLIENT = t3.CLIENT \n" +
                "  AND t1.GSSD_BR_ID = t3.GSSD_BR_ID \n" +
                "  AND t1.GSSD_DATE = t3.GSSD_DATE\n" +
                "  LEFT JOIN (\n" +
                "  SELECT\n" +
                "  a.CLIENT,\n" +
                "  b.GCD_YEAR,\n" +
                "  b.GCD_WEEK,\n" +
                "  a.GSSPM_BR_ID,\n" +
                "  a.GSSPM_DATE,\n" +
                "  SUM( GSSPM_AMT ) medicalSalesAmt \n" +
                "  FROM\n" +
                "  V_GAIA_SD_SALE_PAY_MSG a\n" +
                "  INNER JOIN GAIA_STORE_DATA st ON a.GSSPM_BR_ID = st.STO_CODE \n" +
                "  AND st.CLIENT = a.CLIENT\n" +
                "  INNER JOIN GAIA_CAL_DT b ON a.GSSPM_DATE = b.GCD_DATE \n" +
                "  WHERE  a.GSSPM_ID = '4000' and a.GSSPM_DATE >= '" + inData.getStartDate() + "' AND a.GSSPM_DATE <= '" + inData.getEndDate() + "' ");
        StringBuilder t51 = new StringBuilder().append(" GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  b.GCD_YEAR,\n" +
                "  b.GCD_WEEK,\n" +
                "  a.GSSPM_BR_ID,\n" +
                "  a.GSSPM_DATE \n" +
                "  ) t4 ON t1.CLIENT = t4.CLIENT \n" +
                "  AND t1.GSSD_DATE = t4.GSSPM_DATE \n" +
                "  AND t1.GSSD_BR_ID = t4.GSSPM_BR_ID\n" +
                "  LEFT JOIN (\n" +
                "  SELECT\n" +
                "  t.CLIENT,\n" +
                "  t.GSSR_DATE,\n" +
                "  t.GCD_YEAR,\n" +
                "  t.GCD_WEEK,\n" +
                "  t.GSSR_BR_ID,\n" +
                "  count( t.GSSR_BILL_NO ) autoQty \n" +
                "  FROM\n" +
                "  (\n" +
                "  SELECT\n" +
                "    a.CLIENT,\n" +
                "    c.GCD_YEAR,\n" +
                "    c.GCD_WEEK,\n" +
                "    a.GSSR_DATE,\n" +
                "    a.GSSR_BR_ID,\n" +
                "    a.GSSR_BILL_NO \n" +
                "  FROM\n" +
                "    GAIA_SD_SR_RECORD a\n" +
                "    LEFT JOIN GAIA_SD_SALE_D b ON a.CLIENT = b.CLIENT \n" +
                "    AND a.GSSR_BILL_NO = b.GSSD_BILL_NO \n" +
                "    AND a.GSSR_BR_ID = b.GSSD_BR_ID \n" +
                "    AND a.GSSR_PRO_ID = b.GSSD_PRO_ID\n" +
                "    LEFT JOIN GAIA_CAL_DT c ON a.GSSR_DATE = c.GCD_DATE\n" +
                "    LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON pb.CLIENT = b.CLIENT \n" +
                "    AND pb.PRO_SELF_CODE = b.GSSD_PRO_ID \n" +
                "    AND pb.PRO_SITE = b.GSSD_BR_ID\n" +
                "    LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS cc ON cc.PRO_COMP_CODE = pb.PRO_COMPCLASS\n" +
                "    LEFT JOIN GAIA_FRANCHISEE d ON d.CLIENT = a.CLIENT\n" +
                "    LEFT JOIN GAIA_STORE_DATA st ON a.GSSR_BR_ID = st.STO_CODE \n" +
                "    AND st.CLIENT = a.CLIENT \n" +
                "  WHERE\n" +
                "    GSSR_FLAG LIKE 'A%' and a.GSSR_DATE >= '" + inData.getStartDate() + "' AND a.GSSR_DATE <= '" + inData.getEndDate() + "' ");
        StringBuilder t61 = new StringBuilder().append(" GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  c.GCD_YEAR,\n" +
                "  c.GCD_WEEK,\n" +
                "  a.GSSR_BILL_NO,\n" +
                "  a.GSSR_DATE,\n" +
                "  a.GSSR_BR_ID \n" +
                "  ) t \n" +
                "  GROUP BY\n" +
                "  t.CLIENT,\n" +
                "  t.GCD_YEAR,\n" +
                "  t.GCD_WEEK,\n" +
                "  t.GSSR_DATE,\n" +
                "  t.GSSR_BR_ID \n" +
                "  ) t5 ON t1.CLIENT = t5.CLIENT \n" +
                "  AND t1.GSSD_DATE = t5.GSSR_DATE \n" +
                "  AND t1.GSSD_BR_ID = t5.GSSR_BR_ID\n" +
                "  LEFT JOIN (\n" +
                "  SELECT\n" +
                "  t.CLIENT,\n" +
                "  t.GSSR_DATE,\n" +
                "  t.GCD_YEAR,\n" +
                "  t.GCD_WEEK,\n" +
                "  t.GSSR_BR_ID,\n" +
                "  count( t.GSSR_BILL_NO ) manualQty \n" +
                "  FROM\n" +
                "  (\n" +
                "  SELECT\n" +
                "  a.CLIENT,\n" +
                "  c.GCD_YEAR,\n" +
                "  c.GCD_WEEK,\n" +
                "  a.GSSR_DATE,\n" +
                "  a.GSSR_BR_ID,\n" +
                "  a.GSSR_BILL_NO \n" +
                "  FROM\n" +
                "  GAIA_SD_SR_RECORD a\n" +
                "  LEFT JOIN GAIA_SD_SALE_D b ON a.CLIENT = b.CLIENT \n" +
                "  AND a.GSSR_BILL_NO = b.GSSD_BILL_NO \n" +
                "  AND a.GSSR_BR_ID = b.GSSD_BR_ID \n" +
                "  AND a.GSSR_PRO_ID = b.GSSD_PRO_ID\n" +
                "  LEFT JOIN GAIA_CAL_DT c ON a.GSSR_DATE = c.GCD_DATE\n" +
                "  LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON pb.CLIENT = b.CLIENT \n" +
                "  AND pb.PRO_SELF_CODE = b.GSSD_PRO_ID \n" +
                "  AND pb.PRO_SITE = b.GSSD_BR_ID\n" +
                "  LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS cc ON cc.PRO_COMP_CODE = pb.PRO_COMPCLASS\n" +
                "  LEFT JOIN GAIA_FRANCHISEE d ON d.CLIENT = a.CLIENT\n" +
                "  LEFT JOIN GAIA_STORE_DATA st ON a.GSSR_BR_ID = st.STO_CODE \n" +
                "  AND st.CLIENT = a.CLIENT \n" +
                "  WHERE\n" +
                "  GSSR_FLAG LIKE 'M%' and a.GSSR_DATE >= '" + inData.getStartDate() + "' AND a.GSSR_DATE <= '" + inData.getEndDate() + "' ");
        StringBuilder t71 = new StringBuilder().append(" GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  c.GCD_YEAR,\n" +
                "  c.GCD_WEEK,\n" +
                "  a.GSSR_BILL_NO,\n" +
                "  a.GSSR_DATE,\n" +
                "  a.GSSR_BR_ID \n" +
                "  ) t \n" +
                "  GROUP BY\n" +
                "  t.CLIENT,\n" +
                "  t.GCD_YEAR,\n" +
                "  t.GCD_WEEK,\n" +
                "  t.GSSR_DATE,\n" +
                "  t.GSSR_BR_ID \n" +
                "  ) t6 ON t1.CLIENT = t6.CLIENT \n" +
                "  AND t1.GSSD_DATE = t6.GSSR_DATE \n" +
                "  AND t1.GSSD_BR_ID = t6.GSSR_BR_ID\n" +
                "  LEFT JOIN (\n" +
                "  SELECT\n" +
                "  a.CLIENT,\n" +
                "  sum( d.GSSD_AMT ) associateSalesAmt,\n" +
                "  a.GSSS_BR_ID,\n" +
                "  a.GSSS_DATE,\n" +
                "  c.GCD_YEAR,\n" +
                "  c.GCD_WEEK,\n" +
                "  count( DISTINCT GSSD_BILL_NO ) successQty,\n" +
                "  sum( d.GSSD_AMT ) - sum( d.GSSD_MOV_PRICE ) associateGross,\n" +
                "  sum( d.GSSD_MOV_PRICE ) GSSD_MOV_PRICE \n" +
                "  FROM\n" +
                "  GAIA_SD_SR_SALE_RECORD a\n" +
                "  LEFT JOIN GAIA_CAL_DT c ON a.GSSS_DATE = c.GCD_DATE\n" +
                "  LEFT JOIN GAIA_SD_SALE_D d ON d.CLIENT = a.CLIENT \n" +
                "  AND d.GSSD_BILL_NO = a.GSSS_BILL_NO \n" +
                "  AND d.GSSD_PRO_ID = a.GSSS_PRO_ID \n" +
                "  AND d.GSSD_BR_ID = a.GSSS_BR_ID \n" +
                "  LEFT JOIN GAIA_PRODUCT_BUSINESS e on e.CLIENT = a.CLIENT and e.PRO_SELF_CODE = a.GSSS_PRO_ID and e.PRO_SITE = a.GSSS_BR_ID\n" +
                "  LEFT JOIN GAIA_STORE_DATA f on a.CLIENT=f.CLIENT and f.STO_CODE = a.GSSS_BR_ID \n" +
                "  LEFT JOIN GAIA_FRANCHISEE g on g.CLIENT = a.CLIENT\n" +
                "  LEFT JOIN GAIA_PRODUCT_COMPONENT_CLASS h on e.PRO_COMPCLASS  = h.PRO_COMP_CODE\n" +
                "  WHERE a.GSSS_DATE  >= '" + inData.getStartDate() + "' AND a.GSSS_DATE  <= '" + inData.getEndDate() + "' ");

        //查询条件 成分大类
        if (StringUtils.isNotBlank(inData.getProCompBigCode())) {
            t1.append(" and g.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t21.append(" and g.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t31.append(" and g.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t51.append(" and cc.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t61.append(" and cc.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");
            t71.append(" and h.PRO_COMP_BIG_CODE in (" + inData.getProCompBigCode() + ")");

        }
        //查询条件 成分中类
        if (StringUtils.isNotBlank(inData.getProCompMidCode())) {
            t1.append(" and g.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t21.append(" and g.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t31.append(" and g.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t51.append(" and cc.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t61.append(" and cc.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");
            t71.append(" and h.PRO_COMP_MID_CODE in (" + inData.getProCompMidCode() + ")");

        }
        //查询条件 成分小类
        if (StringUtils.isNotBlank(inData.getProCompLitCode())) {
            t1.append(" and g.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t21.append(" and g.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t31.append(" and g.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t51.append(" and cc.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t61.append(" and cc.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");
            t71.append(" and h.PRO_COMP_LIT_CODE in (" + inData.getProCompLitCode() + ")");

        }
        //查询条件 成分分类
        if (StringUtils.isNotBlank(inData.getProCompCode())) {
            t1.append(" and g.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t21.append(" and g.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t31.append(" and g.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t51.append(" and cc.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t61.append(" and cc.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");
            t71.append(" and h.PRO_COMP_CODE in (" + inData.getProCompCode() + ")");

        }
        //查询条件 加盟商
        if (StringUtils.isNotBlank(inData.getClientStr())) {
            t1.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t21.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t31.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t41.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t51.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t61.append(" and a.CLIENT in (" + inData.getClientStr() + ")");
            t71.append(" and a.CLIENT in (" + inData.getClientStr() + ")");

        }
        if (StringUtils.isNotBlank(inData.getFrancType1()) ) {
            t1.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t21.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t31.append(" and ( c.FRANC_TYPE1 = " + inData.getFrancType1());
            t51.append(" and ( d.FRANC_TYPE1 = " + inData.getFrancType1());
            t61.append(" and ( d.FRANC_TYPE1 = " + inData.getFrancType1());
            t71.append(" and ( g.FRANC_TYPE1 = " + inData.getFrancType1());
            //查询条件 连锁公司
            if (StringUtils.isNotBlank(inData.getFrancType2())) {
                t1.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t21.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t31.append(" or c.FRANC_TYPE2 = " + inData.getFrancType2());
                t51.append(" or d.FRANC_TYPE2 = " + inData.getFrancType2());
                t61.append(" or d.FRANC_TYPE2 = " + inData.getFrancType2());
                t71.append(" or g.FRANC_TYPE2 = " + inData.getFrancType2());
            }
            //查询条件 批发公司
            if (StringUtils.isNotBlank(inData.getFrancType3())) {
                t1.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t21.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t31.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                t51.append(" or d.FRANC_TYPE3 = " + inData.getFrancType3());
                t61.append(" or d.FRANC_TYPE3 = " + inData.getFrancType3());
                t71.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
            }
            t1.append(" ) ");
            t21.append(" ) ");
            t31.append(" ) ");
            t51.append(" ) ");
            t61.append(" ) ");
            t71.append(" ) ");
        } else if (StringUtils.isBlank(inData.getFrancType1()) && StringUtils.isNotBlank(inData.getFrancType2())) {
            //查询条件 连锁公司
            if (StringUtils.isNotBlank(inData.getFrancType2())) {
                t1.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t21.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t31.append(" and ( c.FRANC_TYPE2 = " + inData.getFrancType2());
                t51.append(" and ( d.FRANC_TYPE2 = " + inData.getFrancType2());
                t61.append(" and ( d.FRANC_TYPE2 = " + inData.getFrancType2());
                t71.append(" and ( g.FRANC_TYPE2 = " + inData.getFrancType2());

                //查询条件 批发公司
                if (StringUtils.isNotBlank(inData.getFrancType3())) {
                    t1.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t21.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t31.append(" or c.FRANC_TYPE3 = " + inData.getFrancType3());
                    t51.append(" or d.FRANC_TYPE3 = " + inData.getFrancType3());
                    t61.append(" or d.FRANC_TYPE3 = " + inData.getFrancType3());
                    t71.append(" or g.FRANC_TYPE3 = " + inData.getFrancType3());
                }
            }
            t1.append(" ) ");
            t21.append(" ) ");
            t31.append(" ) ");
            t51.append(" ) ");
            t61.append(" ) ");
            t71.append(" ) ");
        } else if (StringUtils.isBlank(inData.getFrancType1()) && StringUtils.isBlank(inData.getFrancType2()) && StringUtils.isNotBlank(inData.getFrancType3())) {
            t1.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t21.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t31.append(" and c.FRANC_TYPE3 = " + inData.getFrancType3());
            t51.append(" and d.FRANC_TYPE3 = " + inData.getFrancType3());
            t61.append(" and d.FRANC_TYPE3 = " + inData.getFrancType3());
            t71.append(" and g.FRANC_TYPE3 = " + inData.getFrancType3());
        }
        //查询条件 门店
        if (CollectionUtil.isNotEmpty(inData.getStoCodeList())) {
            List<String> stoCodeList = inData.getStoCodeList();
            t1.append(" AND ");
            t21.append(" AND ");
            t31.append(" AND ");
            t41.append(" AND ");
            t51.append(" AND ");
            t61.append(" AND ");
            t71.append(" AND ");
            for (int i = 0; i < stoCodeList.size(); i++) {
                String[] arr = stoCodeList.get(i).split("-");
                if (i == stoCodeList.size() - 1) {
                    t1.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t21.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t31.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "')");
                    t41.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSPM_BR_ID = '" + arr[1] + "')");
                    t51.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSR_BR_ID = '" + arr[1] + "')");
                    t61.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSR_BR_ID = '" + arr[1] + "')");
                    t71.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSS_BR_ID = '" + arr[1] + "')");
                } else {
                    t1.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t21.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t31.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSD_BR_ID = '" + arr[1] + "') or ");
                    t41.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSPM_BR_ID = '" + arr[1] + "') or ");
                    t51.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSR_BR_ID = '" + arr[1] + "') or ");
                    t61.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSR_BR_ID = '" + arr[1] + "') or ");
                    t71.append("(a.CLIENT = '" + arr[0] + "' AND a.GSSS_BR_ID = '" + arr[1] + "') or ");
                }
            }
        }
        //查询条件 店型
        if (StringUtils.isNotBlank(inData.getGsstVersionStr())) {
            t1.append(" and d.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t21.append(" and d.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t31.append(" and d.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t51.append(" and st.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t61.append(" and st.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");
            t71.append(" and f.GSST_VERSION in (" + inData.getGsstVersionStr() + ")");

        }
        t1.append(t211).append(t21).append(t311).append(t312).append(t31).append(t41).append(t51).append(t61).append(t71);

        StringBuilder end = new StringBuilder().append(" GROUP BY\n" +
                "  a.CLIENT,\n" +
                "  c.GCD_YEAR,\n" +
                "  c.GCD_WEEK,\n" +
                "  a.GSSS_BR_ID,\n" +
                "  a.GSSS_DATE \n" +
                " ) t7 ON t7.CLIENT = t1.CLIENT \n" +
                " AND t7.GSSS_DATE = t1.GSSD_DATE \n" +
                " AND t7.GSSS_BR_ID = t1.GSSD_BR_ID " +
                " GROUP by t1.GCD_YEAR, t2.salesDayQty, t2.salesStoresQty");
        StringBuilder order = new StringBuilder().append(" order by t1.GCD_YEAR");

        //公司维度
        if ("1".equals(inData.getDimension())) {
            head1.append(",t1.CLIENT clientId,t1.francName ");
            end.append(",t1.CLIENT,t1.francName ");
            order.append(",t1.CLIENT,t1.francName ");

        }
        //门店维度
        if ("2".equals(inData.getDimension())) {
            head1.append(",t1.CLIENT clientId,t1.GSSD_BR_ID gssdBrId,t1.stoName,t1.francName ");
            end.append(",t1.CLIENT,t1.GSSD_BR_ID,t1.stoName,t1.francName");
            order.append(",t1.CLIENT,t1.GSSD_BR_ID,t1.stoName");

        }
        //省级维度
        if ("3".equals(inData.getDimension())) {
            head1.append(",COALESCE(t1.province,'') province  ,COALESCE(t1.provinceName,'') provinceName ");
            end.append(",t1.province ,t1.provinceName");
            order.append(",t1.province ,t1.provinceName");

        }
        //市级维度
        if ("4".equals(inData.getDimension())) {
            head1.append(",COALESCE(t1.province,'') province ,COALESCE(t1.provinceName,'') provinceName ,COALESCE(t1.city,'')city ,COALESCE(t1.cityName,'')cityName ");
            end.append(",t1.province ,t1.provinceName ,t1.city ,t1.cityName ");
            order.append(",t1.province ,t1.provinceName ,t1.city ,t1.cityName ");

        }

        //周报表
        if ("week".equals(inData.getWeekOrMonth())) {
            head1.append(",t1.GCD_WEEK w");
            end.append(",t1.GCD_WEEK ");
            order.append(",t1.GCD_WEEK");
            //周
            t312.append("  AND t1.GCD_WEEK = t2.GCD_WEEK \n");

        } else {
            //月度报表
            head1.append(",t1.GCD_MONTH m");
            end.append(",t1.GCD_MONTH");
            order.append(",t1.GCD_MONTH");
            //周
            t312.append("  AND t1.GCD_MONTH = t2.GCD_MONTH \n");

        }
        StringBuilder sql1 = head1.append(t1).append(end).append(order);
        log.info("sql1统计数据：{}", sql1.toString());
        List<WeeklySalesReportInfo> reportOutData = kylinJdbcTemplate.query(sql1.toString(), RowMapper.getDefault(WeeklySalesReportInfo.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(reportOutData));


        return reportOutData;
    }

    private void setSingle(List<MonthData> thisYearList, List<MonthData> thisYearSalesDayList) {
        int i = 0;
        for (MonthData monthData : thisYearList) {
            BigDecimal salesDay = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(thisYearSalesDayList)) {
                for (MonthData data : thisYearSalesDayList) {
                    if (monthData.getDate().equals(data.getDate())) {
                        salesDay = data.getSalesDay();
                        monthData.setSalesDay(salesDay);
                        break;
                    }
                }
            }
            monthData.setDate((i + 1) + "月");
            //单店日均销售额
            BigDecimal singleSalesAmt = divide(monthData.getSalesAmt(), salesDay);
            monthData.setSingleSalesAmt(singleSalesAmt.multiply(new BigDecimal("10000")));
            //单店日均交易次数
            BigDecimal singleTransactionQty = divide(monthData.getTransactionQty(), salesDay);
            monthData.setSingleTransactionQty(singleTransactionQty);
            //客单价 实收金额 / 交易次数
            BigDecimal price = divide(monthData.getSalesAmt(), monthData.getTransactionQty());
            monthData.setPrice(price.multiply(new BigDecimal("10000")));
            i++;
        }
    }

    private void getSum(BusinessReportRequest inData, String lastYear, String lastStartDate, String lastEndDate, List<MonthData> lastYearDate) {
        List<MonthDataKL> lastMonth = listMonthDate(inData.getClient(), lastStartDate, lastEndDate, inData.getStoCode());
        Map<String, List<MonthDataKL>> lastY = lastMonth.stream().collect(Collectors.groupingBy(monthDataKL -> monthDataKL.getGSSDDATE().substring(0, 7)));
        Set<String> lastK = lastY.keySet();
        String lastMonthParam = "";
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                lastMonthParam = lastYear + "-0" + i;
            } else {
                lastMonthParam = lastYear + "-" + i;
            }
            MonthData lastMonthD = new MonthData();
            lastMonthD.setDate(lastMonthParam);
            for (String key : lastK) {
                if (lastMonthParam.equals(key)) {
                    List<MonthDataKL> monthDataKLS = lastY.get(key);
                    BigDecimal salesSum = monthDataKLS.stream().map(MonthDataKL -> {
                        BigDecimal salesAmt = new BigDecimal(MonthDataKL.getSalesAmt());
                        return salesAmt;
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal("10000"));
                    lastMonthD.setSalesAmt(salesSum);
                    BigDecimal transactionQtySum = monthDataKLS.stream().map(MonthDataKL -> {
                        BigDecimal transactionQty = new BigDecimal(MonthDataKL.getTransactionQty());
                        return transactionQty;
                    }).reduce(BigDecimal.ZERO, BigDecimal::add);
                    lastMonthD.setTransactionQty(transactionQtySum);
                    break;
                }
            }
            lastYearDate.add(lastMonthD);
        }
    }

    private void addMonthDate(List<MonthData> lastYearDate, int i, MonthDataKL lastMonth) {
        MonthData lastDate = new MonthData();
        lastDate.setDate(i + "月");
        lastDate.setSalesAmt(new BigDecimal(ObjectUtil.isNotEmpty(lastMonth.getSalesAmt()) ? lastMonth.getSalesAmt().toString() : "0"));
        lastDate.setTransactionQty(new BigDecimal(ObjectUtil.isNotEmpty(lastMonth.getTransactionQty()) ? lastMonth.getTransactionQty().toString() : "0"));
        lastYearDate.add(lastDate);
    }

    private List<MonthDataKL> listMonthDate(String client, String startDate, String endDate, String storCode) {
        StringBuilder builder = new StringBuilder().append("select sum(GSSD_AMT) salesAmt, count(DISTINCT GSSD_BILL_NO) transactionQty,GSSD_DATE GSSDDATE");
        builder.append(" from GAIA_SD_SALE_D where CLIENT = '" + client + "'");
        if (StringUtils.isNotEmpty(storCode)) {
            builder.append(" and  GSSD_BR_ID >= '" + storCode + "'");
        }
        if (StringUtils.isNotEmpty(startDate)) {
            builder.append(" and  GSSD_DATE >= '" + startDate + "'");
        }
        if (StringUtils.isNotEmpty(endDate)) {
            builder.append(" and  GSSD_DATE <= '" + endDate + "'");
        }
        builder.append(" group by GSSD_DATE");
        log.info("sql：{}", builder.toString());
        List<MonthDataKL> thisYearDate = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(MonthDataKL.class));
        return thisYearDate;
    }

    private void setMedicalMonthData(List<MonthData> lastVipYearList, List<MonthData> lastYearList) {
        for (MonthData thisVipYearData : lastVipYearList) {
            for (MonthData thisYearData : lastYearList) {
                if (thisVipYearData.getDate().equals(thisYearData.getDate())) {
                    //会员销售占比   会员销售/实收金额
                    BigDecimal medicalRate = divide(thisVipYearData.getSalesAmt(), thisYearData.getSalesAmt());
                    thisVipYearData.setMedicalRate(medicalRate);
                    //会员交易占比   会员交易次数/交易次数
                    BigDecimal transactionQtyRate = divide(thisVipYearData.getTransactionQty(), thisYearData.getTransactionQty());
                    thisVipYearData.setTransactionQtyRate(transactionQtyRate);
                    break;
                }
            }
        }
    }

    private void setMothData(List<MonthData> lastVipYearList, List<MonthData> lastYearList) {
        for (MonthData thisVipYearData : lastVipYearList) {
            for (MonthData thisYearData : lastYearList) {
                if (thisVipYearData.getDate().equals(thisYearData.getDate())) {
                    //会员销售占比   会员销售/实收金额
                    BigDecimal vipRate = divide(thisVipYearData.getSalesAmt(), thisYearData.getSalesAmt());
                    thisVipYearData.setVipRate(vipRate);
                    //会员交易占比   会员交易次数/交易次数
                    BigDecimal transactionQtyRate = divide(thisVipYearData.getTransactionQty(), thisYearData.getTransactionQty());
                    thisVipYearData.setTransactionQtyRate(transactionQtyRate);
                    break;
                }
            }
        }
    }

    private List<MonthData> getMonthData(BusinessReportRequest inData, String lastYear) {
        List<MonthData> lastYearList = businessReportMapper.listMonthSalesInfo(lastYear, inData.getClient(), inData.getStoCode());
        lastYearList.forEach(lastYearData -> {
            //单店日均销售
            BigDecimal singleSalesAmt = lastYearData.getSalesAmt().compareTo(BigDecimal.ZERO) == 0 ? lastYearData.getSalesAmt() : lastYearData.getSalesAmt().multiply(new BigDecimal("10000")).divide(lastYearData.getSalesDay(), 4, BigDecimal.ROUND_HALF_UP);
            lastYearData.setSingleSalesAmt(singleSalesAmt);
            //单店日均交易次数
            BigDecimal singleTransactionQty = lastYearData.getTransactionQty().compareTo(BigDecimal.ZERO) == 0 ? lastYearData.getTransactionQty() : lastYearData.getTransactionQty().divide(lastYearData.getSalesDay(), 4, BigDecimal.ROUND_HALF_UP);
            lastYearData.setSingleTransactionQty(singleTransactionQty);
            //客单价
            BigDecimal price = lastYearData.getSalesAmt().compareTo(BigDecimal.ZERO) == 0 ? lastYearData.getSalesAmt() : lastYearData.getSalesAmt().multiply(new BigDecimal("10000")).divide(lastYearData.getTransactionQty(), 4, BigDecimal.ROUND_HALF_UP);
            lastYearData.setPrice(price);

        });
        return lastYearList;
    }

    private MedicalInsuranceSales getMedicalInsuranceSales(OverallSales overallSales, OverallSales lastOverallSales, OverallSales lastYearOverallSales, MedicalInsuranceSales medicalSales, MedicalInsuranceSales lastMedicalSales, MedicalInsuranceSales lastYearMedicalSales) {
        MedicalInsuranceSales medical = new MedicalInsuranceSales();
        BigDecimal salesAmt = BigDecimal.ZERO;
        BigDecimal medTransactionRate = BigDecimal.ZERO;
        BigDecimal transactionQty = BigDecimal.ZERO;
        BigDecimal medSalesAmtRate = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(medicalSales)) {
            salesAmt = ObjectUtil.isNotEmpty(medicalSales.getSalesAmt()) ? medicalSales.getSalesAmt() : BigDecimal.ZERO;
            medical.setSalesAmt(salesAmt);
            //本月医销比
            medSalesAmtRate = divide(medicalSales.getSalesAmt(), overallSales.getSalesAmt());
            medical.setMedSalesAmtRate(medSalesAmtRate);
            //本月医保交易次数
            transactionQty = ObjectUtil.isNotEmpty(medicalSales.getTransactionQty()) ? medicalSales.getTransactionQty() : BigDecimal.ZERO;
            medical.setTransactionQty(transactionQty);
            //本月医交比
            medTransactionRate = divide(medicalSales.getTransactionQty(), overallSales.getTransactionQty());
            medical.setMedTransactionRate(medTransactionRate);

        }
        if (ObjectUtil.isNotEmpty(lastMedicalSales)) {
            BigDecimal lastSalesAmt = ObjectUtil.isNotEmpty(lastMedicalSales.getSalesAmt()) ? lastMedicalSales.getSalesAmt() : BigDecimal.ZERO;
            //上月会员销售额
            medical.setLastSalesAmt(lastSalesAmt);
            //上月会员销售额环比
            BigDecimal lastSalesAmtRate = chainComparison(salesAmt, lastSalesAmt);
            medical.setLastSalesAmtRate(lastSalesAmtRate);
            //上月会销比
            medical.setLastMedSalesAmtRate(divide(lastSalesAmt, lastOverallSales.getSalesAmt()));
            //上月会销比-环比
            BigDecimal lastMedSalesAmtRateHB = chainComparison(medical.getMedSalesAmtRate(), medical.getLastMedSalesAmtRate());
            medical.setLastMedSalesAmtRateHB(lastMedSalesAmtRateHB);
            //上月会员交易次数
            medical.setLastTransactionQty(checkNull(lastMedicalSales.getTransactionQty()));
            //上月会员交易次数-环比
            BigDecimal lastTransactionQtyRate = chainComparison(transactionQty, lastMedicalSales.getTransactionQty());
            medical.setLastTransactionQtyRate(lastTransactionQtyRate);
            //上月会交比
            BigDecimal lastMedTransactionRate = divide(lastMedicalSales.getTransactionQty(), lastOverallSales.getTransactionQty());
            medical.setLastMedTransactionRate(lastMedTransactionRate);
            //上月会交比-环比
            BigDecimal lastMedTransactionRateHB = chainComparison(medTransactionRate, lastMedTransactionRate);
            medical.setLastMedTransactionRateHB(lastMedTransactionRateHB);
        }

        if (ObjectUtil.isNotEmpty(lastYearMedicalSales)) {
            BigDecimal lastYearSalesAmt = ObjectUtil.isNotEmpty(lastYearMedicalSales.getSalesAmt()) ? lastYearMedicalSales.getSalesAmt() : BigDecimal.ZERO;
            //同期会员销售额
            medical.setLastYearSalesAmt(lastYearSalesAmt);
            //同期会员销售额同比
            BigDecimal lastYearSalesAmtRate = chainComparison(salesAmt, lastYearSalesAmt);
            medical.setLastYearMedSalesAmtRate(lastYearSalesAmtRate);
            //同期会销比
            BigDecimal lastYearMedSalesAmtRate = divide(lastYearSalesAmt, lastYearOverallSales.getSalesAmt());
            medical.setLastYearSalesAmtRate(lastYearMedSalesAmtRate);
            //同期会销比-同比
            BigDecimal lastYearMedSalesAmtRateTB = chainComparison(medSalesAmtRate, lastYearMedSalesAmtRate);
            medical.setLastYearMedSalesAmtRateTB(lastYearMedSalesAmtRateTB);
            //同期会员交易次数
            medical.setLastYearTransactionQty(checkNull(lastYearMedicalSales.getTransactionQty()));
            //同期会员交易次数-同比
            BigDecimal lastYearTransactionQtyRate = chainComparison(transactionQty, lastYearMedicalSales.getTransactionQty());
            medical.setLastYearTransactionQtyRate(lastYearTransactionQtyRate);
            //同期会交比
            BigDecimal lastYearMedTransactionRate = divide(lastYearMedicalSales.getTransactionQty(), lastYearOverallSales.getTransactionQty());
            medical.setLastYearMedTransactionRate(lastYearMedTransactionRate);
            //同期会交比同比
            BigDecimal lastYearVipTransactionRateTB = chainComparison(medTransactionRate, lastYearMedTransactionRate);
            medical.setLastYearMedTransactionRateTB(lastYearVipTransactionRateTB);
        }
        return medical;
    }

    private VIPInfo getVipInfo(OverallSales overallSales, OverallSales lastOverallSales, OverallSales lastYearOverallSales, VIPInfo vipInfo, VIPInfo lastVipInfo, VIPInfo lastYearVipInfo) {
        VIPInfo vip = new VIPInfo();
        BigDecimal salesAmt = BigDecimal.ZERO;
        BigDecimal vipTransactionRate = BigDecimal.ZERO;
        BigDecimal transactionQty = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(vipInfo)) {
            salesAmt = ObjectUtil.isNotEmpty(vipInfo.getSalesAmt()) ? vipInfo.getSalesAmt() : BigDecimal.ZERO;
            vip.setSalesAmt(salesAmt);
            //本月会销比
            vip.setVipSalesAmtRate(overallSales.getSalesAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : vipInfo.getSalesAmt().divide(overallSales.getSalesAmt(), 4, BigDecimal.ROUND_HALF_UP));
            //本月会员交易次数
            transactionQty = vipInfo.getTransactionQty();
            vip.setTransactionQty(transactionQty);
            //本月会交比
            vipTransactionRate = overallSales.getTransactionQty().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : vipInfo.getTransactionQty().divide(overallSales.getTransactionQty(), 4, BigDecimal.ROUND_HALF_UP);
            vip.setVipTransactionRate(vipTransactionRate);

        }
        if (ObjectUtil.isNotEmpty(lastVipInfo)) {
            BigDecimal lastSalesAmt = ObjectUtil.isNotEmpty(lastVipInfo.getSalesAmt()) ? lastVipInfo.getSalesAmt() : BigDecimal.ZERO;
            //上月会员销售额
            vip.setLastSalesAmt(lastSalesAmt);
            //上月会员销售额环比
            BigDecimal lastSalesAmtRate = chainComparison(salesAmt, lastSalesAmt);
            vip.setLastSalesAmtRate(lastSalesAmtRate);
            //上月会销比
            vip.setLastVipSalesAmtRate(lastOverallSales.getSalesAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : lastSalesAmt.divide(lastOverallSales.getSalesAmt(), 4, BigDecimal.ROUND_HALF_UP));
            //上月会销比-环比
            BigDecimal lastVipSalesAmtRateHB = chainComparison(vip.getVipSalesAmtRate(), vip.getLastVipSalesAmtRate());
            vip.setLastVipSalesAmtRateHB(lastVipSalesAmtRateHB);
            //上月会员交易次数
            vip.setLastTransactionQty(lastVipInfo.getTransactionQty());
            //上月会员交易次数-环比
            BigDecimal lastTransactionQtyRate = chainComparison(transactionQty, lastVipInfo.getTransactionQty());
            vip.setLastTransactionQtyRate(lastTransactionQtyRate);
            //上月会交比
            BigDecimal lastVipTransactionRate = divide(lastVipInfo.getTransactionQty(), lastOverallSales.getTransactionQty());
            vip.setLastVipTransactionRate(lastVipTransactionRate);
            //上月会交比-环比
            BigDecimal lastVipTransactionRateHB = chainComparison(vipTransactionRate, lastVipTransactionRate);
            vip.setLastVipTransactionRateHB(lastVipTransactionRateHB);
        }

        if (ObjectUtil.isNotEmpty(lastYearVipInfo)) {
            BigDecimal lastYearSalesAmt = ObjectUtil.isNotEmpty(lastYearVipInfo.getSalesAmt()) ? lastYearVipInfo.getSalesAmt() : BigDecimal.ZERO;
            //同期会员销售额
            vip.setLastYearSalesAmt(lastYearSalesAmt);
            //同期会员销售额同比
            BigDecimal lastYearSalesAmtRate = chainComparison(salesAmt, lastYearSalesAmt);
            vip.setLastYearSalesAmtRate(lastYearSalesAmtRate);
            //同期会销比
            BigDecimal lastYearVipSalesAmtRate = divide(lastYearSalesAmt, lastYearOverallSales.getSalesAmt());
            vip.setLastYearVipSalesAmtRate(lastYearVipSalesAmtRate);
            //同期会销比-同比
            BigDecimal lastYearVipSalesAmtRateTB = chainComparison(vipInfo.getVipSalesAmtRate(), lastYearVipSalesAmtRate);
            vip.setLastYearVipSalesAmtRateTB(lastYearVipSalesAmtRateTB);
            //同期会员交易次数
            vip.setLastYearTransactionQty(checkNull(lastYearVipInfo.getTransactionQty()));
            //同期会员交易次数-同比
            BigDecimal lastYearTransactionQtyRate = chainComparison(transactionQty, lastYearVipInfo.getTransactionQty());
            vip.setLastYearTransactionQtyRate(lastYearTransactionQtyRate);
            //同期会交比
            BigDecimal lastYearVipTransactionRate = divide(lastYearVipInfo.getTransactionQty(), lastYearOverallSales.getTransactionQty());
            vip.setLastYearVipTransactionRate(lastYearVipTransactionRate);
            //同期会交比同比
            BigDecimal lastYearVipTransactionRateTB = chainComparison(vipTransactionRate, lastYearVipTransactionRate);
            vip.setLastYearVipTransactionRateTB(lastYearVipTransactionRateTB);
        }
        return vip;
    }

    private BigDecimal divide(BigDecimal lastYearSalesAmt, BigDecimal salesAmt2) {
        lastYearSalesAmt = ObjectUtil.isNotEmpty(lastYearSalesAmt) ? lastYearSalesAmt : BigDecimal.ZERO;
        salesAmt2 = ObjectUtil.isNotEmpty(salesAmt2) ? salesAmt2 : BigDecimal.ZERO;
        return salesAmt2.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : lastYearSalesAmt.divide(salesAmt2, 4, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal divide(Double lastYearSalesAmt, Double salesAmt2) {
        lastYearSalesAmt = ObjectUtil.isNotEmpty(lastYearSalesAmt) ? lastYearSalesAmt : 0.0;
        salesAmt2 = ObjectUtil.isNotEmpty(salesAmt2) ? salesAmt2 : 0.0;
        return new BigDecimal(salesAmt2.toString()).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(lastYearSalesAmt.toString()).divide(new BigDecimal(salesAmt2.toString()), 4, BigDecimal.ROUND_HALF_UP);
    }

    private SingleStoreInfo getSingleStoreInfo(BusinessReportResponse response, OverallSales overallSales, OverallSales lastOverallSales, OverallSales lastYearOverallSales) {
        SingleStoreInfo singleStoreInfo = new SingleStoreInfo();
        BigDecimal salesAmt = BigDecimal.ZERO;
        BigDecimal transactionQty = BigDecimal.ZERO;
        BigDecimal unitPrice = BigDecimal.ZERO;

        //本月销售额
        if (ObjectUtil.isNotEmpty(overallSales)) {
            salesAmt = divide(checkNull(overallSales.getSalesAmt()).multiply(new BigDecimal("10000")), new BigDecimal(String.valueOf(response.getSalesDay())));
            singleStoreInfo.setSalesAmt(salesAmt);
            //本月交易次数
            transactionQty = divide(checkNull(overallSales.getTransactionQty()), new BigDecimal(String.valueOf(response.getSalesDay())));
            singleStoreInfo.setTransactionQty(transactionQty);
            //本月客单价
            unitPrice = divide(salesAmt, transactionQty);
            singleStoreInfo.setUnitPrice(unitPrice);
        }
        if (ObjectUtil.isNotEmpty(lastOverallSales)) {
            //上月销售额
            BigDecimal lastSalesAmt = divide(lastOverallSales.getSalesAmt().multiply(new BigDecimal("10000")), lastOverallSales.getSalesDay());
            singleStoreInfo.setLastSalesAmt(lastSalesAmt);
            //上月销售额环比
            BigDecimal lastSalesAmtRate = chainComparison(salesAmt, lastSalesAmt);
            singleStoreInfo.setLastSalesAmtRate(lastSalesAmtRate);
            //上月交易次数
            BigDecimal lastTransactionQty = divide(checkNull(lastOverallSales.getTransactionQty()), new BigDecimal(String.valueOf(lastOverallSales.getSalesDay())));
            ;
            singleStoreInfo.setLastTransactionQty(lastTransactionQty);
            //上月交易次数环比
            BigDecimal lastTransactionQtyRate = chainComparison(transactionQty, lastTransactionQty);
            singleStoreInfo.setLastTransactionQtyRate(lastTransactionQtyRate);
            //上月客单价
            BigDecimal lastUnitPrice = divide(lastSalesAmt, lastTransactionQty);
            singleStoreInfo.setLastUnitPrice(lastUnitPrice);
            //上月客单价环比
            BigDecimal lastUnitPriceRate = chainComparison(unitPrice, lastUnitPrice);
            singleStoreInfo.setLastUnitPriceRate(lastUnitPriceRate);
        }
        if (ObjectUtil.isNotEmpty(lastYearOverallSales)) {
            //同期销售额
            BigDecimal lastYearSalesAmt = divide(lastYearOverallSales.getSalesAmt().multiply(new BigDecimal("10000")), lastYearOverallSales.getSalesDay());
            singleStoreInfo.setLastYearSalesAmt(lastYearSalesAmt);
            //同期销售额环比
            BigDecimal lastYearSalesAmtRate = chainComparison(salesAmt, lastYearSalesAmt);
            singleStoreInfo.setLastYearSalesAmtRate(lastYearSalesAmtRate);
            //同期交易次数
            BigDecimal lastYearTransactionQty = divide(lastYearOverallSales.getTransactionQty(), lastYearOverallSales.getSalesDay());
            singleStoreInfo.setLastYearTransactionQty(lastYearTransactionQty);
            //同期交易次数环比
            BigDecimal lastYearTransactionQtyRate = chainComparison(transactionQty, lastYearTransactionQty);
            singleStoreInfo.setLastYearTransactionQtyRate(lastYearTransactionQtyRate);
            //同期客单价
            BigDecimal lastYearUnitPrice = divide(lastYearSalesAmt, lastYearTransactionQty);
            singleStoreInfo.setLastYearUnitPrice(lastYearUnitPrice);
            //同期客单价环比
            BigDecimal lastYearUnitPriceRate = chainComparison(unitPrice, lastYearUnitPrice);
            singleStoreInfo.setLastYearUnitPriceRate(lastYearUnitPriceRate);
        }
        return singleStoreInfo;
    }

    /**
     *
     * @param overallSales 本月
     * @param lastOverallSales 上期
     * @param lastYearOverallSales 同期
     */
    private void setOverallSales(OverallSales overallSales, OverallSales lastOverallSales, OverallSales lastYearOverallSales) {
        if (ObjectUtil.isNotEmpty(overallSales)) {
            if (ObjectUtil.isNotEmpty(lastOverallSales)) {
                overallSales.setLastSalesAmt(checkNull(lastOverallSales.getSalesAmt()));
                overallSales.setLastGross(checkNull(lastOverallSales.getGross()));
                overallSales.setLastGRate(checkNull(lastOverallSales.getGRate()));
            }
            if (ObjectUtil.isNotEmpty(lastYearOverallSales)) {
                overallSales.setLastYearSalesAmt(checkNull(lastYearOverallSales.getSalesAmt()));
                overallSales.setLastYearGross(checkNull(lastYearOverallSales.getGross()));
                overallSales.setLastYearGRate(checkNull(lastYearOverallSales.getGRateR()));
            }
        }
        if (ObjectUtil.isNotEmpty(lastOverallSales)) {
            //上月销售环比   （本月-上月）÷上月
            BigDecimal lastSalesAmtRate = chainComparison(overallSales.getSalesAmt(), lastOverallSales.getSalesAmt());
            overallSales.setLastSalesAmtRate(lastSalesAmtRate);
            //上月毛利环比
            BigDecimal lastGrossRate = chainComparison(overallSales.getGross(), lastOverallSales.getGross());
            overallSales.setLastGrossRate(lastGrossRate);
            //上月毛利率环比
            BigDecimal lastGRateR = chainComparison(overallSales.getGRate(), lastOverallSales.getGRate());
            overallSales.setLastGRateR(lastGRateR);

        }
        if (ObjectUtil.isNotEmpty(lastYearOverallSales)) {
            //同期销售同比
            BigDecimal lastYearSalesAmtRate = chainComparison(checkNull(overallSales.getSalesAmt()), checkNull(lastYearOverallSales.getSalesAmt()));
            overallSales.setLastYearSalesAmtRate(lastYearSalesAmtRate);
            //同期毛利同比
            BigDecimal lastYearGrossRate = chainComparison(checkNull(overallSales.getGross()), checkNull(lastYearOverallSales.getGross()));
            overallSales.setLastYearGrossRate(lastYearGrossRate);
            //同期毛利率同比
            BigDecimal lastYearGRateR = chainComparison(checkNull(overallSales.getGRateR()), checkNull(lastYearOverallSales.getGRateR()));
            overallSales.setLastYearGRateR(lastYearGRateR);
        }
    }

    private BigDecimal checkNull(BigDecimal num) {
        BigDecimal bigDecimal = ObjectUtil.isNotEmpty(num) ? num : BigDecimal.ZERO;
        return bigDecimal;
    }

    private BigDecimal checkNull(Double num) {
        BigDecimal bigDecimal = ObjectUtil.isNotEmpty(num) ? new BigDecimal(num.toString()) : BigDecimal.ZERO;
        return bigDecimal;
    }

    /**
     * 计算环比值 和 同比值
     * 环比 本月-上月
     * 同比 （本月-去年同期）
     *
     * @param SalesAmt
     * @param lastSalesAmt
     * @return
     */
    private BigDecimal subtract(BigDecimal SalesAmt, BigDecimal lastSalesAmt) {
        SalesAmt = ObjectUtil.isEmpty(SalesAmt) ? BigDecimal.ZERO : SalesAmt;
        lastSalesAmt = ObjectUtil.isEmpty(lastSalesAmt) ? BigDecimal.ZERO : lastSalesAmt;
        BigDecimal subtract = SalesAmt.subtract(lastSalesAmt);
        return subtract;
    }

    private BigDecimal subtract(Double SalesAmt, Double lastSalesAmt) {
        SalesAmt = ObjectUtil.isEmpty(SalesAmt) ? 0.0 : SalesAmt;
        lastSalesAmt = ObjectUtil.isEmpty(lastSalesAmt) ? 0.0 : lastSalesAmt;
        BigDecimal subtract = new BigDecimal(SalesAmt.toString()).subtract(new BigDecimal(lastSalesAmt.toString()));
        return subtract;
    }

    /**
     * 计算环比 和 同比
     * 环比 （本月-上月）÷上月
     * 同比 （本月-去年同期）÷去年同期
     *
     * @param SalesAmt
     * @param lastSalesAmt
     * @return
     */
    private BigDecimal chainComparison(BigDecimal SalesAmt, BigDecimal lastSalesAmt) {
        BigDecimal subtract = subtract(SalesAmt, lastSalesAmt);
        BigDecimal HB = divide(subtract, lastSalesAmt);
        return HB;
    }
}

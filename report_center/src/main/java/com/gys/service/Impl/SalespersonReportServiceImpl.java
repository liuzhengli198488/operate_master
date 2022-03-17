package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.gys.common.data.*;
import com.gys.common.exception.BusinessException;
import com.gys.common.kylin.RowMapper;
import com.gys.common.redis.RedisManager;
import com.gys.common.response.ResultEnum;
import com.gys.entity.*;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.commissionplan.StoreAvgAmt;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.entity.data.commissionplan.app.AppCommissionSummary;
import com.gys.entity.data.salesSummary.PersonSalesInData;
import com.gys.mapper.*;
import com.gys.service.PersonalSalesReportCommissionCalc;
import com.gys.service.SalespersonReportService;
import com.gys.util.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.weekend.Weekend;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings("all")
public class SalespersonReportServiceImpl implements SalespersonReportService, PersonalSalesReportCommissionCalc {

    private final String PERSONAL_SALE_KEY = "PERSONALSALE";

    private final String PERSONAL_SALE_KEY_V2 = "PERSONALSALE_V2";

    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;

    @Autowired
    private GaiaGlobalDataMapper gaiaGlobalDataMapper;

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private GaiaTichengPlanZMapper gaiaTichengPlanZMapper;

    @Resource
    private TichengProplanProNMapper tichengProplanProNMapper;

    @Resource
    private GaiaTichengPlanZMapper tichengPlanZMapper;

    @Resource
    private GaiaCalDateMapper gaiaCalDateMapper;

    @Resource
    private GaiaTichengSaleplanZMapper tichengSaleplanZMapper;

    @Resource
    private GaiaTichengRejectClassMapper tichengRejectClassMapper;

    @Resource
    private GaiaTichengRejectProMapper tichengRejectProMapper;

    @Resource
    private GaiaTichengSaleplanMMapper tichengSaleplanMMapper;

    @Override
    public Map<String, Object> personalSales(PersonalSaleInData inData) throws ParseException {
        String resultMapStr = (String) redisManager.get(PERSONAL_SALE_KEY + "-" + inData.getClient() + "-" + inData.getUserId() + "-" + inData.getType());
        Map resultMap = JSON.parseObject(resultMapStr, Map.class);
        if (ObjectUtil.isEmpty(resultMap)) {
            resultMap = getPersonSaleTable(inData);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            redisManager.set(PERSONAL_SALE_KEY + "-" + inData.getClient() + "-" + inData.getUserId() + "-" + inData.getType(), JSON.toJSONString(resultMap), (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000);
        }
        return resultMap;
    }


    @Override
    public Map<String, Object> personalSaleReportV2(PersonalSaleInData inData) throws ParseException {
        String key =
                PERSONAL_SALE_KEY_V2 + "-" + inData.getClient() + "-" + inData.getUserId() + "-" + inData.getType();
        String resultMapStr = (String) redisManager.get(key);
        Map resultMap = JSON.parseObject(resultMapStr, Map.class);
        if (ObjectUtil.isEmpty(resultMap)) {
            try {
                resultMap = getPersonSaleTableV2(inData);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(ResultEnum.UNKNOWN_ERROR.getMsg());
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            redisManager.set(key, JSON.toJSONString(resultMap), (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000);
        }
        return resultMap;
    }

    private HashMap<String, Map<String, Object>> getPersonSaleTableV2(PersonalSaleInData inData) {
        Date now = new Date();
        List<Map<String, Object>> currentTable = new ArrayList<>();
        List<Map<String, Object>> periodTable = new ArrayList<>();
        HashMap<String, Map<String, Object>> resultMap = new HashMap<>();
        Map<String, Object> amt = new HashMap<>();
        Map<String, Object> grossAmt = new HashMap<>();
        Map<String, Object> grossRate = new HashMap<>();
        Map<String, Object> payCount = new HashMap<>();
        Map<String, Object> kedanPrice = new HashMap<>();
        Map<String, Object> saleTicheng = new HashMap<>();
        Map<String, Object> proTicheng = new HashMap<>();
        resultMap.put("amt", amt);
        resultMap.put("grossAmt", grossAmt);
        resultMap.put("grossRate", grossRate);
        resultMap.put("payCount", payCount);
        resultMap.put("kedanPrice", kedanPrice);
        resultMap.put("saleTicheng", saleTicheng);
        resultMap.put("proTicheng", proTicheng);

        String type = inData.getType();
        LocalDate localDate = LocalDate.now();

        // 获取时间维度,讲具体日期映射成查询维度，日 月 周
        Weekend<GaiaCalDate> calDateWeekend = new Weekend<>(GaiaCalDate.class);
        calDateWeekend.weekendCriteria()
                .andGreaterThan(GaiaCalDate::getGcdDate,
                        localDate.minusYears(2).minusDays(1).toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY))
                .andLessThan(GaiaCalDate::getGcdDate, localDate.plusDays(1).toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY));
        List<GaiaCalDate> gaiaCalDates = gaiaCalDateMapper.selectByExample(calDateWeekend);
        Map<String, String> calDateMap = gaiaCalDates.stream()
                .collect(Collectors.toMap(GaiaCalDate::getGcdDate,
                        s -> {
                            if ("1".equals(type)) {
                                return s.getGcdDate();
                            } else if ("2".equals(type)) {
                                return s.getGcdWeek();
                            } else {
                                return s.getGcdMonth();
                            }
                        }));

        getDataV2(inData, currentTable, now, resultMap, calDateMap, "current");

        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        switch (inData.getType()) {
            case "1":
                instance.add(Calendar.WEEK_OF_YEAR, -1);
                now = instance.getTime();
                break;
            case "2":
            case "3":
                instance.add(Calendar.YEAR, -1);
                now = instance.getTime();
                break;
        }
        getDataV2(inData, periodTable, now, resultMap, calDateMap, "period");
        Map<String, Object> tichengTotal = getTotalData(resultMap.get("saleTicheng"), resultMap.get("proTicheng"), currentTable, periodTable);
        resultMap.put("tichengTotal", tichengTotal);
        return resultMap;
    }

    private HashMap<String, Object> getPersonSaleTable(PersonalSaleInData inData) throws ParseException {
        Date now = new Date();
        List<Map<String, Object>> currentTable = new ArrayList<>();
        List<Map<String, Object>> periodTable = new ArrayList<>();
        HashMap<String, Object> resultMap = new HashMap<>();
        Map<String, Object> amt = new HashMap<>();
        Map<String, Object> grossAmt = new HashMap<>();
        Map<String, Object> grossRate = new HashMap<>();
        Map<String, Object> payCount = new HashMap<>();
        Map<String, Object> kedanPrice = new HashMap<>();
        Map<String, Object> saleTicheng = new HashMap<>();
        Map<String, Object> proTicheng = new HashMap<>();
        resultMap.put("amt", amt);
        resultMap.put("grossAmt", grossAmt);
        resultMap.put("grossRate", grossRate);
        resultMap.put("payCount", payCount);
        resultMap.put("kedanPrice", kedanPrice);
        resultMap.put("saleTicheng", saleTicheng);
        resultMap.put("proTicheng", proTicheng);

        getData(inData, currentTable, now, resultMap, "current");

        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);

        switch (inData.getType()) {
            case "1":
                instance.add(Calendar.WEEK_OF_YEAR, -1);
                now = instance.getTime();
                break;
            case "2":
            case "3":
                instance.add(Calendar.YEAR, -1);
                now = instance.getTime();
                break;
        }

        getData(inData, periodTable, now, resultMap, "period");
        Map<String, Object> tichengTotal = getTotalData((Map<String, Object>) resultMap.get("saleTicheng"), (Map<String, Object>) resultMap.get("proTicheng"), currentTable, periodTable);
        resultMap.put("tichengTotal", tichengTotal);
        return resultMap;
    }

    private Map<String, Object> getTotalData(Map<String, Object> saleTicheng, Map<String, Object> proTicheng, List<Map<String, Object>> currentTable, List<Map<String, Object>> periodTable) {
        Map<String, Object> tichengTotal = new HashMap<>();
        List<Map<String, Object>> tichengTotalCur = copyList(currentTable);
        List<Map<String, Object>> tichengTotalPer = copyList(periodTable);
        summation(saleTicheng, proTicheng, tichengTotalCur, "current");
        summation(saleTicheng, proTicheng, tichengTotalPer, "period");
        setDefaultMap(tichengTotalCur);
        setDefaultMap(tichengTotalPer);
        tichengTotal.put("current", tichengTotalCur);
        tichengTotal.put("period", tichengTotalPer);
        return tichengTotal;
    }

    private void summation(Map<String, Object> saleTicheng, Map<String, Object> proTicheng, List<Map<String, Object>> tichengTotal, String timeType) {
        for (Map<String, Object> map : tichengTotal) {
            Object time = map.get("time");
            BigDecimal value = BigDecimal.ZERO;
            List<Map<String, Object>> saleCur = (List<Map<String, Object>>) saleTicheng.get(timeType);
            List<Map<String, Object>> proCur = (List<Map<String, Object>>) proTicheng.get(timeType);
            for (Map<String, Object> sale : saleCur) {
                if (sale.get("time").equals(time)) {
                    value = value.add(new BigDecimal(sale.get("value").toString()));
                }
            }
            for (Map<String, Object> pro : proCur) {
                if (pro.get("time").equals(time)) {
                    value = value.add(new BigDecimal(pro.get("value").toString()));
                }
            }
            map.put("value", value);
        }
    }

    private void getDataV2(PersonalSaleInData inData,
                           List<Map<String, Object>> table,
                           Date now,
                           Map<String, Map<String, Object>> resultMap,
                           Map<String, String> calDateMap,
                           String timeType) {
        String client = inData.getClient();
        String userId = inData.getUserId();
        String type = inData.getType();
        // 组装查询条件，时间范围
        Map<String, Object> selectParam = setSelectParam(type, now);
        // 设置默认返回值
        getTimeMapNew(table, type, now);
        // sql拼接从 kylin 获取营业额，交易次数，客单价
        StringBuilder sb = new StringBuilder()
                .append("SELECT\n")
                .append("\tgssd.GSSD_DATE saleDate,\n")
                .append("\tROUND( COALESCE(SUM( gssd.GSSD_AMT ),0), 2 ) amt,\n")
                .append("\tROUND(round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),4), 2) as grossProfitAmt,\n")
                .append("\tROUND(case sum(gssd.GSSD_AMT) when 0 then 0 else (ROUND(round(sum(gssd.GSSD_AMT),2) - " +
                        "round(sum(gssd.GSSD_MOV_PRICE),4), 2)) / sum(gssd.GSSD_AMT) end, 4) as  grossProfitRate,\n")
                .append("\tCOUNT( DISTINCT gssd.GSSD_BILL_NO ) payCount\n")
                .append("FROM GAIA_SD_SALE_D gssd\n")
                .append("WHERE gssd.CLIENT = '").append(client).append("'\n")
                .append("AND gssd.GSSD_SALER_ID = '").append(userId).append("'\n")
                .append(selectParam.get("queryTime"))
                .append("GROUP BY gssd.GSSD_DATE");
        List<AppCommissionSummary> saleInfos = kylinJdbcTemplate.query(sb.toString(), BeanPropertyRowMapper.newInstance(AppCommissionSummary.class));

        // 设置时间维度
        for (AppCommissionSummary saleInfo : saleInfos) {
            LocalDate gssdDate = saleInfo.getSaleDate();
            saleInfo.setTimeType(calDateMap.get(gssdDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY)));
        }

        // 根据时间维度分组
        Map<String, List<AppCommissionSummary>> saleInfoMap = saleInfos.stream()
                .collect(Collectors.groupingBy(AppCommissionSummary::getTimeType));

        // 初始化默认值
        List<Map<String, Object>> amts = copyList(table);
        List<Map<String, Object>> grossAmt = copyList(table);
        List<Map<String, Object>> grossRate = copyList(table);
        List<Map<String, Object>> payCounts = copyList(table);
        List<Map<String, Object>> customerPrices = copyList(table);
        List<Map<String, Object>> saleCommissions = copyList(table);
        List<Map<String, Object>> proCommissions = copyList(table);

        for (Map.Entry<String, List<AppCommissionSummary>> entry : saleInfoMap.entrySet()) {
            String key = entry.getKey();
            List<AppCommissionSummary> value = entry.getValue();
            // 营业额汇总

            AppCommissionSummary reduce = value.stream()
                    .reduce(new AppCommissionSummary(), AppCommissionSummary::summary, AppCommissionSummary::summary);
            BigDecimal amt = reduce.getAmt();

            // 交易次数 去重
            long payCount = reduce.getPayCount();
            setMapValue2(amts, amt, key);
            setMapValue2(payCounts, payCount, key);
            // 客单价
            setMapValue2(customerPrices, BigDecimalUtil.divide(amt, payCount, 2), key);
            // 毛利额
            setMapValue2(grossAmt, reduce.getGrossProfitAmt(), key);
            // 毛利率
            setMapValue2(grossRate, reduce.getGrossProfitRate(), key);
        }

        // 整合提成方案

        // 获取开始结束时间
        String startDate = com.gys.util.DateUtil.formatDate((Date) selectParam.get("startDate"));
        String endDate = com.gys.util.DateUtil.formatDate((Date) selectParam.get("endDate"));


        // 1.计算单品提成
        StoreCommissionSummaryDO storeCommissionSummaryDO = new StoreCommissionSummaryDO();
        storeCommissionSummaryDO.setPageSize(null);
        storeCommissionSummaryDO.setPageNum(null);
        storeCommissionSummaryDO.setClient(client);
        storeCommissionSummaryDO.setStartDate(startDate);
        storeCommissionSummaryDO.setEndDate(endDate);
        storeCommissionSummaryDO.setSummaryType(2);
        storeCommissionSummaryDO.setUserId(userId);
        Map<String, AppCommissionSummary> proCommissionPlan = calcProCommissionPlan(storeCommissionSummaryDO, calDateMap);
        if (MapUtil.isNotEmpty(proCommissionPlan)) {
            Set<Map.Entry<String, AppCommissionSummary>> entries = proCommissionPlan.entrySet();
            for (Map.Entry<String, AppCommissionSummary> entry : entries) {
                Object time = entry.getKey();
                AppCommissionSummary value = entry.getValue();
                setMapValue2(proCommissions, value.getCommissionAmt(), time);
            }
        }

        // 2.计算销售提成
        storeCommissionSummaryDO = new StoreCommissionSummaryDO();
        storeCommissionSummaryDO.setPageSize(null);
        storeCommissionSummaryDO.setPageNum(null);
        storeCommissionSummaryDO.setClient(client);
        storeCommissionSummaryDO.setStartDate(startDate);
        storeCommissionSummaryDO.setEndDate(endDate);
        storeCommissionSummaryDO.setSummaryType(2);
        storeCommissionSummaryDO.setUserId(userId);
        Map<String, AppCommissionSummary> saleCommissionPlan = calcSaleCommissionPlan(storeCommissionSummaryDO, calDateMap);
        if (MapUtil.isNotEmpty(saleCommissionPlan)) {
            Set<Map.Entry<String, AppCommissionSummary>> entries = saleCommissionPlan.entrySet();
            for (Map.Entry<String, AppCommissionSummary> entry : entries) {
                Object time = entry.getKey();
                AppCommissionSummary value = entry.getValue();
                setMapValue2(saleCommissions, value.getCommissionAmt(), time);
            }
        }

        setDefaultMap(amts);
        setDefaultMap(grossAmt);
        setDefaultMap(grossRate);
        setDefaultMap(payCounts);
        setDefaultMap(customerPrices);
        setDefaultMap(saleCommissions);
        setDefaultMap(proCommissions);

        resultMap.get("amt").put(timeType, amts);
        resultMap.get("grossAmt").put(timeType, grossAmt);
        resultMap.get("grossRate").put(timeType, grossRate);
        resultMap.get("payCount").put(timeType, payCounts);
        resultMap.get("kedanPrice").put(timeType, customerPrices);
        resultMap.get("saleTicheng").put(timeType, saleCommissions);
        resultMap.get("proTicheng").put(timeType, proCommissions);
    }

    /**
     * 销售提成汇总计算
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param calDateMap               calDateMap
     * @return Map<String, AppCommissionSummary>
     */
    private Map<String, AppCommissionSummary> calcSaleCommissionPlan(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                                     Map<String, String> calDateMap) {
        String startDate = storeCommissionSummaryDO.getStartDate();
        String endDate = storeCommissionSummaryDO.getEndDate();
        String client = storeCommissionSummaryDO.getClient();
        String userId = storeCommissionSummaryDO.getUserId();

        // 获取销售提成方案
        List<StoreCommissionSummary> selectSaleCommissionPlan = tichengSaleplanZMapper.selectSaleCommissionSummary(storeCommissionSummaryDO);
        List<AppCommissionSummary> tempSaleCommissionSummaries = new ArrayList<>();
        Map<String, List<String>> rejectProMap = new HashMap<>();
        Map<String, List<String>> rejectClassMap = new HashMap<>();

        LocalDate nowLocalDate = LocalDate.now();
        String nowDate = nowLocalDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        EmpSaleDetailInData inData = new EmpSaleDetailInData();
        inData.setClient(client);

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

            boolean flag = false;
            String tempEndDate = endDate;
            // 如果查询时间大于等于当天,
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
            if (assertInTimeRange(storeCommissionSummaryDO, nowDate)) {
                // 销售提成方案下商品数据，不包括剔除的
                String sqlSb = generateSaleCommissionSqlAndResult(storeCommissionSummaryDO, planIfNegative);
                List<AppCommissionSummary> appCommissionSummaries = kylinJdbcTemplate.query(sqlSb, BeanPropertyRowMapper.newInstance(AppCommissionSummary.class));
                if (CollectionUtil.isNotEmpty(appCommissionSummaries)) {
                    for (AppCommissionSummary appCommissionSummary : appCommissionSummaries) {
                        AppCommissionSummary temp = new AppCommissionSummary();
                        BeanUtil.copyProperties(appCommissionSummary, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        BeanUtil.copyProperties(saleCommissionPlan, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        temp.setStoCode(appCommissionSummary.getStoCode());
                        tempSaleCommissionSummaries.add(temp);
                    }
                }
            }

            // 如果查询时间大于等于当天
            if (flag) {
                // 获取当天实时数据
                inData.setPlanId(planId);
                inData.setToday(nowDate);
                inData.setStoArr(storeCommissionSummaryDO.getStoCodes());
                inData.setNameSearchType("1");
                inData.setNameSearchIdList(Collections.singletonList(userId));
                List<EmpSaleDetailResVo> dbList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
                renderCommissionDataWithToday(saleCommissionPlan, dbList, tempSaleCommissionSummaries);
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

        if (CollectionUtil.isNotEmpty(tempSaleCommissionSummaries)) {

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
            Iterator<AppCommissionSummary> iterator = tempSaleCommissionSummaries.iterator();
            while (iterator.hasNext()) {
                AppCommissionSummary appCommissionSummary = iterator.next();
                Integer planId = appCommissionSummary.getPlanId();

                LocalDate saleDate = appCommissionSummary.getSaleDate();
                totalSaleDays.add(saleDate.toString());
                // 如果设置了剔除折扣率
                String planRejectDiscountRateSymbol = appCommissionSummary.getPlanRejectDiscountRateSymbol();
                String planRejectDiscountRate = appCommissionSummary.getPlanRejectDiscountRate();

                appCommissionSummary.setTimeType(calDateMap.get(saleDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY)));

                BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getZkAmt());
                BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getYsAmt());
                BigDecimal grossProfitRate = calcGrossProfitRate(appCommissionSummary);
                if (!appCommissionSummary.getRealTime()) {
                    // 不是实时数据计算
                    appCommissionSummary.setGrossProfitRate(grossProfitRate);
                    // 计算折扣率
                    appCommissionSummary.setZkl(calcDiscountRate(ysAmt, zkAmt));
                }
                if (StringUtils.isNotEmpty(planRejectDiscountRateSymbol) && StringUtils.isNotEmpty(planRejectDiscountRate)) {
                    BigDecimal zkl = appCommissionSummary.getZkl();
                    // 过滤不在折扣率范围之内的数据
                    if (filterNotInRange(planRejectDiscountRateSymbol, planRejectDiscountRate, zkl)) {
                        continue;
                    }
                }
                List<String> rejectPros = rejectProMap.get(String.valueOf(planId));
                if (CollectionUtil.isNotEmpty(rejectPros) && rejectPros.contains(appCommissionSummary.getProId())) {
                    continue;

                }
                List<String> rejectClass = rejectClassMap.get(String.valueOf(planId));
                if (CollectionUtil.isNotEmpty(rejectClass) && rejectClass.contains(appCommissionSummary.getProClassCode())) {
                    continue;

                }
                List<GaiaTichengSaleplanM> gaiaTichengSaleplanMS = commissionPlanRuleMap.get(String.valueOf(appCommissionSummary.getPlanId()));
                if (CollectionUtil.isNotEmpty(gaiaTichengSaleplanMS)) {
                    // 门店日均销售额
                    BigDecimal amtAvg =
                            BigDecimalUtil.toBigDecimal(storeAvgAmtMap.get(planId + appCommissionSummary.getStoCode()));
                    BigDecimal amt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getAmt());
                    Optional<GaiaTichengSaleplanM> first = gaiaTichengSaleplanMS.stream()
                            .filter(item ->
                                    amtAvg.compareTo(item.getMinDailySaleAmt()) > 0 &&
                                            amtAvg.compareTo(item.getMaxDailySaleAmt()) <= 0 &&
                                            grossProfitRate.multiply(new BigDecimal(100)).compareTo(item.getMinProMll()) > 0 &&
                                            grossProfitRate.multiply(new BigDecimal(100)).compareTo(item.getMaxProMll()) <= 0).findFirst();

                    if (StrUtil.isNotBlank(appCommissionSummary.getPlanIfNegative()) && "0".equals(appCommissionSummary.getPlanIfNegative())) {
                        if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                            continue;
                        }
                    }

                    if (first.isPresent()) {
                        BigDecimal tichengScale = first.get().getTichengScale();
                        String planAmtWay = appCommissionSummary.getPlanAmtWay();
                        BigDecimal baseAmount = BigDecimal.ZERO;
                        //提成方式: 0 按销售额提成 1 按毛利额提成
                        if ("0".equals(planAmtWay)) {
                            baseAmount = amt;
                        } else if ("1".equals(planAmtWay)) {
                            baseAmount = BigDecimalUtil.toBigDecimal(appCommissionSummary.getGrossProfitAmt());
                        }
                        // 提成金额
                        appCommissionSummary.setCommissionAmt(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengScale.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
                    }
                }
            }
            return groupingByStore(tempSaleCommissionSummaries, null);
        }
        return null;
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

    private void getData(PersonalSaleInData inData, List<Map<String, Object>> table, Date now, Map<String, Object> resultMap, String timeType) throws ParseException {
        Map<String, Object> selectParam = getSelectParamNew(inData.getType(), now);
        getTimeMapNew(table, inData.getType(), now);
        StringBuilder queryBuilder = new StringBuilder().append("SELECT ")
                .append(" ROUND( COALESCE(SUM( GSSD_AMT ),0), 2 ) amt, ")
                .append(" ROUND( COALESCE(SUM( GSSD_MOV_PRICE ),0), 2 ) mov, ")
                .append(" ROUND( COALESCE(SUM( GSSD_AMT ),0)- COALESCE(SUM( GSSD_MOV_PRICE ),0), 2 ) grossAmt, ")
                .append(" case COALESCE(SUM( GSSD_AMT ),0) when 0 then 0 else ROUND(( COALESCE(SUM( GSSD_AMT ),0)- COALESCE(SUM( GSSD_MOV_PRICE ),0))/ COALESCE(SUM( GSSD_AMT ),0)* 100, 2 ) end as grossRate, ")
                .append(" COUNT( DISTINCT GSSD_BILL_NO ) payCount, ")
                .append(" case COUNT( DISTINCT GSSD_BILL_NO ) when 0 then 0 else ROUND( COALESCE(sum( GSSD_AMT ),0) / COUNT( DISTINCT GSSD_BILL_NO ), 2 ) end as kedanPrice, ")
                .append(selectParam.get("selectTime"))
                .append(" FROM GAIA_SD_SALE_D gssd INNER JOIN GAIA_CAL_DT cd ON gssd.GSSD_DATE = cd.GCD_DATE ")
                .append(" WHERE  client= '").append(inData.getClient()).append("'")
                .append(" and GSSD_SALER_ID= '").append(inData.getUserId()).append("'")
                .append(selectParam.get("queryTime"))
                .append(" GROUP BY ")
                .append(selectParam.get("groupTime"));
        List<Map<String, Object>> maps = kylinJdbcTemplate.queryForList(queryBuilder.toString());

        List<Map<String, Object>> amt = copyList(table);
        List<Map<String, Object>> grossAmt = copyList(table);
        List<Map<String, Object>> grossRate = copyList(table);
        List<Map<String, Object>> payCount = copyList(table);
        List<Map<String, Object>> kedanPrice = copyList(table);
        for (Map<String, Object> map : maps) {
            Object time = map.get("TIMETYPE");
            setMapValue(amt, map.get("AMT"), time);
            setMapValue(grossAmt, map.get("GROSSAMT"), time);
            setMapValue(grossRate, map.get("GROSSRATE"), time);
            setMapValue(payCount, map.get("PAYCOUNT"), time);
            setMapValue(kedanPrice, map.get("KEDANPRICE"), time);
        }
        setDefaultMap(amt);
        setDefaultMap(grossAmt);
        setDefaultMap(grossRate);
        setDefaultMap(payCount);
        setDefaultMap(kedanPrice);

        ((Map<String, Object>) resultMap.get("amt")).put(timeType, amt);
        ((Map<String, Object>) resultMap.get("grossAmt")).put(timeType, grossAmt);
        ((Map<String, Object>) resultMap.get("grossRate")).put(timeType, grossRate);
        ((Map<String, Object>) resultMap.get("payCount")).put(timeType, payCount);
        ((Map<String, Object>) resultMap.get("kedanPrice")).put(timeType, kedanPrice);
        //获取销售提成表
        List<Map<String, Object>> saleTicheng = getSaleTable(inData, table, selectParam);
        ((Map<String, Object>) resultMap.get("saleTicheng")).put(timeType, saleTicheng);
        //获取单品提成表
        List<Map<String, Object>> proTicheng = getProTable(inData, table, selectParam);
        ((Map<String, Object>) resultMap.get("proTicheng")).put(timeType, proTicheng);
    }

    /**
     * 单品提成汇总计算
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param calDateMap               calDateMap
     * @return Map<String, AppCommissionSummary>
     */
    private Map<String, AppCommissionSummary> calcProCommissionPlan(StoreCommissionSummaryDO storeCommissionSummaryDO,
                                                                    Map<String, String> calDateMap) {
        String startDate = storeCommissionSummaryDO.getStartDate();
        String endDate = storeCommissionSummaryDO.getEndDate();
        String client = storeCommissionSummaryDO.getClient();
        String userId = storeCommissionSummaryDO.getUserId();

        // 获取单品提成方案列表
        List<StoreCommissionSummary> selectProductCommissionPlans = tichengProplanProNMapper.selectProductCommissionPlan(storeCommissionSummaryDO);

        // 当前天
        LocalDate nowLocalDate = LocalDate.now();
        String nowDate = nowLocalDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY);
        EmpSaleDetailInData empSaleDetailInData = new EmpSaleDetailInData();
        empSaleDetailInData.setClient(client);

        List<AppCommissionSummary> tempProductCommissionSummaries = new ArrayList<>();

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
            if (assertInTimeRange(storeCommissionSummaryDO, nowDate)) {
                // 每个商品的记录, 包括没有提成的商品
                String sqlSb = generateProCommissionSqlAndResult(storeCommissionSummaryDO);
                List<AppCommissionSummary> appCommissionSummaries = kylinJdbcTemplate.query(sqlSb, BeanPropertyRowMapper.newInstance(AppCommissionSummary.class));
                if (CollectionUtil.isNotEmpty(appCommissionSummaries)) {
                    // 单品提成方案下商品数据
                    for (AppCommissionSummary appCommissionSummary : appCommissionSummaries) {
                        AppCommissionSummary temp = new AppCommissionSummary();
                        BeanUtil.copyProperties(appCommissionSummary, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        BeanUtil.copyProperties(productCommissionPlan, temp, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        tempProductCommissionSummaries.add(temp);
                    }
                }
            }

            // 如果查询时间大于等于当天
            if (flag) {
                // 获取当天实时数据
                empSaleDetailInData.setPlanId(productCommissionPlan.getPlanId());
                empSaleDetailInData.setToday(nowDate);
                empSaleDetailInData.setStoArr(storeCommissionSummaryDO.getStoCodes());
                empSaleDetailInData.setNameSearchType("1");
                empSaleDetailInData.setNameSearchIdList(Collections.singletonList(userId));
                List<EmpSaleDetailResVo> dbList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(empSaleDetailInData);
                renderCommissionDataWithToday(productCommissionPlan, dbList, tempProductCommissionSummaries);
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
            Iterator<AppCommissionSummary> iterator = tempProductCommissionSummaries.iterator();
            while (iterator.hasNext()) {
                AppCommissionSummary appCommissionSummary = iterator.next();
                Integer planId = appCommissionSummary.getPlanId();
                String proId = appCommissionSummary.getProId();
                String subPlanId = appCommissionSummary.getSubPlanId();

                LocalDate saleDate = appCommissionSummary.getSaleDate();
                appCommissionSummary.setTimeType(calDateMap.get(saleDate.toString().replaceAll(StrUtil.DASHED, StrUtil.EMPTY)));

                if (subPlanNum.containsKey(String.valueOf(planId))) {
                    HashSet<String> subPlanIdSet = subPlanNum.get(String.valueOf(planId));
                    subPlanIdSet.add(subPlanId);
                    subPlanNum.put(String.valueOf(planId), subPlanIdSet);
                } else {
                    HashSet<String> subPlanIdSet = new HashSet<>();
                    subPlanIdSet.add(subPlanId);
                    subPlanNum.put(String.valueOf(planId), subPlanIdSet);
                }

                appCommissionSummary.setPlanType(1);
                appCommissionSummary.setPlanTypeName("单品提成");
                BigDecimal amt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getAmt());
                BigDecimal zkAmt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getZkAmt());
                BigDecimal ysAmt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getYsAmt());
                // 折扣率
                appCommissionSummary.setZkl(BigDecimalUtil.divide(zkAmt, ysAmt, 4));
                // 毛利率
                appCommissionSummary.setGrossProfitRate(BigDecimalUtil.divide(appCommissionSummary.getGrossProfitAmt(), amt, 4));
                BigDecimal zkl = appCommissionSummary.getZkl();

                String planRejectDiscountRateSymbol = appCommissionSummary.getPlanRejectDiscountRateSymbol();
                String planRejectDiscountRate = appCommissionSummary.getPlanRejectDiscountRate();
                String planIfNegative = appCommissionSummary.getPlanIfNegative();

                boolean flag = true;
                // 如果设置了剔除折扣率
                if (StrUtil.isAllNotBlank(planRejectDiscountRateSymbol, planRejectDiscountRate)) {
                    if (calcSymbol(BigDecimalUtil.toBigDecimal(zkl), BigDecimalUtil.toBigDecimal(planRejectDiscountRate), planRejectDiscountRateSymbol)) {
                        flag = false;
                    }
                }

                // 负毛利率不参与提成
                if ("0".equals(planIfNegative)) {
                    BigDecimal grossProfitRate = BigDecimalUtil.format(appCommissionSummary.getGrossProfitRate());
                    if (grossProfitRate.compareTo(BigDecimal.ZERO) < 0) {
                        flag = false;
                    }
                }

                Integer settingId = appCommissionSummary.getSettingId();
                List<TichengProplanProN> commissionsProPlan = commissionProPlanMap.get(client + "_" + planId + "_" + proId + "_" + settingId);
                if (CollectionUtil.isNotEmpty(commissionsProPlan)) {
                    TichengProplanProN tichengProplanProN = commissionsProPlan.get(0);
                    if (flag) {
                        BigDecimal qyt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getQyt());
                        BigDecimal grossProfitAmt = BigDecimalUtil.toBigDecimal(appCommissionSummary.getGrossProfitAmt());
                        // 计算提成金额
                        BigDecimal commissionAmt = calcCommissionTotal(tichengProplanProN, qyt, ysAmt, amt, grossProfitAmt);
                        appCommissionSummary.setCommissionAmt(commissionAmt);
                    }
                }
            }
            return groupingByStore(tempProductCommissionSummaries, subPlanNum);
        }
        return null;
    }


    private List<Map<String, Object>> getSaleTable(PersonalSaleInData inData, List<Map<String, Object>> table, Map<String, Object> selectParam) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        Date startDate = (Date) selectParam.get("startDate");
        Date endDate = (Date) selectParam.get("endDate");

        instance.setTime(startDate);

        List<Map<String, Object>> maps = new ArrayList<>();
        /*String startDateStr = format.format(startDate);
        String endDateStr = format.format(endDate);
        List<GaiaTichengSaleplanZ> saleplanZList = gaiaTichengPlanZMapper.selectSalePlanByClientAndStartDayAndEndDay(inData.getClient(),startDateStr,endDateStr);
        if (ObjectUtil.isNotEmpty(saleplanZList)) {
            while (instance.getTime().compareTo(endDate) <= 0) {
                Date time = instance.getTime();
                List<GaiaTichengSaleplanZ> collect = saleplanZList.stream().filter(plan -> {
                    try {
                        return format.parse(plan.getPlanStartDate()).compareTo(time) <= 0 && format.parse(plan.getPlanEndDate()).compareTo(time) >= 0;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(collect)){
                    Map<String, Object> map = dailySellCommission(time, inData, selectParam);
                    if (ObjectUtil.isNotEmpty(map)) {
                        maps.add(map);
                    }
                }
                Map<String, Object> map = dailySellCommission(time, inData, selectParam);
                if (ObjectUtil.isNotEmpty(map)) {
                    maps.add(map);
                }
                instance.add(Calendar.DAY_OF_YEAR, 1);
            }
        }*/

        while (instance.getTime().compareTo(endDate) <= 0) {
            Date time = instance.getTime();
            Map<String, Object> map = dailySellCommission(time, inData, selectParam);
            if (ObjectUtil.isNotEmpty(map)) {
                maps.add(map);
            }
            instance.add(Calendar.DAY_OF_YEAR, 1);
        }

        HashMap<String, BigDecimal> saleMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                BigDecimal ticheng = (BigDecimal) map.get("TICHENG");
                if (saleMap.get(map.get("TIMETYPE").toString()) != null) {
                    ticheng = ticheng.add(saleMap.get(map.get("TIMETYPE").toString()));
                }
                saleMap.put(map.get("TIMETYPE").toString(), ticheng);
            }
        }

        if (ObjectUtil.isNotEmpty(saleMap)) {
            Set<Map.Entry<String, BigDecimal>> entries = saleMap.entrySet();
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            HashMap<String, Object> timeMap = new HashMap<>();
            for (Map.Entry<String, BigDecimal> entry : entries) {
                timeMap.put("TIMETYPE", entry.getKey());
                timeMap.put("TICHENG", entry.getValue());
                list.add(timeMap);
            }
            maps = list;
        }

        List<Map<String, Object>> saleTicheng = copyList(table);

        for (Map<String, Object> map : maps) {
            Object time = map.get("TIMETYPE");
            setMapValue(saleTicheng, map.get("TICHENG"), time);
        }

        setDefaultMap(saleTicheng);
        return saleTicheng;
    }

    public Map<String, Object> dailySellCommission(Date day, PersonalSaleInData inData, Map<String, Object> selectParam) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dayStr = format.format(day);
        String dayStrWithfu = dateFormat.format(day);
        BigDecimal ticheng = BigDecimal.ZERO;

        GaiaTichengSaleplanZ planZ = gaiaTichengPlanZMapper.selectSalePlanByClientAndDay(inData.getClient(), dayStr);
        if (ObjectUtil.isEmpty(planZ)) {
            return null;
        }

        List<String> stoCodeList = gaiaTichengPlanZMapper.getDaySaleStoCode(inData.getClient(), inData.getUserId(), dayStr);
        if (ObjectUtil.isEmpty(stoCodeList)) {
            return null;
        }

        for (String stoCode : stoCodeList) {
            GaiaTichengSaleplanZ saleplanZ = gaiaTichengPlanZMapper.selectPlanByClientAndDayAndPlanType(inData.getClient(), stoCode, dayStr, "1");
            if (ObjectUtil.isEmpty(saleplanZ)) {
                return null;
            }
            List<GaiaTichengProplanZ> proplanZList = gaiaTichengPlanZMapper.selectTimeInterleavingProPlan(inData.getClient(), stoCode, saleplanZ.getPlanStartDate(), saleplanZ.getPlanEndDate());

            Set<String> proCodeList = new HashSet<>();
            if (ObjectUtil.isNotEmpty(proplanZList)) {
                proplanZList = proplanZList.stream().filter(pro -> "0".equals(pro.getPlanProductWay())).collect(Collectors.toList());
                for (GaiaTichengProplanZ proplanZ : proplanZList) {
                    List<String> proList = gaiaTichengPlanZMapper.getProCodeByClientAndPid(inData.getClient(), proplanZ.getId());
                    proCodeList.addAll(proList);
                }
            }

            List<String> rejectProCode = gaiaTichengPlanZMapper.getRejectProCodeByClientAndPid(inData.getClient(), Long.parseLong(saleplanZ.getId().toString()));
            if (ObjectUtil.isNotEmpty(rejectProCode)) {
                proCodeList.addAll(rejectProCode);
            }

            List<String> classList = gaiaTichengPlanZMapper.getRejectClassByPid(Long.parseLong(saleplanZ.getId().toString()));

            //确定提成比例
            long betweenDay = DateUtil.betweenDay(format.parse(saleplanZ.getPlanStartDate()), format.parse(saleplanZ.getPlanEndDate()), true) + 1;

            StringBuilder queryBuilder = new StringBuilder().append("SELECT ")
                    .append("case ").append(betweenDay).append(" when 0 then 0 else ").append("SUM(GSSD_AMT)/").append(betweenDay).append(" end as").append(" avgAmt ")
                    .append("from GAIA_SD_SALE_D ")
                    .append("where CLIENT = '").append(inData.getClient()).append("'")
                    .append(" and GSSD_BR_ID = '").append(stoCode).append("'")
                    .append(" and GSSD_DATE >= '").append(dateFormat.format(format.parse(saleplanZ.getPlanStartDate()))).append("'")
                    .append(" and GSSD_DATE <= '").append(dateFormat.format(format.parse(saleplanZ.getPlanEndDate()))).append("'");
            List<Map<String, Object>> maps = kylinJdbcTemplate.queryForList(queryBuilder.toString());
            BigDecimal avgAmt = new BigDecimal(0);
            if (ObjectUtil.isNotEmpty(maps) && ObjectUtil.isNotEmpty(maps.get(0)) && ObjectUtil.isNotEmpty(maps.get(0).get("AVGAMT"))) {
                avgAmt = (BigDecimal) maps.get(0).get("AVGAMT");
            }

            List<GaiaTichengSaleplanM> saleplanMList = gaiaTichengPlanZMapper.getTiChengScale(inData.getClient(), Long.parseLong(saleplanZ.getId().toString()), avgAmt);

            StringBuilder tichengQuery = new StringBuilder().append("select ");
            if ("0".equals(saleplanZ.getPlanAmtWay())) {
                tichengQuery.append(" round( COALESCE(sum(GSSD_AMT),0) ,2) as baseAmount , "); //销售额
            } else if ("1".equals(saleplanZ.getPlanAmtWay())) {
                tichengQuery.append(" round( COALESCE(sum(GSSD_AMT),0),2) - round( COALESCE(sum(GSSD_MOV_PRICE),0),2) as baseAmount, "); //毛利额
            }
            if ("0".equals(saleplanZ.getPlanRateWay())) {
                tichengQuery.append(" case COALESCE(sum(GSSD_MOV_PRICE),0) when 0 then 0 else round( COALESCE(sum( GSSD_AMT ),0)+ COALESCE(sum( GSSD_ZK_AMT ),0), 2 )/ round(COALESCE(sum(GSSD_MOV_PRICE),0), 2 ) end as  grossProfitRate ");//
            } else if ("1".equals(saleplanZ.getPlanRateWay())) {
                tichengQuery.append(" GSSD_KL_MARGIN_AREA as grossProfitRate ");//销售毛利率
            }
            tichengQuery.append(" from GAIA_SD_SALE_D sale ");
            if (ObjectUtil.isNotEmpty(classList)) {
                tichengQuery.append(" INNER JOIN GAIA_PRODUCT_BUSINESS pb on sale.CLIENT = pb.CLIENT and sale.GSSD_BR_ID = pb.PRO_SITE and sale.GSSD_PRO_ID = pb.PRO_SELF_CODE");
            }
            tichengQuery.append(" where sale.CLIENT = '").append(inData.getClient()).append("'")
                    .append(" and sale.GSSD_BR_ID = '").append(stoCode).append("'")
                    .append(" and sale.GSSD_DATE = '").append(dayStrWithfu).append("'")
                    .append(" and sale.GSSD_SALER_ID = '").append(inData.getUserId()).append("'");
            if (ObjectUtil.isNotEmpty(proCodeList)) {
                StringBuilder proCode = new StringBuilder();
                proCodeList.forEach(pro -> proCode.append("'").append(pro).append("'").append(","));
                String s = proCode.toString();
                s = s.substring(0, s.length() - 1);
                tichengQuery.append(" and sale.GSSD_PRO_ID not in(").append(s).append(") ");
            }

            if (ObjectUtil.isNotEmpty(classList)) {
                StringBuilder classStr = new StringBuilder();
                classList.forEach(cls -> classStr.append("'").append(cls).append("'").append(","));
                String s = classStr.toString();
                s = s.substring(0, s.length() - 1);
                tichengQuery.append(" and pb.PRO_CLASS not in(").append(s).append(") ");
            }
            if ("0".equals(saleplanZ.getPlanIfNegative())) {
                tichengQuery.append(" AND sale.GSSD_KL_NEGATIVE_MARGIN >= '0' ");
            }
            tichengQuery.append(" GROUP BY GSSD_BILL_NO,GSSD_PRO_ID,GSSD_KL_MARGIN_AREA");
            List<Map<String, Object>> saleList = kylinJdbcTemplate.queryForList(tichengQuery.toString());

            for (Map<String, Object> sale : saleList) {
                BigDecimal grossProfitRate = (BigDecimal) sale.get("grossProfitRate");
                BigDecimal baseAmount = (BigDecimal) sale.get("baseAmount");
                List<GaiaTichengSaleplanM> collect = saleplanMList.stream().filter(m -> m.getMinProMll().compareTo(grossProfitRate) <= 0 && m.getMaxProMll().compareTo(grossProfitRate) > 0).collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(collect) && ObjectUtil.isNotEmpty(baseAmount)) {
                    BigDecimal tichengScale = collect.get(0).getTichengScale();
                    BigDecimal decimal = baseAmount.multiply(tichengScale).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                    ticheng = ticheng.add(decimal);
                }
            }

        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("TICHENG", ticheng);

        if ("1".equals(inData.getType())) {
            result.put("TIMETYPE", dayStrWithfu);
        } else {
            Map<String, String> map = gaiaTichengPlanZMapper.getDate(dayStr, (String) selectParam.get("selectTime"));
            result.put("TIMETYPE", map.get("timeType"));
        }
        return result;
    }

    private List<Map<String, Object>> getProTable(PersonalSaleInData inData, List<Map<String, Object>> table, Map<String, Object> selectParam) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        Date startDate = (Date) selectParam.get("startDate");
        Date endDate = (Date) selectParam.get("endDate");

        instance.setTime(startDate);
        List<Map<String, Object>> maps = new ArrayList<>();

        while (instance.getTime().compareTo(endDate) <= 0) {
            Date time = instance.getTime();
            Map<String, Object> map = dailyProCommission(time, inData, selectParam);
            if (ObjectUtil.isNotEmpty(map)) {
                maps.add(map);
            }
            instance.add(Calendar.DAY_OF_YEAR, 1);
        }

        HashMap<String, BigDecimal> saleMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                BigDecimal ticheng = (BigDecimal) map.get("TICHENG");
                if (saleMap.get(map.get("TIMETYPE").toString()) != null) {
                    ticheng = ticheng.add(saleMap.get(map.get("TIMETYPE").toString()));
                }
                saleMap.put(map.get("TIMETYPE").toString(), ticheng);
            }
        }

        if (ObjectUtil.isNotEmpty(saleMap)) {
            Set<Map.Entry<String, BigDecimal>> entries = saleMap.entrySet();
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            HashMap<String, Object> timeMap = new HashMap<>();
            for (Map.Entry<String, BigDecimal> entry : entries) {
                timeMap.put("TIMETYPE", entry.getKey());
                timeMap.put("TICHENG", entry.getValue());
                list.add(timeMap);
            }
            maps = list;
        }

        List<Map<String, Object>> saleTicheng = copyList(table);

        for (Map<String, Object> map : maps) {
            Object time = map.get("TIMETYPE");
            setMapValue(saleTicheng, map.get("TICHENG"), time);
        }

        setDefaultMap(saleTicheng);
        return saleTicheng;
    }

    private Map<String, Object> dailyProCommission(Date day, PersonalSaleInData inData, Map<String, Object> selectParam) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dayStr = format.format(day);
        String dayStrWithfu = dateFormat.format(day);
        BigDecimal ticheng = BigDecimal.ZERO;

        List<GaiaTichengProplanZ> planZ = gaiaTichengPlanZMapper.selectProPlanByClientAndDay(inData.getClient(), dayStr);
        if (ObjectUtil.isEmpty(planZ)) {
            return null;
        }

        List<String> stoCodeList = gaiaTichengPlanZMapper.getDaySaleStoCode(inData.getClient(), inData.getUserId(), dayStr);
        if (ObjectUtil.isEmpty(stoCodeList)) {
            return null;
        }

        for (String stoCode : stoCodeList) {
            GaiaTichengProplanZ proplanZ = gaiaTichengPlanZMapper.selectProPlanByClientAndStoCodeAndDay(inData.getClient(), stoCode, dayStr);
            List<GaiaTichengProplanPro> proList = gaiaTichengPlanZMapper.getProListByClientAndPid(inData.getClient(), proplanZ.getId());
            List<String> proCodeList = proList.stream().map(pro -> pro.getProCode()).collect(Collectors.toList());

            StringBuilder queryBuilder = new StringBuilder().append("select ")
                    .append(" GSSD_PRO_ID proCode,")
                    .append(" sum(GSSD_QTY) qty")
                    .append(" from GAIA_SD_SALE_D sale ")
                    .append(" where CLIENT = '").append(inData.getClient()).append("'")
                    .append(" and GSSD_BR_ID = '").append(stoCode).append("'")
                    .append(" and GSSD_DATE = '").append(dayStrWithfu).append("'")
                    .append(" and GSSD_SALER_ID = '").append(inData.getUserId()).append("'");
            if (ObjectUtil.isNotEmpty(proCodeList)) {
                StringBuilder proCode = new StringBuilder();
                proCodeList.forEach(pro -> proCode.append("'").append(pro).append("'").append(","));
                String s = proCode.toString();
                s = s.substring(0, s.length() - 1);
                queryBuilder.append(" and GSSD_PRO_ID in(").append(s).append(") ");
            }
            queryBuilder.append("group by GSSD_BILL_NO,GSSD_PRO_ID");
            List<Map<String, Object>> mapList = kylinJdbcTemplate.queryForList(queryBuilder.toString());
            for (Map<String, Object> map : mapList) {
                List<GaiaTichengProplanPro> proPlan = proList.stream().filter(pro -> pro.getProCode().equals(map.get("PROCODE"))).collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(proPlan) && ObjectUtil.isNotEmpty(proPlan.get(0).getTichengAmt())) {
                    ticheng = ticheng.add(proPlan.get(0).getTichengAmt().multiply((BigDecimal) map.get("QTY")));
                }
            }
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("TICHENG", ticheng);
        if ("1".equals(inData.getType())) {
            result.put("TIMETYPE", dayStrWithfu);
        } else {
            Map<String, String> map = gaiaTichengPlanZMapper.getDate(dayStr, (String) selectParam.get("selectTime"));
            result.put("TIMETYPE", map.get("timeType"));
        }
        return result;
    }

    /*@Override
    public boolean judgment(PersonalSaleInData inData) {
        String globalType = gaiaGlobalDataMapper.globalType(inData.getClient(),null);
        if ("0".equals(globalType)){
            return true;
        }
        return false;
    }*/

    private void setDefaultMap(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            if (map.get("value") instanceof Map) {
                map.put("value", ((Map<?, ?>) map.get("value")).get("value"));
            }
        }
    }

    private void setMapValue(List<Map<String, Object>> list, Object value, Object o) {
        for (Map<String, Object> map : list) {
            if (map.get("value") instanceof Map && ((Map<?, ?>) map.get("value")).get("key").equals(o.toString())) {
                map.put("value", value);
            }
        }
    }

    private void setMapValue2(List<Map<String, Object>> list, Object value, Object o) {
        for (Map<String, Object> map : list) {
            if (map.get("value") instanceof Map) {
                if (MapUtil.getStr((Map<?, ?>) map.get("value"), "key").replaceAll("-", "").equals(o.toString())) {
                    map.put("value", value);
                }
            }
        }
    }

    /**
     * 设置查询参数
     *
     * @param type type
     * @param now  now
     * @return Map<String, Object>
     */
    private Map<String, Object> setSelectParam(String type, Date now) {
        String queryTime = "";
        Date startDate = new Date(0);
        Date endDate = new Date(0);
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        switch (type) {
            case "1":
                instance.setTime(now);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                startDate = instance.getTime();
                queryTime += "AND gssd.GSSD_DATE >= '" + format.format(instance.getTime()) + "'\n";
                instance.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                endDate = instance.getTime();
                queryTime += "AND gssd.GSSD_DATE <= '" + format.format(instance.getTime()) + "'\n";
                break;
            case "2":
                instance.setTime(now);
                endDate = instance.getTime();
                queryTime += "AND gssd.GSSD_DATE <= '" + format.format(instance.getTime()) + "'\n";
                instance.add(Calendar.WEEK_OF_YEAR, -7);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                startDate = instance.getTime();
                queryTime += "AND gssd.GSSD_DATE >= '" + format.format(instance.getTime()) + "'\n";
                break;
            case "3":
                instance.setTime(now);
                endDate = instance.getTime();
                queryTime += "AND gssd.GSSD_DATE <= '" + format.format(instance.getTime()) + "'\n";
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMinimum(Calendar.DAY_OF_YEAR));
                startDate = instance.getTime();
                queryTime += "AND gssd.GSSD_DATE >= '" + format.format(instance.getTime()) + "'\n";
                break;
        }
        HashMap<String, Object> selectParam = new HashMap<>();
        selectParam.put("queryTime", queryTime);
        selectParam.put("startDate", startDate);
        selectParam.put("endDate", endDate);
        return selectParam;
    }

    private Map<String, Object> getSelectParamNew(String type, Date now) {
        String selectTime = "";
        String groupTime = "";
        String queryTime = "";
        Date startDate = new Date(0);
        Date endDate = new Date(0);
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        switch (type) {
            case "1":
                instance.setTime(now);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                startDate = instance.getTime();
                queryTime += " and GSSD_DATE >= '" + format.format(instance.getTime()) + "'";
                instance.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                endDate = instance.getTime();
                queryTime += " and GSSD_DATE <= '" + format.format(instance.getTime()) + "'";
                selectTime = "cd.GCD_DATE timeType";
                groupTime = "cd.GCD_DATE";
                break;
            case "2":
                instance.setTime(now);
                endDate = instance.getTime();
                queryTime += " and GSSD_DATE <= '" + format.format(instance.getTime()) + "'";
                instance.add(Calendar.WEEK_OF_YEAR, -7);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                startDate = instance.getTime();
                queryTime += " and GSSD_DATE >= '" + format.format(instance.getTime()) + "'";
                selectTime = "cd.GCD_WEEK timeType";
                groupTime = "cd.GCD_WEEK";
                break;
            case "3":
                instance.setTime(now);
                endDate = instance.getTime();
                queryTime += " and GSSD_DATE <= '" + format.format(instance.getTime()) + "'";
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMinimum(Calendar.DAY_OF_YEAR));
                startDate = instance.getTime();
                queryTime += " and GSSD_DATE >= '" + format.format(instance.getTime()) + "'";
                selectTime = "cd.GCD_MONTH timeType";
                groupTime = "cd.GCD_MONTH";
                break;
        }
        HashMap<String, Object> selectParam = new HashMap<>();
        selectParam.put("selectTime", selectTime);
        selectParam.put("queryTime", queryTime);
        selectParam.put("groupTime", groupTime);
        selectParam.put("startDate", startDate);
        selectParam.put("endDate", endDate);
        return selectParam;
    }

    private Map<String, String> getSelectParam(String type, Map<String, Object> tableMap) {
        String selectTime = "";
        String groupTime = "";
        String queryTime = "";
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DateUtil.offset(DateUtil.date(), DateField.YEAR, -1);
        DateUtil.offsetDay(DateUtil.date(), DateUtil.dayOfWeek(DateUtil.date()));
        String now1 = DateUtil.now();
        switch (type) {
            case "1":
                instance.setTime(now);
                instance.add(Calendar.DAY_OF_MONTH, -7);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                queryTime += " and GSSD_DATE >= '" + format.format(instance.getTime()) + "'";
                tableMap.put("period", String.format("%02d", instance.get(Calendar.WEEK_OF_YEAR)));

                instance.setTime(now);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                queryTime += " and GSSD_DATE <= '" + format.format(instance.getTime()) + "'";
                tableMap.put("current", String.format("%02d", instance.get(Calendar.WEEK_OF_YEAR)));

                selectTime = "cd.GCD_DATE timeCell,cd.GCD_WEEK timeType";
                groupTime = "cd.GCD_DATE,cd.GCD_WEEK";
                break;
            case "2":
                instance.setTime(now);
                queryTime += " and GSSD_DATE <= '" + format.format(instance.getTime()) + "'";
                tableMap.put("current", String.format("%04d", instance.get(Calendar.YEAR)));
                instance.add(Calendar.YEAR, -1);
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMinimum(Calendar.DAY_OF_YEAR));
                queryTime += " and GSSD_DATE >= '" + format.format(instance.getTime()) + "'";
                tableMap.put("period", String.format("%04d", instance.get(Calendar.YEAR)));

                selectTime = "cd.GCD_WEEK timeCell,cd.GCD_YEAR timeType";
                groupTime = "cd.GCD_WEEK,cd.GCD_YEAR";
                break;
            case "3":
                instance.setTime(now);
                queryTime += " and GSSD_DATE <= '" + format.format(instance.getTime()) + "'";
                tableMap.put("current", String.format("%04d", instance.get(Calendar.YEAR)));
                instance.add(Calendar.YEAR, -1);
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMinimum(Calendar.DAY_OF_YEAR));
                queryTime += " and GSSD_DATE >= '" + format.format(instance.getTime()) + "'";
                tableMap.put("period", String.format("%04d", instance.get(Calendar.YEAR)));

                selectTime = "cd.GCD_MONTH timeCell,cd.GCD_YEAR timeType";
                groupTime = "cd.GCD_MONTH,cd.GCD_YEAR";
                break;
        }
        HashMap<String, String> selectParam = new HashMap<>();
        selectParam.put("selectTime", selectTime);
        selectParam.put("queryTime", queryTime);
        selectParam.put("groupTime", groupTime);
        return selectParam;
    }

    private static void getTimeMap(List<Map<String, Object>> currentTable, List<Map<String, Object>> periodTable, String type) {
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        switch (type) {
            case "1":
                instance.setTime(now);
                instance.add(Calendar.DAY_OF_MONTH, -7);
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String[] days = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
                for (String day : days) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("time", format.format(instance.getTime()));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", day);
                    hashMap.put("value", map);
                    periodTable.add(hashMap);
                    instance.add(Calendar.DAY_OF_MONTH, 1);
                }
                for (String day : days) {
                    if (instance.getTime().compareTo(now) > 0) break;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("time", format.format(instance.getTime()));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", day);
                    hashMap.put("value", map);
                    currentTable.add(hashMap);
                    instance.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case "2":
                instance.setTime(now);
                int curNum = instance.get(Calendar.WEEK_OF_YEAR);
                instance.set(Calendar.WEEK_OF_YEAR, instance.getActualMaximum(Calendar.WEEK_OF_YEAR));
                instance.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                if (now.compareTo(instance.getTime()) > 0) {
                    curNum += 1;
                }
                for (int i = 0; i < curNum; i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", String.format("%02d", (i + 1)));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", (i + 1));
                    hashMap.put("value", map);
                    currentTable.add(hashMap);
                }

                instance.setTime(now);
                instance.add(Calendar.YEAR, -1);
                instance.set(Calendar.WEEK_OF_YEAR, instance.getActualMaximum(Calendar.WEEK_OF_YEAR));
                instance.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                int perNum = instance.get(Calendar.WEEK_OF_YEAR);
                Date time = instance.getTime();
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMaximum(Calendar.DAY_OF_YEAR));
                if (instance.getTime().compareTo(time) > 0) {
                    perNum += 1;
                }
                for (int i = 0; i < perNum; i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", String.format("%02d", (i + 1)));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", (i + 1));
                    hashMap.put("value", map);
                    periodTable.add(hashMap);
                }
                break;
            case "3":
                instance.setTime(now);
                instance.add(Calendar.YEAR, -1);
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMinimum(Calendar.DAY_OF_YEAR));
                String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
                for (String month : months) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", String.format("%02d", instance.get(Calendar.MONTH) + 1));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", month);
                    hashMap.put("value", map);
                    periodTable.add(hashMap);
                    instance.add(Calendar.MONTH, 1);
                }
                for (String month : months) {
                    if (instance.getTime().compareTo(now) > 0) break;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", String.format("%02d", instance.get(Calendar.MONTH) + 1));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", month);
                    hashMap.put("value", map);
                    currentTable.add(hashMap);
                    instance.add(Calendar.MONTH, 1);
                }
                break;
        }
    }

    private static void getTimeMapNew(List<Map<String, Object>> table, String type, Date now) {
        Calendar instance = Calendar.getInstance();
        instance.setFirstDayOfWeek(Calendar.MONDAY);
        instance.setTime(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        switch (type) {
            case "1":
                instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String[] days = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
                for (String day : days) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", format.format(instance.getTime()));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", day);
                    hashMap.put("value", map);
                    table.add(hashMap);
                    instance.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case "2":
                instance.add(Calendar.WEEK_OF_YEAR, -7);
                for (int i = 0; i < 8; i++) {
                    int curNum = instance.get(Calendar.WEEK_OF_YEAR);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", String.format("%02d", curNum));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", curNum);
                    hashMap.put("value", map);
                    table.add(hashMap);
                    instance.add(Calendar.WEEK_OF_YEAR, 1);
                }
                break;
            case "3":
                instance.set(Calendar.DAY_OF_YEAR, instance.getActualMinimum(Calendar.DAY_OF_YEAR));
                String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
                for (String month : months) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", String.format("%02d", instance.get(Calendar.MONTH) + 1));
                    map.put("value", 0);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", month);
                    hashMap.put("value", map);
                    table.add(hashMap);
                    instance.add(Calendar.MONTH, 1);
                }
                break;
        }
    }

    @Override
    public List<SalesRankOutData> listEmployeeSalesRank(PersonSalesInData inData) {
        String endTime = com.gys.util.DateUtil.getYearMonthLastFormat(inData.getStartDate(), "yyyy-MM-dd");
        String startTime = com.gys.util.DateUtil.getYearMonthFirstFormat(inData.getStartDate(), "yyyy-MM-dd");
        StringBuilder sql = getSql(inData, endTime, startTime);
        log.info(String.format("<APP员工销售排名><员工销售排名><打印sql：%s>", sql));
        List<SalesRankOutData> outDataList = kylinJdbcTemplate.query(sql.toString(), RowMapper.getDefault(SalesRankOutData.class));
        log.info(String.format("<APP员工销售排名><员工销售排名><统计数据返回结果：%s>", outDataList));
        if (!CollectionUtils.isEmpty(outDataList)) {
            for (SalesRankOutData salesRankOutData : outDataList) {
                if ("1".equals(inData.getType())) {
                    //营业额
                    salesRankOutData.setQty(salesRankOutData.getSalesAmt());
                    salesRankOutData.setType("1");
                }
                if ("2".equals(inData.getType())) {
                    //毛利额
                    salesRankOutData.setQty(salesRankOutData.getGross());
                    salesRankOutData.setType("2");
                }
                if ("3".equals(inData.getType())) {
                    //毛利率
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getGross()), checkNull(salesRankOutData.getSalesAmt())).doubleValue());
                    salesRankOutData.setType("3");
                }
                if ("4".equals(inData.getType())) {
                    //会员卡
                    salesRankOutData.setQty(salesRankOutData.getVipQty());
                    salesRankOutData.setType("4");
                }
                if ("5".equals(inData.getType())) {
                    //交易次数
                    salesRankOutData.setQty(salesRankOutData.getBillQty());
                    salesRankOutData.setType("5");
                }
                if ("6".equals(inData.getType())) {
                    //客单价
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getSalesAmt()), checkNull(salesRankOutData.getBillQty())).doubleValue());
                    salesRankOutData.setType("6");
                }
                if ("8".equals(inData.getType())) {
                    //品单价
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getPcSalesAmt()), checkNull(salesRankOutData.getPcSalesQty())).doubleValue());
                    salesRankOutData.setType("8");
                }
                if ("7".equals(inData.getType())) {
                    //客品次
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getGoodsQty()), checkNull(salesRankOutData.getPcBillQty())).doubleValue());
                    salesRankOutData.setType("7");
                }
                if ("9".equals(inData.getType())) {
                    //单店日均销售
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getSalesAmt()), checkNull(salesRankOutData.getSalesDay())).doubleValue());
                    salesRankOutData.setType("9");
                }
                if ("10".equals(inData.getType())) {
                    //单店日均毛利
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getGross()), checkNull(salesRankOutData.getSalesDay())).doubleValue());
                    salesRankOutData.setType("10");
                }
                if ("11".equals(inData.getType())) {
                    //单店日均交易
                    salesRankOutData.setQty(divide(checkNull(salesRankOutData.getBillQty()), checkNull(salesRankOutData.getSalesDay())).doubleValue());
                    salesRankOutData.setType("11");
                }
            }
            if ("asc".equals(inData.getOrder())) {
                //正序
                outDataList = outDataList.stream().sorted(Comparator.comparing(SalesRankOutData::getQty))
                        .collect(Collectors.toList());
            } else {
                //倒叙
                outDataList = outDataList.stream().sorted(Comparator.comparing(SalesRankOutData::getQty).reversed())
                        .collect(Collectors.toList());
            }
            if (StringUtils.isEmpty(inData.getOrder())) {
                //如果没有传 order 全部返回
                return outDataList;
            } else {
                if (outDataList.size() <= inData.getCount()) {
                    return outDataList;
                } else {
                    return outDataList.subList(0, inData.getCount());
                }
            }
        }
        return outDataList;
    }

    private StringBuilder getSql(PersonSalesInData inData, String endTime, String startTime) {
        StringBuilder head1 = new StringBuilder().append(" select * from (select t1.GSSD_BR_ID stoCode,t1.CLIENT client,t1.USER_NAM userName,t1.GSSD_SALER_ID userId,t1.stoName,t1.salesAmt,t1.salesQty ,sum( t1.salesAmt )- sum( t1.GSSD_MOV_PRICE ) gross,t2.billQty,t3.salesDay,t4.goodsQty,COALESCE(t5.vipQty,0)vipQty,t6.pcSalesAmt,t6.pcSalesQty,t7.pcBillQty \n" +
                "from (SELECT\n" +
                "\ta.CLIENT,\n" +
                "\ta.GSSD_BR_ID,\n" +
                "\td.STO_SHORT_NAME stoName,\n" +
                "\tsum( a.GSSD_AMT ) salesAmt,\n" +
                "\tsum( a.GSSD_QTY ) salesQty,\n" +
                "\tsum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                "\ta.CLIENT clientId,\n" +
                "\tc.FRANC_NAME francName,\n" +
                "\ta.GSSD_SALER_ID,\n" +
                "\tf.USER_NAM\n" +
                "\tFROM\n" +
                "\tGAIA_SD_SALE_D a\n" +
                "\tINNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "\tLEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "\tINNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = d.STO_CODE\n" +
                "\tINNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "\tAND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "\tLEFT JOIN GAIA_USER_DATA f on f.CLIENT = a.CLIENT and f.USER_ID = a.GSSD_SALER_ID" +
                "\tinner JOIN GAIA_PRODUCT_CLASS g on g.PRO_CLASS_CODE = e.PRO_CLASS \n" +
                "\tWHERE  a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t1 = new StringBuilder().append(" GROUP BY\n" +
                "\ta.CLIENT,\n" +
                "\td.STO_SHORT_NAME,\n" +
                "\tc.FRANC_NAME,\n" +
                "\ta.GSSD_BR_ID,\n" +
                "\ta.GSSD_SALER_ID,\n" +
                "\tf.USER_NAM) t1\n" +
                "\tleft join (select aa.CLIENT,aa.GSSD_BR_ID,aa.GSSD_SALER_ID,aa.USER_NAM,count(1) billQty from (select a.CLIENT,a.GSSD_BR_ID,a.GSSD_BILL_NO,b.USER_NAM,a.GSSD_SALER_ID from GAIA_SD_SALE_D a\n" +
                "\tLEFT JOIN GAIA_USER_DATA b on a.CLIENT = b.CLIENT and a.GSSD_SALER_ID = b.USER_ID " +
                "\t where a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t2 = new StringBuilder().append("GROUP BY a.CLIENT,a.GSSD_BR_ID,a.GSSD_BILL_NO,a.GSSD_SALER_ID,b.USER_NAM)aa GROUP BY aa.CLIENT,aa.GSSD_BR_ID,aa.GSSD_SALER_ID,aa.USER_NAM)t2\n" +
                "\ton t1.CLIENT = t2.CLIENT and t1.GSSD_BR_ID =t2.GSSD_BR_ID and t2.GSSD_SALER_ID = t1.GSSD_SALER_ID\n" +
                "\tleft join (select aa.CLIENT,aa.GSSD_BR_ID,aa.GSSD_SALER_ID,aa.USER_NAM,count(1) salesDay from (select a.CLIENT,a.GSSD_BR_ID,a.GSSD_DATE,f.USER_NAM,a.GSSD_SALER_ID " +
                "  from GAIA_SD_SALE_D a\n" +
                "\tINNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "\tLEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "\tINNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = d.STO_CODE\n" +
                "\tINNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "\tAND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "\tLEFT JOIN GAIA_USER_DATA f ON f.CLIENT = a.CLIENT \n" +
                "\tAND f.USER_ID = a.GSSD_SALER_ID\n" +
                "\tinner JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS " +
                "\twhere " +
                "\ta.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t3 = new StringBuilder().append(" GROUP BY a.CLIENT,a.GSSD_BR_ID,a.GSSD_DATE,a.GSSD_SALER_ID,f.USER_NAM)aa GROUP BY aa.CLIENT,aa.GSSD_BR_ID,aa.GSSD_SALER_ID,aa.USER_NAM)t3 \ton t1.CLIENT = t3.CLIENT and t1.GSSD_BR_ID =t3.GSSD_BR_ID and t2.GSSD_SALER_ID = t3.GSSD_SALER_ID\n" +
                "\tleft join (select aa.CLIENT,aa.GSSD_BR_ID,aa.GSSD_SALER_ID,aa.USER_NAM,count(1) goodsQty from (select a.CLIENT,a.GSSD_PRO_ID,a.GSSD_BR_ID,a.GSSD_BILL_NO,f.USER_NAM,a.GSSD_SALER_ID from GAIA_SD_SALE_D a\n" +
                "\tINNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "\tLEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "\tINNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = d.STO_CODE\n" +
                "\tINNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "\tAND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "\tLEFT JOIN GAIA_USER_DATA f ON f.CLIENT = a.CLIENT \n" +
                "\tAND f.USER_ID = a.GSSD_SALER_ID\n" +
                "\tinner JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS " +
                "\twhere e.PRO_CLASS NOT LIKE '301%' \n" +
                "\tAND e.PRO_CLASS NOT LIKE '302%' \n" +
                "\tAND e.PRO_CLASS NOT LIKE '8%' and a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t4 = new StringBuilder().append(")aa GROUP BY aa.CLIENT,aa.GSSD_BR_ID,aa.GSSD_SALER_ID,aa.USER_NAM)t4 on t1.CLIENT = t4.CLIENT and t1.GSSD_BR_ID =t4.GSSD_BR_ID and t2.GSSD_SALER_ID = t4.GSSD_SALER_ID\n" +
                "\tLEFT JOIN (select a.CLIENT,a.GSMBC_BR_ID,b.USER_ID,b.USER_NAM,count(1) vipQty from GAIA_SD_MEMBER_CARD a\n" +
                "\tLEFT JOIN GAIA_USER_DATA b on a.CLIENT = b.CLIENT and a.GSMBC_CREATE_SALER = b.USER_ID\n" +
                "\tLEFT JOIN GAIA_STORE_DATA c on c.CLIENT = a.CLIENT and c.STO_CODE = a.GSMBC_BR_ID " +
                "\twhere a.GSMBC_CREATE_DATE >= '" + startTime + "' AND a.GSMBC_CREATE_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t5 = new StringBuilder().append("GROUP BY\n" +
                "\ta.CLIENT,\n" +
                "\ta.GSMBC_BR_ID,\n" +
                "\tb.USER_ID,\n" +
                "\tb.USER_NAM \n" +
                "\t) t5 ON t1.CLIENT = t5.CLIENT \n" +
                "\tAND t1.GSSD_BR_ID = t5.GSMBC_BR_ID \n" +
                "\tAND t2.GSSD_SALER_ID = t5.USER_ID \n" +
                "\tLEFT JOIN (SELECT\n" +
                "\ta.CLIENT,\n" +
                "\ta.GSSD_BR_ID,\n" +
                "\td.STO_SHORT_NAME stoName,\n" +
                "\tsum( a.GSSD_AMT ) pcSalesAmt,\n" +
                "\tsum( a.GSSD_QTY ) pcSalesQty,\n" +
                "\tsum( a.GSSD_MOV_PRICE ) GSSD_MOV_PRICE,\n" +
                "\ta.CLIENT clientId,\n" +
                "\tc.FRANC_NAME francName,\n" +
                "\ta.GSSD_SALER_ID,\n" +
                "\tf.USER_NAM \n" +
                "\tFROM\n" +
                "\tGAIA_SD_SALE_D a\n" +
                "\tINNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "\tLEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "\tINNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = d.STO_CODE\n" +
                "\tINNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "\tAND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "\tLEFT JOIN GAIA_USER_DATA f ON f.CLIENT = a.CLIENT \n" +
                "\tAND f.USER_ID = a.GSSD_SALER_ID\n" +
                "\tINNER JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS \n" +
                "\tWHERE\n" +
                "\te.PRO_CLASS NOT LIKE '301%' \n" +
                "\tAND e.PRO_CLASS NOT LIKE '302%' \n" +
                "\tAND e.PRO_CLASS NOT LIKE '8%' and a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t6 = new StringBuilder().append("GROUP BY\n" +
                "\ta.CLIENT,\n" +
                "\td.STO_SHORT_NAME,\n" +
                "\tc.FRANC_NAME,\n" +
                "\ta.GSSD_BR_ID,\n" +
                "\ta.GSSD_SALER_ID,\n" +
                "\tf.USER_NAM \n" +
                "\t) t6 on t1.CLIENT = t6.CLIENT \n" +
                "\tAND t1.GSSD_BR_ID = t6.GSSD_BR_ID \n" +
                "\tAND t1.GSSD_SALER_ID = t6.GSSD_SALER_ID \n" +
                "\tLEFT JOIN (SELECT\n" +
                "\taa.CLIENT,\n" +
                "\taa.GSSD_BR_ID,\n" +
                "\taa.GSSD_SALER_ID,\n" +
                "\taa.USER_NAM,\n" +
                "\tcount( 1 ) pcBillQty \n" +
                "\tFROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\ta.CLIENT,\n" +
                "\ta.GSSD_BR_ID,\n" +
                "\ta.GSSD_BILL_NO,\n" +
                "\tf.USER_NAM,\n" +
                "\ta.GSSD_SALER_ID \n" +
                "\tFROM\n" +
                "\tGAIA_SD_SALE_D a\n" +
                "\tINNER JOIN GAIA_CAL_DT b ON a.GSSD_DATE = b.GCD_DATE\n" +
                "\tLEFT JOIN GAIA_FRANCHISEE c ON c.CLIENT = a.CLIENT\n" +
                "\tINNER JOIN GAIA_STORE_DATA d ON d.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = d.STO_CODE\n" +
                "\tINNER JOIN GAIA_PRODUCT_BUSINESS e ON e.CLIENT = a.CLIENT \n" +
                "\tAND a.GSSD_BR_ID = e.PRO_SITE \n" +
                "\tAND a.GSSD_PRO_ID = e.PRO_SELF_CODE\n" +
                "\tLEFT JOIN GAIA_USER_DATA f ON f.CLIENT = a.CLIENT \n" +
                "\tAND f.USER_ID = a.GSSD_SALER_ID\n" +
                "\tINNER JOIN GAIA_PRODUCT_CLASS g ON g.PRO_CLASS_CODE = e.PRO_CLASS \n" +
                "\tWHERE " +
                "\te.PRO_CLASS NOT LIKE '301%' \n" +
                "\tAND e.PRO_CLASS NOT LIKE '302%' \n" +
                "\tAND e.PRO_CLASS NOT LIKE '8%' and a.GSSD_DATE >= '" + startTime + "' AND a.GSSD_DATE <= '" + endTime + "' and a.client = '" + inData.getClient() + "'");
        StringBuilder t7 = new StringBuilder().append("\n" +
                "\tGROUP BY\n" +
                "\ta.CLIENT,\n" +
                "\ta.GSSD_BR_ID,\n" +
                "\ta.GSSD_BILL_NO,\n" +
                "\ta.GSSD_SALER_ID,\n" +
                "\tf.USER_NAM \n" +
                "\t) aa \n" +
                "\tGROUP BY\n" +
                "\taa.CLIENT,\n" +
                "\taa.GSSD_BR_ID,\n" +
                "\taa.GSSD_SALER_ID,\n" +
                "\taa.USER_NAM \n" +
                "\t) t7 on t1.CLIENT = t7.CLIENT \n" +
                "\tAND t1.GSSD_BR_ID = t7.GSSD_BR_ID \n" +
                "\tAND t7.GSSD_SALER_ID = t1.GSSD_SALER_ID\n" +
                "\tWHERE\n" +
                "\t1 = 1 \n" +
                "\tGROUP BY\n" +
                "\tt1.GSSD_BR_ID,\n" +
                "\tt1.CLIENT,\n" +
                "\tt1.USER_NAM,\n" +
                "\tt1.GSSD_SALER_ID,\n" +
                "\tt1.stoName,\n" +
                "\tt1.salesAmt,\n" +
                "\tt1.salesQty,\n" +
                "\tt2.billQty,\n" +
                "\tt3.salesDay,\n" +
                "\tt4.goodsQty,\n" +
                "\tt5.vipQty ,t6.pcSalesAmt,t6.pcSalesQty,t7.pcBillQty\n" +
                "\t) tt \n" +
                "WHERE\n" +
                "\t1 = 1");
        if (StringUtils.isNotEmpty(inData.getKeyWords())) {
            t7.append(" and (tt.userName like '%" + inData.getKeyWords() + "%'");
            t7.append(" or tt.userId like '%" + inData.getKeyWords() + "%'");
            t7.append(" or tt.stoName like '%" + inData.getKeyWords() + "%'");
            t7.append(" or tt.stoCode like '%" + inData.getKeyWords() + "%' )");
        }
        if (!CollectionUtils.isEmpty(inData.getStoCodeList())) {
            StringBuilder stoCodeList = new StringBuilder().append("('");
            for (int i = 0; i < inData.getStoCodeList().size(); i++) {
                if (i == inData.getStoCodeList().size() - 1) {
                    stoCodeList.append(inData.getStoCodeList().get(i) + "')");
                } else {
                    stoCodeList.append(inData.getStoCodeList().get(i) + "','");
                }
            }
            head1.append(" and a.GSSD_BR_ID in " + stoCodeList);
            t1.append(" and a.GSSD_BR_ID in " + stoCodeList);
            t2.append(" and a.GSSD_BR_ID in " + stoCodeList);
            t3.append(" and a.GSSD_BR_ID in " + stoCodeList);
            t4.append(" and a.GSMBC_BR_ID in " + stoCodeList);
            t5.append(" and a.GSSD_BR_ID in " + stoCodeList);
            t6.append(" and a.GSSD_BR_ID in " + stoCodeList);

        }
        StringBuilder sql = head1.append(t1).append(t2).append(t3).append(t4).append(t5).append(t6).append(t7);
        return sql;
    }

    private BigDecimal divide(BigDecimal lastYearSalesAmt, BigDecimal salesAmt2) {
        lastYearSalesAmt = ObjectUtil.isNotEmpty(lastYearSalesAmt) ? lastYearSalesAmt : BigDecimal.ZERO;
        salesAmt2 = ObjectUtil.isNotEmpty(salesAmt2) ? salesAmt2 : BigDecimal.ZERO;
        return salesAmt2.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : lastYearSalesAmt.divide(salesAmt2, 4, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal checkNull(Double num) {
        BigDecimal bigDecimal = ObjectUtil.isNotEmpty(num) ? new BigDecimal(num.toString()) : BigDecimal.ZERO;
        return bigDecimal;
    }

    private List<Map<String, Object>> copyList(List<Map<String, Object>> list) {
        ArrayList<Map<String, Object>> ts = new ArrayList<>();
        for (Map<String, Object> t : list) {
            HashMap<String, Object> map = new HashMap<>();
            Set<Map.Entry<String, Object>> entries = t.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            ts.add(map);
        }
        return ts;
    }

    /**
     * 首页看板_员工
     *
     * @param request
     * @return
     */
    @Override
    public EmployeesData getPersonnelData(GetLoginOutData userInfo, Integer dayType) throws ParseException {
        if (Objects.isNull(userInfo)) {
            throw new BusinessException("请重新登录");
        }
       /* SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String da = "2021-09-04";
        Date parse = format.parse(da);*/
        Date date = new Date();
      /*  String startTime="20210801";
        String endTime = "20210802";*/
        String startTime = null;
        String endTime = null;
        if (dayType == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, -24);
            date = calendar.getTime();
            //昨天日期
            endTime = com.gys.util.DateUtil.formatDate(date);
            //昨天的昨天日期
            calendar.set(Calendar.HOUR_OF_DAY, -24);
            date = calendar.getTime();
            startTime = com.gys.util.DateUtil.formatDate(date);
        } else if (dayType == 1) {
            //今天
            endTime = com.gys.util.DateUtil.formatDate(new Date());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, -24);
            date = calendar.getTime();
            //昨天
            startTime = com.gys.util.DateUtil.formatDate(date);

        }

        EmployeesData.Employees employeesDay = new EmployeesData.Employees();
        EmployeesData.Employees employeesYear = new EmployeesData.Employees();
        List<EmployeesData.Employees> data = this.gaiaGlobalDataMapper.getPersonnelData(userInfo.getClient(), userInfo.getUserId(), startTime, endTime);
        if (CollUtil.isNotEmpty(data)) {
            for (EmployeesData.Employees datum : data) {
                if (Objects.equals(datum.getDateTime(), endTime)) {
                    BeanUtil.copyProperties(datum, employeesDay);
                }
                if (Objects.equals(datum.getDateTime(), startTime)) {
                    BeanUtil.copyProperties(datum, employeesYear);
                }
            }
        }

        //获取销售天数
        int daySales = gaiaGlobalDataMapper.getDaysSales(userInfo.getClient(), userInfo.getUserId(), com.gys.util.DateUtil.formatDate(new Date(), "yyyyMM"));
        //获取会员卡
        List<Map<String, String>> cardCount = gaiaGlobalDataMapper.getDayCard(userInfo.getClient(), userInfo.getUserId(), endTime, startTime);
        if (CollUtil.isNotEmpty(cardCount)) {
            for (Map<String, String> card : cardCount) {
                if (Objects.equals(card.get("dates"), employeesDay.getDateTime())) {
                    employeesDay.setMemberCard(Convert.toInt(card.get("cardCount")));
                }
                if (Objects.equals(card.get("dates"), employeesYear.getDateTime())) {
                    employeesYear.setMemberCard(Convert.toInt(card.get("cardCount")));
                }
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        //封装参数
        PersonalSaleInData inData = new PersonalSaleInData();
        inData.setType("1");
        inData.setUserId(userInfo.getUserId());
        inData.setClient(userInfo.getClient());
        Map<String, Object> selectParamNew = getSelectParamNew("1", format.parse(endTime));

        String client = userInfo.getClient();
        String userId = userInfo.getUserId();
        calcCommissionAmt(client, userId, endTime, employeesDay);
        //获取销售提成
//        Map<String, Object> salesFeesMap = dailySellCommission(format.parse(endTime), inData, selectParamNew);
//        if (CollUtil.isNotEmpty(salesFeesMap)) {
//            HashMap<String, Object> result = new HashMap<>();
//            result.put("ticheng", salesFeesMap.get("TICHENG"));
//            result.put("day", salesFeesMap.get("TIMETYPE"));
//            employeesDay.setSalesCommissions(Convert.toStr(new BigDecimal(Convert.toStr(salesFeesMap.get("TICHENG"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
//        }
        //获取单品提成
//        Map<String, Object> itemFeesMap = dailyProCommission(format.parse(endTime), inData, selectParamNew);
//        if (CollUtil.isNotEmpty(itemFeesMap)) {
//            employeesDay.setItemFees(Convert.toStr(new BigDecimal(Convert.toStr(itemFeesMap.get("TICHENG"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
//        }
        //获取提成合计
        employeesDay.setCommissionTotals(Convert.toStr((new BigDecimal(employeesDay.getSalesCommissions()).add(new BigDecimal(employeesDay.getItemFees())))));

        //-------------------------------------------------昨日数据----------------------------------------------------------------------------
        //昨日数据
        Map<String, Object> selectParamYear = getSelectParamNew("1", format.parse(startTime));

        calcCommissionAmt(client, userId, startTime, employeesYear);

        //获取销售提成
//        Map<String, Object> salesFeesYearMap = dailySellCommission(format.parse(startTime), inData, selectParamYear);
//        if (CollUtil.isNotEmpty(salesFeesYearMap)) {
//            HashMap<String, Object> result = new HashMap<>();
//            result.put("ticheng", salesFeesYearMap.get("TICHENG"));
//            result.put("day", salesFeesYearMap.get("TIMETYPE"));
//
//
//            employeesYear.setSalesCommissions(Convert.toStr(new BigDecimal(Convert.toStr(salesFeesYearMap.get("TICHENG"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
//        }
        //获取单品提成
//        Map<String, Object> itemFeesYearMap = dailyProCommission(format.parse(startTime), inData, selectParamYear);
//        if (CollUtil.isNotEmpty(itemFeesYearMap)) {
//            employeesYear.setItemFees(Convert.toStr(new BigDecimal(Convert.toStr(itemFeesYearMap.get("TICHENG"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
//        }
        //获取提成合计
        employeesYear.setCommissionTotals(Convert.toStr((new BigDecimal(employeesYear.getSalesCommissions()).add(new BigDecimal(employeesYear.getItemFees())))));


        //今天实收金额
        BigDecimal actualAmountDay = new BigDecimal(employeesDay.getActualAmount());
        //昨天实收金额
        BigDecimal actualAmountYear = new BigDecimal(employeesYear.getActualAmount());
        if (actualAmountDay.compareTo(actualAmountYear) == 1) {
            employeesDay.setActualAmountRanking(0);
        } else if (actualAmountDay.compareTo(actualAmountYear) == -1) {
            employeesDay.setActualAmountRanking(1);
        }
        //今天毛利额
        BigDecimal grossProfitAmountDay = new BigDecimal(employeesDay.getGrossProfitAmount());
        //昨日毛利额
        BigDecimal grossProfitAmountYear = new BigDecimal(employeesYear.getGrossProfitAmount());
        if (grossProfitAmountDay.compareTo(grossProfitAmountYear) == 1) {
            employeesDay.setGrossProfitAmountRanking(0);
        } else if (grossProfitAmountDay.compareTo(grossProfitAmountYear) == -1) {
            employeesDay.setGrossProfitAmountRanking(1);
        }
        //今天毛利率
        BigDecimal grossMarginRateDay = new BigDecimal(employeesDay.getGrossMarginRate());
        //昨日毛利率
        BigDecimal grossMarginRateYear = new BigDecimal(employeesYear.getGrossMarginRate());
        if (grossMarginRateDay.compareTo(grossMarginRateYear) == 1) {
            employeesDay.setGrossMarginRateRanking(0);
        } else if (grossMarginRateDay.compareTo(grossMarginRateYear) == -1) {
            employeesDay.setGrossMarginRateRanking(1);
        }

        //今天交易次数
        BigDecimal getTradeCountDay = new BigDecimal(employeesDay.getTradeCount());
        //昨日交易次数
        BigDecimal getTradeCountYear = new BigDecimal(employeesYear.getTradeCount());

        if (getTradeCountDay.compareTo(getTradeCountYear) == 1) {
            employeesDay.setTradeCountRanking(0);
        } else if (getTradeCountDay.compareTo(getTradeCountYear) == -1) {
            employeesDay.setTradeCountRanking(1);
        }


        //今天客单价
        BigDecimal customerPriceDay = new BigDecimal(employeesDay.getCustomerPrice());
        //昨日客单价
        BigDecimal customerPriceYear = new BigDecimal(employeesYear.getCustomerPrice());
        if (customerPriceDay.compareTo(customerPriceYear) == 1) {
            employeesDay.setCustomerPriceRanking(0);
        } else if (customerPriceDay.compareTo(customerPriceYear) == -1) {
            employeesDay.setCustomerPriceRanking(1);
        }

        //今天会员卡
        BigDecimal memberCardDay = new BigDecimal(employeesDay.getMemberCard());
        //昨日会员卡
        BigDecimal memberCardYear = new BigDecimal(employeesYear.getMemberCard());
        if (memberCardDay.compareTo(memberCardYear) == 1) {
            employeesDay.setMemberCardRanking(0);
        } else if (memberCardDay.compareTo(memberCardYear) == -1) {
            employeesDay.setMemberCardRanking(1);
        }


        //今天提成合计
        BigDecimal commissionTotalsDay = new BigDecimal(employeesDay.getCommissionTotals());
        //昨日提成合计
        BigDecimal commissionTotalsYear = new BigDecimal(employeesYear.getCommissionTotals());
        if (commissionTotalsDay.compareTo(commissionTotalsYear) == 1) {
            employeesDay.setCommissionTotalsRanking(0);
        } else if (commissionTotalsDay.compareTo(commissionTotalsYear) == -1) {
            employeesDay.setCommissionTotalsRanking(1);
        }


        //今天销售提成
        BigDecimal salesCommissionsDay = new BigDecimal(employeesDay.getSalesCommissions());
        //昨日销售提成
        BigDecimal salesCommissionsYear = new BigDecimal(employeesYear.getSalesCommissions());
        if (salesCommissionsDay.compareTo(salesCommissionsYear) == 1) {
            employeesDay.setSalesCommissionsRanking(0);
        } else if (salesCommissionsDay.compareTo(salesCommissionsYear) == -1) {
            employeesDay.setSalesCommissionsRanking(1);
        }

        //今天单品提成
        BigDecimal itemFeesDay = new BigDecimal(employeesDay.getItemFees());
        //昨日单品提成
        BigDecimal itemFeesYear = new BigDecimal(employeesYear.getItemFees());
        if (itemFeesDay.compareTo(itemFeesYear) == 1) {
            employeesDay.setItemFeesRanking(0);
        } else if (itemFeesDay.compareTo(itemFeesYear) == -1) {
            employeesDay.setItemFeesRanking(1);
        }

        //毛利率拼接
        employeesDay.setGrossMarginRate(employeesDay.getGrossMarginRate().concat("%"));

        EmployeesData employeesData = new EmployeesData();
        employeesData.setEmployeesData(employeesDay);
        employeesData.setDaysSales(daySales);
        return employeesData;
    }

    private void calcCommissionAmt(String client, String userId, String day,
                                   EmployeesData.Employees employees) {
        StoreCommissionSummaryDO storeCommissionSummaryDO = new StoreCommissionSummaryDO();
        storeCommissionSummaryDO.setPageSize(null);
        storeCommissionSummaryDO.setPageNum(null);
        storeCommissionSummaryDO.setClient(client);
        storeCommissionSummaryDO.setStartDate(day);
        storeCommissionSummaryDO.setEndDate(day);
        storeCommissionSummaryDO.setSummaryType(2);
        storeCommissionSummaryDO.setUserId(userId);
        Weekend<GaiaCalDate> calDateWeekend = new Weekend<>(GaiaCalDate.class);
        calDateWeekend.weekendCriteria()
                .andEqualTo(GaiaCalDate::getGcdDate, day);
        List<GaiaCalDate> gaiaCalDates = gaiaCalDateMapper.selectByExample(calDateWeekend);
        Map<String, String> calDateMap = gaiaCalDates.stream()
                .collect(Collectors.toMap(GaiaCalDate::getGcdDate,
                        s -> s.getGcdDate()));

        // 1.计算单品提成
        Map<String, AppCommissionSummary> proCommissionPlan = calcProCommissionPlan(storeCommissionSummaryDO, calDateMap);
        if (proCommissionPlan != null && proCommissionPlan.get(day) != null) {
            employees.setItemFees(proCommissionPlan.get(day).getCommissionAmt().toString());
        }

        storeCommissionSummaryDO = new StoreCommissionSummaryDO();
        storeCommissionSummaryDO.setPageSize(null);
        storeCommissionSummaryDO.setPageNum(null);
        storeCommissionSummaryDO.setClient(client);
        storeCommissionSummaryDO.setStartDate(day);
        storeCommissionSummaryDO.setEndDate(day);
        storeCommissionSummaryDO.setSummaryType(2);
        storeCommissionSummaryDO.setUserId(userId);
        Map<String, AppCommissionSummary> saleCommissionPlan = calcSaleCommissionPlan(storeCommissionSummaryDO, calDateMap);
        if (saleCommissionPlan != null && saleCommissionPlan.get(day) != null) {
            employees.setSalesCommissions(saleCommissionPlan.get(day).getCommissionAmt().toString());
        }
    }

}

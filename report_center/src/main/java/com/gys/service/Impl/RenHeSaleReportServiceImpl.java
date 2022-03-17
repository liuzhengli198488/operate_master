package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.common.exception.BusinessException;
import com.gys.entity.data.salesSummary.PersonSalesInData;
import com.gys.entity.data.salesSummary.PersonSalesOutDataTotal;
import com.gys.entity.renhe.RenHePersonSales;
import com.gys.entity.renhe.StoreProductSaleSummaryInData;
import com.gys.entity.renhe.StoreProductSaleSummaryOutData;
import com.gys.entity.renhe.StoreSaleSummaryInData;
import com.gys.mapper.GaiaSalesSummaryMapper;
import com.gys.mapper.GaiaSdSaleDMapper;
import com.gys.mapper.GaiaSdStoresGroupMapper;
import com.gys.report.entity.*;
import com.gys.service.RenHeSaleReportService;
import com.gys.util.CollectorsUtil;
import com.gys.util.CommonUtil;
import com.gys.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RenHeSaleReportServiceImpl implements RenHeSaleReportService {

    @Autowired
    private GaiaSalesSummaryMapper summaryMapper;
    @Resource
    private GaiaSdStoresGroupMapper gaiaSdStoresGroupMapper;
    @Autowired
    private GaiaSdSaleDMapper saleDMapper;


    @Override
    public Map<String, Object> selectStoreSaleSummaryByBrId(StoreSaleSummaryInData summaryInData) {
        Map<String, Object> result = new HashMap<>();
        //校验参数
        if (ObjectUtil.isEmpty(summaryInData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(summaryInData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        if (StrUtil.isNotBlank(summaryInData.getStatDatePart())) {
            summaryInData.setStatDatePart(summaryInData.getStatDatePart() + "00");
        }
        if (StrUtil.isNotBlank(summaryInData.getEndDatePart())) {
            summaryInData.setEndDatePart(summaryInData.getEndDatePart() + "59");
        }
        //获取支付方式
        GetPayInData inData = new GetPayInData();
        inData.setClientId(summaryInData.getClient());
        inData.setType("1");
        String gssgId = summaryInData.getGssgId();
        if (StringUtils.isNotBlank(gssgId)) {
            summaryInData.setGssgIds(Arrays.asList(gssgId.split(StrUtil.COMMA)));
        }
        Set<String> stoGssgTypeSet = new HashSet<>();
        String stoGssgType = summaryInData.getStoGssgType();
        boolean noChooseFlag = true;
        if (stoGssgType != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : stoGssgType.split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            summaryInData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        String stoAttribute = summaryInData.getStoAttribute();
        if (stoAttribute != null) {
            noChooseFlag = false;
            summaryInData.setStoAttributes(Arrays.asList(stoAttribute.split(StrUtil.COMMA)));
        }
        String stoIfMedical = summaryInData.getStoIfMedical();
        if (stoIfMedical != null) {
            noChooseFlag = false;
            summaryInData.setStoIfMedicals(Arrays.asList(stoIfMedical.split(StrUtil.COMMA)));
        }
        String stoTaxClass = summaryInData.getStoTaxClass();
        if (stoTaxClass != null) {
            noChooseFlag = false;
            summaryInData.setStoTaxClasss(Arrays.asList(stoTaxClass.split(StrUtil.COMMA)));
        }
        String stoIfDtp = summaryInData.getStoIfDtp();
        if (stoIfDtp != null) {
            noChooseFlag = false;
            summaryInData.setStoIfDtps(Arrays.asList(stoIfDtp.split(StrUtil.COMMA)));
        }

        //查询结果list
        List<Map<String, Object>> salesStoSummaries = summaryMapper.selectStoreSaleSummaryByBrId(summaryInData);
        //获取门店分类
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(summaryInData.getClient());
        //对list进行处理
        for (Map<String, Object> item : salesStoSummaries) {
            String brId = MapUtil.getStr(item, "brId");
            // 转换
            if (stoAttribute != null || noChooseFlag) {
                item.put("stoAttribute", StoreAttributeEnum.getName(MapUtil.getStr(item, "stoAttribute")));
            }
            if (stoIfMedical != null || noChooseFlag) {
                item.put("stoIfMedical", StoreMedicalEnum.getName(MapUtil.getStr(item, "stoIfMedical")));
            }
            if (stoIfDtp != null || noChooseFlag) {
                item.put("stoIfDtp", StoreDTPEnum.getName(MapUtil.getStr(item, "stoIfDtp")));
            }
            if (stoTaxClass != null || noChooseFlag) {
                item.put("stoTaxClass", StoreTaxClassEnum.getName(MapUtil.getStr(item, "stoTaxClass")));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(brId)).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType gaiaStoreCategoryType : collect) {
                String field = null;
                boolean flag = false;
                String gssgType = gaiaStoreCategoryType.getGssgType();
                if (noChooseFlag) {
                    if (gssgType.contains("DX0001")) {
                        field = "shopType";
                    } else if (gssgType.contains("DX0002")) {
                        field = "storeEfficiencyLevel";
                    } else if (gssgType.contains("DX0003")) {
                        field = "directManaged";
                    } else if (gssgType.contains("DX0004")) {
                        field = "managementArea";
                    }
                    if (StringUtils.isNotBlank(field)) {
                        item.put(field, gaiaStoreCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(gssgType)) {
                            field = "shopType";
                            flag = true;
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(gssgType)) {
                            field = "storeEfficiencyLevel";
                            flag = true;
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(gssgType)) {
                            field = "directManaged";
                            flag = true;
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(gssgType)) {
                            field = "managementArea";
                            flag = true;
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                    if (flag) {
                        item.put(field, gaiaStoreCategoryType.getGssgIdName());
                    }
                }
            }
        }
        Map<String, Object> totalCensus = new HashMap<>();
        //统计
        for (Map<String, Object> item : salesStoSummaries) {
            if (StrUtil.isNotBlank(Convert.toStr(item.get("datePart")))) {
                String hm = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(item.get("datePart")), "HHmmss"), "HH:mm");
                totalCensus.put("datePart", hm);
            }
            if (totalCensus.containsKey("gssdnormalAmt")) {
                BigDecimal gssdnormalAmt = new BigDecimal(totalCensus.get("gssdnormalAmt").toString()).add(new BigDecimal(item.get("gssdnormalAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("gssdnormalAmt", gssdnormalAmt);
            } else {
                totalCensus.put("gssdnormalAmt", item.get("gssdnormalAmt").toString());
            }
            if (totalCensus.containsKey("gssdAmt")) {
                BigDecimal gssdAmt = new BigDecimal(totalCensus.get("gssdAmt").toString()).add(new BigDecimal(item.get("gssdAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("gssdAmt", gssdAmt);
            } else {
                totalCensus.put("gssdAmt", item.get("gssdAmt").toString());
            }
            if (totalCensus.containsKey("discountAmt")) {
                BigDecimal discountAmt = new BigDecimal(totalCensus.get("discountAmt").toString()).add(new BigDecimal(item.get("discountAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("discountAmt", discountAmt);
            } else {
                totalCensus.put("discountAmt", item.get("discountAmt").toString());
            }
            if (totalCensus.containsKey("costAmt")) {
                BigDecimal costAmt = new BigDecimal(totalCensus.get("costAmt").toString()).add(new BigDecimal(item.get("costAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("costAmt", costAmt);
            } else {
                totalCensus.put("costAmt", item.get("costAmt").toString());
            }
            if (totalCensus.containsKey("grossProfitAmt")) {
                BigDecimal grossProfitAmt = new BigDecimal(totalCensus.get("grossProfitAmt").toString()).add(new BigDecimal(item.get("grossProfitAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("grossProfitAmt", grossProfitAmt);
            } else {
                totalCensus.put("grossProfitAmt", item.get("grossProfitAmt").toString());
            }
            if (totalCensus.containsKey("gsshHykAmt")) {
                BigDecimal gsshHykAmt = new BigDecimal(totalCensus.get("gsshHykAmt").toString()).add(new BigDecimal(item.get("gsshHykAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("gsshHykAmt", gsshHykAmt);
            } else {
                totalCensus.put("gsshHykAmt", item.get("gsshHykAmt").toString());
            }
            if (totalCensus.containsKey("payCount")) {
                BigDecimal payCount = new BigDecimal(totalCensus.get("payCount").toString()).add(new BigDecimal(item.get("payCount").toString())).setScale(0, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("payCount", payCount);
            } else {
                totalCensus.put("payCount", item.get("payCount").toString());
            }
            if (totalCensus.containsKey("allCostAmt")) {
                BigDecimal allCostAmt = new BigDecimal(totalCensus.get("allCostAmt").toString()).add(new BigDecimal(item.get("allCostAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("allCostAmt", allCostAmt);
            } else {
                totalCensus.put("allCostAmt", item.get("allCostAmt").toString());
            }
            if (totalCensus.containsKey("payDayTime")) {
                BigDecimal payDayTime = new BigDecimal(totalCensus.get("payDayTime").toString()).add(new BigDecimal(item.get("payDayTime").toString())).setScale(0, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("payDayTime", payDayTime);
            } else {
                totalCensus.put("payDayTime", item.get("payDayTime").toString());
            }
            if (totalCensus.containsKey("jfdhZkAmt")) {
                BigDecimal jfdhZkAmt = new BigDecimal(totalCensus.get("jfdhZkAmt").toString()).add(new BigDecimal(item.get("jfdhZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("jfdhZkAmt", jfdhZkAmt);
            } else {
                totalCensus.put("jfdhZkAmt", item.get("jfdhZkAmt").toString());
            }
            if (totalCensus.containsKey("pmZkAmt")) {
                BigDecimal pmZkAmt = new BigDecimal(totalCensus.get("pmZkAmt").toString()).add(new BigDecimal(item.get("pmZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("pmZkAmt", pmZkAmt);
            } else {
                totalCensus.put("pmZkAmt", item.get("pmZkAmt").toString());
            }
            if (totalCensus.containsKey("jfdxZkAmt")) {
                BigDecimal jfdxZkAmt = new BigDecimal(totalCensus.get("jfdxZkAmt").toString()).add(new BigDecimal(item.get("jfdxZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("jfdxZkAmt", jfdxZkAmt);
            } else {
                totalCensus.put("jfdxZkAmt", item.get("jfdxZkAmt").toString());
            }
            if (totalCensus.containsKey("dzqZkAmt")) {
                BigDecimal dzqZkAmt = new BigDecimal(totalCensus.get("dzqZkAmt").toString()).add(new BigDecimal(item.get("dzqZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("dzqZkAmt", dzqZkAmt);
            } else {
                totalCensus.put("dzqZkAmt", item.get("dzqZkAmt").toString());
            }
            if (totalCensus.containsKey("dyqZkAmt")) {
                BigDecimal dyqZkAmt = new BigDecimal(totalCensus.get("dyqZkAmt").toString()).add(new BigDecimal(item.get("dyqZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("dyqZkAmt", dyqZkAmt);
            } else {
                totalCensus.put("dyqZkAmt", item.get("dyqZkAmt").toString());
            }
            //山西仁和定制字段
            if (totalCensus.containsKey("restoreSaleAmt")) {
                BigDecimal restoreSaleAmt = new BigDecimal(totalCensus.get("restoreSaleAmt").toString()).add(new BigDecimal(item.get("restoreSaleAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("restoreSaleAmt", restoreSaleAmt);
            } else {
                totalCensus.put("restoreSaleAmt", item.get("restoreSaleAmt").toString());
            }
            if (totalCensus.containsKey("doctorGssdAmt")) {
                BigDecimal doctorGssdAmt = new BigDecimal(totalCensus.get("doctorGssdAmt").toString()).add(new BigDecimal(item.get("doctorGssdAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("doctorGssdAmt", doctorGssdAmt);
            } else {
                totalCensus.put("doctorGssdAmt", item.get("doctorGssdAmt").toString());
            }
            if (totalCensus.containsKey("doctorGrossProfitAmt")) {
                BigDecimal doctorGrossProfitAmt = new BigDecimal(totalCensus.get("doctorGrossProfitAmt").toString()).add(new BigDecimal(item.get("doctorGrossProfitAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("doctorGrossProfitAmt", doctorGrossProfitAmt);
            } else {
                totalCensus.put("doctorGrossProfitAmt", item.get("doctorGrossProfitAmt").toString());
            }
            if (totalCensus.containsKey("doctorPayCount")) {
                BigDecimal doctorPayCount = new BigDecimal(totalCensus.get("doctorPayCount").toString()).add(new BigDecimal(item.get("doctorPayCount").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("doctorPayCount", doctorPayCount);
            } else {
                totalCensus.put("doctorPayCount", item.get("doctorPayCount").toString());
            }
            if (totalCensus.containsKey("therapyGssdAmt")) {
                BigDecimal therapyGssdAmt = new BigDecimal(totalCensus.get("therapyGssdAmt").toString()).add(new BigDecimal(item.get("therapyGssdAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("therapyGssdAmt", therapyGssdAmt);
            } else {
                totalCensus.put("therapyGssdAmt", item.get("therapyGssdAmt").toString());
            }
            if (totalCensus.containsKey("therapyGrossProfitAmt")) {
                BigDecimal therapyGrossProfitAmt = new BigDecimal(totalCensus.get("therapyGrossProfitAmt").toString()).add(new BigDecimal(item.get("therapyGrossProfitAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("therapyGrossProfitAmt", therapyGrossProfitAmt);
            } else {
                totalCensus.put("therapyGrossProfitAmt", item.get("therapyGrossProfitAmt").toString());
            }
            if (totalCensus.containsKey("doctorGssdMovPrices")) {
                BigDecimal doctorGssdMovPrices = new BigDecimal(totalCensus.get("doctorGssdMovPrices").toString()).add(new BigDecimal(item.get("doctorGssdMovPrices").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("doctorGssdMovPrices", doctorGssdMovPrices);
            } else {
                totalCensus.put("doctorGssdMovPrices", item.get("doctorGssdMovPrices").toString());
            }
            if (totalCensus.containsKey("therapyGssdMovPrices")) {
                BigDecimal therapyGssdMovPrices = new BigDecimal(totalCensus.get("therapyGssdMovPrices").toString()).add(new BigDecimal(item.get("therapyGssdMovPrices").toString())).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("therapyGssdMovPrices", therapyGssdMovPrices);
            } else {
                totalCensus.put("therapyGssdMovPrices", item.get("therapyGssdMovPrices").toString());
            }

        }
        //统计综合数据单独处理
        if (salesStoSummaries.size() > 0) {
            String discountRate = "0.00%";
            if (!(new BigDecimal(totalCensus.get("gssdnormalAmt").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                discountRate = new BigDecimal(totalCensus.get("discountAmt").toString()).divide(new BigDecimal(totalCensus.get("gssdnormalAmt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
            }
            totalCensus.put("discountRate", discountRate);
            String grossProfitRate = "0.00%";
            String gsshHykCost = "0.00%";
            if (!(new BigDecimal(totalCensus.get("gssdAmt").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                grossProfitRate = new BigDecimal(totalCensus.get("grossProfitAmt").toString()).divide(new BigDecimal(totalCensus.get("gssdAmt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
                gsshHykCost = new BigDecimal(totalCensus.get("gsshHykAmt").toString()).divide(new BigDecimal(totalCensus.get("gssdAmt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
            }
            totalCensus.put("grossProfitRate", grossProfitRate);
            BigDecimal gsshSinglePrice = BigDecimal.ZERO;
            BigDecimal dailyPayAmt = BigDecimal.ZERO;
            BigDecimal dailyPayCount = BigDecimal.ZERO;
            BigDecimal dailyProfitAmt = BigDecimal.ZERO;
            if (!(new BigDecimal(totalCensus.get("payCount").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                gsshSinglePrice = new BigDecimal(totalCensus.get("gssdAmt").toString()).divide(new BigDecimal(totalCensus.get("payCount").toString()), 2, BigDecimal.ROUND_HALF_UP);
            }
            if (!(new BigDecimal(totalCensus.get("payDayTime").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                dailyPayAmt = new BigDecimal(totalCensus.get("gssdAmt").toString()).divide(new BigDecimal(totalCensus.get("payDayTime").toString()), 2, BigDecimal.ROUND_HALF_UP);
                dailyPayCount = new BigDecimal(totalCensus.get("payCount").toString()).divide(new BigDecimal(totalCensus.get("payDayTime").toString()), 2, BigDecimal.ROUND_HALF_UP);
                dailyProfitAmt = new BigDecimal(totalCensus.get("grossProfitAmt").toString()).divide(new BigDecimal(totalCensus.get("payDayTime").toString()), 2, BigDecimal.ROUND_HALF_UP);
            }
            totalCensus.put("gsshSinglePrice", gsshSinglePrice);
            totalCensus.put("dailyPayAmt", dailyPayAmt);
            totalCensus.put("dailyPayCount", dailyPayCount);
            totalCensus.put("gsshHykCost", gsshHykCost);
            totalCensus.put("dailyProfitAmt", dailyProfitAmt);
            //总医疗毛利率
            if (new BigDecimal(totalCensus.get("doctorGssdAmt").toString()).compareTo(BigDecimal.ZERO) == 0) {
                totalCensus.put("doctorGrossProfitRate", "0.00%");
            } else {
                BigDecimal doctorGrossProfitRate = (new BigDecimal(totalCensus.get("doctorGssdAmt").toString()).subtract(new BigDecimal(totalCensus.get("doctorGssdMovPrices").toString()))).divide(new BigDecimal(totalCensus.get("doctorGssdAmt").toString()), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("doctorGrossProfitRate", doctorGrossProfitRate.multiply(new BigDecimal(100)).setScale(2,RoundingMode.HALF_UP)+"%");
            }
            //总诊疗毛利率
            if (new BigDecimal(totalCensus.get("therapyGssdAmt").toString()).compareTo(BigDecimal.ZERO) == 0) {
                totalCensus.put("therapyGrossProfitRate", "0.00%");
            } else {
                BigDecimal therapyGrossProfitRate = (new BigDecimal(totalCensus.get("therapyGssdAmt").toString()).subtract(new BigDecimal(totalCensus.get("therapyGssdMovPrices").toString()))).divide(new BigDecimal(totalCensus.get("therapyGssdAmt").toString()), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("therapyGrossProfitRate", therapyGrossProfitRate.multiply(new BigDecimal(100)).setScale(2,RoundingMode.HALF_UP)+"%");
            }
            //客单价
            if (new BigDecimal(totalCensus.get("doctorPayCount").toString()).compareTo(BigDecimal.ZERO) == 0) {
                totalCensus.put("doctorGsshSinglePrice", 0);
            } else {
                BigDecimal doctorGsshSinglePrice = (new BigDecimal(totalCensus.get("doctorGssdAmt").toString())).divide(new BigDecimal(totalCensus.get("doctorPayCount").toString()), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
                totalCensus.put("doctorGsshSinglePrice", doctorGsshSinglePrice.setScale(2,RoundingMode.HALF_UP));
            }


            result.put("totalCensus", totalCensus);
            result.put("itemCensus", salesStoSummaries);
        }
        return result;
    }

    @Override
    public PageInfo selectStoreSaleByDate(StoreSaleDateInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        if (inData.getDateType().equals("1")) {
            inData.setStartDate(DateUtil.getYearFirst(Integer.parseInt(inData.getStartDate())));
            inData.setEndDate(DateUtil.getYearLast(Integer.parseInt(inData.getEndDate())));
        }
        if (inData.getDateType().equals("2")) {
            inData.setStartDate(DateUtil.getYearMonthFirst(inData.getStartDate()));
            inData.setEndDate(DateUtil.getYearMonthLast(inData.getEndDate()));
        }
        if (StrUtil.isNotBlank(inData.getStatDatePart())) {
            inData.setStatDatePart(inData.getStatDatePart() + "00");
        }
        if (StrUtil.isNotBlank(inData.getEndDatePart())) {
            inData.setEndDatePart(inData.getEndDatePart() + "59");
        }

        List<Map<String, Object>> outData = this.saleDMapper.selectRenHeStoreSaleByDate(inData);

        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData) && outData.get(0).size() > 1) {
            // 集合列的数据汇总
            Map<String, Object> outTotal = this.saleDMapper.selectRenHeStoreSaleByDateTotal(inData);
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectStoreSalesSummaryByDate(com.gys.entity.data.salesSummary.StoreSaleDateInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        if (StrUtil.isNotBlank(inData.getStatDatePart())) {
            inData.setStatDatePart(inData.getStatDatePart() + "00");
        }
        if (StrUtil.isNotBlank(inData.getEndDatePart())) {
            inData.setEndDatePart(inData.getEndDatePart() + "59");
        }
        if (inData.getDateType().equals("1")) {
            inData.setStartDate(DateUtil.getYearFirst(Integer.parseInt(inData.getStartDate())));
            inData.setEndDate(DateUtil.getYearLast(Integer.parseInt(inData.getEndDate())));
        }

        if (inData.getDateType().equals("2")) {
            inData.setStartDate(DateUtil.getYearMonthFirst(inData.getStartDate()));
            inData.setEndDate(DateUtil.getYearMonthLast(inData.getEndDate()));
        }

        List<Map<String, Object>> outData = summaryMapper.selectStoreSalesSummaryByDate(inData);
        if (inData.getGrossProfitRateMax() != null && inData.getGrossProfitRateMax().length() > 0) {
            outData = outData.stream().filter(out -> {

                if (out.get("grossProfitRate") != null) {
                    return ((BigDecimal) out.get("grossProfitRate")).compareTo(BigDecimal.valueOf(Double.valueOf(inData.getGrossProfitRateMax()))) < 1;
                } else {
                    return false;
                }

            }).collect(Collectors.toList());
        }
        if (inData.getGrossProfitRateMin() != null && inData.getGrossProfitRateMin().length() > 0) {
            outData = outData.stream().filter(out -> {

                if (out.get("grossProfitRate") != null) {
                    return ((BigDecimal) out.get("grossProfitRate")).compareTo(BigDecimal.valueOf(Double.valueOf(inData.getGrossProfitRateMin()))) > -1;
                } else {
                    return false;
                }

            }).collect(Collectors.toList());
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            // 集合列的数据汇总
            Map<String, Object> outSto = this.summaryMapper.findSalesSummaryByTotal(inData);
            Long stoCount = (Long) outSto.get("stoCount");

            Map<String, Object> outTotal = new HashMap<>();
            outTotal.put("stoCount", stoCount);
            for (Map<String, Object> out : outData) {

                if (StrUtil.isNotBlank(Convert.toStr(out.get("datePart")))) {
                    String hm = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(out.get("datePart")), "HHmmss"), "HH:mm");
                    out.put("datePart", hm);
                }
                if (outTotal.containsKey("amountReceivable")) {
                    BigDecimal amountReceivable = new BigDecimal(outTotal.get("amountReceivable").toString()).add(new BigDecimal(out.get("amountReceivable").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amountReceivable", amountReceivable);
                } else {
                    outTotal.put("amountReceivable", out.get("amountReceivable").toString());
                }
                if (outTotal.containsKey("amt")) {
                    BigDecimal amt = new BigDecimal(outTotal.get("amt").toString()).add(new BigDecimal(out.get("amt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amt", amt);
                } else {
                    outTotal.put("amt", out.get("amt").toString());
                }
                if (outTotal.containsKey("zkJfdx")) {
                    BigDecimal zkJfdx = new BigDecimal(outTotal.get("zkJfdx").toString()).add(new BigDecimal(out.get("zkJfdx").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("zkJfdx", zkJfdx);
                } else {
                    outTotal.put("zkJfdx", out.get("zkJfdx").toString());
                }
                if (outTotal.containsKey("zkDyq")) {
                    BigDecimal zkDyq = new BigDecimal(outTotal.get("zkDyq").toString()).add(new BigDecimal(out.get("zkDyq").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("zkDyq", zkDyq);
                } else {
                    outTotal.put("zkDyq", out.get("zkDyq").toString());
                }
                if (outTotal.containsKey("zkDzq")) {
                    BigDecimal zkDzq = new BigDecimal(outTotal.get("zkDzq").toString()).add(new BigDecimal(out.get("zkDzq").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("zkDzq", zkDzq);
                } else {
                    outTotal.put("zkDzq", out.get("zkDzq").toString());
                }
                if (outTotal.containsKey("movPrices")) {
                    BigDecimal movPrices = new BigDecimal(outTotal.get("movPrices").toString()).add(new BigDecimal(out.get("movPrices").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("movPrices", movPrices);
                } else {
                    outTotal.put("movPrices", out.get("movPrices").toString());
                }
                if (outTotal.containsKey("grossProfitMargin")) {
                    BigDecimal grossProfitMargin = new BigDecimal(outTotal.get("grossProfitMargin").toString()).add(new BigDecimal(out.get("grossProfitMargin").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("grossProfitMargin", grossProfitMargin);
                } else {
                    outTotal.put("grossProfitMargin", out.get("grossProfitMargin").toString());
                }
                if (outTotal.containsKey("discountAmt")) {
                    BigDecimal discountAmt = new BigDecimal(outTotal.get("discountAmt").toString()).add(new BigDecimal(out.get("discountAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("discountAmt", discountAmt);
                } else {
                    outTotal.put("discountAmt", out.get("discountAmt").toString());
                }
                if (outTotal.containsKey("numberTradesByDay")) {
                    BigDecimal numberTradesByDay = new BigDecimal(outTotal.get("numberTradesByDay").toString()).add(new BigDecimal(out.get("numberTradesByDay").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("numberTradesByDay", numberTradesByDay);
                } else {
                    outTotal.put("numberTradesByDay", out.get("numberTradesByDay").toString());
                }
                if (outTotal.containsKey("memberSale")) {
                    BigDecimal memberSale = new BigDecimal(outTotal.get("memberSale").toString()).add(new BigDecimal(out.get("memberSale").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("memberSale", memberSale);
                } else {
                    outTotal.put("memberSale", out.get("memberSale").toString());
                }
                if (outTotal.containsKey("amtByDay")) {
                    BigDecimal amtByDay = new BigDecimal(outTotal.get("amtByDay").toString()).add(new BigDecimal(out.get("amtByDay").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amtByDay", amtByDay);
                } else {
                    outTotal.put("amtByDay", out.get("amtByDay").toString());
                }
                if (outTotal.containsKey("numberTrades")) {
                    BigDecimal amtByDay = new BigDecimal(outTotal.get("numberTrades").toString()).add(new BigDecimal(out.get("numberTrades").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("numberTrades", amtByDay);
                } else {
                    outTotal.put("numberTrades", out.get("numberTrades").toString());
                }

                if (outTotal.containsKey("restoreSaleAmt")) {
                    BigDecimal restoreSaleAmt = new BigDecimal(outTotal.get("restoreSaleAmt").toString()).add(new BigDecimal(out.get("restoreSaleAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("restoreSaleAmt", restoreSaleAmt);
                } else {
                    outTotal.put("restoreSaleAmt", out.get("restoreSaleAmt").toString());
                }
                if (outTotal.containsKey("doctorGssdAmt")) {
                    BigDecimal doctorGssdAmt = new BigDecimal(outTotal.get("doctorGssdAmt").toString()).add(new BigDecimal(out.get("doctorGssdAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("doctorGssdAmt", doctorGssdAmt);
                } else {
                    outTotal.put("doctorGssdAmt", out.get("doctorGssdAmt").toString());
                }
                if (outTotal.containsKey("doctorGrossProfitAmt")) {
                    BigDecimal doctorGrossProfitAmt = new BigDecimal(outTotal.get("doctorGrossProfitAmt").toString()).add(new BigDecimal(out.get("doctorGrossProfitAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("doctorGrossProfitAmt", doctorGrossProfitAmt);
                } else {
                    outTotal.put("doctorGrossProfitAmt", out.get("doctorGrossProfitAmt").toString());
                }
                if (outTotal.containsKey("doctorPayCount")) {
                    BigDecimal doctorPayCount = new BigDecimal(outTotal.get("doctorPayCount").toString()).add(new BigDecimal(out.get("doctorPayCount").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("doctorPayCount", doctorPayCount);
                } else {
                    outTotal.put("doctorPayCount", out.get("doctorPayCount").toString());
                }
                if (outTotal.containsKey("therapyGssdAmt")) {
                    BigDecimal therapyGssdAmt = new BigDecimal(outTotal.get("therapyGssdAmt").toString()).add(new BigDecimal(out.get("therapyGssdAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("therapyGssdAmt", therapyGssdAmt);
                } else {
                    outTotal.put("therapyGssdAmt", out.get("therapyGssdAmt").toString());
                }
                if (outTotal.containsKey("therapyGrossProfitAmt")) {
                    BigDecimal therapyGrossProfitAmt = new BigDecimal(outTotal.get("therapyGrossProfitAmt").toString()).add(new BigDecimal(out.get("therapyGrossProfitAmt").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("therapyGrossProfitAmt", therapyGrossProfitAmt);
                } else {
                    outTotal.put("therapyGrossProfitAmt", out.get("therapyGrossProfitAmt").toString());
                }
                if (outTotal.containsKey("doctorGssdMovPrices")) {
                    BigDecimal doctorGssdMovPrices = new BigDecimal(outTotal.get("doctorGssdMovPrices").toString()).add(new BigDecimal(out.get("doctorGssdMovPrices").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("doctorGssdMovPrices", doctorGssdMovPrices);
                } else {
                    outTotal.put("doctorGssdMovPrices", out.get("doctorGssdMovPrices").toString());
                }
                if (outTotal.containsKey("therapyGssdMovPrices")) {
                    BigDecimal therapyGssdMovPrices = new BigDecimal(outTotal.get("therapyGssdMovPrices").toString()).add(new BigDecimal(out.get("therapyGssdMovPrices").toString())).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("therapyGssdMovPrices", therapyGssdMovPrices);
                } else {
                    outTotal.put("therapyGssdMovPrices", out.get("therapyGssdMovPrices").toString());
                }
            }

            if (outData != null && outData.size() > 0) {
                String grossProfitRate = "0.00%";
                if (!(new BigDecimal(outTotal.get("amt").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    grossProfitRate = new BigDecimal(outTotal.get("grossProfitMargin").toString()).divide(new BigDecimal(outTotal.get("amt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
                }
                outTotal.put("grossProfitRate", grossProfitRate);
                String discountRate = "0.00%";
                if (!(new BigDecimal(outTotal.get("amountReceivable").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    discountRate = (new BigDecimal(outTotal.get("amountReceivable").toString()).subtract(new BigDecimal(outTotal.get("amt").toString()))).divide(new BigDecimal(outTotal.get("amountReceivable").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
                }
                outTotal.put("discountRate", discountRate);
                String memberSaleRate = "0.00%";
                if (!(new BigDecimal(outTotal.get("amt").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    memberSaleRate = new BigDecimal(outTotal.get("memberSale").toString()).divide(new BigDecimal(outTotal.get("amt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) + "%";
                }
                outTotal.put("memberSaleRate", memberSaleRate);

                String perTicketSales = "0.00";
                if (!(new BigDecimal(outTotal.get("amountReceivable").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    perTicketSales = new BigDecimal(outTotal.get("amt").toString()).divide(new BigDecimal(outTotal.get("numberTrades").toString()), 4, BigDecimal.ROUND_HALF_UP) + "";
                }
                outTotal.put("perTicketSales", perTicketSales);
                //总医疗毛利率
                if (new BigDecimal(outTotal.get("doctorGssdAmt").toString()).compareTo(BigDecimal.ZERO) == 0) {
                    outTotal.put("doctorGrossProfitRate", "0.00%");
                } else {
                    BigDecimal doctorGrossProfitRate = (new BigDecimal(outTotal.get("doctorGssdAmt").toString()).subtract(new BigDecimal(outTotal.get("doctorGssdMovPrices").toString()))).divide(new BigDecimal(outTotal.get("doctorGssdAmt").toString()), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("doctorGrossProfitRate", doctorGrossProfitRate.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"%");
                }
                //总诊疗毛利率
                if (new BigDecimal(outTotal.get("therapyGssdAmt").toString()).compareTo(BigDecimal.ZERO) == 0) {
                    outTotal.put("therapyGrossProfitRate", "0.00%");
                } else {
                    BigDecimal therapyGrossProfitRate = (new BigDecimal(outTotal.get("therapyGssdAmt").toString()).subtract(new BigDecimal(outTotal.get("therapyGssdMovPrices").toString()))).divide(new BigDecimal(outTotal.get("therapyGssdAmt").toString()), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("therapyGrossProfitRate", therapyGrossProfitRate.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"%");
                }
                //医疗客单价
                if (new BigDecimal(outTotal.get("doctorPayCount").toString()).compareTo(BigDecimal.ZERO) == 0) {
                    outTotal.put("doctorGsshSinglePrice", 0);
                } else {
                    BigDecimal doctorGsshSinglePrice = (new BigDecimal(outTotal.get("doctorGssdAmt").toString())).divide(new BigDecimal(outTotal.get("doctorPayCount").toString()), RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
                    outTotal.put("doctorGsshSinglePrice", doctorGsshSinglePrice.setScale(2, RoundingMode.HALF_UP));
                }
            }


            System.out.println(outTotal);
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectStoreProductSaleSummary(StoreProductSaleSummaryInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        boolean noChooseFlag = true;
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            inData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        if (inData.getStoAttribute() != null) {
            noChooseFlag = false;
            inData.setStoAttributes(Arrays.asList(inData.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfMedical() != null) {
            noChooseFlag = false;
            inData.setStoIfMedicals(Arrays.asList(inData.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (inData.getStoTaxClass() != null) {
            noChooseFlag = false;
            inData.setStoTaxClasss(Arrays.asList(inData.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfDtp() != null) {
            noChooseFlag = false;
            inData.setStoIfDtps(Arrays.asList(inData.getStoIfDtp().split(StrUtil.COMMA)));
        }
        List<StoreProductSaleSummaryOutData> outData;
        StoreProductSaleStoreOutTotal outTotal;
        outData = this.saleDMapper.selectRenHeStoreProductSaleSummary(inData);
        outTotal = this.saleDMapper.selectRenHeStoreProductSaleSummaryTotal(inData);
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClient());

        for (StoreProductSaleSummaryOutData saleStoreOutData : outData) {
            //转换
            if (saleStoreOutData.getStoAttribute() != null || noChooseFlag) {
                saleStoreOutData.setStoAttribute(StoreAttributeEnum.getName(saleStoreOutData.getStoAttribute()));
            }
            if (saleStoreOutData.getStoIfMedical() != null || noChooseFlag) {
                saleStoreOutData.setStoIfMedical(StoreMedicalEnum.getName(saleStoreOutData.getStoIfMedical()));
            }
            if (saleStoreOutData.getStoIfDtp() != null || noChooseFlag) {
                saleStoreOutData.setStoIfDtp(StoreDTPEnum.getName(saleStoreOutData.getStoIfDtp()));
            }
            if (saleStoreOutData.getStoTaxClass() != null || noChooseFlag) {
                saleStoreOutData.setStoTaxClass(StoreTaxClassEnum.getName(saleStoreOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(saleStoreOutData.getStoCode())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        saleStoreOutData.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        saleStoreOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        saleStoreOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        saleStoreOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            saleStoreOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            saleStoreOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            saleStoreOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            saleStoreOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }
        BigDecimal onlineSaleQty = BigDecimal.ZERO;
        BigDecimal onlineAmt = BigDecimal.ZERO;
        BigDecimal onlineGrossAmt = BigDecimal.ZERO;
        BigDecimal onlineMov = BigDecimal.ZERO;
        String onlineGrossRate = "0.00%";
        onlineSaleQty = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineSaleQty() == null ? BigDecimal.ZERO : out.getOnlineSaleQty()));
        onlineAmt = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineAmt() == null ? BigDecimal.ZERO : out.getOnlineAmt()));
        onlineGrossAmt = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineGrossAmt() == null ? BigDecimal.ZERO : out.getOnlineGrossAmt()));
        onlineMov = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineMov() == null ? BigDecimal.ZERO : out.getOnlineMov()));
        onlineGrossRate = onlineGrossAmt.divide(onlineAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : onlineAmt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
        if (ObjectUtil.isNotNull(outTotal)) {
            outTotal.setOnlineSaleQty(onlineSaleQty);
            outTotal.setOnlineAmt(onlineAmt);
            outTotal.setOnlineGrossAmt(onlineGrossAmt);
            outTotal.setOnlineMov(onlineMov);
            outTotal.setOnlineGrossRate(onlineGrossRate);
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            // 集合列的数据汇总
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectPersonSales(PersonSalesInData inData) {
        if(StringUtils.isEmpty(inData.getType())) {
            throw new BusinessException("提示：查询人员类型不能为空!");
        }
        if(StringUtils.isEmpty(inData.getNotSales())) {
            throw new BusinessException("提示：是否分门店查询不能为空!");
        }
        if (StringUtils.isEmpty(inData.getStartDate()) || StringUtils.isEmpty(inData.getEndDate())) {
            throw new BusinessException("提示：请选择开始时间和结束时间!");
        }
        List<RenHePersonSales> outData = summaryMapper.selectRenHePersonSales(inData);
        PageInfo pageInfo; // 创建分页对象
        if (CollUtil.isNotEmpty(outData)) {  // 判断集合是否为空
            PersonSalesOutDataTotal outTotal  = summaryMapper.selectRenHePersonSalesTotal(inData);
            pageInfo = new PageInfo(outData,outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

}

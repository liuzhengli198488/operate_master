package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.gys.common.constant.CommonConstant;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.GaiaStoreData;
import com.gys.entity.data.salesSummary.*;
import com.gys.entity.renhe.StoreSaleSummaryInData;
import com.gys.mapper.GaiaSalesSummaryMapper;
import com.gys.mapper.GaiaSdStoresGroupMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.report.entity.GaiaStoreCategoryType;
import com.gys.report.entity.GetPayInData;
import com.gys.report.entity.GetPayTypeOutData;
import com.gys.report.service.PayService;
import com.gys.service.SalesSummaryService;
import com.gys.util.*;
import io.swagger.annotations.ApiModelProperty;
import com.gys.util.CommonUtil;
import com.gys.util.UtilConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/4/29 13:43
 * @Version 1.0.0
 **/
@Slf4j
@Service
public class SalesSummaryServiceImpl implements SalesSummaryService {

    @Autowired
    private GaiaSalesSummaryMapper summaryMapper;

    @Autowired
    private GaiaStoreDataMapper gaiaStoreDataMapper;

    @Resource
    private GaiaSdStoresGroupMapper gaiaSdStoresGroupMapper;

    @Resource
    private PayService payService;

    @Autowired
    private GaiaStoreDataMapper storeDao;

    @Autowired
    private CosUtils cosUtils;

    @Override
    public PageInfo selectSalesByProduct(SalesSummaryData summaryData) {
        //???????????????   flag  0????????????  1?????????
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(summaryData.getClient(), null, UtilConst.GSSP_ID_IMPRO_DETAIL);
        if (ObjectUtil.isEmpty(flag)){
            summaryData.setFlag("0");
        }else {
            summaryData.setFlag(flag);
        }
        if(ObjectUtil.isNotEmpty(summaryData.getClassArr())){
            summaryData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(summaryData.getClassArr()));
        }
        if(StringUtils.isNotEmpty(summaryData.getGssdProId())){
            //???????????????
            if(summaryData.getGssdProId().contains("!") || summaryData.getGssdProId().contains("???")){
                String strSub1 = StrUtil.sub(summaryData.getGssdProId(), 0, -1);
                summaryData.setProGlyph(strSub1);
            }else {
                summaryData.setProArr(summaryData.getGssdProId().split("\\s+ |\\s+|,"));
            }
        }
        // ???????????????????????????????????????
        if (StringUtils.isNotEmpty(summaryData.getProSaleClass())){
            if (ObjectUtil.isEmpty(summaryData.getProSaleClassList())){
                ArrayList<String> proSaleClassList = new ArrayList<>();
                proSaleClassList.add(summaryData.getProSaleClass());
                summaryData.setProSaleClassList(proSaleClassList);
            }else {
                List<String> proSaleClassList = summaryData.getProSaleClassList();
                proSaleClassList.add(summaryData.getProSaleClass());
                summaryData.setProSaleClassList(proSaleClassList);
            }
        }
        List<GaiaSalesSummary> salesSummary = summaryMapper.selectSalesByProduct(summaryData);
        salesSummary.removeAll(Collections.singleton(null));
        GaiaSalesSummaryTotal salesSummaryTotal = new GaiaSalesSummaryTotal(); //????????????????????????

        PageInfo pageInfo; // ??????????????????
        if (CollUtil.isNotEmpty(salesSummary)) {  // ????????????????????????
            // salesSummaryTotal = summaryMapper.selectSalesByProductTotal(summaryData);
            //??????
            BigDecimal proQty = BigDecimal.ZERO;
            //????????????
            BigDecimal gssdnormalAmt = BigDecimal.ZERO;
            //????????????
            BigDecimal gssdAmt = BigDecimal.ZERO;
            //????????????
            BigDecimal discountAmt = BigDecimal.ZERO;
            //?????????
            BigDecimal discountRate = BigDecimal.ZERO;
            //?????????
            BigDecimal costAmt = BigDecimal.ZERO;
            //?????????
            BigDecimal grossProfitAmt = BigDecimal.ZERO;
            //?????????
            BigDecimal grossProfitRate = BigDecimal.ZERO;
            //????????????
            BigDecimal gsisdQty = BigDecimal.ZERO;
            for (GaiaSalesSummary gaiaSalesSummary : salesSummary) {
                proQty = proQty.add(Objects.isNull(gaiaSalesSummary.getProQty())?BigDecimal.ZERO:gaiaSalesSummary.getProQty());
                gssdnormalAmt = gssdnormalAmt.add(Objects.isNull(gaiaSalesSummary.getGssdnormalAmt())?BigDecimal.ZERO:gaiaSalesSummary.getGssdnormalAmt());
                gssdAmt = gssdAmt.add(Objects.isNull(gaiaSalesSummary.getGssdAmt())?BigDecimal.ZERO:gaiaSalesSummary.getGssdAmt());
                discountAmt = discountAmt.add(Objects.isNull(gaiaSalesSummary.getDiscountAmt())?BigDecimal.ZERO:gaiaSalesSummary.getDiscountAmt());
                costAmt = costAmt.add(Objects.isNull(gaiaSalesSummary.getCostAmt())?BigDecimal.ZERO:gaiaSalesSummary.getCostAmt());
                grossProfitAmt = grossProfitAmt.add(Objects.isNull(gaiaSalesSummary.getGrossProfitAmt())?BigDecimal.ZERO:gaiaSalesSummary.getGrossProfitAmt());
                gsisdQty = gsisdQty.add(Objects.isNull(gaiaSalesSummary.getGsisdQty())?BigDecimal.ZERO:gaiaSalesSummary.getGsisdQty());
            }
            salesSummaryTotal.setProQty(proQty);
            salesSummaryTotal.setGssdnormalAmt(gssdnormalAmt);
            salesSummaryTotal.setGssdAmt(gssdAmt);
            salesSummaryTotal.setDiscountAmt(discountAmt);
            //?????????
            salesSummaryTotal.setDiscountRate(BigDecimal.ZERO.compareTo(gssdnormalAmt) ==0?"0.00%": Convert.toStr(discountAmt.divide(gssdnormalAmt,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)).concat("%"));
            salesSummaryTotal.setCostAmt(costAmt);
            salesSummaryTotal.setGrossProfitAmt(grossProfitAmt);
            //?????????
            salesSummaryTotal.setGrossProfitRate(BigDecimal.ZERO.compareTo(gssdAmt) ==0?"0.00%": Convert.toStr(grossProfitAmt.divide(gssdAmt,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)).concat("%"));
            salesSummaryTotal.setGsisdQty(gsisdQty);
            pageInfo = new PageInfo(salesSummary);
            pageInfo.setListNum(salesSummaryTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo selectPersonSales(PersonSalesInData inData) {
        if(StringUtils.isEmpty(inData.getType())) {
            throw new BusinessException("???????????????????????????????????????!");
        }
        if(StringUtils.isEmpty(inData.getNotSales())) {
            throw new BusinessException("??????????????????????????????????????????!");
        }
        if (StringUtils.isEmpty(inData.getStartDate()) || StringUtils.isEmpty(inData.getEndDate())) {
            throw new BusinessException("?????????????????????????????????????????????!");
        }
        if(StrUtil.isNotBlank(inData.getStatDatePart())){
            inData.setStatDatePart(inData.getStatDatePart()+"00");
        }
        if(StrUtil.isNotBlank(inData.getEndDatePart())){
            inData.setEndDatePart(inData.getEndDatePart()+"59");
        }
        List<PersonSalesOutData> outData = summaryMapper.selectPersonSales(inData);
        PageInfo pageInfo; // ??????????????????
        if (CollUtil.isNotEmpty(outData)) {  // ????????????????????????
            PersonSalesOutDataTotal outTotal  = summaryMapper.selectPersonSalesTotal(inData);
            pageInfo = new PageInfo(outData,outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<PersonSalesOutData> selectPersonSalesByCSV(PersonSalesInData inData) {
        if(StrUtil.isNotBlank(inData.getStatDatePart())){
            inData.setStatDatePart(inData.getStatDatePart()+"00");
        }
        if(StrUtil.isNotBlank(inData.getEndDatePart())){
            inData.setEndDatePart(inData.getEndDatePart()+"59");
        }
        return summaryMapper.selectPersonSales(inData);
    }

    @Override
    public PageInfo selectPersonSalesDetail(PersonSalesDetaiInlData inData) {
        if(StringUtils.isEmpty(inData.getType())) {
            throw new BusinessException("???????????????????????????????????????!");
        }
//        if(StringUtils.isEmpty(inData.getNotSales())) {
//            throw new BusinessException("??????????????????????????????????????????!");
//        }
        if (StringUtils.isEmpty(inData.getStartDate()) || StringUtils.isEmpty(inData.getEndDate())) {
            throw new BusinessException("?????????????????????????????????????????????!");
        }
        List<PersonSalesDetailOutData> outData = summaryMapper.selectPersonSalesDetail(inData);
        PageInfo pageInfo; // ??????????????????
        if (CollUtil.isNotEmpty(outData)) {  // ????????????????????????
            PersonSalesDetailOutDataTotal outTotal  = summaryMapper.selectPersonSalesDetailTotal(inData);
            pageInfo = new PageInfo(outData,outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<PersonSalesDetailOutData> selectPersonSalesDetailByCSV(PersonSalesDetaiInlData inData) {
       return summaryMapper.selectPersonSalesDetail(inData);
    }

    @Override
    public Map<String, Object> findSalesSummaryByBrId(SalesSummaryDataReport summaryData) {

        Map<String, Object> result = new HashMap<>();
        if (ObjectUtil.isEmpty(summaryData.getStartDate())) {
            throw new BusinessException("???????????????????????????");
        }
        if (ObjectUtil.isEmpty(summaryData.getEndDate())) {
            throw new BusinessException("???????????????????????????");
        }
        if(StrUtil.isNotBlank(summaryData.getStatDatePart())){
            summaryData.setStatDatePart(summaryData.getStatDatePart()+"00");
        }
        if(StrUtil.isNotBlank(summaryData.getEndDatePart())){
            summaryData.setEndDatePart(summaryData.getEndDatePart()+"59");
        }
        GetPayInData inData = new GetPayInData();
        inData.setClientId(summaryData.getClient());
        inData.setType("1");
        List<GetPayTypeOutData> payTypeOutData = payService.payTypeListByClient(inData);
        if (payTypeOutData != null && payTypeOutData.size() > 0) {
            summaryData.setPayTypeOutData(payTypeOutData);
        }
       /* if(Objects.nonNull(summaryData.getPageNum()) && Objects.nonNull(summaryData.getPageSize())){
            PageHelper.startPage(summaryData.getPageNum(),summaryData.getPageSize());
        }*/

        String gssgId = summaryData.getGssgId();
        if (StringUtils.isNotBlank(gssgId)) {
            summaryData.setGssgIds(Arrays.asList(gssgId.split(StrUtil.COMMA)));
        }
        Set<String> stoGssgTypeSet = new HashSet<>();
        String stoGssgType = summaryData.getStoGssgType();
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
            summaryData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        String stoAttribute = summaryData.getStoAttribute();
        if (stoAttribute != null) {
            noChooseFlag = false;
            summaryData.setStoAttributes(Arrays.asList(stoAttribute.split(StrUtil.COMMA)));
        }
        String stoIfMedical = summaryData.getStoIfMedical();
        if (stoIfMedical != null) {
            noChooseFlag = false;
            summaryData.setStoIfMedicals(Arrays.asList(stoIfMedical.split(StrUtil.COMMA)));
        }
        String stoTaxClass = summaryData.getStoTaxClass();
        if (stoTaxClass != null) {
            noChooseFlag = false;
            summaryData.setStoTaxClasss(Arrays.asList(stoTaxClass.split(StrUtil.COMMA)));
        }
        String stoIfDtp = summaryData.getStoIfDtp();
        if (stoIfDtp != null) {
            noChooseFlag = false;
            summaryData.setStoIfDtps(Arrays.asList(stoIfDtp.split(StrUtil.COMMA)));
        }

        List<Map<String, Object>> salesStoSummaries = summaryMapper.findSalesSummaryByBrId(summaryData);
      System.err.println("salesStoSummaries="+JSON.toJSONString(salesStoSummaries));
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(summaryData.getClient());
        for (Map<String, Object> item : salesStoSummaries) {
            for (GetPayTypeOutData outData : payTypeOutData) {
                if (!item.containsKey(outData.getGspmId())) {
                    item.put(outData.getGspmId(), "0.00");
                }
            }

            String brId = MapUtil.getStr(item, "brId");
            // ??????
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
        DecimalFormat df1 =new DecimalFormat();
        DecimalFormat df2 =new DecimalFormat("#0.00");
        BigDecimal totlePrice=BigDecimal.ZERO;
        df1.applyPattern("-0.00");
        for (Map<String, Object> item : salesStoSummaries) {
            if(StrUtil.isNotBlank(Convert.toStr(item.get("datePart")))){
                String hm =  cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(item.get("datePart")), "HHmmss"), "HH:mm");
                totalCensus.put("datePart",hm);
            }
            if (totalCensus.containsKey("gssdnormalAmt")) {//????????????
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
            if (totalCensus.containsKey("discountAmt")) {//??????
                BigDecimal discountAmt = new BigDecimal(totalCensus.get("discountAmt").toString()).add(new BigDecimal(item.get("discountAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("discountAmt", discountAmt);
            } else {
                totalCensus.put("discountAmt", item.get("discountAmt").toString());
            }
            if (totalCensus.containsKey("costAmt")) {//????????????
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
            if (totalCensus.containsKey("allCostAmt")) {//????????????
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
            if (totalCensus.containsKey("jfdxZkAmt")) {//??????????????????
                //BigDecimal jfdxZkAmt = new BigDecimal(totalCensus.get("jfdxZkAmt").toString()).add(new BigDecimal(item.get("jfdxZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                if(totalCensus.get("gssdnormalAmt").toString().startsWith("-") && totalCensus.get("allCostAmt").toString().startsWith("-")){
                    if(item.get("jfdxZkAmt").toString().startsWith("-")){
                        totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("jfdxZkAmt").toString())));
                        item.put("jfdxZkAmt",df2.format(item.get("jfdxZkAmt").toString()));
                    }else{
                        totlePrice=totlePrice.add(new BigDecimal(df1.format(df2.format(item.get("jfdxZkAmt").toString()))));
                        item.put("jfdxZkAmt", df2.format(df1.format(df2.format(item.get("jfdxZkAmt").toString()))));
                    }
                }else{
                    totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("jfdxZkAmt").toString())));
                    item.put("jfdxZkAmt",df2.format(item.get("jfdxZkAmt").toString()));
                }
            } else {
                if(item.get("gssdnormalAmt").toString().startsWith("-") && item.get("allCostAmt").toString().startsWith("-")){
                    if(item.get("jfdxZkAmt").toString().startsWith("-")){
                        totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("jfdxZkAmt").toString())));
                        item.put("jfdxZkAmt", df2.format(item.get("jfdxZkAmt").toString()));
                    }else{
                        totlePrice=totlePrice.add(new BigDecimal(df1.format(df2.format(item.get("jfdxZkAmt").toString()))));
                        item.put("jfdxZkAmt", df2.format(df1.format(df2.format(item.get("jfdxZkAmt").toString()))));
                    }
                }else{
                    totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("jfdxZkAmt").toString())));
                    item.put("jfdxZkAmt", df2.format(df1.format(df2.format(item.get("jfdxZkAmt").toString()))));
                }

            }
            totalCensus.put("jfdxZkAmt", totlePrice);

            if(totalCensus.containsKey("dzqZkAmt")) {//???????????????
               // BigDecimal dzqZkAmt = new BigDecimal(totalCensus.get("dzqZkAmt").toString()).add(new BigDecimal(item.get("dzqZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                if(item.get("gssdnormalAmt").toString().startsWith("-") && item.get("allCostAmt").toString().startsWith("-")){
                    if(item.get("dzqZkAmt").toString().startsWith("-")){
                        totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dzqZkAmt").toString())));
                        item.put("dzqZkAmt", df2.format(item.get("dzqZkAmt").toString()));
                    }else{
                        totlePrice=totlePrice.add(new BigDecimal(df1.format(df2.format(item.get("dzqZkAmt").toString()))));
                        item.put("dzqZkAmt", df2.format(df1.format(df2.format(item.get("dzqZkAmt").toString()))));
                    }
                }else{
                    totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dzqZkAmt").toString())));
                    item.put("dzqZkAmt", df2.format(item.get("dzqZkAmt").toString()));
                }

            } else {
                if(item.get("gssdnormalAmt").toString().startsWith("-") && totalCensus.get("allCostAmt").toString().startsWith("-")){
                    if(item.get("dzqZkAmt").toString().startsWith("-")){
                        totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dzqZkAmt").toString())));
                        item.put("dzqZkAmt", df2.format(item.get("dzqZkAmt").toString()));
                    }else{
                        totlePrice=totlePrice.add(new BigDecimal(df1.format(df2.format(item.get("dzqZkAmt").toString()))));
                        item.put("dzqZkAmt", df2.format(df1.format(df2.format(item.get("dzqZkAmt").toString()))));
                    }
                }else{
                    totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dzqZkAmt").toString())));
                    item.put("dzqZkAmt", df2.format(item.get("dzqZkAmt").toString()));
                }
            }
            totalCensus.put("dzqZkAmt", totlePrice);

            if(totalCensus.containsKey("dyqZkAmt")) {//???????????????
                //BigDecimal dyqZkAmt = new BigDecimal(totalCensus.get("dyqZkAmt").toString()).add(new BigDecimal(item.get("dyqZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                if(item.get("gssdnormalAmt").toString().startsWith("-") && totalCensus.get("allCostAmt").toString().startsWith("-")){
                    if(totalCensus.get("dyqZkAmt").toString().startsWith("-")){
                        totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dyqZkAmt").toString())));
                        item.put("dyqZkAmt", df2.format(item.get("dyqZkAmt").toString()));
                    }else{
                        totlePrice=totlePrice.add(new BigDecimal(df1.format(df2.format(item.get("dyqZkAmt").toString()))));
                        item.put("dyqZkAmt", df2.format(df1.format(df2.format(item.get("dyqZkAmt").toString()))));
                    }
                }else{
                    totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dyqZkAmt").toString())));
                    item.put("dyqZkAmt", df2.format(item.get("dyqZkAmt").toString()));
                }
            } else {
                if(item.get("gssdnormalAmt").toString().startsWith("-") && totalCensus.get("allCostAmt").toString().startsWith("-")){
                    if(totalCensus.get("dyqZkAmt").toString().startsWith("-")){
                        totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dyqZkAmt").toString())));
                    }else{
                        totlePrice=totlePrice.add(new BigDecimal(df1.format(df2.format(item.get("dyqZkAmt").toString()))));
                    }
                }else{
                    totlePrice=totlePrice.add(new BigDecimal(df2.format(item.get("dyqZkAmt").toString())));
                    item.put("dyqZkAmt", df2.format(item.get("dyqZkAmt").toString()));
                }
            }
            totalCensus.put("dyqZkAmt", totlePrice);

            if (totalCensus.containsKey("pmZkAmt")) {
                BigDecimal pmZkAmt = new BigDecimal(totalCensus.get("pmZkAmt").toString()).add(new BigDecimal(item.get("pmZkAmt").toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                totalCensus.put("pmZkAmt", pmZkAmt);
            } else {
                totalCensus.put("pmZkAmt", item.get("pmZkAmt").toString());
            }
            for (GetPayTypeOutData outData : payTypeOutData) {
                if (totalCensus.containsKey(outData.getGspmId())) {
                    BigDecimal amt = new BigDecimal(totalCensus.get(outData.getGspmId()).toString()).add(new BigDecimal(item.get(outData.getGspmId()).toString())).setScale(4, BigDecimal.ROUND_HALF_UP);
                    totalCensus.put(outData.getGspmId(), amt);
                } else {
                    totalCensus.put(outData.getGspmId(), item.get(outData.getGspmId()));
                }
            }
        }
        if (salesStoSummaries.size() > 0 && salesStoSummaries != null) {
            String discountRate = "0.00%";
            //????????????
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
//        List<Map<String,Object>> getPayMsgTotal = summaryMapper.getPayMsgTotal(summaryData);
//        if (getPayMsgTotal.size() > 0 && getPayMsgTotal != null){
//            for (Map<String,Object> map : getPayMsgTotal) {
//                totalCensus.put(map.get("gsspmId").toString(),map.get("gsspmAmt"));
//            }
//        }
//        for (GetPayTypeOutData outData : payTypeOutData) {
//            if (!(totalCensus.containsKey(outData.getGspmId()))){
//                totalCensus.put(outData.getGspmId(),0.00);
//            }
//        }
            totalCensus.put("gsshSinglePrice", gsshSinglePrice);
            totalCensus.put("dailyPayAmt", dailyPayAmt);
            totalCensus.put("dailyPayCount", dailyPayCount);
            totalCensus.put("gsshHykCost", gsshHykCost);
            totalCensus.put("dailyProfitAmt", dailyProfitAmt);
            result.put("totalCensus", totalCensus);
            result.put("itemCensus", salesStoSummaries);
        }
        return result;
    }

    @Override
    public PageInfo findSalesSummaryByDate(StoreSaleDateInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("???????????????????????????");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("???????????????????????????");
        }
        if(StrUtil.isNotBlank(inData.getStatDatePart())){
            inData.setStatDatePart(inData.getStatDatePart()+"00");
        }
        if(StrUtil.isNotBlank(inData.getEndDatePart())){
            inData.setEndDatePart(inData.getEndDatePart()+"59");
        }
        inData.setDateType("3");
        if (inData.getDateType().equals("1")) {
            inData.setStartDate(DateUtil.getYearFirst(Integer.parseInt(inData.getStartDate())));
            inData.setEndDate(DateUtil.getYearLast(Integer.parseInt(inData.getEndDate())));
        }

        if (inData.getDateType().equals("2")) {
            inData.setStartDate(DateUtil.getYearMonthFirst(inData.getStartDate()));
            inData.setEndDate(DateUtil.getYearMonthLast(inData.getEndDate()));
        }

        GetPayInData pay = new GetPayInData();
        pay.setClientId(inData.getClient());
        pay.setType("1");
        //??????????????????
        List<GetPayTypeOutData> payTypeOutData = payService.payTypeListByClient(pay);
        if (payTypeOutData != null && payTypeOutData.size() > 0) {
            inData.setPayTypeOutData(payTypeOutData);
        }

        List<Map<String, Object>> outData = summaryMapper.findSalesSummaryByDate(inData);
        Double totalPrice=0.0000;
        DecimalFormat df1=new DecimalFormat();
        DecimalFormat df2=new DecimalFormat("#0.00");
        outData = getOutDate(outData, df1, df2);
        System.err.println("findSalesSummaryByDate:outData="+outData);
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
        BigDecimal totlePrice=BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(outData)) {

            // ????????????????????????
            Map<String, Object> outSto = this.summaryMapper.findSalesSummaryByTotal(inData);
            Long stoCount = (Long) outSto.get("stoCount");

            Map<String, Object> outTotal = new HashMap<>();
            outTotal.put("stoCount", stoCount);
            for (Map<String, Object> out : outData) {

                if(StrUtil.isNotBlank(Convert.toStr(out.get("datePart")))){
                    String hm =  cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(out.get("datePart")), "HHmmss"), "HH:mm");
                    out.put("datePart",hm);
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
                for (GetPayTypeOutData payType : payTypeOutData) {
                    if (outTotal.containsKey(payType.getGspmKey())) {

                        BigDecimal amt = new BigDecimal(outTotal.get(payType.getGspmKey()).toString())
                                .add(new BigDecimal(out.get(payType.getGspmKey()).toString()))
                                .setScale(4, BigDecimal.ROUND_HALF_UP);
                        outTotal.put(payType.getGspmKey(), amt);

                    } else {
                        outTotal.put(payType.getGspmKey(), out.get(payType.getGspmKey()));
                    }
                }
            }

            if (outData != null && outData.size() > 0) {
                String grossProfitRate = "0.00%";
                if (!(new BigDecimal(outTotal.get("amt").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    grossProfitRate = new BigDecimal(outTotal.get("grossProfitMargin").toString()).divide(new BigDecimal(outTotal.get("amt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "";
                }
                outTotal.put("grossProfitRate", grossProfitRate);
                String discountRate = "0.00%";
                if (!(new BigDecimal(outTotal.get("amountReceivable").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    discountRate = (new BigDecimal(outTotal.get("amountReceivable").toString()).subtract(new BigDecimal(outTotal.get("amt").toString()))).divide(new BigDecimal(outTotal.get("amountReceivable").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP) + "";
                }
                outTotal.put("discountRate", discountRate);
                String memberSaleRate = "0.00%";
                if (!(new BigDecimal(outTotal.get("amt").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    memberSaleRate = new BigDecimal(outTotal.get("memberSale").toString()).divide(new BigDecimal(outTotal.get("amt").toString()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")) + "";
                }
                outTotal.put("memberSaleRate", memberSaleRate);

                String perTicketSales = "0.00";
                if (!(new BigDecimal(outTotal.get("amountReceivable").toString()).compareTo(BigDecimal.ZERO) == 0)) {
                    perTicketSales = new BigDecimal(outTotal.get("amt").toString()).divide(new BigDecimal(outTotal.get("numberTrades").toString()), 4, BigDecimal.ROUND_HALF_UP) + "";
                }
                outTotal.put("perTicketSales", perTicketSales);
            }


            System.out.println(outTotal);
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    private List<Map<String, Object>> getOutDate(List<Map<String, Object>> outData, DecimalFormat df1, DecimalFormat df2) {
        List<Map<String, Object>> inDate=outData;
        for (Map<String, Object> item : outData) {
            if (StrUtil.isNotBlank(Convert.toStr(item.get("datePart")))) {
                String hm = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(item.get("datePart")), "HHmmss"), "HH:mm:ss");
                item.put("datePart", hm);
            }
            if(item.get("amt").toString().startsWith("-") && item.get("amountReceivable").toString().startsWith("-")){
                if(!item.get("zkJfdx").toString().startsWith("-") && !df2.format(Double.parseDouble(item.get("zkJfdx").toString())).equals("0.00")){//???????????????
                    item.put("zkJfdx",df1.format(item.get("zkJfdx")));
                }
                if(!item.get("zkDzq").toString().startsWith("-") && !df2.format(Double.parseDouble(item.get("zkDzq").toString())).equals("0.00")){//???????????????
                    item.put("zkDzq",df1.format(item.get("zkDzq")));
                }
                if(!item.get("zkDyq").toString().startsWith("-") && !df2.format(Double.parseDouble(item.get("zkDyq").toString())).equals("0.00")){//??????????????????
                    item.put("zkDyq",df1.format(item.get("zkDyq")));
                }
            }
        }
        return outData;
    }

    /**
     * WEB?????????(??????)
     *
     * @param data ????????????
     * @return ????????????List<Map < String, Object>>
     */
    @Override
    public PageInfo selectWebSalesSummaryByDate(WebStoreSaleDateInData data) {
        //???????????????????????????
        //List<String> sites = selectAuthStoreList(data.getClient(), data.getUserId()).stream().map(site -> site.getStoCode()).collect(Collectors.toList());
        List<StoreOutDatas> storeOutDatas = selectAuthStoreList(data.getClient(), data.getUserId());
        if (CollUtil.isNotEmpty(storeOutDatas)) {
            List<String> sites = storeOutDatas.stream().map(site -> site.getStoCode()).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(sites)) {
                Map<String, Object> mapData = new HashMap<>();
                //????????????
                Map<String, Object> totalSum = new HashMap<>();
                //??????????????????
                List<Map<String, Object>> itemCensusList = new ArrayList<>();
                SalesSummaryDataReport summaryData = new SalesSummaryDataReport();
                BeanUtil.copyProperties(data, summaryData);
                //?????????????????? ???????????? ????????????  ???????????? ???????????? ?????????
                Map<String, Object> salesSummaryByBrId = findSalesSummaryByBrId(summaryData);
                List<Map<String, Object>> unitList = new ArrayList<>();
                //?????????????????????????????????0
                if (MapUtil.isEmpty(salesSummaryByBrId)) {
                    //??????????????????
                    totalSum.put("payDayTime", BigDecimal.ZERO);
                    //??????????????????
                    totalSum.put("gssdAmt", BigDecimal.ZERO);
                    //??????????????????
                    totalSum.put("payCount", BigDecimal.ZERO);
                    //???????????????
                    totalSum.put("gsshSinglePrice", BigDecimal.ZERO);
                    //???????????????
                    totalSum.put("guest", BigDecimal.ZERO);
                    //???????????????
                    totalSum.put("productunitprice", BigDecimal.ZERO);
                    //??????????????????
                    totalSum.put("unitCount", BigDecimal.ZERO);
                    //???????????????
                    totalSum.put("ejectRate", "0.00%");
                    //??????????????????
                    totalSum.put("unionBusinessCount", BigDecimal.ZERO);
                    //???????????????
                    totalSum.put("businessCountRate", "0.00%");
                    //???????????????
                    totalSum.put("unionBusinessAmt", BigDecimal.ZERO);
                    //??????????????????
                    totalSum.put("getUnionBusinessAmtRate", "0.00%");
                    mapData.put("totalCensus", totalSum);
                    mapData.put("itemCensus", itemCensusList);
                    return new PageInfo(itemCensusList, totalSum);
                }
                Map<String, BigDecimal> sumCount = new HashMap<>();
                //????????????
                List<Map<String, Object>> itemCensus = (List<Map<String, Object>>) salesSummaryByBrId.get("itemCensus");
                List<String> brIdList = new ArrayList<>();
                if (CollUtil.isNotEmpty(itemCensus)) {
                    List<Map<String, Object>> itemSite = new ArrayList<>();
                    sites.forEach(site -> itemSite.addAll(itemCensus.stream().filter(item -> site.equals(item.get("brId"))).collect(Collectors.toList())));
                    if (CollUtil.isNotEmpty(itemSite)) {
                        //?????????????????????
                        brIdList = itemSite.stream().map(brId -> Convert.toStr(brId.get("brId"))).collect(Collectors.toList());
                        //???????????????????????????
                        if (Objects.nonNull(data.getPageNum()) && Objects.nonNull(data.getPageSize())) {
                            PageHelper.startPage(data.getPageNum(), data.getPageSize());
                        }
                        unitList = summaryMapper.selectProductUnit(ArrayUtil.isNotEmpty(data.getSiteArr())?brIdList:null, data.getStartDate(), data.getEndDate(), data.getClient());
                        if (CollUtil.isNotEmpty(unitList)) {
                            itemCensusList = unitList.stream().map(unit -> {
                                Map<String, Object> map = new HashMap<>();
                                for (Map<String, Object> item : itemSite) {
                                    if (unit.get("brId").equals(item.get("brId"))) {
                                        map.put("brId", item.get("brId"));
                                        map.put("brName", item.get("brName"));
                                        map.put("payDayTime", item.get("payDayTime"));
                                        BigDecimal amt = new BigDecimal(Convert.toStr(item.get("gssdAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                                        map.put("gssdAmt", amt);
                                        BigDecimal gsshSinglePrice = new BigDecimal(Convert.toStr(item.get("gsshSinglePrice"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                                        map.put("gsshSinglePrice", gsshSinglePrice);
                                        map.put("payCount", item.get("payCount"));
                                        //????????????
                                        int payDayTime = Convert.toInt(item.get("payDayTime"));
                                        if (sumCount.containsKey("payDayTimeSum")) {
                                            sumCount.put("payDayTimeSum", sumCount.get("payDayTimeSum").add(new BigDecimal(payDayTime)));
                                        } else {
                                            sumCount.put("payDayTimeSum", new BigDecimal(payDayTime));
                                        }
                                        //????????????

                                        if (sumCount.containsKey("amtSum")) {
                                            sumCount.put("amtSum", sumCount.get("amtSum").add(amt));
                                        } else {
                                            sumCount.put("amtSum", amt);
                                        }
                                        //?????????
                                        if (sumCount.containsKey("gsshSinglePriceSum")) {
                                            sumCount.put("gsshSinglePriceSum", sumCount.get("gsshSinglePriceSum").add(gsshSinglePrice));
                                        } else {
                                            sumCount.put("gsshSinglePriceSum", gsshSinglePrice);
                                        }
                                        //????????????
                                        BigDecimal payCount = new BigDecimal(Convert.toStr(item.get("payCount")));
                                        if (sumCount.containsKey("payCountSum")) {
                                            sumCount.put("payCountSum", sumCount.get("payCountSum").add(payCount));
                                        } else {
                                            sumCount.put("payCountSum", payCount);
                                        }

                                        BigDecimal guest = new BigDecimal(Convert.toStr(unit.get("guest"))).setScale(2, RoundingMode.HALF_UP);
                                        map.put("guest", guest);
                                        BigDecimal productunitprice = new BigDecimal(Convert.toStr(unit.get("productunitprice"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                                        map.put("productunitprice", productunitprice);
                                        //???????????????
                                        String xspx = Convert.toStr(unit.get("xspx"));
                                        if (sumCount.containsKey("xspxSum")) {
                                            sumCount.put("xspxSum", sumCount.get("xspxSum").add(new BigDecimal(xspx)));
                                        } else {
                                            sumCount.put("xspxSum", new BigDecimal(xspx));
                                        }
                                        String jycs = Convert.toStr(unit.get("jycs"));
                                        if (sumCount.containsKey("jycsSum")) {
                                            sumCount.put("jycsSum", sumCount.get("jycsSum").add(new BigDecimal(jycs)));
                                        } else {
                                            sumCount.put("jycsSum", new BigDecimal(jycs));
                                        }
                                        String gssdAmts = Convert.toStr(unit.get("gssdAmt"));
                                        if (sumCount.containsKey("gssdAmtSum")) {
                                            sumCount.put("gssdAmtSum", sumCount.get("gssdAmtSum").add(new BigDecimal(Convert.toStr(gssdAmts))));
                                        } else {
                                            sumCount.put("gssdAmtSum", new BigDecimal(Convert.toStr(gssdAmts)));
                                        }
                                        String gssdQty = Convert.toStr(unit.get("gssdQty"));
                                        if (sumCount.containsKey("gssdQtySum")) {
                                            sumCount.put("gssdQtySum", sumCount.get("gssdQtySum").add(new BigDecimal(gssdQty)));
                                        } else {
                                            sumCount.put("gssdQtySum", new BigDecimal(gssdQty));
                                        }
                                    /*if (sumCount.containsKey("gsshSinglePriceSum")) {
                                        sumCount.put("gsshSinglePriceSum", sumCount.get("gsshSinglePriceSum").add(new BigDecimal(Convert.toStr(map.get("gsshSinglePrice")))));
                                    } else {
                                        sumCount.put("gsshSinglePriceSum", new BigDecimal(Convert.toStr(map.get("gsshSinglePrice"))));
                                    }*/
                                    }
                                }
                                return map;
                            }).collect(Collectors.toList());
                        }
                        if (CollUtil.isNotEmpty(itemCensusList)) {
                            itemCensusList.forEach(cen -> {
                                cen.put("unitCount", BigDecimal.ZERO);
                                cen.put("ejectRate", "0.00%");
                                cen.put("unionBusinessCount", BigDecimal.ZERO);
                                cen.put("unionBusinessAmt", BigDecimal.ZERO);
                                cen.put("getUnionBusinessAmtRate", "0.00%");
                                cen.put("businessCountRate", BigDecimal.valueOf(0).setScale(2));
                            });
                            sumCount.put("unionBusinessCountSum", BigDecimal.ZERO);
                            sumCount.put("unionBusinessAmtSum", BigDecimal.ZERO);
                            sumCount.put("unitCountSum", BigDecimal.ZERO);
                            sumCount.put("inputCountSum", BigDecimal.ZERO);
                        }
                        //??????????????????????????????
                        List<Map<String, Object>> popUpDataList = summaryMapper.selectPopUpData(ArrayUtil.isNotEmpty(data.getSiteArr())?brIdList:null, data.getStartDate(), data.getEndDate(), data.getClient());
                        if (CollUtil.isNotEmpty(popUpDataList)) {
                            for (Map<String, Object> cen : itemCensusList) {
                                for (Map<String, Object> po : popUpDataList) {
                                    if (Objects.equals(po.get("brId"), cen.get("brId"))) {
                                        //??????????????????
                                        int unitCount = Convert.toInt(po.get("unitCount"));
                                        cen.put("unitCount", unitCount);
                                        //??????????????????  po.get("inputCount")
                                        int inputCount = Convert.toInt(po.get("inputCount"));
                                        //?????????
                                        cen.put("ejectRate", inputCount != 0 ? Convert.toStr(new BigDecimal(unitCount).divide(BigDecimal.valueOf(inputCount), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).concat("%") : "0.00%");
                                        //????????????????????????
                                        if (sumCount.containsKey("unitCountSum")) {
                                            sumCount.put("unitCountSum", sumCount.get("unitCountSum").add(BigDecimal.valueOf(unitCount)));
                                        } else {
                                            sumCount.put("unitCountSum", new BigDecimal(unitCount));
                                        }
                                        //??????????????????
                                        if (sumCount.containsKey("inputCountSum")) {
                                            sumCount.put("inputCountSum", sumCount.get("inputCountSum").add(BigDecimal.valueOf(inputCount)));
                                        } else {
                                            sumCount.put("inputCountSum", new BigDecimal(inputCount));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //?????????????????????????????????????????? ??????????????? ??????????????????
                List<Map<String, Object>> popUpBrDataList = summaryMapper.selectPopUpBrData(ArrayUtil.isNotEmpty(data.getSiteArr())?brIdList:null, data.getStartDate(), data.getEndDate(), data.getClient());
                if (CollUtil.isNotEmpty(popUpBrDataList)) {
                    for (Map<String, Object> upBr : popUpBrDataList) {
                        for (Map<String, Object> cen : itemCensusList) {
                            if (Objects.equals(upBr.get("brId"), cen.get("brId"))) {
                                //??????????????????
                                int unionBusinessCount = Convert.toInt(upBr.get("unionBusinessCount"));
                                cen.put("unionBusinessCount", unionBusinessCount);
                                //???????????????
                                BigDecimal unionBusinessAmt = new BigDecimal(Convert.toStr(upBr.get("unionBusinessAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                                cen.put("unionBusinessAmt", unionBusinessAmt);
                                //?????????
                                //cen.put("businessCountRate", !(Convert.toStr(cen.get("payCount")).equals("0")) ? Convert.toStr(new BigDecimal(unionBusinessCount).divide(new BigDecimal(Convert.toStr(cen.get("payCount"))), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))).concat("%") : "0.00%");
                                //?????????????????????
                                cen.put("getUnionBusinessAmtRate", unionBusinessAmt != null && !(0 == (Convert.toDouble(cen.get("gssdAmt")))) ? Convert.toStr(unionBusinessAmt.divide(new BigDecimal(Convert.toStr(cen.get("gssdAmt"))), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).concat("%") : "0.00%");
                                //????????????????????????
                                if (sumCount.containsKey("unionBusinessCountSum")) {
                                    sumCount.put("unionBusinessCountSum", sumCount.get("unionBusinessCountSum").add(new BigDecimal(unionBusinessCount)));
                                } else {
                                    sumCount.put("unionBusinessCountSum", BigDecimal.valueOf(unionBusinessCount));
                                }
                                //?????????????????????
                                if (sumCount.containsKey("unionBusinessAmtSum")) {
                                    sumCount.put("unionBusinessAmtSum", sumCount.get("unionBusinessAmtSum").add(unionBusinessAmt));
                                } else {
                                    sumCount.put("unionBusinessAmtSum", unionBusinessAmt);
                                }
                            }
                        }
                    }
                }
                //???????????????????????????
                List<Map<String, Object>> closing = summaryMapper.selectClosingData(ArrayUtil.isNotEmpty(data.getSiteArr())?brIdList:null, data.getStartDate(), data.getEndDate(), data.getClient());
                if (CollUtil.isNotEmpty(closing)) {
                    //??????????????????
                    if (CollUtil.isNotEmpty(itemCensusList)) {
                        for (Map<String, Object> code : closing) {
                            for (Map<String, Object> item : itemCensusList) {
                                if (Objects.equals(item.get("brId"), code.get("siteCode"))) {
                                    int unionBusinessCount = Convert.toInt(item.get("unionBusinessCount"));
                                    String unionCount = Convert.toStr(code.get("unionBussinessCount"));
                                    item.put("businessCountRate", !(Convert.toDouble(unionCount) == 0) ? new BigDecimal(unionBusinessCount).divide(new BigDecimal(Convert.toStr(unionCount)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) : BigDecimal.valueOf(0).setScale(2));
                                    //?????????????????????
                                    if (sumCount.containsKey("unionCountSum")) {
                                        sumCount.put("unionCountSum", sumCount.get("unionCountSum").add(new BigDecimal(unionCount)));
                                    } else {
                                        sumCount.put("unionCountSum", new BigDecimal(unionCount));
                                    }
                                }
                            }
                        }
                    }
                }
                if (MapUtil.isNotEmpty(sumCount)) {

                    totalSum.put("payDayTime", sumCount.get("payDayTimeSum"));
                    totalSum.put("gssdAmt", sumCount.get("amtSum"));
                    totalSum.put("payCount", sumCount.get("payCountSum"));
                    totalSum.put("gsshSinglePrice", sumCount.get("payCountSum") != null && Convert.toLong(sumCount.get("payCountSum")) != 0 ? sumCount.get("amtSum").divide(sumCount.get("payCountSum"), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP) : "0.00");
                    //?????????
                    BigDecimal xspxSum = sumCount.get("xspxSum");
                    BigDecimal jycsSum = sumCount.get("jycsSum");
                    totalSum.put("guest", Convert.toStr(sumCount.get("xspxSum").divide(sumCount.get("jycsSum"), 2, RoundingMode.HALF_UP)));
                    //????????? guest productunitprice
                    totalSum.put("productunitprice", Convert.toStr(sumCount.get("gssdAmtSum").divide(sumCount.get("gssdQtySum"), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP)));
                    //??????????????????
                    totalSum.put("unitCount", sumCount.get("unitCountSum"));
                    //???????????????
                    totalSum.put("ejectRate", sumCount.get("unitCountSum") != null && !(0 == Convert.toDouble(sumCount.get("inputCountSum"))) ? Convert.toStr(sumCount.get("unitCountSum").divide(sumCount.get("inputCountSum"), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).concat("%") : "0.00%");
                    //???????????????
                    totalSum.put("businessCountRate", sumCount.get("unionCountSum") != null && !(Convert.toDouble(sumCount.get("unionCountSum")) == 0) ? Convert.toStr(sumCount.get("unionBusinessCountSum").divide(sumCount.get("unionCountSum"), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).concat("%") : "0.00%");
                    //??????????????????
                    totalSum.put("getUnionBusinessAmtRate", sumCount.get("unionBusinessAmtSum") != null && !(0 == (Convert.toDouble(sumCount.get("amtSum")))) ? Convert.toStr(sumCount.get("unionBusinessAmtSum").divide(new BigDecimal(Convert.toStr(sumCount.get("amtSum"))), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)).concat("%") : "0.00%");
                    //??????????????????
                    totalSum.put("unionBusinessCount", sumCount.get("unionBusinessCountSum"));
                    //???????????????
                    totalSum.put("unionBusinessAmt", sumCount.get("unionBusinessAmtSum"));

                } else {
                    totalSum.put("payDayTime", BigDecimal.ZERO);
                    totalSum.put("gssdAmt", BigDecimal.ZERO);
                    totalSum.put("payCount", BigDecimal.ZERO);
                    totalSum.put("gsshSinglePrice", BigDecimal.ZERO);
                    totalSum.put("guest", BigDecimal.ZERO);
                    totalSum.put("productunitprice", BigDecimal.ZERO);
                    //??????????????????
                    totalSum.put("unitCount", BigDecimal.ZERO);
                    //???????????????
                    totalSum.put("ejectRate", "0.00%");
                    //???????????????
                    totalSum.put("unionBusinessCount", BigDecimal.ZERO);
                    //??????????????????
                    totalSum.put("getUnionBusinessAmtRate", "0.00%");
                    totalSum.put("businessCountRate", "0.00%");
                    totalSum.put("unionBusinessAmt", BigDecimal.ZERO);
                }

                PageInfo pageInfo = new PageInfo();
                mapData.put("totalCensus", totalSum);
                mapData.put("itemCensus", itemCensusList);
                pageInfo = new PageInfo(itemCensusList, totalSum);
                PageInfo pa = new PageInfo<>(unitList);
                pageInfo.setTotal(pa.getTotal());
                pageInfo.setPages(pa.getPages());
                pageInfo.setPageNum(pa.getPageNum());
                pageInfo.setPageSize(pa.getPageSize());
                return pageInfo;
            }
        }
        return new PageInfo();
    }

    public List<StoreOutDatas> selectAuthStoreList(String client, String userId) {

        List<StoreOutDatas> storeList = new ArrayList();
        List<String> storeCodeList = this.summaryMapper.selectAuthStoreList(client, userId);
        if (CollUtil.isEmpty(storeCodeList)) {
            return null;
        } else {
            Example example = new Example(GaiaStoreData.class);
            example.createCriteria().andEqualTo("client", client).andEqualTo("stoStatus", "0");
            List<GaiaStoreData> storeDataList = this.storeDao.selectByExample(example);
            List<StoreOutDatas> allStoreList = new ArrayList<>();
            if (ObjectUtil.isEmpty(storeDataList)) {
                return null;
            } else {
                for (GaiaStoreData storeData : storeDataList) {
                    StoreOutDatas store = new StoreOutDatas();
                    store.setStoCode(storeData.getStoCode());
                    store.setStoName(storeData.getStoName());
                    store.setStoAttribute(storeData.getStoAttribute());
                    store.setStoShortName(storeData.getStoShortName());
                    allStoreList.add(store);
                }

                storeList.clear();
                if (storeCodeList.contains("GAD")) {
                    storeList.addAll(allStoreList);
                } else {
                    for (String storeCode : storeCodeList) {
                        for (GaiaStoreData storeData : storeDataList) {
                            if (storeCode.equals(storeData.getStoCode())) {
                                StoreOutDatas store = new StoreOutDatas();
                                store.setStoCode(storeData.getStoCode());
                                store.setStoName(storeData.getStoName());
                                store.setStoAttribute(storeData.getStoAttribute());
                                store.setStoShortName(storeData.getStoShortName());
                                storeList.add(store);
                            }
                        }

                    }
                }
            }

        }
        return storeList;
    }

    /**
     * web???????????????
     *
     * @param data
     * @return
     */
    @Override
    public Result exportSalesSummary(WebStoreSaleDateInData data) {
        data.setPageNum(null);
        data.setPageSize(null);
        //?????????????????? ???????????? ????????????
        PageInfo result = selectWebSalesSummaryByDate(data);
        if (Objects.isNull(result) && CollUtil.isEmpty(result.getList())) {
            throw new BusinessException("???????????????????????????");
        }
        //????????????
        List<Map<String, Object>> productSpecialParamList = (List<Map<String, Object>>) result.getList();
        if (ValidateUtil.isEmpty(productSpecialParamList)) {
            throw new BusinessException("???????????????????????????");
        }
        List<List<Object>> dataList = new ArrayList<>(productSpecialParamList.size());
        Integer count = 1;
        List<Object> lineList = new ArrayList<>();
        for (Map<String, Object> param : productSpecialParamList) {
            //????????????
            lineList = new ArrayList<>();
            //??????
            lineList.add(count);
            count += 1;
            //????????????
            lineList.add(Convert.toStr(param.get("brId")));
            //????????????
            lineList.add(Convert.toStr(param.get("brName")));
            //????????????
            lineList.add(Convert.toStr(param.get("payDayTime")));
            //????????????
            lineList.add(Convert.toStr(new BigDecimal(Convert.toStr(param.get("gssdAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
            //????????????
            lineList.add(Convert.toStr(param.get("payCount")));
            //?????????
            lineList.add(Convert.toStr(new BigDecimal(Convert.toStr(param.get("gsshSinglePrice"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
            //?????????guest
            lineList.add(Convert.toStr(new BigDecimal(Convert.toStr(param.get("guest"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
            //?????????
            lineList.add(Convert.toStr(new BigDecimal(Convert.toStr(param.get("productunitprice"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
            //??????????????????
            lineList.add(Convert.toStr(param.get("unitCount")));
            //?????????
            lineList.add(Convert.toStr(param.get("ejectRate")));
            //??????????????????
            lineList.add(Convert.toStr(param.get("unionBusinessCount")));
            //?????????
            lineList.add(Convert.toStr(param.get("businessCountRate")).concat("%"));
            //???????????????
            lineList.add(Convert.toStr(new BigDecimal(Convert.toStr(param.get("unionBusinessAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
            //??????????????????
            lineList.add(Convert.toStr(param.get("getUnionBusinessAmtRate")));
            dataList.add(lineList);
        }
        List<Object> total = new ArrayList<>();
        Map<String, Object> totalSum = (Map<String, Object>) result.getListNum();
        total.add("??????");
        //????????????
        total.add(null);
        //????????????
        total.add(null);
        //????????????
        total.add(Convert.toStr(totalSum.get("payDayTime")));
        //????????????
        total.add(Convert.toStr(new BigDecimal(Convert.toStr(totalSum.get("gssdAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
        //????????????
        total.add(Convert.toStr(totalSum.get("payCount")));
        //?????????
        total.add(Convert.toStr(new BigDecimal(Convert.toStr(totalSum.get("gsshSinglePrice"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
        //?????????guest
        total.add(Convert.toStr(new BigDecimal(Convert.toStr(totalSum.get("guest"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
        //?????????
        total.add(Convert.toStr(new BigDecimal(Convert.toStr(totalSum.get("productunitprice"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
        //??????????????????
        total.add(Convert.toStr(totalSum.get("unitCount")));
        //?????????
        total.add(Convert.toStr(totalSum.get("ejectRate")));
        //??????????????????
        total.add(Convert.toStr(totalSum.get("unionBusinessCount")));
        //?????????
        total.add(Convert.toStr(totalSum.get("businessCountRate")));
        //???????????????
        total.add(Convert.toStr(new BigDecimal(Convert.toStr(totalSum.get("unionBusinessAmt"))).setScale(2, BigDecimal.ROUND_HALF_UP)));
        //??????????????????
        total.add(Convert.toStr(totalSum.get("getUnionBusinessAmtRate")));
        dataList.addAll(Arrays.asList(total));

        //????????????
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HSSFWorkbook workbook = ExcelUtils.exportExcel2(
                new ArrayList<String[]>() {{
                    add(CommonConstant.COMBINED_MEDICATION_QUERY_REPORT_USERS);
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(dataList);
                }},
                new ArrayList<String>() {{
                    add(CommonConstant.COMBINED_MEDICATION_QUERY_REPORT_NAME);
                }});

        Result uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = CommonConstant.COMBINED_MEDICATION_QUERY_REPORT_NAME + "-" + CommonUtil.getyyyyMMdd() + ".xls";
            uploadResult = cosUtils.uploadFile(bos, fileName);
            bos.flush();
        } catch (IOException e) {
            log.error("??????????????????:{}", e.getMessage(), e);
            throw new BusinessException("?????????????????????");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("???????????????:{}", e.getMessage(), e);
                throw new BusinessException("??????????????????");
            }
        }
        return uploadResult;
    }
}

package com.gys.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.gys.common.data.PageInfo;
import com.gys.entity.SupplierSaleMan;
import com.gys.mapper.GaiaSdSaleDMapper;
import com.gys.mapper.SupplierSummaryMapper;
import com.gys.report.entity.*;
import com.gys.service.SupplierSummaryService;
import com.gys.util.CommonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierSummaryServiceImpl implements SupplierSummaryService {

    @Autowired
    SupplierSummaryMapper supplierSummaryMapper;

    @Autowired
    GaiaSdSaleDMapper gaiaSdSaleDMapper;


    @Override
    public List<SupplierSaleMan> getSaleManList(String client ,String supplierCode) {
        return supplierSummaryMapper.querySaleManList(client, supplierCode);
    }

    @Override
    public PageInfo getSupplierSummaryList(SupplierSummaryInData inData) {
        List<ProductSalesBySupplierWithSaleManOutData> productSupplierData = new ArrayList<>();
        PageInfo pageInfo;
        Set<String> stoGssgTypeSet = new HashSet<>();
        String stoGssgType = inData.getStoGssgType();
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
            inData.setStoGssgTypes(stoGssgTypes);
        }
        String stoAttribute = inData.getStoAttribute();
        if (stoAttribute != null) {
            inData.setStoAttributes(Arrays.asList(stoAttribute.split(StrUtil.COMMA)));
        }
        String stoIfMedical = inData.getStoIfMedical();
        if (stoIfMedical != null) {
            inData.setStoIfMedicals(Arrays.asList(stoIfMedical.split(StrUtil.COMMA)));
        }
        String stoTaxClass = inData.getStoTaxClass();
        if (stoTaxClass != null) {
            inData.setStoTaxClasss(Arrays.asList(stoTaxClass.split(StrUtil.COMMA)));
        }
        String stoIfDtp = inData.getStoIfDtp();
        if (stoIfDtp != null) {
            inData.setStoIfDtps(Arrays.asList(stoIfDtp.split(StrUtil.COMMA)));
        }
        List<SalespersonsSalesDetailsOutData> saleList = gaiaSdSaleDMapper.getProductSalesBySupplierWithBatch(inData);                 // ??????????????????????????????
        List<ProductSalesBySupplierWithSaleManOutData>  salespersonsSalesDetailsOutData = new ArrayList<ProductSalesBySupplierWithSaleManOutData>();
        if (saleList.size() > 0) {
            // ?????????????????????
            productSupplierData = supplierSummaryMapper.getProductSalesBySupplier(inData);              // ?????????????????????
            if (productSupplierData.size() > 0) {
                //?????????????????????????????? ????????????????????????????????????
                salespersonsSalesDetailsOutData = summaryWithSupplier(saleList, productSupplierData,inData);
            } else {
                return new PageInfo();
            }
        } else {
            return new PageInfo();
        }
        ProductSalesBySupplierOutTotal supplierOutTotal = new ProductSalesBySupplierOutTotal();
        // ????????????????????????
        for (ProductSalesBySupplierWithSaleManOutData supplierOutData : salespersonsSalesDetailsOutData) {
            supplierOutTotal.setAmountReceivable(CommonUtil.stripTrailingZeros(supplierOutData.getAmountReceivable()).add(CommonUtil.stripTrailingZeros(supplierOutTotal.getAmountReceivable())));
            supplierOutTotal.setAmt(CommonUtil.stripTrailingZeros(supplierOutData.getAmt()).add(CommonUtil.stripTrailingZeros(supplierOutTotal.getAmt())));
            supplierOutTotal.setIncludeTaxSale(CommonUtil.stripTrailingZeros(supplierOutData.getIncludeTaxSale()).add(CommonUtil.stripTrailingZeros(supplierOutTotal.getIncludeTaxSale())));
            supplierOutTotal.setGrossProfitMargin(CommonUtil.stripTrailingZeros(supplierOutData.getGrossProfitMargin()).add(CommonUtil.stripTrailingZeros(supplierOutTotal.getGrossProfitMargin())));
            supplierOutTotal.setQty(CommonUtil.stripTrailingZeros(supplierOutData.getQty()).add(CommonUtil.stripTrailingZeros(supplierOutTotal.getQty())));

        }
        if (supplierOutTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
            DecimalFormat df = new DecimalFormat("0.00%");
            supplierOutTotal.setGrossProfitRate(df.format(CommonUtil.stripTrailingZeros(supplierOutTotal.getGrossProfitMargin()).divide(CommonUtil.stripTrailingZeros(supplierOutTotal.getAmt()), BigDecimal.ROUND_HALF_EVEN)));
        }

        pageInfo = new PageInfo(salespersonsSalesDetailsOutData, supplierOutTotal);
        return pageInfo;
    }


    private List<ProductSalesBySupplierWithSaleManOutData> summaryWithSupplier(List<SalespersonsSalesDetailsOutData> saleList,List<ProductSalesBySupplierWithSaleManOutData> productSupplierInData, SupplierSummaryInData inData){
        List<ProductSalesBySupplierWithSaleManOutData> productSupplierDataList = new ArrayList<ProductSalesBySupplierWithSaleManOutData>();
        ArrayList<ProductSalesBySupplierWithSaleManOutData> productSalesBySupplierWithSaleManOutData = new ArrayList<>();
        for (SalespersonsSalesDetailsOutData saleSum : saleList) {
            //?????????????????? ????????????????????????????????????
            BigDecimal sumQty = CommonUtil.stripTrailingZeros(saleSum.getQty());
            for (ProductSalesBySupplierWithSaleManOutData supplierOutData : productSupplierInData) {
                //???????????????????????????,???????????????null?????????0
                supplierOutData.setAmountReceivable(ObjectUtils.isEmpty(supplierOutData.getAmountReceivable()) ? BigDecimal.ZERO : supplierOutData.getAmountReceivable());
                supplierOutData.setAmt(ObjectUtils.isEmpty(supplierOutData.getAmt()) ? BigDecimal.ZERO : supplierOutData.getAmt());
                supplierOutData.setIncludeTaxSale(ObjectUtils.isEmpty(supplierOutData.getIncludeTaxSale()) ? BigDecimal.ZERO : supplierOutData.getIncludeTaxSale());
                supplierOutData.setGrossProfitMargin(ObjectUtils.isEmpty(supplierOutData.getGrossProfitMargin()) ? BigDecimal.ZERO : supplierOutData.getGrossProfitMargin());
                if (saleSum.getSelfCode().equals(supplierOutData.getProCode()) && saleSum.getStoCode().equals(supplierOutData.getStoCode()) && saleSum.getBatch().equals(supplierOutData.getBatch())) {
                    //?????????????????? ????????????????????????????????????
                    // ??????==?????????
                    BigDecimal supQty = CommonUtil.stripTrailingZeros(supplierOutData.getQty());
                    //??????????????????????????????????????????????????????
                    BigDecimal rateQty = BigDecimal.ONE;
                    if (sumQty.compareTo(BigDecimal.ZERO) != 0) {
                        rateQty = supQty.divide(sumQty, 10, BigDecimal.ROUND_HALF_EVEN);
                    }
                    supplierOutData.setAmountReceivable(CommonUtil.stripTrailingZeros(saleSum.getAmountReceivable()).multiply(rateQty));//????????????
                    supplierOutData.setAmt(CommonUtil.stripTrailingZeros(saleSum.getAmt()).multiply(rateQty));//????????????
                    supplierOutData.setIncludeTaxSale(CommonUtil.stripTrailingZeros(saleSum.getIncludeTaxSale()).multiply(rateQty));//?????????
                    supplierOutData.setGrossProfitMargin(CommonUtil.stripTrailingZeros(saleSum.getGrossProfitMargin()).multiply(rateQty));//?????????
                    //?????????????????????   ?????????????????????0  NND
                    if (supplierOutData.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                        supplierOutData.setGrossProfitRate(supplierOutData.getGrossProfitMargin().
                                multiply(new BigDecimal(100)).
                                divide(supplierOutData.getAmt(), 2, BigDecimal.ROUND_HALF_EVEN)
                        );//?????????=?????????/????????????
                    } else {
                        supplierOutData.setGrossProfitRate(BigDecimal.ZERO);
                    }
                    // ????????????????????????????????????
                    productSupplierDataList.add(supplierOutData);
                }
            }
        }
        Map<String, List<ProductSalesBySupplierWithSaleManOutData>> collect = new HashMap<String, List<ProductSalesBySupplierWithSaleManOutData>>();
        // ????????????????????????
        if (inData.getWithSaleMan().equals("0") && inData.getWithSto().equals("0") && inData.getWithPro().equals("0")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode()));
        }
        else if (inData.getWithSaleMan().equals("1") && inData.getWithSto().equals("0") && inData.getWithPro().equals("0")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getGssCode()));
        }
        else if (inData.getWithSaleMan().equals("0") && inData.getWithSto().equals("1") && inData.getWithPro().equals("0")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getStoCode()));
        }
        else if (inData.getWithSaleMan().equals("0") && inData.getWithSto().equals("0") && inData.getWithPro().equals("1")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getProCode()));
        }
        else if (inData.getWithSaleMan().equals("1") && inData.getWithSto().equals("1") && inData.getWithPro().equals("0")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getGssCode() + '_' + item.getStoCode()));
        }
        else if (inData.getWithSaleMan().equals("1") && inData.getWithSto().equals("0") && inData.getWithPro().equals("1")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getGssCode() + '_' + item.getProCode()));
        }
        else if (inData.getWithSaleMan().equals("0") && inData.getWithSto().equals("1") && inData.getWithPro().equals("1")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getStoCode() + '_' + item.getProCode()));
        }
        else if (inData.getWithSaleMan().equals("1") && inData.getWithSto().equals("1") && inData.getWithPro().equals("1")){
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode() + '_' + item.getGssCode() + '_' + item.getStoCode() + '_' + item.getProCode()));
        }
        else {
            // ???????????????????????????
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode()));
        }
        // ??????????????????
        for (String s : collect.keySet()) {
            ProductSalesBySupplierWithSaleManOutData productSalesBySupplierWithSaleManOutData1 = new ProductSalesBySupplierWithSaleManOutData();
            // ???????????????0
            productSalesBySupplierWithSaleManOutData1.setQty(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setAmountReceivable(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setAmt(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setIncludeTaxSale(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setGrossProfitMargin(BigDecimal.ZERO);
            // ????????????
            List<ProductSalesBySupplierWithSaleManOutData> productSalesBySupplierWithSaleManOutData2 = collect.get(s);
            ProductSalesBySupplierWithSaleManOutData firstData = productSalesBySupplierWithSaleManOutData2.get(0);
            productSalesBySupplierWithSaleManOutData1.setSupplierCode(firstData.getSupplierCode());  // ?????????????????????
            productSalesBySupplierWithSaleManOutData1.setSupplierName(firstData.getSupplierName());  // ?????????????????????
            // ????????????
            productSalesBySupplierWithSaleManOutData1.setGssCode(firstData.getGssCode());  // ?????????????????????
            productSalesBySupplierWithSaleManOutData1.setGssName(firstData.getGssName());  // ?????????????????????
            // ?????????
            productSalesBySupplierWithSaleManOutData1.setStoCode(firstData.getStoCode());  // ??????????????????
            productSalesBySupplierWithSaleManOutData1.setStoName(firstData.getStoName());  // ??????????????????
            // ?????????
            productSalesBySupplierWithSaleManOutData1.setProCode(firstData.getProCode());  // ??????????????????
            productSalesBySupplierWithSaleManOutData1.setProCommonName(firstData.getProCommonName());  // ??????????????????
            productSalesBySupplierWithSaleManOutData1.setProName(firstData.getProName());  // ??????????????????
            productSalesBySupplierWithSaleManOutData1.setExpiryDate(firstData.getExpiryDate());  // ??????????????????
            productSalesBySupplierWithSaleManOutData1.setExpiryDay(firstData.getExpiryDay());  // ??????????????????
            productSalesBySupplierWithSaleManOutData1.setSpecs(firstData.getSpecs());  // ????????????
            productSalesBySupplierWithSaleManOutData1.setUnit(firstData.getUnit());  // ????????????
            productSalesBySupplierWithSaleManOutData1.setFactoryName(firstData.getFactoryName());  // ??????????????????
            for (ProductSalesBySupplierWithSaleManOutData salesBySupplierWithSaleManOutData : productSalesBySupplierWithSaleManOutData2) {
                productSalesBySupplierWithSaleManOutData1.setQty(productSalesBySupplierWithSaleManOutData1.getQty().add(salesBySupplierWithSaleManOutData.getQty()));   // ??????
                productSalesBySupplierWithSaleManOutData1.setAmountReceivable(productSalesBySupplierWithSaleManOutData1.getAmountReceivable().add(salesBySupplierWithSaleManOutData.getAmountReceivable()));    // ????????????
                productSalesBySupplierWithSaleManOutData1.setAmt(productSalesBySupplierWithSaleManOutData1.getAmt().add(salesBySupplierWithSaleManOutData.getAmt()));    // ????????????
                productSalesBySupplierWithSaleManOutData1.setIncludeTaxSale(productSalesBySupplierWithSaleManOutData1.getIncludeTaxSale().add(salesBySupplierWithSaleManOutData.getIncludeTaxSale()));    // ?????????
                productSalesBySupplierWithSaleManOutData1.setGrossProfitMargin(productSalesBySupplierWithSaleManOutData1.getGrossProfitMargin().add(salesBySupplierWithSaleManOutData.getGrossProfitMargin()));  //?????????
            }
            //?????????????????????   ?????????????????????0  NND
            if (productSalesBySupplierWithSaleManOutData1.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                productSalesBySupplierWithSaleManOutData1.setGrossProfitRate(productSalesBySupplierWithSaleManOutData1.getGrossProfitMargin().
                        multiply(new BigDecimal(100)).
                        divide(productSalesBySupplierWithSaleManOutData1.getAmt(), 2, BigDecimal.ROUND_HALF_EVEN)
                );//?????????=?????????/????????????
            } else {
                productSalesBySupplierWithSaleManOutData1.setGrossProfitRate(BigDecimal.ZERO);
            }
            productSalesBySupplierWithSaleManOutData.add(productSalesBySupplierWithSaleManOutData1);
        }
        return productSalesBySupplierWithSaleManOutData;
    }
}

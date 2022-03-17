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
        List<SalespersonsSalesDetailsOutData> saleList = gaiaSdSaleDMapper.getProductSalesBySupplierWithBatch(inData);                 // 门店商品销售数据汇总
        List<ProductSalesBySupplierWithSaleManOutData>  salespersonsSalesDetailsOutData = new ArrayList<ProductSalesBySupplierWithSaleManOutData>();
        if (saleList.size() > 0) {
            // 根据业务员查询
            productSupplierData = supplierSummaryMapper.getProductSalesBySupplier(inData);              // 根据业务员查询
            if (productSupplierData.size() > 0) {
                //根据供应商业务员汇总 获取该商品所有的销售信息
                salespersonsSalesDetailsOutData = summaryWithSupplier(saleList, productSupplierData,inData);
            } else {
                return new PageInfo();
            }
        } else {
            return new PageInfo();
        }
        ProductSalesBySupplierOutTotal supplierOutTotal = new ProductSalesBySupplierOutTotal();
        // 集合列的数据汇总
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
            //根据商品汇总 获取该商品所有的销售数量
            BigDecimal sumQty = CommonUtil.stripTrailingZeros(saleSum.getQty());
            for (ProductSalesBySupplierWithSaleManOutData supplierOutData : productSupplierInData) {
                //用百分比乘各个数据,处理数据将null替换为0
                supplierOutData.setAmountReceivable(ObjectUtils.isEmpty(supplierOutData.getAmountReceivable()) ? BigDecimal.ZERO : supplierOutData.getAmountReceivable());
                supplierOutData.setAmt(ObjectUtils.isEmpty(supplierOutData.getAmt()) ? BigDecimal.ZERO : supplierOutData.getAmt());
                supplierOutData.setIncludeTaxSale(ObjectUtils.isEmpty(supplierOutData.getIncludeTaxSale()) ? BigDecimal.ZERO : supplierOutData.getIncludeTaxSale());
                supplierOutData.setGrossProfitMargin(ObjectUtils.isEmpty(supplierOutData.getGrossProfitMargin()) ? BigDecimal.ZERO : supplierOutData.getGrossProfitMargin());
                if (saleSum.getSelfCode().equals(supplierOutData.getProCode()) && saleSum.getStoCode().equals(supplierOutData.getStoCode()) && saleSum.getBatch().equals(supplierOutData.getBatch())) {
                    //根据批次汇总 获取该商品所有的异动数量
                    // 批次==供应商
                    BigDecimal supQty = CommonUtil.stripTrailingZeros(supplierOutData.getQty());
                    //算出该加盟商销售数量占总销量的百分比
                    BigDecimal rateQty = BigDecimal.ONE;
                    if (sumQty.compareTo(BigDecimal.ZERO) != 0) {
                        rateQty = supQty.divide(sumQty, 10, BigDecimal.ROUND_HALF_EVEN);
                    }
                    supplierOutData.setAmountReceivable(CommonUtil.stripTrailingZeros(saleSum.getAmountReceivable()).multiply(rateQty));//应收金额
                    supplierOutData.setAmt(CommonUtil.stripTrailingZeros(saleSum.getAmt()).multiply(rateQty));//实收金额
                    supplierOutData.setIncludeTaxSale(CommonUtil.stripTrailingZeros(saleSum.getIncludeTaxSale()).multiply(rateQty));//成本额
                    supplierOutData.setGrossProfitMargin(CommonUtil.stripTrailingZeros(saleSum.getGrossProfitMargin()).multiply(rateQty));//毛利额
                    //在错误的情况下   实收金额可能是0  NND
                    if (supplierOutData.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                        supplierOutData.setGrossProfitRate(supplierOutData.getGrossProfitMargin().
                                multiply(new BigDecimal(100)).
                                divide(supplierOutData.getAmt(), 2, BigDecimal.ROUND_HALF_EVEN)
                        );//毛利率=毛利额/实收金额
                    } else {
                        supplierOutData.setGrossProfitRate(BigDecimal.ZERO);
                    }
                    // 将符合条件的数据加入列表
                    productSupplierDataList.add(supplierOutData);
                }
            }
        }
        Map<String, List<ProductSalesBySupplierWithSaleManOutData>> collect = new HashMap<String, List<ProductSalesBySupplierWithSaleManOutData>>();
        // 根据不同情况分组
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
            // 根据供应商编码分组
            collect = productSupplierDataList.stream().collect(Collectors.groupingBy(item -> item.getSupplierCode()));
        }
        // 对值进行累加
        for (String s : collect.keySet()) {
            ProductSalesBySupplierWithSaleManOutData productSalesBySupplierWithSaleManOutData1 = new ProductSalesBySupplierWithSaleManOutData();
            // 初始化值为0
            productSalesBySupplierWithSaleManOutData1.setQty(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setAmountReceivable(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setAmt(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setIncludeTaxSale(BigDecimal.ZERO);
            productSalesBySupplierWithSaleManOutData1.setGrossProfitMargin(BigDecimal.ZERO);
            // 累加数据
            List<ProductSalesBySupplierWithSaleManOutData> productSalesBySupplierWithSaleManOutData2 = collect.get(s);
            ProductSalesBySupplierWithSaleManOutData firstData = productSalesBySupplierWithSaleManOutData2.get(0);
            productSalesBySupplierWithSaleManOutData1.setSupplierCode(firstData.getSupplierCode());  // 设置供应商编码
            productSalesBySupplierWithSaleManOutData1.setSupplierName(firstData.getSupplierName());  // 设置供应商名称
            // 按业务员
            productSalesBySupplierWithSaleManOutData1.setGssCode(firstData.getGssCode());  // 设置业务员编码
            productSalesBySupplierWithSaleManOutData1.setGssName(firstData.getGssName());  // 设置业务员名称
            // 按门店
            productSalesBySupplierWithSaleManOutData1.setStoCode(firstData.getStoCode());  // 设置门店编码
            productSalesBySupplierWithSaleManOutData1.setStoName(firstData.getStoName());  // 设置门店名称
            // 按商品
            productSalesBySupplierWithSaleManOutData1.setProCode(firstData.getProCode());  // 设置商品编码
            productSalesBySupplierWithSaleManOutData1.setProCommonName(firstData.getProCommonName());  // 设置通用名称
            productSalesBySupplierWithSaleManOutData1.setProName(firstData.getProName());  // 设置商品名称
            productSalesBySupplierWithSaleManOutData1.setExpiryDate(firstData.getExpiryDate());  // 设置有效期至
            productSalesBySupplierWithSaleManOutData1.setExpiryDay(firstData.getExpiryDay());  // 设置效期时间
            productSalesBySupplierWithSaleManOutData1.setSpecs(firstData.getSpecs());  // 设置规格
            productSalesBySupplierWithSaleManOutData1.setUnit(firstData.getUnit());  // 设置单位
            productSalesBySupplierWithSaleManOutData1.setFactoryName(firstData.getFactoryName());  // 设置生产厂家
            for (ProductSalesBySupplierWithSaleManOutData salesBySupplierWithSaleManOutData : productSalesBySupplierWithSaleManOutData2) {
                productSalesBySupplierWithSaleManOutData1.setQty(productSalesBySupplierWithSaleManOutData1.getQty().add(salesBySupplierWithSaleManOutData.getQty()));   // 数量
                productSalesBySupplierWithSaleManOutData1.setAmountReceivable(productSalesBySupplierWithSaleManOutData1.getAmountReceivable().add(salesBySupplierWithSaleManOutData.getAmountReceivable()));    // 应收金额
                productSalesBySupplierWithSaleManOutData1.setAmt(productSalesBySupplierWithSaleManOutData1.getAmt().add(salesBySupplierWithSaleManOutData.getAmt()));    // 实收金额
                productSalesBySupplierWithSaleManOutData1.setIncludeTaxSale(productSalesBySupplierWithSaleManOutData1.getIncludeTaxSale().add(salesBySupplierWithSaleManOutData.getIncludeTaxSale()));    // 成本额
                productSalesBySupplierWithSaleManOutData1.setGrossProfitMargin(productSalesBySupplierWithSaleManOutData1.getGrossProfitMargin().add(salesBySupplierWithSaleManOutData.getGrossProfitMargin()));  //毛利额
            }
            //在错误的情况下   实收金额可能是0  NND
            if (productSalesBySupplierWithSaleManOutData1.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                productSalesBySupplierWithSaleManOutData1.setGrossProfitRate(productSalesBySupplierWithSaleManOutData1.getGrossProfitMargin().
                        multiply(new BigDecimal(100)).
                        divide(productSalesBySupplierWithSaleManOutData1.getAmt(), 2, BigDecimal.ROUND_HALF_EVEN)
                );//毛利率=毛利额/实收金额
            } else {
                productSalesBySupplierWithSaleManOutData1.setGrossProfitRate(BigDecimal.ZERO);
            }
            productSalesBySupplierWithSaleManOutData.add(productSalesBySupplierWithSaleManOutData1);
        }
        return productSalesBySupplierWithSaleManOutData;
    }
}

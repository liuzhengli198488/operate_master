package com.gys.report.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.*;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.data.InventoryInquiry.EndingInventoryOutData;
import com.gys.mapper.*;
import com.gys.report.common.constant.CommonConstant;
import com.gys.report.common.constant.RepertoryConst;
import com.gys.report.entity.*;
import com.gys.report.service.PayService;
import com.gys.report.service.ReportFormsService;
import com.gys.util.*;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportFormsServiceImpl implements ReportFormsService {
    @Autowired
    private GaiaMaterialDocMapper materialDocMapper;
    @Autowired
    private GaiaSdSaleDMapper saleDMapper;
    @Autowired
    private GaiaSdSaleHMapper saleHMapper;
    @Resource
    private PayService payService;
    @Autowired
    private GaiaSdSaleDMapper gaiaSdSaleDMapper;
    @Autowired
    private GaiaSdBatchChangeMapper sdBatchChanegMapper;
    @Autowired
    private GaiaStoreDataMapper gaiaStoreDataMapper;
    @Autowired
    private GaiaSdStoresGroupMapper gaiaSdStoresGroupMapper;
    @Resource
    public CosUtils cosUtils;
    @Override
    public PageInfo<SupplierDealOutData> selectSupplierDealPage(SupplierDealInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum() - 1, inData.getPageSize());
        }
        if ((ObjectUtil.isEmpty(inData.getStartDate()) || ObjectUtil.isEmpty(inData.getEndDate())) && StringUtils.isEmpty(inData.getDnId())) {
            throw new BusinessException("日期或单号不能为空！");
        }
        List<SupplierDealOutData> outData = this.materialDocMapper.selectSupplierDealPageBySiteCode(inData);
        List<SupplierDealOutData> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(outData)) {
            List<Map<String, Object>> selectSupplierName = this.materialDocMapper.selectSupplierName(inData);
            if (StrUtil.isNotBlank(inData.getPoSupplierSalesman())) {
                //查询业务员
                if (CollUtil.isNotEmpty(selectSupplierName)) {
                    for (Map<String, Object> map : selectSupplierName) {
                        for (SupplierDealOutData outDatum : outData) {
                            if (Objects.equals(Convert.toStr(map.get("client")), outDatum.getCLIENT()) && Objects.equals(Convert.toStr(map.get("supSite")), outDatum.getSiteCode()) && Objects.equals(Convert.toStr(map.get("supSelfCode")), outDatum.getSupCode()) && Objects.equals(Convert.toStr(map.get("gssCode")), outDatum.getPoSupplierSalesman())) {
                                outDatum.setPoSupplierSalesmanName(Convert.toStr(map.get("gssName")));
                                list.add(outDatum);
                            }
                        }
                    }
                }
            } else {
                if (CollUtil.isNotEmpty(selectSupplierName)) {
                    for (Map<String, Object> map : selectSupplierName) {
                        for (SupplierDealOutData outDatum : outData) {
                            if (Objects.equals(Convert.toStr(map.get("client")), outDatum.getCLIENT()) && Objects.equals(Convert.toStr(map.get("supSite")), outDatum.getSiteCode()) && Objects.equals(Convert.toStr(map.get("supSelfCode")), outDatum.getSupCode()) && Objects.equals(Convert.toStr(map.get("gssCode")), outDatum.getPoSupplierSalesman())) {
                                outDatum.setPoSupplierSalesmanName(Convert.toStr(map.get("gssName")));
                            }
                        }
                    }
                }
                list = outData;
            }
        }

        PageInfo pageInfo;

        if (ObjectUtil.isNotEmpty(list)) {
            //开单金额
            BigDecimal billingAmount = BigDecimal.ZERO;
            //税金
            BigDecimal rateBat = BigDecimal.ZERO;
            //含税成本额
            BigDecimal totalAmount = BigDecimal.ZERO;
            //去税成本额
            BigDecimal batAmt = BigDecimal.ZERO;
            BigDecimal zero = BigDecimal.ZERO;
            for (SupplierDealOutData supplierDealOutData : list) {
                billingAmount = billingAmount.add(Objects.nonNull(supplierDealOutData.getBillingAmount())?supplierDealOutData.getBillingAmount().setScale(4, BigDecimal.ROUND_HALF_UP):zero);
                rateBat = rateBat.add(Objects.nonNull(supplierDealOutData.getRateBat())?supplierDealOutData.getRateBat().setScale(4, BigDecimal.ROUND_HALF_UP):zero);
                totalAmount = totalAmount.add(Objects.nonNull(supplierDealOutData.getTotalAmount())?supplierDealOutData.getTotalAmount().setScale(4, BigDecimal.ROUND_HALF_UP):zero);
                batAmt = batAmt.add(Objects.nonNull(supplierDealOutData.getBatAmt())?supplierDealOutData.getBatAmt().setScale(4, BigDecimal.ROUND_HALF_UP):zero);

            }
            SupplierDealOutTotal outTotal = new SupplierDealOutTotal();
            outTotal.setBillingAmount(billingAmount);
            outTotal.setRateBat(rateBat);
            outTotal.setTotalAmount(totalAmount);
            outTotal.setBatAmt(batAmt);

            pageInfo = new PageInfo(list, outTotal);
        } else {
            pageInfo = new PageInfo(list);
        }

        return pageInfo;
    }

    @Override
    public PageInfo<SupplierDealOutData> selectSupplierDealDetailPage(SupplierDealInData inData) {

        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if ((ObjectUtil.isEmpty(inData.getStartDate()) || ObjectUtil.isEmpty(inData.getEndDate())) && StringUtils.isEmpty(inData.getDnId())) {
            throw new BusinessException("日期或单号不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        List<SupplierDealOutData> outData = this.materialDocMapper.selectSupplierDealDetailPage(inData);
        List<SupplierDealOutData> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(outData)) {
            //查询业务员
            List<Map<String, Object>> selectSupplierName = this.materialDocMapper.selectSupplierName(inData);
            if (StrUtil.isNotBlank(inData.getPoSupplierSalesman())) {

                if (CollUtil.isNotEmpty(selectSupplierName)) {
                    for (Map<String, Object> map : selectSupplierName) {
                        for (SupplierDealOutData outDatum : outData) {
                            if (Objects.equals(Convert.toStr(map.get("client")), outDatum.getCLIENT()) && Objects.equals(Convert.toStr(map.get("supSite")), outDatum.getSiteCode()) && Objects.equals(Convert.toStr(map.get("supSelfCode")), outDatum.getSupCode()) && Objects.equals(Convert.toStr(map.get("gssCode")), outDatum.getPoSupplierSalesman())) {
                                outDatum.setPoSupplierSalesmanName(Convert.toStr(map.get("gssName")));
                                list.add(outDatum);
                            }
                        }
                    }
                }
            } else {
                if (CollUtil.isNotEmpty(selectSupplierName)) {
                    for (Map<String, Object> map : selectSupplierName) {
                        for (SupplierDealOutData outDatum : outData) {
                            if (Objects.equals(Convert.toStr(map.get("client")), outDatum.getCLIENT()) && Objects.equals(Convert.toStr(map.get("supSite")), outDatum.getSiteCode()) && Objects.equals(Convert.toStr(map.get("supSelfCode")), outDatum.getSupCode()) && Objects.equals(Convert.toStr(map.get("gssCode")), outDatum.getPoSupplierSalesman())) {
                                outDatum.setPoSupplierSalesmanName(Convert.toStr(map.get("gssName")));
                            }
                        }
                    }
                }
                list = outData;
            }
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(list)) {
            //开单金额
            BigDecimal billingAmount = BigDecimal.ZERO;
            //税金
            BigDecimal rateBat = BigDecimal.ZERO;
            //含税成本额
            BigDecimal totalAmount = BigDecimal.ZERO;
            //去税成本额
            BigDecimal batAmt = BigDecimal.ZERO;
            //数量
            BigDecimal qty = BigDecimal.ZERO;
            for (SupplierDealOutData supplierDealOutData : list) {
                billingAmount = billingAmount.add(supplierDealOutData.getBillingAmount().setScale(4, BigDecimal.ROUND_HALF_UP));
                rateBat = rateBat.add(supplierDealOutData.getRateBat().setScale(4, BigDecimal.ROUND_HALF_UP));
                totalAmount = totalAmount.add(supplierDealOutData.getTotalAmount().setScale(4, BigDecimal.ROUND_HALF_UP));
                batAmt = batAmt.add(supplierDealOutData.getBatAmt().setScale(4, BigDecimal.ROUND_HALF_UP));
                qty = qty.add(supplierDealOutData.getQty());
            }
            SupplierDealOutTotal outTotal = new SupplierDealOutTotal();
            outTotal.setBillingAmount(billingAmount);
            outTotal.setRateBat(rateBat);
            outTotal.setTotalAmount(totalAmount);
            outTotal.setBatAmt(batAmt);
            outTotal.setQty(qty);
            //SupplierDealOutTotal outTotal = this.materialDocMapper.selectSupplierDealDetailByTotal(inData);
            pageInfo = new PageInfo(list, outTotal);
        } else {
            pageInfo = new PageInfo(list);
        }
        return pageInfo;
    }

    @Override
    public PageInfo<WholesaleSaleOutData> selectWholesaleSalePage(WholesaleSaleInData inData) {
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("单据号或起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("单据号或结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCodes())) {
            inData.setProArr(inData.getProCodes().split("\\s+ |\\s+|,"));
        }
        /*if (StringUtils.isNotEmpty(inData.getMatType())) {
            if (inData.getMatType().equals("XD")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("XD")));
            } else if (inData.getMatType().equals("ED")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("ED")));
            } else if (inData.getMatType().equals("PD")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("PD", "ND", "PX")));
            } else if (inData.getMatType().equals("TD")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("TD", "MD")));
            } else if (inData.getMatType().equals("PX")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("PX")));
            }
        } else {
            inData.setMatTypes(new ArrayList<String>(Arrays.asList("XD", "PD", "ND", "ED", "TD", "MD", "PX")));
        }*/
        if (CollUtil.isNotEmpty(inData.getMatTypes())) {
            List<String> matTypes = inData.getMatTypes();
            for (int i = 0; i < matTypes.size(); i++) {
                if (matTypes.get(i).equals("PD")) {
                    matTypes.add("PX");
                }
            }
        } else {
            inData.setMatTypes(new ArrayList<String>(Arrays.asList("XD", "PD", "ND", "ED", "TD", "MD", "PX")));
        }
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        List<WholesaleSaleOutData> outData = this.materialDocMapper.selectWholesaleSalePage(inData);

        //新增销售员、抬头备注字段
        //销售员取值:GAIA_WHOLESALE_SALESMAN表GSS_NAME字段 用CLIENT+GSS_CODE关联
        //GSS_CODE取值:GAIA_SO_HEADER表GWS_CODE字段
        List<WholesaleSaleOutData> salemanList = this.materialDocMapper.selectWholesaleSalemanList(inData);
        for (WholesaleSaleOutData out : outData) {
            for (WholesaleSaleOutData saleOutData : salemanList) {
                if (out.getDnId().equals(saleOutData.getDnId())
                        && out.getMatType().equals(saleOutData.getMatType())) {
                    out.setGwoOwnerSaleMan(saleOutData.getGwoOwnerSaleMan());
                    out.setGwoOwnerSaleManUserId(saleOutData.getGwoOwnerSaleManUserId());
                    out.setSoHeaderRemark(saleOutData.getSoHeaderRemark());
                }
            }
        }
        if (StrUtil.isNotEmpty(inData.getGwoOwnerSaleMan())) {
            outData = outData.stream().filter(out -> {
                return StringUtils.isNotEmpty(out.getGwoOwnerSaleManUserId())
                        && out.getGwoOwnerSaleManUserId().equals(inData.getGwoOwnerSaleMan());
            }).collect(Collectors.toList());
        }
        if (StrUtil.isNotEmpty(inData.getSoHeaderRemark())) {
            outData = outData.stream().filter(out -> {
                return StringUtils.isNotEmpty(out.getSoHeaderRemark())
                        && out.getSoHeaderRemark().equals(inData.getSoHeaderRemark());
            }).collect(Collectors.toList());
        }

        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            WholesaleSaleOutTotal outNum = new WholesaleSaleOutTotal();
            for (WholesaleSaleOutData out : outData) {
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outNum.setBatAmt(CommonUtil.stripTrailingZeros(outNum.getBatAmt()).add(CommonUtil.stripTrailingZeros(out.getBatAmt())));
                outNum.setRateBat(CommonUtil.stripTrailingZeros(outNum.getRateBat()).add(CommonUtil.stripTrailingZeros(out.getRateBat())));
                outNum.setTotalAmount(CommonUtil.stripTrailingZeros(outNum.getTotalAmount()).add(CommonUtil.stripTrailingZeros(out.getTotalAmount())));
                outNum.setAddAmt(CommonUtil.stripTrailingZeros(outNum.getAddAmt()).add(CommonUtil.stripTrailingZeros(out.getAddAmt())));
                outNum.setAddTax(CommonUtil.stripTrailingZeros(outNum.getAddTax()).add(CommonUtil.stripTrailingZeros(out.getAddTax())));
                outNum.setAddtotalAmount(CommonUtil.stripTrailingZeros(outNum.getAddtotalAmount()).add(CommonUtil.stripTrailingZeros(out.getAddtotalAmount())));
                outNum.setMovAmt(CommonUtil.stripTrailingZeros(outNum.getMovAmt()).add(CommonUtil.stripTrailingZeros(out.getMovAmt())));
                outNum.setRateMov(CommonUtil.stripTrailingZeros(outNum.getRateMov()).add(CommonUtil.stripTrailingZeros(out.getRateMov())));
                outNum.setMovTotalAmount(CommonUtil.stripTrailingZeros(outNum.getMovTotalAmount()).add(CommonUtil.stripTrailingZeros(out.getMovTotalAmount())));

            }
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo<WholesaleSaleOutData> selectWholesaleSaleDetailPage(WholesaleSaleInData inData) {
        //输入单号可以不输入时间
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("单据号或起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("单据号或结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCodes())) {
            inData.setProArr(inData.getProCodes().split("\\s+ |\\s+|,"));
        }
         /*if (StringUtils.isNotEmpty(inData.getMatType())) {
            if (inData.getMatType().equals("XD")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("XD")));
            } else if (inData.getMatType().equals("ED")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("ED")));
            } else if (inData.getMatType().equals("PD")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("PD", "ND")));
            } else if (inData.getMatType().equals("TD")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("TD", "MD")));
            } else if (inData.getMatType().equals("PX")) {
                inData.setMatTypes(new ArrayList<String>(Arrays.asList("PX")));
            }
        } else {
            inData.setMatTypes(new ArrayList<String>(Arrays.asList("XD", "PD", "ND", "ED", "TD", "MD", "PX")));
        }*/
        if (CollUtil.isEmpty(inData.getMatTypes())) {
            inData.setMatTypes(new ArrayList<String>(Arrays.asList("XD", "PD", "ND", "ED", "TD", "MD", "PX")));
        }
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        List<WholesaleSaleOutData> outData = this.materialDocMapper.selectWholesaleSaleDetailPage(inData);
        //新增销售员、抬头备注字段
        //销售员取值:GAIA_WHOLESALE_SALESMAN表GSS_NAME字段 用CLIENT+GSS_CODE关联
        //GSS_CODE取值:GAIA_SO_HEADER表GWS_CODE字段
        List<WholesaleSaleOutData> salemanList = this.materialDocMapper.selectWholesaleDetailSalemanList(inData);
        for (WholesaleSaleOutData out : outData) {
            for (WholesaleSaleOutData saleOutData : salemanList) {
                if (out.getPostDate().equals(saleOutData.getPostDate())
                        && out.getProCode().equals(saleOutData.getProCode())
                        && out.getDnId().equals(saleOutData.getDnId())
                        && out.getMatType().equals(saleOutData.getMatType())) {
                    out.setGwoOwnerSaleMan(saleOutData.getGwoOwnerSaleMan());
                    out.setGwoOwnerSaleManUserId(saleOutData.getGwoOwnerSaleManUserId());
                    out.setSoHeaderRemark(saleOutData.getSoHeaderRemark());
                }
            }
        }
        if (StrUtil.isNotEmpty(inData.getGwoOwnerSaleMan())) {
            outData = outData.stream().filter(out -> {
                return StringUtils.isNotEmpty(out.getGwoOwnerSaleManUserId())
                        && out.getGwoOwnerSaleManUserId().equals(inData.getGwoOwnerSaleMan());
            }).collect(Collectors.toList());
        }
        if (StrUtil.isNotEmpty(inData.getSoHeaderRemark())) {
            outData = outData.stream().filter(out -> {
                return StringUtils.isNotEmpty(out.getSoHeaderRemark())
                        && out.getSoHeaderRemark().equals(inData.getSoHeaderRemark());
            }).collect(Collectors.toList());
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            WholesaleSaleOutTotal outNum = new WholesaleSaleOutTotal();
            for (WholesaleSaleOutData out : outData) {
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outNum.setBatAmt(CommonUtil.stripTrailingZeros(outNum.getBatAmt()).add(CommonUtil.stripTrailingZeros(out.getBatAmt())));
                outNum.setRateBat(CommonUtil.stripTrailingZeros(outNum.getRateBat()).add(CommonUtil.stripTrailingZeros(out.getRateBat())));
                outNum.setTotalAmount(CommonUtil.stripTrailingZeros(outNum.getTotalAmount()).add(CommonUtil.stripTrailingZeros(out.getTotalAmount())));
                outNum.setAddAmt(CommonUtil.stripTrailingZeros(outNum.getAddAmt()).add(CommonUtil.stripTrailingZeros(out.getAddAmt())));
                outNum.setAddTax(CommonUtil.stripTrailingZeros(outNum.getAddTax()).add(CommonUtil.stripTrailingZeros(out.getAddTax())));
                outNum.setAddtotalAmount(CommonUtil.stripTrailingZeros(outNum.getAddtotalAmount()).add(CommonUtil.stripTrailingZeros(out.getAddtotalAmount())));
                outNum.setMovAmt(CommonUtil.stripTrailingZeros(outNum.getMovAmt()).add(CommonUtil.stripTrailingZeros(out.getMovAmt())));
                outNum.setRateMov(CommonUtil.stripTrailingZeros(outNum.getRateMov()).add(CommonUtil.stripTrailingZeros(out.getRateMov())));
                outNum.setMovTotalAmount(CommonUtil.stripTrailingZeros(outNum.getMovTotalAmount()).add(CommonUtil.stripTrailingZeros(out.getMovTotalAmount())));

            }
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            pageInfo = new PageInfo();
        }


        return pageInfo;
    }

    @Override
    public PageInfo<BusinessDocumentOutData> selectBusinessDocumentPage(BusinessDocumentInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("单据号或起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("单据号或结束日期不能为空！");
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
        List<BusinessDocumentOutData> outData = this.materialDocMapper.selectBusinessDocumentPage(inData);
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClient());

        for (BusinessDocumentOutData documentOutData : outData) {
            //转换
            if (documentOutData.getStoAttribute() != null || noChooseFlag) {
                documentOutData.setStoAttribute(StoreAttributeEnum.getName(documentOutData.getStoAttribute()));
            }
            if (documentOutData.getStoIfMedical() != null || noChooseFlag) {
                documentOutData.setStoIfMedical(StoreMedicalEnum.getName(documentOutData.getStoIfMedical()));
            }
            if (documentOutData.getStoIfDtp() != null || noChooseFlag) {
                documentOutData.setStoIfDtp(StoreDTPEnum.getName(documentOutData.getStoIfDtp()));
            }
            if (documentOutData.getStoTaxClass() != null || noChooseFlag) {
                documentOutData.setStoTaxClass(StoreTaxClassEnum.getName(documentOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(documentOutData.getStoCode())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        documentOutData.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        documentOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        documentOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        documentOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            //BusinessDocumentOutTotal outTotal = this.materialDocMapper.selectBusinessDocumentTotal(inData);
            BusinessDocumentOutTotal outTotal = BusinessDocumentOutTotal.builder()
                    .rateBat(outData.stream().map(BusinessDocumentOutData::getRateBat).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .totalAmount(outData.stream().map(BusinessDocumentOutData::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .batAmt(outData.stream().map(BusinessDocumentOutData::getBatAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .qty(outData.stream().map(BusinessDocumentOutData::getQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .build();
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo<BusinessDocumentOutData> selectBusinessDocumentDetailPage(BusinessDocumentInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("单据号或起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("单据号或结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCodes())) {
            inData.setProArr(inData.getProCodes().split("\\s+ |\\s+|,"));
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
        List<BusinessDocumentOutData> outData = new ArrayList<>();
//            if(inData.getMatType().equals("BD") || inData.getMatType().equals("ZD")){
        //报损
        List<BusinessDocumentOutData> bdStore = new ArrayList<>();
        List<BusinessDocumentOutData> bdDc = new ArrayList<>();
        //当同时为空或者有值的时候  都查
//                if((StringUtils.isNotEmpty(inData.getStore()) && StringUtils.isNotEmpty(inData.getDc())) ||
//                        (StringUtils.isEmpty(inData.getStore()) && StringUtils.isEmpty(inData.getDc()))){
//                }else if(StringUtils.isNotEmpty(inData.getStore())){ //报损  门店
//
//                }else if(StringUtils.isNotEmpty(inData.getDc())){  //报损  配送中心
//                }
        outData = this.materialDocMapper.selectBusinessDocumentDetailPage(inData);
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClient());

        for (BusinessDocumentOutData documentOutData : outData) {
            //转换
            if (documentOutData.getStoAttribute() != null || noChooseFlag) {
                documentOutData.setStoAttribute(StoreAttributeEnum.getName(documentOutData.getStoAttribute()));
            }
            if (documentOutData.getStoIfMedical() != null || noChooseFlag) {
                documentOutData.setStoIfMedical(StoreMedicalEnum.getName(documentOutData.getStoIfMedical()));
            }
            if (documentOutData.getStoIfDtp() != null || noChooseFlag) {
                documentOutData.setStoIfDtp(StoreDTPEnum.getName(documentOutData.getStoIfDtp()));
            }
            if (documentOutData.getStoTaxClass() != null || noChooseFlag) {
                documentOutData.setStoTaxClass(StoreTaxClassEnum.getName(documentOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(documentOutData.getStoCode())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        documentOutData.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        documentOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        documentOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        documentOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            documentOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }

//            }
//            if(inData.getMatType().equals("ZD")){
//                //自用
//                List<BusinessDocumentOutData> zdStore = new ArrayList<>();
//                List<BusinessDocumentOutData> zdDc = new ArrayList<>();
//                //当同时为空或者有值的时候  都查
//                if((StringUtils.isNotEmpty(inData.getStore()) && StringUtils.isNotEmpty(inData.getDc())) ||
//                        (StringUtils.isEmpty(inData.getStore()) && StringUtils.isEmpty(inData.getDc()))){
//                    zdStore = this.materialDocMapper.selectByZdStore(inData);
//                    zdDc = this.materialDocMapper.selectByZdDc(inData);
//                }else if(StringUtils.isNotEmpty(inData.getStore())){  //自用  门店
//                    zdStore = this.materialDocMapper.selectByZdStore(inData);
//                }else if(StringUtils.isNotEmpty(inData.getDc())){      //自用  配送中心
//                    zdDc = this.materialDocMapper.selectByZdDc(inData);
//                }
//                outData.addAll(zdStore);
//                outData.addAll(zdDc);
//            }
//           else if(inData.getMatType() .equals("DD") ){
//                //盘点 remark 为空
//                List<BusinessDocumentOutData>  dd = this.materialDocMapper.selectByDd(inData);
//                outData.addAll(dd);
//            }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            //BusinessDocumentOutTotal outTotal = this.materialDocMapper.selectBusinessDocumentDetailTotal(inData);
            BusinessDocumentOutTotal outTotal = BusinessDocumentOutTotal.builder()
                    .rateBat(outData.stream().map(BusinessDocumentOutData::getRateBat).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .totalAmount(outData.stream().map(BusinessDocumentOutData::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .batAmt(outData.stream().map(BusinessDocumentOutData::getBatAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .qty(outData.stream().map(BusinessDocumentOutData::getQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .proLSAmt(outData.stream().map(BusinessDocumentOutData::getProLSAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add))
                    .build();
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo<StoreRateSellOutData> selectStoreRateSellDetailPage(StoreRateSellInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
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
        List<StoreRateSellOutData> outData = this.saleDMapper.selectStoreRateSellDetailPage(inData);
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClient());

        for (StoreRateSellOutData sellOutData : outData) {
            //转换
            if (sellOutData.getStoAttribute() != null || noChooseFlag) {
                sellOutData.setStoAttribute(StoreAttributeEnum.getName(sellOutData.getStoAttribute()));
            }
            if (sellOutData.getStoIfMedical() != null || noChooseFlag) {
                sellOutData.setStoIfMedical(StoreMedicalEnum.getName(sellOutData.getStoIfMedical()));
            }
            if (sellOutData.getStoIfDtp() != null || noChooseFlag) {
                sellOutData.setStoIfDtp(StoreDTPEnum.getName(sellOutData.getStoIfDtp()));
            }
            if (sellOutData.getStoTaxClass() != null || noChooseFlag) {
                sellOutData.setStoTaxClass(StoreTaxClassEnum.getName(sellOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(sellOutData.getBrId())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        sellOutData.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        sellOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        sellOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        sellOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            sellOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            sellOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            sellOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            sellOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            StoreRateSellOutData outNum = new StoreRateSellOutData();
            for (StoreRateSellOutData out : outData) {
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outNum.setAmountReceivable(CommonUtil.stripTrailingZeros(outNum.getAmountReceivable()).add(CommonUtil.stripTrailingZeros(out.getAmountReceivable())));
                outNum.setAmt(CommonUtil.stripTrailingZeros(outNum.getAmt()).add(CommonUtil.stripTrailingZeros(out.getAmt())));
                outNum.setDeduction(CommonUtil.stripTrailingZeros(outNum.getDeduction()).add(CommonUtil.stripTrailingZeros(out.getDeduction())));
                outNum.setRemoveTaxSale(CommonUtil.stripTrailingZeros(outNum.getRemoveTaxSale()).add(CommonUtil.stripTrailingZeros(out.getRemoveTaxSale())));
                outNum.setMovPrices(CommonUtil.stripTrailingZeros(outNum.getMovPrices()).add(CommonUtil.stripTrailingZeros(out.getMovPrices())));
                outNum.setIncludeTaxSale(CommonUtil.stripTrailingZeros(outNum.getIncludeTaxSale()).add(CommonUtil.stripTrailingZeros(out.getIncludeTaxSale())));
                outNum.setGrossProfitMargin(CommonUtil.stripTrailingZeros(outNum.getGrossProfitMargin()).add(CommonUtil.stripTrailingZeros(out.getGrossProfitMargin())));
                outNum.setAddMovPrices(CommonUtil.stripTrailingZeros(outNum.getAddMovPrices()).add(CommonUtil.stripTrailingZeros(out.getAddMovPrices())));
                outNum.setAddIncludeTaxSale(CommonUtil.stripTrailingZeros(outNum.getAddIncludeTaxSale()).add(CommonUtil.stripTrailingZeros(out.getAddIncludeTaxSale())));
                outNum.setAddGrossProfitMargin(CommonUtil.stripTrailingZeros(outNum.getAddGrossProfitMargin()).add(CommonUtil.stripTrailingZeros(out.getAddGrossProfitMargin())));
            }
            DecimalFormat df = new DecimalFormat("0.00%");
            outNum.setGrossProfitRate(df.format(outNum.getGrossProfitMargin().divide(outNum.getAmt(), BigDecimal.ROUND_HALF_EVEN)));
            outNum.setAddGrossProfitRate(df.format(outNum.getAddGrossProfitMargin().divide(outNum.getAmt(), BigDecimal.ROUND_HALF_EVEN)));
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo<StoreRateSellOutData> selectStoreRateSellPage(StoreRateSellInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
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
        List<StoreRateSellOutData> outData = this.saleDMapper.selectStoreRateSellPage(inData);

        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClient());

        for (StoreRateSellOutData rateSellOutData : outData) {
            //转换
            if (rateSellOutData.getStoAttribute() != null || noChooseFlag) {
                rateSellOutData.setStoAttribute(StoreAttributeEnum.getName(rateSellOutData.getStoAttribute()));
            }
            if (rateSellOutData.getStoIfMedical() != null || noChooseFlag) {
                rateSellOutData.setStoIfMedical(StoreMedicalEnum.getName(rateSellOutData.getStoIfMedical()));
            }
            if (rateSellOutData.getStoIfDtp() != null || noChooseFlag) {
                rateSellOutData.setStoIfDtp(StoreDTPEnum.getName(rateSellOutData.getStoIfDtp()));
            }
            if (rateSellOutData.getStoTaxClass() != null || noChooseFlag) {
                rateSellOutData.setStoTaxClass(StoreTaxClassEnum.getName(rateSellOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(rateSellOutData.getBrId())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        rateSellOutData.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        rateSellOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        rateSellOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        rateSellOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            rateSellOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            rateSellOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            rateSellOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            rateSellOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            StoreRateSellOutData outNum = new StoreRateSellOutData();
            for (StoreRateSellOutData out : outData) {
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outNum.setAmountReceivable(CommonUtil.stripTrailingZeros(outNum.getAmountReceivable()).add(CommonUtil.stripTrailingZeros(out.getAmountReceivable())));
                outNum.setAmt(CommonUtil.stripTrailingZeros(outNum.getAmt()).add(CommonUtil.stripTrailingZeros(out.getAmt())));
                outNum.setDeduction(CommonUtil.stripTrailingZeros(outNum.getDeduction()).add(CommonUtil.stripTrailingZeros(out.getDeduction())));
                outNum.setRemoveTaxSale(CommonUtil.stripTrailingZeros(outNum.getRemoveTaxSale()).add(CommonUtil.stripTrailingZeros(out.getRemoveTaxSale())));
                outNum.setMovPrices(CommonUtil.stripTrailingZeros(outNum.getMovPrices()).add(CommonUtil.stripTrailingZeros(out.getMovPrices())));
                outNum.setIncludeTaxSale(CommonUtil.stripTrailingZeros(outNum.getIncludeTaxSale()).add(CommonUtil.stripTrailingZeros(out.getIncludeTaxSale())));
                outNum.setGrossProfitMargin(CommonUtil.stripTrailingZeros(outNum.getGrossProfitMargin()).add(CommonUtil.stripTrailingZeros(out.getGrossProfitMargin())));
                outNum.setAddMovPrices(CommonUtil.stripTrailingZeros(outNum.getAddMovPrices()).add(CommonUtil.stripTrailingZeros(out.getAddMovPrices())));
                outNum.setAddIncludeTaxSale(CommonUtil.stripTrailingZeros(outNum.getAddIncludeTaxSale()).add(CommonUtil.stripTrailingZeros(out.getAddIncludeTaxSale())));
                outNum.setAddGrossProfitMargin(CommonUtil.stripTrailingZeros(outNum.getAddGrossProfitMargin()).add(CommonUtil.stripTrailingZeros(out.getAddGrossProfitMargin())));
            }
            DecimalFormat df = new DecimalFormat("0.00%");
            outNum.setGrossProfitRate(df.format(outNum.getGrossProfitMargin().divide(outNum.getAmt(), BigDecimal.ROUND_HALF_EVEN)));
            outNum.setAddGrossProfitRate(df.format(outNum.getAddGrossProfitMargin().divide(outNum.getAmt(), BigDecimal.ROUND_HALF_EVEN)));
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectStoreSaleByDay(StoreSaleDayInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        GetPayInData pay = new GetPayInData();
        pay.setClientId(inData.getClient());
        pay.setType("1");

        //获取支付类型
        List<GetPayTypeOutData> payTypeOutData = payService.payTypeListByClient(pay);
        if (payTypeOutData != null && payTypeOutData.size() > 0) {
            inData.setPayTypeOutData(payTypeOutData);
        }
        //先查询权限   flag  0：不开启  1：开启
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClient(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        inData.setFlag(flag);
        List<Map<String, Object>> outData = this.saleDMapper.selectStoreSaleByDay(inData);
        PageInfo pageInfo;
        BigDecimal totalPrice=BigDecimal.ZERO;
        DecimalFormat df1=new DecimalFormat();
        DecimalFormat df2=new DecimalFormat("#0.00");
        df1.applyPattern("-0.00");
        if (ObjectUtil.isNotEmpty(outData)) {
            outData = getOutDate(outData,df1,df2);
            // 集合列的数据汇总
            Map<String, Object> mapTotal = this.saleDMapper.selectStoreSaleByDayTotal(inData);
            for (Map<String, Object> item : outData){
                //if(item.get("amt").toString().startsWith("-") && item.get("amountReceivable").toString().startsWith("-")){
                totalPrice=totalPrice.add(new BigDecimal(item.get("zkDyq").toString()));
                mapTotal.put("zkDyq",totalPrice);
                totalPrice=totalPrice.add(new BigDecimal(item.get("zkJfdx").toString()));
                mapTotal.put("zkJfdx",totalPrice);
                totalPrice=totalPrice.add(new BigDecimal(item.get("zkDzq").toString()));
                mapTotal.put("zkDzq",totalPrice);
                // }
            }
            if (ValidateUtil.isNotEmpty(flag)) {
                mapTotal.put("flag", flag);
            } else {
                mapTotal.put("flag", "0");
            }

            pageInfo = new PageInfo(outData, mapTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }
    /**
    * @date 2022/2/22 17:36
    * @author liuzhengli
    * @Description TODO 门店日期销售明细查询(加盟商用)
    * @param  * @param null
    * @return {@link null}
    * @Version1.0
    **/
    @Override
    public PageInfo selectStoreSaleByDayAndClient(StoreSaleDayInData inData) {
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
        GetPayInData pay = new GetPayInData();
        pay.setClientId(inData.getClient());
        pay.setType("1");

        //获取支付类型
        List<GetPayTypeOutData> payTypeOutData = payService.payTypeListByClient(pay);
        if (payTypeOutData != null && payTypeOutData.size() > 0) {
            inData.setPayTypeOutData(payTypeOutData);
        }
        List<Map<String, Object>> outData = this.saleDMapper.selectStoreSaleByDay(inData);
        PageInfo pageInfo;
        BigDecimal totalPrice=BigDecimal.ZERO;
        DecimalFormat df1=new DecimalFormat();
        DecimalFormat df2=new DecimalFormat("#0.00");
        df1.applyPattern("-0.00");
        if (ObjectUtil.isNotEmpty(outData)) {
            outData = getOutDate(outData,df1,df2);
            // 集合列的数据汇总
            Map<String, Object> mapTotal = this.saleDMapper.selectStoreSaleByDayTotal(inData);
            for (Map<String, Object> item : outData){
                    totalPrice=totalPrice.add(new BigDecimal(item.get("zkDyq").toString()));
                    mapTotal.put("zkDyq",totalPrice);
                    totalPrice=totalPrice.add(new BigDecimal(item.get("zkJfdx").toString()));
                    mapTotal.put("zkJfdx",totalPrice);
                    totalPrice=totalPrice.add(new BigDecimal(item.get("zkDzq").toString()));
                    mapTotal.put("zkDzq",totalPrice);
            }
            pageInfo = new PageInfo(outData, mapTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    private List<Map<String, Object>> getOutDate(List<Map<String, Object>> outData,DecimalFormat df1,DecimalFormat df2) {
            for (Map<String, Object> item : outData) {
                if (StrUtil.isNotBlank(Convert.toStr(item.get("datePart")))) {
                    String hm = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(item.get("datePart")), "HHmmss"), "HH:mm:ss");
                    item.put("datePart", hm);
                }
                if(item.get("amt").toString().startsWith("-") && item.get("amountReceivable").toString().startsWith("-")){
                    if(item.get("zkJfdx").toString().startsWith("-") && !df2.format(Double.parseDouble(item.get("zkJfdx").toString())).equals("0.00")){//积分抵扣卷
                        item.put("zkJfdx",df2.format(item.get("zkJfdx")));
                    }else{
                        item.put("zkJfdx",df1.format(df2.format(new BigDecimal(item.get("zkJfdx").toString()))));
                    }

                    if(item.get("zkDzq").toString().startsWith("-") && !df2.format(Double.parseDouble(item.get("zkDzq").toString())).equals("0.00")){//电子抵扣卷
                        item.put("zkDzq",df1.format(item.get("zkDzq")));
                    }else{
                        item.put("zkDzq",df1.format(df2.format(new BigDecimal(item.get("zkDzq").toString()))));
                    }

                    if(item.get("zkDyq").toString().startsWith("-") && !df2.format(Double.parseDouble(item.get("zkDyq").toString())).equals("0.00")){//抵用卷拆扣卷
                        item.put("zkDyq",df1.format(item.get("zkDyq")));
                    }else{
                        item.put("zkDyq",df1.format(df2.format(new BigDecimal(item.get("zkDzq").toString()))));
                    }
                }
            }
            System.err.println(outData);
        return outData;
    }

    @Override
    public PageInfo selectInventoryChangeSummary(InventoryChangeSummaryInData inData) {

        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }

        //查询期初数据
        List<InventoryChangeSummaryOutData> startData = getStartData(inData);

        String qcDate=getCurrentClientQcDate(inData.getClient());
        if (Integer.parseInt(StrUtil.isEmpty(qcDate)?"0":qcDate) >= Integer.parseInt(StrUtil.isEmpty(inData.getStartDate())?"0":inData.getStartDate())) {
            inData.setQcDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.parse(qcDate, DatePattern.PURE_DATE_PATTERN),1),DatePattern.PURE_DATE_PATTERN));
        }else {
            inData.setQcDate(inData.getStartDate());
        }
        //查询期间数据
        List<InventoryChangeSummaryOutData> distributionAndAdjustAndSaleData = this.materialDocMapper.selectDistributionAndAdjustSummaryData(inData);

        //合并期初、期间数据
        for (InventoryChangeSummaryOutData out : startData) {
            for (InventoryChangeSummaryOutData bet : distributionAndAdjustAndSaleData) {
                if (out.getProCode().equals(bet.getProCode())) {
                    out.setPurchaseAmount(bet.getPurchaseAmount() == null ? BigDecimal.ZERO : bet.getPurchaseAmount());
                    out.setReturnAmount(bet.getReturnAmount() == null ? BigDecimal.ZERO : bet.getReturnAmount());
                    out.setSaleAmount(bet.getSaleAmount() == null ? BigDecimal.ZERO : bet.getSaleAmount());
                    out.setAdjustAmt(bet.getAdjustAmt() == null ? BigDecimal.ZERO : bet.getAdjustAmt());
                    out.setDistributionAmt(bet.getDistributionAmt() == null ? BigDecimal.ZERO : bet.getDistributionAmt());
                    out.setProfitLossAmount(bet.getProfitLossAmount() == null ? BigDecimal.ZERO : bet.getProfitLossAmount());
                    out.setQcAmt(bet.getQcAmt() == null ? BigDecimal.ZERO : bet.getQcAmt());

                    out.setPurchaseQty(bet.getPurchaseQty() == null ? BigDecimal.ZERO : bet.getPurchaseQty());
                    out.setReturnQty(bet.getReturnQty() == null ? BigDecimal.ZERO : bet.getReturnQty());
                    out.setSaleQty(bet.getSaleQty() == null ? BigDecimal.ZERO : bet.getSaleQty());
                    out.setAdjustQty(bet.getAdjustQty() == null ? BigDecimal.ZERO : bet.getAdjustQty());
                    out.setDistributionQty(bet.getDistributionQty() == null ? BigDecimal.ZERO : bet.getDistributionQty());
                    out.setProfitLossQty(bet.getProfitLossQty() == null ? BigDecimal.ZERO : bet.getProfitLossQty());
                    out.setQcQty(bet.getQcQty() == null ? BigDecimal.ZERO : bet.getQcQty());

                    out.setEndAmt(
                            out.getStartAmt() == null ? BigDecimal.ZERO : out.getStartAmt()
                                    .add(bet.getPurchaseAmount() == null ? BigDecimal.ZERO : bet.getPurchaseAmount())
                                    .add(bet.getReturnAmount() == null ? BigDecimal.ZERO : bet.getReturnAmount())
                                    .add(bet.getSaleAmount() == null ? BigDecimal.ZERO : bet.getSaleAmount())
                                    .add(bet.getAdjustAmt() == null ? BigDecimal.ZERO : bet.getAdjustAmt())
                                    .add(bet.getDistributionAmt() == null ? BigDecimal.ZERO : bet.getDistributionAmt())
                                    .add(bet.getProfitLossAmount() == null ? BigDecimal.ZERO : bet.getProfitLossAmount())
                                    .add(bet.getQcAmt() == null ? BigDecimal.ZERO : bet.getQcAmt())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                    out.setEndQty(
                            out.getStartQty() == null ? BigDecimal.ZERO : out.getStartQty()
                                    .add(bet.getPurchaseQty() == null ? BigDecimal.ZERO : bet.getPurchaseQty())
                                    .add(bet.getReturnQty() == null ? BigDecimal.ZERO : bet.getReturnQty())
                                    .add(bet.getSaleQty() == null ? BigDecimal.ZERO : bet.getSaleQty())
                                    .add(bet.getAdjustQty() == null ? BigDecimal.ZERO : bet.getAdjustQty())
                                    .add(bet.getDistributionQty() == null ? BigDecimal.ZERO : bet.getDistributionQty())
                                    .add(bet.getProfitLossQty() == null ? BigDecimal.ZERO : bet.getProfitLossQty())
                                    .add(bet.getQcQty() == null ? BigDecimal.ZERO : bet.getQcQty())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
        //没有期初，但是有期间
        List<String> proCode = startData.stream().map(InventoryChangeSummaryOutData::getProCode).collect(Collectors.toList());
        distributionAndAdjustAndSaleData.removeIf(bet -> {
            return proCode.contains(bet.getProCode());
        });
        for (InventoryChangeSummaryOutData bet : distributionAndAdjustAndSaleData) {
            bet.setStartAmt(BigDecimal.ZERO);
            bet.setStartQty(BigDecimal.ZERO);
            bet.setEndAmt(
                    BigDecimal.ZERO
                            .add(bet.getPurchaseAmount() == null ? BigDecimal.ZERO : bet.getPurchaseAmount())
                            .add(bet.getReturnAmount() == null ? BigDecimal.ZERO : bet.getReturnAmount())
                            .add(bet.getSaleAmount() == null ? BigDecimal.ZERO : bet.getSaleAmount())
                            .add(bet.getAdjustAmt() == null ? BigDecimal.ZERO : bet.getAdjustAmt())
                            .add(bet.getDistributionAmt() == null ? BigDecimal.ZERO : bet.getDistributionAmt())
                            .add(bet.getProfitLossAmount() == null ? BigDecimal.ZERO : bet.getProfitLossAmount())
                            .add(bet.getQcAmt() == null ? BigDecimal.ZERO : bet.getQcAmt())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
            bet.setEndQty(
                    BigDecimal.ZERO
                            .add(bet.getPurchaseQty() == null ? BigDecimal.ZERO : bet.getPurchaseQty())
                            .add(bet.getReturnQty() == null ? BigDecimal.ZERO : bet.getReturnQty())
                            .add(bet.getSaleQty() == null ? BigDecimal.ZERO : bet.getSaleQty())
                            .add(bet.getAdjustQty() == null ? BigDecimal.ZERO : bet.getAdjustQty())
                            .add(bet.getDistributionQty() == null ? BigDecimal.ZERO : bet.getDistributionQty())
                            .add(bet.getProfitLossQty() == null ? BigDecimal.ZERO : bet.getProfitLossQty())
                            .add(bet.getQcQty() == null ? BigDecimal.ZERO : bet.getQcQty())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        startData.addAll(distributionAndAdjustAndSaleData);
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(startData)) {
            // 集合列的数据汇总
            InventoryChangeSummaryOutTotal outTotal = new InventoryChangeSummaryOutTotal();
            //计算合计
            outTotal = computeInventoryChangeSummaryTotal(startData, outTotal);
            pageInfo = new PageInfo(startData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectInventoryChangeSummaryWithStore(InventoryChangeSummaryInData inData) {
        if(ObjectUtils.isEmpty(inData.getSiteArr())){
            List<String> stoCodes = materialDocMapper.getStoCode(inData.getClient());
            if (stoCodes.size()>0){
                String[] strings = stoCodes.toArray(new String[stoCodes.size()]);
                inData.setSiteArr(strings);
            }
        }
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }

        //查询期初数据
        List<InventoryChangeSummaryWithStoreOutData> startData = getStartDataWithStore(inData);

        //查询期间数据
        List<InventoryChangeSummaryWithStoreOutData> distributionAndAdjustAndSaleData = this.materialDocMapper.selectDistributionAndAdjustSummaryDataWithStore(inData);

        //合并期初、期间数据
        for (InventoryChangeSummaryWithStoreOutData out : startData) {
            for (InventoryChangeSummaryWithStoreOutData bet : distributionAndAdjustAndSaleData) {
                if (out.getProCode().equals(bet.getProCode())) {
                    out.setPurchaseAmount(bet.getPurchaseAmount() == null ? BigDecimal.ZERO : bet.getPurchaseAmount());
                    out.setReturnAmount(bet.getReturnAmount() == null ? BigDecimal.ZERO : bet.getReturnAmount());
                    out.setSaleAmount(bet.getSaleAmount() == null ? BigDecimal.ZERO : bet.getSaleAmount());
                    out.setAdjustAmt(bet.getAdjustAmt() == null ? BigDecimal.ZERO : bet.getAdjustAmt());
                    out.setDistributionAmt(bet.getDistributionAmt() == null ? BigDecimal.ZERO : bet.getDistributionAmt());
                    out.setProfitLossAmount(bet.getProfitLossAmount() == null ? BigDecimal.ZERO : bet.getProfitLossAmount());
                    out.setQcAmt(bet.getQcAmt() == null ? BigDecimal.ZERO : bet.getQcAmt());

                    out.setPurchaseQty(bet.getPurchaseQty() == null ? BigDecimal.ZERO : bet.getPurchaseQty());
                    out.setReturnQty(bet.getReturnQty() == null ? BigDecimal.ZERO : bet.getReturnQty());
                    out.setSaleQty(bet.getSaleQty() == null ? BigDecimal.ZERO : bet.getSaleQty());
                    out.setAdjustQty(bet.getAdjustQty() == null ? BigDecimal.ZERO : bet.getAdjustQty());
                    out.setDistributionQty(bet.getDistributionQty() == null ? BigDecimal.ZERO : bet.getDistributionQty());
                    out.setProfitLossQty(bet.getProfitLossQty() == null ? BigDecimal.ZERO : bet.getProfitLossQty());
                    out.setQcQty(bet.getQcQty() == null ? BigDecimal.ZERO : bet.getQcQty());

                    out.setEndAmt(
                            out.getStartAmt() == null ? BigDecimal.ZERO : out.getStartAmt()
                                    .add(bet.getPurchaseAmount() == null ? BigDecimal.ZERO : bet.getPurchaseAmount())
                                    .add(bet.getReturnAmount() == null ? BigDecimal.ZERO : bet.getReturnAmount())
                                    .add(bet.getSaleAmount() == null ? BigDecimal.ZERO : bet.getSaleAmount())
                                    .add(bet.getAdjustAmt() == null ? BigDecimal.ZERO : bet.getAdjustAmt())
                                    .add(bet.getDistributionAmt() == null ? BigDecimal.ZERO : bet.getDistributionAmt())
                                    .add(bet.getProfitLossAmount() == null ? BigDecimal.ZERO : bet.getProfitLossAmount())
                                    .add(bet.getQcAmt() == null ? BigDecimal.ZERO : bet.getQcAmt())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                    out.setEndQty(
                            out.getStartQty() == null ? BigDecimal.ZERO : out.getStartQty()
                                    .add(bet.getPurchaseQty() == null ? BigDecimal.ZERO : bet.getPurchaseQty())
                                    .add(bet.getReturnQty() == null ? BigDecimal.ZERO : bet.getReturnQty())
                                    .add(bet.getSaleQty() == null ? BigDecimal.ZERO : bet.getSaleQty())
                                    .add(bet.getAdjustQty() == null ? BigDecimal.ZERO : bet.getAdjustQty())
                                    .add(bet.getDistributionQty() == null ? BigDecimal.ZERO : bet.getDistributionQty())
                                    .add(bet.getProfitLossQty() == null ? BigDecimal.ZERO : bet.getProfitLossQty())
                                    .add(bet.getQcQty() == null ? BigDecimal.ZERO : bet.getQcQty())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP));
                    out.setUnitPrice(bet.getUnitPrice() == null ? BigDecimal.ZERO : out.getUnitPrice());
                    out.setSaleAmt(out.getSaleQty().multiply(out.getUnitPrice()));
                }
            }
        }
        //没有期初，但是有期间
        List<String> proCode = startData.stream().map(InventoryChangeSummaryWithStoreOutData::getProCode).collect(Collectors.toList());
        distributionAndAdjustAndSaleData.removeIf(bet -> {
            return proCode.contains(bet.getProCode());
        });
        for (InventoryChangeSummaryWithStoreOutData bet : distributionAndAdjustAndSaleData) {
            bet.setStartAmt(BigDecimal.ZERO);
            bet.setStartQty(BigDecimal.ZERO);
            bet.setEndAmt(
                    BigDecimal.ZERO
                            .add(bet.getPurchaseAmount() == null ? BigDecimal.ZERO : bet.getPurchaseAmount())
                            .add(bet.getReturnAmount() == null ? BigDecimal.ZERO : bet.getReturnAmount())
                            .add(bet.getSaleAmount() == null ? BigDecimal.ZERO : bet.getSaleAmount())
                            .add(bet.getAdjustAmt() == null ? BigDecimal.ZERO : bet.getAdjustAmt())
                            .add(bet.getDistributionAmt() == null ? BigDecimal.ZERO : bet.getDistributionAmt())
                            .add(bet.getProfitLossAmount() == null ? BigDecimal.ZERO : bet.getProfitLossAmount())
                            .add(bet.getQcAmt() == null ? BigDecimal.ZERO : bet.getQcAmt())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
            bet.setEndQty(
                    BigDecimal.ZERO
                            .add(bet.getPurchaseQty() == null ? BigDecimal.ZERO : bet.getPurchaseQty())
                            .add(bet.getReturnQty() == null ? BigDecimal.ZERO : bet.getReturnQty())
                            .add(bet.getSaleQty() == null ? BigDecimal.ZERO : bet.getSaleQty())
                            .add(bet.getAdjustQty() == null ? BigDecimal.ZERO : bet.getAdjustQty())
                            .add(bet.getDistributionQty() == null ? BigDecimal.ZERO : bet.getDistributionQty())
                            .add(bet.getProfitLossQty() == null ? BigDecimal.ZERO : bet.getProfitLossQty())
                            .add(bet.getQcQty() == null ? BigDecimal.ZERO : bet.getQcQty())
                            .setScale(2, BigDecimal.ROUND_HALF_UP));
            bet.setUnitPrice(bet.getUnitPrice() == null ? BigDecimal.ZERO : bet.getUnitPrice());
            bet.setSaleAmt(bet.getSaleQty().multiply(bet.getUnitPrice()));
        }
        startData.addAll(distributionAndAdjustAndSaleData);
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(startData)) {
            // 集合列的数据汇总
            InventoryChangeSummaryWithStoreOutTotal outTotal = new InventoryChangeSummaryWithStoreOutTotal();
            //计算合计
            outTotal = computeInventoryChangeSummaryWithStoreTotal(startData, outTotal);
            pageInfo = new PageInfo(startData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    private String getCurrentClientQcDate(String client) {
        return this.materialDocMapper.getQcDate(client);
    }




    private List<InventoryChangeSummaryWithStoreOutData> getStartDataWithStore(InventoryChangeSummaryInData inData) {

        //门店按月同步 仓库按天同步
        List<InventoryChangeSummaryWithStoreOutData> stoQcData = new ArrayList<>(); //门店期初数据
        if (StrUtil.isEmpty(inData.getSiteCode()) && ArrayUtil.isNotEmpty(inData.getSiteArr())) {
            //门店 (期初:物料表的期初加上上个月的库存加上这个月一号到开始日期的期间 合计等于期初 )
            //1.先获取用户输入的开始日期的上一个月的库存数据
            stoQcData = this.materialDocMapper.getStartDataByStoWithStore(inData);
            InventoryChangeSummaryInData stoInData = new InventoryChangeSummaryInData();
            //2.计算剩余的日期的中间发生额
            List<InventoryChangeSummaryWithStoreOutData> restStoData = new ArrayList<>();
            BeanUtil.copyProperties(inData, stoInData);
            //剩余日期开始日期: 开始日期对应月份的一号
            String startDate = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.beginOfMonth(cn.hutool.core.date.DateUtil.parse(inData.getStartDate(), "yyyyMMdd").toCalendar()).getTime(), "yyyyMMdd");
            //3.剩余日期的发生额加上月末库存等于期初
            if (!startDate.equals(inData.getStartDate())) { //如果剩余日期的开始日期就是1号那么就没有剩余日期
                stoInData.setStartDate(startDate);
                //结束日期 : 开始日期的前一天
                stoInData.setEndDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.parse(inData.getStartDate(), "yyyyMMdd"), -1), "yyyyMMdd"));
                restStoData = this.materialDocMapper.selectInventoryChangeSummaryWithStore(stoInData);
                stoQcData = mergeLastMonthDataAndRestDataWithStore(stoQcData, restStoData);
            }
            for (InventoryChangeSummaryWithStoreOutData startData : stoQcData) {
                startData.setEndAmt(startData.getStartAmt() == null ? BigDecimal.ZERO : startData.getStartAmt());
                startData.setEndQty(startData.getStartQty() == null ? BigDecimal.ZERO : startData.getStartQty());
                startData.setSaleQty(startData.getSaleQty() == null ? BigDecimal.ZERO : startData.getSaleQty());
                startData.setUnitPrice(startData.getUnitPrice() == null ? BigDecimal.ZERO : startData.getUnitPrice());
                startData.setSaleAmt(startData.getSaleQty().multiply(startData.getUnitPrice()));
            }
        }
        return stoQcData;
    }

    private InventoryChangeSummaryOutTotal computeInventoryChangeSummaryTotal(List<InventoryChangeSummaryOutData> outData, InventoryChangeSummaryOutTotal outTotal) {
        if (ObjectUtil.isNull(outTotal)) {
            outTotal = new InventoryChangeSummaryOutTotal();
        }
        BigDecimal startAmt = BigDecimal.ZERO;
        BigDecimal startQty = BigDecimal.ZERO;
        BigDecimal endAmt = BigDecimal.ZERO;
        BigDecimal endQty = BigDecimal.ZERO;
        BigDecimal adjustAmt = BigDecimal.ZERO;
        BigDecimal adjustQty = BigDecimal.ZERO;
        BigDecimal distributionAmt = BigDecimal.ZERO;
        BigDecimal distributionQty = BigDecimal.ZERO;
        BigDecimal saleAmount = BigDecimal.ZERO;
        BigDecimal saleQty = BigDecimal.ZERO;
        BigDecimal qcAmt = BigDecimal.ZERO;
        BigDecimal qcQty = BigDecimal.ZERO;
        BigDecimal lossAmt = BigDecimal.ZERO;
        BigDecimal lossQty = BigDecimal.ZERO;
        BigDecimal purchaseAmt = BigDecimal.ZERO;
        BigDecimal purchaseQty = BigDecimal.ZERO;
        BigDecimal returnAmt = BigDecimal.ZERO;
        BigDecimal returnQty = BigDecimal.ZERO;
        startAmt = outData.stream().map(InventoryChangeSummaryOutData::getStartAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        startQty = outData.stream().map(InventoryChangeSummaryOutData::getStartQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        adjustAmt = outData.stream().map(InventoryChangeSummaryOutData::getAdjustAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        adjustQty = outData.stream().map(InventoryChangeSummaryOutData::getAdjustQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        distributionAmt = outData.stream().map(InventoryChangeSummaryOutData::getDistributionAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        distributionQty = outData.stream().map(InventoryChangeSummaryOutData::getDistributionQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        saleAmount = outData.stream().map(InventoryChangeSummaryOutData::getSaleAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        saleQty = outData.stream().map(InventoryChangeSummaryOutData::getSaleQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        qcAmt = outData.stream().map(InventoryChangeSummaryOutData::getQcAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        qcQty = outData.stream().map(InventoryChangeSummaryOutData::getQcQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        lossAmt = outData.stream().map(InventoryChangeSummaryOutData::getProfitLossAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        lossQty = outData.stream().map(InventoryChangeSummaryOutData::getProfitLossQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        purchaseAmt = outData.stream().map(InventoryChangeSummaryOutData::getPurchaseAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        purchaseQty = outData.stream().map(InventoryChangeSummaryOutData::getPurchaseQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        returnAmt = outData.stream().map(InventoryChangeSummaryOutData::getReturnAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        returnQty = outData.stream().map(InventoryChangeSummaryOutData::getReturnQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        endAmt = startAmt
                .add(qcAmt)
                .add(adjustAmt)
                .add(distributionAmt)
                .add(saleAmount)
                .add(lossAmt)
                .add(returnAmt)
                .add(purchaseAmt)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        endQty = startQty
                .add(qcQty)
                .add(adjustQty)
                .add(distributionQty)
                .add(saleQty)
                .add(lossQty)
                .add(returnQty)
                .add(purchaseQty)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        outTotal.setStartAmt(startAmt);
        outTotal.setStartQty(startQty);
        outTotal.setPurchaseAmount(purchaseAmt);
        outTotal.setPurchaseQty(purchaseQty);
        outTotal.setReturnAmount(returnAmt);
        outTotal.setReturnQty(returnQty);
        outTotal.setProfitLossAmount(lossAmt);
        outTotal.setProfitLossQty(lossQty);
        outTotal.setEndAmt(endAmt);
        outTotal.setEndQty(endQty);
        outTotal.setAdjustAmt(adjustAmt);
        outTotal.setAdjustQty(adjustQty);
        outTotal.setDistributionAmt(distributionAmt);
        outTotal.setDistributionQty(distributionQty);
        outTotal.setSaleAmount(saleAmount);
        outTotal.setSaleQty(saleQty);
        outTotal.setQcAmt(qcAmt);
        outTotal.setQcQty(qcQty);
        return outTotal;
    }

    private InventoryChangeSummaryWithStoreOutTotal computeInventoryChangeSummaryWithStoreTotal(List<InventoryChangeSummaryWithStoreOutData> outData, InventoryChangeSummaryWithStoreOutTotal outTotal) {
        if (ObjectUtil.isNull(outTotal)) {
            outTotal = new InventoryChangeSummaryWithStoreOutTotal();
        }
        BigDecimal startAmt = BigDecimal.ZERO;
        BigDecimal startQty = BigDecimal.ZERO;
        BigDecimal endAmt = BigDecimal.ZERO;
        BigDecimal endQty = BigDecimal.ZERO;
        BigDecimal adjustAmt = BigDecimal.ZERO;
        BigDecimal adjustQty = BigDecimal.ZERO;
        BigDecimal distributionAmt = BigDecimal.ZERO;
        BigDecimal distributionQty = BigDecimal.ZERO;
        BigDecimal saleAmount = BigDecimal.ZERO;
        BigDecimal saleQty = BigDecimal.ZERO;
        BigDecimal qcAmt = BigDecimal.ZERO;
        BigDecimal qcQty = BigDecimal.ZERO;
        BigDecimal lossAmt = BigDecimal.ZERO;
        BigDecimal lossQty = BigDecimal.ZERO;
        BigDecimal purchaseAmt = BigDecimal.ZERO;
        BigDecimal purchaseQty = BigDecimal.ZERO;
        BigDecimal returnAmt = BigDecimal.ZERO;
        BigDecimal returnQty = BigDecimal.ZERO;
        BigDecimal saleAmt = BigDecimal.ZERO;
        startAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getStartAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        startQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getStartQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        adjustAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getAdjustAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        adjustQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getAdjustQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        distributionAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getDistributionAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        distributionQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getDistributionQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        saleAmount = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getSaleAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        saleQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getSaleQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        qcAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getQcAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        qcQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getQcQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        lossAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getProfitLossAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        lossQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getProfitLossQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        purchaseAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getPurchaseAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        purchaseQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getPurchaseQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        returnAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getReturnAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        returnQty = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getReturnQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        saleAmt = outData.stream().map(InventoryChangeSummaryWithStoreOutData::getSaleAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);
        endAmt = startAmt
                .add(qcAmt)
                .add(adjustAmt)
                .add(distributionAmt)
                .add(saleAmount)
                .add(lossAmt)
                .add(returnAmt)
                .add(purchaseAmt)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        endQty = startQty
                .add(qcQty)
                .add(adjustQty)
                .add(distributionQty)
                .add(saleQty)
                .add(lossQty)
                .add(returnQty)
                .add(purchaseQty)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        outTotal.setStartAmt(startAmt);
        outTotal.setStartQty(startQty);
        outTotal.setPurchaseAmount(purchaseAmt);
        outTotal.setPurchaseQty(purchaseQty);
        outTotal.setReturnAmount(returnAmt);
        outTotal.setReturnQty(returnQty);
        outTotal.setProfitLossAmount(lossAmt);
        outTotal.setProfitLossQty(lossQty);
        outTotal.setEndAmt(endAmt);
        outTotal.setEndQty(endQty);
        outTotal.setAdjustAmt(adjustAmt);
        outTotal.setAdjustQty(adjustQty);
        outTotal.setDistributionAmt(distributionAmt);
        outTotal.setDistributionQty(distributionQty);
        outTotal.setSaleAmount(saleAmount);
        outTotal.setSaleQty(saleQty);
        outTotal.setQcAmt(qcAmt);
        outTotal.setQcQty(qcQty);
        outTotal.setSaleAmt(saleAmt);
        return outTotal;
    }

    /**
     * 查询期初数据
     *
     * @param inData
     * @return
     */
    private List<InventoryChangeSummaryOutData> getStartData(InventoryChangeSummaryInData inData) {

        //门店按月同步 仓库按天同步
        List<InventoryChangeSummaryOutData> stoQcData = new ArrayList<>(); //门店期初数据
        List<InventoryChangeSummaryOutData> dcQcData = new ArrayList<>();  //仓库期初数据
        List<InventoryChangeSummaryOutData> allData = new ArrayList<>();   //所有门店+仓库期初数据
        if (StrUtil.isEmpty(inData.getSiteCode()) && ArrayUtil.isNotEmpty(inData.getSiteArr())) {
            //门店 (期初:物料表的期初加上上个月的库存加上这个月一号到开始日期的期间 合计等于期初 )
            //1.先获取用户输入的开始日期的上一个月的库存数据
            stoQcData = this.materialDocMapper.getStartDataBySto(inData);
            InventoryChangeSummaryInData stoInData = new InventoryChangeSummaryInData();
            //2.计算剩余的日期的中间发生额
            List<InventoryChangeSummaryOutData> restStoData = new ArrayList<>();
            BeanUtil.copyProperties(inData, stoInData);
            //剩余日期开始日期: 开始日期对应月份的一号
            String startDate = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.beginOfMonth(cn.hutool.core.date.DateUtil.parse(inData.getStartDate(), "yyyyMMdd").toCalendar()).getTime(), "yyyyMMdd");
            //3.剩余日期的发生额加上月末库存等于期初
            if (!startDate.equals(inData.getStartDate())) { //如果剩余日期的开始日期就是1号那么就没有剩余日期
                stoInData.setStartDate(startDate);
                //结束日期 : 开始日期的前一天
                stoInData.setEndDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.parse(inData.getStartDate(), "yyyyMMdd"), -1), "yyyyMMdd"));
                restStoData = this.materialDocMapper.selectInventoryChangeSummary(stoInData);
                stoQcData = mergeLastMonthDataAndRestData(stoQcData, restStoData);
            }
            for (InventoryChangeSummaryOutData startData : stoQcData) {
                startData.setEndAmt(startData.getStartAmt() == null ? BigDecimal.ZERO : startData.getStartAmt());
                startData.setEndQty(startData.getStartQty() == null ? BigDecimal.ZERO : startData.getStartQty());
            }
        }
        //选仓库
        //2.直接取前一天的库存数据作为期初 (期初:物料表的期初加上仓库表的库存乘以单价)
        if (ArrayUtil.isEmpty(inData.getSiteArr()) && StrUtil.isNotEmpty(inData.getSiteCode())) {
            dcQcData = this.materialDocMapper.getStartDataByDc(inData);
            for (InventoryChangeSummaryOutData dcQcAmt : dcQcData) {
                dcQcAmt.setPurchaseQty(BigDecimal.ZERO);
                dcQcAmt.setReturnQty(BigDecimal.ZERO);
                dcQcAmt.setSaleQty(BigDecimal.ZERO);
                dcQcAmt.setProfitLossQty(BigDecimal.ZERO);
                dcQcAmt.setAdjustQty(BigDecimal.ZERO);
                dcQcAmt.setDistributionQty(BigDecimal.ZERO);
                dcQcAmt.setPurchaseAmount(BigDecimal.ZERO);
                dcQcAmt.setReturnAmount(BigDecimal.ZERO);
                dcQcAmt.setSaleAmount(BigDecimal.ZERO);
                dcQcAmt.setProfitLossAmount(BigDecimal.ZERO);
                dcQcAmt.setAdjustAmt(BigDecimal.ZERO);
                dcQcAmt.setDistributionAmt(BigDecimal.ZERO);
                dcQcAmt.setEndAmt(dcQcAmt.getStartAmt());
                dcQcAmt.setEndQty(dcQcAmt.getStartQty());
            }
        }
        //3.门店和仓库都不选的情况
        if (ArrayUtil.isEmpty(inData.getSiteArr()) && StrUtil.isEmpty(inData.getSiteCode())) {
            allData = this.materialDocMapper.getStartDataAll(inData);
            for (InventoryChangeSummaryOutData all : allData) {
                all.setPurchaseQty(BigDecimal.ZERO);
                all.setReturnQty(BigDecimal.ZERO);
                all.setSaleQty(BigDecimal.ZERO);
                all.setProfitLossQty(BigDecimal.ZERO);
                all.setAdjustQty(BigDecimal.ZERO);
                all.setDistributionQty(BigDecimal.ZERO);
                all.setPurchaseAmount(BigDecimal.ZERO);
                all.setReturnAmount(BigDecimal.ZERO);
                all.setSaleAmount(BigDecimal.ZERO);
                all.setProfitLossAmount(BigDecimal.ZERO);
                all.setAdjustAmt(BigDecimal.ZERO);
                all.setDistributionAmt(BigDecimal.ZERO);
                all.setEndAmt(all.getStartAmt());
                all.setEndQty(all.getStartQty());
            }
        }
        stoQcData.addAll(dcQcData);
        stoQcData.addAll(allData);
        return stoQcData;
    }

    private List<InventoryChangeSummaryWithStoreOutData> mergeLastMonthDataAndRestDataWithStore(List<InventoryChangeSummaryWithStoreOutData> startData, List<InventoryChangeSummaryWithStoreOutData> restData) {

        List<String> startCodeList = startData.stream().map(InventoryChangeSummaryWithStoreOutData::getProCode).collect(Collectors.toList());

        for (InventoryChangeSummaryWithStoreOutData lastMonth : startData) {
            for (InventoryChangeSummaryWithStoreOutData rest : restData) {
                if (lastMonth.getProCode().equals(rest.getProCode())) {
                    BeanUtil.copyProperties(rest, lastMonth, "startAmt", "startQty");
                }
            }
        }
        //如果没有上个月库存数据但是有月初到开始日期的剩余数据
        for (InventoryChangeSummaryWithStoreOutData rest : restData) {
            if (!startCodeList.contains(rest.getProCode())) {

                rest.setStartAmt(BigDecimal.ZERO
                        .add(rest.getQcAmt() == null ? BigDecimal.ZERO : rest.getQcAmt())
                        .add(rest.getPurchaseAmount() == null ? BigDecimal.ZERO : rest.getPurchaseAmount())
                        .add(rest.getReturnAmount() == null ? BigDecimal.ZERO : rest.getReturnAmount())
                        .add(rest.getSaleAmount() == null ? BigDecimal.ZERO : rest.getSaleAmount())
                        .add(rest.getAdjustAmt() == null ? BigDecimal.ZERO : rest.getAdjustAmt())
                        .add(rest.getDistributionAmt() == null ? BigDecimal.ZERO : rest.getDistributionAmt())
                        .add(rest.getProfitLossAmount() == null ? BigDecimal.ZERO : rest.getProfitLossAmount())
                );
                rest.setStartQty(BigDecimal.ZERO
                        .add(rest.getQcQty() == null ? BigDecimal.ZERO : rest.getQcQty())
                        .add(rest.getPurchaseQty() == null ? BigDecimal.ZERO : rest.getPurchaseQty())
                        .add(rest.getReturnQty() == null ? BigDecimal.ZERO : rest.getReturnQty())
                        .add(rest.getSaleQty() == null ? BigDecimal.ZERO : rest.getSaleQty())
                        .add(rest.getAdjustQty() == null ? BigDecimal.ZERO : rest.getAdjustQty())
                        .add(rest.getDistributionQty() == null ? BigDecimal.ZERO : rest.getDistributionQty())
                        .add(rest.getProfitLossQty() == null ? BigDecimal.ZERO : rest.getProfitLossQty())
                );
                startData.add(rest);
            }
        }
        return startData;
    }

    private List<InventoryChangeSummaryOutData> mergeLastMonthDataAndRestData(List<InventoryChangeSummaryOutData> startData, List<InventoryChangeSummaryOutData> restData) {

        List<String> startCodeList = startData.stream().map(InventoryChangeSummaryOutData::getProCode).collect(Collectors.toList());

        for (InventoryChangeSummaryOutData lastMonth : startData) {
            for (InventoryChangeSummaryOutData rest : restData) {
                if (lastMonth.getProCode().equals(rest.getProCode())) {
                    BeanUtil.copyProperties(rest, lastMonth, "startAmt", "startQty");
                }
            }
        }
        //如果没有上个月库存数据但是有月初到开始日期的剩余数据
        for (InventoryChangeSummaryOutData rest : restData) {
            if (!startCodeList.contains(rest.getProCode())) {

                rest.setStartAmt(BigDecimal.ZERO
                        .add(rest.getQcAmt() == null ? BigDecimal.ZERO : rest.getQcAmt())
                        .add(rest.getPurchaseAmount() == null ? BigDecimal.ZERO : rest.getPurchaseAmount())
                        .add(rest.getReturnAmount() == null ? BigDecimal.ZERO : rest.getReturnAmount())
                        .add(rest.getSaleAmount() == null ? BigDecimal.ZERO : rest.getSaleAmount())
                        .add(rest.getAdjustAmt() == null ? BigDecimal.ZERO : rest.getAdjustAmt())
                        .add(rest.getDistributionAmt() == null ? BigDecimal.ZERO : rest.getDistributionAmt())
                        .add(rest.getProfitLossAmount() == null ? BigDecimal.ZERO : rest.getProfitLossAmount())
                );
                rest.setStartQty(BigDecimal.ZERO
                        .add(rest.getQcQty() == null ? BigDecimal.ZERO : rest.getQcQty())
                        .add(rest.getPurchaseQty() == null ? BigDecimal.ZERO : rest.getPurchaseQty())
                        .add(rest.getReturnQty() == null ? BigDecimal.ZERO : rest.getReturnQty())
                        .add(rest.getSaleQty() == null ? BigDecimal.ZERO : rest.getSaleQty())
                        .add(rest.getAdjustQty() == null ? BigDecimal.ZERO : rest.getAdjustQty())
                        .add(rest.getDistributionQty() == null ? BigDecimal.ZERO : rest.getDistributionQty())
                        .add(rest.getProfitLossQty() == null ? BigDecimal.ZERO : rest.getProfitLossQty())
                );
                startData.add(rest);
            }
        }
        return startData;
    }


    /**
     * 门店用
     * 供应商商品销售合计
     * 陈老板和李老板设计要求的
     *
     * @param inData
     * @return
     */
    @Override
    public PageInfo selectProductSalesBySupplierByStore(ProductSalesBySupplierInData inData) {
        List<ProductSalesBySupplierOutData> productSupplierData = new ArrayList<>();
        PageInfo pageInfo;
//        BigDecimal ss = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        List<SalespersonsSalesDetailsOutData> saleList = gaiaSdSaleDMapper.getProductSalesBySupplier(inData);
        if (saleList.size() > 0) {
            productSupplierData = sdBatchChanegMapper.getProductSalesBySupplier(inData);
            if (productSupplierData.size() > 0) {
                for (SalespersonsSalesDetailsOutData saleSum : saleList) {
                    //根据商品编码汇总 获取该商品所有的销售数量
                    BigDecimal sumQty = CommonUtil.stripTrailingZeros(saleSum.getQty());
                    for (ProductSalesBySupplierOutData supplierOutData : productSupplierData) {
                        if (saleSum.getSelfCode().equals(supplierOutData.getProCode()) && saleSum.getStoCode().equals(supplierOutData.getStoCode())) {
                            //根据批次汇总 获取该商品所有的异动数量
                            // 批次==供应商
                            BigDecimal supQty = CommonUtil.stripTrailingZeros(supplierOutData.getQty());
                            //算出该加盟商销售数量占总销量的百分比
                            BigDecimal rateQty = BigDecimal.ONE;
                            if (sumQty.compareTo(BigDecimal.ZERO) != 0) {
                                rateQty = supQty.divide(sumQty, 10, BigDecimal.ROUND_HALF_EVEN);
                            }
                            //用百分比乘各个数据
                            supplierOutData.setAmountReceivable(CommonUtil.stripTrailingZeros(saleSum.getAmountReceivable()).multiply(rateQty));//应收金额
//                            ss = ss.add(supplierOutData.getAmountReceivable());
                            supplierOutData.setAmt(CommonUtil.stripTrailingZeros(saleSum.getAmt()).multiply(rateQty));//实收金额
                            supplierOutData.setIncludeTaxSale(CommonUtil.stripTrailingZeros(saleSum.getIncludeTaxSale()).multiply(rateQty));//成本额
                            supplierOutData.setGrossProfitMargin(CommonUtil.stripTrailingZeros(saleSum.getGrossProfitMargin()).multiply(rateQty));//毛利额
//                        try {
//                            System.out.println("异常商品:"+supplierOutData.getBrId()+","+supplierOutData.getProCode()+","+supplierOutData.getBatch()+","+supplierOutData.getAmt());
                            //在错误的情况下   实收金额可能是0  NND
                            if (supplierOutData.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                                supplierOutData.setGrossProfitRate(supplierOutData.getGrossProfitMargin().
                                        multiply(new BigDecimal(100)).
                                        divide(supplierOutData.getAmt(), 2, BigDecimal.ROUND_HALF_EVEN)
                                );//毛利率=毛利额/实收金额
                            } else {
                                supplierOutData.setGrossProfitRate(BigDecimal.ZERO);
                            }


                        }
                    }
                }
            } else {
                return new PageInfo();
            }
        } else {
            return new PageInfo();
        }
//        System.out.println(ss);
        ProductSalesBySupplierOutTotal supplierOutTotal = new ProductSalesBySupplierOutTotal();
        // 集合列的数据汇总
        for (ProductSalesBySupplierOutData supplierOutData : productSupplierData) {
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

        pageInfo = new PageInfo(productSupplierData, supplierOutTotal);
        return pageInfo;
    }

    /**
     * 供应商用
     * 供应商商品销售合计
     * 陈老板和李老板设计要求的
     *
     * @param inData
     * @return
     */
    @Override
    public PageInfo selectProductSalesBySupplier(ProductSalesBySupplierInData inData) {
        List<ProductSalesBySupplierOutData> productSupplierData = new ArrayList<>();
        PageInfo pageInfo;
//        BigDecimal ss = BigDecimal.ZERO;
        //添加验证，查询时间不能超过90天
        if(StrUtil.isBlank(inData.getStartDate()) || StrUtil.isBlank(inData.getEndDate())){
            throw new BusinessException(" 开始时间和结束时间不能为空！");
        }
        if(DateUtil.differentDaysByMillisecond(DateUtil.getFullDateStartDate(inData.getStartDate()),DateUtil.getFullDateEndDate(inData.getEndDate()))>90){
            throw new BusinessException(" 查询时间不能超过90天！");
        }
        List<SalespersonsSalesDetailsOutData> saleList = gaiaSdSaleDMapper.getProductSalesBySupplier(inData);
        if (saleList.size() > 0) {
            if(Objects.nonNull(inData.getPageSize()) && Objects.nonNull(inData.getPageNum())){
                PageHelper.startPage(inData.getPageNum(),inData.getPageSize());
            }
            productSupplierData = sdBatchChanegMapper.getProductSalesBySupplier(inData);
            if (productSupplierData.size() > 0) {
                for (SalespersonsSalesDetailsOutData saleSum : saleList) {
                    //根据商品编码汇总 获取该商品所有的销售数量
                    BigDecimal sumQty = CommonUtil.stripTrailingZeros(saleSum.getQty());
                    for (ProductSalesBySupplierOutData supplierOutData : productSupplierData) {
                        if (saleSum.getSelfCode().equals(supplierOutData.getProCode()) && saleSum.getStoCode().equals(supplierOutData.getStoCode())) {
                            //根据批次汇总 获取该商品所有的异动数量
                            // 批次==供应商
                            BigDecimal supQty = CommonUtil.stripTrailingZeros(supplierOutData.getQty());
                            //算出该加盟商销售数量占总销量的百分比
                            BigDecimal rateQty = BigDecimal.ONE;
                            if (sumQty.compareTo(BigDecimal.ZERO) != 0) {
                                rateQty = supQty.divide(sumQty, 10, BigDecimal.ROUND_HALF_EVEN);
                            }
                            //用百分比乘各个数据
                            supplierOutData.setAmountReceivable(CommonUtil.stripTrailingZeros(saleSum.getAmountReceivable()).multiply(rateQty));//应收金额
//                            ss = ss.add(supplierOutData.getAmountReceivable());
                            supplierOutData.setAmt(CommonUtil.stripTrailingZeros(saleSum.getAmt()).multiply(rateQty));//实收金额
                            supplierOutData.setIncludeTaxSale(CommonUtil.stripTrailingZeros(saleSum.getIncludeTaxSale()).multiply(rateQty));//成本额
                            supplierOutData.setGrossProfitMargin(CommonUtil.stripTrailingZeros(saleSum.getGrossProfitMargin()).multiply(rateQty));//毛利额
//                        try {
//                            System.out.println("异常商品:"+supplierOutData.getBrId()+","+supplierOutData.getProCode()+","+supplierOutData.getBatch()+","+supplierOutData.getAmt());
                            //在错误的情况下   实收金额可能是0  NND
                            if (supplierOutData.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                                supplierOutData.setGrossProfitRate(supplierOutData.getGrossProfitMargin().
                                        multiply(new BigDecimal(100)).
                                        divide(supplierOutData.getAmt(), 2, BigDecimal.ROUND_HALF_EVEN)
                                );//毛利率=毛利额/实收金额
                            } else {
                                supplierOutData.setGrossProfitRate(BigDecimal.ZERO);
                            }


                        }
                    }
                }
            } else {
                return new PageInfo();
            }
        } else {
            return new PageInfo();
        }
//        System.out.println(ss);
        ProductSalesBySupplierOutTotal supplierOutTotal = new ProductSalesBySupplierOutTotal();
        // 集合列的数据汇总
        for (ProductSalesBySupplierOutData supplierOutData : productSupplierData) {
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

        pageInfo = new PageInfo(productSupplierData, supplierOutTotal);
        return pageInfo;
    }

    @Override
    public PageInfo selectProductSaleByClient(StoreProductSaleClientInData inData) {
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
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
            }
            inData.setStoGssgTypes(stoGssgTypes);
        }
        if (inData.getStoAttribute() != null) {
            inData.setStoAttributes(Arrays.asList(inData.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfMedical() != null) {
            inData.setStoIfMedicals(Arrays.asList(inData.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (inData.getStoTaxClass() != null) {
            inData.setStoTaxClasss(Arrays.asList(inData.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfDtp() != null) {
            inData.setStoIfDtps(Arrays.asList(inData.getStoIfDtp().split(StrUtil.COMMA)));
        }
        List<StoreProductSaleClientOutData> outData = this.saleDMapper.selectProductSaleByClient(inData);
        //删除过滤数据
        if(inData.getCountType()!=null && inData.getCountType() == 1){
            if(inData.getCountStart()!= null){
                outData.removeIf(bet -> {
                    return ((bet.getSumStock() ==null ?0:bet.getSumStock().doubleValue())<inData.getCountStart());
                });
            }
            if(inData.getCountEnd()!= null){
                outData.removeIf(bet -> {
                    return ((bet.getSumStock() ==null ?0:bet.getSumStock().doubleValue())>inData.getCountEnd());
                });
            }
        }else if(inData.getCountType()!=null && inData.getCountType() == 2){
            if(inData.getCountStart()!= null){
                outData.removeIf(bet -> {
                    return ((bet.getStoQty() ==null ?0:bet.getStoQty().doubleValue())<inData.getCountStart());
                });
            }
            if(inData.getCountEnd()!= null){
                outData.removeIf(bet -> {
                    return ((bet.getStoQty() ==null ?0:bet.getStoQty().doubleValue())>inData.getCountEnd());
                });
            }
        }else if(inData.getCountType()!=null && inData.getCountType() == 3){
            if(inData.getCountStart()!= null){
                outData.removeIf(bet -> {
                    return ((bet.getDcQty() ==null ?0:bet.getDcQty().doubleValue())<inData.getCountStart());
                });
            }
            if(inData.getCountEnd()!= null){
                outData.removeIf(bet -> {
                    return ((bet.getDcQty() ==null ?0:bet.getDcQty().doubleValue())>inData.getCountEnd());
                });
            }
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {// 集合列的数据汇总
//            StoreProductSaleClientOutTotal outTotal = this.saleDMapper.selectProductSaleByClientTatol(inData);
            StoreProductSaleClientOutTotal outTotal = new StoreProductSaleClientOutTotal();
            BigDecimal dcTotal = BigDecimal.ZERO;
            BigDecimal stoTotal = BigDecimal.ZERO;
            BigDecimal sumStock = BigDecimal.ZERO;
            BigDecimal onlineSaleQty = BigDecimal.ZERO;
            BigDecimal onlineAmt = BigDecimal.ZERO;
            BigDecimal onlineGrossAmt = BigDecimal.ZERO;
            BigDecimal onlineMov = BigDecimal.ZERO;
//            String onlineGrossRate = "0.00%";
            BigDecimal qty = BigDecimal.ZERO;
            BigDecimal amountReceivable = BigDecimal.ZERO;
            BigDecimal amt = BigDecimal.ZERO;
            BigDecimal deduction = BigDecimal.ZERO;
            BigDecimal grossProfitMargin = BigDecimal.ZERO;
            BigDecimal addGrossProfitMargin = BigDecimal.ZERO;
            for(StoreProductSaleClientOutData data : outData){
                qty = qty.add(Objects.nonNull(data.getQty()) ? data.getQty() : BigDecimal.ZERO);
                amountReceivable = amountReceivable.add(Objects.nonNull(data.getAmountReceivable()) ? data.getAmountReceivable() : BigDecimal.ZERO);
                amt = amt.add(Objects.nonNull(data.getAmt()) ? data.getAmt() : BigDecimal.ZERO);
                deduction = deduction.add(Objects.nonNull(data.getDeduction()) ? data.getDeduction() : BigDecimal.ZERO);
                grossProfitMargin = grossProfitMargin.add(Objects.nonNull(data.getGrossProfitMargin()) ? data.getGrossProfitMargin() : BigDecimal.ZERO);
                addGrossProfitMargin = addGrossProfitMargin.add(Objects.nonNull(data.getAddGrossProfitMargin()) ? data.getAddGrossProfitMargin() : BigDecimal.ZERO);
                onlineSaleQty = onlineSaleQty.add(Objects.nonNull(data.getOnlineSaleQty()) ? data.getOnlineSaleQty() : BigDecimal.ZERO);
                onlineAmt = onlineAmt.add(Objects.nonNull(data.getOnlineAmt()) ? data.getOnlineAmt() : BigDecimal.ZERO);
                onlineGrossAmt = onlineGrossAmt.add(Objects.nonNull(data.getOnlineGrossAmt()) ? data.getOnlineGrossAmt() : BigDecimal.ZERO);
                onlineMov = onlineMov.add(Objects.nonNull(data.getOnlineMov()) ? data.getOnlineMov() : BigDecimal.ZERO);
                dcTotal = dcTotal.add(Objects.nonNull(data.getDcQty()) ? data.getDcQty() : BigDecimal.ZERO);
                stoTotal = stoTotal.add(Objects.nonNull(data.getStoQty()) ? data.getStoQty() : BigDecimal.ZERO);
                sumStock = sumStock.add(Objects.nonNull(data.getSumStock()) ? data.getSumStock() : BigDecimal.ZERO);
            }

//            onlineSaleQty = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineSaleQty() == null ? BigDecimal.ZERO : out.getOnlineSaleQty()));
//            onlineAmt = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineAmt() == null ? BigDecimal.ZERO : out.getOnlineAmt()));
//            onlineGrossAmt = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineGrossAmt() == null ? BigDecimal.ZERO : out.getOnlineGrossAmt()));
//            onlineMov = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineMov() == null ? BigDecimal.ZERO : out.getOnlineMov()));
//            onlineGrossRate = onlineGrossAmt.divide(onlineAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : onlineAmt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
//            for (StoreProductSaleClientOutData out : outData) {
//                if (out.getDcQty() != null) {
//                    dcTotal = dcTotal.add(new BigDecimal(out.getDcQty()));
//                    sumStock = sumStock.add(new BigDecimal(out.getDcQty()));
//                }
//                if (out.getStoQty() != null) {
//                    stoTotal = stoTotal.add(new BigDecimal(out.getStoQty()));
//                    sumStock = sumStock.add(new BigDecimal(out.getStoQty()));
//                }
//            }
            outTotal.setQty(qty);
            outTotal.setAmountReceivable(amountReceivable);
            outTotal.setAmt(amt);
            outTotal.setDeduction(deduction);
            outTotal.setGrossProfitMargin(grossProfitMargin);
            outTotal.setGrossProfitRate(grossProfitMargin.divide(amt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : amt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            outTotal.setAddGrossProfitMargin(addGrossProfitMargin);
            outTotal.setAddGrossProfitRate(addGrossProfitMargin.divide(amt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : amt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            outTotal.setAveragePrice(amt.divide(qty, 4, BigDecimal.ROUND_HALF_UP));
            outTotal.setDcQty(dcTotal);
            outTotal.setStoQty(stoTotal);
            outTotal.setSumStock(sumStock);
            outTotal.setOnlineSaleQty(onlineSaleQty);
            outTotal.setOnlineAmt(onlineAmt);
            outTotal.setOnlineGrossAmt(onlineGrossAmt);
            outTotal.setOnlineMov(onlineMov);
            outTotal.setOnlineGrossRate(onlineGrossAmt.divide(onlineAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : onlineAmt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectProductSaleByStore(StoreProductSaleStoreInData inData) {
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
        List<StoreProductSaleStoreOutData> outData;
        StoreProductSaleStoreOutTotal outTotal = new StoreProductSaleStoreOutTotal();
        //选择商品的时候要把没有销售的门店也带出来
        if (ObjectUtil.isEmpty(inData.getProArr())) {
            outData = this.saleDMapper.selectProductSaleByStore(inData);
//            outTotal = this.saleDMapper.selectProductSaleByStoreTatol(inData);
        } else {
            outData = this.saleDMapper.selectProductSaleByProAllStore(inData);
//            outTotal = this.saleDMapper.selectProductSaleByProAllStoreTatol(inData);
        }
        if(inData.getCountStart()!= null){
            outData.removeIf(bet -> {
                return ((bet.getSumStock() ==null ?0:bet.getSumStock().doubleValue())<inData.getCountStart());
            });
        }
        if(inData.getCountEnd()!= null){
            outData.removeIf(bet -> {
                return ((bet.getSumStock() ==null ?0:bet.getSumStock().doubleValue())>inData.getCountEnd());
            });
        }
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClient());
        for (StoreProductSaleStoreOutData saleStoreOutData : outData) {
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
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType()) && storeCategoryType.getGssgId().equals(inData.getStoGssgTypes().get(0).getGssgId())) {
                            if(tmpStoGssgTypeSet.contains(storeCategoryType.getGssgIdName())){
                                saleStoreOutData.setManagementArea(storeCategoryType.getGssgIdName());
                                tmpStoGssgTypeSet.remove("DX0004");
                            }
                        }
                    }
                }
            }
        }

//        onlineSaleQty = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineSaleQty() == null ? BigDecimal.ZERO : out.getOnlineSaleQty()));
//        onlineAmt = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineAmt() == null ? BigDecimal.ZERO : out.getOnlineAmt()));
//        onlineGrossAmt = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineGrossAmt() == null ? BigDecimal.ZERO : out.getOnlineGrossAmt()));
//        onlineMov = outData.stream().collect(CollectorsUtil.summingBigDecimal(out -> out.getOnlineMov() == null ? BigDecimal.ZERO : out.getOnlineMov()));
//        onlineGrossRate = onlineGrossAmt.divide(onlineAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : onlineAmt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
//        if (ObjectUtil.isNotNull(outTotal)) {
//            outTotal.setOnlineSaleQty(onlineSaleQty);
//            outTotal.setOnlineAmt(onlineAmt);
//            outTotal.setOnlineGrossAmt(onlineGrossAmt);
//            outTotal.setOnlineMov(onlineMov);
//            outTotal.setOnlineGrossRate(onlineGrossRate);
//            outTotal.setSumStock(sumStock);
//        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {// 集合列的数据汇总
//            StoreProductSaleClientOutTotal outTotal = this.saleDMapper.selectProductSaleByClientTatol(inData);
            BigDecimal sumStock = BigDecimal.ZERO;
            BigDecimal onlineSaleQty = BigDecimal.ZERO;
            BigDecimal onlineAmt = BigDecimal.ZERO;
            BigDecimal onlineGrossAmt = BigDecimal.ZERO;
            BigDecimal onlineMov = BigDecimal.ZERO;
//            String onlineGrossRate = "0.00%";
            BigDecimal qty = BigDecimal.ZERO;
            BigDecimal amountReceivable = BigDecimal.ZERO;
            BigDecimal amt = BigDecimal.ZERO;
            BigDecimal deduction = BigDecimal.ZERO;
            BigDecimal grossProfitMargin = BigDecimal.ZERO;
            BigDecimal addGrossProfitMargin = BigDecimal.ZERO;
            for(StoreProductSaleStoreOutData data : outData){
                qty = qty.add(Objects.nonNull(data.getQty()) ? data.getQty() : BigDecimal.ZERO);
                amountReceivable = amountReceivable.add(Objects.nonNull(data.getAmountReceivable()) ? data.getAmountReceivable() : BigDecimal.ZERO);
                amt = amt.add(Objects.nonNull(data.getAmt()) ? data.getAmt() : BigDecimal.ZERO);
                deduction = deduction.add(Objects.nonNull(data.getDeduction()) ? data.getDeduction() : BigDecimal.ZERO);
                grossProfitMargin = grossProfitMargin.add(Objects.nonNull(data.getGrossProfitMargin()) ? data.getGrossProfitMargin() : BigDecimal.ZERO);
                addGrossProfitMargin = addGrossProfitMargin.add(Objects.nonNull(data.getAddGrossProfitMargin()) ? data.getAddGrossProfitMargin() : BigDecimal.ZERO);
                onlineSaleQty = onlineSaleQty.add(Objects.nonNull(data.getOnlineSaleQty()) ? data.getOnlineSaleQty() : BigDecimal.ZERO);
                onlineAmt = onlineAmt.add(Objects.nonNull(data.getOnlineAmt()) ? data.getOnlineAmt() : BigDecimal.ZERO);
                onlineGrossAmt = onlineGrossAmt.add(Objects.nonNull(data.getOnlineGrossAmt()) ? data.getOnlineGrossAmt() : BigDecimal.ZERO);
                onlineMov = onlineMov.add(Objects.nonNull(data.getOnlineMov()) ? data.getOnlineMov() : BigDecimal.ZERO);
                sumStock = sumStock.add(Objects.nonNull(data.getSumStock()) ? data.getSumStock() : BigDecimal.ZERO);
            }
            outTotal.setQty(qty);
            outTotal.setAmountReceivable(amountReceivable);
            outTotal.setAmt(amt);
            outTotal.setDeduction(deduction);
            outTotal.setGrossProfitMargin(grossProfitMargin);
            outTotal.setGrossProfitRate(grossProfitMargin.divide(amt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : amt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            outTotal.setAddGrossProfitMargin(addGrossProfitMargin);
            outTotal.setAddGrossProfitRate(addGrossProfitMargin.divide(amt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : amt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            outTotal.setAveragePrice(amt.divide(qty, 4, BigDecimal.ROUND_HALF_UP));
            outTotal.setSumStock(sumStock);
            outTotal.setOnlineSaleQty(onlineSaleQty);
            outTotal.setOnlineAmt(onlineAmt);
            outTotal.setOnlineGrossAmt(onlineGrossAmt);
            outTotal.setOnlineMov(onlineMov);
            outTotal.setOnlineGrossRate(onlineGrossAmt.divide(onlineAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : onlineAmt, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            pageInfo = new PageInfo(outData, outTotal);
        }else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public Boolean updateStoreSaleInvoiced(List<UpdateInvoicedInData> data) {
        return this.saleHMapper.updateInvoicedByBillNo(data) == 1;
    }

    @Override
    public PageInfo selectSupplierSummaryDealPage(SupplierDealInData data) {
        if (ObjectUtil.isNotNull(data.getPageNum()) && ObjectUtil.isNotNull(data.getPageSize())) {
            PageHelper.startPage(data.getPageNum(), data.getPageSize());
        }
        if (ObjectUtil.isEmpty(data.getStartDate()) || ObjectUtil.isEmpty(data.getEndDate())) {
            throw new BusinessException("日期不能为空");
        }
        List<SupplierDealOutData> outData = this.materialDocMapper.selectSupplierSummaryDealPage(data);
        if (StrUtil.isNotBlank(data.getStoCode())) {
            outData = outData.stream().filter(s -> s.getStoCode().contains(data.getStoCode())).collect(Collectors.toList());
        }
        List<SupplierDealOutData> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(outData)) {
            List<Map<String, Object>> selectSupplierName = this.materialDocMapper.selectSupplierName(data);
            if (StrUtil.isNotBlank(data.getPoSupplierSalesman())) {
                //查询业务员
                if (CollUtil.isNotEmpty(selectSupplierName)) {
                    for (Map<String, Object> map : selectSupplierName) {
                        for (SupplierDealOutData outDatum : outData) {
                            if (Objects.equals(Convert.toStr(map.get("client")), outDatum.getCLIENT()) && Objects.equals(Convert.toStr(map.get("supSite")), outDatum.getSiteCode()) && Objects.equals(Convert.toStr(map.get("supSelfCode")), outDatum.getSupCode()) && Objects.equals(Convert.toStr(map.get("gssCode")), outDatum.getPoSupplierSalesman())) {
                                outDatum.setPoSupplierSalesmanName(Convert.toStr(map.get("gssName")));
                                list.add(outDatum);
                            }
                        }
                    }
                }
            } else {
                if (CollUtil.isNotEmpty(selectSupplierName)) {
                    for (Map<String, Object> map : selectSupplierName) {
                        for (SupplierDealOutData outDatum : outData) {
                            if (Objects.equals(Convert.toStr(map.get("client")), outDatum.getCLIENT()) && Objects.equals(Convert.toStr(map.get("supSite")), outDatum.getSiteCode()) && Objects.equals(Convert.toStr(map.get("supSelfCode")), outDatum.getSupCode()) && Objects.equals(Convert.toStr(map.get("gssCode")), outDatum.getPoSupplierSalesman())) {
                                outDatum.setPoSupplierSalesmanName(Convert.toStr(map.get("gssName")));
                            }
                        }
                    }
                }
                list = outData;
            }
        }

        PageInfo pageInfo;

        if (ObjectUtil.isNotEmpty(list)) {
            //开单金额
            BigDecimal billingAmount = BigDecimal.ZERO;
            //税金
            BigDecimal rateBat = BigDecimal.ZERO;
            //含税成本额
            BigDecimal totalAmount = BigDecimal.ZERO;
            //去税成本额
            BigDecimal batAmt = BigDecimal.ZERO;

            for (SupplierDealOutData supplierDealOutData : list) {
                billingAmount = billingAmount.add(supplierDealOutData.getBillingAmount().setScale(4, BigDecimal.ROUND_HALF_UP));
                rateBat = rateBat.add(supplierDealOutData.getRateBat().setScale(4, BigDecimal.ROUND_HALF_UP));
                totalAmount = totalAmount.add(supplierDealOutData.getTotalAmount().setScale(4, BigDecimal.ROUND_HALF_UP));
                batAmt = batAmt.add(supplierDealOutData.getBatAmt().setScale(4, BigDecimal.ROUND_HALF_UP));

            }
            SupplierDealOutTotal outTotal = new SupplierDealOutTotal();
            outTotal.setBillingAmount(billingAmount);
            outTotal.setRateBat(rateBat);
            outTotal.setTotalAmount(totalAmount);
            outTotal.setBatAmt(batAmt);

            pageInfo = new PageInfo(list, outTotal);
        } else {
            pageInfo = new PageInfo(list);
        }

        return pageInfo;

    }
    @Override
    public PageInfo<WholesaleSaleOutData> selectdeviveryDatasPage(WholesaleSaleInData inData) {
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("单据号或起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getDnId()) && ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("单据号或结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getProCodes())) {
            inData.setProArr(inData.getProCodes().split("\\s+ |\\s+|,"));
        }
        if (CollUtil.isNotEmpty(inData.getMatTypes())) {
            List<String> matTypes = inData.getMatTypes();
            for (int i = 0; i < matTypes.size(); i++) {
                if (matTypes.get(i).equals("PD")) {
                    matTypes.add("PX");
                }
            }
        }else {
            inData.setMatTypes(new ArrayList<>(Arrays.asList("XD", "PD", "ND", "ED", "TD", "MD", "PX")));
        }
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        List<WholesaleSaleOutData> outData = this.materialDocMapper.selectdeviveryDatas(inData);

        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            if("0".equals(inData.getCondition())){
                for (WholesaleSaleOutData wod : outData) {
                    wod.setGwoOwnerSaleMan(null);
                    wod.setGwoOwnerSaleManUserId(null);
                    wod.setBuillingStaffCode(null);
                    wod.setBuillingStaffName(null);
                }
            }else if("1".equals(inData.getCondition())){
                for (WholesaleSaleOutData wod : outData) {
                    wod.setBuillingStaffCode(null);
                    wod.setBuillingStaffName(null);
                }
            }else if("2".equals(inData.getCondition())){
                for (WholesaleSaleOutData wod : outData) {
                    wod.setGwoOwnerSaleMan(null);
                    wod.setGwoOwnerSaleManUserId(null);
                }
            }
            WholesaleSaleOutTotal outNum = new WholesaleSaleOutTotal();
            for (WholesaleSaleOutData out : outData) {
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outNum.setBatAmt(CommonUtil.stripTrailingZeros(outNum.getBatAmt()).add(CommonUtil.stripTrailingZeros(out.getBatAmt())));
                outNum.setRateBat(CommonUtil.stripTrailingZeros(outNum.getRateBat()).add(CommonUtil.stripTrailingZeros(out.getRateBat())));
                outNum.setTotalAmount(CommonUtil.stripTrailingZeros(outNum.getTotalAmount()).add(CommonUtil.stripTrailingZeros(out.getTotalAmount())));
                outNum.setAddAmt(CommonUtil.stripTrailingZeros(outNum.getAddAmt()).add(CommonUtil.stripTrailingZeros(out.getAddAmt())));
                outNum.setAddTax(CommonUtil.stripTrailingZeros(outNum.getAddTax()).add(CommonUtil.stripTrailingZeros(out.getAddTax())));
                outNum.setAddtotalAmount(CommonUtil.stripTrailingZeros(outNum.getAddtotalAmount()).add(CommonUtil.stripTrailingZeros(out.getAddtotalAmount())));
                outNum.setMovAmt(CommonUtil.stripTrailingZeros(outNum.getMovAmt()).add(CommonUtil.stripTrailingZeros(out.getMovAmt())));
                outNum.setRateMov(CommonUtil.stripTrailingZeros(outNum.getRateMov()).add(CommonUtil.stripTrailingZeros(out.getRateMov())));
                outNum.setMovTotalAmount(CommonUtil.stripTrailingZeros(outNum.getMovTotalAmount()).add(CommonUtil.stripTrailingZeros(out.getMovTotalAmount())));

            }
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;

    }

    @Override
    public void deviveryDataCollectSelectExport(HttpServletResponse response, WholesaleSaleInData inData) {
        PageInfo pageInfo = this.selectdeviveryDatasPage(inData);
        List<WholesaleSaleOutData> outDataList = pageInfo.getList();
        WholesaleSaleOutTotal total = (WholesaleSaleOutTotal) pageInfo.getListNum();
        if (CollUtil.isEmpty(outDataList)) {
            throw new BusinessException("导出数据不能为空");
        }
        List<WholesaleSaleExportOutData> dataList = outDataList.stream().map(outData -> {
            WholesaleSaleExportOutData exportOutData = new WholesaleSaleExportOutData();
            BeanUtil.copyProperties(outData, exportOutData);
            if (StringUtils.isNotEmpty(outData.getGwoOwnerSaleManUserId())) {
                exportOutData.setGwoOwnerSaleMan(outData.getGwoOwnerSaleManUserId()+"-"+outData.getGwoOwnerSaleMan());
            }else{
                exportOutData.setGwoOwnerSaleMan("");
            }
            if (StringUtils.isNotEmpty(outData.getBuillingStaffCode())) {
                exportOutData.setBuillingStaffName(outData.getBuillingStaffCode()+"-"+outData.getBuillingStaffName());
            }else{
                exportOutData.setBuillingStaffName("");
            }
            exportOutData.setMovAmt(outData.getMovAmt() == null?BigDecimal.ZERO.setScale(2):outData.getMovAmt().setScale(2, RoundingMode.HALF_UP));
            exportOutData.setRateMov(outData.getRateMov() == null?BigDecimal.ZERO.setScale(2):outData.getRateMov().setScale(2, RoundingMode.HALF_UP));
            exportOutData.setMovTotalAmount(outData.getMovTotalAmount() == null?BigDecimal.ZERO.setScale(2):outData.getMovTotalAmount().setScale(2, RoundingMode.HALF_UP));
            exportOutData.setAddAmt(outData.getAddAmt() == null?BigDecimal.ZERO.setScale(2):outData.getAddAmt().setScale(2, RoundingMode.HALF_UP));
            exportOutData.setAddTax(outData.getAddTax() == null?BigDecimal.ZERO.setScale(2):outData.getAddTax().setScale(2,RoundingMode.HALF_UP));
            exportOutData.setAddtotalAmount(outData.getAddtotalAmount() == null?BigDecimal.ZERO.setScale(2):outData.getAddtotalAmount().setScale(2,RoundingMode.HALF_UP));
            return exportOutData;
        }).collect(Collectors.toList());
        WholesaleSaleExportOutData totalData = new WholesaleSaleExportOutData();
        BeanUtil.copyProperties(total, totalData);
        totalData.setMovAmt(totalData.getMovAmt() == null?BigDecimal.ZERO.setScale(2):totalData.getMovAmt().setScale(2, RoundingMode.HALF_UP));
        totalData.setRateMov(totalData.getRateMov() == null?BigDecimal.ZERO.setScale(2):totalData.getRateMov().setScale(2, RoundingMode.HALF_UP));
        totalData.setMovTotalAmount(totalData.getMovTotalAmount() == null?BigDecimal.ZERO.setScale(2):totalData.getMovTotalAmount().setScale(2, RoundingMode.HALF_UP));
        totalData.setAddAmt(totalData.getAddAmt() == null?BigDecimal.ZERO.setScale(2):totalData.getAddAmt().setScale(2, RoundingMode.HALF_UP));
        totalData.setAddTax(totalData.getAddTax() == null?BigDecimal.ZERO.setScale(2):totalData.getAddTax().setScale(2,RoundingMode.HALF_UP));
        totalData.setAddtotalAmount(totalData.getAddtotalAmount() == null?BigDecimal.ZERO.setScale(2):totalData.getAddtotalAmount().setScale(2,RoundingMode.HALF_UP));
        dataList.add(totalData);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "配送数据汇总-导出";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try {
            EasyExcel.write(response.getOutputStream(),WholesaleSaleExportOutData.class)
                    .registerWriteHandler(ExcelStyleUtil.getStyle())
                    .sheet(0).doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectWholesaleSaleDetailPageExport(HttpServletResponse response, WholesaleSaleInData data) {
        PageInfo pageInfo = this.selectWholesaleSaleDetailPage(data);
        List<WholesaleSaleOutData> outDataList = pageInfo.getList();
        WholesaleSaleOutTotal total = (WholesaleSaleOutTotal) pageInfo.getListNum();
        if (CollUtil.isEmpty(outDataList)) {
            throw new BusinessException("导出数据不能为空");
        }
        List<WholesaleSaleDetailExportOutData> dataList = outDataList.stream().map(outData -> {
            WholesaleSaleDetailExportOutData exportOutData = new WholesaleSaleDetailExportOutData();
            BeanUtil.copyProperties(outData, exportOutData);
            if (StrUtil.isNotEmpty(outData.getGwoOwnerSaleMan())) {
                exportOutData.setGwoOwnerSaleMan(outData.getGwoOwnerSaleManUserId()+"-"+outData.getGwoOwnerSaleMan());
            }
            exportOutData.setAddAmt(outData.getAddAmt() != null ? outData.getAddAmt().setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            exportOutData.setAddTax(outData.getAddTax() != null ? outData.getAddTax().setScale(4, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            exportOutData.setAddtotalAmount(outData.getAddtotalAmount() != null ? outData.getAddtotalAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            return exportOutData;
        }).collect(Collectors.toList());
        WholesaleSaleDetailExportOutData totalData = new WholesaleSaleDetailExportOutData();
        BeanUtil.copyProperties(total, totalData);
        dataList.add(totalData);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "配送单据明细导出";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        try {
            EasyExcel.write(response.getOutputStream(), WholesaleSaleDetailExportOutData.class)
                    .registerWriteHandler(ExcelStyleUtil.getStyle())
                    .sheet(0).doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectWholesaleSalePageExport(HttpServletResponse response, WholesaleSaleInData data) {
        PageInfo pageInfo = this.selectWholesaleSalePage(data);
        List<WholesaleSaleOutData> outDataList = pageInfo.getList();
        WholesaleSaleOutTotal total = (WholesaleSaleOutTotal) pageInfo.getListNum();
        if (CollUtil.isEmpty(outDataList)) {
            throw new BusinessException("导出数据不能为空");
        }
        List<WholesaleSaleSummaryExportOutData> dataList = outDataList.stream().map(outData -> {
            WholesaleSaleSummaryExportOutData exportOutData = new WholesaleSaleSummaryExportOutData();
            BeanUtil.copyProperties(outData, exportOutData);
            if (StrUtil.isNotEmpty(outData.getGwoOwnerSaleMan())) {
                exportOutData.setGwoOwnerSaleMan(outData.getGwoOwnerSaleManUserId()+"-"+outData.getGwoOwnerSaleMan());
            }
            exportOutData.setAddAmt(outData.getAddAmt() == null?BigDecimal.ZERO.setScale(4):outData.getAddAmt().setScale(4,RoundingMode.HALF_UP));
            exportOutData.setAddTax(outData.getAddTax() == null?BigDecimal.ZERO.setScale(4):outData.getAddTax().setScale(4,RoundingMode.HALF_UP));
            exportOutData.setAddtotalAmount(outData.getAddtotalAmount() == null?BigDecimal.ZERO.setScale(2):outData.getAddtotalAmount().setScale(2,RoundingMode.HALF_UP));
            return exportOutData;
        }).collect(Collectors.toList());
        WholesaleSaleSummaryExportOutData totalData = new WholesaleSaleSummaryExportOutData();
        BeanUtil.copyProperties(total, totalData);
        dataList.add(totalData);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "配送单据导出";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try {
            EasyExcel.write(response.getOutputStream(),WholesaleSaleSummaryExportOutData.class)
                    .registerWriteHandler(ExcelStyleUtil.getStyle())
                    .sheet(0).doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
    * @date 2022/2/23 13:37
    * @author liuzhengli
    * @Description TODO
    * @param  * @param null
    * @return {@link null}
    * @Version1.0
    **/
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
        if(StrUtil.isNotBlank(inData.getStatDatePart())){
            inData.setStatDatePart(inData.getStatDatePart()+"00");
        }
        if(StrUtil.isNotBlank(inData.getEndDatePart())){
            inData.setEndDatePart(inData.getEndDatePart()+"59");
        }

        GetPayInData pay = new GetPayInData();
        pay.setClientId(inData.getClient());
        pay.setType("1");
        //获取支付类型
        List<GetPayTypeOutData> payTypeOutData = payService.payTypeListByClient(pay);
        if (payTypeOutData != null && payTypeOutData.size() > 0) {
            inData.setPayTypeOutData(payTypeOutData);
        }

        List<Map<String, Object>> outData = this.saleDMapper.selectStoreSaleByDate(inData);

        PageInfo pageInfo;
        Double totalPrice=0.0000;
        DecimalFormat df1=new DecimalFormat();
        DecimalFormat df2=new DecimalFormat("#0.00");
        df1.applyPattern("-0.00");
        System.err.println("selectStoreSaleByDate:outData="+outData);
        if (ObjectUtil.isNotEmpty(outData)) {
            outData = getOutDate(outData, df1, df2);
            // 集合列的数据汇总
            Map<String, Object> outTotal = this.saleDMapper.selectStoreSaleByTatol(inData);
            for (Map<String, Object> item : outData){
                //if(item.get("amt").toString().startsWith("-") && item.get("amountReceivable").toString().startsWith("-")){
                totalPrice+=Double.parseDouble(item.get("zkDyq").toString());
                outTotal.put("zkDyq",totalPrice);
                // }
            }
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public PageInfo selectInventoryChangeSummaryDetail(InventoryChangeSummaryInData inData) {

        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (StringUtils.isEmpty(inData.getProCode())) {
            throw new BusinessException("请选择商品！");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        List<InventoryChangeSummaryDetailOutData> outData = this.materialDocMapper.selectInventoryChangeSummaryDetail(inData);
        InventoryChangeSummaryDetailOutData outNum = new InventoryChangeSummaryDetailOutData();
        BigDecimal startCount=BigDecimal.ZERO;
        //期间数量
        BigDecimal betweenCount=outData.stream().map(InventoryChangeSummaryDetailOutData::getQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
        if (StringUtils.isNotEmpty(inData.getStartDate())) {
            List<InventoryChangeSummaryOutData> startData= this.materialDocMapper.getStartDataAll(inData);
            //期初数量
            startCount=startData.stream().map(InventoryChangeSummaryOutData::getStartQty).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        //期末数量
        BigDecimal endCount = startCount.add(betweenCount).setScale(2,BigDecimal.ROUND_HALF_UP);
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            // 集合列的数据汇总
            outNum.setStartCount(startCount.toString());
            outNum.setBetweenCount(betweenCount.toString());
            outNum.setEndCount(endCount.toString());
            for (InventoryChangeSummaryDetailOutData out : outData) {
                outNum.setAmount(CommonUtil.stripTrailingZeros(outNum.getAmount()).add(CommonUtil.stripTrailingZeros(out.getAmount())));
                outNum.setAddAmount(CommonUtil.stripTrailingZeros(outNum.getAddAmount()).add(CommonUtil.stripTrailingZeros(out.getAddAmount())));
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
            }
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            outNum.setStartCount(startCount.toString());
            outNum.setBetweenCount(betweenCount.toString());
            outNum.setEndCount(endCount.toString());
            pageInfo = new PageInfo();
            pageInfo.setListNum(outNum);
        }

        return pageInfo;
    }

    @Override
    public PageInfo<InventoryChangeCheckOutData> selectInventoryStockCheckListByDc(InventoryChangeCheckInData data) {
        if (CollUtil.isEmpty(data.getSiteCodeList())) {
            throw new BusinessException("仓库编码必填");
        }
        List<InventoryChangeCheckOutData> outData=new ArrayList<>();
        InventoryChangeCheckOutData startAmt = this.materialDocMapper.selectInventoryStockCheckAmtDcStartList(data);
        InventoryChangeCheckOutData betweenAmt=this.materialDocMapper.selectInventoryStockCheckDcBetweenAmt(data);
        if (ObjectUtil.isNull(betweenAmt)) {
            if (ObjectUtil.isNull(startAmt)) {
                startAmt=new InventoryChangeCheckOutData();
                startAmt.setSiteCode(data.getSiteCodeList().get(0));
                String dcName=this.materialDocMapper.findDcNameByDcCode(data.getClient(),data.getSiteCodeList().get(0));
                startAmt.setSiteName(dcName);
                startAmt.setStartAmt(BigDecimal.ZERO);
            }
            startAmt.setBetweenProductIn(BigDecimal.ZERO);
            startAmt.setBetweenProductRefound(BigDecimal.ZERO);
            startAmt.setBetweenDistribution(BigDecimal.ZERO);
            startAmt.setBetweenStockRefound(BigDecimal.ZERO);
            startAmt.setBetweenSale(BigDecimal.ZERO);
            startAmt.setBetweenLoss(BigDecimal.ZERO);
            startAmt.setBetweenQc(BigDecimal.ZERO);
        } else {
            if (ObjectUtil.isNull(startAmt)) {
                startAmt=new InventoryChangeCheckOutData();
                startAmt.setSiteCode(betweenAmt.getSiteCode());
                startAmt.setSiteName(betweenAmt.getSiteName());
                startAmt.setStartAmt(BigDecimal.ZERO);
            }
            startAmt.setBetweenProductIn(betweenAmt.getBetweenProductIn()==null?BigDecimal.ZERO:betweenAmt.getBetweenProductIn());
            startAmt.setBetweenProductRefound(betweenAmt.getBetweenProductRefound()==null?BigDecimal.ZERO:betweenAmt.getBetweenProductRefound());
            startAmt.setBetweenDistribution(betweenAmt.getBetweenDistribution()==null?BigDecimal.ZERO:betweenAmt.getBetweenDistribution());
            startAmt.setBetweenStockRefound(betweenAmt.getBetweenStockRefound()==null?BigDecimal.ZERO:betweenAmt.getBetweenStockRefound());
            startAmt.setBetweenSale(betweenAmt.getBetweenSale()==null?BigDecimal.ZERO:betweenAmt.getBetweenSale());
            startAmt.setBetweenLoss(betweenAmt.getBetweenLoss()==null?BigDecimal.ZERO:betweenAmt.getBetweenLoss());
            startAmt.setBetweenQc(betweenAmt.getBetweenQc()==null?BigDecimal.ZERO:betweenAmt.getBetweenQc());
        }

        if (null != betweenAmt) {
            startAmt.setEndAmt(
                    startAmt.getStartAmt() == null ? BigDecimal.ZERO : startAmt.getStartAmt()
                            .add(betweenAmt.getBetweenProductIn() == null ? BigDecimal.ZERO : betweenAmt.getBetweenProductIn())
                            .add(betweenAmt.getBetweenProductRefound() == null ? BigDecimal.ZERO : betweenAmt.getBetweenProductRefound())
                            .add(betweenAmt.getBetweenDistribution() == null ? BigDecimal.ZERO : betweenAmt.getBetweenDistribution())
                            .add(betweenAmt.getBetweenStockRefound() == null ? BigDecimal.ZERO : betweenAmt.getBetweenStockRefound())
                            .add(betweenAmt.getBetweenSale() == null ? BigDecimal.ZERO : betweenAmt.getBetweenSale())
                            .add(betweenAmt.getBetweenLoss() == null ? BigDecimal.ZERO : betweenAmt.getBetweenLoss())
                            .add(betweenAmt.getBetweenQc() == null ? BigDecimal.ZERO : betweenAmt.getBetweenQc())
            );
        } else {
            startAmt.setEndAmt(startAmt.getStartAmt());
            startAmt.setBetweenProductIn(BigDecimal.ZERO);
            startAmt.setBetweenProductRefound(BigDecimal.ZERO);
            startAmt.setBetweenDistribution(BigDecimal.ZERO);
            startAmt.setBetweenStockRefound(BigDecimal.ZERO);
            startAmt.setBetweenSale(BigDecimal.ZERO);
            startAmt.setBetweenLoss(BigDecimal.ZERO);
            startAmt.setBetweenQc(BigDecimal.ZERO);
        }
        startAmt=formatBigDecimal(startAmt);
        outData.add(startAmt);

        InventoryChangeCheckOutData total=computeTotal(outData);
        return new PageInfo<>(outData,total);
    }

    private InventoryChangeCheckOutData formatBigDecimal(InventoryChangeCheckOutData startAmt) {
        startAmt.setStartAmt(NumberUtil.round(startAmt.getStartAmt(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenProductIn(NumberUtil.round(startAmt.getBetweenProductIn(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenProductRefound(NumberUtil.round(startAmt.getBetweenProductRefound(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenDistribution(NumberUtil.round(startAmt.getBetweenDistribution(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenStockRefound(NumberUtil.round(startAmt.getBetweenStockRefound(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenSale(NumberUtil.round(startAmt.getBetweenSale(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenLoss(NumberUtil.round(startAmt.getBetweenLoss(),2, RoundingMode.HALF_UP));
        startAmt.setBetweenQc(NumberUtil.round(startAmt.getBetweenQc(),2, RoundingMode.HALF_UP));
        startAmt.setEndAmt(NumberUtil.round(startAmt.getEndAmt(),2, RoundingMode.HALF_UP));
        return startAmt;
    }

    private InventoryChangeCheckOutData computeTotal(List<InventoryChangeCheckOutData> outData) {
        InventoryChangeCheckOutData total=new InventoryChangeCheckOutData();
        BigDecimal startAmt=BigDecimal.ZERO;
        BigDecimal betweenProductIn=BigDecimal.ZERO;
        BigDecimal betweenProductRefound=BigDecimal.ZERO;
        BigDecimal betweenDistribution=BigDecimal.ZERO;
        BigDecimal betweenStockRefound=BigDecimal.ZERO;
        BigDecimal betweenSale=BigDecimal.ZERO;
        BigDecimal betweenLoss=BigDecimal.ZERO;
        BigDecimal betweenQc=BigDecimal.ZERO;
        BigDecimal endAmt=BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(outData)) {
            startAmt = outData.stream().map(InventoryChangeCheckOutData::getStartAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenProductIn = outData.stream().map(InventoryChangeCheckOutData::getBetweenProductIn).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenProductRefound = outData.stream().map(InventoryChangeCheckOutData::getBetweenProductRefound).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenDistribution = outData.stream().map(InventoryChangeCheckOutData::getBetweenDistribution).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenStockRefound = outData.stream().map(InventoryChangeCheckOutData::getBetweenStockRefound).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenSale = outData.stream().map(InventoryChangeCheckOutData::getBetweenSale).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenLoss = outData.stream().map(InventoryChangeCheckOutData::getBetweenLoss).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            betweenQc = outData.stream().map(InventoryChangeCheckOutData::getBetweenQc).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
            endAmt = outData.stream().map(InventoryChangeCheckOutData::getEndAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        total.setStartAmt(startAmt);
        total.setBetweenProductIn(betweenProductIn);
        total.setBetweenProductRefound(betweenProductRefound);
        total.setBetweenDistribution(betweenDistribution);
        total.setBetweenStockRefound(betweenStockRefound);
        total.setBetweenSale(betweenSale);
        total.setBetweenLoss(betweenLoss);
        total.setBetweenQc(betweenQc);
        total.setEndAmt(endAmt);
        return total;
    }

    @Override
    public void checkListDcExport(InventoryChangeCheckInData data, HttpServletResponse response) {
        PageInfo<InventoryChangeCheckOutData> pageInfo = this.selectInventoryStockCheckListByDc(data);
        InventoryChangeCheckOutData total = pageInfo.getListNum();
        total.setSiteCode("合计");
        total.setSiteName("");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        ExcelWriter excelWriter = null;
        try {
            String fileName = URLEncoder.encode("期末仓库库存导出", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            excelWriter = EasyExcel.write(response.getOutputStream(),InventoryChangeCheckOutData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0, 0 + "").build();
            excelWriter.write(pageInfo.getList(),writeSheet);
            excelWriter.write(CollUtil.newArrayList(total),writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageInfo<InventoryChangeCheckOutData> selectInventoryStockCheckListBySto(InventoryChangeCheckInData data) {
        if (CollUtil.isEmpty(data.getSiteCodeList())) {
            throw new BusinessException("门店必填");
        }
        List<StoClassOutData> stoList = setValue(data);
        //返回对象
        List<InventoryChangeCheckOutData> outData=new ArrayList<>();
        //上个月库存金额
        List<InventoryChangeCheckOutData> startAmtList=this.materialDocMapper.selectInventoryStockCheckAmtStoStartList(data);
        //求这个月初到开始日期的剩余期间
        List<InventoryChangeCheckOutData> restStartAmtList=getRestStartDataList(data);
        //合并上个月库存和剩余期间得到期初
        startAmtList=mergeStartData(startAmtList,restStartAmtList);
        //求开始日期到结束日期的期间发生
        String qcDate = getCurrentClientQcDate(data.getClient());
        if (Integer.parseInt(StrUtil.isEmpty(qcDate)?"0":qcDate) >= Integer.parseInt(StrUtil.isEmpty(data.getStartDate())?"0":data.getStartDate())) {
            data.setQcDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.parse(qcDate, DatePattern.PURE_DATE_PATTERN),1),DatePattern.PURE_DATE_PATTERN));
        }else {
            data.setQcDate(data.getStartDate());
        }
        List<InventoryChangeCheckOutData> betweenAmt=this.materialDocMapper.selectInventoryStockCheckStoBetweenAmt(data);
        //合并期初、期间
        for (InventoryChangeCheckOutData start : startAmtList) {
            for (InventoryChangeCheckOutData bet : betweenAmt) {
                if (start.getSiteCode().equals(bet.getSiteCode())) {

                    start.setBetweenQc(bet.getBetweenQc());
                    start.setBetweenProductIn(bet.getBetweenProductIn());
                    start.setBetweenProductRefound(bet.getBetweenProductRefound());
                    start.setBetweenDistribution(bet.getBetweenDistribution());
                    start.setBetweenStockRefound(bet.getBetweenStockRefound());
                    start.setBetweenSale(bet.getBetweenSale());
                    start.setBetweenLoss(bet.getBetweenLoss());
                    start.setEndAmt(
                            BigDecimal.ZERO
                                    .add(start.getStartAmt()==null?BigDecimal.ZERO:start.getStartAmt())
                                    .add(bet.getBetweenProductIn()==null?BigDecimal.ZERO:bet.getBetweenProductIn())
                                    .add(bet.getBetweenProductRefound()==null?BigDecimal.ZERO:bet.getBetweenProductRefound())
                                    .add(bet.getBetweenDistribution()==null?BigDecimal.ZERO:bet.getBetweenDistribution())
                                    .add(bet.getBetweenStockRefound()==null?BigDecimal.ZERO:bet.getBetweenStockRefound())
                                    .add(bet.getBetweenSale()==null?BigDecimal.ZERO:bet.getBetweenSale())
                                    .add(bet.getBetweenLoss()==null?BigDecimal.ZERO:bet.getBetweenLoss())
                                    .add(bet.getBetweenQc()==null?BigDecimal.ZERO:bet.getBetweenQc())
                    );
                    outData.add(start);
                }
            }
        }
        //如果没有期初为空
        if (CollUtil.isEmpty(startAmtList)) {
            for (InventoryChangeCheckOutData bet : betweenAmt) {
                bet.setStartAmt(BigDecimal.ZERO);
                bet.setEndAmt(
                        bet.getBetweenProductIn()==null?BigDecimal.ZERO:bet.getBetweenProductIn()
                                .add(bet.getBetweenProductRefound()==null?BigDecimal.ZERO:bet.getBetweenProductRefound())
                                .add(bet.getBetweenDistribution()==null?BigDecimal.ZERO:bet.getBetweenDistribution())
                                .add(bet.getBetweenStockRefound()==null?BigDecimal.ZERO:bet.getBetweenStockRefound())
                                .add(bet.getBetweenSale()==null?BigDecimal.ZERO:bet.getBetweenSale())
                                .add(bet.getBetweenLoss()==null?BigDecimal.ZERO:bet.getBetweenLoss())
                                .add(bet.getBetweenQc()==null?BigDecimal.ZERO:bet.getBetweenQc())
                );
            }
            outData.addAll(betweenAmt);
        }
        List<InventoryChangeCheckOutData> newOutData = new ArrayList<>();
        for (InventoryChangeCheckOutData changeCheckOutData : outData){
            for (StoClassOutData stoClassOutData : stoList){
                if (changeCheckOutData.getSiteCode().equals(stoClassOutData.getStoCode())){
                    changeCheckOutData.setStoAttribute(stoClassOutData.getStoAttribute());
                    changeCheckOutData.setDirectManaged(stoClassOutData.getDirectManaged());
                    changeCheckOutData.setShopType(stoClassOutData.getShopType());
                    changeCheckOutData.setStoIfDtp(stoClassOutData.getStoIfDtp());
                    changeCheckOutData.setStoIfMedical(stoClassOutData.getStoIfMedical());
                    changeCheckOutData.setStoreEfficiencyLevel(stoClassOutData.getStoreEfficiencyLevel());
                    changeCheckOutData.setStoTaxClass(stoClassOutData.getStoTaxClass());
                    changeCheckOutData.setManagementArea(stoClassOutData.getManagementArea());
                    newOutData.add(changeCheckOutData);
                }
            }
        }
        InventoryChangeCheckOutData total=computeTotal(newOutData);
        return new PageInfo<>(newOutData,total);
    }

    /**
     * 求门店剩余期初数据
     * @param data
     * @return
     */
    private List<InventoryChangeCheckOutData> getRestStartDataList(InventoryChangeCheckInData data) {
        List<InventoryChangeCheckOutData> restStartAmtList=new ArrayList<>();
        InventoryChangeCheckInData restInData=new InventoryChangeCheckInData();
        BeanUtil.copyProperties(data,restInData);
        String startDate = data.getStartDate();
        //求开始日期对应月份的1号
        String beginOfMonth = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.beginOfMonth(cn.hutool.core.date.DateUtil.parse(startDate, "yyyyMMdd")), "yyyyMMdd");
        //求开始日期的前一天
        String theDayBeforeStartDate = cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.parse(startDate), -1), "yyyyMMdd");
        if (startDate.equals(beginOfMonth)) {//如果开始日期就是1号那么就没有门店剩余期初数据
            return restStartAmtList;
        }
        restInData.setStartDate(beginOfMonth);
        restInData.setEndDate(theDayBeforeStartDate);
        restStartAmtList = this.materialDocMapper.selectInventoryStockCheckStoBetweenAmt(restInData);
        return restStartAmtList;
    }

    private List<StoClassOutData> setValue(InventoryChangeCheckInData data) {
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        boolean noChooseFlag = true;
        if (data.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : data.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            data.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        if (data.getStoAttribute() != null) {
            noChooseFlag = false;
            data.setStoAttributes(Arrays.asList(data.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (data.getStoIfMedical() != null) {
            noChooseFlag = false;
            data.setStoIfMedicals(Arrays.asList(data.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (data.getStoTaxClass() != null) {
            noChooseFlag = false;
            data.setStoTaxClasss(Arrays.asList(data.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (data.getStoIfDtp() != null) {
            noChooseFlag = false;
            data.setStoIfDtps(Arrays.asList(data.getStoIfDtp().split(StrUtil.COMMA)));
        }
        List<StoClassOutData> stoList = gaiaStoreDataMapper.getStoreCodeByStoClass(data);
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(data.getClient());

        for (StoClassOutData sto : stoList) {
            //转换
            if (sto.getStoAttribute() != null || noChooseFlag) {
                sto.setStoAttribute(StoreAttributeEnum.getName(sto.getStoAttribute()));
            }
            if (sto.getStoIfMedical() != null || noChooseFlag) {
                sto.setStoIfMedical(StoreMedicalEnum.getName(sto.getStoIfMedical()));
            }
            if (sto.getStoIfDtp() != null || noChooseFlag) {
                sto.setStoIfDtp(StoreDTPEnum.getName(sto.getStoIfDtp()));
            }
            if (sto.getStoTaxClass() != null || noChooseFlag) {
                sto.setStoTaxClass(StoreTaxClassEnum.getName(sto.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(sto.getStoCode())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        sto.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        sto.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        sto.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        sto.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            sto.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            sto.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            sto.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            sto.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }
        return stoList;
    }
    //门店:合并上个月库存期初加这个月月初到开始日期前一天的期间
    private List<InventoryChangeCheckOutData> mergeStartData(List<InventoryChangeCheckOutData> startAmtList, List<InventoryChangeCheckOutData> restStartAmtList) {
        for (InventoryChangeCheckOutData startQc : startAmtList) {

            for (InventoryChangeCheckOutData restAmt : restStartAmtList) {
                if (startQc.getSiteCode().equals(restAmt.getSiteCode())) {
                    startQc.setStartAmt(
                            startQc.getStartAmt()==null?BigDecimal.ZERO:startQc.getStartAmt()
                                    .add(restAmt.getBetweenProductIn()==null?BigDecimal.ZERO:restAmt.getBetweenProductIn())
                                    .add(restAmt.getBetweenProductRefound()==null?BigDecimal.ZERO:restAmt.getBetweenProductRefound())
                                    .add(restAmt.getBetweenDistribution()==null?BigDecimal.ZERO:restAmt.getBetweenDistribution())
                                    .add(restAmt.getBetweenStockRefound()==null?BigDecimal.ZERO:restAmt.getBetweenStockRefound())
                                    .add(restAmt.getBetweenSale()==null?BigDecimal.ZERO:restAmt.getBetweenSale())
                                    .add(restAmt.getBetweenLoss()==null?BigDecimal.ZERO:restAmt.getBetweenLoss())
                                    .add(restAmt.getBetweenQc()==null?BigDecimal.ZERO:restAmt.getBetweenQc())
                    );
                }
            }
        }
        List<String> siteCodeList = startAmtList.stream().map(InventoryChangeCheckOutData::getSiteCode).collect(Collectors.toList());
        restStartAmtList.removeIf(rest->{
            return siteCodeList.contains(rest.getSiteCode());
        });
        //给默认值
        for (InventoryChangeCheckOutData rest : restStartAmtList) {
            rest.setStartAmt(rest.getBetweenQc()==null?BigDecimal.ZERO:rest.getBetweenQc());
            rest.setEndAmt(
                    rest.getBetweenQc()==null?BigDecimal.ZERO:rest.getBetweenQc()
                            .add(rest.getBetweenProductIn()==null?BigDecimal.ZERO:rest.getBetweenProductIn())
                            .add(rest.getBetweenProductRefound()==null?BigDecimal.ZERO:rest.getBetweenProductRefound())
                            .add(rest.getBetweenDistribution()==null?BigDecimal.ZERO:rest.getBetweenDistribution())
                            .add(rest.getBetweenStockRefound()==null?BigDecimal.ZERO:rest.getBetweenStockRefound())
                            .add(rest.getBetweenSale()==null?BigDecimal.ZERO:rest.getBetweenSale())
                            .add(rest.getBetweenLoss()==null?BigDecimal.ZERO:rest.getBetweenLoss())
            );
        }
        startAmtList.addAll(restStartAmtList);
        return startAmtList;
    }

    @Override
    public void checkListStoExport(InventoryChangeCheckInData data, HttpServletResponse response) {
        PageInfo<InventoryChangeCheckOutData> pageInfo = this.selectInventoryStockCheckListBySto(data);
        InventoryChangeCheckOutData total = pageInfo.getListNum();
        total.setSiteCode("合计");
        total.setSiteName("");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        ExcelWriter excelWriter = null;
        try {
            String fileName = URLEncoder.encode("期末门店库存导出", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            excelWriter = EasyExcel.write(response.getOutputStream(),InventoryChangeCheckOutData.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0, 0 + "").build();
            excelWriter.write(pageInfo.getList(),writeSheet);
            excelWriter.write(CollUtil.newArrayList(total),writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 供应商商品销售明细导出
     *
     * @param data
     * @return
     */
    @Override
    public Result exportProductSalesBySupplier(ProductSalesBySupplierInData inData) throws IOException {
        log.info("[供应商商品销售明细导出开始,入参：{}", JSONObject.toJSONString(inData));
        inData.setPageNum(null);
        inData.setPageSize(null);
        PageInfo pageInfo = this.selectProductSalesBySupplier(inData);
        String fileName = "供应商商品销售明细导出";
        if (Objects.nonNull(pageInfo) && CollUtil.isNotEmpty(pageInfo.getList())) {
            List<ProductSalesBySupplierOutData> outData = (List<ProductSalesBySupplierOutData>)pageInfo.getList();
            ProductSalesBySupplierOutTotal supplierOutTotal = (ProductSalesBySupplierOutTotal)pageInfo.getListNum();
            if(Objects.nonNull(supplierOutTotal)){
                ProductSalesBySupplierOutData data = new ProductSalesBySupplierOutData();
                data.setStoCode("合计:");
                data.setQty(supplierOutTotal.getQty());
                data.setAmountReceivable(supplierOutTotal.getAmountReceivable());
                data.setIncludeTaxSale(supplierOutTotal.getIncludeTaxSale());
                data.setAmt(supplierOutTotal.getAmt());
                data.setGrossProfitMargin(supplierOutTotal.getGrossProfitMargin());
                String grossProfitRate= StrUtil.isNotBlank(supplierOutTotal.getGrossProfitRate())?supplierOutTotal.getGrossProfitRate().split("%")[0]:"0.00";
                data.setGrossProfitRate(new BigDecimal(grossProfitRate));
                outData.add(data);
            }
            CsvFileInfo csvInfo = null;
            // 导出
            // byte数据
            csvInfo = CsvClient.getCsvByte(outData, fileName, null);
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
}

package com.gys.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.mapper.*;
import com.gys.util.*;
import com.gys.report.common.constant.CommonConstant;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import com.gys.report.entity.*;
import com.gys.report.service.SalesSummaryOfSalesmenReportService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SalesSummaryOfSalesmenReportServiceImpl implements SalesSummaryOfSalesmenReportService {
    @Autowired
    private GaiaStoreDataMapper gaiaStoreDataMapper;
    @Autowired
    private GaiaSdSaleDMapper gaiaSdSaleDMapper;
    @Autowired
    private RelatedSaleEjectMapper relatedSaleEjectMapper;
    @Resource
    CosUtils cosUtils;
    @Autowired
    private GaiaSdStoresGroupMapper gaiaSdStoresGroupMapper;
    @Resource
    private GaiaProductBusinessMapper productBusinessMapper;

    @Override
    public PageInfo<GetSalesSummaryOfSalesmenReportOutData> queryReport(GetSalesSummaryOfSalesmenReportInData inData) {
        //先查询权限   flag  0：不开启  1：开启
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        if (ObjectUtil.isEmpty(flag)) {
            inData.setFlag("0");
        } else {
            inData.setFlag(flag);
        }

        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }

        List<GetSalesSummaryOfSalesmenReportOutData> outData = this.gaiaSdSaleDMapper.querySalesSummaryOfSalesmenReport(inData);
        int index = 0;
        for (GetSalesSummaryOfSalesmenReportOutData i : outData) {
            index++;
            i.setIndex(index);
        }

        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outData)) {
            GetSalesSummaryOfSalesmenReportOutData countAll = this.gaiaSdSaleDMapper.queryCountSalesSummar(inData);
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(countAll);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo<GetSalesSummaryOfSalesmenReportOutDataUnion> unionQueryReport(GetSalesSummaryOfSalesmenReportInData inData) {
        //先查询权限   flag  0：不开启  1：开启
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        if (ObjectUtil.isEmpty(flag)) {
            inData.setFlag("0");
        } else {
            inData.setFlag(flag);
        }


        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        List<GetSalesSummaryOfSalesmenReportOutDataUnion> resList = new ArrayList<>();
        List<GetSalesSummaryOfSalesmenReportOutData> outData = this.gaiaSdSaleDMapper.querySalesSummaryOfSalesmenReport(inData);
        Map<String, GetSalesSummaryOfSalesmenReportOutData> outDataMap = new HashMap<>();
        int index = 0;
        for (GetSalesSummaryOfSalesmenReportOutData i : outData) {
            index++;
            i.setIndex(index);
            outDataMap.put(i.getSellerCode(), i);
        }
        if (inData.getPageNum() == null) {
            inData.setPageNum(1);
        }
        if (inData.getPageSize() == null) {
            inData.setPageSize(10000);
        }

        RelatedSaleEjectInData relatedSaleEjectInData = new RelatedSaleEjectInData();
        relatedSaleEjectInData.setClient(inData.getClientId());
        relatedSaleEjectInData.setSiteCode(inData.getBrId());
        relatedSaleEjectInData.setQueryUserId(inData.getQueryUserId());
        relatedSaleEjectInData.setQueryEndDate(inData.getQueryEndDate());
        relatedSaleEjectInData.setQueryStartDate(inData.getQueryStartDate());
        List<RelatedSaleEjectRes> relatedSaleEjectResList = relatedSaleEjectMapper.selectTclGroupByUserId(relatedSaleEjectInData);//查询弹出率
        Map<String, RelatedSaleEjectRes> relatedSaleEjectResMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(relatedSaleEjectResList)) {
            for (RelatedSaleEjectRes relatedSaleEjectRes : relatedSaleEjectResList) {
                if (StrUtil.isNotBlank(relatedSaleEjectRes.getCreateBy())) {
                    relatedSaleEjectResMap.put(relatedSaleEjectRes.getCreateBy(), relatedSaleEjectRes);
                }
            }
        }

        List<GetSalesSummaryOfSalesmenReportOutDataUnion> unionOrderList = this.gaiaSdSaleDMapper.selectUnionSaleOrderByUserId(relatedSaleEjectInData);
        Map<String, GetSalesSummaryOfSalesmenReportOutDataUnion> unionOrderMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(unionOrderList)) {
            for (GetSalesSummaryOfSalesmenReportOutDataUnion res : unionOrderList) {
                if (StrUtil.isNotBlank(res.getSellerCode())) {
                    unionOrderMap.put(res.getSellerCode(), res);
                }
            }
        }

        List<GetSalesSummaryOfSalesmenReportOutDataUnion> countAndAmtList = this.gaiaSdSaleDMapper.selectUnionSaleCountAndAmtByUserId(relatedSaleEjectInData);
        Map<String, GetSalesSummaryOfSalesmenReportOutDataUnion> countAndAmtMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(countAndAmtList)) {
            for (GetSalesSummaryOfSalesmenReportOutDataUnion res : countAndAmtList) {
                if (StrUtil.isNotBlank(res.getSellerCode())) {
                    countAndAmtMap.put(res.getSellerCode(), res);
                }
            }
        }

        if(Objects.nonNull(inData.getPageNum()) &&  Objects.nonNull(inData.getPageSize()) ){
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        List<GetSalesSummaryOfSalesmenReportOutDataUnion> outDataUnion = this.gaiaSdSaleDMapper.querySalesSummaryOfSalesmenReportUnion(inData);
//        Map<String,GetSalesSummaryOfSalesmenReportOutDataUnion> outDataUnionUnMap = new HashMap<>();

        for (GetSalesSummaryOfSalesmenReportOutDataUnion i : outDataUnion) {
            if (outDataMap.get(i.getSellerCode()) != null) {
                RelatedSaleEjectRes relatedSaleEjectRes = relatedSaleEjectResMap.get(i.getSellerCode());
                BeanUtils.copyProperties(outDataMap.get(i.getSellerCode()), i);
                i.setInputCount(relatedSaleEjectResMap.get(i.getSellerCode()) == null ? 0 : relatedSaleEjectResMap.get(i.getSellerCode()).getInputCount());
                i.setUnitCount(relatedSaleEjectResMap.get(i.getSellerCode()) == null ? 0 : relatedSaleEjectResMap.get(i.getSellerCode()).getUnitCount());
                if (relatedSaleEjectRes != null && relatedSaleEjectRes.getInputCount() != null && relatedSaleEjectRes.getUnitCount() != null && relatedSaleEjectRes.getUnitCount() != 0 && relatedSaleEjectRes.getInputCount() != 0) {
                    BigDecimal res = new BigDecimal(relatedSaleEjectRes.getUnitCount()).divide(new BigDecimal(relatedSaleEjectRes.getInputCount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                    i.setEjectRate(res.setScale(2, BigDecimal.ROUND_HALF_UP));
                    i.setEjectRateStr(res.toPlainString() + "%");
                } else {
                    i.setEjectRate(new BigDecimal(0));
                    i.setEjectRateStr(0 + "%");
                }
                GetSalesSummaryOfSalesmenReportOutDataUnion countAndAmt = countAndAmtMap.get(i.getSellerCode());
                GetSalesSummaryOfSalesmenReportOutDataUnion unionOrder = unionOrderMap.get(i.getSellerCode());
                i.setBusinessCount(countAndAmtMap.get(i.getSellerCode()) == null ? 0 : countAndAmtMap.get(i.getSellerCode()).getBusinessCount());
                i.setUnionBusinessCount(countAndAmtMap.get(i.getSellerCode()) == null ? 0 : countAndAmtMap.get(i.getSellerCode()).getUnionBusinessCount());
                if (unionOrder != null && unionOrder.getUnionBusinessCount() != null && countAndAmt != null && countAndAmt.getUnionBusinessCount() != null && countAndAmt.getUnionBusinessCount() != 0) {
                    i.setGlcjcs(new BigDecimal(countAndAmt.getUnionBusinessCount()));
                    i.setYgltcxpcs(new BigDecimal(unionOrder.getUnionBusinessCount()));
                    BigDecimal res = new BigDecimal(countAndAmt.getUnionBusinessCount()).divide(new BigDecimal(unionOrder.getUnionBusinessCount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                    i.setBusinessCountRate(res);
                    i.setBusinessCountRateStr(res.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "%");
                } else {
                    i.setGlcjcs(new BigDecimal(0));
                    i.setYgltcxpcs(new BigDecimal(0));
                    i.setBusinessCountRate(new BigDecimal(0));
                    i.setBusinessCountRateStr(0 + "%");
                }
                i.setUnionBusinessAmt(countAndAmtMap.get(i.getSellerCode()) == null ? BigDecimal.ZERO : countAndAmtMap.get(i.getSellerCode()).getUnionBusinessAmt().setScale(2, RoundingMode.HALF_UP));
                if (countAndAmt != null && countAndAmt.getBusinessAmt() != null && countAndAmt.getUnionBusinessAmt() != null && countAndAmt.getBusinessAmt().intValue() != 0 && countAndAmt.getUnionBusinessAmt().intValue() != 0) {
                    BigDecimal businessAmt =i.getSsAmount();
                    i.setUnionBusinessAmtRate(businessAmt.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0") : i.getUnionBusinessAmt().divide(businessAmt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
//
//                    BigDecimal res = countAndAmt.getUnionBusinessAmt().divide(businessAmt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
//                    i.setUnionBusinessAmtRate(res);
                    i.setUnionBusinessAmtRateStr(i.getUnionBusinessAmtRateStr());
                } else {
                    i.setUnionBusinessAmtRate(new BigDecimal(0));
                    i.setUnionBusinessAmtRateStr(0 + "%");
                }

                i.setBusinessAmt(countAndAmtMap.get(i.getSellerCode()) == null ? BigDecimal.ZERO : countAndAmtMap.get(i.getSellerCode()).getBusinessAmt());

                resList.add(i);

            }
        }
        PageInfo pageInfoSearch = new PageInfo(outDataUnion);

        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(resList)) {
            GetSalesSummaryOfSalesmenReportOutDataUnion totalInfo = getTotalInfo(resList);
//            GetSalesSummaryOfSalesmenReportOutData countAll = this.gaiaSdSaleDMapper.queryCountSalesSummar(inData);
            pageInfo = new PageInfo(resList);
            pageInfo.setTotal(pageInfoSearch.getTotal());
            pageInfo.setPages(pageInfoSearch.getPages());
            pageInfo.setListNum(totalInfo);
//            Map<String,Object> extendsInfo = new HashMap<>();
//            extendsInfo.put("totalInfo",totalInfo);
//            pageInfo.setExtendsInfo(extendsInfo);
//            pageInfo.setListNum(countAll);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    /**
     * 联合用药员工计算分页合计
     * @param inData
     * @return
     */
    private GetSalesSummaryOfSalesmenReportOutDataUnion totalInfo(GetSalesSummaryOfSalesmenReportInData inData) {
        List<GetSalesSummaryOfSalesmenReportOutDataUnion> dataUnions = unionQueryReportList(inData);
        return getTotalInfo(dataUnions);
    }


    public List<GetSalesSummaryOfSalesmenReportOutDataUnion> unionQueryReportList(GetSalesSummaryOfSalesmenReportInData inData) {
        //先查询权限   flag  0：不开启  1：开启
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        if (ObjectUtil.isEmpty(flag)) {
            inData.setFlag("0");
        } else {
            inData.setFlag(flag);
        }


        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        List<GetSalesSummaryOfSalesmenReportOutDataUnion> resList = new ArrayList<>();
        List<GetSalesSummaryOfSalesmenReportOutData> outData = this.gaiaSdSaleDMapper.querySalesSummaryOfSalesmenReport(inData);
        Map<String, GetSalesSummaryOfSalesmenReportOutData> outDataMap = new HashMap<>();
        int index = 0;
        for (GetSalesSummaryOfSalesmenReportOutData i : outData) {
            index++;
            i.setIndex(index);
            outDataMap.put(i.getSellerCode(), i);
        }
        if (inData.getPageNum() == null) {
            inData.setPageNum(1);
        }
        if (inData.getPageSize() == null) {
            inData.setPageSize(10000);
        }

        RelatedSaleEjectInData relatedSaleEjectInData = new RelatedSaleEjectInData();
        relatedSaleEjectInData.setClient(inData.getClientId());
        relatedSaleEjectInData.setSiteCode(inData.getBrId());
        relatedSaleEjectInData.setQueryUserId(inData.getQueryUserId());
        relatedSaleEjectInData.setQueryEndDate(inData.getQueryEndDate());
        relatedSaleEjectInData.setQueryStartDate(inData.getQueryStartDate());
        List<RelatedSaleEjectRes> relatedSaleEjectResList = relatedSaleEjectMapper.selectTclGroupByUserId(relatedSaleEjectInData);//查询弹出率
        Map<String, RelatedSaleEjectRes> relatedSaleEjectResMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(relatedSaleEjectResList)) {
            for (RelatedSaleEjectRes relatedSaleEjectRes : relatedSaleEjectResList) {
                if (StrUtil.isNotBlank(relatedSaleEjectRes.getCreateBy())) {
                    relatedSaleEjectResMap.put(relatedSaleEjectRes.getCreateBy(), relatedSaleEjectRes);
                }
            }
        }

        List<GetSalesSummaryOfSalesmenReportOutDataUnion> unionOrderList = this.gaiaSdSaleDMapper.selectUnionSaleOrderByUserId(relatedSaleEjectInData);
        Map<String, GetSalesSummaryOfSalesmenReportOutDataUnion> unionOrderMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(unionOrderList)) {
            for (GetSalesSummaryOfSalesmenReportOutDataUnion res : unionOrderList) {
                if (StrUtil.isNotBlank(res.getSellerCode())) {
                    unionOrderMap.put(res.getSellerCode(), res);
                }
            }
        }

        List<GetSalesSummaryOfSalesmenReportOutDataUnion> countAndAmtList = this.gaiaSdSaleDMapper.selectUnionSaleCountAndAmtByUserId(relatedSaleEjectInData);
        Map<String, GetSalesSummaryOfSalesmenReportOutDataUnion> countAndAmtMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(countAndAmtList)) {
            for (GetSalesSummaryOfSalesmenReportOutDataUnion res : countAndAmtList) {
                if (StrUtil.isNotBlank(res.getSellerCode())) {
                    countAndAmtMap.put(res.getSellerCode(), res);
                }
            }
        }

        if(Objects.nonNull(inData.getPageNum()) &&  Objects.nonNull(inData.getPageSize()) ){
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        List<GetSalesSummaryOfSalesmenReportOutDataUnion> outDataUnion = this.gaiaSdSaleDMapper.querySalesSummaryOfSalesmenReportUnion(inData);
//        Map<String,GetSalesSummaryOfSalesmenReportOutDataUnion> outDataUnionUnMap = new HashMap<>();

        for (GetSalesSummaryOfSalesmenReportOutDataUnion i : outDataUnion) {
            if (outDataMap.get(i.getSellerCode()) != null) {
                RelatedSaleEjectRes relatedSaleEjectRes = relatedSaleEjectResMap.get(i.getSellerCode());
                BeanUtils.copyProperties(outDataMap.get(i.getSellerCode()), i);
                i.setInputCount(relatedSaleEjectResMap.get(i.getSellerCode()) == null ? 0 : relatedSaleEjectResMap.get(i.getSellerCode()).getInputCount());
                i.setUnitCount(relatedSaleEjectResMap.get(i.getSellerCode()) == null ? 0 : relatedSaleEjectResMap.get(i.getSellerCode()).getUnitCount());
                if (relatedSaleEjectRes != null && relatedSaleEjectRes.getInputCount() != null && relatedSaleEjectRes.getUnitCount() != null && relatedSaleEjectRes.getUnitCount() != 0 && relatedSaleEjectRes.getInputCount() != 0) {
                    BigDecimal res = new BigDecimal(relatedSaleEjectRes.getUnitCount()).divide(new BigDecimal(relatedSaleEjectRes.getInputCount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                    i.setEjectRate(res.setScale(2, BigDecimal.ROUND_HALF_UP));
                    i.setEjectRateStr(res.toPlainString() + "%");
                } else {
                    i.setEjectRate(new BigDecimal(0));
                    i.setEjectRateStr(0 + "%");
                }
                GetSalesSummaryOfSalesmenReportOutDataUnion countAndAmt = countAndAmtMap.get(i.getSellerCode());
                GetSalesSummaryOfSalesmenReportOutDataUnion unionOrder = unionOrderMap.get(i.getSellerCode());
                i.setBusinessCount(countAndAmtMap.get(i.getSellerCode()) == null ? 0 : countAndAmtMap.get(i.getSellerCode()).getBusinessCount());
                i.setUnionBusinessCount(countAndAmtMap.get(i.getSellerCode()) == null ? 0 : countAndAmtMap.get(i.getSellerCode()).getUnionBusinessCount());
                if (unionOrder != null && unionOrder.getUnionBusinessCount() != null && countAndAmt != null && countAndAmt.getUnionBusinessCount() != null && countAndAmt.getUnionBusinessCount() != 0) {
                    i.setGlcjcs(new BigDecimal(countAndAmt.getUnionBusinessCount()));
                    i.setYgltcxpcs(new BigDecimal(unionOrder.getUnionBusinessCount()));
                    BigDecimal res = new BigDecimal(countAndAmt.getUnionBusinessCount()).divide(new BigDecimal(unionOrder.getUnionBusinessCount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                    i.setBusinessCountRate(res);
                    i.setBusinessCountRateStr(res.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "%");
                } else {
                    i.setGlcjcs(new BigDecimal(0));
                    i.setYgltcxpcs(new BigDecimal(0));
                    i.setBusinessCountRate(new BigDecimal(0));
                    i.setBusinessCountRateStr(0 + "%");
                }
                i.setUnionBusinessAmt(countAndAmtMap.get(i.getSellerCode()) == null ? BigDecimal.ZERO : countAndAmtMap.get(i.getSellerCode()).getUnionBusinessAmt().setScale(2, RoundingMode.HALF_UP));
                if (countAndAmt != null && countAndAmt.getBusinessAmt() != null && countAndAmt.getUnionBusinessAmt() != null && countAndAmt.getBusinessAmt().intValue() != 0 && countAndAmt.getUnionBusinessAmt().intValue() != 0) {
                    BigDecimal businessAmt = i.getSsAmount();
                    i.setUnionBusinessAmtRate(businessAmt.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0") : i.getUnionBusinessAmt().divide(businessAmt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
//
//                    BigDecimal res = countAndAmt.getUnionBusinessAmt().divide(businessAmt, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
//                    i.setUnionBusinessAmtRate(res);
                    i.setUnionBusinessAmtRateStr(i.getUnionBusinessAmtRateStr());
                } else {
                    i.setUnionBusinessAmtRate(new BigDecimal(0));
                    i.setUnionBusinessAmtRateStr(0 + "%");
                }
                i.setBusinessAmt(countAndAmtMap.get(i.getSellerCode()) == null ? BigDecimal.ZERO : countAndAmtMap.get(i.getSellerCode()).getBusinessAmt());
                resList.add(i);
            }
        }
        return resList;
    }


    @Override
    public Result unionQueryExport(GetSalesSummaryOfSalesmenReportInData inData) {
        inData.setPageNum(null);
        inData.setPageSize(null);
        PageInfo<GetSalesSummaryOfSalesmenReportOutDataUnion> pageInfo = this.unionQueryReport(inData);
        List<GetSalesSummaryOfSalesmenReportOutDataUnion> resList = pageInfo.getList();
        String fileName = "联合用药-员工";
        if (resList != null && resList.size() > 0) {
            CsvFileInfo csvInfo = null;
            // 导出
            GetSalesSummaryOfSalesmenReportOutDataUnion totalInfo = getTotalInfo(resList);
            totalInfo.setSellerName("合计");
            totalInfo.setSellerCode("");
            resList.add(totalInfo);
            csvInfo = CsvClient.getCsvByteWithSupperClass(resList, fileName, Collections.singletonList((short) 1));
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
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        } else {
            throw new BusinessException("提示：没有查询到数据,请修改查询条件!");
        }
    }

    @Override
    public PageInfo getSalespersonsSalesDetails(GetSalesSummaryOfSalesmenReportInData inData) {
        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getBillNo())) {
            inData.setBillNoArr(inData.getBillNo().split("\\s+ |\\s+"));
        }
        if (StringUtils.isNotEmpty(inData.getQueryProId())) {
            inData.setProArr(inData.getQueryProId().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (StringUtils.isNotEmpty(inData.getMemberCardId())) {
            inData.setMemberCarArr(inData.getMemberCardId().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy1())) {
            inData.setZdy1(inData.getProZdy1().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy2())) {
            inData.setZdy2(inData.getProZdy2().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy3())) {
            inData.setZdy3(inData.getProZdy3().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy4())) {
            inData.setZdy4(inData.getProZdy4().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy5())) {
            inData.setZdy5(inData.getProZdy5().split("\\s+ |\\s+|,"));
        }
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        inData.setFlag(flag);
        // 将销售等级装入销售等级列表
        if (StringUtils.isNotEmpty(inData.getProSaleClass())){
            if (ObjectUtil.isEmpty(inData.getProSaleClassList())){
                ArrayList<String> proSaleClassList = new ArrayList<>();
                proSaleClassList.add(inData.getProSaleClass());
                inData.setProSaleClassList(proSaleClassList);
            }else {
                List<String> proSaleClassList = inData.getProSaleClassList();
                proSaleClassList.add(inData.getProSaleClass());
                inData.setProSaleClassList(proSaleClassList);
            }
        }
        List<SalespersonsSalesDetailsOutData> outData = gaiaSdSaleDMapper.getSalespersonsSalesDetails(inData);
        PageInfo pageInfo;
        SalespersonsSalesDetailsOutTotal outTotal = new SalespersonsSalesDetailsOutTotal();

        if (ObjectUtil.isNotEmpty(outData)) {
            outTotal = gaiaSdSaleDMapper.getSalespersonsSalesDetailsTotal(inData);
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    /**
     * 商品销售明细表导出
     *
     * @param inData
     * @param response
     * @return
     */
    @Override
    public void exportSalespersonsSalesDetails(GetSalesSummaryOfSalesmenReportInData inData, HttpServletResponse response) {
        ExportStatusUtil.checkExportAuthority(inData.getClientId(), inData.getBrId());
        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getBillNo())) {
            inData.setBillNoArr(inData.getBillNo().split("\\s+ |\\s+"));
        }
        if (StringUtils.isNotEmpty(inData.getQueryProId())) {
            inData.setProArr(inData.getQueryProId().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy1())) {
            inData.setZdy1(inData.getProZdy1().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy2())) {
            inData.setZdy2(inData.getProZdy2().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy3())) {
            inData.setZdy3(inData.getProZdy3().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy4())) {
            inData.setZdy4(inData.getProZdy4().split("\\s+ |\\s+|,"));
        }
        if (StringUtils.isNotEmpty(inData.getProZdy5())) {
            inData.setZdy5(inData.getProZdy5().split("\\s+ |\\s+|,"));
        }
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        inData.setFlag(flag);

        CsvFileInfo fileInfo = new CsvFileInfo(new byte[0], 0, "商品销售明细表");

        List<SalespersonsSalesDetailsOutData> list = new ArrayList<>();
        AtomicInteger i = new AtomicInteger(1);
        gaiaSdSaleDMapper.getSalespersonsSalesDetails(inData,resultContext -> {
            SalespersonsSalesDetailsOutData outData = resultContext.getResultObject();
            outData.setIndex(i.get());
            i.getAndIncrement();
            outData.setDiscountRateExport(outData.getDiscountRate().setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            // 将折扣金额转为string
            if (ObjectUtil.isEmpty(outData.getDiscount())){
                outData.setExportDiscount("0.00");
            }else {
                BigDecimal discount = outData.getDiscount();
                String strDiscount = discount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                outData.setExportDiscount(strDiscount+" ");
            }
            list.add(outData);
            if (list.size() == CsvClient.BATCH_SIZE) {
                CsvClient.handle(list, fileInfo);
            }
        });

        CsvClient.endHandle(response,list,fileInfo,()->{
            if (ObjectUtil.isEmpty(fileInfo.getFileContent())){
                throw new BusinessException("导出数据不能为空！");
            }
            SalespersonsSalesDetailsOutTotal outTotal = gaiaSdSaleDMapper.getSalespersonsSalesDetailsTotal(inData);;
            byte[] bytes = ("\r\n合计,,,,,,,,,,,,,,,,,,,,,,,,,,,0.00," + outTotal.getQty() + "," + outTotal.getAmountReceivable() + "," + outTotal.getAmt() + "," + outTotal.getDiscount() + "," + outTotal.getDiscountRate()).getBytes(StandardCharsets.UTF_8);
            byte[] all = ArrayUtils.addAll(fileInfo.getFileContent(), bytes);
            fileInfo.setFileContent(all);
            fileInfo.setFileSize(all.length);
        });
    }

    @Override
    public PageInfo getSalespersonsSalesDetailsByClient(GetSalesSummaryOfSalesmenReportInData inData) {
        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getBillNo())) {
            inData.setBillNoArr(inData.getBillNo().split("\\s+ |\\s+"));
        }
        if (StringUtils.isNotEmpty(inData.getQueryProId())) {
            inData.setProArr(inData.getQueryProId().split("\\s+ |\\s+|,"));
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

        //添加验证，查询时间不能超过90天
        if(DateUtil.differentDaysByMillisecond(DateUtil.getFullDateStartDate(inData.getQueryStartDate()),DateUtil.getFullDateEndDate(inData.getQueryEndDate()))>90){
            throw new BusinessException(" 查询时间不能超过90天！");
        }
        PageInfo pageInfo;
//        List<SalespersonsSalesDetailsOutData> outData = gaiaSdSaleDMapper.getSalespersonsSalesDetails(inData);
        //优化供应商查询的检索方案，检索末次供应商
//        PageHelper.startPage(inData.getPageNum(),inData.getPageSize());
        long start = System.currentTimeMillis();
        List<SalespersonsSalesDetailsOutData> outData = gaiaSdSaleDMapper.getSalespersonsSalesDetailsNew(inData);
        long end = System.currentTimeMillis();
        System.out.println((end-start)/1000 + "================");
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClientId());

        for (SalespersonsSalesDetailsOutData salesDetailsOutData : outData){
            //转换
            if (salesDetailsOutData.getStoAttribute() != null || noChooseFlag){
                salesDetailsOutData.setStoAttribute(StoreAttributeEnum.getName(salesDetailsOutData.getStoAttribute()));
            }
            if (salesDetailsOutData.getStoIfMedical() != null || noChooseFlag){
                salesDetailsOutData.setStoIfMedical(StoreMedicalEnum.getName(salesDetailsOutData.getStoIfMedical()));
            }
            if (salesDetailsOutData.getStoIfDtp() != null || noChooseFlag){
                salesDetailsOutData.setStoIfDtp(StoreDTPEnum.getName(salesDetailsOutData.getStoIfDtp()));
            }
            if (salesDetailsOutData.getStoTaxClass() != null || noChooseFlag){
                salesDetailsOutData.setStoTaxClass(StoreTaxClassEnum.getName(salesDetailsOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(salesDetailsOutData.getStoCode())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect){
                boolean flag = false;
                if (noChooseFlag){
                    if (storeCategoryType.getGssgType().contains("DX0001")){
                        salesDetailsOutData.setShopType(storeCategoryType.getGssgIdName());
                    }else if (storeCategoryType.getGssgType().contains("DX0002")){
                        salesDetailsOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    }else if (storeCategoryType.getGssgType().contains("DX0003")){
                        salesDetailsOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    }else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        salesDetailsOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                }else {
                    if (!stoGssgTypeSet.isEmpty()){
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            salesDetailsOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            salesDetailsOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            salesDetailsOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            salesDetailsOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }

        SalespersonsSalesDetailsOutTotal outTotal = new SalespersonsSalesDetailsOutTotal();

        if (ObjectUtil.isNotEmpty(outData)) {
            //计算合计
            BigDecimal prcOne = BigDecimal.ZERO;
            BigDecimal jdQty = BigDecimal.ZERO;
            BigDecimal amt = BigDecimal.ZERO;
            BigDecimal qty = BigDecimal.ZERO;
            //应收金额
            BigDecimal amountReceivable = BigDecimal.ZERO;
            //成本额
            BigDecimal includeTaxSale = BigDecimal.ZERO;
            //毛利额
            BigDecimal grossProfitMargin = BigDecimal.ZERO;
            //折扣金额
            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal addAmt = BigDecimal.ZERO;
            BigDecimal addTxa = BigDecimal.ZERO;
            for (SalespersonsSalesDetailsOutData data : outData) {
                prcOne = prcOne.add(Objects.nonNull(data.getPrcOne()) ? data.getPrcOne() : BigDecimal.ZERO);
                jdQty = jdQty.add(Objects.nonNull(data.getJdQty()) ? data.getJdQty() : BigDecimal.ZERO);
                amt = amt.add(Objects.nonNull(data.getAmt()) ? data.getAmt() : BigDecimal.ZERO);
                qty = qty.add(Objects.nonNull(data.getQty()) ? data.getQty() : BigDecimal.ZERO);
                amountReceivable = amountReceivable.add(Objects.nonNull(data.getAmountReceivable()) ? data.getAmountReceivable() : BigDecimal.ZERO);
                includeTaxSale = includeTaxSale.add(Objects.nonNull(data.getIncludeTaxSale()) ? data.getIncludeTaxSale() : BigDecimal.ZERO);
                grossProfitMargin = grossProfitMargin.add(Objects.nonNull(data.getGrossProfitMargin()) ? data.getGrossProfitMargin() : BigDecimal.ZERO);
                discount = discount.add(Objects.nonNull(data.getDiscount()) ? data.getDiscount() : BigDecimal.ZERO);
                addAmt = addAmt.add(Objects.nonNull(data.getAddAmt()) ? data.getAddAmt() : BigDecimal.ZERO);
                addTxa = addTxa.add(Objects.nonNull(data.getAddTxa()) ? data.getAddTxa() : BigDecimal.ZERO);

            }
            BigDecimal div = prcOne.multiply(qty);
            //折扣率
            if (BigDecimal.ZERO.compareTo(div) == 0) {
                outTotal.setDiscountRate("0.00%");
            } else {
                outTotal.setDiscountRate(Convert.toStr((prcOne.multiply(jdQty).subtract(amt)).divide(div, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP)).concat("%"));
            }
            if(BigDecimal.ZERO.compareTo(amt) ==0){
                outTotal.setGrossProfitRate("0.00%");
            }else {
                outTotal.setGrossProfitRate(Convert.toStr((amt.subtract(addAmt).subtract(addTxa)).divide(amt,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP)).concat("%"));
            }
            outTotal.setQty(qty);
            outTotal.setAmt(amt);
            outTotal.setAmountReceivable(amountReceivable);
            outTotal.setIncludeTaxSale(includeTaxSale);
            outTotal.setGrossProfitMargin(grossProfitMargin);
            outTotal.setDiscount(discount);
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByPro(GetSalesSummaryOfSalesmenReportInData inData) {
        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getBillNo())) {
            inData.setBillNoArr(inData.getBillNo().split("\\s+ |\\s+"));
//            System.out.println(inData.getBillNoArr());
        }
        if (StringUtils.isNotEmpty(inData.getQueryProId())) {
            inData.setProArr(inData.getQueryProId().split("\\s+ |\\s+|,"));
//            System.out.println(inData.getBillNoArr());
        }
        return gaiaSdSaleDMapper.getSalespersonsSalesByPro(inData);
    }

    @Override
    public List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByUser(GetSalesSummaryOfSalesmenReportInData inData) {
        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getBillNo())) {
            inData.setBillNoArr(inData.getBillNo().split("\\s+ |\\s+"));
            System.out.println(inData.getBillNoArr());
        }
        if (StringUtils.isNotEmpty(inData.getQueryProId())) {
            inData.setProArr(inData.getQueryProId().split("\\s+ |\\s+|,"));
//            System.out.println(inData.getBillNoArr());
        }
        return gaiaSdSaleDMapper.getSalespersonsSalesByUser(inData);
    }

    @Override
    public List<SalespersonsSalesDetailsOutData> getSalespersonsSalesByDoctor(GetSalesSummaryOfSalesmenReportInData inData) {
        if (StringUtils.isEmpty(inData.getQueryStartDate())) {
            throw new BusinessException(" 起始日期不能为空！");
        }
        if (StringUtils.isEmpty(inData.getQueryEndDate())) {
            throw new BusinessException(" 结束日期不能为空！");
        }
        if (StringUtils.isNotEmpty(inData.getBillNo())) {
            inData.setBillNoArr(inData.getBillNo().split("\\s+ |\\s+"));
            System.out.println(inData.getBillNoArr());
        }
        if (StringUtils.isNotEmpty(inData.getQueryProId())) {
            inData.setProArr(inData.getQueryProId().split("\\s+ |\\s+|,"));
//            System.out.println(inData.getBillNoArr());
        }
        return gaiaSdSaleDMapper.getSalespersonsSalesByDoctor(inData);
    }

    @Override
    public List<SupplierInfoDTO> getSupByClient(String client) {
        return gaiaSdSaleDMapper.getSupByClient(client);
    }

    @Override
    public List<String> getProStatusList(GetLoginOutData userInfo) {
        List<String> resList = new ArrayList<>();
        List<String> proStatusList = gaiaSdSaleDMapper.getProStatusList(userInfo.getClient());
        if(CollectionUtil.isNotEmpty(proStatusList)){
            resList = proStatusList.stream().filter(x->x!=null).collect(Collectors.toList());
        }
        return resList;
    }

    private GetSalesSummaryOfSalesmenReportOutDataUnion getTotalInfo(List<GetSalesSummaryOfSalesmenReportOutDataUnion> resList) {
        GetSalesSummaryOfSalesmenReportOutDataUnion res = new GetSalesSummaryOfSalesmenReportOutDataUnion();
        Integer salesDaysTotal = 0;
        BigDecimal ssAmount = BigDecimal.ZERO;
        Integer tradedTime = 0;
        Integer xspx = 0;
        BigDecimal visitUnitPrice = BigDecimal.ZERO;
        BigDecimal kpc = BigDecimal.ZERO;
        BigDecimal pdj = BigDecimal.ZERO;
        Integer inputCount = 0;
        Integer unitCount = 0;
        BigDecimal ejectRate = BigDecimal.ZERO;
        ;//弹出率
        Integer unionBusinessCount = 0;
        BigDecimal businessCountRate = BigDecimal.ZERO;
        BigDecimal unionBusinessAmt = BigDecimal.ZERO;
        BigDecimal unionBusinessAmtRate = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        BigDecimal glcjcs = BigDecimal.ZERO;//关联成交次数
        BigDecimal ygltcxpcs = BigDecimal.ZERO;//有关联弹出的小票张数
        Integer totalQyt = 0;
        if (CollectionUtil.isNotEmpty(resList)) {
            for (GetSalesSummaryOfSalesmenReportOutDataUnion union : resList) {
                totalAmt = totalAmt.add(union.getTotalAmt());
                totalQyt = totalQyt + union.getTotalQyt();
                xspx = xspx + union.getXspx();
                if (StrUtil.isNotBlank(union.getSalesDays())) {
                    salesDaysTotal = salesDaysTotal + Integer.parseInt(union.getSalesDays());
                }
                if (!ObjectUtils.isEmpty(union.getSsAmount())) {
                    ssAmount = ssAmount.add(union.getSsAmount());
                }
                if (union.getJycs() != null) {
                    tradedTime = tradedTime + Integer.parseInt(union.getTradedTime());
                }
                if (StrUtil.isNotBlank(union.getVisitUnitPrice())) {
                    visitUnitPrice = visitUnitPrice.add(new BigDecimal(union.getVisitUnitPrice()));
                }
                if (StrUtil.isNotBlank(union.getKpc())) {
                    kpc = kpc.add(new BigDecimal(union.getKpc()));
                }
                if (StrUtil.isNotBlank(union.getPdj())) {
                    pdj = pdj.add(new BigDecimal(union.getPdj()));
                }
                if (union.getInputCount() != null) {
                    inputCount = inputCount + union.getInputCount();
                }
                if (union.getUnitCount() != null) {
                    unitCount = unitCount + union.getUnitCount();
                }
                if (union.getEjectRate() != null) {
                    ejectRate = ejectRate.add((union.getEjectRate()));
                }
                if (union.getUnionBusinessCount() != null) {
                    unionBusinessCount = unionBusinessCount + union.getUnionBusinessCount();
                }
                if (union.getBusinessCountRate() != null) {
                    businessCountRate = businessCountRate.add((union.getBusinessCountRate()));
                }
                if (union.getUnionBusinessAmt() != null) {
                    unionBusinessAmt = unionBusinessAmt.add(union.getUnionBusinessAmt());
                }
                if (union.getUnionBusinessAmtRate() != null) {
                    unionBusinessAmtRate = unionBusinessAmtRate.add(union.getUnionBusinessAmtRate());
                }
                if (union.getGlcjcs() != null) {
                    glcjcs = glcjcs.add((union.getGlcjcs()));
                }
                if (union.getYgltcxpcs() != null) {
                    ygltcxpcs = ygltcxpcs.add((union.getYgltcxpcs()));
                }


            }
            res.setSalesDays(salesDaysTotal + "");
            res.setSsAmount(ssAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
            res.setTradedTime(tradedTime + "");
            res.setVisitUnitPrice(tradedTime.equals(0)?"0":res.getSsAmount().divide(new BigDecimal(tradedTime), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).toPlainString() + "");
            res.setKpc(Objects.equals(res.getTradedTime(),"0")?"0":new BigDecimal(xspx).divide(new BigDecimal(res.getTradedTime()), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).toPlainString() + "");
            res.setPdj(totalQyt.equals(0)?"0":totalAmt.divide(new BigDecimal(totalQyt), 4, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "");
            res.setInputCount(inputCount);
            res.setUnitCount(unitCount);
            if (inputCount != null && unitCount != null && unitCount != 0 && inputCount != 0) {
                BigDecimal resEjectRate =Objects.equals(inputCount,0)?BigDecimal.ZERO: new BigDecimal(unitCount).divide(new BigDecimal(inputCount), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                res.setEjectRate(resEjectRate.setScale(2, BigDecimal.ROUND_HALF_UP));
                res.setEjectRateStr(resEjectRate.toPlainString() + "%");
            } else {
                res.setEjectRate(new BigDecimal(0));
                res.setEjectRateStr(0 + "%");
            }
//            res.setEjectRate(ejectRate);
//            res.setEjectRateStr(res.getEjectRateStr());
            res.setUnionBusinessCount(unionBusinessCount);
            if (glcjcs.compareTo(BigDecimal.ZERO) != 0 && ygltcxpcs.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal resUnionBusinessCountRate = glcjcs.divide(ygltcxpcs, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                res.setBusinessCountRate(resUnionBusinessCountRate);
                res.setBusinessCountRateStr(resUnionBusinessCountRate.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "%");
            } else {
                res.setBusinessCountRate(new BigDecimal(0));
                res.setBusinessCountRateStr(0 + "%");
            }
//            res.setBusinessCountRate(businessCountRate);
//            res.setBusinessCountRateStr(res.getBusinessCountRateStr());
            res.setUnionBusinessAmt(unionBusinessAmt);

            if (ssAmount.compareTo(BigDecimal.ZERO) != 0 && unionBusinessAmt.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal resUnionBusinessAmtRate = unionBusinessAmt.divide(ssAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
                res.setUnionBusinessAmtRate(resUnionBusinessAmtRate);
                res.setUnionBusinessAmtRateStr(res.getUnionBusinessAmtRateStr());
            } else {
                res.setUnionBusinessAmtRate(new BigDecimal(0));
                res.setUnionBusinessAmtRateStr(0 + "%");
            }
//            res.setUnionBusinessAmtRate(unionBusinessAmtRate);
//            res.setUnionBusinessAmtRateStr(res.getUnionBusinessAmtRateStr());
        }

        return res;
    }

    public PageInfo<GetMemberInfoOutData> queryMemberInfo(GetMemberInfoInData inData) {
        PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        if (ObjectUtil.isEmpty(inData.getMateType())) {
            inData.setMateType("0");
        }
        if (StringUtils.isNotEmpty(inData.getMemberCardId())) {
            inData.setMemberCarArr(inData.getMemberCardId().split("\\s+ |\\s+|,"));
        }
        List<GetMemberInfoOutData> outData = this.gaiaSdSaleDMapper.queryMemberInfo(inData);
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            pageInfo = new PageInfo(outData);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public List<ProductTssxOutData> getProTssx(String client) {
        return productBusinessMapper.getProTssxByClient(client);
    }

    /**
     * 获取加盟商下的所以营业员
     * @param inData
     * @return
     */
    public List<GetUserOutData> selectClientUser(String client) {

//        List<GaiaUserData> out = this.gaiaUserDataMapper.select(gaiaUserData);
        List<GaiaUserData> out = this.gaiaSdSaleDMapper.selectClientUser(client);
        List<GetUserOutData> outDataList = new ArrayList();
        out.forEach((item) -> {
            if ("0".equals(item.getUserSta())) {
                GetUserOutData user = new GetUserOutData();
                user.setUserId(item.getUserId());
                user.setLoginName(item.getUserNam());
                outDataList.add(user);
            }

        });
        return outDataList;
    }
}

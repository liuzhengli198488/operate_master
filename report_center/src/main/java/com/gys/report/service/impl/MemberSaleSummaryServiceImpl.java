//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.GaiaSdMemberBasicMapper;
import com.gys.report.entity.MemberSalesInData;
import com.gys.report.service.MemberSaleSummaryService;
import com.gys.util.CommonUtil;
import java.math.BigDecimal;
import java.util.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gys.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class MemberSaleSummaryServiceImpl implements MemberSaleSummaryService {
    @Autowired
    private GaiaSdMemberBasicMapper gaiaSdMemberBasicMapper;

    public MemberSaleSummaryServiceImpl() {
    }

    public Map<String, Object> selectMemberCollectList(MemberSalesInData inData) {
        Map<String, Object> result = new HashMap();
        Map<String, Object> listNum = new HashMap();
        BigDecimal selledDays = BigDecimal.ZERO;
        BigDecimal gssdnormalAmt = BigDecimal.ZERO;
        BigDecimal ssAmount = BigDecimal.ZERO;
        BigDecimal allCostAmt = BigDecimal.ZERO;
        BigDecimal grossProfitAmt = BigDecimal.ZERO;
        String grossProfitRate = "0.00%";
        BigDecimal tradedTime = BigDecimal.ZERO;
        BigDecimal dailyPayAmt = BigDecimal.ZERO;
        BigDecimal discountAmt = BigDecimal.ZERO;
        String discountRate = "0.00%";
        BigDecimal dailyPayCount = BigDecimal.ZERO;
        BigDecimal proAvgCount = BigDecimal.ZERO;
        BigDecimal billAvgPrice = BigDecimal.ZERO;
        BigDecimal totalSelledDays = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal proCount = BigDecimal.ZERO;
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("开始日期不能为空！");
        } else if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        } else {
            List list;
            Iterator var21;
            Map item;
            String grossProfitRateNum;
            String discountRateNum;
            if (inData.getState().equals("0")) {
                list = this.gaiaSdMemberBasicMapper.selectMemberList(inData);
                if (list.size() > 0 && list != null) {
                    for(var21 = list.iterator(); var21.hasNext(); proCount = proCount.add(new BigDecimal(item.get("proCount").toString()))) {
                        item = (Map)var21.next();
                        totalSelledDays = totalSelledDays.add(new BigDecimal(item.get("totalSelledDays").toString()));
                        selledDays = selledDays.add(new BigDecimal(item.get("selledDays").toString()));
                        gssdnormalAmt = gssdnormalAmt.add(new BigDecimal(item.get("gssdnormalAmt").toString()));
                        ssAmount = ssAmount.add(new BigDecimal(item.get("ssAmount").toString()));
                        allCostAmt = allCostAmt.add(new BigDecimal(item.get("allCostAmt").toString()));
                        grossProfitAmt = grossProfitAmt.add(new BigDecimal(item.get("grossProfitAmt").toString()));
                        grossProfitRateNum = StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").substring(0, StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").indexOf("%"));
                        item.put("grossProfitRate", (new BigDecimal(grossProfitRateNum)).setScale(2, 4) + "%");
                        item.put("discountRate", ObjectUtils.isEmpty(item.get("discountRate"))?BigDecimal.ZERO.setScale(2):item.get("discountRate"));
                        tradedTime = tradedTime.add(new BigDecimal(item.get("tradedTime").toString()));
                        dailyPayAmt = dailyPayAmt.add(new BigDecimal(item.get("dailyPayAmt").toString()));
                        discountAmt = discountAmt.add(new BigDecimal(item.get("discountAmt").toString()));
                        dailyPayCount = dailyPayCount.add(new BigDecimal(item.get("dailyPayCount").toString()));
                        proAvgCount = proAvgCount.add(new BigDecimal(item.get("proAvgCount").toString()));
                        billAvgPrice = billAvgPrice.add(new BigDecimal(item.get("billAvgPrice").toString()));
                        count = count.add(new BigDecimal(item.get("count").toString()));
                    }

                    listNum.put("count", count);
                    listNum.put("proCount", proCount);
                    listNum.put("selledDays", selledDays);
                    listNum.put("gssdnormalAmt", gssdnormalAmt.setScale(2, 4));
                    listNum.put("ssAmount", ssAmount.setScale(2, 4));
                    listNum.put("allCostAmt", allCostAmt.setScale(2, 4));
                    listNum.put("grossProfitAmt", grossProfitAmt.setScale(2, 4));
                    listNum.put("tradedTime", tradedTime);
                    if (selledDays.compareTo(BigDecimal.ZERO) != 0) {
                        dailyPayCount = totalSelledDays.divide(tradedTime, 4, 4).setScale(2, 4);
                    }

                    listNum.put("discountAmt", discountAmt.setScale(2, 4));
                    listNum.put("dailyPayCount", dailyPayCount);
                    if (tradedTime.compareTo(BigDecimal.ZERO) != 0) {
                        dailyPayAmt = ssAmount.divide(tradedTime, 4, 4).setScale(2, 4);
                        proAvgCount = proCount.divide(tradedTime, 2, 4);
                    }

                    if (count.compareTo(BigDecimal.ZERO) != 0) {
                        billAvgPrice = ssAmount.divide(count, 2, 4);
                    }

                    listNum.put("dailyPayAmt", dailyPayAmt.setScale(2, 4));
                    listNum.put("billAvgPrice", billAvgPrice);
                    listNum.put("proAvgCount", proAvgCount);
                    if (ssAmount.compareTo(BigDecimal.ZERO) != 0) {
                        grossProfitRate = grossProfitAmt.divide(ssAmount, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("grossProfitRate", grossProfitRate);
                    if (gssdnormalAmt.compareTo(BigDecimal.ZERO) != 0) {
                        discountRate = discountAmt.divide(gssdnormalAmt, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("discountRate", discountRate);
                    listNum.put("totalSelledDays", totalSelledDays);
                    result.put("list", list);
                    result.put("listNum", listNum);
                }
            } else {
                list = this.gaiaSdMemberBasicMapper.selectMemberStoreList(inData);
                if (list.size() > 0 && list != null) {
                    for(var21 = list.iterator(); var21.hasNext(); proCount = proCount.add(new BigDecimal(item.get("proCount").toString()))) {
                        item = (Map)var21.next();
                        totalSelledDays = totalSelledDays.add(new BigDecimal(item.get("totalSelledDays").toString()));
                        selledDays = selledDays.add(new BigDecimal(item.get("selledDays").toString()));
                        gssdnormalAmt = gssdnormalAmt.add(new BigDecimal(item.get("gssdnormalAmt").toString()));
                        ssAmount = ssAmount.add(new BigDecimal(item.get("ssAmount").toString()));
                        allCostAmt = allCostAmt.add(new BigDecimal(item.get("allCostAmt").toString()));
                        grossProfitAmt = grossProfitAmt.add(new BigDecimal(item.get("grossProfitAmt").toString()));
                        grossProfitRateNum = StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").substring(0, StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").indexOf("%"));
                        item.put("grossProfitRate", (new BigDecimal(grossProfitRateNum)).setScale(2, 4) + "%");
                        item.put("discountRate", ObjectUtils.isEmpty(item.get("discountRate"))?BigDecimal.ZERO.setScale(2):item.get("discountRate"));
                        tradedTime = tradedTime.add(new BigDecimal(item.get("tradedTime").toString()));
                        dailyPayAmt = dailyPayAmt.add(new BigDecimal(item.get("dailyPayAmt").toString()));
                        discountAmt = discountAmt.add(new BigDecimal(item.get("discountAmt").toString()));
                        dailyPayCount = dailyPayCount.add(new BigDecimal(item.get("dailyPayCount").toString()));
                        proAvgCount = proAvgCount.add(new BigDecimal(item.get("proAvgCount").toString()));
                        billAvgPrice = billAvgPrice.add(new BigDecimal(item.get("billAvgPrice").toString()));
                        count = count.add(new BigDecimal(item.get("count").toString()));
                    }

                    listNum.put("count", count);
                    listNum.put("proCount", proCount);
                    listNum.put("selledDays", selledDays);
                    listNum.put("gssdnormalAmt", gssdnormalAmt.setScale(2, 4));
                    listNum.put("ssAmount", ssAmount.setScale(2, 4));
                    listNum.put("allCostAmt", allCostAmt.setScale(2, 4));
                    listNum.put("grossProfitAmt", grossProfitAmt.setScale(2, 4));
                    listNum.put("tradedTime", tradedTime);
                    if (selledDays.compareTo(BigDecimal.ZERO) != 0) {
                        dailyPayCount = totalSelledDays.divide(tradedTime, 4, 4).setScale(2, 4);
                    }

                    listNum.put("discountAmt", discountAmt.setScale(2, 4));
                    listNum.put("dailyPayCount", dailyPayCount);
                    if (tradedTime.compareTo(BigDecimal.ZERO) != 0) {
                        dailyPayAmt = ssAmount.divide(tradedTime, 4, 4).setScale(2, 4);
                        proAvgCount = proCount.divide(tradedTime, 2, 4);
                    }

                    if (count.compareTo(BigDecimal.ZERO) != 0) {
                        billAvgPrice = ssAmount.divide(count, 2, 4);
                    }

                    listNum.put("dailyPayAmt", dailyPayAmt.setScale(2, 4));
                    listNum.put("billAvgPrice", billAvgPrice);
                    listNum.put("proAvgCount", proAvgCount);
                    if (ssAmount.compareTo(BigDecimal.ZERO) != 0) {
                        grossProfitRate = grossProfitAmt.divide(ssAmount, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("grossProfitRate", grossProfitRate);
                    if (gssdnormalAmt.compareTo(BigDecimal.ZERO) != 0) {
                        discountRate = discountAmt.divide(gssdnormalAmt, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("discountRate", discountRate);
                    listNum.put("totalSelledDays", totalSelledDays);
                    result.put("list", list);
                    result.put("listNum", listNum);
                }
            }

            return result;
        }
    }

    public PageInfo selectMenberDetailList(MemberSalesInData inData) {
        PageInfo pageInfo = new PageInfo();;
        Map<String, Object> result = new HashMap();
        Map<String, Object> listNum = new HashMap();
        BigDecimal gssdnormalAmt = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal ssAmount = BigDecimal.ZERO;
        BigDecimal discountAmt = BigDecimal.ZERO;
        String discountRate = "0.00%";
        BigDecimal allCostAmt = BigDecimal.ZERO;
        BigDecimal grossProfitAmt = BigDecimal.ZERO;
        String grossProfitRate = "0.00%";
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setProductClass(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }

        if (ObjectUtil.isNotEmpty(inData.getProCode())) {
            inData.setProductCode(inData.getProCode().split(","));
        }

        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("开始日期不能为空！");
        } else if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        } else {
            //添加验证，查询时间不能超过90天
            if(DateUtil.differentDaysByMillisecond(DateUtil.getFullDateStartDate(inData.getStartDate()),DateUtil.getFullDateEndDate(inData.getEndDate()))>90){
                throw new BusinessException(" 查询时间不能超过90天！");
            }
            List list;
            Iterator var13;
            Map item;
            String grossProfitRateNum;
            String discountRateNum;
            if (inData.getState().equals("0")) {
                if (Objects.nonNull(inData.getPageNum()) && Objects.nonNull(inData.getPageSize())) {
                    PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
                }
                list = this.gaiaSdMemberBasicMapper.selectMemberPro(inData);
                if (list.size() > 0 && list != null) {
                    for(var13 = list.iterator(); var13.hasNext(); count = count.add(new BigDecimal(item.get("count").toString()))) {
                        item = (Map)var13.next();
                        gssdnormalAmt = gssdnormalAmt.add(new BigDecimal(item.get("gssdnormalAmt").toString()));
                        ssAmount = ssAmount.add(new BigDecimal(item.get("ssAmount").toString()));
                        allCostAmt = allCostAmt.add(new BigDecimal(item.get("allCostAmt").toString()));
                        grossProfitAmt = grossProfitAmt.add(new BigDecimal(item.get("grossProfitAmt").toString()));
                        grossProfitRateNum = StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").substring(0, StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").indexOf("%"));
                        item.put("grossProfitRate", (new BigDecimal(grossProfitRateNum)).setScale(2, 4) + "%");
                        discountAmt = discountAmt.add(new BigDecimal(item.get("discountAmt").toString()));
                        discountRateNum = StringUtils.defaultIfEmpty((String)item.get("discountRate"), "0%").substring(0, StringUtils.defaultIfEmpty((String)item.get("discountRate"), "0%").indexOf("%"));
                        item.put("discountRate", (new BigDecimal(discountRateNum)).setScale(2, 4) + "%");
                    }

                    listNum.put("gssdnormalAmt", gssdnormalAmt.setScale(2, 4));
                    listNum.put("ssAmount", ssAmount.setScale(2, 4));
                    listNum.put("allCostAmt", allCostAmt.setScale(2, 4));
                    listNum.put("grossProfitAmt", grossProfitAmt.setScale(2, 4));
                    listNum.put("discountAmt", discountAmt.setScale(2, 4));
                    if (ssAmount.compareTo(BigDecimal.ZERO) != 0) {
                        grossProfitRate = grossProfitAmt.divide(ssAmount, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("grossProfitRate", grossProfitRate);
                    if (gssdnormalAmt.compareTo(BigDecimal.ZERO) != 0) {
                        discountRate = discountAmt.divide(gssdnormalAmt, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("discountRate", discountRate);
                    listNum.put("count", count);
                    /*result.put("list", list);
                    result.put("listNum", listNum);*/
                }
            } else {
                if (Objects.nonNull(inData.getPageNum()) && Objects.nonNull(inData.getPageSize())) {
                    PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
                }
                list = this.gaiaSdMemberBasicMapper.selectMemberProSto(inData);
                System.err.println("list="+list);
                if (list.size() > 0 && list != null) {
                    for(var13 = list.iterator(); var13.hasNext(); count = count.add(new BigDecimal(item.get("count").toString()))) {
                        item = (Map)var13.next();
                        gssdnormalAmt = gssdnormalAmt.add(new BigDecimal(item.get("gssdnormalAmt").toString()));
                        ssAmount = ssAmount.add(new BigDecimal(item.get("ssAmount").toString()));
                        allCostAmt = allCostAmt.add(new BigDecimal(item.get("allCostAmt").toString()));
                        grossProfitAmt = grossProfitAmt.add(new BigDecimal(item.get("grossProfitAmt").toString()));
                        grossProfitRateNum = StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").substring(0, StringUtils.defaultIfEmpty((String)item.get("grossProfitRate"), "0%").indexOf("%"));
                        item.put("grossProfitRate", (new BigDecimal(grossProfitRateNum)).setScale(2, 4) + "%");
                        discountAmt = discountAmt.add(new BigDecimal(item.get("discountAmt").toString()));
                        discountRateNum = StringUtils.defaultIfEmpty((String)item.get("discountRate"), "0%").substring(0, StringUtils.defaultIfEmpty((String)item.get("discountRate"), "0%").indexOf("%"));
                        item.put("discountRate", (new BigDecimal(discountRateNum)).setScale(2, 4) + "%");
                    }

                    listNum.put("gssdnormalAmt", gssdnormalAmt.setScale(2, 4));
                    listNum.put("ssAmount", ssAmount.setScale(2, 4));
                    listNum.put("allCostAmt", allCostAmt.setScale(2, 4));
                    listNum.put("grossProfitAmt", grossProfitAmt.setScale(2, 4));
                    listNum.put("discountAmt", discountAmt.setScale(2, 4));
                    if (ssAmount.compareTo(BigDecimal.ZERO) != 0) {
                        grossProfitRate = grossProfitAmt.divide(ssAmount, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("grossProfitRate", grossProfitRate);
                    if (gssdnormalAmt.compareTo(BigDecimal.ZERO) != 0) {
                        discountRate = discountAmt.divide(gssdnormalAmt, 4, 4).multiply(new BigDecimal("100")).setScale(2, 4) + "%";
                    }

                    listNum.put("discountRate", discountRate);
                    listNum.put("count", count);
                   /* result.put("list", list);
                    result.put("listNum", listNum);*/
                }
            }
            if (CollUtil.isNotEmpty(list)) {
                pageInfo = new PageInfo(list);
                pageInfo.setListNum(listNum);
            }
            return pageInfo;
        }
    }

    public List<LinkedHashMap<String, Object>> selectMemberListCSV(MemberSalesInData inData) {
        return this.gaiaSdMemberBasicMapper.selectMemberList(inData);
    }

    public List<LinkedHashMap<String, Object>> selectMemberStoListCSV(MemberSalesInData inData) {
        return this.gaiaSdMemberBasicMapper.selectMemberStoreList(inData);
    }

    public List<LinkedHashMap<String, Object>> selectMemberProCSV(MemberSalesInData inData) {
        return this.gaiaSdMemberBasicMapper.selectMemberProSto(inData);
    }

    public List<LinkedHashMap<String, Object>> selectMemberProStoCSV(MemberSalesInData inData) {
        return this.gaiaSdMemberBasicMapper.selectMemberProSto(inData);
    }
}

package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.gys.common.data.CommodityClassification;
import com.gys.common.data.CommonVo;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.ViewSourceEnum;
import com.gys.common.exception.BusinessException;
import com.gys.entity.*;
import com.gys.entity.data.dropdata.*;
import com.gys.entity.data.xhl.dto.*;
import com.gys.entity.data.xhl.vo.*;
import com.gys.mapper.GaiaFranchiseeMapper;
import com.gys.mapper.GaiaSdReplenishHMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.mapper.GaiaXhlHMapper;
import com.gys.service.DropRateAppService;
import com.gys.service.DropRateService;
import com.gys.util.ConditionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("dropRateService")
@Transactional(rollbackFor = Exception.class)
public class DropRateServiceImpl implements DropRateService {

    @Resource
    private GaiaXhlHMapper xhlHMapper;
    @Resource
    private GaiaFranchiseeMapper gaiaFranchiseeMapper;
    @Resource
    private GaiaStoreDataMapper storeManager;
    @Resource
    private GaiaSdReplenishHMapper gaiaSdReplenishHMapper;
    private DropRateAppService dropRateAppService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public Map<String, Object> init() {
        Map<String, Object> res = new HashMap<>();
        //????????????????????? "1":?????? "2":?????? "3":??????
        List<CommonVo> reportTypes = new ArrayList<>(Arrays.asList(new CommonVo("1", "??????"), new CommonVo("2", "??????"), new CommonVo("3", "??????")));
        res.put("reportTypes", reportTypes);

        //???????????????????????? 1-??????????????? 2-??????????????? 3-???????????????
        List<CommonVo> rateTypes = new ArrayList<>(Arrays.asList(new CommonVo("1", "???????????????"), new CommonVo("2", "???????????????"), new CommonVo("3", "???????????????")));
        res.put("rateTypes", rateTypes);
        return res;
    }

    @Override
    public PageInfo reportData(GetLoginOutData loginUser, DropRateInData inData) {

        if (StrUtil.isBlank(inData.getStartDate()) || StrUtil.isBlank(inData.getEndDate())) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (StrUtil.isBlank(inData.getReportType())) {
            throw new BusinessException("??????????????????");
        }
        if (inData.getRateType() == null) {
            throw new BusinessException("?????????????????????");
        }
        List<DropRateRes> resList = new ArrayList<>();
        //?????????????????????????????????????????????????????????????????????/???????????????/???????????????/???????????????/
        Map<String, DropRateRes> inConditionDataMap = handleClientBasicInfoMap(loginUser.getClient(), inData);

        //?????????????????????
        Map<String, DropRateRes> averageDataMap = handleAverageInfoMap(inData);

        if (CollectionUtil.isNotEmpty(inConditionDataMap)) {
            for (String key : inConditionDataMap.keySet()) {
                DropRateRes res = inConditionDataMap.get(key);
                if (averageDataMap.get(res.getShowTime()) != null) {
                    res.setAverageUpProductNum(averageDataMap.get(res.getShowTime()).getAverageUpProductNum());
                    res.setAverageDownProductNum(averageDataMap.get(res.getShowTime()).getAverageDownProductNum());
                    res.setAverageRate(new BigDecimal("0.00").compareTo(res.getAverageDownProductNum())  == 0?BigDecimal.ZERO:res.getAverageUpProductNum().divide(res.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                }
                if (res.getAverageRate() != null) {
                    res.setDiff(res.getProductRate().subtract(res.getIndustryAverage()));
                }
                resList.add(res);
            }
        }
        resList = resList.stream().sorted(Comparator.comparing(DropRateRes::getShowTime)).collect(Collectors.toList());
        setRaw(resList,inData.getRateType());
        PageInfo pageInfo; // ??????????????????
        if (CollUtil.isNotEmpty(resList)) {  // ????????????????????????
            pageInfo = new PageInfo(resList);
            DropRateRes totalInfo = handleTotal(resList);
            if(totalInfo!=null){
                totalInfo.setShowTime("??????");
                pageInfo.setListNum(totalInfo);
            }
        } else {
            pageInfo = new PageInfo();
        }
        threadPoolTaskExecutor.execute(() -> dropRateAppService.updateViewSource(loginUser.getClient(), ViewSourceEnum.WEB.type));
        return pageInfo;
    }

    private void setRaw(List<DropRateRes> resList, Integer rateType) {
        if (CollUtil.isNotEmpty(resList)) {
            for (DropRateRes dropRateRes : resList) {
                if (Objects.equals(rateType, 1)) {
                    //????????????
                    dropRateRes.setRawQuantity(dropRateRes.getUpQuantity());
                    dropRateRes.setRawAmount(dropRateRes.getUpAmount());
                    dropRateRes.setRawProductNum(dropRateRes.getUpProductNum());
                } else {
                    dropRateRes.setRawQuantity(dropRateRes.getDownQuantity());
                    dropRateRes.setRawAmount(dropRateRes.getDownAmount());
                    dropRateRes.setRawProductNum(dropRateRes.getDownProductNum());
                }
            }
        }
    }

   /* private void setRaw1(List<ReportInfoSummaryVo> resList){
        if(CollUtil.isNotEmpty(resList)){
            for (ReportInfoSummaryVo dropRateRes : resList) {
              //  dropRateRes.setRawQuantity(dropRateRes.getUpQuantity());
               // dropRateRes.setRawAmount(dropRateRes.getUpAmount());
              //  dropRateRes.setRawProductNum(dropRateRes.getUpProductNum());
            }
        }
    }*/

    private ReportInfoSummaryVo handleTotal1(List<ReportInfoSummaryVo> resList) {
        ReportInfoSummaryVo total = new ReportInfoSummaryVo();
        /*total.setUpQuantity(BigDecimal.ZERO);
        total.setDownQuantity(BigDecimal.ZERO);
        total.setRawQuantity(BigDecimal.ZERO);

        total.setUpAmount(BigDecimal.ZERO);
        total.setDownAmount(BigDecimal.ZERO);
        total.setRawAmount(BigDecimal.ZERO);


        total.setUpProductNum(BigDecimal.ZERO);
        total.setDownProductNum(BigDecimal.ZERO);
        total.setRawProductNum(BigDecimal.ZERO);*/


        total.setAverageUpProductNum(BigDecimal.ZERO);
        total.setAverageDownProductNum(BigDecimal.ZERO);

        total.setIndustryAverage(BigDecimal.ZERO);


        BigDecimal count = BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(resList)){
            for(ReportInfoSummaryVo res : resList){
                // ??????
                total.setOrderNum(total.getOrderNum().add(res.getOrderNum()));
                total.setDnNum(total.getDnNum().add(res.getDnNum()));
                total.setSendNum(total.getSendNum().add(res.getSendNum()));
                total.setGzNum(total.getGzNum().add(res.getGzNum()));
                total.setFinalNum(total.getFinalNum().add(res.getFinalNum()));
              //  total.setRawQuantity(total.getRawQuantity().add(res.getRawQuantity()));
                // ??????
                total.setOrderAmount(total.getOrderAmount().add(res.getOrderAmount()));
                total.setDnAmount(total.getDnAmount().add(res.getDnAmount()));
                total.setSendAmount(total.getSendAmount().add(res.getSendAmount()));
                total.setGzAmount(total.getGzAmount().add(res.getGzAmount()));
                total.setFinalOrderAmount(total.getFinalOrderAmount().add(res.getFinalOrderAmount()));

                // ??????
                total.setOrderProductNum(total.getOrderProductNum().add(res.getOrderProductNum()));
                total.setDnProductNum(total.getDnProductNum().add(res.getDnProductNum()));
                total.setSendProductNum(total.getSendProductNum().add(res.getSendProductNum()));
                total.setGzProductNum(total.getGzProductNum().add(res.getGzProductNum()));
                total.setFinalProductNum(total.getFinalProductNum().add(res.getFinalProductNum()));

               /* total.setUpAmount(total.getUpAmount().add(res.getUpAmount()));
                total.setDownAmount(total.getDownAmount().add(res.getDownAmount()));
                total.setRawAmount(total.getRawAmount(). add(res.getRawAmount()));

                total.setUpProductNum(total.getUpProductNum().add(res.getUpProductNum()));
                total.setDownProductNum(total.getDownProductNum().add(res.getDownProductNum()));
                total.setRawProductNum(total.getRawProductNum().add(res.getRawProductNum()));*/

                total.setAverageUpProductNum(total.getAverageUpProductNum().add(res.getAverageUpProductNum()));
                total.setAverageDownProductNum(total.getAverageDownProductNum().add(res.getAverageDownProductNum()));

                total.setIndustryAverage(total.getIndustryAverage().add(res.getIndustryAverage()));
                total.setReplenishmentStoreNum(total.getReplenishmentStoreNum()+res.getReplenishmentStoreNum());
                count = count.add(new BigDecimal("1"));
            }
            total.setDistributionNumRate(total.getDnNum().divide(total.getOrderNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            if(total.getSendNum().compareTo(BigDecimal.ZERO)>0){
                total.setSendNumRate(total.getGzNum().divide(total.getSendNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }else {
                total.setSendNumRate(BigDecimal.ZERO);
            }
            if(total.getFinalNum().compareTo(BigDecimal.ZERO)>0){
                total.setFinalNumRate(total.getGzNum().divide(total.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }else {
                total.setFinalNumRate(BigDecimal.ZERO);
            }


            total.setDistributionNumRateStr(total.getDistributionNumRate().toPlainString()+"%");
            total.setSendNumRateStr(total.getSendNumRate().toPlainString()+"%");
            total.setFinalNumRateStr(total.getFinalNumRate().toPlainString()+"%");

            total.setDistributionAmountRate(total.getDnAmount().divide(total.getOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            if(total.getSendAmount().compareTo(BigDecimal.ZERO)>0){
                total.setSendAmountRate(total.getGzAmount().divide(total.getSendAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }else {
                total.setSendAmountRate(BigDecimal.ZERO);
            }
            if(total.getFinalOrderAmount().compareTo(BigDecimal.ZERO)>0){
                total.setFinalAmountRate(total.getGzAmount().divide(total.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }else {
                total.setFinalAmountRate(BigDecimal.ZERO);
            }


            total.setDistributionAmountRateStr(total.getDistributionAmountRate().toPlainString()+"%");
            total.setSendAmountRateStr(total.getSendAmountRate().toPlainString()+"%");
            total.setFinalAmountRateStr(total.getFinalAmountRate().toPlainString()+"%");

            total.setDistributionProductRate(total.getDnProductNum().divide(total.getOrderProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            if(total.getSendProductNum().compareTo(BigDecimal.ZERO)>0){
                total.setSendProductRate(total.getGzProductNum().divide(total.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }else {
                total.setSendProductRate(BigDecimal.ZERO);
            }
            if(total.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                total.setFinalProductRate(total.getGzProductNum().divide(total.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }else {
                total.setFinalProductRate(BigDecimal.ZERO);
            }


            total.setDistributionProductRateStr(total.getDistributionProductRate().toPlainString()+"%");
            total.setSendProductRateStr(total.getSendProductRate().toPlainString()+"%");
            total.setFinalProductRateStr(total.getFinalProductRate().toPlainString()+"%");

            //total.setQuantityRate(new BigDecimal("0.00").compareTo(total.getDownQuantity())  == 0?BigDecimal.ZERO:total.getUpQuantity().divide(total.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2));
            //total.setAmountRate(new BigDecimal("0.00").compareTo(total.getDownAmount())  == 0?BigDecimal.ZERO:total.getUpAmount().divide(total.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2));
            //total.setProductRate(new BigDecimal("0.00").compareTo(total.getDownProductNum())  == 0?BigDecimal.ZERO:total.getUpProductNum().divide(total.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2));
            total.setAverageRate(new BigDecimal("0.00").compareTo(total.getAverageDownProductNum())  == 0?BigDecimal.ZERO:total.getAverageUpProductNum().divide(total.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));

            BigDecimal ind = new BigDecimal("0.00").compareTo(total.getIndustryAverage())  == 0?BigDecimal.ZERO: total.getIndustryAverage().divide(count,4,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP);
            total.setDiff(total.getAverageRate().subtract(ind));
            total.setIndustryAverage(ind);
        }else{
            return null;
        }

        return total;
    }

    private DropRateRes handleTotal(List<DropRateRes> resList) {
        DropRateRes total = new DropRateRes();
        total.setUpQuantity(BigDecimal.ZERO);
        total.setDownQuantity(BigDecimal.ZERO);
        total.setRawQuantity(BigDecimal.ZERO);

        total.setUpAmount(BigDecimal.ZERO);
        total.setDownAmount(BigDecimal.ZERO);
        total.setRawAmount(BigDecimal.ZERO);


        total.setUpProductNum(BigDecimal.ZERO);
        total.setDownProductNum(BigDecimal.ZERO);
        total.setRawProductNum(BigDecimal.ZERO);


        total.setAverageUpProductNum(BigDecimal.ZERO);
        total.setAverageDownProductNum(BigDecimal.ZERO);

        total.setIndustryAverage(BigDecimal.ZERO);


        BigDecimal count = BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(resList)){
            for(DropRateRes res : resList){
                total.setUpQuantity(total.getUpQuantity().add(res.getUpQuantity()));
                total.setDownQuantity(total.getDownQuantity().add(res.getDownQuantity()));
                total.setRawQuantity(total.getRawQuantity().add(res.getRawQuantity()));

                total.setUpAmount(total.getUpAmount().add(res.getUpAmount()));
                total.setDownAmount(total.getDownAmount().add(res.getDownAmount()));
                total.setRawAmount(total.getRawAmount().add(res.getRawAmount()));

                total.setUpProductNum(total.getUpProductNum().add(res.getUpProductNum()));
                total.setDownProductNum(total.getDownProductNum().add(res.getDownProductNum()));
                total.setRawProductNum(total.getRawProductNum().add(res.getRawProductNum()));

                total.setAverageUpProductNum(total.getAverageUpProductNum().add(res.getAverageUpProductNum()));
                total.setAverageDownProductNum(total.getAverageDownProductNum().add(res.getAverageDownProductNum()));

                total.setIndustryAverage(total.getIndustryAverage().add(res.getIndustryAverage()));
                count = count.add(new BigDecimal("1"));
            }
            total.setQuantityRate(new BigDecimal("0.00").compareTo(total.getDownQuantity())  == 0?BigDecimal.ZERO:total.getUpQuantity().divide(total.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2));
            total.setAmountRate(new BigDecimal("0.00").compareTo(total.getDownAmount())  == 0?BigDecimal.ZERO:total.getUpAmount().divide(total.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2));
            total.setProductRate(new BigDecimal("0.00").compareTo(total.getDownProductNum())  == 0?BigDecimal.ZERO:total.getUpProductNum().divide(total.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2));
            total.setAverageRate(new BigDecimal("0.00").compareTo(total.getAverageDownProductNum())  == 0?BigDecimal.ZERO:total.getAverageUpProductNum().divide(total.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
            BigDecimal ind = new BigDecimal("0.00").compareTo(total.getIndustryAverage())  == 0?BigDecimal.ZERO: total.getIndustryAverage().divide(count,4,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP);
            total.setDiff(total.getProductRate().subtract(ind));
            total.setIndustryAverage(ind);
        }else{
            return null;
        }

        return total;
    }

    /**
     * ???????????????????????????,???????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    private Map<String, ReportInfoSummaryVo> handleAverageInfoMap(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        Map<String, ReportInfoSummaryVo> resMap = new HashMap<>();
        //List<GaiaXhlH> dbDataList = findInConditionData(dto);
        // ????????????
        if (CollectionUtil.isNotEmpty(dbDataList)) {
            List<ReportInfoSummaryVo> finalDataList = handleAverageDbDataByReportType(dbDataList, dto);
            if (CollectionUtil.isNotEmpty(finalDataList)) {
                BigDecimal averageUpProductNum = BigDecimal.ZERO;
                BigDecimal averageDownProductNum = BigDecimal.ZERO;
                finalDataList.forEach(x -> {
                    x.setAverageUpProductNum(averageUpProductNum.add(x.getAverageUpProductNum()));
                    x.setAverageDownProductNum(averageDownProductNum.add(x.getAverageDownProductNum()));
                    resMap.put(x.getDate(), x);
                });
            }
        }
        return resMap;
    }

    /**
     * ???????????????????????????,???????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    private Map<String, DropRateRes> handleAverageInfoMap(DropRateInData inData) {
        Map<String, DropRateRes> resMap = new HashMap<>();
        List<GaiaXhlH> dbDataList = this.findInConditionData(null, inData);
        if (CollectionUtil.isNotEmpty(dbDataList)) {
            List<DropRateRes> finalDataList = handleAverageDbDataByReportType(dbDataList, inData);
            if (CollectionUtil.isNotEmpty(finalDataList)) {
                BigDecimal averageUpProductNum = BigDecimal.ZERO;
                BigDecimal averageDownProductNum = BigDecimal.ZERO;
                finalDataList.forEach(x -> {
                    x.setAverageUpProductNum(averageUpProductNum.add(x.getAverageUpProductNum()));
                    x.setAverageDownProductNum(averageDownProductNum.add(x.getAverageDownProductNum()));
                    resMap.put(x.getShowTime(), x);
                });
            }
        }
        return resMap;
    }

    private List<DropRateRes> handleAverageDbDataByReportType(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        String reportType = inData.getReportType();
        switch (reportType) {
            //??????
            case "1":
                List<DropRateRes> dayList = handleAverageDayData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //??????
            case "2":
                List<DropRateRes> weekList = handleAverageWeekData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(weekList)) {
                    resList = weekList;
                }
                break;
            //??????
            case "3":
                List<DropRateRes> monthList = handleAverageMonthData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(monthList)) {
                    resList = monthList;
                }
                break;
        }
        return resList;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleAverageMonthData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();
        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yy-MM");
            //?????????????????????????????????????????????????????????????????????????????????
            String key = h.getYearName() + "???" + tjDateTranceStr.split("-")[1] + "???";
            DropRateRes res = null;
            if (tempMap.get(key) == null) {
                res = new DropRateRes();
                res.setShowTime(key);
                res.setAverageUpProductNum(h.getUpProductNum()==null?BigDecimal.ZERO:h.getUpProductNum());
                res.setAverageDownProductNum(h.getDownProductNum()==null?BigDecimal.ZERO:h.getDownProductNum());
            } else {
                res = tempMap.get(key);
                res.setAverageUpProductNum(res.getAverageUpProductNum().add(h.getUpProductNum()));
                res.setAverageDownProductNum(res.getAverageDownProductNum().add(h.getDownProductNum()));
            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                DropRateRes data = tempMap.get(key);
                //??????????????????
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * ???????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleAverageWeekData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();
        for (GaiaXhlH h : dbDataList) {
            //?????????????????????????????????????????????????????????????????????????????????
            String key = h.getYearName() + "???" + h.getWeekNum() + "???";
            DropRateRes res = null;
            if (tempMap.get(key) == null) {
                res = new DropRateRes();
                res.setShowTime(key);
                res.setAverageUpProductNum(h.getUpProductNum()==null?BigDecimal.ZERO:h.getUpProductNum());
                res.setAverageDownProductNum(h.getDownProductNum()==null?BigDecimal.ZERO:h.getDownProductNum());
            } else {
                res = tempMap.get(key);
                res.setAverageUpProductNum(res.getAverageUpProductNum().add(h.getUpProductNum()));
                res.setAverageDownProductNum(res.getAverageDownProductNum().add(h.getDownProductNum()));
            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                DropRateRes data = tempMap.get(key);
                //??????????????????
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleAverageDayData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        //db???????????????????????????????????????????????????????????????????????????????????????
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();
        for (GaiaXhlH h : dbDataList) {
            String key = DateUtil.format(h.getTjDate(), "yyyy???MM???dd???");
            DropRateRes res = null;
            if (tempMap.get(key) != null) {
                res = tempMap.get(key);
                res.setAverageUpProductNum(res.getAverageUpProductNum().add(h.getUpProductNum()));
                res.setAverageDownProductNum(res.getAverageDownProductNum().add(h.getDownProductNum()));
            } else {
                res = new DropRateRes();
                res.setAverageUpProductNum(h.getUpProductNum());
                res.setAverageDownProductNum(h.getDownProductNum());
                res.setShowTime(key);
            }
            tempMap.put(key, res);
        }
        if(CollectionUtil.isNotEmpty(tempMap)){
            for(String key : tempMap.keySet()){
                DropRateRes res = tempMap.get(key);
                res.setAverageRate(new BigDecimal("0.00").compareTo(res.getAverageDownProductNum())  == 0?BigDecimal.ZERO:res.getAverageUpProductNum().divide(res.getAverageDownProductNum(),4,RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2,RoundingMode.HALF_UP));
                resList.add(res);
            }
        }
        return resList;
    }

    /**
     * ???????????????4????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param client
     * @param inData
     * @return
     */
    private Map<String, DropRateRes> handleClientBasicInfoMap(String client, DropRateInData inData) {
        Map<String, DropRateRes> resMap = new HashMap<>();
        List<GaiaXhlH> dbDataList = this.findInConditionData(client, inData);
        if (CollectionUtil.isNotEmpty(dbDataList)) {
            List<DropRateRes> finalDataList = handleDbDataByReportType(dbDataList, inData);
            if (CollectionUtil.isNotEmpty(finalDataList)) {
                finalDataList.forEach(x -> {
                    resMap.put(x.getShowTime(), x);
                });
            }
        }
        return resMap;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleDbDataByReportType(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        String reportType = inData.getReportType();
        switch (reportType) {
            //??????
            case "1":
                List<DropRateRes> dayList = handleDayData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //??????
            case "2":
                List<DropRateRes> weekList = handleWeekData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(weekList)) {
                    resList = weekList;
                }
                break;
            //??????
            case "3":
                List<DropRateRes> monthList = handleMonthData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(monthList)) {
                    resList = monthList;
                }
                break;
        }
        return resList;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleMonthData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();

        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yyyy-MM");
            //?????????????????????????????????????????????????????????????????????????????????
            String key = h.getYearName() + "???" + tjDateTranceStr.split("-")[1] + "???";
            DropRateRes res = null;
            if (tempMap.get(key) == null) {
                res = new DropRateRes();
                res.setShowTime(key);

                //??????
                res.setUpQuantity(h.getUpQuantity());
                res.setDownQuantity(h.getDownQuantity());

                //??????
                res.setUpAmount(h.getUpAmount());
                res.setDownAmount(h.getDownAmount());

                //??????
                res.setUpProductNum(h.getUpProductNum());
                res.setDownProductNum(h.getDownProductNum());

                //industryAverage ???????????????
                res.setIndustryAverage(h.getIndustryAverage());
                res.setCount(new BigDecimal("1"));

            } else {
                res = tempMap.get(key);
                //??????
                res.setUpQuantity(res.getUpQuantity().add(h.getUpQuantity()));
                res.setDownQuantity(res.getDownQuantity().add(h.getDownQuantity()));

                //??????
                res.setUpAmount(res.getUpAmount().add(h.getUpAmount()));
                res.setDownAmount(res.getDownAmount().add(h.getDownAmount()));

                //??????
                res.setUpProductNum(res.getUpProductNum().add(h.getUpProductNum()));
                res.setDownProductNum(res.getDownProductNum().add(h.getDownProductNum()));

                //industryAverage ???????????????
                res.setIndustryAverage(res.getIndustryAverage().add(h.getIndustryAverage()));
                res.setCount(res.getCount().add(new BigDecimal("1")));
            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                DropRateRes data = tempMap.get(key);
                //??????????????????
                data.setQuantityRate(new BigDecimal("0.00").compareTo(data.getDownQuantity())  == 0?BigDecimal.ZERO:data.getUpQuantity().divide(data.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                data.setAmountRate(new BigDecimal("0.00").compareTo(data.getDownAmount())  == 0?BigDecimal.ZERO:data.getUpAmount().divide(data.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                data.setProductRate(new BigDecimal("0.00").compareTo(data.getDownProductNum())  == 0?BigDecimal.ZERO:data.getUpProductNum().divide(data.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                data.setIndustryAverage(BigDecimal.ZERO.compareTo(data.getCount()) == 0? BigDecimal.ZERO: data.getIndustryAverage().divide(data.getCount(),4,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * ???????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleWeekData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();

        for (GaiaXhlH h : dbDataList) {
            //?????????????????????????????????????????????????????????????????????????????????
            String key = h.getYearName() + "???" + h.getWeekNum() + "???";
            DropRateRes res = null;
            BigDecimal count = BigDecimal.ZERO;
            if (tempMap.get(key) == null) {
                res = new DropRateRes();
                res.setShowTime(key);

                //??????
                res.setUpQuantity(h.getUpQuantity());
                res.setDownQuantity(h.getDownQuantity());

                //??????
                res.setUpAmount(h.getUpAmount());
                res.setDownAmount(h.getDownAmount());

                //??????
                res.setUpProductNum(h.getUpProductNum());
                res.setDownProductNum(h.getDownProductNum());

                //industryAverage ???????????????
                res.setIndustryAverage(h.getIndustryAverage());
                res.setCount(new BigDecimal("1"));

            } else {
                res = tempMap.get(key);
                //??????
                res.setUpQuantity(res.getUpQuantity().add(h.getUpQuantity()));
                res.setDownQuantity(res.getDownQuantity().add(h.getDownQuantity()));

                //??????
                res.setUpAmount(res.getUpAmount().add(h.getUpAmount()));
                res.setDownAmount(res.getDownAmount().add(h.getDownAmount()));

                //??????
                res.setUpProductNum(res.getUpProductNum().add(h.getUpProductNum()));
                res.setDownProductNum(res.getDownProductNum().add(h.getDownProductNum()));

                //industryAverage ???????????????
                res.setIndustryAverage(res.getIndustryAverage().add(h.getIndustryAverage()));
                res.setCount(res.getCount().add(new BigDecimal("1")));

            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                DropRateRes data = tempMap.get(key);
                //??????????????????
                data.setQuantityRate(new BigDecimal("0.00").compareTo(data.getDownQuantity())  == 0?BigDecimal.ZERO:data.getUpQuantity().divide(data.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                data.setAmountRate(new BigDecimal("0.00").compareTo(data.getDownAmount())  == 0?BigDecimal.ZERO:data.getUpAmount().divide(data.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                data.setProductRate(new BigDecimal("0.00").compareTo(data.getDownProductNum())  == 0?BigDecimal.ZERO:data.getUpProductNum().divide(data.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                data.setIndustryAverage(BigDecimal.ZERO.compareTo(data.getCount()) == 0? BigDecimal.ZERO: data.getIndustryAverage().divide(data.getCount(),4,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleDayData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        //db???????????????????????????????????????????????????????????????????????????????????????
        List<DropRateRes> resList = new ArrayList<>();
        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yyyy???MM???dd???");
            DropRateRes res = new DropRateRes();
            BeanUtils.copyProperties(h, res);
            res.setShowTime(tjDateTranceStr);
            resList.add(res);
        }
        return resList;
    }


    /**
     * ?????????????????????????????????????????????---????????????????????????app?????????????????????
     *
     * @param client
     * @param inData
     * @return
     */
    private List<GaiaXhlH> findInConditionData(String client, DropRateInData inData) {
        List<GaiaXhlH> resList = new ArrayList<>();
        Example example = new Example(GaiaXhlH.class);
        Example.Criteria criteria = example.createCriteria();

        criteria
                .andEqualTo("deleteFlag", 0)
                .andEqualTo("tjType", inData.getRateType())
                .andBetween("tjDate", com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(inData.getStartDate(), "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00"), com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(inData.getEndDate(), "yyyy-MM-dd"), "yyyy-MM-dd 23:59:59"));
        if (StrUtil.isNotBlank(client)) {
            criteria.andEqualTo("client", client);
        }
        List<GaiaXhlH> dbList = this.xhlHMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(dbList)) {
            dbList.forEach(gaiaXhlH->{
                //??????????????????????????????????????????(????????????)
                gaiaXhlH.setUpQuantity(gaiaXhlH.getUpQuantity().setScale(0, BigDecimal.ROUND_HALF_UP));
                //??????????????????????????????????????????(????????????)
                gaiaXhlH.setDownQuantity(gaiaXhlH.getDownQuantity().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????(????????????)
                //gaiaXhlH.setQuantityRate(gaiaXhlH.getQuantityRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setUpAmount(gaiaXhlH.getUpAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setDownAmount(gaiaXhlH.getDownAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????(????????????)
                //gaiaXhlH.setAmountRate(gaiaXhlH.getAmountRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setUpProductNum(gaiaXhlH.getUpProductNum().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setDownProductNum(gaiaXhlH.getDownProductNum().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????(????????????)
                // gaiaXhlH.setProductRate(gaiaXhlH.getProductRate().setScale(0, BigDecimal.ROUND_HALF_UP));
            });
            //?????????????????????????????? 0??? 1???
            if(Objects.equals(1,inData.getShopGoodsType())){
                for (GaiaXhlH gaiaXhlH : dbList) {
                    //??????????????????????????????????????????(????????????)
                    gaiaXhlH.setUpQuantity(gaiaXhlH.getUpQuantityLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //??????????????????????????????????????????(????????????)
                    gaiaXhlH.setDownQuantity(gaiaXhlH.getDownQuantityLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //???????????????(????????????)
                    gaiaXhlH.setQuantityRate(gaiaXhlH.getQuantityRateLess());
                    //???????????????????????????(????????????)
                    gaiaXhlH.setUpAmount(gaiaXhlH.getUpAmountLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //???????????????????????????(????????????)
                    gaiaXhlH.setDownAmount(gaiaXhlH.getDownAmountLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //???????????????(????????????)
                    gaiaXhlH.setAmountRate(gaiaXhlH.getAmountRateLess());
                    //???????????????????????????(????????????)
                    gaiaXhlH.setUpProductNum(gaiaXhlH.getUpProductNumLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //???????????????????????????(????????????)
                    gaiaXhlH.setDownProductNum(gaiaXhlH.getDownProductNumLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //???????????????(????????????)
                    gaiaXhlH.setProductRate(gaiaXhlH.getProductRateLess());
                }
            }
            resList = dbList;
        }
        if(CollUtil.isNotEmpty(resList)) {
            resList = resList.stream().filter(s ->
                    !(s.getUpQuantity().compareTo(BigDecimal.ZERO) == 0) &&
                            !(s.getUpAmount().compareTo(BigDecimal.ZERO) == 0) &&
                            !(s.getUpProductNum().compareTo(BigDecimal.ZERO) == 0)).collect(Collectors.toList());

        }
        return resList;
    }

    /**
     * ??????????????? ??????????????? ??????????????? ??????????????? ?????????
     * ????????????????????????????????????90.0%-95.0%?????????????????????????????????????????????????????????????????????????????????????????????95.0%-100.0%???????????????PS:?????????????????????????????????3
     * ??????????????????map???key
     * finalIndustryAverage ???????????????
     * billingIndustryAverage ???????????????
     * depotIndustryAverage ???????????????
     * @return
     */
    @Override
    public Map<String, BigDecimal> getIndustryAverage(String clientId) {
        //???????????? 2010-01-10??????
        String todayDate = com.gys.util.DateUtil.formatDate2(new Date());
        //????????????
        String yesterdayDate = com.gys.util.DateUtil.getFormatDate(LocalDate.now().minusDays(1));
        //???????????????
        String bigYesterdayDate = com.gys.util.DateUtil.getFormatDate(LocalDate.now().minusDays(2));
        //????????????????????????
        HashMap<String, BigDecimal> conditionData = getConditionData(clientId);
        //conditionData
        return conditionData;
    }




    private HashMap<String, BigDecimal> getConditionData(String client) {
        List<GaiaXhlH> dbList = this.xhlHMapper.selectDate(client);
        HashMap<String, BigDecimal> randomNumber = new HashMap<>();
        if (CollectionUtil.isNotEmpty(dbList)) {
            List<BigDecimal> billingIndustryAverageList = new ArrayList<>();
            List<BigDecimal> depotIndustryAverageList = new ArrayList<>();
            List<BigDecimal> finalIndustryAverageList = new ArrayList<>();
            //??????
            for (GaiaXhlH gaiaXhlH : dbList) {
                if (Objects.equals(gaiaXhlH.getTjType(), 1)) {
                    billingIndustryAverageList.add(gaiaXhlH.getIndustryAverage());//93.88  //98
                } else if (Objects.equals(gaiaXhlH.getTjType(), 2)) {
                    depotIndustryAverageList.add(gaiaXhlH.getIndustryAverage());
                } else if (Objects.equals(gaiaXhlH.getTjType(), 3)) {
                    finalIndustryAverageList.add(gaiaXhlH.getIndustryAverage());
                }
            }
            HashMap<String, BigDecimal> decimalHashMap = getRandomNumber();
            BigDecimal finalIndustryAverage = decimalHashMap.get("finalIndustryAverage");
            BigDecimal billingIndustryAverage = decimalHashMap.get("billingIndustryAverage");
            BigDecimal depotIndustryAverage = decimalHashMap.get("depotIndustryAverage");

            if (CollUtil.isNotEmpty(billingIndustryAverageList)) {
                BigDecimal bigDecimal = BigDecimal.ZERO;
                while (true) {
                    if (billingIndustryAverageList.contains(billingIndustryAverage)) {
                        bigDecimal = new BigDecimal(Convert.toStr(Math.random() * (95.0 - 90.0) + 90.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        billingIndustryAverage = bigDecimal;
                    } else {
                        break;
                    }
                }

            }
            if (CollUtil.isNotEmpty(depotIndustryAverageList)) {
                BigDecimal bigDecimal = BigDecimal.ZERO;
                while (true) {
                    if (depotIndustryAverageList.contains(depotIndustryAverage)) {
                        bigDecimal = new BigDecimal(Convert.toStr(Math.random() * (100.0 - 95.0) + 95.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        depotIndustryAverage = bigDecimal;
                    } else {
                        break;
                    }
                }

            }
            if (CollUtil.isNotEmpty(finalIndustryAverageList)) {
                while (true) {
                    if (finalIndustryAverageList.contains(finalIndustryAverage)) {
                        BigDecimal bigDecimal = new BigDecimal(Convert.toStr(Math.random() * (95.0 - 90.0) + 90.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        //??????????????????
                        finalIndustryAverage = bigDecimal;
                        if (finalIndustryAverage.compareTo(billingIndustryAverage) == 1) {
                            while (true) {
                                if (finalIndustryAverage.compareTo(billingIndustryAverage) == 1) {
                                    BigDecimal bigD = new BigDecimal(Convert.toStr(Math.random() * (95.0 - 90.0) + 90.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                                    finalIndustryAverage = bigD;
                                } else {
                                    break;
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            randomNumber.put("finalIndustryAverage", finalIndustryAverage);
            randomNumber.put("billingIndustryAverage", billingIndustryAverage);
            randomNumber.put("depotIndustryAverage", depotIndustryAverage);
        } else {
            //???????????????????????????
            randomNumber = getRandomNumber();
        }
        return randomNumber;
    }

    /**
     * 1,3?????????90.0%-95.0%????????????
     * 2????????????95.0%-100.0%????????????
     * finalIndustryAverage ???????????????
     * billingIndustryAverage ???????????????
     *  DepotIndustryAverage ???????????????
     * @return
     */
    private HashMap<String, BigDecimal> getRandomNumber() {
        HashMap<String, BigDecimal> hashMap = Maps.newHashMap();
        BigDecimal finalIndustryAverage = new BigDecimal(Convert.toStr(Math.random() * (95.0 - 90.0) + 90.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal billingIndustryAverage = new BigDecimal(Convert.toStr(Math.random() * (95.0 - 90.0) + 90.0)).setScale(2,BigDecimal.ROUND_HALF_UP);
        BigDecimal depotIndustryAverage = new BigDecimal(Convert.toStr(Math.random() * (100.0 - 95.0) + 95.0)).setScale(2,BigDecimal.ROUND_HALF_UP);
        if(finalIndustryAverage.compareTo(billingIndustryAverage) == 1){
            while (true){
                 if(finalIndustryAverage.compareTo(billingIndustryAverage) == 1){
                    finalIndustryAverage = new BigDecimal(Convert.toStr(Math.random() * (95.0 - 90.0) + 90.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                }else {
                    break;
                }
            }
        }
        hashMap.put("finalIndustryAverage",finalIndustryAverage);
        hashMap.put("billingIndustryAverage",billingIndustryAverage);
        hashMap.put("depotIndustryAverage",depotIndustryAverage);
        return hashMap;
    }



    /**
     * ???????????????????????????????????????
     */
    @Override
    public PageInfo getHistoryPostedUnloadRateDetailList(GetLoginOutData loginUser, GetHistoryPostedUnloadRateDetailResponse pageQueryForm) {
        GetHistoryPostedUnloadRateDetailListForm form = new GetHistoryPostedUnloadRateDetailListForm(); //pageQueryForm.getConditions().get(0);

        //????????????
        List<SearchRangeDTO> setCommodityCategory = new ArrayList<>();
        SearchRangeDTO rangeDTO  = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getProClass());
        rangeDTO.setEndRange(pageQueryForm.getProClass());
        setCommodityCategory.add(rangeDTO);
        form.setCommodityCategory(setCommodityCategory);

        //????????????
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartPleaseOrderDate());
        rangeDTO.setEndRange(pageQueryForm.getEndPleaseOrderDate());
        setCommodityCategory.add(rangeDTO);
        form.setPleaseOrderDate(setCommodityCategory);

        //deliveryOrderDate ????????????
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartDeliveryOrderDate());
        rangeDTO.setEndRange(pageQueryForm.getEndDeliveryOrderDate());
        setCommodityCategory.add(rangeDTO);
        form.setDeliveryOrderDate(setCommodityCategory);

        //postingDate ????????????
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartPostingDate());
        rangeDTO.setEndRange(pageQueryForm.getEndPostingDate());
        setCommodityCategory.add(rangeDTO);
        form.setPostingDate(setCommodityCategory);

        //postingDate ????????????
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartCustomerNo());
        rangeDTO.setEndRange(pageQueryForm.getEndCustomerNo());
        setCommodityCategory.add(rangeDTO);
        form.setCustomerNo(setCommodityCategory);



        //????????????
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getProCondition());
        rangeDTO.setEndRange(pageQueryForm.getProCondition());
        setCommodityCategory.add(rangeDTO);
        form.setCommodityCondition(setCommodityCategory);

        log.info("?????????????????????????????????????????????,??????{} " + JSONObject.toJSONString(form));
        loginUser.setClient(null);
        loginUser.setDcCode(null);
        PageInfo pageInfo = new PageInfo();
        SearchRangeDTO pleaseOrderDate = ConditionUtils.getFirstRangeCondition(form.getPleaseOrderDate());//????????????
        SearchRangeDTO deliveryOrderDate = ConditionUtils.getFirstRangeCondition(form.getDeliveryOrderDate());//????????????
        SearchRangeDTO postingDate = ConditionUtils.getFirstRangeCondition(form.getPostingDate());//????????????
        SearchRangeDTO customerNo = ConditionUtils.getFirstRangeCondition(form.getCustomerNo());//????????????
        String commodityCategory = ConditionUtils.getFirstStartCondition(form.getCommodityCategory());//????????????
        String commodityCondition = ConditionUtils.getFirstStartCondition(form.getCommodityCondition());//????????????
        List<GaiaViewWmsTjXhl> list;
        if (StrUtil.isBlank(pageQueryForm.getPageNum()) || StrUtil.isBlank(pageQueryForm.getPageSize())) {
            list = this.xhlHMapper.selectHistoryList(pageQueryForm.getClientId(), pageQueryForm.getDcId(), pageQueryForm.getReplenishStyle(), pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        } else {
            PageHelper.startPage(Integer.parseInt(pageQueryForm.getPageNum()), Integer.parseInt(pageQueryForm.getPageSize()));
            list = this.xhlHMapper.selectHistoryList(pageQueryForm.getClientId(), pageQueryForm.getDcId(), pageQueryForm.getReplenishStyle(), pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
            com.github.pagehelper.PageInfo info = new com.github.pagehelper.PageInfo(list);
            BeanUtil.copyProperties(info,pageInfo);
        }

        if (CollUtil.isEmpty(list)) {
            return pageInfo;
        }
        List<GetHistoryPostedUnloadRateDetailListVO> unloadRateDetailList = list.stream().map(xhl -> {
            if(Objects.nonNull(xhl)){
                if(StrUtil.isNotBlank(xhl.getPleaseorderdate())){
                    xhl.setPleaseorderdate(com.gys.util.DateUtil.dateConvertStr(xhl.getPleaseorderdate()));
                }
                if(StrUtil.isNotBlank(xhl.getOpenorderdate())){
                    xhl.setOpenorderdate(com.gys.util.DateUtil.dateConvertStr(xhl.getOpenorderdate()));
                }
                if(StrUtil.isNotBlank(xhl.getPostingdate())){
                    xhl.setPostingdate(com.gys.util.DateUtil.dateConvertStr(xhl.getPostingdate()));
                }
            }
            GetHistoryPostedUnloadRateDetailListVO vo = new GetHistoryPostedUnloadRateDetailListVO(xhl);
            return vo;
        }).collect(Collectors.toList());
        NumberFormat nformat = NumberFormat.getNumberInstance();
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal zero = new BigDecimal("0.0000");
        //??????????????????
        BigDecimal distributionItems = BigDecimal.ZERO;
        //??????????????????
        BigDecimal deliveryItem = BigDecimal.ZERO;
        //??????????????????
        BigDecimal finalItem = BigDecimal.ZERO;

        Integer number  = 1;
        for (int i = 0; i < unloadRateDetailList.size(); i++) {
            unloadRateDetailList.get(i).setNumber(Convert.toStr(number));
            number +=1;
            String isPost = unloadRateDetailList.get(i).getIsPost();//????????????,0:???,1:???

            //??????
            BigDecimal matmovprice = unloadRateDetailList.get(i).getMatmovprice();
            //??????
            if (matmovprice == null) {
                matmovprice = new BigDecimal("1.0000");
            }

            //????????????
            BigDecimal orderQuantity
                    = unloadRateDetailList.get(i).getOrderQuantity();
            //??????????????????
            BigDecimal deliveryQuantity
                    = unloadRateDetailList.get(i).getDeliveryQuantity();
            //?????????????????????
            BigDecimal deliveryPostedQuantityRate = BigDecimal.ZERO;
            if (null != unloadRateDetailList.get(i).getDeliveryPostedQuantityRate()) {
                deliveryPostedQuantityRate = unloadRateDetailList.get(i).getDeliveryPostedQuantityRate();
            }

            //GetHistoryPostedUnloadRateDetailListVO deliveryRateVO = new GetHistoryPostedUnloadRateDetailListVO();
            if (orderQuantity == null || orderQuantity.equals(zero) || matmovprice.equals(zero)) {
                unloadRateDetailList.get(i).setDeliveryQuantitySatisfiedRate("0.00%");//?????????????????????
                unloadRateDetailList.get(i).setDeliveryAmountSatisfiedRate("0.00%");//?????????????????????
                distributionItems = distributionItems.add(new BigDecimal("1.00"));
            } else {

                //????????????????????? = ??????????????????/????????????
                String deliveryQuantitySatisfiedRate
                        = (deliveryQuantity.divide(orderQuantity, 2, BigDecimal.ROUND_HALF_UP)).multiply(hundred).toString() + "%";
                //????????????????????? = (??????????????????*??????)/(???????????????*??????)
                String deliveryAmountSatisfiedRate
                        = (((deliveryQuantity.multiply(matmovprice))
                        .divide((orderQuantity.multiply(matmovprice)), 2, BigDecimal.ROUND_HALF_UP))).multiply(hundred).toString() + "%";

                unloadRateDetailList.get(i).setDeliveryQuantitySatisfiedRate(deliveryQuantitySatisfiedRate);//?????????????????????
                unloadRateDetailList.get(i).setDeliveryAmountSatisfiedRate(deliveryAmountSatisfiedRate);//?????????????????????

                //????????????
                if (!deliveryQuantitySatisfiedRate.equals("0.00%") && !deliveryAmountSatisfiedRate.equals("0.00%"))
                    distributionItems = distributionItems.add(new BigDecimal("1.00"));
            }

            //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (StringUtils.isNotBlank(isPost) && isPost.equals("1")) {//==================???????????????,????????????????????????,???????????????,??????????????????

                if (deliveryQuantity.equals(zero) || deliveryPostedQuantityRate.equals(zero) || matmovprice.equals(zero)) {
                    unloadRateDetailList.get(i).setShipmentQuantitySatisfiedRate("0.00%");//?????????????????????
                    unloadRateDetailList.get(i).setShipmentAmountSatisfiedRate("0.00%");//?????????????????????
                } else {
                    //????????????????????? = ?????????????????????/??????????????????
                    String shipmentQuantitySatisfiedRate
                            = (deliveryPostedQuantityRate.divide(deliveryQuantity, 2, BigDecimal.ROUND_HALF_UP)).multiply(hundred).toString() + "%";
                    unloadRateDetailList.get(i).setShipmentQuantitySatisfiedRate(shipmentQuantitySatisfiedRate);//?????????????????????
                    //????????????????????? = (?????????????????????*??????)/(??????????????????*??????)
                    String shipmentAmountSatisfiedRate
                            = (((deliveryPostedQuantityRate.multiply(matmovprice))
                            .divide((deliveryQuantity.multiply(matmovprice)), 2, BigDecimal.ROUND_HALF_UP))).multiply(hundred).toString() + "%";
                    unloadRateDetailList.get(i).setShipmentAmountSatisfiedRate(shipmentAmountSatisfiedRate);//?????????????????????
                    deliveryItem = deliveryItem.add(new BigDecimal("1.00"));
                }

                if (orderQuantity == null || orderQuantity.equals(zero) || matmovprice.equals(zero)) {
                    unloadRateDetailList.get(i).setFinalQuantitySatisfiedRate("0.00%");//?????????????????????
                    unloadRateDetailList.get(i).setFinalAmountSatisfiedRate("0.00%");//?????????????????????
                    finalItem = finalItem.add(new BigDecimal("1.00"));
                } else {

                    //????????????????????? = ?????????????????????/????????????
                    String finalQuantitySatisfiedRate
                            = (deliveryPostedQuantityRate.divide(orderQuantity, 2, BigDecimal.ROUND_HALF_UP)).multiply(hundred).toString() + "%";
                    //????????????????????? = (?????????????????????*??????)/(????????????*??????)
                    String finalAmountSatisfiedRate
                            = (((deliveryPostedQuantityRate.multiply(matmovprice))
                            .divide((orderQuantity.multiply(matmovprice)), 2, BigDecimal.ROUND_HALF_UP))).multiply(hundred).toString() + "%";

                    unloadRateDetailList.get(i).setFinalQuantitySatisfiedRate(finalQuantitySatisfiedRate);//?????????????????????
                    unloadRateDetailList.get(i).setFinalAmountSatisfiedRate(finalAmountSatisfiedRate);//?????????????????????

                    //????????????
                    if (!finalQuantitySatisfiedRate.equals("0.00%") && !finalAmountSatisfiedRate.equals("0.00%"))
                        finalItem = finalItem.add(new BigDecimal("1.00"));
                }

            }
            unloadRateDetailList.get(i).setDeliveryItemSatisfiedRate(null);
            unloadRateDetailList.get(i).setShipmentItemSatisfiedRate(null);
            unloadRateDetailList.get(i).setFinalItemSatisfiedRate(null);
        }
        GetHistoryPostedUnloadRateDetailListVO deliveryRateVO = new GetHistoryPostedUnloadRateDetailListVO();
        BigDecimal size = new BigDecimal(unloadRateDetailList.size()).setScale(2, BigDecimal.ROUND_HALF_UP);
        System.err.println("size= " + size);
        // ????????????
        GetHistoryPostedUnloadRateTotalDTO dto = this.xhlHMapper.selectHistoryTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        BigDecimal total = dto.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
        //????????????????????????????????????
        Integer integer = this.xhlHMapper.selectTotalNumber(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        //?????????????????????
        Integer isPostTotal = this.xhlHMapper.selectIsPostTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        //???????????????????????????????????????????????????
        Integer isPostDeliveryTotal = this.xhlHMapper.selectisPostDeliveryTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        //????????????????????????????????????
        Integer isPostNumberTotal = this.xhlHMapper.selectIsPostNumberTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());


        deliveryRateVO.setOrderQuantity(null);
        deliveryRateVO.setOrderQuantityTotal(null);

        deliveryRateVO.setDeliveryQuantity(null);
        deliveryRateVO.setDeliveryQuantityTotal(null);

        deliveryRateVO.setDeliveryQuantitySatisfiedRate(null);
        deliveryRateVO.setDeliveryQuantitySatisfiedRateTotal(null);


        deliveryRateVO.setDeliveryAmountSatisfiedRate(null);
        deliveryRateVO.setDeliveryAmountSatisfiedRateTotal(null);

        deliveryRateVO.setDeliveryPostedQuantityRate(null);
        deliveryRateVO.setDeliveryPostedQuantityRateTotal(null);



        deliveryRateVO.setFinalAmountSatisfiedRate(null);
        deliveryRateVO.setFinalAmountSatisfiedRateTotal(null);

        deliveryRateVO.setFinalQuantitySatisfiedRate(null);
        deliveryRateVO.setFinalQuantitySatisfiedRateTotal(null);

        deliveryRateVO.setShipmentAmountSatisfiedRate(null);
        deliveryRateVO.setShipmentAmountSatisfiedRateTotal(null);

        deliveryRateVO.setShipmentQuantitySatisfiedRate(null);
        deliveryRateVO.setShipmentQuantitySatisfiedRateTotal(null);
        deliveryRateVO.setDeliveryItemSatisfiedRateTotal(BigDecimal.ZERO.compareTo(total)==0?"0.00%":new BigDecimal(integer).divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//?????????????????????
        deliveryRateVO.setDeliveryItemSatisfiedRate(BigDecimal.ZERO.compareTo(total)==0?"0.00%":new BigDecimal(integer).divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//?????????????????????
        if (isPostNumberTotal > 0) {
            deliveryRateVO.setShipmentItemSatisfiedRateTotal(BigDecimal.ZERO.compareTo(new BigDecimal(isPostDeliveryTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostDeliveryTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//?????????????????????
            deliveryRateVO.setShipmentItemSatisfiedRate(BigDecimal.ZERO.compareTo(new BigDecimal(isPostDeliveryTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostDeliveryTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//?????????????????????
            deliveryRateVO.setFinalItemSatisfiedRateTotal(BigDecimal.ZERO.compareTo(new BigDecimal(isPostTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//?????????????????????
            deliveryRateVO.setFinalItemSatisfiedRate(BigDecimal.ZERO.compareTo(new BigDecimal(isPostTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//?????????????????????
        }
        unloadRateDetailList.forEach(detailVO -> {
            //????????????
            CommodityClassification commodityClassification = CommodityClassification.getEnum(
                    StrUtil.isNotBlank(detailVO.getCommodityCategory()) ? detailVO.getCommodityCategory().substring(0,1) : null);
            detailVO.setCommodityCategory(commodityClassification != null ? commodityClassification.getComment()
                    : detailVO.getCommodityCategory());
        });

        pageInfo.setListNum(deliveryRateVO);
        pageInfo.setList(unloadRateDetailList);

        return pageInfo;
    }

    /**
     * ??????????????????(?????????)
     * @param loginUser ???????????????
     * @param clientId ???????????????
     * @param clientName ???????????????
     * @return
     */
    @Override
    public List<GetClientListDTO> getClientList(GetLoginOutData loginUser, String clientName) {
        List<GetClientListDTO> clientList = this.xhlHMapper.getClientList(clientName);
        return clientList;
    }

    /**
     * ??????????????????
     * @param loginUser
     * @return
     */
    @Override
    public List<Map<String, String>> getReplenishStyle(GetLoginOutData loginUser) {

        List<String> mapList = this.xhlHMapper.getReplenishStyle();
        List<Map<String, String>> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(mapList)) {
            for (String type : mapList) {
                Map<String, String> map = Maps.newHashMap();
                if (Objects.equals(type, "0")) {
                    map.put("type", "0");
                    map.put("name", "????????????");
                    list.add(map);
                } else if (Objects.equals(type, "1")) {
                    map.put("type", "1");
                    map.put("name", "????????????");
                    list.add(map);
                } else if (Objects.equals(type, "2")) {
                    map.put("type", "2");
                    map.put("name", "??????");
                    list.add(map);
                } else if (Objects.equals(type, "3")) {
                    map.put("type", "3");
                    map.put("name", "??????");
                    list.add(map);
                } else if (Objects.equals(type, "4")) {
                    map.put("type", "4");
                    map.put("name", "??????");
                    list.add(map);
                }
            }
        }
        return list;

    }

    /**
     * ????????? -- ??????????????????
     */
    @Override
    public List<PullingBean> getCommodityCategoryList(GetLoginOutData loginUser) {
        CommodityClassification[] values = CommodityClassification.values();
        List<PullingBean> list = Stream.of(values).filter(s -> StrUtil.isNotBlank(s.getValue()) && StrUtil.isNotBlank(s.getComment())).map(value -> {
            PullingBean pb = new PullingBean();
            pb.setNumber(value.getValue());
            pb.setName(value.getComment());
            return pb;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public boolean generateHistoryPostedUnloadRate(GetLoginOutData loginUser,GenerateHistoryPostedUnloadRateForm generateHistoryPostedUnloadRateForm) {
        String selDate = generateHistoryPostedUnloadRateForm.getStatisticDate();//????????????
        System.out.println(selDate);
        log.info("???????????????????????????,??????????????????,????????????: " + selDate);

        int days = 0;
        int threshold = 0;
        while (days < 10 && threshold < 20) {
            List<GaiaViewWmsTjXhl> list = this.xhlHMapper.selectListByOpenOrderDate(selDate);

            if (!list.isEmpty()) {
                Map<String, List<GaiaViewWmsTjXhl>> map = list.stream().collect(Collectors.groupingBy(e -> e.getClient() + "_" + e.getPorsite()));
                Set<String> keySet = map.keySet();

                for (String key : keySet) {
                    String[] arr = key.split("_");
                    insertTjXhl(map.get(key), arr[0], arr[1], selDate);
                }
                days++;
            }
            //selDate = getPreviousDay(selDate,"1");
            threshold++;
        }

        return true;
     }



    private void insertTjXhl(List<GaiaViewWmsTjXhl> list, String client, String proSite, String selDate) {
        BigDecimal size = new BigDecimal(list.size()).setScale(2,BigDecimal.ROUND_HALF_UP);

        //????????????
        BigDecimal deliveryItem = BigDecimal.ZERO;
        //????????????
        BigDecimal deliverGoodsItem = BigDecimal.ZERO;
        //????????????
        BigDecimal finalItem = BigDecimal.ZERO;

        //????????????????????????
        BigDecimal deliveryQuantityTotal = BigDecimal.ZERO;
        //?????????????????????
        BigDecimal orderquantityTotal = BigDecimal.ZERO;
        //???????????????
        BigDecimal postedquantityTotal =  BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);

        //???????????????
        BigDecimal orderAmountTotal = BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);
        //??????????????????
        BigDecimal deliveryAmountTotal = BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);
        //????????????????????????
        BigDecimal postAmountTotal = BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);

        for (GaiaViewWmsTjXhl tjXhl : list) {
            deliveryQuantityTotal = deliveryQuantityTotal.add(tjXhl.getDeliveryquantity()).setScale(2, RoundingMode.HALF_UP);
            orderquantityTotal = orderquantityTotal.add(tjXhl.getOrderquantity()).setScale(2, RoundingMode.HALF_UP);
            postedquantityTotal = postedquantityTotal.add(Optional.ofNullable(tjXhl.getDeliverypostedquantityrate())
                    .orElse(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP)));

            //??????????????????
            BigDecimal deliveryquantity = tjXhl.getDeliveryquantity();
            //???????????????
            BigDecimal orderQuantity = tjXhl.getOrderquantity();
            //????????????
            BigDecimal postQuantity = tjXhl.getDeliverypostedquantityrate();
            if(postQuantity ==null){
                postQuantity =  BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);
            }
            //????????????
            BigDecimal matmovprice = tjXhl.getMatmovprice();
            if(matmovprice==null){
                matmovprice = new BigDecimal("1.0000");
            }
            //????????????
            if(!deliveryquantity.equals("0.0000") && !orderQuantity.equals("0.0000") && !matmovprice.equals("0.0000") ){
                deliveryItem = deliveryItem.add(new BigDecimal("1.00"));
            }
            //????????????,????????????
            if(!postQuantity.equals("0.0000")){
                deliverGoodsItem =deliverGoodsItem.add(new BigDecimal("1.00"));
                finalItem = finalItem.add(new BigDecimal("1.00"));
            }
            //???????????????
            orderAmountTotal =orderAmountTotal.add(orderQuantity.multiply(matmovprice));
            //??????????????????
            deliveryAmountTotal = deliveryAmountTotal.add(deliveryquantity.multiply(matmovprice));
            //???????????????
            postAmountTotal = postAmountTotal.add(postQuantity.multiply(matmovprice));
        }
        // ????????????
        TjXhl xhl = new TjXhl();
        xhl.setClient(client);
        xhl.setProSite(proSite);
        xhl.setZyRq(selDate);

        this.xhlHMapper.del(client , proSite, selDate);

        TjXhl tjXhl = new TjXhl();
        tjXhl.setClient(client);//?????????
        tjXhl.setProSite(proSite);//??????
        tjXhl.setZyRq(selDate);//????????????

        //????????????????????? = ????????????????????????/?????????????????????
        tjXhl.setSlMzl(deliveryQuantityTotal.divide(orderquantityTotal,2,RoundingMode.HALF_UP));
        //????????????????????? = ???????????????????????????/???????????????
        tjXhl.setJeMzl(deliveryAmountTotal.divide(orderAmountTotal,2,RoundingMode.HALF_UP));
        //?????????????????????
        tjXhl.setPxMzl(deliveryItem = deliveryItem.divide(size,2,RoundingMode.HALF_UP));
        tjXhl.setZyXl("1");
        //tjXhl.setId();
        this.xhlHMapper.save1(tjXhl);


        //????????????????????? = ????????????????????????/???????????????????????????
        if (deliveryQuantityTotal.compareTo(BigDecimal.ZERO) == 0){
            tjXhl.setSlMzl(new BigDecimal(1));
        }else {
            tjXhl.setSlMzl(postedquantityTotal.divide(deliveryQuantityTotal,2,RoundingMode.HALF_UP));
        }
        //????????????????????? = ????????????????????????/???????????????????????????
        if (deliveryAmountTotal.compareTo(BigDecimal.ZERO) == 0){
            tjXhl.setJeMzl(new BigDecimal(1));
        }else {
            tjXhl.setJeMzl(postAmountTotal.divide(deliveryAmountTotal,2,RoundingMode.HALF_UP));
        }
        //?????????????????????
        tjXhl.setPxMzl(deliveryItem = deliveryItem.divide(size,2,RoundingMode.HALF_UP));
        tjXhl.setZyXl("2");
        //??????
        //this.baseMapper.insert(tjXhl);

        //????????????????????? = ????????????????????????/???????????????
        tjXhl.setSlMzl(postedquantityTotal.divide(orderquantityTotal,2,RoundingMode.HALF_UP));
        //????????????????????? = ????????????????????????/???????????????
        tjXhl.setJeMzl(postAmountTotal.divide(orderAmountTotal,2,RoundingMode.HALF_UP));
        //???????????????
        tjXhl.setPxMzl(finalItem = finalItem.divide(size,2,RoundingMode.HALF_UP));
        tjXhl.setZyXl("3");
        //??????
        //this.baseMapper.insert(tjXhl);
    }
    @Override
    public List<ViewGetProductPositioningListVo> getProductPositioningList(ViewGetProductPositioningListForm viewGetProductPositioningListForm) {
        if(StringUtils.isBlank(viewGetProductPositioningListForm.getProductInfo())){
            return null;
        }
        String productInfo = viewGetProductPositioningListForm.getProductInfo();
        List<ViewProduct> list = this.xhlHMapper.selectProList(productInfo);
        List<ViewGetProductPositioningListVo> productList=new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            ViewGetProductPositioningListVo getProductPositioningListVo;
            for (ViewProduct viewProduct :list){
                getProductPositioningListVo= new ViewGetProductPositioningListVo(viewProduct.getProCode(),viewProduct.getProCode()+" "+viewProduct.getProName());
                if(!productList.contains(getProductPositioningListVo)){
                    productList.add(getProductPositioningListVo);
                }
            }
        }
        return productList;

    }

    //4.??????????????????????????????????????????/???????????????
    //5.
    //6.
    //7.???????????????????????????????????????????????????
    //8.??????????????????????????????????????????????????????
    //9.???????????????????????????????????????????????????

    //10.??????????????????????????????????????????/???????????????
    //11.?????????????????????????????????????????????/??????????????????
    //12.?????????????????????????????????????????????/????????????

    //13.?????????????????????????????????????????????
    //14.????????????????????????????????????????????????????????????
    //15. ?????????????????????????????????????????????
    //16.?????????????????????????????????????????????/??????????????????
    //17.????????????????????????????????????????????????/?????????????????????
    //18.????????????????????????????????????????????????/??????????????????
    //19.??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public PageInfo<ReportInfoVo> getListDropList(ReportInfoDto dto) {
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        String beginDate = com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getBeginDate(), "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00");
        String endDate = com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getEndDate(), "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00");
        dto.setBeginDate(beginDate);
        dto.setEndDate(endDate);
        List<ReportInfoVo> infoVos= xhlHMapper.getListDropList(dto);
        //List<ReportInfoVo> reportInfoVos = infoVos.stream().filter(reportInfoVo -> reportInfoVo.getGzNum().compareTo(BigDecimal.ZERO)>0).collect(Collectors.toList());
        for(ReportInfoVo vo :infoVos){
            //vo.setIndustryAverage(vo.getIndustryAverage());
            //??????????????????????????????????????????/???????????????
            vo.setDistributionNumRate(vo.getDnNum().divide(vo.getOrderNum(),4,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            //?????????????????????????????????????????????/??????????????????
            if(vo.getSendNum().compareTo(BigDecimal.ZERO)==0){
                vo.setSendNumRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setSendNumRate(vo.getGzNum().divide(vo.getSendNum(),4,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //?????????????????????????????????????????????/????????????
            if(vo.getFinalNum().compareTo(BigDecimal.ZERO)==0){
                vo.setFinalNumRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setFinalNumRate(vo.getGzNum().divide(vo.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //??????????????????????????????????????????/???????????????
            if(vo.getOrderAmount().compareTo(BigDecimal.ZERO)==0){
                vo.setDistributionAmountRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setDistributionAmountRate(vo.getDnAmount().divide(vo.getOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //?????????????????????????????????????????????/??????????????????
            if(vo.getSendAmount().compareTo(BigDecimal.ZERO)==0){
                vo.setSendAmountRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setSendAmountRate(vo.getGzAmount().divide(vo.getSendAmount(),4,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //12.?????????????????????????????????????????????/????????????
            if(vo.getFinalOrderAmount().compareTo(BigDecimal.ZERO)==0){
                vo.setFinalAmountRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setFinalAmountRate(vo.getGzAmount().divide(vo.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //?????????????????????????????????????????????/??????????????????
            vo.setDistributionProductRate(vo.getDnProductNum().divide(vo.getOrderProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            //????????????????????????????????????????????????/?????????????????????
            if(vo.getSendProductNum().compareTo(BigDecimal.ZERO)==0) {
                vo.setSendProductRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setSendProductRate(vo.getGzProductNum().divide(vo.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            // ????????????????????????????????????????????????/??????????????????
            if(vo.getFinalProductNum().compareTo(BigDecimal.ZERO)==0){
                vo.setFinalProductRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setFinalProductRate(vo.getGzProductNum().divide(vo.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            List<ReportInfoVo> vos = infoVos.stream().filter(reportInfoVo -> reportInfoVo.getDate().equals(vo.getDate())).collect(Collectors.toList());
             if(CollectionUtils.isNotEmpty(vos)){
                 BigDecimal up = vos.stream().map(ReportInfoVo::getGzProductNum).reduce(BigDecimal.ZERO,BigDecimal::add);
                 BigDecimal down = vos.stream().map(ReportInfoVo::getFinalProductNum).reduce(BigDecimal.ZERO,BigDecimal::add);
                 if(down.compareTo(BigDecimal.ZERO)>0){
                     BigDecimal decimal = up.divide(down, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                     vo.setAverageRateStr(decimal.toPlainString()+"%");
                     vo.setDifference(decimal.subtract(vo.getIndustryAverage()).toPlainString()+"%");
                 }else {
                     vo.setAverageRateStr("0");
                     vo.setDifference(BigDecimal.ZERO.subtract(vo.getIndustryAverage()).toPlainString()+"%");
                 }
                 vo.setIndustryAverage(vo.getIndustryAverage());
                 vo.setIndustryAverageStr(vo.getIndustryAverage().toPlainString()+"%");
             }

          }
         PageInfo pageInfo=new PageInfo(infoVos);
        return pageInfo;
    }




    @Override
    public PageInfo getReportSummary(ReportSummaryDto dto) {
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        //????????????
        List<GaiaXhlH> dbDataList = findInConditionData(dto);
        // ????????????
        Map<String, ReportInfoSummaryVo> inConditionDataMap = handleClientBasicInfoMap(dbDataList,dto);
        //?????????????????????
        Map<String, ReportInfoSummaryVo> averageDataMap = handleAverageInfoMap(dbDataList,dto);

        if (CollectionUtil.isNotEmpty(inConditionDataMap)) {
            for (String key : inConditionDataMap.keySet()) {
                ReportInfoSummaryVo res = inConditionDataMap.get(key);
                if (averageDataMap.get(res.getDate()) != null) {
                    res.setAverageUpProductNum(averageDataMap.get(res.getDate()).getAverageUpProductNum());
                    res.setAverageDownProductNum(averageDataMap.get(res.getDate()).getAverageDownProductNum());
                    res.setAverageRate(new BigDecimal("0.00").compareTo(res.getAverageDownProductNum())  == 0?BigDecimal.ZERO:res.getAverageUpProductNum().divide(res.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                }
                if (res.getAverageRate() != null) {
                    res.setDiff(res.getFinalProductRate().subtract(res.getIndustryAverage()));
                }
                res.setIndustryAverageStr(res.getIndustryAverageStr());
                resList.add(res);
            }
        }
        resList = resList.stream().sorted(Comparator.comparing(ReportInfoSummaryVo::getDate)).collect(Collectors.toList());
        PageInfo pageInfo; // ??????????????????
        if (CollUtil.isNotEmpty(resList)) {  // ????????????????????????
            pageInfo = new PageInfo(resList);
            ReportInfoSummaryVo totalInfo = handleTotal1(resList);
            if(totalInfo!=null){
                totalInfo.setDate("??????");
                pageInfo.setListNum(totalInfo);
            }
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }



    private Map<String, ReportInfoSummaryVo> handleClientBasicInfoMap(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        // ??????????????????
        List<String> collect = dbDataList.stream().map(GaiaXhlH::getClient).distinct().collect(Collectors.toList());
        Map<String, ReportInfoSummaryVo> resMap = new HashMap<>();
        //??????????????????
        ReplenishDto replenishDto =new ReplenishDto();
        replenishDto.setClients(collect);
        replenishDto.setBeginDate(com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getStartDate(), "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00"));
        replenishDto.setEndDate(com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getEndDate(), "yyyy-MM-dd"), "yyyy-MM-dd 23:59:59"));
       // List<ReplenishVo> replenishVos =  gaiaSdReplenishHMapper.getAllReplenishes(replenishDto);
          if(CollectionUtils.isEmpty(collect)){
              // ???????????????????????????
              return resMap;
          }
        Map<String, List<ClientBaseInfoVo>> stringListMap = gaiaFranchiseeMapper.getClientsInfo(collect).stream().collect(Collectors.groupingBy(ClientBaseInfoVo::getClient));
        if (CollectionUtil.isNotEmpty(dbDataList)) {
            //????????????
            List<ReportInfoSummaryVo> finalDataList = handleDbDataByReportType(dbDataList, dto,replenishDto);
            // ?????? ??????
            if (CollectionUtil.isNotEmpty(finalDataList)) {
                finalDataList.forEach(x -> {
                    x.setProvince(stringListMap.get(x.getClient()).get(0).getFrancProv());
                    x.setCity(stringListMap.get(x.getClient()).get(0).getFrancCity());
                    x.setFrancName(stringListMap.get(x.getClient()).get(0).getFrancName());
                    resMap.put(x.getDate()+x.getClient(), x);
                });
            }
        }
        return resMap;
    }

    private List<GaiaXhlH> findInConditionData(ReportSummaryDto dto) {
        List<GaiaXhlH> resList = new ArrayList<>();
        String startDate = com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getStartDate(), "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00");
        String endDate = com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getEndDate(), "yyyy-MM-dd"), "yyyy-MM-dd 23:59:59");
        // ????????????
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        //?????? ????????????????????????????????????
        List<GaiaXhlH> dbList = new ArrayList<>();
        if (dto.getTag() == null || dto.getTag() == 0) {
            //?????????????????????
            dbList = xhlHMapper.getSummaryListLess(dto);
        } else {
            // ??????????????????
            dbList = xhlHMapper.getSummaryList(dto);
        }
        if (CollectionUtil.isNotEmpty(dbList)) {
            dbList.forEach(gaiaXhlH -> {
                //??????????????????????????????????????????(????????????)
                gaiaXhlH.setUpQuantity(gaiaXhlH.getUpQuantity().setScale(2, BigDecimal.ROUND_HALF_UP));
                //??????????????????????????????????????????(????????????)
                gaiaXhlH.setDownQuantity(gaiaXhlH.getDownQuantity().setScale(2, BigDecimal.ROUND_HALF_UP));
                //???????????????(????????????)
                //gaiaXhlH.setQuantityRate(gaiaXhlH.getQuantityRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setUpAmount(gaiaXhlH.getUpAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setDownAmount(gaiaXhlH.getDownAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
                //???????????????(????????????)
                //gaiaXhlH.setAmountRate(gaiaXhlH.getAmountRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setUpProductNum(gaiaXhlH.getUpProductNum().setScale(2, BigDecimal.ROUND_HALF_UP));
                //???????????????????????????(????????????)
                gaiaXhlH.setDownProductNum(gaiaXhlH.getDownProductNum().setScale(2, BigDecimal.ROUND_HALF_UP));
                //???????????????(????????????)
                // gaiaXhlH.setProductRate(gaiaXhlH.getProductRate().setScale(0, BigDecimal.ROUND_HALF_UP));
            });
            resList = dbList;
        }
        // ??????????????????????????????0
        if (CollUtil.isNotEmpty(resList)) {
            resList = resList.stream().filter(s ->
                   (s.getDownQuantity().compareTo(BigDecimal.ZERO) > 0)
                    ||(s.getDownAmount().compareTo(BigDecimal.ZERO) > 0) ||
                    (s.getDownProductNum().compareTo(BigDecimal.ZERO) >0)).collect(Collectors.toList());
        }
        return resList;
    }

    private List<ReportInfoSummaryVo> handleDbDataByReportType(List<GaiaXhlH> dbDataList, ReportSummaryDto dto, ReplenishDto replenishDto) {
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        String reportType = dto.getReportType();
        switch (reportType) {
            //??????
            case "1":
                List<ReportInfoSummaryVo> dayList = handleDayData(dbDataList, dto,replenishDto);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //??????
            case "2":
                List<ReportInfoSummaryVo> weekList = handleWeekData(dbDataList, dto,replenishDto);
                if (CollectionUtil.isNotEmpty(weekList)) {
                   resList = weekList;
                }
                break;
            //??????
            case "3":
                List<ReportInfoSummaryVo> monthList = handleMonthData(dbDataList, dto,replenishDto);
                if (CollectionUtil.isNotEmpty(monthList)) {
                    resList = monthList;
                }
                break;
        }
        return resList;
    }

    // ???????????????
    private List<ReportInfoSummaryVo> handleMonthData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto,ReplenishDto replenishDto) {
        //b.split("-")[1]
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        List<ReplenishVo> allReplenishes = gaiaSdReplenishHMapper.getMonthReplenishes(replenishDto);
        Map<String, Map<String, Map<String, Map<Date, List<GaiaXhlH>>>>> collect = dbDataList.stream().collect(Collectors.groupingBy(GaiaXhlH::getYearName, Collectors.groupingBy(gaiaXhlH -> DateUtil.format(gaiaXhlH.getTjDate(), "yyyy-MM").split("-")[1], Collectors.groupingBy(GaiaXhlH::getClient, Collectors.groupingBy(GaiaXhlH::getTjDate)))));
        for (String year : collect.keySet()) {
            Map<String, Map<String, Map<Date, List<GaiaXhlH>>>> stringMapMap = collect.get(year);
            // ??????
            for (String month : stringMapMap.keySet()) {
                // client
                Map<String, Map<Date, List<GaiaXhlH>>> mapMap = stringMapMap.get(month);
                for (String client : mapMap.keySet()) {
                    Map<Date, List<GaiaXhlH>> dateListMap = mapMap.get(client);
                    Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();

                    for (Date date : dateListMap.keySet()) {
                      Integer num= getMonthReplenishNum(allReplenishes,year,month,client);
                        List<GaiaXhlH> list = dateListMap.get(date);
                        StringBuilder stringBuilder = new StringBuilder();
                        String dateSrt = stringBuilder.append(year).append("???").append(DateUtil.format(list.get(0).getTjDate(), "yyyy-MM").split("-")[1]).append("???").toString();
                        String key = stringBuilder.append(year).append("???").append(DateUtil.format(list.get(0).getTjDate(), "yyyy-MM").split("-")[1]).append("???").toString();
                        vo.setDate(dateSrt);
                        vo.setClient(client);
                        //?????????????????????????????????????????????????????????????????????????????????
                        if (tempMap.get(key) == null) {
                            if (list.size() ==1) {
                                // ??????????????????????????? ???????????????
                                vo.setOrderNum(list.get(0).getDownQuantity());
                                vo.setDnNum(list.get(0).getUpQuantity());
                              //  vo.setGzNum(list.get(1).getUpQuantity());
                              //  vo.setSendNum(list.get(1).getDownQuantity());
                              //  vo.setFinalNum(list.get(2).getDownQuantity());

                                vo.setOrderAmount(list.get(0).getDownAmount());
                                vo.setDnAmount(list.get(0).getUpAmount());
                             //   vo.setGzAmount(list.get(1).getUpAmount());
                             //   vo.setSendAmount(list.get(1).getDownAmount());
                              //  vo.setFinalOrderAmount(list.get(2).getDownAmount());

                                vo.setOrderProductNum(list.get(0).getDownProductNum());
                                vo.setDnProductNum(list.get(0).getUpProductNum());
                               // vo.setGzProductNum(list.get(1).getUpProductNum());
                              //  vo.setSendProductNum(list.get(1).getDownProductNum());
                               // vo.setFinalProductNum(list.get(2).getDownProductNum());
                               // vo.setIndustryAverage(list.get(2).getIndustryAverage());
                                vo.setClient(list.get(0).getClient());
                                vo.setReplenishmentStoreNum(num);
                                tempMap.put(key, vo);

                            }

                            if (list.size() == 2&&((list.get(1).getTjType()==3))) {
                                // ??????????????????????????? ???????????????
                                vo.setOrderNum(list.get(0).getDownQuantity());
                                vo.setDnNum(list.get(0).getUpQuantity());
                                //  vo.setGzNum(list.get(1).getUpQuantity());
                                //  vo.setSendNum(list.get(1).getDownQuantity());
                                  vo.setFinalNum(list.get(1).getDownQuantity());

                                vo.setOrderAmount(list.get(0).getDownAmount());
                                vo.setDnAmount(list.get(0).getUpAmount());
                                //   vo.setGzAmount(list.get(1).getUpAmount());
                                //   vo.setSendAmount(list.get(1).getDownAmount());
                                  vo.setFinalOrderAmount(list.get(1).getDownAmount());

                                vo.setOrderProductNum(list.get(0).getDownProductNum());
                                vo.setDnProductNum(list.get(0).getUpProductNum());
                                // vo.setGzProductNum(list.get(1).getUpProductNum());
                                //  vo.setSendProductNum(list.get(1).getDownProductNum());
                                 vo.setFinalProductNum(list.get(1).getDownProductNum());
                                 vo.setIndustryAverage(list.get(1).getIndustryAverage());
                                vo.setClient(list.get(0).getClient());
                                vo.setReplenishmentStoreNum(num);
                                tempMap.put(key, vo);

                            }
                            if (list.size() == 3) {
                                // ??????????????????
                                vo.setOrderNum(list.get(0).getDownQuantity());
                                vo.setDnNum(list.get(0).getUpQuantity());
                                vo.setGzNum(list.get(1).getUpQuantity());
                                vo.setSendNum(list.get(1).getDownQuantity());
                                vo.setFinalNum(list.get(2).getDownQuantity());

                                vo.setOrderAmount(list.get(0).getDownAmount());
                                vo.setDnAmount(list.get(0).getUpAmount());
                                vo.setGzAmount(list.get(1).getUpAmount());
                                vo.setSendAmount(list.get(1).getDownAmount());
                                vo.setFinalOrderAmount(list.get(2).getDownAmount());

                                vo.setOrderProductNum(list.get(0).getDownProductNum());
                                vo.setDnProductNum(list.get(0).getUpProductNum());
                                vo.setGzProductNum(list.get(1).getUpProductNum());
                                vo.setSendProductNum(list.get(1).getDownProductNum());
                                vo.setFinalProductNum(list.get(2).getDownProductNum());
                                vo.setIndustryAverage(list.get(2).getIndustryAverage());
                                vo.setClient(list.get(0).getClient());
                                vo.setReplenishmentStoreNum(num);
                                tempMap.put(key, vo);
                            }
                        } else {
                            if ( list.size() == 1) {
                                // ???????????????????????????
                                vo.setOrderNum(vo.getOrderNum().add(list.get(0).getDownQuantity()));
                                vo.setDnNum(vo.getDnNum().add(list.get(0).getUpQuantity()));
                               // vo.setGzNum(vo.getGzNum().add(list.get(1).getUpQuantity()));
                               // vo.setSendNum(vo.getSendNum().add(list.get(1).getDownQuantity()));
                               // vo.setFinalNum(vo.getFinalNum().add(list.get(2).getDownQuantity()));

                                vo.setOrderProductNum(vo.getOrderProductNum().add(list.get(0).getDownProductNum()));
                                vo.setDnProductNum(vo.getDnProductNum().add(list.get(0).getUpProductNum()));
                               // vo.setGzProductNum(vo.getGzProductNum().add(list.get(1).getUpProductNum()));
                               // vo.setSendProductNum(vo.getSendProductNum().add(list.get(1).getDownProductNum()));
                               // vo.setFinalProductNum(vo.getFinalProductNum().add(list.get(2).getDownProductNum()));

                                vo.setOrderAmount(vo.getOrderAmount().add(list.get(0).getDownAmount()));
                                vo.setDnAmount(vo.getDnAmount().add(list.get(0).getUpAmount()));
                               // vo.setGzAmount(vo.getGzAmount().add(list.get(1).getUpAmount()));
                               // vo.setSendAmount(vo.getSendAmount().add(list.get(1).getDownAmount()));
                               // vo.setFinalOrderAmount(vo.getFinalOrderAmount().add(list.get(2).getDownAmount()));
                                vo.setIndustryAverage(list.get(0).getIndustryAverage());
                                tempMap.put(key, vo);
                            }
                            if (list.size() == 2&&((list.get(1).getTjType()==3))) {
                                // ??????????????????????????? ???????????????
                                vo.setOrderNum(vo.getOrderNum().add(list.get(0).getDownQuantity()));
                                vo.setDnNum(vo.getDnNum().add(list.get(0).getUpQuantity()));
                                //  vo.setGzNum(list.get(1).getUpQuantity());
                                //  vo.setSendNum(list.get(1).getDownQuantity());
                                vo.setFinalNum(vo.getFinalNum().add(list.get(1).getDownQuantity()));

                                vo.setOrderAmount(vo.getOrderAmount().add(list.get(0).getDownAmount()));
                                vo.setDnAmount(vo.getDnAmount().add(list.get(0).getUpAmount()));
                                //   vo.setGzAmount(list.get(1).getUpAmount());
                                //   vo.setSendAmount(list.get(1).getDownAmount());
                                vo.setFinalOrderAmount(vo.getFinalOrderAmount().add(list.get(1).getDownAmount()));

                                vo.setOrderProductNum(vo.getOrderProductNum().add(list.get(0).getDownProductNum()));
                                vo.setDnProductNum(vo.getDnProductNum().add(list.get(0).getUpProductNum()));
                                // vo.setGzProductNum(list.get(1).getUpProductNum());
                                //  vo.setSendProductNum(list.get(1).getDownProductNum());
                                vo.setFinalProductNum(vo.getFinalProductNum().add(list.get(1).getDownProductNum()));
                                vo.setIndustryAverage(list.get(1).getIndustryAverage());
                                vo.setClient(list.get(0).getClient());
                                vo.setReplenishmentStoreNum(num);
                                tempMap.put(key, vo);

                            }
                            if (list.size() == 3) {
                                vo.setOrderNum(vo.getOrderNum().add(list.get(0).getDownQuantity()));
                                vo.setDnNum(vo.getDnNum().add(list.get(0).getUpQuantity()));
                                vo.setGzNum(vo.getGzNum().add(list.get(1).getUpQuantity()));
                                vo.setSendNum(vo.getSendNum().add(list.get(1).getDownQuantity()));
                                vo.setFinalNum(vo.getFinalNum().add(list.get(2).getDownQuantity()));

                                vo.setOrderProductNum(vo.getOrderProductNum().add(list.get(0).getDownProductNum()));
                                vo.setDnProductNum(vo.getDnProductNum().add(list.get(0).getUpProductNum()));
                                vo.setGzProductNum(vo.getGzProductNum().add(list.get(1).getUpProductNum()));
                                vo.setSendProductNum(vo.getSendProductNum().add(list.get(1).getDownProductNum()));
                                vo.setFinalProductNum(vo.getFinalProductNum().add(list.get(2).getDownProductNum()));

                                vo.setOrderAmount(vo.getOrderAmount().add(list.get(0).getDownAmount()));
                                vo.setDnAmount(vo.getDnAmount().add(list.get(0).getUpAmount()));
                                vo.setGzAmount(vo.getGzAmount().add(list.get(1).getUpAmount()));
                                vo.setSendAmount(vo.getSendAmount().add(list.get(1).getDownAmount()));
                                vo.setFinalOrderAmount(vo.getFinalOrderAmount().add(list.get(2).getDownAmount()));
                                vo.setIndustryAverage(list.get(2).getIndustryAverage());
                                tempMap.put(key, vo);
                            }

                        }

                    }
                    if (CollectionUtil.isNotEmpty(tempMap)) {
                        for (String key1 : tempMap.keySet()) {
                            ReportInfoSummaryVo data = tempMap.get(key1);
                            //??????????????????
                            // data.setQuantityRate(new BigDecimal("0.00").compareTo(data.getDownQuantity())  == 0?BigDecimal.ZERO:data.getUpQuantity().divide(data.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                            // data.setAmountRate(new BigDecimal("0.00").compareTo(data.getDownAmount())  == 0?BigDecimal.ZERO:data.getUpAmount().divide(data.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                            // data.setProductRate(new BigDecimal("0.00").compareTo(data.getDownProductNum())  == 0?BigDecimal.ZERO:data.getUpProductNum().divide(data.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                            // data.setIndustryAverage(BigDecimal.ZERO.compareTo(data.getCount()) == 0? BigDecimal.ZERO: data.getIndustryAverage().divide(data.getCount(),4,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP));

                            data.setDistributionProductRate(data.getDnProductNum().divide(data.getOrderProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            data.setDistributionProductRateStr(data.getDnProductNum().divide(data.getOrderProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            if(data.getSendProductNum().compareTo(BigDecimal.ZERO)>0){
                                data.setSendProductRate(data.getGzProductNum().divide(data.getSendProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setSendProductRateStr(data.getGzProductNum().divide(data.getSendProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setSendProductRate(BigDecimal.ZERO);
                                data.setSendProductRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }

                            if(data.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalProductRate(data.getGzProductNum().divide(data.getFinalProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setFinalProductRateStr(data.getGzProductNum().divide(data.getFinalProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setFinalProductRate(BigDecimal.ZERO);
                                data.setFinalProductRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }

                            data.setDistributionAmountRate(data.getDnAmount().divide(data.getOrderAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            data.setDistributionAmountRateStr(data.getDnAmount().divide(data.getOrderAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            if(data.getSendAmount().compareTo(BigDecimal.ZERO)>0){
                                data.setSendAmountRate(data.getGzAmount().divide(data.getSendAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setSendAmountRateStr(data.getGzAmount().divide(data.getSendAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setSendAmountRate(BigDecimal.ZERO);
                                data.setSendAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }
                            if(data.getFinalOrderAmount().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalAmountRate(data.getGzAmount().divide(data.getFinalOrderAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setFinalAmountRateStr(data.getGzAmount().divide(data.getFinalOrderAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setFinalAmountRate(BigDecimal.ZERO);
                                data.setFinalAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }


                            data.setDistributionNumRate(data.getDnNum().divide(data.getOrderNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            if(data.getSendNum().compareTo(BigDecimal.ZERO)>0){
                                data.setSendNumRate(data.getGzNum().divide(data.getSendNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setSendNumRateStr(data.getGzNum().divide(data.getSendNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setSendNumRate(BigDecimal.ZERO);
                                data.setSendNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }

                            if(data.getFinalNum().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalNumRate(data.getGzNum().divide(data.getFinalNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setFinalNumRateStr(data.getGzNum().divide(data.getFinalNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setFinalNumRate(BigDecimal.ZERO);
                                data.setFinalNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }
                            data.setDistributionNumRateStr(data.getDnNum().divide(data.getOrderNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            resList.add(data);
                        }
                    }
                }
    }}
 return resList;
}

    private Integer getMonthReplenishNum(List<ReplenishVo> replenishDto, String year, String month, String client) {
        List<ReplenishVo> list = replenishDto.stream().filter(replenishVo -> replenishVo.getYearNum().equals(Integer.valueOf(year)) && replenishVo.getMonthNum().equals(Integer.valueOf(month)) && replenishVo.getClient().equals(client)).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(list)){
            // ???????????????
            return list.get(0).getNum();
        }
        return 0;
    }

    /**
     * ???????????????
     *
     * @param
     * @param replenishDto
     * @return
     */
    private List<ReportInfoSummaryVo> handleWeekData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto, ReplenishDto replenishDto) {
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        List<ReplenishVo> allReplenishes = gaiaSdReplenishHMapper.getWeekReplenishes(replenishDto);
        Map<String, Map<Integer, Map<String, Map<Date, List<GaiaXhlH>>>>> stringMapMap = dbDataList.stream().collect(Collectors.groupingBy(GaiaXhlH::getYearName, Collectors.groupingBy(GaiaXhlH::getWeekNum, Collectors.groupingBy(GaiaXhlH::getClient, Collectors.groupingBy(GaiaXhlH::getTjDate)))));
        for (String yearName  :stringMapMap.keySet()){
            Map<Integer, Map<String, Map<Date, List<GaiaXhlH>>>> integerMapMap = stringMapMap.get(yearName);
            for (Integer weekNum:integerMapMap.keySet()){

                Map<String, Map<Date, List<GaiaXhlH>>> mapMap = integerMapMap.get(weekNum);

                for (String client :mapMap.keySet()){

                    Map<Date, List<GaiaXhlH>> listMap = mapMap.get(client);
                      // ???????????????????????????
                    Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
                    ReportInfoSummaryVo vo =new ReportInfoSummaryVo();
                    StringBuilder stringBuilder=new StringBuilder();
                    String dateSrt= stringBuilder.append(yearName).append("???").append(weekNum).append("???").toString();
                    String key= stringBuilder.append(yearName).append("???").append(weekNum).append("???").toString();
                    vo.setDate(dateSrt);
                    vo.setClient(client);

                     for(Date date :listMap.keySet()){
                         List<GaiaXhlH> list = listMap.get(date);
                         if(tempMap.get(key)==null){
                             if(list.size()==1){
                                 //????????????????????????
                                 Integer num  = getWeekReplenishNum(allReplenishes,client,weekNum,yearName);
                                 // ??????????????????
                                 vo.setOrderNum(list.get(0).getDownQuantity());
                                 vo.setDnNum(list.get(0).getUpQuantity());
                                // vo.setGzNum(list.get(1).getUpQuantity());
                               //  vo.setSendNum(list.get(1).getDownQuantity());
                                // vo.setFinalNum(list.get(2).getDownQuantity());

                                 vo.setOrderAmount(list.get(0).getDownAmount());
                                 vo.setDnAmount(list.get(0).getUpAmount());
                               //  vo.setGzAmount(list.get(1).getUpAmount());
                               //  vo.setSendAmount(list.get(1).getDownAmount());
                               //  vo.setFinalOrderAmount(list.get(2).getDownAmount());

                                 vo.setOrderProductNum(list.get(0).getDownProductNum());
                                 vo.setDnProductNum(list.get(0).getUpProductNum());
                                // vo.setGzProductNum(list.get(1).getUpProductNum());
                               //  vo.setSendProductNum(list.get(1).getDownProductNum());
                               //  vo.setFinalProductNum(list.get(2).getDownProductNum());
                                 vo.setIndustryAverage(list.get(0).getIndustryAverage());
                                 vo.setClient(list.get(0).getClient());
                                 vo.setReplenishmentStoreNum(num);
                                 tempMap.put(key,vo);
                             }
                             if(list.size() == 2&&((list.get(1).getTjType()==3))){
                                 //????????????????????????
                                 Integer num  = getWeekReplenishNum(allReplenishes,client,weekNum,yearName);
                                 // ??????????????????
                                 vo.setOrderNum(list.get(0).getDownQuantity());
                                 vo.setDnNum(list.get(0).getUpQuantity());
                                 // vo.setGzNum(list.get(1).getUpQuantity());
                                 //  vo.setSendNum(list.get(1).getDownQuantity());
                                  vo.setFinalNum(list.get(1).getDownQuantity());

                                 vo.setOrderAmount(list.get(0).getDownAmount());
                                 vo.setDnAmount(list.get(0).getUpAmount());
                                 //  vo.setGzAmount(list.get(1).getUpAmount());
                                 //  vo.setSendAmount(list.get(1).getDownAmount());
                                   vo.setFinalOrderAmount(list.get(1).getDownAmount());

                                 vo.setOrderProductNum(list.get(0).getDownProductNum());
                                 vo.setDnProductNum(list.get(0).getUpProductNum());
                                 // vo.setGzProductNum(list.get(1).getUpProductNum());
                                 //  vo.setSendProductNum(list.get(1).getDownProductNum());
                                   vo.setFinalProductNum(list.get(1).getDownProductNum());
                                 vo.setIndustryAverage(list.get(0).getIndustryAverage());
                                 vo.setClient(list.get(0).getClient());
                                 vo.setReplenishmentStoreNum(num);
                                 tempMap.put(key,vo);
                             }
                             if(list.size()==3){
                      Integer num  = getWeekReplenishNum(allReplenishes,client,weekNum,yearName);
                                 // ??????????????????
                                 vo.setOrderNum(list.get(0).getDownQuantity());
                                 vo.setDnNum(list.get(0).getUpQuantity());
                                 vo.setGzNum(list.get(1).getUpQuantity());
                                 vo.setSendNum(list.get(1).getDownQuantity());
                                 vo.setFinalNum(list.get(2).getDownQuantity());

                                 vo.setOrderAmount(list.get(0).getDownAmount());
                                 vo.setDnAmount(list.get(0).getUpAmount());
                                 vo.setGzAmount(list.get(1).getUpAmount());
                                 vo.setSendAmount(list.get(1).getDownAmount());
                                 vo.setFinalOrderAmount(list.get(2).getDownAmount());

                                 vo.setOrderProductNum(list.get(0).getDownProductNum());
                                 vo.setDnProductNum(list.get(0).getUpProductNum());
                                 vo.setGzProductNum(list.get(1).getUpProductNum());
                                 vo.setSendProductNum(list.get(1).getDownProductNum());
                                 vo.setFinalProductNum(list.get(2).getDownProductNum());
                                 vo.setIndustryAverage(list.get(2).getIndustryAverage());
                                 vo.setClient(list.get(0).getClient());
                                 vo.setReplenishmentStoreNum(num);
                                 tempMap.put(key,vo);
                             }
                         }else {
                             if(list.size()==1){
                                 // ????????????????????????
                                 vo.setOrderNum(vo.getOrderNum().add(list.get(0).getDownQuantity()));
                                 vo.setDnNum(vo.getDnNum().add(list.get(0).getUpQuantity()));
                                // vo.setGzNum(vo.getGzNum().add(list.get(1).getUpQuantity()));
                                // vo.setSendNum(vo.getSendNum().add(list.get(1).getDownQuantity()));
                                // vo.setFinalNum(vo.getFinalNum().add(list.get(2).getDownQuantity()));

                                 vo.setOrderProductNum(vo.getOrderProductNum().add(list.get(0).getDownProductNum()));
                                 vo.setDnProductNum(vo.getDnProductNum().add(list.get(0).getUpProductNum()));
                                // vo.setGzProductNum(vo.getGzProductNum().add(list.get(1).getUpProductNum()));
                               //  vo.setSendProductNum(vo.getSendProductNum().add(list.get(1).getDownProductNum()));
                               //  vo.setFinalProductNum(vo.getFinalProductNum().add(list.get(2).getDownProductNum()));

                                 vo.setOrderAmount(vo.getOrderAmount().add(list.get(0).getDownAmount()));
                                 vo.setDnAmount(vo.getDnAmount().add(list.get(0).getUpAmount()));
                                // vo.setGzAmount(vo.getGzAmount().add(list.get(1).getUpAmount()));
                                // vo.setSendAmount(vo.getSendAmount().add(list.get(1).getDownAmount()));
                                // vo.setFinalOrderAmount(vo.getFinalOrderAmount().add(list.get(2).getDownAmount()));
                                 vo.setIndustryAverage(list.get(0).getIndustryAverage());
                                 tempMap.put(key,vo);
                             }

                             if(list.size() == 2&&((list.get(1).getTjType()==3))){
                                 // ????????????????????????
                                 vo.setOrderNum(vo.getOrderNum().add(list.get(0).getDownQuantity()));
                                 vo.setDnNum(vo.getDnNum().add(list.get(0).getUpQuantity()));
                                 // vo.setGzNum(vo.getGzNum().add(list.get(1).getUpQuantity()));
                                 // vo.setSendNum(vo.getSendNum().add(list.get(1).getDownQuantity()));
                                  vo.setFinalNum(vo.getFinalNum().add(list.get(1).getDownQuantity()));

                                 vo.setOrderProductNum(vo.getOrderProductNum().add(list.get(0).getDownProductNum()));
                                 vo.setDnProductNum(vo.getDnProductNum().add(list.get(0).getUpProductNum()));
                                 // vo.setGzProductNum(vo.getGzProductNum().add(list.get(1).getUpProductNum()));
                                 //  vo.setSendProductNum(vo.getSendProductNum().add(list.get(1).getDownProductNum()));
                                   vo.setFinalProductNum(vo.getFinalProductNum().add(list.get(1).getDownProductNum()));

                                 vo.setOrderAmount(vo.getOrderAmount().add(list.get(0).getDownAmount()));
                                 vo.setDnAmount(vo.getDnAmount().add(list.get(0).getUpAmount()));
                                 // vo.setGzAmount(vo.getGzAmount().add(list.get(1).getUpAmount()));
                                 // vo.setSendAmount(vo.getSendAmount().add(list.get(1).getDownAmount()));
                                  vo.setFinalOrderAmount(vo.getFinalOrderAmount().add(list.get(1).getDownAmount()));
                                 vo.setIndustryAverage(list.get(0).getIndustryAverage());
                                 tempMap.put(key,vo);
                             }
                             if(list.size()==3){
                                 vo.setOrderNum(vo.getOrderNum().add(list.get(0).getDownQuantity()));
                                 vo.setDnNum(vo.getDnNum().add(list.get(0).getUpQuantity()));
                                 vo.setGzNum(vo.getGzNum().add(list.get(1).getUpQuantity()));
                                 vo.setSendNum(vo.getSendNum().add(list.get(1).getDownQuantity()));
                                 vo.setFinalNum(vo.getFinalNum().add(list.get(2).getDownQuantity()));

                                 vo.setOrderProductNum(vo.getOrderProductNum().add(list.get(0).getDownProductNum()));
                                 vo.setDnProductNum(vo.getDnProductNum().add(list.get(0).getUpProductNum()));
                                 vo.setGzProductNum(vo.getGzProductNum().add(list.get(1).getUpProductNum()));
                                 vo.setSendProductNum(vo.getSendProductNum().add(list.get(1).getDownProductNum()));
                                 vo.setFinalProductNum(vo.getFinalProductNum().add(list.get(2).getDownProductNum()));

                                 vo.setOrderAmount(vo.getOrderAmount().add(list.get(0).getDownAmount()));
                                 vo.setDnAmount(vo.getDnAmount().add(list.get(0).getUpAmount()));
                                 vo.setGzAmount(vo.getGzAmount().add(list.get(1).getUpAmount()));
                                 vo.setSendAmount(vo.getSendAmount().add(list.get(1).getDownAmount()));
                                 vo.setFinalOrderAmount(vo.getFinalOrderAmount().add(list.get(2).getDownAmount()));
                                 vo.setIndustryAverage(list.get(2).getIndustryAverage());
                                 tempMap.put(key,vo);
                             }

                         }

                     }
                    if (CollectionUtil.isNotEmpty(tempMap)) {
                        for (String key1 : tempMap.keySet()) {
                            ReportInfoSummaryVo data = tempMap.get(key1);
                            //??????????????????
                           // data.setQuantityRate(new BigDecimal("0.00").compareTo(data.getDownQuantity())  == 0?BigDecimal.ZERO:data.getUpQuantity().divide(data.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                           // data.setAmountRate(new BigDecimal("0.00").compareTo(data.getDownAmount())  == 0?BigDecimal.ZERO:data.getUpAmount().divide(data.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                           // data.setProductRate(new BigDecimal("0.00").compareTo(data.getDownProductNum())  == 0?BigDecimal.ZERO:data.getUpProductNum().divide(data.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                           // data.setIndustryAverage(BigDecimal.ZERO.compareTo(data.getCount()) == 0? BigDecimal.ZERO: data.getIndustryAverage().divide(data.getCount(),4,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP));

                            data.setDistributionProductRate(data.getDnProductNum().divide(data.getOrderProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            data.setDistributionProductRateStr(data.getDnProductNum().divide(data.getOrderProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            if(data.getSendProductNum().compareTo(BigDecimal.ZERO)>0){
                                data.setSendProductRate(data.getGzProductNum().divide(data.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            }else {
                                data.setSendProductRate(BigDecimal.ZERO);
                            }
                            if(data.getSendProductNum().compareTo(BigDecimal.ZERO)>0){
                                data.setSendProductRateStr(data.getGzProductNum().divide(data.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setSendProductRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }

                            if(data.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalProductRate(data.getGzProductNum().divide(data.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            }else {
                                data.setFinalProductRate(BigDecimal.ZERO);
                            }
                            if(data.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalProductRateStr(data.getGzProductNum().divide(data.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setFinalProductRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }

                            if(data.getOrderAmount().compareTo(BigDecimal.ZERO)>0){
                                data.setDistributionAmountRate(data.getDnAmount().divide(data.getOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                                data.setDistributionAmountRateStr(data.getDnAmount().divide(data.getOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setDistributionAmountRate(BigDecimal.ZERO);
                                data.setDistributionAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }

                            if(data.getSendAmount().compareTo(BigDecimal.ZERO)>0){
                                data.setSendAmountRate(data.getGzAmount().divide(data.getSendAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            }else {
                                data.setSendAmountRate(BigDecimal.ZERO);
                            }
                             if(data.getSendAmount().compareTo(BigDecimal.ZERO)>0){
                                 data.setSendAmountRateStr(data.getGzAmount().divide(data.getSendAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                             }else {
                                 data.setSendAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                             }

                            if(data.getFinalOrderAmount().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalAmountRate(data.getGzAmount().divide(data.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            }else {
                                data.setFinalAmountRate(BigDecimal.ZERO);
                            }
                            if(data.getFinalOrderAmount().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalAmountRateStr(data.getGzAmount().divide(data.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                            }else {
                                data.setFinalAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                            }


                            data.setDistributionNumRate(data.getDnNum().divide(data.getOrderNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            if(data.getSendNum().compareTo(BigDecimal.ZERO)>0){
                                data.setSendNumRate(data.getGzNum().divide(data.getSendNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            }else {
                                data.setSendNumRate(BigDecimal.ZERO);
                            }

                            if(data.getFinalNum().compareTo(BigDecimal.ZERO)>0){
                                data.setFinalNumRate(data.getGzNum().divide(data.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                            }else{
                                data.setFinalNumRate(BigDecimal.ZERO);
                            }

                            data.setDistributionNumRateStr(data.getDnNum().divide(data.getOrderNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                             if(data.getSendNum().compareTo(BigDecimal.ZERO)>0){
                                 data.setSendNumRateStr(data.getGzNum().divide(data.getSendNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                             }else {
                                 data.setSendNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                             }
                              if(data.getFinalNum().compareTo(BigDecimal.ZERO)>0){
                                  data.setFinalNumRateStr(data.getGzNum().divide(data.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                              }else {
                                  data.setFinalNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                              }
                            resList.add(data);
                        }
                    }

                    //                    vo.setDistributionNumRate(sortList.get(0).getQuantityRate());
                    //                    vo.setSendNumRate(sortList.get(1).getQuantityRate());
                    //                    vo.setFinalNumRate(sortList.get(2).getQuantityRate());
                    //                    // ??????
                    //                    vo.setOrderAmount(sortList.get(0).getDownAmount());
                    //                    vo.setGzAmount(sortList.get(1).getUpAmount());
                    //                    vo.setFinalAmountRate(sortList.get(2).getDownAmount());
                    //                    vo.setDistributionAmountRate(sortList.get(0).getAmountRate());
                    //                    vo.setSendAmountRate(sortList.get(1).getAmountRate());
                    //                    vo.setFinalAmountRate(sortList.get(2).getAmountRate());
                    //                    // ??????
                    //                    vo.setOrderProductNum(sortList.get(0).getDownProductNum());
                    //                    vo.setGzProductNum(sortList.get(1).getUpProductNum());
                    //                    vo.setFinalProductNum(sortList.get(2).getDownProductNum());

                }
              }
        }
        return resList;
    }

    private Integer getWeekReplenishNum(List<ReplenishVo> replenishDto, String client, Integer weekNum, String yearName) {
        List<ReplenishVo> list = replenishDto.stream().filter(replenishVo -> replenishVo.getYearNum().equals(Integer.valueOf(yearName)) && replenishVo.getWeekNum().equals(weekNum-1) && replenishVo.getClient().equals(client)).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(list)){
           // ???????????????
           return list.get(0).getNum();
        }
        return 0;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @param replenishDto
     * @return
     */
    private List<ReportInfoSummaryVo> handleDayData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto, ReplenishDto replenishDto) {
        //db???????????????????????????????????????????????????????????????????????????????????????
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        List<ReplenishVo> allReplenishes = gaiaSdReplenishHMapper.getAllReplenishes(replenishDto);
        // ????????????
        //??????
        Map<Date, Map<String, List<GaiaXhlH>>> dateMapMap = dbDataList.stream().collect(Collectors.groupingBy(GaiaXhlH::getTjDate, Collectors.groupingBy(GaiaXhlH::getClient)));

        for (Date date : dateMapMap.keySet()) {
            Map<String, List<GaiaXhlH>> stringListMap = dateMapMap.get(date);

            for (String client : stringListMap.keySet()) {
                //?????????????????????list
                List<GaiaXhlH> list = stringListMap.get(client);
                list.sort(Comparator.comparing(GaiaXhlH::getTjType));
                if (list.size() == 1) {
                    //?????????????????? ????????????
                    Integer num= getDayReplenishNum(allReplenishes, date, client);
                    //?????? type 1 2 3
                    List<GaiaXhlH> sortList = list.stream().sorted(Comparator.comparing(GaiaXhlH::getTjType)).collect(Collectors.toList());
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();
                    String tjDateTranceStr = DateUtil.format(sortList.get(0).getTjDate(), "yyyy???MM???dd???");
                    vo.setDate(tjDateTranceStr);
                    //??????
                    vo.setOrderNum(sortList.get(0).getDownQuantity());
                    vo.setDnNum(sortList.get(0).getUpQuantity());
                   // vo.setGzNum(sortList.get(1).getUpQuantity());
                   // vo.setSendNum(sortList.get(1).getDownQuantity());
                   // vo.setFinalNum(sortList.get(2).getDownQuantity());

                    vo.setDistributionNumRate(sortList.get(0).getQuantityRate());
                    vo.setDistributionNumRateStr(sortList.get(0).getQuantityRate().toPlainString()+"%");
                    //vo.setSendNumRate(sortList.get(1).getQuantityRate());
                    vo.setSendNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                   // vo.setFinalNumRate(sortList.get(2).getQuantityRate());
                    vo.setFinalNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    // ??????
                    vo.setOrderAmount(sortList.get(0).getDownAmount());
                    vo.setDnAmount(sortList.get(0).getUpAmount());
                   // vo.setGzAmount(sortList.get(1).getUpAmount());
                   // vo.setSendAmount(sortList.get(1).getDownAmount());
                   // vo.setFinalOrderAmount(sortList.get(2).getDownAmount());

                    vo.setDistributionAmountRate(sortList.get(0).getAmountRate());
                    vo.setDistributionAmountRateStr(sortList.get(0).getAmountRate().toPlainString()+"%");
                   // vo.setSendAmountRate(sortList.get(1).getAmountRate());
                   vo.setSendAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                  //  vo.setFinalAmountRate(sortList.get(2).getAmountRate());
                    vo.setFinalAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    // ??????
                    vo.setOrderProductNum(sortList.get(0).getDownProductNum());
                    vo.setDnProductNum(sortList.get(0).getUpProductNum());
                    //vo.setGzProductNum(sortList.get(1).getUpProductNum());
                   // vo.setSendProductNum(sortList.get(1).getDownProductNum());
                  //  vo.setFinalProductNum(sortList.get(2).getDownProductNum());

                    vo.setDistributionProductRate(sortList.get(0).getProductRate());
                    vo.setDistributionProductRateStr(sortList.get(0).getProductRate().toPlainString()+"%");
                   // vo.setSendProductRate(sortList.get(1).getProductRate());
                    vo.setSendProductRateStr(BigDecimal.ZERO.toPlainString()+"%");
                 //   vo.setFinalProductRate(sortList.get(2).getProductRate());
                     vo.setFinalProductRateStr(BigDecimal.ZERO.toPlainString()+"%");

                    vo.setIndustryAverage(sortList.get(0).getIndustryAverage());
                    vo.setClient(client);
                    vo.setReplenishmentStoreNum(num);
                    // vo.setReplenishmentStoreNum(stringListMap.size());
                    // 1???app 2????????????
                    if(sortList.get(0).getViewSource()!=null){
                        if(sortList.get(0).getViewSource()==1){
                            vo.setMobileViewNum(1);
                        }
                        if(sortList.get(0).getViewSource()==2){
                            vo.setWebViewNum(1);
                        }
                    }
                    resList.add(vo);
                }
                //??????????????? ???????????????????????????
                if (list.size() == 2&&((list.get(1).getTjType()==3))) {
                    //?????????????????? ????????????
                    Integer num= getDayReplenishNum(allReplenishes, date, client);
                    //?????? type 1 2 3
                    List<GaiaXhlH> sortList = list.stream().sorted(Comparator.comparing(GaiaXhlH::getTjType)).collect(Collectors.toList());
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();
                    String tjDateTranceStr = DateUtil.format(sortList.get(0).getTjDate(), "yyyy???MM???dd???");
                    vo.setDate(tjDateTranceStr);
                    //??????
                    vo.setOrderNum(sortList.get(0).getDownQuantity());
                    vo.setDnNum(sortList.get(0).getUpQuantity());
                    // vo.setGzNum(sortList.get(1).getUpQuantity());
                    // vo.setSendNum(sortList.get(1).getDownQuantity());
                     vo.setFinalNum(sortList.get(1).getDownQuantity());

                    vo.setDistributionNumRate(sortList.get(0).getQuantityRate());
                    vo.setDistributionNumRateStr(sortList.get(0).getQuantityRate().toPlainString()+"%");
                    //vo.setSendNumRate(sortList.get(1).getQuantityRate());
                    vo.setSendNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    // vo.setFinalNumRate(sortList.get(2).getQuantityRate());
                    vo.setFinalNumRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    // ??????
                    vo.setOrderAmount(sortList.get(0).getDownAmount());
                    vo.setDnAmount(sortList.get(0).getUpAmount());
                    // vo.setGzAmount(sortList.get(1).getUpAmount());
                    // vo.setSendAmount(sortList.get(1).getDownAmount());
                     vo.setFinalOrderAmount(sortList.get(1).getDownAmount());

                    vo.setDistributionAmountRate(sortList.get(0).getAmountRate());
                    vo.setDistributionAmountRateStr(sortList.get(0).getAmountRate().toPlainString()+"%");
                    // vo.setSendAmountRate(sortList.get(1).getAmountRate());
                    vo.setSendAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    //  vo.setFinalAmountRate(sortList.get(2).getAmountRate());
                    vo.setFinalAmountRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    // ??????
                    vo.setOrderProductNum(sortList.get(0).getDownProductNum());
                    vo.setDnProductNum(sortList.get(0).getUpProductNum());
                    //vo.setGzProductNum(sortList.get(1).getUpProductNum());
                    // vo.setSendProductNum(sortList.get(1).getDownProductNum());
                      vo.setFinalProductNum(sortList.get(1).getDownProductNum());

                    vo.setDistributionProductRate(sortList.get(0).getProductRate());
                    vo.setDistributionProductRateStr(sortList.get(0).getProductRate().toPlainString()+"%");
                    // vo.setSendProductRate(sortList.get(1).getProductRate());
                    vo.setSendProductRateStr(BigDecimal.ZERO.toPlainString()+"%");
                    //   vo.setFinalProductRate(sortList.get(2).getProductRate());
                    vo.setFinalProductRateStr(BigDecimal.ZERO.toPlainString()+"%");

                    vo.setIndustryAverage(sortList.get(0).getIndustryAverage());
                    vo.setClient(client);
                    vo.setReplenishmentStoreNum(num);
                    // vo.setReplenishmentStoreNum(stringListMap.size());
                    // 1???app 2????????????
                    if(sortList.get(0).getViewSource()!=null){
                        if(sortList.get(0).getViewSource()==1){
                            vo.setMobileViewNum(1);
                        }
                        if(sortList.get(0).getViewSource()==2){
                            vo.setWebViewNum(1);
                        }
                    }
                    resList.add(vo);
                }
                if (list.size() == 3) {
                    Integer num= getDayReplenishNum(allReplenishes, date, client);
                    //?????? type 1 2 3
                    List<GaiaXhlH> sortList = list.stream().sorted(Comparator.comparing(GaiaXhlH::getTjType)).collect(Collectors.toList());
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();
                    String tjDateTranceStr = DateUtil.format(sortList.get(0).getTjDate(), "yyyy???MM???dd???");
                    vo.setDate(tjDateTranceStr);
                    //??????
                    vo.setOrderNum(sortList.get(0).getDownQuantity());
                    vo.setDnNum(sortList.get(0).getUpQuantity());
                    vo.setGzNum(sortList.get(1).getUpQuantity());
                    vo.setSendNum(sortList.get(1).getDownQuantity());
                    vo.setFinalNum(sortList.get(2).getDownQuantity());

                    vo.setDistributionNumRate(sortList.get(0).getQuantityRate());
                    vo.setDistributionNumRateStr(sortList.get(0).getQuantityRate().toPlainString()+"%");
                    vo.setSendNumRate(sortList.get(1).getQuantityRate());
                    vo.setSendNumRateStr(sortList.get(1).getQuantityRate().toPlainString()+"%");
                    vo.setFinalNumRate(sortList.get(2).getQuantityRate());
                    vo.setFinalNumRateStr(sortList.get(2).getQuantityRate().toPlainString()+"%");
                    // ??????
                    vo.setOrderAmount(sortList.get(0).getDownAmount());
                    vo.setDnAmount(sortList.get(0).getUpAmount());
                    vo.setGzAmount(sortList.get(1).getUpAmount());
                    vo.setSendAmount(sortList.get(1).getDownAmount());
                    vo.setFinalOrderAmount(sortList.get(2).getDownAmount());

                    vo.setDistributionAmountRate(sortList.get(0).getAmountRate());
                    vo.setDistributionAmountRateStr(sortList.get(0).getAmountRate().toPlainString()+"%");
                    vo.setSendAmountRate(sortList.get(1).getAmountRate());
                    vo.setSendAmountRateStr(sortList.get(1).getAmountRate().toPlainString()+"%");
                    vo.setFinalAmountRate(sortList.get(2).getAmountRate());
                    vo.setFinalAmountRateStr(sortList.get(2).getAmountRate().toPlainString()+"%");
                    // ??????
                    vo.setOrderProductNum(sortList.get(0).getDownProductNum());
                    vo.setDnProductNum(sortList.get(0).getUpProductNum());
                    vo.setGzProductNum(sortList.get(1).getUpProductNum());
                    vo.setSendProductNum(sortList.get(1).getDownProductNum());
                    vo.setFinalProductNum(sortList.get(2).getDownProductNum());

                    vo.setDistributionProductRate(sortList.get(0).getProductRate());
                    vo.setDistributionProductRateStr(sortList.get(0).getProductRate().toPlainString()+"%");
                    vo.setSendProductRate(sortList.get(1).getProductRate());
                    vo.setSendProductRateStr(sortList.get(1).getProductRate().toPlainString()+"%");
                    vo.setFinalProductRate(sortList.get(2).getProductRate());
                    vo.setFinalProductRateStr(sortList.get(2).getProductRate().toPlainString()+"%");

                    vo.setIndustryAverage(sortList.get(2).getIndustryAverage());
                    vo.setClient(client);
                    vo.setReplenishmentStoreNum(num);
                   // vo.setReplenishmentStoreNum(stringListMap.size());
                    // 1???app 2????????????
                    if(sortList.get(0).getViewSource()!=null){
                        if(sortList.get(0).getViewSource()==1){
                            vo.setMobileViewNum(1);
                        }
                        if(sortList.get(0).getViewSource()==2){
                            vo.setWebViewNum(1);
                        }
                    }
                    resList.add(vo);
                }
            }
        }
        return resList;
    }

    private Integer getDayReplenishNum(List<ReplenishVo> replenishDto, Date date, String client) {
        String formatDate = com.gys.util.DateUtil.formatDate(date, "yyyy-MM-dd 00:00:00");
        List<ReplenishVo> vos = replenishDto.stream().filter(replenishVo -> replenishVo.getClient().equals(client) &&com.gys.util.DateUtil.formatDate(replenishVo.getTjDate(), "yyyy-MM-dd 00:00:00").equals(formatDate)).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(vos)){
            return vos.get(0).getNum();
        }
        return 0;
    }


    private List<ReportInfoSummaryVo> handleAverageDbDataByReportType(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        String reportType = dto.getReportType();
        switch (reportType) {
            //??????
            case "1":
                List<ReportInfoSummaryVo> dayList = handleAverageDayData(dbDataList, dto);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //??????
            case "2":
                List<ReportInfoSummaryVo> weekList = handleAverageWeekData(dbDataList, dto);
                if (CollectionUtil.isNotEmpty(weekList)) {
                    resList = weekList;
                }
                break;
            //??????
            case "3":
                List<ReportInfoSummaryVo> monthList = handleAverageMonthData(dbDataList, dto);
                if (CollectionUtil.isNotEmpty(monthList)) {
                    resList = monthList;
                }
                break;
        }
        return resList;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @return
     */
    private List<ReportInfoSummaryVo> handleAverageDayData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        //db???????????????????????????????????????????????????????????????????????????????????????
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
        //??????????????? ????????????????????????
        dbDataList= dbDataList.stream().filter(gaiaXhlH->gaiaXhlH.getTjType()==3).collect(Collectors.toList());
        for (GaiaXhlH h : dbDataList) {
            String key = DateUtil.format(h.getTjDate(), "yyyy???MM???dd???");
            ReportInfoSummaryVo res = null;
            if (tempMap.get(key) != null) {
                res = tempMap.get(key);
                res.setAverageUpProductNum(res.getAverageUpProductNum().add(h.getUpProductNum()));
                res.setAverageDownProductNum(res.getAverageDownProductNum().add(h.getDownProductNum()));
            } else {
                res = new ReportInfoSummaryVo();
                res.setAverageUpProductNum(h.getUpProductNum());
                res.setAverageDownProductNum(h.getDownProductNum());
                res.setDate(key);
            }
            tempMap.put(key, res);
        }
        if(CollectionUtil.isNotEmpty(tempMap)){
            for(String key : tempMap.keySet()){
                ReportInfoSummaryVo res = tempMap.get(key);
                res.setAverageRate(new BigDecimal("0.00").compareTo(res.getAverageDownProductNum())  == 0?BigDecimal.ZERO:res.getAverageUpProductNum().divide(res.getAverageDownProductNum(),4,RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2,RoundingMode.HALF_UP));
                resList.add(res);
            }
        }
        return resList;
    }

    private List<ReportInfoSummaryVo> handleAverageWeekData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
        dbDataList= dbDataList.stream().filter(gaiaXhlH->gaiaXhlH.getTjType()==3).collect(Collectors.toList());
        for (GaiaXhlH h : dbDataList) {
            //?????????????????????????????????????????????????????????????????????????????????
            String key = h.getYearName() + "???" + h.getWeekNum() + "???";
            ReportInfoSummaryVo res = null;
            if (tempMap.get(key) == null) {
                res = new ReportInfoSummaryVo();
                res.setDate(key);
                res.setAverageUpProductNum(h.getUpProductNum()==null?BigDecimal.ZERO:h.getUpProductNum());
                res.setAverageDownProductNum(h.getDownProductNum()==null?BigDecimal.ZERO:h.getDownProductNum());
            } else {
                res = tempMap.get(key);
                res.setAverageUpProductNum(res.getAverageUpProductNum().add(h.getUpProductNum()));
                res.setAverageDownProductNum(res.getAverageDownProductNum().add(h.getDownProductNum()));
            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                ReportInfoSummaryVo data = tempMap.get(key);
                //??????????????????
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * ??????????????????
     *
     * @param inData
     * @return
     */
    private List<ReportInfoSummaryVo> handleAverageMonthData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
        dbDataList= dbDataList.stream().filter(gaiaXhlH->gaiaXhlH.getTjType()==3).collect(Collectors.toList());
        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yy-MM");
            //?????????????????????????????????????????????????????????????????????????????????
            String key = h.getYearName() + "???" + tjDateTranceStr.split("-")[1] + "???";
            ReportInfoSummaryVo res = null;
            if (tempMap.get(key) == null) {
                res = new ReportInfoSummaryVo();
                res.setDate(key);
                res.setAverageUpProductNum(h.getUpProductNum()==null?BigDecimal.ZERO:h.getUpProductNum());
                res.setAverageDownProductNum(h.getDownProductNum()==null?BigDecimal.ZERO:h.getDownProductNum());
            } else {
                res = tempMap.get(key);
                res.setAverageUpProductNum(res.getAverageUpProductNum().add(h.getUpProductNum()));
                res.setAverageDownProductNum(res.getAverageDownProductNum().add(h.getDownProductNum()));
            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                ReportInfoSummaryVo data = tempMap.get(key);
                //??????????????????
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    @Override
    public ClientStoreVo getClientsOrStores(ClientStoreDto dto) {
         ClientStoreVo clientStoreVo =new ClientStoreVo();
         if(StringUtils.isNotBlank(dto.getClient())) {
         List<ClientVo> franchiseeList = gaiaFranchiseeMapper.getClients(dto.getClient());
         clientStoreVo.setClientVoList(franchiseeList);
         return clientStoreVo;
        }
        if(StringUtils.isNotBlank(dto.getStore())) {
            List<StoreData> storeDataList = storeManager.getStores(dto.getStore());
            clientStoreVo.setStoreData(storeDataList);
            return clientStoreVo;
        }
        return null;
    }

    @Override
    public com.github.pagehelper.PageInfo<ClientBaseInfoVo> getClientBaseInfo(QueryDto dto) {
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        List<ClientBaseInfoVo> voList = gaiaFranchiseeMapper.getClientBaseInfo();
        com.github.pagehelper.PageInfo<ClientBaseInfoVo> pageInfo = new com.github.pagehelper.PageInfo<>(voList);
        return pageInfo;
    }

    @Override
    public void updateInfo(ClientBaseInfoDto dto) {
     int num=  gaiaFranchiseeMapper.updateAll(dto.getVoList());
    }

    @Override
    public List<StoreData> getStores(List<String> dto) {
        List<StoreData>  list =gaiaFranchiseeMapper.getAllStores(dto);
        return list;
    }

    @Override
    public List<ClientBaseInfoVo> getClientsInfo(QueryDto dto) {
        List<ClientBaseInfoVo> voList=  gaiaFranchiseeMapper.getClientsInfo(dto.getClients());
        return voList;
    }

    @Override
    public ReportInfoExportVo getTotalReportInfo(List<ReportInfoVo> list) {
        //
        ReportInfoExportVo infoExportVo=new ReportInfoExportVo();
        infoExportVo.setSendNum(BigDecimal.ZERO);
        infoExportVo.setGzNum(BigDecimal.ZERO);
        infoExportVo.setFinalNum(BigDecimal.ZERO);
        infoExportVo.setSendAmount(BigDecimal.ZERO);
        infoExportVo.setGzAmount(BigDecimal.ZERO);
        infoExportVo.setFinalOrderAmount(BigDecimal.ZERO);
        infoExportVo.setSendProductNum(BigDecimal.ZERO);
        infoExportVo.setGzProductNum(BigDecimal.ZERO);
        infoExportVo.setFinalProductNum(BigDecimal.ZERO);
        for (ReportInfoVo vo: list){
            infoExportVo.setSendNum(infoExportVo.getSendNum().add(vo.getSendNum()));
            infoExportVo.setGzNum(infoExportVo.getGzNum().add(vo.getGzNum()));
            infoExportVo.setFinalNum(infoExportVo.getFinalNum().add(vo.getFinalNum()));
            infoExportVo.setDistributionNumRate(infoExportVo.getSendNum().divide(infoExportVo.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            infoExportVo.setSendNumRate(infoExportVo.getGzNum().divide(infoExportVo.getSendNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            infoExportVo.setFinalNumRate(infoExportVo.getGzNum().divide(infoExportVo.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");

            infoExportVo.setSendAmount(infoExportVo.getSendAmount().add(vo.getSendAmount()));
            infoExportVo.setGzAmount(infoExportVo.getGzAmount().add(vo.getGzAmount()));
            infoExportVo.setFinalOrderAmount(infoExportVo.getFinalOrderAmount().add(vo.getFinalOrderAmount()));

            infoExportVo.setDistributionAmountRate(infoExportVo.getSendAmount().divide(infoExportVo.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            infoExportVo.setSendAmountRate(infoExportVo.getGzAmount().divide(infoExportVo.getSendAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            infoExportVo.setFinalAmountRate(infoExportVo.getGzAmount().divide(infoExportVo.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");

            infoExportVo.setSendProductNum(infoExportVo.getSendProductNum().add(vo.getSendProductNum()));
            infoExportVo.setGzProductNum(infoExportVo.getGzProductNum().add(vo.getGzProductNum()));
            infoExportVo.setFinalProductNum(infoExportVo.getFinalProductNum().add(vo.getGzProductNum()));

            infoExportVo.setDistributionProductRate(infoExportVo.getSendProductNum().divide(infoExportVo.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            infoExportVo.setSendProductRate(infoExportVo.getGzProductNum().divide(infoExportVo.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            infoExportVo.setFinalProductRate(infoExportVo.getGzProductNum().divide(infoExportVo.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
        }
        infoExportVo.setDate("??????");
        return infoExportVo;
    }

    @Override
    public ReportInfoVo getListTotal(List<ReportInfoVo> list) {
            //
            ReportInfoVo infoExportVo=new ReportInfoVo();
            infoExportVo.setSendNum(BigDecimal.ZERO);
            infoExportVo.setGzNum(BigDecimal.ZERO);
            infoExportVo.setFinalNum(BigDecimal.ZERO);
            infoExportVo.setSendAmount(BigDecimal.ZERO);
            infoExportVo.setGzAmount(BigDecimal.ZERO);
            infoExportVo.setFinalOrderAmount(BigDecimal.ZERO);
            infoExportVo.setSendProductNum(BigDecimal.ZERO);
            infoExportVo.setGzProductNum(BigDecimal.ZERO);
            infoExportVo.setFinalProductNum(BigDecimal.ZERO);
            infoExportVo.setIndustryAverage(BigDecimal.ZERO);
            for (ReportInfoVo vo: list){
                infoExportVo.setSendNum(infoExportVo.getSendNum().add(vo.getSendNum()));
                infoExportVo.setGzNum(infoExportVo.getGzNum().add(vo.getGzNum()));
                infoExportVo.setFinalNum(infoExportVo.getFinalNum().add(vo.getFinalNum()));
                if(infoExportVo.getFinalNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setDistributionNumRate(infoExportVo.getSendNum().divide(infoExportVo.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setDistributionNumRate(BigDecimal.ZERO.toPlainString()+"%");
                }
                if(infoExportVo.getSendNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setSendNumRate(infoExportVo.getGzNum().divide(infoExportVo.getSendNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setSendNumRate(BigDecimal.ZERO.toPlainString()+"%");
                }
                if(infoExportVo.getFinalNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setFinalNumRate(infoExportVo.getGzNum().divide(infoExportVo.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setFinalNumRate(BigDecimal.ZERO.toPlainString()+"%");
                }
                infoExportVo.setSendAmount(infoExportVo.getSendAmount().add(vo.getSendAmount()));
                infoExportVo.setGzAmount(infoExportVo.getGzAmount().add(vo.getGzAmount()));
                infoExportVo.setFinalOrderAmount(infoExportVo.getFinalOrderAmount().add(vo.getFinalOrderAmount()));
                if(infoExportVo.getFinalOrderAmount().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setDistributionAmountRate(infoExportVo.getSendAmount().divide(infoExportVo.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setDistributionAmountRate(BigDecimal.ZERO.toPlainString()+"%");
                }
                if(infoExportVo.getSendAmount().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setSendAmountRate(infoExportVo.getGzAmount().divide(infoExportVo.getSendAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setSendAmountRate(BigDecimal.ZERO.toPlainString()+"%");
                }
                if(infoExportVo.getFinalOrderAmount().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setFinalAmountRate(infoExportVo.getGzAmount().divide(infoExportVo.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setFinalAmountRate(BigDecimal.ZERO.toPlainString()+"%") ;
                }


                infoExportVo.setSendProductNum(infoExportVo.getSendProductNum().add(vo.getSendProductNum()));
                infoExportVo.setGzProductNum(infoExportVo.getGzProductNum().add(vo.getGzProductNum()));
                infoExportVo.setFinalProductNum(infoExportVo.getFinalProductNum().add(vo.getGzProductNum()));
                if(infoExportVo.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setDistributionProductRate(infoExportVo.getSendProductNum().divide(infoExportVo.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setDistributionProductRate(BigDecimal.ZERO.toPlainString()+"%") ;
                }
                if(infoExportVo.getSendProductNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setSendProductRate(infoExportVo.getGzProductNum().divide(infoExportVo.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setSendProductRate(BigDecimal.ZERO.toPlainString()+"%") ;
                }
                if(infoExportVo.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setFinalProductRate(infoExportVo.getGzProductNum().divide(infoExportVo.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
                }else {
                    infoExportVo.setFinalProductRate(BigDecimal.ZERO.toPlainString()+"%");
                }
                infoExportVo.setIndustryAverage(infoExportVo.getIndustryAverage().add(vo.getIndustryAverage()));
            }
            if(list.size()>0){
                infoExportVo.setIndustryAverage(infoExportVo.getIndustryAverage().divide(BigDecimal.valueOf(list.size()),4,RoundingMode.HALF_UP).setScale(2,RoundingMode.HALF_UP));
                infoExportVo.setIndustryAverageStr(infoExportVo.getIndustryAverage().toPlainString()+"%");
                if(infoExportVo.getFinalProductNum().compareTo(BigDecimal.ZERO)>0){
                    infoExportVo.setAverageRate(infoExportVo.getGzProductNum().divide(infoExportVo.getFinalProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
                }else {
                    infoExportVo.setAverageRate(BigDecimal.ZERO);
                }
                infoExportVo.setAverageRateStr(infoExportVo.getAverageRate().toPlainString()+"%");
                infoExportVo.setDifference(infoExportVo.getAverageRate().subtract(infoExportVo.getIndustryAverage()).toPlainString()+"%");
                infoExportVo.setDate("??????");
            }
            return infoExportVo;
    }


    public static void main(String[] args) {

    }

}

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
        //初始化报表类型 "1":日报 "2":周报 "3":月报
        List<CommonVo> reportTypes = new ArrayList<>(Arrays.asList(new CommonVo("1", "日报"), new CommonVo("2", "周报"), new CommonVo("3", "月报")));
        res.put("reportTypes", reportTypes);

        //初始化满足率类型 1-开单配货率 2-仓库发货率 3-最终下货率
        List<CommonVo> rateTypes = new ArrayList<>(Arrays.asList(new CommonVo("1", "开单配货率"), new CommonVo("2", "仓库发货率"), new CommonVo("3", "最终下货率")));
        res.put("rateTypes", rateTypes);
        return res;
    }

    @Override
    public PageInfo reportData(GetLoginOutData loginUser, DropRateInData inData) {

        if (StrUtil.isBlank(inData.getStartDate()) || StrUtil.isBlank(inData.getEndDate())) {
            throw new BusinessException("开始时间和结束时间必填");
        }
        if (StrUtil.isBlank(inData.getReportType())) {
            throw new BusinessException("报表类型必填");
        }
        if (inData.getRateType() == null) {
            throw new BusinessException("满足率类型必填");
        }
        List<DropRateRes> resList = new ArrayList<>();
        //根据用户输入条件进行的汇总，汇总数据，显示时间/数量下货率/金额下货率/品项下货率/
        Map<String, DropRateRes> inConditionDataMap = handleClientBasicInfoMap(loginUser.getClient(), inData);

        //统计行业平均值
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
        PageInfo pageInfo; // 创建分页对象
        if (CollUtil.isNotEmpty(resList)) {  // 判断集合是否为空
            pageInfo = new PageInfo(resList);
            DropRateRes totalInfo = handleTotal(resList);
            if(totalInfo!=null){
                totalInfo.setShowTime("合计");
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
                    //开单配货
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
                // 数量
                total.setOrderNum(total.getOrderNum().add(res.getOrderNum()));
                total.setDnNum(total.getDnNum().add(res.getDnNum()));
                total.setSendNum(total.getSendNum().add(res.getSendNum()));
                total.setGzNum(total.getGzNum().add(res.getGzNum()));
                total.setFinalNum(total.getFinalNum().add(res.getFinalNum()));
              //  total.setRawQuantity(total.getRawQuantity().add(res.getRawQuantity()));
                // 金额
                total.setOrderAmount(total.getOrderAmount().add(res.getOrderAmount()));
                total.setDnAmount(total.getDnAmount().add(res.getDnAmount()));
                total.setSendAmount(total.getSendAmount().add(res.getSendAmount()));
                total.setGzAmount(total.getGzAmount().add(res.getGzAmount()));
                total.setFinalOrderAmount(total.getFinalOrderAmount().add(res.getFinalOrderAmount()));

                // 品项
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
     * 统计行业品项平均值,此处统计所有的加盟商的，并符合用户筛选范围内的数据
     *
     * @return
     */
    private Map<String, ReportInfoSummaryVo> handleAverageInfoMap(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        Map<String, ReportInfoSummaryVo> resMap = new HashMap<>();
        //List<GaiaXhlH> dbDataList = findInConditionData(dto);
        // 补货门店
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
     * 统计行业品项平均值,此处统计所有的加盟商的，并符合用户筛选范围内的数据
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
            //日报
            case "1":
                List<DropRateRes> dayList = handleAverageDayData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //周报
            case "2":
                List<DropRateRes> weekList = handleAverageWeekData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(weekList)) {
                    resList = weekList;
                }
                break;
            //月报
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
     * 处理月度报表
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleAverageMonthData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();
        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yy-MM");
            //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
            String key = h.getYearName() + "年" + tjDateTranceStr.split("-")[1] + "月";
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
                //处理最终结果
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * 处理周报表
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleAverageWeekData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();
        for (GaiaXhlH h : dbDataList) {
            //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
            String key = h.getYearName() + "年" + h.getWeekNum() + "周";
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
                //处理最终结果
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * 处理日度报表
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleAverageDayData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        //db中存储的就是每天加盟商级别的统计信息，直接进行处理展示即可
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();
        for (GaiaXhlH h : dbDataList) {
            String key = DateUtil.format(h.getTjDate(), "yyyy年MM月dd日");
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
     * 处理报表前4列数据，展示日期，数量下货率，金额下货率，品项下货率，统计级别为加盟商一个时间段内的数据
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
     * 根据报表类型不同进行不同的处理
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleDbDataByReportType(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        String reportType = inData.getReportType();
        switch (reportType) {
            //日报
            case "1":
                List<DropRateRes> dayList = handleDayData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //周报
            case "2":
                List<DropRateRes> weekList = handleWeekData(dbDataList, inData);
                if (CollectionUtil.isNotEmpty(weekList)) {
                    resList = weekList;
                }
                break;
            //月报
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
     * 处理月度报表
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleMonthData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();

        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yyyy-MM");
            //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
            String key = h.getYearName() + "年" + tjDateTranceStr.split("-")[1] + "月";
            DropRateRes res = null;
            if (tempMap.get(key) == null) {
                res = new DropRateRes();
                res.setShowTime(key);

                //数量
                res.setUpQuantity(h.getUpQuantity());
                res.setDownQuantity(h.getDownQuantity());

                //金额
                res.setUpAmount(h.getUpAmount());
                res.setDownAmount(h.getDownAmount());

                //品项
                res.setUpProductNum(h.getUpProductNum());
                res.setDownProductNum(h.getDownProductNum());

                //industryAverage 行业平均值
                res.setIndustryAverage(h.getIndustryAverage());
                res.setCount(new BigDecimal("1"));

            } else {
                res = tempMap.get(key);
                //数量
                res.setUpQuantity(res.getUpQuantity().add(h.getUpQuantity()));
                res.setDownQuantity(res.getDownQuantity().add(h.getDownQuantity()));

                //金额
                res.setUpAmount(res.getUpAmount().add(h.getUpAmount()));
                res.setDownAmount(res.getDownAmount().add(h.getDownAmount()));

                //品项
                res.setUpProductNum(res.getUpProductNum().add(h.getUpProductNum()));
                res.setDownProductNum(res.getDownProductNum().add(h.getDownProductNum()));

                //industryAverage 行业平均值
                res.setIndustryAverage(res.getIndustryAverage().add(h.getIndustryAverage()));
                res.setCount(res.getCount().add(new BigDecimal("1")));
            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                DropRateRes data = tempMap.get(key);
                //处理最终结果
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
     * 处理周报表
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleWeekData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        List<DropRateRes> resList = new ArrayList<>();
        Map<String, DropRateRes> tempMap = new HashMap<>();

        for (GaiaXhlH h : dbDataList) {
            //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
            String key = h.getYearName() + "年" + h.getWeekNum() + "周";
            DropRateRes res = null;
            BigDecimal count = BigDecimal.ZERO;
            if (tempMap.get(key) == null) {
                res = new DropRateRes();
                res.setShowTime(key);

                //数量
                res.setUpQuantity(h.getUpQuantity());
                res.setDownQuantity(h.getDownQuantity());

                //金额
                res.setUpAmount(h.getUpAmount());
                res.setDownAmount(h.getDownAmount());

                //品项
                res.setUpProductNum(h.getUpProductNum());
                res.setDownProductNum(h.getDownProductNum());

                //industryAverage 行业平均值
                res.setIndustryAverage(h.getIndustryAverage());
                res.setCount(new BigDecimal("1"));

            } else {
                res = tempMap.get(key);
                //数量
                res.setUpQuantity(res.getUpQuantity().add(h.getUpQuantity()));
                res.setDownQuantity(res.getDownQuantity().add(h.getDownQuantity()));

                //金额
                res.setUpAmount(res.getUpAmount().add(h.getUpAmount()));
                res.setDownAmount(res.getDownAmount().add(h.getDownAmount()));

                //品项
                res.setUpProductNum(res.getUpProductNum().add(h.getUpProductNum()));
                res.setDownProductNum(res.getDownProductNum().add(h.getDownProductNum()));

                //industryAverage 行业平均值
                res.setIndustryAverage(res.getIndustryAverage().add(h.getIndustryAverage()));
                res.setCount(res.getCount().add(new BigDecimal("1")));

            }
            tempMap.put(key, res);
        }
        if (CollectionUtil.isNotEmpty(tempMap)) {
            for (String key : tempMap.keySet()) {
                DropRateRes data = tempMap.get(key);
                //处理最终结果
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
     * 处理日度报表
     *
     * @param inData
     * @return
     */
    private List<DropRateRes> handleDayData(List<GaiaXhlH> dbDataList, DropRateInData inData) {
        //db中存储的就是每天加盟商级别的统计信息，直接进行处理展示即可
        List<DropRateRes> resList = new ArrayList<>();
        for (GaiaXhlH h : dbDataList) {
            String tjDateTranceStr = DateUtil.format(h.getTjDate(), "yyyy年MM月dd日");
            DropRateRes res = new DropRateRes();
            BeanUtils.copyProperties(h, res);
            res.setShowTime(tjDateTranceStr);
            resList.add(res);
        }
        return resList;
    }


    /**
     * 根据条件查询符合情况的原始数据---此处数据源来源为app端实现底层逻辑
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
                //分子数量：开单数量、过账数量(排除铺货)
                gaiaXhlH.setUpQuantity(gaiaXhlH.getUpQuantity().setScale(0, BigDecimal.ROUND_HALF_UP));
                //分母数量：订单数量、开单数量(排除铺货)
                gaiaXhlH.setDownQuantity(gaiaXhlH.getDownQuantity().setScale(0, BigDecimal.ROUND_HALF_UP));
                //数量满足率(排除铺货)
                //gaiaXhlH.setQuantityRate(gaiaXhlH.getQuantityRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //开单金额、配送金额(排除铺货)
                gaiaXhlH.setUpAmount(gaiaXhlH.getUpAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                //订单金额、开单金额(排除铺货)
                gaiaXhlH.setDownAmount(gaiaXhlH.getDownAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                //金额满足率(排除铺货)
                //gaiaXhlH.setAmountRate(gaiaXhlH.getAmountRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //开单品项、过账品项(排除铺货)
                gaiaXhlH.setUpProductNum(gaiaXhlH.getUpProductNum().setScale(0, BigDecimal.ROUND_HALF_UP));
                //订单品项、开单品项(排除铺货)
                gaiaXhlH.setDownProductNum(gaiaXhlH.getDownProductNum().setScale(0, BigDecimal.ROUND_HALF_UP));
                //品项满足率(排除铺货)
                // gaiaXhlH.setProductRate(gaiaXhlH.getProductRate().setScale(0, BigDecimal.ROUND_HALF_UP));
            });
            //判断是否计入主动铺货 0否 1是
            if(Objects.equals(1,inData.getShopGoodsType())){
                for (GaiaXhlH gaiaXhlH : dbList) {
                    //分子数量：开单数量、过账数量(排除铺货)
                    gaiaXhlH.setUpQuantity(gaiaXhlH.getUpQuantityLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //分母数量：订单数量、开单数量(排除铺货)
                    gaiaXhlH.setDownQuantity(gaiaXhlH.getDownQuantityLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //数量满足率(排除铺货)
                    gaiaXhlH.setQuantityRate(gaiaXhlH.getQuantityRateLess());
                    //开单金额、配送金额(排除铺货)
                    gaiaXhlH.setUpAmount(gaiaXhlH.getUpAmountLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //订单金额、开单金额(排除铺货)
                    gaiaXhlH.setDownAmount(gaiaXhlH.getDownAmountLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //金额满足率(排除铺货)
                    gaiaXhlH.setAmountRate(gaiaXhlH.getAmountRateLess());
                    //开单品项、过账品项(排除铺货)
                    gaiaXhlH.setUpProductNum(gaiaXhlH.getUpProductNumLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //订单品项、开单品项(排除铺货)
                    gaiaXhlH.setDownProductNum(gaiaXhlH.getDownProductNumLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                    //品项满足率(排除铺货)
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
     * 获取每天的 最终下货率 开单配货率 仓库发货率 平均值
     * 最终下货率与开单配货率取90.0%-95.0%的随机值，最终下货率≤开单配货率，取值后保持不变。仓库发货率取95.0%-100.0%的随机值。PS:随机值连续相同值次数≤3
     * 以下属性当作map的key
     * finalIndustryAverage 最终下货率
     * billingIndustryAverage 开单配货率
     * depotIndustryAverage 仓库发货率
     * @return
     */
    @Override
    public Map<String, BigDecimal> getIndustryAverage(String clientId) {
        //今天日期 2010-01-10格式
        String todayDate = com.gys.util.DateUtil.formatDate2(new Date());
        //昨天日期
        String yesterdayDate = com.gys.util.DateUtil.getFormatDate(LocalDate.now().minusDays(1));
        //大昨天日期
        String bigYesterdayDate = com.gys.util.DateUtil.getFormatDate(LocalDate.now().minusDays(2));
        //拿这三天来做比较
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
            //配货
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
                        //最终大于开单
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
            //获取不到前两天数据
            randomNumber = getRandomNumber();
        }
        return randomNumber;
    }

    /**
     * 1,3类型取90.0%-95.0%的随机值
     * 2，类型取95.0%-100.0%的随机值
     * finalIndustryAverage 最终下货率
     * billingIndustryAverage 开单配货率
     *  DepotIndustryAverage 仓库发货率
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
     * 获取已过账的下货率详情列表
     */
    @Override
    public PageInfo getHistoryPostedUnloadRateDetailList(GetLoginOutData loginUser, GetHistoryPostedUnloadRateDetailResponse pageQueryForm) {
        GetHistoryPostedUnloadRateDetailListForm form = new GetHistoryPostedUnloadRateDetailListForm(); //pageQueryForm.getConditions().get(0);

        //商品大类
        List<SearchRangeDTO> setCommodityCategory = new ArrayList<>();
        SearchRangeDTO rangeDTO  = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getProClass());
        rangeDTO.setEndRange(pageQueryForm.getProClass());
        setCommodityCategory.add(rangeDTO);
        form.setCommodityCategory(setCommodityCategory);

        //请货日期
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartPleaseOrderDate());
        rangeDTO.setEndRange(pageQueryForm.getEndPleaseOrderDate());
        setCommodityCategory.add(rangeDTO);
        form.setPleaseOrderDate(setCommodityCategory);

        //deliveryOrderDate 开单日期
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartDeliveryOrderDate());
        rangeDTO.setEndRange(pageQueryForm.getEndDeliveryOrderDate());
        setCommodityCategory.add(rangeDTO);
        form.setDeliveryOrderDate(setCommodityCategory);

        //postingDate 过账日期
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartPostingDate());
        rangeDTO.setEndRange(pageQueryForm.getEndPostingDate());
        setCommodityCategory.add(rangeDTO);
        form.setPostingDate(setCommodityCategory);

        //postingDate 客户编码
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getStartCustomerNo());
        rangeDTO.setEndRange(pageQueryForm.getEndCustomerNo());
        setCommodityCategory.add(rangeDTO);
        form.setCustomerNo(setCommodityCategory);



        //商品条件
        setCommodityCategory = new ArrayList<>();
        rangeDTO = new SearchRangeDTO();
        rangeDTO.setStartRange(pageQueryForm.getProCondition());
        rangeDTO.setEndRange(pageQueryForm.getProCondition());
        setCommodityCategory.add(rangeDTO);
        form.setCommodityCondition(setCommodityCategory);

        log.info("获取已过账的下货率详情列表开始,参数{} " + JSONObject.toJSONString(form));
        loginUser.setClient(null);
        loginUser.setDcCode(null);
        PageInfo pageInfo = new PageInfo();
        SearchRangeDTO pleaseOrderDate = ConditionUtils.getFirstRangeCondition(form.getPleaseOrderDate());//请货日期
        SearchRangeDTO deliveryOrderDate = ConditionUtils.getFirstRangeCondition(form.getDeliveryOrderDate());//开单日期
        SearchRangeDTO postingDate = ConditionUtils.getFirstRangeCondition(form.getPostingDate());//过账日期
        SearchRangeDTO customerNo = ConditionUtils.getFirstRangeCondition(form.getCustomerNo());//客户编码
        String commodityCategory = ConditionUtils.getFirstStartCondition(form.getCommodityCategory());//商品大类
        String commodityCondition = ConditionUtils.getFirstStartCondition(form.getCommodityCondition());//商品条件
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
        //配货品项合计
        BigDecimal distributionItems = BigDecimal.ZERO;
        //发货品项合计
        BigDecimal deliveryItem = BigDecimal.ZERO;
        //最终品项合计
        BigDecimal finalItem = BigDecimal.ZERO;

        Integer number  = 1;
        for (int i = 0; i < unloadRateDetailList.size(); i++) {
            unloadRateDetailList.get(i).setNumber(Convert.toStr(number));
            number +=1;
            String isPost = unloadRateDetailList.get(i).getIsPost();//是否过账,0:否,1:是

            //单价
            BigDecimal matmovprice = unloadRateDetailList.get(i).getMatmovprice();
            //单价
            if (matmovprice == null) {
                matmovprice = new BigDecimal("1.0000");
            }

            //订单数量
            BigDecimal orderQuantity
                    = unloadRateDetailList.get(i).getOrderQuantity();
            //配送单原数量
            BigDecimal deliveryQuantity
                    = unloadRateDetailList.get(i).getDeliveryQuantity();
            //配送单过账数量
            BigDecimal deliveryPostedQuantityRate = BigDecimal.ZERO;
            if (null != unloadRateDetailList.get(i).getDeliveryPostedQuantityRate()) {
                deliveryPostedQuantityRate = unloadRateDetailList.get(i).getDeliveryPostedQuantityRate();
            }

            //GetHistoryPostedUnloadRateDetailListVO deliveryRateVO = new GetHistoryPostedUnloadRateDetailListVO();
            if (orderQuantity == null || orderQuantity.equals(zero) || matmovprice.equals(zero)) {
                unloadRateDetailList.get(i).setDeliveryQuantitySatisfiedRate("0.00%");//配货数量满足率
                unloadRateDetailList.get(i).setDeliveryAmountSatisfiedRate("0.00%");//配货金额满足率
                distributionItems = distributionItems.add(new BigDecimal("1.00"));
            } else {

                //配货数量满足率 = 配送单原数量/订单数量
                String deliveryQuantitySatisfiedRate
                        = (deliveryQuantity.divide(orderQuantity, 2, BigDecimal.ROUND_HALF_UP)).multiply(hundred).toString() + "%";
                //配货金额满足率 = (配送单原数量*单价)/(订单原数量*单价)
                String deliveryAmountSatisfiedRate
                        = (((deliveryQuantity.multiply(matmovprice))
                        .divide((orderQuantity.multiply(matmovprice)), 2, BigDecimal.ROUND_HALF_UP))).multiply(hundred).toString() + "%";

                unloadRateDetailList.get(i).setDeliveryQuantitySatisfiedRate(deliveryQuantitySatisfiedRate);//配货数量满足率
                unloadRateDetailList.get(i).setDeliveryAmountSatisfiedRate(deliveryAmountSatisfiedRate);//配货金额满足率

                //配货品项
                if (!deliveryQuantitySatisfiedRate.equals("0.00%") && !deliveryAmountSatisfiedRate.equals("0.00%"))
                    distributionItems = distributionItems.add(new BigDecimal("1.00"));
            }

            //开单还未过帐的，查询时先计算配货满足率，另外两个满足率先空着，等过帐后再出数据
            if (StringUtils.isNotBlank(isPost) && isPost.equals("1")) {//==================开单已过账,三个满足率都计算,开单未过账,只计算配货率

                if (deliveryQuantity.equals(zero) || deliveryPostedQuantityRate.equals(zero) || matmovprice.equals(zero)) {
                    unloadRateDetailList.get(i).setShipmentQuantitySatisfiedRate("0.00%");//发货数量满足率
                    unloadRateDetailList.get(i).setShipmentAmountSatisfiedRate("0.00%");//发货金额满足率
                } else {
                    //发货数量满足率 = 配送单过账数量/配送单原数量
                    String shipmentQuantitySatisfiedRate
                            = (deliveryPostedQuantityRate.divide(deliveryQuantity, 2, BigDecimal.ROUND_HALF_UP)).multiply(hundred).toString() + "%";
                    unloadRateDetailList.get(i).setShipmentQuantitySatisfiedRate(shipmentQuantitySatisfiedRate);//发货数量满足率
                    //发货金额满足率 = (配送单过账数量*单价)/(配送单原数量*单价)
                    String shipmentAmountSatisfiedRate
                            = (((deliveryPostedQuantityRate.multiply(matmovprice))
                            .divide((deliveryQuantity.multiply(matmovprice)), 2, BigDecimal.ROUND_HALF_UP))).multiply(hundred).toString() + "%";
                    unloadRateDetailList.get(i).setShipmentAmountSatisfiedRate(shipmentAmountSatisfiedRate);//发货金额满足率
                    deliveryItem = deliveryItem.add(new BigDecimal("1.00"));
                }

                if (orderQuantity == null || orderQuantity.equals(zero) || matmovprice.equals(zero)) {
                    unloadRateDetailList.get(i).setFinalQuantitySatisfiedRate("0.00%");//最终数量满足率
                    unloadRateDetailList.get(i).setFinalAmountSatisfiedRate("0.00%");//最终金额满足率
                    finalItem = finalItem.add(new BigDecimal("1.00"));
                } else {

                    //最终数量满足率 = 配送单过账数量/订单数量
                    String finalQuantitySatisfiedRate
                            = (deliveryPostedQuantityRate.divide(orderQuantity, 2, BigDecimal.ROUND_HALF_UP)).multiply(hundred).toString() + "%";
                    //最终金额满足率 = (配送单过账数量*单价)/(订单数量*单价)
                    String finalAmountSatisfiedRate
                            = (((deliveryPostedQuantityRate.multiply(matmovprice))
                            .divide((orderQuantity.multiply(matmovprice)), 2, BigDecimal.ROUND_HALF_UP))).multiply(hundred).toString() + "%";

                    unloadRateDetailList.get(i).setFinalQuantitySatisfiedRate(finalQuantitySatisfiedRate);//最终数量满足率
                    unloadRateDetailList.get(i).setFinalAmountSatisfiedRate(finalAmountSatisfiedRate);//最终金额满足率

                    //最终品项
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
        // 全部合计
        GetHistoryPostedUnloadRateTotalDTO dto = this.xhlHMapper.selectHistoryTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        BigDecimal total = dto.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
        //配送单原数量大于零的行数
        Integer integer = this.xhlHMapper.selectTotalNumber(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        //已过账行数合计
        Integer isPostTotal = this.xhlHMapper.selectIsPostTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        //已过账且配送单原数量大于零行数合计
        Integer isPostDeliveryTotal = this.xhlHMapper.selectisPostDeliveryTotal(pageQueryForm.getClientId(),pageQueryForm.getDcId(),pageQueryForm.getReplenishStyle(),pleaseOrderDate, deliveryOrderDate, postingDate, customerNo, commodityCategory, commodityCondition, loginUser.getClient(), loginUser.getDcCode());
        //配送过账数量大于零的行数
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
        deliveryRateVO.setDeliveryItemSatisfiedRateTotal(BigDecimal.ZERO.compareTo(total)==0?"0.00%":new BigDecimal(integer).divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//配货品项满足率
        deliveryRateVO.setDeliveryItemSatisfiedRate(BigDecimal.ZERO.compareTo(total)==0?"0.00%":new BigDecimal(integer).divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//配货品项满足率
        if (isPostNumberTotal > 0) {
            deliveryRateVO.setShipmentItemSatisfiedRateTotal(BigDecimal.ZERO.compareTo(new BigDecimal(isPostDeliveryTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostDeliveryTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//发货品项满足率
            deliveryRateVO.setShipmentItemSatisfiedRate(BigDecimal.ZERO.compareTo(new BigDecimal(isPostDeliveryTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostDeliveryTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//发货品项满足率
            deliveryRateVO.setFinalItemSatisfiedRateTotal(BigDecimal.ZERO.compareTo(new BigDecimal(isPostTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//最终品项满足率
            deliveryRateVO.setFinalItemSatisfiedRate(BigDecimal.ZERO.compareTo(new BigDecimal(isPostTotal))==0?"0.00%":new BigDecimal(isPostNumberTotal).divide(new BigDecimal(isPostTotal), 4, BigDecimal.ROUND_HALF_UP).multiply(hundred).setScale(2, RoundingMode.HALF_UP).toString() + "%");//最终品项满足率
        }
        unloadRateDetailList.forEach(detailVO -> {
            //商品大类
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
     * 获取所有客户(加盟商)
     * @param loginUser 登陆人信息
     * @param clientId 加盟商编号
     * @param clientName 加盟商姓名
     * @return
     */
    @Override
    public List<GetClientListDTO> getClientList(GetLoginOutData loginUser, String clientName) {
        List<GetClientListDTO> clientList = this.xhlHMapper.getClientList(clientName);
        return clientList;
    }

    /**
     * 获取捕获方式
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
                    map.put("name", "正常补货");
                    list.add(map);
                } else if (Objects.equals(type, "1")) {
                    map.put("type", "1");
                    map.put("name", "紧急补货");
                    list.add(map);
                } else if (Objects.equals(type, "2")) {
                    map.put("type", "2");
                    map.put("name", "铺货");
                    list.add(map);
                } else if (Objects.equals(type, "3")) {
                    map.put("type", "3");
                    map.put("name", "互调");
                    list.add(map);
                } else if (Objects.equals(type, "4")) {
                    map.put("type", "4");
                    map.put("name", "直配");
                    list.add(map);
                }
            }
        }
        return list;

    }

    /**
     * 下货率 -- 商品大类下拉
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
        String selDate = generateHistoryPostedUnloadRateForm.getStatisticDate();//统计日期
        System.out.println(selDate);
        log.info("统计已过账的下货率,插入下货率表,统计日期: " + selDate);

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

        //配货品项
        BigDecimal deliveryItem = BigDecimal.ZERO;
        //发货品项
        BigDecimal deliverGoodsItem = BigDecimal.ZERO;
        //最终品项
        BigDecimal finalItem = BigDecimal.ZERO;

        //配送单原数量总数
        BigDecimal deliveryQuantityTotal = BigDecimal.ZERO;
        //订单原数量总数
        BigDecimal orderquantityTotal = BigDecimal.ZERO;
        //过账总数量
        BigDecimal postedquantityTotal =  BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);

        //订单总金额
        BigDecimal orderAmountTotal = BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);
        //配送单总金额
        BigDecimal deliveryAmountTotal = BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);
        //配送单过账总金额
        BigDecimal postAmountTotal = BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);

        for (GaiaViewWmsTjXhl tjXhl : list) {
            deliveryQuantityTotal = deliveryQuantityTotal.add(tjXhl.getDeliveryquantity()).setScale(2, RoundingMode.HALF_UP);
            orderquantityTotal = orderquantityTotal.add(tjXhl.getOrderquantity()).setScale(2, RoundingMode.HALF_UP);
            postedquantityTotal = postedquantityTotal.add(Optional.ofNullable(tjXhl.getDeliverypostedquantityrate())
                    .orElse(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP)));

            //配送单原数量
            BigDecimal deliveryquantity = tjXhl.getDeliveryquantity();
            //订单原数量
            BigDecimal orderQuantity = tjXhl.getOrderquantity();
            //过账数量
            BigDecimal postQuantity = tjXhl.getDeliverypostedquantityrate();
            if(postQuantity ==null){
                postQuantity =  BigDecimal.ZERO.setScale(4,RoundingMode.HALF_UP);
            }
            //移动单价
            BigDecimal matmovprice = tjXhl.getMatmovprice();
            if(matmovprice==null){
                matmovprice = new BigDecimal("1.0000");
            }
            //配货品项
            if(!deliveryquantity.equals("0.0000") && !orderQuantity.equals("0.0000") && !matmovprice.equals("0.0000") ){
                deliveryItem = deliveryItem.add(new BigDecimal("1.00"));
            }
            //发货品项,最终品项
            if(!postQuantity.equals("0.0000")){
                deliverGoodsItem =deliverGoodsItem.add(new BigDecimal("1.00"));
                finalItem = finalItem.add(new BigDecimal("1.00"));
            }
            //订单总金额
            orderAmountTotal =orderAmountTotal.add(orderQuantity.multiply(matmovprice));
            //配送单总金额
            deliveryAmountTotal = deliveryAmountTotal.add(deliveryquantity.multiply(matmovprice));
            //过账总金额
            postAmountTotal = postAmountTotal.add(postQuantity.multiply(matmovprice));
        }
        // 先删后插
        TjXhl xhl = new TjXhl();
        xhl.setClient(client);
        xhl.setProSite(proSite);
        xhl.setZyRq(selDate);

        this.xhlHMapper.del(client , proSite, selDate);

        TjXhl tjXhl = new TjXhl();
        tjXhl.setClient(client);//加盟商
        tjXhl.setProSite(proSite);//地点
        tjXhl.setZyRq(selDate);//作业日期

        //配货数量满足率 = 配送单原数量总数/订单原数量总数
        tjXhl.setSlMzl(deliveryQuantityTotal.divide(orderquantityTotal,2,RoundingMode.HALF_UP));
        //配货金额满足率 = 配送单原数量总金额/订单总金额
        tjXhl.setJeMzl(deliveryAmountTotal.divide(orderAmountTotal,2,RoundingMode.HALF_UP));
        //配货品项满足率
        tjXhl.setPxMzl(deliveryItem = deliveryItem.divide(size,2,RoundingMode.HALF_UP));
        tjXhl.setZyXl("1");
        //tjXhl.setId();
        this.xhlHMapper.save1(tjXhl);


        //发货数量满足率 = 配送单过账总数量/配送单原数量总数量
        if (deliveryQuantityTotal.compareTo(BigDecimal.ZERO) == 0){
            tjXhl.setSlMzl(new BigDecimal(1));
        }else {
            tjXhl.setSlMzl(postedquantityTotal.divide(deliveryQuantityTotal,2,RoundingMode.HALF_UP));
        }
        //发货金额满足率 = 配送单过账总金额/配送单原数量总金额
        if (deliveryAmountTotal.compareTo(BigDecimal.ZERO) == 0){
            tjXhl.setJeMzl(new BigDecimal(1));
        }else {
            tjXhl.setJeMzl(postAmountTotal.divide(deliveryAmountTotal,2,RoundingMode.HALF_UP));
        }
        //发货品项满足率
        tjXhl.setPxMzl(deliveryItem = deliveryItem.divide(size,2,RoundingMode.HALF_UP));
        tjXhl.setZyXl("2");
        //暂停
        //this.baseMapper.insert(tjXhl);

        //最终数量满足率 = 配送单过账总数量/订单总数量
        tjXhl.setSlMzl(postedquantityTotal.divide(orderquantityTotal,2,RoundingMode.HALF_UP));
        //最终金额满足率 = 配送单过账总金额/订单总金额
        tjXhl.setJeMzl(postAmountTotal.divide(orderAmountTotal,2,RoundingMode.HALF_UP));
        //最终满足率
        tjXhl.setPxMzl(finalItem = finalItem.divide(size,2,RoundingMode.HALF_UP));
        tjXhl.setZyXl("3");
        //暂停
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

    //4.配货数量下货率：配送单原数量/原订单数量
    //5.
    //6.
    //7.出库原金额：配送单所有编码金额之和
    //8.过账金额：配送单所有编码过账金额之和
    //9.原订单金额：原订单所有编码金额之和

    //10.配货金额下货率：配送单原金额/原订单金额
    //11.发货金额下货率：配送单过帐金额/配送单原金额
    //12.最终金额下货率：配送单过帐金额/订单金额

    //13.出库原品项：配送单编码去重计数
    //14.过账品项：配送单所有过账编码数量去重计数
    //15. 原订单品项：原订单编码去重计数
    //16.配货品项下货率：配送单原品项数/原订单品项数
    //17.发货品项下货率：配送单过帐品项数/配送单原品项数
    //18.最终品项下货率：配送单过账品项数/原订单品项数
    //19.品项平均下货率：取计算时间范围内药德所有客户的下货率平均值，即所有客户（配送单过账品项数之和（分子）与订单品项数之和（分母）的比较）
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
            //配货数量下货率：配送单原数量/原订单数量
            vo.setDistributionNumRate(vo.getDnNum().divide(vo.getOrderNum(),4,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            //发货数量下货率：配送单过帐数量/配送单原数量
            if(vo.getSendNum().compareTo(BigDecimal.ZERO)==0){
                vo.setSendNumRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setSendNumRate(vo.getGzNum().divide(vo.getSendNum(),4,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //最终数量下货率：配送单过帐数量/订单数量
            if(vo.getFinalNum().compareTo(BigDecimal.ZERO)==0){
                vo.setFinalNumRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setFinalNumRate(vo.getGzNum().divide(vo.getFinalNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //配货金额下货率：配送单原金额/原订单金额
            if(vo.getOrderAmount().compareTo(BigDecimal.ZERO)==0){
                vo.setDistributionAmountRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setDistributionAmountRate(vo.getDnAmount().divide(vo.getOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //发货金额下货率：配送单过帐金额/配送单原金额
            if(vo.getSendAmount().compareTo(BigDecimal.ZERO)==0){
                vo.setSendAmountRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setSendAmountRate(vo.getGzAmount().divide(vo.getSendAmount(),4,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //12.最终金额下货率：配送单过帐金额/订单金额
            if(vo.getFinalOrderAmount().compareTo(BigDecimal.ZERO)==0){
                vo.setFinalAmountRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setFinalAmountRate(vo.getGzAmount().divide(vo.getFinalOrderAmount(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            //配货品项下货率：配送单原品项数/原订单品项数
            vo.setDistributionProductRate(vo.getDnProductNum().divide(vo.getOrderProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            //发货品项下货率：配送单过帐品项数/配送单原品项数
            if(vo.getSendProductNum().compareTo(BigDecimal.ZERO)==0) {
                vo.setSendProductRate(BigDecimal.ZERO.toPlainString()+"%");
            }else {
                vo.setSendProductRate(vo.getGzProductNum().divide(vo.getSendProductNum(),4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2).toPlainString()+"%");
            }
            // 最终品项下货率：配送单过账品项数/原订单品项数
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
        //基础数据
        List<GaiaXhlH> dbDataList = findInConditionData(dto);
        // 汇总处理
        Map<String, ReportInfoSummaryVo> inConditionDataMap = handleClientBasicInfoMap(dbDataList,dto);
        //统计行业平均值
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
        PageInfo pageInfo; // 创建分页对象
        if (CollUtil.isNotEmpty(resList)) {  // 判断集合是否为空
            pageInfo = new PageInfo(resList);
            ReportInfoSummaryVo totalInfo = handleTotal1(resList);
            if(totalInfo!=null){
                totalInfo.setDate("合计");
                pageInfo.setListNum(totalInfo);
            }
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }



    private Map<String, ReportInfoSummaryVo> handleClientBasicInfoMap(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        // 基础数据处理
        List<String> collect = dbDataList.stream().map(GaiaXhlH::getClient).distinct().collect(Collectors.toList());
        Map<String, ReportInfoSummaryVo> resMap = new HashMap<>();
        //补货基础数据
        ReplenishDto replenishDto =new ReplenishDto();
        replenishDto.setClients(collect);
        replenishDto.setBeginDate(com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getStartDate(), "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00"));
        replenishDto.setEndDate(com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getEndDate(), "yyyy-MM-dd"), "yyyy-MM-dd 23:59:59"));
       // List<ReplenishVo> replenishVos =  gaiaSdReplenishHMapper.getAllReplenishes(replenishDto);
          if(CollectionUtils.isEmpty(collect)){
              // 查不到数据直接返回
              return resMap;
          }
        Map<String, List<ClientBaseInfoVo>> stringListMap = gaiaFranchiseeMapper.getClientsInfo(collect).stream().collect(Collectors.groupingBy(ClientBaseInfoVo::getClient));
        if (CollectionUtil.isNotEmpty(dbDataList)) {
            //处理数据
            List<ReportInfoSummaryVo> finalDataList = handleDbDataByReportType(dbDataList, dto,replenishDto);
            // 省市 时间
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
        // 重新过滤
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        //过滤 计入主动铺货和非主动铺货
        List<GaiaXhlH> dbList = new ArrayList<>();
        if (dto.getTag() == null || dto.getTag() == 0) {
            //不计入主动铺货
            dbList = xhlHMapper.getSummaryListLess(dto);
        } else {
            // 计入主动铺货
            dbList = xhlHMapper.getSummaryList(dto);
        }
        if (CollectionUtil.isNotEmpty(dbList)) {
            dbList.forEach(gaiaXhlH -> {
                //分子数量：开单数量、过账数量(排除铺货)
                gaiaXhlH.setUpQuantity(gaiaXhlH.getUpQuantity().setScale(2, BigDecimal.ROUND_HALF_UP));
                //分母数量：订单数量、开单数量(排除铺货)
                gaiaXhlH.setDownQuantity(gaiaXhlH.getDownQuantity().setScale(2, BigDecimal.ROUND_HALF_UP));
                //数量满足率(排除铺货)
                //gaiaXhlH.setQuantityRate(gaiaXhlH.getQuantityRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //开单金额、配送金额(排除铺货)
                gaiaXhlH.setUpAmount(gaiaXhlH.getUpAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
                //订单金额、开单金额(排除铺货)
                gaiaXhlH.setDownAmount(gaiaXhlH.getDownAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
                //金额满足率(排除铺货)
                //gaiaXhlH.setAmountRate(gaiaXhlH.getAmountRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                //开单品项、过账品项(排除铺货)
                gaiaXhlH.setUpProductNum(gaiaXhlH.getUpProductNum().setScale(2, BigDecimal.ROUND_HALF_UP));
                //订单品项、开单品项(排除铺货)
                gaiaXhlH.setDownProductNum(gaiaXhlH.getDownProductNum().setScale(2, BigDecimal.ROUND_HALF_UP));
                //品项满足率(排除铺货)
                // gaiaXhlH.setProductRate(gaiaXhlH.getProductRate().setScale(0, BigDecimal.ROUND_HALF_UP));
            });
            resList = dbList;
        }
        // 过滤的时候过滤分母为0
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
            //日报
            case "1":
                List<ReportInfoSummaryVo> dayList = handleDayData(dbDataList, dto,replenishDto);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //周报
            case "2":
                List<ReportInfoSummaryVo> weekList = handleWeekData(dbDataList, dto,replenishDto);
                if (CollectionUtil.isNotEmpty(weekList)) {
                   resList = weekList;
                }
                break;
            //月报
            case "3":
                List<ReportInfoSummaryVo> monthList = handleMonthData(dbDataList, dto,replenishDto);
                if (CollectionUtil.isNotEmpty(monthList)) {
                    resList = monthList;
                }
                break;
        }
        return resList;
    }

    // 处理月报表
    private List<ReportInfoSummaryVo> handleMonthData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto,ReplenishDto replenishDto) {
        //b.split("-")[1]
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        List<ReplenishVo> allReplenishes = gaiaSdReplenishHMapper.getMonthReplenishes(replenishDto);
        Map<String, Map<String, Map<String, Map<Date, List<GaiaXhlH>>>>> collect = dbDataList.stream().collect(Collectors.groupingBy(GaiaXhlH::getYearName, Collectors.groupingBy(gaiaXhlH -> DateUtil.format(gaiaXhlH.getTjDate(), "yyyy-MM").split("-")[1], Collectors.groupingBy(GaiaXhlH::getClient, Collectors.groupingBy(GaiaXhlH::getTjDate)))));
        for (String year : collect.keySet()) {
            Map<String, Map<String, Map<Date, List<GaiaXhlH>>>> stringMapMap = collect.get(year);
            // 月份
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
                        String dateSrt = stringBuilder.append(year).append("年").append(DateUtil.format(list.get(0).getTjDate(), "yyyy-MM").split("-")[1]).append("月").toString();
                        String key = stringBuilder.append(year).append("年").append(DateUtil.format(list.get(0).getTjDate(), "yyyy-MM").split("-")[1]).append("月").toString();
                        vo.setDate(dateSrt);
                        vo.setClient(client);
                        //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
                        if (tempMap.get(key) == null) {
                            if (list.size() ==1) {
                                // 会出现被过滤的情况 只有开单的
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
                                // 会出现被过滤的情况 只有开单的
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
                                // 三种类型全的
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
                                // 会出现被过滤的情况
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
                                // 会出现被过滤的情况 只有开单的
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
                            //处理最终结果
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
            // 会有多个值
            return list.get(0).getNum();
        }
        return 0;
    }

    /**
     * 处理周报表
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
                      // 统计周内供应商汇总
                    Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
                    ReportInfoSummaryVo vo =new ReportInfoSummaryVo();
                    StringBuilder stringBuilder=new StringBuilder();
                    String dateSrt= stringBuilder.append(yearName).append("年").append(weekNum).append("周").toString();
                    String key= stringBuilder.append(yearName).append("年").append(weekNum).append("周").toString();
                    vo.setDate(dateSrt);
                    vo.setClient(client);

                     for(Date date :listMap.keySet()){
                         List<GaiaXhlH> list = listMap.get(date);
                         if(tempMap.get(key)==null){
                             if(list.size()==1){
                                 //只有开单配送情况
                                 Integer num  = getWeekReplenishNum(allReplenishes,client,weekNum,yearName);
                                 // 三种类型全的
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
                                 //只有开单配送情况
                                 Integer num  = getWeekReplenishNum(allReplenishes,client,weekNum,yearName);
                                 // 三种类型全的
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
                                 // 三种类型全的
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
                                 // 只有开单配货情况
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
                                 // 只有开单配货情况
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
                            //处理最终结果
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
                    //                    // 金额
                    //                    vo.setOrderAmount(sortList.get(0).getDownAmount());
                    //                    vo.setGzAmount(sortList.get(1).getUpAmount());
                    //                    vo.setFinalAmountRate(sortList.get(2).getDownAmount());
                    //                    vo.setDistributionAmountRate(sortList.get(0).getAmountRate());
                    //                    vo.setSendAmountRate(sortList.get(1).getAmountRate());
                    //                    vo.setFinalAmountRate(sortList.get(2).getAmountRate());
                    //                    // 品项
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
           // 会有多个值
           return list.get(0).getNum();
        }
        return 0;
    }

    /**
     * 处理日度报表
     *
     * @param inData
     * @param replenishDto
     * @return
     */
    private List<ReportInfoSummaryVo> handleDayData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto, ReplenishDto replenishDto) {
        //db中存储的就是每天加盟商级别的统计信息，直接进行处理展示即可
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        List<ReplenishVo> allReplenishes = gaiaSdReplenishHMapper.getAllReplenishes(replenishDto);
        // 日报统计
        //分组
        Map<Date, Map<String, List<GaiaXhlH>>> dateMapMap = dbDataList.stream().collect(Collectors.groupingBy(GaiaXhlH::getTjDate, Collectors.groupingBy(GaiaXhlH::getClient)));

        for (Date date : dateMapMap.keySet()) {
            Map<String, List<GaiaXhlH>> stringListMap = dateMapMap.get(date);

            for (String client : stringListMap.keySet()) {
                //拿到需要组装的list
                List<GaiaXhlH> list = stringListMap.get(client);
                list.sort(Comparator.comparing(GaiaXhlH::getTjType));
                if (list.size() == 1) {
                    //只有发货配货 其余为空
                    Integer num= getDayReplenishNum(allReplenishes, date, client);
                    //处理 type 1 2 3
                    List<GaiaXhlH> sortList = list.stream().sorted(Comparator.comparing(GaiaXhlH::getTjType)).collect(Collectors.toList());
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();
                    String tjDateTranceStr = DateUtil.format(sortList.get(0).getTjDate(), "yyyy年MM月dd日");
                    vo.setDate(tjDateTranceStr);
                    //数量
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
                    // 金额
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
                    // 品项
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
                    // 1是app 2是网页端
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
                //特殊情况下 只有第一条和第三条
                if (list.size() == 2&&((list.get(1).getTjType()==3))) {
                    //只有发货配货 其余为空
                    Integer num= getDayReplenishNum(allReplenishes, date, client);
                    //处理 type 1 2 3
                    List<GaiaXhlH> sortList = list.stream().sorted(Comparator.comparing(GaiaXhlH::getTjType)).collect(Collectors.toList());
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();
                    String tjDateTranceStr = DateUtil.format(sortList.get(0).getTjDate(), "yyyy年MM月dd日");
                    vo.setDate(tjDateTranceStr);
                    //数量
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
                    // 金额
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
                    // 品项
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
                    // 1是app 2是网页端
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
                    //处理 type 1 2 3
                    List<GaiaXhlH> sortList = list.stream().sorted(Comparator.comparing(GaiaXhlH::getTjType)).collect(Collectors.toList());
                    ReportInfoSummaryVo vo = new ReportInfoSummaryVo();
                    String tjDateTranceStr = DateUtil.format(sortList.get(0).getTjDate(), "yyyy年MM月dd日");
                    vo.setDate(tjDateTranceStr);
                    //数量
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
                    // 金额
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
                    // 品项
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
                    // 1是app 2是网页端
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
            //日报
            case "1":
                List<ReportInfoSummaryVo> dayList = handleAverageDayData(dbDataList, dto);
                if (CollectionUtil.isNotEmpty(dayList)) {
                    resList = dayList;
                }
                break;
            //周报
            case "2":
                List<ReportInfoSummaryVo> weekList = handleAverageWeekData(dbDataList, dto);
                if (CollectionUtil.isNotEmpty(weekList)) {
                    resList = weekList;
                }
                break;
            //月报
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
     * 处理日度报表
     *
     * @param inData
     * @return
     */
    private List<ReportInfoSummaryVo> handleAverageDayData(List<GaiaXhlH> dbDataList, ReportSummaryDto dto) {
        //db中存储的就是每天加盟商级别的统计信息，直接进行处理展示即可
        List<ReportInfoSummaryVo> resList = new ArrayList<>();
        Map<String, ReportInfoSummaryVo> tempMap = new HashMap<>();
        //会有多组值 这里只最终下货率
        dbDataList= dbDataList.stream().filter(gaiaXhlH->gaiaXhlH.getTjType()==3).collect(Collectors.toList());
        for (GaiaXhlH h : dbDataList) {
            String key = DateUtil.format(h.getTjDate(), "yyyy年MM月dd日");
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
            //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
            String key = h.getYearName() + "年" + h.getWeekNum() + "周";
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
                //处理最终结果
                data.setAverageRate(new BigDecimal("0.00").compareTo(data.getAverageDownProductNum())  == 0?BigDecimal.ZERO:data.getAverageUpProductNum().divide(data.getAverageDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                resList.add(data);
            }
        }
        return resList;
    }

    /**
     * 处理月度报表
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
            //将数据根据年周进行分组，并汇总基础数据，用于下一步计算
            String key = h.getYearName() + "年" + tjDateTranceStr.split("-")[1] + "月";
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
                //处理最终结果
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
        infoExportVo.setDate("总计");
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
                infoExportVo.setDate("总计");
            }
            return infoExportVo;
    }


    public static void main(String[] args) {

    }

}

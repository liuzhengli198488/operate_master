package com.gys.service.Impl;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.GaiaStoreOutSuggestionH;
import com.gys.entity.data.suggestion.dto.QueryAdjustmentDto;
import com.gys.entity.data.suggestion.dto.UpdateExportDto;
import com.gys.entity.data.suggestion.vo.*;
import com.gys.mapper.GaiaStoreOutSuggestionHMapper;
import com.gys.service.GaiaStoreOutSuggestionHService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.gys.common.constant.CommonConstant.*;
import static com.gys.util.DateUtil.DEFAULT_FORMAT3;
import static com.gys.util.Util.isNumbers;

/**
 * @Auther: tzh
 * @Date: 2022/1/12 16:38
 * @Description: GaiaStoreOutSuggestionHImpl
 * @Version 1.0.0
 */
@Service
@Slf4j
public class GaiaStoreOutSuggestionHImpl implements GaiaStoreOutSuggestionHService {
    @Resource
    private GaiaStoreOutSuggestionHMapper gaiaStoreOutSuggestionHMapper;

    @Override
    public PageInfo<AdjustmentSummaryVo> getSuggestions(QueryAdjustmentDto dto) {
        //处理数据
        //格式化时间
        dto.setBeginDate(com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getBeginDate(), "yyyyMMdd"), "yyyy-MM-dd 00:00:00"));
        dto.setEndDate(com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(dto.getEndDate(), "yyyyMMdd"), "yyyy-MM-dd 23:59:59"));
        return handleDataByType(dto);
    }

    @Override
    public void updateOutExport(UpdateExportDto dto) {
        // 判断兼容
        Long id =null;
        boolean numbers = isNumbers(dto.getId());
        if(numbers){
            // 是主键
            id= Long.valueOf(dto.getId());
        }else {
        GaiaStoreOutSuggestionH suggestionH= gaiaStoreOutSuggestionHMapper.getOne(dto);
        if(suggestionH==null){
            throw new BusinessException("调出调剂不存在");
        }
            id= suggestionH.getId();
        }

        GaiaStoreOutSuggestionH gaiaStoreOutSuggestionH = gaiaStoreOutSuggestionHMapper.selectByPrimaryKey(id);
        if(gaiaStoreOutSuggestionH==null){
            throw new BusinessException("调出调剂不存在");
        }
        //确认前 首次导出
        if(gaiaStoreOutSuggestionH.getStatus()!=3&&gaiaStoreOutSuggestionH.getExportBeforeConfirm()==0){
            gaiaStoreOutSuggestionH.setExportBeforeConfirm(1);
            gaiaStoreOutSuggestionH.setUpdateTime(new Date());
            gaiaStoreOutSuggestionHMapper.updateByPrimaryKeySelective(gaiaStoreOutSuggestionH);
        }
         // 确认后 首次导出
        if(gaiaStoreOutSuggestionH.getStatus()==3&&gaiaStoreOutSuggestionH.getExportAfterConfirm()==0){
            gaiaStoreOutSuggestionH.setExportAfterConfirm(1);
            gaiaStoreOutSuggestionH.setUpdateTime(new Date());
            gaiaStoreOutSuggestionHMapper.updateByPrimaryKeySelective(gaiaStoreOutSuggestionH);
        }

    }

    private PageInfo<AdjustmentSummaryVo> handleDataByType(QueryAdjustmentDto dto) {
        // 日报
        if (String.valueOf(1).equals(dto.getType())) {
            PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
            //原始数据条数
            List<AdjustmentBaseInfo> suggestion = gaiaStoreOutSuggestionHMapper.getOriginalSuggestions(dto);
            // 没有数据的时候
            if(CollectionUtils.isEmpty(suggestion)){
                return new PageInfo<>();
            }
            List<String> clientIds = suggestion.stream().map(AdjustmentBaseInfo::getClientId).distinct().collect(Collectors.toList());
            List<String> codes = suggestion.stream().map(AdjustmentBaseInfo::getBillCode).distinct().collect(Collectors.toList());
            //合并后数据
            List<AdjustmentBaseInfo> suggestions = gaiaStoreOutSuggestionHMapper.getSuggestionsAll(clientIds,codes);
            //为空直接返回
            if (CollectionUtils.isEmpty(suggestions)) {
                return new PageInfo<>();
            }
            return dealDay(suggestions);
        }
        // 周报
        if (String.valueOf(2).equals(dto.getType())) {
           // List<AdjustmentBaseInfo> suggestions = gaiaStoreOutSuggestionHMapper.getWeekSuggestions(dto);
            List<AdjustmentBaseInfo> suggestion = gaiaStoreOutSuggestionHMapper.getOriginalSuggestions(dto);
            if(CollectionUtils.isEmpty(suggestion)){
                return new PageInfo<>();
            }
            List<String> clientIds = suggestion.stream().map(AdjustmentBaseInfo::getClientId).distinct().collect(Collectors.toList());
            List<String> codes = suggestion.stream().map(AdjustmentBaseInfo::getBillCode).distinct().collect(Collectors.toList());
            //合并后数据
            List<AdjustmentBaseInfo> suggestions = gaiaStoreOutSuggestionHMapper.getSuggestionsAll(clientIds,codes);
            //为空直接返回
            if (CollectionUtils.isEmpty(suggestions)) {
                return new PageInfo<>();
            }
            //取出时间值
            List<String> collect = suggestions.stream().map(AdjustmentBaseInfo::getDate).distinct().collect(Collectors.toList());
            List<CalendarVo>  voList = gaiaStoreOutSuggestionHMapper.getDateWeek(collect);
            Map<String, List<CalendarVo>> listMap = voList.stream().collect(Collectors.groupingBy(CalendarVo::getGcdDate));
            //转化
            for (AdjustmentBaseInfo baseInfo:suggestions){
                String gcdYear = listMap.get(baseInfo.getDate()).get(0).getGcdYear();
                String gcdWeek = listMap.get(baseInfo.getDate()).get(0).getGcdWeek();
                baseInfo.setDate(gcdYear+YEAR+gcdWeek+WEEK);
            }
            return weekDay(suggestions,listMap);
        }
        //月报
        if (String.valueOf(3).equals(dto.getType())) {
           // List<AdjustmentBaseInfo> suggestions = gaiaStoreOutSuggestionHMapper.getMonthSuggestions(dto);
            List<AdjustmentBaseInfo> suggestion = gaiaStoreOutSuggestionHMapper.getOriginalSuggestions(dto);
            if(CollectionUtils.isEmpty(suggestion)){
                return new PageInfo<>();
            }
            List<String> clientIds = suggestion.stream().map(AdjustmentBaseInfo::getClientId).distinct().collect(Collectors.toList());
            List<String> codes = suggestion.stream().map(AdjustmentBaseInfo::getBillCode).distinct().collect(Collectors.toList());
            //合并后数据
            List<AdjustmentBaseInfo> suggestions = gaiaStoreOutSuggestionHMapper.getSuggestionsAll(clientIds,codes);
            //为空直接返回
            if (CollectionUtils.isEmpty(suggestions)) {
                return new PageInfo<>();
            }
            List<String> collect = suggestions.stream().map(AdjustmentBaseInfo::getDate).distinct().collect(Collectors.toList());
            List<CalendarVo>  voList = gaiaStoreOutSuggestionHMapper.getDateWeek(collect);
            Map<String, List<CalendarVo>> listMap = voList.stream().collect(Collectors.groupingBy(CalendarVo::getGcdDate));
            //转化
            for (AdjustmentBaseInfo baseInfo:suggestions){
                String gcdYear = listMap.get(baseInfo.getDate()).get(0).getGcdYear();
                String gcdMonth = listMap.get(baseInfo.getDate()).get(0).getGcdMonth();
                baseInfo.setDate(gcdYear+YEAR+gcdMonth+MONTH);
            }
            return monthDay(suggestions,listMap);
        }
      return null;
    }

    private PageInfo<AdjustmentSummaryVo> monthDay(List<AdjustmentBaseInfo> suggestions, Map<String, List<CalendarVo>> listMap) {
        // 实际品项
        List<String> clientids =suggestions.stream().map(AdjustmentBaseInfo::getClientId).distinct().collect(Collectors.toList());
        List<String> inBillCodes =suggestions.stream().map(AdjustmentBaseInfo::getInBillCode).distinct().collect(Collectors.toList());
        List<ActualItemVo> voList=gaiaStoreOutSuggestionHMapper.queryMonthActualItem(clientids,inBillCodes);
        for (ActualItemVo vo:voList){
            String gcdYear = listMap.get(vo.getDate()).get(0).getGcdYear();
            String gcdMonth = listMap.get(vo.getDate()).get(0).getGcdMonth();
            vo.setDate(gcdYear+YEAR+gcdMonth+MONTH);
        }
        Map<String, List<ActualItemVo>> map = voList.stream().collect(Collectors.groupingBy(s -> fetchKey(s)));

        List<AdjustmentSummaryVo> summaryVoList = new ArrayList<>();
        //分组时间,供应商,单号
        Map<String, Map<String, Map<String, List<AdjustmentBaseInfo>>>> collect = suggestions.stream().collect(Collectors.groupingBy(AdjustmentBaseInfo::getDate, Collectors.groupingBy(AdjustmentBaseInfo::getClientId, Collectors.groupingBy(AdjustmentBaseInfo::getBillCode))));
        for (String date : collect.keySet()) {
            //时间
            Map<String, Map<String, List<AdjustmentBaseInfo>>> stringMapMap = collect.get(date);
            for (String client : stringMapMap.keySet()) {
                //供应商
                AdjustmentSummaryVo vo = new AdjustmentSummaryVo();
                Map<String, List<AdjustmentBaseInfo>> stringListMap = stringMapMap.get(client);
                //调出门店数量
                List<String> stringCallOutCodes =new ArrayList<>();
                //调出完成门店数量
                List<String> stringCallOutCompletCodes =new ArrayList<>();
                for (String billCode : stringListMap.keySet()) {
                    //单号
                    List<AdjustmentBaseInfo> baseInfoList = stringListMap.get(billCode);
                    baseInfoList=baseInfoList.stream().distinct().collect(Collectors.toList());
                    //当日如果有门店多次调出 都统计
                    //调出门店
                    List<String> callOutCodes= baseInfoList.stream().map(AdjustmentBaseInfo::getOutStoCode).distinct().collect(Collectors.toList());
                    stringCallOutCodes.addAll(callOutCodes);
                    //赋值
                    BeanCopyUtils.copyPropertiesIgnoreNull(baseInfoList.get(0), vo);
                    //vo.setCallOutStoresNumber(Integer.parseInt(String.valueOf(count)));

                    //关联门店
                    long inCount = baseInfoList.stream().map(AdjustmentBaseInfo::getInStoCode).count();
                    //0-待处理 1-已确认 2-已失效 3-已完成  1 3 认为有反应
                   // long inBillStatusCount = baseInfoList.stream().filter(adjustmentBaseInfo -> adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3).count();
                    Long inBillStatusCount=0L;
                    if(CollectionUtils.isNotEmpty(baseInfoList)){
                        for (AdjustmentBaseInfo adjustmentBaseInfo:baseInfoList){
                            if(adjustmentBaseInfo.getInBillStatus()!=null){
                                if(adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3){
                                    inBillStatusCount++;
                                }
                            }                         }
                        // inBillStatusCount = baseInfoList.stream().filter(adjustmentBaseInfo -> adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3).collect(Collectors.toList()).stream().count();
                    }
                    //门店调剂品项数
                    //调出门店已经完成门店数
                    List<String> callOutCompletStore = baseInfoList.stream().filter(s -> s.getOutBillStatus().equals(3)).collect(Collectors.toList()).stream().map(AdjustmentBaseInfo::getOutStoCode).distinct().collect(Collectors.toList());
                    stringCallOutCompletCodes.addAll(callOutCompletStore);
                    List<ActualItemVo> actualItemVoList = map.get(date + client + billCode);
                    dealStoreNumber(vo, inCount, inBillStatusCount, baseInfoList,actualItemVoList);
                    //门店调剂实际品项数 单独查询

                }
                long countCallOutStoreNumber = stringCallOutCodes.stream().count();
                long callOutCompletStoreNumber = stringCallOutCompletCodes.stream().count();
                vo.setCallOutStoresNumber(Integer.parseInt(String.valueOf(countCallOutStoreNumber)));
                vo.setCallOutCompleteStore(Integer.parseInt(String.valueOf(callOutCompletStoreNumber)));
                int actual = vo.getStoreAdjustActualItem() == null ? 0 : vo.getStoreAdjustActualItem();
                vo.setAdjustmentSatisfactionRate(new BigDecimal(actual).divide(new BigDecimal(vo.getStoreItem()),4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
               /* String gcdYear = listMap.get(vo.getDate()).get(0).getGcdYear();
                String gcdMonth = listMap.get(vo.getDate()).get(0).getGcdMonth();
                vo.setDate(gcdYear+YEAR+gcdMonth+MONTH);*/
                summaryVoList.add(vo);
            }
        }
        //处理合计问题
        AdjustmentSummaryVoTotal  total =new AdjustmentSummaryVoTotal();
        //调出门店数
        int sum = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getCallOutStoresNumber).sum();
        total.setCallOutStoresNumber(sum);
        //关联一家门店数量
        int sumOneStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateOneStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateOneStore).sum();
        total.setAssociateOneStore(sumOneStore);
        //关联二家门店数量
        int sumTwoStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateTwoStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateTwoStore).sum();
        total.setAssociateTwoStore(sumTwoStore);
        //关联三家门店数量
        int sumThreeStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateThreeStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateThreeStore).sum();
        total.setAssociateThreeStore(sumThreeStore);
        //调入门店反馈门店数
        int feedStores = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAdjustFeedbackStores).sum();
        total.setAdjustFeedbackStores(feedStores);
        //调出门店调剂确认前导出次数
        int beforeCallOut = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getBeforeCallOutConfirmationStoreExport).sum();
        total.setBeforeCallOutConfirmationStoreExport(beforeCallOut);

        //调出门店调剂确认后导出次数
        int afterCallOut = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAfterCallOutConfirmationStoreExport).sum();
        total.setAfterCallOutConfirmationStoreExport(afterCallOut);

        //调入门店调剂确认前导出次数
        int beforeCallIn = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getBeforeCallInConfirmationStoreExport).sum();
        total.setBeforeCallOutConfirmationStoreExport(beforeCallIn);

        //调入门店调剂确认后导出次数
        int afterCallIn = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAfterCallInConfirmationStoreExport).sum();
        total.setAfterCallOutConfirmationStoreExport(afterCallIn);

        //门店调剂品项数
        int  storeItem= summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getStoreItem).sum();
        total.setStoreItem(storeItem);

        //门店调剂实际品项数
        int  adjustActualItem= summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getStoreAdjustActualItem()!=null).mapToInt(AdjustmentSummaryVo::getStoreAdjustActualItem).sum();
        total.setStoreAdjustActualItem(adjustActualItem);
        //调剂满足率
        total.setAdjustmentSatisfactionRate(new BigDecimal(adjustActualItem).divide(new BigDecimal(storeItem),4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString()+"%");
        //调出门店完成门店数
        int completeStore = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getCallOutCompleteStore).sum();
        total.setCallOutCompleteStore(completeStore);
        summaryVoList.sort(Comparator.comparing(AdjustmentSummaryVo::getDate).reversed());
        PageInfo pageInfo=new PageInfo<>();
        pageInfo.setList(summaryVoList);
        pageInfo.setListNum(total);
        return pageInfo;
    }

    // 周报
    private PageInfo<AdjustmentSummaryVo> weekDay(List<AdjustmentBaseInfo> suggestions, Map<String, List<CalendarVo>> listMap) {
        // 实际品项
        List<String> clientids =suggestions.stream().map(AdjustmentBaseInfo::getClientId).distinct().collect(Collectors.toList());
        List<String> inBillCodes =suggestions.stream().map(AdjustmentBaseInfo::getInBillCode).distinct().collect(Collectors.toList());
        List<ActualItemVo> voList=gaiaStoreOutSuggestionHMapper.queryWeekActualItem(clientids,inBillCodes);
        for(ActualItemVo vo :voList){
                String gcdYear = listMap.get(vo.getDate()).get(0).getGcdYear();
                String gcdWeek = listMap.get(vo.getDate()).get(0).getGcdWeek();
                vo.setDate(gcdYear+YEAR+gcdWeek+WEEK);
        }
        Map<String, List<ActualItemVo>> map = voList.stream().collect(Collectors.groupingBy(s -> fetchKey(s)));

        List<AdjustmentSummaryVo> summaryVoList = new ArrayList<>();
        //分组时间,供应商,单号
        Map<String, Map<String, Map<String, List<AdjustmentBaseInfo>>>> collect = suggestions.stream().collect(Collectors.groupingBy(AdjustmentBaseInfo::getDate, Collectors.groupingBy(AdjustmentBaseInfo::getClientId, Collectors.groupingBy(AdjustmentBaseInfo::getBillCode))));
        for (String date : collect.keySet()) {
            //时间
            Map<String, Map<String, List<AdjustmentBaseInfo>>> stringMapMap = collect.get(date);
            for (String client : stringMapMap.keySet()) {
                //供应商
                AdjustmentSummaryVo vo = new AdjustmentSummaryVo();
                Map<String, List<AdjustmentBaseInfo>> stringListMap = stringMapMap.get(client);
                //调出门店数量
                List<String> stringCallOutCodes =new ArrayList<>();
                //调出完成门店数量
                List<String> stringCallOutCompletCodes =new ArrayList<>();
                for (String billCode : stringListMap.keySet()) {
                    //单号
                    List<AdjustmentBaseInfo> baseInfoList = stringListMap.get(billCode);
                    baseInfoList=baseInfoList.stream().distinct().collect(Collectors.toList());
                    //当日如果有门店多次调出 都统计
                    //调出门店
                    List<String> callOutCodes= baseInfoList.stream().map(AdjustmentBaseInfo::getOutStoCode).distinct().collect(Collectors.toList());
                    stringCallOutCodes.addAll(callOutCodes);
                    //赋值
                    BeanCopyUtils.copyPropertiesIgnoreNull(baseInfoList.get(0), vo);
                    //vo.setCallOutStoresNumber(Integer.parseInt(String.valueOf(count)));

                    //关联门店
                    long inCount = baseInfoList.stream().map(AdjustmentBaseInfo::getInStoCode).count();
                    //0-待处理 1-已确认 2-已失效 3-已完成  1 3 认为有反应
                  //  long inBillStatusCount = baseInfoList.stream().filter(adjustmentBaseInfo -> adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3).count();
                    Long inBillStatusCount=0L;
                    if(CollectionUtils.isNotEmpty(baseInfoList)){
                        for (AdjustmentBaseInfo adjustmentBaseInfo:baseInfoList){
                            if(adjustmentBaseInfo.getInBillStatus()!=null){
                                if(adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3){
                                    inBillStatusCount++;
                                }
                            }                         }
                        // inBillStatusCount = baseInfoList.stream().filter(adjustmentBaseInfo -> adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3).collect(Collectors.toList()).stream().count();
                    }
                    //门店调剂品项数
                    //调出门店已经完成门店数
                    List<String> callOutCompletStore = baseInfoList.stream().filter(s -> s.getOutBillStatus().equals(3)).collect(Collectors.toList()).stream().map(AdjustmentBaseInfo::getOutStoCode).distinct().collect(Collectors.toList());
                    stringCallOutCompletCodes.addAll(callOutCompletStore);
                    List<ActualItemVo> actualItemVoList = map.get(date + client + billCode);
                    dealStoreNumber(vo, inCount, inBillStatusCount, baseInfoList,actualItemVoList);
                    //门店调剂实际品项数 单独查询

                }
                long countCallOutStoreNumber = stringCallOutCodes.stream().count();
                long callOutCompletStoreNumber = stringCallOutCompletCodes.stream().count();
                vo.setCallOutStoresNumber(Integer.parseInt(String.valueOf(countCallOutStoreNumber)));
                vo.setCallOutCompleteStore(Integer.parseInt(String.valueOf(callOutCompletStoreNumber)));
                int actual = vo.getStoreAdjustActualItem() == null ? 0 : vo.getStoreAdjustActualItem();
                vo.setAdjustmentSatisfactionRate(new BigDecimal(actual).divide(new BigDecimal(vo.getStoreItem()),4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
               /* String gcdYear = listMap.get(vo.getDate()).get(0).getGcdYear();
                String gcdWeek = listMap.get(vo.getDate()).get(0).getGcdWeek();
                vo.setDate(gcdYear+YEAR+gcdWeek+WEEK);*/
                summaryVoList.add(vo);
            }
        }
        //处理合计问题
        AdjustmentSummaryVoTotal  total =new AdjustmentSummaryVoTotal();
        //调出门店数
        int sum = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getCallOutStoresNumber).sum();
        total.setCallOutStoresNumber(sum);
        //关联一家门店数量
        int sumOneStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateOneStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateOneStore).sum();
        total.setAssociateOneStore(sumOneStore);
        //关联二家门店数量
        int sumTwoStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateTwoStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateTwoStore).sum();
        total.setAssociateTwoStore(sumTwoStore);
        //关联三家门店数量
        int sumThreeStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateThreeStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateThreeStore).sum();
        total.setAssociateThreeStore(sumThreeStore);
        //调入门店反馈门店数
        int feedStores = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAdjustFeedbackStores).sum();
        total.setAdjustFeedbackStores(feedStores);
        //调出门店调剂确认前导出次数
        int beforeCallOut = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getBeforeCallOutConfirmationStoreExport).sum();
        total.setBeforeCallOutConfirmationStoreExport(beforeCallOut);

        //调出门店调剂确认后导出次数
        int afterCallOut = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAfterCallOutConfirmationStoreExport).sum();
        total.setAfterCallOutConfirmationStoreExport(afterCallOut);

        //调入门店调剂确认前导出次数
        int beforeCallIn = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getBeforeCallInConfirmationStoreExport).sum();
        total.setBeforeCallOutConfirmationStoreExport(beforeCallIn);

        //调入门店调剂确认后导出次数
        int afterCallIn = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAfterCallInConfirmationStoreExport).sum();
        total.setAfterCallOutConfirmationStoreExport(afterCallIn);

        //门店调剂品项数
        int  storeItem= summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getStoreItem).sum();
        total.setStoreItem(storeItem);

        //门店调剂实际品项数
        int  adjustActualItem= summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getStoreAdjustActualItem()!=null).mapToInt(AdjustmentSummaryVo::getStoreAdjustActualItem).sum();
        total.setStoreAdjustActualItem(adjustActualItem);
        //调剂满足率
        total.setAdjustmentSatisfactionRate(new BigDecimal(adjustActualItem).divide(new BigDecimal(storeItem),4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString()+"%");
        //调出门店完成门店数
        int completeStore = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getCallOutCompleteStore).sum();
        total.setCallOutCompleteStore(completeStore);
        summaryVoList.sort(Comparator.comparing(AdjustmentSummaryVo::getDate).reversed());
        PageInfo pageInfo=new PageInfo<>();
        pageInfo.setList(summaryVoList);
        pageInfo.setListNum(total);
        return pageInfo;
    }

    // 日报
    private PageInfo<AdjustmentSummaryVo> dealDay(List<AdjustmentBaseInfo> suggestions) {
        // 查询数量
        // 实际品项
        List<String> clientids =suggestions.stream().map(AdjustmentBaseInfo::getClientId).distinct().collect(Collectors.toList());
        List<String> inBillCodes =suggestions.stream().map(AdjustmentBaseInfo::getInBillCode).distinct().collect(Collectors.toList());
        List<ActualItemVo> voList=gaiaStoreOutSuggestionHMapper.queryActualItem(clientids,inBillCodes);
        Map<String, List<ActualItemVo>> map = voList.stream().collect(Collectors.groupingBy(s -> fetchKey(s)));

        List<AdjustmentSummaryVo> summaryVoList = new ArrayList<>();
        //分组时间,供应商,单号
        Map<String, Map<String, Map<String, List<AdjustmentBaseInfo>>>> collect = suggestions.stream().collect(Collectors.groupingBy(AdjustmentBaseInfo::getDate, Collectors.groupingBy(AdjustmentBaseInfo::getClientId, Collectors.groupingBy(AdjustmentBaseInfo::getBillCode))));
        for (String date : collect.keySet()) {
            //时间
            Map<String, Map<String, List<AdjustmentBaseInfo>>> stringMapMap = collect.get(date);
            for (String client : stringMapMap.keySet()) {
                //供应商
                AdjustmentSummaryVo vo = new AdjustmentSummaryVo();
                Map<String, List<AdjustmentBaseInfo>> stringListMap = stringMapMap.get(client);
                //调出门店数量
                List<String> stringCallOutCodes =new ArrayList<>();
                //调出完成门店数量
                List<String> stringCallOutCompletCodes =new ArrayList<>();
                for (String billCode : stringListMap.keySet()) {
                    //单号
                    List<AdjustmentBaseInfo> baseInfoList = stringListMap.get(billCode);
                    baseInfoList=baseInfoList.stream().distinct().collect(Collectors.toList());
                    //当日如果有门店多次调出 都统计
                    //调出门店
                    List<String> callOutCodes= baseInfoList.stream().map(AdjustmentBaseInfo::getOutStoCode).distinct().collect(Collectors.toList());
                    stringCallOutCodes.addAll(callOutCodes);
                    //赋值
                    BeanCopyUtils.copyPropertiesIgnoreNull(baseInfoList.get(0), vo);
                    //vo.setCallOutStoresNumber(Integer.parseInt(String.valueOf(count)));

                    //关联门店
                    long inCount = baseInfoList.stream().map(AdjustmentBaseInfo::getInStoCode).count();
                    //0-待处理 1-已确认 2-已失效 3-已完成  1 3 认为有反应
                    Long inBillStatusCount=0L;
                    if(CollectionUtils.isNotEmpty(baseInfoList)){
                         for (AdjustmentBaseInfo adjustmentBaseInfo:baseInfoList){
                               if(adjustmentBaseInfo.getInBillStatus()!=null){
                                   if(adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3){
                                       inBillStatusCount++;
                                   }
                               }                         }
                        // inBillStatusCount = baseInfoList.stream().filter(adjustmentBaseInfo -> adjustmentBaseInfo.getInBillStatus() == 1 || adjustmentBaseInfo.getInBillStatus() == 3).collect(Collectors.toList()).stream().count();
                    }
                    //门店调剂品项数
                    //调出门店已经完成门店数
                    List<String> callOutCompletStore = baseInfoList.stream().filter(s -> s.getOutBillStatus().equals(3)).collect(Collectors.toList()).stream().map(AdjustmentBaseInfo::getOutStoCode).distinct().collect(Collectors.toList());
                    stringCallOutCompletCodes.addAll(callOutCompletStore);
                    List<ActualItemVo> actualItemVoList = map.get(date + client + billCode);
                    dealStoreNumber(vo, inCount, inBillStatusCount, baseInfoList,actualItemVoList);
                    //门店调剂实际品项数 单独查询

                }
                long countCallOutStoreNumber = stringCallOutCodes.stream().count();
                long callOutCompletStoreNumber = stringCallOutCompletCodes.stream().count();
                vo.setCallOutStoresNumber(Integer.parseInt(String.valueOf(countCallOutStoreNumber)));
                vo.setCallOutCompleteStore(Integer.parseInt(String.valueOf(callOutCompletStoreNumber)));
                vo.setDate(DateUtil.formatDate(DateUtil.getStringToDate2(date),DEFAULT_FORMAT3));
                int actual = vo.getStoreAdjustActualItem() == null ? 0 : vo.getStoreAdjustActualItem();
                vo.setAdjustmentSatisfactionRate(new BigDecimal(actual).divide(new BigDecimal(vo.getStoreItem()),4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
                summaryVoList.add(vo);
            }
        }
        //处理合计问题
        AdjustmentSummaryVoTotal total =new AdjustmentSummaryVoTotal();
        //summaryVoList= summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateOneStore()!=null||adjustmentSummaryVo.getAssociateTwoStore()!=null||adjustmentSummaryVo.getAssociateThreeStore()!=null).collect(Collectors.toList());
        //调出门店数
        int sum = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getCallOutStoresNumber).sum();
        total.setCallOutStoresNumber(sum);
        //关联一家门店数量
        int sumOneStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateOneStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateOneStore).sum();
        total.setAssociateOneStore(sumOneStore);
        //关联二家门店数量
        int sumTwoStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateTwoStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateTwoStore).sum();
        total.setAssociateTwoStore(sumTwoStore);
        //关联三家门店数量
        int sumThreeStore = summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getAssociateThreeStore()!=null).mapToInt(AdjustmentSummaryVo::getAssociateThreeStore).sum();
        total.setAssociateThreeStore(sumThreeStore);
        //调入门店反馈门店数
        int feedStores = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAdjustFeedbackStores).sum();
        total.setAdjustFeedbackStores(feedStores);
        //调出门店调剂确认前导出次数
        int beforeCallOut = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getBeforeCallOutConfirmationStoreExport).sum();
        total.setBeforeCallOutConfirmationStoreExport(beforeCallOut);

        //调出门店调剂确认后导出次数
        int afterCallOut = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAfterCallOutConfirmationStoreExport).sum();
        total.setAfterCallOutConfirmationStoreExport(afterCallOut);

        //调入门店调剂确认前导出次数
        int beforeCallIn = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getBeforeCallInConfirmationStoreExport).sum();
        total.setBeforeCallOutConfirmationStoreExport(beforeCallIn);

        //调入门店调剂确认后导出次数
        int afterCallIn = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getAfterCallInConfirmationStoreExport).sum();
        total.setAfterCallOutConfirmationStoreExport(afterCallIn);

        //门店调剂品项数
        int  storeItem= summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getStoreItem).sum();
        total.setStoreItem(storeItem);

        //门店调剂实际品项数
        int  adjustActualItem= summaryVoList.stream().filter(adjustmentSummaryVo -> adjustmentSummaryVo.getStoreAdjustActualItem()!=null).mapToInt(AdjustmentSummaryVo::getStoreAdjustActualItem).sum();
        total.setStoreAdjustActualItem(adjustActualItem);
        //调剂满足率
        total.setAdjustmentSatisfactionRate(new BigDecimal(adjustActualItem).divide(new BigDecimal(storeItem),4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString()+"%");
        //调出门店完成门店数
        int completeStore = summaryVoList.stream().mapToInt(AdjustmentSummaryVo::getCallOutCompleteStore).sum();
        total.setCallOutCompleteStore(completeStore);
        PageInfo pageInfo=new PageInfo<>();
        summaryVoList.sort(Comparator.comparing(AdjustmentSummaryVo::getDate).reversed());
        pageInfo.setList(summaryVoList);
        pageInfo.setListNum(total);
       // PageInfo<AdjustmentSummaryVo> pageInfo = new PageInfo<>(summaryVoList);
        return pageInfo;
    }

    private String fetchKey(ActualItemVo s) {
        return  s.getDate()+s.getClient()+s.getOutBillCode();
    }

    private AdjustmentSummaryVo dealStoreNumber(AdjustmentSummaryVo vo, long count, long inCount, List<AdjustmentBaseInfo> info, List<ActualItemVo> actualItemVoList) {
        int parseInt = Integer.parseInt(String.valueOf(count));
        // 关联门店数
        if (parseInt == 1) {
            int i = vo.getAssociateOneStore() == null ? 0 : vo.getAssociateOneStore();
            vo.setAssociateOneStore(i+1);
        }
        if (parseInt == 2) {
            int i = vo.getAssociateTwoStore() == null ? 0 : vo.getAssociateTwoStore();
            vo.setAssociateTwoStore(i+ 1);
        }
        if (parseInt == 3) {
            int i = vo.getAssociateThreeStore() == null ? 0 : vo.getAssociateThreeStore();
            vo.setAssociateThreeStore(i + 1);
        }
        //调入门店反馈门店数
        int inStoreStatus = Integer.parseInt(String.valueOf(inCount));
        int adFeedStore = vo.getAdjustFeedbackStores() == null ? 0 : vo.getAdjustFeedbackStores();
        vo.setAdjustFeedbackStores(adFeedStore + inStoreStatus);

        //调出品项数
        Integer outItemsNumber = info.get(0).getOutItemsNumber();
        int callOutStores = vo.getStoreItem() == null ? 0 : vo.getStoreItem();
        vo.setStoreItem(callOutStores + outItemsNumber);
        //实际调出品项数
        if(CollectionUtils.isNotEmpty(actualItemVoList)){
            int item = vo.getStoreAdjustActualItem() == null ? 0 : vo.getStoreAdjustActualItem();
            vo.setStoreAdjustActualItem(item+actualItemVoList.get(0).getNumber());
        }
        //调出确认前
        Integer outExportBeforeConfirm = info.stream().mapToInt(AdjustmentBaseInfo::getOutExportBeforeConfirm).sum();
        int beforeOutConfirmat = vo.getBeforeCallOutConfirmationStoreExport() == null ? 0 : vo.getBeforeCallOutConfirmationStoreExport();
        vo.setBeforeCallOutConfirmationStoreExport(beforeOutConfirmat+outExportBeforeConfirm);
        //调出确认后
        Integer outExportAfterConfirm = info.stream().mapToInt(AdjustmentBaseInfo::getOutExportAfterConfirm).sum();
        int afterOutConfirmat = vo.getAfterCallOutConfirmationStoreExport() == null ? 0 : vo.getAfterCallOutConfirmationStoreExport();
        vo.setAfterCallOutConfirmationStoreExport(outExportAfterConfirm+afterOutConfirmat);
        //调入确认前
        Integer inExportBeforeConfirm = info.stream().mapToInt(AdjustmentBaseInfo::getInExportBeforeConfirm).sum();
        int beforeCallIn = vo.getBeforeCallInConfirmationStoreExport() == null ? 0 : vo.getBeforeCallInConfirmationStoreExport();
        vo.setBeforeCallInConfirmationStoreExport(inExportBeforeConfirm+beforeCallIn);
        //调入确认后
        Integer inExportAfterConfirm = info.stream().mapToInt(AdjustmentBaseInfo::getInExportAfterConfirm).sum();
        int afterCallIn = vo.getAfterCallInConfirmationStoreExport() == null ? 0 : vo.getAfterCallInConfirmationStoreExport();
        vo.setAfterCallInConfirmationStoreExport(inExportAfterConfirm+afterCallIn);
        return vo;
    }

  
}

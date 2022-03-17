package com.gys.service.Impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.exception.BusinessException;
import com.gys.common.redis.RedisManager;
import com.gys.entity.priceProposal.condition.*;
import com.gys.entity.priceProposal.dto.*;
import com.gys.entity.priceProposal.entity.ProductPriceProposalD;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;
import com.gys.entity.priceProposal.vo.AllStosVO;
import com.gys.entity.priceProposal.vo.PriceProposalDetailListVO;
import com.gys.entity.priceProposal.vo.PriceProposalListVO;
import com.gys.mapper.ProductPriceProposalMapper;
import com.gys.service.ProductPriceProposalService;
import com.gys.util.UtilMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议业务实现类
 * @CreateTime 2022-01-11 14:47:00
 */
@Service
@Slf4j
public class ProductPriceProposalServiceImpl implements ProductPriceProposalService {

    @Autowired
    private ProductPriceProposalMapper priceProposalMapper;

    @Autowired
    private RedisManager redisManager;

    @Override
    public void makeData() {
//        log.info("=======================[价格建议]定时任务开始=======================");
//        DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String today = LocalDate.now().format(fmDate);
//        // 市级维度
//        List<CityDimensionDTO> cityDimensionDTOList = priceProposalMapper.selectCityDimensionList(null, null);
//        log.info("=======================[价格建议]定时任务查询出市级维度=======================");
//        // 差价A B组
//        List<PriceProposalADTO> aList = Lists.newArrayList();
//        List<PriceProposalBDTO> bList = Lists.newArrayList();
//        String maxNo = priceProposalMapper.getMaxNo();
//        if (StrUtil.isNotBlank(maxNo)) {
//            maxNo = "JG" + today + (Integer.parseInt(maxNo.substring(9)) + 1);
//        } else {
//            maxNo = "JG" + today + "001";
//        }
//        String finalMaxNo = maxNo;
//        log.info("=======================[价格建议]定时任务生成价格建议单号=======================");
//        cityDimensionDTOList.forEach(cityDimensionDTO -> {
//            // 查询商品是否有价格分类，如果没有则默认
//            List<Map<String, String>> rangeMapList = priceProposalMapper.selectPriceRangeByClientId(cityDimensionDTO.getCityId()) .size() == 0 ? getDefaultRangeMapList() : priceProposalMapper.selectPriceRangeByClientId(cityDimensionDTO.getCityId());
//            // 所在价格区间的商品信息
//            Map<String, List<StoDimensionDTO>> priceRangeStosMap = Maps.newHashMap();
//            // 根据区域平均售价判断商品分类ABCD
//            Map<String, String> priceClassifyMap = getPriceClassifyMap(rangeMapList, cityDimensionDTO.getAvgSellingPrice());
//            // 根据省市查询门店维度
//            List<StoDimensionDTO> stoDimensionDTOList = priceProposalMapper.selectStoDimensionList(cityDimensionDTO.getProvinceId(), cityDimensionDTO.getCityId());
//            log.info("=======================[价格建议]定时任务查询门店维度=======================");
//            if (MapUtil.isNotEmpty(priceClassifyMap)) {
//                if (StrUtil.isNotBlank(priceClassifyMap.get("start")) && StrUtil.isNotBlank(priceClassifyMap.get("end")) && StrUtil.isNotBlank(priceClassifyMap.get("gap"))) {
//                    int start = Integer.parseInt(priceClassifyMap.get("start")), end = Integer.parseInt(priceClassifyMap.get("end")), gap = Integer.parseInt(priceClassifyMap.get("gap"));
//                    // 根据起始价格、结束价格、价格间隔计算价格区间
//                    String[] priceRangeArr = PriceClassifyUtil.getStrArray(start, end, gap);
//                    // TODO 保存价格区间
//                    for (StoDimensionDTO stoDimensionDTO : stoDimensionDTOList) {
//                        for (int i = 0; i < priceRangeArr.length; i++) {
//                            // 判断商品平均售价是否在价格区间
//                            String locationRange = PriceClassifyUtil.getNumLocation(priceRangeArr[i], stoDimensionDTO.getAvgSellingPrice());
//                            if (StrUtil.isNotBlank(locationRange)) {
//                                log.info("=======================[价格建议]定时任务匹配出价格区间=======================");
//                                // 记录各个区间的贝叶斯概率
//                                saveRangeClassifyInfo(stoDimensionDTO, finalMaxNo, locationRange, String.valueOf(gap));
//                                // 在区间，如果此区间已经有值
//                                if (priceRangeStosMap.get(locationRange) != null) {
//                                    List<StoDimensionDTO> tempList = priceRangeStosMap.get(locationRange);
//                                    tempList.add(stoDimensionDTO);
//                                    priceRangeStosMap.put(locationRange, tempList);
//                                } else {
//                                    List tempList = Lists.newArrayList();
//                                    tempList.add(stoDimensionDTO);
//                                    priceRangeStosMap.put(locationRange, tempList);
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//                // 汇总各区间内商品的贝叶斯概率
//                Map<String, Double> rangeBayesianMap = Maps.newHashMap();
//                double[] rangeBayesianArr = new double[priceRangeStosMap.keySet().size()];
//                int index = 0;
//                for (Map.Entry<String, List<StoDimensionDTO>> entry : priceRangeStosMap.entrySet()) {
//                    if (entry.getValue() != null || entry.getValue().size() > 0) {
//                        rangeBayesianMap.put(entry.getKey(), entry.getValue().stream().mapToDouble(StoDimensionDTO::getBayesianProbability).sum());
//                        rangeBayesianArr[index] = entry.getValue().stream().mapToDouble(StoDimensionDTO::getBayesianProbability).sum();
//                        index++;
//                    }
//                }
//                log.info("=======================[价格建议]定时任务汇总贝叶斯概率=======================");
//                String range = null;
//                // 取汇总贝叶斯概率最大的区间
//                if (rangeBayesianArr.length > 0) {
//                    double maxBayesian = Arrays.stream(rangeBayesianArr).max().getAsDouble();
//                    for (String str : rangeBayesianMap.keySet()) {
//                        if (maxBayesian == rangeBayesianMap.get(str)) {
//                            range = str;
//                            break;
//                        }
//                    }
//                }
//                log.info("=======================[价格建议]定时任务取最大的贝叶斯概率=======================");
//                if (StrUtil.isNotBlank(range)) {
//                    String left = String.join(".", String.valueOf(Integer.parseInt(range.split("-")[0])), String.valueOf(PriceClassifyUtil.getTailNum()));
//                    String right = String.join(".", String.valueOf(Integer.parseInt(range.split("-")[1])), String.valueOf(PriceClassifyUtil.getTailNum()));
//                    String finalRange = String.join("-", left, right);
//
//                    List<StoDimensionDTO> stoList = priceRangeStosMap.get(range);
//                    for (StoDimensionDTO stoDimensionDTO : stoList) {
//                        log.info("=======================[价格建议]定时任务比对商品价格区间=======================");
//                        // 获取门店类型 1.单体 2.连锁
//                        String stoOrgType = priceProposalMapper.selectStoOrgType(stoDimensionDTO.getClientId(), stoDimensionDTO.getStoCode());
//                        if (StrUtil.isNotBlank(stoOrgType)) {
//                            boolean inLocation = Boolean.TRUE;
//                            String avgSellingPrice = null;
//                            switch (stoOrgType) {
//                                case "1":
//                                    // 单体
//                                    List<StoDimensionDTO> singleStoList = priceProposalMapper.selectStoDimensionListByOrgType(stoDimensionDTO.getClientId());
//                                    if (CollUtil.isNotEmpty(singleStoList)) {
//                                        List<StoDimensionDTO> matchStoList = singleStoList.stream().filter(StoDimensionDTO -> stoDimensionDTO.getProCode().equals(StoDimensionDTO.getProCode())).collect(Collectors.toList());
//                                        if (CollUtil.isNotEmpty(matchStoList)) {
//                                            avgSellingPrice = matchStoList.get(0).getAvgSellingPrice();
//                                            inLocation = StrUtil.isNotBlank(PriceClassifyUtil.getNumLocation(finalRange, avgSellingPrice)) ? true : false;
//                                        }
//                                    }
//                                    break;
//                                case "2":
//                                    // 连锁
//                                    List<CustomerDimensionDTO> customerDimensionDTOList = priceProposalMapper.selectCustomerDimensionList(stoDimensionDTO.getProvinceId(), stoDimensionDTO.getCityId(), stoDimensionDTO.getClientId(), stoDimensionDTO.getProCode());
//                                    if (CollUtil.isNotEmpty(customerDimensionDTOList)) {
//                                        avgSellingPrice = customerDimensionDTOList.get(0).getAvgSellingPrice();
//                                        inLocation = StrUtil.isNotBlank(PriceClassifyUtil.getNumLocation(finalRange, avgSellingPrice)) ? true : false;
//                                    }
//                                    break;
//                            }
//                            if (!inLocation) {
//                                // 查询商品零售价格审批
//                                RetailPriceDTO retailPriceDTO = priceProposalMapper.selectRetailPrice(stoDimensionDTO.getClientId(), stoDimensionDTO.getStoCode(), stoDimensionDTO.getProCode());
//                                if (ObjectUtil.isNotEmpty(retailPriceDTO)) {
//                                    long betweenDays = betweenDays(today, retailPriceDTO.getPrcEffectDate());
//                                    if (betweenDays < -40L || (betweenDays >= -40L && retailPriceDTO.getPrcApprovalSuatus().equals("2"))) {
//                                        if (StrUtil.isNotBlank(avgSellingPrice)) {
//                                            // 商品平均售价不在区间内
//                                            double proposalPrice = calcProposalPrice(finalRange, Double.parseDouble(avgSellingPrice));
//                                            if (proposalPrice != 0.0d) {
//                                                log.info("=======================[价格建议]定时任务计算AB组=======================");
//                                                // 计算差价，>0为A组，<0为B组
//                                                if (proposalPrice - Double.parseDouble(avgSellingPrice) > 0.0d) {
//                                                    PriceProposalADTO adto = new PriceProposalADTO();
//                                                    BeanUtils.copyProperties(stoDimensionDTO, adto);
//                                                    adto.setRange(finalRange);
//                                                    adto.setUpOrDown(1);
//                                                    adto.setProposalPrice(proposalPrice);
//                                                    adto.setDiff(proposalPrice - Double.parseDouble(avgSellingPrice));
//                                                    aList.add(adto);
//                                                } else {
//                                                    PriceProposalBDTO bdto = new PriceProposalBDTO();
//                                                    BeanUtils.copyProperties(stoDimensionDTO, bdto);
//                                                    bdto.setRange(finalRange);
//                                                    bdto.setUpOrDown(2);
//                                                    bdto.setProposalPrice(proposalPrice);
//                                                    bdto.setDiff(proposalPrice - Double.parseDouble(avgSellingPrice));
//                                                    bList.add(bdto);
//                                                }
//                                                // 记录区间比对信息
//                                                ProductPriceProposalZ priceProposalZ = new ProductPriceProposalZ();
//                                                priceProposalZ.setPriceProposalNo(finalMaxNo);
//                                                priceProposalZ.setProvinceCode(stoDimensionDTO.getProvinceId());
//                                                priceProposalZ.setProvinceName(stoDimensionDTO.getProvince());
//                                                priceProposalZ.setCityCode(stoDimensionDTO.getCityId());
//                                                priceProposalZ.setCityName(stoDimensionDTO.getCity());
//                                                priceProposalZ.setClientId(stoDimensionDTO.getClientId());
//                                                priceProposalZ.setClientName(stoDimensionDTO.getClientName());
//                                                priceProposalZ.setStoCode(stoDimensionDTO.getStoCode());
//                                                priceProposalZ.setStoName(stoDimensionDTO.getStoName());
//                                                priceProposalZ.setProCode(stoDimensionDTO.getProCode());
//                                                priceProposalZ.setAvgSellingPrice(stoDimensionDTO.getAvgSellingPrice());
//                                                priceProposalZ.setBayesianProbability(String.valueOf(stoDimensionDTO.getBayesianProbability()));
//                                                priceProposalZ.setPriceRegionLow(finalRange.split("-")[0]);
//                                                priceProposalZ.setPriceRegionHigh(finalRange.split("-")[1]);
//                                                priceProposalZ.setPriceStep(priceClassifyMap.get("gap"));
//                                                HttpServletRequest request = getRequest();
//                                                String userId = null;
//                                                if (request != null) {
//                                                    userId = getLoginUser(request).getUserId();
//                                                    priceProposalZ.setCreatedId(userId);
//                                                }
//                                                priceProposalMapper.insertPriceProposalZ(priceProposalZ);
//                                                log.info("=======================[价格建议]定时任务记录中间表信息=======================");
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//
//            }
//        });
//        // 差价从大到小排序
//        List<PriceProposalADTO> finalAList = aList.stream().sorted(Comparator.comparing(PriceProposalADTO::getDiff).reversed()).collect(Collectors.toList());
//        List<PriceProposalBDTO> finalBList = bList.stream().sorted(Comparator.comparing(PriceProposalBDTO::getDiff).reversed()).collect(Collectors.toList());
//        // 计算AB组比例
//        if (finalAList.size() + finalBList.size() > 50) {
//            int totalSize = finalAList.size() + finalBList.size();
//            double aPercent = new BigDecimal((double) finalAList.size() / totalSize).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//            double bPercent = new BigDecimal((double) finalBList.size() / totalSize).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//            int aCount = (int) (aPercent * 50);
//            int BCount = (int) (bPercent * 50);
//            finalAList = finalAList.subList(0, aCount);
//            finalBList = finalBList.subList(0, BCount);
//        }
//        log.info("=======================[价格建议]定时任务计算AB组比例=======================");
//        // 存储价格建议主表信息
//        ProductPriceProposalH priceProposalH = new ProductPriceProposalH();
//        priceProposalH.setPriceProposalNo(maxNo);
//        DateTimeFormatter nowDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String invalidDate = getInvalidTime(LocalDateTime.now().format(nowDate));
//        priceProposalH.setPriceProposalInvalidTime(invalidDate);
//        priceProposalH.setPriceProposalItemNum(finalAList.size() + finalBList.size());
//        priceProposalH.setBillStatus(0);
//        HttpServletRequest request = getRequest();
//        String userId = null;
////        if (request != null) {
////            userId = getLoginUser(request).getUserId();
////            priceProposalH.setCreatedId(userId);
////        }
//        if (finalAList.size() > 0 && finalBList.size() > 0) {
//            priceProposalMapper.insertPriceProposalH(priceProposalH);
//            log.info("=======================[价格建议]定时任务存储主表信息=======================");
//            // 存储价格建议详情信息
//            handlePricePoposal(finalAList, finalBList, maxNo, userId);
//            log.info("=======================[价格建议]定时任务存储详情信息=======================");
//        }
//        log.info("=======================[价格建议]定时任务结束=======================");
    }

    @Override
    public void clearData() {
        priceProposalMapper.clearHData();
        priceProposalMapper.clearDData();
        priceProposalMapper.clearZData();
    }

    @Override
    public List<PriceProposalListVO> selectPriceProposalList(SelectPriceProposalListCondition condition) {
        if (StrUtil.isBlank(condition.getBillStatus())) {
            condition.setBillStatus(null);
        }
        List<PriceProposalListVO> proposalListVOList = priceProposalMapper.selectPriceProposalList(condition);
        proposalListVOList.forEach(proposal -> {
            List<Integer> nums = priceProposalMapper.selectAdjustedPriceNum(proposal.getPriceProposalNo(), condition.getClientId());
//            proposal.setPriceProposalItemNum(nums.get(2));
            proposal.setSetItemNum(nums.get(0));
            if (nums.get(0) == nums.get(1)) {
                proposal.setBillStatus(1);
            }
        });
        return proposalListVOList;
    }

    @Override
    public List<PriceProposalDetailListVO> selectPriceProposalDetailList(SelectPriceProposalDetailListCondition condition) {
        List<PriceProposalDetailListVO> priceProposalDetailListVOList = priceProposalMapper.selectPriceProposalDetailList(condition);
        priceProposalDetailListVOList.forEach(detail -> {
            detail.setSuggestedRetailPrice(String.join("-", detail.getSuggestedRetailPriceLow(), detail.getSuggestedRetailPriceHigh()));
        });
        return priceProposalDetailListVOList;
    }

    @Override
    public void export(HttpServletResponse response, SelectPriceProposalDetailListCondition condition) throws IOException {
        List<PriceProposalDetailExcelDTO> excels = Lists.newArrayList();
        List<PriceProposalDetailListVO> priceProposalDetailListVOList = priceProposalMapper.selectPriceProposalDetailList(condition);
        AtomicInteger no = new AtomicInteger(1);
        priceProposalDetailListVOList.forEach(detail -> {
            detail.setSuggestedRetailPrice(String.join("-", detail.getSuggestedRetailPriceLow(), detail.getSuggestedRetailPriceHigh()));
            if (StrUtil.isBlank(detail.getLatestCostPrice())) detail.setLatestCostPrice("0.00");
            PriceProposalDetailExcelDTO excel = new PriceProposalDetailExcelDTO();
            BeanUtils.copyProperties(detail, excel);
            excel.setNo(no.getAndIncrement());
            if (detail.getAdjustStatus() == 1) {
                excel.setAdjsutStatus("已处理");
            } else {
                excel.setAdjsutStatus("待处理");
            }
            excels.add(excel);
        });
        String name = "价格建议".concat(condition.getPriceProposalNo()).concat(".xlsx");
        String fileName = URLEncoder.encode(name, CharsetUtil.UTF_8);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        WriteSheet writeSheet1 = EasyExcel.writerSheet(0, fileName).head(PriceProposalDetailExcelDTO.class).build();
        excelWriter.write(excels, writeSheet1);
        excelWriter.finish();
    }

    @Override
    public void savePriceProposaInfo(List<SavePriceProposalCondition> conditions, String clientId) {
        for (SavePriceProposalCondition condition : conditions) {
            condition.setClientId(clientId);
            priceProposalMapper.updatePriceProposalNewRetailPrice(condition);
        }
    }

    @Override
    public List<AllStosVO> selectAllStosByClientId(String clientId) {
        return priceProposalMapper.selectAllStosByClientId(clientId);
    }

    @Override
    public List<AllStosVO> stoQuickSearch(StoQuickSearchCondition condition) {
        List<AllStosVO> resultVos = Lists.newArrayList();
        for (int i = 0; i < condition.getIfMedicalcare().length; i++) {
            if (condition.getIfMedicalcare()[i] == 0) {
                condition.setContained(1);
            }
        }
        if (ArrayUtil.isNotEmpty(condition.getGssgType())) {
            for (String[] arr : condition.getGssgType()) {
                List<String> gssgIds = Lists.newArrayList();
                List<String> gssgTypes = Lists.newArrayList();
                gssgTypes.add(arr[0]);
                gssgIds.add(arr[1]);
                condition.setGssgTypes(gssgTypes);
                condition.setGssgIds(gssgIds);
                List<AllStosVO> vos = priceProposalMapper.stoQuickSearch(condition);
                resultVos.addAll(vos);
            }
            if (resultVos != null && resultVos.size() > 0) {
                resultVos = resultVos.stream()
                        .collect(Collectors.collectingAndThen
                                (Collectors.toCollection(() ->
                                                new TreeSet<>(Comparator.comparing(t -> t.getStoCode()))),
                                        ArrayList::new
                                )
                        );
            }
            return resultVos;
        }
        return priceProposalMapper.stoQuickSearch(condition);
    }

    @Override
    public RetailPriceInfoSaveRequestDto saveRetailPriceInfo(SaveRetailPriceInfoCondition condition, String clientId) {
        RetailPriceInfoSaveRequestDto dto = new RetailPriceInfoSaveRequestDto();
        List<RetailPriceInfoItem> items = Lists.newArrayList();
        for (ProInfoCondition pro : condition.getPros()) {
            RetailPriceInfoItem item = new RetailPriceInfoItem();
            item.setPrcProduct(pro.getProCode());
            item.setPrcAmount(new BigDecimal(pro.getNewPrice()));
            item.setPrcEffectDate(condition.getPrcEffectDate());
            Map<String, String> map = priceProposalMapper.selectUnitAndLsj(clientId, pro.getProCode());
            if (MapUtil.isNotEmpty(map)) {
                // 修改前价格
                item.setPrcAmountBefore(StrUtil.isNotBlank(map.get("proLsj")) ? new BigDecimal(map.get("proLsj")) : null);
                // 单位
                item.setPrcUnit(map.get("proUnit"));
                items.add(item);
            }
            priceProposalMapper.updatePriceProposalAdjustStatus(condition.getPriceProposalNo(), clientId, pro.getProCode());
//            int waitAdjustNum = priceProposalMapper.selectAdjustedPriceNum(condition.getPriceProposalNo(), clientId);
//            if (waitAdjustNum == 0) {
//
//            }
        }
        dto.setClient(clientId);
        dto.setPrcClass("P001");
        dto.setStoreList(Arrays.asList(condition.getStoCode()));
        dto.setProList(items);
        // 更新处理状态
        return dto;
    }

    private Map<String, String> getPriceClassifyMap(List<Map<String, String>> rangeMapList, String avgSellingPrice) {
        for (Map<String, String> rangeMap : rangeMapList) {
            if (StrUtil.isNotBlank(avgSellingPrice)) {
                if (Double.parseDouble(avgSellingPrice) > Double.parseDouble(String.valueOf(rangeMap.get("start")))
                        && Double.parseDouble(avgSellingPrice) <= Double.parseDouble(String.valueOf(rangeMap.get("end")))) {
                    return rangeMap;
                }
            }
        }
        return null;
    }

    private void saveRangeClassifyInfo(StoDimensionDTO stoDimensionDTO, String finalMaxNo, String locationRange, String gap) {
        ProductPriceProposalZ priceProposalZ = new ProductPriceProposalZ();
        priceProposalZ.setPriceProposalNo(finalMaxNo);
        priceProposalZ.setProvinceCode(stoDimensionDTO.getProvinceId());
        priceProposalZ.setProvinceName(stoDimensionDTO.getProvince());
        priceProposalZ.setCityCode(stoDimensionDTO.getCityId());
        priceProposalZ.setCityName(stoDimensionDTO.getCity());
        priceProposalZ.setClientId(stoDimensionDTO.getClientId());
        priceProposalZ.setClientName(stoDimensionDTO.getClientName());
        priceProposalZ.setStoCode(stoDimensionDTO.getStoCode());
        priceProposalZ.setStoName(stoDimensionDTO.getStoName());
        priceProposalZ.setProCode(stoDimensionDTO.getProCode());
        priceProposalZ.setAvgSellingPrice(stoDimensionDTO.getAvgSellingPrice());
        priceProposalZ.setBayesianProbability(String.valueOf(stoDimensionDTO.getBayesianProbability()));
        priceProposalZ.setPriceRegionLow(locationRange.split("-")[0]);
        priceProposalZ.setPriceRegionHigh(locationRange.split("-")[1]);
        priceProposalZ.setPriceStep(gap);
//        priceProposalMapper.insertPriceProposalZ(priceProposalZ);
    }

    private List<Map<String, String>> getDefaultRangeMapList() {
        List<Map<String, String>> rangeMapList = Lists.newArrayList();
        Map<String, String> rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "A");
        rangeMap.put("start", "0");
        rangeMap.put("end", "10");
        rangeMap.put("gap", "1");
        rangeMapList.add(rangeMap);
        rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "B");
        rangeMap.put("start", "10");
        rangeMap.put("end", "50");
        rangeMap.put("gap", "5");
        rangeMapList.add(rangeMap);
        rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "C");
        rangeMap.put("start", "50");
        rangeMap.put("end", "100");
        rangeMap.put("gap", "10");
        rangeMapList.add(rangeMap);
        rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "D");
        rangeMap.put("start", "100");
        rangeMap.put("end", "10000");
        rangeMap.put("gap", "20");
        rangeMapList.add(rangeMap);

        return rangeMapList;
    }

    private double calcProposalPrice(String range, double avgPrice) {
        String[] rangeArr = range.split("-");
        if (avgPrice < Double.parseDouble(rangeArr[0])) {
            return Double.parseDouble(rangeArr[0]);
        } else if (avgPrice > Double.parseDouble(rangeArr[1])) {
            return Double.parseDouble(rangeArr[1]);
        }
        return 0.0d;
    }

    private void handlePricePoposal(List<PriceProposalADTO> aList, List<PriceProposalBDTO> bList, String maxNo, String userId) {
        List<ProductPriceProposalD> finalPricePriposalDList = Lists.newArrayList();
//        for (PriceProposalADTO adto : aList) {
//            ProductPriceProposalD priceProposalD = new ProductPriceProposalD();
//            priceProposalD.setPriceProposalNo(maxNo);
//            priceProposalD.setProvinceCode(adto.getProvinceId());
//            priceProposalD.setProvinceName(adto.getProvince());
//            priceProposalD.setCityCode(adto.getCityId());
//            priceProposalD.setCityName(adto.getCity());
//            priceProposalD.setClientId(adto.getClientId());
//            priceProposalD.setClientName(adto.getClientName());
//            priceProposalD.setStoCode(adto.getStoCode());
//            priceProposalD.setStoName(adto.getStoName());
//            priceProposalD.setProCode(adto.getProCode());
//            priceProposalD.setProDesc(adto.getProDepict());
//            priceProposalD.setProSpecs(adto.getProSpecs());
//            priceProposalD.setProFactoryName(adto.getFactoryName());
//            priceProposalD.setProUnit(adto.getProUnit());
//            priceProposalD.setProClass(adto.getProClass());
//            priceProposalD.setProClassName(adto.getProClassName());
//            priceProposalD.setProCompClass(adto.getProCompClass());
//            priceProposalD.setProCompClassName(adto.getProCompClassName());
//            priceProposalD.setProBigClass(adto.getProBigClass());
//            priceProposalD.setProBigClassName(adto.getProBigClassName());
//            priceProposalD.setCreatedId(userId);
//            priceProposalD.setAdjustStatus(0);
//            priceProposalD.setLatestCostPrice(priceProposalMapper.getLatestCostPrice(adto.getClientId(), adto.getStoCode(), adto.getProCode()));
//            priceProposalD.setOriginalAvgSellingPrice(adto.getAvgSellingPrice());
//            priceProposalD.setSuggestedRetailPriceLow(adto.getRange().split("-")[0]);
//            priceProposalD.setSuggestedRetailPriceHigh(adto.getRange().split("-")[1]);
//            priceProposalD.setNewRetailPrice(String.valueOf(adto.getProposalPrice()));
//            priceProposalD.setProposalType(adto.getUpOrDown());
//            finalPricePriposalDList.add(priceProposalD);
//            priceProposalMapper.insertPriceProposalD(priceProposalD);
//        }
//        for (PriceProposalBDTO bdto : bList) {
//            ProductPriceProposalD priceProposalD = new ProductPriceProposalD();
//            priceProposalD.setPriceProposalNo(maxNo);
//            priceProposalD.setProvinceCode(bdto.getProvinceId());
//            priceProposalD.setProvinceName(bdto.getProvince());
//            priceProposalD.setCityCode(bdto.getCityId());
//            priceProposalD.setCityName(bdto.getCity());
//            priceProposalD.setClientId(bdto.getClientId());
//            priceProposalD.setClientName(bdto.getClientName());
//            priceProposalD.setStoCode(bdto.getStoCode());
//            priceProposalD.setStoName(bdto.getStoName());
//            priceProposalD.setProCode(bdto.getProCode());
//            priceProposalD.setProDesc(bdto.getProDepict());
//            priceProposalD.setProSpecs(bdto.getProSpecs());
//            priceProposalD.setProFactoryName(bdto.getFactoryName());
//            priceProposalD.setProUnit(bdto.getProUnit());
//            priceProposalD.setProClass(bdto.getProClass());
//            priceProposalD.setProClassName(bdto.getProClassName());
//            priceProposalD.setProCompClass(bdto.getProCompClass());
//            priceProposalD.setProCompClassName(bdto.getProCompClassName());
//            priceProposalD.setProBigClass(bdto.getProBigClass());
//            priceProposalD.setProBigClassName(bdto.getProBigClassName());
//            priceProposalD.setCreatedId(userId);
//            priceProposalD.setAdjustStatus(0);
//            priceProposalD.setLatestCostPrice(priceProposalMapper.getLatestCostPrice(bdto.getClientId(), bdto.getStoCode(), bdto.getProCode()));
//            priceProposalD.setOriginalAvgSellingPrice(bdto.getAvgSellingPrice());
//            priceProposalD.setSuggestedRetailPriceLow(bdto.getRange().split("-")[0]);
//            priceProposalD.setSuggestedRetailPriceHigh(bdto.getRange().split("-")[1]);
//            priceProposalD.setNewRetailPrice(String.valueOf(bdto.getProposalPrice()));
//            priceProposalD.setProposalType(bdto.getUpOrDown());
//            finalPricePriposalDList.add(priceProposalD);
//            priceProposalMapper.insertPriceProposalD(priceProposalD);
//        }
//        priceProposalMapper.batchInsertPriceProposalD(finalPricePriposalDList);
    }

    private String getInvalidTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, 5);
            return sdf.format(cal.getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private long betweenDays(String now, String past) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// 自定义时间格式

        Calendar calendar_a = Calendar.getInstance();// 获取日历对象
        Calendar calendar_b = Calendar.getInstance();

        Date date_a = null;
        Date date_b = null;

        try {
            date_a = simpleDateFormat.parse(now);//字符串转Date
            date_b = simpleDateFormat.parse(past);
            calendar_a.setTime(date_a);// 设置日历
            calendar_b.setTime(date_b);
        } catch (ParseException e) {
            e.printStackTrace();//格式化异常
        }

        long time_a = calendar_a.getTimeInMillis();
        long time_b = calendar_b.getTimeInMillis();

        long between_days = (time_b - time_a) / (1000 * 3600 * 24);//计算相差天数

        return between_days;
    }

    private GetLoginOutData getLoginUser(HttpServletRequest request) {
        String token = getRequestHeaderParameter("X-Token");
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(UtilMessage.Token);
        }
        String userInfo = (String) this.redisManager.get(request.getHeader("X-Token"));
        JSONObject jsonObject = JSON.parseObject(userInfo);
        GetLoginOutData data = JSONObject.toJavaObject(jsonObject, GetLoginOutData.class);
        return data;
    }

    private String getRequestHeaderParameter(String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            return request.getHeader(key);
        }
        return null;
    }

    private HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            return request;
        }
        return null;
    }

}

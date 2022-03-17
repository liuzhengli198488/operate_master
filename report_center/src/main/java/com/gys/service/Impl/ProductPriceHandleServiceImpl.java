package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gys.entity.priceProposal.dto.*;
import com.gys.entity.priceProposal.entity.ProductPriceProposalD;
import com.gys.entity.priceProposal.entity.ProductPriceProposalH;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;
import com.gys.mapper.ProductPriceProposalMapper;
import com.gys.service.ProductPriceDataService;
import com.gys.service.ProductPriceHandleService;
import com.gys.util.priceProposal.PriceClassifyUtil;
import com.gys.util.priceProposal.PriceProposalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议数据处理
 * @CreateTime 2022-01-18 14:58:00
 */
@Service
@Slf4j
public class ProductPriceHandleServiceImpl implements ProductPriceHandleService {

    @Autowired
    private ProductPriceDataService priceDataService;

    @Autowired
    private ProductPriceProposalMapper priceProposalMapper;

    @Override
    public List<ProductPriceProposalZ> handleCityDimension(List<CityDimensionDTO> cityDimensionDTOList, String maxNo, PriceProposalClientDTO priceProposalClientDTO) {
        // 加盟商市级区域内所有商品所在的价格区间分类
        List<ProductPriceProposalZ> cityProClassifyDTOList = Lists.newArrayList();
        log.info("=======================[价格建议]开始处理区域市级维度=======================");
        log.info("=======================[价格建议]查询城市是否有价格分类，如果没有则默认=======================");
        // 查询商品是否有价格分类，如果没有则默认
        List<Map<String, String>> rangeMapList = priceProposalMapper.selectPriceRangeByClientId(priceProposalClientDTO.getClientId()).size() == 0 ? PriceProposalUtil.getDefaultRangeMapList() : priceProposalMapper.selectPriceRangeByClientId(priceProposalClientDTO.getClientId());
        log.info("=======================[价格建议]开始匹配商品价格区间分类ABCD=======================");
        for (CityDimensionDTO cityDimensionDTO : cityDimensionDTOList) {
            // 根据区域平均售价判断每个商品的价格区间分类A/B/C/D
            ProductPriceProposalZ productPriceProposalZ = PriceProposalUtil.getPriceClassifyMap(maxNo, rangeMapList, cityDimensionDTO, priceProposalClientDTO);
            if (ObjectUtil.isNotEmpty(productPriceProposalZ)) {
                cityProClassifyDTOList.add(productPriceProposalZ);
            }
        }
        // 记录商品对应的价格区间分类
        log.info("=======================[价格建议]记录市级区域商品价格区间分类ABCD=======================");
        if (cityProClassifyDTOList.size() > 0) {
            priceDataService.saveCityRangeInfo(cityProClassifyDTOList);
        }
        return cityProClassifyDTOList;

    }

    @Override
    public List<ProductPriceProposalZ> handleStoDimension(List<ProductPriceProposalZ> cityProClassifyDTOList, String maxNo, PriceProposalClientDTO priceProposalClientDTO) {
        // 区域门店商品对应的价格区间
        List<ProductPriceProposalZ> stoProRangeDTOList = Lists.newArrayList();
        // 根据省市查询门店维度
        List<StoDimensionDTO> stoDimensionDTOList = priceProposalMapper.selectStoDimensionList(priceProposalClientDTO.getProvinceId(), priceProposalClientDTO.getCityId(), priceProposalClientDTO.getClientId());
        for (StoDimensionDTO stoDimensionDTO : stoDimensionDTOList) {
            if (stoDimensionDTO.getProCode().equals("10040342")) {
                System.out.println(111);
            }
            // todo 优化为map取值
            List<ProductPriceProposalZ> stoRangeList = cityProClassifyDTOList.stream().filter(pro -> pro.getProCode().equals(stoDimensionDTO.getProCode())).collect(Collectors.toList());
            if (CollUtil.isEmpty(stoRangeList)) {
                continue;
            }
            ProductPriceProposalZ productPriceProposalZ = stoRangeList.get(0);
            int start = Integer.parseInt(productPriceProposalZ.getPriceRegionLow()), end = Integer.parseInt(productPriceProposalZ.getPriceRegionHigh()), gap = Integer.parseInt(productPriceProposalZ.getPriceStep());
            // 根据起始价格、结束价格、价格间隔计算价格区间
            String[] priceRangeArr = PriceClassifyUtil.getStrArray(start, end, gap);
            for (int i = 0; i < priceRangeArr.length; i++) {
                // 判断商品平均售价是否在价格区间
                String locationRange = PriceClassifyUtil.getNumLocation(priceRangeArr[i], stoDimensionDTO.getAvgSellingPrice());
                if (StrUtil.isBlank(locationRange)) {
                    continue;
                }
                ProductPriceProposalZ stoProRangeDTO = new ProductPriceProposalZ();
                BeanUtils.copyProperties(productPriceProposalZ, stoProRangeDTO);
                // 商品在价格区间，补充门店信息
                fillStoRangeInfo(stoProRangeDTO, stoDimensionDTO, locationRange, gap);
                stoProRangeDTO.setStoCode(stoDimensionDTO.getStoCode());
                stoProRangeDTOList.add(stoProRangeDTO);
            }
        }
        if (stoProRangeDTOList.size() > 0) {
            priceDataService.saveCityRangeInfo(stoProRangeDTOList);
        }
        return stoProRangeDTOList;
    }

    /**
     * @Author jiht
     * @Description 补充门店维度ProductPriceProposalZ表数据
     * @Date 2022/1/26 13:40
     * @Param [productPriceProposalZ, stoDimensionDTO, locationRange, gap]
     * @Return void
     **/
    private void fillStoRangeInfo(ProductPriceProposalZ productPriceProposalZ, StoDimensionDTO stoDimensionDTO, String locationRange, int gap) {
        if ("10012681".equals(stoDimensionDTO.getProCode())) {
            log.info("xxxxxx:{},{},{}", stoDimensionDTO.getClientId(), stoDimensionDTO.getStoCode(), stoDimensionDTO.getBayesianProbability());
        }
        // 门店维护上月品类模型，按照省市取，不到加盟商
//        productPriceProposalZ.setStoCode(stoDimensionDTO.getStoCode());
//        productPriceProposalZ.setStoName(stoDimensionDTO.getStoName());
        productPriceProposalZ.setAvgSellingPrice(stoDimensionDTO.getAvgSellingPrice());
        productPriceProposalZ.setBayesianProbability(String.valueOf(stoDimensionDTO.getBayesianProbability()));
        productPriceProposalZ.setPriceRegionLow(locationRange.split("-")[0]);
        productPriceProposalZ.setPriceRegionHigh(locationRange.split("-")[1]);
        productPriceProposalZ.setPriceStep(String.valueOf(gap));
        productPriceProposalZ.setDataType("2");
        productPriceProposalZ.setProDepict(stoDimensionDTO.getProDepict());
        productPriceProposalZ.setProSpecs(stoDimensionDTO.getProSpecs());
        productPriceProposalZ.setFactoryName(stoDimensionDTO.getFactoryName());
        productPriceProposalZ.setProUnit(stoDimensionDTO.getProUnit());
        productPriceProposalZ.setProCompClass(stoDimensionDTO.getProCompClass());
        productPriceProposalZ.setProCompClassName(stoDimensionDTO.getProCompClassName());
        productPriceProposalZ.setProBigClass(stoDimensionDTO.getProBigClass());
        productPriceProposalZ.setProBigClassName(stoDimensionDTO.getProBigClassName());
        productPriceProposalZ.setProClass(stoDimensionDTO.getProClass());
        productPriceProposalZ.setProClassName(stoDimensionDTO.getProClassName());
    }

    @Override
    public List<ProductPriceProposalZ> summaryStoMaxBayesianProbability(List<ProductPriceProposalZ> stoProRangeDTOList, PriceProposalClientDTO client) {
        List<ProductPriceProposalZ> maxList = Lists.newArrayList();
        if (CollUtil.isNotEmpty(stoProRangeDTOList)) {
            Map<String, List<ProductPriceProposalZ>> groupSto = stoProRangeDTOList.stream().collect(Collectors.groupingBy(ProductPriceProposalZ::getProCode));
            for (String proCodeKey : groupSto.keySet()) {
                if (proCodeKey.equals("10024116"))  {
                    System.out.println(22);
                }
                List<ProductPriceProposalZ> pros = groupSto.get(proCodeKey);
                // 按照区间间隔汇总
                Map<String, List<ProductPriceProposalZ>> groupStoLow = pros.stream().collect(Collectors.groupingBy(ProductPriceProposalZ::getPriceRegionLow));
                String maxBayesiantPriceRegionLow = "";
                double maxBayesianProbability = 0.0;
                String stoStr = "";
                for (String priceRegionLow : groupStoLow.keySet()) {
                    List<ProductPriceProposalZ> groupStoLowList = groupStoLow.get(priceRegionLow);
                    double sumBayesianProbability = 0;
                    // 汇总间隔贝叶斯概率
                    for (ProductPriceProposalZ tempBayesianProbability : groupStoLowList) {
                        sumBayesianProbability += Double.parseDouble(tempBayesianProbability.getBayesianProbability());
                    }
                    // 取最大间隔的下限
                    if (maxBayesianProbability <= sumBayesianProbability) {
                        maxBayesianProbability = sumBayesianProbability;
                        maxBayesiantPriceRegionLow = priceRegionLow;
                    }
                    if (maxBayesianProbability == sumBayesianProbability) {
                        List<Map<String, String>> stos = priceProposalMapper.selectStosByClientId(client.getClientId());
                        for (Map<String, String> sto : stos) {
                            stoStr += sto.get("gsstBrId") + "-" + sto.get("gsstBrName") + "-" + stos.size();
                        }
                    }
                }
                // 获取贝叶斯概率最大
                ProductPriceProposalZ max = getMax(pros, maxBayesiantPriceRegionLow, maxBayesianProbability);
                max.setDataType("3");
                max.setStoInfo(stoStr);
                maxList.add(max);
            }
        }
        if (maxList.size() > 0) {
            priceDataService.saveCityRangeInfo(maxList);
        }
        return maxList;
    }

    @Override
    public List<ProductPriceProposalZ> randomStoRange(List<ProductPriceProposalZ> maxList) {
        List<ProductPriceProposalZ> randomList = Lists.newArrayList();
        for (ProductPriceProposalZ pro : maxList) {
            if (pro.getProCode().equals("10024116")) {
                System.out.println(1);
            }
            String left = String.join(".", String.valueOf(Integer.parseInt(pro.getPriceRegionLow())), String.valueOf(PriceClassifyUtil.getTailNum()));
            String right = String.join(".", String.valueOf(Integer.parseInt(pro.getPriceRegionHigh())), String.valueOf(PriceClassifyUtil.getTailNum()));
//            String finalRange = String.join("-", left, right);
            pro.setPriceRegionLow(left);
            pro.setPriceRegionHigh(right);
            pro.setDataType("4");
            randomList.add(pro);
        }
        if (randomList.size() > 0) {
            priceDataService.saveCityRangeInfo(randomList);
        }
        return randomList;
    }

    @Override
    public List<ProductPriceProposalZ> matchStoPriceByRange(List<ProductPriceProposalZ> randomList, PriceProposalClientDTO clientDTO) {
        List<ProductPriceProposalZ> needProposalProsList = Lists.newArrayList();
        switch (PriceProposalUtil.getClientType(clientDTO.getType2())) {
            case "1":
                // 单体
                List<StoDimensionDTO> singleStoList = priceProposalMapper.selectStoDimensionListByOrgType(clientDTO.getProvinceId(), clientDTO.getCityId(), clientDTO.getClientId());
                if (CollUtil.isNotEmpty(singleStoList)) {
                    for (ProductPriceProposalZ z : randomList) {
                        String finalRange = String.join("-", z.getPriceRegionLow(), z.getPriceRegionHigh());
                        List<StoDimensionDTO> matchStoList = singleStoList.stream().filter(StoDimensionDTO -> z.getProCode().equals(StoDimensionDTO.getProCode())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(matchStoList)) {
                            String avgSellingPrice = "";
                            if (matchStoList.size() <= 1 && matchStoList.size() > 0) {
                                avgSellingPrice = matchStoList.get(0).getAvgSellingPrice();
                            } else {
                                avgSellingPrice = matchStoList.get(1).getAvgSellingPrice();
                            }
                            if (StrUtil.isBlank(PriceClassifyUtil.getNumLocation(finalRange, avgSellingPrice))) {
                                // 商品价格不在价格区间
                                z.setStoCode(matchStoList.get(0).getStoCode());
                                z.setStoName(matchStoList.get(0).getStoName());
                                z.setAvgSellingPrice(avgSellingPrice);
                                z.setBayesianProbability(String.valueOf(matchStoList.get(0).getBayesianProbability()));
                                z.setDataType("5");
                                needProposalProsList.add(z);
                            }
                        }
                    }
                }
                break;
            case "2":
                // 连锁
                List<CustomerDimensionDTO> customerDimensionDTOList = priceProposalMapper.selectCustomerDimensionList(clientDTO.getProvinceId(), clientDTO.getCityId(), clientDTO.getClientId());
                if (CollUtil.isNotEmpty(customerDimensionDTOList)) {
                    for (ProductPriceProposalZ z : randomList) {
                        if (z.getProCode().equals("10024116")) {
                            System.out.println("------------");
                        }
                        List<String> positionList = priceProposalMapper.selectProPosition(z.getProCode(), clientDTO.getClientId());
                        if (positionList != null && positionList.size() > 0) {
                            int i = 0;
                            for (String s : positionList) {
                                if (StrUtil.isNotBlank(s) && !s.equalsIgnoreCase("X")) {
                                    i++;
                                }
                            }
                            if (i <= 0) {
                                continue;
                            }
                        }
                        String finalRange = String.join("-", z.getPriceRegionLow(), z.getPriceRegionHigh());
                        List<CustomerDimensionDTO> matchStoList = customerDimensionDTOList.stream().filter(CustomerDimensionDTO -> z.getProCode().equals(CustomerDimensionDTO.getProCode())).collect(Collectors.toList());
                        if (CollUtil.isNotEmpty(matchStoList)) {
                            String avgSellingPrice = "";
                            if (matchStoList.size() <= 1 && matchStoList.size() > 0) {
                                avgSellingPrice = matchStoList.get(0).getAvgSellingPrice();
                            } else {
                                avgSellingPrice = matchStoList.get(1).getAvgSellingPrice();
                            }
                            if (StrUtil.isBlank(PriceClassifyUtil.getNumLocation(finalRange, avgSellingPrice))) {
                                // 商品价格不在价格区间
                                z.setAvgSellingPrice(avgSellingPrice);
                                z.setDataType("5");
                                needProposalProsList.add(z);
                            }
                        }
                    }
                }
                break;
        }
        if (needProposalProsList.size() > 0) {
            priceDataService.saveCityRangeInfo(needProposalProsList);
        }
        return needProposalProsList;
    }

    @Override
    public List<PriceProposalADTO> getNeedSaveProposalPros(List<ProductPriceProposalZ> matchProposalProsList) {
        List<PriceProposalADTO> list = Lists.newArrayList();
        for (ProductPriceProposalZ z : matchProposalProsList) {
            if (z.getProCode().equals("10024116")) {
                System.out.println(1);
            }
            String finalRange = String.join("-", z.getPriceRegionLow(), z.getPriceRegionHigh());
            // 查询商品零售价格审批
//            RetailPriceDTO retailPriceDTO = priceProposalMapper.selectRetailPrice(z.getClientId(), z.getStoCode(), z.getProCode());
//            if (ObjectUtil.isNotEmpty(retailPriceDTO)) {
//                long betweenDays = PriceProposalUtil.betweenDays(PriceProposalUtil.getToday(), retailPriceDTO.getPrcEffectDate());
//                if (betweenDays < -40L || (betweenDays >= -40L && retailPriceDTO.getPrcApprovalSuatus().equals("2"))) {
                    // 商品平均售价不在区间内
                    if (StrUtil.isNotBlank(z.getAvgSellingPrice())) {
                        double proposalPrice = PriceProposalUtil.calcProposalPrice(finalRange, Double.parseDouble(z.getAvgSellingPrice()));
                        if (proposalPrice != 0.0d) {
                            log.info("=======================[价格建议]定时任务计算AB组=======================");
                            // 计算差价，>0为A组，<0为B组
                            PriceProposalADTO adto = new PriceProposalADTO();
                            BeanUtils.copyProperties(z, adto);
                            adto.setRange(finalRange);
                            adto.setProposalPrice(proposalPrice);
                            adto.setDiff(proposalPrice - Double.parseDouble(z.getAvgSellingPrice()));
                            if (proposalPrice - Double.parseDouble(z.getAvgSellingPrice()) > 0.0d) {
                                adto.setUpOrDown(1);
                            } else {
                                adto.setUpOrDown(2);
                            }
                            list.add(adto);
                        }
                    }
//                }
//            }
        }
        return list;
    }

    @Override
    public void savePros(List<PriceProposalADTO> needSaveProsList, String maxNo, String invalidDate) {
        List<PriceProposalADTO> finalABList = Lists.newArrayList();
        // 差价从大到小排序
        List<PriceProposalADTO> finalAList = needSaveProsList.stream().filter(pro -> pro.getUpOrDown() == 1).collect(Collectors.toList());
        List<PriceProposalADTO> finalBList = needSaveProsList.stream().filter(pro -> pro.getUpOrDown() == 2).collect(Collectors.toList());
        finalAList = finalAList.stream().sorted(Comparator.comparing(PriceProposalADTO::getDiff).reversed()).collect(Collectors.toList());
        finalBList = finalBList.stream().sorted(Comparator.comparing(PriceProposalADTO::getDiff).reversed()).collect(Collectors.toList());
        // 计算AB组比例
        if (finalAList.size() + finalBList.size() > 50) {
            int totalSize = finalAList.size() + finalBList.size();
            double aPercent = new BigDecimal((double) finalAList.size() / totalSize).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            double bPercent = new BigDecimal((double) finalBList.size() / totalSize).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            int aCount = (int) (aPercent * 50);
            int bCount = (int) (bPercent * 50);
            if (finalAList.size() >= aCount) {
                finalAList = finalAList.subList(0, aCount);
            } else {
                finalAList = finalAList.subList(0, finalAList.size());
            }
            if (finalBList.size() >= bCount) {
                finalBList = finalBList.subList(0, finalBList.size());
            }

        }
        log.info("=======================[价格建议]定时任务计算AB组比例=======================");
        if (finalAList.size() > 0 || finalBList.size() > 0) {
            // 存储价格建议详情信息
            finalABList.addAll(finalAList);
            finalABList.addAll(finalBList);
            List<ProductPriceProposalD> resList = priceDataService.savePricePoposal(finalABList, maxNo, null);
            log.info("=======================[价格建议]定时任务存储详情信息=======================");
            // 存储价格建议主表信息
            ProductPriceProposalH priceProposalH = new ProductPriceProposalH();
            priceProposalH.setPriceProposalNo(maxNo);
            priceProposalH.setPriceProposalInvalidTime(invalidDate);
            priceProposalH.setPriceProposalItemNum(resList.size());
            priceProposalH.setBillStatus(0);
            priceProposalMapper.insertPriceProposalH(priceProposalH);
            log.info("=======================[价格建议]定时任务存储主表信息=======================");
        }
    }


    private ProductPriceProposalZ getMax(List<ProductPriceProposalZ> list, String maxBayesiantPriceRegionLow, double maxBayesianProbability) {
        ProductPriceProposalZ max = new ProductPriceProposalZ();
        for (ProductPriceProposalZ temp : list) {
            if (maxBayesiantPriceRegionLow.equals(temp.getPriceRegionLow())) {
                BeanUtils.copyProperties(temp, max);
                max.setBayesianProbability(String.valueOf(maxBayesianProbability));
            }
        }
        return max;
    }

}
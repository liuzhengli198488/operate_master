package com.gys.service.Impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.gys.entity.priceProposal.dto.*;
import com.gys.entity.priceProposal.entity.ProductPriceProposalD;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;
import com.gys.mapper.ProductPriceProposalMapper;
import com.gys.service.ProductPriceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议
 * @CreateTime 2022-01-18 15:31:00
 */
@Service
@Slf4j
public class ProductPriceDataServiceImpl implements ProductPriceDataService {

    @Autowired
    private ProductPriceProposalMapper priceProposalMapper;

    @Override
    public void saveCityRangeInfo(List<ProductPriceProposalZ> cityProClassifyDTOList) {
        priceProposalMapper.batchInsertPriceProposalZ(cityProClassifyDTOList);
    }

    @Override
    public void saveStoRangeInfo(StoDimensionDTO stoDimensionDTO, String maxNo, String range, String gap) {
        ProductPriceProposalZ priceProposalZ = new ProductPriceProposalZ();
        priceProposalZ.setPriceProposalNo(maxNo);
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
        priceProposalZ.setPriceRegionLow(range.split("-")[0]);
        priceProposalZ.setPriceRegionHigh(range.split("-")[1]);
        priceProposalZ.setPriceStep("2:" + gap);
//        priceProposalMapper.insertPriceProposalZ(priceProposalZ);
    }

    @Override
    public void saveStoTotalBayesianProbabilityInfo(String maxNo, String range, double totalBayesianProbability, PriceProposalClientDTO priceProposalClientDTO) {
        ProductPriceProposalZ priceProposalZ = new ProductPriceProposalZ();
        priceProposalZ.setPriceProposalNo(maxNo);
        priceProposalZ.setClientId(priceProposalClientDTO.getClientId());
        priceProposalZ.setClientName(priceProposalClientDTO.getClientName());
        priceProposalZ.setProvinceCode(priceProposalClientDTO.getProvinceId());
        priceProposalZ.setCityCode(priceProposalClientDTO.getCityId());
        priceProposalZ.setBayesianProbability(String.valueOf(totalBayesianProbability));
        priceProposalZ.setPriceRegionLow(range.split("-")[0]);
        priceProposalZ.setPriceRegionHigh(range.split("-")[1]);
        priceProposalZ.setPriceStep("3:");
//        priceProposalMapper.insertPriceProposalZ(priceProposalZ);
    }

    @Override
    public void saveRangeMatchInfo(StoDimensionDTO stoDimensionDTO, String maxNo, String range) {
        ProductPriceProposalZ priceProposalZ = new ProductPriceProposalZ();
        priceProposalZ.setPriceProposalNo(maxNo);
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
        priceProposalZ.setPriceRegionLow(range.split("-")[0]);
        priceProposalZ.setPriceRegionHigh(range.split("-")[1]);
        priceProposalZ.setPriceStep("4:");
//        HttpServletRequest request = getRequest();
//        String userId = null;
//        if (request != null) {
//            userId = getLoginUser(request).getUserId();
//            priceProposalZ.setCreatedId(userId);
//        }
//        priceProposalMapper.insertPriceProposalZ(priceProposalZ);
    }

    @Override
    public List<ProductPriceProposalD> savePricePoposal(List<PriceProposalADTO> aList, String maxNo, String userId) {
        List<ProductPriceProposalD> finalPricePriposalDList = Lists.newArrayList();
        for (PriceProposalADTO adto : aList) {
            ProductPriceProposalD priceProposalD = new ProductPriceProposalD();
            priceProposalD.setPriceProposalNo(maxNo);
            priceProposalD.setProvinceCode(adto.getProvinceCode());
            priceProposalD.setProvinceName(adto.getProvinceName());
            priceProposalD.setCityCode(adto.getCityCode());
            priceProposalD.setCityName(adto.getCityName());
            priceProposalD.setClientId(adto.getClientId());
            priceProposalD.setClientName(adto.getClientName());
//            priceProposalD.setStoCode(adto.getStoCode());
//            priceProposalD.setStoName(adto.getStoName());
            priceProposalD.setStoCode("");
            priceProposalD.setStoName("");
            priceProposalD.setProCode(adto.getProCode());
            List<String> proSelfCodes = priceProposalMapper.getProSelfCode(adto.getClientId(), adto.getProCode(), adto.getProvinceCode(), adto.getCityCode());
            if (proSelfCodes.size() > 0) {
                priceProposalD.setProSelfCode(proSelfCodes.get(0));
            } else {
                continue;
            }
            priceProposalD.setCreatedId(userId);
            priceProposalD.setAdjustStatus(0);

            String latestCostPrice = priceProposalMapper.getLatestCostPrice(adto.getClientId(), adto.getStoCode(), priceProposalD.getProSelfCode());
            latestCostPrice = StrUtil.isNotBlank(latestCostPrice) ? latestCostPrice : "0.00";
            latestCostPrice = new BigDecimal(Double.parseDouble(latestCostPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            priceProposalD.setLatestCostPrice(latestCostPrice);

            String avgSellingPrice = adto.getAvgSellingPrice();
            avgSellingPrice = StrUtil.isNotBlank(avgSellingPrice) ? avgSellingPrice : "0.00";
            avgSellingPrice = new BigDecimal(Double.parseDouble(avgSellingPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            priceProposalD.setOriginalAvgSellingPrice(avgSellingPrice);

            String suggestedRetailPriceLow = adto.getRange().split("-")[0];
            suggestedRetailPriceLow = new BigDecimal(Double.parseDouble(suggestedRetailPriceLow)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            priceProposalD.setSuggestedRetailPriceLow(suggestedRetailPriceLow);

            String suggestedRetailPriceHigh = adto.getRange().split("-")[1];
            suggestedRetailPriceHigh = new BigDecimal(Double.parseDouble(suggestedRetailPriceHigh)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            priceProposalD.setSuggestedRetailPriceHigh(suggestedRetailPriceHigh);

            String newRetailPrice = String.valueOf(adto.getProposalPrice());
            newRetailPrice = new BigDecimal(Double.parseDouble(newRetailPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            priceProposalD.setNewRetailPrice(newRetailPrice);

            priceProposalD.setProposalType(adto.getUpOrDown());
            priceProposalD.setProDesc(adto.getProDepict());
            priceProposalD.setProSpecs(adto.getProSpecs());
            priceProposalD.setProFactoryName(adto.getFactoryName());
            priceProposalD.setProUnit(adto.getProUnit());
            priceProposalD.setProCompClass(adto.getProCompClass());
            priceProposalD.setProCompClassName(adto.getProCompClassName());
            priceProposalD.setProBigClass(adto.getProBigClass());
            priceProposalD.setProBigClassName(adto.getProBigClassName());
            priceProposalD.setProClass(adto.getProClass());
            priceProposalD.setProClassName(adto.getProClassName());
            finalPricePriposalDList.add(priceProposalD);
        }
        priceProposalMapper.batchInsertPriceProposalD(finalPricePriposalDList);
        return finalPricePriposalDList;
    }

}

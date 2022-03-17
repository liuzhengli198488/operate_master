package com.gys.service;

import com.gys.entity.priceProposal.dto.*;
import com.gys.entity.priceProposal.entity.ProductPriceProposalD;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;

import java.util.List;
import java.util.Map;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议
 * @CreateTime 2022-01-18 15:25:00
 */
public interface ProductPriceDataService {

    void saveCityRangeInfo(List<ProductPriceProposalZ> cityProClassifyDTOList);

    void saveStoRangeInfo(StoDimensionDTO stoDimensionDTO, String maxNo, String range, String gap);

    void saveStoTotalBayesianProbabilityInfo(String maxNo, String range, double totalBayesianProbability, PriceProposalClientDTO priceProposalClientDTO);

    void saveRangeMatchInfo(StoDimensionDTO stoDimensionDTO, String maxNo, String range);

    List<ProductPriceProposalD> savePricePoposal(List<PriceProposalADTO> aList, String maxNo, String userId);

}

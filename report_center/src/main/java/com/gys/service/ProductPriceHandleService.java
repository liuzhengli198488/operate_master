package com.gys.service;

import com.gys.entity.priceProposal.dto.CityDimensionDTO;
import com.gys.entity.priceProposal.dto.PriceProposalADTO;
import com.gys.entity.priceProposal.dto.PriceProposalClientDTO;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;

import java.util.List;
import java.util.Map;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议数据处理
 * @CreateTime 2022-01-18 14:53:00
 */
public interface ProductPriceHandleService {

    /**
     * 处理区域市级维度数据
     * @param cityDimensionDTOList
     * @param maxNo
     * @param priceProposalClientDTO
     * @return
     */
    List<ProductPriceProposalZ> handleCityDimension(List<CityDimensionDTO> cityDimensionDTOList, String maxNo, PriceProposalClientDTO priceProposalClientDTO);

    /**
     * 处理区域门店维度数据
     * @param cityProClassifyDTOList
     * @param maxNo
     * @param priceProposalClientDTO
     * @return
     */
    List<ProductPriceProposalZ> handleStoDimension(List<ProductPriceProposalZ> cityProClassifyDTOList, String maxNo, PriceProposalClientDTO priceProposalClientDTO);

    /**
     * 汇总商品贝叶斯概率,贝叶斯概率汇总最高的价格区间为定价区间
     * @param stoProRangeDTOList
     * @return
     */
    List<ProductPriceProposalZ> summaryStoMaxBayesianProbability(List<ProductPriceProposalZ> stoProRangeDTOList, PriceProposalClientDTO client);

    /**
     *  商品定价区间随机值处理
     * @param maxList
     * @return
     */
    List<ProductPriceProposalZ> randomStoRange(List<ProductPriceProposalZ> maxList);

    /**
     *  根据客户类型匹配商品价格是否在定价区间内
     * @param randomList
     * @param clientDTO
     * @return
     */
    List<ProductPriceProposalZ> matchStoPriceByRange(List<ProductPriceProposalZ> randomList, PriceProposalClientDTO clientDTO);

    /**
     * 计算比例存储需要价格建议的商品
     * @param matchProposalProsList
     */
    List<PriceProposalADTO> getNeedSaveProposalPros(List<ProductPriceProposalZ> matchProposalProsList);

    void savePros(List<PriceProposalADTO> needSaveProsList, String maxNo, String invalidDate);

}

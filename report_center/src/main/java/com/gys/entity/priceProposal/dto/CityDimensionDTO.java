package com.gys.entity.priceProposal.dto;

import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 市级维度
 * @CreateTime 2022-01-12 10:34:00
 */
@Data
public class CityDimensionDTO {

    // 省份
    private String province;
    // 省份ID
    private String provinceId;
    // 城市
    private String city;
    // 城市ID
    private String cityId;
    // 商品Code
    private String proCode;
    // 贝叶斯概率
    private String bayesianProbability;
    // 平均售价
    private String avgSellingPrice;

}

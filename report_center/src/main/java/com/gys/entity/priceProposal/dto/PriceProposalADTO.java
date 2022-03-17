package com.gys.entity.priceProposal.dto;

import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 需要调价建议的商品 A组
 * @CreateTime 2022-01-16 23:07:00
 */
@Data
public class PriceProposalADTO {

    // 省份
    private String provinceName;
    // 省份ID
    private String provinceCode;
    // 城市
    private String cityName;
    // 城市ID
    private String cityCode;
    // 商品Code
    private String proCode;
    // 门店Code
    private String stoCode;
    // 门店名称
    private String stoName;
    // 加盟商
    private String clientName;
    // 加盟商Id
    private String clientId;
    // 平均售价
    private String avgSellingPrice;
    // 贝叶斯概率
    private double bayesianProbability;
    // 商品描述
    private String proDepict;
    // 商品规格
    private String proSpecs;
    // 生产厂家
    private String factoryName;
    // 单位
    private String proUnit;
    // 成分分类编码
    private String proCompClass;
    // 成分分类名称
    private String proCompClassName;
    // 商品一级分类编码
    private String proBigClass;
    // 商品一级分类
    private String proBigClassName;
    // 商品分类编码
    private String proClass;
    // 商品分类
    private String proClassName;
    // 建议价格范围
    private String range;
    // 涨价 / 降价 1.涨价 2.降价
    private int upOrDown;
    // 建议价格
    private double proposalPrice;
    // 差价
    private double diff;

}

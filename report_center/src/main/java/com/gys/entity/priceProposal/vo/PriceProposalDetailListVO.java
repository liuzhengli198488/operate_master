package com.gys.entity.priceProposal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 查询价格建议详情列表返回的数据
 * @CreateTime 2022-01-13 10:25:00
 */
@Data
@ApiModel("查询价格建议详情列表返回的数据")
public class PriceProposalDetailListVO {

    @ApiModelProperty("价格建议单号")
    private String priceProposalNo;

    @ApiModelProperty("加盟商ID")
    private Integer clientId;

    @ApiModelProperty("处理情况 1.已调整 0.未调整")
    private Integer adjustStatus;

    @ApiModelProperty("药德商品编码")
    private String proCode;

    @ApiModelProperty("商品自编码")
    private String proSelfCode;

    @ApiModelProperty("商品描述")
    private String proDesc;

    @ApiModelProperty("商品规格")
    private String proSpecs;

    @ApiModelProperty("生产厂家")
    private String proFactoryName;

    @ApiModelProperty("单位")
    private String proUnit;

    @ApiModelProperty("最新成本价")
    private String latestCostPrice;

    @ApiModelProperty("原平均售价")
    private String originalAvgSellingPrice;

    @ApiModelProperty("建议零售价 - 低")
    private String suggestedRetailPriceLow;

    @ApiModelProperty("建议零售价 - 高")
    private String suggestedRetailPriceHigh;

    @ApiModelProperty("建议零售价")
    private String suggestedRetailPrice;

    @ApiModelProperty("新零售价")
    private String newRetailPrice;

    @ApiModelProperty("成分分类")
    private String proCompClass;

    @ApiModelProperty("成分分类描述")
    private String proCompClassName;

    @ApiModelProperty("商品一级分类编码")
    private String proBigClass;

    @ApiModelProperty("商品一级分类描述")
    private String proBigClassName;

    @ApiModelProperty("商品分类编码")
    private String proClass;

    @ApiModelProperty("商品分类描述")
    private String proClassName;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("省份名称")
    private String provinceName;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("城市名称")
    private String cityName;

}

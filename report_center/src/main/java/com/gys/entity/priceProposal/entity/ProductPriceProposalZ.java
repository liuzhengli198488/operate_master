package com.gys.entity.priceProposal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Jinwencheng
 * @version 1.0
 * @Description 价格建议
 * @createTime 2022-01-11 13:52:00
 */
@ApiModel("价格建议中间表")
@Data
@Table(name = "GAIA_PRODUCT_PRICE_PROPOSAL_Z")
public class ProductPriceProposalZ implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("价格建议单号")
    @Column(name = "PRICE_PROPOSAL_NO")
    private String priceProposalNo;

    @ApiModelProperty("省份编码")
    @Column(name = "PROVINCE_CODE")
    private String provinceCode;

    @ApiModelProperty("省份名称")
    @Column(name = "PROVINCE_NAME")
    private String provinceName;

    @ApiModelProperty("城市编码")
    @Column(name = "CITY_CODE")
    private String cityCode;

    @ApiModelProperty("城市名称")
    @Column(name = "CITY_NAME")
    private String cityName;

    @ApiModelProperty("加盟商ID")
    @Column(name = "CLIENT_ID")
    private String clientId;

    @ApiModelProperty("加盟商")
    @Column(name = "CLIENT_NAME")
    private String clientName;

    @ApiModelProperty("门店编码")
    @Column(name = "STO_CODE")
    private String stoCode;

    @ApiModelProperty("门店名称")
    @Column(name = "STO_NAME")
    private String stoName;

    @ApiModelProperty("药德商品编码")
    @Column(name = "PRO_CODE")
    private String proCode;

    @ApiModelProperty("商品编码")
    @Column(name = "PRO_SELF_CODE")
    private String proSelfCode;

    @ApiModelProperty("平均售价")
    @Column(name = "AVG_SELLING_PRICE")
    private String avgSellingPrice;

    @ApiModelProperty("贝叶斯概率")
    @Column(name = "BAYESIAN_PROBABILITY")
    private String bayesianProbability;

    @ApiModelProperty("起始价格")
    @Column(name = "PRICE_REGION_LOW")
    private String priceRegionLow;

    @ApiModelProperty("结束价格")
    @Column(name = "PRICE_REGION_HIGH")
    private String priceRegionHigh;

    @ApiModelProperty("价格间隔")
    @Column(name = "PRICE_STEP")
    private String priceStep;

    @ApiModelProperty("创建人ID")
    @Column(name = "CREATED_ID")
    private String createdId;

    @ApiModelProperty("创建时间")
    @Column(name = "CREATED_TIME")
    private String createdTime;

    @ApiModelProperty("数据类型")
    @Column(name = "DATA_TYPE")
    private String dataType;

    @ApiModelProperty("门店信息")
    @Column(name = "STO_INFO")
    private String stoInfo;

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

}

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
@ApiModel("价格建议明细表")
@Data
@Table(name = "GAIA_PRODUCT_PRICE_PROPOSAL_D")
public class ProductPriceProposalD implements Serializable {

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

    @ApiModelProperty("商品描述")
    @Column(name = "PRO_DESC")
    private String proDesc;

    @ApiModelProperty("商品规格")
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    @ApiModelProperty("生产厂家")
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    @ApiModelProperty("单位")
    @Column(name = "PRO_UNIT")
    private String proUnit;

    @ApiModelProperty("最新成本价")
    @Column(name = "LATEST_COST_PRICE")
    private String latestCostPrice;

    @ApiModelProperty("原平均售价")
    @Column(name = "ORIGINAL_AVG_SELLING_PRICE")
    private String originalAvgSellingPrice;

    @ApiModelProperty("建议零售价 - 低")
    @Column(name = "SUGGESTED_RETAIL_PRICE_LOW")
    private String suggestedRetailPriceLow;

    @ApiModelProperty("建议零售价 - 高")
    @Column(name = "SUGGESTED_RETAIL_PRICE_HIGH")
    private String suggestedRetailPriceHigh;

    @ApiModelProperty("新零售价")
    @Column(name = "NEW_RETAIL_PRICE")
    private String newRetailPrice;

    @ApiModelProperty("价格建议类型1.涨价 2.降价")
    @Column(name = "PROPOSAL_TYPE")
    private Integer proposalType;

    @ApiModelProperty("商品分类编码")
    @Column(name = "PRO_CLASS")
    private String proClass;

    @ApiModelProperty("商品分类描述")
    @Column(name = "PRO_CLASS_NAME")
    private String proClassName;

    @ApiModelProperty("成分分类")
    @Column(name = "PRO_COMP_CLASS")
    private String proCompClass;

    @ApiModelProperty("成分分类描述")
    @Column(name = "PRO_COMP_CLASS_NAME")
    private String proCompClassName;

    @ApiModelProperty("商品一级分类编码")
    @Column(name = "PRO_BIG_CLASS")
    private String proBigClass;

    @ApiModelProperty("商品一级分类描述")
    @Column(name = "PRO_BIG_CLASS_NAME")
    private String proBigClassName;

    @ApiModelProperty("创建人ID")
    @Column(name = "CREATED_ID")
    private String createdId;

    @ApiModelProperty("创建时间")
    @Column(name = "CREATED_TIME")
    private String createdTime;

    @ApiModelProperty("调整状态1.已调整 0.未调整")
    @Column(name = "ADJUST_STATUS")
    private Integer adjustStatus;

    @ApiModelProperty("调整人ID")
    @Column(name = "USER_ID")
    private String userId;

    @ApiModelProperty("调整人")
    @Column(name = "USER_NAME")
    private String userName;

    @ApiModelProperty("调整时间")
    @Column(name = "ADJUST_TIME")
    private String adjustTime;

}

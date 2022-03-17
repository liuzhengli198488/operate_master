package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 商品品类模型 
 * </p>
 *
 * @author wangyifan
 * @since 2021-08-10
 */
@Data
@Table( name = "GAIA_COMMODITY_CATEGORY_MODEL")
public class CommodityCategoryModel implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @Id
    @Column(name = "ID")
    private Long id;

    @ApiModelProperty(value = "省份")
    @Column(name = "PROVINCE")
    private String province;

    @ApiModelProperty(value = "省份编码")
    @Column(name = "PROVINCE_ID")
    private String provinceId;

    @ApiModelProperty(value = "市名")
    @Column(name = "CITY")
    private String city;

    @ApiModelProperty(value = "市编码")
    @Column(name = "CITY_ID")
    private String cityId;

    @ApiModelProperty(value = "客户编码")
    @Column(name = "CLIENT_ID")
    private String clientId;

    @ApiModelProperty(value = "客户名")
    @Column(name = "CLIENT_NAME")
    private String clientName;

    @ApiModelProperty(value = "门店编码")
    @Column(name = "STO_CODE")
    private String stoCode;

    @ApiModelProperty(value = "门店名称")
    @Column(name = "STO_NAME")
    private String stoName;

    @ApiModelProperty(value = "店型")
    @Column(name = "STO_VERSION")
    private String stoVersion;

    @ApiModelProperty(value = "药德商品通用编码")
    @Column(name = "PRO_CODE")
    private String proCode;

    @ApiModelProperty(value = "商品描述")
    @Column(name = "PRO_DEPICT")
    private String proDepict;

    @ApiModelProperty(value = "商品规格")
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    @ApiModelProperty(value = "生产厂家")
    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @ApiModelProperty(value = "单位")
    @Column(name = "PRO_UNIT")
    private String proUnit;

    @ApiModelProperty(value = "门店数")
    @Column(name = "STO_NUM")
    private Integer stoNum;

    @ApiModelProperty(value = "动销门店数")
    @Column(name = "ICOUNT")
    private Integer icount;

    @ApiModelProperty(value = "商品动销天数")
    @Column(name = "PAY_DAYS")
    private Integer payDays;

    @ApiModelProperty(value = "总销售天数")
    @Column(name = "SALE_DAYS")
    private Integer saleDays;

    @ApiModelProperty(value = "商品交易次数")
    @Column(name = "PAY_NUMBER")
    private Integer payNumber;

    @ApiModelProperty(value = "总交易次数")
    @Column(name = "SALE_COUNT")
    private Integer saleCount;

    @ApiModelProperty(value = "销售概率")
    @Column(name = "SALE_ABOUT")
    private String saleAbout;

    @ApiModelProperty(value = "总销售额")
    @Column(name = "GSSD_AMT")
    private String gssdAmt;

    @ApiModelProperty(value = "总毛利额")
    @Column(name = "GROSS_PROFIT_AMT")
    private String grossProfitAmt;

    @ApiModelProperty(value = "单次销售额")
    @Column(name = "SINGLE_AMT")
    private String singleAmt;

    @ApiModelProperty(value = "单次毛利额")
    @Column(name = "SINGLE_GROSS_PROFIT")
    private String singleGrossProfit;

    @ApiModelProperty(value = "综合排名")
    @Column(name = "COMPREHENSIVE_RANK")
    private Integer comprehensiveRank;

    @ApiModelProperty(value = "成分分类编码")
    @Column(name = "PRO_COMP_CLASS")
    private String proCompClass;

    @ApiModelProperty(value = "成分分类名称")
    @Column(name = "PRO_COMP_CLASS_NAME")
    private String proCompClassName;

    @ApiModelProperty(value = "商品分类编码")
    @Column(name = "PRO_CLASS")
    private String proClass;

    @ApiModelProperty(value = "商品分类")
    @Column(name = "PRO_CLASS_NAME")
    private String proClassName;

    @ApiModelProperty(value = "销售成本额")
    @Column(name = "GSSDMOVPRICE")
    private String gssdmovprice;

    @ApiModelProperty(value = "销售概率排名")
    @Column(name = "SALE_ABOUT_RANK")
    private Integer saleAboutRank;

    @ApiModelProperty(value = "单次销售额排名")
    @Column(name = "SINGLE_AMT_RANK")
    private Integer singleAmtRank;

    @ApiModelProperty(value = "单次毛利额排名")
    @Column(name = "SINGLE_GROSS_PROFIT_RANK")
    private Integer singleGrossProfitRank;

    @ApiModelProperty(value = "排名之和")
    @Column(name = "RANKSUM")
    private Integer ranksum;

    @ApiModelProperty(value = "商品自编码")
    @Column(name = "GSSD_PRO_ID")
    private String gssdProId;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "COMPADM_CRE_TIME")
    private String compadmCreTime;


}

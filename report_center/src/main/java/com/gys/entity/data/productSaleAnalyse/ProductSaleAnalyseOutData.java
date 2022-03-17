package com.gys.entity.data.productSaleAnalyse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaleAnalyseOutData {
    @ApiModelProperty(value = "商品自编码")
    private String gssdProId;
    @ApiModelProperty(value = "店型")
    private String stoVersion;
    @ApiModelProperty(value = "客户编码")
    private String clientId;
    @ApiModelProperty(value = "客户名")
    private String clientName;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名")
    private String stoName;
    @ApiModelProperty(value = "省份编码")
    private String provinceId;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "市编码")
    private String cityId;
    @ApiModelProperty(value = "市名")
    private String city;
    @ApiModelProperty(value = "商品通用编码")
    private String proCode;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
    @ApiModelProperty(value = "商品规格")
    private String proSpecs;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "成分一级分类编码")
    private String proBigCompClass;
    @ApiModelProperty(value = "成分一级分类名称")
    private String proBigCompClassName;
    @ApiModelProperty(value = "成分二级分类编码")
    private String proMidCompClass;
    @ApiModelProperty(value = "成分二级分类名称")
    private String proMidCompClassName;
    @ApiModelProperty(value = "成分分类编码")
    private String proCompClass;
    @ApiModelProperty(value = "成分分类名称")
    private String proCompClassName;
    @ApiModelProperty(value = "商品一级分类编码")
    private String proBigClass;
    @ApiModelProperty(value = "商品一级分类")
    private String proBigClassName;
    @ApiModelProperty(value = "商品二级分类编码")
    private String proMidClass;
    @ApiModelProperty(value = "商品二级分类")
    private String proMidClassName;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "商品分类")
    private String proClassName;
    @ApiModelProperty(value = "总销售天数")
    private String saleDays;
    @ApiModelProperty(value = "总交易次数")
    private String saleCount;
    @ApiModelProperty(value = "总销量")
    private String saleQty;
    @ApiModelProperty(value = "商品销售天数")
    private String payDays;
    @ApiModelProperty(value = "动销门店数")
    private String icount;
    @ApiModelProperty(value = "商品交易次数")
    private String payNumber;
    @ApiModelProperty(value = "总销售额")
    private String gssdAmt;
    @ApiModelProperty(value = "销售成本额")
    private String gssdmovprice;
    @ApiModelProperty(value = "销售概率")
    private String saleAbout;
    @ApiModelProperty(value = "销售概率之和")
    private String saleAboutSum;
    @ApiModelProperty(value = "单次销售额")
    private String singleAmt;
    @ApiModelProperty(value = "单次毛利额")
    private String singleGrossProfit;
    @ApiModelProperty(value = "销售概率排名")
    private Integer saleAboutRank;
    @ApiModelProperty(value = "单次销售额排名")
    private Integer singleAmtRank;
    @ApiModelProperty(value = "单次毛利额排名")
    private Integer singleGrossProfitRank;
    @ApiModelProperty(value = "排名之和")
    private Integer rankSum;
    @ApiModelProperty(value = "综合排名")
    private Integer comprehensiveRank;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
    @ApiModelProperty(value = "门店数")
    private Integer stoNum;
    @ApiModelProperty(value = "贝叶斯概率")
    private BigDecimal bayesianProbability;
}

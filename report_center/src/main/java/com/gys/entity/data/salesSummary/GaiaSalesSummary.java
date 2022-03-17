package com.gys.entity.data.salesSummary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售管理汇总实体
 *
 * @author xiaoyuan on 2020/7/24
 */
@Data
public class GaiaSalesSummary implements Serializable {
    private static final long serialVersionUID = -3771261720088233548L;

    private Integer index;
    /**
     * 商品编码
     */
    @ApiModelProperty(value = "商品编码")
    private String proCode;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String proName;

    /**
     * 零售价
     */
    @ApiModelProperty(value = "零售价")
    private String normalPrice;

    /**
     * 厂家
     */
    @ApiModelProperty(value = "厂家")
    private String proFactoryName;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String proUnit;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    private String proSpecs;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private BigDecimal proQty;

    /**
     * 应收金额
     */
    @ApiModelProperty(value = "应收金额")
    private BigDecimal gssdnormalAmt;

    /**
     * 实收金额
     */
    @ApiModelProperty(value = "实收金额")
    private BigDecimal gssdAmt;

    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额")
    private BigDecimal discountAmt;

    /**
     * 折扣率
     */
    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;

    /**
     * 成本额
     */
    @ApiModelProperty(value = "成本额")
    private BigDecimal costAmt;

    /**
     * 毛利额
     */
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;

    /**
     * 毛利率
     */
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;

    /**
     * 库存数量
     */
    @ApiModelProperty(value = "库存数量")
    private BigDecimal gsisdQty;

    @ApiModelProperty(value = "商品状态")
    private String proStaus;

    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;


    @ApiModelProperty(value = "商品通用名称")
    private String prCommonName;
    @ApiModelProperty(value = "销售等级")
    private String proSaleClass;
    @ApiModelProperty(value = "商品自定义1")
    private String proZdy1;
    @ApiModelProperty(value = "商品自定义2")
    private String proZdy2;
    @ApiModelProperty(value = "商品自定义3")
    private String proZdy3;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;

    /**
     * 特殊属性
     */
    private String proTssx;

}

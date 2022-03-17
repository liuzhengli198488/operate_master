package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 提成方案单品提成明细表
 * </p>
 *
 * @author flynn
 * @since 2021-11-15
 */
@Data
@Table( name = "GAIA_TICHENG_PROPLAN_PRO_N")
public class TichengProplanProN implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @Id
    @Column(name = "ID")
    private Long id;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "提成方案主表主键ID")
    @Column(name = "PID")
    private Long pid;

    @ApiModelProperty(value = "每个提成设置的id，可以理解为子方案的id")
    @Column(name = "SETTING_ID")
    private Integer settingId;

    @ApiModelProperty(value = "单品提成方式 0 不参与销售提成 1 参与销售提成")
    @Column(name = "PLAN_PRODUCT_WAY")
    private String planProductWay;

    @ApiModelProperty(value = "提成方式 1 按零售额提成 2 按销售额提成  3按毛利率提成")
    @Column(name = "PLIANT_PERCENTAGE_TYPE")
    private String pliantPercentageType;

    @ApiModelProperty(value = "商品编码")
    @Column(name = "PRO_CODE")
    private String proCode;

    @ApiModelProperty(value = "商品名")
    @Column(name = "PRO_NAME")
    private String proName;

    @ApiModelProperty(value = "规格")
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    @ApiModelProperty(value = "生产企业")
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    @ApiModelProperty(value = "成本价")
    @Column(name = "PRO_COST_PRICE")
    private BigDecimal proCostPrice;

    @ApiModelProperty(value = "零售价")
    @Column(name = "PRO_PRICE_NORMAL")
    private BigDecimal proPriceNormal;

    @ApiModelProperty(value = "提成级别 1 一级 2 二级 3 三级")
    @Column(name = "TICHENG_LEVEL")
    private String tichengLevel;

    @ApiModelProperty(value = "达成数量1")
    @Column(name = "SALE_QTY")
    private BigDecimal saleQty;

    @ApiModelProperty(value = "提成金额1")
    @Column(name = "TICHENG_AMT")
    private BigDecimal tichengAmt;

    @ApiModelProperty(value = "提成比例1（只填数字省略百分号）")
    @Column(name = "TICHENG_RATE")
    private BigDecimal tichengRate;

    @ApiModelProperty(value = "达成数量2")
    @Column(name = "SALE_QTY2")
    private BigDecimal saleQty2;

    @ApiModelProperty(value = "提成金额2")
    @Column(name = "TICHENG_AMT2")
    private BigDecimal tichengAmt2;

    @ApiModelProperty(value = "提成比例2（只填数字省略百分号）")
    @Column(name = "TICHENG_RATE2")
    private BigDecimal tichengRate2;

    @ApiModelProperty(value = "达成数量3")
    @Column(name = "SALE_QTY3")
    private BigDecimal saleQty3;

    @ApiModelProperty(value = "提成金额3")
    @Column(name = "TICHENG_AMT3")
    private BigDecimal tichengAmt3;

    @ApiModelProperty(value = "提成比例3（只填数字省略百分号）")
    @Column(name = "TICHENG_RATE3")
    private BigDecimal tichengRate3;

    @ApiModelProperty(value = "操作状态 0 未删除 1 已删除")
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    @ApiModelProperty(value = "剔除折扣率 操作符号 =、＞、＞＝、＜、＜＝")
    @Column(name = "PLAN_REJECT_DISCOUNT_RATE_SYMBOL")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 数值")
    @Column(name = "PLAN_REJECT_DISCOUNT_RATE")
    private String planRejectDiscountRate;


}

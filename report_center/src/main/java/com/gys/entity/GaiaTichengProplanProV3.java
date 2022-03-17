package com.gys.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "GAIA_TICHENG_PROPLAN_PRO_N")
@Data
public class GaiaTichengProplanProV3 {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 提成方案主表主键ID
     */
    @Column(name = "PID")
    private Long pid;

    /**
     * 单品提成方式 0 不参与销售提成 1 参与销售提成
     */
    @Column(name = "PLAN_PRODUCT_WAY")
    private String planProductWay;

    /**
     * 提成方式 1 按零售额提成 2 按销售额提成 3 按毛利额提成
     */
    @Column(name = "PLIANT_PERCENTAGE_TYPE")
    private String planPercentageType;

    /**
     * 商品编码
     */
    @Column(name = "PRO_CODE")
    private String proCode;

    /**
     * 商品名
     */
    @Column(name = "PRO_NAME")
    private String proName;

    /**
     * 规格
     */
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    /**
     * 生产企业
     */
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    /**
     * 成本价
     */
    @Column(name = "PRO_COST_PRICE")
    private BigDecimal proCostPrice;

    /**
     * 零售价
     */
    @Column(name = "PRO_PRICE_NORMAL")
    private BigDecimal proPriceNormal;

    /**
     *提成等级
     */
    @Column(name = "TICHENG_LEVEL")
    private String tichengLevel;

    /**
     * 达成数量1
     */
    @Column(name = "SALE_QTY")
    private BigDecimal saleQty;

    /**
     * 提成金额1
     */
    @Column(name = "TICHENG_AMT")
    private BigDecimal tichengAmt;

    /**
     * 提成比例1
     */
    @Column(name = "TICHENG_RATE")
    private BigDecimal tichengRate;

    /**
     * 达成数量2
     */
    @Column(name = "SALE_QTY2")
    private BigDecimal saleQty2;

    /**
     * 提成金额2
     */
    @Column(name = "TICHENG_AMT2")
    private BigDecimal tichengAmt2;

    /**
     * 提成比例2
     */
    @Column(name = "TICHENG_RATE2")
    private BigDecimal tichengRate2;

    /**
     * 达成数量3
     */
    @Column(name = "SALE_QTY3")
    private BigDecimal saleQty3;

    /**
     * 提成金额3
     */
    @Column(name = "TICHENG_AMT3")
    private BigDecimal tichengAmt3;

    /**
     * 提成比例3
     */
    @Column(name = "TICHENG_RATE3")
    private BigDecimal tichengRate3;

    /**
     * 操作状态 0 未删除 1 已删除
     */
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;
}

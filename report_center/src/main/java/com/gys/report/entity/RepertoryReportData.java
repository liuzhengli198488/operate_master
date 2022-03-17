package com.gys.report.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RepertoryReportData {

    // 大类代码
    private String bigClassCode;
    // 大类名称
    private String bigClassName;
    // 中类代码
    private String midClassCode;
    // 中类名称
    private String midClassName;
    // 商品分类代码
    private String proClassCode;
    // 商品分类名称
    private String proClassName;
    // 定位
    private String proPosition;


	// 库存品项_总库
    private BigDecimal inventoryItem_total;
    // 库存成本额_总库
    private BigDecimal costAmount_total;
    // 周转天数_总库
    private String turnoverDays_total;

    // 库存品项_仓库
    private BigDecimal inventoryItem_dep;
    // 库存成本额_仓库
    private BigDecimal costAmount_dep;
    // 周转天数_仓库
    private String turnoverDays_dep;

    // 库存品项_门店
    private BigDecimal inventoryItem_store;
    // 库存成本额_门店
    private BigDecimal costAmount_store;
    // 周转天数_门店
    private String turnoverDays_store;

    // 库存品项_总库效期
    private BigDecimal inventoryItem_totalExpiry;
    // 库存成本额_总库效期
    private BigDecimal costAmount_totalExpiry;
    // 周转天数_总库效期
    private String turnoverDays_totalExpiry;

    // 库存品项_仓库效期
    private BigDecimal inventoryItem_depExpiry;
    // 库存成本额_仓库效期
    private BigDecimal costAmount_depExpiry;
    // 周转天数_仓库效期
    private String turnoverDays_depExpiry;

    // 库存品项_门店效期
    private BigDecimal inventoryItem_storeExpiry;
    // 库存成本额_门店效期
    private BigDecimal costAmount_storeExpiry;
    // 周转天数_门店效期
    private String turnoverDays_storeExpiry;

    // 销售成本
    private BigDecimal costOfRevenues;
}

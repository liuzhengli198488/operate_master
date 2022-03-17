package com.gys.entity.data.commissionplan;

import com.gys.annotation.CommissionReportField;
import com.gys.util.BigDecimalUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * @author wu mao yin
 * @Title: 门店提成汇总
 * @date 2021/11/2214:28
 */
@Data
public class StoreCommissionSummary implements Serializable {

    @ApiModelProperty("加盟商")
    private String client;

    @ApiModelProperty("方案id")
    private Integer planId;

    @CommissionReportField(fieldName = "方案名称", isTotalItem = true)
    @ApiModelProperty("方案名称")
    private String planName;

    @CommissionReportField(fieldName = "起始时间")
    @ApiModelProperty("起始时间")
    private String planStartDate;

    @CommissionReportField(fieldName = "结束时间")
    @ApiModelProperty("结束时间")
    private String planEndDate;

    @ApiModelProperty("提成类型")
    private Integer planType;

    @CommissionReportField(fieldName = "提成类型")
    @ApiModelProperty("提成类型名称")
    private String planTypeName;

    @ApiModelProperty("子方案id")
    private String subPlanId;

    @CommissionReportField(fieldName = "子方案名称", showSubPlan = true)
    @ApiModelProperty("子方案名称")
    private String subPlanName;

    @CommissionReportField(fieldName = "营业员工号", type = 2)
    @ApiModelProperty("营业员工号")
    private String saleManCode;

    @CommissionReportField(fieldName = "营业员姓名", type = 2)
    @ApiModelProperty("营业员姓名")
    private String saleManName;

    @CommissionReportField(fieldName = "门店编码", showStore = true)
    @ApiModelProperty("门店编码")
    private String stoCode;

    @CommissionReportField(fieldName = "门店名称", showStore = true)
    @ApiModelProperty("门店名称")
    private String stoName;

    @CommissionReportField(fieldName = "销售天数", isTotalItem = true)
    @ApiModelProperty("销售天数")
    private Integer saleDays;

    @CommissionReportField(fieldName = "销售日期", showSaleDate = true)
    @ApiModelProperty("销售日期")
    private LocalDate saleDate;

    private String saleDateStr;

    @CommissionReportField(fieldName = "成本额", isTotalItem = true)
    @ApiModelProperty("成本额")
    private BigDecimal costAmt;

    @CommissionReportField(fieldName = "应收金额", isTotalItem = true)
    @ApiModelProperty("应收金额")
    private BigDecimal ysAmt;

    @CommissionReportField(fieldName = "实收金额", isTotalItem = true)
    @ApiModelProperty("实收金额")
    private BigDecimal amt;

    @CommissionReportField(fieldName = "毛利额", isTotalItem = true)
    @ApiModelProperty("毛利额")
    private BigDecimal grossProfitAmt;

    @CommissionReportField(fieldName = "毛利率", suffix = "%", isTotalItem = true)
    @ApiModelProperty("毛利率")
    private BigDecimal grossProfitRate;

    @CommissionReportField(fieldName = "折扣金额", isTotalItem = true)
    @ApiModelProperty("折扣金额")
    private BigDecimal zkAmt;

    @CommissionReportField(fieldName = "折扣率", suffix = "%", isTotalItem = true)
    @ApiModelProperty("折扣率")
    private BigDecimal zkl;

    @CommissionReportField(fieldName = "提成商品成本额", isTotalItem = true)
    @ApiModelProperty("提成商品成本额")
    private BigDecimal commissionCostAmt;

    @CommissionReportField(fieldName = "提成商品销售额", isTotalItem = true)
    @ApiModelProperty("提成商品销售额")
    private BigDecimal commissionSales;

    @CommissionReportField(fieldName = "提成商品毛利额", isTotalItem = true)
    @ApiModelProperty("提成商品毛利额")
    private BigDecimal commissionGrossProfitAmt;

    @CommissionReportField(fieldName = "提成商品毛利率", isTotalItem = true, suffix = "%")
    @ApiModelProperty("提成商品毛利率")
    private BigDecimal commissionGrossProfitRate;

    @CommissionReportField(fieldName = "提成金额", isTotalItem = true)
    @ApiModelProperty("提成金额")
    private BigDecimal commissionAmt;

    @CommissionReportField(fieldName = "提成销售比", isTotalItem = true, suffix = "%")
    @ApiModelProperty("提成销售比")
    private BigDecimal commissionSalesRatio;

    @CommissionReportField(fieldName = "提成毛利比", isTotalItem = true, suffix = "%")
    @ApiModelProperty("提成毛利比")
    private BigDecimal commissionGrossProfitRatio;

    @ApiModelProperty(value = "门店分配比例", hidden = true)
    private String planScaleSto;

    @ApiModelProperty(value = "销售员分配比例", hidden = true)
    private String planScaleSaler;

    @ApiModelProperty(value = "提成方式: 0 按销售额提成 1 按毛利额提成", hidden = true)
    private String planAmtWay;

    @ApiModelProperty(value = "提成方式:0 按商品毛利率  1 按销售毛利率", hidden = true)
    private String planRateWay;

    @ApiModelProperty(value = "剔除折扣率 操作符号 =、＞、＞＝、＜、＜＝", hidden = true)
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 数值", hidden = true)
    private String planRejectDiscountRate;

    @ApiModelProperty(value = "负毛利率商品是否不参与销售提成 0 是 1 否", hidden = true)
    private String planIfNegative;

    @ApiModelProperty(value = "审核状态 0 未审核 1 已审核 2 已停用", hidden = true)
    private String planStatus;

    @ApiModelProperty(value = "商品编码", hidden = true)
    private String proId;

    @ApiModelProperty(value = "日均销售额", hidden = true)
    private String amtAvg;

    @ApiModelProperty(value = "日均销售额-天数", hidden = true)
    private String klDates;

    @ApiModelProperty(hidden = true)
    private String qyt;

    @ApiModelProperty(value = "是否剔除的商品", hidden = true)
    private Boolean weedOut;

    @ApiModelProperty(value = "子方案id", hidden = true)
    private Integer settingId;

    @ApiModelProperty(value = "商品分类", hidden = true)
    private String proClassCode;

    @ApiModelProperty("合计销售天数")
    private Set<String> totalSaleDays;

    @ApiModelProperty(value = "是否不显示成本， true是，false否")
    private Boolean notShowAmt;
    
    @ApiModelProperty( hidden = true)
    private Boolean realTime = false;

    public StoreCommissionSummary summary(StoreCommissionSummary storeCommissionSummary) {
        this.costAmt = BigDecimalUtil.add(this.costAmt, storeCommissionSummary.getCostAmt());
        this.ysAmt = BigDecimalUtil.add(this.ysAmt, storeCommissionSummary.getYsAmt());
        this.amt = BigDecimalUtil.add(this.amt, storeCommissionSummary.getAmt());
        this.grossProfitAmt = BigDecimalUtil.add(this.grossProfitAmt, storeCommissionSummary.getGrossProfitAmt());
        this.zkAmt = BigDecimalUtil.add(this.zkAmt, storeCommissionSummary.getZkAmt());
        this.commissionAmt = BigDecimalUtil.add(this.commissionAmt, storeCommissionSummary.getCommissionAmt());
        this.commissionCostAmt = BigDecimalUtil.add(this.commissionCostAmt, storeCommissionSummary.getCommissionCostAmt());
        this.commissionSales = BigDecimalUtil.add(this.commissionSales, storeCommissionSummary.getCommissionSales());
        this.commissionGrossProfitAmt = BigDecimalUtil.add(this.commissionGrossProfitAmt, storeCommissionSummary.getCommissionGrossProfitAmt());
        return this;
    }

}

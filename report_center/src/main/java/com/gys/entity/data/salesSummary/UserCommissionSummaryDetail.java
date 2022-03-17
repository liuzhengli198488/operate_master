package com.gys.entity.data.salesSummary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户提成明细
 * </p>
 *
 * @author yifan.wang
 * @since 2022-02-11
 */
@Data
@Table(name = "GAIA_USER_COMMISSION_SUMMARY_DETAIL")
public class UserCommissionSummaryDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @Id
    @Column(name = "ID")
    private Long id;

    @ApiModelProperty(value = "加盟商")
    @Column(name = "CLIENT")
    private String client;

    @ApiModelProperty(value = "方案id")
    @Column(name = "PLAN_ID")
    private String planId;

    @ApiModelProperty(value = "方案名称")
    @Column(name = "PLAN_NAME")
    private String planName;

    @ApiModelProperty(value = "子方案id")
    @Column(name = "SUB_PLAN_ID")
    private String subPlanId;

    @ApiModelProperty(value = "子方案名称")
    @Column(name = "SUB_PLAN_NAME")
    private String subPlanName;

    @ApiModelProperty(value = "方案开始时间")
    @Column(name = "PLAN_START_DATE")
    private String planStartDate;

    @ApiModelProperty(value = "方案结束时间")
    @Column(name = "PLAN_END_DATE")
    private String planEndDate;

    @ApiModelProperty(value = "提成类型（1：销售提成，2：单品提成）")
    @Column(name = "COMMISSION_TYPE")
    private Integer commissionType;

    @ApiModelProperty(value = "营业员工号")
    @Column(name = "SALER_ID")
    private String salerId;

    @ApiModelProperty(value = "营业员名称")
    @Column(name = "SALER_NAME")
    private String salerName;

    @ApiModelProperty(value = "门店编码")
    @Column(name = "STO_CODE")
    private String stoCode;

    @ApiModelProperty(value = "门店名称")
    @Column(name = "STO_NAME")
    private String stoName;

    @ApiModelProperty(value = "销售日期")
    @Column(name = "SALE_DATE")
    private String saleDate;

    @ApiModelProperty(value = "销售单号")
    @Column(name = "BILL_NO")
    private String billNo;

    @ApiModelProperty(value = "商品编码")
    @Column(name = "PRO_ID")
    private String proId;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "PRO_NAME")
    private String proName;

    @ApiModelProperty(value = "通用名称")
    @Column(name = "PRO_COMMONNAME")
    private String proCommonname;

    @ApiModelProperty(value = "规格")
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    @ApiModelProperty(value = "量单位")
    @Column(name = "PRO_UNIT")
    private String proUnit;

    @ApiModelProperty(value = "生产企业代码")
    @Column(name = "PRO_FACTORY_CODE")
    private String proFactoryCode;

    @ApiModelProperty(value = "生产企业")
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    @ApiModelProperty(value = "商品批号")
    @Column(name = "BATCH_NO")
    private String batchNo;

    @ApiModelProperty(value = "商品有效期")
    @Column(name = "VALID_DATE")
    private String validDate;

    @ApiModelProperty(value = "数量")
    @Column(name = "QTY")
    private String qty;

    @ApiModelProperty(value = "参考零售价")
    @Column(name = "PRO_LSJ")
    private BigDecimal proLsj;

    @ApiModelProperty(value = "成本额")
    @Column(name = "COST_AMT")
    private BigDecimal costAmt;

    @ApiModelProperty(value = "应收金额")
    @Column(name = "YS_AMT")
    private BigDecimal ysAmt;

    @ApiModelProperty(value = "实收金额")
    @Column(name = "AMT")
    private BigDecimal amt;

    @ApiModelProperty(value = "毛利额")
    @Column(name = "GROSS_PROFIT_AMT")
    private BigDecimal grossProfitAmt;

    @ApiModelProperty(value = "毛利率")
    @Column(name = "GROSS_PROFIT_RATE")
    private BigDecimal grossProfitRate;

    @ApiModelProperty(value = "折扣金额")
    @Column(name = "ZK_AMT")
    private BigDecimal zkAmt;

    @ApiModelProperty(value = "折扣率")
    @Column(name = "ZK_RATE")
    private BigDecimal zkRate;

    @ApiModelProperty(value = "提成金额")
    @Column(name = "COMMISSION_AMT")
    private BigDecimal commissionAmt;

    @ApiModelProperty(value = "提成销售占比")
    @Column(name = "COMMISSION_SALES_RATIO")
    private BigDecimal commissionSalesRatio;

    @ApiModelProperty(value = "提成毛利占比")
    @Column(name = "COMMISSION_GROSS_PROFIT_RATIO")
    private BigDecimal commissionGrossProfitRatio;

    @ApiModelProperty(value = "收银工号")
    @Column(name = "EMP_ID")
    private String empId;

    @ApiModelProperty(value = "收银员姓名")
    @Column(name = "EMP_NAME")
    private String empName;

    @ApiModelProperty(value = "医生编号")
    @Column(name = "DOCTOR_ID")
    private String doctorId;

    @ApiModelProperty(value = "医生姓名")
    @Column(name = "DOCTOR_NAME")
    private String doctorName;

    @ApiModelProperty(value = "商品分类")
    @Column(name = "PRO_CLASS")
    private String proClass;

    @ApiModelProperty(value = "商品分类名称")
    @Column(name = "PRO_CLASS_NAME")
    private String proClassName;

    @ApiModelProperty(value = "商品大类")
    @Column(name = "PRO_BIG_CLASS_NAME")
    private String proBigClassName;

    @ApiModelProperty(value = "商品中类")
    @Column(name = "PRO_MID_CLASS_NAME")
    private String proMidClassName;

    @ApiModelProperty(value = "销售级别")
    @Column(name = "PRO_SLAE_CLASS")
    private String proSlaeClass;

    @ApiModelProperty(value = "商品定位")
    @Column(name = "PRO_POSITION")
    private String proPosition;

    @ApiModelProperty(value = "业务员编码")
    @Column(name = "BAT_SUPPLIER_SALESMAN")
    private String batSupplierSalesman;

    @ApiModelProperty(value = "供应商编码")
    @Column(name = "BAT_SUPPLIER_CODE")
    private String batSupplierCode;

    @ApiModelProperty(value = "供应商名称")
    @Column(name = "SUP_NAME")
    private String supName;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;


}

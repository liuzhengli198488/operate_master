package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ：gyx
 * @Date ：Created in 15:32 2021/12/1
 * @Description：批发销售明细导出Vo
 * @Modified By：gyx
 * @Version:
 */
@Data
public class WholesaleSaleDetailExportOutData {

    @ApiModelProperty(value = "业务单据号")
    @ExcelProperty(value = "业务单据号",index = 0)
    private String dnId;

    @ApiModelProperty(value = "业务名称")
    @ExcelProperty(value = "业务名称",index = 1)
    private String matName;

    @ApiModelProperty(value = "发生时间")
    @ExcelProperty(value = "发生时间",index = 2)
    private String postDate;

    @ApiModelProperty(value = "商品编码")
    @ExcelProperty(value = "商品编码",index = 3)
    private String proCode;

    @ApiModelProperty(value = "批号")
    @ExcelProperty(value = "批号",index = 4)
    private String batBatchNo;

    @ApiModelProperty(value = "采购单价")
    @ExcelProperty(value = "采购单价",index = 5)
    private BigDecimal batPoPrice;

    @ApiModelProperty(value = "商品名称")
    @ExcelProperty(value = "商品名称",index = 6)
    private String proName;

    @ApiModelProperty(value = "商品通用名称")
    @ExcelProperty(value = "商品通用名称",index = 7)
    private String proCommonName;

    @ApiModelProperty(value = "规格")
    @ExcelProperty(value = "规格",index = 8)
    private String specs;

    @ApiModelProperty(value = "单位")
    @ExcelProperty(value = "单位",index = 9)
    private String unit;

    @ApiModelProperty(value = "生产厂家")
    @ExcelProperty(value = "生产厂家",index = 10)
    private String factoryName;

    @ApiModelProperty(value = "供应商编码")
    @ExcelProperty(value = "供应商编码",index = 11)
    private String supplierCode;

    @ApiModelProperty(value = "供应商名称")
    @ExcelProperty(value = "供应商名称",index = 12)
    private String supplierName;

    @ApiModelProperty(value = "去税成本额")
    @ExcelProperty(value = "去税成本额",index = 13)
    private BigDecimal movAmt;

    @ApiModelProperty(value = "去税税金")
    @ExcelProperty(value = "去税税金",index = 14)
    private BigDecimal rateMov;

    @ApiModelProperty(value = "移动含税成本额")
    @ExcelProperty(value = "移动含税成本额",index = 15)
    private BigDecimal movTotalAmount;

    @ApiModelProperty(value = " 数量")
    @ExcelProperty(value = "数量",index = 16)
    private BigDecimal qty;

    @ApiModelProperty(value = "税率值")
    @ExcelProperty(value = "税率值",index = 17)
    private String rate;

    @ApiModelProperty(value = "客户编码")
    @ExcelProperty(value = "客户编码",index = 18)
    private String stoCode;

    @ApiModelProperty(value = "客户名称")
    @ExcelProperty(value = "客户名称",index = 19)
    private String stoName;

    @ApiModelProperty(value = "调拨去税金额")
    @ExcelProperty(value = "调拨去税金额",index = 20)
    private BigDecimal addAmt;

    @ApiModelProperty(value = "调拨税金")
    @ExcelProperty(value = "调拨税金",index = 21)
    private BigDecimal addTax;

    @ApiModelProperty(value = "调拨金额")
    @ExcelProperty(value = "调拨金额",index = 22)
    private BigDecimal addtotalAmount;

    @ApiModelProperty(value = " 单价")
    @ExcelProperty(value = "调拨单价",index = 23)
    private BigDecimal addAmtRate;

    @ApiModelProperty(value = "发生地点")
    @ExcelProperty(value = "发生地点",index = 24)
    private String siteCode;

    @ApiModelProperty(value = "发生地点")
    @ExcelProperty(value = "发生地点名称",index = 25)
    private String siteName;

    //税务分类编码
    @ExcelProperty(value = "税务分类编码",index = 26)
    private String taxClassCode;

    //销售员
    @ExcelProperty(value = "销售员",index = 27)
    private String gwoOwnerSaleMan;

    @ExcelProperty(value = "抬头备注",index = 28)
    private String soHeaderRemark;
    // 大类名称
    @ExcelProperty(value = "大类",index = 29)
    private String bigClassName;
    // 中类名称
    @ExcelProperty(value = "中类",index = 30)
    private String midClassName;
    // 商品分类名称
    @ExcelProperty(value = "小类",index = 31)
    private String proClassName;
    @ExcelProperty(value = "开单员",index = 32)
    private String buillingStaffName;

}

package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangdong
 * @date 2021/7/22 15:10
 */
@Data
public class WebOrderDetailDataDto {

    @ExcelProperty(value = "序号",index = 0)
    @ApiModelProperty("序号")
    private String index;
    @ExcelProperty(value = "平台单号",index = 1)
    @ApiModelProperty("平台单号")
    private String platformOrderId;
    @ExcelProperty(value = "本地单号",index = 2)
    @ApiModelProperty("本地单号")
    private String orderId;
    @ExcelProperty(value = "销售单号",index = 3)
    @ApiModelProperty("销售单号")
    private String saleOrderId;
    @ExcelProperty(value = "商品编码",index = 4)
    @ApiModelProperty("商品编码")
    private String proId;
    @ExcelProperty(value = "通用名",index = 5)
    @ApiModelProperty("通用名")
    private String proCommonName;
    @ExcelProperty(value = "规格",index = 6)
    @ApiModelProperty("规格")
    private String proSpec;
    @ExcelProperty(value = "单位",index = 7)
    @ApiModelProperty("单位")
    private String unit;
    @ExcelProperty(value = "厂家",index = 8)
    @ApiModelProperty("厂家")
    private String factoryName;
    @ExcelProperty(value = "产地",index = 9)
    @ApiModelProperty("产地")
    private String place;
    @ExcelProperty(value = "批号",index = 10)
    @ApiModelProperty("批号")
    private String batchNo;
    @ExcelProperty(value = "效期",index = 11)
    @ApiModelProperty("效期")
    private String validateDate;
    @ExcelProperty(value = "数量",index = 12)
    @ApiModelProperty("数量")
    private String num;
//    @ExcelProperty(value = "平台零售价",index = 13)
//    @ApiModelProperty("平台零售价")
//    private String platformOriginalPrice;
//    @ExcelProperty(value = "平台实收金额",index = 14)
//    @ApiModelProperty("平台实收金额")
//    private String platformActualPrice;
//    @ExcelProperty(value = "平台折扣金额",index = 15)
//    @ApiModelProperty("平台折扣金额")
//    private String platformZkPrice;
    @ExcelProperty(value = "平台单价",index = 13)
    @ApiModelProperty("平台单价")
    private BigDecimal platformSinglePrice;
    @ExcelProperty(value = "销售零售价",index = 14)
    @ApiModelProperty("销售零售价")
    private BigDecimal saleTotalAmt;
    @ExcelProperty(value = "销售实收金额",index = 15)
    @ApiModelProperty("销售实收金额")
    private BigDecimal saleActualPrice;
    @ExcelProperty(value = "销售折扣金额",index = 16)
    @ApiModelProperty("销售折扣金额")
    private BigDecimal saleZkPrice;
    @ExcelProperty(value = "销售单价",index = 17)
    @ApiModelProperty("销售单价")
    private BigDecimal saleSinglePrice;


    @ExcelIgnore
    @ApiModelProperty("订单平台")
    private String platform;
    @ExcelIgnore
    @ApiModelProperty("订单创建日期")
    private String createDate;



}

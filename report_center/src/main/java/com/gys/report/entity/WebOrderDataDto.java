package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangdong
 * @date 2021/7/21 15:37
 */
@Data
public class WebOrderDataDto {

    @ExcelProperty(value = "序号",index = 0)
    @ApiModelProperty("序号")
    private String index;
    @ExcelProperty(value = "门店编码",index = 1)
    @ApiModelProperty("门店编码")
    private String stoCode;
    @ExcelProperty(value = "门店简称",index = 2)
    @ApiModelProperty("门店简称")
    private String stoName;
    @ExcelProperty(value = "订单平台",index = 3)
    @ApiModelProperty("订单平台")
    private String platform;
    @ExcelProperty(value = "平台单号",index = 4)
    @ApiModelProperty("平台单号")
    private String platformOrderId;
    @ExcelProperty(value = "本地单号",index = 5)
    @ApiModelProperty("本地单号")
    private String orderId;
    @ExcelProperty(value = "订单创建日期",index = 6)
    @ApiModelProperty("订单创建日期")
    private String createDate;
    @ExcelProperty(value = "订单完成日期",index = 7)
    @ApiModelProperty("订单完成日期")
    private String finishDate;
    @ExcelProperty(value = "订单状态",index = 8)
    @ApiModelProperty("订单状态")
    private String status;
    @ExcelProperty(value = "平台零售金额",index = 9)
    @ApiModelProperty("平台零售金额")
    private String platformOriginalPrice;
    @ExcelProperty(value = "平台折扣金额",index = 10)
    @ApiModelProperty("平台折扣金额")
    private BigDecimal platformZkPrice;
    @ExcelProperty(value = "平台配送费",index = 11)
    @ApiModelProperty("平台配送费")
    private String platformShippingFee;
    @ExcelProperty(value = "平台使用费",index = 12)
    @ApiModelProperty("平台使用费")
    private BigDecimal platformUseFee;
    @ExcelProperty(value = "平台实收金额",index = 13)
    @ApiModelProperty("平台实收金额")
    private BigDecimal platformActualPrice;
    @ExcelProperty(value = "顾客实付金额",index = 14)
    @ApiModelProperty("顾客实付金额")
    private BigDecimal customerPay;
    @ExcelProperty(value = "销售单号",index = 15)
    @ApiModelProperty("销售单号")
    private String saleOrderId;
    @ExcelProperty(value = "销售日期",index = 16)
    @ApiModelProperty("销售日期")
    private String saleDate;
    @ExcelProperty(value = "销售零售总额",index = 17)
    @ApiModelProperty("销售零售总额")
    private BigDecimal saleTotalAmt;
    @ExcelProperty(value = "销售实收金额",index = 18)
    @ApiModelProperty("销售实收金额")
    private BigDecimal saleActualPrice;
    @ExcelProperty(value = "销售折扣金额",index = 19)
    @ApiModelProperty("销售折扣金额")
    private BigDecimal saleZkPrice;


}

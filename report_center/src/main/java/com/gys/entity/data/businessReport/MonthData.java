package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员销售
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class MonthData {
    @ApiModelProperty(value = "本月会员销售额")
    private BigDecimal salesAmt;
    @ApiModelProperty(value = "日期")
    private String date;
    @ApiModelProperty(value = "交易次数")
    private BigDecimal transactionQty;
    @ApiModelProperty(value = "毛利")
    private BigDecimal gross;
    @ApiModelProperty(value = "毛利率")
    private BigDecimal gRate;
    @ApiModelProperty(value = "单点销售额")
    private BigDecimal singleSalesAmt;
    @ApiModelProperty(value = "单点销售次数")
    private BigDecimal singleTransactionQty;
    @ApiModelProperty(value = "客单价")
    private BigDecimal price;
    @ApiModelProperty(value = "销售天数")
    private BigDecimal salesDay;
    private BigDecimal stoQty;
    @ApiModelProperty(value = "会员销售占比")
    private BigDecimal vipRate;
    @ApiModelProperty(value = "会员交易占比")
    private BigDecimal transactionQtyRate;
    @ApiModelProperty(value = "医保销售占比")
    private BigDecimal medicalRate;

}

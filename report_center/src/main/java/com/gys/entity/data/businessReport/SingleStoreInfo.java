package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 单店日均
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class SingleStoreInfo {
    @ApiModelProperty(value = "本月销售额")
    private BigDecimal salesAmt;
    @ApiModelProperty(value = "上月销售额")
    private BigDecimal lastSalesAmt;
    @ApiModelProperty(value = "上月销售额环比")
    private BigDecimal lastSalesAmtRate;
    @ApiModelProperty(value = "同期销售额")
    private BigDecimal lastYearSalesAmt;
    @ApiModelProperty(value = "同期销售额同比")
    private BigDecimal lastYearSalesAmtRate;
    @ApiModelProperty(value = "本月交易次数")
    private BigDecimal transactionQty;
    @ApiModelProperty(value = "上月交易次数")
    private BigDecimal lastTransactionQty;
    @ApiModelProperty(value = "上月交易次数环比")
    private BigDecimal lastTransactionQtyRate;
    @ApiModelProperty(value = "同期交易次数")
    private BigDecimal lastYearTransactionQty;
    @ApiModelProperty(value = "同期交易次数同比")
    private BigDecimal lastYearTransactionQtyRate;
    @ApiModelProperty(value = "本月客单价")
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "上月客单价")
    private BigDecimal lastUnitPrice;
    @ApiModelProperty(value = "上月客单价环比")
    private BigDecimal lastUnitPriceRate;
    @ApiModelProperty(value = "同期客单价")
    private BigDecimal lastYearUnitPrice;
    @ApiModelProperty(value = "同期客单价同比")
    private BigDecimal lastYearUnitPriceRate;

}

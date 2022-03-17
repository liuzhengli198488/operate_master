package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 整体销售
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class OverallSales {
    @ApiModelProperty(value = "本月销售额")
    private BigDecimal salesAmt;
    @ApiModelProperty(value = "本月销售额达成率")
    private BigDecimal salesAmtRate;
    @ApiModelProperty(value = "上月销售额")
    private BigDecimal lastSalesAmt;
    @ApiModelProperty(value = "上月销售额环比")
    private BigDecimal lastSalesAmtRate;
    @ApiModelProperty(value = "同期销售额")
    private BigDecimal lastYearSalesAmt;
    @ApiModelProperty(value = "同期销售额同比")
    private BigDecimal lastYearSalesAmtRate;
    @ApiModelProperty(value = "本月毛利")
    private BigDecimal gross;
    @ApiModelProperty(value = "本月毛利达成率")
    private BigDecimal grossRate;
    @ApiModelProperty(value = "上月毛利")
    private BigDecimal lastGross;
    @ApiModelProperty(value = "上月毛利环比")
    private BigDecimal lastGrossRate;
    @ApiModelProperty(value = "同期毛利")
    private BigDecimal lastYearGross;
    @ApiModelProperty(value = "同期毛利同比")
    private BigDecimal lastYearGrossRate;
    @ApiModelProperty(value = "本月毛利率")
    private BigDecimal gRate;
    @ApiModelProperty(value = "本月毛利率达成率")
    private BigDecimal gRateR;
    @ApiModelProperty(value = "上月毛利率")
    private BigDecimal lastGRate;
    @ApiModelProperty(value = "上月毛利率环比")
    private BigDecimal lastGRateR;
    @ApiModelProperty(value = "同期毛利率")
    private BigDecimal lastYearGRate;
    @ApiModelProperty(value = "同期毛利率同比")
    private BigDecimal lastYearGRateR;
    @ApiModelProperty(value = "交易次数")
    private BigDecimal transactionQty;
    private BigDecimal salesDay;

}

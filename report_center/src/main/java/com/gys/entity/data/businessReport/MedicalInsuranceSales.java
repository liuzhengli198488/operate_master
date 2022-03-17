package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 医保销售
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class MedicalInsuranceSales {
    @ApiModelProperty(value = "本月医保销售额")
    private BigDecimal salesAmt;
    @ApiModelProperty(value = "上月医保销售额")
    private BigDecimal lastSalesAmt;
    @ApiModelProperty(value = "上月医保销售额环比")
    private BigDecimal lastSalesAmtRate;
    @ApiModelProperty(value = "同期医保销售额")
    private BigDecimal lastYearSalesAmt;
    @ApiModelProperty(value = "同期医保销售额同比")
    private BigDecimal lastYearSalesAmtRate;
    @ApiModelProperty(value = "本月医销比")
    private BigDecimal medSalesAmtRate;
    @ApiModelProperty(value = "上月医销比")
    private BigDecimal lastMedSalesAmtRate;
    @ApiModelProperty(value = "上月医销比环比")
    private BigDecimal lastMedSalesAmtRateHB;
    @ApiModelProperty(value = "同期医销比")
    private BigDecimal lastYearMedSalesAmtRate;
    @ApiModelProperty(value = "同期医销比同比")
    private BigDecimal lastYearMedSalesAmtRateTB;
    @ApiModelProperty(value = "本月医保交易次数")
    private BigDecimal transactionQty;
    @ApiModelProperty(value = "上月医保交易次数")
    private BigDecimal lastTransactionQty;
    @ApiModelProperty(value = "上月医保交易次数环比")
    private BigDecimal lastTransactionQtyRate;
    @ApiModelProperty(value = "同期医保交易次数")
    private BigDecimal lastYearTransactionQty;
    @ApiModelProperty(value = "同期医保交易次数同比")
    private BigDecimal lastYearTransactionQtyRate;
    @ApiModelProperty(value = "本月医交比")
    private BigDecimal medTransactionRate;
    @ApiModelProperty(value = "上月医交比")
    private BigDecimal lastMedTransactionRate;
    @ApiModelProperty(value = "上月医交比环比")
    private BigDecimal lastMedTransactionRateHB;
    @ApiModelProperty(value = "同期医交比")
    private BigDecimal lastYearMedTransactionRate;
    @ApiModelProperty(value = "同期医交比同比")
    private BigDecimal lastYearMedTransactionRateTB;

}

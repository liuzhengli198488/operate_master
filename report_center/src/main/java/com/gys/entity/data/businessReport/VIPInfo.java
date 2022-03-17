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
public class VIPInfo {
    @ApiModelProperty(value = "本月会员销售额")
    private BigDecimal salesAmt;
    @ApiModelProperty(value = "上月会员销售额")
    private BigDecimal lastSalesAmt;
    @ApiModelProperty(value = "上月会员销售额环比")
    private BigDecimal lastSalesAmtRate;
    @ApiModelProperty(value = "同期会员销售额")
    private BigDecimal lastYearSalesAmt;
    @ApiModelProperty(value = "同期会员销售额同比")
    private BigDecimal lastYearSalesAmtRate;
    @ApiModelProperty(value = "本月会销比")
    private BigDecimal vipSalesAmtRate;
    @ApiModelProperty(value = "上月会销比")
    private BigDecimal lastVipSalesAmtRate;
    @ApiModelProperty(value = "上月会销比环比")
    private BigDecimal lastVipSalesAmtRateHB;
    @ApiModelProperty(value = "同期会销比")
    private BigDecimal lastYearVipSalesAmtRate;
    @ApiModelProperty(value = "同期会销比同比")
    private BigDecimal lastYearVipSalesAmtRateTB;
    @ApiModelProperty(value = "本月会员交易次数")
    private BigDecimal transactionQty;
    @ApiModelProperty(value = "上月会员交易次数")
    private BigDecimal lastTransactionQty;
    @ApiModelProperty(value = "上月会员交易次数环比")
    private BigDecimal lastTransactionQtyRate;
    @ApiModelProperty(value = "同期会员交易次数")
    private BigDecimal lastYearTransactionQty;
    @ApiModelProperty(value = "同期会员交易次数同比")
    private BigDecimal lastYearTransactionQtyRate;
    @ApiModelProperty(value = "本月会交比")
    private BigDecimal vipTransactionRate;
    @ApiModelProperty(value = "上月会交比")
    private BigDecimal lastVipTransactionRate;
    @ApiModelProperty(value = "上月会交比环比")
    private BigDecimal lastVipTransactionRateHB;
    @ApiModelProperty(value = "同期会交比")
    private BigDecimal lastYearVipTransactionRate;
    @ApiModelProperty(value = "同期会交比同比")
    private BigDecimal lastYearVipTransactionRateTB;

}

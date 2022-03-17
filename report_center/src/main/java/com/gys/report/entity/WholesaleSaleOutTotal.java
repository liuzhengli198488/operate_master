package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "批发销售查询总计")
public class WholesaleSaleOutTotal {

    @ApiModelProperty(value = "去税金额")
    private BigDecimal batAmt;
    @ApiModelProperty(value = "税金")
    private BigDecimal rateBat;
    @ApiModelProperty(value = "合计金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "加点后去税金额")
    private BigDecimal addAmt;
    @ApiModelProperty(value = "加点后税金")
    private BigDecimal addTax;
    @ApiModelProperty(value = "加点后合计金额")
    private BigDecimal addtotalAmount;
    @ApiModelProperty(value = " 数量")
    private BigDecimal qty;

    @ApiModelProperty(value = "移动去税成本额")
    private BigDecimal movAmt;
    @ApiModelProperty(value = "移动去税税金")
    private BigDecimal rateMov;
    @ApiModelProperty(value = "移动含税成本额")
    private BigDecimal movTotalAmount;

}

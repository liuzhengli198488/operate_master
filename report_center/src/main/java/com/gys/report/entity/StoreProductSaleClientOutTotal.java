package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class StoreProductSaleClientOutTotal {


    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "应收金额")
    private BigDecimal amountReceivable;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @ApiModelProperty(value = "折扣额")
    private BigDecimal deduction;
    @ApiModelProperty(value = "去税销售额")
    private BigDecimal removeTaxSale;
    @ApiModelProperty(value = "去税成本额")
    private BigDecimal movPrices;
    @ApiModelProperty(value = "含税成本额")
    private BigDecimal includeTaxSale;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
    @ApiModelProperty(value = "加点后去税成本额")
    private BigDecimal addMovPrices;
    @ApiModelProperty(value = "加点后含税成本额")
    private BigDecimal addIncludeTaxSale;
    @ApiModelProperty(value = "加点后毛利额")
    private BigDecimal addGrossProfitMargin;
    @ApiModelProperty(value = "加点后毛利率")
    private String addGrossProfitRate;
    @ApiModelProperty(value = "仓库库存")
    private BigDecimal dcQty;
    @ApiModelProperty(value = "门店库存")
    private BigDecimal stoQty;

    @ApiModelProperty(value = "线上销售数量")
    private BigDecimal onlineSaleQty;
    @ApiModelProperty(value = "线上实收金额")
    private BigDecimal onlineAmt;
    @ApiModelProperty(value = "线上毛利额")
    private BigDecimal onlineGrossAmt;
    @ApiModelProperty(value = "线上成本额")
    private BigDecimal onlineMov;
    @ApiModelProperty(value = "线上毛利率")
    private String onlineGrossRate;

    //平均销售价
    private BigDecimal averagePrice;

    //总库存量
    private BigDecimal sumStock;
}

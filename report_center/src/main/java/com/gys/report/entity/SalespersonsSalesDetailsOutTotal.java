package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class SalespersonsSalesDetailsOutTotal {

    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "应收金额")
    private BigDecimal amountReceivable;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @ApiModelProperty(value = "成本额")
    private BigDecimal includeTaxSale;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
    @ApiModelProperty(value = "折扣金额")
    private BigDecimal discount;
    @ApiModelProperty(value = "折扣率")
    private String discountRate;
}

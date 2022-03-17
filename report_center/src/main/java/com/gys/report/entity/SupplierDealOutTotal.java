package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "供应商往来单据查询")
public class SupplierDealOutTotal {

    @ApiModelProperty(value = "去税金额")
    private BigDecimal batAmt;
    @ApiModelProperty(value = "税金")
    private BigDecimal rateBat;
    @ApiModelProperty(value = "合计金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "开单金额")
    private BigDecimal billingAmount;

}

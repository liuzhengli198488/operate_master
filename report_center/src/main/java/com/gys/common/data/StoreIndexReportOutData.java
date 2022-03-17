package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StoreIndexReportOutData implements Serializable {

    private static final long serialVersionUID = -3781934088499015325L;

    @ApiModelProperty(value = "销售额")
    private BigDecimal saleAmt;

    @ApiModelProperty(value = "毛利额")
    private BigDecimal profitAmt;

    @ApiModelProperty(value = "毛利率")
    private BigDecimal profitMargin;

    @ApiModelProperty(value = "交易次数")
    private BigDecimal transactionCount;

    @ApiModelProperty(value = "客单价")
    private BigDecimal custPrice;


}

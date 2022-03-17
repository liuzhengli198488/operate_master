package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NoSaleTotalOutData {
    @ApiModelProperty(value = "库存数量")
    private BigDecimal stockQty;
    @ApiModelProperty(value = "库存成本额")
    private BigDecimal costAmt;
    @ApiModelProperty(value = "期间销售数量")
    private BigDecimal gssdQty;
}

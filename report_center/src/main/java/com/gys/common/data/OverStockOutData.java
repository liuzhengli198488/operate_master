package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverStockOutData {
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "积压成本额")
    private BigDecimal totalAmt;
    @ApiModelProperty(value = "销售额")
    private BigDecimal gssdAmt;
    @ApiModelProperty(value = "积压天数")
    private BigDecimal stockDay;
    @ApiModelProperty(value = "近30天销售量")
    private BigDecimal gssdQty;
    private String proSpecs;
    private String factoryName;
    @ApiModelProperty(value = "库存数量")
    private String totalQty;
}

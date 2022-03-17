package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSalesBySupplierOutTotal {
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "批次")
    private String batch;
    @ApiModelProperty(value = "门店编码")
    private String brId;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @ApiModelProperty(value = "批次异动表数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "销售数量占比")
    private BigDecimal qtyProportion;
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
}

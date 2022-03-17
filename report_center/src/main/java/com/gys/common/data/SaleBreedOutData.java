package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleBreedOutData {
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "销售量")
    private String gssdQty;
    @ApiModelProperty(value = "销售额")
    private String gssdAmt;
    @ApiModelProperty(value = "毛利额")
    private String grossProfitAmt;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
    private String proSpecs;
    private String factoryName;
}

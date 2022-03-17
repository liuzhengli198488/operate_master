package com.gys.entity.data.productSaleAnalyse;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StoreInfoOutData {
    @ApiModelProperty(value = "省份编码")
    private String provinceId;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "市编码")
    private String cityId;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "客户编码")
    private String clientId;
    @ApiModelProperty(value = "客户名")
    private String clientName;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名")
    private String stoName;
    @ApiModelProperty(value = "店型编码")
    private String stoType;
    @ApiModelProperty(value = "店型")
    private String stoTypeName;
    private String stoNum;//门店数
}

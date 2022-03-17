package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SupplierSaleMan {

    @ApiModelProperty(value = "业务员编号")
    private String gssCode;

    @ApiModelProperty(value = "业务员姓名")
    private String gssName;
}

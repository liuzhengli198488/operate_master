package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("供应商对象")
@Data
public class SupplierInfoDTO {

    @ApiModelProperty("供应商编码")
    private String supCode;

    @ApiModelProperty("供应商名称")
    private String supName;
}
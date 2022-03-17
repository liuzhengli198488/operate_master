package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateInvoicedInData {

    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "门店编码", required = true)
    private String stoCode;

    @ApiModelProperty(value = "门店编码", required = true)
    private String billNo;

    @ApiModelProperty(value = "销售日期", required = true)
    private String saleDate;

}

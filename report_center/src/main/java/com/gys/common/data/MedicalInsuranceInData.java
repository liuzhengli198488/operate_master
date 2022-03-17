package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MedicalInsuranceInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "起始日期")
    private String startDate;
    @ApiModelProperty(value = "结束日期")
    private String endDate;
    @ApiModelProperty(value = "物料凭证类型数组")
    private String[] matType;
    @ApiModelProperty(value = "导出excel类型 1 xls 2 xlsx")
    private Integer type;
}

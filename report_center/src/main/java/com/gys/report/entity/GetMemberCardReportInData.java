package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class GetMemberCardReportInData implements Serializable {
    private static final long serialVersionUID = -812779531184640124L;

    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @ApiModelProperty(value = "店号")
    private String brId;

    @ApiModelProperty(value = "收银工号")
    private String querySyNum;

    @ApiModelProperty(value = "班次  0:通班/ 1:早班 / 2: 中班 / 3 :晚班")
    private String queryBc;


    @NotBlank(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间")
    private String queryEndDate;

    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间")
    private String queryStartDate;


    @ApiModelProperty(value = "门店")
    private String[] brIdList;
}

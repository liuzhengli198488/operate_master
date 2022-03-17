package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SaveUserCommissionSummaryDetailInData {

    @ApiModelProperty(value = "加盟商")
    private List<String> clients;

    @ApiModelProperty(value = "开始日期日期yyyyMMdd")
    private String startDate;

    @ApiModelProperty(value = "结束日期日期yyyyMMdd")
    private String endDate;

    @ApiModelProperty(value = "1:销售提成 2:单品提成")
    private String type;

}

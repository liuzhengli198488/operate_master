package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayClientInData {
    @ApiModelProperty(value = "用户名")
    private String clientId;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "付款类型")
    private String payType;
    @ApiModelProperty(value = "付款编号")
    private String payNo;
    @ApiModelProperty(value = "是否明细 0 否 1 是")
    private String isShowDetail;
    @ApiModelProperty(value = "页数")
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;
}

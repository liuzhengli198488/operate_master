package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.catalina.LifecycleState;

import java.util.List;

@Data
public class Production {

    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "店号")
    private String gsahBrId;

    @ApiModelProperty(value = "商品编码")
    private String productCode;

    @ApiModelProperty(value = "起始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "门店")
    private String[] brIdList;

}

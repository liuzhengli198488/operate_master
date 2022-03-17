package com.gys.entity.data.MonthPushMoney.V2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "PushMoneyListV2InData",description = "入参")
public class PushMoneyListV2InData {
    private String client;
    @ApiModelProperty(value = "提成方案id")
    private int id;
    @ApiModelProperty(value = "提成方案类型 1.销售 2.商品")
    private String type;
    @ApiModelProperty(value = "门店编码")
    private List<String> stoArr;
    @ApiModelProperty(value = "开始时间 ")
    private String startDate;
    @ApiModelProperty(value = "结束时间 ")
    private String endDate;
}

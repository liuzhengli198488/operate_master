package com.gys.entity.data.MonthPushMoney.V2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PushMoneyBySalespersonV2InData",description = "营业员销售提成入参")
public class PushMoneyBySalespersonV2InData {
    private String client;
    @ApiModelProperty("编码id")
    private Integer id;
    @ApiModelProperty("编码营业员id")
    private String salerId;
    @ApiModelProperty("提成方案类型 1.销售 2.商品")
    private String type;
    @ApiModelProperty("门店编码")
    private List<String> stoArr;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "来源 1正常 2试算")
    private String source;
    @ApiModelProperty(value = "提成名称")
    private String planName;
    private Integer pageNum;
    private Integer pageSize;
    private String stoCode;
    private Integer planId;

    public PushMoneyBySalespersonV2InData(String client, Integer id, String salerId, String startDate, String endDate, List<String> stoArr) {
        this.client = client;
        this.id = id;
        this.salerId = salerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stoArr = stoArr;
    }
}

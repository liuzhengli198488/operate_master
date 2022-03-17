package com.gys.entity.data.MonthPushMoney.V2;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "PushMoneyByStoreSalepersonV2OutData",description = "门店提成")
public class PushMoneyByStoreSalepersonV2OutData {
    private String client;
    @ApiModelProperty(value = "方案名称")
    private String planName;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endtDate;
    @ApiModelProperty(value = "提成类型")
    private String type;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "营业员编码")
    private String salerId;
    @ApiModelProperty(value = "营业员名称")
    private String salerName;
    @ApiModelProperty(value = "销售天数")
    private BigDecimal days;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "提成合计")
    private BigDecimal deductionWage;
    @ApiModelProperty(value = "提成占比")
    private BigDecimal deductionWageRate;

}

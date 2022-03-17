package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverallSalesPlanData {

    @ApiModelProperty(value = "计划营业额")
    private BigDecimal planDailyPayAmt = BigDecimal.ZERO;

    @ApiModelProperty(value = "计划毛利额")
    private BigDecimal planGrossProfit = BigDecimal.ZERO;

    @ApiModelProperty(value = "计划毛利率")
    private BigDecimal planGrossMargin = BigDecimal.ZERO;

    @ApiModelProperty(value = "状态")
    private String planStatus;


}

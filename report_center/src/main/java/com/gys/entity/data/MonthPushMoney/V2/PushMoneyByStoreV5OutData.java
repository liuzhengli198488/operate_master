package com.gys.entity.data.MonthPushMoney.V2;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "PushMoneyByStoreV5OutData",description = "门店提成")
public class PushMoneyByStoreV5OutData {
    private String client;
    @ApiModelProperty(value = "方案ID")
    private Integer planId;
    @ApiModelProperty(value = "方案名称")
    private String planName;
    @ApiModelProperty(value = "子方案名称")
    private String zPlanName;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endtDate;
    @ApiModelProperty(value = "提成类型名")
    private String type;
    @ApiModelProperty(value = "提成类型")
    private String typeValue;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "销售天数")
    private BigDecimal days;
    @ApiModelProperty(value = "成本额")
    private BigDecimal costAmt;
    @ApiModelProperty(value = "应收金额")
    private BigDecimal ysAmt;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;
    @ApiModelProperty(value = "折扣金额")
    private BigDecimal zkAmt;
    @ApiModelProperty(value = "折扣率")
    private BigDecimal zkRate;
    @ApiModelProperty(value = "提成商品成本额")
    private BigDecimal tcCostAmt;
    @ApiModelProperty(value = "提成商品应收销售额")
    private BigDecimal tcYsAmt;
    @ApiModelProperty(value = "提成商品销售额")
    private BigDecimal tcAmt;
    @ApiModelProperty(value = "提成商品毛利额")
    private BigDecimal tcGrossProfitAmt;
    @ApiModelProperty(value = "提成商品毛利率")
    private BigDecimal tcGrossProfitRate;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "提成合计")
    private BigDecimal tiTotal;

    @ApiModelProperty(value = "提成合计")
    private BigDecimal deductionWage;
    @ApiModelProperty(value = "提成销售占比")
    private BigDecimal deductionWageAmtRate;
    @ApiModelProperty(value = "提成毛利占比")
    private BigDecimal deductionWageGrossProfitRate;


    private List<String> storeCodes;


}

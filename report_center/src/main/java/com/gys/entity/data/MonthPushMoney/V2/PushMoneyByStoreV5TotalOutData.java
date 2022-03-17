package com.gys.entity.data.MonthPushMoney.V2;


import com.gys.annotation.CommissionReportField;
import com.gys.common.data.CommonVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "PushMoneyByStoreV5OutData", description = "门店提成")
public class PushMoneyByStoreV5TotalOutData {

    private String client;

    @ApiModelProperty(value = "方案ID")
    private Integer planId;

    @CommissionReportField(fieldName = "方案名称")
    @ApiModelProperty(value = "方案名称")
    private String planName;

    @CommissionReportField(fieldName = "起始日期")
    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @CommissionReportField(fieldName = "结束日期")
    @ApiModelProperty(value = "结束时间")
    private String endtDate;

    @CommissionReportField(fieldName = "提成类型")
    @ApiModelProperty(value = "提成类型名")
    private String type;

    @ApiModelProperty(value = "提成类型")
    private String typeValue;

    @CommissionReportField(fieldName = "子方案名称", showSubPlan = true)
    @ApiModelProperty(value = "子方案名称")
    private String cPlanName;

    @CommissionReportField(fieldName = "适用店数")
    @ApiModelProperty(value = "适用店数")
    private Integer stoNum;

    @CommissionReportField(fieldName = "生效门店")
    @ApiModelProperty(value = "生效门店")
    private Integer effectiveStore;

    @CommissionReportField(fieldName = "销售天数", isTotalItem = true)
    @ApiModelProperty(value = "销售天数")
    private BigDecimal days;

    @CommissionReportField(fieldName = "成本额", isTotalItem = true)
    @ApiModelProperty(value = "成本额")
    private BigDecimal costAmt;

    @CommissionReportField(fieldName = "应收金额", isTotalItem = true)
    @ApiModelProperty(value = "应收金额")
    private BigDecimal ysAmt;

    @CommissionReportField(fieldName = "实收金额", isTotalItem = true)
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;

    @CommissionReportField(fieldName = "毛利额", isTotalItem = true)
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;

    @CommissionReportField(fieldName = "毛利率", isTotalItem = true, suffix = "%")
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;

    @CommissionReportField(fieldName = "折扣金额", isTotalItem = true)
    @ApiModelProperty(value = "折扣金额")
    private BigDecimal zkAmt;

    @CommissionReportField(fieldName = "折扣率", isTotalItem = true, suffix = "%")
    @ApiModelProperty(value = "折扣率")
    private BigDecimal zkRate;

    @CommissionReportField(fieldName = "提成商品成本额", isTotalItem = true)
    @ApiModelProperty(value = "提成商品成本额")
    private BigDecimal tcCostAmt;

    @ApiModelProperty(value = "提成商品销售额")
    private BigDecimal tcYsAmt;

    @CommissionReportField(fieldName = "提成商品销售额", isTotalItem = true)
    @ApiModelProperty(value = "提成商品销售额")
    private BigDecimal tcAmt;

    @CommissionReportField(fieldName = "提成商品毛利额", isTotalItem = true)
    @ApiModelProperty(value = "提成商品毛利额")
    private BigDecimal tcGrossProfitAmt;

    @CommissionReportField(fieldName = "提成商品毛利率", isTotalItem = true, suffix = "%")
    @ApiModelProperty(value = "提成商品毛利率")
    private BigDecimal tcGrossProfitRate;

    @CommissionReportField(fieldName = "提成金额", isTotalItem = true)
    @ApiModelProperty(value = "提成金额")
    private BigDecimal tiTotal;

    @ApiModelProperty(value = "提成合计")
    private BigDecimal deductionWage;

    @CommissionReportField(fieldName = "提成销售比", isTotalItem = true, suffix = "%")
    @ApiModelProperty(value = "提成销售占比")
    private BigDecimal deductionWageAmtRate;

    @CommissionReportField(fieldName = "提成毛利比", isTotalItem = true, suffix = "%")
    @ApiModelProperty(value = "提成毛利占比")
    private BigDecimal deductionWageGrossProfitRate;


    private List<CommonVo> storeCodes;

}

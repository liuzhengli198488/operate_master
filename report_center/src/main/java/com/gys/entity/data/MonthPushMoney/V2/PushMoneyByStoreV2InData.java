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
@ApiModel(value = "PushMoneyByStoreV2InData", description = "")
public class PushMoneyByStoreV2InData {

    private String client;

    @ApiModelProperty(value = "提成方案id")
    private int planId;

    @ApiModelProperty(value = "提成方案类型 1.销售 2.商品")
    private String type;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "试算开始时间")
    private String trialStartDate;

    @ApiModelProperty(value = "试算结束时间")
    private String trialEndDate;

    @ApiModelProperty(value = "来源 1正常 2试算")
    private String source;

    @ApiModelProperty(value = "门店数组")
    private List<String> stoArr;

    @ApiModelProperty(value = "提成名称")
    private String planName;

    @ApiModelProperty(value = "提成门店")
    private String stoCode;

    @ApiModelProperty(value = "是否展示子方案，1是 0否")
    private String ifShowZplan;

    @ApiModelProperty(value = "剔除折扣率 操作符号")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 百分比值")
    private String planRejectDiscountRate;

    @ApiModelProperty(value = "子方案名称")
    private String cPlanName;

    private Integer pageNum;

    private Integer pageSize;



    public PushMoneyByStoreV2InData(String client, String source, int planId, List<String> stoArr, String planRejectDiscountRateSymbol, String planRejectDiscountRate) {
        this.client = client;
        this.source = source;
        this.planId = planId;
        this.stoArr = stoArr;
        this.planRejectDiscountRateSymbol = planRejectDiscountRateSymbol;
        this.planRejectDiscountRate = planRejectDiscountRate;
    }

}

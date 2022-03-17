package com.gys.entity.data.MonthPushMoney;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年11月17日 下午7:23
 */
@Data
public class PushMoneyByStoreV5InData {

    private String client;

    @ApiModelProperty(value = "来源 1正常 2试算")
    private String source;

    @ApiModelProperty(value = "提成方案id")
    private Integer planId;

    @ApiModelProperty(value = "门店数组")
    private List<String> stoArr;

    @ApiModelProperty(value = "剔除折扣率 操作符号")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 百分比值")
    private String planRejectDiscountRate;

    private String ifShowZplan;//是否展示子方案，1是0否

    private String settingId;

    private String userChooseEndDate;

    private String userChooseStartDate;

    public PushMoneyByStoreV5InData(String client, String source, int planId, List<String> stoArr, String planRejectDiscountRateSymbol, String planRejectDiscountRate) {
        this.client = client;
        this.source = source;
        this.planId = planId;
        this.stoArr = stoArr;
        this.planRejectDiscountRateSymbol = planRejectDiscountRateSymbol;
        this.planRejectDiscountRate = planRejectDiscountRate;
    }
}


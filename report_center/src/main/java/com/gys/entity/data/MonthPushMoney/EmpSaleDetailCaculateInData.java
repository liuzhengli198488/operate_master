package com.gys.entity.data.MonthPushMoney;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年11月24日 下午5:09
 */
@Data
public class EmpSaleDetailCaculateInData implements Serializable {

    private String client;

    @ApiModelProperty(value = "提成方案id")
    private Integer planId;

    @ApiModelProperty(value = "门店数组")
    private List<String> stoArr;

    @ApiModelProperty(value = "剔除折扣率 操作符号")
    private String planRejectDiscountRateSymbol;

    @ApiModelProperty(value = "剔除折扣率 百分比值")
    private String planRejectDiscountRate;

    private String ifShowZplan;//是否展示子方案，1是0否

}


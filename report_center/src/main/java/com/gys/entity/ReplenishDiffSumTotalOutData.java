package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ReplenishDiffSumTotalOutData implements Serializable {

    @ApiModelProperty(value = "日期")
    private String dateStr;

    @ApiModelProperty(value = "省")
    private String provName;

    @ApiModelProperty(value = "市")
    private String cityName;

    @ApiModelProperty(value = "加盟商编号")
    private String client;

    @ApiModelProperty(value = "加盟商名称")
    private String clientName;

    @ApiModelProperty(value = "补货方式")
    private String pattern;

    @ApiModelProperty(value = "补货门店数")
    private BigDecimal replenishStoreCount;

    @ApiModelProperty(value = "单店单次原补货品项")
    private BigDecimal singleReplenishCount;

    @ApiModelProperty(value = "单店单次实际补货品项")
    private BigDecimal singleActualReplenishCount;

    @ApiModelProperty(value = "原单品项数")
    private BigDecimal oldReplenishProCount;

    @ApiModelProperty(value = "新增品项数")
    private BigDecimal addCount;

    @ApiModelProperty(value = "新增占比")
    private String addProportion;

    @ApiModelProperty(value = "删除品项数")
    private BigDecimal deleteCount;

    @ApiModelProperty(value = "删除占比")
    private String deleteProportion;

    @ApiModelProperty(value = "一致品项数")
    private BigDecimal equalCount;

    @ApiModelProperty(value = "一致占比")
    private String equalProportion;

    @ApiModelProperty(value = "修改品项数")
    private BigDecimal editCount;

    @ApiModelProperty(value = "修改占比")
    private String editProportion;

    @ApiModelProperty(value = "补货公式零售额")
    private BigDecimal retailSaleAmt;

    @ApiModelProperty(value = "实际零售额")
    private BigDecimal actualRetailSaleAmt;

    @ApiModelProperty(value = "零售额差异")
    private BigDecimal diffAmt;

    @ApiModelProperty(value = "补货公式成本额")
    private BigDecimal costAmt;

    @ApiModelProperty(value = "实际成本额")
    private BigDecimal actualCostAmt;

    @ApiModelProperty(value = "成本额差异")
    private BigDecimal diffCostAmt;
}
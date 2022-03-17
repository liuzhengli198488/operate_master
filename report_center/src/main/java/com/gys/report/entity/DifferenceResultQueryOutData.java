package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回实体
 *
 * @author xiaoyuan on 2020/10/14
 */
@Data
public class DifferenceResultQueryOutData implements Serializable {
    private static final long serialVersionUID = -7380978636936765129L;

    @ApiModelProperty(value = "序号")
    private Integer index;

    @ApiModelProperty(value = "门店编码")
    private String brId;

    @ApiModelProperty(value = "门店名称")
    private String brName;

    @ApiModelProperty(value = "总品项数")
    private String totalNumberOfItems;

    @ApiModelProperty(value = "总品项差异数")
    private String totalProductDifference;

    @ApiModelProperty(value = "总差异率")
    private String totalDifferenceRate;

    @ApiModelProperty(value = "总差异成本额")
    private String totalVarianceCost;

    @ApiModelProperty(value = "总差异销售额")
    private String totalVarianceSales;

    @ApiModelProperty(value = "盘盈品项")
    private String inventoryItems;

    @ApiModelProperty(value = "盘盈成本额")
    private String inventoryCost;

    @ApiModelProperty(value = "盘亏品项")
    private String lostItems;

    @ApiModelProperty(value = "盘亏成本额")
    private String inventoryLossCost;

    @ApiModelProperty(value = "中药差异品项")
    private String differenceItemsOfChineseMedicine;

    @ApiModelProperty(value = "中药差异金额")
    private String chineseMedicineDifferenceAmount;

    @ApiModelProperty(value = "剔除中药差异品项")
    private String eliminateChineseMedicineItems;

    @ApiModelProperty(value = "剔除中药差异金额")
    private String excludeTheAmountOfChineseMedicine;

    @ApiModelProperty(value = "剔除中药差异率")
    private String eliminateTheDifferenceRateOfChineseMedicine;
}

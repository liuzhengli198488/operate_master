package com.gys.entity.data.Category;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/4 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatisticResponse {
    @ApiModelProperty(value = "类型：1调整， 2淘汰，3引进")
    /**
     * 类型：1调整， 2淘汰，3引进
     */
    private Integer type;
    @ApiModelProperty(value = "销售额")
    /**销售额*/
    private String salesAmount;
    @ApiModelProperty(value = "毛利额")
    /**毛利额*/
    private String profitAmount;
    @ApiModelProperty(value = "交易次数")
    /**交易次数*/
    private Integer orderCount;
    @ApiModelProperty(value = "销售额环比")
    /**销售额环比*/
    private Integer salesAmountCompare;
    @ApiModelProperty(value = "毛利额环比")
    /**毛利额环比*/
    private Integer profitAmountCompare;
    @ApiModelProperty(value = "交易次数环比")
    /**交易次数环比*/
    private Integer orderCountCompare;
}

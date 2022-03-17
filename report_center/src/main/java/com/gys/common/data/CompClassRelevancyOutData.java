package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompClassRelevancyOutData {
    @ApiModelProperty(value = "关联交易次数")
    private BigDecimal billCount;
    @ApiModelProperty(value = "销售额")
    private BigDecimal gssdAmt;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;
    @ApiModelProperty(value = "关联成分编码")
    private String proCompClass;
    @ApiModelProperty(value = "关联成分名")
    private String proCompClassName;
    @ApiModelProperty(value = "成分排名")
    private Integer rankNo;
    @ApiModelProperty(value = "关联率")
    private String relevancyRate;
}

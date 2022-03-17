package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RelevancyOutData {
    @ApiModelProperty(value = "成功关联次数")
    private BigDecimal srCount;
    @ApiModelProperty(value = "成功关联销售额")
    private BigDecimal saleAmt;
    @ApiModelProperty(value = "成功关联毛利额")
    private BigDecimal profitAmt;
    @ApiModelProperty(value = "成功关联品种数")
    private BigDecimal srProCount;
    @ApiModelProperty(value = "自动弹出次数")
    private BigDecimal srACount;
    @ApiModelProperty(value = "手工弹出次数")
    private BigDecimal srMCount;
    @ApiModelProperty(value = "关联弹出次数")
    private BigDecimal totalCount;
    @ApiModelProperty(value = "动销品种数")
    private BigDecimal saleProCount;
}

package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CompClassTopTen {
    @ApiModelProperty(value = "成分编码")
    private String firstProCompClass;
    @ApiModelProperty(value = "成分名")
    private String firstProCompClassName;
    @ApiModelProperty(value = "成分交易次数")
    private BigDecimal firstBillCount;
    @ApiModelProperty(value = "成分总交易额")
    private BigDecimal firstAmt;
    @ApiModelProperty(value = "关联成分TOP3")
    private List<CompClassRelevancyOutData> compClassRelevancyOutDataList;
}

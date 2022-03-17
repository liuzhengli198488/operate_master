package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompClassOutData {
    @ApiModelProperty(value = "成分编码")
    private String proCompClass;
    @ApiModelProperty(value = "成分名称")
    private String proCompClassName;
    @ApiModelProperty(value = "动销品种数")
    private String proCount;
    @ApiModelProperty(value = "销售额")
    private String gssdAmt;
    @ApiModelProperty(value = "毛利额")
    private String grossProfitAmt;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
}

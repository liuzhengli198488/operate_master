package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReportOutData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名")
    private String stoName;
    @ApiModelProperty(value = "动销品种数")
    private String proCount;
    @ApiModelProperty(value = "销售额")
    private String totalAmt;
    @ApiModelProperty(value = "毛利贡献率")
    private String contributionRate;
    @ApiModelProperty(value = "毛利率")
    private String totalProfitRate;
    @ApiModelProperty(value = "毛利额")
    private String totalProfit;
    @ApiModelProperty(value = "大类")
    private String bigClass;
    @ApiModelProperty(value = "大类编码")
    private String bigClassCode;
    @ApiModelProperty(value = "中类")
    private String midClass;
    @ApiModelProperty(value = "大类编码")
    private String midClassCode;
}

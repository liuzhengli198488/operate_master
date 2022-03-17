package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreIndexReportInData implements Serializable {

    private static final long serialVersionUID = -3781934088499015325L;

    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @ApiModelProperty(value = "门店编码")
    private String brId;

    @ApiModelProperty(value = "日月标志 0-日 1-月")
    private String dayOrMonth;

    @ApiModelProperty(value = "起始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;
}

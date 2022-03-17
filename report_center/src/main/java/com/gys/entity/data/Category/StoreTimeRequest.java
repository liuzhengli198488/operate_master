package com.gys.entity.data.Category;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/4 14:22
 */
@Data
public class StoreTimeRequest {
    @ApiModelProperty(hidden = true)
    /**加盟商*/
    private String client;
    @ApiModelProperty(value  = "门店")
    /**门店*/
    private String stoCode;
    @ApiModelProperty(hidden = true)
    /**开始时间*/
    private String startDate;
    @ApiModelProperty(hidden = true)
    /**结束时间*/
    private String endDate;

    @ApiModelProperty(value  = "1 本周/月 2 上周/月 3 往周/月 ")
    private Integer chooseType;
    @ApiModelProperty(value  = "几月/几周")
    private Integer number;
    @ApiModelProperty(value  = "月/周 1 月份 2 周")
    private Integer weekOrMonth;
    @ApiModelProperty(value  = "年")
    private Integer year;
}

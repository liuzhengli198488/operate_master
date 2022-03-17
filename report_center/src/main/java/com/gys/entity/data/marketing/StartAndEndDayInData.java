package com.gys.entity.data.marketing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StartAndEndDayInData implements Serializable {

    private static final long serialVersionUID = -7740911306970324149L;

    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店")
    private String brId;
    @ApiModelProperty(value = "年份")
    private Integer year;
    @ApiModelProperty(value = "月份")
    private Integer month;
    @ApiModelProperty(value = "查询日期")
    private String queryDate;
    @ApiModelProperty(value = "上月最后一天日期")
    private String lastMonthLastDay;
    @ApiModelProperty(value = "上年本月最后一天日期")
    private String lastYearThisMonthLastDay;

}

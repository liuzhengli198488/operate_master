package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DropRateInData implements Serializable {

    private static final long serialVersionUID = 2693127762221570140L;

    @ApiModelProperty(value = "计入主动捕获 0是 1否")
    private Integer shopGoodsType;
    /**
     * 满足率类型：1-开单配货率 2-仓库发货率 3-最终下货率
     */
    @ApiModelProperty(value = "满足率类型：1-开单配货率 2-仓库发货率 3-最终下货率")
    private Integer rateType;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    /**
     * 报表类型： 1.日报  2.周报 3.月报
     */
    @ApiModelProperty(value = "报表类型： 1.日报  2.周报 3.月报")
    private String reportType;

}

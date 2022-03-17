package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReplenishDiffSumInData extends ProvCityInData implements Serializable {

    @ApiModelProperty(value = "加盟商列表")
    private List<String> clientList;

    @ApiModelProperty(value = "补货方式多选列表 0-正常补货 1-紧急补货 2-铺货 3-互调 4-直配 null-全部")
    private List<String> patternList;

    @ApiModelProperty(value = "报表类型 必填 1-日报 2-周报 3-月报")
    private String type;

    @ApiModelProperty(value = "起始日期")
    private String beginDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;


}

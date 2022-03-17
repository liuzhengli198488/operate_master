package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SelectMedicalSummaryData {

    @ApiModelProperty(value = "退货不参与 0:否 1是")
    private String participate = "0";

    @ApiModelProperty(value = "门店集合")
    private List<String> stores;

    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "查询开始日期")
    private String startDate;

    @ApiModelProperty(value = "查询结束日期")
    private String endDate;

    @ApiModelProperty(value = "险种类型  310职工基本医疗保险"+
                                        "320公务员医疗补助"+
                                        "330大额医疗费用补助"+
                                        "340离休人员医疗保障"+
                                        "390城乡居民基本医疗保险"+
                                        "392城乡居民大病医疗保险"+
                                        "510生育保险")
    private List<String> inSuTypes;
}

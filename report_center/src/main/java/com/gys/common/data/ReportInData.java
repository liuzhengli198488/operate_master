package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReportInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "分类 1 TOP10 2 TOP50")
    private String type;
    @ApiModelProperty(value = "1 本周/月 2 上周/月 3 往周/月 4 去年本周")
    private String chooseType;
    @ApiModelProperty(value = "月/周  1 月份 2 周")
    private String weekOrMonth;
    @ApiModelProperty(value = "年份")
    private Integer year;
    @ApiModelProperty(value = "几月/几周")
    private String number;
    @ApiModelProperty(value = "类别 1 大类 2 中类")
    private String categoryClass;
    private String startDate;
    private String endDate;
    private String compClass;
    @ApiModelProperty(value = "积压天数")
    private String heaven;

    @ApiModelProperty(value = "积压天数 积压成本额切换 1天数排序 2成本额排序")
    private String dayType;
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 页面条数
     */
    private Integer pageSize;
}

package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class SalesStatisticsReportInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "报表维度 1-周报 2-月报")
    private String reportDimension;
    @ApiModelProperty(value = "报表类型 1-汇总报表 2-大类报表 3-中类报表 4-商品分类报表")
    private String reportType;
    @ApiModelProperty(value = "报表二级类型 1-定位 2-毛利区间")
    private String reportSecondType;
    @ApiModelProperty(value = "开始日期 20211201")
    private String startDate;
    @ApiModelProperty(value = "结束日期 20211223")
    private String endDate;
    @ApiModelProperty(value = "商品分类")
    private List<String[]> proClass;
    @ApiModelProperty(value = "商品分类备用")
    private List<String> proClassBak;

    private int pageNum;
    private int pageSize;
}

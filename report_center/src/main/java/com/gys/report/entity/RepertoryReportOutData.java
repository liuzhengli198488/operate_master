package com.gys.report.entity;

import lombok.Data;

import java.util.List;


@Data
public class RepertoryReportOutData {

    //库存报表数据集合
    private List<RepertoryReportData> list;
    //库存报表数据汇总
    private RepertoryReportData listSum;
    // 页码
    private Integer pageIndex;
    // 每页条数
    private Integer pageSize;
    // 总页数
    private Integer pageCount;
    // 总行数
    private Integer rowCount;

}

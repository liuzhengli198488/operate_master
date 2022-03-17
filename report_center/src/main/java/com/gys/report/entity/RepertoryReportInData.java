package com.gys.report.entity;

import lombok.Data;

import java.util.List;


@Data
public class RepertoryReportInData {
    // 加盟商
    private String client;
    // 创建人ID
    private String userId;
    // 报表维度 日报：daily
    private String reportDimension;
    // 报表类型 汇总报表 summary,大类 general,中类 medium,商品分类报表 commodityClassification
    private String reportType;
    // 二级维度 全部 all ,定位 position
    private String secondDimension;
    // 大类 中类 商品分类
    private String [][] classArr;
    private List<String> classArrs;
    // 是否选择效期
    private int isCheckedPeriod;
    // 有效期
    private String validityPeriod;
    // 查询日期
    private String queryDate;
    // 页面下标 默认从1开始
    private Integer pageIndex = 1;
    // 每页条数 默认：50
    private Integer pageSize = 50;
    // 过滤条数 默认：0
    private Integer offsetSize = 0;
}

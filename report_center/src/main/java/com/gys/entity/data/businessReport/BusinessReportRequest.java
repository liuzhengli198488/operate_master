package com.gys.entity.data.businessReport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author XiaoZY
 * @date 2021/4/1
 */
@Data
public class BusinessReportRequest {
    @ApiModelProperty(value  = "加盟商")
    /**加盟商*/
    private String client;
    private String clientStr;
    @ApiModelProperty(value  = "门店")
    /**门店*/
    private String stoCode;
    private List<String> stoCodeList;
    @ApiModelProperty(value  = "报表日期      格式 202104")
    /**报表日期*/
    private String startDate;
    private String date;

    private String endDate;
    private String lastMonthStart;
    private String lastMonthEnd;
    private Integer startYear;
    private Integer year;
    private Integer endYear;
    private Integer startMonth;
    private Integer endMonth;
    private Integer startWeek;
    private Integer endWeek;
    private String messageId;
    private String reportType;
    private String dimension;
    private String weekOrMonth;
    //成份大类
    private String proCompBigCode;
    //成份中类
    private String proCompMidCode;
    //成分小类
    private String proCompLitCode;
    //成分分类
    private String proCompCode;
    private List<List<String>> proCompCodeList;
    private String francType;
    private String francTypeFlag;
    //单体店
    private String francType1;
    //连锁公司
    private String francType2;
    //批发公司
    private String francType3;

    private String stoCodeStr;

    private String gsstVersionStr;
    //店型
    private List<String> gsstVersionList;
    //客户类型
    private List<String> francTypeList;
    //客户
    private List<String> clientList;

    private String stoAttribute;
    //门店属性
    private List<String> stoAttributeList;



}

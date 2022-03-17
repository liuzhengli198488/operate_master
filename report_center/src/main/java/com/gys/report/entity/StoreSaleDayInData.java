package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class StoreSaleDayInData {

    @ApiModelProperty(value = "销售单号")
    private List<String> gssdBillNos;;
    @ApiModelProperty(value = "开始时间段1030")
    private String statDatePart;

    @ApiModelProperty(value = "结束时间段1130")
    private String endDatePart;
    private String client;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "支付类型")
    private String[] payName;
    @ApiModelProperty(value = "收银员")
    private String cashier;
    @ApiModelProperty(value = "开始时间")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    private String endDate;
    @ApiModelProperty(value = "用户编码")
    private String userId;
    @ApiModelProperty(value = "收款方式")
    private String saleType;
    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;
    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;
    @ApiModelProperty(value = "时间类型 1.年 2.月 3.日")
    private String dateType;
    private List<GetPayTypeOutData> payTypeOutData;
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;

    @ApiModelProperty(value = "销售备注")
    private String saleRemark;

    /**
     * 是否开启查询白名单标志   0：不开启  1：开启
     */
    private String flag;

    private String thirdVoucherId;
}

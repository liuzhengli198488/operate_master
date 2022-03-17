package com.gys.entity.data.marketing;

import com.gys.common.data.SalesRankOutData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MarketingReportOutData implements Serializable {

    private static final long serialVersionUID = -7740911306970324149L;

    @ApiModelProperty(value = "本月活动次数")
    private String totalCount;

    @ApiModelProperty(value = "本月推送次数")
    private String pushCount;

    @ApiModelProperty(value = "本月开展次数")
    private String devCount;

    @ApiModelProperty(value = "门店数")
    private String storeCount;

    @ApiModelProperty(value = "销售天数")
    private String saleDayCount;

    @ApiModelProperty(value = "门店数")
    private Integer stoQty;

    @ApiModelProperty(value = "活动名称")
    private String gsphName;

    @ApiModelProperty(value = "活动简述")
    private String gsphRemarks;

    @ApiModelProperty(value = "活动开始日期")
    private String gsphBeginDate;

    @ApiModelProperty(value = "活动结束日期")
    private String gsphEndDate;

    @ApiModelProperty(value = "上期开始日期")
    private String sqStartTime;
    @ApiModelProperty(value = "上期结束日期")
    private String sqEndTime;
    @ApiModelProperty(value = "同期结束日期")
    private String tqEndTime;
    @ApiModelProperty(value = "同期开始日期")
    private String tqStartTime;

    @ApiModelProperty(value = "营销活动id")
    private String gsphMarketid;

    private String client;

    @ApiModelProperty(value = "活动结束时间")
    private String gsphEndTime;

    @ApiModelProperty(value = "活动开始时间")
    private String gsphBeginTime;

    @ApiModelProperty(value = "促销活动单号")
    private String gsphVoucherId;
    @ApiModelProperty(value = "促销品销售额占比")
    private Double promotionalSalesRate;
    @ApiModelProperty(value = "非促销品销售额占比")
    private Double unPromotionalSalesRate;

    private Double promotionalBillRate;

    private Double unPromotionalBillRate;

    private SalesRankOutData bqSales;

    private SalesRankOutData sqSales;

    private SalesRankOutData tqSales;



}

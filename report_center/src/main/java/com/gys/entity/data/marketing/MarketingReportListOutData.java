package com.gys.entity.data.marketing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MarketingReportListOutData implements Serializable {

    private static final long serialVersionUID = -7740911306970324149L;

    @ApiModelProperty(value = "本期销售")
    private String thisMonthAmt;

    @ApiModelProperty(value = "上期销售")
    private String lastMonthAmt;

    @ApiModelProperty(value = "同期销售")
    private String lastYearAmt;

    @ApiModelProperty(value = "上期销售环比")
    private String lastMonthProportion;

    @ApiModelProperty(value = "同期销售同比")
    private String lastYearProportion;

    @ApiModelProperty(value = "本期促销名称")
    private String activityName;

    @ApiModelProperty(value = "本期促销金额")
    private String thisMonthPromotionAmt;

    @ApiModelProperty(value = "上月促销金额")
    private String lastMonthPromotionAmt;

    @ApiModelProperty(value = "上年本月促销金额")
    private String lastYearPromotionAmt;

    @ApiModelProperty(value = "上月促销金额新增环比")
    private String lastMonthPromotionProp;

    @ApiModelProperty(value = "上年本月促销金额新增同比")
    private String lastYearPromotionProp;

    @ApiModelProperty(value = "本期促销商品数量")
    private String thisMonthPromotionQty;

    @ApiModelProperty(value = "上月促销商品数量")
    private String lastMonthPromotionQty;

    @ApiModelProperty(value = "上年本月促销商品数量")
    private String lastYearPromotionQty;

    @ApiModelProperty(value = "上月促销商品数量环比")
    private String lastMonthPromotionQtyProp;

    @ApiModelProperty(value = "上年本月促销商品数量同比")
    private String lastYearPromotionQtyProp;

    @ApiModelProperty(value = "活动起始日期")
    private String activityBeginDate;

    @ApiModelProperty(value = "活动结束日期")
    private String activityEndDate;
}

package com.gys.entity.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GaiaSalesStoSummary {
    private static final long serialVersionUID = -3771261720088233548L;

    private Integer index;
    @ApiModelProperty(value = "门店编码")
    private String brId;
    @ApiModelProperty(value = "门店名")
    private String brName;
    @ApiModelProperty(value = "零售价")
    private String normalPrice;
    @ApiModelProperty(value = "会员销售金额")
    private String gsshHykAmt;
    @ApiModelProperty(value = "会员销售占比")
    private String gsshHykCost;
    @ApiModelProperty(value = "客单价")
    private String gsshSinglePrice;
    @ApiModelProperty(value = "交易次数")
    private String payCount;
    @ApiModelProperty(value = "日均销售额")
    private String dailyPayAmt;
    @ApiModelProperty(value = "日均交易次数")
    private String dailyPayCount;
    @ApiModelProperty(value = "税率")
    private String taxValue;
    @ApiModelProperty(value = "销售天数")
    private String payDayTime;

    @ApiModelProperty(value = "应收金额")
    private String gssdnormalAmt;

    /**
     * 实收金额
     */
    @ApiModelProperty(value = "实收金额")
    private String gssdAmt;

    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额")
    private String discountAmt;

    /**
     * 折扣率
     */
    @ApiModelProperty(value = "折扣率")
    private String discountRate;

    /**
     * 成本额
     */
    @ApiModelProperty(value = "去税成本额")
    private String costAmt;

    /**
     * 成本额
     */
    @ApiModelProperty(value = "含税成本额")
    private String allCostAmt;
    /**
     * 毛利额
     */
    @ApiModelProperty(value = "毛利额")
    private String grossProfitAmt;

    /**
     * 毛利率
     */
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;

    /**
     * 库存数量
     */
    @ApiModelProperty(value = "支付方式列表")
    private List<GaiaSalesPayMent> payList;
}

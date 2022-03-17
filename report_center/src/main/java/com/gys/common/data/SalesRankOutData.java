package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SalesRankOutData {
    @ApiModelProperty(value = "userId")
    private String userId;
    @ApiModelProperty(value = "加盟商")
    private String client;
    @ApiModelProperty(value = "店员")
    private String userName;
    @ApiModelProperty(value = "数量")
    private Double qty;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "门店Code")
    private String stoCode;
    @ApiModelProperty(value = "中类名称")
    private String midClassName;
    @ApiModelProperty(value = "中类Code")
    private String midClassCode;
    @ApiModelProperty(value = "销售额")
    private Double salesAmt;
    @ApiModelProperty(value = "top销售额")
    private Double topSalesAmt;
    @ApiModelProperty(value = "top销售量")
    private Double topSalesQty;
    @ApiModelProperty(value = "毛利")
    private Double gross;
    @ApiModelProperty(value = "毛利率")
    private Double grossRate;
    @ApiModelProperty(value = "交易次数")
    private Double billQty;
    @ApiModelProperty(value = "客单价")
    private Double personPrice;
    @ApiModelProperty(value = "销售天数")
    private Double salesDay;
    @ApiModelProperty(value = "单点日均交易")
    private Double avaBillQty;
    @ApiModelProperty(value = "单店日均销售额")
    private Double avaSalesAmt;
    @ApiModelProperty(value = "单店日均毛利")
    private Double avaGross;
    @ApiModelProperty(value = "品单价")
    private Double goodsPrice;
    @ApiModelProperty(value = "客品数")
    private Double personGoodsQty;
    @ApiModelProperty(value = "销售占比")
    private Double salesAmtRate;
    @ApiModelProperty(value = "销售量占比")
    private Double salesQtyRate;
    private Double vipQty;
    private Double salesQty;
    private Double goodsQty;
    private Double pcSalesAmt;
    private Double pcSalesQty;
    private Double pcBillQty;
    private Double cxAmt;
    private Double cxBillQty;

    private String type;
    @ApiModelProperty(value = "商品编码")
    private String proSelfCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;

}

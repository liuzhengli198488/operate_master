package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalesStatisticsReportOutData {
    @ApiModelProperty(value = "商品大类")
    private String proBigClassCode;
    @ApiModelProperty(value = "商品大类名称")
    private String proBigClassName;
    @ApiModelProperty(value = "商品中类")
    private String proMidClassCode;
    @ApiModelProperty(value = "商品中类名称")
    private String proMidClassName;
    @ApiModelProperty(value = "商品分类")
    private String proClassCode;
    @ApiModelProperty(value = "商品分类名称")
    private String proClassName;
    @ApiModelProperty(value = "定位")
    private String position;
    @ApiModelProperty(value = "毛利区间")
    private String profitInterval;
    @ApiModelProperty(value = "时间")
    private String date;
    @ApiModelProperty(value = "动销门店数")
    private String movStos;
    @ApiModelProperty(value = "动销品项")
    private String movItems;
    @ApiModelProperty(value = "销售量")
    private String saleQty;
    @ApiModelProperty(value = "销售额")
    private String saleAmt;
    @ApiModelProperty(value = "销售占比")
    private String saleRate;
    @ApiModelProperty(value = "毛利额")
    private String profit;
    @ApiModelProperty(value = "毛利率")
    private String profitRate;
    @ApiModelProperty(value = "折扣率")
    private String discountRate;
    @ApiModelProperty(value = "医保销售额")
    private String ebAmt;
    @ApiModelProperty(value = "医保销售占比")
    private String ebSaleRate;
    @ApiModelProperty(value = "单品平均销售价")
    private String avgProPrice;
    @ApiModelProperty(value = "客单价")
    private String avgCusPrice;
    @ApiModelProperty(value = "日均交易次数")
    private String avgDailyTradeTimes;
    @ApiModelProperty(value = "客单品项数")
    private String avgCusItem;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "账单号")
    private String billNo;
    @ApiModelProperty(value = "商品自编码")
    private String proSelfCode;
    @ApiModelProperty(value = "折扣金额")
    private String zkAmt;

    public SalesStatisticsReportOutData(){
        this.saleQty="0";
        this.saleAmt="0";
        this.profit="0";
        this.zkAmt="0";
        this.ebAmt="0";
        this.profitInterval="";
        this.position="";
        this.movStos="0";
        this.movItems="0";
        this.saleRate="0";
        this.profitRate="0";
        this.discountRate="0";
        this.ebSaleRate="0";
        this.avgProPrice="0";
        this.avgCusPrice="0";
        this.avgDailyTradeTimes="0";
        this.avgCusItem="0";
    }
}

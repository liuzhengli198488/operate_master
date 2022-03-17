package com.gys.report.entity;

import com.gys.util.csv.annotation.CsvCell;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GetSalesSummaryOfSalesmenReportOutData implements Serializable {
    private static final long serialVersionUID = -4151234302151668982L;
    private Integer index;

    @CsvCell(title = "员工工号", index = 1, fieldNo = 1)
    @ApiModelProperty(value = "营业员编码")
    private String sellerCode;
    @CsvCell(title = "员工姓名", index = 2, fieldNo = 1)
    @ApiModelProperty(value = "营业员名")
    private String sellerName;
    @CsvCell(title = "销售天数", index = 3, fieldNo = 1)
    @ApiModelProperty(value = "销售日期")
    private String salesDays ="0";
    @ApiModelProperty(value = "来客数")
    private String customersAmt = "0";
    @ApiModelProperty(value = "日均来客数")
    private String daliyAveCustomersNum = "0";
    @CsvCell(title = "客单价", index = 6, fieldNo = 1)
    @ApiModelProperty(value = "客单价")
    private String visitUnitPrice = "0";
    @CsvCell(title = "实收金额", index = 4, fieldNo = 1)
    @ApiModelProperty(value = "实收金额")
    private BigDecimal ssAmount = BigDecimal.ZERO;
    @ApiModelProperty(value = "日均实收金额")
    private String daliygsddRmbAmt = "0";
    @ApiModelProperty(value = "折扣金额")
    private String discountAmt = "0";
    @ApiModelProperty(value = "折扣率")
    private String discountRate = "0.00%";
    @ApiModelProperty(value = "毛利额")
    private String grossProfitAmt = "0";
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate = "0.00%";
    @ApiModelProperty(value = "应收金额")
    private String receivableAmt = "0";
    @ApiModelProperty(value = "销售天数")
    private String selledDays ="0";

    @CsvCell(title = "交易次数", index = 5, fieldNo = 1)
    @ApiModelProperty(value = "交易次数")
    private String tradedTime ="0";
    @ApiModelProperty(value = "客品次")
    private String proAvgCount = "0";
    @ApiModelProperty(value = "品单价")
    private String billAvgPrice = "0";
    @ApiModelProperty(value = "会员销售额")
    private String memeberSaleAmt = "0";
    @ApiModelProperty(value = "会员销售占比")
    private String memeberSaleRate = "0";
    @ApiModelProperty(value = "含税成本额")
    private String allCostAmt = "0";
    @ApiModelProperty(value = "去税成本额")
    private String costAmt = "0";
    @ApiModelProperty(value = "日均销售金额")
    private String dailyPayAmt = "0";
    @ApiModelProperty(value = "日均交易次数")
    private String dailyPayCount ="0";

    private String flag;
}

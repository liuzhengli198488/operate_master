package com.gys.report.entity;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
@CsvRow("日期销售折扣汇总表")
public class StoreBudgetPlanOutData {
    @ApiModelProperty(value = "销售日期")
    @CsvCell(title = "销售日期", index = 1, fieldNo = 1)
    private String saleDate;
    @ApiModelProperty(value = "门店数量")
    @CsvCell(title = "门店数量", index = 2, fieldNo = 1)
    private BigDecimal stoCount;
    @ApiModelProperty(value = "税率")
    private BigDecimal stoTaxRate;
    @ApiModelProperty(value = "税率")
    @CsvCell(title = "税率", index = 3, fieldNo = 1)
    private String stoTaxRates;
    @ApiModelProperty(value = "成本额")
    @CsvCell(title = "成本额", index = 4, fieldNo = 1)
    private BigDecimal movPrices;
    @ApiModelProperty(value = "应收金额")
    @CsvCell(title = "应收金额", index = 5, fieldNo = 1)
    private BigDecimal amt;
    @ApiModelProperty(value = "实收金额")
    @CsvCell(title = "实收金额", index = 6, fieldNo = 1)
    private BigDecimal amountReceivable;
    @ApiModelProperty(value = "毛利额")
    @CsvCell(title = "毛利额", index = 7, fieldNo = 1)
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;
    @ApiModelProperty(value = "毛利率")
    @CsvCell(title = "毛利率", index = 8, fieldNo = 1)
    private String grossProfitRates;
    @ApiModelProperty(value = "折扣总额")
    @CsvCell(title = "折扣总额", index = 9, fieldNo = 1)
    private BigDecimal discountAmt;
}

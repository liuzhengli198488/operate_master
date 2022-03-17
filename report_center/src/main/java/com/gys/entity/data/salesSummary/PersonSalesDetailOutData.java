package com.gys.entity.data.salesSummary;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 人员销售明细查询 出参
 * @Author huxinxin
 * @Date 2021/5/20 15:56
 * @Version 1.0.0
 **/
@Data
@CsvRow("员工销售汇总数据")
public class PersonSalesDetailOutData {
    @CsvCell(title = "人员编码", index = 1, fieldNo = 1)
    private String userCode;  //人员编码
    @CsvCell(title = "人员姓名", index = 2, fieldNo = 1)
    private String userName;  //人员姓名
    @CsvCell(title = "门店编码", index = 3, fieldNo = 1)
    private String stoCode;   //门店编码
    @CsvCell(title = "门店名称", index = 4, fieldNo = 1)
    private String stoName;   //门店名称
    @CsvCell(title = "销售日期", index = 5, fieldNo = 1)
    private String saleDate;     //销售日期
    @CsvCell(title = "应收金额", index = 6, fieldNo = 1)
    private BigDecimal amountReceivable;  //应收金额
    @CsvCell(title = "实收金额", index = 7, fieldNo = 1)
    private BigDecimal amt;   //实收金额
    @CsvCell(title = "交易次数", index = 8, fieldNo = 1)
    private BigDecimal numberTrades;   //交易次数
    @CsvCell(title = "客单价", index = 9, fieldNo = 1)
    private BigDecimal perTicketSales;  //客单价
    @CsvCell(title = "折扣金额", index = 10, fieldNo = 1)
    private BigDecimal discountAmt;   //折扣金额
    @CsvCell(title = "折扣率", index = 11, fieldNo = 1)
    private BigDecimal discountRate;  //折扣率
//    @CsvCell(title = "日均销售额", index = 12, fieldNo = 1)
//    private BigDecimal amtByDay;  //日均销售额
//    @CsvCell(title = "日均交易次数", index = 13, fieldNo = 1)
//    private BigDecimal numberTradesByDay;  //日均交易次数
    @CsvCell(title = "会员销售金额", index = 14, fieldNo = 1)
    private BigDecimal memberSale;   //会员销售金额
    @CsvCell(title = "会员销售占比", index = 15, fieldNo = 1)
    private BigDecimal memberSaleRate;   //会员销售占比
    @CsvCell(title = "成本额", index = 16, fieldNo = 1)
    private BigDecimal includeTaxSale;   //成本额
    @CsvCell(title = "毛利额", index = 17, fieldNo = 1)
    private BigDecimal grossProfitMargin;  //毛利额
    @CsvCell(title = "毛利率", index = 18, fieldNo = 1)
    private BigDecimal grossProfitRate;  //毛利率
    @CsvCell(title = "客品次", index = 19, fieldNo = 1)
    private BigDecimal kpc;  //客品次
    @CsvCell(title = "品单价", index = 20, fieldNo = 1)
    private BigDecimal pdj;  //品单价
}

package com.gys.entity.data.salesSummary;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description 人员销售汇总查询 合计
 * @Author huxinxin
 * @Date 2021/5/20 16:17
 * @Version 1.0.0
 **/
@Data
public class PersonSalesOutDataTotal {
    private int saleDays;     //销售日期
    private BigDecimal amountReceivable;  //应收金额
    private BigDecimal amt;   //实收金额
    private BigDecimal numberTrades;   //交易次数
    private BigDecimal perTicketSales;  //客单价
    private BigDecimal discountAmt;   //折扣金额
    private String discountRate;  //折扣率
    private BigDecimal amtByDay;  //日均销售额
    private BigDecimal numberTradesByDay;  //日均交易次数
    private BigDecimal memberSale;   //会员销售金额
    private String memberSaleRate;   //会员销售占比
    private BigDecimal includeTaxSale;   //成本额
    private BigDecimal grossProfitMargin;  //毛利额
    private String grossProfitRate;  //毛利率
    private BigDecimal kpc;//客品次
    private BigDecimal pdj;//品单价
    private BigDecimal ctmAmt; //中药(散装)销售
    private BigDecimal ctmGrossProfitMargin; //中药(散装)毛利额
    private String ctmGrossProfitRate; //中药(散装)毛利率
}

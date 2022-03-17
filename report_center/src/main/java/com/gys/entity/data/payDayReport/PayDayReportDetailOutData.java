package com.gys.entity.data.payDayReport;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayDayReportDetailOutData {
    /**
    对账单号
     */
    private String voucherId;
    /**
    支付编码
     */
    private String gspddPayType;
    /**
    支付名称
     */
    private String gspddPayName;
    /**
    销售应收
     */
    private BigDecimal saleAmount;
    /**
    储值卡充值
     */
    private BigDecimal cardAmount;
     /**
    对账金额
     */
    private BigDecimal saleinputAmt;
     /***
    差异
     */
    private BigDecimal saleDifference;
}

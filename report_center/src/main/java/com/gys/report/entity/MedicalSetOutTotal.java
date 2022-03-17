package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *  医保结算
 */
@Data
public class MedicalSetOutTotal implements Serializable {

    /**
     * 合计医疗费总额
     */
    private BigDecimal medfeeSumant;

    /**
     * 合计全自费金额
     */
    private BigDecimal fulamtOwnpayAmt;

    /**
     * 合计超限价自费费用
     */
    private BigDecimal overlmtSelfpay;

    /**
     * 合计先行自付金额
     */
    private BigDecimal preselfpayAmt;

    /**
     * 合计符合政策范围金额
     */
    private BigDecimal inscpScpAmt;

    /**
     * 合计实际支付起付线
     */
    private BigDecimal actPayDedc;

    /**
     * 合计基本医疗保险统筹基金支出
     */
    private BigDecimal hifpPay;

    /**
     * 合计公务员医疗补助资金支出
     */
    private BigDecimal cvlservPay;

    /**
     * 合计企业补充医疗保险基金支出
     */
    private BigDecimal hifesPay;

    /**
     * 合计居民大病保险资金支出
     */
    private BigDecimal hifmiPay;

    /**
     * 合计职工大额医疗费用补助基金支出
     */
    private BigDecimal hifobPay;

    /**
     * 合计医疗救助基金支出
     */
    private BigDecimal mafPay;

    /**
     * 合计其他支出
     */
    private BigDecimal othPay;

    /**
     * 合计基金支付总额
     */
    private BigDecimal fundPaySumamt;

    /**
     * 合计个人负担总金额
     */
    private BigDecimal psnPartAmt;

    /**
     * 合计个人账户支出
     */
    private BigDecimal acctPay;

    /**
     * 合计个人现金支付
     */
    private BigDecimal psnCashPayamt;

    /**
     * 合计余额
     */
    private BigDecimal balc;

    /**
     * 合计个人账户共济支付金额
     */
    private BigDecimal acctMulaidPay;

}

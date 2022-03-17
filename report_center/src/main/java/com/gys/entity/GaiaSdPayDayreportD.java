package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "GAIA_SD_PAY_DAYREPORT_D")
public class GaiaSdPayDayreportD implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 单号
     */
    @Id
    @Column(name = "GSPDD_VOUCHER_ID")
    private String gspddVoucherId;

    /**
     * 店号
     */
    @Id
    @Column(name = "GSPDD_BR_ID")
    private String gspddBrId;

    /**
     * 行号
     */
    @Id
    @Column(name = "GSPDD_SERIAL")
    private String gspddSerial;

    /**
     * 支付类型  0.对账金额 1.销售支付 2.储值卡充值 3.挂号支付 4.代销售
     */
    @Column(name = "GSPDD_PAY_TYPE")
    private String gspddPayType;

    /**
     * 支付方式
     */
    @Column(name = "GSPDD_SALE_PAYMETHOD_ID")
    private String gspddSalePaymethodId;

    /**
     * 应收金额/充值金额
     */
    @Column(name = "GSPDD_SALE_RECEIVABLE_AMT")
    private BigDecimal gspddSaleReceivableAmt;

    /**
     * 对账金额（总）暂停使用
     */
    @Column(name = "GSPDD_SALE_INPUT_AMT")
    private BigDecimal gspddSaleInputAmt;
    /**
     * 对账金额（总）暂停使用
     */
    @Column(name = "GSPDD_SALE_DIFFERENCE")
    private BigDecimal gspddSaleDifference;

    private static final long serialVersionUID = 1L;

    /**
     * 获取加盟商
     *
     * @return CLIENT - 加盟商
     */
    public String getClient() {
        return client;
    }

    /**
     * 设置加盟商
     *
     * @param client 加盟商
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * 获取单号
     *
     * @return GSPDD_VOUCHER_ID - 单号
     */
    public String getGspddVoucherId() {
        return gspddVoucherId;
    }

    /**
     * 设置单号
     *
     * @param gspddVoucherId 单号
     */
    public void setGspddVoucherId(String gspddVoucherId) {
        this.gspddVoucherId = gspddVoucherId;
    }

    /**
     * 获取店号
     *
     * @return GSPDD_BR_ID - 店号
     */
    public String getGspddBrId() {
        return gspddBrId;
    }

    /**
     * 设置店号
     *
     * @param gspddBrId 店号
     */
    public void setGspddBrId(String gspddBrId) {
        this.gspddBrId = gspddBrId;
    }

    /**
     * 获取行号
     *
     * @return GSPDD_SERIAL - 行号
     */
    public String getGspddSerial() {
        return gspddSerial;
    }

    /**
     * 设置行号
     *
     * @param gspddSerial 行号
     */
    public void setGspddSerial(String gspddSerial) {
        this.gspddSerial = gspddSerial;
    }

    /**
     * 获取支付类型  0.对账金额 1.销售支付 2.储值卡充值 3.挂号支付 4.代销售
     *
     * @return GSPDD_PAY_TYPE - 支付类型  0.对账金额 1.销售支付 2.储值卡充值 3.挂号支付 4.代销售
     */
    public String getGspddPayType() {
        return gspddPayType;
    }

    /**
     * 设置支付类型  0.对账金额 1.销售支付 2.储值卡充值 3.挂号支付 4.代销售
     *
     * @param gspddPayType 支付类型  0.对账金额 1.销售支付 2.储值卡充值 3.挂号支付 4.代销售
     */
    public void setGspddPayType(String gspddPayType) {
        this.gspddPayType = gspddPayType;
    }

    /**
     * 获取支付方式
     *
     * @return GSPDD_SALE_PAYMETHOD_ID - 支付方式
     */
    public String getGspddSalePaymethodId() {
        return gspddSalePaymethodId;
    }

    /**
     * 设置支付方式
     *
     * @param gspddSalePaymethodId 支付方式
     */
    public void setGspddSalePaymethodId(String gspddSalePaymethodId) {
        this.gspddSalePaymethodId = gspddSalePaymethodId;
    }

    /**
     * 获取应收金额/充值金额
     *
     * @return GSPDD_SALE_RECEIVABLE_AMT - 应收金额/充值金额
     */
    public BigDecimal getGspddSaleReceivableAmt() {
        return gspddSaleReceivableAmt;
    }

    /**
     * 设置应收金额/充值金额
     *
     * @param gspddSaleReceivableAmt 应收金额/充值金额
     */
    public void setGspddSaleReceivableAmt(BigDecimal gspddSaleReceivableAmt) {
        this.gspddSaleReceivableAmt = gspddSaleReceivableAmt;
    }

    /**
     * 获取对账金额（总）暂停使用
     *
     * @return GSPDD_SALE_INPUT_AMT - 对账金额（总）暂停使用
     */
    public BigDecimal getGspddSaleInputAmt() {
        return gspddSaleInputAmt;
    }

    /**
     * 设置对账金额（总）暂停使用
     *
     * @param gspddSaleInputAmt 对账金额（总）暂停使用
     */
    public void setGspddSaleInputAmt(BigDecimal gspddSaleInputAmt) {
        this.gspddSaleInputAmt = gspddSaleInputAmt;
    }

    public BigDecimal getGspddSaleDifference() {
        return gspddSaleDifference;
    }

    public void setGspddSaleDifference(BigDecimal gspddSaleDifference) {
        this.gspddSaleDifference = gspddSaleDifference;
    }
}
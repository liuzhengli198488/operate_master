package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Table(name = "GAIA_SD_SALE_PAY_MSG")
public class GaiaSdSalePayMsg implements Serializable {
    private static final long serialVersionUID = -5218981657845301268L;
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String client;

    /**
     * 店号
     */
    @Id
    @Column(name = "GSSPM_BR_ID")
    private String gsspmBrId;

    /**
     * 日期
     */
    @Id
    @Column(name = "GSSPM_DATE")
    private String gsspmDate;

    /**
     * 销售单号
     */
    @Id
    @Column(name = "GSSPM_BILL_NO")
    private String gsspmBillNo;

    /**
     * 支付编码
     */
    @Id
    @Column(name = "GSSPM_ID")
    private String gsspmId;

    /**
     * 支付类型 1.销售支付 2.储值卡充值 3.挂号支付
     */
    @Id
    @Column(name = "GSSPM_TYPE")
    private String gsspmType;

    /**
     * 支付名称
     */
    @Column(name = "GSSPM_NAME")
    private String gsspmName;

    /**
     * 支付卡号
     */
    @Column(name = "GSSPM_CARD_NO")
    private String gsspmCardNo;

    /**
     * 支付金额
     */
    @Column(name = "GSSPM_AMT")
    private BigDecimal gsspmAmt;

    /**
     * RMB收款金额
     */
    @Column(name = "GSSPM_RMB_AMT")
    private BigDecimal gsspmRmbAmt;

    /**
     * 找零金额
     */
    @Column(name = "GSSPM_ZL_AMT")
    private BigDecimal gsspmZlAmt;

    /**
     * 是否日结 0/空为否1为是
     */
    @Column(name = "GSSPM_DAYREPORT")
    private String gsspmDayreport;


    /**
     * 医保卡号
     */
    @Column(name = "GSSPM_MEDICAL_CARD")
    private String medicalCard;

    /**
     * 医保姓名
     */
    @Column(name = "GSSPM_MEDICAL_NAME")
    private String medicalName;

    /**
     * 账户余额
     */
    @Column(name = "GSSPM_ACCOUNT_BALANCE")
    private String accountBalance;


}
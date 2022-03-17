package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "GAIA_SD_PAY_DAYREPORT_H")
public class GaiaSdPayDayreportH implements Serializable {
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
    @Column(name = "GSPDH_VOUCHER_ID")
    private String gspdhVoucherId;

    /**
     * 店号
     */
    @Id
    @Column(name = "GSPDH_BR_ID")
    private String gspdhBrId;

    /**
     * 销售日期
     */
    @Column(name = "GSPDH_SALE_DATE")
    private String gspdhSaleDate;

    /**
     * 审核日期
     */
    @Column(name = "GSPDH_CHECK_DATE")
    private String gspdhCheckDate;

    /**
     * 审核时间
     */
    @Column(name = "GSPDH_CHECK_TIME")
    private String gspdhCheckTime;

    /**
     * 对账员工
     */
    @Column(name = "GSPDH_EMP")
    private String gspdhEmp;

    /**
     * 应收金额汇总
     */
    @Column(name = "GSPDHTOTAL_SALES_AMT")
    private BigDecimal gspdhtotalSalesAmt;

    /**
     * 对账金额汇总
     */
    @Column(name = "GSPDHTOTAL_INPUT_AMT")
    private BigDecimal gspdhtotalInputAmt;

    /**
     * 审核状态
     */
    @Column(name = "GSPDH_STATUS")
    private String gspdhStatus;

    /**
     * 是否生成凭证
     */
    @Column(name = "GSPDH_ACCOUNTING_FLAG")
    private String gspdhAccountingFlag;

    /**
     * 凭证号
     */
    @Column(name = "GSPDH_ACCOUNTING_NO")
    private String gspdhAccountingNo;

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
     * @return GSPDH_VOUCHER_ID - 单号
     */
    public String getGspdhVoucherId() {
        return gspdhVoucherId;
    }

    /**
     * 设置单号
     *
     * @param gspdhVoucherId 单号
     */
    public void setGspdhVoucherId(String gspdhVoucherId) {
        this.gspdhVoucherId = gspdhVoucherId;
    }

    /**
     * 获取店号
     *
     * @return GSPDH_BR_ID - 店号
     */
    public String getGspdhBrId() {
        return gspdhBrId;
    }

    /**
     * 设置店号
     *
     * @param gspdhBrId 店号
     */
    public void setGspdhBrId(String gspdhBrId) {
        this.gspdhBrId = gspdhBrId;
    }

    /**
     * 获取销售日期
     *
     * @return GSPDH_SALE_DATE - 销售日期
     */
    public String getGspdhSaleDate() {
        return gspdhSaleDate;
    }

    /**
     * 设置销售日期
     *
     * @param gspdhSaleDate 销售日期
     */
    public void setGspdhSaleDate(String gspdhSaleDate) {
        this.gspdhSaleDate = gspdhSaleDate;
    }

    /**
     * 获取审核日期
     *
     * @return GSPDH_CHECK_DATE - 审核日期
     */
    public String getGspdhCheckDate() {
        return gspdhCheckDate;
    }

    /**
     * 设置审核日期
     *
     * @param gspdhCheckDate 审核日期
     */
    public void setGspdhCheckDate(String gspdhCheckDate) {
        this.gspdhCheckDate = gspdhCheckDate;
    }

    /**
     * 获取审核时间
     *
     * @return GSPDH_CHECK_TIME - 审核时间
     */
    public String getGspdhCheckTime() {
        return gspdhCheckTime;
    }

    /**
     * 设置审核时间
     *
     * @param gspdhCheckTime 审核时间
     */
    public void setGspdhCheckTime(String gspdhCheckTime) {
        this.gspdhCheckTime = gspdhCheckTime;
    }

    /**
     * 获取对账员工
     *
     * @return GSPDH_EMP - 对账员工
     */
    public String getGspdhEmp() {
        return gspdhEmp;
    }

    /**
     * 设置对账员工
     *
     * @param gspdhEmp 对账员工
     */
    public void setGspdhEmp(String gspdhEmp) {
        this.gspdhEmp = gspdhEmp;
    }

    /**
     * 获取应收金额汇总
     *
     * @return GSPDHTOTAL_SALES_AMT - 应收金额汇总
     */
    public BigDecimal getGspdhtotalSalesAmt() {
        return gspdhtotalSalesAmt;
    }

    /**
     * 设置应收金额汇总
     *
     * @param gspdhtotalSalesAmt 应收金额汇总
     */
    public void setGspdhtotalSalesAmt(BigDecimal gspdhtotalSalesAmt) {
        this.gspdhtotalSalesAmt = gspdhtotalSalesAmt;
    }

    /**
     * 获取对账金额汇总
     *
     * @return GSPDHTOTAL_INPUT_AMT - 对账金额汇总
     */
    public BigDecimal getGspdhtotalInputAmt() {
        return gspdhtotalInputAmt;
    }

    /**
     * 设置对账金额汇总
     *
     * @param gspdhtotalInputAmt 对账金额汇总
     */
    public void setGspdhtotalInputAmt(BigDecimal gspdhtotalInputAmt) {
        this.gspdhtotalInputAmt = gspdhtotalInputAmt;
    }

    /**
     * 获取审核状态
     *
     * @return GSPDH_STATUS - 审核状态
     */
    public String getGspdhStatus() {
        return gspdhStatus;
    }

    /**
     * 设置审核状态
     *
     * @param gspdhStatus 审核状态
     */
    public void setGspdhStatus(String gspdhStatus) {
        this.gspdhStatus = gspdhStatus;
    }

    /**
     * 获取是否生成凭证
     *
     * @return GSPDH_ACCOUNTING_FLAG - 是否生成凭证
     */
    public String getGspdhAccountingFlag() {
        return gspdhAccountingFlag;
    }

    /**
     * 设置是否生成凭证
     *
     * @param gspdhAccountingFlag 是否生成凭证
     */
    public void setGspdhAccountingFlag(String gspdhAccountingFlag) {
        this.gspdhAccountingFlag = gspdhAccountingFlag;
    }

    /**
     * 获取凭证号
     *
     * @return GSPDH_ACCOUNTING_NO - 凭证号
     */
    public String getGspdhAccountingNo() {
        return gspdhAccountingNo;
    }

    /**
     * 设置凭证号
     *
     * @param gspdhAccountingNo 凭证号
     */
    public void setGspdhAccountingNo(String gspdhAccountingNo) {
        this.gspdhAccountingNo = gspdhAccountingNo;
    }
}
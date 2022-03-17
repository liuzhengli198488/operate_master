package com.gys.report.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "GAIA_SD_REPLENISH_COMPARED_D")
public class GaiaSdReplenishComparedD implements Serializable {
    @Id
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    @Id
    @Column(name = "GSRD_BR_ID")
    private String gsrdBrId;

    @Id
    @Column(name = "GSRD_VOUCHER_ID")
    private String gsrdVoucherId;

    @Id
    @Column(name = "GSRD_PRO_ID")
    private String gsrdProId;

    @Column(name = "GSRD_DATE")
    private String gsrdDate;

    @Column(name = "GSRD_PROPOSE_QTY")
    private BigDecimal gsrdProposeQty;

    @Column(name = "GSRD_NEED_QTY")
    private BigDecimal gsrdNeedQty;

    @Column(name = "GSRD_STATUS")
    private String gsrdStatus;

    @Column(name = "GSRD_PO_PRC")
    private BigDecimal gsrdPoPrc;

    @Column(name = "GSRD_ACC_PRC")
    private BigDecimal gsrdAccPrc;

    @Column(name = "GSRD_CR_DATE")
    private String gsrdCrDate;

    @Column(name = "GSRD_CR_TIME")
    private String gsrdCrTime;

    @Column(name = "GSRD_CR_ID")
    private String gsrdCrId;

    private static final long serialVersionUID = 1L;

    public BigDecimal getGsrdPoPrc() {
        return gsrdPoPrc;
    }

    public void setGsrdPoPrc(BigDecimal gsrdPoPrc) {
        this.gsrdPoPrc = gsrdPoPrc;
    }

    public BigDecimal getGsrdAccPrc() {
        return gsrdAccPrc;
    }

    public void setGsrdAccPrc(BigDecimal gsrdAccPrc) {
        this.gsrdAccPrc = gsrdAccPrc;
    }

    /**
     * @return CLIENT
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * @return GSRD_BR_ID
     */
    public String getGsrdBrId() {
        return gsrdBrId;
    }

    /**
     * @param gsrdBrId
     */
    public void setGsrdBrId(String gsrdBrId) {
        this.gsrdBrId = gsrdBrId;
    }

    /**
     * @return GSRD_VOUCHER_ID
     */
    public String getGsrdVoucherId() {
        return gsrdVoucherId;
    }

    /**
     * @param gsrdVoucherId
     */
    public void setGsrdVoucherId(String gsrdVoucherId) {
        this.gsrdVoucherId = gsrdVoucherId;
    }

    /**
     * @return GSRD_PRO_ID
     */
    public String getGsrdProId() {
        return gsrdProId;
    }

    /**
     * @param gsrdProId
     */
    public void setGsrdProId(String gsrdProId) {
        this.gsrdProId = gsrdProId;
    }

    /**
     * @return GSRD_DATE
     */
    public String getGsrdDate() {
        return gsrdDate;
    }

    /**
     * @param gsrdDate
     */
    public void setGsrdDate(String gsrdDate) {
        this.gsrdDate = gsrdDate;
    }

    /**
     * @return GSRD_PROPOSE_QTY
     */
    public BigDecimal getGsrdProposeQty() {
        return gsrdProposeQty;
    }

    /**
     * @param gsrdProposeQty
     */
    public void setGsrdProposeQty(BigDecimal gsrdProposeQty) {
        this.gsrdProposeQty = gsrdProposeQty;
    }

    /**
     * @return GSRD_NEED_QTY
     */
    public BigDecimal getGsrdNeedQty() {
        return gsrdNeedQty;
    }

    /**
     * @param gsrdNeedQty
     */
    public void setGsrdNeedQty(BigDecimal gsrdNeedQty) {
        this.gsrdNeedQty = gsrdNeedQty;
    }

    /**
     * @return GSRD_STATUS
     */
    public String getGsrdStatus() {
        return gsrdStatus;
    }

    /**
     * @param gsrdStatus
     */
    public void setGsrdStatus(String gsrdStatus) {
        this.gsrdStatus = gsrdStatus;
    }

    /**
     * @return GSRD_CR_DATE
     */
    public String getGsrdCrDate() {
        return gsrdCrDate;
    }

    /**
     * @param gsrdCrDate
     */
    public void setGsrdCrDate(String gsrdCrDate) {
        this.gsrdCrDate = gsrdCrDate;
    }

    /**
     * @return GSRD_CR_TIME
     */
    public String getGsrdCrTime() {
        return gsrdCrTime;
    }

    /**
     * @param gsrdCrTime
     */
    public void setGsrdCrTime(String gsrdCrTime) {
        this.gsrdCrTime = gsrdCrTime;
    }

    /**
     * @return GSRD_CR_ID
     */
    public String getGsrdCrId() {
        return gsrdCrId;
    }

    /**
     * @param gsrdCrId
     */
    public void setGsrdCrId(String gsrdCrId) {
        this.gsrdCrId = gsrdCrId;
    }
}
package com.gys.entity;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_SD_ELECTRON_CHANGE")
@CsvRow
public class GaiaSdElectronChange implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String client;

    /**
     * 会员卡号
     */
    @CsvCell(title = "会员卡号", index = 11, fieldNo = 1)
    @Id
    @Column(name = "GSEC_MEMBER_ID")
    private String gsecMemberId;

    /**
     * 电子券号
     */
    @Id
    @Column(name = "GSEC_ID")
    private String gsecId;

    /**
     * 电子券活动号
     */
    @CsvCell(title = "电子券活动号", index = 12, fieldNo = 1)
    @Id
    @Column(name = "GSEB_ID")
    private String gsebId;

    /**
     * 是否使用  N为否，Y为是
     */
    @CsvCell(title = "是否使用", index = 7, fieldNo = 1)
    @Id
    @Column(name = "GSEC_STATUS")
    private String gsecStatus;

    /**
     * 电子券金额
     */
    @CsvCell(title = "电子券金额", index = 13, fieldNo = 1)
    @Column(name = "GSEC_AMT")
    private BigDecimal gsecAmt;

    /**
     * 电子券描述
     */
    @CsvCell(title = "电子券描述", index = 14, fieldNo = 1)
    @Column(name = "GSEB_NAME")
    private String gsebName;

    /**
     * 途径  0为线下，1为线上
     */
    @Column(name = "GSEC_FLAG")
    private String gsecFlag;

    /**
     * 用券销售单号
     */
    @CsvCell(title = "用券销售单号", index = 5, fieldNo = 1)
    @Column(name = "GSEC_BILL_NO")
    private String gsecBillNo;

    /**
     * 销售门店
     */
    @CsvCell(title = "用券门店", index = 4, fieldNo = 1)
    @Column(name = "GSEC_BR_ID")
    private String gsecBrId;

    /**
     * 销售日期
     */
    @CsvCell(title = "用券销售日期", index = 6, fieldNo = 1)
    @Column(name = "GSEC_SALE_DATE")
    private String gsecSaleDate;

    /**
     * 创建日期
     */
    @Column(name = "GSEC_CREATE_DATE")
    private String gsecCreateDate;

    /**
     * 失效日期
     */
    @Column(name = "GSEC_FAIL_DATE")
    private String gsecFailDate;

    /**
     * 送券销售单号
     */
    @CsvCell(title = "送券销售单号", index = 2, fieldNo = 1)
    @Column(name = "GSEC_GIVE_BILL_NO")
    private String gsecGiveBillNo;

    /**
     * 用券截止日期
     */
    @Column(name = "GSEC_USE_EXPIRATION_DATE")
    private String gsecUseExpirationDate;

    /**
     * 主题单号
     */
    @CsvCell(title = "主题单号", index = 8, fieldNo = 1)
    @Column(name = "GSETS_ID")
    private String gsetsId;

    /**
     * 主题属性（CM=消费型，NM=新会员，FM=老会员）
     */
    @CsvCell(title = "主题属性", index = 9, fieldNo = 1)
    @Column(name = "GSETS_ATTRIBUTE")
    private String gsetsAttribute;

    /**
     * 主题描述
     */
    @CsvCell(title = "主题描述", index = 10, fieldNo = 1)
    @Column(name = "GSETS_DESCRIBE")
    private String gsetsDescribe;

    /**
     * 送券销售门店
     */
    @CsvCell(title = "送券门店", index = 1, fieldNo = 1)
    @Column(name = "GSEC_GIVE_BR_ID")
    private String gsecGiveBrId;

    /**
     * 送券销售日期
     */
    @CsvCell(title = "送券销售日期", index = 3, fieldNo = 1)
    @Column(name = "GSEC_GIVE_SALE_DATE")
    private Date gsecGiveSaleDate;

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
     * 获取会员卡号
     *
     * @return GSEC_MEMBER_ID - 会员卡号
     */
    public String getGsecMemberId() {
        return gsecMemberId;
    }

    /**
     * 设置会员卡号
     *
     * @param gsecMemberId 会员卡号
     */
    public void setGsecMemberId(String gsecMemberId) {
        this.gsecMemberId = gsecMemberId;
    }

    /**
     * 获取电子券号
     *
     * @return GSEC_ID - 电子券号
     */
    public String getGsecId() {
        return gsecId;
    }

    /**
     * 设置电子券号
     *
     * @param gsecId 电子券号
     */
    public void setGsecId(String gsecId) {
        this.gsecId = gsecId;
    }

    /**
     * 获取电子券活动号
     *
     * @return GSEB_ID - 电子券活动号
     */
    public String getGsebId() {
        return gsebId;
    }

    /**
     * 设置电子券活动号
     *
     * @param gsebId 电子券活动号
     */
    public void setGsebId(String gsebId) {
        this.gsebId = gsebId;
    }

    /**
     * 获取是否使用  N为否，Y为是
     *
     * @return GSEC_STATUS - 是否使用  N为否，Y为是
     */
    public String getGsecStatus() {
        return gsecStatus;
    }

    /**
     * 设置是否使用  N为否，Y为是
     *
     * @param gsecStatus 是否使用  N为否，Y为是
     */
    public void setGsecStatus(String gsecStatus) {
        this.gsecStatus = gsecStatus;
    }

    /**
     * 获取电子券金额
     *
     * @return GSEC_AMT - 电子券金额
     */
    public BigDecimal getGsecAmt() {
        return gsecAmt;
    }

    /**
     * 设置电子券金额
     *
     * @param gsecAmt 电子券金额
     */
    public void setGsecAmt(BigDecimal gsecAmt) {
        this.gsecAmt = gsecAmt;
    }

    /**
     * 获取电子券描述
     *
     * @return GSEB_NAME - 电子券描述
     */
    public String getGsebName() {
        return gsebName;
    }

    /**
     * 设置电子券描述
     *
     * @param gsebName 电子券描述
     */
    public void setGsebName(String gsebName) {
        this.gsebName = gsebName;
    }

    /**
     * 获取途径  0为线下，1为线上
     *
     * @return GSEC_FLAG - 途径  0为线下，1为线上
     */
    public String getGsecFlag() {
        return gsecFlag;
    }

    /**
     * 设置途径  0为线下，1为线上
     *
     * @param gsecFlag 途径  0为线下，1为线上
     */
    public void setGsecFlag(String gsecFlag) {
        this.gsecFlag = gsecFlag;
    }

    /**
     * 获取用券销售单号
     *
     * @return GSEC_BILL_NO - 用券销售单号
     */
    public String getGsecBillNo() {
        return gsecBillNo;
    }

    /**
     * 设置用券销售单号
     *
     * @param gsecBillNo 用券销售单号
     */
    public void setGsecBillNo(String gsecBillNo) {
        this.gsecBillNo = gsecBillNo;
    }

    /**
     * 获取销售门店
     *
     * @return GSEC_BR_ID - 销售门店
     */
    public String getGsecBrId() {
        return gsecBrId;
    }

    /**
     * 设置销售门店
     *
     * @param gsecBrId 销售门店
     */
    public void setGsecBrId(String gsecBrId) {
        this.gsecBrId = gsecBrId;
    }

    /**
     * 获取销售日期
     *
     * @return GSEC_SALE_DATE - 销售日期
     */
    public String getGsecSaleDate() {
        return gsecSaleDate;
    }

    /**
     * 设置销售日期
     *
     * @param gsecSaleDate 销售日期
     */
    public void setGsecSaleDate(String gsecSaleDate) {
        this.gsecSaleDate = gsecSaleDate;
    }

    /**
     * 获取创建日期
     *
     * @return GSEC_CREATE_DATE - 创建日期
     */
    public String getGsecCreateDate() {
        return gsecCreateDate;
    }

    /**
     * 设置创建日期
     *
     * @param gsecCreateDate 创建日期
     */
    public void setGsecCreateDate(String gsecCreateDate) {
        this.gsecCreateDate = gsecCreateDate;
    }

    /**
     * 获取失效日期
     *
     * @return GSEC_FAIL_DATE - 失效日期
     */
    public String getGsecFailDate() {
        return gsecFailDate;
    }

    /**
     * 设置失效日期
     *
     * @param gsecFailDate 失效日期
     */
    public void setGsecFailDate(String gsecFailDate) {
        this.gsecFailDate = gsecFailDate;
    }

    /**
     * 获取送券销售单号
     *
     * @return GSEC_GIVE_BILL_NO - 送券销售单号
     */
    public String getGsecGiveBillNo() {
        return gsecGiveBillNo;
    }

    /**
     * 设置送券销售单号
     *
     * @param gsecGiveBillNo 送券销售单号
     */
    public void setGsecGiveBillNo(String gsecGiveBillNo) {
        this.gsecGiveBillNo = gsecGiveBillNo;
    }

    /**
     * 获取用券截止日期
     *
     * @return GSEC_USE_EXPIRATION_DATE - 用券截止日期
     */
    public String getGsecUseExpirationDate() {
        return gsecUseExpirationDate;
    }

    /**
     * 设置用券截止日期
     *
     * @param gsecUseExpirationDate 用券截止日期
     */
    public void setGsecUseExpirationDate(String gsecUseExpirationDate) {
        this.gsecUseExpirationDate = gsecUseExpirationDate;
    }

    /**
     * 获取主题单号
     *
     * @return GSETS_ID - 主题单号
     */
    public String getGsetsId() {
        return gsetsId;
    }

    /**
     * 设置主题单号
     *
     * @param gsetsId 主题单号
     */
    public void setGsetsId(String gsetsId) {
        this.gsetsId = gsetsId;
    }

    /**
     * 获取主题属性
     *
     * @return GSETS_ATTRIBUTE - 主题属性
     */
    public String getGsetsAttribute() {
        return gsetsAttribute;
    }

    /**
     * 设置主题属性
     *
     * @param gsetsAttribute 主题属性
     */
    public void setGsetsAttribute(String gsetsAttribute) {
        this.gsetsAttribute = gsetsAttribute;
    }

    /**
     * 获取主题描述
     *
     * @return GSETS_DESCRIBE - 主题描述
     */
    public String getGsetsDescribe() {
        return gsetsDescribe;
    }

    /**
     * 设置主题描述
     *
     * @param gsetsDescribe 主题描述
     */
    public void setGsetsDescribe(String gsetsDescribe) {
        this.gsetsDescribe = gsetsDescribe;
    }

    /**
     * 获取送券销售门店
     *
     * @return GSEC_GIVE_BR_ID - 送券销售门店
     */
    public String getGsecGiveBrId() {
        return gsecGiveBrId;
    }

    /**
     * 设置送券销售门店
     *
     * @param gsecGiveBrId 送券销售门店
     */
    public void setGsecGiveBrId(String gsecGiveBrId) {
        this.gsecGiveBrId = gsecGiveBrId;
    }

    /**
     * 获取送券销售日期
     *
     * @return GSEC_GIVE_SALE_DATE - 送券销售日期
     */
    public Date getGsecGiveSaleDate() {
        return gsecGiveSaleDate;
    }

    /**
     * 设置送券销售日期
     *
     * @param gsecGiveSaleDate 送券销售日期
     */
    public void setGsecGiveSaleDate(Date gsecGiveSaleDate) {
        this.gsecGiveSaleDate = gsecGiveSaleDate;
    }
}
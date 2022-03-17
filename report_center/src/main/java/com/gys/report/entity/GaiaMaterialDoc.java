package com.gys.report.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "GAIA_MATERIAL_DOC")
public class GaiaMaterialDoc implements Serializable {
    @Id
    @Column(name = "CLIENT")
    private String client;

    /**
     * 物料凭证号
     */
    @Id
    @Column(name = "MAT_ID")
    private String matId;

    /**
     * 物料凭证年份
     */
    @Id
    @Column(name = "MAT_YEAR")
    private String matYear;

    /**
     * 物料凭证行号
     */
    @Id
    @Column(name = "MAT_LINE_NO")
    private String matLineNo;

    /**
     * 物料凭证类型
     */
    @Column(name = "MAT_TYPE")
    private String matType;

    /**
     * 凭证日期
     */
    @Column(name = "MAT_DOC_DATE")
    private String matDocDate;

    /**
     * 过账日期
     */
    @Column(name = "MAT_POST_DATE")
    private String matPostDate;

    /**
     * 抬头备注
     */
    @Column(name = "MAT_HEAD_REMARK")
    private String matHeadRemark;

    /**
     * 商品编码
     */
    @Column(name = "MAT_PRO_CODE")
    private String matProCode;

    /**
     * 地点
     */
    @Column(name = "MAT_SITE_CODE")
    private String matSiteCode;

    /**
     * 库存地点
     */
    @Column(name = "MAT_LOCATION_CODE")
    private String matLocationCode;

    /**
     * 批次
     */
    @Column(name = "MAT_BATCH")
    private String matBatch;

    /**
     * 物料凭证数量
     */
    @Column(name = "MAT_QTY")
    private BigDecimal matQty;

    /**
     * 总金额（批次）
     */
    @Column(name = "MAT_BAT_AMT")
    private BigDecimal matBatAmt;

    /**
     * 基本计量单位
     */
    @Column(name = "MAT_UINT")
    private String matUint;

    /**
     * 总金额（移动）
     */
    @Column(name = "MAT_MOV_AMT")
    private BigDecimal matMovAmt;

    /**
     * 税金（批次）
     */
    @Column(name = "MAT_RATE_BAT")
    private BigDecimal matRateBat;

    /**
     * 税金（移动）
     */
    @Column(name = "MAT_RATE_MOV")
    private BigDecimal matRateMov;

    /**
     * 借/贷标识
     */
    @Column(name = "MAT_DEBIT_CREDIT")
    private String matDebitCredit;

    /**
     * 订单号
     */
    @Column(name = "MAT_PO_ID")
    private String matPoId;

    /**
     * 订单行号
     */
    @Column(name = "MAT_PO_LINENO")
    private String matPoLineno;

    /**
     * 业务单号
     */
    @Column(name = "MAT_DN_ID")
    private String matDnId;

    /**
     * 业务单行号
     */
    @Column(name = "MAT_DN_LINENO")
    private String matDnLineno;

    /**
     * 物料凭证行备注
     */
    @Column(name = "MAT_LINE_REMARK")
    private String matLineRemark;

    /**
     * 创建人
     */
    @Column(name = "MAT_CREATE_BY")
    private String matCreateBy;

    /**
     * 创建日期
     */
    @Column(name = "MAT_CREATE_DATE")
    private String matCreateDate;

    /**
     * 创建时间
     */
    @Column(name = "MAT_CREATE_TIME")
    private String matCreateTime;

    private static final long serialVersionUID = 1L;

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
     * 获取物料凭证号
     *
     * @return MAT_ID - 物料凭证号
     */
    public String getMatId() {
        return matId;
    }

    /**
     * 设置物料凭证号
     *
     * @param matId 物料凭证号
     */
    public void setMatId(String matId) {
        this.matId = matId;
    }

    /**
     * 获取物料凭证年份
     *
     * @return MAT_YEAR - 物料凭证年份
     */
    public String getMatYear() {
        return matYear;
    }

    /**
     * 设置物料凭证年份
     *
     * @param matYear 物料凭证年份
     */
    public void setMatYear(String matYear) {
        this.matYear = matYear;
    }

    /**
     * 获取物料凭证行号
     *
     * @return MAT_LINE_NO - 物料凭证行号
     */
    public String getMatLineNo() {
        return matLineNo;
    }

    /**
     * 设置物料凭证行号
     *
     * @param matLineNo 物料凭证行号
     */
    public void setMatLineNo(String matLineNo) {
        this.matLineNo = matLineNo;
    }

    /**
     * 获取物料凭证类型
     *
     * @return MAT_TYPE - 物料凭证类型
     */
    public String getMatType() {
        return matType;
    }

    /**
     * 设置物料凭证类型
     *
     * @param matType 物料凭证类型
     */
    public void setMatType(String matType) {
        this.matType = matType;
    }

    /**
     * 获取凭证日期
     *
     * @return MAT_DOC_DATE - 凭证日期
     */
    public String getMatDocDate() {
        return matDocDate;
    }

    /**
     * 设置凭证日期
     *
     * @param matDocDate 凭证日期
     */
    public void setMatDocDate(String matDocDate) {
        this.matDocDate = matDocDate;
    }

    /**
     * 获取过账日期
     *
     * @return MAT_POST_DATE - 过账日期
     */
    public String getMatPostDate() {
        return matPostDate;
    }

    /**
     * 设置过账日期
     *
     * @param matPostDate 过账日期
     */
    public void setMatPostDate(String matPostDate) {
        this.matPostDate = matPostDate;
    }

    /**
     * 获取抬头备注
     *
     * @return MAT_HEAD_REMARK - 抬头备注
     */
    public String getMatHeadRemark() {
        return matHeadRemark;
    }

    /**
     * 设置抬头备注
     *
     * @param matHeadRemark 抬头备注
     */
    public void setMatHeadRemark(String matHeadRemark) {
        this.matHeadRemark = matHeadRemark;
    }

    /**
     * 获取商品编码
     *
     * @return MAT_PRO_CODE - 商品编码
     */
    public String getMatProCode() {
        return matProCode;
    }

    /**
     * 设置商品编码
     *
     * @param matProCode 商品编码
     */
    public void setMatProCode(String matProCode) {
        this.matProCode = matProCode;
    }

    /**
     * 获取地点
     *
     * @return MAT_SITE_CODE - 地点
     */
    public String getMatSiteCode() {
        return matSiteCode;
    }

    /**
     * 设置地点
     *
     * @param matSiteCode 地点
     */
    public void setMatSiteCode(String matSiteCode) {
        this.matSiteCode = matSiteCode;
    }

    /**
     * 获取库存地点
     *
     * @return MAT_LOCATION_CODE - 库存地点
     */
    public String getMatLocationCode() {
        return matLocationCode;
    }

    /**
     * 设置库存地点
     *
     * @param matLocationCode 库存地点
     */
    public void setMatLocationCode(String matLocationCode) {
        this.matLocationCode = matLocationCode;
    }

    /**
     * 获取批次
     *
     * @return MAT_BATCH - 批次
     */
    public String getMatBatch() {
        return matBatch;
    }

    /**
     * 设置批次
     *
     * @param matBatch 批次
     */
    public void setMatBatch(String matBatch) {
        this.matBatch = matBatch;
    }

    /**
     * 获取物料凭证数量
     *
     * @return MAT_QTY - 物料凭证数量
     */
    public BigDecimal getMatQty() {
        return matQty;
    }

    /**
     * 设置物料凭证数量
     *
     * @param matQty 物料凭证数量
     */
    public void setMatQty(BigDecimal matQty) {
        this.matQty = matQty;
    }

    /**
     * 获取总金额（批次）
     *
     * @return MAT_BAT_AMT - 总金额（批次）
     */
    public BigDecimal getMatBatAmt() {
        return matBatAmt;
    }

    /**
     * 设置总金额（批次）
     *
     * @param matBatAmt 总金额（批次）
     */
    public void setMatBatAmt(BigDecimal matBatAmt) {
        this.matBatAmt = matBatAmt;
    }

    /**
     * 获取基本计量单位
     *
     * @return MAT_UINT - 基本计量单位
     */
    public String getMatUint() {
        return matUint;
    }

    /**
     * 设置基本计量单位
     *
     * @param matUint 基本计量单位
     */
    public void setMatUint(String matUint) {
        this.matUint = matUint;
    }

    /**
     * 获取总金额（移动）
     *
     * @return MAT_MOV_AMT - 总金额（移动）
     */
    public BigDecimal getMatMovAmt() {
        return matMovAmt;
    }

    /**
     * 设置总金额（移动）
     *
     * @param matMovAmt 总金额（移动）
     */
    public void setMatMovAmt(BigDecimal matMovAmt) {
        this.matMovAmt = matMovAmt;
    }

    /**
     * 获取税金（批次）
     *
     * @return MAT_RATE_BAT - 税金（批次）
     */
    public BigDecimal getMatRateBat() {
        return matRateBat;
    }

    /**
     * 设置税金（批次）
     *
     * @param matRateBat 税金（批次）
     */
    public void setMatRateBat(BigDecimal matRateBat) {
        this.matRateBat = matRateBat;
    }

    /**
     * 获取税金（移动）
     *
     * @return MAT_RATE_MOV - 税金（移动）
     */
    public BigDecimal getMatRateMov() {
        return matRateMov;
    }

    /**
     * 设置税金（移动）
     *
     * @param matRateMov 税金（移动）
     */
    public void setMatRateMov(BigDecimal matRateMov) {
        this.matRateMov = matRateMov;
    }

    /**
     * 获取借/贷标识
     *
     * @return MAT_DEBIT_CREDIT - 借/贷标识
     */
    public String getMatDebitCredit() {
        return matDebitCredit;
    }

    /**
     * 设置借/贷标识
     *
     * @param matDebitCredit 借/贷标识
     */
    public void setMatDebitCredit(String matDebitCredit) {
        this.matDebitCredit = matDebitCredit;
    }

    /**
     * 获取订单号
     *
     * @return MAT_PO_ID - 订单号
     */
    public String getMatPoId() {
        return matPoId;
    }

    /**
     * 设置订单号
     *
     * @param matPoId 订单号
     */
    public void setMatPoId(String matPoId) {
        this.matPoId = matPoId;
    }

    /**
     * 获取订单行号
     *
     * @return MAT_PO_LINENO - 订单行号
     */
    public String getMatPoLineno() {
        return matPoLineno;
    }

    /**
     * 设置订单行号
     *
     * @param matPoLineno 订单行号
     */
    public void setMatPoLineno(String matPoLineno) {
        this.matPoLineno = matPoLineno;
    }

    /**
     * 获取业务单号
     *
     * @return MAT_DN_ID - 业务单号
     */
    public String getMatDnId() {
        return matDnId;
    }

    /**
     * 设置业务单号
     *
     * @param matDnId 业务单号
     */
    public void setMatDnId(String matDnId) {
        this.matDnId = matDnId;
    }

    /**
     * 获取业务单行号
     *
     * @return MAT_DN_LINENO - 业务单行号
     */
    public String getMatDnLineno() {
        return matDnLineno;
    }

    /**
     * 设置业务单行号
     *
     * @param matDnLineno 业务单行号
     */
    public void setMatDnLineno(String matDnLineno) {
        this.matDnLineno = matDnLineno;
    }

    /**
     * 获取物料凭证行备注
     *
     * @return MAT_LINE_REMARK - 物料凭证行备注
     */
    public String getMatLineRemark() {
        return matLineRemark;
    }

    /**
     * 设置物料凭证行备注
     *
     * @param matLineRemark 物料凭证行备注
     */
    public void setMatLineRemark(String matLineRemark) {
        this.matLineRemark = matLineRemark;
    }

    /**
     * 获取创建人
     *
     * @return MAT_CREATE_BY - 创建人
     */
    public String getMatCreateBy() {
        return matCreateBy;
    }

    /**
     * 设置创建人
     *
     * @param matCreateBy 创建人
     */
    public void setMatCreateBy(String matCreateBy) {
        this.matCreateBy = matCreateBy;
    }

    /**
     * 获取创建日期
     *
     * @return MAT_CREATE_DATE - 创建日期
     */
    public String getMatCreateDate() {
        return matCreateDate;
    }

    /**
     * 设置创建日期
     *
     * @param matCreateDate 创建日期
     */
    public void setMatCreateDate(String matCreateDate) {
        this.matCreateDate = matCreateDate;
    }

    /**
     * 获取创建时间
     *
     * @return MAT_CREATE_TIME - 创建时间
     */
    public String getMatCreateTime() {
        return matCreateTime;
    }

    /**
     * 设置创建时间
     *
     * @param matCreateTime 创建时间
     */
    public void setMatCreateTime(String matCreateTime) {
        this.matCreateTime = matCreateTime;
    }
}
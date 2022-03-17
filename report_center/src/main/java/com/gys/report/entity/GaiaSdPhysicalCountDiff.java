package com.gys.report.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "GAIA_SD_PHYSICAL_COUNT_DIFF")
public class GaiaSdPhysicalCountDiff implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String clientId;

    /**
     * 差异单号
     */
    @Id
    @Column(name = "GSPCD_VOUCHER_ID")
    private String gspcdVoucherId;

    /**
     * 盘点单号
     */
    @Id
    @Column(name = "GSPC_VOUCHER_ID")
    private String gspcVoucherId;

    /**
     * 门店
     */
    @Id
    @Column(name = "GSPCD_BR_ID")
    private String gspcdBrId;

    /**
     * 日期
     */
    @Id
    @Column(name = "GSPCD_DATE")
    private String gspcdDate;

    /**
     * 类型
     */
    @Id
    @Column(name = "GSPCD_TYPE")
    private String gspcdType;

    /**
     * 时间
     */
    @Column(name = "GSPCD_TIME")
    private String gspcdTime;

    /**
     * 编码
     */
    @Id
    @Column(name = "GSPCD_PRO_ID")
    private String gspcdProId;

    /**
     * 批号
     */
    @Column(name = "GSPCD_BATCH_NO")
    private String gspcdBatchNo;

    /**
     * 库存数量
     */
    @Column(name = "GSPCD_STOCH_QTY")
    private String gspcdStochQty;

    /**
     * 盘点数量
     */
    @Column(name = "GSPCD_PC_FIRST_QTY")
    private String gspcdPcFirstQty;

    /**
     * 初盘差异
     */
    @Column(name = "GSPCD_DIFF_FIRST_QTY")
    private String gspcdDiffFirstQty;

    /**
     * 初盘修改
     */
    @Column(name = "GSPCD_PC_SECOND_QTY")
    private String gspcdPcSecondQty;

    /**
     * 复盘差异
     */
    @Column(name = "GSPCD_DIFF_SECOND_QTY")
    private String gspcdDiffSecondQty;

    /**
     * 审核日期
     */
    @Column(name = "GSPCD_EXAMINE_DATE")
    private String gspcdExamineDate;

    /**
     * 审核工号
     */
    @Column(name = "GSPCD_EXAMINE_EMP")
    private String gspcdExamineEmp;

    /**
     * 状态
     */
    @Column(name = "GSPCD_STATUS")
    private String gspcdStatus;

    /**
     * 是否转损益单
     */
    @Column(name = "GSPCD_IS_FLAG")
    private String gspcdIsFlag;

    private static final long serialVersionUID = 1L;

    /**
     * 获取加盟商
     *
     * @return CLIENT_ID - 加盟商
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * 设置加盟商
     *
     * @param clientId 加盟商
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * 获取差异单号
     *
     * @return GSPCD_VOUCHER_ID - 差异单号
     */
    public String getGspcdVoucherId() {
        return gspcdVoucherId;
    }

    /**
     * 设置差异单号
     *
     * @param gspcdVoucherId 差异单号
     */
    public void setGspcdVoucherId(String gspcdVoucherId) {
        this.gspcdVoucherId = gspcdVoucherId;
    }

    /**
     * 获取门店
     *
     * @return GSPCD_BR_ID - 门店
     */
    public String getGspcdBrId() {
        return gspcdBrId;
    }

    /**
     * 设置门店
     *
     * @param gspcdBrId 门店
     */
    public void setGspcdBrId(String gspcdBrId) {
        this.gspcdBrId = gspcdBrId;
    }

    /**
     * 获取日期
     *
     * @return GSPCD_DATE - 日期
     */
    public String getGspcdDate() {
        return gspcdDate;
    }

    /**
     * 设置日期
     *
     * @param gspcdDate 日期
     */
    public void setGspcdDate(String gspcdDate) {
        this.gspcdDate = gspcdDate;
    }

    /**
     * 获取类型
     *
     * @return GSPCD_TYPE - 类型
     */
    public String getGspcdType() {
        return gspcdType;
    }

    /**
     * 设置类型
     *
     * @param gspcdType 类型
     */
    public void setGspcdType(String gspcdType) {
        this.gspcdType = gspcdType;
    }

    public String getGspcVoucherId() {
        return gspcVoucherId;
    }

    public void setGspcVoucherId(String gspcVoucherId) {
        this.gspcVoucherId = gspcVoucherId;
    }

    /**
     * 获取时间
     *
     * @return GSPCD_TIME - 时间
     */
    public String getGspcdTime() {
        return gspcdTime;
    }

    /**
     * 设置时间
     *
     * @param gspcdTime 时间
     */
    public void setGspcdTime(String gspcdTime) {
        this.gspcdTime = gspcdTime;
    }

    /**
     * 获取编码
     *
     * @return GSPCD_PRO_ID - 编码
     */
    public String getGspcdProId() {
        return gspcdProId;
    }

    /**
     * 设置编码
     *
     * @param gspcdProId 编码
     */
    public void setGspcdProId(String gspcdProId) {
        this.gspcdProId = gspcdProId;
    }

    /**
     * 获取批号
     *
     * @return GSPCD_BATCH_NO - 批号
     */
    public String getGspcdBatchNo() {
        return gspcdBatchNo;
    }

    /**
     * 设置批号
     *
     * @param gspcdBatchNo 批号
     */
    public void setGspcdBatchNo(String gspcdBatchNo) {
        this.gspcdBatchNo = gspcdBatchNo;
    }

    /**
     * 获取库存数量
     *
     * @return GSPCD_STOCH_QTY - 库存数量
     */
    public String getGspcdStochQty() {
        return gspcdStochQty;
    }

    /**
     * 设置库存数量
     *
     * @param gspcdStochQty 库存数量
     */
    public void setGspcdStochQty(String gspcdStochQty) {
        this.gspcdStochQty = gspcdStochQty;
    }

    /**
     * 获取盘点数量
     *
     * @return GSPCD_PC_FIRST_QTY - 盘点数量
     */
    public String getGspcdPcFirstQty() {
        return gspcdPcFirstQty;
    }

    /**
     * 设置盘点数量
     *
     * @param gspcdPcFirstQty 盘点数量
     */
    public void setGspcdPcFirstQty(String gspcdPcFirstQty) {
        this.gspcdPcFirstQty = gspcdPcFirstQty;
    }

    /**
     * 获取初盘差异
     *
     * @return GSPCD_DIFF_FIRST_QTY - 初盘差异
     */
    public String getGspcdDiffFirstQty() {
        return gspcdDiffFirstQty;
    }

    /**
     * 设置初盘差异
     *
     * @param gspcdDiffFirstQty 初盘差异
     */
    public void setGspcdDiffFirstQty(String gspcdDiffFirstQty) {
        this.gspcdDiffFirstQty = gspcdDiffFirstQty;
    }

    /**
     * 获取初盘修改
     *
     * @return GSPCD_PC_SECOND_QTY - 初盘修改
     */
    public String getGspcdPcSecondQty() {
        return gspcdPcSecondQty;
    }

    /**
     * 设置初盘修改
     *
     * @param gspcdPcSecondQty 初盘修改
     */
    public void setGspcdPcSecondQty(String gspcdPcSecondQty) {
        this.gspcdPcSecondQty = gspcdPcSecondQty;
    }

    /**
     * 获取复盘差异
     *
     * @return GSPCD_DIFF_SECOND_QTY - 复盘差异
     */
    public String getGspcdDiffSecondQty() {
        return gspcdDiffSecondQty;
    }

    /**
     * 设置复盘差异
     *
     * @param gspcdDiffSecondQty 复盘差异
     */
    public void setGspcdDiffSecondQty(String gspcdDiffSecondQty) {
        this.gspcdDiffSecondQty = gspcdDiffSecondQty;
    }

    /**
     * 获取审核日期
     *
     * @return GSPCD_EXAMINE_DATE - 审核日期
     */
    public String getGspcdExamineDate() {
        return gspcdExamineDate;
    }

    /**
     * 设置审核日期
     *
     * @param gspcdExamineDate 审核日期
     */
    public void setGspcdExamineDate(String gspcdExamineDate) {
        this.gspcdExamineDate = gspcdExamineDate;
    }

    /**
     * 获取审核工号
     *
     * @return GSPCD_EXAMINE_EMP - 审核工号
     */
    public String getGspcdExamineEmp() {
        return gspcdExamineEmp;
    }

    /**
     * 设置审核工号
     *
     * @param gspcdExamineEmp 审核工号
     */
    public void setGspcdExamineEmp(String gspcdExamineEmp) {
        this.gspcdExamineEmp = gspcdExamineEmp;
    }

    /**
     * 获取状态
     *
     * @return GSPCD_STATUS - 状态
     */
    public String getGspcdStatus() {
        return gspcdStatus;
    }

    /**
     * 设置状态
     *
     * @param gspcdStatus 状态
     */
    public void setGspcdStatus(String gspcdStatus) {
        this.gspcdStatus = gspcdStatus;
    }

    /**
     * 获取是否转损益单
     *
     * @return GSPCD_IS_FLAG - 是否转损益单
     */
    public String getGspcdIsFlag() {
        return gspcdIsFlag;
    }

    /**
     * 设置是否转损益单
     *
     * @param gspcdIsFlag 是否转损益单
     */
    public void setGspcdIsFlag(String gspcdIsFlag) {
        this.gspcdIsFlag = gspcdIsFlag;
    }
}
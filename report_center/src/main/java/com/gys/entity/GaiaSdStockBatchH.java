package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "GAIA_SD_STOCK_BATCH_H")
public class GaiaSdStockBatchH implements Serializable {
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
    @Column(name = "GSSB_BR_ID")
    private String gssbBrId;

    /**
     * 商品编码
     */
    @Id
    @Column(name = "GSSB_PRO_ID")
    private String gssbProId;

    /**
     * 批号
     */
    @Id
    @Column(name = "GSSB_BATCH_NO")
    private String gssbBatchNo;

    /**
     * 批次
     */
    @Id
    @Column(name = "GSSB_BATCH")
    private String gssbBatch;

    /**
     * 年份
     */
    @Id
    @Column(name = "MAT_ASSESS_YEAR")
    private String matAssessYear;

    /**
     * 月份
     */
    @Id
    @Column(name = "MAT_ASSESS_MONTH")
    private String matAssessMonth;

    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private Long id;

    /**
     * 数量
     */
    @Column(name = "GSSB_QTY")
    private BigDecimal gssbQty;

    /**
     * 效期
     */
    @Column(name = "GSSB_VAILD_DATE")
    private String gssbVaildDate;

    /**
     * 更新日期
     */
    @Column(name = "GSSB_UPDATE_DATE")
    private String gssbUpdateDate;

    /**
     * 更新人员编号
     */
    @Column(name = "GSSB_UPDATE_EMP")
    private String gssbUpdateEmp;

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
     * 获取店号
     *
     * @return GSSB_BR_ID - 店号
     */
    public String getGssbBrId() {
        return gssbBrId;
    }

    /**
     * 设置店号
     *
     * @param gssbBrId 店号
     */
    public void setGssbBrId(String gssbBrId) {
        this.gssbBrId = gssbBrId;
    }

    /**
     * 获取商品编码
     *
     * @return GSSB_PRO_ID - 商品编码
     */
    public String getGssbProId() {
        return gssbProId;
    }

    /**
     * 设置商品编码
     *
     * @param gssbProId 商品编码
     */
    public void setGssbProId(String gssbProId) {
        this.gssbProId = gssbProId;
    }

    /**
     * 获取批号
     *
     * @return GSSB_BATCH_NO - 批号
     */
    public String getGssbBatchNo() {
        return gssbBatchNo;
    }

    /**
     * 设置批号
     *
     * @param gssbBatchNo 批号
     */
    public void setGssbBatchNo(String gssbBatchNo) {
        this.gssbBatchNo = gssbBatchNo;
    }

    /**
     * 获取批次
     *
     * @return GSSB_BATCH - 批次
     */
    public String getGssbBatch() {
        return gssbBatch;
    }

    /**
     * 设置批次
     *
     * @param gssbBatch 批次
     */
    public void setGssbBatch(String gssbBatch) {
        this.gssbBatch = gssbBatch;
    }

    /**
     * 获取年份
     *
     * @return MAT_ASSESS_YEAR - 年份
     */
    public String getMatAssessYear() {
        return matAssessYear;
    }

    /**
     * 设置年份
     *
     * @param matAssessYear 年份
     */
    public void setMatAssessYear(String matAssessYear) {
        this.matAssessYear = matAssessYear;
    }

    /**
     * 获取月份
     *
     * @return MAT_ASSESS_MONTH - 月份
     */
    public String getMatAssessMonth() {
        return matAssessMonth;
    }

    /**
     * 设置月份
     *
     * @param matAssessMonth 月份
     */
    public void setMatAssessMonth(String matAssessMonth) {
        this.matAssessMonth = matAssessMonth;
    }

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取数量
     *
     * @return GSSB_QTY - 数量
     */
    public BigDecimal getGssbQty() {
        return gssbQty;
    }

    /**
     * 设置数量
     *
     * @param gssbQty 数量
     */
    public void setGssbQty(BigDecimal gssbQty) {
        this.gssbQty = gssbQty;
    }

    /**
     * 获取效期
     *
     * @return GSSB_VAILD_DATE - 效期
     */
    public String getGssbVaildDate() {
        return gssbVaildDate;
    }

    /**
     * 设置效期
     *
     * @param gssbVaildDate 效期
     */
    public void setGssbVaildDate(String gssbVaildDate) {
        this.gssbVaildDate = gssbVaildDate;
    }

    /**
     * 获取更新日期
     *
     * @return GSSB_UPDATE_DATE - 更新日期
     */
    public String getGssbUpdateDate() {
        return gssbUpdateDate;
    }

    /**
     * 设置更新日期
     *
     * @param gssbUpdateDate 更新日期
     */
    public void setGssbUpdateDate(String gssbUpdateDate) {
        this.gssbUpdateDate = gssbUpdateDate;
    }

    /**
     * 获取更新人员编号
     *
     * @return GSSB_UPDATE_EMP - 更新人员编号
     */
    public String getGssbUpdateEmp() {
        return gssbUpdateEmp;
    }

    /**
     * 设置更新人员编号
     *
     * @param gssbUpdateEmp 更新人员编号
     */
    public void setGssbUpdateEmp(String gssbUpdateEmp) {
        this.gssbUpdateEmp = gssbUpdateEmp;
    }
}
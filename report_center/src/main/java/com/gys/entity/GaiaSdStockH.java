package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_SD_STOCK_H")
public class GaiaSdStockH implements Serializable {
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
    @Column(name = "GSS_BR_ID")
    private String gssBrId;

    /**
     * 商品编码
     */
    @Id
    @Column(name = "GSS_PRO_ID")
    private String gssProId;

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

    /**
     * 数量
     */
    @Column(name = "GSS_QTY")
    private BigDecimal gssQty;

    /**
     * 更新日期
     */
    @Column(name = "GSS_UPDATE_DATE")
    private String gssUpdateDate;

    /**
     * 更新人员编号
     */
    @Column(name = "GSS_UPDATE_EMP")
    private String gssUpdateEmp;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;

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
     * @return GSS_BR_ID - 店号
     */
    public String getGssBrId() {
        return gssBrId;
    }

    /**
     * 设置店号
     *
     * @param gssBrId 店号
     */
    public void setGssBrId(String gssBrId) {
        this.gssBrId = gssBrId;
    }

    /**
     * 获取商品编码
     *
     * @return GSS_PRO_ID - 商品编码
     */
    public String getGssProId() {
        return gssProId;
    }

    /**
     * 设置商品编码
     *
     * @param gssProId 商品编码
     */
    public void setGssProId(String gssProId) {
        this.gssProId = gssProId;
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
     * 获取数量
     *
     * @return GSS_QTY - 数量
     */
    public BigDecimal getGssQty() {
        return gssQty;
    }

    /**
     * 设置数量
     *
     * @param gssQty 数量
     */
    public void setGssQty(BigDecimal gssQty) {
        this.gssQty = gssQty;
    }

    /**
     * 获取更新日期
     *
     * @return GSS_UPDATE_DATE - 更新日期
     */
    public String getGssUpdateDate() {
        return gssUpdateDate;
    }

    /**
     * 设置更新日期
     *
     * @param gssUpdateDate 更新日期
     */
    public void setGssUpdateDate(String gssUpdateDate) {
        this.gssUpdateDate = gssUpdateDate;
    }

    /**
     * 获取更新人员编号
     *
     * @return GSS_UPDATE_EMP - 更新人员编号
     */
    public String getGssUpdateEmp() {
        return gssUpdateEmp;
    }

    /**
     * 设置更新人员编号
     *
     * @param gssUpdateEmp 更新人员编号
     */
    public void setGssUpdateEmp(String gssUpdateEmp) {
        this.gssUpdateEmp = gssUpdateEmp;
    }

    /**
     * @return LAST_UPDATE_TIME
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * @param lastUpdateTime
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
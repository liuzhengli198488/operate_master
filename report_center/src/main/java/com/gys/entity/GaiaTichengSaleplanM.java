package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_TICHENG_SALEPLAN_M")
public class GaiaTichengSaleplanM implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 销售提成方案主表主键ID
     */
    @Column(name = "PID")
    private Long pid;

    /**
     * 最小日均销售额
     */
    @Column(name = "MIN_DAILY_SALE_AMT")
    private BigDecimal minDailySaleAmt;

    /**
     * 最大日均销售额
     */
    @Column(name = "MAX_DAILY_SALE_AMT")
    private BigDecimal maxDailySaleAmt;

    /**
     * 毛利级别
     */
    @Column(name = "PRO_SALE_CLASS")
    private String proSaleClass;

    /**
     * 最小毛利率
     */
    @Column(name = "MIN_PRO_MLL")
    private BigDecimal minProMll;

    /**
     * 最大毛利率
     */
    @Column(name = "MAX_PRO_MLL")
    private BigDecimal maxProMll;

    /**
     * 提成比例
     */
    @Column(name = "TICHENG_SCALE")
    private BigDecimal tichengScale;

    /**
     * 操作状态 0 未删除 1 已删除
     */
    @Column(name = "DELETE_FLAG")
    private String deleteFlag;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键ID
     *
     * @return ID - 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

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
     * 获取销售提成方案主表主键ID
     *
     * @return PID - 销售提成方案主表主键ID
     */
    public Long getPid() {
        return pid;
    }

    /**
     * 设置销售提成方案主表主键ID
     *
     * @param pid 销售提成方案主表主键ID
     */
    public void setPid(Long pid) {
        this.pid = pid;
    }

    /**
     * 获取最小日均销售额
     *
     * @return MIN_DAILY_SALE_AMT - 最小日均销售额
     */
    public BigDecimal getMinDailySaleAmt() {
        return minDailySaleAmt;
    }

    /**
     * 设置最小日均销售额
     *
     * @param minDailySaleAmt 最小日均销售额
     */
    public void setMinDailySaleAmt(BigDecimal minDailySaleAmt) {
        this.minDailySaleAmt = minDailySaleAmt;
    }

    /**
     * 获取最大日均销售额
     *
     * @return MAX_DAILY_SALE_AMT - 最大日均销售额
     */
    public BigDecimal getMaxDailySaleAmt() {
        return maxDailySaleAmt;
    }

    /**
     * 设置最大日均销售额
     *
     * @param maxDailySaleAmt 最大日均销售额
     */
    public void setMaxDailySaleAmt(BigDecimal maxDailySaleAmt) {
        this.maxDailySaleAmt = maxDailySaleAmt;
    }

    /**
     * 获取毛利级别
     *
     * @return PRO_SALE_CLASS - 毛利级别
     */
    public String getProSaleClass() {
        return proSaleClass;
    }

    /**
     * 设置毛利级别
     *
     * @param proSaleClass 毛利级别
     */
    public void setProSaleClass(String proSaleClass) {
        this.proSaleClass = proSaleClass;
    }

    /**
     * 获取最小毛利率
     *
     * @return MIN_PRO_MLL - 最小毛利率
     */
    public BigDecimal getMinProMll() {
        return minProMll;
    }

    /**
     * 设置最小毛利率
     *
     * @param minProMll 最小毛利率
     */
    public void setMinProMll(BigDecimal minProMll) {
        this.minProMll = minProMll;
    }

    /**
     * 获取最大毛利率
     *
     * @return MAX_PRO_MLL - 最大毛利率
     */
    public BigDecimal getMaxProMll() {
        return maxProMll;
    }

    /**
     * 设置最大毛利率
     *
     * @param maxProMll 最大毛利率
     */
    public void setMaxProMll(BigDecimal maxProMll) {
        this.maxProMll = maxProMll;
    }

    /**
     * 获取提成比例
     *
     * @return TICHENG_SCALE - 提成比例
     */
    public BigDecimal getTichengScale() {
        return tichengScale;
    }

    /**
     * 设置提成比例
     *
     * @param tichengScale 提成比例
     */
    public void setTichengScale(BigDecimal tichengScale) {
        this.tichengScale = tichengScale;
    }

    /**
     * 获取操作状态 0 未删除 1 已删除
     *
     * @return DELETE_FLAG - 操作状态 0 未删除 1 已删除
     */
    public String getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 设置操作状态 0 未删除 1 已删除
     *
     * @param deleteFlag 操作状态 0 未删除 1 已删除
     */
    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
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
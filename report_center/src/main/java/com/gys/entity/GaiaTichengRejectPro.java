package com.gys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_TICHENG_REJECT_PRO")
public class GaiaTichengRejectPro implements Serializable {
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
     * 提成方案主表主键ID
     */
    @Column(name = "PID")
    private Long pid;

    /**
     * 商品编码
     */
    @Column(name = "PRO_CODE")
    private String proCode;

    /**
     * 商品名
     */
    @Column(name = "PRO_NAME")
    private String proName;

    /**
     * 规格
     */
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    /**
     * 生产企业
     */
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    /**
     * 成本价
     */
    @Column(name = "PRO_COST_PRICE")
    private BigDecimal proCostPrice;

    /**
     * 零售价
     */
    @Column(name = "PRO_PRICE_NORMAL")
    private BigDecimal proPriceNormal;

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
     * 获取提成方案主表主键ID
     *
     * @return PID - 提成方案主表主键ID
     */
    public Long getPid() {
        return pid;
    }

    /**
     * 设置提成方案主表主键ID
     *
     * @param pid 提成方案主表主键ID
     */
    public void setPid(Long pid) {
        this.pid = pid;
    }

    /**
     * 获取商品编码
     *
     * @return PRO_CODE - 商品编码
     */
    public String getProCode() {
        return proCode;
    }

    /**
     * 设置商品编码
     *
     * @param proCode 商品编码
     */
    public void setProCode(String proCode) {
        this.proCode = proCode;
    }

    /**
     * 获取商品名
     *
     * @return PRO_NAME - 商品名
     */
    public String getProName() {
        return proName;
    }

    /**
     * 设置商品名
     *
     * @param proName 商品名
     */
    public void setProName(String proName) {
        this.proName = proName;
    }

    /**
     * 获取规格
     *
     * @return PRO_SPECS - 规格
     */
    public String getProSpecs() {
        return proSpecs;
    }

    /**
     * 设置规格
     *
     * @param proSpecs 规格
     */
    public void setProSpecs(String proSpecs) {
        this.proSpecs = proSpecs;
    }

    /**
     * 获取生产企业
     *
     * @return PRO_FACTORY_NAME - 生产企业
     */
    public String getProFactoryName() {
        return proFactoryName;
    }

    /**
     * 设置生产企业
     *
     * @param proFactoryName 生产企业
     */
    public void setProFactoryName(String proFactoryName) {
        this.proFactoryName = proFactoryName;
    }

    /**
     * 获取成本价
     *
     * @return PRO_COST_PRICE - 成本价
     */
    public BigDecimal getProCostPrice() {
        return proCostPrice;
    }

    /**
     * 设置成本价
     *
     * @param proCostPrice 成本价
     */
    public void setProCostPrice(BigDecimal proCostPrice) {
        this.proCostPrice = proCostPrice;
    }

    /**
     * 获取零售价
     *
     * @return PRO_PRICE_NORMAL - 零售价
     */
    public BigDecimal getProPriceNormal() {
        return proPriceNormal;
    }

    /**
     * 设置零售价
     *
     * @param proPriceNormal 零售价
     */
    public void setProPriceNormal(BigDecimal proPriceNormal) {
        this.proPriceNormal = proPriceNormal;
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
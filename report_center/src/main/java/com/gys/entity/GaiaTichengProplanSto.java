package com.gys.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_TICHENG_PROPLAN_STO")
public class GaiaTichengProplanSto implements Serializable {
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
     * 门店编码
     */
    @Column(name = "STO_CODE")
    private String stoCode;

    /**
     * 提成方案主表主键ID
     */
    @Column(name = "PID")
    private Long pid;

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
     * 获取门店编码
     *
     * @return STO_CODE - 门店编码
     */
    public String getStoCode() {
        return stoCode;
    }

    /**
     * 设置门店编码
     *
     * @param stoCode 门店编码
     */
    public void setStoCode(String stoCode) {
        this.stoCode = stoCode;
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
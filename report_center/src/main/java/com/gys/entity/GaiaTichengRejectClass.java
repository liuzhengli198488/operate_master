package com.gys.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_TICHENG_REJECT_CLASS")
public class GaiaTichengRejectClass implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 销售提成方案主表主键ID
     */
    @Column(name = "PID")
    private Long pid;

    /**
     * 一级商品分类
     */
    @Column(name = "PRO_BIG_CLASS")
    private String proBigClass;

    /**
     * 二级商品分类
     */
    @Column(name = "PRO_MID_CLASS")
    private String proMidClass;

    /**
     * 商品分类
     */
    @Column(name = "PRO_CLASS")
    private String proClass;

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
     * 获取一级商品分类
     *
     * @return PRO_BIG_CLASS - 一级商品分类
     */
    public String getProBigClass() {
        return proBigClass;
    }

    /**
     * 设置一级商品分类
     *
     * @param proBigClass 一级商品分类
     */
    public void setProBigClass(String proBigClass) {
        this.proBigClass = proBigClass;
    }

    /**
     * 获取二级商品分类
     *
     * @return PRO_MID_CLASS - 二级商品分类
     */
    public String getProMidClass() {
        return proMidClass;
    }

    /**
     * 设置二级商品分类
     *
     * @param proMidClass 二级商品分类
     */
    public void setProMidClass(String proMidClass) {
        this.proMidClass = proMidClass;
    }

    /**
     * 获取商品分类
     *
     * @return PRO_CLASS - 商品分类
     */
    public String getProClass() {
        return proClass;
    }

    /**
     * 设置商品分类
     *
     * @param proClass 商品分类
     */
    public void setProClass(String proClass) {
        this.proClass = proClass;
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
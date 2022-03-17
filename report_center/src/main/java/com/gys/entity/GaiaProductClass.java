package com.gys.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "GAIA_PRODUCT_CLASS")
public class GaiaProductClass implements Serializable {
    /**
     * 商品分类编码
     */
    @Id
    @Column(name = "PRO_CLASS_CODE")
    private String proClassCode;

    /**
     * 商品大类编码
     */
    @Id
    @Column(name = "PRO_BIG_CLASS_CODE")
    private String proBigClassCode;

    /**
     * 商品中类编码
     */
    @Id
    @Column(name = "PRO_MID_CLASS_CODE")
    private String proMidClassCode;

    /**
     * 商品大类名称
     */
    @Column(name = "PRO_BIG_CLASS_NAME")
    private String proBigClassName;

    /**
     * 商品中类名称
     */
    @Column(name = "PRO_MID_CLASS_NAME")
    private String proMidClassName;

    /**
     * 商品分类名称
     */
    @Column(name = "PRO_CLASS_NAME")
    private String proClassName;

    /**
     * 商品分类状态
     */
    @Column(name = "PRO_CLASS_STATUS")
    private String proClassStatus;

    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取商品分类编码
     *
     * @return PRO_CLASS_CODE - 商品分类编码
     */
    public String getProClassCode() {
        return proClassCode;
    }

    /**
     * 设置商品分类编码
     *
     * @param proClassCode 商品分类编码
     */
    public void setProClassCode(String proClassCode) {
        this.proClassCode = proClassCode;
    }

    /**
     * 获取商品大类编码
     *
     * @return PRO_BIG_CLASS_CODE - 商品大类编码
     */
    public String getProBigClassCode() {
        return proBigClassCode;
    }

    /**
     * 设置商品大类编码
     *
     * @param proBigClassCode 商品大类编码
     */
    public void setProBigClassCode(String proBigClassCode) {
        this.proBigClassCode = proBigClassCode;
    }

    /**
     * 获取商品中类编码
     *
     * @return PRO_MID_CLASS_CODE - 商品中类编码
     */
    public String getProMidClassCode() {
        return proMidClassCode;
    }

    /**
     * 设置商品中类编码
     *
     * @param proMidClassCode 商品中类编码
     */
    public void setProMidClassCode(String proMidClassCode) {
        this.proMidClassCode = proMidClassCode;
    }

    /**
     * 获取商品大类名称
     *
     * @return PRO_BIG_CLASS_NAME - 商品大类名称
     */
    public String getProBigClassName() {
        return proBigClassName;
    }

    /**
     * 设置商品大类名称
     *
     * @param proBigClassName 商品大类名称
     */
    public void setProBigClassName(String proBigClassName) {
        this.proBigClassName = proBigClassName;
    }

    /**
     * 获取商品中类名称
     *
     * @return PRO_MID_CLASS_NAME - 商品中类名称
     */
    public String getProMidClassName() {
        return proMidClassName;
    }

    /**
     * 设置商品中类名称
     *
     * @param proMidClassName 商品中类名称
     */
    public void setProMidClassName(String proMidClassName) {
        this.proMidClassName = proMidClassName;
    }

    /**
     * 获取商品分类名称
     *
     * @return PRO_CLASS_NAME - 商品分类名称
     */
    public String getProClassName() {
        return proClassName;
    }

    /**
     * 设置商品分类名称
     *
     * @param proClassName 商品分类名称
     */
    public void setProClassName(String proClassName) {
        this.proClassName = proClassName;
    }

    /**
     * 获取商品分类状态
     *
     * @return PRO_CLASS_STATUS - 商品分类状态
     */
    public String getProClassStatus() {
        return proClassStatus;
    }

    /**
     * 设置商品分类状态
     *
     * @param proClassStatus 商品分类状态
     */
    public void setProClassStatus(String proClassStatus) {
        this.proClassStatus = proClassStatus;
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
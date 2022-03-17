package com.gys.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "GAIA_SD_STORES_GROUP")
public class GaiaSdStoresGroup implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 分类编号
     */
    @Id
    @Column(name = "GSSG_ID")
    private String gssgId;

    /**
     * 门店编码
     */
    @Id
    @Column(name = "GSSG_BR_ID")
    private String gssgBrId;

    /**
     * 分类类型编码
     */
    @Column(name = "GSSG_TYPE")
    private String gssgType;

    /**
     * 修改人
     */
    @Column(name = "GSSG_UPDATE_EMP")
    private String gssgUpdateEmp;

    /**
     * 修改日期
     */
    @Column(name = "GSSG_UPDATE_DATE")
    private String gssgUpdateDate;

    /**
     * 修改时间
     */
    @Column(name = "GSSG_UPDATE_TIME")
    private String gssgUpdateTime;

    /**
     * 分类名称
     */
    @Column(name = "GSSG_NAME")
    private String gssgName;

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
     * 获取分类编号
     *
     * @return GSSG_ID - 分类编号
     */
    public String getGssgId() {
        return gssgId;
    }

    /**
     * 设置分类编号
     *
     * @param gssgId 分类编号
     */
    public void setGssgId(String gssgId) {
        this.gssgId = gssgId;
    }

    /**
     * 获取门店编码
     *
     * @return GSSG_BR_ID - 门店编码
     */
    public String getGssgBrId() {
        return gssgBrId;
    }

    /**
     * 设置门店编码
     *
     * @param gssgBrId 门店编码
     */
    public void setGssgBrId(String gssgBrId) {
        this.gssgBrId = gssgBrId;
    }

    /**
     * 获取分类类型编码
     *
     * @return GSSG_TYPE - 分类类型编码
     */
    public String getGssgType() {
        return gssgType;
    }

    /**
     * 设置分类类型编码
     *
     * @param gssgType 分类类型编码
     */
    public void setGssgType(String gssgType) {
        this.gssgType = gssgType;
    }

    /**
     * 获取修改人
     *
     * @return GSSG_UPDATE_EMP - 修改人
     */
    public String getGssgUpdateEmp() {
        return gssgUpdateEmp;
    }

    /**
     * 设置修改人
     *
     * @param gssgUpdateEmp 修改人
     */
    public void setGssgUpdateEmp(String gssgUpdateEmp) {
        this.gssgUpdateEmp = gssgUpdateEmp;
    }

    /**
     * 获取修改日期
     *
     * @return GSSG_UPDATE_DATE - 修改日期
     */
    public String getGssgUpdateDate() {
        return gssgUpdateDate;
    }

    /**
     * 设置修改日期
     *
     * @param gssgUpdateDate 修改日期
     */
    public void setGssgUpdateDate(String gssgUpdateDate) {
        this.gssgUpdateDate = gssgUpdateDate;
    }

    /**
     * 获取修改时间
     *
     * @return GSSG_UPDATE_TIME - 修改时间
     */
    public String getGssgUpdateTime() {
        return gssgUpdateTime;
    }

    /**
     * 设置修改时间
     *
     * @param gssgUpdateTime 修改时间
     */
    public void setGssgUpdateTime(String gssgUpdateTime) {
        this.gssgUpdateTime = gssgUpdateTime;
    }

    /**
     * 获取分类名称
     *
     * @return GSSG_NAME - 分类名称
     */
    public String getGssgName() {
        return gssgName;
    }

    /**
     * 设置分类名称
     *
     * @param gssgName 分类名称
     */
    public void setGssgName(String gssgName) {
        this.gssgName = gssgName;
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
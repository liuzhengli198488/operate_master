package com.gys.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Table(name = "GAIA_PRODUCT_MATCH")
@Data
public class GaiaProductMatch {
    /**
     * 加盟商
     */
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 门店
     */
    @Column(name = "STO_CODE")
    private String stoCode;

    /**
     * 匹配编码
     */
    @Column(name = "MATCH_CODE")
    private String matchCode;

    /**
     * 匹配批次
     */
    @Column(name = "MATCH_BATCH")
    private String matchBatch;
    /**
     * 匹配药德商品编码
     */
    @Column(name = "MATCH_PRO_CODE")
    private String matchProCode;

    /**
     * 匹配类型 0-未匹配，1-部分匹配，2-完全匹配
     */
    @Column(name = "MATCH_TYPE")
    private String matchType;

    /**
     * 匹配度
     */
    @Column(name = "MATCH_DEGREE")
    private String matchDegree;

    /**
     * 药德商品编码
     */
    @Column(name = "PRO_CODE")
    private String proCode;

    /**
     * 商品处理状态 0-未处理，1-自动处理，2-手动处理
     */
    @Column(name = "PRO_MATCH_STATUS")
    private String proMatchStatus;

    /**
     * 通用名称
     */
    @Column(name = "PRO_NAME")
    private String proName;

    /**
     * 规格
     */
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    /**
     * 计量单位
     */
    @Column(name = "PRO_UNIT")
    private String proUnit;

    /**
     * 国际条形码1
     */
    @Column(name = "PRO_BARCODE")
    private String proBarcode;

    /**
     * 批准文号
     */
    @Column(name = "PRO_REGISTER_NO")
    private String proRegisterNo;

    /**
     * 生产厂家
     */
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    /**
     * 商品分类
     */
    @Column(name = "PRO_CLASS")
    private String proClass;

    /**
     * 商品分类描述
     */
    @Column(name = "PRO_CLASS_NAME")
    private String proClassName;

    /**
     * 商品成分分类
     */
    @Column(name = "PRO_COMPCLASS")
    private String proCompclass;

    /**
     * 商品成分分类描述
     */
    @Column(name = "PRO_COMPCLASS_NAME")
    private String proCompclassName;

    /**
     * 创建人
     */
    @Column(name = "MATCH_CREATER")
    private String matchCreater;

    /**
     * 创建日期
     */
    @Column(name = "MATCH_CREATE_DATE")
    private String matchCreateDate;

    /**
     * 创建时间
     */
    @Column(name = "MATCH_CREATE_TIME")
    private String matchCreateTime;

    /**
     * 修改加盟商
     */
    @Column(name = "UPDATE_CLIENT")
    private String updateClient;

    /**
     * 修改人
     */
    @Column(name = "UPDATE_USER")
    private String updateUser;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private String updateTime;
}

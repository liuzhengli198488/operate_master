package com.gys.entity;

import lombok.Data;


@Data
public class ProductBasicInData {
    /**
     * 客户编码
     */
    private String clientId;
    /**
     * 门店编码
     */
    private String stoCode;
    /**
     * 用户商品编码
     */
    private String matchProCode;

    /**
     * 匹配编码
     */
    private String matchCode;

    /**
     * 药德商品编码
     */
    private String proCode;

    /**
     * 通用名称
     */
    private String proCommonname;

    /**
     * 商品描述
     */
    private String proDepict;

    /**
     * 商品名
     */
    private String proName;

    /**
     * 规格
     */
    private String proSpecs;

    /**
     * 计量单位
     */
    private String proUnit;

    /**
     * 剂型
     */
    private String proForm;

    /**
     * 细分剂型
     */
    private String proPartform;

    /**
     * 最小剂量（以mg/ml计算）
     */
    private String proMindose;
    /**
     * 总剂量（以mg/ml计算）
     */
    private String proTotaldose;

    /**
     * 国际条形码1
     */
    private String proBarcode;

    /**
     * 国际条形码2
     */
    private String proBarcode2;

    /**
     * 批准文号分类
     */
    private String proRegisterClass;

    /**
     * 批准文号
     */
    private String proRegisterNo;

    /**
     * 商品分类
     */
    private String proClass;

    /**
     * 商品分类描述
     */
    private String proClassName;

    /**
     * 成分分类
     */
    private String proCompclass;

    /**
     * 成分分类描述
     */
    private String proCompclassName;

    /**
     * 处方类别
     */
    private String proPresclass;

    /**
     * 生产企业
     */
    private String proFactoryName;

    /**
     * 保质期
     */
    private String proLife;

    /**
     * 保质期单位
     */
    private String proLifeUnit;

    /**
     * 上市许可持有人
     */
    private String proHolder;

    /**
     * 进项税率
     */
    private String proInputTax;

    /**
     * 销项税率
     */
    private String proOutputTax;

    /**
     * 生产类别
     */
    private String proProduceClass;

    /**
     * 产地
     */
    private String proPlace;

    /**
     * 贮存条件
     */
    private String proStorageCondition;
    /**
     * 修改加盟商
     */
    private String updateClient;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private String updateTime;

}

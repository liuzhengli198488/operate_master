package com.gys.entity;

import lombok.Data;

@Data
public class ProductInfo {
    /**
     * 编码
     */
    private String proCode;
    /**
     * 商品条码1
     */
    private String proBarcode;
    /**
     * 商品条码2
     */
    private String proBarcode2;
    /**
     * 通用名
     */
    private String proCommonName;
    /**
     * 商品规格
     */
    private String proSpecs;
    /**
     * 批准文号
     */
    private String proRegisterNo;
    /**
     * 生产厂家
     */
    private String factoryName;

    private String proClass;

    private String proClassName;

    private String proCompClass;

    private String proCompClassName;
}

package com.gys.entity.data.productMatch;

import lombok.Data;

@Data
public class ProBasicInfo {
    private String clientId;//加盟商
    private String matchBatch;
    private String[] stoCodes; //门店编码数组
    private String proCode;//用户商品编码
    private String proCodeG;//商品编码
    private String proCommonNameG;//通用名称
    private String proSpecsG;//规格
    private String factoryNameG;//生产厂家
    private String proBarcodeG;//国际条形码1
    private String proBarcode2G;//国际条形码
    private String proRegisterNoG;//批准文号
    private String proClassG;//商品分类
    private String proClassNameG;//商品分类
    private String proComponentG;//成分分类
    private String proComponentNameG;//
    private String isClient;// Y 是加盟商更新
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

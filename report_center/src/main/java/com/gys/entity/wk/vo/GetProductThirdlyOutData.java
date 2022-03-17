package com.gys.entity.wk.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetProductThirdlyOutData {
    /**
     * 商品编码
     */
    private String proCode ;
    /**
     * 通用名称
     */
    private String proCommonName ;
    /**
     * 商品名称
     */
    private String name ;
    /**
     * 助记码
     */
    private String pym ;
    /**
     * 规格
     */
    private String specs ;
    /**
     * 计量单位
     */
    private String unit ;
    /**
     * 剂型
     */
    private String form ;
    /**
     * 产地
     */
    private String proPlace ;
    /**
     * 生产企业
     */
    private String proFactoryName ;
    /**
     * 批准文号
     */
    private String approvalNo;

    /**
     * 存储条件
     */
    private String proStorage;

    /**
     * 商品类别
     */
    private String proCategory;
    /**
     * 商品类别名称
     */
    private String proCategoryName;


    private String kysl ;// "总部数量";
    private String ifMed ;// "是否医保";
    private String proThreeCode ;// "第三方编码";
    private BigDecimal priceNormal ;// "零售价";
    private BigDecimal addAmt ;// "配送价";
    private String inventory;// "本地库存";

    private BigDecimal unitPrice;
    private String addRate; //getAddAmt
    private BigDecimal addPrice; //加点金额
    private String proNoPurchase; //禁止采购 0 否 1 是
    private String supName;
    private String proNoApply;   //是否禁止请货 0-否 1-是
}

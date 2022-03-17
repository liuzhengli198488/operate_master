package com.gys.report.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2022/2/16 10:25
 */
@Data
public class KylinSaleOutData {
    /**
     * 商品id
     */
    private String  gssdProId;

    /**
     * 门店编码
     */
    private String gssdBrId;

    /**
     * 7天分类
     */
    private String  type60;

    /**
     * 7天销量
     */
    private String  storeSaleQty60;

    /**
     * 30天分类
     */
    private String  type30;

    /**
     * 30天 销量
     */
    private String storeSaleQty30;

    /**
     * 90天分类
     */
    private String  type90;

    /**
     * 90天销量
     */
    private String  storeSaleQty90;
}

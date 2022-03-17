package com.gys.entity.data;

import lombok.Data;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/6 9:46
 */
@Data
public class YaoJianOutData {
    /**
     * 出库单号
     */
    private String billNo;

    /**
     * 商品编码
     */
    private String proCode;

    /**
     * 商品名称
     */
    private String proName;

    /**
     * 规格
     */
    private String proSpecs;

    /**
     * 单位
     */
    private String proUnit;

    /**
     * 产地
     */
    private String proFactoryName;

    /**
     * 批号
     */
    private String batchNo;

    /**
     * 药检批号
     */
    private String yjBatchNo;

    /**
     * 批号效期
     */
    private String validityDate;

    /**
     * 是否有报告
     */
    private String isReport;

    /**
     * 供应商编码
     */
    private String supplierCoder;
    /**
     *拣货单号
     */
    private String  whereWmJhdh;
}

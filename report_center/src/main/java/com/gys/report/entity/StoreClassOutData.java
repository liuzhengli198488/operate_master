package com.gys.report.entity;

import lombok.Data;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/20 15:47
 */
@Data
public class StoreClassOutData {
    /**
     * 门店编码
     */
    private String stoCode;

    /**
     * 门店属性
     */
    private String stoAttribute;

    /**
     * 店效级别
     */
    private String storeEfficiencyLevel;

    /**
     * 是否医保店
     */
    private String stoIfMedical;

    /**
     * 是否直营管理
     */
    private String directManaged;

    /**
     *纳税属性
     */
    private String stoTaxClass;

    /**
     * DTP
     */
    private String stoIfDtp;

    /**
     *管理区域
     */
    private String managementArea;

    /**
     * 店型
     */
    private String shopType;
}

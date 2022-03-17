package com.gys.report.entity;

import lombok.Data;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/27 21:51
 */
@Data
public class StoClassOutData {
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
     * 纳税属性
     */
    private String stoTaxClass;

    /**
     * 是否直营管理
     */
    private String directManaged;

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

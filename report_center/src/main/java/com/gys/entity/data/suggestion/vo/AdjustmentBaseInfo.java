package com.gys.entity.data.suggestion.vo;

import lombok.Data;

/**
 * @Auther: tzh
 * @Date: 2022/1/12 21:55
 * @Description: AdjustmentBaseInfo
 * @Version 1.0.0
 */
@Data
public class AdjustmentBaseInfo {
    /**
     * 调剂时间
     */
    private String  date;
    /**
     * 省份名称
     */
    private String  provinceName;

    /**
     * 城市名称
     */
    private String  cityName;

    /**
     * 客户id
     */
    private  String clientId;


    /**
     * 客户名称
     */
    private  String clientName;

    /**
     * 单号
     */
    private  String billCode;

    /**
     * 调出品项数
     */
    private  Integer   outItemsNumber;

    /**
     * 调出门店编号
     */
    private String outStoCode;

    /**
     * 调入门店编号
     */
    private String inStoCode;


    private String inBillCode;

    /**
     * 调出门店编号
     */
    private Integer outBillStatus;

    /**
     * 调入门店状态
     */
    private  Integer inBillStatus;

    /**
     * 调出确认前导出
     */
    private  Integer outExportBeforeConfirm;

    /**
     * 调出确认后导出
     */
    private  Integer outExportAfterConfirm;

    /**
     * 调入确认前导出
     */
    private  Integer inExportBeforeConfirm;

    /**
     * 调入确认后导出
     */
    private  Integer inExportAfterConfirm;


}

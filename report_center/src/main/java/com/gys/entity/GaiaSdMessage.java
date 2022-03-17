package com.gys.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class GaiaSdMessage implements Serializable {

    private static final long serialVersionUID = 6122215051136852935L;

    private Integer index;
    /**
     * 加盟商
     */
    private String client;

    /**
     * 门店
     */
    private String gsmId;

    /**
     * 消息单号
     */
    private String gsmVoucherId;
    /**
     * 消息类型
     */
    private String gsmType;

    /**
     * 消息类型名称
     */
    private String gsmTypeName;

    /**
     * 消息数值
     */
    private String gsmValue;

    /**
     * 消息内容
     */
    private String gsmRemark;

    /**
     * 是否查看
     */
    private String gsmFlag;

    /**
     * 跳转页面
     */
    private String gsmPage;

    /**
     * 业务单号
     */
    private String gsmBusinessVoucherId;

    /**
     * 消息送达日期
     */
    private String gsmArriveDate;

    /**
     * 消息送达时间
     */
    private String gsmArriveTime;

    /**
     * 消息查看日期
     */
    private String gsmCheckDate;

    /**
     * 消息查看时间
     */
    private String gsmCheckTime;

    /**
     * 消息查看人员
     */
    private String gsmCheckEmp;

    /**
     * 平台类型  APP  WEB  FX
     */
    private String gsmPlatForm;

    /**
     * 效期天数
     */
    private String gsmWarningDay;
    /**
     * 删除标记:0-未删除 1-删除
     */
    private String gsmDeleteFlag;
}
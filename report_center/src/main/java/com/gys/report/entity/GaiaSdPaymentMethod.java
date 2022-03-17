package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author xiaoyuan
 */
@Data
@Table(name = "GAIA_SD_PAYMENT_METHOD")
public class GaiaSdPaymentMethod implements Serializable {
    private static final long serialVersionUID = -2021071537583704469L;

    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String client;

    /**
     * 门店号
     */
    @Id
    @Column(name = "GSPM_BR_ID")
    private String gspmBrId;

    /**
     * 编号
     */
    @Id
    @Column(name = "GSPM_ID")
    private String gspmId;

    /**
     * 名称
     */
    @Column(name = "GSPM_NAME")
    private String gspmName;


    /**
     * 行号
     */
    @Column(name = "GSPM_SERIAL")
    private String gspmSerial;

    /**
     * 类型
     */
    @Column(name = "GSPM_TYPE")
    private String gspmType;

    /**
     * 是否为接口  0为否，1为是
     */
    @Column(name = "GSPM_FALG")
    private String gspmFalg;

    /**
     * 储值卡充值是否可用
     */
    @Column(name = "GSPM_RECHARGE")
    private String gspmRecharge;

    /**
     * 财务客户编码
     */
    @Column(name = "GSPM_FI_ID")
    private String gspmFiId;

    /**
     * 支付方式说明
     */
    @Column(name = "GSPM_REMARK")
    private String gspmRemark;

    /**
     * 支付方式说明
     */
    @Column(name = "GSPM_REMARK")
    private String gspmRemark1;

    /**
     * 退货说明
     */
    @Column(name = "GSPM_REMARK2")
    private String gspmRemark2;

    /**
     * 订购是否可用     0.不可用   1.可用
     */
    @Column(name = "GSPM_PERORD")
    private String gspmPerord;



}

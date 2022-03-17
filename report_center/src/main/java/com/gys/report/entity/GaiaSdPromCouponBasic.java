//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 电子券信息表
 */
@Table(name = "GAIA_SD_PROM_COUPON_BASIC")
@Data
public class GaiaSdPromCouponBasic implements Serializable {
    private static final long serialVersionUID = -435896337907988878L;
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String clientId;

    /**
     * 电子劵号
     */
    @Id
    @Column(name = "GSPCB_COUPON_ID")
    private String gspcbCouponId;

    /**
     * 电子券活动号
     */
    @Id
    @Column(name = "GSPCB_ACT_NO")
    private String gspcbActNo;

    /**
     * 电子券金额
     */
    @Column(name = "GSPCB_COUPON_AMT")
    private BigDecimal gspcbCouponAmt;

    /**
     * 会员卡号
     */
    @Column(name = "GSPCB_MEMBER_ID")
    private String gspcbMemberId;

    /**
     * 送券途径 0为线下，1为线上
     */
    @Column(name = "GSPCB_GAIN_FLAG")
    private String gspcbGainFlag;

    /**
     * 送券销售单号
     */
    @Column(name = "GSPCB_GRANT_BILL_NO")
    private String gspcbGrantBillNo;

    /**
     * 送券门店
     */
    @Column(name = "GSPCB_GRANT_BR_ID")
    private String gspcbGrantBrId;

    /**
     * 送券日期
     */
    @Column(name = "GSPCB_GRANT_DATE")
    private String gspcbGrantDate;

    /**
     * 用券销售单号
     */
    @Column(name = "GSPCB_USE_BILL_NO")
    private String gspcbUseBillNo;

    /**
     * 用券门店
     */
    @Column(name = "GSPCB_USE_BR_ID")
    private String gspcbUseBrId;

    /**
     * 用券日期
     */
    @Column(name = "GSPCB_USE_DATE")
    private String gspcbUseDate;

    /**
     * 状态 0为不可用，1为可用
     */
    @Column(name = "GSPCB_STATUS")
    private String gspcbStatus;

}

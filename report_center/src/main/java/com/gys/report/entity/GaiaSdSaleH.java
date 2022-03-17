package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店促销主表
 */
@Table(name = "GAIA_SD_SALE_H")
@Data
public class GaiaSdSaleH implements Serializable {
    private static final long serialVersionUID = 2402206677886443396L;
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String clientId;

    /**
     * 销售单号
     */
    @Id
    @Column(name = "GSSH_BILL_NO")
    private String gsshBillNo;

    /**
     * 店名
     */
    @Column(name = "GSSH_BR_ID")
    private String gsshBrId;

    /**
     * 销售日期
     */
    @Column(name = "GSSH_DATE")
    private String gsshDate;

    /**
     * 销售时间
     */
    @Column(name = "GSSH_TIME")
    private String gsshTime;

    /**
     * 收银工号
     */
    @Column(name = "GSSH_EMP")
    private String gsshEmp;

    /**
     * 发票号
     */
    @Column(name = "GSSH_TAX_NO")
    private String gsshTaxNo;

    /**
     * 会员卡号
     */
    @Column(name = "GSSH_HYK_NO")
    private String gsshHykNo;

    /**
     * 折扣金额
     */
    @Column(name = "GSSH_ZK_AMT")
    private BigDecimal gsshZkAmt;

    /**
     * 应收金额
     */
    @Column(name = "GSSH_YS_AMT")
    private BigDecimal gsshYsAmt;

    /**
     * 应收金额
     */
    @Column(name = "GSSH_RMB_ZL_AMT")
    private BigDecimal gsshRmbZlAmt;

    /**
     * 现金收银金额
     */
    @Column(name = "GSSH_RMB_AMT")
    private BigDecimal gsshRmbAmt;

    /**
     * 抵用券卡号
     */
    @Column(name = "GSSH_DYQ_NO")
    private String gsshDyqNo;

    /**
     * 抵用券金额
     */
    @Column(name = "GSSH_DYQ_AMT")
    private BigDecimal gsshDyqAmt;

    /**
     * 抵用原因
     */
    @Column(name = "GSSH_DYQ_TYPE")
    private String gsshDyqType;

    /**
     * 储值卡号
     */
    @Column(name = "GSSH_RECHARGE_CARD_NO")
    private String gsshRechargeCardNo;

    /**
     * 储值卡消费金额
     */
    @Column(name = "GSSH_RECHARGE_CARD_AMT")
    private BigDecimal gsshRechargeCardAmt;

    /**
     * 电子券充值卡号1
     */
    @Column(name = "GSSH_DZQCZ_ACTNO1")
    private String gsshDzqczActno1;

    /**
     * 电子券充值金额1
     */
    @Column(name = "GSSH_DZQCZ_AMT1")
    private BigDecimal gsshDzqczAmt1;

    /**
     * 电子券抵用卡号1
     */
    @Column(name = "GSSH_DZQDY_ACTNO1")
    private String gsshDzqdyActno1;

    /**
     * 电子券抵用金额1
     */
    @Column(name = "GSSH_DZQDY_AMT1")
    private BigDecimal gsshDzqdyAmt1;

    /**
     * 增加积分
     */
    @Column(name = "GSSH_INTEGRAL_ADD")
    private String gsshIntegralAdd;

    /**
     * 兑换积分
     */
    @Column(name = "GSSH_INTEGRAL_EXCHANGE")
    private String gsshIntegralExchange;

    /**
     * 	加价兑换积分金额
     */
    @Column(name = "GSSH_INTEGRAL_EXCHANGE_AMT")
    private BigDecimal gsshIntegralExchangeAmt;

    /**
     * 抵现积分
     */
    @Column(name = "GSSH_INTEGRAL_CASH")
    private String gsshIntegralCash;

    /**
     * 抵现金额
     */
    @Column(name = "GSSH_INTEGRAL_CASH_AMT")
    private BigDecimal gsshIntegralCashAmt;

    /**
     * 支付方式1
     */
    @Column(name = "GSSH_PAYMENT_NO1")
    private String gsshPaymentNo1;

    /**
     * 支付金额1
     */
    @Column(name = "GSSH_PAYMENT_AMT1")
    private BigDecimal gsshPaymentAmt1;

    /**
     * 退货原销售单号
     */
    @Column(name = "GSSH_BILL_NO_RETURN")
    private String gsshBillNoReturn;

    /**
     * 退货审核人员
     */
    @Column(name = "GSSH_EMP_RETURN")
    private String gsshEmpReturn;

    /**
     * 参加促销活动类型1
     */
    @Column(name = "GSSH_PROMOTION_TYPE1")
    private String gsshPromotionType1;

    /**
     * 参加促销活动类型2
     */
    @Column(name = "GSSH_PROMOTION_TYPE2")
    private String gsshPromotionType2;


    /**
     * 参加促销活动类型3
     */
    @Column(name = "GSSH_PROMOTION_TYPE3")
    private String gsshPromotionType3;

    /**
     * 参加促销活动类型4
     */
    @Column(name = "GSSH_PROMOTION_TYPE4")
    private String gsshPromotionType4;

    /**
     * 参加促销活动类型5
     */
    @Column(name = "GSSH_PROMOTION_TYPE5")
    private String gsshPromotionType5;

    /**
     * 手机销售单号
     */
    @Column(name = "GSSH_REGISTER_VOUCHER_ID")
    private String gsshRegisterVoucherId;

    /**
     * 代销售店号
     */
    @Column(name = "GSSH_REPLACE_BR_ID")
    private String gsshReplaceBrId;

    /**
     * 代销售营业员
     */
    @Column(name = "GSSH_REPLACE_SALER_ID")
    private String gsshReplaceSalerId;

    /**
     * 是否挂起 1挂起 0不挂起
     */
    @Column(name = "GSSH_HIDE_FLAG")
    private String gsshHideFlag;

    /**
     * 是否允许调出销售 1为是，0为否
     */
    @Column(name = "GSSH_CALL_ALLOW")
    private String gsshCallAllow;

    /**
     * 移动销售码
     */
    @Column(name = "GSSH_EMP_GROUP_NAME")
    private String gsshEmpGroupName;

    /**
     * 调用次数
     */
    @Column(name = "GSSH_CALL_QTY")
    private String gsshCallQty;

    /**
     * 支付方式2
     */
    @Column(name = "GSSH_PAYMENT_NO2")
    private String gsshPaymentNo2;

    /**
     * 支付金额2
     */
    @Column(name = "GSSH_PAYMENT_AMT2")
    private BigDecimal gsshPaymentAmt2;

    /**
     * 支付方式3
     */
    @Column(name = "GSSH_PAYMENT_NO3")
    private String gsshPaymentNo3;

    /**
     * 支付金额3
     */
    @Column(name = "GSSH_PAYMENT_AMT3")
    private BigDecimal gsshPaymentAmt3;

    /**
     * 支付方式4
     */
    @Column(name = "GSSH_PAYMENT_NO4")
    private String gsshPaymentNo4;

    /**
     * 支付金额4
     */
    @Column(name = "GSSH_PAYMENT_AMT4")
    private BigDecimal gsshPaymentAmt4;

    /**
     * 支付方式5
     */
    @Column(name = "GSSH_PAYMENT_NO5")
    private String gsshPaymentNo5;

    /**
     * 支付金额5
     */
    @Column(name = "GSSH_PAYMENT_AMT5")
    private BigDecimal gsshPaymentAmt5;

    /**
     * 电子券抵用卡号2
     */
    @Column(name = "GSSH_DZQDY_ACTNO2")
    private String gsshDzqdyActno2;

    /**
     * 电子券抵用金额2
     */
    @Column(name = "GSSH_DZQDY_AMT2")
    private BigDecimal gsshDzqdyAmt2;

    /**
     * 电子券抵用卡号3
     */
    @Column(name = "GSSH_DZQDY_ACTNO3")
    private String gsshDzqdyActno3;

    /**
     * 电子券抵用金额3
     */
    @Column(name = "GSSH_DZQDY_AMT3")
    private BigDecimal gsshDzqdyAmt3;

    /**
     * 电子券抵用卡号4
     */
    @Column(name = "GSSH_DZQDY_ACTNO4")
    private String gsshDzqdyActno4;

    /**
     * 电子券抵用金额4
     */
    @Column(name = "GSSH_DZQDY_AMT4")
    private BigDecimal gsshDzqdyAmt4;

    /**
     * 电子券抵用卡号5
     */
    @Column(name = "GSSH_DZQDY_ACTNO5")
    private String gsshDzqdyActno5;

    /**
     * 电子券抵用金额5
     */
    @Column(name = "GSSH_DZQDY_AMT5")
    private BigDecimal gsshDzqdyAmt5;

    /**
     * 零售价格
     */
    @Column(name = "GSSH_NORMAL_AMT")
    private BigDecimal gsshNormalAmt;

    /**
     * 是否日结 0/空为否1为是
     */
    @Column(name = "GSSH_DAYREPORT")
    private String gsshDayreport;

    /**
     * 记录代煎供应商编码 是否有值判断是否中药代煎
     */
    @Column(name = "GSSH_CR_FLAG")
    private String gsshCrFlag;

    /**
     * 代煎状态 0未开方，1已完成，2已退货
     */
    @Column(name = "GSSH_CR_STATUS")
    private String gsshCrStatus;

    /**
     * 订单退货状态：0：已退货，1：医保退货, 2:医保退货失败
     */
    @Column(name = "GSSH_RETURN_STATUS")
    private String gsshReturnStatus;

    /**
     * 是否关联销售标识1表示是0表示否
     */
    @Column(name = "GSSH_IF_RELATED_SALE")
    private String gsshIfRelatedSale;

    /**
     * 验证 挂单前状态
     */
    @Transient
    private String centralAndWestern;

    /**
     * 当前贴数
     */
    @Transient
    private String herbalNum;
}

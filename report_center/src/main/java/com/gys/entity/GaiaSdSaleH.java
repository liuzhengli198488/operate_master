package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 门店销售主表
 * </p>
 *
 * @author flynn
 * @since 2021-12-29
 */
@Data
@Table( name = "GAIA_SD_SALE_H")
public class GaiaSdSaleH implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "加盟商")
    @Id
    @Column(name = "ID")
    private String client;

    @ApiModelProperty(value = "销售单号")
    @Column(name = "GSSH_BILL_NO")
    private String gsshBillNo;

    @ApiModelProperty(value = "店名")
    @Column(name = "GSSH_BR_ID")
    private String gsshBrId;

    @ApiModelProperty(value = "销售日期")
    @Column(name = "GSSH_DATE")
    private String gsshDate;

    @ApiModelProperty(value = "销售时间")
    @Column(name = "GSSH_TIME")
    private String gsshTime;

    @ApiModelProperty(value = "收银工号")
    @Column(name = "GSSH_EMP")
    private String gsshEmp;

    @ApiModelProperty(value = "发票号")
    @Column(name = "GSSH_TAX_NO")
    private String gsshTaxNo;

    @ApiModelProperty(value = "会员卡号")
    @Column(name = "GSSH_HYK_NO")
    private String gsshHykNo;

    @ApiModelProperty(value = "零售价格")
    @Column(name = "GSSH_NORMAL_AMT")
    private BigDecimal gsshNormalAmt;

    @ApiModelProperty(value = "折扣金额")
    @Column(name = "GSSH_ZK_AMT")
    private BigDecimal gsshZkAmt;

    @ApiModelProperty(value = "应收金额")
    @Column(name = "GSSH_YS_AMT")
    private BigDecimal gsshYsAmt;

    @ApiModelProperty(value = "现金找零")
    @Column(name = "GSSH_RMB_ZL_AMT")
    private BigDecimal gsshRmbZlAmt;

    @ApiModelProperty(value = "现金收银金额")
    @Column(name = "GSSH_RMB_AMT")
    private BigDecimal gsshRmbAmt;

    @ApiModelProperty(value = "抵用券卡号")
    @Column(name = "GSSH_DYQ_NO")
    private String gsshDyqNo;

    @ApiModelProperty(value = "抵用券金额")
    @Column(name = "GSSH_DYQ_AMT")
    private BigDecimal gsshDyqAmt;

    @ApiModelProperty(value = "抵用原因")
    @Column(name = "GSSH_DYQ_TYPE")
    private String gsshDyqType;

    @ApiModelProperty(value = "储值卡号")
    @Column(name = "GSSH_RECHARGE_CARD_NO")
    private String gsshRechargeCardNo;

    @ApiModelProperty(value = "储值卡消费金额")
    @Column(name = "GSSH_RECHARGE_CARD_AMT")
    private BigDecimal gsshRechargeCardAmt;

    @ApiModelProperty(value = "电子券充值卡号1")
    @Column(name = "GSSH_DZQCZ_ACTNO1")
    private String gsshDzqczActno1;

    @ApiModelProperty(value = "电子券充值金额1")
    @Column(name = "GSSH_DZQCZ_AMT1")
    private BigDecimal gsshDzqczAmt1;

    @ApiModelProperty(value = "电子券抵用卡号1")
    @Column(name = "GSSH_DZQDY_ACTNO1")
    private String gsshDzqdyActno1;

    @ApiModelProperty(value = "电子券抵用金额1")
    @Column(name = "GSSH_DZQDY_AMT1")
    private BigDecimal gsshDzqdyAmt1;

    @ApiModelProperty(value = "增加积分")
    @Column(name = "GSSH_INTEGRAL_ADD")
    private String gsshIntegralAdd;

    @ApiModelProperty(value = "兑换积分")
    @Column(name = "GSSH_INTEGRAL_EXCHANGE")
    private String gsshIntegralExchange;

    @ApiModelProperty(value = "	加价兑换积分金额")
    @Column(name = "GSSH_INTEGRAL_EXCHANGE_AMT")
    private BigDecimal gsshIntegralExchangeAmt;

    @ApiModelProperty(value = "抵现积分")
    @Column(name = "GSSH_INTEGRAL_CASH")
    private String gsshIntegralCash;

    @ApiModelProperty(value = "抵现金额")
    @Column(name = "GSSH_INTEGRAL_CASH_AMT")
    private BigDecimal gsshIntegralCashAmt;

    @ApiModelProperty(value = "支付方式1")
    @Column(name = "GSSH_PAYMENT_NO1")
    private String gsshPaymentNo1;

    @ApiModelProperty(value = "支付金额1")
    @Column(name = "GSSH_PAYMENT_AMT1")
    private BigDecimal gsshPaymentAmt1;

    @ApiModelProperty(value = "退货原销售单号")
    @Column(name = "GSSH_BILL_NO_RETURN")
    private String gsshBillNoReturn;

    @ApiModelProperty(value = "退货审核人员")
    @Column(name = "GSSH_EMP_RETURN")
    private String gsshEmpReturn;

    @ApiModelProperty(value = "参加促销活动类型1")
    @Column(name = "GSSH_PROMOTION_TYPE1")
    private String gsshPromotionType1;

    @ApiModelProperty(value = "参加促销活动类型2")
    @Column(name = "GSSH_PROMOTION_TYPE2")
    private String gsshPromotionType2;

    @ApiModelProperty(value = "参加促销活动类型3")
    @Column(name = "GSSH_PROMOTION_TYPE3")
    private String gsshPromotionType3;

    @ApiModelProperty(value = "参加促销活动类型4")
    @Column(name = "GSSH_PROMOTION_TYPE4")
    private String gsshPromotionType4;

    @ApiModelProperty(value = "参加促销活动类型5")
    @Column(name = "GSSH_PROMOTION_TYPE5")
    private String gsshPromotionType5;

    @ApiModelProperty(value = "移动收银单号/外卖单号")
    @Column(name = "GSSH_REGISTER_VOUCHER_ID")
    private String gsshRegisterVoucherId;

    @ApiModelProperty(value = "代销售店号")
    @Column(name = "GSSH_REPLACE_BR_ID")
    private String gsshReplaceBrId;

    @ApiModelProperty(value = "代销售营业员")
    @Column(name = "GSSH_REPLACE_SALER_ID")
    private String gsshReplaceSalerId;

    @ApiModelProperty(value = "是否挂起 1挂起 0不挂起 默认不挂起 2移动收银")
    @Column(name = "GSSH_HIDE_FLAG")
    private String gsshHideFlag;

    @ApiModelProperty(value = "是否允许调出销售 1为是，0为否")
    @Column(name = "GSSH_CALL_ALLOW")
    private String gsshCallAllow;

    @ApiModelProperty(value = "移动销售码")
    @Column(name = "GSSH_EMP_GROUP_NAME")
    private String gsshEmpGroupName;

    @ApiModelProperty(value = "调用次数")
    @Column(name = "GSSH_CALL_QTY")
    private BigDecimal gsshCallQty;

    @ApiModelProperty(value = "支付方式2")
    @Column(name = "GSSH_PAYMENT_NO2")
    private String gsshPaymentNo2;

    @ApiModelProperty(value = "支付金额2")
    @Column(name = "GSSH_PAYMENT_AMT2")
    private BigDecimal gsshPaymentAmt2;

    @ApiModelProperty(value = "支付方式3")
    @Column(name = "GSSH_PAYMENT_NO3")
    private String gsshPaymentNo3;

    @ApiModelProperty(value = "支付金额3")
    @Column(name = "GSSH_PAYMENT_AMT3")
    private BigDecimal gsshPaymentAmt3;

    @ApiModelProperty(value = "支付方式4")
    @Column(name = "GSSH_PAYMENT_NO4")
    private String gsshPaymentNo4;

    @ApiModelProperty(value = "支付金额4")
    @Column(name = "GSSH_PAYMENT_AMT4")
    private BigDecimal gsshPaymentAmt4;

    @ApiModelProperty(value = "支付方式5")
    @Column(name = "GSSH_PAYMENT_NO5")
    private String gsshPaymentNo5;

    @ApiModelProperty(value = "支付金额5")
    @Column(name = "GSSH_PAYMENT_AMT5")
    private BigDecimal gsshPaymentAmt5;

    @ApiModelProperty(value = "电子券抵用卡号2")
    @Column(name = "GSSH_DZQDY_ACTNO2")
    private String gsshDzqdyActno2;

    @ApiModelProperty(value = "电子券抵用金额2")
    @Column(name = "GSSH_DZQDY_AMT2")
    private BigDecimal gsshDzqdyAmt2;

    @ApiModelProperty(value = "电子券抵用卡号3")
    @Column(name = "GSSH_DZQDY_ACTNO3")
    private String gsshDzqdyActno3;

    @ApiModelProperty(value = "电子券抵用金额3")
    @Column(name = "GSSH_DZQDY_AMT3")
    private BigDecimal gsshDzqdyAmt3;

    @ApiModelProperty(value = "电子券抵用卡号4")
    @Column(name = "GSSH_DZQDY_ACTNO4")
    private String gsshDzqdyActno4;

    @ApiModelProperty(value = "电子券抵用金额4")
    @Column(name = "GSSH_DZQDY_AMT4")
    private BigDecimal gsshDzqdyAmt4;

    @ApiModelProperty(value = "销售备注")
    @Column(name = "GSSH_DZQDY_ACTNO5")
    private String gsshDzqdyActno5;

    @ApiModelProperty(value = "电子券抵用金额5")
    @Column(name = "GSSH_DZQDY_AMT5")
    private BigDecimal gsshDzqdyAmt5;

    @ApiModelProperty(value = "是否日结 0/空为否1为是")
    @Column(name = "GSSH_DAYREPORT")
    private String gsshDayreport;

    @Column(name = "LAST_UPDATE_TIME")
    private LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "委托代煎供应商编号")
    @Column(name = "GSSH_CR_FLAG")
    private String gsshCrFlag;

    @ApiModelProperty(value = "代煎状态 0未开方，1已完成，2已退货")
    @Column(name = "GSSH_CR_STATUS")
    private String gsshCrStatus;

    @ApiModelProperty(value = "订单退货状态：0：已退货，1：医保退货完成, 2:医保退货失败")
    @Column(name = "GSSH_RETURN_STATUS")
    private String gsshReturnStatus;

    @ApiModelProperty(value = "是否关联销售标识1表示是0表示否")
    @Column(name = "GSSH_IF_RELATED_SALE")
    private String gsshIfRelatedSale;

    @ApiModelProperty(value = " 4-His订单5-门店代售 6.His门店单")
    @Column(name = "GSSH_BILL_TYPE")
    private String gsshBillType;

    @ApiModelProperty(value = "订单类型， 0-本地pos销售单；1-中药代煎单；2-HIS门诊单 默认为空")
    @Column(name = "GSSH_ORDER_SOURCE")
    private String gsshOrderSource;

    @ApiModelProperty(value = "是否开票：0/NULL为否，1为是")
    @Column(name = "GSSH_INVOICED")
    private String gsshInvoiced;


}

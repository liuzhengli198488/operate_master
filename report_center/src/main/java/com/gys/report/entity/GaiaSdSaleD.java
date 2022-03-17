package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiaoyuan
 */
@Table(name = "GAIA_SD_SALE_D")
@Data
public class GaiaSdSaleD implements Serializable {
    private static final long serialVersionUID = -4973982049505859450L;

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
    @Column(name = "GSSD_BILL_NO")
    private String gssdBillNo;

    /**
     * 店号
     */
    @Id
    @Column(name = "GSSD_BR_ID")
    private String gssdBrId;

    /**
     * 销售日期
     */
    @Id
    @Column(name = "GSSD_DATE")
    private String gssdDate;

    /**
     * 行号
     */
    @Id
    @Column(name = "GSSD_SERIAL")
    private String gssdSerial;

    /**
     * 商品编码
     */
    @Column(name = "GSSD_PRO_ID")
    private String gssdProId;

    /**
     * 商品批号
     */
    @Column(name = "GSSD_BATCH_NO")
    private String gssdBatchNo;

    /**
     * 商品批次
     */
    @Column(name = "GSSD_BATCH")
    private String gssdBatch;

    /**
     * 商品有效期
     */
    @Column(name = "GSSD_VALID_DATE")
    private String gssdValidDate;

    /**
     * 目前是折扣
     */
    @Column(name = "GSSD_ST_CODE")
    private String gssdStCode;

    /**
     * 单品零售价
     */
    @Column(name = "GSSD_PRC1")
    private BigDecimal gssdPrc1;

    /**
     * 单品应收价
     */
    @Column(name = "GSSD_PRC2")
    private BigDecimal gssdPrc2;

    /**
     * 数量
     */
    @Column(name = "GSSD_QTY")
    private String gssdQty;

    /**
     * 汇总应收金额
     */
    @Column(name = "GSSD_AMT")
    private BigDecimal gssdAmt;

    /**
     * 兑换单个积分
     */
    @Column(name = "GSSD_INTEGRAL_EXCHANGE_SINGLE")
    private String gssdIntegralExchangeSingle;

    /**
     * 兑换积分汇总
     */
    @Column(name = "GSSD_INTEGRAL_EXCHANGE_COLLECT")
    private String gssdIntegralExchangeCollect;

    /**
     * 单个加价兑换积分金额
     */
    @Column(name = "GSSD_INTEGRAL_EXCHANGE_AMT_SINGLE")
    private BigDecimal gssdIntegralExchangeAmtSingle;

    /**
     * 加价兑换积分金额汇总
     */
    @Column(name = "GSSD_INTEGRAL_EXCHANGE_AMT_COLLECT")
    private BigDecimal gssdIntegralExchangeAmtCollect;

    /**
     * 商品折扣总金额
     */
    @Column(name = "GSSD_ZK_AMT")
    private BigDecimal gssdZkAmt;

    /**
     * 积分兑换折扣金额
     */
    @Column(name = "GSSD_ZK_JFDH")
    private BigDecimal gssdZkJfdh;

    /**
     * 积分抵现折扣金额
     */
    @Column(name = "GSSD_ZK_JFDX")
    private BigDecimal gssdZkJfdx;

    /**
     * 电子券折扣金额
     */
    @Column(name = "GSSD_ZK_DZQ")
    private BigDecimal gssdZkDzq;

    /**
     * 抵用券折扣金额
     */
    @Column(name = "GSSD_ZK_DYQ")
    private BigDecimal gssdZkDyq;

    /**
     * 促销折扣金额
     */
    @Column(name = "GSSD_ZK_PM")
    private BigDecimal gssdZkPm;

    /**
     * 营业员编号
     */
    @Column(name = "GSSD_SALER_ID")
    private String gssdSalerId;

    /**
     * 医生编号
     */
    @Column(name = "GSSD_DOCTOR_ID")
    private String gssdDoctorId;

    /**
     * 医生加点金额
     */
    @Transient
    private BigDecimal doctorAddAmt;

    /**
     * 参加促销主题编号
     */
    @Column(name = "GSSD_PM_SUBJECT_ID")
    private String gssdPmSubjectId;

    /**
     * 参加促销类型编号
     */
    @Column(name = "GSSD_PM_ID")
    private String gssdPmId;

    /**
     * 参加促销类型说明
     */
    @Column(name = "GSSD_PM_CONTENT")
    private String gssdPmContent;

    /**
     * 参加促销活动名称
     */
    @Column(name = "GSSD_PM_ACTIVITY_ID")
    private String gssdPmActivityId;

    /**
     * 参加促销活动名称
     */
    @Column(name = "GSSD_PM_ACTIVITY_NAME")
    private String gssdPmActivityName;

    /**
     * 参加促销活动参数
     */
    @Column(name = "GSSD_PM_ACTIVITY_FLAG")
    private String gssdPmActivityFlag;

    /**
     * 是否登记处方信息 0否1是
     */
    @Column(name = "GSSD_RECIPEL_FLAG")
    private String gssdRecipelFlag;

    /**
     * 是否为关联推荐
     */
    @Column(name = "GSSD_RELATION_FLAG")
    private String gssdRelationFlag;

    /**
     * 特殊药品购买人身份证
     */
    @Column(name = "GSSD_SPECIALMED_IDCARD")
    private String gssdSpecialmedIdcard;

    /**
     * 特殊药品购买人姓名
     */
    @Column(name = "GSSD_SPECIALMED_NAME")
    private String gssdSpecialmedName;

    /**
     * 特殊药品购买人性别
     */
    @Column(name = "GSSD_SPECIALMED_SEX")
    private String gssdSpecialmedSex;

    /**
     * 特殊药品购买人出生日期
     */
    @Column(name = "GSSD_SPECIALMED_BIRTHDAY")
    private String gssdSpecialmedBirthday;

    /**
     * 特殊药品购买人手机
     */
    @Column(name = "GSSD_SPECIALMED_MOBILE")
    private String gssdSpecialmedMobile;

    /**
     * 特殊药品购买人地址
     */
    @Column(name = "GSSD_SPECIALMED_ADDRESS")
    private String gssdSpecialmedAddress;

    /**
     * 特殊药品电子监管码
     */
    @Column(name = "GSSD_SPECIALMED_ECODE")
    private String gssdSpecialmedEcode;

    /**
     * 价格状态
     */
    @Column(name = "GSSD_PRO_STATUS")
    private String gssdProStatus;

    /**
     * 批次成本价
     */
    @Column(name = "GSSD_BATCH_COST")
    private BigDecimal gssdBatchCost;

    /**
     * 批次成本税额
     */
    @Column(name = "GSSD_BATCH_TAX")
    private BigDecimal gssdBatchTax;

    /**
     * 移动平均单价
     */
    @Column(name = "GSSD_MOV_PRICE")
    private BigDecimal gssdMovPrice;

    /**
     * 移动平均总价
     */
    @Column(name = "GSSD_MOV_PRICES")
    private BigDecimal gssdMovPrices;

    /**
     * 税率
     */
    @Column(name = "GSSD_MOV_TAX")
    private BigDecimal gssdMovTax;

    /**
     * 是否生成凭证 1-是 0-否
     */
    @Column(name = "GSSD_AD_FLAG")
    private String gssdAdFlag;

    /**
     * 初始值数量
     */
    @Column(name = "GSSD_ORI_QTY")
    private String gssdOriQty;

    /**
     * 贴数
     */
    @Column(name = "GSSD_DOSE")
    private String gssdDose;

    /**
     * 移动税额
     */
    @Column(name = "GSSD_TAX_RATE")
    private BigDecimal gssdTaxRate;

    /**
     * 加点后金额
     */
    @Column(name = "GSSD_ADD_AMT")
    private BigDecimal gssdAddAmt;

    /**
     * 加点后税金
     */
    @Column(name = "GSSD_ADD_TAX")
    private BigDecimal gssdAddTax;

    /**
     * 是否负库存 0 : 否 / 1: 是
     */
    @Column(name = "GSSD_IF_FKC")
    private String gssdIFkc;

    /**
     * 判断当前商品是否挂单
     */
    @Transient
    private String gssdPendingOrder;

    /**
     * 是否关联销售标识1表示是0表示否
     */
    @Column(name = "GSSD_IF_RELATED_SALE")
    private String gssdIfRelatedSale;
}

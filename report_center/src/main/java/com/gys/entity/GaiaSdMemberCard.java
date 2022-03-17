package com.gys.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "GAIA_SD_MEMBER_CARD")
public class GaiaSdMemberCard implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT LAST_INSERT_ID()")
    private String client;

    /**
     * 会员ID
     */
    @Id
    @Column(name = "GSMBC_MEMBER_ID")
    private String gsmbcMemberId;

    /**
     * 所属组织，单体：店号，连锁：连锁编码
     */
    @Id
    @Column(name = "GSMBC_ORG_ID")
    private String gsmbcOrgId;

    /**
     * 会员卡号
     */
    @Column(name = "GSMBC_CARD_ID")
    private String gsmbcCardId;

    /**
     * 所属店号
     */
    @Column(name = "GSMBC_BR_ID")
    private String gsmbcBrId;

    /**
     * 渠道
     */
    @Column(name = "GSMBC_CHANNEL")
    private String gsmbcChannel;

    /**
     * 卡类型
     */
    @Column(name = "GSMBC_CLASS_ID")
    private String gsmbcClassId;

    /**
     * 当前积分
     */
    @Column(name = "GSMBC_INTEGRAL")
    private String gsmbcIntegral;

    /**
     * 最后积分日期
     */
    @Column(name = "GSMBC_INTEGRAL_LASTDATE")
    private String gsmbcIntegralLastdate;

    /**
     * 清零积分日期
     */
    @Column(name = "GSMBC_ZERO_DATE")
    private String gsmbcZeroDate;

    /**
     * 新卡创建日期
     */
    @Column(name = "GSMBC_CREATE_DATE")
    private String gsmbcCreateDate;

    /**
     * 类型
     */
    @Column(name = "GSMBC_TYPE")
    private String gsmbcType;

    /**
     * openID
     */
    @Column(name = "GSMBC_OPEN_ID")
    private String gsmbcOpenId;

    /**
     * 卡状态
     */
    @Column(name = "GSMBC_STATUS")
    private String gsmbcStatus;

    /**
     * 开卡人员
     */
    @Column(name = "GSMBC_CREATE_SALER")
    private String gsmbcCreateSaler;

    /**
     * 开卡门店
     */
    @Column(name = "GSMBC_OPEN_CARD")
    private String gsmbcOpenCard;

    private static final long serialVersionUID = 1L;

    /**
     * 获取加盟商
     *
     * @return CLIENT - 加盟商
     */
    public String getClient() {
        return client;
    }

    /**
     * 设置加盟商
     *
     * @param client 加盟商
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * 获取会员ID
     *
     * @return GSMBC_MEMBER_ID - 会员ID
     */
    public String getGsmbcMemberId() {
        return gsmbcMemberId;
    }

    /**
     * 设置会员ID
     *
     * @param gsmbcMemberId 会员ID
     */
    public void setGsmbcMemberId(String gsmbcMemberId) {
        this.gsmbcMemberId = gsmbcMemberId;
    }

    /**
     * 获取所属组织，单体：店号，连锁：连锁编码
     *
     * @return GSMBC_ORG_ID - 所属组织，单体：店号，连锁：连锁编码
     */
    public String getGsmbcOrgId() {
        return gsmbcOrgId;
    }

    /**
     * 设置所属组织，单体：店号，连锁：连锁编码
     *
     * @param gsmbcOrgId 所属组织，单体：店号，连锁：连锁编码
     */
    public void setGsmbcOrgId(String gsmbcOrgId) {
        this.gsmbcOrgId = gsmbcOrgId;
    }

    /**
     * 获取会员卡号
     *
     * @return GSMBC_CARD_ID - 会员卡号
     */
    public String getGsmbcCardId() {
        return gsmbcCardId;
    }

    /**
     * 设置会员卡号
     *
     * @param gsmbcCardId 会员卡号
     */
    public void setGsmbcCardId(String gsmbcCardId) {
        this.gsmbcCardId = gsmbcCardId;
    }

    /**
     * 获取所属店号
     *
     * @return GSMBC_BR_ID - 所属店号
     */
    public String getGsmbcBrId() {
        return gsmbcBrId;
    }

    /**
     * 设置所属店号
     *
     * @param gsmbcBrId 所属店号
     */
    public void setGsmbcBrId(String gsmbcBrId) {
        this.gsmbcBrId = gsmbcBrId;
    }

    /**
     * 获取渠道
     *
     * @return GSMBC_CHANNEL - 渠道
     */
    public String getGsmbcChannel() {
        return gsmbcChannel;
    }

    /**
     * 设置渠道
     *
     * @param gsmbcChannel 渠道
     */
    public void setGsmbcChannel(String gsmbcChannel) {
        this.gsmbcChannel = gsmbcChannel;
    }

    /**
     * 获取卡类型
     *
     * @return GSMBC_CLASS_ID - 卡类型
     */
    public String getGsmbcClassId() {
        return gsmbcClassId;
    }

    /**
     * 设置卡类型
     *
     * @param gsmbcClassId 卡类型
     */
    public void setGsmbcClassId(String gsmbcClassId) {
        this.gsmbcClassId = gsmbcClassId;
    }

    /**
     * 获取当前积分
     *
     * @return GSMBC_INTEGRAL - 当前积分
     */
    public String getGsmbcIntegral() {
        return gsmbcIntegral;
    }

    /**
     * 设置当前积分
     *
     * @param gsmbcIntegral 当前积分
     */
    public void setGsmbcIntegral(String gsmbcIntegral) {
        this.gsmbcIntegral = gsmbcIntegral;
    }

    /**
     * 获取最后积分日期
     *
     * @return GSMBC_INTEGRAL_LASTDATE - 最后积分日期
     */
    public String getGsmbcIntegralLastdate() {
        return gsmbcIntegralLastdate;
    }

    /**
     * 设置最后积分日期
     *
     * @param gsmbcIntegralLastdate 最后积分日期
     */
    public void setGsmbcIntegralLastdate(String gsmbcIntegralLastdate) {
        this.gsmbcIntegralLastdate = gsmbcIntegralLastdate;
    }

    /**
     * 获取清零积分日期
     *
     * @return GSMBC_ZERO_DATE - 清零积分日期
     */
    public String getGsmbcZeroDate() {
        return gsmbcZeroDate;
    }

    /**
     * 设置清零积分日期
     *
     * @param gsmbcZeroDate 清零积分日期
     */
    public void setGsmbcZeroDate(String gsmbcZeroDate) {
        this.gsmbcZeroDate = gsmbcZeroDate;
    }

    /**
     * 获取新卡创建日期
     *
     * @return GSMBC_CREATE_DATE - 新卡创建日期
     */
    public String getGsmbcCreateDate() {
        return gsmbcCreateDate;
    }

    /**
     * 设置新卡创建日期
     *
     * @param gsmbcCreateDate 新卡创建日期
     */
    public void setGsmbcCreateDate(String gsmbcCreateDate) {
        this.gsmbcCreateDate = gsmbcCreateDate;
    }

    /**
     * 获取类型
     *
     * @return GSMBC_TYPE - 类型
     */
    public String getGsmbcType() {
        return gsmbcType;
    }

    /**
     * 设置类型
     *
     * @param gsmbcType 类型
     */
    public void setGsmbcType(String gsmbcType) {
        this.gsmbcType = gsmbcType;
    }

    /**
     * 获取openID
     *
     * @return GSMBC_OPEN_ID - openID
     */
    public String getGsmbcOpenId() {
        return gsmbcOpenId;
    }

    /**
     * 设置openID
     *
     * @param gsmbcOpenId openID
     */
    public void setGsmbcOpenId(String gsmbcOpenId) {
        this.gsmbcOpenId = gsmbcOpenId;
    }

    /**
     * 获取卡状态
     *
     * @return GSMBC_STATUS - 卡状态
     */
    public String getGsmbcStatus() {
        return gsmbcStatus;
    }

    /**
     * 设置卡状态
     *
     * @param gsmbcStatus 卡状态
     */
    public void setGsmbcStatus(String gsmbcStatus) {
        this.gsmbcStatus = gsmbcStatus;
    }

    /**
     * 获取开卡人员
     *
     * @return GSMBC_CREATE_SALER - 开卡人员
     */
    public String getGsmbcCreateSaler() {
        return gsmbcCreateSaler;
    }

    /**
     * 设置开卡人员
     *
     * @param gsmbcCreateSaler 开卡人员
     */
    public void setGsmbcCreateSaler(String gsmbcCreateSaler) {
        this.gsmbcCreateSaler = gsmbcCreateSaler;
    }

    /**
     * 获取开卡门店
     *
     * @return GSMBC_OPEN_CARD - 开卡门店
     */
    public String getGsmbcOpenCard() {
        return gsmbcOpenCard;
    }

    /**
     * 设置开卡门店
     *
     * @param gsmbcOpenCard 开卡门店
     */
    public void setGsmbcOpenCard(String gsmbcOpenCard) {
        this.gsmbcOpenCard = gsmbcOpenCard;
    }
}
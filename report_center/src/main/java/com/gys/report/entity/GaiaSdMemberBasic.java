package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "GAIA_SD_MEMBER_BASIC")
@Data
public class GaiaSdMemberBasic implements Serializable {
    @Id
    @Column(name = "CLIENT")
    private String clientId;

    /**
     * 会员id
     */
    @Id
    @Column(name = "GSMB_MEMBER_ID")
    private String gsmbMemberId;

    /**
     * 会员名字
     */
    @Column(name = "GSMB_NAME")
    private String gsmbName;

    /**
     * 地址
     */
    @Column(name = "GSMB_ADDRESS")
    private String gsmbAddress;

    /**
     * 手机
     */
    @Column(name = "GSMB_MOBILE")
    private String gsmbMobile;

    /**
     * 电话
     */
    @Column(name = "GSMB_TEL")
    private String gsmbTel;

    /**
     * 性别
     */
    @Column(name = "GSMB_SEX")
    private String gsmbSex;

    /**
     * 身份证
     */
    @Column(name = "GSMB_CREDENTIALS")
    private String gsmbCredentials;

    /**
     * 年龄
     */
    @Column(name = "GSMB_AGE")
    private String gsmbAge;

    /**
     * 生日
     */
    @Column(name = "GSMB_BIRTH")
    private String gsmbBirth;

    /**
     * BB姓名
     */
    @Column(name = "GSMB_BB_NAME")
    private String gsmbBbName;

    /**
     * BB性别
     */
    @Column(name = "GSMB_BB_SEX")
    private String gsmbBbSex;

    /**
     * BB年龄
     */
    @Column(name = "GSMB_BB_AGE")
    private String gsmbBbAge;

    /**
     * 慢病类型
     */
    @Column(name = "GSMB_CHRONIC_DISEASE_TYPE")
    private String gsmbChronicDiseaseType;

    /**
     * 是否接受本司信息
     */
    @Column(name = "GSMB_CONTACT_ALLOWED")
    private String gsmbContactAllowed;

    /**
     * 会员状态
     */
    @Column(name = "GSMB_STATUS")
    private String gsmbStatus;

    /**
     * 修改资料门店
     */
    @Column(name = "GSMB_UPDATE_BR_ID")
    private String gsmbUpdateBrId;

    /**
     * 修改资料人员
     */
    @Column(name = "GSMB_UPDATE_SALER")
    private String gsmbUpdateSaler;

    /**
     * 修改资料日期
     */
    @Column(name = "GSMB_UPDATE_DATE")
    private String gsmbUpdateDate;

    /**
     * 姓名拼音码
     */
    @Column(name = "GSMB_PYM")
    private String gsmbPym;

}

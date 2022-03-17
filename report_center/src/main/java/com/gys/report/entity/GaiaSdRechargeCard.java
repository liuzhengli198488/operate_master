package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author
 */
@Data
@Table(name = "GAIA_SD_RECHARGE_CARD")
public class GaiaSdRechargeCard implements Serializable {
    private static final long serialVersionUID = 4605117814476389157L;
    @Id
    @Column( name = "CLIENT")
    private String clientId;

    @Id
    @Column(name = "GSRC_ACCOUNT_ID")
    private String gsrcAccountId;

    @Id
    @Column(name = "GSRC_ID")
    private String gsrcId;

    @Column(name = "GSRC_BR_ID")
    private String gsrcBrId;

    @Column(name = "GSRC_DATE")
    private String gsrcDate;

    @Column(name = "GSRC_EMP")
    private String gsrcEmp;

    @Column(name = "GSRC_STATUS")
    private String gsrcStatus;

    @Column(name = "GSRC_NAME")
    private String gsrcName;

    @Column(name = "GSRC_SEX")
    private String gsrcSex;

    @Column(name = "GSRC_MOBILE")
    private String gsrcMobile;

    @Column(name = "GSRC_TEL")
    private String gsrcTel;

    @Column(name = "GSRC_ADDRESS")
    private String gsrcAddress;

    @Column(name = "GSRC_PASSWORD")
    private String gsrcPassword;

    @Column(name = "GSRC_AMT")
    private BigDecimal gsrcAmt;

    @Column(name = "GSRC_UPDATE_BR_ID")
    private String gsrcUpdateBrId;

    @Column(name = "GSRC_UPDATE_DATE")
    private String gsrcUpdateDate;

    @Column(name = "GSRC_UPDATE_EMP")
    private String gsrcUpdateEmp;

    @Column(name ="GSRC_MEMBER_CARD_ID")
    private String gsrcMemberCardId;

    @Column(name ="GSRC_ORG_TYPE")
    private String gsrcOrgType;

    @Column(name ="GSRC_ORG_ID")
    private String gsrcOrgId;
}

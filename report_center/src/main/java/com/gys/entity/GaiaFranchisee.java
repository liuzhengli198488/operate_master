package com.gys.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author xiaoyuan
 */
@Data
@Table(name = "GAIA_FRANCHISEE")
public class GaiaFranchisee implements Serializable {
    private static final long serialVersionUID = -3089489746829289669L;

    /**
     * 加盟商
     */
    @Id
    @Column( name = "CLIENT" )
    private String client;

    /**
     * 加盟商名称
     */
    @Column( name = "FRANC_NAME" )
    private String francName;

    /**
     * 统一社会信用代码
     */
    @Column(name = "FRANC_NO")
    private String francNo;

    /**
     * 法人/负责人
     */
    @Column(name = "FRANC_LEGAL_PERSON")
    private String francLegalPerson;

    /**
     * 质量负责人
     */
    @Column(name = "FRANC_QUA")
    private String francQua;

    /**
     * 详细地址
     */
    @Column(name = "FRANC_ADDR")
    private String francAddr;

    /**
     * 创建日期
     */
    @Column(name = "FRANC_CRE_DATE")
    private String francCreDate;

    /**
     * 创建时间
     */
    @Column(name = "FRANC_CRE_TIME")
    private String francCreTime;

    /**
     * 创建人账号
     */
    @Column(name = "FRANC_CRE_ID")
    private String francCreId;

    /**
     * 修改日期
     */
    @Column(name = "FRANC_MODI_DATE")
    private String francModiDate;

    /**
     * 修改时间
     */
    @Column(name = "FRANC_MODI_TIME")
    private String francModiTime;

    /**
     * 修改人账号
     */
    @Column(name = "FRANC_MODI_ID")
    private String francModiId;

    /**
     * logo地址
     */
    @Column(name = "FRANC_LOGO")
    private String francLogo;

    /**
     * 公司性质-单体店
     */
    @Column(name = "FRANC_TYPE1")
    private String francType1;

    /**
     * 公司性质-连锁公司
     */
    @Column(name = "FRANC_TYPE2")
    private String francType2;

    /**
     * 公司性质-批发公司
     */
    @Column(name = "FRANC_TYPE3")
    private String francType3;

    /**
     * 委托人账号
     */
    @Column(name = "FRANC_ASS")
    private String francAss;

    /**
     * 省份
     */
    @Column(name = "FRANC_PROV")
    private String francProv;

    /**
     * 地市
     */
    @Column(name = "FRANC_CITY")
    private String francCity;

    @Column(name = "FRANC_STATUS")
    private String francStatus;

}

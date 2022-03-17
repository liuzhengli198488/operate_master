package com.gys.report.entity;

import lombok.Data;

import java.io.Serializable;

/**

 * @author xiayuan
 * @date 2020-10-16
 */
@Data
public class GaiaDcData implements Serializable {
    private static final long serialVersionUID = 8106902359554477292L;
    /**
     * 加盟商
     */

    private String client;

    /**
     * DC编码
     */
    private String dcCode;
    /**
     * DC名称
     */
    private String dcName;

    /**
     * 助记码
     */
    private String dcPym;

    /**
     * DC简称
     */
    private String dcShortName;

    /**
     * DC地址
     */
    private String dcAdd;

    /**
     * DC电话
     */
    private String dcTel;

    /**
     * DC状态 0是1否
     */
    private String dcStatus;

    /**
     * 虚拟仓标记
     */
    private String dcInvent;

    /**
     * 是否有批发资质
     */
    private String dcWholesale;

    /**
     * 连锁总部
     */
    private String dcChainHead;

    /**
     * 纳税主体
     */
    private String dcTaxSubject;

    /**
     * 部门负责人ID
     */
    private String dcHeadId;

    /**
     * 部门负责人姓名
     */
    private String dcHeadNam;

    /**
     * 创建日期
     */
    private String dcCreDate;

    /**
     * 创建时间
     */
    private String dcCreTime;

    /**
     * 创建人账号
     */
    private String dcCreId;

    /**
     * 修改日期
     */
    private String dcModiDate;

    /**
     * 修改时间
     */
    private String dcModiTime;

    /**
     * 修改人账号
     */
    private String dcModiId;

    private String dcDeliveryDays;

    private String dcType;

    private String dcNo;

    private String dcLegalPerson;

    private String dcQua;
}
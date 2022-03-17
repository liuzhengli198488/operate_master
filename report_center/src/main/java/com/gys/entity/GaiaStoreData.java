package com.gys.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "GAIA_STORE_DATA")
public class GaiaStoreData implements Serializable {
    /**
     * 加盟商
     */
    @Id
    @Column(name = "CLIENT")
    private String client;

    /**
     * 门店编码
     */
    @Id
    @Column(name = "STO_CODE")
    private String stoCode;

    /**
     * 门店名称
     */
    @Column(name = "STO_NAME")
    private String stoName;

    /**
     * 助记码
     */
    @Column(name = "STO_PYM")
    private String stoPym;

    /**
     * 门店简称
     */
    @Column(name = "STO_SHORT_NAME")
    private String stoShortName;

    /**
     * 门店属性
     */
    @Column(name = "STO_ATTRIBUTE")
    private String stoAttribute;

    /**
     * 配送方式
     */
    @Column(name = "STO_DELIVERY_MODE")
    private String stoDeliveryMode;

    /**
     * 关联门店
     */
    @Column(name = "STO_RELATION_STORE")
    private String stoRelationStore;

    /**
     * 门店状态
     */
    @Column(name = "STO_STATUS")
    private String stoStatus;

    /**
     * 经营面积
     */
    @Column(name = "STO_AREA")
    private String stoArea;

    /**
     * 开业日期
     */
    @Column(name = "STO_OPEN_DATE")
    private String stoOpenDate;

    /**
     * 关店日期
     */
    @Column(name = "STO_CLOSE_DATE")
    private String stoCloseDate;

    /**
     * 详细地址
     */
    @Column(name = "STO_ADD")
    private String stoAdd;

    /**
     * 省
     */
    @Column(name = "STO_PROVINCE")
    private String stoProvince;

    /**
     * 城市
     */
    @Column(name = "STO_CITY")
    private String stoCity;

    /**
     * 区/县
     */
    @Column(name = "STO_DISTRICT")
    private String stoDistrict;

    /**
     * 是否医保店
     */
    @Column(name = "STO_IF_MEDICALCARE")
    private String stoIfMedicalcare;

    /**
     * DTP
     */
    @Column(name = "STO_IF_DTP")
    private String stoIfDtp;

    /**
     * 税分类
     */
    @Column(name = "STO_TAX_CLASS")
    private String stoTaxClass;

    /**
     * 委托配送公司
     */
    @Column(name = "STO_DELIVERY_COMPANY")
    private String stoDeliveryCompany;

    /**
     * 连锁总部
     */
    @Column(name = "STO_CHAIN_HEAD")
    private String stoChainHead;

    /**
     * 纳税主体
     */
    @Column(name = "STO_TAX_SUBJECT")
    private String stoTaxSubject;

    /**
     * 税率
     */
    @Column(name = "STO_TAX_RATE")
    private String stoTaxRate;

    /**
     * 统一社会信用代码
     */
    @Column(name = "STO_NO")
    private String stoNo;

    /**
     * 法人
     */
    @Column(name = "STO_LEGAL_PERSON")
    private String stoLegalPerson;

    /**
     * 质量负责人
     */
    @Column(name = "STO_QUA")
    private String stoQua;

    /**
     * 创建日期
     */
    @Column(name = "STO_CRE_DATE")
    private String stoCreDate;

    /**
     * 创建时间
     */
    @Column(name = "STO_CRE_TIME")
    private String stoCreTime;

    /**
     * 创建人账号
     */
    @Column(name = "STO_CRE_ID")
    private String stoCreId;

    /**
     * 修改日期
     */
    @Column(name = "STO_MODI_DATE")
    private String stoModiDate;

    /**
     * 修改时间
     */
    @Column(name = "STO_MODI_TIME")
    private String stoModiTime;

    /**
     * 修改人账号
     */
    @Column(name = "STO_MODI_ID")
    private String stoModiId;

    /**
     * LOGO地址
     */
    @Column(name = "STO_LOGO")
    private String stoLogo;

    /**
     * 门店组编码
     */
    @Column(name = "STOG_CODE")
    private String stogCode;

    /**
     * 店长
     */
    @Column(name = "STO_LEADER")
    private String stoLeader;

    /**
     * 配送中心
     */
    @Column(name = "STO_DC_CODE")
    private String stoDcCode;

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
     * 获取门店编码
     *
     * @return STO_CODE - 门店编码
     */
    public String getStoCode() {
        return stoCode;
    }

    /**
     * 设置门店编码
     *
     * @param stoCode 门店编码
     */
    public void setStoCode(String stoCode) {
        this.stoCode = stoCode;
    }

    /**
     * 获取门店名称
     *
     * @return STO_NAME - 门店名称
     */
    public String getStoName() {
        return stoName;
    }

    /**
     * 设置门店名称
     *
     * @param stoName 门店名称
     */
    public void setStoName(String stoName) {
        this.stoName = stoName;
    }

    /**
     * 获取助记码
     *
     * @return STO_PYM - 助记码
     */
    public String getStoPym() {
        return stoPym;
    }

    /**
     * 设置助记码
     *
     * @param stoPym 助记码
     */
    public void setStoPym(String stoPym) {
        this.stoPym = stoPym;
    }

    /**
     * 获取门店简称
     *
     * @return STO_SHORT_NAME - 门店简称
     */
    public String getStoShortName() {
        return stoShortName;
    }

    /**
     * 设置门店简称
     *
     * @param stoShortName 门店简称
     */
    public void setStoShortName(String stoShortName) {
        this.stoShortName = stoShortName;
    }

    /**
     * 获取门店属性
     *
     * @return STO_ATTRIBUTE - 门店属性
     */
    public String getStoAttribute() {
        return stoAttribute;
    }

    /**
     * 设置门店属性
     *
     * @param stoAttribute 门店属性
     */
    public void setStoAttribute(String stoAttribute) {
        this.stoAttribute = stoAttribute;
    }

    /**
     * 获取配送方式
     *
     * @return STO_DELIVERY_MODE - 配送方式
     */
    public String getStoDeliveryMode() {
        return stoDeliveryMode;
    }

    /**
     * 设置配送方式
     *
     * @param stoDeliveryMode 配送方式
     */
    public void setStoDeliveryMode(String stoDeliveryMode) {
        this.stoDeliveryMode = stoDeliveryMode;
    }

    /**
     * 获取关联门店
     *
     * @return STO_RELATION_STORE - 关联门店
     */
    public String getStoRelationStore() {
        return stoRelationStore;
    }

    /**
     * 设置关联门店
     *
     * @param stoRelationStore 关联门店
     */
    public void setStoRelationStore(String stoRelationStore) {
        this.stoRelationStore = stoRelationStore;
    }

    /**
     * 获取门店状态
     *
     * @return STO_STATUS - 门店状态
     */
    public String getStoStatus() {
        return stoStatus;
    }

    /**
     * 设置门店状态
     *
     * @param stoStatus 门店状态
     */
    public void setStoStatus(String stoStatus) {
        this.stoStatus = stoStatus;
    }

    /**
     * 获取经营面积
     *
     * @return STO_AREA - 经营面积
     */
    public String getStoArea() {
        return stoArea;
    }

    /**
     * 设置经营面积
     *
     * @param stoArea 经营面积
     */
    public void setStoArea(String stoArea) {
        this.stoArea = stoArea;
    }

    /**
     * 获取开业日期
     *
     * @return STO_OPEN_DATE - 开业日期
     */
    public String getStoOpenDate() {
        return stoOpenDate;
    }

    /**
     * 设置开业日期
     *
     * @param stoOpenDate 开业日期
     */
    public void setStoOpenDate(String stoOpenDate) {
        this.stoOpenDate = stoOpenDate;
    }

    /**
     * 获取关店日期
     *
     * @return STO_CLOSE_DATE - 关店日期
     */
    public String getStoCloseDate() {
        return stoCloseDate;
    }

    /**
     * 设置关店日期
     *
     * @param stoCloseDate 关店日期
     */
    public void setStoCloseDate(String stoCloseDate) {
        this.stoCloseDate = stoCloseDate;
    }

    /**
     * 获取详细地址
     *
     * @return STO_ADD - 详细地址
     */
    public String getStoAdd() {
        return stoAdd;
    }

    /**
     * 设置详细地址
     *
     * @param stoAdd 详细地址
     */
    public void setStoAdd(String stoAdd) {
        this.stoAdd = stoAdd;
    }

    /**
     * 获取省
     *
     * @return STO_PROVINCE - 省
     */
    public String getStoProvince() {
        return stoProvince;
    }

    /**
     * 设置省
     *
     * @param stoProvince 省
     */
    public void setStoProvince(String stoProvince) {
        this.stoProvince = stoProvince;
    }

    /**
     * 获取城市
     *
     * @return STO_CITY - 城市
     */
    public String getStoCity() {
        return stoCity;
    }

    /**
     * 设置城市
     *
     * @param stoCity 城市
     */
    public void setStoCity(String stoCity) {
        this.stoCity = stoCity;
    }

    /**
     * 获取区/县
     *
     * @return STO_DISTRICT - 区/县
     */
    public String getStoDistrict() {
        return stoDistrict;
    }

    /**
     * 设置区/县
     *
     * @param stoDistrict 区/县
     */
    public void setStoDistrict(String stoDistrict) {
        this.stoDistrict = stoDistrict;
    }

    /**
     * 获取是否医保店
     *
     * @return STO_IF_MEDICALCARE - 是否医保店
     */
    public String getStoIfMedicalcare() {
        return stoIfMedicalcare;
    }

    /**
     * 设置是否医保店
     *
     * @param stoIfMedicalcare 是否医保店
     */
    public void setStoIfMedicalcare(String stoIfMedicalcare) {
        this.stoIfMedicalcare = stoIfMedicalcare;
    }

    /**
     * 获取DTP
     *
     * @return STO_IF_DTP - DTP
     */
    public String getStoIfDtp() {
        return stoIfDtp;
    }

    /**
     * 设置DTP
     *
     * @param stoIfDtp DTP
     */
    public void setStoIfDtp(String stoIfDtp) {
        this.stoIfDtp = stoIfDtp;
    }

    /**
     * 获取税分类
     *
     * @return STO_TAX_CLASS - 税分类
     */
    public String getStoTaxClass() {
        return stoTaxClass;
    }

    /**
     * 设置税分类
     *
     * @param stoTaxClass 税分类
     */
    public void setStoTaxClass(String stoTaxClass) {
        this.stoTaxClass = stoTaxClass;
    }

    /**
     * 获取委托配送公司
     *
     * @return STO_DELIVERY_COMPANY - 委托配送公司
     */
    public String getStoDeliveryCompany() {
        return stoDeliveryCompany;
    }

    /**
     * 设置委托配送公司
     *
     * @param stoDeliveryCompany 委托配送公司
     */
    public void setStoDeliveryCompany(String stoDeliveryCompany) {
        this.stoDeliveryCompany = stoDeliveryCompany;
    }

    /**
     * 获取连锁总部
     *
     * @return STO_CHAIN_HEAD - 连锁总部
     */
    public String getStoChainHead() {
        return stoChainHead;
    }

    /**
     * 设置连锁总部
     *
     * @param stoChainHead 连锁总部
     */
    public void setStoChainHead(String stoChainHead) {
        this.stoChainHead = stoChainHead;
    }

    /**
     * 获取纳税主体
     *
     * @return STO_TAX_SUBJECT - 纳税主体
     */
    public String getStoTaxSubject() {
        return stoTaxSubject;
    }

    /**
     * 设置纳税主体
     *
     * @param stoTaxSubject 纳税主体
     */
    public void setStoTaxSubject(String stoTaxSubject) {
        this.stoTaxSubject = stoTaxSubject;
    }

    /**
     * 获取税率
     *
     * @return STO_TAX_RATE - 税率
     */
    public String getStoTaxRate() {
        return stoTaxRate;
    }

    /**
     * 设置税率
     *
     * @param stoTaxRate 税率
     */
    public void setStoTaxRate(String stoTaxRate) {
        this.stoTaxRate = stoTaxRate;
    }

    /**
     * 获取统一社会信用代码
     *
     * @return STO_NO - 统一社会信用代码
     */
    public String getStoNo() {
        return stoNo;
    }

    /**
     * 设置统一社会信用代码
     *
     * @param stoNo 统一社会信用代码
     */
    public void setStoNo(String stoNo) {
        this.stoNo = stoNo;
    }

    /**
     * 获取法人
     *
     * @return STO_LEGAL_PERSON - 法人
     */
    public String getStoLegalPerson() {
        return stoLegalPerson;
    }

    /**
     * 设置法人
     *
     * @param stoLegalPerson 法人
     */
    public void setStoLegalPerson(String stoLegalPerson) {
        this.stoLegalPerson = stoLegalPerson;
    }

    /**
     * 获取质量负责人
     *
     * @return STO_QUA - 质量负责人
     */
    public String getStoQua() {
        return stoQua;
    }

    /**
     * 设置质量负责人
     *
     * @param stoQua 质量负责人
     */
    public void setStoQua(String stoQua) {
        this.stoQua = stoQua;
    }

    /**
     * 获取创建日期
     *
     * @return STO_CRE_DATE - 创建日期
     */
    public String getStoCreDate() {
        return stoCreDate;
    }

    /**
     * 设置创建日期
     *
     * @param stoCreDate 创建日期
     */
    public void setStoCreDate(String stoCreDate) {
        this.stoCreDate = stoCreDate;
    }

    /**
     * 获取创建时间
     *
     * @return STO_CRE_TIME - 创建时间
     */
    public String getStoCreTime() {
        return stoCreTime;
    }

    /**
     * 设置创建时间
     *
     * @param stoCreTime 创建时间
     */
    public void setStoCreTime(String stoCreTime) {
        this.stoCreTime = stoCreTime;
    }

    /**
     * 获取创建人账号
     *
     * @return STO_CRE_ID - 创建人账号
     */
    public String getStoCreId() {
        return stoCreId;
    }

    /**
     * 设置创建人账号
     *
     * @param stoCreId 创建人账号
     */
    public void setStoCreId(String stoCreId) {
        this.stoCreId = stoCreId;
    }

    /**
     * 获取修改日期
     *
     * @return STO_MODI_DATE - 修改日期
     */
    public String getStoModiDate() {
        return stoModiDate;
    }

    /**
     * 设置修改日期
     *
     * @param stoModiDate 修改日期
     */
    public void setStoModiDate(String stoModiDate) {
        this.stoModiDate = stoModiDate;
    }

    /**
     * 获取修改时间
     *
     * @return STO_MODI_TIME - 修改时间
     */
    public String getStoModiTime() {
        return stoModiTime;
    }

    /**
     * 设置修改时间
     *
     * @param stoModiTime 修改时间
     */
    public void setStoModiTime(String stoModiTime) {
        this.stoModiTime = stoModiTime;
    }

    /**
     * 获取修改人账号
     *
     * @return STO_MODI_ID - 修改人账号
     */
    public String getStoModiId() {
        return stoModiId;
    }

    /**
     * 设置修改人账号
     *
     * @param stoModiId 修改人账号
     */
    public void setStoModiId(String stoModiId) {
        this.stoModiId = stoModiId;
    }

    /**
     * 获取LOGO地址
     *
     * @return STO_LOGO - LOGO地址
     */
    public String getStoLogo() {
        return stoLogo;
    }

    /**
     * 设置LOGO地址
     *
     * @param stoLogo LOGO地址
     */
    public void setStoLogo(String stoLogo) {
        this.stoLogo = stoLogo;
    }

    /**
     * 获取门店组编码
     *
     * @return STOG_CODE - 门店组编码
     */
    public String getStogCode() {
        return stogCode;
    }

    /**
     * 设置门店组编码
     *
     * @param stogCode 门店组编码
     */
    public void setStogCode(String stogCode) {
        this.stogCode = stogCode;
    }

    /**
     * 获取店长
     *
     * @return STO_LEADER - 店长
     */
    public String getStoLeader() {
        return stoLeader;
    }

    /**
     * 设置店长
     *
     * @param stoLeader 店长
     */
    public void setStoLeader(String stoLeader) {
        this.stoLeader = stoLeader;
    }

    /**
     * 获取配送中心
     *
     * @return STO_DC_CODE - 配送中心
     */
    public String getStoDcCode() {
        return stoDcCode;
    }

    /**
     * 设置配送中心
     *
     * @param stoDcCode 配送中心
     */
    public void setStoDcCode(String stoDcCode) {
        this.stoDcCode = stoDcCode;
    }
}
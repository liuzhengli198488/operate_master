package com.gys.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "GAIA_PRODUCT_BASIC")
public class GaiaProductBasic implements Serializable {
    /**
     * 商品编码
     */
    @Id
    @Column(name = "PRO_CODE")
    private String proCode;

    /**
     * 通用名称
     */
    @Column(name = "PRO_COMMONNAME")
    private String proCommonname;

    /**
     * 商品描述
     */
    @Column(name = "PRO_DEPICT")
    private String proDepict;

    /**
     * 助记码
     */
    @Column(name = "PRO_PYM")
    private String proPym;

    /**
     * 商品名
     */
    @Column(name = "PRO_NAME")
    private String proName;

    /**
     * 规格
     */
    @Column(name = "PRO_SPECS")
    private String proSpecs;

    /**
     * 计量单位
     */
    @Column(name = "PRO_UNIT")
    private String proUnit;

    /**
     * 剂型
     */
    @Column(name = "PRO_FORM")
    private String proForm;

    /**
     * 细分剂型
     */
    @Column(name = "PRO_PARTFORM")
    private String proPartform;

    /**
     * 最小剂量（以mg/ml计算）
     */
    @Column(name = "PRO_MINDOSE")
    private String proMindose;

    /**
     * 总剂量（以mg/ml计算）
     */
    @Column(name = "PRO_TOTALDOSE")
    private String proTotaldose;

    /**
     * 国际条形码1
     */
    @Column(name = "PRO_BARCODE")
    private String proBarcode;

    /**
     * 国际条形码2
     */
    @Column(name = "PRO_BARCODE2")
    private String proBarcode2;

    /**
     * 批准文号分类
     */
    @Column(name = "PRO_REGISTER_CLASS")
    private String proRegisterClass;

    /**
     * 批准文号
     */
    @Column(name = "PRO_REGISTER_NO")
    private String proRegisterNo;

    /**
     * 批准文号批准日期
     */
    @Column(name = "PRO_REGISTER_DATE")
    private String proRegisterDate;

    /**
     * 批准文号失效日期
     */
    @Column(name = "PRO_REGISTER_EXDATE")
    private String proRegisterExdate;

    /**
     * 商品分类
     */
    @Column(name = "PRO_CLASS")
    private String proClass;

    /**
     * 商品分类描述
     */
    @Column(name = "PRO_CLASS_NAME")
    private String proClassName;

    /**
     * 成分分类
     */
    @Column(name = "PRO_COMPCLASS")
    private String proCompclass;

    /**
     * 成分分类描述
     */
    @Column(name = "PRO_COMPCLASS_NAME")
    private String proCompclassName;

    /**
     * 处方类别
     */
    @Column(name = "PRO_PRESCLASS")
    private String proPresclass;

    /**
     * 生产企业代码
     */
    @Column(name = "PRO_FACTORY_CODE")
    private String proFactoryCode;

    /**
     * 生产企业
     */
    @Column(name = "PRO_FACTORY_NAME")
    private String proFactoryName;

    /**
     * 商标
     */
    @Column(name = "PRO_MARK")
    private String proMark;

    /**
     * 品牌标识名
     */
    @Column(name = "PRO_BRAND")
    private String proBrand;

    /**
     * 品牌区分
     */
    @Column(name = "PRO_BRAND_CLASS")
    private String proBrandClass;

    /**
     * 保质期
     */
    @Column(name = "PRO_LIFE")
    private String proLife;

    /**
     * 保质期单位
     */
    @Column(name = "PRO_LIFE_UNIT")
    private String proLifeUnit;

    /**
     * 上市许可持有人
     */
    @Column(name = "PRO_HOLDER")
    private String proHolder;

    /**
     * 进项税率
     */
    @Column(name = "PRO_INPUT_TAX")
    private String proInputTax;

    /**
     * 销项税率
     */
    @Column(name = "PRO_OUTPUT_TAX")
    private String proOutputTax;

    /**
     * 药品本位码
     */
    @Column(name = "PRO_BASIC_CODE")
    private String proBasicCode;

    /**
     * 税务分类编码
     */
    @Column(name = "PRO_TAX_CLASS")
    private String proTaxClass;

    /**
     * 管制特殊分类
     */
    @Column(name = "PRO_CONTROL_CLASS")
    private String proControlClass;

    /**
     * 生产类别
     */
    @Column(name = "PRO_PRODUCE_CLASS")
    private String proProduceClass;

    /**
     * 贮存条件
     */
    @Column(name = "PRO_STORAGE_CONDITION")
    private String proStorageCondition;

    /**
     * 商品仓储分区
     */
    @Column(name = "PRO_STORAGE_AREA")
    private String proStorageArea;

    /**
     * 长（以MM计算）
     */
    @Column(name = "PRO_LONG")
    private String proLong;

    /**
     * 宽（以MM计算）
     */
    @Column(name = "PRO_WIDE")
    private String proWide;

    /**
     * 高（以MM计算）
     */
    @Column(name = "PRO_HIGH")
    private String proHigh;

    /**
     * 中包装量
     */
    @Column(name = "PRO_MID_PACKAGE")
    private String proMidPackage;

    /**
     * 大包装量
     */
    @Column(name = "PRO_BIG_PACKAGE")
    private String proBigPackage;

    /**
     * 启用电子监管码
     */
    @Column(name = "PRO_ELECTRONIC_CODE")
    private String proElectronicCode;

    /**
     * 生产经营许可证号
     */
    @Column(name = "PRO_QS_CODE")
    private String proQsCode;

    /**
     * 最大销售量
     */
    @Column(name = "PRO_MAX_SALES")
    private String proMaxSales;

    /**
     * 说明书代码
     */
    @Column(name = "PRO_INSTRUCTION_CODE")
    private String proInstructionCode;

    /**
     * 说明书内容
     */
    @Column(name = "PRO_INSTRUCTION")
    private String proInstruction;

    /**
     * 国家医保品种
     */
    @Column(name = "PRO_MED_PRODCT")
    private String proMedProdct;

    /**
     * 国家医保品种编码
     */
    @Column(name = "PRO_MED_PRODCTCODE")
    private String proMedProdctcode;

    /**
     * 生产国家
     */
    @Column(name = "PRO_COUNTRY")
    private String proCountry;

    /**
     * 产地
     */
    @Column(name = "PRO_PLACE")
    private String proPlace;

    /**
     * 状态
     */
    @Column(name = "PRO_STATUS")
    private String proStatus;

    /**
     * 可服用天数
     */
    @Column(name = "PRO_TAKE_DAYS")
    private String proTakeDays;

    /**
     * 用法用量
     */
    @Column(name = "PRO_USAGE")
    private String proUsage;

    /**
     * 禁忌说明
     */
    @Column(name = "PRO_CONTRAINDICATION")
    private String proContraindication;

    /**
     * 修改加盟商
     */
    @Column(name = "UPDATE_CLIENT")
    private String updateClient;

    /**
     * 修改人
     */
    @Column(name = "UPDATE_USER")
    private String updateUser;

    /**
     * 修改时间
     */
    @Column(name = "UPDATE_TIME")
    private String updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取商品编码
     *
     * @return PRO_CODE - 商品编码
     */
    public String getProCode() {
        return proCode;
    }

    /**
     * 设置商品编码
     *
     * @param proCode 商品编码
     */
    public void setProCode(String proCode) {
        this.proCode = proCode;
    }

    /**
     * 获取通用名称
     *
     * @return PRO_COMMONNAME - 通用名称
     */
    public String getProCommonname() {
        return proCommonname;
    }

    /**
     * 设置通用名称
     *
     * @param proCommonname 通用名称
     */
    public void setProCommonname(String proCommonname) {
        this.proCommonname = proCommonname;
    }

    /**
     * 获取商品描述
     *
     * @return PRO_DEPICT - 商品描述
     */
    public String getProDepict() {
        return proDepict;
    }

    /**
     * 设置商品描述
     *
     * @param proDepict 商品描述
     */
    public void setProDepict(String proDepict) {
        this.proDepict = proDepict;
    }

    /**
     * 获取助记码
     *
     * @return PRO_PYM - 助记码
     */
    public String getProPym() {
        return proPym;
    }

    /**
     * 设置助记码
     *
     * @param proPym 助记码
     */
    public void setProPym(String proPym) {
        this.proPym = proPym;
    }

    /**
     * 获取商品名
     *
     * @return PRO_NAME - 商品名
     */
    public String getProName() {
        return proName;
    }

    /**
     * 设置商品名
     *
     * @param proName 商品名
     */
    public void setProName(String proName) {
        this.proName = proName;
    }

    /**
     * 获取规格
     *
     * @return PRO_SPECS - 规格
     */
    public String getProSpecs() {
        return proSpecs;
    }

    /**
     * 设置规格
     *
     * @param proSpecs 规格
     */
    public void setProSpecs(String proSpecs) {
        this.proSpecs = proSpecs;
    }

    /**
     * 获取计量单位
     *
     * @return PRO_UNIT - 计量单位
     */
    public String getProUnit() {
        return proUnit;
    }

    /**
     * 设置计量单位
     *
     * @param proUnit 计量单位
     */
    public void setProUnit(String proUnit) {
        this.proUnit = proUnit;
    }

    /**
     * 获取剂型
     *
     * @return PRO_FORM - 剂型
     */
    public String getProForm() {
        return proForm;
    }

    /**
     * 设置剂型
     *
     * @param proForm 剂型
     */
    public void setProForm(String proForm) {
        this.proForm = proForm;
    }

    /**
     * 获取细分剂型
     *
     * @return PRO_PARTFORM - 细分剂型
     */
    public String getProPartform() {
        return proPartform;
    }

    /**
     * 设置细分剂型
     *
     * @param proPartform 细分剂型
     */
    public void setProPartform(String proPartform) {
        this.proPartform = proPartform;
    }

    /**
     * 获取最小剂量（以mg/ml计算）
     *
     * @return PRO_MINDOSE - 最小剂量（以mg/ml计算）
     */
    public String getProMindose() {
        return proMindose;
    }

    /**
     * 设置最小剂量（以mg/ml计算）
     *
     * @param proMindose 最小剂量（以mg/ml计算）
     */
    public void setProMindose(String proMindose) {
        this.proMindose = proMindose;
    }

    /**
     * 获取总剂量（以mg/ml计算）
     *
     * @return PRO_TOTALDOSE - 总剂量（以mg/ml计算）
     */
    public String getProTotaldose() {
        return proTotaldose;
    }

    /**
     * 设置总剂量（以mg/ml计算）
     *
     * @param proTotaldose 总剂量（以mg/ml计算）
     */
    public void setProTotaldose(String proTotaldose) {
        this.proTotaldose = proTotaldose;
    }

    /**
     * 获取国际条形码1
     *
     * @return PRO_BARCODE - 国际条形码1
     */
    public String getProBarcode() {
        return proBarcode;
    }

    /**
     * 设置国际条形码1
     *
     * @param proBarcode 国际条形码1
     */
    public void setProBarcode(String proBarcode) {
        this.proBarcode = proBarcode;
    }

    /**
     * 获取国际条形码2
     *
     * @return PRO_BARCODE2 - 国际条形码2
     */
    public String getProBarcode2() {
        return proBarcode2;
    }

    /**
     * 设置国际条形码2
     *
     * @param proBarcode2 国际条形码2
     */
    public void setProBarcode2(String proBarcode2) {
        this.proBarcode2 = proBarcode2;
    }

    /**
     * 获取批准文号分类
     *
     * @return PRO_REGISTER_CLASS - 批准文号分类
     */
    public String getProRegisterClass() {
        return proRegisterClass;
    }

    /**
     * 设置批准文号分类
     *
     * @param proRegisterClass 批准文号分类
     */
    public void setProRegisterClass(String proRegisterClass) {
        this.proRegisterClass = proRegisterClass;
    }

    /**
     * 获取批准文号
     *
     * @return PRO_REGISTER_NO - 批准文号
     */
    public String getProRegisterNo() {
        return proRegisterNo;
    }

    /**
     * 设置批准文号
     *
     * @param proRegisterNo 批准文号
     */
    public void setProRegisterNo(String proRegisterNo) {
        this.proRegisterNo = proRegisterNo;
    }

    /**
     * 获取批准文号批准日期
     *
     * @return PRO_REGISTER_DATE - 批准文号批准日期
     */
    public String getProRegisterDate() {
        return proRegisterDate;
    }

    /**
     * 设置批准文号批准日期
     *
     * @param proRegisterDate 批准文号批准日期
     */
    public void setProRegisterDate(String proRegisterDate) {
        this.proRegisterDate = proRegisterDate;
    }

    /**
     * 获取批准文号失效日期
     *
     * @return PRO_REGISTER_EXDATE - 批准文号失效日期
     */
    public String getProRegisterExdate() {
        return proRegisterExdate;
    }

    /**
     * 设置批准文号失效日期
     *
     * @param proRegisterExdate 批准文号失效日期
     */
    public void setProRegisterExdate(String proRegisterExdate) {
        this.proRegisterExdate = proRegisterExdate;
    }

    /**
     * 获取商品分类
     *
     * @return PRO_CLASS - 商品分类
     */
    public String getProClass() {
        return proClass;
    }

    /**
     * 设置商品分类
     *
     * @param proClass 商品分类
     */
    public void setProClass(String proClass) {
        this.proClass = proClass;
    }

    /**
     * 获取商品分类描述
     *
     * @return PRO_CLASS_NAME - 商品分类描述
     */
    public String getProClassName() {
        return proClassName;
    }

    /**
     * 设置商品分类描述
     *
     * @param proClassName 商品分类描述
     */
    public void setProClassName(String proClassName) {
        this.proClassName = proClassName;
    }

    /**
     * 获取成分分类
     *
     * @return PRO_COMPCLASS - 成分分类
     */
    public String getProCompclass() {
        return proCompclass;
    }

    /**
     * 设置成分分类
     *
     * @param proCompclass 成分分类
     */
    public void setProCompclass(String proCompclass) {
        this.proCompclass = proCompclass;
    }

    /**
     * 获取成分分类描述
     *
     * @return PRO_COMPCLASS_NAME - 成分分类描述
     */
    public String getProCompclassName() {
        return proCompclassName;
    }

    /**
     * 设置成分分类描述
     *
     * @param proCompclassName 成分分类描述
     */
    public void setProCompclassName(String proCompclassName) {
        this.proCompclassName = proCompclassName;
    }

    /**
     * 获取处方类别
     *
     * @return PRO_PRESCLASS - 处方类别
     */
    public String getProPresclass() {
        return proPresclass;
    }

    /**
     * 设置处方类别
     *
     * @param proPresclass 处方类别
     */
    public void setProPresclass(String proPresclass) {
        this.proPresclass = proPresclass;
    }

    /**
     * 获取生产企业代码
     *
     * @return PRO_FACTORY_CODE - 生产企业代码
     */
    public String getProFactoryCode() {
        return proFactoryCode;
    }

    /**
     * 设置生产企业代码
     *
     * @param proFactoryCode 生产企业代码
     */
    public void setProFactoryCode(String proFactoryCode) {
        this.proFactoryCode = proFactoryCode;
    }

    /**
     * 获取生产企业
     *
     * @return PRO_FACTORY_NAME - 生产企业
     */
    public String getProFactoryName() {
        return proFactoryName;
    }

    /**
     * 设置生产企业
     *
     * @param proFactoryName 生产企业
     */
    public void setProFactoryName(String proFactoryName) {
        this.proFactoryName = proFactoryName;
    }

    /**
     * 获取商标
     *
     * @return PRO_MARK - 商标
     */
    public String getProMark() {
        return proMark;
    }

    /**
     * 设置商标
     *
     * @param proMark 商标
     */
    public void setProMark(String proMark) {
        this.proMark = proMark;
    }

    /**
     * 获取品牌标识名
     *
     * @return PRO_BRAND - 品牌标识名
     */
    public String getProBrand() {
        return proBrand;
    }

    /**
     * 设置品牌标识名
     *
     * @param proBrand 品牌标识名
     */
    public void setProBrand(String proBrand) {
        this.proBrand = proBrand;
    }

    /**
     * 获取品牌区分
     *
     * @return PRO_BRAND_CLASS - 品牌区分
     */
    public String getProBrandClass() {
        return proBrandClass;
    }

    /**
     * 设置品牌区分
     *
     * @param proBrandClass 品牌区分
     */
    public void setProBrandClass(String proBrandClass) {
        this.proBrandClass = proBrandClass;
    }

    /**
     * 获取保质期
     *
     * @return PRO_LIFE - 保质期
     */
    public String getProLife() {
        return proLife;
    }

    /**
     * 设置保质期
     *
     * @param proLife 保质期
     */
    public void setProLife(String proLife) {
        this.proLife = proLife;
    }

    /**
     * 获取保质期单位
     *
     * @return PRO_LIFE_UNIT - 保质期单位
     */
    public String getProLifeUnit() {
        return proLifeUnit;
    }

    /**
     * 设置保质期单位
     *
     * @param proLifeUnit 保质期单位
     */
    public void setProLifeUnit(String proLifeUnit) {
        this.proLifeUnit = proLifeUnit;
    }

    /**
     * 获取上市许可持有人
     *
     * @return PRO_HOLDER - 上市许可持有人
     */
    public String getProHolder() {
        return proHolder;
    }

    /**
     * 设置上市许可持有人
     *
     * @param proHolder 上市许可持有人
     */
    public void setProHolder(String proHolder) {
        this.proHolder = proHolder;
    }

    /**
     * 获取进项税率
     *
     * @return PRO_INPUT_TAX - 进项税率
     */
    public String getProInputTax() {
        return proInputTax;
    }

    /**
     * 设置进项税率
     *
     * @param proInputTax 进项税率
     */
    public void setProInputTax(String proInputTax) {
        this.proInputTax = proInputTax;
    }

    /**
     * 获取销项税率
     *
     * @return PRO_OUTPUT_TAX - 销项税率
     */
    public String getProOutputTax() {
        return proOutputTax;
    }

    /**
     * 设置销项税率
     *
     * @param proOutputTax 销项税率
     */
    public void setProOutputTax(String proOutputTax) {
        this.proOutputTax = proOutputTax;
    }

    /**
     * 获取药品本位码
     *
     * @return PRO_BASIC_CODE - 药品本位码
     */
    public String getProBasicCode() {
        return proBasicCode;
    }

    /**
     * 设置药品本位码
     *
     * @param proBasicCode 药品本位码
     */
    public void setProBasicCode(String proBasicCode) {
        this.proBasicCode = proBasicCode;
    }

    /**
     * 获取税务分类编码
     *
     * @return PRO_TAX_CLASS - 税务分类编码
     */
    public String getProTaxClass() {
        return proTaxClass;
    }

    /**
     * 设置税务分类编码
     *
     * @param proTaxClass 税务分类编码
     */
    public void setProTaxClass(String proTaxClass) {
        this.proTaxClass = proTaxClass;
    }

    /**
     * 获取管制特殊分类
     *
     * @return PRO_CONTROL_CLASS - 管制特殊分类
     */
    public String getProControlClass() {
        return proControlClass;
    }

    /**
     * 设置管制特殊分类
     *
     * @param proControlClass 管制特殊分类
     */
    public void setProControlClass(String proControlClass) {
        this.proControlClass = proControlClass;
    }

    /**
     * 获取生产类别
     *
     * @return PRO_PRODUCE_CLASS - 生产类别
     */
    public String getProProduceClass() {
        return proProduceClass;
    }

    /**
     * 设置生产类别
     *
     * @param proProduceClass 生产类别
     */
    public void setProProduceClass(String proProduceClass) {
        this.proProduceClass = proProduceClass;
    }

    /**
     * 获取贮存条件
     *
     * @return PRO_STORAGE_CONDITION - 贮存条件
     */
    public String getProStorageCondition() {
        return proStorageCondition;
    }

    /**
     * 设置贮存条件
     *
     * @param proStorageCondition 贮存条件
     */
    public void setProStorageCondition(String proStorageCondition) {
        this.proStorageCondition = proStorageCondition;
    }

    /**
     * 获取商品仓储分区
     *
     * @return PRO_STORAGE_AREA - 商品仓储分区
     */
    public String getProStorageArea() {
        return proStorageArea;
    }

    /**
     * 设置商品仓储分区
     *
     * @param proStorageArea 商品仓储分区
     */
    public void setProStorageArea(String proStorageArea) {
        this.proStorageArea = proStorageArea;
    }

    /**
     * 获取长（以MM计算）
     *
     * @return PRO_LONG - 长（以MM计算）
     */
    public String getProLong() {
        return proLong;
    }

    /**
     * 设置长（以MM计算）
     *
     * @param proLong 长（以MM计算）
     */
    public void setProLong(String proLong) {
        this.proLong = proLong;
    }

    /**
     * 获取宽（以MM计算）
     *
     * @return PRO_WIDE - 宽（以MM计算）
     */
    public String getProWide() {
        return proWide;
    }

    /**
     * 设置宽（以MM计算）
     *
     * @param proWide 宽（以MM计算）
     */
    public void setProWide(String proWide) {
        this.proWide = proWide;
    }

    /**
     * 获取高（以MM计算）
     *
     * @return PRO_HIGH - 高（以MM计算）
     */
    public String getProHigh() {
        return proHigh;
    }

    /**
     * 设置高（以MM计算）
     *
     * @param proHigh 高（以MM计算）
     */
    public void setProHigh(String proHigh) {
        this.proHigh = proHigh;
    }

    /**
     * 获取中包装量
     *
     * @return PRO_MID_PACKAGE - 中包装量
     */
    public String getProMidPackage() {
        return proMidPackage;
    }

    /**
     * 设置中包装量
     *
     * @param proMidPackage 中包装量
     */
    public void setProMidPackage(String proMidPackage) {
        this.proMidPackage = proMidPackage;
    }

    /**
     * 获取大包装量
     *
     * @return PRO_BIG_PACKAGE - 大包装量
     */
    public String getProBigPackage() {
        return proBigPackage;
    }

    /**
     * 设置大包装量
     *
     * @param proBigPackage 大包装量
     */
    public void setProBigPackage(String proBigPackage) {
        this.proBigPackage = proBigPackage;
    }

    /**
     * 获取启用电子监管码
     *
     * @return PRO_ELECTRONIC_CODE - 启用电子监管码
     */
    public String getProElectronicCode() {
        return proElectronicCode;
    }

    /**
     * 设置启用电子监管码
     *
     * @param proElectronicCode 启用电子监管码
     */
    public void setProElectronicCode(String proElectronicCode) {
        this.proElectronicCode = proElectronicCode;
    }

    /**
     * 获取生产经营许可证号
     *
     * @return PRO_QS_CODE - 生产经营许可证号
     */
    public String getProQsCode() {
        return proQsCode;
    }

    /**
     * 设置生产经营许可证号
     *
     * @param proQsCode 生产经营许可证号
     */
    public void setProQsCode(String proQsCode) {
        this.proQsCode = proQsCode;
    }

    /**
     * 获取最大销售量
     *
     * @return PRO_MAX_SALES - 最大销售量
     */
    public String getProMaxSales() {
        return proMaxSales;
    }

    /**
     * 设置最大销售量
     *
     * @param proMaxSales 最大销售量
     */
    public void setProMaxSales(String proMaxSales) {
        this.proMaxSales = proMaxSales;
    }

    /**
     * 获取说明书代码
     *
     * @return PRO_INSTRUCTION_CODE - 说明书代码
     */
    public String getProInstructionCode() {
        return proInstructionCode;
    }

    /**
     * 设置说明书代码
     *
     * @param proInstructionCode 说明书代码
     */
    public void setProInstructionCode(String proInstructionCode) {
        this.proInstructionCode = proInstructionCode;
    }

    /**
     * 获取说明书内容
     *
     * @return PRO_INSTRUCTION - 说明书内容
     */
    public String getProInstruction() {
        return proInstruction;
    }

    /**
     * 设置说明书内容
     *
     * @param proInstruction 说明书内容
     */
    public void setProInstruction(String proInstruction) {
        this.proInstruction = proInstruction;
    }

    /**
     * 获取国家医保品种
     *
     * @return PRO_MED_PRODCT - 国家医保品种
     */
    public String getProMedProdct() {
        return proMedProdct;
    }

    /**
     * 设置国家医保品种
     *
     * @param proMedProdct 国家医保品种
     */
    public void setProMedProdct(String proMedProdct) {
        this.proMedProdct = proMedProdct;
    }

    /**
     * 获取国家医保品种编码
     *
     * @return PRO_MED_PRODCTCODE - 国家医保品种编码
     */
    public String getProMedProdctcode() {
        return proMedProdctcode;
    }

    /**
     * 设置国家医保品种编码
     *
     * @param proMedProdctcode 国家医保品种编码
     */
    public void setProMedProdctcode(String proMedProdctcode) {
        this.proMedProdctcode = proMedProdctcode;
    }

    /**
     * 获取生产国家
     *
     * @return PRO_COUNTRY - 生产国家
     */
    public String getProCountry() {
        return proCountry;
    }

    /**
     * 设置生产国家
     *
     * @param proCountry 生产国家
     */
    public void setProCountry(String proCountry) {
        this.proCountry = proCountry;
    }

    /**
     * 获取产地
     *
     * @return PRO_PLACE - 产地
     */
    public String getProPlace() {
        return proPlace;
    }

    /**
     * 设置产地
     *
     * @param proPlace 产地
     */
    public void setProPlace(String proPlace) {
        this.proPlace = proPlace;
    }

    /**
     * 获取状态
     *
     * @return PRO_STATUS - 状态
     */
    public String getProStatus() {
        return proStatus;
    }

    /**
     * 设置状态
     *
     * @param proStatus 状态
     */
    public void setProStatus(String proStatus) {
        this.proStatus = proStatus;
    }

    /**
     * 获取可服用天数
     *
     * @return PRO_TAKE_DAYS - 可服用天数
     */
    public String getProTakeDays() {
        return proTakeDays;
    }

    /**
     * 设置可服用天数
     *
     * @param proTakeDays 可服用天数
     */
    public void setProTakeDays(String proTakeDays) {
        this.proTakeDays = proTakeDays;
    }

    /**
     * 获取用法用量
     *
     * @return PRO_USAGE - 用法用量
     */
    public String getProUsage() {
        return proUsage;
    }

    /**
     * 设置用法用量
     *
     * @param proUsage 用法用量
     */
    public void setProUsage(String proUsage) {
        this.proUsage = proUsage;
    }

    /**
     * 获取禁忌说明
     *
     * @return PRO_CONTRAINDICATION - 禁忌说明
     */
    public String getProContraindication() {
        return proContraindication;
    }

    public String getUpdateClient() {
        return updateClient;
    }

    public void setUpdateClient(String updateClient) {
        this.updateClient = updateClient;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 设置禁忌说明
     *
     * @param proContraindication 禁忌说明
     */
    public void setProContraindication(String proContraindication) {
        this.proContraindication = proContraindication;
    }
}
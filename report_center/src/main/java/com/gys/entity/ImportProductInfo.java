package com.gys.entity;

import lombok.Data;

@Data
public class ImportProductInfo {
    /**
     * 商品编码 PRO_CODE
     */
    private String proCode;

    /**
     * 通用名称 PRO_COMMONNAME
     */
    private String proCommonname;

    /**
     * 商品描述 PRO_DEPICT
     */
    private String proDepict;

    /**
     * 助记码 PRO_PYM
     */
    private String proPym;

    /**
     * 商品名 PRO_NAME
     */
    private String proName;

    /**
     * 规格 PRO_SPECS
     */
    private String proSpecs;

    /**
     * 计量单位 PRO_UNIT
     */
    private String proUnit;

    /**
     * 剂型 PRO_FORM
     */
    private String proForm;

    /**
     * 国际条形码1 PRO_BARCODE
     */
    private String proBarcode;

    /**
     * 批准文号 PRO_REGISTER_NO
     */
    private String proRegisterNo;

    /**
     * 批准文号批准日期 PRO_REGISTER_DATE
     */
    private String proRegisterDate;

    /**
     * 批准文号失效日期 PRO_REGISTER_EXDATE
     */
    private String proRegisterExdate;

    /**
     * 商品分类 PRO_CLASS
     */
    private String proClass;

    /**
     * 成分分类 PRO_COMPCLASS
     */
    private String proCompclass;

    /**
     * 商品分类描述 PRO_CLASS
     */
    private String proClassName;

    /**
     * 成分分类描述 PRO_COMPCLASS_NAME
     */
    private String proCompclassName;

    /**
     * 处方类别 1-处方药，2-甲类OTC，3-乙类OTC，4-双跨处方 PRO_PRESCLASS
     */
    private String proPresclass;

    /**
     * 生产企业代码 PRO_FACTORY_CODE
     */
    private String proFactoryCode;

    /**
     * 生产企业 PRO_FACTORY_NAME
     */
    private String proFactoryName;

    /**
     * 商标 PRO_MARK
     */
    private String proMark;

    /**
     * 保质期 PRO_LIFE
     */
    private String proLife;

    /**
     * 上市许可持有人 PRO_HOLDER
     */
    private String proHolder;

    /**
     * 进项税率 PRO_INPUT_TAX
     */
    private String proInputTax;

    /**
     * 销项税率 PRO_OUTPUT_TAX
     */
    private String proOutputTax;

    /**
     * 药品本位码 PRO_BASIC_CODE
     */
    private String proBasicCode;

    /**
     * 税务分类编码 PRO_TAX_CLASS
     */
    private String proTaxClass;

    /**
     * 管制特殊分类 1-毒性药品，2-麻醉药品，3-一类精神药品，4-二类精神药品，5-易制毒药品（麻黄碱），6-放射性药品，7-生物制品（含胰岛素），8-兴奋剂（除胰岛素），9-第一类器械，10-第二类器械，11-第三类器械，12-其它管制
     * PRO_CONTROL_CLASS
     */
    private String proControlClass;

    /**
     * 生产类别 1-辅料，2-化学药品，3-生物制品，4-中药，5-器械 PRO_PRODUCE_CLASS
     */
    private String proProduceClass;

    /**
     * 贮存条件 1-常温，2-阴凉，3-冷藏 PRO_STORAGE_CONDITION
     */
    private String proStorageCondition;

    /**
     * 商品仓储分区 1-内服药品，2-外用药品，3-中药饮片区，4-精装中药区，5-针剂，6-二类精神药品，7-含麻药品，8-冷藏商品，9-外用非药，10-医疗器械，11-食品，12-保健食品，13-易燃商品区
     * PRO_STORAGE_AREA
     */
    private String proStorageArea;

    /**
     * 长（以MM计算） PRO_LONG
     */
    private String proLong;

    /**
     * 宽（以MM计算） PRO_WIDE
     */
    private String proWide;

    /**
     * 高（以MM计算） PRO_HIGH
     */
    private String proHigh;

    /**
     * 中包装量 PRO_MID_PACKAGE
     */
    private String proMidPackage;

    /**
     * 大包装量 PRO_BIG_PACKAGE
     */
    private String proBigPackage;

    /**
     * 启用电子监管码 0-否，1-是 PRO_ELECTRONIC_CODE
     */
    private String proElectronicCode;

    /**
     * 生产经营许可证号 PRO_QS_CODE
     */
    private String proQsCode;

    /**
     * 最大销售量 PRO_MAX_SALES
     */
    private String proMaxSales;

    /**
     * 说明书代码 PRO_INSTRUCTION_CODE
     */
    private String proInstructionCode;

    /**
     * 说明书内容 PRO_INSTRUCTION
     */
    private String proInstruction;

    /**
     * 国家医保品种 0-否，1-是 PRO_MED_PRODCT
     */
    private String proMedProdct;

    /**
     * 商品状态 0 可用 1不可用 PRO_STATUS
     */
    private String proStatus;

    /**
     * 国家医保品种编码 PRO_MED_PRODCTCODE
     */
    private String proMedProdctcode;

    /**
     * 生产国家 PRO_COUNTRY
     */
    private String proCountry;

    /**
     * 产地 PRO_PLACE
     */
    private String proPlace;

    /**
     * 可服用天数 PRO_TAKE_DAYS
     */
    private String proTakeDays;

    /**
     * 用法用量 PRO_USAGE
     */
    
    private String proUsage;

    /**
     * 禁忌说明 PRO_CONTRAINDICATION
     */
    
    private String proContraindication;

    /**
     * 国家医保目录编号 PRO_MED_LISTNUM
     */
    
    private String proMedListnum;

    /**
     * 国家医保目录名称 PRO_MED_LISTNAME
     */
    
    private String proMedListname;

    /**
     * 国家医保医保目录剂型 PRO_MED_LISTFORM
     */
    
    private String proMedListform;

}

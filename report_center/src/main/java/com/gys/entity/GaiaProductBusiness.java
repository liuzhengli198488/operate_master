package com.gys.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "GAIA_PRODUCT_BUSINESS")
public class GaiaProductBusiness implements Serializable {
    private static final long serialVersionUID = 2518741337174308594L;

    /**
     * 加盟商
     */
    @Id
    @Column( name = "CLIENT")
    private String client;


    /**
     * 商品自编码
     */
    @Column(name = "PRO_CODE")
    private String proCode;


    /**
     * 地点
     */
    @Id
    @Column(name = "PRO_SITE")
    private String proSite;


    /**
     * 商品自编码
     */
    @Id
    @Column(name = "PRO_SELF_CODE")
    private String proSelfCode;

    /**
     * 匹配状态 0-未匹配，1-部分匹配，2-完全匹配
     */
    @Column(name = "PRO_MATCH_STATUS")
    private String proMatchStatus;

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
     *  批准文号分类 1-国产药品，2-进口药品，3-国产器械，4-进口器械，5-中药饮片，6-特殊化妆品，7-消毒用品，8-保健食品，9-QS商品，10-其它
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
     * 处方类别 1-处方药，2-甲类OTC，3-乙类OTC，4-双跨处方
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
     * 品牌区分 1-全国品牌，2-省份品牌，3-地区品牌，4-本地品牌，5-其它
     */
    @Column(name = "PRO_BRAND_CLASS")
    private String proBrandClass;

    /**
     * 保质期
     */
    @Column(name = "PRO_LIFE")
    private String proLife;

    /**
     * 保质期单位 1-天，2-月，3-年
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
     * 管制特殊分类 1-毒性药品，2-麻醉药品，3-一类精神药品，4-二类精神药品，5-易制毒药品（麻黄碱），6-放射性药品，7-生物制品（含胰岛素），8-兴奋剂（除胰岛素），9-第一类器械，10-第二类器械，11-第三类器械，12-其它管制
     */
    @Column(name = "PRO_CONTROL_CLASS")
    private String proControlClass;

    /**
     * 生产类别 1-辅料，2-化学药品，3-生物制品，4-中药，5-器械
     */
    @Column(name = "PRO_PRODUCE_CLASS")
    private String proProduceClass;

    /**
     * 贮存条件 1-常温，2-阴凉，3-冷藏
     */
    @Column(name = "PRO_STORAGE_CONDITION")
    private String proStorageCondition;

    /**
     * 商品仓储分区 1-内服药品，2-外用药品，3-中药饮片区，4-精装中药区，5-针剂，6-二类精神药品，7-含麻药品，8-冷藏商品，9-外用非药，10-医疗器械，11-食品，12-保健食品，13-易燃商品区
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
     * 启用电子监管码 0-否，1-是
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
     * 国家医保品种 0-否，1-是
     */
    @Column(name = "PRO_MED_PRODCT")
    private String proMedProdct;

    /**
     * 商品状态 0 可用 1不可用
     */
    @Column(name = "PRO_STATUS")
    private String proStatus;

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
     * 商品定位
     */
    @Column(name = "PRO_POSITION")
    private String proPosition;

    /**
     * 禁止销售
     */
    @Column(name = "PRO_NO_RETAIL")
    private String proNoRetail;

    /**
     * 禁止采购
     */
    @Column(name = "PRO_NO_PURCHASE")
    private String proNoPurchase;

    /**
     * 禁止配送
     */
    @Column(name = "PRO_NO_DISTRIBUTED")
    private String proNoDistributed;

    /**
     * 禁止退厂
     */
    @Column(name = "PRO_NO_SUPPLIER")
    private String proNoSupplier;

    /**
     * 禁止退仓
     */
    @Column(name = "PRO_NO_DC")
    private String proNoDc;

    /**
     * 禁止调剂
     */
    @Column(name = "PRO_NO_ADJUST")
    private String proNoAdjust;

    /**
     * 禁止批发
     */
    @Column(name = "PRO_NO_SALE")
    private String proNoSale;

    /**
     * 禁止请货
     */
    @Column(name = "PRO_NO_APPLY")
    private String proNoApply;

    /**
     * 是否拆零 1是0否
     */
    @Column(name = "PRO_IFPART")
    private String proIfpart;

    /**
     * 拆零单位
     */
    @Column(name = "PRO_PART_UINT")
    private String proPartUint;

    /**
     * 拆零比例
     */
    @Column(name = "PRO_PART_RATE")
    private String proPartRate;

    /**
     * 采购单位
     */
    @Column(name = "PRO_PURCHASE_UNIT")
    private String proPurchaseUnit;

    /**
     * 采购比例
     */
    @Column(name = "PRO_PURCHASE_RATE")
    private String proPurchaseRate;

    /**
     * 采购单位
     */
    @Column(name = "PRO_SALE_UNIT")
    private String proSaleUnit;

    /**
     * 采购比例
     */
    @Column(name = "PRO_SALE_RATE")
    private String proSaleRate;

    /**
     * 最小订货量
     */
    @Column(name = "PRO_MIN_QTY")
    private BigDecimal proMinQty;

    /**
     * 是否医保
     */
    @Column(name = "PRO_IF_MED")
    private String proIfMed;

    /**
     * 销售级别
     */
    @Column(name = "PRO_SLAE_CLASS")
    private String proSlaeClass;

    /**
     * 限购数量
     */
    @Column(name = "PRO_LIMIT_QTY")
    private BigDecimal proLimitQty;

    /**
     * 含麻最大配货量
     */
    @Column(name = "PRO_MAX_QTY")
    private BigDecimal proMaxQty;

    /**
     * 按中包装量倍数配货
     */
    @Column(name = "PRO_PACKAGE_FLAG")
    private String proPackageFlag;

    /**
     * 是否重点养护
     */
    @Column(name = "PRO_KEY_CARE")
    private String proKeyCare;

    /**
     * 中药规格
     */
    @Column(name = "PRO_TCM_SPECS")
    private String proTcmSpecs;

    /**
     * 中药批准文号
     */
    @Column(name = "PRO_TCM_REGISTER_NO")
    private String proTcmRegisterNo;

    /**
     * 中药生产企业代码
     */
    @Column(name = "PRO_TCM_FACTORY_CODE")
    private String proTcmFactoryCode;

    /**
     * 中药产地
     */
    @Column(name = "PRO_TCM_PLACE")
    private String proTcmPlace;

    /**
     * 固定货位
     */
    @Column(name = "PRO_FIX_BIN")
    private String proFixBin;

    /**
     * 创建日期
     */
    @Column(name = "PRO_CREATE_DATE")
    private String proCreateDate;

    /**
     * 参考进货价
     */
    @Column(name = "PRO_CGJ")
    private BigDecimal proCgj;

    /**
     * 参考零售价
     */
    @Column(name = "PRO_LSJ")
    private BigDecimal proLsj;

    /**
     * 参考毛利率
     */
    @Column(name = "PRO_MLL")
    private BigDecimal proMll;

    /**
     * 国家医保目录编号
     */
    @Column(name = "PRO_MED_LISTNUM")
    private String proMedListnum;

    /**
     * 国家医保目录名称
     */
    @Column(name = "PRO_MED_LISTNAME")
    private String proMedListname;

    /**
     * 国家医保医保目录剂型
     */
    @Column(name = "PRO_MED_LISTFORM")
    private String proMedListform;

    /**
     * 外包装图片
     */
    @Column(name = "PRO_PICTURE_WBZ")
    private String proPictureWbz;

    /**
     * 说明书图片
     */
    @Column(name = "PRO_PICTURE_SMS")
    private String proPictureSms;

    /**
     * 批件图片
     */
    @Column(name = "PRO_PICTURE_PJ")
    private String proPicturePj;

    /**
     * 内包装图片
     */
    @Column(name = "PRO_PICTURE_NBZ")
    private String proPictureNbz;

    /**
     * 内包装图片
     */
    @Column(name = "PRO_PICTURE_ZZ")
    private String proPictureZz;

    /**
     * 其他图片
     */
    @Column(name = "PRO_PICTURE_QT")
    private String proPictureQt;


    /**
     * 自定义2
     */
    @Column(name = "PRO_ZDY2")
    private String proZdy2;

    /**
     * 自定义3
     */
    @Column(name = "PRO_ZDY3")
    private String proZdy3;

    /**
     * 自定义4
     */
    @Column(name = "PRO_ZDY4")
    private String proZdy4;

    /**
     * 自定义5
     */
    @Column(name = "PRO_ZDY5")
    private String proZdy5;

    /**
     * 商品分类
     */
    @Column(name = "PRO_SCLASS")
    private String proSclass;

    /**
     * 医保刷卡数量(目前在用)
     */
    @Column(name = "PRO_ZDY1")
    private String proZdy1;

    /**
     * 医保刷卡数量
     */
    @Column(name = "PRO_MED_QTY")
    private String proMedQty;

    /**
     * 医保编码
     */
    @Column(name = "PRO_MED_ID")
    private String proMedId;

    /**
     * 不打折
     */
    @Column(name = "PRO_BDZ")
    private String proBdz;

    /**
     * 特殊属性：1-防疫
     */
    @Column(name = "PRO_Tssx")
    private String proTssx;
    /**
     * 医保类型：1-甲类 2-乙类
     */
    @Column(name = "PRO_YBLX")
    private String proYblx;
    /**
     * 批文注册证号
     */
    @Column(name = "PRO_PWZCZ")
    private String proPwzcz;
    /**
     * 质量标准
     */
    @Column(name = "PRO_ZLBZ")
    private String proZlbz;
    /**
     * 排除：1-是
     */
    @Column(name = "PRO_OUT")
    private String proOut;
    /**
     * 是否批发：0-否 1-是
     */
    @Column(name = "PRO_IF_WHOLESALE")
    private String proIfWholesale;
    /**
     * 经营类别
     */
    @Column(name = "PRO_JYLB")
    private String proJylb;

    /**
     * 是否发送购药提醒：0-否 1-是
     */
    @Column(name = "PRO_IF_SMS")
    private String proIfSms;
}

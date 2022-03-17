package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.List;

@Data
public class GetSalesSummaryOfSalesmenReportInData implements Serializable {
    private static final long serialVersionUID = -5756433723218409475L;

    @ApiModelProperty(value = "加盟号")
    private String clientId;

    @ApiModelProperty(value = "店号")
    private String brId;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "支付类型")
    private String[] payName;
    @ApiModelProperty(value = "营业员id")
    private String queryUserId;
    @ApiModelProperty(value = "收银员id")
    private String empUserId;
    @ApiModelProperty(value = "生产厂家")
    private String factory;
    @ApiModelProperty(value = "医生id")
    private String queryDoctorId;
    @ApiModelProperty(value = "商品id")
    private String queryProId;
    @ApiModelProperty(value = "商品id")
    private String[] proArr;
    @ApiModelProperty(value = "起始日期")
    private String queryStartDate;

    @ApiModelProperty(value = "结束日期")
    private String queryEndDate;

    @ApiModelProperty(value = "会员卡号")
    private String gsshHykNo;

    @Column(name = "商品分类")
    private String proClass;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;
    @ApiModelProperty(value = "最大毛利率")
    private String grossProfitRateMax;

    @ApiModelProperty(value = "最小毛利率")
    private String grossProfitRateMin;

    @ApiModelProperty(value = "是否医保")
    private String medProdctStatus;
    @ApiModelProperty(value = "会员ID")
    private String  memberId;
    @ApiModelProperty(value = "会员ID")
    private String  memberName;
    @ApiModelProperty(value = "单号集合")
    private String[] billNoArr;
    @ApiModelProperty(value = "单号")
    private String billNo;
    @ApiModelProperty(value = "批号")
    private String batchNo;
    @ApiModelProperty(value = "效期")
    private String expiryDay;

    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "导出人员类型(导出用)")
    private String userType;
    private Integer pageNum;
    private Integer front;
    private Integer pageSize;
    private Integer end;
    private String orderBy;

    /**
     * 是否查询黑名单标志   商品白名单参数 1 为不显示
     */
    private String flag;

    @ApiModelProperty(value = "销售等级")
    private String proSaleClass;
    @ApiModelProperty(value = "商品自定义1")
    private String proZdy1;
    @ApiModelProperty(value = "商品自定义2")
    private String proZdy2;
    @ApiModelProperty(value = "商品自定义3")
    private String proZdy3;
    @ApiModelProperty(value = "商品自定义4")
    private String proZdy4;
    @ApiModelProperty(value = "商品自定义5")
    private String proZdy5;

    //商品自分类
    private String[] prosClass;
    //销售级别
    private String[] saleClass;
    //商品定位
    private String[] proPosition;
    //禁止采购
    private String purchase;
    //自定义1
    private String[] zdy1;
    //自定义2
    private String[] zdy2;
    //自定义3
    private String[] zdy3;
    //自定义4
    private String[] zdy4;
    //自定义5
    private String[] zdy5;

    @ApiModelProperty(value = "特殊定义0.药品 1.医疗器械 2.含麻药品 3.保健品")
    private String specialType;

    private List<String> saleStatusList;//销售状态

    @ApiModelProperty(value = "分类id")
    private String gssgId;

    @ApiModelProperty(hidden = true)
    private List<String> gssgIds;

    @ApiModelProperty(value = "分类类型")
    private String stoGssgType;

    @ApiModelProperty(hidden = true)
    private List<GaiaStoreCategoryType> stoGssgTypes;

    @ApiModelProperty(value = "门店属性")
    private String stoAttribute;

    @ApiModelProperty(hidden = true)
    private List<String> stoAttributes;

    @ApiModelProperty(value = "是否医保店")
    private String stoIfMedical;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfMedicals;

    @ApiModelProperty(value = "纳税属性")
    private String stoTaxClass;

    @ApiModelProperty(hidden = true)
    private List<String> stoTaxClasss;

    @ApiModelProperty(value = "DTP")
    private String stoIfDtp;

    @ApiModelProperty(hidden = true)
    private List<String> stoIfDtps;

    @ApiModelProperty(value = "销售等级列表")
    private List<String> proSaleClassList;
    @ApiModelProperty(value = "会员卡号")
    private String memberCardId;
    @ApiModelProperty(value = "会员卡号集合")
    private String[] memberCarArr;

    /**
     * 特殊属性
     */
    private List<String> proTssx;
    /**
     * 三方单号
     */
    private String thirdVoucherId;
}

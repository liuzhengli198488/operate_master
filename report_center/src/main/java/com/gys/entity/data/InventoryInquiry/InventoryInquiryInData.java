package com.gys.entity.data.InventoryInquiry;

import com.gys.report.entity.GaiaStoreCategoryType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InventoryInquiryInData implements Serializable {
    private static final long serialVersionUID = -4138234128478380895L;
    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "商品编码批量查询")
    private String[] proArr;

    private String gssmBrId;

    @ApiModelProperty(value = "是否医保")
    private String medProdctStatus;

    @ApiModelProperty(value = "商品分类")
    private String proClass;

    @ApiModelProperty(value = "生产厂家")
    private String factory;

    @ApiModelProperty(value = "医保类型")
    private String yblx;

    private String proStorageCondition;

    @ApiModelProperty(value = "库存门店")
    List<InventoryStore> inventoryStore;

    @ApiModelProperty(value = "是否显示0库存")
    private String noZero;

    @ApiModelProperty(value = "行数")
    private Integer pageSize;

    @ApiModelProperty(value = "页数")
    private Integer pageNum;

    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    private List<String> classArrs;

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

    @ApiModelProperty(value = "是否批号查询 0:否 / 1:是")
    private String type;
    @ApiModelProperty(value = "是否隐藏库存数量为0和库存成本额都为0的数据 默认为true")
    private Boolean isHide;
    @ApiModelProperty(value = "地点批量查询")
    private String[] siteArr;
    @ApiModelProperty(value = "效期天数")
    private String expiryData;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "效期天数")
    private String expiryDay;

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

    @ApiModelProperty(value = "业务员维度")
    private Integer isSales;

    @ApiModelProperty(value = "业务员集合")
    private List<String> saleCodes;
}

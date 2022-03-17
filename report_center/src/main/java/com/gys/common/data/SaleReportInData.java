package com.gys.common.data;

import com.gys.entity.data.InventoryInquiry.InventoryStore;
import com.gys.report.entity.GaiaStoreCategoryType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SaleReportInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店批量查询")
    private String[] stoArr;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "商品批量数组")
    private String[] proArr;
    @ApiModelProperty(value = "商品分类查询")
    private String[][] classArr;
    @ApiModelProperty(value = "商品分类查询")
    private List<String> classArrs;
    @ApiModelProperty(value = "配送中心查询")
    private String dcCode;
    @ApiModelProperty(value = "不动销天数")
    private String noSaleDay;
    @ApiModelProperty(value = "不动销数量")
    private String saleNum;
    @ApiModelProperty(value = "是否医保 0 否 1 是  '' 全部")
    private String isMedOrNot;
    @ApiModelProperty(value = "库存门店")
    List<InventoryStore> inventoryStore;

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
}

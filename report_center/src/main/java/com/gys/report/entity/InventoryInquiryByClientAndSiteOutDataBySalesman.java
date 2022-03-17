package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class InventoryInquiryByClientAndSiteOutDataBySalesman implements Serializable {
    @ApiModelProperty(value = "店号")
    @ExcelProperty(value = "店号", index = 0)
    private String gssmBrId;

    @ApiModelProperty(value = "地点")
    @ExcelProperty(value = "地点", index = 1)
    private String gssBrName;

    @ApiModelProperty(value = "商品编号")
    @ExcelProperty(value = "商品编码", index = 2)
    private String gssmProId;

    @ApiModelProperty(value = "国际条形码")
    @ExcelProperty(value = "国际条形码", index = 3)
    private String proBarcode;

    @ApiModelProperty(value = "商品名")
    @ExcelProperty(value = "商品名称", index = 4)
    private String gssmProName;

    @ApiModelProperty(value = "商品通用名称")
    @ExcelProperty(value = "商品通用名称", index = 5)
    private String proCommonName;

    @ApiModelProperty(value = "总数量")
    @ExcelProperty(value = "库存", index = 6)
    private BigDecimal qty;

    @ExcelProperty(value = "供应商编码", index = 7)
    private String supplierCode;

    @ExcelProperty(value = "供应商名称", index = 8)
    private String supplierName;

    @ApiModelProperty(value = "零售额")
    @ExcelProperty(value = "零售额", index = 9)
    private BigDecimal retailSales;

    @ApiModelProperty(value = "零售价")
    @ExcelProperty(value = "零售价", index = 10)
    private BigDecimal retailPrice;

    @ApiModelProperty(value = "会员价")
    @ExcelProperty(value = "会员价", index = 11)
    private BigDecimal memberPrice;

    @ApiModelProperty(value = "会员日价")
    @ExcelProperty(value = "会员日价", index = 12)
    private BigDecimal memberDayPrice;

    @ApiModelProperty(value = "生产厂家")
    @ExcelProperty(value = "生产厂家", index = 13)
    private String factory;

    @ApiModelProperty(value = "产地")
    @ExcelProperty(value = "产地", index = 14)
    private String origin;

    @ApiModelProperty(value = "剂型")
    @ExcelProperty(value = "剂型", index = 15)
    private String dosageForm;

    @ApiModelProperty(value = "单位")
    @ExcelProperty(value = "单位", index = 16)
    private String unit;

    @ApiModelProperty(value = "规格")
    @ExcelProperty(value = "规格", index = 17)
    private String format;

    @ApiModelProperty(value = "批准文号")
    @ExcelProperty(value = "批准文号", index = 18)
    private String approvalNum;

    @ApiModelProperty(value = "加点后含税成本价")
    @ExcelProperty(value = "成本价", index = 19)
    private BigDecimal addPrice;

    @ApiModelProperty(value = "加点后含税成本额")
    @ExcelProperty(value = "成本额", index = 20)
    private BigDecimal addAmount;

    @ApiModelProperty(value = "商品大类编码")
    @ExcelProperty(value = "商品大类编码", index = 21)
    private String bigClass;

    @ApiModelProperty(value = "商品中类编码")
    @ExcelProperty(value = "商品中类编码", index = 22)
    private String midClass;

    @ApiModelProperty(value = "商品分类编码")
    @ExcelProperty(value = "商品分类编码", index = 23)
    private String proClass;

    @ApiModelProperty(value = "是否医保")
    @ExcelProperty(value = "是否医保", index = 24)
    private String ifMed;

    @ApiModelProperty(value = "医保类型")
    @ExcelProperty(value = "医保类型", index = 25)
    private String yblx;

    @ApiModelProperty(value = "医保编码")
    @ExcelProperty(value = "医保编码", index = 26)
    private String medProdctCode;

    @ApiModelProperty(value = "医保刷卡数量")
    @ExcelProperty(value = "医保刷卡数量", index = 27)
    private String proMedQty;

    @ApiModelProperty(value = "是否批发")
    @ExcelProperty(value = "是否批发", index = 28)
    private String isWholeSale;

    //销售级别
    @ExcelProperty(value = "销售级别", index = 29)
    private String saleClass;

    @ApiModelProperty(value = "商品定位")
    @ExcelProperty(value = "商品定位", index = 30)
    private String proPosition;

    //商品自分类
    @ExcelProperty(value = "商品自分类", index = 31)
    private String prosClass;

    //禁止采购
    @ExcelProperty(value = "禁止采购", index = 32)
    private String purchase;

    @ApiModelProperty(value = "自定义1")
    @ExcelProperty(value = "自定义字段1", index = 33)
    private String zdy1;

    @ApiModelProperty(value = "自定义2")
    @ExcelProperty(value = "自定义字段2", index = 34)
    private String zdy2;

    @ApiModelProperty(value = "自定义3")
    @ExcelProperty(value = "自定义字段3", index = 35)
    private String zdy3;

    @ApiModelProperty(value = "自定义4")
    @ExcelProperty(value = "自定义字段4", index = 36)
    private String zdy4;

    @ApiModelProperty(value = "自定义5")
    @ExcelProperty(value = "自定义字段5", index = 37)
    private String zdy5;

    @ApiModelProperty(value = "业务员编码")
    @ExcelProperty(value = "业务员编码", index = 38)
    private String saleCode;

    @ApiModelProperty(value = "业务员名字")
    @ExcelProperty(value = "业务员名字", index = 39)
    private String saleName;

    @ExcelProperty(value = "最新进价", index = 40)
    private BigDecimal poPrice;

    /**
     * 门店属性
     */
    @ExcelIgnore
    private String stoAttribute;

    /**
     * 是否医保店
     */
    @ExcelIgnore
    private String stoIfMedical;

    /**
     * DTP
     */
    @ExcelIgnore
    private String stoIfDtp;

    /**
     * 纳税属性
     */
    @ExcelIgnore
    private String stoTaxClass;

    /**
     * 店型
     */
    @ExcelIgnore
    private String shopType;

    /**
     * 店效级别
     */
    @ExcelIgnore
    private String storeEfficiencyLevel;

    /**
     * 是否直营管理
     */
    @ExcelIgnore
    private String directManaged;

    /**
     * 管理区域
     */
    @ExcelIgnore
    private String managementArea;

    @ApiModelProperty(value = "加盟商")
    @ExcelIgnore
    private String clientId;

    @ApiModelProperty(value = "序号")
    @ExcelIgnore
    private Integer index;


    @ApiModelProperty(value = "去税成本价")
    @ExcelIgnore
    private BigDecimal matMovPrice;

    @ApiModelProperty(value = "去税成本额")
    @ExcelIgnore
    private BigDecimal costAmount;

    @ApiModelProperty(value = "含税成本价")
    @ExcelIgnore
    private BigDecimal matMovRatePrice;

    @ApiModelProperty(value = "含税成本额")
    @ExcelIgnore
    private BigDecimal costRateAmount;

    /**
     * 30天銷量
     */
    @ExcelIgnore
    private BigDecimal daysOfSales30;

    /**
     * 60天銷量
     */
    @ExcelIgnore
    private BigDecimal daysOfSales60;

    /**
     * 90銷量
     */
    @ExcelIgnore
    private BigDecimal daysOfSales90;

    @ExcelIgnore
    private String gssmBatchNo;

    @ExcelIgnore
    private String productDate;

    @ExcelIgnore
    private String validUntil;

    @ExcelIgnore
    private Integer expiryData;

    @ApiModelProperty(value = "入库日期")
    @ExcelIgnore
    private String inDate;
}

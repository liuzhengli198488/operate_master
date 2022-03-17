package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class InventoryInquiryByClientAndBatchNoOutData implements Serializable {
    private static final long serialVersionUID = 8458845923235734876L;
    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @ApiModelProperty(value = "店号")
    private String gssmBrId;

    @ApiModelProperty(value = "序号")
    private Integer index;

    @ApiModelProperty(value = "店店名")
    private String gssmBrName;

    @ApiModelProperty(value = "商品编号")
    private String gssmProId;

    @ApiModelProperty(value = "商品名")
    private String gssmProName;

    @ApiModelProperty(value = "商品通用名称")
    private String  proCommonName;
    @ApiModelProperty(value = "自定义1")
    private String zdy1;
    @ApiModelProperty(value = "自定义2")
    private String zdy2;
    @ApiModelProperty(value = "自定义3")
    private String zdy3;
    @ApiModelProperty(value = "自定义4")
    private String zdy4;
    @ApiModelProperty(value = "自定义5")
    private String zdy5;
    @ApiModelProperty(value = "批号")
    private String gssmBatchNo;

    @ApiModelProperty(value = "批次")
    private String gssmBatch;

    @ApiModelProperty(value = "效期")
    private String validUntil;

    @ApiModelProperty(value = "门店库存")
    private BigDecimal storeQty;

    @ApiModelProperty(value = "门店去税成本额")
    private BigDecimal storeCostAmount;

    @ApiModelProperty(value = "门店含税成本额")
    private BigDecimal storeCostRateAmount;

    @ApiModelProperty(value = "仓库库存")
    private BigDecimal dcQty;

    @ApiModelProperty(value = "仓库去税成本额")
    private BigDecimal dcCostAmount;

    @ApiModelProperty(value = "仓库含税成本额")
    private BigDecimal dcCostRateAmount;

    @ApiModelProperty(value = "生产企业")
    private String factory;

    @ApiModelProperty(value = "产地")
    private String origin;

    @ApiModelProperty(value = "剂型")
    private String dosageForm;

    @ApiModelProperty(value = "计量单位")
    private String unit;

    @ApiModelProperty(value = "规格")
    private String format;

    @ApiModelProperty(value = "批准文号")
    private String approvalNum;

    @ApiModelProperty(value = "总数量")
    private BigDecimal qtySum;
    @ApiModelProperty(value = "去税成本额汇总")
    private BigDecimal costSum;
    @ApiModelProperty(value = "含税成本额汇总")
    private BigDecimal costRateSum;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @ApiModelProperty(value = "医保编码")
    private String medProdctCode;
    @ApiModelProperty(value = "商品定位")
    private String proPosition;
    @ApiModelProperty(value = "医保刷卡数量")
    private String proMedQty;
    @ApiModelProperty(value = "是否批发")
    private String isWholeSale;
    @ApiModelProperty(value = "是否医保")
    private String ifMed;
    @ApiModelProperty(value = "医保类型")
    private String yblx;
    @ApiModelProperty(value = "效期天数")
    private Integer expiryData;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    //销售级别
    private String saleClass;
    //商品自分类
    private String prosClass;
    //禁止采购
    private String purchase;

    /**
     * 门店属性
     */
    private String stoAttribute;

    /**
     * 店效级别
     */
    private String storeEfficiencyLevel;

    /**
     * 是否医保店
     */
    private String stoIfMedical;

    /**
     * 纳税属性
     */
    private String stoTaxClass;

    /**
     * 是否直营管理
     */
    private String directManaged;

    /**
     * DTP
     */
    private String stoIfDtp;

    /**
     *管理区域
     */
    private String managementArea;

    /**
     * 店型
     */
    private String shopType;

    /**
     * 商品描述
     */
    private String proDepict;

    /**
     * 生产日期
     */
    private String productDate;
}

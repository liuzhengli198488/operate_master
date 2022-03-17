package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "业务单据查询")
public class BusinessDocumentOutData {
    @ApiModelProperty(value = "商品编码")
    private String proId;
    @ApiModelProperty(value = "商品名称")
    private String proCommonName;
    @ApiModelProperty(value = "规格")
    private String specs;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "业务类型")
    private String matType;
    @ApiModelProperty(value = "业务名称")
    private String matName;
    @ApiModelProperty(value = "发生时间")
    private String postDate;
    @ApiModelProperty(value = "发生时间")
    private String docDate;
    @ApiModelProperty(value = "业务单据号")
    private String dnId;
    @ApiModelProperty(value = "去税金额")
    private BigDecimal batAmt;
    @ApiModelProperty(value = "税金")
    private BigDecimal rateBat;
    @ApiModelProperty(value = "税率")
    private String proInputTax;
    @ApiModelProperty(value = "合计金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "发生地点")
    private String stoCode;
    @ApiModelProperty(value = "发生地点名称")
    private String stoName;
    @ApiModelProperty(value = "申请部门")
    private String sqr;
    @ApiModelProperty(value = "申请部门名称")
    private String sqrmc;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    /**
     * 领用部门
     */
    private String gsishBranch;
    /**
     * 备注
     */
    private String gsishRemark;

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
     * 零售价
     */
    private BigDecimal proLSJ;

    /**
     * 零售额
     */
    private BigDecimal proLSAmt;

    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;

    @ApiModelProperty(value = "商品中类编码")
    private String midClass;

    @ApiModelProperty(value = "商品分类编码")
    private String proClass;

}

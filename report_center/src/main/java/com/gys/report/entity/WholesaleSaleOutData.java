package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "批发销售查询")
public class WholesaleSaleOutData {
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty("批号")
    private String batBatchNo;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "商品通用名称")
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
    @ApiModelProperty(value = "批次去税成本额")
    private BigDecimal batAmt;
    @ApiModelProperty(value = "批次去税税金")
    private BigDecimal rateBat;
    @ApiModelProperty(value = "批次含税成本额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "税率编码")
    private String poRate;
    @ApiModelProperty(value = "税率值")
    private String rate;
    @ApiModelProperty(value = "调拨去税金额")
    private BigDecimal addAmt;
    @ApiModelProperty(value = "调拨税金")
    private BigDecimal addTax;
    @ApiModelProperty(value = "调拨金额")
    private BigDecimal addtotalAmount;
    @ApiModelProperty(value = "客户编码")
    private String stoCode;
    @ApiModelProperty(value = "客户名称")
    private String stoName;
    @ApiModelProperty(value = " 数量")
    private BigDecimal qty;
    @ApiModelProperty(value = " 单价")
    private BigDecimal addAmtRate;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;

    @ApiModelProperty(value = "移动去税成本额")
    private BigDecimal movAmt;
    @ApiModelProperty(value = "移动去税税金")
    private BigDecimal rateMov;
    @ApiModelProperty(value = "移动含税成本额")
    private BigDecimal movTotalAmount;
    @ApiModelProperty(value = "发生地点")
    private String siteCode;
    @ApiModelProperty(value = "发生地点")
    private String siteName;
    //税务分类编码
    @ExcelProperty(value = "税务分类编码",index = 29)
    private String taxClassCode;
    //销售员
    @ExcelProperty(value = "销售员",index = 30)
    private String gwoOwnerSaleMan;

    @ExcelProperty(value = "销售员编码",index = 31)
    private String gwoOwnerSaleManUserId;
    //货主
    private String gwoOwner;
    //抬头备注
    @ExcelProperty(value = "抬头备注",index = 32)
    private String soHeaderRemark;

    //采购单价（入库批次价）
    @ExcelProperty(value = "采购单价（入库批次价）",index = 33)
    private BigDecimal batPoPrice;

    //商品自分类
    @ExcelProperty(value = "商品自分类",index = 34)
    private String prosClass;
    //销售级别
    @ExcelProperty(value = "销售级别",index = 35)
    private String saleClass;
    //商品定位
    @ExcelProperty(value = "商品定位",index = 36)
    private String proPosition;
    //禁止采购
    @ExcelProperty(value = "禁止采购",index = 37)
    private String purchase;
    //自定义1
    @ExcelProperty(value = "自定义1",index = 38)
    private String zdy1;
    //自定义2
    @ExcelProperty(value = "自定义2",index = 39)
    private String zdy2;
    //自定义3
    @ExcelProperty(value = "自定义3",index = 40)
    private String zdy3;
    //自定义4
    @ExcelProperty(value = "自定义4",index = 41)
    private String zdy4;
    //自定义5
    @ExcelProperty(value = "自定义5",index = 42)
    private String zdy5;
    // 大类代码
    private String bigClassCode;
    // 大类名称
    private String bigClassName;
    // 中类代码
    private String midClassCode;
    // 中类名称
    private String midClassName;
    // 商品分类代码
    private String proClassCode;
    // 商品分类名称
    private String proClassName;
    /**
     * 商品描述
     */
    private String proDepict;
    //开单人员编码
    private String buillingStaffCode;
    // 开单人员姓名
    private String buillingStaffName;
    //序号
    private Integer index;


}

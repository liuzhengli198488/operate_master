package com.gys.report.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
@CsvRow("商品销售明细表")
public class SalespersonsSalesDetailsOutData {
    @JSONField(serialize = false)
    @CsvCell(title = "序号", index = 1, fieldNo = 1)
    private Integer index;
    @ApiModelProperty(value = "门店编码")
    @CsvCell(title = "店号", index = 2, fieldNo = 1)
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    @CsvCell(title = "门店名称", index = 3, fieldNo = 1)
    private String stoName;
    @ApiModelProperty(value = "销售日期")
    @CsvCell(title = "销售日期", index = 4, fieldNo = 1)
    private String saleDate;
    @ApiModelProperty(value = "销售单号")
    @CsvCell(title = "销售单号", index = 5, fieldNo = 1)
    private String billNo;
    @ApiModelProperty(value = "商品编码")
    @CsvCell(title = "商品编码", index = 6, fieldNo = 1)
    private String selfCode;
    @ApiModelProperty(value = "商品名称")
    @CsvCell(title = "商品名称", index = 7, fieldNo = 1)
    private String proName;
    @ApiModelProperty(value = "通用名")
    @CsvCell(title = "通用名", index = 8, fieldNo = 1)
    private String proCommonName;
    @ApiModelProperty(value = "商品描述")
    @CsvCell(title = "商品描述", index = 9, fieldNo = 1)
    private String proDepict;
    @ApiModelProperty(value = "批号")
    @CsvCell(title = "批号", index = 10, fieldNo = 1)
    private String batchNo;
    @ApiModelProperty(value = "有效期")
    @CsvCell(title = "有效期", index = 11, fieldNo = 1)
    private String validData;
    @ApiModelProperty(value = "效期天数")
    @CsvCell(title = "效期天数", index = 12, fieldNo = 1)
    private int expiryDay;
    @ApiModelProperty(value = "商品大类编码")
    @CsvCell(title = "商品大类编码", index = 13, fieldNo = 1)
    private String bigClass;
    @ApiModelProperty(value = "商品中类编码")
    @CsvCell(title = "商品中类编码", index = 14, fieldNo = 1)
    private String midClass;
    @ApiModelProperty(value = "商品分类编码")
    @CsvCell(title = "商品分类编码", index = 15, fieldNo = 1)
    private String proClass;
    @ApiModelProperty(value = "支付类型")
    @CsvCell(title = "收银方式", index = 16, fieldNo = 1)
    private String payName;
    @ApiModelProperty(value = "销售等级")
    @CsvCell(title = "销售等级", index = 17, fieldNo = 1)
    private String proSaleClass;
    @ApiModelProperty(value = "自定义1")
    @CsvCell(title = "商品自定义1", index = 18, fieldNo = 1)
    private String zdy1;
    @ApiModelProperty(value = "自定义2")
    @CsvCell(title = "商品自定义2", index = 19, fieldNo = 1)
    private String zdy2;
    @ApiModelProperty(value = "自定义3")
    @CsvCell(title = "商品自定义3", index = 20, fieldNo = 1)
    private String zdy3;
    @CsvCell(title = "商品自定义4", index = 21, fieldNo = 1)
    @ApiModelProperty(value = "自定义4")
    private String zdy4;
    @CsvCell(title = "商品自定义5", index = 22, fieldNo = 1)
    @ApiModelProperty(value = "自定义5")
    private String zdy5;
    @ApiModelProperty(value = "是否医保")
    @CsvCell(title = "是否医保", index = 23, fieldNo = 1)
    private String ifMed;
    @ApiModelProperty(value = "医保编码")
    @CsvCell(title = "医保编码", index = 24, fieldNo = 1)
    private String medProdctCode;
    @ApiModelProperty(value = "规格")
    @CsvCell(title = "规格", index = 25, fieldNo = 1)
    private String specs;
    @ApiModelProperty(value = "单位")
    @CsvCell(title = "单位", index = 26, fieldNo = 1)
    private String unit;
    @ApiModelProperty(value = "生产厂家")
    @CsvCell(title = "生产厂家", index = 27, fieldNo = 1)
    private String factoryName;
    @ApiModelProperty(value = "零售价")
    @CsvCell(title = "零售价", index = 28, fieldNo = 1)
    private BigDecimal prcOne;
    @ApiModelProperty(value = "数量")
    @CsvCell(title = "销售数量", index = 29, fieldNo = 1)
    private BigDecimal qty;
    @ApiModelProperty(value = "应收金额")
    @CsvCell(title = "应收金额", index = 30, fieldNo = 1)
    private BigDecimal amountReceivable;
    @ApiModelProperty(value = "实收金额")
    @CsvCell(title = "实收金额", index = 31, fieldNo = 1)
    private BigDecimal amt;
    @ApiModelProperty(value = "折扣金额")
    private BigDecimal discount;
    @ApiModelProperty(value = "导出折扣金额")
    @CsvCell(title = "折扣金额", index = 32, fieldNo = 1)
    private String exportDiscount;
    @ApiModelProperty(value = "折扣率")
    private BigDecimal discountRate;
    @ApiModelProperty(value = "折扣率")
    @CsvCell(title = "折扣率", index = 33, fieldNo = 1)
    private String discountRateExport;
    @CsvCell(title = "销售状态", index = 34, fieldNo = 1)
    private String saleStatus;

    @ApiModelProperty(value = "营业员工号")
    @CsvCell(title = "营业员工号", index = 35, fieldNo = 1)
    private String salerId;
    @ApiModelProperty(value = "营业员姓名")
    @CsvCell(title = "营业员姓名", index = 36, fieldNo = 1)
    private String salerName;
    @ApiModelProperty(value = "医生工号")
    @CsvCell(title = "医生工号", index = 37, fieldNo = 1)
    private String doctorId;
    @ApiModelProperty(value = "医生姓名")
    @CsvCell(title = "医生姓名", index = 38, fieldNo = 1)
    private String doctorName;
    @ApiModelProperty(value = "会员卡号")
    @CsvCell(title = "会员卡号", index = 39, fieldNo = 1)
    private String hykNo;
    @ApiModelProperty(value = "会员姓名")
    @CsvCell(title = "会员姓名", index = 40, fieldNo = 1)
    private String hykName;
    @ApiModelProperty(value = "会员手机号")
    @CsvCell(title = "会员手机号", index = 41, fieldNo = 1)
    private String hykMobile;
    @ApiModelProperty(value = "成本额")
    private BigDecimal includeTaxSale;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private BigDecimal grossProfitRate;
    @ApiModelProperty(value = "收银员工号")
    private String empId;
    @ApiModelProperty(value = "收银员姓名")
    private String empName;
    @ApiModelProperty(value = "会员ID")
    private String memberId;
    @ApiModelProperty(value = "编码(导出)")
    private String userCode;
    @ApiModelProperty(value = "姓名(导出")
    private String userName;
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @ApiModelProperty(value = "批准文号")
    private String proRegisterNo;
    @CsvCell(title = "三方单号", index = 41, fieldNo = 1)
    private String thirdVoucherId;

    //商品自分类
    private String prosClass;
    //商品定位
    private String proPosition;
    //禁止采购
    private String purchase;
    private BigDecimal jdQty;
    private BigDecimal addAmt;
    private BigDecimal addTxa;


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
     * 特殊属性
     */
    @CsvCell(title = "特殊属性", index = 42, fieldNo = 1)
    private String proTssx;

    @ApiModelProperty(value = "批次")
    private String batch;


}

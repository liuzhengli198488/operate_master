package com.gys.entity.data.consignment.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Auther: tzh
 * @Date: 2021/12/7 10:40
 * @Description: StoreReportVo
 * @Version 1.0.0
 */
@Data
@ApiModel
@CsvRow("商品销售明细表")
public class StoreReportVo {
    @JSONField(serialize = false)
    @CsvCell(title = "序号", index = 1, fieldNo = 1)
    private Integer index;

    @CsvCell(title = "推荐门店编码", index = 2, fieldNo = 1)
    private String gsshReplaceBrId;

    @CsvCell(title = "推荐门店名称", index = 3, fieldNo = 1)
    private String gsshReplaceBrName;

    @CsvCell(title = "店号", index = 4, fieldNo = 1)
    private String stoCode;

    @CsvCell(title = "销售门店名称", index = 5, fieldNo = 1)
    private String stoName;

    @CsvCell(title = "销售日期", index = 6, fieldNo = 1)
    private String saleDate;

    @CsvCell(title = "销售单号", index = 7, fieldNo = 1)
    private String billNo;

    @CsvCell(title = "商品编码", index = 8, fieldNo = 1)
    private String selfCode;

    @CsvCell(title = "商品名称", index = 9, fieldNo = 1)
    private String proName;
    @CsvCell(title = "通用名", index = 10, fieldNo = 1)
    private String proCommonName;
    @CsvCell(title = "商品描述", index = 11, fieldNo = 1)
    private String proDepict;
    @ApiModelProperty(value = "规格")
    @CsvCell(title = "规格", index = 12, fieldNo = 1)
    private String specs;

    @CsvCell(title = "生产厂家", index = 13, fieldNo = 1)
    private String factoryName;

    @CsvCell(title = "产地", index = 14, fieldNo = 1)
    private String  proPlace;
    @ApiModelProperty(value = "单位")
    @CsvCell(title = "单位", index = 15, fieldNo = 1)
    private String unit;

    @CsvCell(title = "批号", index = 16, fieldNo = 1)
    private String batchNo;
    @CsvCell(title = "有效期", index = 17, fieldNo = 1)
    private String validData;

    @CsvCell(title = "商品大类编码", index = 18, fieldNo = 1)
    private String bigClass;
    @CsvCell(title = "商品中类编码", index = 19, fieldNo = 1)
    private String midClass;
    @CsvCell(title = "商品分类编码", index = 20, fieldNo = 1)
    private String proClass;

    @CsvCell(title = "收银方式", index = 21, fieldNo = 1)
    private String payName;

    @ApiModelProperty(value = "是否医保")
    @CsvCell(title = "是否医保", index = 22, fieldNo = 1)
    private String ifMed;

    @ApiModelProperty(value = "医保编码")
    @CsvCell(title = "医保编码", index = 23, fieldNo = 1)
    private String medProdctCode;

    @ApiModelProperty(value = "零售价")
    @CsvCell(title = "零售价", index = 24, fieldNo = 1)
    private BigDecimal prcOne;
    @ApiModelProperty(value = "数量")
    @CsvCell(title = "销售数量", index = 25, fieldNo = 1)
    private BigDecimal qty;

    @ApiModelProperty(value = "应收金额")
    @CsvCell(title = "应收金额", index = 26, fieldNo = 1)
    private BigDecimal amountReceivable;

    @ApiModelProperty(value = "实收金额")
    @CsvCell(title = "实收金额", index = 27, fieldNo = 1)
    private BigDecimal amt;

    @ApiModelProperty(value = "成本额")
    @CsvCell(title = "成本额", index = 28, fieldNo = 1)
    private BigDecimal includeTaxSale;

    @ApiModelProperty(value = "毛利额")
    @CsvCell(title = "毛利额", index = 29, fieldNo = 1)
    private BigDecimal grossProfitMargin;

    @ApiModelProperty(value = "毛利率")
    @CsvCell(title = "毛利率", index = 30, fieldNo = 1)
    private String grossProfitRate;

    @ApiModelProperty(value = "折扣金额")
    @CsvCell(title = "折扣金额", index = 31, fieldNo = 1)
    private BigDecimal discount;

    @ApiModelProperty(value = "折扣率")
    @CsvCell(title = "折扣率", index = 32, fieldNo = 1)
    private String discountRate;

    @ApiModelProperty(value = "营业员工号")
    @CsvCell(title = "营业员工号", index = 33, fieldNo = 1)
    private String salerId;

    @ApiModelProperty(value = "营业员姓名")
    @CsvCell(title = "营业员姓名", index = 34, fieldNo = 1)
    private String salerName;

    @ApiModelProperty(value = "医生工号")
    @CsvCell(title = "医生工号", index = 35, fieldNo = 1)
    private String doctorId;

    @ApiModelProperty(value = "医生姓名")
    @CsvCell(title = "医生姓名", index = 36, fieldNo = 1)
    private String doctorName;

    @CsvCell(title = "收银员工号", index = 37, fieldNo = 1)
    private String empId;

    @CsvCell(title = "收银员姓名", index = 38, fieldNo = 1)
    private String empName;

    @CsvCell(title = "销售等级", index = 39, fieldNo = 1)
    private String proSaleClass;

    @ApiModelProperty(value = "会员卡号")
    @CsvCell(title = "会员卡号", index = 40, fieldNo = 1)
    private String hykNo;

    @ApiModelProperty(value = "会员姓名")
    @CsvCell(title = "会员姓名", index = 41, fieldNo = 1)
    private String hykName;

    @ApiModelProperty(value = "会员手机号")
    @CsvCell(title = "会员手机号", index = 42, fieldNo = 1)
    private String hykMobile;

    @CsvCell(title = "销售状态", index = 43, fieldNo = 1)
    private String saleStatus;

//    @CsvCell(title = "效期天数", index = 36, fieldNo = 1)
//    private int expiryDay;


    /*@CsvCell(title = "销售等级", index = 16, fieldNo = 1)

    @CsvCell(title = "商品自定义1", index = 17, fieldNo = 1)
    private String zdy1;
    @CsvCell(title = "商品自定义2", index = 18, fieldNo = 1)
    private String zdy2;
    @CsvCell(title = "商品自定义3", index = 19, fieldNo = 1)
    private String zdy3;
    private String zdy4;
    private String zdy5;








    @ApiModelProperty(value = "成本额")
    private BigDecimal includeTaxSale;
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;

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

    //商品自分类
    private String prosClass;
    //商品定位
    private String proPosition;
    //禁止采购
    private String purchase;
    private BigDecimal jdQty;
    private BigDecimal addAmt;
    private BigDecimal addTxa;*/
}

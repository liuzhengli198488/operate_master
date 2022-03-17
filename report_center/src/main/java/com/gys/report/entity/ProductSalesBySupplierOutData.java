package com.gys.report.entity;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
@CsvRow("供应商商品销售明细导出")
@Data
public class ProductSalesBySupplierOutData {
    @CsvCell(title = "商品编码", index = 3, fieldNo = 1)
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @CsvCell(title = "商品名称", index = 5, fieldNo = 1)
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @CsvCell(title = "通用名称", index = 4, fieldNo = 1)
    @ApiModelProperty(value = "通用名称")
    private String proCommonName;
    @CsvCell(title = "规格", index = 9, fieldNo = 1)
    @ApiModelProperty(value = "规格")
    private String specs;
    @CsvCell(title = "效期天数", index = 7, fieldNo = 1)
    @ApiModelProperty(value = "效期天数")
    private String expiryDay;
    @CsvCell(title = "有效期至", index = 6, fieldNo = 1)
    @ApiModelProperty(value = "有效期")
    private String expiryDate;
    @CsvCell(title = "单位", index = 10, fieldNo = 1)
    @ApiModelProperty(value = "单位")
    private String unit;
    @CsvCell(title = "生产厂家", index = 13, fieldNo = 1)
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @ApiModelProperty(value = "批次")
    private String batch;
    @CsvCell(title = "供应商编码", index = 11, fieldNo = 1)
    @ApiModelProperty(value = "供应商编码")
    private String supplierCode;
    @CsvCell(title = "供应商名称", index = 12, fieldNo = 1)
    @ApiModelProperty(value = "供应商名称")
    private String supplierName;
    @CsvCell(title = "数量", index = 8, fieldNo = 1)
    @ApiModelProperty(value = "批次异动表数量")
    private BigDecimal qty;
    @ApiModelProperty(value = "销售数量占比 暂时不加")
    private BigDecimal qtyProportion;
    @CsvCell(title = "应收金额", index = 14, fieldNo = 1)
    @ApiModelProperty(value = "应收金额")
    private BigDecimal amountReceivable;
    @CsvCell(title = "实收金额", index = 16, fieldNo = 1)
    @ApiModelProperty(value = "实收金额")
    private BigDecimal amt;
    @CsvCell(title = "成本额", index = 15, fieldNo = 1)
    @ApiModelProperty(value = "成本额")
    private BigDecimal includeTaxSale;
    @CsvCell(title = "毛利额", index = 17, fieldNo = 1)
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitMargin;
    @CsvCell(title = "毛利率", index = 18, fieldNo = 1)
    @ApiModelProperty(value = "毛利率%")
    private BigDecimal grossProfitRate;

    @CsvCell(title = "门店编码", index = 1, fieldNo = 1)
    @ApiModelProperty(value = "门店编码")
    private String stoCode;

    @CsvCell(title = "门店名称", index = 2, fieldNo = 1)
    @ApiModelProperty(value = "门店名称")
    private String stoName;

    //商品自分类
    private String prosClass;
    //销售级别
    private String saleClass;
    //商品定位
    private String proPosition;
    //禁止采购
    private String purchase;
    //自定义1
    private String zdy1;
    //自定义2
    private String zdy2;
    //自定义3
    private String zdy3;
    //自定义4
    private String zdy4;
    //自定义5
    private String zdy5;
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
}

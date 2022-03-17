package com.gys.entity.data.InventoryInquiry;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@CsvRow("期末库存导出")
public class EndingInventoryOutData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @CsvCell(title = "期间日期", index = 1, fieldNo = 1)
    private String recordDate;

    @CsvCell(title = "地点编号", index = 2, fieldNo = 1)
    private String siteCode;

    @CsvCell(title = "地点名称", index = 3, fieldNo = 1)
    private String siteName;

    @CsvCell(title = "商品编码", index = 4, fieldNo = 1)
    private String proCode;

    @CsvCell(title = "国际条形码", index = 5, fieldNo = 1)
    private String proBarcode;

    @CsvCell(title = "商品名称", index = 6, fieldNo = 1)
    private String proName;

    @CsvCell(title = "商品通用名称", index = 7, fieldNo = 1)
    private String proCommonName;

    @CsvCell(title = "库存", index = 8, fieldNo = 1)
    private BigDecimal qty;

    @CsvCell(title = "供应商编码", index = 9, fieldNo = 2)
    private String supplierCode;

    @CsvCell(title = "供应商名称", index = 10, fieldNo = 2)
    private String supplierName;

    @CsvCell(title = "批号", index = 11, fieldNo = 2)
    private String batchNo;

    @CsvCell(title = "入库时间", index = 12, fieldNo = 2)
    private String inDate;

    @CsvCell(title = "到货时间", index = 13, fieldNo = 2)
    private String deliveryDate;

    @CsvCell(title = "到货数量", index = 14, fieldNo = 2)
    private String deliveryQty;

    @CsvCell(title = "有效期", index = 15, fieldNo = 2)
    private BigDecimal expiryDate;

    @CsvCell(title = "效期天数", index = 16, fieldNo = 2)
    private BigDecimal expiryDays;

    @CsvCell(title = "生产企业", index = 17, fieldNo = 1)
    private String factory;

    @CsvCell(title = "产地", index = 18, fieldNo = 1)
    private String origin;

    @CsvCell(title = "剂型", index = 19, fieldNo = 1)
    private String dosageForm;

    @CsvCell(title = "计量单位", index = 20, fieldNo = 1)
    private String unit;

    @CsvCell(title = "规格", index = 21, fieldNo = 1)
    private String format;

    @CsvCell(title = "批准文号", index = 22, fieldNo = 1)
    private String approvalNum;

    @CsvCell(title = "商品大类编码", index = 23, fieldNo = 1)
    private String bigClass;

    @CsvCell(title = "商品中类编码", index = 24, fieldNo = 1)
    private String midClass;

    @CsvCell(title = "商品分类编码", index = 25, fieldNo = 1)
    private String proClass;

    @CsvCell(title = "税金", index = 26, fieldNo = 1)
    private BigDecimal taxes;

    @CsvCell(title = "去税成本额", index = 27, fieldNo = 1)
    private BigDecimal cost;

    @CsvCell(title = "含税成本额", index = 28, fieldNo = 1)
    private BigDecimal includeTax;

    @CsvCell(title = "是否批发", index = 29, fieldNo = 1)
    private String noWholesale;

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

}

package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ：gyx
 * @Date ：Created in 16:28 2021/10/21
 * @Description：进销存库存检查报表出参
 * @Modified By：gyx
 * @Version:
 */
@Data
public class InventoryChangeCheckOutData {


    //门店编码
    @ExcelProperty(value = "门店编码",index = 0)
    private String siteCode;

    //门店名称
    @ExcelProperty(value = "门店名称",index = 1)
    private String siteName;

    //期初余额
    @ExcelProperty(value = "期初余额",index = 2)
    private BigDecimal startAmt;

    //期间进货
    @ExcelProperty(value = "期间进货",index = 3)
    private BigDecimal betweenProductIn;

    //期间退货
    @ExcelProperty(value = "期间退货",index = 4)
    private BigDecimal betweenProductRefound;

    //期间配送
    @ExcelProperty(value = "期间配送",index = 5)
    private BigDecimal betweenDistribution;

    //期间退库
    @ExcelProperty(value = "期间退库",index = 6)
    private BigDecimal betweenStockRefound;

    //期间销售
    @ExcelProperty(value = "期间销售",index = 7)
    private BigDecimal betweenSale;

    //期间损益
    @ExcelProperty(value = "期间损益",index = 8)
    private BigDecimal betweenLoss;

    ////期间期初导入
    @ExcelProperty(value = "期间期初导入",index = 9)
    private BigDecimal betweenQc;

    //期末余额
    @ExcelProperty(value = "期末余额",index = 10)
    private BigDecimal endAmt;

    /**
     * 门店属性
     */
    @ExcelProperty(value = "门店属性")
    private String stoAttribute;

    /**
     * 店效级别
     */
    @ExcelProperty(value = "店效级别")
    private String storeEfficiencyLevel;

    /**
     * 是否医保店
     */
    @ExcelProperty(value = "是否医保店")
    private String stoIfMedical;

    /**
     * 纳税属性
     */
    @ExcelProperty(value = "纳税属性")
    private String stoTaxClass;

    /**
     * 是否直营管理
     */
    @ExcelProperty(value = "是否直营管理")
    private String directManaged;

    /**
     * DTP
     */
    @ExcelProperty(value = "DTP")
    private String stoIfDtp;

    /**
     *管理区域
     */
    @ExcelProperty(value = "管理区域")
    private String managementArea;

    /**
     * 店型
     */
    @ExcelProperty(value = "店型")
    private String shopType;
}

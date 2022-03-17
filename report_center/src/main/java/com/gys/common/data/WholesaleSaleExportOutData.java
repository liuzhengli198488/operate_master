package com.gys.common.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ：csm
 * @Date ：Created in 15:20 2021/12/1
 * @Description：配送数据汇总导出Vo
 * @Modified By：gyx
 * @Version:
 */
@Data
public class WholesaleSaleExportOutData {

    @ExcelProperty(value = "业务名称",index = 0)
    private String matName;

    @ExcelProperty(value = "税率值",index = 1)
    private String rate;

    @ExcelProperty(value = "去税成本额",index = 2)
    private BigDecimal movAmt;

    @ExcelProperty(value = "去税税金",index = 3)
    private BigDecimal rateMov;

    @ExcelProperty(value = "移动含税成本额",index = 4)
    private BigDecimal movTotalAmount;

    @ExcelProperty(value = "调拨去税金额",index = 5)
    private BigDecimal addAmt;

    @ExcelProperty(value = "调拨税金",index = 6)
    private BigDecimal addTax;

    @ExcelProperty(value = "调拨金额",index = 7)
    private BigDecimal addtotalAmount;

    @ExcelProperty(value = "客户编码",index = 8)
    private String stoCode;

    @ExcelProperty(value = "客户名称",index = 9)
    private String stoName;

    @ExcelProperty(value = "发生地点",index = 10)
    private String siteCode;

    @ExcelProperty(value = "发生地点名称",index = 11)
    private String siteName;

    @ExcelProperty(value = "销售员",index = 12)
    private String gwoOwnerSaleMan;

    @ExcelProperty(value = "开单员",index = 13)
    private String buillingStaffName;

}

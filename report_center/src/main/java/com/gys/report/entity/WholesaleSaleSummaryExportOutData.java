package com.gys.report.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author ：gyx
 * @Date ：Created in 15:20 2021/12/1
 * @Description：批发销售汇总导出Vo
 * @Modified By：gyx
 * @Version:
 */
@Data
public class WholesaleSaleSummaryExportOutData {

    @ApiModelProperty(value = "业务单据号")
    @ExcelProperty(value = "业务单据号",index = 0)
    private String dnId;

    @ApiModelProperty(value = "业务名称")
    @ExcelProperty(value = "业务名称",index = 1)
    private String matName;

    @ApiModelProperty(value = "发生时间")
    @ExcelProperty(value = "发生时间",index = 2)
    private String postDate;

    @ApiModelProperty(value = "税率值")
    @ExcelProperty(value = "税率值",index = 3)
    private String rate;

    @ApiModelProperty(value = "去税成本额")
    @ExcelProperty(value = "去税成本额",index = 4)
    private BigDecimal movAmt;

    @ApiModelProperty(value = "去税税金")
    @ExcelProperty(value = "去税税金",index = 5)
    private BigDecimal rateMov;

    @ApiModelProperty(value = "移动含税成本额")
    @ExcelProperty(value = "移动含税成本额",index = 6)
    private BigDecimal movTotalAmount;

    @ApiModelProperty(value = "调拨去税金额")
    @ExcelProperty(value = "调拨去税金额",index = 7)
    private BigDecimal addAmt;

    @ApiModelProperty(value = "调拨税金")
    @ExcelProperty(value = "调拨税金",index = 8)
    private BigDecimal addTax;

    @ApiModelProperty(value = "调拨金额")
    @ExcelProperty(value = "调拨金额",index = 9)
    private BigDecimal addtotalAmount;

    @ApiModelProperty(value = "客户编码")
    @ExcelProperty(value = "客户编码",index = 10)
    private String stoCode;

    @ApiModelProperty(value = "客户名称")
    @ExcelProperty(value = "客户名称",index = 11)
    private String stoName;

    @ApiModelProperty(value = "发生地点")
    @ExcelProperty(value = "发生地点",index = 12)
    private String siteCode;

    @ApiModelProperty(value = "发生地点")
    @ExcelProperty(value = "发生地点名称",index = 13)
    private String siteName;

    //销售员
    @ExcelProperty(value = "销售员",index = 14)
    private String gwoOwnerSaleMan;

    @ExcelProperty(value = "抬头备注",index = 15)
    private String soHeaderRemark;
    // 大类名称
    @ExcelProperty(value = "大类",index = 16)
    private String bigClassName;
    // 中类名称
    @ExcelProperty(value = "中类",index = 17)
    private String midClassName;
    // 商品分类名称
    @ExcelProperty(value = "小类",index = 18)
    private String proClassName;
    @ExcelProperty(value = "开单员",index = 19)
    private String buillingStaffName;

}

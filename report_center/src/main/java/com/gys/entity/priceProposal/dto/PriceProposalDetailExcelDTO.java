package com.gys.entity.priceProposal.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description xlsx
 * @CreateTime 2022-01-13 10:25:00
 */
@Data
@ApiModel("价格建议详情列表excel")
public class PriceProposalDetailExcelDTO {

    @ExcelProperty("处理情况")
    private String adjsutStatus;

    @ExcelProperty("序号")
    private Integer no;

    @ExcelProperty("商品编码")
    private String proSelfCode;

    @ExcelProperty("商品描述")
    private String proDesc;

    @ExcelProperty("规格")
    private String proSpecs;

    @ExcelProperty("生产厂家")
    private String proFactoryName;

    @ExcelProperty("单位")
    private String proUnit;

    @ExcelProperty("最新成本价")
    private String latestCostPrice;

    @ExcelProperty("原平均售价")
    private String originalAvgSellingPrice;

    @ExcelProperty("建议零售价")
    private String suggestedRetailPrice;

    @ExcelProperty("新零售价")
    private String newRetailPrice;

    @ExcelProperty("成分分类")
    private String proCompClass;

    @ExcelProperty("成分分类描述")
    private String proCompClassName;

    @ExcelProperty("大类")
    private String proBigClass;

    @ExcelProperty("大类名称")
    private String proBigClassName;

    @ExcelProperty("商品分类编码")
    private String proClass;

    @ExcelProperty("商品分类描述")
    private String proClassName;

    @ExcelProperty("省份名称")
    private String provinceName;

    @ExcelProperty("城市名称")
    private String cityName;

}

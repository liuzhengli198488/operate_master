package com.gys.entity.data.productSaleAnalyse;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
@CsvRow("客户商品品类模板")
public class ProductSaleAnalyseOutDataClientCSV {
    @CsvCell(title = "省", index = 1, fieldNo = 1)
    @ApiModelProperty(value = "省份")
    private String province;
    @CsvCell(title = "市", index = 2, fieldNo = 1)
    @ApiModelProperty(value = "市名")
    private String city;
    @CsvCell(title = "客户ID", index = 3, fieldNo = 1)
    @ApiModelProperty(value = "客户编码")
    private String clientId;
    @CsvCell(title = "客户名称", index = 4, fieldNo = 1)
    @ApiModelProperty(value = "客户名")
    private String clientName;
    @CsvCell(title = "药德通用编码", index = 5, fieldNo = 1)
    @ApiModelProperty(value = "商品通用编码")
    private String proCode;
    @CsvCell(title = "商品描述", index = 6, fieldNo = 1)
    @ApiModelProperty(value = "商品描述")
    private String proDepict;
    @CsvCell(title = "规格", index = 7, fieldNo = 1)
    @ApiModelProperty(value = "商品规格")
    private String proSpecs;
    @CsvCell(title = "生产厂家", index = 8, fieldNo = 1)
    @ApiModelProperty(value = "生产厂家")
    private String factoryName;
    @CsvCell(title = "单位", index = 9, fieldNo = 1)
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @CsvCell(title = "门店数", index = 10, fieldNo = 1)
    @ApiModelProperty(value = "门店数")
    private Integer stoNum;
    @CsvCell(title = "动销门店数", index = 11, fieldNo = 1)
    @ApiModelProperty(value = "动销门店数")
    private String icount;
    @CsvCell(title = "商品动销天数", index = 12, fieldNo = 1)
    @ApiModelProperty(value = "商品销售天数")
    private String payDays;
    @CsvCell(title = "总销售天数", index = 13, fieldNo = 1)
    @ApiModelProperty(value = "总销售天数")
    private String saleDays;
    @CsvCell(title = "商品交易次数", index = 14, fieldNo = 1)
    @ApiModelProperty(value = "商品交易次数")
    private String payNumber;
    @CsvCell(title = "商品动销天数总交易次数", index = 15, fieldNo = 1)
    @ApiModelProperty(value = "总交易次数")
    private String saleCount;
    @CsvCell(title = "销售概率(%)", index = 16, fieldNo = 1)
    @ApiModelProperty(value = "销售概率")
    private String saleAbout;
    @CsvCell(title = "贝叶斯概率(%)", index = 17, fieldNo = 1)
    @ApiModelProperty(value = "贝叶斯概率")
    private BigDecimal bayesianProbability;
    @CsvCell(title = "总销量", index = 18, fieldNo = 1)
    @ApiModelProperty(value = "总销量")
    private String saleQty;
    @CsvCell(title = "总销售额", index = 19, fieldNo = 1)
    @ApiModelProperty(value = "总销售额")
    private String gssdAmt;
    @CsvCell(title = "总毛利额", index = 20, fieldNo = 1)
    @ApiModelProperty(value = "毛利额")
    private BigDecimal grossProfitAmt;
    @CsvCell(title = "毛利率", index = 21, fieldNo = 1)
    @ApiModelProperty(value = "毛利率")
    private String grossProfitRate;
    @CsvCell(title = "单次销售额", index = 22, fieldNo = 1)
    @ApiModelProperty(value = "单次销售额")
    private String singleAmt;
    @CsvCell(title = "单次毛利额", index = 23, fieldNo = 1)
    @ApiModelProperty(value = "单次毛利额")
    private String singleGrossProfit;
    @CsvCell(title = "综合排名", index = 24, fieldNo = 1)
    @ApiModelProperty(value = "综合排名")
    private Integer comprehensiveRank;
    @CsvCell(title = "成分一级分类", index = 25, fieldNo = 1)
    @ApiModelProperty(value = "成分一级分类编码")
    private String proBigCompClass;
    @CsvCell(title = "成分一级分类描述", index = 26, fieldNo = 1)
    @ApiModelProperty(value = "成分一级分类名称")
    private String proBigCompClassName;
    @CsvCell(title = "成分二级分类", index = 27, fieldNo = 1)
    @ApiModelProperty(value = "成分二级分类编码")
    private String proMidCompClass;
    @CsvCell(title = "成分二级分类描述", index = 28, fieldNo = 1)
    @ApiModelProperty(value = "成分二级分类名称")
    private String proMidCompClassName;
    @CsvCell(title = "成分分类", index = 29, fieldNo = 1)
    @ApiModelProperty(value = "成分分类编码")
    private String proCompClass;
    @CsvCell(title = "成分分类描述", index = 30, fieldNo = 1)
    @ApiModelProperty(value = "成分分类名称")
    private String proCompClassName;
    @CsvCell(title = "商品一级分类", index = 31, fieldNo = 1)
    @ApiModelProperty(value = "商品一级分类编码")
    private String proBigClass;
    @CsvCell(title = "商品一级分类描述", index = 32, fieldNo = 1)
    @ApiModelProperty(value = "商品一级分类")
    private String proBigClassName;
    @CsvCell(title = "商品二级分类", index = 33, fieldNo = 1)
    @ApiModelProperty(value = "商品二级分类编码")
    private String proMidClass;
    @CsvCell(title = "商品二级分类描述", index = 34, fieldNo = 1)
    @ApiModelProperty(value = "商品二级分类")
    private String proMidClassName;
    @CsvCell(title = "商品分类", index = 35, fieldNo = 1)
    @ApiModelProperty(value = "商品分类编码")
    private String proClass;
    @CsvCell(title = "商品分类描述", index = 36, fieldNo = 1)
    @ApiModelProperty(value = "商品分类")
    private String proClassName;
}

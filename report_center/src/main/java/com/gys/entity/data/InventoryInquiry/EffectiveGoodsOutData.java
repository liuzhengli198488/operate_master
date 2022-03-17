package com.gys.entity.data.InventoryInquiry;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "效期汇总")
@CsvRow("效期汇总导出")
public class EffectiveGoodsOutData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;

    @ApiModelProperty(value = "月份")
    @CsvCell(title = "月份", index = 1, fieldNo = 1)
    private String recordDate;

    @ApiModelProperty(value = "地点编码")
    @CsvCell(title = "地点编码", index = 2, fieldNo = 1)
    private String siteCode;

    @ApiModelProperty(value = "地点名称")
    @CsvCell(title = "地点名称", index = 3, fieldNo = 1)
    private String siteName;

    @ApiModelProperty(value = "库存品项")
    @CsvCell(title = "库存品项", index = 4, fieldNo = 1)
    private BigDecimal InventoryItem;

    @ApiModelProperty(value = "效期品项")
    @CsvCell(title = "效期品项", index = 5, fieldNo = 1)
    private BigDecimal expiryItem;

    @ApiModelProperty(value = "效期品项占比")
    @CsvCell(title = "效期品项占比", index = 6, fieldNo = 1)
    private BigDecimal expiryRate;

    @ApiModelProperty(value = "库存成本额")
    @CsvCell(title = "库存成本额", index = 7, fieldNo = 1)
    private BigDecimal includeTax;

    @ApiModelProperty(value = "效期成本额")
    @CsvCell(title = "效期成本额", index = 8, fieldNo = 1)
    private BigDecimal expiryIncludeTax;

    @ApiModelProperty(value = "效期成本占比")
    @CsvCell(title = "效期成本占比", index = 9, fieldNo = 1)
    private BigDecimal expiryIncludeRate;

    @ApiModelProperty(value = "库存零售额")
    @CsvCell(title = "库存零售额", index = 10, fieldNo = 1)
    private BigDecimal retailSales;

    @ApiModelProperty(value = "效期零售额")
    @CsvCell(title = "效期零售额", index = 11, fieldNo = 1)
    private BigDecimal expiryRetailSales;

    @ApiModelProperty(value = "效期零售占比")
    @CsvCell(title = "效期零售占比", index = 12, fieldNo = 1)
    private BigDecimal expiryRetailRate;

}

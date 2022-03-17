package com.gys.entity.data.InventoryInquiry;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@CsvRow("期末库存导出")
public class EffectiveGoodsOutDataTotal {

    @CsvCell(title = "库存品项", index = 4, fieldNo = 1)
    private BigDecimal InventoryItem;

    @CsvCell(title = "效期品项", index = 5, fieldNo = 1)
    private BigDecimal expiryItem;

    @CsvCell(title = "效期品项占比", index = 6, fieldNo = 1)
    private String expiryRate;

    @CsvCell(title = "库存成本额", index = 7, fieldNo = 1)
    private BigDecimal includeTax;

    @CsvCell(title = "效期成本额", index = 8, fieldNo = 1)
    private BigDecimal expiryIncludeTax;

    @CsvCell(title = "效期成本占比", index = 9, fieldNo = 1)
    private String expiryIncludeRate;

    @CsvCell(title = "库存零售额", index = 10, fieldNo = 1)
    private BigDecimal retailSales;

    @CsvCell(title = "效期零售额", index = 11, fieldNo = 1)
    private BigDecimal expiryRetailSales;

    @CsvCell(title = "效期零售占比", index = 12, fieldNo = 1)
    private String expiryRetailRate;
}

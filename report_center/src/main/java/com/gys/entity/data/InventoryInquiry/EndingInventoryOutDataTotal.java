package com.gys.entity.data.InventoryInquiry;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@CsvRow("期末库存导出")
public class EndingInventoryOutDataTotal {

    // "库存"
    private BigDecimal qty;


    // "税金"
    private BigDecimal taxes;

    // "去税成本额"
    private BigDecimal cost;

    // "含税成本额"
    private BigDecimal includeTax;

}

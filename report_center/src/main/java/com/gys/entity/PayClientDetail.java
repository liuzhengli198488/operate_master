package com.gys.entity;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@CsvRow("用户付款明细")
public class PayClientDetail {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @CsvCell(title = "门店编码", index = 1, fieldNo = 1)
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @CsvCell(title = "门店名", index = 2, fieldNo = 1)
    @ApiModelProperty(value = "门店名")
    private String stoName;
    @CsvCell(title = "付款金额（元）", index = 3, fieldNo = 1)
    @ApiModelProperty(value = "付款金额（元）")
    private BigDecimal payAmt;
    @CsvCell(title = "付款基数（元）", index = 4, fieldNo = 1)
    @ApiModelProperty(value = "付款基数（元）")
    private BigDecimal payBasicNum;
    @CsvCell(title = "月数（个）", index = 5, fieldNo = 1)
    @ApiModelProperty(value = "月数（个）")
    private Integer monthNum;
    @CsvCell(title = "付款类型", index = 6, fieldNo = 1)
    @ApiModelProperty(value = "付款类型")
    private String payType;
    @CsvCell(title = "付款期间", index = 7, fieldNo = 1)
    @ApiModelProperty(value = "付款期间")
    private String payDate;
}

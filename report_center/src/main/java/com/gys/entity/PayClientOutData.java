package com.gys.entity;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@CsvRow("用户付款记录")
public class PayClientOutData {
    @CsvCell(title = "用户编码", index = 1, fieldNo = 1)
    @ApiModelProperty(value = "用户编码")
    private String clientId;
    @CsvCell(title = "用户名", index = 2, fieldNo = 1)
    @ApiModelProperty(value = "用户名")
    private String clientName;
    @CsvCell(title = "付款日期", index = 3, fieldNo = 1)
    @ApiModelProperty(value = "付款日期")
    private String payDate;
    @CsvCell(title = "付款金额", index = 4, fieldNo = 1)
    @ApiModelProperty(value = "付款金额")
    private BigDecimal payAmt;
    @CsvCell(title = "付款执行人", index = 5, fieldNo = 1)
    @ApiModelProperty(value = "付款执行人")
    private String userName;
    @CsvCell(title = "付款订单号", index = 6, fieldNo = 1)
    @ApiModelProperty(value = "付款订单号")
    private String payNo;
    @CsvCell(title = "开票类型", index = 7, fieldNo = 1)
    @ApiModelProperty(value = "开票类型")
    private String invoiceType;
    @CsvCell(title = "付款类型", index = 8, fieldNo = 1)
    @ApiModelProperty(value = "付款类型名")
    private String payTypeName;
    @ApiModelProperty(value = "付款类型")
    private String payType;

}

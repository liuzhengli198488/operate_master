package com.gys.report.entity;

import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "进销存明细查询返回实体")
@CsvRow("进销存明细")
public class InvoicingOutData {

    @ApiModelProperty(hidden = true)
    @CsvCell(title = "序号", index = 1, fieldNo = 1)
    private Integer index;

    @ApiModelProperty(value = "商品编码")
    @CsvCell(title = "商品编码", index = 2, fieldNo = 1)
    private String gssbProId;

    @ApiModelProperty(value = "商品名称")
    @CsvCell(title = "商品名称", index = 3, fieldNo = 1)
    private String gssbProName;

    @ApiModelProperty(value = "商品通用名")
    @CsvCell(title = "商品通用名", index = 4, fieldNo = 1)
    private String proCommonname;

    @ApiModelProperty(value = "商品描述")
    @CsvCell(title = "商品描述", index = 5, fieldNo = 1)
    private String proDepict;

    @ApiModelProperty(value = "零售价")
    @CsvCell(title = "零售价", index = 6, fieldNo = 1)
    private BigDecimal gssbRetailPrice;

    @ApiModelProperty(value = "批号")
    @CsvCell(title = "批号", index = 7, fieldNo = 1)
    private String gssbBatchNo;

    @ApiModelProperty(value = "有效期至")
    @CsvCell(title = "有效期至", index = 8, fieldNo = 1)
    private String gssbValidUntil;

    @ApiModelProperty(value = "类型")
    @CsvCell(title = "类型", index = 9, fieldNo = 1)
    private String gssbType;

    @ApiModelProperty(value = "医保编码")
    @CsvCell(title = "医保编码", index = 10, fieldNo = 1)
    private String proMedId;

    @ApiModelProperty(value = "是否医保")
    @CsvCell(title = "是否医保", index = 11, fieldNo = 1)
    private String ifMed;

    @ApiModelProperty(value = "单号")
    @CsvCell(title = "单号", index = 12, fieldNo = 1)
    private String gssbVoucherId;

    @ApiModelProperty(value = "配送单号")
    @CsvCell(title = "配送单号", index = 13, fieldNo = 1)
    private String deliveryOrderNo;

    @ApiModelProperty(value = "拣货单号")
    @CsvCell(title = "拣货单号", index = 14, fieldNo = 1)
    private String pickingOrderNo;

    @ApiModelProperty(value = "开单日期")
    @CsvCell(title = "开单日期", index = 15, fieldNo = 1)
    private String pickingDate;

    @ApiModelProperty(value = "发生日期")
    @CsvCell(title = "日期", index = 16, fieldNo = 1)
    private String gssbDate;

    @ApiModelProperty(value = "数量")
    @CsvCell(title = "数量", index = 17, fieldNo = 1)
    private BigDecimal gssbQty;

    @ApiModelProperty(value = "厂家")
    @CsvCell(title = "厂家", index = 18, fieldNo = 1)
    private String gssbFactory;

    @ApiModelProperty(value = "产地")
    @CsvCell(title = "产地", index = 19, fieldNo = 1)
    private String gssbOrigin;

    @ApiModelProperty(value = "剂型")
    @CsvCell(title = "剂型", index = 20, fieldNo = 1)
    private String gssbDosageForm;

    @ApiModelProperty(value = "单位")
    @CsvCell(title = "单位", index = 21, fieldNo = 1)
    private String gssbUnit;

    @ApiModelProperty(value = "规格")
    @CsvCell(title = "规格", index = 22, fieldNo = 1)
    private String gssbFormat;

    @ApiModelProperty(value = "批准文号")
    @CsvCell(title = "批准文号", index = 23, fieldNo = 1)
    private String gssbApprovalNum;

    @ApiModelProperty(value = "配送金额")
    @CsvCell(title = "配送金额", index = 24, fieldNo = 1)
    private BigDecimal deliveryAmount;

    @ApiModelProperty(value = "配送价")
    @CsvCell(title = "配送价", index = 25, fieldNo = 1)
    private BigDecimal deliveryPrice;

    @ApiModelProperty(value = "退货单号")
    private String billReturnNo;

    @ApiModelProperty(value = "")
    private List<InvoicingOutData> detail;

    @ApiModelProperty(hidden = true)
    private String clientId;

    @ApiModelProperty(hidden = true)
    private String gssbBrId;
}

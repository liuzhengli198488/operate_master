
package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GetReplenishDetailInData implements Serializable {
    private static final long serialVersionUID = 428457577361056485L;
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "补货门店")
    private String gsrdBrId;
    @ApiModelProperty(value = "补货门店名")
    private String gsrdBrName;
    @ApiModelProperty(value = "补货单号")
    private String gsrdVoucherId;
    @ApiModelProperty(value = "补货日期")
    private String gsrdDate;
    @ApiModelProperty(value = "行号")
    private String gsrdSerial;
    @ApiModelProperty(value = "商品编码")
    private String gsrdProId;
    @ApiModelProperty(value = "商品名")
    private String gsrdProName;
    @ApiModelProperty(value = "商品通用名")
    private String gsrdProCommonName;
    @ApiModelProperty(value = "移动平均价")
    private BigDecimal gsrdAverageCost;
    @ApiModelProperty(value = "毛利率")
    private String gsrdGrossMargin;
    @ApiModelProperty(value = "90天销量")
    private String gsrdSalesDays1;
    @ApiModelProperty(value = "30天销量")
    private String gsrdSalesDays2;
    @ApiModelProperty(value = "7天销量")
    private String gsrdSalesDays3;
    @ApiModelProperty(value = "建议补货量")
    private String gsrdProposeQty;
    @ApiModelProperty(value = "补货量")
    private String gsrdNeedQty;
    @ApiModelProperty(value = "订单价")
    private String voucherAmt;
    @ApiModelProperty(value = "本店库存")
    private String gsrdStockStore;
    @ApiModelProperty(value = "仓库库存")
    private String gsrdStockDepot;
    @ApiModelProperty(value = "最小陈列")
    private String gsrdDisplayMin;
    @ApiModelProperty(value = "中包装")
    private String gsrdPackMidsize;
    @ApiModelProperty(value = "零售价")
    private BigDecimal gsrdProPrice;
    @ApiModelProperty(value = "最后一次供应商编码")
    private String gsrdLastSupid;
    @ApiModelProperty(value = "最后一次供应商")
    private String gsrdLastSupName;
    @ApiModelProperty(value = "计量单位")
    private String gsrdProUnit;
    @ApiModelProperty(value = "税率")
    private String gsrdProRate;
    @ApiModelProperty(value = "付款条件")
    private String payTerm;
    @ApiModelProperty(value = "规格")
    private String gsrdProPec;
    @ApiModelProperty(value = "生产企业")
    private String gsrdProCompany;
    @ApiModelProperty(value = "最后一次进货价")
    private String gsrdLastPrice;
    @ApiModelProperty(value = "采购订单单价")
    private String poPrice;
    @ApiModelProperty(value = "销项税率")
    private String gsrdTaxTate;
    @ApiModelProperty(value = "进项税率编码")
    private String gsrdProRateCode;
    @ApiModelProperty(value = "是否手工添加 1 是")
    private String gsrdFlag;
    @ApiModelProperty(value = "是否特殊商品 0 否 1 是")
    private String isSpecial;
    @ApiModelProperty(value = "商品大类编码")
    private String proBigClass;
    @ApiModelProperty(value = "商品大类编码")
    private String bigClass;
    @ApiModelProperty(value = "配送中心编码")
    private String dcCode;
    @ApiModelProperty("所有门店库存")
    private BigDecimal allStoreStock;

    @ApiModelProperty("是否修改过原始单")
    private Boolean isEditOriginal;
}

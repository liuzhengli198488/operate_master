
package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetReplenishOutData implements Serializable {
    private static final long serialVersionUID = -5690244155319260233L;
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "补货单号")
    private String gsrhVoucherId;
    @ApiModelProperty(value = "补货日期(现用)")
    private String gsrhDate;
    @ApiModelProperty(value = "补货类型")
    private String gsrhType;
    @ApiModelProperty(value = "补货金额")
    private BigDecimal gsrhTotalAmt;
    @ApiModelProperty(value = "补货数量")
    private String gsrhTotalQty;
    @ApiModelProperty(value = "采购总价")
    private String voucherAmt;
    @ApiModelProperty(value = "补货人员")
    private String gsrhEmp;
    @ApiModelProperty(value = "补货状态是否审核")
    private String gsrhStatus;
    @ApiModelProperty(value = "采购单号")
    private String gsrhPoid;
    @ApiModelProperty(value = "库存数量")
    private String stock;
    @ApiModelProperty(value = "管制药品数量")
    private String param;
    @ApiModelProperty(value = "90天销售总数量")
    private String salesCountOne;
    @ApiModelProperty(value = "30天销售总数量")
    private String salesCountTwo;
    @ApiModelProperty(value = "7天销售总数量")
    private String salesCountThree;
    @ApiModelProperty(value = "平均成本价")
    private BigDecimal averageCost;
    @ApiModelProperty(value = "批次成本价")
    private BigDecimal batchCost;
    @ApiModelProperty(value = "商品编码")
    private String proId;
    @ApiModelProperty(value = "销售数量")
    private String saleQty;
    @ApiModelProperty(value = "销售日期")
    private String saleDate;
    @ApiModelProperty(value = "补货方式 0正常补货，1紧急补货,2铺货,3互调")
    private String gsrhPattern;
    private List<GetReplenishDetailOutData> detailList;
    private String brId;
    @ApiModelProperty(value = "审核时间")
    private String gsrhTime;
    @ApiModelProperty(value = "是否特殊补货单 0 否 1 是")
    private String isSpecial;
    @ApiModelProperty(value = "创建时间")
    private String gsrhCreateTime;
    @ApiModelProperty(value = "审核日期(现用)")
    private String gsrhCheckDate;
    @ApiModelProperty(value = "补货地点")
    private String gsrhAddr;
}

package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SelectMedicalSummaryDTO {


    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "险种类型  310职工基本医疗保险"+
                                        "320公务员医疗补助"+
                                        "330大额医疗费用补助"+
                                        "340离休人员医疗保障"+
                                        "390城乡居民基本医疗保险"+
                                        "392城乡居民大病医疗保险"+
                                        "510生育保险")
    private String inSuType;

    @ApiModelProperty(value = "险种名称")
    private String inSuTypeName;

    @ApiModelProperty("清算类别")
    private String clrType;

    @ApiModelProperty("清算名称")
    private String clrTypeName;

    @ApiModelProperty("医疗费总额")
    private BigDecimal medfeeSumant;

    @ApiModelProperty("基金支付总额")
    private BigDecimal fundPaySumamt;

    @ApiModelProperty("个人账户支出")
    private BigDecimal acctPay;

    @ApiModelProperty("定点医疗结算笔数")
    private String medicalSettlementCount;

    @ApiModelProperty("对账结果")
    private String accountResult;

    @ApiModelProperty("个人现金支付")
    private BigDecimal psnCashPayamt;

    @ApiModelProperty("人次")
    private BigDecimal personTime;

    @ApiModelProperty("对账结果说明")
    private BigDecimal accountResultExplain;

    @ApiModelProperty("清算经办机构")
    private String clrOptins;

    @ApiModelProperty("清算经办机构名称")
    private String clrOptinsName;

    @ApiModelProperty("门店编码")
    private String storeCode;

    @ApiModelProperty("门店编码名称")
    private String storeName;
}

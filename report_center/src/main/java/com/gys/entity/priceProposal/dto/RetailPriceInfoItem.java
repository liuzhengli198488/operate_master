package com.gys.entity.priceProposal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel("调价申请")
public class RetailPriceInfoItem {

    @ApiModelProperty(value = "商品编号", name = "prcProduct")
    @NotBlank(message = "商品编号不能为空")
    private String prcProduct;

    @ApiModelProperty(value = "金额", name = "prcAmount")
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.0", message = "金额不能为负数")
    @DecimalMax(value = "999999999.9999", message = "金额格式不正确")
    private BigDecimal prcAmount;

    @ApiModelProperty(value = "有效期起", name = "prcEffectDate")
    @NotBlank(message = "有效期起不能为空")
    private String prcEffectDate;

    @ApiModelProperty(value = "修改前价格", name = "prcAmountBefore")
    @DecimalMin(value = "0.0", message = "修改前价格不能为负数")
    @DecimalMax(value = "999999999.9999", message = "修改前价格格式不正确")
    private BigDecimal prcAmountBefore;

    @ApiModelProperty(value = "单位", name = "prcUnit")
    @NotBlank(message = "单位不能为空")
    private String prcUnit;

    @ApiModelProperty(value = "不允许积分", name = "prcNoIntegral")
//    @NotBlank(message = "不允许积分不能为空")
    private String prcNoIntegral;

    @ApiModelProperty(value = "不允许会员卡打折", name = "prcNoDiscount")
//    @NotBlank(message = "不允许会员卡打折不能为空")
    private String prcNoDiscount;

    @ApiModelProperty(value = "不允许积分兑换", name = "prcNoExchange")
//    @NotBlank(message = "不允许积分兑换不能为空")
    private String prcNoExchange;

    @ApiModelProperty(value = "限购数量", name = "prcLimitAmount")
    private String prcLimitAmount;

    @ApiModelProperty(value = "总部调价", name = "prcHeadPrice")
    private String prcHeadPrice;

    /**
     * 销售级别
     */
    private String prcSlaeClass;
    /**
     * 自定义字段1
     */
    private String prcZdy1;
    /**
     * 自定义字段2
     */
    private String prcZdy2;
    /**
     * 自定义字段3
     */
    private String prcZdy3;
    /**
     * 自定义字段4
     */
    private String prcZdy4;
    /**
     * 自定义字段5
     */
    private String prcZdy5;
}

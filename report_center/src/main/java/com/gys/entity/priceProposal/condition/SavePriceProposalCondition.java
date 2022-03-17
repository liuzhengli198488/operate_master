package com.gys.entity.priceProposal.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 保存调价建议信息需要的参数
 * @CreateTime 2022-01-12 10:37:00
 */
@Data
public class SavePriceProposalCondition {

    @ApiModelProperty("价格建议单号")
    @NotBlank(message = "价格建议单号不能为空")
    private String priceProposalNo;

    @ApiModelProperty("省份编码")
    @NotBlank(message = "省份编码不能为空")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    @ApiModelProperty("加盟商id")
    private String clientId;

    @ApiModelProperty("商品编码")
    @NotBlank(message = "商品编码不能为空")
    private String proCode;

    @ApiModelProperty("新零售价")
    @NotBlank(message = "新零售价不能为空")
    private String newPrice;

}

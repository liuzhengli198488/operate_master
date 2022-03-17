package com.gys.entity.priceProposal.condition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 查询价格建议详情列表需要的参数
 * @CreateTime 2022-01-13 10:17:00
 */
@Data
@ApiModel("查询价格建议详情列表需要的参数")
public class SelectPriceProposalDetailListCondition {

    @ApiModelProperty("加盟商id")
    private String clientId;

    @ApiModelProperty("价格建议单号")
    @NotBlank(message = "价格建议单号不能为空")
    private String priceProposalNo;

}

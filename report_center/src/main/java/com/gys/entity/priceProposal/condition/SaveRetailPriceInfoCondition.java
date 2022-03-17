package com.gys.entity.priceProposal.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 提交调价建议信息需要的参数
 * @CreateTime 2022-01-12 10:37:00
 */
@Data
public class SaveRetailPriceInfoCondition {

    @ApiModelProperty("价格建议单号")
    private String priceProposalNo;

    @ApiModelProperty("商品编码")
    private List<ProInfoCondition> pros;

    @ApiModelProperty("门店编码")
    private String[] stoCode;

    @ApiModelProperty(value = "有效期起")
    private String prcEffectDate;

//    @ApiModelProperty(value = "加盟商id")
//    private String clientId;

}

package com.gys.entity.priceProposal.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 提交调价建议信息需要的参数
 * @CreateTime 2022-01-12 10:37:00
 */
@Data
public class ProInfoCondition {

    @ApiModelProperty("商品编码")
    private String proCode;

    @ApiModelProperty("新零售价")
    private String newPrice;

}

package com.gys.entity.priceProposal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 所有门店信息
 * @CreateTime 2022-01-13 15:45:00
 */
@Data
@ApiModel("所有门店信息")
public class AllStosVO {

    @ApiModelProperty("门店编码")
    private String stoCode;
    @ApiModelProperty("门店名称")
    private String stoName;

}

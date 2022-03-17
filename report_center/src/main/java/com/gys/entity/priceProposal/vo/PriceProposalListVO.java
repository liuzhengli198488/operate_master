package com.gys.entity.priceProposal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 查询价格建议列表返回的数据
 * @CreateTime 2022-01-13 10:25:00
 */
@Data
@ApiModel("查询价格建议列表返回的数据")
public class PriceProposalListVO {

    @ApiModelProperty("价格建议单号")
    private String priceProposalNo;

    @ApiModelProperty("价格建议时间")
    private String createdTime;

    @ApiModelProperty("价格建议失效时间")
    private String priceProposalInvalidTime;

    @ApiModelProperty("价格建议品项数")
    private Integer priceProposalItemNum;

    @ApiModelProperty("已调价品项数")
    private Integer setItemNum;

    @ApiModelProperty("单据状态0.待完成 1.已完成 2.失效")
    private Integer billStatus;

}

package com.gys.entity.priceProposal.condition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 查询价格建议列表需要的参数
 * @CreateTime 2022-01-13 10:17:00
 */
@Data
@ApiModel("查询价格建议列表需要的参数")
public class SelectPriceProposalListCondition {

    private String clientId;

    @ApiModelProperty("价格建议单号")
    private String priceProposalNo;

    @ApiModelProperty("起始日期yyyy-MM-dd")
    private String startTime;

    @ApiModelProperty("结束日期yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty("单据状态NULL.全部 0.待完成 1.已完成 2.失效")
    private String billStatus;

    @ApiModelProperty("商品编码")
    private String proCode;

}

package com.gys.entity.priceProposal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Jinwencheng
 * @version 1.0
 * @Description 价格建议
 * @createTime 2022-01-11 13:52:00
 */
@ApiModel("价格建议主表")
@Data
@Table(name = "GAIA_PRODUCT_PRICE_PROPOSAL_H")
public class ProductPriceProposalH implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("价格建议单号")
    @Column(name = "PRICE_PROPOSAL_NO")
    private String priceProposalNo;

    @ApiModelProperty("价格建议失效时间")
    @Column(name = "PRICE_PROPOSAL_INVALID_TIME")
    private String priceProposalInvalidTime;

    @ApiModelProperty("价格建议品项数")
    @Column(name = "PRICE_PROPOSAL_ITEM_NUM")
    private Integer priceProposalItemNum;

    @ApiModelProperty("单据状态0.待完成 1.已完成 2.失效")
    @Column(name = "BILL_STATUS")
    private Integer billStatus;

    @ApiModelProperty("创建人ID")
    @Column(name = "CREATED_ID")
    private String createdId;

    @ApiModelProperty("创建时间")
    @Column(name = "CREATED_TIME")
    private String createdTime;

}

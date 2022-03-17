package com.gys.entity.priceProposal.dto;

import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 零售价格审批
 * @CreateTime 2022-01-12 10:34:00
 */
@Data
public class RetailPriceDTO {

    // 价格生效日期
    private String prcEffectDate;
    // 审批状态 1.审批通过 2.审批未通过
    private String prcApprovalSuatus;

}

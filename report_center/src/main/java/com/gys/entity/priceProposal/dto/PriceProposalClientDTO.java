package com.gys.entity.priceProposal.dto;

import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 加盟商信息
 * @CreateTime 2022-01-19 14:52:00
 */
@Data
public class PriceProposalClientDTO {

    private String clientId;

    private String clientName;

    private String provinceId;

    private String cityId;

    private String type1;

    private String type2;

}

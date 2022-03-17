package com.gys.entity.priceProposal.dto;

import lombok.Data;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description TODO
 * @CreateTime 2022-01-20 2:38:00
 */
@Data
public class CityProClassifyDTO {

    private String maxNo;

    private String clientId;

    private String clientName;

    private String provinceId;

    private String provinceName;

    private String cityId;

    private String cityName;

    private String proCode;

    private String avgSellingPrice;

    private String bayesianProbability;

    private String start;

    private String end;

    private String gap;

}

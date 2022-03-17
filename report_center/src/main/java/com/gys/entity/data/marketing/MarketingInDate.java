package com.gys.entity.data.marketing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MarketingInDate implements Serializable {

    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "门店")
    private String stoCode;
    @ApiModelProperty(value = "营销活动id")
    private String gsphMarketid;
    private String userId;
    private int qty;




}

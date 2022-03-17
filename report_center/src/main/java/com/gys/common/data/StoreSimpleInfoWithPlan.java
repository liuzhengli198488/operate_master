package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wu mao yin
 * @Title: 门店简要信息
 * @date 2021/11/2614:01
 */
@Data
public class StoreSimpleInfoWithPlan implements Serializable {

    @ApiModelProperty("方案id")
    private Integer planId;

    @ApiModelProperty("门店编码")
    private String stoCode;

    @ApiModelProperty("门店名称")
    private String stoName;

    @ApiModelProperty("门店简称")
    private String stoShortName;

}

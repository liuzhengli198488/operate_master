package com.gys.entity.priceProposal.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("调价申请提交参数")
public class RetailPriceInfoSaveRequestDto {

    private String client;

    @ApiModelProperty(value = "价格类型", name = "prcClass")
    @NotBlank(message = "价格类型不能为空")
    private String prcClass;

    @ApiModelProperty(value = "门店编码", name = "storeList")
    @NotNull(message = "门店编码不能为空")
    private List<String> storeList;


    @ApiModelProperty(value = "调价商品集合", name = "prcClass")
    @NotEmpty(message = "调价商品不能不传")
    private List<RetailPriceInfoItem> proList;


}

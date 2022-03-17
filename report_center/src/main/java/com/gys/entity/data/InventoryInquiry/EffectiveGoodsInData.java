package com.gys.entity.data.InventoryInquiry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 效期商品汇总查询 入参
 * @Author huxinxin
 * @Date 2021/5/26 10:22
 * @Version 1.0.0
 **/
@Data
@ApiModel
public class EffectiveGoodsInData {
    private String clientId;
    @ApiModelProperty(value = "开始时间")
    private String startMonth;
    @ApiModelProperty(value = "结束时间")
    private String endMonth;
    private List<String> dcEndMonth;
    @ApiModelProperty(value = "地点编码")
    private String[] siteArr;
    @ApiModelProperty(value = "效期天数")
    private String expiryDays;
}

package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NoSaleOutData {
    @ApiModelProperty(value = "汇总")
    private NoSaleTotalOutData totalOutData;
    @ApiModelProperty(value = "列表")
    private List<NoSaleItemOutData> itemOutData;
}

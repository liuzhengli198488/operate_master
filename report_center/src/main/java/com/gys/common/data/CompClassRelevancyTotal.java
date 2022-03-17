package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CompClassRelevancyTotal {
    @ApiModelProperty(value = "关联改善")
    private String compClassRelevancyContent;
    @ApiModelProperty(value = "TOP10成分")
    private List<CompClassTopTen> compClassTopTenList;
}

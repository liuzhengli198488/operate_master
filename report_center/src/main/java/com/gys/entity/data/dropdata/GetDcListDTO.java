package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetDcListDTO {


    @ApiModelProperty(value = "配送仓库ID")
    private String dcId;

    @ApiModelProperty(value = "配送仓库名称")
    private String dcName;

}

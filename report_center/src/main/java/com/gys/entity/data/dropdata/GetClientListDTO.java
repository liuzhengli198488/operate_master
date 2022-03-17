package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetClientListDTO {

    @ApiModelProperty(value = "加盟商ID")
    private String clientId;

    @ApiModelProperty(value = "加盟商姓名")
    private String clientName;

    private List<GetDcListDTO> dcList;

}

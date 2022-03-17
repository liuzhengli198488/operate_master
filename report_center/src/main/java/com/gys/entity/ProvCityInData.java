package com.gys.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProvCityInData implements Serializable {

    /**
     * 省
     */
    @ApiModelProperty(value="省")
    private List<String> provinceList;

    /**
     * 市
     */
    @ApiModelProperty(value="市")
    private List<String> cityList;
}

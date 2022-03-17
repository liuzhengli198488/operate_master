package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wu mao yin
 * @Title: 查询营业员入参
 * @date 2021/12/310:23
 */
@Data
public class SelectAssistantDTO implements Serializable {

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @ApiModelProperty("加盟商")
    private String client;

    @ApiModelProperty("门店")
    private String stoCode;

    @ApiModelProperty("搜索条件")
    private String searchValue;

    @ApiModelProperty("员工类型 1: 营业员，2: 收银员，3: 医生")
    private Integer staffType;

    @ApiModelProperty("是否当前用户加盟商下营业员，true:是，false:否, 默认 true")
    private boolean curUserClient = true;

    @ApiModelProperty(hidden = true)
    private List<String> staff;

}

package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 实体传参
 *
 * @author xiaoyuan on 2020/10/14
 */
@Data
public class DifferenceResultQueryInVo implements Serializable {
    private static final long serialVersionUID = 28530001316728033L;

    private String client;


    @ApiModelProperty(value = "店名地点")
    private String brId;

    @ApiModelProperty(value = "仓库地点")
    private String brSite;

    @NotBlank(message = "起始时间不可为空!")
    @ApiModelProperty(value = "起始时间")
    private String startDate;

    @NotBlank(message = "结束时间不可为空!")
    @ApiModelProperty(value = "结束时间")
    private String endDate;

    private String minBr;

    @ApiModelProperty(value = "盘点单号")
    private String gspcVoucherId;

    private String brName;

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "商品编码集合")
    private String[] proArr;
}

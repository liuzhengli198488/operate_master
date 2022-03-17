package com.gys.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 质量管理体系审核报告
 * GAIA_INTERNAL_QUALITY_REVIEW
 * @author 
 */
@Data
public class GaiaInternalQualityReviewVo {

    @ApiModelProperty(value = "主键")
    private Long id;


    @ApiModelProperty(value = "加盟商")
    private String client;


    @ApiModelProperty(value = "gsp编号")
    private String voucherId;

    @ApiModelProperty(value = "评审内容")
    private String reviewContent;


    @ApiModelProperty(value = "评审质量")
    private String reviewQuality;

    @ApiModelProperty(value = "建议")
    private String suggest;


    @ApiModelProperty(value = "实施情况")
    private String effectContent;

    @ApiModelProperty(value = "评审范围")
    private String reviewScope;


    @ApiModelProperty(value = "评审目的")
    private String reviewTarget;


    @ApiModelProperty(value = "评审日期")
    private String reviewTime;


    @ApiModelProperty(value = "审核依据")
    private String reviewBase;


    @ApiModelProperty(value = "评审人员")
    private String reviewUser;


    @ApiModelProperty(value = "审核组织")
    private String reviewOrg;


    @ApiModelProperty(value = "受审部门")
    private String trialDep;


    @ApiModelProperty(value = "是否删除：0-未删除 1-删除")
    private Integer isDelete;


    @ApiModelProperty(value = "创建者")
    private String createUser;


    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    @ApiModelProperty(value = "更新者")
    private String updateUser;


    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
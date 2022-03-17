package com.gys.entity.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 *
 * 入参
 * @author 
 */
@Data
public class GaiaInternalQualityReviewInData {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "gsp编号")
    private String voucherId;

    @NotBlank(message = "评审内容不能为空")
    @ApiModelProperty(value = "评审内容")
    private String reviewContent;

    @NotBlank(message = "评审质量不能为空")
    @ApiModelProperty(value = "评审质量")
    private String reviewQuality;

    @NotBlank(message = "建议不能为空")
    @ApiModelProperty(value = "建议")
    private String suggest;

    @NotBlank(message = "实施情况不能为空")
    @ApiModelProperty(value = "实施情况")
    private String effectContent;

    @NotBlank(message = "评审范围不能为空")
    @ApiModelProperty(value = "评审范围")
    private String reviewScope;


    @NotBlank(message = "评审目的不能为空")
    @ApiModelProperty(value = "评审目的")
    private String reviewTarget;

    @ApiModelProperty(value = "评审日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    @NotBlank(message = "审核依据不能为空")
    @ApiModelProperty(value = "审核依据")
    private String reviewBase;

    @NotBlank(message = "评审人员不能为空")
    @ApiModelProperty(value = "评审人员")
    private String reviewUser;

    @NotBlank(message = "审核组织不能为空")
    @ApiModelProperty(value = "审核组织")
    private String reviewOrg;

    @NotBlank(message = "受审部门不能为空")
    @ApiModelProperty(value = "受审部门")
    private String trialDep;

    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startDate;


    @ApiModelProperty(value = "截至日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endDate;


    @ApiModelProperty(value = "当前页")
    private Integer pageNum;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

}
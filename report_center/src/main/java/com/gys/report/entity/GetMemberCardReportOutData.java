package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetMemberCardReportOutData implements Serializable {
    private static final long serialVersionUID = -3439869796791237870L;

    /**
     * 班次
     */
    @ApiModelProperty(value = "班次")
    private String bc;
    /**
     * 店号
     */
    @ApiModelProperty(value = "店号")
    private String brId;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private String index;

    /**
     * 收银工号
     */
    @ApiModelProperty(value = "收银工号")
    private String syNum;

    /**
     * 新增会员数量
     */
    @ApiModelProperty(value = "新增会员数量")
    private String addNum;

    /**
     * 店名
     */
    @ApiModelProperty(value = "店名")
    private String brName;

    /**
     * 销售日期
     */
    @ApiModelProperty(value = "销售日期")
    private String gsshDate;

    /**
     * 收银员名称
     */
    @ApiModelProperty(value = "收银员名称")
    private String userName;
}

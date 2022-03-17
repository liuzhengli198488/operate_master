package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 关联销售弹出率统计表
 * </p>
 *
 * @author flynn
 * @since 2021-08-18
 */
@Data
public class RelatedSaleEjectRes implements Serializable {


    private static final long serialVersionUID = -4137404086379368449L;

    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "门店编码")
    private String siteCode;

    @ApiModelProperty(value = "创建日期")
    private String createDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "输入商品编码次数")
    private Integer inputCount;

    @ApiModelProperty(value = "弹出关联商品次数")
    private Integer unitCount;

}

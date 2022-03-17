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
public class RelatedSaleEjectInData implements Serializable {


    private static final long serialVersionUID = -1979848075528395319L;


    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "门店编码")
    private String siteCode;

    @ApiModelProperty(value = "商品编码")
    private String proCode;

    @ApiModelProperty(value = "起始日期")
    private String queryStartDate;

    @ApiModelProperty(value = "结束日期")
    private String queryEndDate;

    @ApiModelProperty(value = "创建人")
    private String queryUserId;


}

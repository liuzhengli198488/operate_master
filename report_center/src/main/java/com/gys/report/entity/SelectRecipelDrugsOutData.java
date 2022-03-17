package com.gys.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyuan
 */
@Data
@ApiModel(value = "",description = "处方商品信息")
public class SelectRecipelDrugsOutData implements Serializable {
    private static final long serialVersionUID = 6193100968925977750L;
    @ApiModelProperty(value = "规格")
    private String specs;
    @ApiModelProperty(value = "厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "零售价")
    private String prePrice;
    @ApiModelProperty(value = "数量")
    private String num;
    @ApiModelProperty(value = "序号")
    private String index;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "商品编码")
    private String proCode;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "批号")
    private String gssdBatch;
    @ApiModelProperty(value = "")
    private String indexD;
    @ApiModelProperty(value = "行号")
    private String serial;
    @ApiModelProperty(value = "销售时间")
    private String gssrSaleDate;
    @ApiModelProperty(value = "销售单号")
    private String gssrSaleBillNo;


}

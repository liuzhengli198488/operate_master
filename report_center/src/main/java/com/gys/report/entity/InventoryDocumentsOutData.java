package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyuan on 2021/3/5
 */
@Data
public class InventoryDocumentsOutData implements Serializable {

    private static final long serialVersionUID = 4305981809726896332L;
    private String client;

    @ApiModelProperty(value = "序号")
    private Integer index;

    @ApiModelProperty(value = "店名编码")
    private String brId;

    @ApiModelProperty(value = "店名名称")
    private String brName;

    @ApiModelProperty(value = "盘点日期")
    private String gspcDate;

    @ApiModelProperty(value = "盘点单号")
    private String gspcVoucherId;

    @ApiModelProperty(value = "创建人")
    private String gspcEmp;

    @ApiModelProperty(value = "盘点品项数")
    private String numberOfItems;

    @ApiModelProperty(value = "差异品项数")
    private String differenceItem;

    @ApiModelProperty(value = "过账日期")
    private String postingDate;

}

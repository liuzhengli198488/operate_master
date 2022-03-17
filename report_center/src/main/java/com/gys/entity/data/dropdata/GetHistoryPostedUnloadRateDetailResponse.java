package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 获取已过账的下货率详情列表入参
 */
@Data
public class GetHistoryPostedUnloadRateDetailResponse {



    @ApiModelProperty(value = "请货开始日期,格式:yyyyMMdd")
    private String startPleaseOrderDate;

    @ApiModelProperty(value = "请货结束日期,格式:yyyyMMdd")
    private String endPleaseOrderDate;


    @ApiModelProperty(value = "开单开始日期,格式:yyyyMMdd")
    private String startDeliveryOrderDate;

    @ApiModelProperty(value = "开单结束日期,格式:yyyyMMdd")
    private String endDeliveryOrderDate;




    @ApiModelProperty(value = "过账开始日期,格式:yyyyMMdd")
    private String startPostingDate;

    @ApiModelProperty(value = "过账结束日期,格式:yyyyMMdd")
    private String endPostingDate;



    @ApiModelProperty(value = "客户开始编码")
    private String startCustomerNo;

    @ApiModelProperty(value = "客户结束编码")
    private String endCustomerNo;




    @ApiModelProperty(value = "商品大类")
    private String proClass;


    @ApiModelProperty(value = "商品条件")
    private String proCondition;


    @ApiModelProperty(value = "药德客户")
    private List<String> clientId;

    @ApiModelProperty(value = "配送中心")
    private List<String> dcId;

    @ApiModelProperty(value = "配送方式0正常补货 2铺货 3互调 4直配 null全部")
    private String replenishStyle;


    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码")
    private String pageNum;
    /**
     * 分页大小
     */
    @ApiModelProperty(value = "分页大小")
    private String pageSize;


}

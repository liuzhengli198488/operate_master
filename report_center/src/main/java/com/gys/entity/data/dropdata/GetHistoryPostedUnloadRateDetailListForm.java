package com.gys.entity.data.dropdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 获取已过账的下货率详情列表入参
 */
@Data
public class GetHistoryPostedUnloadRateDetailListForm {

    @ApiModelProperty(value = "请货日期,格式:yyyyMMdd")
    private List<SearchRangeDTO> pleaseOrderDate;

    @ApiModelProperty(value = "开单日期,格式:yyyyMMdd")
    private List<SearchRangeDTO> deliveryOrderDate;

    @ApiModelProperty(value = "过账日期,格式:yyyyMMdd")
    private List<SearchRangeDTO> postingDate;

    @ApiModelProperty(value = "客户编码")
    private List<SearchRangeDTO> customerNo;

    @ApiModelProperty(value = "商品大类")
    private List<SearchRangeDTO> commodityCategory;

    @ApiModelProperty(value = "商品条件")
    private List<SearchRangeDTO> commodityCondition;

}

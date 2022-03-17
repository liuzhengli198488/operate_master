package com.gys.entity.data.Category;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/5 14:03
 */
@Data
public class CategoryExecuteResponse {
    @ApiModelProperty(value = "推荐调整")
    /**推荐调整*/
    private BigDecimal recommendAdjust;
    @ApiModelProperty(value = "实际调整")
    /**实际调整*/
    private BigDecimal actualAdjust;
    @ApiModelProperty(value = "差异调整")
    /**差异调整*/
    private BigDecimal diffAdjust;
    @ApiModelProperty(value = "推荐淘汰")
    /**推荐淘汰*/
    private BigDecimal recommendOut;
    @ApiModelProperty(value = "实际淘汰")
    /**实际淘汰*/
    private BigDecimal actualOut;
    @ApiModelProperty(value = "差异淘汰")
    /**差异淘汰*/
    private BigDecimal diffOut;
    @ApiModelProperty(value = "推荐引进")
    /**推荐引进*/
    private BigDecimal recommendImport;
    @ApiModelProperty(value = "实际引进")
    /**实际引进*/
    private BigDecimal actualImport;
    @ApiModelProperty(value = "差异引进")
    /**差异引进*/
    private BigDecimal diffAdImport;
}

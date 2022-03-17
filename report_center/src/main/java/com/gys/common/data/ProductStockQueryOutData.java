package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductStockQueryOutData {
    @ApiModelProperty(value = "商品自编码")
    private String proSelfCode;
    @ApiModelProperty(value = "商品名称")
    private String proName;
    @ApiModelProperty(value = "规格")
    private String proSpecs;
    @ApiModelProperty(value = "单位")
    private String proUnit;
    @ApiModelProperty(value = "生产厂家")
    private String proFactoryName;
    @ApiModelProperty(value = "参考零售价")
    private String proLSJ;
    @ApiModelProperty(value = "总库存量")
    private String totalStockQty;
    @ApiModelProperty(value = "仓库库存量")
    private String wmsQty;
    @ApiModelProperty(value = "门店库存量")
    private String storeQty;
    @ApiModelProperty(value = "门店数")
    private String storeNumers;
    @ApiModelProperty(value = "30天动销量")
    private String movingQty;
    @ApiModelProperty(value = "末次购进价")
    private String lastSupplierPrice;
    @ApiModelProperty(value = "末次购进供应商")
    private String lastSupplierFactory;
    @ApiModelProperty(value = "门店编码")
    private String stoCode;
    @ApiModelProperty(value = "门店名称")
    private String stoName;
    @ApiModelProperty(value = "成本价")
    private String costAmt;

}

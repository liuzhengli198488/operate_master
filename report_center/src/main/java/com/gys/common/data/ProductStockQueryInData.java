package com.gys.common.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductStockQueryInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    @ApiModelProperty(value = "商品搜索字符串")
    private String proSearchInfo;
    @ApiModelProperty(value = "商品编码")
    private String proSelfCode;
    @ApiModelProperty(value = "门店搜索字符串")
    private String storeSearchInfo;
    @ApiModelProperty(value = "排序类型 1-门店编码、2-门店名称、3-库存数量、4-30天移动销量")
    private String sortType;
    @ApiModelProperty(value = "是否包含非直营管理门店 1-是，2-否")
    private String isContainDirectStore;

    private int pageNum;
    private int pageSize;
}

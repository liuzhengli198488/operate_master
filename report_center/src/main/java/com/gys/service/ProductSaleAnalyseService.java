package com.gys.service;

import com.gys.common.response.Result;
import com.gys.entity.data.productSaleAnalyse.ProductSaleAnalyseInData;
import com.gys.entity.data.productSaleAnalyse.ProductSaleAnalyseOutData;

import java.util.List;
import java.util.Map;

public interface ProductSaleAnalyseService {
    List<ProductSaleAnalyseOutData> productAnalyseList(ProductSaleAnalyseInData inData);

    Map<String,Object> selectStoreInfoBySale(ProductSaleAnalyseInData inData);

    Result exportProductSaleAnalyse(ProductSaleAnalyseInData inData);

}

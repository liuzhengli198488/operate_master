package com.gys.service;

import com.gys.common.data.JsonResult;
import com.gys.common.data.ProductStockQueryInData;

public interface ProductStockQueryService {

    JsonResult searchProductInfo(ProductStockQueryInData inData);

    JsonResult queryProductInfo(ProductStockQueryInData inData);

    JsonResult queryStoreInfo(ProductStockQueryInData inData);

}

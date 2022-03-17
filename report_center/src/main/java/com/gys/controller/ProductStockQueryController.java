package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.ProductStockQueryInData;
import com.gys.service.ProductStockQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/app/productStockQuery/")
public class ProductStockQueryController extends BaseController {

    @Autowired
    private ProductStockQueryService ProductStockQueryService;

    @ApiOperation(value = "搜索商品信息")
    @PostMapping({"searchProductInfo"})
    public JsonResult searchProductInfo(HttpServletRequest request, @RequestBody ProductStockQueryInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return this.ProductStockQueryService.searchProductInfo(inData);
    }

    @ApiOperation(value = "查询商品信息")
    @PostMapping({"queryProductInfo"})
    public JsonResult queryProductInfo(HttpServletRequest request, @RequestBody ProductStockQueryInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return this.ProductStockQueryService.queryProductInfo(inData);
    }

    @ApiOperation(value = "查询门店明细")
    @PostMapping({"queryStoreInfo"})
    public JsonResult queryStoreInfo(HttpServletRequest request, @RequestBody ProductStockQueryInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");

        return this.ProductStockQueryService.queryStoreInfo(inData);
    }
}

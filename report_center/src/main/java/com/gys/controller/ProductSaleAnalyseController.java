package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.JsonResult;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.data.productSaleAnalyse.ProductSaleAnalyseInData;
import com.gys.entity.data.productSaleAnalyse.ProductSaleAnalyseOutData;
import com.gys.entity.data.productSaleAnalyse.StoreInfoOutData;
import com.gys.service.ProductSaleAnalyseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "商品品类模型")
@RequestMapping({"/product/analyse"})
@Slf4j
public class ProductSaleAnalyseController extends BaseController {
    @Autowired
    private ProductSaleAnalyseService analyseService;

    @ApiOperation(value = "商品品类模型查询",response = ProductSaleAnalyseOutData.class)
    @PostMapping({"productAnalyseList"})
    public JsonResult medicalInsuranceList(@RequestBody ProductSaleAnalyseInData inData) {
//        inData.setClientId("10000005");
        return JsonResult.success(this.analyseService.productAnalyseList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "过滤条件查询",response = StoreInfoOutData.class)
    @PostMapping({"analyseSelectList"})
    public JsonResult analyseSelectList(@RequestBody ProductSaleAnalyseInData inData) {
//        inData.setClientId("10000005");
        return JsonResult.success(this.analyseService.selectStoreInfoBySale(inData), "提示：获取数据成功！");
    }

    @Log("商品品类模型导出")
    @ApiOperation(value = "商品品类模型导出")
    @PostMapping({"/exportProductSaleAnalyse"})
    public Result exportProductSaleAnalyse(@RequestBody ProductSaleAnalyseInData inData){
        return analyseService.exportProductSaleAnalyse(inData);
    }
}

package com.gys.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.*;
import com.gys.service.MaterialDocumentService;
import com.gys.service.SaleReportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/materialDocument")
public class MaterialDocumentController extends BaseController {

    @Autowired
    private MaterialDocumentService materialDocumentService;

    @ApiOperation(value = "物料凭证监控", response = NoSaleOutData.class)
    @PostMapping({"noLogList"})
    public JsonResult noLogList(HttpServletRequest request, @RequestBody MaterialDocumentInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        return JsonResult.success(this.materialDocumentService.noLogList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "查询所有加盟商", response = NoSaleOutData.class)
    @PostMapping({"listClient"})
    public JsonResult listClient(HttpServletRequest request, @RequestBody MaterialDocumentInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        return JsonResult.success(this.materialDocumentService.listClient(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "根据加盟商编号查询所有门店和仓库", response = NoSaleOutData.class)
    @PostMapping({"listClientDCAndStorage"})
    public JsonResult listClientDCAndStorage(HttpServletRequest request, @RequestBody MaterialDocumentInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        return JsonResult.success(this.materialDocumentService.listClientDCAndStorage(inData), "提示：获取数据成功！");
    }


}

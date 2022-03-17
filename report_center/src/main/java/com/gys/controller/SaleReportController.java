package com.gys.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.*;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.data.payDayReport.PayDayReportDetailInData;
import com.gys.entity.data.payDayReport.PayDayReportInData;
import com.gys.service.SaleReportService;
import com.gys.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/salereport/")
public class SaleReportController extends BaseController {

    @Autowired
    private SaleReportService saleReportService;

    @ApiOperation(value = "不动销商品查询",response = NoSaleOutData.class)
    @PostMapping({"noSaleReportList"})
    public JsonResult noSaleReportList(HttpServletRequest request, @RequestBody SaleReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.saleReportService.selectNosaleReportList(inData), "提示：获取数据成功！");
    }
    /**
     * @Author huxinxin
     * @Description // 不动销商品查询 新 包括门店和仓库
     * @Date 14:38 2021/4/25
     * @Param [request, inData]
     * @return com.gys.common.data.JsonResult
     **/
    @Log("不动销商品查询(新)")
    @ApiOperation(value = "不动销商品查询(新)",response = NoSaleOutData.class)
    @PostMapping({"noSaleReportListNew"})
    public JsonResult noSaleReportListNew(HttpServletRequest request, @RequestBody SaleReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.saleReportService.selectNosaleReportListNew(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "医保商品信息查询",response = MedicalInsuranceOutData.class)
    @PostMapping({"medicalInsuranceList"})
    public JsonResult medicalInsuranceList(HttpServletRequest request, @RequestBody MedicalInsuranceInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(this.saleReportService.selectMedicalInsuranceList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "医保进销存导出")
    @PostMapping({"exportMedicalInsurance"})
    public Result exportMedicalInsurance(HttpServletRequest request, @RequestBody MedicalInsuranceInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        String fileName = "";
        //给文件命名。随机命名
        if(inData.getType() == 2){
            fileName = "医保商品进销存-" + CommonUtil.getyyyyMMdd() + ".xlsx";
        }else {
            fileName = "医保商品进销存-" + CommonUtil.getyyyyMMdd() + ".xls";
        }
        //告诉浏览器数据格式，将头和数据传到前台
        return this.saleReportService.exportMedicalInsurance(inData,fileName);
    }

    @PostMapping({"selectPayDayReport"})
    public JsonResult selectPayDayReport(HttpServletRequest request, @RequestBody PayDayReportInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        return JsonResult.success( this.saleReportService.selectPayDayReport(inData), "提示：获取数据成功！");
    }

    @PostMapping({"selectPayDayReportDetail"})
    public JsonResult selectPayDayReportDetail(HttpServletRequest request, @RequestBody PayDayReportDetailInData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        return JsonResult.success( this.saleReportService.selectPayDayReportDetail(inData), "提示：获取数据成功！");
    }
}

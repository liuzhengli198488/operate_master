package com.gys.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.data.GaiaSalesStoSummary;
import com.gys.entity.data.salesSummary.*;
import com.gys.service.SalesSummaryService;
import com.gys.util.CosUtils;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Description 销售管理查询
 * @Author huxinxin
 * @Date 2021/4/29 13:35
 * @Version 1.0.0
 **/
@RestController
@Api(tags = "销售汇总查询")
@RequestMapping({"/salesSummary"})
@Slf4j
public class SalesSummaryController extends BaseController {
    @Autowired
    private SalesSummaryService salesSummaryService;
    @Resource
    public CosUtils cosUtils;
    /**
     * 查询列表
     * @param request
     * @param summaryData
     * @return
     */
    @ApiOperation(value = "列表查询/条件查询")
    @PostMapping("/selectSalesByProduct")
    public JsonResult selectSalesSummary(HttpServletRequest request, @RequestBody SalesSummaryData summaryData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setClient(userInfo.getClient());
        summaryData.setStoreCode(userInfo.getDepId());
        return JsonResult.success(salesSummaryService.selectSalesByProduct(summaryData),"返回成功");
    }

    /**
     * 查询列表
     * @param request
     * @param inData
     * @return
     */
    @Log("人员销售汇总查询")
    @ApiOperation(value = "人员销售汇总查询")
    @PostMapping("/selectPersonSales")
    public JsonResult selectPersonSales(HttpServletRequest request, @RequestBody PersonSalesInData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(salesSummaryService.selectPersonSales(inData),"返回成功");
    }

    @Log("员工销售汇总查询导出")
    @ApiOperation(value = "员工销售汇总查询导出")
    @PostMapping({"/selectPersonSalesCSV"})
    public Result selectPersonSalesCSV(HttpServletRequest request, @RequestBody PersonSalesInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        List<PersonSalesOutData> outData = salesSummaryService.selectPersonSalesByCSV(inData);
        String fileName = "员工销售汇总导出";

        if (outData.size() > 0) {
            CsvFileInfo csvInfo =null;
            // 导出
            // byte数据
            if(StringUtils.isNotEmpty(inData.getNotSto())){
                if("Y".equals(inData.getNotSto())){
                     csvInfo = CsvClient.getCsvByte(outData,fileName,null);
                }else {
                     csvInfo = CsvClient.getCsvByte(outData,fileName, Collections.singletonList((short) 1));
                }
            }else {
                return Result.error("导出失败");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result result = null;
            try {
                bos.write(csvInfo.getFileContent());
                result = cosUtils.uploadFile(bos, csvInfo.getFileName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bos.flush();
                bos.close();
            }
            return result;

        } else {
            throw new BusinessException("提示：没有查询到数据!");
        }
    }

    /**
     * 查询列表
     * @param request
     * @param inData
     * @return
     */
    @Log("员工销售明细查询")
    @ApiOperation(value = "员工销售明细查询")
    @PostMapping("/selectPersonSalesDetail")
    public JsonResult selectPersonSalesDetail(HttpServletRequest request, @RequestBody PersonSalesDetaiInlData inData){
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(salesSummaryService.selectPersonSalesDetail(inData),"返回成功");
    }
    @Log("员工销售明细导出")
    @ApiOperation(value = "员工销售明细导出")
    @PostMapping({"/selectPersonSalesDetailCSV"})
    public Result selectPersonSalesDetailCSV(HttpServletRequest request, @RequestBody PersonSalesDetaiInlData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        List<PersonSalesDetailOutData> outData = salesSummaryService.selectPersonSalesDetailByCSV(inData);
        String fileName = "员工销售明细导出";

        if (outData.size() > 0) {
            CsvFileInfo csvInfo =null;
            // 导出
            // byte数据
            csvInfo = CsvClient.getCsvByte(outData,fileName, Collections.singletonList((short) 1));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result result = null;
            try {
                bos.write(csvInfo.getFileContent());
                result = cosUtils.uploadFile(bos, csvInfo.getFileName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bos.flush();
                bos.close();
            }
            return result;

        } else {
            throw new BusinessException("提示：没有查询到数据,请修改查询条件!");
        }
    }

    @ApiOperation(value = "门店销售汇总查询",response = GaiaSalesStoSummary.class)
    @PostMapping("/selectSalesSummaryStore")
    public JsonResult selectSalesSummaryByBrId(HttpServletRequest request, @Valid @RequestBody SalesSummaryDataReport summaryData){
      GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setClient(userInfo.getClient());
        summaryData.setUserRestrictInfo(userInfo.getUserRestrictInfo());
        //summaryData.setClient("10000005");
        if("2".equals(userInfo.getUserRestrictInfo().getRestrictType()) && CollectionUtil.isEmpty(userInfo.getUserRestrictInfo().getRestrictStoCodes())){
            throw new BusinessException("此用户在区域下没有门店查看数据权限！");
        }
        return JsonResult.success(salesSummaryService.findSalesSummaryByBrId(summaryData),"返回成功");
    }

    @ApiOperation(value = "日期销售汇总查询")
    @PostMapping("/selectSalesSummaryByDate")
    public JsonResult selectSalesSummaryByDate(HttpServletRequest request, @Valid @RequestBody StoreSaleDateInData summaryData){
       GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setClient(userInfo.getClient());
        summaryData.setUserRestrictInfo(userInfo.getUserRestrictInfo());
        if("2".equals(userInfo.getUserRestrictInfo().getRestrictType()) && CollectionUtil.isEmpty(userInfo.getUserRestrictInfo().getRestrictStoCodes())){
            throw new BusinessException("此用户在区域下没有门店查看数据权限！");
        }
       // summaryData.setClient("10000005");
        return JsonResult.success(salesSummaryService.findSalesSummaryByDate(summaryData),"返回成功");
    }

    @ApiOperation(value = "WEB端报表(用户)")
    @PostMapping("/selectWebSalesSummaryByDate")
    public JsonResult selectWebSalesSummaryByDate(HttpServletRequest request, @Valid @RequestBody WebStoreSaleDateInData data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        data.setUserId(userInfo.getUserId());
        return JsonResult.success(salesSummaryService.selectWebSalesSummaryByDate(data),"数据获取成功");
    }

    @ApiOperation(value = "WEB端报表导出")
    @PostMapping("/exportSalesSummary")
    public Result exportSalesSummary(HttpServletRequest request, @Valid @RequestBody WebStoreSaleDateInData data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        data.setUserId(userInfo.getUserId());
        return salesSummaryService.exportSalesSummary(data);
    }
}

package com.gys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.data.dropdata.*;
import com.gys.entity.data.suggestion.vo.AdjustmentSummaryVo;
import com.gys.entity.data.xhl.dto.*;
import com.gys.entity.data.xhl.vo.*;
import com.gys.service.DropRateService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.CosUtils;
import com.gys.util.ExcelStyleUtil;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.dto.CsvFileInfo;
import com.gys.util.easyExcel.EasyExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 下货率web端控制层
 */
@Slf4j
@RestController
@RequestMapping("/dropRate")
public class DropRateController extends BaseController {

    @Resource
    private DropRateService dropRateService;

    @Resource
    public CosUtils cosUtils;


    @GetMapping("/init")
    public JsonResult init(HttpServletRequest request) {
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.init(), "查询成功");
    }

    /**
     * 报表
     */
    @PostMapping("/report")
    public JsonResult report(HttpServletRequest request, @RequestBody DropRateInData inData) {
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.reportData(loginUser, inData), "查询成功");
    }

    @PostMapping("/test")
   public  JsonResult getIndustryAverage(HttpServletRequest request,String clientId){
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.getIndustryAverage(loginUser.getClient()), "查询成功");
   }

    /**
     * 报表
     */
    @PostMapping("/export")
    public Result export(HttpServletRequest request, @RequestBody DropRateInData inData) {
        GetLoginOutData loginUser = getLoginUser(request);
        List<DropRateRes> outData = dropRateService.reportData(loginUser, inData).getList();
        DropRateRes total = (DropRateRes) dropRateService.reportData(loginUser, inData).getListNum();
        if(CollectionUtil.isNotEmpty(outData)){
            outData.forEach(x->{
                x.setQuantityRateStr(x.getQuantityRateStr());
                x.setAmountRateStr(x.getAmountRateStr());
                x.setProductRateStr(x.getProductRateStr());
                x.setAverageRateStr(x.getAverageRateStr());
                x.setDiffStr(x.getDiffStr());
                x.setIndustryAverageStr(x.getIndustryAverage());
            });
            total.setQuantityRateStr(total.getQuantityRateStr());
            total.setAmountRateStr(total.getAmountRateStr());
            total.setProductRateStr(total.getProductRateStr());
            total.setAverageRateStr(total.getAverageRateStr());
            total.setDiffStr(total.getDiffStr());
            total.setIndustryAverageStr(total.getIndustryAverage());
            outData.add(total);
        }
        changeCsvCellTitleInfo(outData,inData.getReportType());
        if (outData.size() > 0) {
            String fixStr = "";
            if("1".equals(inData.getReportType())){
                fixStr = "日报";
            }else if("2".equals(inData.getReportType())){
                fixStr = "周报";
            }else if("3".equals(inData.getReportType())){
                fixStr = "月报";
            }
            String fileName = "下货率报表-" + fixStr;
            CsvFileInfo csvInfo = null;
            // 导出
            // byte数据
            csvInfo = CsvClient.getCsvByte(outData, fileName, Collections.singletonList((short) 1));
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
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return result;

        } else {
            throw new BusinessException("提示：没有查询到数据!");
        }
    }



    private void changeCsvCellTitleInfo(List<DropRateRes> dataList,String reportType){
        String titleFinalValue = "";
        if("1".equals(reportType)){
            titleFinalValue = "日期";
        }else if("2".equals(reportType)){
            titleFinalValue = "周次";
        }else if("3".equals(reportType)){
            titleFinalValue = "年月";
        }
        try {
            Field field = DropRateRes.class.getDeclaredField("showTime");
            CsvCell csv = field.getAnnotation(CsvCell.class);
            InvocationHandler h = Proxy.getInvocationHandler(csv);
            Field hField = h.getClass().getDeclaredField("memberValues");
            hField.setAccessible(true);
            try {
                Map memberValues = (Map) hField.get(h);
                memberValues.put("title",titleFinalValue);
                String value = csv.title();
                System.out.println(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
    /**
     * 获取已过账的下货率详情列表
     */
    @PostMapping("/getHistoryPostedUnloadRateDetailList")
    @ApiOperation(value = "获取已过账的下货率详情列表")
    public JsonResult  getHistoryPostedUnloadRateDetailList(HttpServletRequest request,@Valid @RequestBody GetHistoryPostedUnloadRateDetailResponse getHistoryPostedUnloadRateDetailListForm) {
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.getHistoryPostedUnloadRateDetailList(loginUser,getHistoryPostedUnloadRateDetailListForm), "查询成功");
    }
    @GetMapping("/getClientList")
    @ApiOperation(value = "获取所有客户(加盟商)")
    public JsonResult getClientList(HttpServletRequest request,String clientName){
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.getClientList(loginUser,clientName), "查询成功");
    }

    @GetMapping("/getReplenishStyle")
    @ApiOperation(value = "获取捕获方式")
    public JsonResult getReplenish(HttpServletRequest request){
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.getReplenishStyle(loginUser), "查询成功");
    }

    /**
     * 下货率 -- 商品大类下拉
     */
    @PostMapping("/getCommodityCategoryList")
    public JsonResult getCommodityCategoryList(HttpServletRequest request) {
        GetLoginOutData loginUser = getLoginUser(request);
        return JsonResult.success(dropRateService.getCommodityCategoryList(loginUser), "查询成功");
    }

    /**
     * 下货率 -- 统计已过账的下货率
     */
    @PostMapping("/generateHistoryPostedUnloadRate/job")
    public JsonResult generateHistoryPostedUnloadRate(HttpServletRequest request,@Valid @RequestBody GenerateHistoryPostedUnloadRateForm generateHistoryPostedUnloadRateForm) {
         GetLoginOutData loginUser = getLoginUser(request);
        return   JsonResult.success(dropRateService.generateHistoryPostedUnloadRate(loginUser,generateHistoryPostedUnloadRateForm), "查询成功");
    }

    @ApiOperation(value = "下货率明细导出CSV")
    @PostMapping({"historyPostedExport"})
    public Result exportCSV(HttpServletRequest request, @Valid @RequestBody GetHistoryPostedUnloadRateDetailResponse getHistoryPostedUnloadRateDetailListForm) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        getHistoryPostedUnloadRateDetailListForm.setPageNum(null);
        getHistoryPostedUnloadRateDetailListForm.setPageSize(null);
        PageInfo detailList = dropRateService.getHistoryPostedUnloadRateDetailList(userInfo, getHistoryPostedUnloadRateDetailListForm);
        String fileName = "下货率详情导出";
        if (Objects.isNull(detailList) || CollUtil.isEmpty(detailList.getList())) {
            throw new BusinessException("提示：导出数据为空!");
        }
        //获取集合中最后一条数据


        List<GetHistoryPostedUnloadRateDetailListVO> outData = (List<GetHistoryPostedUnloadRateDetailListVO>) detailList.getList();
        GetHistoryPostedUnloadRateDetailListVO detailListVO = (GetHistoryPostedUnloadRateDetailListVO) detailList.getListNum();
        detailListVO.setOrderQuantity(null);
        detailListVO.setDeliveryQuantity(null);
        detailListVO.setDeliveryAmountSatisfiedRate(null);
        detailListVO.setDeliveryPostedQuantityRate(null);
        detailListVO.setShipmentQuantitySatisfiedRate(null);
        detailListVO.setShipmentAmountSatisfiedRate(null);
        detailListVO.setFinalQuantitySatisfiedRate(null);
        detailListVO.setFinalAmountSatisfiedRate(null);


        detailListVO.setNumber("合计");
        detailListVO.setDeliveryQuantitySatisfiedRate(detailListVO.getShipmentQuantitySatisfiedRateTotal());
        detailListVO.setDeliveryItemSatisfiedRate(detailListVO.getDeliveryItemSatisfiedRateTotal());
        detailListVO.setShipmentItemSatisfiedRate(detailListVO.getShipmentItemSatisfiedRateTotal());
        detailListVO.setFinalItemSatisfiedRate(detailListVO.getFinalItemSatisfiedRateTotal());
        outData.add(detailListVO);
        if (outData.size() > 0) {
            CsvFileInfo csvInfo = null;
            // 导出
            // byte数据
            csvInfo = CsvClient.getCsvByte(outData, fileName, Collections.singletonList((short) 1));

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
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;

        } else {
            throw new BusinessException("提示：导出数据为空!");
        }
    }
    /**
     * 商品定位查询
     */
    @PostMapping("/getProductPositioningList")
    @ApiOperation(value = "商品定位查询")
    public JsonResult getProductPositioningList(HttpServletRequest request,@RequestBody ViewGetProductPositioningListForm viewGetProductPositioningListForm) {
        GetLoginOutData userInfo = this.getLoginUser(request);

        return JsonResult.success(dropRateService.getProductPositioningList(viewGetProductPositioningListForm), "查询成功");
    }

    /**
     *  下货率明细
     */
    @PostMapping("/reportInfo")
    public JsonResult getreportInfo(HttpServletRequest request,@RequestBody ReportInfoDto dto) {
         if(dto.getPageNum()==null||dto.getPageNum()==0){
             dto.setPageNum(1);
         }
        if(dto.getPageSize()==null||dto.getPageSize()==0){
            dto.setPageSize(100);
        }
        PageInfo listDropList = dropRateService.getListDropList(dto);
        if(listDropList.getList().size()>0){
            ReportInfoVo reportInfoVo=  dropRateService.getListTotal( (List<ReportInfoVo>)listDropList.getList());
            listDropList.setListNum(reportInfoVo);
        }
        return JsonResult.success(listDropList, "查询成功");
    }
    /**
     *  下货率明细导出
     */
    @PostMapping("/exportReportInfo")
    public void export(HttpServletRequest request,HttpServletResponse response,@RequestBody ReportInfoDto dto) {
        if(dto.getPageNum()==null||dto.getPageNum()==0){
            dto.setPageNum(1);
        }
        if(dto.getPageSize()==null||dto.getPageSize()==0){
            dto.setPageSize(100);
        }
        PageInfo list = dropRateService.getListDropList(dto);
        ReportInfoExportVo  infoExportVo  =  dropRateService.getTotalReportInfo(list.getList());
        List<ReportInfoVo> infoVos  = list.getList();
        List<ReportInfoExportVo> dataList=new ArrayList<>();
        if(infoVos.size()>0){
            dataList = infoVos.stream().map(summaryVo -> {
                ReportInfoExportVo exportOutData = new ReportInfoExportVo();
                BeanCopyUtils.copyPropertiesIgnoreNull(summaryVo,exportOutData);
                return exportOutData;
            }).collect(Collectors.toList());
            dataList.add(infoExportVo);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "报表导出";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try {
            EasyExcel.write(response.getOutputStream(),ReportInfoExportVo.class)
                    .registerWriteHandler(ExcelStyleUtil.getStyle())
                    .sheet(0).doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     *  下货率汇总
     */
    @PostMapping("/reportSummary")
    public JsonResult getReportSummary( @Valid @RequestBody ReportSummaryDto dto) {
        PageInfo vos = dropRateService.getReportSummary(dto);
        return JsonResult.success(vos, "查询成功");
    }

    /**
     * 报表
     */
    @PostMapping("/exportReportSummary")
    public void export(HttpServletResponse response, @RequestBody ReportSummaryDto dto) {
        List<ReportInfoSummaryVo> outData = dropRateService.getReportSummary( dto).getList();
        ReportInfoSummaryVo total = (ReportInfoSummaryVo) dropRateService.getReportSummary(dto).getListNum();
        outData.add(total);
        List<ReportInfoSummaryExportVo> dataList = outData.stream().map(summaryVo -> {
            ReportInfoSummaryExportVo exportOutData = new ReportInfoSummaryExportVo();
            BeanCopyUtils.copyPropertiesIgnoreNull(summaryVo,exportOutData);

            exportOutData.setDistributionNumRate(summaryVo.getDistributionNumRate().toPlainString()+"%");
            exportOutData.setSendNumRate(summaryVo.getSendNumRate().toPlainString()+"%");
            exportOutData.setFinalNumRate(summaryVo.getFinalNumRate().toPlainString()+"%");

            exportOutData.setDistributionAmountRate(summaryVo.getDistributionAmountRate().toPlainString()+"%");
            exportOutData.setSendAmountRate(summaryVo.getSendAmountRate().toPlainString()+"%");
            exportOutData.setFinalAmountRate(summaryVo.getFinalAmountRate().toPlainString()+"%");

            exportOutData.setDistributionProductRate(summaryVo.getDistributionProductRate().toPlainString()+"%");
            exportOutData.setSendProductRate(summaryVo.getSendProductRate().toPlainString()+"%");
            exportOutData.setFinalProductRate(summaryVo.getFinalProductRate().toPlainString()+"%");
            return exportOutData;
        }).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(dataList)) {
            throw new BusinessException("未查询到数据!");
        }
        String fileName = "报表导出";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            EasyExcelUtil.writeEasyExcel(response, ReportInfoSummaryExportVo.class,dataList,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //加盟商和门店模糊查询
    @PostMapping("/clientsAndStores")
    public JsonResult getClientsOrStores(HttpServletRequest request,@RequestBody ClientStoreDto dto) {
         ClientStoreVo vo =dropRateService.getClientsOrStores(dto);
        return JsonResult.success(vo, "查询成功");
    }

    //加盟商和城市快捷维护
    @PostMapping("/clientBaseInfo")
    public JsonResult getClientBaseInfo(@RequestBody QueryDto dto) {
        if(dto.getPageNum()==null||dto.getPageNum()==0){
            dto.setPageNum(1);
        }
        if(dto.getPageSize()==null||dto.getPageSize()>100){
            dto.setPageSize(20);
        }
     com.github.pagehelper.PageInfo<ClientBaseInfoVo> voList=dropRateService.getClientBaseInfo(dto);
        return JsonResult.success(voList, "查询成功");
    }

    //加盟商和城市快捷维护
    @PostMapping("/updateClientBaseInfo")
    public JsonResult getClientBaseInfo(@RequestBody ClientBaseInfoDto dto) {
         if(!CollectionUtils.isEmpty(dto.getVoList())){
             dropRateService.updateInfo(dto);
         }
        return JsonResult.success(null, "更新成功");
    }

    @PostMapping("/stores")
    public JsonResult getStoresByClients(@RequestBody ClientDto dto) {
        List<StoreData> list=new ArrayList<>();
        if(!CollectionUtils.isEmpty(dto.getClients())){
            List<String> clients = dto.getClients();
            list  = dropRateService.getStores(clients);
        }
        return JsonResult.success(list, "查询成功");
    }

    //加盟商和城市快捷维护
    @PostMapping("/clientsInfo")
    public JsonResult getClientsInfo(@RequestBody QueryDto dto) {
      List<ClientBaseInfoVo> voList =  dropRateService.getClientsInfo(dto);
        return JsonResult.success(voList, "查询成功");
    }

}

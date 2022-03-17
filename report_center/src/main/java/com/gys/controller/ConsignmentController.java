package com.gys.controller;
import cn.hutool.core.util.ObjectUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.data.consignment.dto.RecommendedDocumentsDto;
import com.gys.entity.data.consignment.dto.StoreDto;
import com.gys.entity.data.consignment.dto.StoreRecommendedSaleDto;
import com.gys.entity.data.consignment.vo.StoreReport;
import com.gys.entity.data.consignment.vo.StoreReportVo;
import com.gys.entity.data.consignment.vo.StoreVoList;
import com.gys.report.entity.StoreSaleDayInData;
import com.gys.service.ConsignmentService;
import com.gys.util.CommonUtil;
import com.gys.util.ExportStatusUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/6 17:53
 * @Description: ConsignmentController
 * @Version 1.0.0
 */
//代售
@RestController
@RequestMapping("/consignment")
public class ConsignmentController extends BaseController {
     @Resource
     private ConsignmentService consignmentService;

    //门店报表
    @PostMapping("storeReport")
    public JsonResult getStoreReport(HttpServletRequest request, @RequestBody StoreRecommendedSaleDto dto){
       GetLoginOutData userInfo = this.getLoginUser(request);
       dto.setClientId(userInfo.getClient());
        //  dto.setClientId("10000005");
        if (StringUtils.isNotBlank(dto.getBillNo())) {
            List<String> strings = Arrays.asList(dto.getBillNo().split(","));
            dto.setBillNoArr(strings);
        }
        if (StringUtils.isNotEmpty(dto.getQueryProId())) {
            List<String> strings = Arrays.asList(dto.getQueryProId().split("\\s+ |\\s+|,"));
            dto.setProArr(strings);
        }
        if (ObjectUtil.isNotEmpty(dto.getClassArr())) {
            dto.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(dto.getClassArr()));
        }
        // 门店id处理 当选择门店 是登陆门店
        String depId = userInfo.getDepId();
        List<String> brIds = dto.getBrIds();
        if(CollectionUtils.isNotEmpty(brIds)){
            // 是否包含当前登陆门店
            if(!brIds.contains(depId)){
                List<String> stringList =new ArrayList<>();
                stringList.add(depId);
                dto.setSaleIds(stringList);
            }
        }

        PageInfo pageInfo=new PageInfo();
        //推荐未完成的
        if(dto.getTag()==0){
            pageInfo= consignmentService.getStoreUnRecommendReport(dto);
        }else {
            pageInfo= consignmentService.getStoreRecommendReport(dto);
        }
        return JsonResult.success(pageInfo,"查询成功");
    }

    //门店报表
    @PostMapping("exportStoreReport")
    public JsonResult exportStoreReport(HttpServletRequest request, HttpServletResponse response,@RequestBody StoreRecommendedSaleDto dto){
        GetLoginOutData userInfo = this.getLoginUser(request);
        dto.setClientId(userInfo.getClient());
       // dto.setClientId("10000005");
       // ExportStatusUtil.checkExportAuthority(dto.getClientId(), dto.getBrId());
        if (StringUtils.isNotBlank(dto.getBillNo())) {
            List<String> strings = Arrays.asList(dto.getBillNo().split(","));
            dto.setBillNoArr(strings);
        }
        if (StringUtils.isNotEmpty(dto.getQueryProId())) {
            List<String> strings = Arrays.asList(dto.getQueryProId().split("\\s+ |\\s+|,"));
            dto.setProArr(strings);
        }
        if (ObjectUtil.isNotEmpty(dto.getClassArr())) {
            dto.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(dto.getClassArr()));
        }
        String depId = userInfo.getDepId();
        List<String> brIds = dto.getBrIds();
        if(CollectionUtils.isNotEmpty(brIds)){
            // 是否包含当前登陆门店
            if(!brIds.contains(depId)){
                List<String> stringList =new ArrayList<>();
                stringList.add(depId);
                dto.setSaleIds(stringList);
            }
        }
       // List<StoreReportVo> reportVoList=new ArrayList<>();
        consignmentService.exportStoreReport(dto,response);
        return JsonResult.success(null,"导出成功");
    }

    //推荐单据
    @PostMapping({"recommendedDocuments"})
    public JsonResult getRecommendedDocuments(HttpServletRequest request,@RequestBody RecommendedDocumentsDto data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
       // data.setClient("10000005");
        return JsonResult.success(consignmentService.getRecommendedDocuments(data), "提示：获取数据成功！");
    }
    //门店报表 推荐门店
    @PostMapping("stores")
    public JsonResult getAllStores(HttpServletRequest request, @RequestBody StoreDto dto){
        GetLoginOutData userInfo = this.getLoginUser(request);
        dto.setClient(userInfo.getClient());
        StoreVoList storeVoList= consignmentService.getAllStores(dto);
        return JsonResult.success(storeVoList,"查询成功");
    }
}

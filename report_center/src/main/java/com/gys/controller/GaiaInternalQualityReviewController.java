package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.common.vo.GaiaInternalQualityReviewVo;
import com.gys.entity.GaiaInternalQualityReview;
import com.gys.entity.data.GaiaInternalQualityReviewInData;
import com.gys.service.GaiaInternalQualityReviewServcie;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * @author csm
 * @date 12/31/2021 - 5:30 PM
 */
@Slf4j
@Api(value = "质量体系",tags = "质量体系评审记录")
@RestController
@RequestMapping("/internalQualityReview/")
public class GaiaInternalQualityReviewController extends BaseController {
    @Resource
    private GaiaInternalQualityReviewServcie gaiaInternalQualityReviewServcie;


    @ApiOperation(value = "查询", response = GaiaInternalQualityReview.class)
    @PostMapping("getItems")
    public JsonResult select(HttpServletRequest request, @RequestBody GaiaInternalQualityReviewInData inData){
        log.info("**************************质量体系评审记录:进入查询");
        GetLoginOutData loginUser = this.getLoginUser(request);
        inData.setClient(loginUser.getClient());
        PageInfo<GaiaInternalQualityReviewVo> item = gaiaInternalQualityReviewServcie.getItem(inData);
        return JsonResult.success(item,"");
    }
    @ApiOperation("新增")
    @PostMapping("addItem")
    public JsonResult add(HttpServletRequest request,@Valid @RequestBody GaiaInternalQualityReviewInData inData){
        log.info("**************************质量体系评审记录:进入新增");
        GetLoginOutData loginUser = this.getLoginUser(request);
        gaiaInternalQualityReviewServcie.addItem(loginUser,inData);
        return JsonResult.success("","新增成功!");
    }
    @ApiOperation("修改")
    @PostMapping("editItems")
    public JsonResult editItems(HttpServletRequest request, @RequestBody List<GaiaInternalQualityReviewInData> inDatas){
        log.info("**************************进入修改");
        GetLoginOutData loginUser = this.getLoginUser(request);
        gaiaInternalQualityReviewServcie.updateItem(loginUser,inDatas);
        return JsonResult.success("","修改成功!");
    }
    @ApiOperation("删除")
    @PostMapping("deleteItems")
    public JsonResult delItems(HttpServletRequest request, @RequestBody List<GaiaInternalQualityReview> inDatas){
        log.info("**************************质量体系评审记录:进入删除");
        GetLoginOutData loginUser = this.getLoginUser(request);
        gaiaInternalQualityReviewServcie.delItem(loginUser,inDatas);
        return JsonResult.success("","删除成功!");
    }
    @ApiOperation("导出")
    @PostMapping("exportExcel")
    public Result exportExcel(HttpServletRequest request,@RequestBody GaiaInternalQualityReviewInData inData){
        log.info("**************************质量体系评审记录:进入导出");
        GetLoginOutData loginUser = this.getLoginUser(request);
        inData.setClient(loginUser.getClient());
           return gaiaInternalQualityReviewServcie.exportExcel(inData);
    }

}

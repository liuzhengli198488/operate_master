package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.data.suggestion.dto.QueryAdjustmentDto;
import com.gys.entity.data.suggestion.dto.UpdateExportDto;
import com.gys.entity.data.suggestion.vo.AdjustmentSummaryVo;
import com.gys.service.GaiaStoreInSuggestionHService;
import com.gys.service.GaiaStoreOutSuggestionHService;
import com.gys.util.easyExcel.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;

/**
 * 门店调剂建议汇总
 */
@Slf4j
@RestController
@RequestMapping("/adjustment/summary")
public class AdjustmentSuggestionController extends BaseController {


    @Resource
    private GaiaStoreOutSuggestionHService outSuggestionHService;

    @Resource
    private GaiaStoreInSuggestionHService inSuggestionHService;

    /**
     * 查询
     */
    @PostMapping("suggestions")
    public JsonResult getSuggestions(HttpServletRequest request,@Valid @RequestBody QueryAdjustmentDto dto) {
        PageInfo<AdjustmentSummaryVo> suggestions = outSuggestionHService.getSuggestions(dto);
        return JsonResult.success(suggestions, "查询成功");
    }

    /**
     * 导出
     */
    @PostMapping("exportsuggestions")
    public void exportsuggestions(HttpServletRequest request, HttpServletResponse response, @RequestBody QueryAdjustmentDto dto) {
        if (dto.getPageNum() == null) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(50);
        }
        PageInfo<AdjustmentSummaryVo> voList = outSuggestionHService.getSuggestions(dto);
        List<AdjustmentSummaryVo> voListList = voList.getList();
        if (CollectionUtils.isEmpty(voListList)) {
            throw new BusinessException("未查询到数据!");
        }
        //AdjustmentSummaryVo listNum = voList.getListNum();
       // voListList.add(listNum);
        String fileName = "报表导出";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            EasyExcelUtil.writeEasyExcel(response,AdjustmentSummaryVo.class,voListList,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("updateOutExport")
    public JsonResult updateOutExport(HttpServletRequest request, @RequestBody UpdateExportDto dto) {
        GetLoginOutData loginUser = getLoginUser(request);
        dto.setClientId(loginUser.getClient());
        dto.setStoreId(loginUser.getDepId());
        outSuggestionHService.updateOutExport(dto);
        return JsonResult.success(null, "更新成功");
    }

    @PostMapping("updateInExport")
    public JsonResult updateInExport(HttpServletRequest request, @RequestBody UpdateExportDto dto) {
        GetLoginOutData loginUser = getLoginUser(request);
        dto.setClientId(loginUser.getClient());
        dto.setStoreId(loginUser.getDepId());
        inSuggestionHService.updateInExport(dto);
        return JsonResult.success(null, "更新成功");
    }
}

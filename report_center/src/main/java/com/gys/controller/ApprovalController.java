package com.gys.controller;
import com.github.pagehelper.PageInfo;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.dto.ImageDto;
import com.gys.entity.data.approval.dto.UrlDto;
import com.gys.entity.data.approval.vo.ApprovalVo;
import com.gys.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *  批件模块
 */
@Slf4j
@RestController
@RequestMapping("/approval")
public class ApprovalController extends BaseController {
    @Resource
    private ApprovalService approvalService;

    /**
     * 查询批件列表
     */
    @PostMapping("approvals")
    public JsonResult getApprovals(HttpServletRequest request, @RequestBody  ApprovalDto dto) {
       GetLoginOutData userInfo = getLoginUser(request);
        dto.setClient(userInfo.getClient());
        if(StringUtils.isBlank(dto.getWhereWmJhdh())&&StringUtils.isBlank(dto.getProName())&&StringUtils.isBlank(dto.getProPym())&&StringUtils.isBlank(dto.getProSelfCode())){
            throw new BusinessException("至少输入一个查询条件!");
        }
        if (dto.getPageNum() == null) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null || dto.getPageSize() > 100) {
            dto.setPageSize(20);
        }
         List<ApprovalVo> approvalVoList= new ArrayList<>();
        //拣货单号为空
        if(StringUtils.isBlank(dto.getWhereWmJhdh())){
          //PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
            PageInfo<ApprovalVo> voPageInfo=approvalService.getAllList(dto);
          return JsonResult.success(voPageInfo,"查询成功");
        }
          //拣货单
          PageInfo<ApprovalVo> pageInfo =approvalService.getListBydeliveryNumber(dto);
       return JsonResult.success(pageInfo,"查询成功");
    }

    @PostMapping("download")
    public void download(HttpServletRequest request,HttpServletResponse response, @RequestBody ImageDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
       dto.setClient(userInfo.getClient());
        List<UrlDto> dtos = dto.getDtos();
        if(CollectionUtils.isEmpty(dtos)){
            throw new BusinessException("未选中下载图片!");
        }
        approvalService.downloadZip(response,dto);
    }

}

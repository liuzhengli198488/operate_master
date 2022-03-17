package com.gys.service;

import com.github.pagehelper.PageInfo;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.dto.ImageDto;
import com.gys.entity.data.approval.vo.ApprovalVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ApprovalService {

    PageInfo<ApprovalVo> getAllList(ApprovalDto dto);

    PageInfo<ApprovalVo> getListBydeliveryNumber(ApprovalDto dto);

    void downloadZip(HttpServletResponse response, ImageDto dtos);
}

package com.gys.controller.wk;

import com.github.pagehelper.PageInfo;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.enums.SerialCodeTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.entity.wk.dto.QualityManagementSystemReportDelDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportQueryDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportUpdateDto;
import com.gys.entity.wk.entity.GaiaQualityManagementSystemReport;
import com.gys.entity.wk.vo.QualityManagementSystemReportVo;
import com.gys.service.CommonService;
import com.gys.service.QualityManagementSystemReportService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.DateUtil;
import com.gys.util.easyExcel.EasyExcelUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther: tzh
 * @Date: 2021/12/30 11:03
 * @Description: 卫康报表 gaia_quality_management_system_report
 * @Version 1.0.0
 */

@RestController
@RequestMapping("/quality/management/")
public class QualityManagementSystemReportController extends BaseController {
    @Resource
    private QualityManagementSystemReportService qualityManagementSystemReportService;
    @Resource
    private CommonService commonService;

    @PostMapping("/addRecords")
    public JsonResult addRecords(HttpServletRequest request, @RequestBody List<QualityManagementSystemReportDto> dtos) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (CollectionUtils.isEmpty(dtos)) {
            throw new BusinessException("新增对象为空");
        }
        String depId = userInfo.getDepId();
        String loginName = userInfo.getLoginName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(depId)) {
            builder.append(depId);
        }
        if (StringUtils.isNotBlank(loginName)) {
            builder.append("-").append(loginName);
        }
        for (QualityManagementSystemReportDto managementSystemReportDto : dtos) {
            String serialCode = commonService.getSerialCode(userInfo.getClient(), SerialCodeTypeEnum.QUALITY_MANAGEMENT);
            if (StringUtils.isBlank(serialCode)) {
                throw new BusinessException("序列号生成出异常！");
            }
            managementSystemReportDto.setVoucherId(serialCode);
            managementSystemReportDto.setClient(userInfo.getClient());
            managementSystemReportDto.setCreateTime(new Date());
            managementSystemReportDto.setUpdateTime(new Date());
            managementSystemReportDto.setIsDelete(0);
            managementSystemReportDto.setCreateUser(builder.toString());
            managementSystemReportDto.setUpdateUser(builder.toString());
            if(managementSystemReportDto.getApproveTime()==null){
                managementSystemReportDto.setApproveTime(new Date());
            }
            if(managementSystemReportDto.getAuditTime()==null){
                managementSystemReportDto.setAuditTime(new Date());
            }
        }
        qualityManagementSystemReportService.addRecords(dtos);
        return JsonResult.success(null, "新增成功");
    }

    @PostMapping("search")
    public JsonResult searchRecords(HttpServletRequest request, @RequestBody QualityManagementSystemReportQueryDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (dto.getPageNum() == null) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(50);
        }
        dto.setClient(userInfo.getClient());
        PageInfo<GaiaQualityManagementSystemReport> recordList = qualityManagementSystemReportService.searchRecords(dto);
        return JsonResult.success(recordList, "查询成功");
    }


    @PostMapping("exportRecords")
    public void exportRecord(HttpServletRequest request, HttpServletResponse response, @RequestBody QualityManagementSystemReportQueryDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        dto.setClient(userInfo.getClient());
        if (dto.getPageNum() == null) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(50);
        }
        PageInfo<GaiaQualityManagementSystemReport> recordList = qualityManagementSystemReportService.searchRecords(dto);
        List<GaiaQualityManagementSystemReport> recordListList = recordList.getList();
        List<QualityManagementSystemReportVo> dataList = new ArrayList<>();
        if (recordListList.size() > 0) {
            dataList = recordListList.stream().map(gaiaQualityManagementSystemReport -> {
                QualityManagementSystemReportVo exportOutData = new QualityManagementSystemReportVo();
                BeanCopyUtils.copyPropertiesIgnoreNull(gaiaQualityManagementSystemReport, exportOutData);
                exportOutData.setAuditTime(DateUtil.formatDate(gaiaQualityManagementSystemReport.getAuditTime()));
                exportOutData.setApproveTime(DateUtil.formatDate(gaiaQualityManagementSystemReport.getApproveTime()));
                return exportOutData;
            }).collect(Collectors.toList());
        }
        try {
            EasyExcelUtil.writeEasyExcel(response, QualityManagementSystemReportVo.class, dataList, "继续教育档案");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("delList")
    public JsonResult delList(HttpServletRequest request, @RequestBody QualityManagementSystemReportDelDto dto) {
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessException("删除内容为空");
        }
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        List<GaiaQualityManagementSystemReport> reportList = qualityManagementSystemReportService.selectList(dto.getIds());
        if (CollectionUtils.isEmpty(reportList)) {
            throw new BusinessException("数据异常!");
        }
        List<GaiaQualityManagementSystemReport> reports = reportList.stream().filter(s -> s.getClient().equals(userInfo.getClient())).collect(Collectors.toList());
        if (reports.size() != reportList.size()) {
            throw new BusinessException("存在异常供应商数据，无法删除!");
        }
        String depId = userInfo.getDepId();
        String loginName = userInfo.getLoginName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(depId)) {
            builder.append(depId);
        }
        if (StringUtils.isNotBlank(loginName)) {
            builder.append("-").append(loginName);
        }
        dto.setUpdateUser(builder.toString());
        qualityManagementSystemReportService.delList(dto);
        return JsonResult.success(null, "删除成功");
    }

    @PostMapping("updateReports")
    public JsonResult updateReport(HttpServletRequest request, @RequestBody QualityManagementSystemReportUpdateDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        //更新判断
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessException("未选中更新对象!");
        }
        //
        List<GaiaQualityManagementSystemReport> reportList = qualityManagementSystemReportService.selectList(dto.getIds());
        if (CollectionUtils.isEmpty(reportList)) {
            throw new BusinessException("数据异常!");
        }
        List<GaiaQualityManagementSystemReport> reports = reportList.stream().filter(s -> s.getClient().equals(userInfo.getClient())).collect(Collectors.toList());
        if (reports.size() != reportList.size()) {
            throw new BusinessException("存在异常供应商数据，无法删除!");
        }
        String depId = userInfo.getDepId();
        String loginName = userInfo.getLoginName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(depId)) {
            builder.append(depId);
        }
        if (StringUtils.isNotBlank(loginName)) {
            builder.append("-").append(loginName);
        }
        dto.setUpdateUser(builder.toString());
        dto.setUpdateTime(new Date());
        qualityManagementSystemReportService.updateList(dto);
        return JsonResult.success(null, "更新成功");
    }

}

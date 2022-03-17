package com.gys.controller.wk;
import com.github.pagehelper.PageInfo;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.enums.SerialCodeTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.entity.wk.dto.*;
import com.gys.entity.wk.dto.EmployeeEducationalRecordUpdateDto;
import com.gys.entity.wk.entity.GaiaEmployeeEducationalRecord;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.vo.EducationalVo;
import com.gys.entity.wk.vo.EmployeeEducationalRecordVo;
import com.gys.service.CommonService;
import com.gys.service.EmployeeEducationalRecordService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.DateUtil;
import com.gys.util.easyExcel.EasyExcelUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther: tzh
 * @Date: 2021/12/30 11:03
 * @Description: 卫康报表 gaia_employee_educational_record
 * @Version 1.0.0
 */

@RestController
@RequestMapping("/employee/educational/")
public class EmployeeEducationalRecordController extends BaseController {
    @Resource
    private EmployeeEducationalRecordService employeeEducationalRecordService;
    @Resource
    private CommonService commonService;

    @PostMapping("/addRecords")
    public JsonResult addRecords(HttpServletRequest request, @RequestBody List<EmployeeEducationalRecordDto> dtos) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (CollectionUtils.isEmpty(dtos)) {
            throw new BusinessException("新增对象为空");
        }
        for (EmployeeEducationalRecordDto dto : dtos) {
            String serialCode = commonService.getSerialCode(userInfo.getClient(), SerialCodeTypeEnum.EMPLOYEE_EDUCATIONAL);
            if (StringUtils.isBlank(serialCode)) {
                throw new BusinessException("序号生成异常!");
            }
            dto.setVoucherId(serialCode);
            dto.setCreateTime(new Date());
            String depId = userInfo.getDepId();
            String loginName = userInfo.getLoginName();
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(depId)) {
                builder.append(depId);
            }
            if (StringUtils.isNotBlank(loginName)) {
                builder.append("-").append(loginName);
            }
            dto.setCreateUser(builder.toString());
            dto.setUpdateUser(builder.toString());
            dto.setUpdateTime(new Date());
            dto.setClient(userInfo.getClient());
            dto.setIsDelete(0);
            if(dto.getScore()==null){
                dto.setScore(BigDecimal.ZERO);
            }
            if(dto.getStartDate()==null){
                dto.setStartDate(new Date());
            }
            if(dto.getEndDate()==null){
                dto.setEndDate(new Date());
            }
        }
        employeeEducationalRecordService.addRecords(dtos);
        return JsonResult.success(null, "新增成功");
    }

    @PostMapping("search")
    public JsonResult searchRecords(HttpServletRequest request, @RequestBody EmployeeEducationalRecordQueryDto dto) {
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
        dto.setClient(userInfo.getClient());
        PageInfo<GaiaEmployeeEducationalRecord> recordList = employeeEducationalRecordService.searchRecords(dto);
        return JsonResult.success(recordList, "查询成功");
    }

    @PostMapping("del")
    public JsonResult delRecords(HttpServletRequest request, @RequestBody EmployeeEducationalRecordDelDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessException("未选择删除对象!");
        }
        //
        List<GaiaEmployeeEducationalRecord> reportList = employeeEducationalRecordService.selectList(dto.getIds());
        if (CollectionUtils.isEmpty(reportList)) {
            throw new BusinessException("数据异常!");
        }
        List<GaiaEmployeeEducationalRecord> reports = reportList.stream().filter(s -> s.getClient().equals(userInfo.getClient())).collect(Collectors.toList());
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
        employeeEducationalRecordService.del(dto);
        return JsonResult.success(null, "删除成功");
    }

    @PostMapping("records")
    public JsonResult getRecord(HttpServletRequest request, @RequestBody List<Integer> dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (CollectionUtils.isEmpty(dto)) {
            throw new BusinessException("未选择对象!");
        }
        List<GaiaEmployeeEducationalRecord> recordList = employeeEducationalRecordService.getList(dto);
        return JsonResult.success(recordList, "查询成功");
    }

    @PostMapping("exportRecords")
    public void exportRecord(HttpServletRequest request, HttpServletResponse response, @RequestBody EmployeeEducationalRecordQueryDto dto) {
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
        PageInfo<GaiaEmployeeEducationalRecord> recordList = employeeEducationalRecordService.searchRecords(dto);
        List<GaiaEmployeeEducationalRecord> recordListList = recordList.getList();
        List<EmployeeEducationalRecordVo> dataList = new ArrayList<>();
        if (recordListList.size() > 0) {
            dataList = recordListList.stream().map(gaiaEmployeeEducationalRecord -> {
                EmployeeEducationalRecordVo exportOutData = new EmployeeEducationalRecordVo();
                BeanCopyUtils.copyPropertiesIgnoreNull(gaiaEmployeeEducationalRecord, exportOutData);
                exportOutData.setStartDate(DateUtil.formatDate(gaiaEmployeeEducationalRecord.getStartDate()));
                exportOutData.setEndDate(DateUtil.formatDate(gaiaEmployeeEducationalRecord.getEndDate()));
                return exportOutData;
            }).collect(Collectors.toList());
        }
        try {
            EasyExcelUtil.writeEasyExcel(response, EmployeeEducationalRecordVo.class, dataList, "继续教育档案");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/importRecords")
    public JsonResult importRecords(@RequestParam("multipartFile") MultipartFile multipartFile) {
        List<EmployeeEducationalRecordUpLoadDto> recordUpLoadDtoList = null;
        try {
            recordUpLoadDtoList = EasyExcelUtil.readExcel(multipartFile.getInputStream(), EmployeeEducationalRecordUpLoadDto.class, new EasyExcelUtil.ExcelListener<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // todo 处理数据
        return JsonResult.success(recordUpLoadDtoList, "导入成功");
    }

    @PostMapping("/updateRecords")
    public JsonResult updateRecords(HttpServletRequest request, @RequestBody EmployeeEducationalRecordUpdateDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        List<GaiaEmployeeEducationalRecord> reportList = employeeEducationalRecordService.selectList(dto.getIds());
        if (CollectionUtils.isEmpty(reportList)) {
            throw new BusinessException("数据异常!");
        }
        List<GaiaEmployeeEducationalRecord> reports = reportList.stream().filter(s -> s.getClient().equals(userInfo.getClient())).collect(Collectors.toList());
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
        dto.setClient(userInfo.getClient());
        dto.setUpdateUser(builder.toString());
        dto.setUpdateTime(new Date());
        employeeEducationalRecordService.update(dto);
        return JsonResult.success(null, "更新成功");
    }

    @PostMapping("/educationals")
    public JsonResult educationals() {
        List<EducationalVo> voList = employeeEducationalRecordService.getEducationals();
        return JsonResult.success(voList, "查询成功");
    }

}

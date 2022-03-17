package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.EmployeeHealthRecordDelDto;
import com.gys.entity.EmployeeHealthRecordDto;
import com.gys.entity.EmployeeHealthRecordModDto;
import com.gys.entity.GaiaEmployeeHealthRecord;
import com.gys.service.EmployeeHealthRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApiOperation("员工个人健康检查档案")
@RestController
@RequestMapping("/employeeHealthRecord/")
public class EmployeeHealthRecordController extends BaseController {

    @Resource
    private EmployeeHealthRecordService employeeHealthRecordService;

    @ApiOperation("员工个人健康检查档案列表查询")
    @PostMapping("getRecords")
    public JsonResult getRecords (HttpServletRequest request, @RequestBody EmployeeHealthRecordDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        dto.setClient(userInfo.getClient());
        return JsonResult.success(employeeHealthRecordService.getEmployeeHealthRecords(dto), "查询成功");
    }

    @ApiOperation("员工个人健康检查档案新增")
    @PostMapping("addRecord")
    public JsonResult addRecord (HttpServletRequest request, @RequestBody GaiaEmployeeHealthRecord dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        dto.setClient(userInfo.getClient());
        dto.setCreateUser(userInfo.getLoginName().concat("-").concat(userInfo.getUserId()));
        employeeHealthRecordService.addEmployeeHealthRecord(dto);
        return JsonResult.success(null, "新增成功");
    }

    @ApiOperation("员工个人健康检查档案删除")
    @PostMapping("deleteRecords")
    public JsonResult deleteRecord (HttpServletRequest request, @RequestBody EmployeeHealthRecordDelDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        dto.setClient(userInfo.getClient());
        dto.setUpdateUser(userInfo.getLoginName().concat("-").concat(userInfo.getUserId()));
        employeeHealthRecordService.deleteEmployeeHealthRecords(dto);
        return JsonResult.success(null, "删除成功");
    }

    @ApiOperation("员工个人健康检查档案修改")
    @PostMapping("modfiyRecords")
    public JsonResult modifRecords (HttpServletRequest request, @RequestBody EmployeeHealthRecordModDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        dto.setClient(userInfo.getClient());
        dto.setUpdateUser(userInfo.getLoginName().concat("-").concat(userInfo.getUserId()));
        employeeHealthRecordService.modifEmployeeHealthRecords(dto);
        return JsonResult.success(null, "修改成功");
    }

    @ApiOperation("员工个人健康检查档案信息导出")
    @PostMapping("reportRecords")
    public void reportRecords (HttpServletRequest request, HttpServletResponse response , @RequestBody EmployeeHealthRecordDto dto) {
       GetLoginOutData userInfo = getLoginUser(request);
        dto.setClient(userInfo.getClient());
        employeeHealthRecordService.reportEmployeeHealthRecords(response,dto);
    }

}

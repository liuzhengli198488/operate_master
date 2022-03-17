package com.gys.service;

import com.github.pagehelper.PageInfo;
import com.gys.entity.EmployeeHealthRecordDelDto;
import com.gys.entity.EmployeeHealthRecordDto;
import com.gys.entity.EmployeeHealthRecordVo;
import com.gys.entity.GaiaEmployeeHealthRecord;
import com.gys.entity.EmployeeHealthRecordModDto;
import javax.servlet.http.HttpServletResponse;

public interface EmployeeHealthRecordService {


    PageInfo<EmployeeHealthRecordVo> getEmployeeHealthRecords(EmployeeHealthRecordDto dto);

    void addEmployeeHealthRecord(GaiaEmployeeHealthRecord dto);

    void deleteEmployeeHealthRecords(EmployeeHealthRecordDelDto dto);

    void modifEmployeeHealthRecords(EmployeeHealthRecordModDto dto);

    void reportEmployeeHealthRecords(HttpServletResponse response , EmployeeHealthRecordDto dto);

}

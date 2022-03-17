package com.gys.service;

import com.github.pagehelper.PageInfo;
import com.gys.entity.wk.dto.EmployeeEducationalRecordDelDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordQueryDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordUpdateDto;
import com.gys.entity.wk.entity.GaiaEmployeeEducationalRecord;
import com.gys.entity.wk.vo.EducationalVo;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 15:43
 * @Description: MedicinalQualityRecordService
 * @Version 1.0.0
 */
public interface EmployeeEducationalRecordService {

    void addRecords(List<EmployeeEducationalRecordDto> dtos);

    PageInfo<GaiaEmployeeEducationalRecord> searchRecords(EmployeeEducationalRecordQueryDto dto);

    void del(EmployeeEducationalRecordDelDto dto);

    List<GaiaEmployeeEducationalRecord> getList(List<Integer> dto);

    void update(EmployeeEducationalRecordUpdateDto dtos);

    List<EducationalVo> getEducationals();

    List<GaiaEmployeeEducationalRecord> selectList(List<Integer> ids);
}

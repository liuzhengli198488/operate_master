package com.gys.mapper;
import com.gys.entity.wk.dto.EmployeeEducationalRecordDelDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordQueryDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordUpdateDto;
import com.gys.entity.wk.entity.GaiaEmployeeEducationalRecord;
import com.gys.entity.wk.vo.EducationalVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaEmployeeEducationalRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GaiaEmployeeEducationalRecord record);

    int insertSelective(GaiaEmployeeEducationalRecord record);

    GaiaEmployeeEducationalRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GaiaEmployeeEducationalRecord record);

    int updateByPrimaryKey(GaiaEmployeeEducationalRecord record);

    void addList(@Param("dtos") List<EmployeeEducationalRecordDto> dtos);

    List searchList(EmployeeEducationalRecordQueryDto dto);

    void del(EmployeeEducationalRecordDelDto dto);

    List<GaiaEmployeeEducationalRecord> getList(@Param("dto") List<Integer> dto);

    void updateList( EmployeeEducationalRecordUpdateDto dto);

    List<EducationalVo> searchEducationals();
}
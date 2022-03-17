package com.gys.service.Impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.entity.wk.dto.EmployeeEducationalRecordDelDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordQueryDto;
import com.gys.entity.wk.dto.EmployeeEducationalRecordUpdateDto;
import com.gys.entity.wk.entity.GaiaEmployeeEducationalRecord;
import com.gys.entity.wk.vo.EducationalVo;
import com.gys.mapper.GaiaEmployeeEducationalRecordMapper;
import com.gys.service.EmployeeEducationalRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 15:44
 * @Description: MedicinalQualityRecordServiceImpl
 * @Version 1.0.0
 */
@Service
@Slf4j
public class EmployeeEducationalRecordImpl implements EmployeeEducationalRecordService {
    @Resource
    private GaiaEmployeeEducationalRecordMapper gaiaEmployeeEducationalRecordMapper;

    @Override
    public void addRecords(List<EmployeeEducationalRecordDto> dtos) {
        gaiaEmployeeEducationalRecordMapper.addList(dtos);
    }

    @Override
    public PageInfo<GaiaEmployeeEducationalRecord> searchRecords(EmployeeEducationalRecordQueryDto dto) {
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        List list = gaiaEmployeeEducationalRecordMapper.searchList(dto);
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo<>();
        }
        PageInfo<GaiaEmployeeEducationalRecord> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void del(EmployeeEducationalRecordDelDto dto) {
        gaiaEmployeeEducationalRecordMapper.del(dto);
    }

    @Override
    public List<GaiaEmployeeEducationalRecord> getList(List<Integer> dto) {

        return gaiaEmployeeEducationalRecordMapper.getList(dto);
    }

    @Override
    public void update(EmployeeEducationalRecordUpdateDto dto) {
          gaiaEmployeeEducationalRecordMapper.updateList(dto);
    }

    @Override
    public List<EducationalVo> getEducationals() {
        return     gaiaEmployeeEducationalRecordMapper.searchEducationals();
    }

    @Override
    public List<GaiaEmployeeEducationalRecord> selectList(List<Integer> ids) {
        return   gaiaEmployeeEducationalRecordMapper.getList(ids);

    }
}

package com.gys.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.entity.wk.dto.QualityManagementSystemReportDelDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportQueryDto;
import com.gys.entity.wk.dto.QualityManagementSystemReportUpdateDto;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.entity.GaiaQualityManagementSystemReport;
import com.gys.mapper.GaiaQualityManagementSystemReportMapper;
import com.gys.service.QualityManagementSystemReportService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.gys.util.DateUtil.DEFAULT_FORMAT2;

/**
 * @Auther: tzh
 * @Date: 2022/1/4 13:30
 * @Description: QualityManagementSystemReportServiceImpl
 * @Version 1.0.0
 */
@Service
@Slf4j
public class QualityManagementSystemReportServiceImpl implements QualityManagementSystemReportService {
    @Resource
    private GaiaQualityManagementSystemReportMapper gaiaQualityManagementSystemReportMapper;
    @Override
    public void addRecords(List<QualityManagementSystemReportDto> dtos) {
        gaiaQualityManagementSystemReportMapper.addList(dtos);
    }

    @Override
    public PageInfo<GaiaQualityManagementSystemReport> searchRecords(QualityManagementSystemReportQueryDto dto) {
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
       /* if(StringUtils.isNotBlank(dto.getBeginDate())){
            dto.setBegin(DateUtil.getStringToDate2(dto.getBeginDate()));
        }
        if(StringUtils.isNotBlank(dto.getEndDate())){
            dto.setEnd(DateUtil.getStringToDate2(dto.getEndDate()));
        }*/
        List list = gaiaQualityManagementSystemReportMapper.searchRecords(dto);
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo<>();
        }
        PageInfo<GaiaQualityManagementSystemReport> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public List<GaiaQualityManagementSystemReport> selectList(List<Long> dto) {
     return     gaiaQualityManagementSystemReportMapper.selectList(dto);

    }

    @Override
    public void delList(QualityManagementSystemReportDelDto dto) {
        gaiaQualityManagementSystemReportMapper.delList(dto);
    }

    @Override
    public GaiaQualityManagementSystemReport selectOne(Long id) {
        return  gaiaQualityManagementSystemReportMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateList(QualityManagementSystemReportUpdateDto dto) {
        gaiaQualityManagementSystemReportMapper.updateList(dto);
    }


}

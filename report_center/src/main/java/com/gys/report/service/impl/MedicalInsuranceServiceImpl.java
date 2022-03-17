package com.gys.report.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.MibSetlInfoMapper;
import com.gys.report.entity.dto.MedicalInsturanceSummaryDTO;
import com.gys.report.entity.vo.MedicalInsturanceExportVO;
import com.gys.report.entity.vo.MedicalInsturanceSummaryVO;
import com.gys.report.service.MedicalInsuranceService;
import com.gys.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MedicalInsuranceServiceImpl implements MedicalInsuranceService {

    @Resource
    private MibSetlInfoMapper mibSetlInfoMapper;

    //查询医疗费用汇总
    @Override
    public JsonResult queryMedicalInsuranceSummary(MedicalInsturanceSummaryDTO summaryDTO, GetLoginOutData login) {
        if(StrUtil.isBlank(summaryDTO.getStoCode())){
            throw new BusinessException("医疗机构不能为空");
        }
        if (StrUtil.isBlank(summaryDTO.getStartDate())||StrUtil.isBlank(summaryDTO.getEndDate())){
            throw new BusinessException(String.format("%s","日期不能为空"));
        }
        List<MedicalInsturanceSummaryVO> medicalInsturanceSummaryVOS = this.mibSetlInfoMapper.queryMedicalInsuranceSummary(summaryDTO);
        MedicalInsturanceExportVO medicalInsturanceExportVO = new MedicalInsturanceExportVO();
        if(CollectionUtil.isNotEmpty(medicalInsturanceSummaryVOS)){
            medicalInsturanceExportVO.setSummaryVOS(medicalInsturanceSummaryVOS);
        }
        medicalInsturanceExportVO.setUserName(login.getLoginName());
        medicalInsturanceExportVO.setStartDate(DateUtil.dateStrFormat1(summaryDTO.getStartDate()));
        medicalInsturanceExportVO.setEndDate(DateUtil.dateStrFormat1(summaryDTO.getEndDate()));
        medicalInsturanceExportVO.setStoName(summaryDTO.getStoName());
        return JsonResult.success(medicalInsturanceExportVO,"查询数据成功");
    }
}

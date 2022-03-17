package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.common.enums.SerialCodeTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.entity.*;
import com.gys.mapper.GaiaEmployeeHealthRecordMapper;
import com.gys.service.CommonService;
import com.gys.service.EmployeeHealthRecordService;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service("employeeHealthRecordService")
public class EmployeeHealthRecordServiceImpl implements EmployeeHealthRecordService {

    @Resource
    private GaiaEmployeeHealthRecordMapper gaiaEmployeeHealthRecordMapper;
    @Resource
    private CommonService commonService;

    @Override
    public PageInfo<EmployeeHealthRecordVo> getEmployeeHealthRecords(EmployeeHealthRecordDto dto) {
        if(ObjectUtil.isEmpty(dto.getPageNum())){
            throw  new BusinessException("页码不能为空");
        }
        if(ObjectUtil.isEmpty(dto.getPageSize())){
            throw  new BusinessException("页数不能为空");
        }
        checkQeyParams(dto);
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        List<EmployeeHealthRecordVo> list = gaiaEmployeeHealthRecordMapper.getEmployeeHealthRecords(dto);
        return  new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEmployeeHealthRecord(GaiaEmployeeHealthRecord dto) {

        checkParams(dto.getEmpId(),dto.getEmpName());
        String voucherId = commonService.getSerialCode(dto.getClient(), SerialCodeTypeEnum.EMPLOYEE_HEALTH);
        dto.setId(null);
        dto.setVoucherId(voucherId);
        Date currentDate = new Date();
        dto.setCreateTime(currentDate);
        dto.setUpdateTime(currentDate);
        gaiaEmployeeHealthRecordMapper.insertSelective(dto);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeHealthRecords(EmployeeHealthRecordDelDto dto) {
        if(CollectionUtils.isEmpty(dto.getIds())){
            throw  new BusinessException("档案编号不能为空");
        }
        GaiaEmployeeHealthRecord gaiaEmployeeHealthRecord = new GaiaEmployeeHealthRecord();
        gaiaEmployeeHealthRecord.setUpdateTime(new Date());
        gaiaEmployeeHealthRecord.setIsDelete(1);
        gaiaEmployeeHealthRecord.setUpdateUser(dto.getUpdateUser());
        for(Long id : dto.getIds()){
            gaiaEmployeeHealthRecord.setId(id);
            updateGaiaEmployeeHealthRecord(gaiaEmployeeHealthRecord,dto.getClient());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifEmployeeHealthRecords(EmployeeHealthRecordModDto dto) {
        if(CollectionUtils.isEmpty(dto.getIds())){
            throw  new BusinessException("档案编号不能为空");
        }
        checkParams(dto.getEmpId(),dto.getEmpName());

        GaiaEmployeeHealthRecord record = new GaiaEmployeeHealthRecord();
        BeanUtils.copyProperties(dto,record);
        record.setUpdateTime(new Date());
        record.setUpdateUser(dto.getUpdateUser());
        for(Long id : dto.getIds()){
            record.setId(id);
            updateGaiaEmployeeHealthRecord(record,dto.getClient());
        }
    }
    private  void updateGaiaEmployeeHealthRecord(GaiaEmployeeHealthRecord gaiaEmployeeHealthRecord ,String client){
        GaiaEmployeeHealthRecord employeeHealthRecord = gaiaEmployeeHealthRecordMapper.selectByPrimaryKey(gaiaEmployeeHealthRecord.getId());
        if(ObjectUtil.isNull(employeeHealthRecord)|| !employeeHealthRecord.getClient().equals(client) ){
            return ;
        }
        gaiaEmployeeHealthRecordMapper.updateByPrimaryKeySelective(gaiaEmployeeHealthRecord);
    }
    @Override
    public void reportEmployeeHealthRecords(HttpServletResponse response , EmployeeHealthRecordDto dto) {
        checkQeyParams(dto);
        List<EmployeeHealthRecordReportVo> list = gaiaEmployeeHealthRecordMapper.reportEmployeeHealthRecords(dto);
        String fileName = "员工个人健康检查档案信息";
        if (list.size() > 0){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            try {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
                EasyExcel.write(response.getOutputStream(), EmployeeHealthRecordReportVo.class)
                        .sheet("0").doWrite(list);
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
                throw new BusinessException("下载失败");
            }
        }else{
            throw new BusinessException("提示：数据为空！");
        }
    }

    private void checkParams(String empId,String empName){
        if(ObjectUtil.isEmpty(empId)){
            throw  new BusinessException("职工编号不能为空");
         }
        if(ObjectUtil.isEmpty(empName)){
            throw  new BusinessException("职工姓名不能为空");
         }
    }
    private void checkQeyParams(EmployeeHealthRecordDto dto) {
        if(StringUtils.isEmpty(dto.getStartDate())){
            throw  new BusinessException("建档开始时间不能为空");
        }
        if(StringUtils.isEmpty(dto.getEndDate())){
            throw  new BusinessException("建档结束时间不能为空");
        }
        Date queryStartDate = DateUtil.transformDate(dto.getStartDate(),"yyyyMMdd");
        Date queryEndDate = DateUtil.transformDate(dto.getEndDate(),"yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(queryEndDate);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        dto.setQueryStartDate(queryStartDate);
        dto.setQueryEndDate(calendar.getTime());
    }
}

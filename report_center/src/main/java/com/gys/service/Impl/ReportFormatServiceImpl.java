package com.gys.service.Impl;


import com.alibaba.fastjson.JSON;
import com.gys.entity.data.reportFormat.ReportFormatData;
import com.gys.mapper.*;
import com.gys.service.ReportFormatService;
import com.gys.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ReportFormatServiceImpl implements ReportFormatService {
    @Resource
    private ReportFormatMapper reportFormatMapper;

    @Override
    public Integer saveReportFormat(Map inData) {
        ReportFormatData reportFormatData = addParam(inData);
        List<ReportFormatData> rs = reportFormatMapper.selectReportFormat(reportFormatData);
        if(CollectionUtils.isEmpty(rs)){
            reportFormatMapper.insertReportFormat(reportFormatData);
        }else{
            reportFormatMapper.updateReportFormat(reportFormatData);
        }
        return 0;
    }

    @Override
    public Integer deleteReportFormat(Map inData) {
        ReportFormatData reportFormatData = addParam(inData);
        List<ReportFormatData> rs = reportFormatMapper.selectReportFormat(reportFormatData);
        if(CollectionUtils.isNotEmpty(rs)){
            reportFormatMapper.deleteReportFormat(reportFormatData);
        }
        return 0;
    }

    @Override
    public ReportFormatData selectReportFormat(Map inData) {
        List<ReportFormatData> rs = reportFormatMapper.selectReportFormat(addParam(inData));
        if(CollectionUtils.isEmpty(rs)){
            return null;
        }else{
            return rs.get(0);
        }
    }

    private ReportFormatData addParam(Map inData){
        ReportFormatData reportFormatData = new ReportFormatData();
        reportFormatData.setClient((String) inData.get("client"));
        reportFormatData.setUserId((String) inData.get("userId"));
        reportFormatData.setResourceId((String) inData.get("resourceId"));
        reportFormatData.setTabName((String) inData.get("tabName"));
        Object json =  inData.get("formatDetail");
        String formatDetail = "";
        if(json !=null){
            formatDetail = JSON.toJSONString(json);
        }
        reportFormatData.setFormatDetail(formatDetail);
        reportFormatData.setCreateDate(DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
        reportFormatData.setUpdateDate(DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
        return reportFormatData;
    }
}

package com.gys.report.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gys.entity.data.salesSummary.SalesSummaryData;
import com.gys.mapper.GaiaProCampaignsMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.report.common.constant.CommonConstant;
import com.gys.report.entity.ProCampaignsOutData;
import com.gys.report.service.ProCampaignsService;
import com.gys.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProCampaignsServiceImpl implements ProCampaignsService {
    @Resource
    private GaiaProCampaignsMapper campaignsMapper;

    @Autowired
    private GaiaStoreDataMapper gaiaStoreDataMapper;

    @Override
    public List<ProCampaignsOutData> selectCampainsProDetails(SalesSummaryData inData) {
        if (StringUtils.isNotEmpty(inData.getGssdProId())) {
            inData.setProArr(inData.getGssdProId().split("\\s+ |\\s+|,"));
        }
        if(ObjectUtil.isNotEmpty(inData.getClassArr())){
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        return campaignsMapper.selectCampainsProDetails(inData);
    }

    @Override
    public List<ProCampaignsOutData> selectCampainsProTotal(SalesSummaryData inData) {
        //先查询权限   flag  0：不开启  1：开启
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClient(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        inData.setFlag(flag);
        if (ObjectUtil.isEmpty(flag)){
            inData.setFlag("0");
        }else {
            inData.setFlag(flag);
        }
        if (StringUtils.isNotEmpty(inData.getGssdProId())) {
            inData.setProArr(inData.getGssdProId().split("\\s+ |\\s+|,"));
        }
        if(ObjectUtil.isNotEmpty(inData.getClassArr())){
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }

        return campaignsMapper.selectCampainsProTotal(inData);
    }

}

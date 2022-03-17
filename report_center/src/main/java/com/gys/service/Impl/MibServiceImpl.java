package com.gys.service.Impl;

import com.gys.common.data.GetLoginOutData;
import com.gys.entity.InData;
import com.gys.entity.MibInfoOutData;
import com.gys.mapper.MibSetlInfoMapper;
import com.gys.service.MibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wavesen.shen
 */
@Service
public class MibServiceImpl implements MibService {

    @Autowired
    private MibSetlInfoMapper mibSetlInfoMapper;


	@Override
    public List<MibInfoOutData> getSeltInfo(GetLoginOutData userInfo, InData inData) {
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        List<MibInfoOutData> hasList = mibSetlInfoMapper.getSeltInfo(inData);
        return hasList;
    }
    @Override
    public List<MibInfoOutData> getSeltInfoSum(GetLoginOutData userInfo, InData inData) {
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        String mibArea = mibSetlInfoMapper.selectStoPriceComparison(inData.getClient(), inData.getStoCode(), "MIB_AREA");
        List<MibInfoOutData> hasList;
        if ("HLJ".equals(mibArea)) {
            hasList = mibSetlInfoMapper.getSeltInfoSumByHlj(inData);
        } else {
            hasList = mibSetlInfoMapper.getSeltInfoSum(inData);
        }

        return hasList;
    }

    @Override
    public List<MibInfoOutData> getSeltInfoSumByHlj2(GetLoginOutData userInfo, InData inData) {
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        List<MibInfoOutData> hasList;
            hasList = mibSetlInfoMapper.getSeltInfoSumByHlj2(inData);

        return hasList;
    }

    @Override
    public List<MibInfoOutData> getSeltInfoSum2(GetLoginOutData userInfo, InData inData) {
        inData.setClient(userInfo.getClient());
        List<MibInfoOutData> hasList = mibSetlInfoMapper.getSeltInfoSum2(inData);
        return hasList;
    }

    @Override
    public List<MibInfoOutData> getSeltInfo3202(GetLoginOutData userInfo, InData inData) {
        inData.setClient(userInfo.getClient());
        inData.setStoCode(userInfo.getDepId());
        List<MibInfoOutData> hasList = mibSetlInfoMapper.getSeltInfo3202(inData);
        return hasList;
    }

}

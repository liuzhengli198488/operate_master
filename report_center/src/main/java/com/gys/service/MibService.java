package com.gys.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.entity.InData;
import com.gys.entity.MibInfoOutData;

import java.util.List;
import java.util.Map;

/**
 * @author wavesen.shen
 */
public interface MibService {

    List<MibInfoOutData> getSeltInfoSum(GetLoginOutData userInfo, InData inData);

    List<MibInfoOutData> getSeltInfo3202(GetLoginOutData userInfo, InData inData);
    List<MibInfoOutData> getSeltInfo(GetLoginOutData userInfo, InData inData);
    List<MibInfoOutData> getSeltInfoSumByHlj2(GetLoginOutData userInfo, InData inData);

    List<MibInfoOutData> getSeltInfoSum2(GetLoginOutData userInfo, InData inData);
}

package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.common.data.SdElectronChangeInData;
import com.gys.common.response.Result;
import com.gys.entity.GaiaSdElectronChange;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2022/1/7 16:00
 */
public interface GaiaSdElectronChangeService {
    PageInfo<GaiaSdElectronChange> getElectronChange(SdElectronChangeInData inData);

    Result exportCSV(SdElectronChangeInData inData);
}

package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.common.data.SdElectronChangeInData;
import com.gys.entity.GaiaSdElectronChange;

import java.util.List;

public interface GaiaSdElectronChangeMapper extends BaseMapper<GaiaSdElectronChange> {
    List<GaiaSdElectronChange> getElectronChange(SdElectronChangeInData inData);
}
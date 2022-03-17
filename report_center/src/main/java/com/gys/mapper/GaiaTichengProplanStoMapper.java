package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengProplanSto;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV2InData;

import java.util.List;
import java.util.Map;

public interface GaiaTichengProplanStoMapper extends BaseMapper<GaiaTichengProplanSto> {
    List<String> selectSto(Map inData);

    List<String> selectStoV3(Map inData);
}
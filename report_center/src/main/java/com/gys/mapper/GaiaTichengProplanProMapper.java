package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengProplanPro;
import com.gys.entity.GaiaTichengProplanProV3;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV2InData;

import java.util.List;
import java.util.Map;

public interface GaiaTichengProplanProMapper extends BaseMapper<GaiaTichengProplanPro> {
    List<Map<String, String>> selectPro(Map inData);

    List<GaiaTichengProplanProV3> selectProV3(Map inData);
}
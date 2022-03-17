package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengProplanZ;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyBySalespersonV2InData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyByStoreV2InData;
import com.gys.entity.data.MonthPushMoney.V2.PushMoneyListV2OutData;
import com.gys.entity.data.MonthPushMoney.V2.TichengZInfoBO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GaiaTichengProplanZMapper extends BaseMapper<GaiaTichengProplanZ> {
    GaiaTichengProplanZ selectTichengProZ(Map inData);

    List<TichengZInfoBO> selectAllByPro(Map inData);
//    GaiaTichengProplanZ selectAllPro(int id);

    List<TichengZInfoBO> selectAllStoreByProTotal(Map inData);//新版

    List<TichengZInfoBO> selectAllStoreByPro(Map inData);//新版

    List<TichengZInfoBO> selectAllStoreByProZ(Map inData);//新版
}
package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaWmsViewYaojian;
import com.gys.entity.data.YaoJianInData;
import com.gys.entity.data.YaoJianOutData;

import java.util.List;

public interface GaiaWmsViewYaojianMapper extends BaseMapper<GaiaWmsViewYaojian> {
    List<YaoJianOutData> getListByProductInfo(YaoJianInData inData);

    List<YaoJianOutData> getListByBillNo(YaoJianInData inData);
}
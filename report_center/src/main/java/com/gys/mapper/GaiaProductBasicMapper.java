package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaProductBasic;
import org.apache.ibatis.annotations.Param;

public interface GaiaProductBasicMapper extends BaseMapper<GaiaProductBasic> {
    GaiaProductBasic queryProductBasicById(@Param("proCode") String proCode);

    void modifyProductBasicById(GaiaProductBasic inData);

    String selectMaxProcode();
}
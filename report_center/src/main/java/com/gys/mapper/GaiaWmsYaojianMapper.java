package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaWmsYaojian;
import org.apache.ibatis.annotations.Param;


public interface GaiaWmsYaojianMapper extends BaseMapper<GaiaWmsYaojian> {
    String getPictureUrl(@Param("client") String client, @Param("supplierCoder") String supplierCoder, @Param("proCode") String proCode, @Param("batchNo") String batchNo);
}
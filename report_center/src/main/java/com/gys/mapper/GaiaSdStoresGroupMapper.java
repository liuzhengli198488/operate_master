package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaSdStoresGroup;
import com.gys.report.entity.GaiaStoreCategoryType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface GaiaSdStoresGroupMapper extends BaseMapper<GaiaSdStoresGroup> {
    List<GaiaStoreCategoryType> selectStoreCategoryByClient(String clientId);
}
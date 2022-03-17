package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.TichengProplanSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author flynn
 * @since 2021-11-15
 */
@Mapper
public interface TichengProplanSettingMapper extends BaseMapper<TichengProplanSetting> {

    /**
     * 根据条件查询子方案
     *
     * @param tichengProplanSetting tichengProplanSetting
     * @return List<TichengProplanSetting>
     */
    List<TichengProplanSetting> selectAllByCondition(TichengProplanSetting tichengProplanSetting);

}


package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaCalDate;
import org.apache.ibatis.annotations.Param;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/12/29 15:05
 */
public interface GaiaCalDateMapper extends BaseMapper<GaiaCalDate> {

    GaiaCalDate getByDate(@Param("date") String date);

}

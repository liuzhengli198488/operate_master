package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengProplanZN;

import java.util.Map;

public interface GaiaTichengProplanZNMapper extends BaseMapper<GaiaTichengProplanZN> {
    GaiaTichengProplanZN selectTichengProZ(Map inData);

}

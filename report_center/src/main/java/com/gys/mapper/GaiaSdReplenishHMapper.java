package com.gys.mapper;
import com.gys.entity.GaiaSdReplenishH;
import com.gys.entity.GaiaSdReplenishHKey;
import com.gys.entity.data.xhl.dto.ReplenishDto;
import com.gys.entity.data.xhl.vo.ReplenishVo;

import java.util.List;

public interface GaiaSdReplenishHMapper {
    int deleteByPrimaryKey(GaiaSdReplenishHKey key);

    int insert(GaiaSdReplenishH record);

    int insertSelective(GaiaSdReplenishH record);

    GaiaSdReplenishH selectByPrimaryKey(GaiaSdReplenishHKey key);

    int updateByPrimaryKeySelective(GaiaSdReplenishH record);

    int updateByPrimaryKey(GaiaSdReplenishH record);

    List<ReplenishVo> getAllReplenishes(ReplenishDto replenishDto);

    List<ReplenishVo> getWeekReplenishes(ReplenishDto replenishDto);

    List<ReplenishVo> getMonthReplenishes(ReplenishDto replenishDto);
}
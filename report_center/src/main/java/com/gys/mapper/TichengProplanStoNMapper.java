package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.common.data.StoreSimpleInfoWithPlan;
import com.gys.entity.data.TichengProplanStoN;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 单品提成方案门店表 Mapper 接口
 * </p>
 *
 * @author flynn
 * @since 2021-11-15
 */
@Mapper
public interface TichengProplanStoNMapper extends BaseMapper<TichengProplanStoN> {

    /**
     * 根据加盟商、门店查询简单
     *
     * @param client client
     * @param planId planId
     * @return List<StoreSimpleInfo>
     */
    List<StoreSimpleInfoWithPlan> selectStoreByPlanIdAndClient(@Param("client") String client, @Param("planId") Integer planId);

}

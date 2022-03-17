package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.common.data.StoreSimpleInfoWithPlan;
import com.gys.entity.GaiaTichengSaleplanSto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wu mao yin
 * @Description: 销售提成关联门店
 * @date 2021/11/26 13:36
 */
public interface GaiaTichengSaleplanStoMapper extends BaseMapper<GaiaTichengSaleplanSto> {

    /**
     * 方案id查询门店编码
     *
     * @param id id
     * @return List<String>
     */
    List<String> selectSto(int id);

    /**
     * 根据加盟商、门店查询简单
     *
     * @param client client
     * @param planId planId
     * @return List<StoreSimpleInfo>
     */
    List<StoreSimpleInfoWithPlan> selectStoreByPlanIdAndClient(@Param("client") String client, @Param("planId") Integer planId);

}
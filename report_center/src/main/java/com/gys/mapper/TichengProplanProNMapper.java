package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.TichengProplanProN;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * <p>
 * 提成方案单品提成明细表 Mapper 接口
 * </p>
 *
 * @author flynn
 * @since 2021-11-15
 */
@Mapper
public interface TichengProplanProNMapper extends BaseMapper<TichengProplanProN> {

    /**
     * 单品提成方案
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return List<StoreCommissionSummary>
     */
    List<StoreCommissionSummary> selectProductCommissionPlan(StoreCommissionSummaryDO storeCommissionSummaryDO);

    /**
     * 单品提成主方案
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return List<StoreCommissionSummary>
     */
    List<StoreCommissionSummary> selectProCommissionMainPlan(StoreCommissionSummaryDO storeCommissionSummaryDO);


}

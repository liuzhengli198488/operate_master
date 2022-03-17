package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.common.data.EmpSaleDetailResVo;
import com.gys.entity.data.MonthPushMoney.TiChenProRes;
import com.gys.entity.data.commissionplan.CommissionSummaryDetailDTO;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.salesSummary.UserCommissionSummaryDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户提成明细 Mapper 接口
 * </p>
 *
 * @author yifan.wang
 * @since 2022-02-11
 */
@Mapper
public interface UserCommissionSummaryDetailMapper extends BaseMapper<UserCommissionSummaryDetail> {

    /**
     * 根据条件获取提成明细
     *
     * @param commissionSummaryDetailDTO commissionSummaryDetailDTO
     * @return List<EmpSaleDetailResVo>
     */
    List<EmpSaleDetailResVo> selectCommissionDetailByCondition(CommissionSummaryDetailDTO commissionSummaryDetailDTO);

    /**
     * 根据条件获取提成明细
     *
     * @param commissionSummaryDetailDTO commissionSummaryDetailDTO
     * @return List<TiChenProRes>
     */
    List<TiChenProRes> selectCommissionDetailByConditionWithTiChenProRes(CommissionSummaryDetailDTO commissionSummaryDetailDTO);

    /**
     * 根据条件获取提成明细
     *
     * @param commissionSummaryDetailDTO commissionSummaryDetailDTO
     * @return List<TiChenProRes>
     */
    List<Map<String, Object>> selectCommissionDetailByConditionWithMap(CommissionSummaryDetailDTO commissionSummaryDetailDTO);

    /**
     * 根据条件获取提成明细
     *
     * @param commissionSummaryDetailDTO commissionSummaryDetailDTO
     * @return List<StoreCommissionSummary>
     */
    List<StoreCommissionSummary> selectCommissionDetailByConditionWithStoreCommissionSummary(CommissionSummaryDetailDTO commissionSummaryDetailDTO);

    /**
     * 获取用户名
     *
     * @return List<Map < String, String>>
     */
    List<Map<String, String>> selectUserName(@Param("client") String client);

    /**
     * 获取所有加盟商
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> getClientList();

    /**
     * 删除加盟商下的数据
     * @param client 加盟商ID
     */
    void deleteClietnts(List<String> clients);
}

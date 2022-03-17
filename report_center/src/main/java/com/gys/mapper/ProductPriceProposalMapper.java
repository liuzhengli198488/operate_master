package com.gys.mapper;

import com.gys.entity.priceProposal.condition.SavePriceProposalCondition;
import com.gys.entity.priceProposal.condition.SelectPriceProposalDetailListCondition;
import com.gys.entity.priceProposal.condition.StoQuickSearchCondition;
import com.gys.entity.priceProposal.dto.*;
import com.gys.entity.priceProposal.entity.ProductPriceProposalD;
import com.gys.entity.priceProposal.entity.ProductPriceProposalH;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;
import com.gys.entity.priceProposal.vo.AllStosVO;
import com.gys.entity.priceProposal.vo.PriceProposalDetailListVO;
import com.gys.entity.priceProposal.vo.PriceProposalListVO;
import com.gys.entity.priceProposal.condition.SelectPriceProposalListCondition;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议mapper接口
 * @CreateTime 2022-01-11 14:51:00
 */
@Mapper
public interface ProductPriceProposalMapper {

    /**
     * 查询市级维度上月品类模型（平均售价）
     * @param provinceId 省份id
     * @param cityId 城市id
     * @return java.util.List<com.gys.entity.priceProposal.dto.CityDimensionDTO>
     */
    List<CityDimensionDTO> selectCityDimensionList(@Param("provinceId") String provinceId,@Param("cityId") String cityId);

    /**
     * 查询门店维度上月品类模型（平均售价、贝叶斯概率）
     * @param provinceId 省份id
     * @param cityId 城市id
     * @return java.util.List<com.gys.entity.priceProposal.dto.StoDimensionDTO>
     */
    List<StoDimensionDTO> selectStoDimensionList(@Param("provinceId") String provinceId, @Param("cityId") String cityId, @Param("clientId") String clientId);

    /**
     * 查询客户维度上月品类模型（平均售价）
     * @param provinceId 省份id
     * @param cityId 城市id
     * @return java.util.List<com.gys.entity.priceProposal.dto.CustomerDimensionDTO>
     */
    List<CustomerDimensionDTO> selectCustomerDimensionList(@Param("provinceId") String provinceId, @Param("cityId") String cityId, @Param("clientId") String clientId);

    /**
     * 查询门店类型1.单体 2.连锁
     * @param clientId 加盟商id
     * @param stoCode 门店编码
     * @return
     */
    String selectStoOrgType(String clientId, String stoCode);

    /**
     * 查询门店维度上月品类模型（平均售价） - 区分门店类型
     * @param clientId 加盟商id
     * @param provinceId 省份id
     * @param cityId 城市id
     * @return java.util.List<com.gys.entity.priceProposal.dto.StoDimensionDTO>
     */
    List<StoDimensionDTO> selectStoDimensionListByOrgType(@Param("provinceId") String provinceId, @Param("cityId") String cityId, @Param("clientId") String clientId);

    /**
     * 查询商品零售价格审批结果
     * @param clientId
     * @param stoCode
     * @param proCode
     * @return java.util.List<com.gys.entity.priceProposal.dto.RetailPriceDTO>
     */
    RetailPriceDTO selectRetailPrice(String clientId, String stoCode, String proCode);

    /**
     * 查询商品价格建议列表
     * @param condition
     * @return java.util.List<com.gys.entity.priceProposal.vo.PriceProposalListVO>
     */
    List<PriceProposalListVO> selectPriceProposalList(SelectPriceProposalListCondition condition);

    /**
     * 查询已调价品项数
     * @param priceProposalNo
     * @return String
     */
    List<Integer> selectAdjustedPriceNum(@Param("priceProposalNo") String priceProposalNo, @Param("clientId") String clientId);

    /**
     * 查询商品价格建议详情列表
     * @param condition
     * @return java.util.List<com.gys.entity.priceProposal.vo.PriceProposalDetailListVO>
     */
    List<PriceProposalDetailListVO> selectPriceProposalDetailList(SelectPriceProposalDetailListCondition condition);

    /**
     * 修改价格建议商品新零售价
     * @param condition
     */
    void updatePriceProposalNewRetailPrice(SavePriceProposalCondition condition);

    /**
     * 查询加盟商下所有门店
     * @param clientId
     * @return java.util.List<com.gys.entity.priceProposal.vo.AllStosVO>
     */
    List<AllStosVO> selectAllStosByClientId(@Param("clientId") String clientId);

    /**
     * 门店快捷选择查询
     * @param condition
     * @return java.util.List<com.gys.entity.priceProposal.vo.AllStosVO>
     */
    List<AllStosVO> stoQuickSearch(StoQuickSearchCondition condition);

    /**
     * 查询商品单位和零售价
     * @param clientId
     * @param proCode
     * @return java.util.Map<String, String>
     */
    Map<String, String> selectUnitAndLsj(@Param("clientId") String clientId, @Param("proCode") String proCode);

    /**
     * 查询价格建议最大编号
     * @return String
     */
    String getMaxNo();

    /**
     * 插入价格建议主表信息
     * @param priceProposalH
     */
    void insertPriceProposalH(ProductPriceProposalH priceProposalH);

    /**
     * 获取最新成本价
     * @param clientId
     * @param proCode
     * @param stoCode
     * @return String
     */
    String getLatestCostPrice(@Param("clientId") String clientId, @Param("stoCode") String stoCode, @Param("proCode") String proCode);

    /**
     * 插入价格建议详情信息
     * @param list
     */
    void batchInsertPriceProposalD(@Param("datas") List<ProductPriceProposalD> list);

    /**
     * 插入价格建议详情信息
     * @param priceProposalD
     */
    void insertPriceProposalD(ProductPriceProposalD priceProposalD);

    /**
     * 插入价格建议中间表信息
     * @param cityProClassifyDTOList
     */
    void batchInsertPriceProposalZ(@Param("datas") List<ProductPriceProposalZ> cityProClassifyDTOList);

    void clearHData();

    void clearDData();

    void clearZData();

    /**
     * 根据city编码查询价格分类区间
     * @param clientId
     * @return java.util.Map
     */
    List<Map<String, String>> selectPriceRangeByClientId(@Param("clientId") String clientId);

    /**
     * 查询所有加盟商
     * @return java.util.List<com.gys.entity.priceProposal.dto.ClientDTO>
     */
    List<PriceProposalClientDTO> selectAllClients(@Param("clientId") String clientId);

    /**
     * 查询商品自编码
     * @param clientId
     * @param proCode
     * @param provinceCode
     * @param cityCode
     * @param stoCode
     * @return String
     */
    List<String> getProSelfCode(@Param("clientId") String clientId, @Param("proCode") String proCode, @Param("provinceCode") String provinceCode, @Param("cityCode") String cityCode);

    /**
     * 根据客户id查询商品
     * @param clientId
     * @return java.util.List
     */
    List<Map<String, String>> selectStosByClientId(String clientId);

    /**
     * 查询客户是否已经计算过价格区间
     * @param clientId
     * @return java.util.List<com.gys.entity.priceProposal.entity.ProductPriceProposalZ>
     */
    List<ProductPriceProposalZ> selectIsRunRangeList(@Param("clientId") String clientId);

    /**
     * 更新价格建议调整状态
     * @param priceProposalNo
     * @param clientNo
     * @param proCode
     */
    void updatePriceProposalAdjustStatus(String priceProposalNo, String clientNo, String proCode);

    /**
     * 查询已调整的条数
     * @param clientId
     * @param priceProposalNo
     * @return
     */
    int checkIsAllAdjust(@Param("clientId") String clientId, @Param("priceProposalNo") String priceProposalNo);

    /**
     * 查询已失效的价格建议单号
     * @param date
     * @return
     */
    List<String> selectInvalidNo(@Param("date") String date);

    /**
     * 更新价格建议状态
     * @param no
     */
    void updatePriceProposalInvalid(@Param("no") String no);

    /**
     * 更新消息提醒状态
     * @param invalidDate
     */
    void updateGSMInfo(@Param("invalidDate") String invalidDate);

    void updateAllGSMInfo();

    List<String> selectProPosition(@Param("proCode") String proCode, @Param("clientId") String clientId);

}

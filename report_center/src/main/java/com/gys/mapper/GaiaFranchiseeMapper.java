package com.gys.mapper;
import com.gys.entity.GaiaFranchisee;
import com.gys.entity.data.xhl.dto.QueryDto;
import com.gys.entity.data.xhl.vo.ClientBaseInfoVo;
import com.gys.entity.data.xhl.vo.ClientVo;
import com.gys.entity.data.xhl.vo.StoreData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaFranchiseeMapper {
    int deleteByPrimaryKey(String client);

    int insert(GaiaFranchisee record);

    int insertSelective(GaiaFranchisee record);

    GaiaFranchisee selectByPrimaryKey(String client);

    int updateByPrimaryKeySelective(GaiaFranchisee record);

    int updateByPrimaryKey(GaiaFranchisee record);

    List<ClientVo> getClients(@Param("client") String client);

    List<ClientBaseInfoVo> getClientBaseInfo();

    int updateAll(List<ClientBaseInfoVo> voList);

    List<StoreData> getAllStores(@Param("dto") List<String> dto);

    List<ClientBaseInfoVo> getClientsInfo(@Param("dto") List<String> dto);
}
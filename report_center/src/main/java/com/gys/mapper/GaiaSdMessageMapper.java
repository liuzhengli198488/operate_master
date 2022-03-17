package com.gys.mapper;

import com.gys.entity.GaiaResource;
import com.gys.entity.GaiaSdMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaSdMessageMapper {

    int updateByPrimaryKeySelective(@Param("record") GaiaSdMessage record);

    List<GaiaSdMessage>listMessage(@Param("clientId") String clientId,@Param("stoCode") String stoCode,@Param("msPlatForm") String msPlatForm);

    List<GaiaResource> selectAuthConfig(@Param("clientId") String clientId, @Param("userId") String userId);

    List<GaiaSdMessage> listMessageDc(@Param("clientId") String clientId,@Param("stoCode") String stoCode,@Param("msPlatForm") String msPlatForm);

    GaiaSdMessage getUnique(GaiaSdMessage inData);

    void updateReadFlag(@Param("client") String client,@Param("proCode") String proCode);

    void updateWeekReadFlag(@Param("client") String client,@Param("weekProCode") String weekProCode);

    List<GaiaSdMessage> listMessageZz(@Param("clientId") String clientId,@Param("msPlatForm") String msPlatForm);

    String selectNextVoucherId(@Param("clientId") String clientId, @Param("type") String type);

    int insertSelective(GaiaSdMessage record);

}
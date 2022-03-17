package com.gys.mapper;

import com.gys.common.data.EmployeesData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Mapper
public interface GaiaGlobalDataMapper {

    String globalType(@Param("client") String client, @Param("stoCode") String stoCode);
    String globalTypeReport(@Param("client") String client, @Param("stoCode") String stoCode,@Param("globalId") String globalId);

    /**
     * 员工首页看板
     * @param client
     * @param userId
     * @param endTime
     * @return
     */
    List<EmployeesData.Employees> getPersonnelData(@Param("client")String client, @Param("userId") String userId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    int getDaysSales(@Param("client")String client, @Param("userId")String userId,@Param("date") String date);


    List<Map<String,String>> getDayCard(@Param("client")String client, @Param("userId")String userId, @Param("startTime")String startTime, @Param("endTime") String endTime);

    List<Map<String, String>> getCostAmtShowConfig(@Param("client") String client);

}

package com.gys.mapper;

import com.gys.common.data.SalesStatisticsReportInData;
import com.gys.common.data.SalesStatisticsReportOutData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SalesStatisticsReportMapper {

    List<SalesStatisticsReportOutData> queryBasicSaleData(@Param("inData") SalesStatisticsReportInData inData);

    List<Map<String, String>> queryProClass();

    List<Map<String, String>> queryProProsition();

    List<Map<String, String>> queryProfitInterval(@Param("inData") SalesStatisticsReportInData inData);

}

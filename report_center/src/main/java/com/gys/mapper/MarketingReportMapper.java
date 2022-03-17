package com.gys.mapper;

import com.gys.entity.data.marketing.MarketingInDate;
import com.gys.entity.data.marketing.MarketingReportListOutData;
import com.gys.entity.data.marketing.MarketingReportOutData;
import com.gys.entity.data.marketing.StartAndEndDayInData;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface MarketingReportMapper {

    String getTotalCount(StartAndEndDayInData inData);

    String getPushCount(StartAndEndDayInData inData);

    String getDevCount(StartAndEndDayInData inData);

    String getStoreCount(StartAndEndDayInData inData);

    String getSaleDayCount(StartAndEndDayInData inData);

    //MarketingReportOutData getMonthReport(StartAndEndDayInData inData);

    MarketingReportListOutData getSaleAmount(@Param("clientId") String clientId, @Param("brId") String brId,
                                             @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<MarketingReportListOutData> getSaleAmountList(StartAndEndDayInData inData);


    MarketingReportOutData getMarketingPlan(MarketingInDate inData);

    List<HashMap<String, Object>> listMarketingStores(MarketingInDate inData);
}

package com.gys.mapper;

import com.gys.entity.data.salesSummary.SalesSummaryData;
import com.gys.report.entity.ProCampaignsOutData;

import java.util.List;

public interface GaiaProCampaignsMapper {
    List<ProCampaignsOutData> selectCampainsProDetails(SalesSummaryData inData);

    List<ProCampaignsOutData>selectCampainsProTotal(SalesSummaryData inData);

}

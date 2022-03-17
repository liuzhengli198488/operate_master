package com.gys.service;
import com.gys.common.data.PageInfo;
import com.gys.entity.data.suggestion.dto.QueryAdjustmentDto;
import com.gys.entity.data.suggestion.dto.UpdateExportDto;
import com.gys.entity.data.suggestion.vo.AdjustmentSummaryVo;

public interface GaiaStoreOutSuggestionHService {
    PageInfo<AdjustmentSummaryVo> getSuggestions(QueryAdjustmentDto dto);

    void updateOutExport(UpdateExportDto dto);
}

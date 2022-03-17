package com.gys.mapper;

import com.gys.entity.GaiaStoreInSuggestionH;
import com.gys.entity.GaiaStoreOutSuggestionH;
import com.gys.entity.data.suggestion.dto.QueryAdjustmentDto;
import com.gys.entity.data.suggestion.dto.UpdateExportDto;
import com.gys.entity.data.suggestion.vo.ActualItemVo;
import com.gys.entity.data.suggestion.vo.AdjustmentBaseInfo;
import com.gys.entity.data.suggestion.vo.CalendarVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaStoreOutSuggestionHMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GaiaStoreOutSuggestionH record);

    int insertSelective(GaiaStoreOutSuggestionH record);

    GaiaStoreOutSuggestionH selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GaiaStoreOutSuggestionH record);

    int updateByPrimaryKey(GaiaStoreOutSuggestionH record);

    List<AdjustmentBaseInfo> getSuggestions(QueryAdjustmentDto dto);

    List<ActualItemVo> queryActualItem(@Param("clientids") List<String> clientids,@Param("inBillCodes") List<String> inBillCodes);

    List<AdjustmentBaseInfo> getWeekSuggestions(QueryAdjustmentDto dto);

    List<ActualItemVo> queryWeekActualItem(@Param("clientids") List<String> clientids,@Param("inBillCodes") List<String> inBillCodes);

    List<AdjustmentBaseInfo> getMonthSuggestions(QueryAdjustmentDto dto);

    List<ActualItemVo> queryMonthActualItem(List<String> clientids, List<String> inBillCodes);

    GaiaStoreOutSuggestionH getOne(UpdateExportDto dto);

    List<CalendarVo> getDateWeek ( @Param("collect") List<String> collect);

    List<AdjustmentBaseInfo> getOriginalSuggestions(QueryAdjustmentDto dto);

    List<AdjustmentBaseInfo> getSuggestionsAll(@Param("clientIds") List<String> clientIds,@Param("codes") List<String> codes);
}
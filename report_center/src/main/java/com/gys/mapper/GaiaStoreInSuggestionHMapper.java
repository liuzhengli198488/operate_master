package com.gys.mapper;

import com.gys.entity.GaiaStoreInSuggestionH;
import com.gys.entity.data.suggestion.dto.UpdateExportDto;

public interface GaiaStoreInSuggestionHMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GaiaStoreInSuggestionH record);

    int insertSelective(GaiaStoreInSuggestionH record);

    GaiaStoreInSuggestionH selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GaiaStoreInSuggestionH record);

    int updateByPrimaryKey(GaiaStoreInSuggestionH record);

    GaiaStoreInSuggestionH getOne(UpdateExportDto dto);
}
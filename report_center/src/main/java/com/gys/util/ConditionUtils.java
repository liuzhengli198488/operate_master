package com.gys.util;

import com.gys.entity.data.dropdata.SearchRangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@Slf4j
public class ConditionUtils {

    //只取第一个起始条件
    public static String getFirstStartCondition(List<SearchRangeDTO> conditionsList) {
        if (CollectionUtils.isNotEmpty(conditionsList)) {
            SearchRangeDTO searchRangeDTO = conditionsList.get(0);
            if (searchRangeDTO != null) {
                String startRange = searchRangeDTO.getStartRange();
                if (StringUtils.isNotBlank(startRange)) {
                    return startRange;
                }
            }
        }
        return null;
    }

    //只取第一个范围条件
    public static SearchRangeDTO getFirstRangeCondition(List<SearchRangeDTO> conditionsList) {
        if (CollectionUtils.isNotEmpty(conditionsList)) {
            SearchRangeDTO searchRangeDTO = conditionsList.get(0);
            if (searchRangeDTO != null
                    && (StringUtils.isNotBlank(searchRangeDTO.getStartRange())
                    || StringUtils.isNotBlank(searchRangeDTO.getEndRange()))) {
                return searchRangeDTO;
            }
        }
        return new SearchRangeDTO();
    }

}

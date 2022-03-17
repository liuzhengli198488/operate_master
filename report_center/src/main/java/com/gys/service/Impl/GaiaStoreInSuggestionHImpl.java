package com.gys.service.Impl;

import com.gys.common.exception.BusinessException;
import com.gys.entity.GaiaStoreInSuggestionH;
import com.gys.entity.GaiaStoreOutSuggestionH;
import com.gys.entity.data.suggestion.dto.UpdateExportDto;
import com.gys.mapper.GaiaStoreInSuggestionHMapper;
import com.gys.service.GaiaStoreInSuggestionHService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.gys.util.Util.isNumbers;

/**
 * @Auther: tzh
 * @Date: 2022/1/17 13:58
 * @Description: GaiaStoreInSuggestionHImpl
 * @Version 1.0.0
 */
@Service
@Slf4j
public class GaiaStoreInSuggestionHImpl implements GaiaStoreInSuggestionHService {
    @Resource
    private GaiaStoreInSuggestionHMapper gaiaStoreInSuggestionHMapper;
    @Override
    public void updateInExport(UpdateExportDto dto) {
        // 判断兼容
        Long id =null;
        boolean numbers = isNumbers(dto.getId());
        if(numbers){
            // 是主键
            id= Long.valueOf(dto.getId());
        }else {
            GaiaStoreInSuggestionH suggestionH= gaiaStoreInSuggestionHMapper.getOne(dto);
            if(suggestionH==null){
                throw new BusinessException("调出调剂不存在");
            }
            id= suggestionH.getId();
        }
        GaiaStoreInSuggestionH gaiaStoreInSuggestionH = gaiaStoreInSuggestionHMapper.selectByPrimaryKey(id);
        if(gaiaStoreInSuggestionH==null){
            throw new BusinessException("调入调剂不存在");
        }
        //确认前 首次导出
        if(gaiaStoreInSuggestionH.getStatus()!=3&&gaiaStoreInSuggestionH.getExportBeforeConfirm()==0){
            gaiaStoreInSuggestionH.setExportBeforeConfirm(1);
            gaiaStoreInSuggestionH.setUpdateTime(new Date());
            gaiaStoreInSuggestionHMapper.updateByPrimaryKeySelective(gaiaStoreInSuggestionH);
        }
        // 确认后 首次导出
        if(gaiaStoreInSuggestionH.getStatus()==3&&gaiaStoreInSuggestionH.getExportAfterConfirm()==0){
            gaiaStoreInSuggestionH.setExportAfterConfirm(1);
            gaiaStoreInSuggestionH.setUpdateTime(new Date());
            gaiaStoreInSuggestionHMapper.updateByPrimaryKeySelective(gaiaStoreInSuggestionH);
        }
    }
}

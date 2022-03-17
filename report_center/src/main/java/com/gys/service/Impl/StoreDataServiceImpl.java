package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.exception.BusinessException;
import com.gys.entity.GaiaStoreData;
import com.gys.entity.InData;
import com.gys.entity.data.InventoryInquiry.InventoryStore;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.service.StoreDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class StoreDataServiceImpl implements StoreDataService {
    @Resource
    private GaiaStoreDataMapper gaiaStoreDataMapper;

    @Override
    public List<InventoryStore> getInventoryStore(InData inData) {
         if (ObjectUtil.isEmpty(inData.getStoArr()) && ObjectUtil.isNotEmpty(inData.getDcCode())) {
            inData.setType("1");
        } else if (ObjectUtil.isNotEmpty(inData.getStoArr()) && ObjectUtil.isEmpty(inData.getDcCode())) {
            inData.setType("2");
        }else if (ObjectUtil.isNotEmpty(inData.getStoArr()) && ObjectUtil.isNotEmpty(inData.getDcCode())) {
            List<String> arr = new ArrayList<>();
             for (int i = 0;i<inData.getStoArr().length; i++){
                 arr.add(inData.getStoArr()[i]);
             }
             arr.add(inData.getDcCode());
//             inData.setStoArr();
         }

        return this.gaiaStoreDataMapper.getInventoryStore(inData);
    }
}

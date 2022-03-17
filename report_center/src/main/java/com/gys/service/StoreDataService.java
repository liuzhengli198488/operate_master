package com.gys.service;


import com.gys.entity.GaiaStoreData;
import com.gys.entity.InData;
import com.gys.entity.data.InventoryInquiry.InventoryStore;

import java.util.List;

public interface StoreDataService {

    List<InventoryStore> getInventoryStore(InData inData);
}

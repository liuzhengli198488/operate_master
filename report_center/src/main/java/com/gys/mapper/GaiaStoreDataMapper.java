package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaStoreData;
import com.gys.entity.InData;
import com.gys.entity.data.InventoryInquiry.InventoryStore;
import com.gys.entity.data.productMatch.StoreInfo;
import com.gys.entity.data.xhl.vo.StoreData;
import com.gys.report.entity.InventoryChangeCheckInData;
import com.gys.report.entity.StoClassOutData;
import com.gys.report.entity.StoreOutData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface GaiaStoreDataMapper extends BaseMapper<GaiaStoreData> {
    List<InventoryStore> getInventoryStore(InData inData);

    String selectStoPriceComparison(@Param("clientId") String client, @Param("stoCode") String stoCode, @Param("paramStr") String paramStr);

    List<StoreInfo> selectStoreAndDcCodes(@Param("clientId") String client);

    List<StoreInfo> selectStores(@Param("clientId") String client);

    List<Map<String,String>>selectAreaNameByLevel();

    List<Map<String,String>>selectClientName();

    List<Map<String,String>> selectClientList(@Param("content") String content);
    StoreOutData getStoreData(@Param("client") String client, @Param("brId") String brId);

    List<StoreData> getStores(@Param("store") String store);

    List<StoClassOutData> getStoreCodeByStoClass(InventoryChangeCheckInData inData);
}
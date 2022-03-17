package com.gys.entity.data.xhl.vo;

import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/15 21:52
 * @Description: ClientStoreVo
 * @Version 1.0.0
 */
@Data
public class ClientStoreVo {
    private List<ClientVo> clientVoList;
    private List<StoreData> storeData;
 }

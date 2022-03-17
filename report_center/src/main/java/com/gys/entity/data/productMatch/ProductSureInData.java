package com.gys.entity.data.productMatch;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductSureInData {
    private String clientId;
    private String matchBatch;
    private String[] stoCodes;
    private List<Map<String,String>> itemList;

    /**
     * 修改加盟商
     */
    private String updateClient;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private String updateTime;
}

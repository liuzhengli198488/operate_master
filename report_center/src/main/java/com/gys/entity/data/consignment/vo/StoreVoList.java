package com.gys.entity.data.consignment.vo;

import lombok.Data;

import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/8 17:20
 * @Description: StoreVoList
 * @Version 1.0.0
 */
@Data
public class StoreVoList {
    private List<StoreVo> recommendList;
    private List<StoreVo> saleList;
}

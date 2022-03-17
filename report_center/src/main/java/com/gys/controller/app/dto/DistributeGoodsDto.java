package com.gys.controller.app.dto;

import lombok.Data;

/**
 * @Auther: tzh
 * @Date: 2021/11/15 10:20
 * @Description: DistributeGoods
 * @Version 1.0.0
 */
@Data
public class DistributeGoodsDto {
    // 1 是全部  0 是 排除主动铺货  默认是0
    private  Integer tag;
}

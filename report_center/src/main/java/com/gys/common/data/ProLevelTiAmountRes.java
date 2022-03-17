package com.gys.common.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @author: flynn
 * @date: 2021年12月30日 下午5:07
 */
@Data
public class ProLevelTiAmountRes {
    //是否需要直接返回标识为，false表示不需要，true表示需要
    private boolean resFlag;

    private BigDecimal tiTotal;
}


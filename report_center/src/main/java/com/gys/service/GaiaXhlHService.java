package com.gys.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/9/1 15:01
 */
public interface GaiaXhlHService {

    void saveStoreXhl(String client, Date tjDate, Map<String, BigDecimal> industryAverage);

}

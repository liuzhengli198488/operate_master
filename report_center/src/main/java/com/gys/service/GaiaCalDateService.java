package com.gys.service;

import com.gys.entity.GaiaCalDate;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/12/29 15:10
 */
public interface GaiaCalDateService {

    GaiaCalDate getByDate(String date);

}

package com.gys.service.Impl;

import com.gys.entity.GaiaCalDate;
import com.gys.mapper.GaiaCalDateMapper;
import com.gys.service.GaiaCalDateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/12/29 15:11
 */
@Service
public class GaiaCalDateServiceImpl implements GaiaCalDateService {
    @Resource
    private GaiaCalDateMapper gaiaCalDateMapper;

    @Override
    public GaiaCalDate getByDate(String date) {
        return gaiaCalDateMapper.getByDate(date);
    }
}

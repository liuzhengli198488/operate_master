package com.gys.report.service.impl;

import com.gys.mapper.GaiaSdPaymentMethodMapper;
import com.gys.report.entity.*;
import com.gys.report.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
@SuppressWarnings("all")
public class PayServiceImpl implements PayService {
    @Resource
    private GaiaSdPaymentMethodMapper paymentMethodMapper;

    @Override
    public List<GetPayTypeOutData> payTypeListByClient(GetPayInData inData) {
        return this.paymentMethodMapper.payTypeListByClient(inData);
    }
}

package com.gys.service;

import com.gys.entity.PayClientDetail;
import com.gys.entity.PayClientInData;
import com.gys.entity.PayClientOutData;

import java.util.List;
import java.util.Map;

public interface PayClientService {

    Map<String,Object> selectPayClientList(PayClientInData inData);

    Map<String,Object> selectPayClientDetail(PayClientInData inData);

    List<PayClientOutData> selectPayClientListByCSV(PayClientInData inData);

    List<PayClientDetail> selectPayClientDetailByCSV(PayClientInData inData);

    List<Map<String,String>> selectClientList(String clientId);
}

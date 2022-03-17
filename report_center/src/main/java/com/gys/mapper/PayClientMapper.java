package com.gys.mapper;

import com.gys.entity.PayClientDetail;
import com.gys.entity.PayClientInData;
import com.gys.entity.PayClientOutData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PayClientMapper {
    List<PayClientOutData> payClientList(PayClientInData inData);

    PayClientOutData payClientTotal(PayClientInData inData);

    List<PayClientDetail>selectPayDetailList(PayClientInData inData);

    PayClientDetail selectPayDetailTotal(PayClientInData inData);

    List<Map<String,String>>selectClientList(@Param("clientId") String clientId);
}

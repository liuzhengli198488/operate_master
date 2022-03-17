package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.entity.PayClientDetail;
import com.gys.entity.PayClientInData;
import com.gys.entity.PayClientOutData;
import com.gys.mapper.PayClientMapper;
import com.gys.service.PayClientService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayClientServiceImpl implements PayClientService {
    @Resource
    private PayClientMapper payClientMapper;

    @Override
    public Map<String, Object> selectPayClientList(PayClientInData inData) {
        Map<String, Object> result = new HashMap<>();
        //合计付款金额
        PayClientOutData totalOutData = payClientMapper.payClientTotal(inData);
        if (ObjectUtil.isNotEmpty(totalOutData)){
            result.put("totalOutData",totalOutData);
        }
        //分页查询用户付款信息
        PageInfo<PayClientOutData> pageInfo = null;
        PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        List<PayClientOutData>  outData = payClientMapper.payClientList(inData);
        if (ObjectUtil.isNotEmpty(outData)) {
            pageInfo = new PageInfo(outData);
        } else {
            pageInfo = new PageInfo();
        }
        result.put("itemOutData",pageInfo);
        return result;
    }

    @Override
    public Map<String, Object> selectPayClientDetail(PayClientInData inData) {
        Map<String, Object> result = new HashMap<>();
        //合计付款金额
        PayClientDetail totalOutData = payClientMapper.selectPayDetailTotal(inData);
        if (ObjectUtil.isNotEmpty(totalOutData)){
            result.put("totalOutData",totalOutData);
        }
        //用户付款明细列表信息
        List<PayClientDetail>  outData = payClientMapper.selectPayDetailList(inData);
        result.put("itemOutData",outData);
        return result;
    }

    @Override
    public List<PayClientOutData> selectPayClientListByCSV(PayClientInData inData) {
        return payClientMapper.payClientList(inData);
    }

    @Override
    public List<PayClientDetail> selectPayClientDetailByCSV(PayClientInData inData) {
        return payClientMapper.selectPayDetailList(inData);
    }

    @Override
    public List<Map<String, String>> selectClientList(String clientId) {
        return payClientMapper.selectClientList(clientId);
    }
}

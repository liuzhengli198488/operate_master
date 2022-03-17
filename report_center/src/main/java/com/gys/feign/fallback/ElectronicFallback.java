package com.gys.feign.fallback;

import com.alibaba.fastjson.JSONObject;
import com.gys.feign.ElectronicService;
import org.springframework.stereotype.Component;

@Component
public class ElectronicFallback implements ElectronicService {
    @Override
    public String addRegisterElectron(String client, String cardNumber) {
        JSONObject json = new JSONObject();
        json.put("code", "-1");
        json.put("message", "调用接口失败,member.addRegisterElectron()");
        return json.toJSONString();
    }
}

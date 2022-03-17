package com.gys.feign;

import com.gys.common.config.FeignServiceConfiguration;
import com.gys.feign.fallback.ElectronicFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "gys-mobile",
        configuration= FeignServiceConfiguration.class,
        fallback = ElectronicFallback.class
//        url = "http://172.19.1.121:10108"
)
public interface ElectronicService {
    /**
     * 线下门店 会员注册送券
     * @param client 加盟商号
     * @param cardNumber 会员卡号
     * @return
     */
    @GetMapping({"/member/addRegisterElectron"})
    String addRegisterElectron(@RequestParam String client,@RequestParam String cardNumber);
}

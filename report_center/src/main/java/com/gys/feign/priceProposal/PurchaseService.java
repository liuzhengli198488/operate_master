//package com.gys.feign.priceProposal;
//
//import com.gys.common.config.FeignServiceConfiguration;
//import com.gys.entity.priceProposal.dto.RetailPriceInfoSaveRequestDto;
//import com.gys.feign.fallback.PurchaseFallback;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.util.List;
//
///**
// * @Author Jinwencheng
// * @Version 1.0
// * @Description 调用调价接口
// * @CreateTime 2022-01-13 19:49:00
// */
//@FeignClient(value = "gys-purchase", configuration = FeignServiceConfiguration.class, fallback = PurchaseFallback.class
//        , url = "http://172.19.1.120:10108"
//)
//public interface PurchaseService {
//
//    @PostMapping({"/retailPrice/saveRetailPriceInfo"})
//    Result saveRetailPriceInfo(@RequestBody RetailPriceInfoSaveRequestDto dto);
//
//}

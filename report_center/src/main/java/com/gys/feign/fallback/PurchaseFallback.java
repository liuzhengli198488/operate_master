//package com.gys.feign.fallback;
//
//import com.gys.entity.priceProposal.dto.RetailPriceInfoSaveRequestDto;
//import com.gys.feign.priceProposal.PurchaseService;
//import com.gys.feign.priceProposal.Result;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PurchaseFallback implements PurchaseService {
//
//    @Override
//    public Result saveRetailPriceInfo(RetailPriceInfoSaveRequestDto dto) {
//        Result result = new Result();
//        result.setCode("-1");
//        result.setMessage("调用接口失败, retailPrice.saveRetailPriceInfo");
//        return result;
//    }
//}

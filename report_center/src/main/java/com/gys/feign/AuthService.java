package com.gys.feign;

import com.gys.common.config.FeignServiceConfiguration;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.feign.vo.UserRestrictInfo;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        value = "gys-auth",
        configuration= FeignServiceConfiguration.class
//                ,url = "http://127.0.0.1:10103"
)
public interface AuthService {
    @PostMapping({"/store/getRestrictStoreListWithType"})
    @Headers("Content-Type: application/json")
    UserRestrictInfo getRestrictStoreList(@RequestBody GetLoginOutData data);
}

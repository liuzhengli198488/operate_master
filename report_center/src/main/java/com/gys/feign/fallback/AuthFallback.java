//package com.gys.feign.fallback;
//
//import com.alibaba.fastjson.JSONObject;
//import com.gys.common.data.GetLoginOutData;
//import com.gys.feign.AuthService;
//import com.gys.feign.vo.UserRestrictInfo;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuthFallback implements AuthService {
//
//
//    @Override
//    public UserRestrictInfo getRestrictStoreList(GetLoginOutData data) {
//        JSONObject json = new JSONObject();
//        json.put("code", "-1");
//        json.put("message", "调用接口失败,获取用户数据权限失败");
//        return json.toJSONString();
//    }
//}

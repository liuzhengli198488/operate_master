package com.gys.service.Impl;

import com.gys.common.enums.SerialCodeTypeEnum;
import com.gys.common.redis.RedisManager;
import com.gys.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Auther: tzh
 * @Date: 2021/12/31 15:43
 * @Description: CommonServiceImpl
 * @Version 1.0.0
 */
@Slf4j
@Service("commonService")
public class CommonServiceImpl  implements CommonService {
    @Autowired
    private RedisManager redisManager;

    @Override
    public String getSerialCode(String client, SerialCodeTypeEnum codeTypeEnum) {
        String prefix = codeTypeEnum.getCodePrefix();
        String key = String.format("%s:%s", client, prefix);
        Long value = redisManager.incrBy(key, 1);
        String fixedZero = codeTypeEnum.fillZero + value;
        return String.format("%s%s", prefix, fixedZero.substring(fixedZero.length() - codeTypeEnum.fillZero.length()));
    }

    @Override
    public String getSdSerialCode(String client, String stoCode, SerialCodeTypeEnum codeTypeEnum) {
        String prefix = codeTypeEnum.getCodePrefix();
        String key = String.format("%s:%s:%s", client, stoCode, prefix);
        Long value = redisManager.incrBy(key, 1);
        String fixedZero = codeTypeEnum.fillZero + value;
        return String.format("%s%s", prefix, fixedZero.substring(fixedZero.length() - codeTypeEnum.fillZero.length()));
    }
}

package com.gys.common.config;

import cn.hutool.core.util.ObjectUtil;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignServiceConfiguration implements RequestInterceptor {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(ObjectUtil.isNotEmpty(requestAttributes)){
            HttpServletRequest request = requestAttributes.getRequest();
            String token = request.getHeader("X-Token");
            requestTemplate.header("X-Token", token);
        }
    }

}

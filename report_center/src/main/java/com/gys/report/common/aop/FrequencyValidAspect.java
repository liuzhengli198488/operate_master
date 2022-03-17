package com.gys.report.common.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.redis.RedisManager;
import com.gys.report.common.annotation.FrequencyValid;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/***
 * @desc: 防重复提交切面类
 * @author: ryan
 * @createTime: 2021/6/22 19:09
 **/
@Slf4j
@Aspect
@Component
public class FrequencyValidAspect {
    @Autowired
    private RedisManager redisManager;

    @Around("@annotation(com.gys.report.common.annotation.FrequencyValid)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        FrequencyValid annotation = targetMethod.getAnnotation(FrequencyValid.class);
        String key = getFrequencyKey(methodSignature.getDeclaringTypeName(), methodSignature.getName());

        Boolean aBoolean = redisManager.setNx(key, "true", annotation.value());
        if (aBoolean != null && aBoolean) {
            Object object = proceedingJoinPoint.proceed();
            return object;
        } else {
            log.error(String.format("<频率验证><重复提交><%d秒内重复提交:%s:%s>", annotation.value(), methodSignature.getDeclaringTypeName(), methodSignature.getName()));
            return JsonResult.error(1000, "您操作的太频繁了，请稍后再试！");
        }
    }

    private String getFrequencyKey(String declaringTypeName, String name) {
        HttpServletRequest request = getRequest();
        String token = request.getHeader("X-Token");
        if (StringUtil.isNotEmpty(token)) {
            String userInfo = (String)this.redisManager.get(token);
            JSONObject jsonObject = JSON.parseObject(userInfo);
            GetLoginOutData data = JSONObject.toJavaObject(jsonObject, GetLoginOutData.class);
            return String.format("operation:%s:%s:%s:%s", declaringTypeName, name, data.getClient(), data.getUserId());
        }
        return request.getSession().getId();

    }

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        return sra != null ? sra.getRequest() : null;
    }
}

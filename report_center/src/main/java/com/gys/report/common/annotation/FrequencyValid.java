package com.gys.report.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * @desc: 接口访问频次验证、防重复提交注解
 * @author: ryan
 * @createTime: 2021/6/22 19:21
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FrequencyValid {

    String name() default "";

    long value() default 5;

}

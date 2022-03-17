package com.gys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author ：gyx
 * @Date ：Created in 22:59 2022/1/15
 * @Description：
 * @Modified By：gyx
 * @Version:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartDataField {
}

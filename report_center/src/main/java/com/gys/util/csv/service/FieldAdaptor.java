package com.gys.util.csv.service;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/4/28 14:24
 * @Version 1.0.0
 **/
@FunctionalInterface
public interface FieldAdaptor {
    /**
     * 处理字段
     * @param <T> 字段值类型
     * @param t 字段值
     * @return 返回字段值的字符串类型
     */
    <T> String process(T t);
}

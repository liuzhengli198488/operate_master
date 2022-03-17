package com.gys.util.csv.annotation;



import com.gys.util.csv.service.FieldAdaptor;
import com.gys.util.csv.service.impl.DefaultFieldAdaptorImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 字段注释，代表标题及注释
 * @Author huxinxin
 * @Date 2021/4/28 14:22
 * @Version 1.0.0
 **/

@Target(FIELD)
@Retention(RUNTIME)
public @interface CsvCell {
    /**
     * 列名
     * @return 名称
     */
    String title() default "";

    /**
     * 所在位置顺序，用于排序
     * @return 索引
     */
    short index() default 0;

    /**
     * 该字段的数字编号，用于字段的自定义导出
     * @return 字段编号
     */
    short fieldNo() default 0;

    /**
     * 字段值自定义处理器
     */
    Class<? extends FieldAdaptor> valueAdaptor() default DefaultFieldAdaptorImpl.class;
}

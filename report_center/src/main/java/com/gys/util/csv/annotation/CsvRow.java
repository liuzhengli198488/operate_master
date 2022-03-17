package com.gys.util.csv.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/4/28 14:19
 * @Version 1.0.0
 **/

@Target(TYPE)
@Retention(RUNTIME)
public @interface CsvRow {

        /**
         * 给出csv的文件名
         * @return 文件名
         */
        String value() default "";
}

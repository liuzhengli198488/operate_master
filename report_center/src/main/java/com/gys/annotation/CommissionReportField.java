package com.gys.annotation;

import java.lang.annotation.*;

/**
 * @author wu mao yin
 * @Title: 门店。员工提成汇总报表导出字段
 * @date 2021/11/2515:58
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CommissionReportField {

    /**
     * 字段名
     */
    String fieldName();

    /**
     * 报表类型 默认1 门店提成汇总报表
     */
    int type() default 1;

    /**
     * 是否子方案
     */
    boolean showSubPlan() default false;

    /**
     * 是否展示门店
     */
    boolean showStore() default false;

    /**
     * 是否展示销售日期
     */
    boolean showSaleDate() default false;

    /**
     * 后缀
     */
    String suffix() default "";

    /**
     * 是否合计项
     */
    boolean isTotalItem() default false;

}


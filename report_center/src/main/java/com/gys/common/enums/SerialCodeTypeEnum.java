package com.gys.common.enums;
import com.gys.util.DateUtil;
import java.util.Date;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/11/29 19:05
 */
public enum SerialCodeTypeEnum {

    ORDER_CODE("ORDER_CODE", DateTypeEnum.DAY, "000000","his订单流水号生成规则、按照月份重置"),

    MEDICINAL_QUALITY("MEDICINAL_QUALITY", DateTypeEnum.DAY, "000000","药品质量流水号生成规则、按照月份重置"),
    QUALITY_MANAGEMENT("QUALITY_MANAGEMENT", DateTypeEnum.DAY, "000000","质量管理体系审核报告流水号生成规则、按照月份重置"),
    EMPLOYEE_EDUCATIONAL("EMPLOYEE_EDUCATIONAL", DateTypeEnum.DAY, "000000","员工继续教育档案流水号生成规则、按照月份重置"),
    EMPLOYEE_HEALTH("EMPLOYEE_HEALTH", DateTypeEnum.DAY, "000000","his订单流水号生成规则、按照月份重置"),
    QUALITY_REVIEW_CODE("QUALITY_REVIEW_CODE", DateTypeEnum.MONTH, "00000", "质量体系内部评审记录表单号、按照月份重置")
    ;

    public final String type;
    public final DateTypeEnum dateTypeEnum;
    public final String fillZero;
    public final String name;

    SerialCodeTypeEnum(String type, DateTypeEnum dateTypeEnum, String fillZero, String name) {
        this.type = type;
        this.dateTypeEnum = dateTypeEnum;
        this.fillZero = fillZero;
        this.name = name;
    }

    public String getCodePrefix() {
        Date date = new Date();
        switch (this.dateTypeEnum) {
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return DateUtil.formatDate(date, this.dateTypeEnum.format);
            default:
                return "";
        }
    }

    public enum DateTypeEnum {
        YEAR(1, "yyyy", "年（每年重置流水号）"),
        MONTH(2, "yyyyMM", "月（每月重置流水号）"),
        DAY(3, "yyyyMMdd", "日（每日重置流水号）"),
        HOUR(4, "yyyyMMddHH", "小时（每小时重置流水号）"),
        MINUTE(5, "yyyyMMddHHmm", "分钟（每分钟重置流水号）"),
        SECOND(6, "yyyyMMddHHmmss", "秒（每秒重置流水号）"),;

        public final Integer type;
        public final String format;
        public final String name;

        DateTypeEnum(Integer type, String format, String name) {
            this.type = type;
            this.format = format;
            this.name = name;
        }
    }

}

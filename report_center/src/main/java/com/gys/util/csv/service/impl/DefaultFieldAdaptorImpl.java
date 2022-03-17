package com.gys.util.csv.service.impl;

import com.gys.util.csv.CsvClient;
import com.gys.util.csv.service.FieldAdaptor;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/4/28 14:23
 * @Version 1.0.0
 **/
public class DefaultFieldAdaptorImpl implements FieldAdaptor {
    @Override
    public <T> String process(T t) {
        String s;
        if (t instanceof String) {
            s = (String) t;
        } else {
            // 时间类型默认转格式
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            if (t instanceof Date) {
                s = new SimpleDateFormat(pattern).format(t);
            } else if (t instanceof LocalDateTime) {
                s = ((LocalDateTime) t).format(DateTimeFormatter.ofPattern(pattern));
            } else {
                s = Objects.toString(t, "");
            }
        }
        s = Objects.toString(s, "");

        // 校验是否全为数字，默认前面添加单引号以在Excel中正确显示
        if (StringUtils.isNotBlank(s) && s.length() > CsvClient.MAX_EXCEL_SHOW_BITS) {
            final String regex = CsvClient.NUMBER_EXPRESSION;
            if (s.matches(regex)) {
                s = "'" + s;
            }
        }
        return s;
    }
}

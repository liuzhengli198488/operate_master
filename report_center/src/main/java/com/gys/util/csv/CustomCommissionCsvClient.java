package com.gys.util.csv;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.constant.CommonConstant;
import com.gys.util.Operation;
import com.gys.util.csv.annotation.CsvCell;
import com.gys.util.csv.annotation.CsvRow;
import com.gys.util.csv.dto.CsvFileInfo;
import com.gys.util.csv.service.FieldAdaptor;
import com.gys.util.csv.service.impl.DefaultFieldAdaptorImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 生成csv文件
 * @Author huxinxin
 * @Date 2021/4/28 14:17
 * @Version 1.0.0
 **/
public class CustomCommissionCsvClient extends CsvClient {

    private static Logger log = LoggerFactory.getLogger(CustomCommissionCsvClient.class);

    /**
     * 获取csv文件流
     *
     * @param ts       数据源
     * @param fieldNos 数据源中需要筛选的字段编号
     * @param <T>      数据源类型
     * @return csv文件流
     */
    public static <T> CsvFileInfo getCsvByte(List<T> ts, String fileName, List<Short> fieldNos, boolean notAdmin) {
        if (ts == null || ts.isEmpty()) {
            log.warn("csv导出失败，无数据导出！");
            return null;
        } else if (ts.size() > MAX_ROW_NUMBER) {
            log.warn("csv导出失败，数据量太大（最大五十万条，实际{}条）！", ts.size());
            return null;
        }
        // 对象
        final Class<?> tClazz = ts.get(0).getClass();
        // 扩展名
        String ext = ".csv";
        // 文件全称
        String fileExtName;
        if (tClazz.isAnnotationPresent(CsvRow.class)) {
            fileName = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            fileExtName = fileName + ext;
        } else {
            throw new IllegalArgumentException("类上缺少@CsvRow注释");
        }

        // 获取字段值
        final List<Field> fields = getFields(fieldNos, tClazz);
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("字段上缺少@CsvCell注释");
        }

        final StringJoiner csvFile = new StringJoiner("\r\n");
        // csv数据标题
        final StringJoiner titleRow = new StringJoiner(",");
        for (Field field : fields) {
            if (notAdmin && CommonConstant.COMMISSION_NOT_SHOW_FIELD.contains(field.getName())) {
                continue;
            }
            titleRow.add(field.getDeclaredAnnotation(CsvCell.class).title());
        }
        csvFile.merge(titleRow);

        // 生成csv格式的数据
        for (T t : ts) {
            final StringJoiner row = new StringJoiner(",");
            for (Field field : fields) {
                if (notAdmin && CommonConstant.COMMISSION_NOT_SHOW_FIELD.contains(field.getName())) {
                    continue;
                }
                field.setAccessible(true);
                row.add(getFieldValue(t, field));
            }
            csvFile.merge(row);
        }

        final byte[] b = addStringEncode(csvFile.toString().getBytes(StandardCharsets.UTF_8));

        // 赋值文件信息
        return new CsvFileInfo(b, b.length, fileExtName);
    }

    /**
     * 获取对象的字段值
     *
     * @param <T>   t 数据源对象
     * @param field 对象的字段信息
     * @return 字段对应的值
     */
    protected static <T> String getFieldValue(T t, Field field) {
        try {
            Object object = field.get(t);
            if (object instanceof String) {
                return symbolManipulation("\t" + object.toString());
            }
            if (ObjectUtil.isNotEmpty(object)) {
                return symbolManipulation(object.toString());
            } else {
                return "";
            }
        } catch (IllegalAccessException e) {
            log.error("字段获取错误！", e);
            return "";
        }
    }

}
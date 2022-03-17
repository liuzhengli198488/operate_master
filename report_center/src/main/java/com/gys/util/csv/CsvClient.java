package com.gys.util.csv;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
public class CsvClient {
    private static Logger log = LoggerFactory.getLogger(CsvClient.class);

    /**
     * csv导出最大行数
     */
    public static final int MAX_ROW_NUMBER = 500_000;

    /**
     * 每批条数
     */
    public static final Integer BATCH_SIZE = 1000;

    /**
     * 数据库一次输出最大条数
     */
    public static final int MAX_SQL_NUMBER = 500_000;

    /**
     * Excel正确显示数字的最大位数
     */
    public static final int MAX_EXCEL_SHOW_BITS = 2;

    /**
     * 纯数字正则表达式
     */
    public static final String NUMBER_EXPRESSION = "^[0-9]*$";

    /**
     * 获取csv文件流
     *
     * @param ts  数据源
     * @param <T> 数据源类型
     * @return csv文件流
     */
//    public static <T> CsvFileInfo getCsvByte(List<T> ts,String fileName) {
//        return getCsvByte(ts, null,fileName);
//    }

    /**
     * 获取csv文件信息
     *
     * @param <T>      ts 数据源
     * @param fileName 文件名
     * @param titleMap 标题名
     * @return 编码后字符串
     */
    public static <T> CsvFileInfo getCsvByteByMap(List<LinkedHashMap<String, T>> ts, String fileName, Map<String, String> titleMap) {
        return doExportByMap(ts, fileName, titleMap);
    }

    /**
     * 获取csv文件流
     *
     * @param ts       数据源
     * @param fieldNos 数据源中需要筛选的字段编号
     * @param <T>      数据源类型
     * @return csv文件流
     */
    public static <T> CsvFileInfo getCsvByte(List<T> ts, String fileName, List<Short> fieldNos) {
        if (ts == null || ts.isEmpty()) {
            log.warn("csv导出失败，无数据导出！");
            return null;
        } else if (ts.size() > MAX_ROW_NUMBER) {
            log.warn("csv导出失败，数据量太大（最大五十万条，实际{}条）！", ts.size());
            return null;
        }

        // 对象
        final Class<?> tClazz = ts.get(0).getClass();

//        // 获取文件名
//        String fileName;
        // 扩展名
        String ext = ".csv";
        // 文件全称
        String fileExtName;
        if (tClazz.isAnnotationPresent(CsvRow.class)) {
            final CsvRow csvRow = tClazz.getDeclaredAnnotation(CsvRow.class);
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
            titleRow.add(field.getDeclaredAnnotation(CsvCell.class).title());
        }
        csvFile.merge(titleRow);

        // 生成csv格式的数据
        for (T t : ts) {
            final StringJoiner row = new StringJoiner(",");
            for (Field field : fields) {
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
     * 获取对象中的字段值，可筛选对象中的字段（需要使用@CsvCell注解，指定字段编号）
     *
     * @param fieldNos 字段编号
     * @param tClazz   对象类
     * @return 筛选后字段
     */
    protected static List<Field> getFields(List<Short> fieldNos, Class<?> tClazz) {
        final Field[] declaredFields = tClazz.getDeclaredFields();
        final List<Field> fields;
        // 字段全部显示
        if (fieldNos == null || fieldNos.isEmpty()) {
            fields = Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(CsvCell.class))
                    .sorted(Comparator.comparingInt(o -> o.getDeclaredAnnotation(CsvCell.class).index()))
                    .collect(Collectors.toList());

            // 自定义显示的字段
        } else {
            fields = Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(CsvCell.class) && fieldNos.contains(field.getDeclaredAnnotation(CsvCell.class).fieldNo()))
                    .sorted(Comparator.comparingInt(o -> o.getDeclaredAnnotation(CsvCell.class).index()))
                    .collect(Collectors.toList());
        }
        return fields;
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
            final CsvCell csvCell = field.getDeclaredAnnotation(CsvCell.class);
//            final FieldAdaptor fieldAdaptor = csvCell.valueAdaptor().newInstance();
//            System.out.println( field +": " + field.get(t) +": " + field.get(t).getClass().getName());
            Object object = field.get(t);
            if (object instanceof String) {
                return symbolManipulation("\t" + object.toString());
            }
            if (ObjectUtil.isNotEmpty(object)) {
                return symbolManipulation(object.toString());
            } else {
                return "";
            }
//            String value = fieldAdaptor.process(field.get(t));
            // csv都转成字符串格式
        } catch (IllegalAccessException e) {
            log.error("字段获取错误！", e);
            return "";
        }
    }

    /**
     * 字段值中的格式处理
     *
     * @param value 字段值
     * @return 处理后的格式
     */
    protected static String symbolManipulation(String value) {
        final String s = "\"";
        final String d = ",";
        final boolean b = value.contains(s);
        // 有双引号时要替换成两个
        if (b) {
            final String s1 = "\"\"";
            value = value.replace(s, s1);
        }

        // 有英文逗号、双引号时要两边加双引号
        if (b || value.contains(d)) {
            value = s + value + s;
        }
        return value;
    }

    /**
     * 针对Map对象的导出
     *
     * @param ts       数据源
     * @param fileName 文件名
     * @param titleMap 标题信息
     * @param <T>      只支持进本类型，（Number、String、Date、..）
     * @return CSV的文件信息
     */
    private static <T> CsvFileInfo doExportByMap(List<LinkedHashMap<String, T>> ts, String fileName, Map<String, String> titleMap) {
        Asserts.notNull(titleMap, "Title");
        fileName = Objects.toString(fileName, "");
        if (CollectionUtils.isEmpty(ts)) {
            log.warn("csv导出失败，无数据导出！");
            return null;
        } else if (ts.size() > MAX_ROW_NUMBER) {
            log.warn("csv导出失败，数据量太大（最大五十万条，实际{}条）！", ts.size());
            return null;
        }

        // 扩展名
        String ext = ".csv";
        // 文件名
        fileName += new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // 文件全称
        String fileExtName = fileName + ext;

        // csv文件内容
        final StringJoiner csvFile = new StringJoiner("\r\n");

        // csv数据标题
        // key值
        final Set<String> keys = titleMap.keySet();
        final StringJoiner titleRow = new StringJoiner(",");
        for (String field : keys) {
            titleRow.add(titleMap.get(field));
        }
        csvFile.merge(titleRow);

        // 生成csv格式的数据
        FieldAdaptor adaptor = new DefaultFieldAdaptorImpl();

        for (Map<String, T> t : ts) {
            final StringJoiner row = new StringJoiner(",");
            for (String titleKey : titleMap.keySet()) {
                row.add(symbolManipulation(adaptor.process(t.get(titleKey) == null ? "" : t.get(titleKey))));
            }
            csvFile.merge(row);
        }
        // 文件的byte信息，*******20200708更正*******
        final byte[] b = addStringEncode(csvFile.toString().getBytes(StandardCharsets.UTF_8));

        // 设置文件格式
        return new CsvFileInfo(b, b.length, fileExtName);
    }

    /**
     * 添加字符串编码，防止中文乱码
     * 20200708更正
     *
     * @param b 字符串流
     * @return 编码后
     */
    protected static byte[] addStringEncode(byte[] b) {
        byte[] bytes = new byte[b.length + 3];
//        bytes[0] = (byte) 0xEF;
//        bytes[1] = (byte) 0xBB;
//        bytes[2] = (byte) 0xBF;
        System.arraycopy(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}, 0, bytes, 0, 3);
        System.arraycopy(b, 0, bytes, 3, b.length);
        return bytes;
    }


    /**
     * 设置下周响应头属性
     *
     * @param response response
     * @param fileName fileName
     */
    public static void setAttr(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        response.reset();
        response.setContentType("application/csv;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".csv", "UTF-8"));
    }

    public static PrintWriter renderTitle(List<String> titles, String fileName, HttpServletResponse response) throws IOException {
        setAttr(response, fileName);
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        // 写入标题头
        StringJoiner stringJoiner = new StringJoiner(StrUtil.COMMA);
        for (String title : titles) {
            stringJoiner.add(title);
        }
        stringJoiner.add("\r\n");
        writer.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
        writer.write(stringJoiner.toString());
        return writer;
    }

    public static PrintWriter renderTitle(List<String> titles, ByteArrayOutputStream bos) {
        PrintWriter writer = new PrintWriter(bos);
        // 写入标题头
        StringJoiner stringJoiner = new StringJoiner(StrUtil.COMMA);
        for (String title : titles) {
            stringJoiner.add(title);
        }
        stringJoiner.add("\r\n");
        writer.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
        writer.write(stringJoiner.toString());
        return writer;
    }


    public static <T> CsvFileInfo getCsvByteWithSupperClass(List<T> ts,String fileName, List<Short> fieldNos) {
        if (ts == null || ts.isEmpty()) {
            log.warn("csv导出失败，无数据导出！");
            return null;
        } else if (ts.size() > MAX_ROW_NUMBER) {
            log.warn("csv导出失败，数据量太大（最大五十万条，实际{}条）！", ts.size());
            return null;
        }

        // 对象
        final Class<?> tClazz = ts.get(0).getClass();
        final Class<?> supClazz = ts.get(0).getClass().getSuperclass();
//        // 获取文件名
//        String fileName;
        // 扩展名
        String ext = ".csv";
        // 文件全称
        String fileExtName;
        if (tClazz.isAnnotationPresent(CsvRow.class)) {
            final CsvRow csvRow = tClazz.getDeclaredAnnotation(CsvRow.class);
            fileName = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            fileExtName = fileName + ext;
        } else {
            throw new IllegalArgumentException("类上缺少@CsvRow注释");
        }

        // 获取字段值
        List<Field> fields = getFields(fieldNos, tClazz);
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("字段上缺少@CsvCell注释");
        }
        final List<Field> superFields = getFields(fieldNos, supClazz);
        if (fields != null && !fields.isEmpty()) {
            fields.addAll(superFields);
        }
        //进行数组排序
        fields = fields.stream()
                .sorted(Comparator.comparingInt(o -> o.getDeclaredAnnotation(CsvCell.class).index()))
                .collect(Collectors.toList());
        final StringJoiner csvFile = new StringJoiner("\r\n");
        // csv数据标题
        final StringJoiner titleRow = new StringJoiner(",");
        for (Field field : fields) {
            titleRow.add(field.getDeclaredAnnotation(CsvCell.class).title());
        }
        csvFile.merge(titleRow);

        // 生成csv格式的数据
        for (T t : ts) {
            final StringJoiner row = new StringJoiner(",");
            for (Field field : fields) {
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
     * 用于mybatis流式查询导出
     * @param list
     * @param info
     * @param <T>
     */
    public static <T> void handle(List<T> list, CsvFileInfo info) {
        try {
            if (ObjectUtil.isNotEmpty(list)){
                CsvFileInfo fileInfo = CsvClient.getCsvByte(list, "", Collections.singletonList((short) 1));
                byte[] fileContent = fileInfo.getFileContent();
                byte[] all = ArrayUtil.addAll(info.getFileContent(), fileContent);
                info.setFileContent(all);
                info.setFileSize(all.length);
            }
        } finally {
            list.clear();
        }
    }


    /**
     * 用于mybatis流式查询导出
     * @param response
     * @param list
     * @param info
     * @param <T>
     */
    public static <T> void endHandle(HttpServletResponse response, List<T> list, CsvFileInfo info) {
        handle(list, info);
        CsvClient.writeCSV(response, info.getFileName(), info.getFileContent());
    }
    /**
     * 导出csv
     *
     * @param response
     * @param fileName
     * @param bytes
     */
    public static void writeCSV(HttpServletResponse response, String fileName, byte[] bytes) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            String lastFileName = fileName + ".csv";
            response.setContentType("application/msexcel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(lastFileName, "UTF-8"));
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    log.error("导出写Csv异常");
                }
            }
        }
    }

    /**
     * 用于mybatis流式查询导出
     * @param response
     * @param list
     * @param info
     * @param operationBeforeExport
     * @param <T>
     */
    public static <T> void endHandle(HttpServletResponse response, List<T> list, CsvFileInfo info, Operation operationBeforeExport) {
        handle(list, info);
        operationBeforeExport.operation();
        CsvClient.writeCSV(response, info.getFileName(), info.getFileContent());
    }
}
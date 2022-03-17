package com.gys.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author xiaoyuan
 */
public class Util {
    public static String getExceptionInfo(Exception ex) {
        String sOut = "";
        sOut = sOut + ex.getMessage() + "\r\n";
        StackTraceElement[] trace = ex.getStackTrace();
        StackTraceElement[] var3 = trace;
        int var4 = trace.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            StackTraceElement s = var3[var5];
            sOut = sOut + "\tat " + s + "\r\n";
        }

        return sOut;
    }

    /**
     * 不够位数的在前面补0，保留code的长度位数字
     *
     * @param code
     * @return
     */
    public static String autoGenericCode(String code) {
        String result = "";
        // 保留code的位数
        result = String.format("%0" + code.length() + "d", Integer.parseInt(code) + 1);

        return result;
    }

    /**
     * 金额正则判断
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        // 判断小数点后4位的数字的正则表达式
        Pattern pattern = compile(UtilMessage.AMOUNT_REGULAR2);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 数量正则判断
     *
     * @param str
     * @return
     */
    public static boolean quantityJudgment(String str) {
        //判断数量正则
        Pattern pattern = compile(UtilMessage.NUMBER_REGULAR);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 金额正则判断2
     *
     * @param str
     * @return
     */
    public static boolean isNumber2(String str) {
        // 判断小数点后4位的数字的正则表达式
        Pattern pattern = compile(UtilMessage.AMOUNT_REGULAR);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author fanjz
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取文件后缀名
     */
    public static String getTheSuffix(String name) {
        String suffix = name.substring(name.lastIndexOf("_") + 1);
        return suffix;
    }

    /**
     * 判断是否为数字，包含负数情况
     *
     * @param str
     * @return
     */
    public static boolean isNumbers(String str) {
        Boolean flag = false;
        String tmp;
        if (StringUtils.isNotBlank(str)) {
            if (str.startsWith("-")) {
                tmp = str.substring(1);
            } else {
                tmp = str;
            }
            flag = tmp.matches("^[0.0-9.0]+$");
        }
        return flag;
    }

    /**
     * 判断日期
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date beginTime, Date endTime) {
        if (DateUtil.isSameDay(beginTime, endTime)) {
            return true;
        }
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (begin.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断时间
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendarTime(Date beginTime, Date endTime) {
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (begin.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    public static Map<String, Object> jsonToMap(String str_json) {
        Map<String, Object> res = null;
        try {
            Gson gson = new Gson();
            res = gson.fromJson(str_json, new TypeToken<Map<String, Object>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
        }
        return res;
    }

    public static boolean compTime(String s1, String s2) {
        try {
            if (s1.indexOf(":") < 0 || s1.indexOf(":") < 0) {
                return false;
            } else {
                String[] array1 = s1.split(":");
                int total1 = Integer.valueOf(array1[0]) * 3600 + Integer.valueOf(array1[1]) * 60 + Integer.valueOf(array1[2]);
                String[] array2 = s2.split(":");
                int total2 = Integer.valueOf(array2[0]) * 3600 + Integer.valueOf(array2[1]) * 60 + Integer.valueOf(array2[2]);
                return total1 - total2 > 0 ? true : false;
            }
        } catch (Exception e) {
            return false;
        }
    }
//    public static void main(String[] args) throws ParseException {
////        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
////        String beginTime = "10:38:05";
////        String endTime = "09:37:25";
////
////        boolean b = compTime(endTime, beginTime);
////        System.out.println(b);
//
////        TableMap<String, Integer> tableMap = new TableMap<>(8);
////        tableMap.put("1111",1111);
////        tableMap.put("1111",2222);
////        tableMap.put("2222",3333);
////        tableMap.put("1111",4444);
////        tableMap.put("1111",55555);
////        System.out.println(tableMap);
////        Integer integer = tableMap.get("2222");
////        System.out.println(integer);
//
//    }

    public static BigDecimal divide(BigDecimal lastYearSalesAmt, BigDecimal salesAmt2) {
        lastYearSalesAmt = ObjectUtil.isNotEmpty(lastYearSalesAmt) ? lastYearSalesAmt : BigDecimal.ZERO;
        salesAmt2 = ObjectUtil.isNotEmpty(salesAmt2) ? salesAmt2 : BigDecimal.ZERO;
        return salesAmt2.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : lastYearSalesAmt.divide(salesAmt2, 4, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal checkNull(Double num) {
        BigDecimal bigDecimal = ObjectUtil.isNotEmpty(num) ? new BigDecimal(num.toString()) : BigDecimal.ZERO;
        return bigDecimal;
    }
}

package com.gys.util;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class DateUtil {

    /**
     * 默认日期格式
     */
    public static String DEFAULT_FORMAT = "yyyyMMdd";
    public static String DEFAULT_FORMAT2 = "yyyy-MM-dd";
    public static String DEFAULT_FORMAT3 = "yyyy年MM月dd日";

    private static final SimpleDateFormat SDF_YYYY_MM_DD = new SimpleDateFormat(DEFAULT_FORMAT2);

    public static String getFirstDayOfNextMonth(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化日期
     *
     * @param date 日期对象
     * @return String 日期字符串
     */
    public static String formatDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);
        String sDate = f.format(date);
        return sDate;
    }

    /**
     * yyyyMMdd 格式转 yyyy-MM-dd
     *
     * @param str 传递的日期字符串
     * @return String
     */
    public static String dateConvertStr(String str) {
        String dateString;
        try {
            Date parse = new SimpleDateFormat("yyyyMMdd").parse(str);
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(parse);
        } catch (ParseException e) {
            dateString = null;
        }
        return dateString;
    }

    /**
     * 格式化日期
     *
     * @param date 日期对象
     * @return String 日期字符串
     */
    public static String formatDate2(Date date) {
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT2);
        String sDate = f.format(date);
        return sDate;
    }

    /**
     * 格式化日期
     *
     * @param date 日期对象
     * @return String 日期字符串
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        String sDate = f.format(date);
        return sDate;
    }

    /**
     * 转换日期
     *
     * @param str 日期对象
     * @return String 日期字符串
     */
    public static Date transformDate(String str, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date date = f.parse(str, pos);
        return date;
    }

    public static String getFormatDate(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT2);
        return dateTimeFormatter.format(date);
    }

    public static String getFormatyyyyMMdd(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
        return dateTimeFormatter.format(date);
    }


    /**
     * 获取当年的第一天
     * @return
     */
//    public static Date getCurrYearFirst(){
//        Calendar currCal=Calendar.getInstance();
//        int currentYear = currCal.get(Calendar.YEAR);
//        return getYearFirst(currentYear);
//    }

    /**
     * 获取当年的最后一天
     * @return
     */
//    public static Date getCurrYearLast(){
//        Calendar currCal=Calendar.getInstance();
//        int currentYear = currCal.get(Calendar.YEAR);
//        return getYearLast(currentYear);
//    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static String getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);
        return f.format(currYearFirst);
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static String getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);
        return f.format(currYearLast);
    }

    /**
     * 获取某年第一天日期
     *
     * @param yearMonth 年月
     * @return Date
     */
    public static String getYearMonthFirst(String yearMonth) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        try {

            Date date = null;
            date = sdf.parse(yearMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);// 设为本月第一天
            return sdf2.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new BusinessException("起始日期异常！");

    }

    /**
     * 获取某年某月最后一天日期
     *
     * @param yearMonth 年月
     * @return Date
     */
    public static String getYearMonthLast(String yearMonth) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        try {

            Date date = null;
            date = sdf.parse(yearMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);// 设为本月第一天
            calendar.add(Calendar.MONTH, 1);// 月份加一
            calendar.add(Calendar.DATE, -1);// 天数加 -1 = 上一个月的最后一天
            return sdf2.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new BusinessException("结束日期异常！");

    }

    /**
     * 获取某年某月最后一天日期
     *
     * @param yearMonth 年月
     * @return Date
     */
    public static String getYearMonthLastFormat(String yearMonth, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf2 = new SimpleDateFormat(format);
        try {

            Date date = null;
            date = sdf.parse(yearMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);// 设为本月第一天
            calendar.add(Calendar.MONTH, 1);// 月份加一
            calendar.add(Calendar.DATE, -1);// 天数加 -1 = 上一个月的最后一天
            return sdf2.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new BusinessException("结束日期异常！");

    }

    /**
     * 获取某年某月最后一天日期
     *
     * @param yearMonth 年月
     * @return Date
     */
    public static String getYearMonthFirstFormat(String yearMonth, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf2 = new SimpleDateFormat(format);
        try {

            Date date = null;
            date = sdf.parse(yearMonth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);// 设为本月第一天
            return sdf2.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new BusinessException("结束日期异常！");

    }

    /**
     * 日期加天数
     *
     * @param date
     * @param format
     * @param day
     * @return
     */
    public static Date addDay(String date, String format, Integer day) {
        Date sDate = transformDate(date, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static Date addDay(Date date, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 日期加月数
     *
     * @param date
     * @param format
     * @param month
     * @return
     */
    public static Date addMonth(String date, String format, Integer month) {
        Date sDate = transformDate(date, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /**
     * 获取上月
     * 描述:<描述函数实现的功能>.
     *
     * @param date
     * @return
     */
    public static String getLastMonth(String date) {
        String lastMonth = "";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        int year = Integer.parseInt(date.substring(0, 4));
        String monthsString = date.substring(4, 6);
        int month;
        if ("0".equals(monthsString.substring(0, 1))) {
            month = Integer.parseInt(monthsString.substring(1, 2));
        } else {
            month = Integer.parseInt(monthsString.substring(0, 2));
        }
        cal.set(year, month - 2, Calendar.DATE);
        lastMonth = dft.format(cal.getTime());
        return lastMonth;
    }

    /**
     * 获取月第一天
     * 描述:<描述函数实现的功能>.
     *
     * @param date
     * @return
     */
    private static String getMinMonthDate(String date) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            if (StringUtils.isNotBlank(date) && !"null".equals(date)) {
                calendar.setTime(dft.parse(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     * 去年同期
     *
     * @param date
     * @return
     */
    public static String getLastYearCurrentDate(String date) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        try {
            if (StringUtils.isNotBlank(date) && !"null".equals(date)) {
                calendar.setTime(dft.parse(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.YEAR, -1);
        return dft.format(calendar.getTime());
    }

    /**
     * 根据结束日期和倒推时间获取起始时间
     *
     * @param date
     * @param days
     * @return
     */
    public static String getStartDateByDays(String date, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String minDateStr = "";
        Calendar calc = Calendar.getInstance();
        try {
            calc.setTime(sdf.parse(date));
            calc.add(calc.DATE, -days);
            Date minDate = calc.getTime();
            minDateStr = sdf.format(minDate);
            System.out.println("minDateStr:" + minDateStr);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return minDateStr;
    }

    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }

    public static String getNextMonthFirstDay(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");//格式化为年月
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");//格式化为年月

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(date));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, 1);
        System.out.println(sdf2.format(calendar.getTime()));
        return sdf2.format(calendar.getTime());
    }

    /**
     * 去年同期的某一天
     *
     * @param date
     * @return
     */
    public static String getLastYearDate(String date) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            if (StringUtils.isNotBlank(date) && !"null".equals(date)) {
                calendar.setTime(dft.parse(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.YEAR, -1);
        return dft.format(calendar.getTime());
    }

    /**
     * 获取上个月的第一天和最后一天
     *
     * @return
     */
    public static Map<String, String> getLastMonth() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //获取前一个月第一天
            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.MONTH, -1);
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            String firstDay = sdf.format(calendar1.getTime());
            //获取前一个月最后一天
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, 0);
            String lastDay = sdf.format(calendar2.getTime());
            Map<String, String> lastMonth = new HashMap<>();
            lastMonth.put("firstDay", firstDay);
            lastMonth.put("lastDay", lastDay);
            return lastMonth;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 根据日期获取星期
     *
     * @param datetime 日期
     * @return 星期
     */
    public static String date2Week(Date datetime) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static int weekOfYear(Date datetime) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(datetime);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * yyyyMMdd 格式转 yyyy-MM-dd
     *
     * @param str 传递的日期字符串
     * @return String
     */
    public static String dateConvert(String str) {
        String dateString;
        try {
            Date parse = new SimpleDateFormat("yyyyMMdd").parse(str);
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(parse);
        } catch (ParseException e) {
            dateString = null;
        }
        return dateString;
    }

    public static long getDiffByDate(String beginDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = null;
        Date endDate = null;
        try {
            beginDate = sdf.parse(beginDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
            throw new BusinessException("日期计算错误");
        }
        long diffDay = (endDate.getTime() - beginDate.getTime() + 1000000) / (60 * 60 * 24 * 1000);
        return diffDay;
    }


    /**
     * @Author jiht
     * @Description 楼上的方法，存在bug，比如：getDiffByDate("20211226","20211227")=0，实际应该为1
     * @Date 2022/1/6 13:44
     * @Param [beginDateStr, endDateStr]
     * @Return long
     **/
    public static long getDiffByDateNew(String beginDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = null;
        Date endDate = null;
        try {
            beginDate = sdf.parse(beginDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
            throw new BusinessException("日期计算错误");
        }
        long diffDay = (endDate.getTime() - beginDate.getTime()) / (60 * 60 * 24 * 1000);
        return diffDay;
    }


    public static String dateStrFormat(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT3);
        String resultDateStr = null;    //再将Date类型格式化为想要的格式
        try {
            Date date = transformDate(dateStr, DEFAULT_FORMAT2);    //将字符串转为Date类型
            resultDateStr = sdf.format(date);
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
            throw new BusinessException("日期格式转换异常！");
        }
        return resultDateStr;
    }

    /**
     * 将yyyymmdd转换成yyyy年mm日dd日
     * @param dateStr
     * @return
     */
    public static String dateStrFormat1(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT3);
        String resultDateStr = null;    //再将Date类型格式化为想要的格式
        try {
            Date date = transformDate(dateStr, DEFAULT_FORMAT);    //将字符串转为Date类型
            resultDateStr = sdf.format(date);
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
            throw new BusinessException("日期格式转换异常！");
        }
        return resultDateStr;
    }

    public static String dateStrFormatExt(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_FORMAT2);
        String resultDateStr = null;    //再将Date类型格式化为想要的格式
        try {
            Date date = transformDate(dateStr, DEFAULT_FORMAT);    //将字符串转为Date类型
            resultDateStr = sdf.format(date);
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
            throw new BusinessException("日期格式转换异常！");
        }
        return resultDateStr;
    }

    //20211209214333
    public static String getCurrent() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date()).replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
        return dateString;
    }

    /**
     * 取最小时间 yyyyMMdd
     *
     * @param str1 str1
     * @param str2 str2
     * @return String
     */
    public static String getMinDate(@NotNull String str1, @NotNull String str2) {
        int min = NumberUtil.min(Integer.parseInt(str1), Integer.parseInt(str2));
        return String.valueOf(min);
    }

    /**
     * 取最大时间 yyyyMMdd
     *
     * @param str1 str1
     * @param str2 str2
     * @return String
     */
    public static String getMaxDate(@NotNull String str1, @NotNull String str2) {
        int max = NumberUtil.max(Integer.parseInt(str1), Integer.parseInt(str2));
        return String.valueOf(max);
    }

    public static String getFullDate() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = f.format(new Date());
        System.out.println(sDate);
        return sDate;
    }

    public static String addDayString(Date date, Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = f.format(calendar.getTime());
        return sDate;
    }

    public static Date getStringToDate(String date) {
        Date sDate = null;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            sDate = f.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sDate;
    }


    /**
     * @Author jiht
     * @Description 获取当前月第一天
     * @Date 2021/12/31 15:32
     * @Param [str1]
     * @Return java.lang.String
     **/
    public static String getFirstDayOfMonth(@NotNull String str1) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化为年月
            Date now = sdf.parse(str1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * @Author jiht
     * @Description 获取当前月最后一天
     * @Date 2021/12/31 15:32
     * @Param [str1]
     * @Return java.lang.String
     **/
    public static String getLastDayOfMonth(@NotNull String str1) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化为年月
            Date now = sdf.parse(str1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * @Author jiht
     * @Description 获取日期属于哪一周
     * @Date 2021/12/31 15:32
     * @Param [str1]
     * @Return java.lang.String
     **/
    public static int getWeekNum(@NotNull String str1) {
        int result = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化为年月
            Date now = sdf.parse(str1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            return calendar.get(Calendar.WEEK_OF_YEAR);
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return result;
    }


    /**
     * @Author jiht
     * @Description 获取周第一天
     * @Date 2021/12/31 15:32
     * @Param [str1]
     * @Return java.lang.String
     **/
    public static String getFirstDayOfWeek(@NotNull String str1) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化为年月
            Date now = sdf.parse(str1);
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(now);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * @Author jiht
     * @Description 获取周最后一天
     * @Date 2021/12/31 15:32
     * @Param [str1]
     * @Return java.lang.String
     **/
    public static String getLastDayOfWeek(@NotNull String str1) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//格式化为年月
            Date now = sdf.parse(str1);
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(now);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return result;
    }
    public static  Date getStringToDate2(String date){
        Date sDate=null;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            sDate = f.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  sDate;
    }

    /**
     * 获取开始时间和结束时间段之内的每一天
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return
     * @throws ParseException
     */
    public static List<String> getDateList(String beginDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(beginDate));
        List<String> dates = new ArrayList<>();
        for (long d = cal.getTimeInMillis(); d <= sdf.parse(endDate).getTime(); d = get_D_Plaus_1(cal)) {
            System.out.println(sdf.format(d));
            dates.add(DateUtil.dates(sdf.format(d)));
        }
        return dates;
    }

    public static long get_D_Plaus_1(Calendar c) {
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        return c.getTimeInMillis();

    }

    public static String dates(String str) {
        String dateString;
        try {
            Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            dateString = new SimpleDateFormat("yyyyMMdd").format(parse);
        } catch (ParseException e) {
            dateString = null;
        }
        return dateString;
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }


    public static Date getFullDateStartDate(String date) {
        Date rsDate = null;
        try {
            String year = date.substring(0, 4);
            String montn = date.substring(4, 6);
            String day = date.substring(6, 8);
            String dateStr = year + "-" + montn + "-" + day + " 00:00:00";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            rsDate = format.parse(dateStr);
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return rsDate;
    }

    public static Date getFullDateEndDate(String date) {
        Date rsDate = null;
        try {
            String year = date.substring(0, 4);
            String montn = date.substring(4, 6);
            String day = date.substring(6, 8);
            String dateStr = year + "-" + montn + "-" + day + " 59:59:59";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            rsDate = format.parse(dateStr);
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return rsDate;
    }

    /**
     * 根据查询开始时间，补充精确到小数点级别的开始时间
     * 入参数格式为yyyyMMdd
     * @param date
     * @return
     */
    public static String getFullDateStartStr(String date) {
        String resStr = "";
        try {
            String year = date.substring(0, 4);
            String montn = date.substring(4, 6);
            String day = date.substring(6, 8);
            resStr = year + "-" + montn + "-" + day + " 00:00:00";
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }

        return resStr;
    }

    /**
     * 根据查询结束时间，补充精确到小数点级别的开始时间
     * 入参数格式为yyyyMMdd
     * @param date
     * @return
     */
    public static String getFullDateEndStr(String date) {
        String resStr = "";
        try {
            String year = date.substring(0, 4);
            String montn = date.substring(4, 6);
            String day = date.substring(6, 8);
            resStr = year + "-" + montn + "-" + day + " 59:59:59";
        } catch (Exception e) {
            log.error("日期格式转换异常：{}", e.getMessage(), e);
        }
        return resStr;
    }


    public static void main(String[] args) {
        System.out.println(getFullDateStartDate("20211101"));
    }
}

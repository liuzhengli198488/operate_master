package com.gys.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gys.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class CalendarUtil {

    /**
     * 返回当前日期的本月最后一天
     * @param date
     * @return
     */
    public static String getLastDayOfMonth(String date) {
        Date currentDate = null;
        try {
            currentDate = new SimpleDateFormat("yyyyMMdd").parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        Date dateTime = cal.getTime();
        return DateUtil.format(dateTime, "yyyyMMdd");
    }


    /**
     * 判断日期是否为本月最后一天
     * @param date
     * @return
     */
    public static boolean isLastDay(String date){
        Date currentDate = null;
        try {
            currentDate = new SimpleDateFormat("yyyyMMdd").parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) != calendar.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        return true;
    }

    /**
     * 获取上月最后一天
     * @param date
     * @return
     */
    public static String getLastMonthLastDay(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(dateTime);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -1);
        //指定日期月份减去一后的 最大天数
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        return sdf.format(lastDateOfPrevMonth);
    }


    /**
     * 获取上年本月最后一天
     * @param date
     * @return
     */
    public static String getLastYearThisMonthLastDay(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(dateTime);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -12);
        //指定日期月份减去一后的 最大天数
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        return sdf.format(lastDateOfPrevMonth);
    }


    /**
     * 获取上月本日  注意：如果没有本日的话，会返回上月最大天数
     * @param date
     * @return
     */
    public static String getLastMonthThisDay(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(dateTime);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -1);
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        return sdf.format(lastDateOfPrevMonth);
    }

    /**
     * 获取上年本月本日的日期
     * @param date
     * @return
     */
    public static String getLastYearThisMonthThisDay(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(dateTime);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -12);
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        return sdf.format(lastDateOfPrevMonth);
    }

    /**
     * 获取上月是否有此日期  如果有返回ture  否则返回false
     * @param date
     * @return
     */
    public static boolean isLastMonthDay(String date) {
        if(ObjectUtil.isEmpty(date)) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(dateTime);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -1);
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        String lastMonthDay = sdf.format(lastDateOfPrevMonth);
        //截取日期最后两位比较是否是同一天
        String thisDay = date.substring(date.length() - 2);
        String lastDay = lastMonthDay.substring(lastMonthDay.length() - 2);
        return thisDay.equals(lastDay);
    }


    /**
     * 获取上年是否有此日期  如果有返回ture  否则返回false
     * @param date
     * @return
     */
    public static boolean isLastYearDay(String date) {
        if(ObjectUtil.isEmpty(date)) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateTime = null;
        try {
            dateTime = sdf.parse(date);
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        Calendar c = Calendar.getInstance();
        //设置为指定日期
        c.setTime(dateTime);
        //指定日期月份减去一
        c.add(Calendar.MONTH, -12);
        //获取最终的时间
        Date lastDateOfPrevMonth = c.getTime();
        String lastMonthDay = sdf.format(lastDateOfPrevMonth);
        //截取日期最后两位比较是否是同一天
        String thisDay = date.substring(date.length() - 2);
        String lastDay = lastMonthDay.substring(lastMonthDay.length() - 2);
        return thisDay.equals(lastDay);
    }


    public static String getThisMonthFirstDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        // 本月起始
        Calendar thisMonthFirstDateCal = Calendar.getInstance();
        thisMonthFirstDateCal.set(Calendar.DAY_OF_MONTH, 1);
        String thisMonthFirstTime = format.format(thisMonthFirstDateCal.getTime());
        return thisMonthFirstTime;
    }

    /**
     * 当前日期
     * @return
     */
    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }


    /**
     * @param startTime  起始日期
     * @param endTime  终止日期
     * @param formatType  日期格式
     * @return
     */
    public static List<String> getDayList(String startTime, String endTime ,String formatType) {
        // 返回的日期集合
        List<String> dayList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat(formatType);
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                dayList.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            log.error("日期格式转换错误", e);
            throw new BusinessException("提示：日期格式转换错误");
        }
        return dayList;
    }


    public static void main(String[] args) throws Exception {
        List<String> yyyyMMdd = getDayList("20210401", "20210405", "yyyyMMdd");
        if(CollUtil.isNotEmpty(yyyyMMdd)) {
            int dayCount = yyyyMMdd.size();
            if(dayCount > 15) {
                //截取最近15天后使用
                List<String> subList = yyyyMMdd.subList(dayCount - 15, dayCount);
                System.out.println(subList);
                System.out.println(subList.get(0));
                System.out.println(subList.get(subList.size() - 1));
            }
        }
    }

}

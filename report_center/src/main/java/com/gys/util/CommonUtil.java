package com.gys.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class CommonUtil {
    public static String getSecondTimestampTwo(Date date) {
        if (null == date) {
            return "0";
        } else {
            String timestamp = String.valueOf(date.getTime() / 1000L);
            return timestamp;
        }
    }

    /**
     * 格式化除数商
     *
     * @param divisor  除数
     * @param dividend 被除数
     * @param format   格式化方式
     * @return
     */
    public static String divideFormat(BigDecimal dividend, BigDecimal divisor, String format) {
        if(BigDecimal.ZERO.compareTo(divisor) == 0){
            return NumberUtil.decimalFormat(format, BigDecimal.ZERO);
        }

        return NumberUtil.decimalFormat(format, NumberUtil.div(dividend, divisor));
    }

    /**
     * 将yyyyMMdd格式化成yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String parseyyyyMMdd(String date) {
        if (StrUtil.isBlank(date)) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        formatter.setLenient(false);
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(newDate);
    }

    /**
     * 将yyyy-MM-dd格式化成yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String parseWebDate(String date) {
        if (StrUtil.isBlank(date)) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(newDate);
    }

    public static String getyyyyMMdd() {
        return DateUtil.format(new Date(), "yyyyMMdd");
    }

    public static String getHHmmss() {
        return DateUtil.format(new Date(), "HHmmss");
    }

    /**
     * 将HHmmss格式化成HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String parseHHmmss(String date) {
        if (StrUtil.isBlank(date)) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        formatter.setLenient(false);
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(newDate);
    }


    /**
     * 提取字符串中的英文字符(取前四位)
     *
     * @param sourceStr
     * @return
     */
    public static String extractEnglishStr(String sourceStr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            char c = sourceStr.charAt(i);
            if ((c <= 'z' && c >= 'a') || (c <= 'Z' && c >= 'A')) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 格式化小数后无效的零，例如：5.0000格式化成5
     *
     * @param num
     * @return
     */
    public static String stripTrailingZeros(String num) {
        num = StrUtil.isEmpty(num) ? "0.0" : num;
        return new BigDecimal(num).stripTrailingZeros().toPlainString();
    }

    public static BigDecimal stripTrailingZeros(BigDecimal num) {
        num = num == null ? BigDecimal.ZERO : num;
        return num.stripTrailingZeros();
    }

    public static BigDecimal stripTrailingZerosStr(String num) {
        if(StringUtils.isNotEmpty(num)){
            BigDecimal bdNum = new BigDecimal(num);
            bdNum = bdNum == null ? BigDecimal.ZERO : bdNum;
            return bdNum.stripTrailingZeros();
        }else {
            return BigDecimal.ZERO;
        }

    }

    public static String generateBillNo(String brId) {
        Date date = DateUtil.date();
        String yearStr = DateUtil.format(date, DatePattern.PURE_DATE_PATTERN);
        String secondTimestampTwo = CommonUtil.getSecondTimestampTwo(date);
        String storeTimestamp = new java.math.BigInteger(secondTimestampTwo.substring(1, secondTimestampTwo.length() - 1) + brId.substring(brId.length() - 5)).toString(36);
        int random = (int) (Math.random() * 10.0D);
        return "SD" + yearStr + storeTimestamp.toUpperCase() + random;
    }

    /**
     * Returns <code>true</code> if <code>multiple</code> is a multiple of <code>base</code>.
     *
     * @param multiple
     * @param base
     * @return
     */
    public static boolean isMultipleOf(final BigDecimal multiple, final BigDecimal base) {
        if (multiple.compareTo(base) == 0) {
            return true;
        }
        try {
            multiple.divide(base, 0, BigDecimal.ROUND_UNNECESSARY);
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public static boolean isBigDecimal(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        int i = (chars[0] == '-') ? 1 : 0;
        if (i == sz) {
            return false;
        }

        if (chars[i] == '.') {
            //除了负号，第一位不能为'小数点'
            return false;
        }

        boolean radixPoint = false;
        for (; i < sz; i++) {
            if (chars[i] == '.') {
                if (radixPoint) {
                    return false;
                }
                radixPoint = true;
            } else if (!(chars[i] >= '0' && chars[i] <= '9')) {
                return false;
            }
        }
        return true;
    }


    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate2(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String str = format.format(date);
        return str;
    }

    /**
     * 获取当月天数
     * @return
     */
    public static Integer dayNum(){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }



    public static Map<String,BigDecimal> addWeightMap(Map<String,BigDecimal> goldMap, Map<String,Object> preMap)
    {
        Map<String,BigDecimal> addMap = new HashMap<String,BigDecimal>();

        //遍历一下查询出来的map
        for(String s : preMap.keySet())
        {
            //找到key相同的字段相加
            if(goldMap.containsKey(s)) {
                addMap.put(s, goldMap.get(s).add((BigDecimal) preMap.get(s)));
            }
            //不同的单独放着
//            else
//            {
//                addMap.put(s, preMap.get(s));
//            }
        }
        //反向再来一遍
//        for(String str : goldMap.keySet())
//        {
//            if(!preMap.containsKey(str))
//            {
//                addMap.put(str, goldMap.get(str));
//            }
//        }

        //删除值为空的项
        //集合类的都不能在循环的时候删除，因为删除元素后集合发生改变继而不能循环了
        //这种删除方式以后不要再用了,严重出错
	     /*for(String key : cutMap.keySet())
	     {
	    	 Double value = cutMap.get(key);
	    	 if(value == 0.0)
	    	 {
	    		 cutMap.remove(key);
	    	 }
	     }*/
        //删除值为空的项
//        Set mapset = addMap.entrySet();
//        Iterator iterator = mapset.iterator();
//
//        while(iterator.hasNext())
//        {
//            Map.Entry mapEntry = (Map.Entry)iterator.next();
//
//            Double value =(Double)mapEntry.getValue();
//
//            if(value == 0.0)
//            {
//                iterator.remove();
//            }
//
//        }

        return addMap;
    }
    /**
     * 两个Map相减
     * @param goldMap
     * @param preMap
     * @return
     */
    public static Map<String,Double> cutWeightMap(Map<String,Double> goldMap,Map<String,Double> preMap)
    {
        Map<String,Double> cutMap = new HashMap<String,Double>();
        for(String s : preMap.keySet())
        {
            if(goldMap.containsKey(s))
            {
                cutMap.put(s, (goldMap.get(s)-preMap.get(s)));

            } else
            {
                cutMap.put(s, -(preMap.get(s)));
            }
        }
        for(String str : goldMap.keySet())
        {
            if(!preMap.containsKey(str))
            {
                cutMap.put(str, goldMap.get(str));
            }
        }

        //删除值为空的项

        Iterator iterator = cutMap.entrySet().iterator();

        while(iterator.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry)iterator.next();

            Double value =(Double)mapEntry.getValue();

            if(value == 0.0)
            {
                iterator.remove();
            }

        }

        return cutMap;
    }


/**
 * @Author huxinxin
 * @Description 二维数组转一维数组  去重
 * @Date 0:45 2021/4/26
 * @Param [arrtwo]
 * @return java.lang.String[]
 **/
    public static List<String> twoDimensionToOneDimensionArrar(String[][] arrtwo){
//            arrtwo= new String[][]{{"1", "101", "10101"}, {"1", "101", "10102"}};
        String[] int1d;
        int len = 0;
        for (String[] element : arrtwo) {
            len += element.length;
        }
        int1d = new String[len];
        int index = 0;
        for (String[] array : arrtwo) {
            for (String element : array) {
                int1d[index++] = element;
            }
        }

        List<String> list = Lists.newArrayList();
        for (int i = 0; i < int1d.length; i++) {
            if (!list.contains(int1d[i])) {
                list.add(int1d[i]);
            }
        }
//        List<String> outList = list.stream().forEach(System.out::println);

        return list ;
    }

    public static String[] twoDimensionToOneDimensionArrar2(String[][] arrtwo){
//            arrtwo= new String[][]{{"1", "101", "10101"}, {"1", "101", "10102"}};
        String[] int1d;
        int len = 0;
        for (String[] element : arrtwo) {
            len += element.length;
        }
        int1d = new String[len];
        int index = 0;
        for (String[] array : arrtwo) {
            for (String element : array) {
                int1d[index++] = element;
            }
        }
        Set set = new HashSet();

        for(int i=0;i<len;i++){

            set.add(int1d[i]);

        }


        return (String[]) set.toArray();
    }

    public static StringBuilder queryByBatch(List<String> arr){
        StringBuilder str = new StringBuilder();
        if(ObjectUtils.isEmpty(arr)){
            return null;
        }else {
            str.append(" ( ");
            for (int i = 0; i < arr.size(); i++) {

                if (i != 0) {
                    str.append(",");
                }
                str.append("'" + arr.get(i) + "'");
            }
            str.append(" ) ");
        }
        return str;
    }
}

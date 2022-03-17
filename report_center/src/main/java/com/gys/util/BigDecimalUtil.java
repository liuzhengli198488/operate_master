package com.gys.util;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.exception.BusinessException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BigDecimalUtil {

    public final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public static int isCompareFlag(BigDecimal a, BigDecimal b) {
        //前提为a、b均不能为null
        //status == -1
        //System.out.println("a小于b");

        //status == 0
        //System.out.println("a等于b");
        //status == 1
        //System.out.println("a大于b");
        //status > -1
        //System.out.println("a大于等于b");

        //status < 1
        //System.out.println("a小于等于b");

        return a.compareTo(b);
    }

    /**
     * Object转BigDecimal类型
     *
     * @param value 要转的object类型
     * @return 转成的BigDecimal类型数据
     */
    public static BigDecimal toBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        } else {
            return BigDecimal.ZERO;
        }
        return ret;
    }

    public static Map<String, BigDecimal> addWeightMap(Map<String, BigDecimal> goldMap, Map<String, Object> preMap) {
        Map<String, BigDecimal> addMap = new HashMap<String, BigDecimal>();

        //遍历一下查询出来的map
        for (String s : preMap.keySet()) {
            //找到key相同的字段相加
            if (goldMap.containsKey(s)) {
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

    /*
     * @Author huxinxin
     * @Description // 加法
     * @Date 14:21 2021/7/6
     * @Param [a, b, scale]
     * @return java.math.BigDecimal
     **/
    public static BigDecimal add(Object a, Object b) {
        BigDecimal first = BigDecimal.ZERO;
        BigDecimal second = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(a)) {
            first = toBigDecimal(a);
        }
        if (ObjectUtil.isNotEmpty(b)) {
            second = toBigDecimal(b);
        }
        return first.add(second);
    }

    /*
     * @Author huxinxin
     * @Description //  减法
     * @Date 14:21 2021/7/6
     * @Param [a, b, scale]
     * @return java.math.BigDecimal
     **/
    public static BigDecimal subtract(Object a, Object b) {
        BigDecimal first = BigDecimal.ZERO;
        BigDecimal second = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(a)) {
            first = toBigDecimal(a);
        }
        if (ObjectUtil.isNotEmpty(b)) {
            second = toBigDecimal(b);
        }

        return first.subtract(second);
    }


    /*
     * @Author huxinxin
     * @Description //TODO
     * @Date 14:21 2021/7/6
     * @Param [a, b, scale]
     * @return java.math.BigDecimal
     **/
    public static BigDecimal multiply(Object a, Object b) {

        BigDecimal first = BigDecimal.ZERO;
        BigDecimal second = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(a)) {
            first = toBigDecimal(a);
        }
        if (ObjectUtil.isNotEmpty(b)) {
            second = toBigDecimal(b);
        }
        if (second.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return first.multiply(second);
    }

    /*
     * @Author huxinxin
     * @Description //  除法
     * @Date 14:21 2021/7/6
     * @Param [a, b, scale]
     * @return java.math.BigDecimal
     **/
    public static BigDecimal divide(Object a, Object b, int scale) {
        if (scale < 0) {
            throw new BusinessException("精度有误,请联系管理员!");
        }
        BigDecimal first = BigDecimal.ZERO;
        BigDecimal second = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(a)) {
            first = toBigDecimal(a);
        }
        if (ObjectUtil.isNotEmpty(b)) {
            second = toBigDecimal(b);
        }
        if (second.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
//        System.out.println(first.divide(second,scale,BigDecimal.ROUND_HALF_UP));
        return first.divide(second, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divideWithPercent(Object a, Object b, int scale) {
        return divide(a, b, scale).multiply(ONE_HUNDRED);
    }

    /*
     * @Author huxinxin
     * @Description // 相除后 转成百分比
     * @Date 14:21 2021/7/6
     * @Param [a, b, scale]
     * @return java.math.BigDecimal
     **/
    public static String divideRate(Object a, Object b, int scale) {
        if (scale < 0) {
            throw new BusinessException("精度有误,请联系管理员!");
        }
        BigDecimal first = BigDecimal.ZERO;
        BigDecimal second = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(a)) {
            first = toBigDecimal(a);
        }
        if (ObjectUtil.isNotEmpty(b)) {
            second = toBigDecimal(b);
        }
        if (second.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(first.divide(second, scale, BigDecimal.ROUND_HALF_UP));
    }

    public static BigDecimal format(BigDecimal arg, int scale, RoundingMode rm) {
        return arg.setScale(scale, rm);
    }

    public static BigDecimal format(BigDecimal arg, int scale) {
        return format(arg, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal format(BigDecimal arg, RoundingMode rm) {
        return format(arg, 4, rm);
    }

    public static BigDecimal format(BigDecimal arg) {
        return format(arg, 4);
    }

    public static BigDecimal addWithPercent(Object a, Object b) {
        return add(a, b).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public static String formatWithPercent(String str) {
        if (StringUtils.isBlank(str)) {
            return BigDecimal.ZERO.toString();
        }
        return formatWithPercent(new BigDecimal(str)).toString();
    }

    public static BigDecimal formatWithPercent(BigDecimal str) {
        if (str == null) {
            return BigDecimal.ZERO;
        }
        return str.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

}

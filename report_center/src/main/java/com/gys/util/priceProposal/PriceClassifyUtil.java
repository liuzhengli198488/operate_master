package com.gys.util.priceProposal;

import cn.hutool.core.util.StrUtil;

import java.util.Random;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 价格分类区间工具类
 * @CreateTime 2022-01-11 18:27:00
 */
public class PriceClassifyUtil {

    public static void main(String[] args) {
//        int start = 100;
//        int end = 10000;
//        int gap = 20;
//        String[] res = getStrArray(start, end, gap);
//        for (String s : res) {
//            System.out.println(s);
//        }
//        System.out.println(getNumLocation("10-15","20.42"));
    }

    /**
     * 动态输出数组所在的范围
     *
     * @param start
     * @param end
     * @param gap
     * @return
     */
    public static String[] getStrArray(int start, int end, int gap) {
        int len = (end - start) / gap, left = start, right = end;
        String[] nums = new String[len];
        int index = 0;
        for (int i = start; i + gap <= end; i = i + gap) {
            nums[index++] = i + "-" + (i + gap);
        }
        return nums;
    }

    /**
     * 输出数字的位置
     *
     * @param str
     * @return
     */
    public static String getNumLocation(String numRange, String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        double num = Double.parseDouble(str);
        String[] arr = numRange.split("-");
        double left = Double.parseDouble(arr[0]);
        double right = Double.parseDouble(arr[1]);
        if (num > left && num <= right) {
            return numRange;
        }
        return null;
    }

    /**
     * 随机生成数字 5-9
     * @return
     */
    public static int getTailNum() {
        int max = 9;
        int min = 5;
        Random random = new Random();
        int i = random.nextInt(max) % (max - min + 1) + min;
        return i;
    }

}

package com.gys.util;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjectUtil;
import com.gys.annotation.BetweenDataField;
import com.gys.annotation.EndDataField;
import com.gys.annotation.StartDataField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author ：gyx
 * @Date ：Created in 23:01 2022/1/15
 * @Description：
 * @Modified By：gyx
 * @Version:
 */
public class EndDataUtil {

    public static <T> T calcEndData(T data) {
        BigDecimal start = BigDecimal.ZERO;
        BigDecimal bet = BigDecimal.ZERO;
        BigDecimal end = BigDecimal.ZERO;

        Class<?> clazz = data.getClass();
        List<Field> fieldList = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList());
        Field startField=null;
        Field endField=null;
        List<Field> betweenFiled=new ArrayList<>();
        for (Field field : fieldList) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(StartDataField.class)) {
                startField=field;
            }
            if (field.isAnnotationPresent(BetweenDataField.class)){
                betweenFiled.add(field);
            }
            if (field.isAnnotationPresent(EndDataField.class)){
                endField=field;
            }
        }
        for (Field field : betweenFiled) {
            Object o = null;
            try {
                o = field.get(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            BigDecimal count=(BigDecimal) o;
            bet=bet.add(count);
        }
        if (ObjectUtil.isNotEmpty(startField)) {
            Object startData = null;
            try {
                startData = startField.get(data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            start=(BigDecimal) startData;
        }
        if (ObjectUtil.isNotEmpty(endField)) {
            end=start.add(bet);
            try {
                endField.set(data,end);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static <T> T mergeStartData(T start,T bet){
        Class<?> startClazz = start.getClass();
        Optional<Field> startFieldOpt = Arrays.stream(startClazz.getDeclaredFields()).filter(filed -> filed.isAnnotationPresent(StartDataField.class)).findFirst();
        if (startFieldOpt.isPresent()) {
            Field startField = startFieldOpt.get();
            startField.setAccessible(true);
            try {
                startField.set(bet,startField.get(start));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bet;
    }

    public static <T> T getEndData(T startData,T betData){
        return calcEndData(mergeStartData(startData,betData));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Ybb{
        @BetweenDataField
        private BigDecimal saleAmt;
        @BetweenDataField
        private BigDecimal lossAmt;
        @StartDataField
        private BigDecimal startAmt;
        @EndDataField
        private BigDecimal endAmt;
        @BetweenDataField
        private BigDecimal distributionAmt;
    }

    public static void main(String[] args) {
        Ybb startYbb = Ybb.builder()
                .startAmt(new BigDecimal(10))
                .build();
        Ybb betYbb = Ybb.builder()
                .saleAmt(new BigDecimal(10))
                .lossAmt(new BigDecimal(10))
                .distributionAmt(new BigDecimal(10)).build();
        Ybb ybb = getEndData(startYbb, betYbb);
        System.out.println(ybb);
    }
}

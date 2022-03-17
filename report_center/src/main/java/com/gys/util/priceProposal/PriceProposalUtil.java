package com.gys.util.priceProposal;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gys.entity.priceProposal.dto.CityDimensionDTO;
import com.gys.entity.priceProposal.dto.CityProClassifyDTO;
import com.gys.entity.priceProposal.dto.PriceProposalClientDTO;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议工具类
 * @CreateTime 2022-01-19 14:07:00
 */
public class PriceProposalUtil {

    public static List<Map<String, String>> getDefaultRangeMapList() {
        List<Map<String, String>> rangeMapList = Lists.newArrayList();
        Map<String, String> rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "A");
        rangeMap.put("start", "0");
        rangeMap.put("end", "10");
        rangeMap.put("gap", "1");
        rangeMapList.add(rangeMap);
        rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "B");
        rangeMap.put("start", "10");
        rangeMap.put("end", "50");
        rangeMap.put("gap", "5");
        rangeMapList.add(rangeMap);
        rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "C");
        rangeMap.put("start", "50");
        rangeMap.put("end", "100");
        rangeMap.put("gap", "10");
        rangeMapList.add(rangeMap);
        rangeMap = Maps.newHashMap();
        rangeMap.put("classify", "D");
        rangeMap.put("start", "100");
        rangeMap.put("end", "10000");
        rangeMap.put("gap", "20");
        rangeMapList.add(rangeMap);

        return rangeMapList;
    }

    public static ProductPriceProposalZ getPriceClassifyMap(String maxNo, List<Map<String, String>> rangeMapList, CityDimensionDTO cityDimensionDTO, PriceProposalClientDTO priceProposalClientDTO) {
        for (Map<String, String> rangeMap : rangeMapList) {
            if (StrUtil.isNotBlank(cityDimensionDTO.getAvgSellingPrice())) {
                if (Double.parseDouble(cityDimensionDTO.getAvgSellingPrice()) > Double.parseDouble(rangeMap.get("start"))
                        && Double.parseDouble(cityDimensionDTO.getAvgSellingPrice()) <= Double.parseDouble(rangeMap.get("end"))) {
                    ProductPriceProposalZ priceProposalZ = new ProductPriceProposalZ();
                    priceProposalZ.setPriceProposalNo(maxNo);
                    priceProposalZ.setClientId(priceProposalClientDTO.getClientId());
                    priceProposalZ.setClientName(priceProposalClientDTO.getClientName());
                    priceProposalZ.setProvinceCode(cityDimensionDTO.getProvinceId());
                    priceProposalZ.setProvinceName(cityDimensionDTO.getProvince());
                    priceProposalZ.setCityCode(priceProposalClientDTO.getCityId());
                    priceProposalZ.setCityName(cityDimensionDTO.getCity());
                    priceProposalZ.setProCode(cityDimensionDTO.getProCode());
                    priceProposalZ.setAvgSellingPrice(cityDimensionDTO.getAvgSellingPrice());
                    priceProposalZ.setBayesianProbability(cityDimensionDTO.getBayesianProbability());
                    priceProposalZ.setPriceRegionLow(rangeMap.get("start"));
                    priceProposalZ.setPriceRegionHigh(rangeMap.get("end"));
                    priceProposalZ.setPriceStep(rangeMap.get("gap"));
                    priceProposalZ.setDataType("1");
                    return priceProposalZ;
                }
            }
        }
        return null;
    }

    public static String getToday() {
        DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = LocalDate.now().format(fmDate);
        return today;
    }

    public static double calcProposalPrice(String range, Double avgPrice) {
        if (avgPrice == null) {
            return 0.0d;
        }
        String[] rangeArr = range.split("-");
        if (avgPrice < Double.parseDouble(rangeArr[0])) {
            return Double.parseDouble(rangeArr[1]);
        } else if (avgPrice > Double.parseDouble(rangeArr[1])) {
            return Double.parseDouble(rangeArr[0]);
        }
        return 0.0d;
    }

    public static long betweenDays(String now, String past) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// 自定义时间格式

        Calendar calendar_a = Calendar.getInstance();// 获取日历对象
        Calendar calendar_b = Calendar.getInstance();

        Date date_a = null;
        Date date_b = null;

        try {
            date_a = simpleDateFormat.parse(now);//字符串转Date
            date_b = simpleDateFormat.parse(past);
            calendar_a.setTime(date_a);// 设置日历
            calendar_b.setTime(date_b);
        } catch (ParseException e) {
            e.printStackTrace();//格式化异常
        }

        long time_a = calendar_a.getTimeInMillis();
        long time_b = calendar_b.getTimeInMillis();

        long between_days = (time_b - time_a) / (1000 * 3600 * 24);//计算相差天数

        return between_days;
    }

    public static String getInvalidTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, 5);
            return sdf.format(cal.getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getClientType(String type2) {
        if (StrUtil.isNotBlank(type2) && type2.equals("1")) {
            return "2";
        }
        return "1";
    }

}

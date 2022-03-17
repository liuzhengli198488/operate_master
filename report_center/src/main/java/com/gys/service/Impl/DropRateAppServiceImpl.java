package com.gys.service.Impl;

import com.gys.common.enums.XhlTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.controller.app.dto.DistributeGoodsDto;
import com.gys.controller.app.vo.*;
import com.gys.entity.GaiaCalDate;
import com.gys.entity.GaiaXhlH;
import com.gys.mapper.GaiaXhlHMapper;
import com.gys.service.DropRateAppService;
import com.gys.service.GaiaCalDateService;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/29 23:41
 */
@Slf4j
@Service("dropRateAppService")
public class DropRateAppServiceImpl implements DropRateAppService {
    @Resource
    private GaiaXhlHMapper gaiaXhlHMapper;
    @Resource
    private GaiaCalDateService gaiaCalDateService;

    @Override
    public DropRateVO lastTimeData(String client, DistributeGoodsDto dto) {
        //查询缓存 todo
        List<GaiaXhlH> list = gaiaXhlHMapper.getLastTimeData(client);
        return transformDropRateVO(list,dto);
    }

    @Override
    public DayDropRateVO dayInfo(String client, DistributeGoodsDto dto) {
        DayDropRateVO dayDropRateVO;
        try {
            //查询缓存 todo

            dayDropRateVO = new DayDropRateVO();
            Map<String, Object> params = new HashMap<>();
            Date currentDate = new Date();
            params.put("client", client);
            params.put("tjType", XhlTypeEnum.FINAL_XHL.type);
            params.put("startTime", DateUtil.formatDate(DateUtils.addDays(currentDate,-7),"yyyy-MM-dd 00:00:00"));
            params.put("endTime", DateUtil.formatDate(DateUtils.addDays(currentDate, -1), "yyyy-MM-dd 23:59:59"));
            //table数据
            List<GaiaXhlH> weekRate = gaiaXhlHMapper.getWeekRate(params);

            List<BaseRate> rateList = getDayDropRateTableData(weekRate,dto);
            //过滤
            if(dto.getTag()==0){
                // 显示数据少需要过滤
                List<BaseRate> collect = rateList.stream().filter(baseRate -> baseRate.getQuantityRate().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
                dayDropRateVO.setRateTables(collect);
            }else {
                dayDropRateVO.setRateTables(rateList);
            }
            //获取行业平均值
            List<GaiaXhlH> averageRate = gaiaXhlHMapper.getWeekAverageRate(params);
            //chart数据
            List<BaseRateChart> chartList = getDayDropRateChartData(averageRate, weekRate,dto);
            //过滤
            List<BaseRateChart> collect = chartList.stream().filter(s -> s.getProductRate().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            if(dto.getTag()==0){
                dayDropRateVO.setRateCharts(collect);
            }else {
                dayDropRateVO.setRateCharts(chartList);
            }

        } catch (Exception e) {
            throw new BusinessException(String.format("查询周下货率情况异常:%s", e.getMessage()), e);
        }
        return dayDropRateVO;
    }

    private List<BaseRateChart> getDayDropRateChartData(List<GaiaXhlH> averageRate, List<GaiaXhlH> weekRate, DistributeGoodsDto dto) {
        List<BaseRateChart> list = new ArrayList<>();
        for (GaiaXhlH gaiaXhlH : weekRate) {
            GaiaXhlH average = averageRate.stream().filter(a -> DateUtil.formatDate(a.getTjDate()).equals(DateUtil.formatDate(gaiaXhlH.getTjDate()))).findFirst().get();
            BaseRateChart rateChart = new BaseRateChart();
            rateChart.setDate(DateUtil.formatDate(gaiaXhlH.getTjDate(), "MM月dd日"));
            if(dto.getTag()!=0){
                // 全部方式
                rateChart.setProductRate(gaiaXhlH.getProductRate().setScale(0, RoundingMode.HALF_UP));
            }else {
                rateChart.setProductRate(gaiaXhlH.getProductRateLess().setScale(0, RoundingMode.HALF_UP));
            }
            //随机数
            rateChart.setAverageRate(average.getIndustryAverage().setScale(0, RoundingMode.HALF_UP));
            list.add(rateChart);
        }
        return list;
    }

    private List<BaseRate> getDayDropRateTableData(List<GaiaXhlH> weekRate, DistributeGoodsDto dto) {
        List<BaseRate> list = new ArrayList<>();
        for (GaiaXhlH gaiaXhlH : weekRate) {
            BaseRate baseRate = new BaseRate();
            baseRate.setDate(DateUtil.formatDate(gaiaXhlH.getTjDate(), "MM月dd日"));
            baseRate.setWeek(DateUtil.date2Week(gaiaXhlH.getTjDate()));
            if(dto.getTag()!=0){
                baseRate.setQuantityRate(gaiaXhlH.getQuantityRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                baseRate.setAmountRate(gaiaXhlH.getAmountRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                baseRate.setProductRate(gaiaXhlH.getProductRate().setScale(0, BigDecimal.ROUND_HALF_UP));
            }else {
                baseRate.setQuantityRate(gaiaXhlH.getQuantityRateLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                baseRate.setAmountRate(gaiaXhlH.getAmountRateLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                baseRate.setProductRate(gaiaXhlH.getProductRateLess().setScale(0, BigDecimal.ROUND_HALF_UP));
            }

            list.add(baseRate);
        }
        return list;
    }

    @Override
    public WeekDropRateVO weekInfo(String client, DistributeGoodsDto dto) {
        WeekDropRateVO weekDropRateVO = new WeekDropRateVO();
        //table数据
        Map<String, Object> weekParams = new HashMap<>();
        GaiaCalDate gaiaCalDate = gaiaCalDateService.getByDate(DateUtil.formatDate(new Date(), "yyyyMMdd"));
        int currentWeek;
        if (gaiaCalDate != null) {
            currentWeek = Integer.parseInt(gaiaCalDate.getGcdWeek());
        } else {
            currentWeek = DateUtil.weekOfYear(new Date());
        }
        if (currentWeek == 1) {
            return weekDropRateVO;
        }
        weekParams.put("client", client);
        weekParams.put("tjType", XhlTypeEnum.FINAL_XHL.type);
        weekParams.put("year", DateUtil.formatDate(new Date(), "yyyy"));
        weekParams.put("startWeekNum", currentWeek > 7 ? currentWeek - 7 : 1);
        weekParams.put("endWeekNum", currentWeek - 1);
        //查询sql
        List<GaiaXhlH> before7WeekRate = gaiaXhlHMapper.getBefore7WeekRate(weekParams);
        //数据转换
        List<BaseRate> rateTables = getBefore7WeekTableData(before7WeekRate,dto);
        rateTables.removeIf(next -> BigDecimal.ZERO.compareTo(next.getAmountRate()) == 0 && BigDecimal.ZERO.compareTo(next.getQuantityRate()) == 0
                && BigDecimal.ZERO.compareTo(next.getProductRate()) == 0);
        weekDropRateVO.setRateTables(rateTables);

        //获取行业平均值
        weekParams.remove("client");
        List<GaiaXhlH> averageRate = gaiaXhlHMapper.getBefore7WeekRate(weekParams);
        //chart数据
        List<BaseRateChart> rateCharts = getBefore7WeekChartData(averageRate, before7WeekRate,dto);

        rateCharts.removeIf(next -> BigDecimal.ZERO.compareTo(next.getProductRate()) == 0);
        weekDropRateVO.setRateCharts(rateCharts);
        return weekDropRateVO;
    }

    private List<BaseRateChart> getBefore7WeekChartData(List<GaiaXhlH> averageRate, List<GaiaXhlH> before7WeekRate, DistributeGoodsDto dto) {
        List<BaseRateChart> list = new ArrayList<>();
        for (GaiaXhlH gaiaXhlH : before7WeekRate) {
            GaiaXhlH average = averageRate.stream().filter(a -> a.getWeekNum().equals(gaiaXhlH.getWeekNum())).findFirst().get();
            BaseRateChart rateChart = new BaseRateChart();
            rateChart.setDate(String.format("%d周", gaiaXhlH.getWeekNum()));
            if(dto.getTag()!=0){
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNum()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpProductNum().divide(gaiaXhlH.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    rateChart.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }else {
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNum()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpProductNum().divide(gaiaXhlH.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    rateChart.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }

           /* if (BigDecimal.ZERO.compareTo(average.getDownProductNum()) < 0) {
                rateChart.setAverageRate(average.getUpProductNum().divide(average.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }*/
            rateChart.setAverageRate(average.getIndustryAverage().setScale(0, BigDecimal.ROUND_HALF_UP));
            list.add(rateChart);
        }
        return list;
    }

    private List<BaseRate> getBefore7WeekTableData(List<GaiaXhlH> before7WeekRate, DistributeGoodsDto dto) {
        List<BaseRate> list = new ArrayList<>();
        for (GaiaXhlH gaiaXhlH : before7WeekRate) {
            BaseRate baseRate = new BaseRate();
            baseRate.setWeek(String.format("%d周", gaiaXhlH.getWeekNum()));
            if(dto.getTag()!=0){
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNum()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpProductNum().divide(gaiaXhlH.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2);
                    baseRate.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownAmount()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpAmount().divide(gaiaXhlH.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2);
                    baseRate.setAmountRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownQuantity()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpQuantity().divide(gaiaXhlH.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2);
                    baseRate.setQuantityRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }else {
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNumLess()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpProductNumLess().divide(gaiaXhlH.getDownProductNumLess(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2);
                    baseRate.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownAmountLess()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpAmountLess().divide(gaiaXhlH.getDownAmountLess(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2);
                    baseRate.setAmountRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownQuantityLess()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpQuantityLess().divide(gaiaXhlH.getDownQuantityLess(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2);
                    baseRate.setQuantityRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }

            list.add(baseRate);
        }
        return list;
    }

    @Override
    public MonthDropRateVO monthInfo(String client, DistributeGoodsDto dto) {
        MonthDropRateVO monthDropRateVO = new MonthDropRateVO();
        //下货率table
        Map<String, Object> monthParams = new HashMap<>();
        monthParams.put("client", client);
        monthParams.put("tjType", XhlTypeEnum.FINAL_XHL.type);
        monthParams.put("year", Calendar.getInstance().get(Calendar.YEAR));
        //增加返回值
        List<GaiaXhlH> currentYearRate = gaiaXhlHMapper.getCurrentYearRate(monthParams);
        //处理值
        List<BaseRate> tableRates = getYearTableRateData(currentYearRate,dto);
        if(dto.getTag()==0){
            List<BaseRate> collect = tableRates.stream().filter(s -> s.getQuantityRate().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            monthDropRateVO.setRateTables(collect);
        }else {
            monthDropRateVO.setRateTables(tableRates);
        }
        //下货率charts
        monthParams.remove("client");
        List<GaiaXhlH> averageRates = gaiaXhlHMapper.getCurrentYearRate(monthParams);

        List<BaseRateChart> rateCharts = getYearChartRateData(averageRates, currentYearRate,dto);
        if(dto.getTag()==0){
            List<BaseRateChart> collect = rateCharts.stream().filter(s -> s.getProductRate().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            monthDropRateVO.setRateCharts(collect);
        }else {
            monthDropRateVO.setRateCharts(rateCharts);
        }
        return monthDropRateVO;
    }

    @Transactional
    @Override
    public void updateViewSource(String client, Integer type) {
        try {
            List<GaiaXhlH> xhlHList = gaiaXhlHMapper.getLastTimeData(client);
            if (xhlHList != null) {
                for (GaiaXhlH gaiaXhlH : xhlHList) {
                    if (gaiaXhlH.getViewSource() == 0) {
                        gaiaXhlH.setViewSource(type);
                        gaiaXhlHMapper.update(gaiaXhlH);
                    }
                }
            }
        } catch (Exception e) {
            log.error("<下货率><更新查看><更新下货率查看标记异常,>", e);
        }
    }

    private List<BaseRateChart> getYearChartRateData(List<GaiaXhlH> averageRates, List<GaiaXhlH> monthRates, DistributeGoodsDto dto) {
        List<BaseRateChart> list = new ArrayList<>();
        if (monthRates == null || monthRates.size() == 0) {
            return list;
        }
        for (GaiaXhlH monthRate : monthRates) {
            BaseRateChart rateChart = new BaseRateChart();
            GaiaXhlH average = averageRates.stream().filter(a -> a.getYearName().equals(monthRate.getYearName())).findFirst().get();
            rateChart.setDate(String.format("%s月", monthRate.getYearName()));
            if(dto.getTag()!=0){
                if (BigDecimal.ZERO.compareTo(monthRate.getDownProductNum()) < 0) {
                    BigDecimal bigDecimal = monthRate.getUpProductNum().divide(monthRate.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    rateChart.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }else {
                if (BigDecimal.ZERO.compareTo(monthRate.getDownProductNumLess()) < 0) {
                    BigDecimal bigDecimal = monthRate.getUpProductNumLess().divide(monthRate.getDownProductNumLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    rateChart.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }
            rateChart.setAverageRate(average.getIndustryAverage().setScale(0, BigDecimal.ROUND_HALF_UP));
           /* if (BigDecimal.ZERO.compareTo(average.getDownProductNum()) < 0) {
                rateChart.setAverageRate(average.getUpProductNum().divide(average.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2));
            }*/
            list.add(rateChart);
        }
        return list;
    }

    private List<BaseRate> getYearTableRateData(List<GaiaXhlH> currentYearRate, DistributeGoodsDto dto) {
        List<BaseRate> list = new ArrayList<>();
        if (currentYearRate == null || currentYearRate.size() == 0) {
            return list;
        }
        for (GaiaXhlH gaiaXhlH : currentYearRate) {
            BaseRate baseRate = new BaseRate();
            baseRate.setMonth(String.format("%s月", gaiaXhlH.getYearName()));
            if(dto.getTag()!=0){
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownQuantity()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpQuantity().divide(gaiaXhlH.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    baseRate.setQuantityRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownAmount()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpAmount().divide(gaiaXhlH.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    baseRate.setAmountRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNum()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpProductNum().divide(gaiaXhlH.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    baseRate.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }else {
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownQuantityLess()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpQuantityLess().divide(gaiaXhlH.getDownQuantityLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    baseRate.setQuantityRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownAmountLess()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpAmountLess().divide(gaiaXhlH.getDownAmountLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    baseRate.setAmountRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNumLess()) < 0) {
                    BigDecimal bigDecimal = gaiaXhlH.getUpProductNumLess().divide(gaiaXhlH.getDownProductNumLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2);
                    baseRate.setProductRate(bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP));
                }
            }


            list.add(baseRate);
        }
        return list;
    }

    private DropRateVO transformDropRateVO(List<GaiaXhlH> list, DistributeGoodsDto dto) {
        DropRateVO dropRateVO = new DropRateVO();
        if (list == null || list.size() == 0) {
            return dropRateVO;
        }
        GaiaXhlH gaiaXhlH = list.get(0);
        dropRateVO.setDate(DateUtil.formatDate(gaiaXhlH.getTjDate(), "yyyy-MM-dd"));
        dropRateVO.setWeek(DateUtil.date2Week(gaiaXhlH.getTjDate()));
        List<DropRateVO.BaseRateInfo> rateInfoList = new ArrayList<>();
        //过滤存在为0情况
        //List<GaiaXhlH> collect = list.stream().filter(s -> s.getUpQuantity().compareTo(BigDecimal.ZERO) > 0 && s.getQuantityRateLess().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

        for (GaiaXhlH xhlH : list) {
            DropRateVO.BaseRateInfo rateInfo = new DropRateVO.BaseRateInfo();
            rateInfo.setRateType(xhlH.getTjType());
            rateInfo.setRateDate(DateUtil.formatDate(xhlH.getTjDate()));
            //   0 不勾选 勾选范围小   1是勾选
            if (dto.getTag()!=0) {
                rateInfo.setQuantityRate(xhlH.getQuantityRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                rateInfo.setAmountRate(xhlH.getAmountRate().setScale(0, BigDecimal.ROUND_HALF_UP));
                rateInfo.setProductRate(xhlH.getProductRate().setScale(0, BigDecimal.ROUND_HALF_UP));
            } else {
                rateInfo.setQuantityRate(xhlH.getQuantityRateLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                rateInfo.setAmountRate(xhlH.getAmountRateLess().setScale(0, BigDecimal.ROUND_HALF_UP));
                rateInfo.setProductRate(xhlH.getProductRateLess().setScale(0, BigDecimal.ROUND_HALF_UP));
            }
            rateInfoList.add(rateInfo);
        }
        if(dto.getTag()==0){
            //展示少 过滤为0   展示多不用过滤
            List<DropRateVO.BaseRateInfo> collect = rateInfoList.stream().filter(s -> s.getQuantityRate().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            dropRateVO.setRateInfoList(collect);
        }else {
            dropRateVO.setRateInfoList(rateInfoList);
        }

        return dropRateVO;
    }


}

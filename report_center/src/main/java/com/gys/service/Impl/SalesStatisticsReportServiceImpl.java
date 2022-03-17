package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.gys.common.constant.CommonConstant;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.data.SalesStatisticsReportInData;
import com.gys.common.data.SalesStatisticsReportOutData;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.SalesStatisticsReportMapper;
import com.gys.service.SalesStatisticsReportService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.DateUtil;
import com.gys.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalesStatisticsReportServiceImpl implements SalesStatisticsReportService {
    @Resource
    private SalesStatisticsReportMapper salesStatisticsReportMapper;

    @Autowired
    private CosUtils cosUtils;

    @Override
    public PageInfo<SalesStatisticsReportOutData> query(SalesStatisticsReportInData inData) {
        log.info("[销售分析]报表查询开始,入参：{}", JSONObject.toJSONString(inData));
        // 数据校验
        checkInputData(inData);
        // 查询数据
        List<SalesStatisticsReportOutData> resultList = queryData(inData);
        // 分页处理
        PageInfo pageInfo;
        if (CollectionUtils.isEmpty(resultList)) {
            pageInfo = new PageInfo(new ArrayList());
        } else {
            int count = resultList.size();
            int startNum = (inData.getPageNum() - 1) * inData.getPageSize();
            int endNum = inData.getPageNum() * inData.getPageSize() > count ? count : inData.getPageNum() * inData.getPageSize();
            pageInfo = new PageInfo(resultList.subList(startNum, endNum));
            pageInfo.setTotal(count);
            pageInfo.setPageNum(inData.getPageNum());
            pageInfo.setPageSize(inData.getPageSize());
        }
        log.info("[销售分析]报表查询结束");

        return pageInfo;
    }

    /**
     * @Author jiht
     * @Description 录入校验
     * @Date 2022/1/4 11:29
     * @Param [inData]
     * @Return void
     **/
    private void checkInputData(SalesStatisticsReportInData inData) {
        if (StringUtils.isEmpty(inData.getClientId())) {
            throw new BusinessException("提示：加盟商查询失败!");
        }
        if (StringUtils.isEmpty(inData.getReportDimension())) {
            throw new BusinessException("提示：请选择报表维度!");
        }
        if (StringUtils.isEmpty(inData.getReportType())) {
            throw new BusinessException("提示：请选择报表类型!");
        }
        if (StringUtils.isEmpty(inData.getStartDate()) || StringUtils.isEmpty(inData.getEndDate())) {
            throw new BusinessException("提示：请录入查询区间!");
        }
    }

    /**
     * @Author jiht
     * @Description 查询数据
     * @Date 2022/1/4 1:45
     * @Param []
     * @Return void
     **/
    private List<SalesStatisticsReportOutData> queryData(SalesStatisticsReportInData inData) {
        Long now = new Date().getTime();
        log.info("[销售分析]分类处理开始，日期 ：{}", new Date());
        // 根据商品分类查询时，直接取分类即可
        if (!CollectionUtils.isEmpty(inData.getProClass())) {
            inData.setProClassBak(new ArrayList<String>());
            inData.getProClass().stream().forEach(o -> inData.getProClassBak().add(o[2]));
        }
        // 查询商品分类定义数据
        Map<String, String> proClassMap = proClassMap();
        // 查询商品定义数据
        Map<String, String> proPrositionMap = proPrositionMap();
        // 查询毛利区间定义数据
        Map<String, Map<String, String>> profitIntervalMap = profitIntervalMap(inData);
        // 查询统计区间销售基础数据
        List<SalesStatisticsReportOutData> basicSaleData = salesStatisticsReportMapper.queryBasicSaleData(inData);
        // 取分类最大值
        List<String> maxKeys = getMaxKeys(basicSaleData, inData, profitIntervalMap);
        // 分隔统计区间
        List<Map<String, String>> dateMapList = calDateInterval(inData.getStartDate(), inData.getEndDate(), inData.getReportDimension());
        log.info("[销售分析]日期间隔划分结果：{}", JSONObject.toJSONString(dateMapList));
        log.info("[销售分析]基础数据准备耗时 ：{}秒", (new Date().getTime() - now) / 1000.00);
        now = new Date().getTime();

        // 最终结果集
        List<SalesStatisticsReportOutData> finalResultList = new ArrayList<>();
        Map<String, List<SalesStatisticsReportOutData>> resultListMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch taskLatch = new CountDownLatch(dateMapList.size());
        // 循环遍历处理每个区间的数据
        dateMapList.stream().forEach(o -> {
            log.info("[销售分析]报表计算区间:{},开始日期:{}，结束日期：{}", o.get("title"), o.get("startDate"), o.get("endDate"));
            // 多线程处理
            executorService.submit(() -> dealIntervalData(o, inData, basicSaleData, profitIntervalMap, proClassMap, proPrositionMap, maxKeys, resultListMap, taskLatch));
        });

        // 等待所有线程执行完成
        while (taskLatch.getCount() > 0) {
            try {
                log.info("[销售分析]剩余{}个区间未处理完成", taskLatch.getCount());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 按顺序加入最终结果集
        dateMapList.stream().forEach(o -> finalResultList.addAll(resultListMap.get(o.get("title"))));
        // 毛利额、销售额、医保销售额 万元处理
        finalResultList.stream().forEach(o -> {
            o.setSaleAmt(new BigDecimal(o.getSaleAmt()).abs().compareTo(new BigDecimal(10000)) < 0 ? o.getSaleAmt() : String.valueOf(new BigDecimal(o.getSaleAmt()).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP)) + "万");
            o.setProfit(new BigDecimal(o.getProfit()).abs().compareTo(new BigDecimal(10000)) < 0 ? o.getProfit() : String.valueOf(new BigDecimal(o.getProfit()).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP)) + "万");
            o.setEbAmt(new BigDecimal(o.getEbAmt()).abs().compareTo(new BigDecimal(10000)) < 0 ? o.getEbAmt() : String.valueOf(new BigDecimal(o.getEbAmt()).divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_UP)) + "万");
            // 毛利区间转中文
            if ("2".equals(inData.getReportSecondType()) && !"小计".equals(o.getProfitInterval())) {
                o.setProfitInterval(profitIntervalMap.get(o.getProfitInterval()) == null ? "" : profitIntervalMap.get(o.getProfitInterval()).get("remark"));
            }
        });
        log.info("[销售分析]内存数据处理合计耗时 ：{}秒", (new Date().getTime() - now) / 1000.00);
        log.info("[销售分析]金额进行万元处理完成");

        return finalResultList;
    }

    /**
     * @Author jiht
     * @Description 处理一个区间的数据
     * @Date 2022/1/11 15:50
     * @Param [o, inData, basicSaleData, profitIntervalMap, proClassMap, proPrositionMap, maxKeys, resultList]
     * @Return void
     **/
    private void dealIntervalData(Map<String, String> o, SalesStatisticsReportInData inData, List<SalesStatisticsReportOutData> basicSaleData, Map<String, Map<String, String>> profitIntervalMap, Map<String, String> proClassMap, Map<String, String> proPrositionMap, List<String> maxKeys, Map<String, List<SalesStatisticsReportOutData>> resultListMap, CountDownLatch taskLatch) {
        Long now2 = new Date().getTime();
        String tempStartDate = o.get("startDate");
        String tempEndDate = o.get("endDate");
        List<SalesStatisticsReportOutData> resultList = new ArrayList<>();
        // 过滤遍历区间的数据
        List<SalesStatisticsReportOutData> filterBasicSaleData = basicSaleData.stream().filter(basic ->
                (DateUtil.getDiffByDateNew(tempStartDate, basic.getDate()) >= 0 && DateUtil.getDiffByDateNew(basic.getDate(), tempEndDate) >= 0)).collect(Collectors.toList());

        // 毛利区间需要先按照商品汇总,并计算毛利区间
        List<SalesStatisticsReportOutData> proSumData = dealProGroupBy(filterBasicSaleData, profitIntervalMap);
        // 按照分类进行分组
        Map<String, List<SalesStatisticsReportOutData>> groupByData = dealGroupBy(proSumData, inData);
        Map<String, List<SalesStatisticsReportOutData>> totalGroupByData = dealGroupBy(filterBasicSaleData, inData);

        // 每个分类进行汇总计算
        Map<String, SalesStatisticsReportOutData> groupByDataMap = new HashMap<>();
        groupByData.forEach((k, v) -> {
            SalesStatisticsReportOutData sumData = calSumData(v, o.get("title"), totalGroupByData.get(k));
            // 设置分类名称、毛利区间
            setReportClass(sumData, k, inData, proClassMap);
            // 设置定位名称
            sumData.setPosition(StringUtils.isEmpty(sumData.getPosition()) ? "" : sumData.getPosition() + proPrositionMap.get(sumData.getPosition()));
            // 销售占比:只汇总查询时，销售占比应该为1
            if("1".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())){
                sumData.setSaleRate("100");
            }
            groupByDataMap.put(k, sumData);
        });
        log.info("[销售分析]报表计算区间:{},分类计算完成，合计：{}条", o.get("title"), groupByDataMap.keySet().size());

        // 按照分类最大分组，月份下不存在的，则填上空值
        fillNotHasKeyData(resultList, groupByDataMap, maxKeys, inData, proClassMap, proPrositionMap, o.get("title"));

        // 单纯汇总查询时，无需小计
        if ((!"1".equals(inData.getReportType()) || !StringUtils.isEmpty(inData.getReportSecondType())) && groupByDataMap.keySet().size() >= 1) {
            // 合计计算
            SalesStatisticsReportOutData sumData = calSumData(proSumData, o.get("title"), filterBasicSaleData);
            sumData.setSaleRate("100");
            sumData.setProBigClassCode("小计");
            sumData.setProBigClassName("小计");
            sumData.setProMidClassCode("小计");
            sumData.setProMidClassName("小计");
            sumData.setProClassCode("小计");
            sumData.setProClassName("小计");
            sumData.setPosition("小计");
            sumData.setProfitInterval("小计");
            // 销售占比:销售额/计算范围内的销售额之和*100%
            groupByDataMap.values().stream().forEach(k -> {
                BigDecimal divideNumber = new BigDecimal(sumData.getSaleAmt());
                BigDecimal saleRate = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(k.getSaleAmt()).multiply(new BigDecimal(100)).divide(divideNumber, 1, BigDecimal.ROUND_HALF_UP);
                k.setSaleRate(String.valueOf(saleRate));
            });
            resultList.add(sumData);
            log.info("[销售分析]报表计算区间:{},汇总计算完成", o.get("title"));
        }
        resultListMap.put(o.get("title"), resultList);
        log.info("[销售分析]报表计算区间:{},区间数据处理耗时 ：{}秒", o.get("title"), (new Date().getTime() - now2) / 1000.00);

        // 执行完一个，计数器减一
        taskLatch.countDown();
    }

    /**
     * @Author jiht
     * @Description 按照分类最大分组，月份下不存在的，则填上空值
     * @Date 2022/1/6 14:43
     * @Param [resultList, groupByDataList, maxKeys]
     * @Return void
     **/
    private void fillNotHasKeyData(List<SalesStatisticsReportOutData> resultList, Map<String, SalesStatisticsReportOutData> groupByDataList, List<String> maxKeys, SalesStatisticsReportInData inData, Map<String, String> proClassMap, Map<String, String> proPrositionMap, String calDate) {
        maxKeys.stream().forEach(o -> {
            if (groupByDataList.get(o) != null) {
                resultList.add(groupByDataList.get(o));
            } else {
                SalesStatisticsReportOutData sumData = new SalesStatisticsReportOutData();
                // 设置分类名称、毛利区间
                setReportClass(sumData, o, inData, proClassMap);
                // 设置定位名称
                sumData.setPosition(StringUtils.isEmpty(sumData.getPosition()) ? "" : sumData.getPosition() + proPrositionMap.get(sumData.getPosition()));
                sumData.setDate(calDate);
                resultList.add(sumData);
            }
        });
    }

    /**
     * @Author jiht
     * @Description 获取查询区间最大分类
     * @Date 2022/1/6 14:47
     * @Param [basicSaleData, inData]
     * @Return java.util.List<java.lang.String>
     **/
    private List<String> getMaxKeys(List<SalesStatisticsReportOutData> basicSaleData, SalesStatisticsReportInData inData, Map<String, Map<String, String>> profitIntervalMap) {
        List<String> maxKeys = new ArrayList<>();

        if ("1".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            maxKeys.add("");
        } else if ("2".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getProBigClassCode()).distinct().collect(Collectors.toList());
        } else if ("3".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode()).distinct().collect(Collectors.toList());
        } else if ("4".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProClassCode()).distinct().collect(Collectors.toList());
        } else if ("1".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getPosition()).distinct().collect(Collectors.toList());
        } else if ("2".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getPosition()).distinct().collect(Collectors.toList());
        } else if ("3".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getPosition()).distinct().collect(Collectors.toList());
        } else if ("4".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            maxKeys = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProClassCode() + "-" + k.getPosition()).distinct().collect(Collectors.toList());
        } else if ("1".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            maxKeys = new ArrayList<>(profitIntervalMap.keySet());
        } else if ("2".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            List<String> basicGroup = basicSaleData.stream().map(k -> k.getProBigClassCode()).distinct().collect(Collectors.toList());
            for (String basic : basicGroup) {
                for (String profit : profitIntervalMap.keySet()) {
                    maxKeys.add(basic + "-" + profit);
                }
            }
        } else if ("3".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            List<String> basicGroup = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode()).distinct().collect(Collectors.toList());
            for (String basic : basicGroup) {
                for (String profit : profitIntervalMap.keySet()) {
                    maxKeys.add(basic + "-" + profit);
                }
            }
        } else if ("4".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            List<String> basicGroup = basicSaleData.stream().map(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProClassCode()).distinct().collect(Collectors.toList());
            for (String basic : basicGroup) {
                for (String profit : profitIntervalMap.keySet()) {
                    maxKeys.add(basic + "-" + profit);
                }
            }
        }

        // 排序
        maxKeys = maxKeys.stream().sorted().collect(Collectors.toList());

        return maxKeys;
    }

    /**
     * @Author jiht
     * @Description 查询商品分类信息
     * @Date 2022/1/4 0:18
     * @Param []
     * @Return java.util.Map<java.lang.String,java.lang.String>
     **/
    private Map<String, String> proClassMap() {
        Map<String, String> proClassMap = new HashMap<>();
        List<Map<String, String>> proClassList = salesStatisticsReportMapper.queryProClass();
        proClassList.stream().forEach(o -> {
            proClassMap.put(o.get("proClass"), o.get("proClassName"));
        });
        return proClassMap;
    }

    /**
     * @Author jiht
     * @Description 查询商品分类信息
     * @Date 2022/1/4 0:18
     * @Param []
     * @Return java.util.Map<java.lang.String,java.lang.String>
     **/
    private Map<String, String> proPrositionMap() {
        Map<String, String> proPrositionMap = new HashMap<>();
        List<Map<String, String>> proPrositionList = salesStatisticsReportMapper.queryProProsition();
        proPrositionList.stream().forEach(o -> {
            proPrositionMap.put(o.get("code"), o.get("name"));
        });
        return proPrositionMap;
    }

    /**
     * @Author jiht
     * @Description 查询商品分类信息
     * @Date 2022/1/4 0:18
     * @Param []
     * @Return java.util.Map<java.lang.String,java.lang.String>
     **/
    private Map<String, Map<String, String>> profitIntervalMap(SalesStatisticsReportInData inData) {
        Map<String, Map<String, String>> profitIntervalMap = new HashMap<>();
        List<Map<String, String>> proPrositionList = salesStatisticsReportMapper.queryProfitInterval(inData);
        proPrositionList.stream().forEach(o -> {
            profitIntervalMap.put(o.get("interbalType"), o);
        });
        return profitIntervalMap;
    }

    /**
     * @Author jiht
     * @Description 合计计算
     * @Date 2022/1/3 21:18
     * @Param [groupByDataList]
     * @Return com.gys.common.data.SalesStatisticsReportOutData
     **/
    private SalesStatisticsReportOutData calSumData(List<SalesStatisticsReportOutData> groupByDataList, String calDate, List<SalesStatisticsReportOutData> totalGroupByDataList) {
        SalesStatisticsReportOutData sumData = new SalesStatisticsReportOutData();
        groupByDataList.stream().forEach(o -> {
            // 销售量
            sumData.setSaleQty(String.valueOf(new BigDecimal(sumData.getSaleQty()).add(new BigDecimal(o.getSaleQty())).setScale(2, BigDecimal.ROUND_HALF_UP)));
            // 销售额
            sumData.setSaleAmt(String.valueOf(new BigDecimal(sumData.getSaleAmt()).add(new BigDecimal(o.getSaleAmt())).setScale(2, BigDecimal.ROUND_HALF_UP)));
            // 毛利额
            sumData.setProfit(String.valueOf(new BigDecimal(sumData.getProfit()).add(new BigDecimal(o.getProfit())).setScale(2, BigDecimal.ROUND_HALF_UP)));
            // 折扣金额
            sumData.setZkAmt(String.valueOf(new BigDecimal(sumData.getZkAmt()).add(new BigDecimal(o.getZkAmt())).setScale(2, BigDecimal.ROUND_HALF_UP)));
            // 医保金额
            sumData.setEbAmt(String.valueOf(new BigDecimal(sumData.getEbAmt()).add(new BigDecimal(o.getEbAmt())).setScale(2, BigDecimal.ROUND_HALF_UP)));
        });

        // 动销门店数 todo 应该要先分组求和
        Set<String> moveStos = totalGroupByDataList.stream().filter(o -> new BigDecimal(o.getSaleQty()).compareTo(BigDecimal.ZERO) > 0).map(o -> o.getStoCode()).collect(Collectors.toSet());
        sumData.setMovStos(String.valueOf(moveStos.size()));
        // 动销品项
        Set<String> movePros = groupByDataList.stream().filter(o -> new BigDecimal(o.getSaleQty()).compareTo(BigDecimal.ZERO) > 0).map(o -> o.getProSelfCode()).collect(Collectors.toSet());
        sumData.setMovItems(String.valueOf(CollectionUtils.isEmpty(movePros) ? 0 : movePros.size()));
        /**
         * 计算 毛利率、折扣率、医保销售占比、单品平均售价、客单价、日均交易次数、客单品项数
         */
        BigDecimal divideNumber;
        // 毛利率:毛利额/销售额*100%
        divideNumber = new BigDecimal(sumData.getSaleAmt());
        BigDecimal profitRate = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(sumData.getProfit()).multiply(new BigDecimal(100)).divide(divideNumber, 1, BigDecimal.ROUND_HALF_UP);
        sumData.setProfitRate(String.valueOf(profitRate));
        // 折扣率:商品折扣总金额 GSSD_ZK_AMT/（销售额  GSSD_AMT+商品折扣总金额 GSSD_ZK_AMT）*100%
        divideNumber = new BigDecimal(sumData.getSaleAmt()).add(new BigDecimal(sumData.getZkAmt()));
        BigDecimal zkRate = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(sumData.getZkAmt()).multiply(new BigDecimal(100)).divide(divideNumber, 1, BigDecimal.ROUND_HALF_UP);
        sumData.setDiscountRate(String.valueOf(zkRate));
        // 医保销售占比:医保销售额/销售额*100%
        divideNumber = new BigDecimal(sumData.getSaleAmt());
        BigDecimal ebRate = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(sumData.getEbAmt()).multiply(new BigDecimal(100)).divide(divideNumber, 1, BigDecimal.ROUND_HALF_UP);
        sumData.setEbSaleRate(String.valueOf(ebRate));
        // 单品平均售价:销售额/销售量
        divideNumber = new BigDecimal(sumData.getSaleQty());
        BigDecimal avgProPrice = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(sumData.getSaleAmt()).divide(divideNumber, 2, BigDecimal.ROUND_HALF_UP);
        sumData.setAvgProPrice(String.valueOf(avgProPrice));
        // 客单价:销售额/总交易次数(总交易次数：维度+销售单号（GSSD_BILL_NO）+销售日期去重后计数)
        Set<String> totalTradeCount = totalGroupByDataList.stream().map(o -> o.getDate() + "-" + o.getStoCode() + "-" + o.getBillNo()).collect(Collectors.toSet());
        divideNumber = new BigDecimal(totalTradeCount.size());
        BigDecimal avgCusPrice = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(sumData.getSaleAmt()).divide(divideNumber, 2, BigDecimal.ROUND_HALF_UP);
        sumData.setAvgCusPrice(String.valueOf(avgCusPrice));
        // 日均交易次数:总交易次数/总销售天数(总交易次数：维度+销售单号（GSSD_BILL_NO）+销售日期去重后计数,总销售天数：加盟商+销售日期（GSSD_DATE）+门店ID（GSSD_BR_ID）去重后计数)
        Set<String> totalSaleDays = totalGroupByDataList.stream().map(o -> o.getDate() + "-" + o.getStoCode()).collect(Collectors.toSet());
        divideNumber = new BigDecimal(totalSaleDays.size());
        BigDecimal avgDailyTradeTimes = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(totalTradeCount.size()).divide(divideNumber, 0, BigDecimal.ROUND_HALF_UP);
        sumData.setAvgDailyTradeTimes(String.valueOf(avgDailyTradeTimes));
        // 客单品项数:交易品项总数/总交易次数(交易品项总数：GSSD_BILL_NO+GSSD_PRO_ID去重后计数)
        Set<String> totalTradeItems = totalGroupByDataList.stream().map(o -> o.getBillNo() + "-" + o.getProSelfCode()).collect(Collectors.toSet());
        divideNumber = new BigDecimal(totalTradeCount.size());
        BigDecimal avgCusItem = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(totalTradeItems.size()).divide(divideNumber, 2, BigDecimal.ROUND_HALF_UP);
        sumData.setAvgCusItem(String.valueOf(avgCusItem));

        // 设置时间
        sumData.setDate(calDate);

        return sumData;
    }

    /**
     * @Author jiht
     * @Description 根据报表维度设置报表
     * @Date 2022/1/3 23:57
     * @Param []
     * @Return void
     **/
    private void setReportClass(SalesStatisticsReportOutData sumData, String reportClass, SalesStatisticsReportInData inData, Map<String, String> proClassMap) {
        String[] reportClassArr = reportClass.split("-");
        if ("1".equals(inData.getReportType())) {
            if ("1".equals(inData.getReportSecondType())) {
                sumData.setPosition(reportClassArr.length < 1 ? "" : reportClassArr[0]);
            } else if ("2".equals(inData.getReportSecondType())) {
                sumData.setProfitInterval(reportClassArr.length < 1 ? "" : reportClassArr[0]);
            }
        } else if ("2".equals(inData.getReportType())) {
            sumData.setProBigClassCode(reportClassArr.length < 1 ? "" : reportClassArr[0]);
            sumData.setProBigClassName(reportClassArr.length < 1 ? "" : proClassMap.get(reportClassArr[0]));
            if ("1".equals(inData.getReportSecondType())) {
                sumData.setPosition(reportClassArr.length < 2 ? "" : reportClassArr[1]);
            } else if ("2".equals(inData.getReportSecondType())) {
                sumData.setProfitInterval(reportClassArr.length < 2 ? "" : reportClassArr[1]);
            }
        } else if ("3".equals(inData.getReportType())) {
            sumData.setProBigClassCode(reportClassArr.length < 1 ? "" : reportClassArr[0]);
            sumData.setProBigClassName(reportClassArr.length < 1 ? "" : proClassMap.get(reportClassArr[0]));
            sumData.setProMidClassCode(reportClassArr.length < 2 ? "" : reportClassArr[1]);
            sumData.setProMidClassName(reportClassArr.length < 2 ? "" : proClassMap.get(reportClassArr[1]));
            if ("1".equals(inData.getReportSecondType())) {
                sumData.setPosition(reportClassArr.length < 3 ? "" : reportClassArr[2]);
            } else if ("2".equals(inData.getReportSecondType())) {
                sumData.setProfitInterval(reportClassArr.length < 3 ? "" : reportClassArr[2]);
            }
        } else if ("4".equals(inData.getReportType())) {
            sumData.setProBigClassCode(reportClassArr.length < 1 ? "" : reportClassArr[0]);
            sumData.setProBigClassName(reportClassArr.length < 1 ? "" : proClassMap.get(reportClassArr[0]));
            sumData.setProMidClassCode(reportClassArr.length < 2 ? "" : reportClassArr[1]);
            sumData.setProMidClassName(reportClassArr.length < 2 ? "" : proClassMap.get(reportClassArr[1]));
            sumData.setProClassCode(reportClassArr.length < 3 ? "" : reportClassArr[2]);
            sumData.setProClassName(reportClassArr.length < 3 ? "" : proClassMap.get(reportClassArr[2]));
            if ("1".equals(inData.getReportSecondType())) {
                sumData.setPosition(reportClassArr.length < 4 ? "" : reportClassArr[3]);
            } else if ("2".equals(inData.getReportSecondType())) {
                sumData.setProfitInterval(reportClassArr.length < 4 ? "" : reportClassArr[3]);
            }
        }
    }

    /**
     * @Author jiht
     * @Description 数据分组
     * @Date 2022/1/3 23:21
     * @Param []
     * @Return java.util.Map<java.lang.String,java.util.List<com.gys.common.data.SalesStatisticsReportOutData>>
     **/
    private Map<String, List<SalesStatisticsReportOutData>> dealGroupBy(List<SalesStatisticsReportOutData> proSumData, SalesStatisticsReportInData inData) {
        Map<String, List<SalesStatisticsReportOutData>> groupByData = null;
        if ("1".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            groupByData = new HashMap<>();
            groupByData.put("", proSumData);
        } else if ("2".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode()));
        } else if ("3".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode()));
        } else if ("4".equals(inData.getReportType()) && StringUtils.isEmpty(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProClassCode()));
        } else if ("1".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getPosition()));
        } else if ("2".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getPosition()));
        } else if ("3".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getPosition()));
        } else if ("4".equals(inData.getReportType()) && "1".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProClassCode() + "-" + k.getPosition()));
        } else if ("1".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProfitInterval()));
        } else if ("2".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProfitInterval()));
        } else if ("3".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProfitInterval()));
        } else if ("4".equals(inData.getReportType()) && "2".equals(inData.getReportSecondType())) {
            groupByData = proSumData.stream().collect(Collectors.groupingBy(k -> k.getProBigClassCode() + "-" + k.getProMidClassCode() + "-" + k.getProClassCode() + "-" + k.getProfitInterval()));
        }

        return groupByData;
    }

    /**
     * @Author jiht
     * @Description 按照商品汇总数据
     * @Date 2022/1/4 19:14
     * @Param [filterBasicSaleData, inData]
     * @Return java.util.List<com.gys.common.data.SalesStatisticsReportOutData>
     **/
    private List<SalesStatisticsReportOutData> dealProGroupBy(List<SalesStatisticsReportOutData> filterBasicSaleData, Map<String, Map<String, String>> profitIntervalMap) {
        List<SalesStatisticsReportOutData> proGroupByList = new ArrayList<>();
        // 统计区间数据按照商品编码进行分组
        Map<String, List<SalesStatisticsReportOutData>> proGroupByData = filterBasicSaleData.stream().collect(Collectors.groupingBy(k -> k.getProSelfCode()));
        // 每个商品的毛利区间划分结果
        Map<String, String> proProfitIntervalMap = new HashMap<>();

        proGroupByData.forEach((k, v) -> {
            SalesStatisticsReportOutData sumData = new SalesStatisticsReportOutData();
            BeanUtil.copyProperties(v.get(0), sumData);
            sumData.setSaleQty("0");
            sumData.setSaleAmt("0");
            sumData.setProfit("0");
            sumData.setZkAmt("0");
            sumData.setEbAmt("0");

            v.stream().forEach(o -> {
                // 销售量
                sumData.setSaleQty(String.valueOf(new BigDecimal(sumData.getSaleQty()).add(new BigDecimal(o.getSaleQty())).setScale(2, BigDecimal.ROUND_HALF_UP)));
                // 销售额
                sumData.setSaleAmt(String.valueOf(new BigDecimal(sumData.getSaleAmt()).add(new BigDecimal(o.getSaleAmt())).setScale(2, BigDecimal.ROUND_HALF_UP)));
                // 毛利额
                sumData.setProfit(String.valueOf(new BigDecimal(sumData.getProfit()).add(new BigDecimal(o.getProfit())).setScale(2, BigDecimal.ROUND_HALF_UP)));
                // 折扣金额
                sumData.setZkAmt(String.valueOf(new BigDecimal(sumData.getZkAmt()).add(new BigDecimal(o.getZkAmt())).setScale(2, BigDecimal.ROUND_HALF_UP)));
                // 医保金额
                sumData.setEbAmt(String.valueOf(new BigDecimal(sumData.getEbAmt()).add(new BigDecimal(o.getEbAmt())).setScale(2, BigDecimal.ROUND_HALF_UP)));
            });

            // 计算毛利率属于哪个毛利区间
            BigDecimal divideNumber = new BigDecimal(sumData.getSaleAmt());
            BigDecimal profitRate = divideNumber.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal(sumData.getProfit()).multiply(new BigDecimal(100)).divide(divideNumber, 1, BigDecimal.ROUND_HALF_UP);

            profitIntervalMap.forEach((pk, pv) -> {
                if (profitRate.compareTo(new BigDecimal(pv.get("startValue"))) > 0 && profitRate.compareTo(new BigDecimal(pv.get("endValue").toString())) <= 0) {
                    sumData.setProfitInterval(pk);
                }
            });
            proGroupByList.add(sumData);
            proProfitIntervalMap.put(sumData.getProSelfCode(), sumData.getProfitInterval());
        });

        // 置上毛利区间
        filterBasicSaleData.stream().forEach(o -> o.setProfitInterval(proProfitIntervalMap.get(o.getProSelfCode())));

        return proGroupByList;
    }

    /**
     * @Author jiht
     * @Description 计算时间统计间隔
     * @Date 2021/12/31 15:52
     * @Param [startDate, endDate, intervalSort]
     * @Return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     **/
    private List<Map<String, String>> calDateInterval(String startDate, String endDate, String intervalSort) {
        if ("1".equals(intervalSort)) {
            // 按周处理
            return calDateIntervalByWeek(startDate, endDate);
        } else if ("2".equals(intervalSort)) {
            // 按月处理
            return calDateIntervalByMonth(startDate, endDate);
        } else {
            // 码值超过范围
            return null;
        }
    }

    /**
     * @Author jiht
     * @Description 按周计算时间间隔
     * @Date 2021/12/31 15:50
     * @Param [startDate, endDate]
     * @Return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     **/
    public List<Map<String, String>> calDateIntervalByWeek(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        List<Map<String, String>> dateMapList = new ArrayList<>();
        // 加入合计
        dateMapList.add(new HashMap<String, String>() {{
            put("title", "合计");
            put("startDate", DateUtil.getFirstDayOfWeek(startDate));
            put("endDate", DateUtil.getLastDayOfWeek(endDate));
        }});
        String tempStartDate = endDate;
        String tempEndDate = endDate;

        while (DateUtil.getDiffByDateNew(startDate, tempEndDate) >= 0) {
            // 计算当月起止时间
            tempStartDate = DateUtil.getFirstDayOfWeek(tempEndDate);
            tempEndDate = DateUtil.getLastDayOfWeek(tempEndDate);

            // 同一年，正常处理
            if (tempEndDate.substring(0, 4).equals(tempStartDate.substring(0, 4))) {
                Map<String, String> week = new HashMap<>();
                week.put("title", tempEndDate.substring(0, 4) + "年" + DateUtil.getWeekNum(tempEndDate) + "周" +
                        "(" + Integer.parseInt(tempStartDate.substring(4, 6)) + "月" + Integer.parseInt(tempStartDate.substring(6, 8)) + "日-" +
                        Integer.parseInt(tempEndDate.substring(4, 6)) + "月" + Integer.parseInt(tempEndDate.substring(6, 8)) + "日)");
                week.put("startDate", tempStartDate);
                week.put("endDate", tempEndDate);
                dateMapList.add(week);
            } else {
                // 周跨年，拆分处理
                // 当年
                Map<String, String> afterWeek = new HashMap<>();
                afterWeek.put("title", tempEndDate.substring(0, 4) + "年" + DateUtil.getWeekNum(tempEndDate) + "周" +
                        "(1月1日-" + Integer.parseInt(tempEndDate.substring(4, 6)) + "月" + Integer.parseInt(tempEndDate.substring(6, 8)) + "日)");
                afterWeek.put("startDate", tempEndDate.substring(0, 4) + "0101");
                afterWeek.put("endDate", tempEndDate);
                dateMapList.add(afterWeek);
                // 上一年
                Map<String, String> beforeWeek = new HashMap<>();
                beforeWeek.put("title", tempStartDate.substring(0, 4) + "年" +
                        (DateUtil.getWeekNum(dateFormat.format(DateUtil.addDay(tempStartDate, "yyyyMMdd", -1))) + 1) + "周" +
                        "(" + Integer.parseInt(tempStartDate.substring(4, 6)) + "月" + Integer.parseInt(tempStartDate.substring(6, 8)) + "日-12月31日)");
                beforeWeek.put("startDate", tempStartDate);
                beforeWeek.put("endDate", tempStartDate.substring(0, 4) + "1231");
                dateMapList.add(beforeWeek);
            }
            // 日期加一周
            tempEndDate = dateFormat.format(DateUtil.addDay(tempEndDate, "yyyyMMdd", -7));
        }
        return dateMapList;
    }

    /**
     * @Author jiht
     * @Description 按月计算时间间隔
     * @Date 2021/12/31 15:50
     * @Param [startDate, endDate]
     * @Return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     **/
    private List<Map<String, String>> calDateIntervalByMonth(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        List<Map<String, String>> dateMapList = new ArrayList<>();
        // 加入合计
        dateMapList.add(new HashMap<String, String>() {{
            put("title", "合计");
            put("startDate", DateUtil.getFirstDayOfMonth(startDate));
            put("endDate", DateUtil.getLastDayOfMonth(endDate));
        }});
        String tempStartDate = endDate;
        String tempEndDate = endDate;

        while (DateUtil.getDiffByDateNew(startDate, tempEndDate) >= 0) {
            // 计算当月起止时间
            tempStartDate = DateUtil.getFirstDayOfMonth(tempEndDate);
            tempEndDate = DateUtil.getLastDayOfMonth(tempEndDate);
            // 放入统计区间
            Map<String, String> month = new HashMap<>();
            month.put("title", tempEndDate.substring(0, 4) + "年" + tempEndDate.substring(4, 6) + "月");
            month.put("startDate", tempStartDate);
            month.put("endDate", tempEndDate);
            dateMapList.add(month);
            // 日期加一个月
            tempEndDate = dateFormat.format(DateUtil.addMonth(tempEndDate, "yyyyMMdd", -1));
        }
        return dateMapList;
    }

    /**
     * @Author jiht
     * @Description 报表导出
     * @Date 2022/1/4 1:49
     * @Param [inData, response]
     * @Return com.gys.common.data.JsonResult
     **/
    public JsonResult export(SalesStatisticsReportInData inData, HttpServletResponse response) {
        log.info("[销售分析]报表导出开始,入参：{}", JSONObject.toJSONString(inData));
        // 数据校验
        checkInputData(inData);
        // 查询数据
        List<SalesStatisticsReportOutData> resultList = queryData(inData);
        // 组装导出数据
        List<List<Object>> detailExportInfo = getExportInfo(resultList, inData);
        // 设置表头
        String[] columnNames = getColumnNames(inData);
        // 生成导出excel
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JsonResult uploadResult = null;
        try {
            byte[] bytes = createCSVContent(columnNames, detailExportInfo);
            bos.write(bytes);

            String fileName = "销售分析-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv";
            uploadResult = cosUtils.uploadFileNew(bos, fileName);
            bos.flush();
        } catch (IOException e) {
            log.error("导出文件失败:{}", e.getMessage(), e);
            throw new BusinessException("导出文件失败！");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭流异常:{}", e.getMessage(), e);
                throw new BusinessException("关闭流异常！");
            }
        }

        return uploadResult;
    }

    /**
     * @Author jiht
     * @Description 组装csv格式字节
     * @Date 2022/1/11 13:22
     * @Param [columnNames, detailExportInfo]
     * @Return byte[]
     **/
    public static byte[] createCSVContent(String[] columnNames, List<List<Object>> detailExportInfo) {
        StringJoiner csvFile = new StringJoiner("\r\n");
        StringJoiner titleRow = new StringJoiner(",");
        for (String columnName : columnNames) {
            titleRow.add(StringUtils.isEmpty(columnName) ? "" : columnName);
        }
        csvFile.merge(titleRow);
        detailExportInfo.forEach(o -> {
            StringJoiner row = new StringJoiner(",");
            o.forEach(k -> row.add(String.valueOf(k)));
            csvFile.merge(row);
        });
        byte[] b = csvFile.toString().getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[b.length + 3];
        System.arraycopy(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}, 0, bytes, 0, 3);
        System.arraycopy(b, 0, bytes, 3, b.length);
        return bytes;
    }

    /**
     * @Author jiht
     * @Description 根据报表维度获取列名
     * @Date 2022/1/4 10:51
     * @Param [inData]
     * @Return java.lang.String[]
     **/
    private String[] getColumnNames(SalesStatisticsReportInData inData) {
        int columnCount = 0;
        String[] columnNames = new String[24];
        // 加入序号
        String[] idColumnNames = {"序号"};
        System.arraycopy(idColumnNames, 0, columnNames, columnCount, idColumnNames.length);
        columnCount += idColumnNames.length;

        if ("2".equals(inData.getReportType()) || "3".equals(inData.getReportType()) || "4".equals(inData.getReportType())) {
            String[] bigClassColumnNames = {"商品大类", "商品大类名称"};
            System.arraycopy(bigClassColumnNames, 0, columnNames, columnCount, bigClassColumnNames.length);
            columnCount += bigClassColumnNames.length;
        }
        if ("3".equals(inData.getReportType()) || "4".equals(inData.getReportType())) {
            String[] midClassColumnNames = {"商品中类", "商品中类名称"};
            System.arraycopy(midClassColumnNames, 0, columnNames, columnCount, midClassColumnNames.length);
            columnCount += midClassColumnNames.length;
        }
        if ("4".equals(inData.getReportType())) {
            String[] proClassColumnNames = {"商品分类", "商品分类名称"};
            System.arraycopy(proClassColumnNames, 0, columnNames, columnCount, proClassColumnNames.length);
            columnCount += proClassColumnNames.length;
        }
        if ("1".equals(inData.getReportSecondType())) {
            String[] positionColumnNames = {"定位"};
            System.arraycopy(positionColumnNames, 0, columnNames, columnCount, positionColumnNames.length);
            columnCount += positionColumnNames.length;
        }
        if ("2".equals(inData.getReportSecondType())) {
            String[] profitColumnNames = {"毛利区间"};
            System.arraycopy(profitColumnNames, 0, columnNames, columnCount, profitColumnNames.length);
            columnCount += profitColumnNames.length;
        }
        // 加入内容列
        String[] contentColumnNames = {"时间", "动销门店数", "动销品项", "销售量", "销售额", "销售占比", "毛利额", "毛利率", "折扣率", "医保销售额", "医保销售占比", "单品平均销售价", "客单价", "日均交易次数", "客单品项数"};
        System.arraycopy(contentColumnNames, 0, columnNames, columnCount, contentColumnNames.length);

        return columnNames;
    }

    /**
     * @Return java.util.List<java.util.List<java.lang.Object>>
     * @Author jiht
     * @Description 明细列表导出数据准备
     * @Date 2021/12/13 15:23
     * @Param [data]
     **/
    private List<List<Object>> getExportInfo(List<SalesStatisticsReportOutData> resultList, SalesStatisticsReportInData inData) {
        // 行号 (同一单号下顺序排列)
        AtomicInteger index = new AtomicInteger(1);
        //组装导出内容
        List<List<Object>> dataList = new ArrayList<>(resultList.size());
        resultList.stream().forEach(o -> {
            //每行数据
            List<Object> lineList = new ArrayList<>();
            //序号
            lineList.add(index.getAndIncrement());
            if ("2".equals(inData.getReportType()) || "3".equals(inData.getReportType()) || "4".equals(inData.getReportType())) {
                lineList.add(o.getProBigClassCode());
                lineList.add(o.getProBigClassName());
            }
            if ("3".equals(inData.getReportType()) || "4".equals(inData.getReportType())) {
                lineList.add(o.getProMidClassCode());
                lineList.add(o.getProMidClassName());
            }
            if ("4".equals(inData.getReportType())) {
                lineList.add(o.getProClassCode());
                lineList.add(o.getProClassName());
            }
            if ("1".equals(inData.getReportSecondType())) {
                lineList.add(o.getPosition());
            }
            if ("2".equals(inData.getReportSecondType())) {
                lineList.add(o.getProfitInterval());
            }
            lineList.add(o.getDate());
            lineList.add(o.getMovStos());
            lineList.add(o.getMovItems());
            lineList.add(o.getSaleQty());
            lineList.add(o.getSaleAmt());
            lineList.add(o.getSaleRate() + "%");
            lineList.add(o.getProfit());
            lineList.add(o.getProfitRate() + "%");
            lineList.add(o.getDiscountRate() + "%");
            lineList.add(o.getEbAmt());
            lineList.add(o.getEbSaleRate() + "%");
            lineList.add(o.getAvgProPrice());
            lineList.add(o.getAvgCusPrice());
            lineList.add(o.getAvgDailyTradeTimes());
            lineList.add(o.getAvgCusItem());

            dataList.add(lineList);
        });
        return dataList;
    }

    /**
     * @Author jiht
     * @Description 根据报表类型，查询默认查询区间
     * @Date 2022/1/5 16:47
     * @Param [inData]
     * @Return com.gys.common.data.JsonResult
     **/
    public JsonResult queryDefaultDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Map<String, String> result = new HashMap<>();
        String currentDate = dateFormat.format(new Date());
        // 上周区间
        String tempWeekDate = dateFormat.format(DateUtil.addDay(DateUtil.getLastDayOfWeek(currentDate), "yyyyMMdd", -7));
        result.put("weekStartDate", DateUtil.getFirstDayOfWeek(tempWeekDate));
        result.put("weekEndDate", DateUtil.getLastDayOfWeek(tempWeekDate));
        // 上月区间
        String tempMonthDate = dateFormat.format(DateUtil.addMonth(currentDate, "yyyyMMdd", -1));
        result.put("monthStartDate", DateUtil.getFirstDayOfMonth(tempMonthDate));
        result.put("monthEndDate", DateUtil.getLastDayOfMonth(tempMonthDate));

        log.info("默认查询区间:{}", JSONObject.toJSONString(result));

        return JsonResult.success(result, "success");
    }
}

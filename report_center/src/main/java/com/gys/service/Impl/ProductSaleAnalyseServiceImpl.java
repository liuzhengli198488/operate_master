package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.kylin.RowMapper;
import com.gys.common.response.Result;
import com.gys.entity.data.ProductBasicInfo;
import com.gys.entity.data.productSaleAnalyse.*;
import com.gys.mapper.BusinessReportMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.mapper.SaleReportMapper;
import com.gys.service.ProductSaleAnalyseService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.DateUtil;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaleAnalyseServiceImpl implements ProductSaleAnalyseService {

    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;

    @Resource
    private SaleReportMapper saleReportMapper;

    @Autowired
    private ProductSaleAnalyseService analyseService;

    @Resource
    private BusinessReportMapper businessReportMapper;

    @Resource
    private GaiaStoreDataMapper storeDataMapper;

    @Resource
    public CosUtils cosUtils;

    @Override
    public List<ProductSaleAnalyseOutData> productAnalyseList(ProductSaleAnalyseInData inData) {
        boolean f = false;
        String startDate = CommonUtil.parseyyyyMMdd(DateUtil.getStartDateByDays(inData.getEndDate(),Integer.parseInt(inData.getAnalyseDay())));
        String endDate = CommonUtil.parseyyyyMMdd(inData.getEndDate());
        log.info("多线程开始时间：" + CommonUtil.getHHmmss());
        CompletableFuture<List<ProductSaleAnalyseOutData>> future = CompletableFuture.supplyAsync(() -> {
            log.info("线程1开始");
            List<ProductSaleAnalyseOutData>  proList = productSaleAnalyseOutDataList(inData,startDate,endDate);
            List<String> proCodes = new ArrayList<>();
            Map<String,String> storeTypeMap = selectStoreType();
            for (int i = 0; i< proList.size(); i++) {
                ProductSaleAnalyseOutData analyse = proList.get(i);
                proCodes.add(analyse.getProCode());
                if ("4".equals(inData.getDimension())) {
                    if (StringUtils.isNotEmpty(analyse.getStoVersion())){
                        analyse.setStoVersion(storeTypeMap.get(analyse.getStoVersion()));
                    }
                }
            }
            log.info("药德商品列表查询开始");
            //药德商品列表查询
            List<ProductBasicInfo> productBasicInfoList = businessReportMapper.selectProductBasicList(proCodes.stream().distinct().collect(Collectors.toList()));
            Map<String, ProductBasicInfo> productBasicInfoMap = productBasicInfoList.stream().collect(Collectors.toMap(ProductBasicInfo::getProCode, x -> x, (a, pcb) -> pcb));
            log.info("药德商品列表查询结束时间：" + CommonUtil.getHHmmss());
            //重新计算销售概率
            for (int i = 0; i< proList.size(); i++) {
                ProductSaleAnalyseOutData analyse = proList.get(i);
                //补全商品信息
                ProductBasicInfo productBasicInfo = productBasicInfoMap.get(analyse.getProCode());
                if (ObjectUtil.isNotEmpty(productBasicInfo)){
                    analyse.setFactoryName(productBasicInfo.getFactoryName());
                    analyse.setProUnit(productBasicInfo.getProUnit());
                    analyse.setProSpecs(productBasicInfo.getProSpecs());
                    analyse.setProDepict(productBasicInfo.getProDepict());
                    analyse.setProClass(productBasicInfo.getProClass());
                    analyse.setProClassName(productBasicInfo.getProClassName());
                    analyse.setProBigClass(productBasicInfo.getProBigClass());
                    analyse.setProBigClassName(productBasicInfo.getProBigClassName());
                    analyse.setProMidClass(productBasicInfo.getProMidClass());
                    analyse.setProMidClassName(productBasicInfo.getProMidClassName());
                    analyse.setProBigCompClass(productBasicInfo.getProBigCompClass());
                    analyse.setProBigCompClassName(productBasicInfo.getProBigCompClassName());
                    analyse.setProMidCompClass(productBasicInfo.getProMidCompClass());
                    analyse.setProMidCompClassName(productBasicInfo.getProMidCompClassName());
                    analyse.setProCompClass(productBasicInfo.getProCompClass());
                    analyse.setProCompClassName(productBasicInfo.getProCompClassName());
                }
            }
            log.info("数据补全结束时间：" + CommonUtil.getHHmmss());
            log.info("线程1结束");
            return proList;
        });
        //商品动销天数
        CompletableFuture<Map<String,String>> payDayfuture = CompletableFuture.supplyAsync(() ->{
            log.info("线程2开始");
            Map<String,String> map = payDayMap(inData,startDate,endDate);
            log.info("线程2结束");
            return map;
        });
        //商品动销天数
        CompletableFuture<Map<String,String>> saleDayfuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程3开始");
            Map<String,String> map = saleDayMap(inData,startDate,endDate);
            log.info("线程3结束");
            return map;

        });
        //商品交易数
        CompletableFuture<Map<String,String>> saleCountfuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程4开始");
            Map<String,String> map =  saleCountMap(inData,startDate,endDate);
            log.info("线程4结束");
            return map;
        });
        CompletableFuture<Map<String,String>>saleStoreNumderfuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程5开始");
            Map<String,String> map = saleStoreNumderMap(inData,startDate,endDate);
            log.info("线程5结束");
            return map;
        });
        //商品交易次数
        CompletableFuture<Map<String,String>>payNumderfuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程6开始");
            Map<String,String> map =  payNumderMap(inData,startDate,endDate);
            log.info("线程6结束");
            return map;
        });
        CompletableFuture<Map<String,String>>areaNamefuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程7开始");
            Map<String,String> map = areaNameMap();
            log.info("线程7结束");
            return map;
        });
        CompletableFuture<Map<String,String>>clientNamefuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程8开始");
            Map<String,String> map = clientNameMap();
            log.info("线程8结束");
            return map;
        });
        CompletableFuture<Map<String,String>>stoCountfuture = CompletableFuture.supplyAsync(() -> {
            log.info("线程9开始");
            Map<String,String> map = stoCountMap(inData,startDate,endDate);
            log.info("线程9结束");
            return map;
        });
        List<ProductSaleAnalyseOutData> outData = new ArrayList<>();
        List<ProductSaleAnalyseOutData> outDataList = new ArrayList<>();
        Map<String, String> b = new HashMap<>();
        Map<String, String> c = new HashMap<>();
        Map<String, String> d = new HashMap<>();
        Map<String, String> sto = new HashMap<>();
        Map<String, String> ssto = new HashMap<>();
        Map<String, String> py = new HashMap<>();
        Map<String, String> ar = new HashMap<>();
        Map<String, String> ci = new HashMap<>();
        CompletableFuture.allOf(future,payDayfuture,saleDayfuture,saleCountfuture,saleStoreNumderfuture,payNumderfuture,areaNamefuture,clientNamefuture,stoCountfuture).join();
        try {

            outDataList = future.get();
            b = payDayfuture.get();
            c = saleDayfuture.get();//总销售天数
            d = saleCountfuture.get();//商品交易数
            sto = stoCountfuture.get();//门店数
            //动销门店数
            ssto = saleStoreNumderfuture.get();
            //商品交易次数
            py = payNumderfuture.get();
            ar = areaNamefuture.get();
            ci = clientNamefuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("执行位置1");
        log.info("多线程结束时间：" + CommonUtil.getHHmmss());
        List<String> proCodes = new ArrayList<>();
        for (int i = 0; i < outDataList.size(); i++) {
            ProductSaleAnalyseOutData analyse = outDataList.get(i);
            proCodes.add(analyse.getProCode());
            String a = "";
            String a1 = "";
            if ("4".equals(inData.getDimension())) {
                if (ObjectUtil.isEmpty(analyse.getProvinceId())) {
                    analyse.setProvinceId("");
                    a = a + "wk-";
                    a1 = a1 + "wk-";
                } else {
                    a = a + analyse.getProvinceId() + "-";
                    a1 = a1 + analyse.getProvinceId() + "-";
                    analyse.setProvince(ar.get(analyse.getProvinceId()));
                }
                if (ObjectUtil.isEmpty(analyse.getCityId())) {
                    analyse.setCityId("");
                    a = a + "wk-";
                    a1 = a1 + "wk-";
                } else {
                    analyse.setCity(ar.get(analyse.getCityId()));
                    a = a + analyse.getCityId() + "-";
                    a1 = a1 + analyse.getCityId() + "-";
                }
                if (ObjectUtil.isEmpty(analyse.getClientId())) {
                    analyse.setClientId("");
                    a = a + "wk-";
                    a1 = a1 + "wk-";
                } else {
                    analyse.setClientName(ci.get(analyse.getClientId()));
                    a = a + analyse.getClientId() + "-";
                    a1 = a1 + analyse.getClientId() + "-";
                }
                if (ObjectUtil.isEmpty(analyse.getStoCode())) {
                    analyse.setStoCode("");
                    a = a + "wk-";
                    a1 = a1 + "wk";
                } else {
                    a = a + analyse.getStoCode() + "-";
                    a1 = a1 + analyse.getStoCode();
                }
                a = a + analyse.getProCode();
            }
            if ("3".equals(inData.getDimension())) {
                if (ObjectUtil.isEmpty(analyse.getProvinceId())) {
                    analyse.setProvinceId("");
                    a = a + "wk-";
                    a1 = a1 + "wk-";
                } else {
                    a = a + analyse.getProvinceId() + "-";
                    a1 = a1 + analyse.getProvinceId() + "-";
                    analyse.setProvince(ar.get(analyse.getProvinceId()));
                }
                if (ObjectUtil.isEmpty(analyse.getCityId())) {
                    analyse.setCityId("");
                    a = a + "wk-";
                    a1 = a1 + "wk-";
                } else {
                    analyse.setCity(ar.get(analyse.getCityId()));
                    a = a + analyse.getCityId() + "-";
                    a1 = a1 + analyse.getCityId() + "-";
                }
                if (ObjectUtil.isEmpty(analyse.getClientId())) {
                    analyse.setClientId("");
                    a = a + "wk-";
                    a1 = a1 + "wk";
                } else {
                    analyse.setClientName(ci.get(analyse.getClientId()));
                    a = a + analyse.getClientId() + "-";
                    a1 = a1 + analyse.getClientId();
                }
                a = a + analyse.getProCode();
            }
            if ("2".equals(inData.getDimension())) {
                if (ObjectUtil.isEmpty(analyse.getProvinceId())) {
                    analyse.setProvinceId("");
                    a = a + "wk-";
                    a1 = a1 + "wk-";
                } else {
                    a = a + analyse.getProvinceId() + "-";
                    a1 = a1 + analyse.getProvinceId() + "-";
                    analyse.setProvince(ar.get(analyse.getProvinceId()));
                }
                if (ObjectUtil.isEmpty(analyse.getCityId())) {
                    analyse.setCityId("");
                    a = a + "wk-";
                    a1 = a1 + "wk";
                } else {
                    analyse.setCity(ar.get(analyse.getCityId()));
                    a = a + analyse.getCityId() + "-";
                    a1 = a1 + analyse.getCityId();
                }
                a = a + analyse.getProCode();
            }
            if ("1".equals(inData.getDimension())) {
                if (ObjectUtil.isEmpty(analyse.getProvinceId())) {
                    analyse.setProvinceId("");
                    a = a + "wk-";
                    a1 = a1 + "wk";
                } else {
                    a = a + analyse.getProvinceId() + "-";
                    a1 = a1 + analyse.getProvinceId();
                    analyse.setProvince(ar.get(analyse.getProvinceId()));
                }
                a = a + analyse.getProCode();
            }
//            log.info("111:" + a);
//            log.info("111:" + a1);
            analyse.setPayDays(b.get(a));
            analyse.setSaleDays(c.get(a1));
            analyse.setSaleCount(d.get(a));
            analyse.setStoNum(Integer.valueOf(StringUtils.isNotEmpty(sto.get(a1)) ? sto.get(a1) : "0"));//门店数
            analyse.setPayNumber(py.get(a));
            analyse.setIcount(ssto.get(a));
            //计算销售概率
            if (ObjectUtil.isNotEmpty(analyse.getPayDays()) && ObjectUtil.isNotEmpty(analyse.getSaleDays())
                    && ObjectUtil.isNotEmpty(analyse.getPayNumber()) && ObjectUtil.isNotEmpty(analyse.getSaleCount())) {
                if (new BigDecimal(analyse.getSaleDays()).compareTo(BigDecimal.ZERO) != 0 && new BigDecimal(analyse.getSaleCount()).compareTo(BigDecimal.ZERO) != 0) {
                    String saleAbout = new BigDecimal(analyse.getPayDays()).divide(new BigDecimal(analyse.getSaleDays()), 8, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(analyse.getPayNumber()).divide(new BigDecimal(analyse.getSaleCount()), 8, BigDecimal.ROUND_HALF_UP))
                            .multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
                    analyse.setSaleAbout(saleAbout);
                } else {
                    analyse.setSaleAbout("0");
                }
                if (new BigDecimal(analyse.getSaleDays()).compareTo(BigDecimal.ZERO) != 0
                        && new BigDecimal(analyse.getSaleCount()).compareTo(BigDecimal.ZERO) != 0
                        && new BigDecimal(analyse.getPayNumber()).compareTo(BigDecimal.ZERO) != 0) {
                    String singleAmt = (new BigDecimal(analyse.getGssdAmt()).divide(new BigDecimal(analyse.getPayNumber()), 8, BigDecimal.ROUND_HALF_UP))
                            .multiply(new BigDecimal(analyse.getPayDays()).divide(new BigDecimal(analyse.getSaleDays()), 8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(analyse.getPayNumber()).divide(new BigDecimal(analyse.getSaleCount()), 8, BigDecimal.ROUND_HALF_UP)))
                            .setScale(4, BigDecimal.ROUND_HALF_UP).toString();
                    analyse.setSingleAmt(singleAmt);
                } else {
                    analyse.setSingleAmt("0");
                }
            } else {
                analyse.setSaleAbout("0");
                analyse.setSingleAmt("0");
            }
        }
        //根据不同省市客户门店去重
        List<ProductSaleAnalyseOutData> collect = new ArrayList<>();
        if ("4".equals(inData.getDimension())) {
            collect = outDataList.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + o.getCityId() + o.getClientId() + o.getStoCode()))), ArrayList::new)
            );
            //获取销售概率之和（同范围）
            for (int o = 0; o < collect.size(); o++) {
                ProductSaleAnalyseOutData p = collect.get(o);
                BigDecimal saleAboutSum = BigDecimal.ZERO;
                for (int u = 0; u < outDataList.size(); u++) {
                    ProductSaleAnalyseOutData analyse = outDataList.get(u);
                    if (p.getProvinceId().equals(analyse.getProvinceId())
                            && p.getCityId().equals(analyse.getCityId())
                            && p.getClientId().equals(analyse.getClientId())
                            && p.getStoCode().equals(analyse.getStoCode())) {
                        saleAboutSum = saleAboutSum.add(new BigDecimal(analyse.getSaleAbout()));
                    }
                }
                p.setSaleAboutSum(saleAboutSum.toString());
            }
        }
        if ("3".equals(inData.getDimension())) {
            collect = outDataList.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + o.getCityId() + o.getClientId()))), ArrayList::new)
            );
            //获取销售概率之和（同范围）
            for (int o = 0; o < collect.size(); o++) {
                ProductSaleAnalyseOutData p = collect.get(o);
                BigDecimal saleAboutSum = BigDecimal.ZERO;
                for (int u = 0; u < outDataList.size(); u++) {
                    ProductSaleAnalyseOutData analyse = outDataList.get(u);
                    if (p.getProvinceId().equals(analyse.getProvinceId())
                            && p.getCityId().equals(analyse.getCityId())
                            && p.getClientId().equals(analyse.getClientId())) {
                        saleAboutSum = saleAboutSum.add(new BigDecimal(analyse.getSaleAbout()));
                    }
                }
                p.setSaleAboutSum(saleAboutSum.toString());
            }
        }
        if ("2".equals(inData.getDimension())) {
            collect = outDataList.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProvinceId() + o.getCityId()))), ArrayList::new)
            );
            //获取销售概率之和（同范围）
            for (int o = 0; o < collect.size(); o++) {
                ProductSaleAnalyseOutData p = collect.get(o);
                BigDecimal saleAboutSum = BigDecimal.ZERO;
                for (int u = 0; u < outDataList.size(); u++) {
                    ProductSaleAnalyseOutData analyse = outDataList.get(u);
                    if (p.getProvinceId().equals(analyse.getProvinceId())
                            && p.getCityId().equals(analyse.getCityId())) {
                        saleAboutSum = saleAboutSum.add(new BigDecimal(analyse.getSaleAbout()));
                    }
                }
                p.setSaleAboutSum(saleAboutSum.toString());
            }
        }
        if ("1".equals(inData.getDimension())) {
            collect = outDataList.stream().filter(s -> Objects.nonNull(s.getProvinceId())).collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProvinceId()))), ArrayList::new)
            );
            //获取销售概率之和（同范围）
            for (int o = 0; o < collect.size(); o++) {
                ProductSaleAnalyseOutData p = collect.get(o);
                BigDecimal saleAboutSum = BigDecimal.ZERO;
                for (int u = 0; u < outDataList.size(); u++) {
                    ProductSaleAnalyseOutData analyse = outDataList.get(u);
                    if (p.getProvinceId().equals(analyse.getProvinceId())) {
                        saleAboutSum = saleAboutSum.add(new BigDecimal(analyse.getSaleAbout()));
                    }
                }
                p.setSaleAboutSum(saleAboutSum.toString());
            }
        }
        log.info("去重省市客户门店结束时间：" + CommonUtil.getHHmmss());
        for (int i = 0; i < collect.size(); i++) {
            ProductSaleAnalyseOutData pb = collect.get(i);
            List<ProductSaleAnalyseOutData> itemList = new ArrayList<>();
            for (int j = 0; j < outDataList.size(); j++) {
                ProductSaleAnalyseOutData e = outDataList.get(j);
                if ("4".equals(inData.getDimension())) {
                    if (pb.getProvinceId().equals(e.getProvinceId())
                            && pb.getClientId().equals(e.getClientId())
                            && pb.getCityId().equals(e.getCityId()) && pb.getStoCode().equals(e.getStoCode())) {
                        itemList.add(e);
                    }
                }
                if ("3".equals(inData.getDimension())) {
                    if (pb.getProvinceId().equals(e.getProvinceId())
                            && pb.getClientId().equals(e.getClientId())
                            && pb.getCityId().equals(e.getCityId())) {
                        itemList.add(e);
                    }
                }
                if ("2".equals(inData.getDimension())) {
                    if (pb.getProvinceId().equals(e.getProvinceId())
                            && pb.getCityId().equals(e.getCityId())) {
                        itemList.add(e);
                    }
                }
                if ("1".equals(inData.getDimension())) {
                    if (pb.getProvinceId().equals(e.getProvinceId())) {
                        itemList.add(e);
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(itemList) && itemList.size() > 0) {
                //销售概率排序（降序）
                List<ProductSaleAnalyseOutData> outData1 = itemList.stream().sorted(Comparator.comparing(ProductSaleAnalyseOutData::getSaleAbout).reversed()).collect(Collectors.toList());
                int count1 = 1;
                for (int k = 0; k < outData1.size(); k++) {
                    ProductSaleAnalyseOutData analyse = outData1.get(k);
                    analyse.setGrossProfitAmt(new BigDecimal(analyse.getGssdAmt()).subtract(new BigDecimal(analyse.getGssdmovprice())));
                    analyse.setSaleAboutRank(count1);
                    if (new BigDecimal(analyse.getGssdAmt()).compareTo(BigDecimal.ZERO) != 0) {
                        //毛利率计算
                        String grossProfitRate = analyse.getGrossProfitAmt().divide(new BigDecimal(analyse.getGssdAmt()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
                        analyse.setGrossProfitRate(grossProfitRate);
                    } else {
                        analyse.setGrossProfitRate("0.00%");
                    }
//                    log.info(analyse.getGssdAmt() + "-" +analyse.getGssdmovprice() + "--" +analyse.getPayNumber() + "---" + analyse.getSaleAbout());
                    //单品毛利额计算
                    if (StringUtils.isNotEmpty(analyse.getPayNumber()) && new BigDecimal(analyse.getPayNumber()).compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal a = (new BigDecimal(analyse.getGssdAmt()).subtract(new BigDecimal(analyse.getGssdmovprice()))).divide(new BigDecimal(analyse.getPayNumber()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(analyse.getSaleAbout()).divide(new BigDecimal(100))).setScale(4, BigDecimal.ROUND_HALF_UP);
                        analyse.setSingleGrossProfit(a.toString());
                    } else {
                        analyse.setSingleGrossProfit("0.00");
                    }
                    count1++;
                }
                //单次销售额排序（降序）
                List<ProductSaleAnalyseOutData> outData2 = outData1.stream().sorted(Comparator.comparing(ProductSaleAnalyseOutData::getSingleAmt).reversed()).collect(Collectors.toList());
                int count2 = 1;
                for (int k = 0; k < outData2.size(); k++) {
                    ProductSaleAnalyseOutData analyse = outData2.get(k);
                    analyse.setSingleAmtRank(count2);
                    analyse.setRankSum(analyse.getSaleAboutRank() + analyse.getSingleAmtRank());
                    count2++;
                }
                List<ProductSaleAnalyseOutData> outData3 = new ArrayList<>();
                if ("1".equals(inData.getIsProfit())) {
                    //单次毛利额排序（降序）
                    outData3 = outData2.stream().sorted(Comparator.comparing(ProductSaleAnalyseOutData::getSingleGrossProfit).reversed()).collect(Collectors.toList());
                    int count3 = 1;
                    for (int k = 0; k < outData3.size(); k++) {
                        ProductSaleAnalyseOutData analyse = outData3.get(k);
                        analyse.setSingleGrossProfitRank(count3);
                        analyse.setRankSum(analyse.getSaleAboutRank() + analyse.getSingleAmtRank() + analyse.getSingleGrossProfitRank());
                        count3++;
                    }
                }
                //综合排序（升序）
                List<ProductSaleAnalyseOutData> result = new ArrayList<>();
                if ("1".equals(inData.getIsProfit())) {
                    //单次毛利额计算入内
                    result = outData3.stream().sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum)).collect(Collectors.toList());
                } else {
                    //单次毛利额不计算入内
                    result = outData2.stream().sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum)).collect(Collectors.toList());
                }
                int count = 1;
                for (int k = 0; k < result.size(); k++) {
                    ProductSaleAnalyseOutData analyse = result.get(k);
                    analyse.setComprehensiveRank(count);
                    count++;
                }
                outData.addAll(result);
            }
        }
        log.info("单个排名结束时间：" + CommonUtil.getHHmmss());
        //省份查询
        if ("1".equals(inData.getDimension())) {
            //获取门店数
            for (int i = 0; i < outData.size(); i++) {
                ProductSaleAnalyseOutData analyse = outData.get(i);
                //计算贝叶斯概率
                for (int o = 0; o < collect.size(); o++) {
                    ProductSaleAnalyseOutData p = collect.get(o);
                    if (p.getProvinceId().equals(analyse.getProvinceId())) {
                        if (new BigDecimal(p.getSaleAboutSum()).compareTo(BigDecimal.ZERO) != 0) {
                            BigDecimal a = new BigDecimal(analyse.getSaleAbout()).divide(new BigDecimal(p.getSaleAboutSum()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            analyse.setBayesianProbability(a);
                        } else {
                            analyse.setBayesianProbability(BigDecimal.ZERO);
                        }
                    }
                }
            }
        }
        //市查询
        if ("2".equals(inData.getDimension())) {
            //获取门店数
            for (int i = 0; i < outData.size(); i++) {
                ProductSaleAnalyseOutData analyse = outData.get(i);
                //计算贝叶斯概率
                for (int o = 0; o < collect.size(); o++) {
                    ProductSaleAnalyseOutData p = collect.get(o);
                    if (p.getProvinceId().equals(analyse.getProvinceId()) && p.getCityId().equals(analyse.getCityId())) {
                        if (new BigDecimal(p.getSaleAboutSum()).compareTo(BigDecimal.ZERO) != 0) {
                            BigDecimal a = new BigDecimal(analyse.getSaleAbout()).divide(new BigDecimal(p.getSaleAboutSum()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            analyse.setBayesianProbability(a);
                        } else {
                            analyse.setBayesianProbability(BigDecimal.ZERO);
                        }
                    }
                }
            }
        }
        //客户
        if ("3".equals(inData.getDimension())) {
            //获取门店数
            for (int i = 0; i < outData.size(); i++) {
                ProductSaleAnalyseOutData analyse = outData.get(i);
                //计算贝叶斯概率
                for (int o = 0; o < collect.size(); o++) {
                    ProductSaleAnalyseOutData p = collect.get(o);
                    if (p.getProvinceId().equals(analyse.getProvinceId())
                            && p.getCityId().equals(analyse.getCityId())
                            && p.getClientId().equals(analyse.getClientId())) {
                        if (new BigDecimal(p.getSaleAboutSum()).compareTo(BigDecimal.ZERO) != 0) {
                            BigDecimal a = new BigDecimal(analyse.getSaleAbout()).divide(new BigDecimal(p.getSaleAboutSum()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            analyse.setBayesianProbability(a);
                        } else {
                            analyse.setBayesianProbability(BigDecimal.ZERO);
                        }
                    }
                }
            }
        }
        //门店
        if ("4".equals(inData.getDimension())) {
            //获取门店数
            for (int i = 0; i < outData.size(); i++) {
                ProductSaleAnalyseOutData analyse = outData.get(i);
                //计算贝叶斯概率
                for (int o = 0; o < collect.size(); o++) {
                    ProductSaleAnalyseOutData p = collect.get(o);
                    if (p.getProvinceId().equals(analyse.getProvinceId())
                            && p.getCityId().equals(analyse.getCityId())
                            && p.getClientId().equals(analyse.getClientId())
                            && p.getStoCode().equals(analyse.getStoCode())) {
                        if (new BigDecimal(p.getSaleAboutSum()).compareTo(BigDecimal.ZERO) != 0) {
                            BigDecimal a = new BigDecimal(analyse.getSaleAbout()).divide(new BigDecimal(p.getSaleAboutSum()), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                            analyse.setBayesianProbability(a);
                        } else {
                            analyse.setBayesianProbability(BigDecimal.ZERO);
                        }
                    }
                }
            }
        }
        log.info("计算贝叶斯概率结束时间：" + CommonUtil.getHHmmss());
//        //重新排序
//        outData = outData.stream()
//                .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum))
//                .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getStoCode))
//                .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getClientId))
//                .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getCityId))
//                .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getProvinceId))
//                .collect(Collectors.toList());
        if ("4".equals(inData.getDimension())) {
            //重新排序
            outData = outData.stream()
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getStoCode))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getClientId))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getCityId))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getProvinceId))
                    .collect(Collectors.toList());
        }
        if ("3".equals(inData.getDimension())) {
            //重新排序
            outData = outData.stream()
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getClientId))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getCityId))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getProvinceId))
                    .collect(Collectors.toList());
        }
        if ("2".equals(inData.getDimension())) {
            //重新排序
            outData = outData.stream()
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getCityId))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getProvinceId))
                    .collect(Collectors.toList());
        }
        if ("1".equals(inData.getDimension())) {
            //重新排序
            outData = outData.stream()
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getRankSum))
                    .sorted(Comparator.comparing(ProductSaleAnalyseOutData::getProvinceId))
                    .collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(inData.getLimitNum())) {
            outData = outData.subList(0, Integer.valueOf(inData.getLimitNum()));
        }
        log.info("最终结束时间：" + CommonUtil.getHHmmss());
        return outData;
    }

    @Override
    public Map<String, Object> selectStoreInfoBySale(ProductSaleAnalyseInData inData) {
        Map<String, Object> result = new HashMap<>();
        List<StoreInfoOutData> infoOutDataList = saleReportMapper.selectStoreInfoBySale(inData);
        //省份列表过滤
        List<StoreInfoOutData> provinceList = infoOutDataList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(StoreInfoOutData::getProvinceId))), ArrayList::new)
        );
        if (ObjectUtil.isNotEmpty(provinceList) && provinceList.size() > 0){
            result.put("provinceList",provinceList);
        }
        //市列表过滤
        List<StoreInfoOutData> cityList = infoOutDataList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(StoreInfoOutData::getCityId))), ArrayList::new)
        );
        if (ObjectUtil.isNotEmpty(cityList) && cityList.size() > 0){
            result.put("cityList",cityList);
        }
        //客户列表过滤
        List<StoreInfoOutData> clientList = infoOutDataList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(StoreInfoOutData::getClientId))), ArrayList::new)
        );
        if (ObjectUtil.isNotEmpty(clientList) && clientList.size() > 0){
            result.put("clientList",clientList);
        }
        //门店列表过滤
        List<StoreInfoOutData> storeList = infoOutDataList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getClientId() + f.getStoCode()))), ArrayList::new)
        );
        if (ObjectUtil.isNotEmpty(storeList) && storeList.size() > 0){
            result.put("storeList",storeList);
        }
        //店型过滤
        List<StoreInfoOutData> stoTypeList = infoOutDataList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(StoreInfoOutData::getStoType))), ArrayList::new)
        );
        if (ObjectUtil.isNotEmpty(stoTypeList) && stoTypeList.size() > 0){
            result.put("stoTypeList",stoTypeList);
        }
        return result;
    }

    @Override
    public Result exportProductSaleAnalyse(ProductSaleAnalyseInData inData){
        Result result = null;
//        GetLoginOutData userInfo = this.getLoginUser(request);
//        inData.setClient(userInfo.getClient());
        List<ProductSaleAnalyseOutData> list = analyseService.productAnalyseList(inData);
        List<ProductSaleAnalyseOutDataProvinceCSV> provinceCSVList = new ArrayList<>();
        List<ProductSaleAnalyseOutDataCityCSV> cityCSVList = new ArrayList<>();
        List<ProductSaleAnalyseOutDataClientCSV> clientCSVList = new ArrayList<>();
        List<ProductSaleAnalyseOutDataStoreCSV> storeCSVList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
            for (ProductSaleAnalyseOutData outData : list) {
                if ("1".equals(inData.getDimension())) {
                    ProductSaleAnalyseOutDataProvinceCSV provinceCSV = new ProductSaleAnalyseOutDataProvinceCSV();
                    BeanUtils.copyProperties(outData, provinceCSV);
                    provinceCSVList.add(provinceCSV);
                }else if ("2".equals(inData.getDimension())){
                    ProductSaleAnalyseOutDataCityCSV cityCSV = new ProductSaleAnalyseOutDataCityCSV();
                    BeanUtils.copyProperties(outData, cityCSV);
                    cityCSVList.add(cityCSV);
                }else if ("3".equals(inData.getDimension())){
                    ProductSaleAnalyseOutDataClientCSV clientCSV = new ProductSaleAnalyseOutDataClientCSV();
                    BeanUtils.copyProperties(outData, clientCSV);
                    clientCSVList.add(clientCSV);
                }else if ("4".equals(inData.getDimension())){
                    ProductSaleAnalyseOutDataStoreCSV storeCSV = new ProductSaleAnalyseOutDataStoreCSV();
                    BeanUtils.copyProperties(outData, storeCSV);
                    storeCSVList.add(storeCSV);
                }
            }
            try {
                if ("1".equals(inData.getDimension())) {
                    CsvFileInfo csvInfo = CsvClient.getCsvByte(provinceCSVList, "省级商品品类模型", Collections.singletonList((short) 1));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        bos.write(csvInfo.getFileContent());
                        result = cosUtils.uploadFile(bos, csvInfo.getFileName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }
                } else if ("2".equals(inData.getDimension())) {
                    CsvFileInfo csvInfo = CsvClient.getCsvByte(cityCSVList, "市级商品品类模型", Collections.singletonList((short) 1));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        bos.write(csvInfo.getFileContent());
                        result = cosUtils.uploadFile(bos, csvInfo.getFileName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }
                } else if ("3".equals(inData.getDimension())) {
                    CsvFileInfo csvInfo = CsvClient.getCsvByte(clientCSVList, "客户商品品类模型", Collections.singletonList((short) 1));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        bos.write(csvInfo.getFileContent());
                        result = cosUtils.uploadFile(bos, csvInfo.getFileName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }
                } else if ("4".equals(inData.getDimension())) {
                    CsvFileInfo csvInfo = CsvClient.getCsvByte(storeCSVList, "门店商品品类模型", Collections.singletonList((short) 1));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        bos.write(csvInfo.getFileContent());
                        result = cosUtils.uploadFile(bos, csvInfo.getFileName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }
                }
            }catch (IOException e){
                e.getMessage();
            }
        } else {
            return Result.error("导出失败");
        }
        return result;
    }

    public Map<String,String> payDayMap(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT count(a.GSSD_DATE) payDays,a.PRO_CODE proCode ");
        if ("4".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince,STO_CITY stoCity,a.CLIENT client,GSSD_BR_ID stoCode");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince,STO_CITY stoCity,a.CLIENT client ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince,STO_CITY stoCity ");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince ");
        }
        builder.append(" FROM (SELECT pb.PRO_CODE,a.GSSD_DATE,b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID ");
        builder.append(" FROM GAIA_SD_SALE_D a  INNER JOIN GAIA_STORE_DATA b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE ");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE ");
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN ( ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,pb.PRO_CODE,a.GSSD_DATE) a ");
        if ("4".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.PRO_CODE,a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.GSSD_BR_ID ");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.PRO_CODE,a.STO_PROVINCE,a.STO_CITY,a.CLIENT ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.PRO_CODE,a.STO_PROVINCE,a.STO_CITY");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.PRO_CODE,a.STO_PROVINCE ");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<ProductSalePayDay> outDataList = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ProductSalePayDay.class));
//        log.info("统计数据返回结果:{}", JSONObject.toJSONString(outDataList));
        for(int i = 0 ; i < outDataList.size();i++){
            ProductSalePayDay a = outDataList.get(i);
            if(ObjectUtil.isEmpty(a.getStoProvince())){
                a.setStoProvince("");
            }
            if (ObjectUtil.isEmpty(a.getStoCity())) {
                a.setStoCity("");
            }
            if (ObjectUtil.isEmpty(a.getClient())) {
                a.setClient("");
            }
            if (ObjectUtil.isEmpty(a.getStoCode())) {
                a.setStoCode("");
            }
        }
        Map<String,String> map = new HashMap<>();
        if ("4".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getStoCode() + "-"
                            + o.getProCode(),
                    ProductSalePayDay::getPayDays, (a, b) -> b));
        }
        if ("3".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getProCode(),
                    ProductSalePayDay::getPayDays, (a, b) -> b));
        }
        if ("2".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getStoCity() + "-" + o.getProCode(),
                    ProductSalePayDay::getPayDays, (a, b) -> b));
        }
        if ("1".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getProCode(),ProductSalePayDay::getPayDays, (a, b) -> b));
        }
        return map;
    }

    public Map<String,String> saleDayMap(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT count( saleDays ) saleDays ");
        //计算维度 1 省级维度 2 市级维度 3 客户维度 4 门店维度
        if ("4".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince,STO_CITY stoCity,a.CLIENT client,GSSD_BR_ID stoCode");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince,STO_CITY stoCity,a.CLIENT client ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince,STO_CITY stoCity ");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(",STO_PROVINCE stoProvince ");
        }
        builder.append(" FROM ( SELECT a.GSSD_DATE saleDays,a.CLIENT,a.GSSD_BR_ID,b.STO_PROVINCE,b.STO_CITY FROM GAIA_SD_SALE_D a ");
        builder.append(" INNER JOIN GAIA_STORE_DATA b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE ");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE ");
        builder.append(" WHERE ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        builder.append(" AND a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");//日期查询
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN ( ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY a.CLIENT,a.GSSD_BR_ID,a.GSSD_DATE,b.STO_PROVINCE,b.STO_CITY ) a ");
        if ("4".equals(inData.getDimension())) {
            builder.append(" GROUP BY STO_PROVINCE,STO_CITY,a.CLIENT,GSSD_BR_ID");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(" GROUP BY STO_PROVINCE,STO_CITY,a.CLIENT");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" GROUP BY STO_PROVINCE,STO_CITY");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" GROUP BY STO_PROVINCE");
        }
        log.info("sql统计数据111：{}", builder.toString());
        List<ProductSaleDay> outDataList = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ProductSaleDay.class));
//        log.info("统计数据返回结果:{}", JSONObject.toJSONString(outDataList));
        for(int i = 0 ; i < outDataList.size();i++){
            ProductSaleDay a = outDataList.get(i);
            if(ObjectUtil.isEmpty(a.getStoProvince())){
                a.setStoProvince("");
            }
            if (ObjectUtil.isEmpty(a.getStoCity())) {
                a.setStoCity("");
            }
            if (ObjectUtil.isEmpty(a.getClient())) {
                a.setClient("");
            }
            if (ObjectUtil.isEmpty(a.getStoCode())) {
                a.setStoCode("");
            }
        }
        Map<String,String> map = new HashMap<>();
        if ("4".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getStoCode(),
                    ProductSaleDay::getSaleDays, (a, b) -> b));
        }
        if ("3".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient(),
                    ProductSaleDay::getSaleDays, (a, b) -> b));
        }
        if ("2".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getStoCity(),
                    ProductSaleDay::getSaleDays, (a, b) -> b));
        }
        if ("1".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince(),ProductSaleDay::getSaleDays, (a, b) -> b));
        }
        return map;
    }

    public Map<String,String> saleCountMap(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT a.PRO_CODE,SUM( b.saleCount ) saleCount ");
        //计算维度 1 省级维度 2 市级维度 3 客户维度 4 门店维度
        if ("4".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity,a.CLIENT client,a.GSSD_BR_ID stoCode");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity,a.CLIENT client ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity ");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince ");
        }
        builder.append(" FROM ( SELECT pb.PRO_CODE,a.GSSD_DATE,( CASE WHEN b.STO_PROVINCE IS NULL THEN '' ELSE b.STO_PROVINCE END ) STO_PROVINCE, ( CASE WHEN b.STO_CITY IS NULL THEN '' ELSE b.STO_CITY END ) STO_CITY,a.CLIENT,a.GSSD_BR_ID ");
        builder.append(" FROM GAIA_SD_SALE_D a  INNER JOIN GAIA_STORE_DATA b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE ");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE ");
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN ( ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,pb.PRO_CODE,a.GSSD_DATE ) a ");
        builder.append(" LEFT JOIN (SELECT COUNT( DISTINCT a.GSSD_BILL_NO ) saleCount,a.GSSD_DATE ");
        builder.append(" ,(CASE WHEN b.STO_PROVINCE IS NULL THEN '' ELSE b.STO_PROVINCE END) STO_PROVINCE,(CASE WHEN b.STO_CITY IS NULL THEN '' ELSE b.STO_CITY END) STO_CITY,a.CLIENT,a.GSSD_BR_ID ");
        builder.append(" FROM GAIA_SD_SALE_D a INNER JOIN GAIA_STORE_DATA b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE ");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE ");
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN (  ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,a.GSSD_DATE ) b ON a.STO_PROVINCE = b.STO_PROVINCE and a.STO_CITY = b.STO_CITY and a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.GSSD_BR_ID AND a.GSSD_DATE = b.GSSD_DATE ");
        if ("4".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.GSSD_BR_ID,a.PRO_CODE");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.PRO_CODE");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.PRO_CODE");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.PRO_CODE");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<ProductSaleCount> outDataList = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ProductSaleCount.class));
//        log.info("统计数据返回结果:{}", JSONObject.toJSONString(outDataList));
        for(int i = 0 ; i < outDataList.size();i++){
            ProductSaleCount a = outDataList.get(i);
            if(ObjectUtil.isEmpty(a.getStoProvince())){
                a.setStoProvince("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCity())) {
                a.setStoCity("wk");
            }
            if (ObjectUtil.isEmpty(a.getClient())) {
                a.setClient("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCode())) {
                a.setStoCode("wk");
            }
        }
        Map<String,String> map = new HashMap<>();
        if ("4".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getStoCode() + "-"
                            + o.getProCode(),
                    ProductSaleCount::getSaleCount, (a, b) -> b));
        }
        if ("3".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getProCode(),
                    ProductSaleCount::getSaleCount, (a, b) -> b));
        }
        if ("2".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getStoCity() + "-"
                            + o.getProCode(),
                    ProductSaleCount::getSaleCount, (a, b) -> b));
        }
        if ("1".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                    + o.getProCode(),ProductSaleCount::getSaleCount, (a, b) -> b));
        }
        log.info(CommonUtil.getHHmmss());
        return map;
    }

    public Map<String,String> stoCountMap(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT count(*) stoNum");
        //计算维度 1 省级维度 2 市级维度 3 客户维度 4 门店维度
        if ("4".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE provinceId,a.STO_CITY cityId,a.CLIENT clientId,a.GSSD_BR_ID stoCode ");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE provinceId,a.STO_CITY cityId,a.CLIENT clientId ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE provinceId,a.STO_CITY cityId ");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE provinceId ");
        }
        builder.append(" from (SELECT b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID ");
        builder.append(" FROM GAIA_SD_SALE_D AS a");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS AS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE");
        builder.append(" INNER JOIN GAIA_STORE_DATA AS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE");
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN (  ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID ) a ");
        if ("4".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.GSSD_BR_ID");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<StoreInfoOutData> stoCount = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(StoreInfoOutData.class));
        Map<String,String> map = new HashMap<>();
        for(int i = 0 ; i < stoCount.size();i++){
            StoreInfoOutData a = stoCount.get(i);
            if(ObjectUtil.isEmpty(a.getProvinceId())){
                a.setProvinceId("wk");
            }
            if (ObjectUtil.isEmpty(a.getCityId())) {
                a.setCityId("wk");
            }
            if (ObjectUtil.isEmpty(a.getClientId())) {
                a.setClientId("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCode())) {
                a.setStoCode("wk");
            }
        }
        if ("4".equals(inData.getDimension())) {
            map = stoCount.stream().collect(Collectors.toMap(o -> o.getProvinceId() + "-"
                            + o.getCityId() + "-"
                            + o.getClientId() + "-"
                            + o.getStoCode(),
                    StoreInfoOutData::getStoNum, (a, b) -> b));
        }
        if ("3".equals(inData.getDimension())) {
            map = stoCount.stream().collect(Collectors.toMap(o -> o.getProvinceId() + "-"
                            + o.getCityId() + "-"
                            + o.getClientId(),
                    StoreInfoOutData::getStoNum, (a, b) -> b));
        }
        if ("2".equals(inData.getDimension())) {
            map = stoCount.stream().collect(Collectors.toMap(o -> o.getProvinceId() + "-" + o.getCityId(),
                    StoreInfoOutData::getStoNum, (a, b) -> b));
        }
        if ("1".equals(inData.getDimension())) {
            map = stoCount.stream().collect(Collectors.toMap(o -> o.getProvinceId(),StoreInfoOutData::getStoNum, (a, b) -> b));
        }
        log.info(CommonUtil.getHHmmss());
        return map;
    }

    //商品交易次数
    public Map<String,String> payNumderMap(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT count(*) payNumber,a.PRO_CODE proCode ");
        //计算维度 1 省级维度 2 市级维度 3 客户维度 4 门店维度
        if ("4".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity,a.CLIENT client,a.GSSD_BR_ID stoCode");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity,a.CLIENT client ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity ");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince ");
        }
        builder.append(" from (SELECT b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,a.GSSD_BILL_NO,pb.PRO_CODE ");
        builder.append(" FROM GAIA_SD_SALE_D AS a");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS AS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE");
        builder.append(" INNER JOIN GAIA_STORE_DATA AS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE");
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN (  ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,a.GSSD_BILL_NO,pb.PRO_CODE ) a ");
        if ("4".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.GSSD_BR_ID,a.PRO_CODE");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.PRO_CODE");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.PRO_CODE");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.PRO_CODE");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<ProductBillCount> outDataList = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ProductBillCount.class));
//        log.info("统计数据返回结果:{}", JSONObject.toJSONString(outDataList));
        for(int i = 0 ; i < outDataList.size();i++){
            ProductBillCount a = outDataList.get(i);
            if(ObjectUtil.isEmpty(a.getStoProvince())){
                a.setStoProvince("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCity())) {
                a.setStoCity("wk");
            }
            if (ObjectUtil.isEmpty(a.getClient())) {
                a.setClient("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCode())) {
                a.setStoCode("wk");
            }
        }
        Map<String,String> map = new HashMap<>();
        if ("4".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getStoCode() + "-" + o.getProCode(),
                    ProductBillCount::getPayNumber, (a, b) -> b));
        }
        if ("3".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-" + o.getProCode(),
                    ProductBillCount::getPayNumber, (a, b) -> b));
        }
        if ("2".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getStoCity() + "-" + o.getProCode(),
                    ProductBillCount::getPayNumber, (a, b) -> b));
        }
        if ("1".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getProCode(),ProductBillCount::getPayNumber, (a, b) -> b));
        }
        log.info(CommonUtil.getHHmmss());
        return map;
    }

    //动销门店数
    public Map<String,String> saleStoreNumderMap(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT count(*) icount,a.PRO_CODE proCode ");
        //计算维度 1 省级维度 2 市级维度 3 客户维度 4 门店维度
        if ("4".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity,a.CLIENT client,a.GSSD_BR_ID stoCode");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity,a.CLIENT client ");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince,a.STO_CITY stoCity ");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(",a.STO_PROVINCE stoProvince ");
        }
        builder.append(" from (SELECT b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,pb.PRO_CODE ");
        builder.append(" FROM GAIA_SD_SALE_D AS a");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS AS pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE");
        builder.append(" INNER JOIN GAIA_STORE_DATA AS b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE");
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN (  ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        builder.append(" GROUP BY b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,pb.PRO_CODE ) a ");
        if ("4".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.GSSD_BR_ID,a.PRO_CODE");
        }
        if ("3".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.CLIENT,a.PRO_CODE");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.STO_CITY,a.PRO_CODE");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" GROUP BY a.STO_PROVINCE,a.PRO_CODE");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<ProductSaleStoreCount> outDataList = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ProductSaleStoreCount.class));
//        log.info("统计数据返回结果:{}", JSONObject.toJSONString(outDataList));
        for(int i = 0 ; i < outDataList.size();i++){
            ProductSaleStoreCount a = outDataList.get(i);
            if(ObjectUtil.isEmpty(a.getStoProvince())){
                a.setStoProvince("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCity())) {
                a.setStoCity("wk");
            }
            if (ObjectUtil.isEmpty(a.getClient())) {
                a.setClient("wk");
            }
            if (ObjectUtil.isEmpty(a.getStoCode())) {
                a.setStoCode("wk");
            }
        }
        Map<String,String> map = new HashMap<>();
        if ("4".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-"
                            + o.getStoCode() + "-" + o.getProCode(),
                    ProductSaleStoreCount::getIcount, (a, b) -> b));
        }
        if ("3".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-"
                            + o.getStoCity() + "-"
                            + o.getClient() + "-" + o.getProCode(),
                    ProductSaleStoreCount::getIcount, (a, b) -> b));
        }
        if ("2".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getStoCity() + "-" + o.getProCode(),
                    ProductSaleStoreCount::getIcount, (a, b) -> b));
        }
        if ("1".equals(inData.getDimension())) {
            map = outDataList.stream().collect(Collectors.toMap(o -> o.getStoProvince() + "-" + o.getProCode(),ProductSaleStoreCount::getIcount, (a, b) -> b));
        }
        log.info(CommonUtil.getHHmmss());
        return map;
    }

    public Map<String,String> areaNameMap(){
        List<Map<String,String>> areaNameList = storeDataMapper.selectAreaNameByLevel();
        Map<String,String> map = new HashMap<>();
        for (int i = 0;i < areaNameList.size();i ++){
            map.put(areaNameList.get(i).get("areaId"),areaNameList.get(i).get("areaName"));
        }
        log.info(CommonUtil.getHHmmss());
        return map;
    }

    public Map<String,String> clientNameMap(){
        List<Map<String,String>> clientNameList = storeDataMapper.selectClientName();
        Map<String,String> map = new HashMap<>();
        for (int i = 0;i < clientNameList.size();i ++){
            map.put(clientNameList.get(i).get("clientId"),clientNameList.get(i).get("clientName"));
        }
        log.info(CommonUtil.getHHmmss());
        return map;
    }

    public List<ProductSaleAnalyseOutData> productSaleAnalyseOutDataList(ProductSaleAnalyseInData inData,String startDate,String endDate){
        StringBuilder builder = new StringBuilder().append("SELECT pb.PRO_CODE proCode,");
        if ("3".equals(inData.getDimension())) {
            builder.append("b.STO_PROVINCE provinceId,b.STO_CITY cityId,a.CLIENT clientId,");//用户编码、名称
        }
        if ("4".equals(inData.getDimension())) {
            builder.append("a.CLIENT clientId,a.GSSD_BR_ID stoCode,b.STO_NAME stoName,b.STO_PROVINCE provinceId,b.STO_CITY cityId,b.GSST_VERSION,");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append("b.STO_PROVINCE provinceId,");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append("b.STO_PROVINCE provinceId,b.STO_CITY cityId,");
        }
        builder.append("sum(a.GSSD_QTY) saleQty,sum( a.GSSD_AMT ) gssdAmt,sum(a.GSSD_MOV_PRICE) gssdmovprice");
        builder.append(" FROM GAIA_SD_SALE_D as a ");
        builder.append(" INNER JOIN GAIA_PRODUCT_BUSINESS as pb ON a.CLIENT = pb.CLIENT AND a.GSSD_BR_ID = pb.PRO_SITE AND a.GSSD_PRO_ID = pb.PRO_SELF_CODE");
        builder.append(" INNER JOIN GAIA_STORE_DATA as b ON a.CLIENT = b.CLIENT AND a.GSSD_BR_ID = b.STO_CODE ");
//        builder.append(" LEFT JOIN GAIA_FRANCHISEE as e ON a.CLIENT = e.CLIENT ");//加盟商（客户）
//        builder.append(" LEFT JOIN GAIA_AREA as c ON b.STO_CITY = c.AREA_ID ");//查询市
//        builder.append(" LEFT JOIN GAIA_AREA as d ON b.STO_PROVINCE = d.AREA_ID ");//查询省
        builder.append(" WHERE a.GSSD_DATE >= '"+ startDate +"' AND a.GSSD_DATE <= '" + endDate +"'");
        builder.append(" AND ( pb.PRO_CODE != '99999999' AND pb.PRO_CODE != '' AND pb.PRO_CODE IS NOT NULL ) ");
        builder.append(" AND ( pb.PRO_CLASS NOT LIKE '8%' AND pb.PRO_CLASS NOT LIKE '301%' AND pb.PRO_CLASS NOT LIKE '302%' ) ");
        //省份查询
        if (ObjectUtil.isNotEmpty(inData.getProvinces())){
            builder.append(" AND b.STO_PROVINCE IN ( ");
            for (int i = 0; i < inData.getProvinces().length;i++) {
                if (i == inData.getProvinces().length - 1){
                    builder.append("'" +inData.getProvinces()[i] + "'");
                }else {
                    builder.append("'" +inData.getProvinces()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //市查询
        if (ObjectUtil.isNotEmpty(inData.getCitise())){
            builder.append(" AND b.STO_CITY IN ( ");
            for (int i = 0; i < inData.getCitise().length;i++) {
                if (i == inData.getCitise().length - 1){
                    builder.append("'" +inData.getCitise()[i] + "'");
                }else {
                    builder.append("'" +inData.getCitise()[i] + "',");
                }
            }
            builder.append(" )");
        }
        //客户、门店
        if (ObjectUtil.isNotEmpty(inData.getClientIds())){
            builder.append(" AND a.CLIENT IN ( ");
            for (int i = 0; i < inData.getClientIds().length; i ++){
                if (i == inData.getClientIds().length-1){
                    builder.append("'"+inData.getClientIds()[i]+"'");
                }else {
                    builder.append("'"+inData.getClientIds()[i]+"',");
                }
            }
            builder.append(" )");
        }
        if (ObjectUtil.isNotEmpty(inData.getStoreIds())){
            builder.append(" AND (");
            for (int i = 0; i < inData.getStoreIds().length; i ++){
                String[] arr = inData.getStoreIds()[i].split("-");
                if (i == inData.getStoreIds().length-1){
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"')");
                }else {
                    builder.append("(a.CLIENT = '"+arr[0]+"' AND a.GSSD_BR_ID = '"+arr[1]+"') or ");
                }
            }
            builder.append(" )");
        }
        if (StringUtils.isNotEmpty(inData.getStoreType())) {
            builder.append(" AND b.GSST_VERSION = '"+inData.getStoreType()+"' ");
        }
        builder.append(" GROUP BY pb.PRO_CODE");
        if ("3".equals(inData.getDimension())) {
            builder.append(" ,b.STO_PROVINCE,b.STO_CITY,a.CLIENT");
        }
        if("4".equals(inData.getDimension())) {
            builder.append(" ,b.GSST_VERSION,b.STO_PROVINCE,b.STO_CITY,a.CLIENT,a.GSSD_BR_ID,b.STO_NAME");
        }
        if ("1".equals(inData.getDimension())) {
            builder.append(" ,b.STO_PROVINCE");
        }
        if ("2".equals(inData.getDimension())) {
            builder.append(" ,b.STO_PROVINCE,b.STO_CITY");
        }
        log.info("sql统计数据：{}", builder.toString());
        List<ProductSaleAnalyseOutData> outDataList = kylinJdbcTemplate.query(builder.toString(), RowMapper.getDefault(ProductSaleAnalyseOutData.class));
//        log.info("统计数据返回结果:{}", JSONObject.toJSONString(outDataList));
        return outDataList;
    }

    public Map<String,String> selectStoreType(){
        List<Map<String,String>> storeTypeList = saleReportMapper.selectStoreType();
        Map<String,String> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(storeTypeList)) {
            for (int i = 0; i < storeTypeList.size(); i++) {
                Map<String,String> m = storeTypeList.get(i);
                map.put(m.get("gssgId"),m.get("gssgName"));
            }
        }
        return map;
    }
}
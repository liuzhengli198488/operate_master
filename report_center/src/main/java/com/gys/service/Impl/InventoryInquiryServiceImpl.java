package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.kylin.RowMapper;
import com.gys.common.response.Result;
import com.gys.common.response.ResultUtil;
import com.gys.entity.InData;
import com.gys.entity.data.InventoryInquiry.*;
import com.gys.mapper.*;
import com.gys.report.entity.*;
import com.gys.service.InventoryInquiryService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.CommonUtil;
import com.gys.util.DateUtil;
import com.gys.util.UtilMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryInquiryServiceImpl implements InventoryInquiryService {
    @Resource
    private GaiaInventoryInquiryMapper inventoryInquiryMapper;
    @Resource
    private GaiaStoreDataMapper gaiaStoreDataMapper;
    @Resource
    private GaiaSdStockHMapper sdStockHMapper;
    @Resource
    private GaiaSdStockBatchHMapper sdStockBatchHMapper;
    @Resource
    private GaiaSdStoresGroupMapper gaiaSdStoresGroupMapper;
    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;

    /**
     * 商品实时库存查询
     * ..................................................
     *
     * @param inData
     * @return
     */
    @Override
    public PageInfo inventoryInquiryByRow(InventoryInquiryInData inData) {
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            inData.setStoGssgTypes(stoGssgTypes);
        }
        if (inData.getStoAttribute() != null) {
            inData.setStoAttributes(Arrays.asList(inData.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfMedical() != null) {
            inData.setStoIfMedicals(Arrays.asList(inData.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (inData.getStoTaxClass() != null) {
            inData.setStoTaxClasss(Arrays.asList(inData.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfDtp() != null) {
            inData.setStoIfDtps(Arrays.asList(inData.getStoIfDtp().split(StrUtil.COMMA)));
        }
        List<LinkedHashMap<String, Object>> outData = null;
        InData inData1 = new InData(inData.getClientId());
        BeanUtils.copyProperties(inData, inData1);
        List<InventoryStore> inventoryStore = this.gaiaStoreDataMapper.getInventoryStore(inData1);
        if (ObjectUtil.isNotEmpty(inventoryStore)) {
            inData.setInventoryStore(inventoryStore);
//            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
            outData = this.inventoryInquiryMapper.inventoryInquiryByRow(inData);

        }
        PageInfo pageInfo = null;

        if (ObjectUtil.isNotEmpty(outData)) {
            Map<String, Object> outTotal = this.inventoryInquiryMapper.inventoryInquiryByRowTotal(inData);

            pageInfo = new PageInfo(outData, outTotal);

        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<LinkedHashMap<String, Object>> inventoryInquiryByRowExecl(InventoryInquiryInData inData) {
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        InData inData1 = new InData(inData.getClientId());
        BeanUtils.copyProperties(inData, inData1);
        List<LinkedHashMap<String, Object>> outData = null;
        List<InventoryStore> inventoryStore = this.gaiaStoreDataMapper.getInventoryStore(inData1);
        if (ObjectUtil.isNotEmpty(inventoryStore)) {
            inData.setInventoryStore(inventoryStore);
            outData = this.inventoryInquiryMapper.inventoryInquiryByRow(inData);

        }
        return outData;
    }

    @Override
    public PageInfo selectEndingInventory(EndingInventoryInData inData) {
        List<EndingInventoryOutData> outData = new ArrayList<>();
        if (StringUtils.isEmpty(inData.getRecordDate())) {
            throw new BusinessException("提示：查询日期不能为空!");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        boolean noChooseFlag = true;
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            inData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        if (inData.getStoAttribute() != null) {
            noChooseFlag = false;
            inData.setStoAttributes(Arrays.asList(inData.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfMedical() != null) {
            noChooseFlag = false;
            inData.setStoIfMedicals(Arrays.asList(inData.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (inData.getStoTaxClass() != null) {
            noChooseFlag = false;
            inData.setStoTaxClasss(Arrays.asList(inData.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfDtp() != null) {
            noChooseFlag = false;
            inData.setStoIfDtps(Arrays.asList(inData.getStoIfDtp().split(StrUtil.COMMA)));
        }
        //按批号查询各个地点下的库存
        //获取指定年月当月最后一天
        String date1 = DateUtil.getYearMonthLast(inData.getRecordDate());
        inData.setEndMonth(date1);
        //获取指定年月下个月第一天
        String date2 = DateUtil.getFirstDayOfNextMonth(date1);
        inData.setDcDate(date2);
        if (UtilMessage.ZERO.equals(inData.getType())) {
            outData = this.sdStockBatchHMapper.selectEndingInventoryByBatch(inData);
        } else if (UtilMessage.ONE.equals(inData.getType())) {
            outData = this.sdStockHMapper.selectEndingInventoryByPro(inData);
        }
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClientId());

        for (EndingInventoryOutData endingInventoryOutData : outData) {
            //转换
            if (endingInventoryOutData.getStoAttribute() != null || noChooseFlag) {
                endingInventoryOutData.setStoAttribute(StoreAttributeEnum.getName(endingInventoryOutData.getStoAttribute()));
            }
            if (endingInventoryOutData.getStoIfMedical() != null || noChooseFlag) {
                endingInventoryOutData.setStoIfMedical(StoreMedicalEnum.getName(endingInventoryOutData.getStoIfMedical()));
            }
            if (endingInventoryOutData.getStoIfDtp() != null || noChooseFlag) {
                endingInventoryOutData.setStoIfDtp(StoreDTPEnum.getName(endingInventoryOutData.getStoIfDtp()));
            }
            if (endingInventoryOutData.getStoTaxClass() != null || noChooseFlag) {
                endingInventoryOutData.setStoTaxClass(StoreTaxClassEnum.getName(endingInventoryOutData.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(endingInventoryOutData.getSiteCode())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        endingInventoryOutData.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        endingInventoryOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        endingInventoryOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        endingInventoryOutData.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            endingInventoryOutData.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            endingInventoryOutData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            endingInventoryOutData.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            endingInventoryOutData.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }
//        long start = 0;
//        long stop = 0;
        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(outData)) {
            EndingInventoryOutDataTotal outTotal = new EndingInventoryOutDataTotal();
            // 集合列的数据汇总
//            start = System.currentTimeMillis();
            for (EndingInventoryOutData out : outData) {
                outTotal.setQty(CommonUtil.stripTrailingZeros(outTotal.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outTotal.setTaxes(CommonUtil.stripTrailingZeros(outTotal.getTaxes()).add(CommonUtil.stripTrailingZeros(out.getTaxes())));
                outTotal.setCost(CommonUtil.stripTrailingZeros(outTotal.getCost()).add(CommonUtil.stripTrailingZeros(out.getCost())));
                outTotal.setIncludeTax(CommonUtil.stripTrailingZeros(outTotal.getIncludeTax()).add(CommonUtil.stripTrailingZeros(out.getIncludeTax())));
            }
//            stop = System.currentTimeMillis();
//            long  ss = TimeUnit.MILLISECONDS.toSeconds(stop - start);
//            log.info(String.valueOf(ss));

            pageInfo = new PageInfo(outData, outTotal);

        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<EndingInventoryOutData> selectEndingInventoryCSV(EndingInventoryInData inData) {
        List<EndingInventoryOutData> outData = new ArrayList<>();
        if (StringUtils.isEmpty(inData.getRecordDate())) {
            throw new BusinessException("提示：查询日期不能为空!");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        //按批号查询各个地点下的库存
        //获取指定年月当月最后一天
        String date1 = DateUtil.getYearMonthLast(inData.getRecordDate());
        inData.setEndMonth(date1);
        //获取指定年月下个月第一天
        String date2 = DateUtil.getFirstDayOfNextMonth(date1);
        inData.setDcDate(date2);
        if (UtilMessage.ZERO.equals(inData.getType())) {
            //按批号查询各个地点下的库存
            outData = this.sdStockBatchHMapper.selectEndingInventoryByBatch(inData);
        } else if (UtilMessage.ONE.equals(inData.getType())) {
//            if (StringUtils.isNotEmpty(inData.getProCode())) {
            //按商品搜索的时候 没有商品信息的门店也要带出来
//                outData = this.inventoryInquiryMapper.inventoryInquiryListByAllStoreAndDc(inData);
//            } else {
            //按地点查询
            outData = this.sdStockHMapper.selectEndingInventoryByPro(inData);
//            }

        }
        return outData;
    }

    @SneakyThrows
    @Override
    public PageInfo selectEffectiveGoods(EffectiveGoodsInData inData) {
        List<EffectiveGoodsOutData> outData = new ArrayList<>();
        List<EffectiveGoodsOutData> outHistory = new ArrayList<>();
        List<EffectiveGoodsOutData> outCurrent = new ArrayList<>();

        List<String> dcEndDate = new ArrayList<>();
        if (StringUtils.isEmpty(inData.getStartMonth()) || StringUtils.isEmpty(inData.getEndMonth())) {
            throw new BusinessException("提示：开始时间和结束时间日期不能为空!");
        }
        if (inData.getStartMonth().compareTo(inData.getEndMonth()) == 1) {
            throw new BusinessException("提示：开始时间不能大于结束日期!");
        }
        Date currentDate = new Date(); //当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String currentMonth = sdf.format(currentDate); //当前月
        //查询时间包括当月 当月库存单独计算
        if (inData.getStartMonth().equals(inData.getEndMonth()) && inData.getEndMonth().equals(currentMonth)) {
            outCurrent = this.sdStockBatchHMapper.selectEffectiveGoodsByCurrent(inData);
            outData.addAll(outCurrent);
        } else {
            //如果当前时间大于 选择的时间 202106>202104 用客户选择的结束时间
            //如果等于  都一样
            //小于 用当前时间
            List<String> dcDate = null;
            if (currentMonth.compareTo(inData.getEndMonth()) == 1) {
                dcDate = DateUtil.getMonthBetween(inData.getStartMonth(), inData.getEndMonth());
            } else {
                dcDate = DateUtil.getMonthBetween(inData.getStartMonth(), currentMonth);
            }
            Boolean flag = false;
            //仓库同步一天一次  前一天凌晨  同步昨天数据 2月1日同步 同步1月30号的数据
            for (String month : dcDate) {
                // 排除掉当前月 单独计算
                if (!month.equals(currentMonth)) {
                    dcEndDate.add(DateUtil.getNextMonthFirstDay(month));
                } else {
                    //查询时间包括当月 当月库存单独计算
                    flag = true;
                }
            }
            inData.setDcEndMonth(dcEndDate);
            outHistory = this.sdStockBatchHMapper.selectEffectiveGoodsByHistory(inData);
            outData.addAll(outHistory);
            //查询
            if (flag) {
                outCurrent = this.sdStockBatchHMapper.selectEffectiveGoodsByCurrent(inData);
                outData.addAll(outCurrent);
            }
        }

//        long start = 0;
//        long stop = 0;
        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(outData)) {
            EffectiveGoodsOutDataTotal outTotal = new EffectiveGoodsOutDataTotal();
            // 集合列的数据汇总
//            start = System.currentTimeMillis();
            for (EffectiveGoodsOutData out : outData) {
                outTotal.setInventoryItem(CommonUtil.stripTrailingZeros(outTotal.getInventoryItem()).add(CommonUtil.stripTrailingZeros(out.getInventoryItem())));
                outTotal.setExpiryItem(CommonUtil.stripTrailingZeros(outTotal.getExpiryItem()).add(CommonUtil.stripTrailingZeros(out.getExpiryItem())));

                outTotal.setIncludeTax(CommonUtil.stripTrailingZeros(outTotal.getIncludeTax()).add(CommonUtil.stripTrailingZeros(out.getIncludeTax())));
                outTotal.setExpiryIncludeTax(CommonUtil.stripTrailingZeros(outTotal.getExpiryIncludeTax()).add(CommonUtil.stripTrailingZeros(out.getExpiryIncludeTax())));

                outTotal.setRetailSales(CommonUtil.stripTrailingZeros(outTotal.getRetailSales()).add(CommonUtil.stripTrailingZeros(out.getRetailSales())));
                outTotal.setExpiryRetailSales(CommonUtil.stripTrailingZeros(outTotal.getExpiryRetailSales()).add(CommonUtil.stripTrailingZeros(out.getExpiryRetailSales())));
            }
            DecimalFormat df = new DecimalFormat("0.00%");

            outTotal.setExpiryRate(df.format(CommonUtil.stripTrailingZeros(outTotal.getExpiryItem()).divide(CommonUtil.stripTrailingZeros(outTotal.getInventoryItem()), 4, BigDecimal.ROUND_HALF_EVEN)));
            outTotal.setExpiryIncludeRate(df.format(CommonUtil.stripTrailingZeros(outTotal.getExpiryIncludeTax()).divide(CommonUtil.stripTrailingZeros(outTotal.getIncludeTax()), 4, BigDecimal.ROUND_HALF_EVEN)));
            outTotal.setExpiryRetailRate(df.format(CommonUtil.stripTrailingZeros(outTotal.getExpiryRetailSales()).divide(CommonUtil.stripTrailingZeros(outTotal.getRetailSales()), 4, BigDecimal.ROUND_HALF_EVEN)));

//            stop = System.currentTimeMillis();
//            long  ss = TimeUnit.MILLISECONDS.toSeconds(stop - start);
//            log.info(String.valueOf(ss));

            pageInfo = new PageInfo(outData, outTotal);

        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    /**
     * 不同的条件走不同的逻辑 不同的表
     * 不要问为什么
     * 按地点查询
     *
     * @param inData
     * @return
     */
    @Override
    public PageInfo inventoryInquiryListByStoreAndDc(InventoryInquiryInData inData) {
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        boolean noChooseFlag = true;
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            inData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        if (inData.getStoAttribute() != null) {
            noChooseFlag = false;
            inData.setStoAttributes(Arrays.asList(inData.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfMedical() != null) {
            noChooseFlag = false;
            inData.setStoIfMedicals(Arrays.asList(inData.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (inData.getStoTaxClass() != null) {
            noChooseFlag = false;
            inData.setStoTaxClasss(Arrays.asList(inData.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfDtp() != null) {
            noChooseFlag = false;
            inData.setStoIfDtps(Arrays.asList(inData.getStoIfDtp().split(StrUtil.COMMA)));
        }
        List<InventoryInquiryByClientAndSiteOutData> outData = new ArrayList<>();
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        outData = this.inventoryInquiryMapper.inventoryInquiryListByAllStoreAndDc(inData);
//        if (UtilMessage.ZERO.equals(inData.getType())) {
//            //按批号查询各个地点下的库存
//            outData = this.inventoryInquiryMapper.inventoryInquiryListByStoreAndDcAndBatchNo(inData);
//        } else if (UtilMessage.ONE.equals(inData.getType())) {
//            if (StringUtils.isNotEmpty(inData.getProCode())) {
//                //按商品搜索的时候 没有商品信息的门店也要带出来
//                outData = this.inventoryInquiryMapper.inventoryInquiryListByAllStoreAndDc(inData);
//            } else {
//                //按地点查询
//                outData = this.inventoryInquiryMapper.inventoryInquiryListByStoreAndDc(inData);
//            }
//        }
        //是否过滤库存和库存成本额都为0的数据
        if (inData.getIsHide() != null && inData.getIsHide()) {
            if (outData.size() > 0) {
                outData = outData.stream()
                        .filter(out -> !(BigDecimal.ZERO.compareTo(out.getQty()) == 0 && BigDecimal.ZERO.compareTo(out.getAddAmount()) == 0))
                        .collect(Collectors.toList());
            }
        }

        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(inData.getClientId());

        for (InventoryInquiryByClientAndSiteOutData inventoryInquiry : outData) {
            //转换
            if (inventoryInquiry.getStoAttribute() != null || noChooseFlag) {
                inventoryInquiry.setStoAttribute(StoreAttributeEnum.getName(inventoryInquiry.getStoAttribute()));
            }
            if (inventoryInquiry.getStoIfMedical() != null || noChooseFlag) {
                inventoryInquiry.setStoIfMedical(StoreMedicalEnum.getName(inventoryInquiry.getStoIfMedical()));
            }
            if (inventoryInquiry.getStoIfDtp() != null || noChooseFlag) {
                inventoryInquiry.setStoIfDtp(StoreDTPEnum.getName(inventoryInquiry.getStoIfDtp()));
            }
            if (inventoryInquiry.getStoTaxClass() != null || noChooseFlag) {
                inventoryInquiry.setStoTaxClass(StoreTaxClassEnum.getName(inventoryInquiry.getStoTaxClass()));
            }
            List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(inventoryInquiry.getGssmBrId())).collect(Collectors.toList());
            Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
            for (GaiaStoreCategoryType storeCategoryType : collect) {
                boolean flag = false;
                if (noChooseFlag) {
                    if (storeCategoryType.getGssgType().contains("DX0001")) {
                        inventoryInquiry.setShopType(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0002")) {
                        inventoryInquiry.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0003")) {
                        inventoryInquiry.setDirectManaged(storeCategoryType.getGssgIdName());
                    } else if (storeCategoryType.getGssgType().contains("DX0004")) {
                        inventoryInquiry.setManagementArea(storeCategoryType.getGssgIdName());
                    }
                } else {
                    if (!stoGssgTypeSet.isEmpty()) {
                        if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                            inventoryInquiry.setShopType(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0001");
                        } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                            inventoryInquiry.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0002");
                        } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                            inventoryInquiry.setDirectManaged(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0003");
                        } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                            inventoryInquiry.setManagementArea(storeCategoryType.getGssgIdName());
                            tmpStoGssgTypeSet.remove("DX0004");
                        }
                    }
                }
            }
        }


//        long start = 0;
//        long stop = 0;
        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(outData)) {
            InventoryInquiryByClientAndSiteOutData outNum = new InventoryInquiryByClientAndSiteOutData();
            // 集合列的数据汇总
//            start = System.currentTimeMillis();
            for (InventoryInquiryByClientAndSiteOutData out : outData) {
                outNum.setRetailSales(CommonUtil.stripTrailingZeros(outNum.getRetailSales()).add(CommonUtil.stripTrailingZeros(out.getRetailSales())));
                outNum.setQty(CommonUtil.stripTrailingZeros(outNum.getQty()).add(CommonUtil.stripTrailingZeros(out.getQty())));
                outNum.setCostAmount(CommonUtil.stripTrailingZeros(outNum.getCostAmount()).add(CommonUtil.stripTrailingZeros(out.getCostAmount())));
                outNum.setCostRateAmount(CommonUtil.stripTrailingZeros(outNum.getCostRateAmount()).add(CommonUtil.stripTrailingZeros(out.getCostRateAmount())));
                outNum.setAddAmount(CommonUtil.stripTrailingZeros(outNum.getAddAmount()).add(CommonUtil.stripTrailingZeros(out.getAddAmount())));
            }
//            stop = System.currentTimeMillis();
//            log.info(String.valueOf(stop - start));
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);

        } else {
            pageInfo = new PageInfo();
        }


        return pageInfo;
    }

    /**
     * 商品实时库存查询
     * ..................................................
     *
     * @param inData
     * @return
     */
    @Override
    public PageInfo inventoryInquiryListByClient(InventoryInquiryInData inData) {
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        boolean noChooseFlag = true;
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            inData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        if (inData.getStoAttribute() != null) {
            noChooseFlag = false;
            inData.setStoAttributes(Arrays.asList(inData.getStoAttribute().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfMedical() != null) {
            noChooseFlag = false;
            inData.setStoIfMedicals(Arrays.asList(inData.getStoIfMedical().split(StrUtil.COMMA)));
        }
        if (inData.getStoTaxClass() != null) {
            noChooseFlag = false;
            inData.setStoTaxClasss(Arrays.asList(inData.getStoTaxClass().split(StrUtil.COMMA)));
        }
        if (inData.getStoIfDtp() != null) {
            noChooseFlag = false;
            inData.setStoIfDtps(Arrays.asList(inData.getStoIfDtp().split(StrUtil.COMMA)));
        }
        List<InventoryInquiryByClientAndBatchNoOutData> outData = new ArrayList<>();
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (UtilMessage.ZERO.equals(inData.getType())) {
            outData = this.inventoryInquiryMapper.inventoryInquiryListByClientAndSiteAndBatchNo(inData);

        } else if (UtilMessage.ONE.equals(inData.getType())) {
            outData = this.inventoryInquiryMapper.inventoryInquiryListByClient(inData);
        }

        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(outData)) {
            InventoryInquiryByClientAndBatchNoOutData outNum = new InventoryInquiryByClientAndBatchNoOutData();
            // 集合列的数据汇总
            for (InventoryInquiryByClientAndBatchNoOutData out : outData) {
                outNum.setQtySum(CommonUtil.stripTrailingZeros(outNum.getQtySum()).add(CommonUtil.stripTrailingZeros(out.getQtySum())));
                outNum.setCostSum(CommonUtil.stripTrailingZeros(outNum.getCostSum()).add(CommonUtil.stripTrailingZeros(out.getCostSum())));
                outNum.setCostRateSum(CommonUtil.stripTrailingZeros(outNum.getCostRateSum()).add(CommonUtil.stripTrailingZeros(out.getCostRateSum())));

                outNum.setStoreQty(CommonUtil.stripTrailingZeros(outNum.getStoreQty()).add(CommonUtil.stripTrailingZeros(out.getStoreQty())));
                outNum.setStoreCostAmount(CommonUtil.stripTrailingZeros(outNum.getStoreCostAmount()).add(CommonUtil.stripTrailingZeros(out.getStoreCostAmount())));
                outNum.setStoreCostRateAmount(CommonUtil.stripTrailingZeros(outNum.getStoreCostRateAmount()).add(CommonUtil.stripTrailingZeros(out.getStoreCostRateAmount())));

                outNum.setDcQty(CommonUtil.stripTrailingZeros(outNum.getDcQty()).add(CommonUtil.stripTrailingZeros(out.getDcQty())));
                outNum.setDcCostAmount(CommonUtil.stripTrailingZeros(outNum.getDcCostAmount()).add(CommonUtil.stripTrailingZeros(out.getDcCostAmount())));
                outNum.setDcCostRateAmount(CommonUtil.stripTrailingZeros(outNum.getDcCostRateAmount()).add(CommonUtil.stripTrailingZeros(out.getDcCostRateAmount())));
            }
            pageInfo = new PageInfo(outData);
            pageInfo.setListNum(outNum);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public Result querySalesInfo(InventoryInquiryInData inData) {
        List<InventoryInquiryByClientAndSiteOutData> list = this.inventoryInquiryMapper.querySalesInfo(inData);
        return ResultUtil.success(list);
    }

    @Override
    public void inventoryInquiryListByStoreExport(InventoryInquiryInData inData, HttpServletResponse response) throws IOException {
        List<InventoryInquiryByClientAndSiteOutData> list = this.inventoryInquiryListByStoreAndDc(inData).getList();
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException("导出数据不能为空");
        }
        String fileName = null;
        try {
            fileName = URLEncoder.encode("地点实时库存导出", "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        Object export = null;
        if ("1".equals(inData.getType()) && inData.getIsSales() == 0) {
            //仅开启批号
            export = new InventoryInquiryByClientAndSiteOutDataByBatch();
        } else if ("0".equals(inData.getType()) && inData.getIsSales() == 1) {
            //仅开启业务员
            export = new InventoryInquiryByClientAndSiteOutDataBySalesman();
        } else if ("1".equals(inData.getType()) && inData.getIsSales() == 1) {
            //开启批号和开启业务员
            export = new InventoryInquiryOutDataByBatchAndSalesman();
        } else {
            //默认
            export = new InventoryInquiryByClientAndSiteOutData();
        }
        try {
            EasyExcel.write(response.getOutputStream(), export.getClass())
                    .sheet("0")
                    .doWrite(list);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

}

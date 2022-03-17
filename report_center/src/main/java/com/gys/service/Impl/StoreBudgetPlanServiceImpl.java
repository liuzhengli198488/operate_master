package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.gys.common.constant.CommonConstant;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.data.StoreBudgetPlanInData;
import com.gys.mapper.GaiaSdStoresGroupMapper;
import com.gys.mapper.StoreBudgetPlanMapper;
import com.gys.report.entity.GaiaStoreCategoryType;
import com.gys.report.entity.StoreBudgetPlanByStoCodeOutData;
import com.gys.report.entity.StoreBudgetPlanOutData;
import com.gys.report.entity.StoreDiscountSummaryData;
import com.gys.service.StoreBudgetPlanService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.ExcelUtils;
import com.gys.util.ValidateUtil;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StoreBudgetPlanServiceImpl implements StoreBudgetPlanService {
    @Autowired
    private StoreBudgetPlanMapper storeBudgetPlanMapper;
    @Resource
    private GaiaSdStoresGroupMapper gaiaSdStoresGroupMapper;
    @Resource
    public CosUtils cosUtils;

    @Override
    public PageInfo findStoreBudgePlanByDate(StoreBudgetPlanInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        List<Map<String, Object>> outData = storeBudgetPlanMapper.findStoreBudgePlanByDate(inData);

        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {

            // 集合列的数据汇总
            // Map<String, Object> outSto = storeBudgetPlanMapper.findStoreBudgePlanByTotal(inData);
            // Long stoCount = (Long) outSto.get("stoCount");

            Map<String, Object> outTotal = new HashMap<>();
            for (Map<String, Object> out : outData) {
                if (outTotal.containsKey("amountReceivable")) {
                    BigDecimal amountReceivable = new BigDecimal(outTotal.get("amountReceivable").toString()).add(new BigDecimal(out.get("amountReceivable").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amountReceivable", amountReceivable);
                } else {
                    outTotal.put("amountReceivable", out.get("amountReceivable").toString());
                }
                if (outTotal.containsKey("amt")) {
                    BigDecimal amt = new BigDecimal(outTotal.get("amt").toString()).add(new BigDecimal(out.get("amt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amt", amt);
                } else {
                    outTotal.put("amt", out.get("amt").toString());
                }
                if (outTotal.containsKey("movPrices")) {
                    BigDecimal movPrices = new BigDecimal(outTotal.get("movPrices").toString()).add(new BigDecimal(out.get("movPrices").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("movPrices", movPrices);
                } else {
                    outTotal.put("movPrices", out.get("movPrices").toString());
                }
                if (outTotal.containsKey("grossProfitMargin")) {
                    BigDecimal grossProfitMargin = new BigDecimal(outTotal.get("grossProfitMargin").toString()).add(new BigDecimal(out.get("grossProfitMargin").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("grossProfitMargin", grossProfitMargin);
                } else {
                    outTotal.put("grossProfitMargin", out.get("grossProfitMargin").toString());
                }
                if (outTotal.containsKey("discountAmt")) {
                    BigDecimal discountAmt = new BigDecimal(outTotal.get("discountAmt").toString()).add(new BigDecimal(out.get("discountAmt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("discountAmt", discountAmt);
                } else {
                    outTotal.put("discountAmt", out.get("discountAmt").toString());
                }
            }
            List<StoreBudgetPlanOutData> list = JSON.parseArray(JSON.toJSONString(outData), StoreBudgetPlanOutData.class);
            pageInfo = new PageInfo(list, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public void exportStoreBudgePlanByDateDetails(StoreBudgetPlanInData inData, HttpServletResponse response) {
        //调用查询接口 查询数据 然后导出
        PageInfo result = findStoreBudgePlanByDate(inData);
        if (Objects.isNull(result) && CollUtil.isEmpty(result.getList())) {
            throw new BusinessException("提示：导出数据为空");
        }
        List<Map<String, Object>> storeList = (List<Map<String, Object>>) result.getList();
        Map<String, Object> outTotal = (Map<String, Object>) result.getListNum();
        if (ValidateUtil.isEmpty(storeList)) {
            throw new BusinessException("提示：导出数据为空");
        }
        CsvFileInfo fileInfo = new CsvFileInfo(new byte[0], 0, "日期销售折扣汇总表");
        List<StoreBudgetPlanOutData> list = JSON.parseArray(JSON.toJSONString(storeList), StoreBudgetPlanOutData.class);

        CsvClient.endHandle(response, list, fileInfo, () -> {
            if (ObjectUtil.isEmpty(fileInfo.getFileContent())) {
                throw new BusinessException("导出数据不能为空！");
            }
            byte[] bytes = ("\r\n合计,,," + outTotal.get("movPrices") + "," + outTotal.get("amt") + "," + outTotal.get("amountReceivable") + "," + outTotal.get("grossProfitMargin") + "," + "," + outTotal.get("discountAmt")).getBytes(StandardCharsets.UTF_8);
            byte[] all = ArrayUtils.addAll(fileInfo.getFileContent(), bytes);
            fileInfo.setFileContent(all);
            fileInfo.setFileSize(all.length);
        });
    }

    @Override
    public PageInfo findStoreBudgePlanByStoCodeAndDate(StoreBudgetPlanInData inData) {
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        List<Map<String, Object>> outData = storeBudgetPlanMapper.findStoreBudgePlanByStoCodeAndDate(inData);
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            Map<String, Object> outTotal = new HashMap<>();
            for (Map<String, Object> out : outData) {
                if (outTotal.containsKey("amountReceivable")) {
                    BigDecimal amountReceivable = new BigDecimal(outTotal.get("amountReceivable").toString()).add(new BigDecimal(out.get("amountReceivable").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amountReceivable", amountReceivable);
                } else {
                    outTotal.put("amountReceivable", out.get("amountReceivable").toString());
                }
                if (outTotal.containsKey("amt")) {
                    BigDecimal amt = new BigDecimal(outTotal.get("amt").toString()).add(new BigDecimal(out.get("amt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amt", amt);
                } else {
                    outTotal.put("amt", out.get("amt").toString());
                }
                if (outTotal.containsKey("movPrices")) {
                    BigDecimal movPrices = new BigDecimal(outTotal.get("movPrices").toString()).add(new BigDecimal(out.get("movPrices").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("movPrices", movPrices);
                } else {
                    outTotal.put("movPrices", out.get("movPrices").toString());
                }
                if (outTotal.containsKey("grossProfitMargin")) {
                    BigDecimal grossProfitMargin = new BigDecimal(outTotal.get("grossProfitMargin").toString()).add(new BigDecimal(out.get("grossProfitMargin").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("grossProfitMargin", grossProfitMargin);
                } else {
                    outTotal.put("grossProfitMargin", out.get("grossProfitMargin").toString());
                }
                if (outTotal.containsKey("discountAmt")) {
                    BigDecimal discountAmt = new BigDecimal(outTotal.get("discountAmt").toString()).add(new BigDecimal(out.get("discountAmt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("discountAmt", discountAmt);
                } else {
                    outTotal.put("discountAmt", out.get("discountAmt").toString());
                }
            }
            List<StoreBudgetPlanByStoCodeOutData> list = JSON.parseArray(JSON.toJSONString(outData), StoreBudgetPlanByStoCodeOutData.class);
            pageInfo = new PageInfo(list, outTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    @Override
    public void exportStoreBudgePlanByStoCodeAndDateDetails(StoreBudgetPlanInData inData, HttpServletResponse response) {
        //调用查询接口 查询数据 然后导出
        PageInfo result = findStoreBudgePlanByStoCodeAndDate(inData);
        if (Objects.isNull(result) && CollUtil.isEmpty(result.getList())) {
            throw new BusinessException("提示：导出数据为空");
        }
        List<Map<String, Object>> storeList = (List<Map<String, Object>>) result.getList();
        Map<String, Object> outTotal = (Map<String, Object>) result.getListNum();
        if (ValidateUtil.isEmpty(storeList)) {
            throw new BusinessException("提示：导出数据为空");
        }
        CsvFileInfo fileInfo = new CsvFileInfo(new byte[0], 0, "门店日期销售折扣表");
        List<StoreBudgetPlanByStoCodeOutData> list = JSON.parseArray(JSON.toJSONString(storeList), StoreBudgetPlanByStoCodeOutData.class);

        CsvClient.endHandle(response, list, fileInfo, () -> {
            if (ObjectUtil.isEmpty(fileInfo.getFileContent())) {
                throw new BusinessException("导出数据不能为空！");
            }
            byte[] bytes = ("\r\n合计,,,," + outTotal.get("movPrices") + "," + outTotal.get("amt") + "," + outTotal.get("amountReceivable") + "," + outTotal.get("grossProfitMargin") + "," + "," + outTotal.get("discountAmt")).getBytes(StandardCharsets.UTF_8);
            byte[] all = ArrayUtils.addAll(fileInfo.getFileContent(), bytes);
            fileInfo.setFileContent(all);
            fileInfo.setFileSize(all.length);
        });
    }

    @Override
    public PageInfo selectStoreDiscountSummary(StoreBudgetPlanInData budgetPlanInData) {
        if (ObjectUtil.isEmpty(budgetPlanInData.getStartDate())) {
            throw new BusinessException("起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(budgetPlanInData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        }
        String gssgId = budgetPlanInData.getGssgId();
        if (StringUtils.isNotBlank(gssgId)) {
            budgetPlanInData.setGssgIds(Arrays.asList(gssgId.split(StrUtil.COMMA)));
        }
        Set<String> stoGssgTypeSet = new HashSet<>();
        String stoGssgType = budgetPlanInData.getStoGssgType();
        boolean noChooseFlag = true;
        if (stoGssgType != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : stoGssgType.split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
                stoGssgTypeSet.add(str[0]);
            }
            budgetPlanInData.setStoGssgTypes(stoGssgTypes);
            noChooseFlag = false;
        }
        String stoAttribute = budgetPlanInData.getStoAttribute();
        if (stoAttribute != null) {
            noChooseFlag = false;
            budgetPlanInData.setStoAttributes(Arrays.asList(stoAttribute.split(StrUtil.COMMA)));
        }
        String stoIfMedical = budgetPlanInData.getStoIfMedical();
        if (stoIfMedical != null) {
            noChooseFlag = false;
            budgetPlanInData.setStoIfMedicals(Arrays.asList(stoIfMedical.split(StrUtil.COMMA)));
        }
        String stoTaxClass = budgetPlanInData.getStoTaxClass();
        if (stoTaxClass != null) {
            noChooseFlag = false;
            budgetPlanInData.setStoTaxClasss(Arrays.asList(stoTaxClass.split(StrUtil.COMMA)));
        }
        String stoIfDtp = budgetPlanInData.getStoIfDtp();
        if (stoIfDtp != null) {
            noChooseFlag = false;
            budgetPlanInData.setStoIfDtps(Arrays.asList(stoIfDtp.split(StrUtil.COMMA)));
        }
        List<Map<String, Object>> outData = storeBudgetPlanMapper.selectStoreDiscountSummary(budgetPlanInData);
        List<GaiaStoreCategoryType> storeCategoryByClient = gaiaSdStoresGroupMapper.selectStoreCategoryByClient(budgetPlanInData.getClient());
        Map<String, Object> outTotal = new HashMap<>();
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {

            for (Map<String, Object> item : outData) {
                String brId = MapUtil.getStr(item, "stoCode");
                // 转换
                if (stoAttribute != null || noChooseFlag) {
                    item.put("stoAttribute", StoreAttributeEnum.getName(MapUtil.getStr(item, "stoAttribute")));
                }
                if (stoIfMedical != null || noChooseFlag) {
                    item.put("stoIfMedical", StoreMedicalEnum.getName(MapUtil.getStr(item, "stoIfMedical")));
                }
                if (stoIfDtp != null || noChooseFlag) {
                    item.put("stoIfDtp", StoreDTPEnum.getName(MapUtil.getStr(item, "stoIfDtp")));
                }
                if (stoTaxClass != null || noChooseFlag) {
                    item.put("stoTaxClass", StoreTaxClassEnum.getName(MapUtil.getStr(item, "stoTaxClass")));
                }
                List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(brId)).collect(Collectors.toList());

                Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);


                for (GaiaStoreCategoryType gaiaStoreCategoryType : collect) {
                    String field = null;
                    boolean flag = false;
                    String gssgType = gaiaStoreCategoryType.getGssgType();
                    if (noChooseFlag) {
                        if (gssgType.contains("DX0001")) {
                            field = "shopType";
                        } else if (gssgType.contains("DX0002")) {
                            field = "storeEfficiencyLevel";
                        } else if (gssgType.contains("DX0003")) {
                            field = "directManaged";
                        } else if (gssgType.contains("DX0004")) {
                            field = "managementArea";
                        }
                        if (StringUtils.isNotBlank(field)) {
                            item.put(field, gaiaStoreCategoryType.getGssgIdName());
                        }
                    } else {
                        if (!stoGssgTypeSet.isEmpty()) {
                            if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(gssgType)) {
                                field = "shopType";
                                flag = true;
                                tmpStoGssgTypeSet.remove("DX0001");
                            } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(gssgType)) {
                                field = "storeEfficiencyLevel";
                                flag = true;
                                tmpStoGssgTypeSet.remove("DX0002");
                            } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(gssgType)) {
                                field = "directManaged";
                                flag = true;
                                tmpStoGssgTypeSet.remove("DX0003");
                            } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(gssgType)) {
                                field = "managementArea";
                                flag = true;
                                tmpStoGssgTypeSet.remove("DX0004");
                            }
                        }
                        if (flag) {
                            item.put(field, gaiaStoreCategoryType.getGssgIdName());
                        }
                    }
                }

                if (outTotal.containsKey("amountReceivable")) {
                    BigDecimal amountReceivable = new BigDecimal(outTotal.get("amountReceivable").toString()).add(new BigDecimal(item.get("amountReceivable").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amountReceivable", amountReceivable);
                } else {
                    outTotal.put("amountReceivable", item.get("amountReceivable").toString());
                }
                if (outTotal.containsKey("amt")) {
                    BigDecimal amt = new BigDecimal(outTotal.get("amt").toString()).add(new BigDecimal(item.get("amt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("amt", amt);
                } else {
                    outTotal.put("amt", item.get("amt").toString());
                }
                if (outTotal.containsKey("movPrices")) {
                    BigDecimal movPrices = new BigDecimal(outTotal.get("movPrices").toString()).add(new BigDecimal(item.get("movPrices").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("movPrices", movPrices);
                } else {
                    outTotal.put("movPrices", item.get("movPrices").toString());
                }
                if (outTotal.containsKey("grossProfitMargin")) {
                    BigDecimal grossProfitMargin = new BigDecimal(outTotal.get("grossProfitMargin").toString()).add(new BigDecimal(item.get("grossProfitMargin").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("grossProfitMargin", grossProfitMargin);
                } else {
                    outTotal.put("grossProfitMargin", item.get("grossProfitMargin").toString());
                }
                if (outTotal.containsKey("discountAmt")) {
                    BigDecimal discountAmt = new BigDecimal(outTotal.get("discountAmt").toString()).add(new BigDecimal(item.get("discountAmt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP);
                    outTotal.put("discountAmt", discountAmt);
                } else {
                    outTotal.put("discountAmt", item.get("discountAmt").toString());
                }

            }
            List<StoreDiscountSummaryData> list = JSON.parseArray(JSON.toJSONString(outData), StoreDiscountSummaryData.class);
            pageInfo = new PageInfo(list, outTotal);

        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public Result exportStoreDiscountSummary(StoreBudgetPlanInData inData) {

        //调用查询接口 查询数据 然后导出
        PageInfo result = selectStoreDiscountSummary(inData);
        if (Objects.isNull(result) && CollUtil.isEmpty(result.getList())) {
            throw new BusinessException("提示：导出数据为空");
        }
        List<StoreDiscountSummaryData> storeList = result.getList();
        if (ValidateUtil.isEmpty(storeList)) {
            throw new BusinessException("提示：导出数据为空");
        }
        List<List<Object>> dataList = new ArrayList<>(storeList.size());
        List<Object> lineList = new ArrayList<>();
        List<String> list = decideTitles(inData);
        for (StoreDiscountSummaryData param : storeList) {
            //每行数据
            lineList = new ArrayList<>();
            //销售日期
            lineList.add(Convert.toStr(param.getSaleDate()));
            //门店编码
            lineList.add(Convert.toStr(param.getStoCode()));
            //门店名称
            lineList.add(Convert.toStr(param.getStoName()));
            //销售天数
            lineList.add(Convert.toStr(param.getSalesDays()));
            //成本额
            lineList.add(Convert.toStr(param.getMovPrices()));
            //应收金额
            lineList.add(Convert.toStr(param.getAmt()));
            //实收金额
            lineList.add(Convert.toStr(param.getAmountReceivable()));
            //毛利额
            lineList.add(Convert.toStr(param.getGrossProfitMargin()));
            //毛利率
            lineList.add(Convert.toStr(param.getGrossProfitRates()));
            //折扣率
            lineList.add(Convert.toStr(param.getGsdStoReBates()));
            //折扣总额
            lineList.add(Convert.toStr(param.getDiscountAmt()));
            //税率
            lineList.add(Convert.toStr(param.getStoTaxRates()));
            if (list.contains("门店属性")) {
                //门店属性
                lineList.add(Convert.toStr(param.getStoAttribute()));
            }
            if (list.contains("是否医保店")) {
                //是否医保店
                lineList.add(Convert.toStr(param.getStoIfMedical()));
            }
            if (list.contains("DTP")) {
                //DTP
                lineList.add(Convert.toStr(param.getStoIfDtp()));
            }
            if (list.contains("纳税属性")) {
                //纳税属性
                lineList.add(Convert.toStr(param.getStoTaxClass()));
            }
            if (list.contains("铺货店型")) {
                //铺货店型
                lineList.add(Convert.toStr(param.getShopType()));
            }
            if (list.contains("店效级别")) {
                //店效级别
                lineList.add(Convert.toStr(param.getStoreEfficiencyLevel()));
            }
            if (list.contains("是否直营管理")) {
                //是否直营管理
                lineList.add(Convert.toStr(param.getDirectManaged()));
            }
            if (list.contains("管理区域")) {
                //管理区域
                lineList.add(Convert.toStr(param.getManagementArea()));
            }
            dataList.add(lineList);
        }
        List<Object> total = new ArrayList<>();
        Map<String, Object> totalSum = (Map<String, Object>) result.getListNum();
        total.add("合计");
        //销售日期
        total.add(null);
        //门店编码
        total.add(null);
        //门店名称
        total.add(null);
        //销售天数
        //成本额
        total.add(Convert.toStr(totalSum.get("movPrices")));
        //应收金额
        total.add(Convert.toStr(totalSum.get("amt")));
        //实收金额
        total.add(Convert.toStr(totalSum.get("amountReceivable")));
        //毛利额
        total.add(Convert.toStr(totalSum.get("grossProfitMargin")));
        //毛利率
        total.add(null);
        //折扣率
        total.add(null);
        //折扣总额
        total.add(Convert.toStr(totalSum.get("discountAmt")));
        dataList.addAll(Arrays.asList(total));

        //写入文件
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HSSFWorkbook workbook = ExcelUtils.exportExcel2(
                new ArrayList<String[]>() {{
                    add(list.toArray(new String[0]));
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(dataList);
                }},
                new ArrayList<String>() {{
                    add(CommonConstant.STORE_DISCOUNT_SUMMARY_REPORT_NAME);
                }});

        Result uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = CommonConstant.STORE_DISCOUNT_SUMMARY_REPORT_NAME + "-" + CommonUtil.getyyyyMMdd() + ".xls";
            uploadResult = cosUtils.uploadFile(bos, fileName);
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

    public List<String> decideTitles(StoreBudgetPlanInData inData) {
        String[] titles = CommonConstant.STORE_DISCOUNT_SUMMARY_REPORT_USERS;
        List<String> titleList = new ArrayList<String>(Arrays.asList(titles));
        String stoAttribute = inData.getStoAttribute();
        String stoIfMedical = inData.getStoIfMedical();
        String stoTaxClass = inData.getStoTaxClass();
        String stoIfDtp = inData.getStoIfDtp();
        String stoGssgType = inData.getStoGssgType();
        if (stoAttribute == null && stoIfMedical == null && stoTaxClass == null && stoIfDtp == null && stoGssgType == null) {
        } else {
            if (stoAttribute == null) {
                titleList.remove("门店属性");
            }
            if (stoIfMedical == null) {
                titleList.remove("是否医保店");
            }
            if (stoTaxClass == null) {
                titleList.remove("纳税属性");
            }
            if (stoIfDtp == null) {
                titleList.remove("DTP");
            }
            if (stoGssgType == null) {
                titleList.remove("铺货店型");
                titleList.remove("店效级别");
                titleList.remove("是否直营管理");
                titleList.remove("管理区域");
            } else {
                Set<String> stoGssgTypeSet = new HashSet<>();
                for (String s : stoGssgType.split(StrUtil.COMMA)) {
                    String[] str = s.split(StrUtil.UNDERLINE);
                    stoGssgTypeSet.add(str[0]);
                }

                if (!stoGssgTypeSet.contains("DX0001")) {
                    titleList.remove("铺货店型");
                }
                if (!stoGssgTypeSet.contains("DX0002")) {
                    titleList.remove("店效级别");
                }
                if (!stoGssgTypeSet.contains("DX0003")) {
                    titleList.remove("是否直营管理");
                }
                if (!stoGssgTypeSet.contains("DX0004")) {
                    titleList.remove("管理区域");
                }
            }
        }
        return titleList;
    }
}

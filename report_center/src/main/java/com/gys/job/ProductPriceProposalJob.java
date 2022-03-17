package com.gys.job;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gys.common.enums.PriceProposalEnum;
import com.gys.entity.GaiaSdMessage;
import com.gys.entity.priceProposal.dto.*;
import com.gys.entity.priceProposal.entity.ProductPriceProposalZ;
import com.gys.mapper.GaiaSdMessageMapper;
import com.gys.mapper.ProductPriceProposalMapper;
import com.gys.service.GaiaSdMessageService;
import com.gys.service.ProductPriceHandleService;
import com.gys.util.DateUtil;
import com.gys.util.ValidateUtil;
import com.gys.util.priceProposal.PriceProposalUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议job
 * @CreateTime 2022-01-17 14:17:00
 */
@Slf4j
@Component
@Controller
@RequestMapping({"/job/"})
public class ProductPriceProposalJob {

    @Autowired
    private ProductPriceHandleService priceHandleService;

    @Autowired
    private ProductPriceProposalMapper priceProposalMapper;

    @Resource
    private GaiaSdMessageService gaiaSdMessageService;

    @Resource
    private GaiaSdMessageMapper gaiaSdMessageMapper;


    @XxlJob("priceProposalData")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping({"makeData"})
    public ReturnT<String> priceProposalData() {
        XxlJobHelper.log("===============[价格建议]定时任务开始===============");
        log.info("=======================[价格建议]定时任务开始=======================");
        Long start = System.currentTimeMillis();
        String param = XxlJobHelper.getJobParam();
//        log.info("[价格建议]定时任务入参：{}" + param);
        String clientId = param;
        String date = "";
        date = ValidateUtil.isEmpty(date) ? DateUtil.formatDate(new Date(), "yyyyMMdd") : date;
        List<PriceProposalADTO> resList = Lists.newArrayList();
        Map<String, String> msgMap = Maps.newHashMap();
        String maxNo = getMaxNo();
        // 查询所有加盟商
        if (StrUtil.isBlank(clientId)) {
            clientId = null;
        }
        List<PriceProposalClientDTO> allClients = priceProposalMapper.selectAllClients(clientId);
        for (PriceProposalClientDTO client : allClients) {
            // 查询本月是否跑过品类模型
            List<ProductPriceProposalZ> isRunRangeList = priceProposalMapper.selectIsRunRangeList(client.getClientId());
            if (isRunRangeList != null && isRunRangeList.size() > 0) {
                log.info("=======================[价格建议]本月已经跑过品类模型数据=======================");
                XxlJobHelper.log("=======================[价格建议]本月已经跑过品类模型数据=======================");
                List<ProductPriceProposalZ> matchProposalProsList = priceHandleService.matchStoPriceByRange(isRunRangeList, client);
                log.info("=======================[价格建议]根据客户类型匹配商品价格是否在定价区间内=======================");
                XxlJobHelper.log("=======================[价格建议]根据客户类型匹配商品价格是否在定价区间内=======================");
                List<PriceProposalADTO> needSaveProsList = priceHandleService.getNeedSaveProposalPros(matchProposalProsList);
                log.info("=======================[价格建议]计算比例需要价格建议的商品=======================");
                XxlJobHelper.log("=======================[价格建议]计算比例需要价格建议的商品=======================");
                resList.addAll(needSaveProsList);
                msgMap.put(client.getClientId(), date);
            } else {
                // 市级维度数据提取
                List<CityDimensionDTO> cityDimensionDTOList = priceProposalMapper.selectCityDimensionList(client.getProvinceId(), client.getCityId());
                log.info("=======================[价格建议]定时任务查询出市级维度=======================");
                XxlJobHelper.log("=======================[价格建议]定时任务查询出市级维度=======================");
                // 市级维度价格区间划分
                List<ProductPriceProposalZ> cityProClassifyDTOList = priceHandleService.handleCityDimension(cityDimensionDTOList, maxNo, client);
                // 门店维度
                List<ProductPriceProposalZ> stoProRangeDTOList = priceHandleService.handleStoDimension(cityProClassifyDTOList, maxNo, client);
                log.info("=======================[价格建议]定时任务查询出门店维度=======================");
                XxlJobHelper.log("=======================[价格建议]定时任务查询出门店维度=======================");
                // 汇总商品贝叶斯概率,贝叶斯概率汇总最高的价格区间为定价区间
                List<ProductPriceProposalZ> maxList = priceHandleService.summaryStoMaxBayesianProbability(stoProRangeDTOList, client);
                log.info("=======================[价格建议]汇总商品贝叶斯概率=======================");
                XxlJobHelper.log("=======================[价格建议]汇总商品贝叶斯概率=======================");
                List<ProductPriceProposalZ> randomList = priceHandleService.randomStoRange(maxList);

                log.info("=======================[价格建议]商品定价区间随机值处理=======================");
                XxlJobHelper.log("=======================[价格建议]商品定价区间随机值处理=======================");
                List<ProductPriceProposalZ> matchProposalProsList = priceHandleService.matchStoPriceByRange(randomList, client);

                log.info("=======================[价格建议]根据客户类型匹配商品价格是否在定价区间内=======================");
                XxlJobHelper.log("=======================[价格建议]根据客户类型匹配商品价格是否在定价区间内=======================");
                List<PriceProposalADTO> needSaveProsList = priceHandleService.getNeedSaveProposalPros(matchProposalProsList);

                log.info("=======================[价格建议]计算比例需要价格建议的商品=======================");
                XxlJobHelper.log("=======================[价格建议]计算比例需要价格建议的商品=======================");
                resList.addAll(needSaveProsList);
                msgMap.put(client.getClientId(), date);
            }
        }
        DateTimeFormatter nowDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String invalidDate = PriceProposalUtil.getInvalidTime(LocalDateTime.now().format(nowDate));
        priceHandleService.savePros(resList, maxNo, invalidDate);
        log.info("=======================[价格建议]存储需要价格建议的商品=======================");
        XxlJobHelper.log("=======================[价格建议]存储需要价格建议的商品=======================");
        log.info("=======================[价格建议]发送消息=======================");
        XxlJobHelper.log("=======================[价格建议]发送消息=======================");
        sendMessage(msgMap, invalidDate);
        log.info("=======================[价格建议]定时任务结束=======================");
        XxlJobHelper.log("=======================[价格建议]定时任务结束=======================");
        Long end = System.currentTimeMillis();
        float time = (float)((end - start) / 1000);
        log.info("=======================[价格建议]任务耗时:" +new BigDecimal(time).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue() + "=======================");
        XxlJobHelper.log("=======================[价格建议]任务耗时:" +new BigDecimal(time).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue() + "=======================");
        XxlJobHelper.log("===============[价格建议]定时任务结束===============");
        return ReturnT.SUCCESS;
    }

    @XxlJob("autoInvalid")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> autoInvalid() {
        XxlJobHelper.log("===============[价格建议-自动失效]定时任务开始===============");
        String param = XxlJobHelper.getJobParam();
        String date = ValidateUtil.isEmpty(param) ? DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss") : param;
        log.info("[价格建议-自动失效]批处理日期：{}", date);
        if (ValidateUtil.isEmpty(date)) {
            log.info("=========[价格建议-自动失效]批处理时间获取异常=========");
            return null;
        }
        List<String> nos = priceProposalMapper.selectInvalidNo(date);
        nos.forEach(n -> {
            priceProposalMapper.updatePriceProposalInvalid(n);
            priceProposalMapper.updateGSMInfo(date);
        });
        XxlJobHelper.log("===============[价格建议-自动失效]定时任务结束===============");
        return ReturnT.SUCCESS;
    }

    @PostMapping({"clearMsg"})
    @Transactional(rollbackFor = Exception.class)
    public String clearMsg() {
        priceProposalMapper.updateAllGSMInfo();
        return "success";
    }

    private String getMaxNo() {
        DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = LocalDate.now().format(fmDate);
        String maxNo = priceProposalMapper.getMaxNo();
        if (StrUtil.isNotBlank(maxNo))
            maxNo = "JG" + today + (Integer.parseInt(maxNo.substring(9)) + 1);
        else
            maxNo = "JG" + today + "001";
        return maxNo;
    }

    private void sendMessage(Map<String, String> msgMap, String invalidDate) {
        for (Map.Entry<String, String> entry : msgMap.entrySet()) {
            GaiaSdMessage sdMessage = new GaiaSdMessage();
            sdMessage.setClient(entry.getKey());
            sdMessage.setGsmId("product");
            sdMessage.setGsmType(PriceProposalEnum.PRODUCT_PRICE_PROPOSAL_CODE.getCode());
            sdMessage.setGsmTypeName(PriceProposalEnum.PRODUCT_PRICE_PROPOSAL_PAGE.getName());
            String voucherId = gaiaSdMessageMapper.selectNextVoucherId(entry.getKey(), "company");
            sdMessage.setGsmVoucherId(voucherId);
            sdMessage.setGsmValue(invalidDate);
            sdMessage.setGsmRemark("您有<font color=\"#FF0000\">" + entry.getValue().substring(0, 4) + "年" + Integer.parseInt(entry.getValue().substring(4, 6)) + "月</font>商品调价建议需确认，请于" + Integer.parseInt(entry.getValue().substring(4, 6)) + "月5日前完成确认。");
            sdMessage.setGsmFlag("N");
            sdMessage.setGsmPage(PriceProposalEnum.PRODUCT_PRICE_PROPOSAL_PAGE.getCode());
            sdMessage.setGsmWarningDay("ProductPriceProposal");
            sdMessage.setGsmPlatForm("WEB");
            sdMessage.setGsmDeleteFlag("0");
            Date date = new Date();
            sdMessage.setGsmArriveDate(DateUtils.format(date, "yyyyMMdd"));
            sdMessage.setGsmArriveTime(DateUtils.format(date, "HHmmss"));
            gaiaSdMessageService.addMessage(sdMessage);
        }
    }

}

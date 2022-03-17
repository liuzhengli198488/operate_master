package com.gys.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gys.common.enums.OrderRuleEnum;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.GaiaSdBatchChangeMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.report.common.constant.CommonConstant;
import com.gys.report.entity.InvoicingInData;
import com.gys.report.entity.InvoicingOutData;
import com.gys.report.entity.InvoicingOutDataObj;
import com.gys.report.service.InvoicingService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.ExportStatusUtil;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InvoicingServiceImpl implements InvoicingService {

    @Resource
    private GaiaSdBatchChangeMapper batchChangeMapper;

    @Autowired
    private GaiaStoreDataMapper gaiaStoreDataMapper;


    @Override
    public void invoicingExport(InvoicingInData inData, HttpServletResponse response) {
        ExportStatusUtil.checkExportAuthority(inData.getClientId(),inData.getGssbBrId());
        InvoicingOutDataObj dataObj = this.getInvoicingList(inData);
        List<InvoicingOutData> invoicingOutDataList = dataObj.getResultList();
        if (ObjectUtil.isEmpty(invoicingOutDataList)){
            throw new BusinessException("导出数据为空！");
        }
        String totalDec = dataObj.getTotalDec();
        for (int i = 0; i < invoicingOutDataList.size(); i++) {
            invoicingOutDataList.get(i).setIndex(i+1);
        }
        CsvFileInfo csvInfo = CsvClient.getCsvByte(invoicingOutDataList, "进销存明细", Collections.singletonList((short) 1));
        if(ObjectUtil.isEmpty(csvInfo)){
            throw new BusinessException("导出数据为空！（或超过50万条）");
        }
        byte[] bytes = ("\r\n备注," + totalDec).getBytes(StandardCharsets.UTF_8);
        byte[] all = ArrayUtils.addAll(csvInfo.getFileContent(), bytes);
        CsvClient.writeCSV(response,"进销存明细",all);
    }

    @Override
    public InvoicingOutDataObj getInvoicingList(InvoicingInData inData) {

        String startStr = inData.getGssbStartDate();
        String endStr = inData.getGssbFinishDate();
        //先查询权限   flag  0：开启  1：不开启
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(inData.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        if (ObjectUtil.isEmpty(flag)){
            inData.setFlag("0");
        }else {
            inData.setFlag(flag);
        }
        if(StrUtil.isBlank(startStr) || StrUtil.isBlank(endStr)){
            throw new BusinessException("日期不可为空");
        }else {
            inData.setGssbStartDate(startStr);
            inData.setGssbFinishDate(endStr);
        }
        if (StringUtils.isNotEmpty(inData.getGssbProId())) {
            inData.setProArr(inData.getGssbProId().split("\\s+ |\\s+|,"));

//            System.out.println(inData.getBillNoArr());
        }
        InvoicingOutDataObj invoicingOutDataObj = new InvoicingOutDataObj();
        Map<String, BigDecimal> countMap = Maps.newLinkedHashMap();
        //获取手动导入手工流数据 加盟商下所有编码
        List<String> inventoryList = batchChangeMapper.getClientInventoryList(inData.getClientId());
        inData.setInventoryList(inventoryList);
        List<InvoicingOutData> resultList = batchChangeMapper.getInventoryList(inData);
        //查询门店手工流数据表
        List<InvoicingOutData> resultBrList = batchChangeMapper.getBrIdInventoryList(inData);
        resultList.addAll(resultBrList);
        Map<String, InvoicingOutData> sumMap = Maps.newLinkedHashMap();
        BigDecimal sum = BigDecimal.ZERO;
        List<InvoicingOutData> realResultList = Lists.newArrayList();
        if(CollUtil.isNotEmpty(resultList)) {
            for (InvoicingOutData outData : resultList) {
                outData.setGssbValidUntil(CommonUtil.parseyyyyMMdd(outData.getGssbValidUntil()));
                outData.setGssbDate(CommonUtil.parseyyyyMMdd(outData.getGssbDate()));
                outData.setGssbType(OrderRuleEnum.getOrderRule(outData.getGssbVoucherId()));
                outData.setGssbQty(CommonUtil.stripTrailingZeros(outData.getGssbQty()));
                outData.setPickingDate(CommonUtil.parseyyyyMMdd(outData.getPickingDate()));
                if (StringUtils.isNotEmpty(outData.getBillReturnNo())) {
                    outData.setGssbQty(outData.getGssbQty().negate());
                }

                StringBuffer sameProStr = new StringBuffer();
                sameProStr.append(outData.getGssbVoucherId())
                        .append("#")
                        .append(outData.getGssbProId())
                        .append("#")
                        .append(outData.getGssbBatchNo());

                String type = outData.getGssbType();
                BigDecimal qty = outData.getGssbQty();
                if(sumMap.get(sameProStr.toString()) != null){
                    sum = qty.add(sumMap.get(sameProStr.toString()).getGssbQty());
                    outData.setGssbQty(sum);
                    sumMap.put(sameProStr.toString(), outData);
                }else{
                    sumMap.put(sameProStr.toString(), outData);
                }

                if(countMap.get(type) != null){
                    qty = qty.add(countMap.get(type));
                }
                countMap.put(type, qty);
            }
        }

        StringBuffer totalDec = new StringBuffer();
        for(String key : countMap.keySet()){
            totalDec.append(key).append("数量:").append(countMap.get(key)).append("个 ");
        }

        for(String key : sumMap.keySet()){
            realResultList.add(sumMap.get(key));
        }

        invoicingOutDataObj.setResultList(realResultList);
        invoicingOutDataObj.setTotalDec(totalDec.toString());
        return invoicingOutDataObj;
    }

}

package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryInData;
import com.gys.entity.data.commodityInventoryReport.CommoditySummaryOutData;
import com.gys.mapper.GaiaCommodityInventoryHMapper;
import com.gys.service.CommodityInventoryReportService;
import com.gys.util.CosUtils;
import com.gys.util.DateUtil;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @desc:
 * @author: RULAISZ
 * @createTime: 2021/11/26 15:42
 */
@Service
@Slf4j
public class CommodityInventoryReportServiceImpl implements CommodityInventoryReportService {

    @Resource
    private GaiaCommodityInventoryHMapper gaiaCommodityInventoryHMapper;
    @Resource
    public CosUtils cosUtils;

    @Override
    public List<CommoditySummaryOutData> summaryList(CommoditySummaryInData inData) {
        //计算起始日期与结束日期相差天数
        long billDateDiffDays = DateUtil.getDiffByDate(inData.getStartBillDate(), inData.getEndBillDate());
        if(billDateDiffDays > 366) {
            throw new BusinessException("提示：调库时间范围不能超过一年！");
        }

        if (ObjectUtil.isNotEmpty(inData.getStartFinishDate()) && ObjectUtil.isNotEmpty(inData.getEndFinishDate())){
            //计算起始日期与结束日期相差天数
            long finishDateDiffDays = DateUtil.getDiffByDate(inData.getStartFinishDate(), inData.getEndFinishDate());
            if(finishDateDiffDays > 366) {
                throw new BusinessException("提示：调库时间范围不能超过一年！");
            }
        }

        List<CommoditySummaryOutData> list = gaiaCommodityInventoryHMapper.summaryList(inData);
        for (CommoditySummaryOutData outData : list) {
            outData.setSuggestReturnCost((outData.getSuggestReturnCost() == null? BigDecimal.ZERO:outData.getSuggestReturnCost().divide(new BigDecimal(10000))).setScale(2,BigDecimal.ROUND_HALF_UP));
            outData.setRealReturnCost((outData.getRealReturnCost() == null?BigDecimal.ZERO:outData.getRealReturnCost().divide(new BigDecimal(10000))).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        return list;
    }

    @Override
    public Result summaryExport(CommoditySummaryInData inData) {
        List<CommoditySummaryOutData> dataList = summaryList(inData);
        String fileName = "商品调库汇总表";
        if (dataList.size() > 0) {
            CsvFileInfo csvInfo =null;
            // 导出
            // byte数据
            csvInfo = CsvClient.getCsvByte(dataList,fileName, Collections.singletonList((short)1));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result result = null;
            try {
                bos.write(csvInfo.getFileContent());
                result = cosUtils.uploadFile(bos, csvInfo.getFileName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;

        } else {
            throw new BusinessException("提示：没有查询到数据！");
        }
    }
}

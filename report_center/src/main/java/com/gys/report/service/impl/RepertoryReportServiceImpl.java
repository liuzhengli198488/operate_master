package com.gys.report.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.mapper.RepertoryReportMapper;
import com.gys.report.common.constant.RepertoryConst;
import com.gys.report.entity.*;
import com.gys.report.service.RepertoryReportService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.ExcelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author xiaoyuan on 2020/9/8
 */
@Service
public class RepertoryReportServiceImpl implements RepertoryReportService {

    @Autowired
    private RepertoryReportMapper repertoryReportMapper;

    @Transactional
    @Override
    public RepertoryReportOutData selectRepertoryData(RepertoryReportInData inData) {
        RepertoryReportOutData re = new RepertoryReportOutData();
        if(inData.getPageIndex()<1 || inData.getPageSize()<1){
            inData.setPageIndex(1);
            inData.setPageSize(50);
        }
        inData.setOffsetSize((inData.getPageIndex()-1)* inData.getPageSize());
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }

        if(StringUtils.isNotEmpty(inData.getReportDimension())
                &&StringUtils.isNotEmpty(inData.getReportType())
                &&StringUtils.isNotEmpty(inData.getSecondDimension())){
            List<RepertoryReportData> list = repertoryReportMapper.selectRepertoryData(inData);
            re.setList(list);
            if(CollectionUtils.isNotEmpty(list)){
                RepertoryReportData listSum =new RepertoryReportData();
                 BigDecimal inventoryItem_total = BigDecimal.ZERO;
                 BigDecimal costAmount_total = BigDecimal.ZERO;
                 BigDecimal inventoryItem_dep = BigDecimal.ZERO;
                 BigDecimal costAmount_dep = BigDecimal.ZERO;
                 BigDecimal inventoryItem_store = BigDecimal.ZERO;
                 BigDecimal costAmount_store = BigDecimal.ZERO;
                 BigDecimal inventoryItem_totalExpiry = BigDecimal.ZERO;
                 BigDecimal costAmount_totalExpiry = BigDecimal.ZERO;
                 BigDecimal inventoryItem_depExpiry = BigDecimal.ZERO;
                 BigDecimal costAmount_depExpiry = BigDecimal.ZERO;
                 BigDecimal inventoryItem_storeExpiry = BigDecimal.ZERO;
                 BigDecimal costAmount_storeExpiry = BigDecimal.ZERO;
                BigDecimal costOfRevenues = BigDecimal.ZERO;
                for(RepertoryReportData data : list){
                    data.setProPosition((String) RepertoryConst.map.get(data.getProPosition()));
                    inventoryItem_total = inventoryItem_total.add(Objects.nonNull(data.getInventoryItem_total()) ? data.getInventoryItem_total() : BigDecimal.ZERO);
                    costAmount_total = costAmount_total.add(Objects.nonNull(data.getCostAmount_total()) ? data.getCostAmount_total() : BigDecimal.ZERO);
                    inventoryItem_dep = inventoryItem_dep.add(Objects.nonNull(data.getInventoryItem_dep()) ? data.getInventoryItem_dep() : BigDecimal.ZERO);
                    costAmount_dep = costAmount_dep.add(Objects.nonNull(data.getCostAmount_dep()) ? data.getCostAmount_dep() : BigDecimal.ZERO);
                    inventoryItem_store = inventoryItem_store.add(Objects.nonNull(data.getInventoryItem_store()) ? data.getInventoryItem_store() : BigDecimal.ZERO);
                    costAmount_store = costAmount_store.add(Objects.nonNull(data.getCostAmount_store()) ? data.getCostAmount_store() : BigDecimal.ZERO);
                    inventoryItem_totalExpiry = inventoryItem_totalExpiry.add(Objects.nonNull(data.getInventoryItem_totalExpiry()) ? data.getInventoryItem_totalExpiry() : BigDecimal.ZERO);
                    costAmount_totalExpiry = costAmount_totalExpiry.add(Objects.nonNull(data.getCostAmount_totalExpiry()) ? data.getCostAmount_totalExpiry() : BigDecimal.ZERO);
                    inventoryItem_depExpiry = inventoryItem_depExpiry.add(Objects.nonNull(data.getInventoryItem_depExpiry()) ? data.getInventoryItem_depExpiry() : BigDecimal.ZERO);
                    costAmount_depExpiry = costAmount_depExpiry.add(Objects.nonNull(data.getCostAmount_depExpiry()) ? data.getCostAmount_depExpiry() : BigDecimal.ZERO);
                    inventoryItem_storeExpiry = inventoryItem_storeExpiry.add(Objects.nonNull(data.getInventoryItem_storeExpiry()) ? data.getInventoryItem_storeExpiry() : BigDecimal.ZERO);
                    costAmount_storeExpiry = costAmount_storeExpiry.add(Objects.nonNull(data.getCostAmount_storeExpiry()) ? data.getCostAmount_storeExpiry() : BigDecimal.ZERO);
                    costOfRevenues = costOfRevenues.add(Objects.nonNull(data.getCostOfRevenues()) ? data.getCostOfRevenues() : BigDecimal.ZERO);
                }
                listSum.setInventoryItem_total(inventoryItem_total);
                listSum.setCostAmount_total(costAmount_total);
                listSum.setInventoryItem_dep(inventoryItem_dep);
                listSum.setCostAmount_dep(costAmount_dep);
                listSum.setInventoryItem_store(inventoryItem_store);
                listSum.setCostAmount_store(costAmount_store);
                listSum.setInventoryItem_totalExpiry(inventoryItem_totalExpiry);
                listSum.setCostAmount_totalExpiry(costAmount_totalExpiry);
                listSum.setInventoryItem_depExpiry(inventoryItem_depExpiry);
                listSum.setCostAmount_depExpiry(costAmount_depExpiry);
                listSum.setInventoryItem_storeExpiry(inventoryItem_storeExpiry);
                listSum.setCostAmount_storeExpiry(costAmount_storeExpiry);
                listSum.setCostOfRevenues(costOfRevenues);
                if(costOfRevenues.doubleValue()<=0){
                    listSum.setTurnoverDays_total("不动销");
                    listSum.setTurnoverDays_dep("不动销");
                    listSum.setTurnoverDays_store("不动销");
                    listSum.setTurnoverDays_totalExpiry("不动销");
                    listSum.setTurnoverDays_depExpiry("不动销");
                    listSum.setTurnoverDays_storeExpiry("不动销");

                }else{
                    costOfRevenues = costOfRevenues.divide(new BigDecimal(30),2,BigDecimal.ROUND_HALF_UP);
                    listSum.setTurnoverDays_total(String.valueOf(costAmount_total.divide(costOfRevenues,0,BigDecimal.ROUND_HALF_UP)));
                    listSum.setTurnoverDays_dep(String.valueOf(costAmount_dep.divide(costOfRevenues,0,BigDecimal.ROUND_HALF_UP)));
                    listSum.setTurnoverDays_store(String.valueOf(costAmount_store.divide(costOfRevenues,0,BigDecimal.ROUND_HALF_UP)));
                    listSum.setTurnoverDays_totalExpiry(String.valueOf(costAmount_totalExpiry.divide(costOfRevenues,0,BigDecimal.ROUND_HALF_UP)));
                    listSum.setTurnoverDays_depExpiry(String.valueOf(costAmount_depExpiry.divide(costOfRevenues,0,BigDecimal.ROUND_HALF_UP)));
                    listSum.setTurnoverDays_storeExpiry(String.valueOf(costAmount_storeExpiry.divide(costOfRevenues,0,BigDecimal.ROUND_HALF_UP)));
                }

                re.setListSum(listSum);

                int count = repertoryReportMapper.selectCount(inData);

                re.setPageIndex(inData.getPageIndex());
                re.setPageSize(inData.getPageSize());

                int a = count/inData.getPageSize();
                int b = count-a*inData.getPageSize()>0?1:0;
                re.setPageCount(a+b);
                re.setRowCount(count);

            }
        }
      return re ;
    }
    @Autowired
    CosUtils cosUtils;
    @Override
    public Result exportRepertoryData(RepertoryReportInData inData) {
        inData.setPageIndex(1);
        inData.setPageSize(39999);

        RepertoryReportOutData re = selectRepertoryData(inData);

        String key = inData.getReportDimension()+"_"+inData.getReportType()+"_"+inData.getSecondDimension()+"_"+inData.getIsCheckedPeriod();
        String[] cols = (String[]) RepertoryConst.map.get(key);
        String[] colsName = new String[cols.length];
        for (int i =0;i< cols.length;i++){
            colsName[i] = (String) RepertoryConst.map.get(cols[i]);
        }
        List<RepertoryReportData> list = re.getList();
        list.add(re.getListSum());
        //转成导出格式
        List<List<Object>> res =  convertList(list,cols,true);
        //写入文件
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HSSFWorkbook workbook = ExcelUtils.exportExcel2(
                new ArrayList<String[]>() {{
                    add(colsName);
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(res);
                }},
                new ArrayList<String>() {{
                    add("报表数据");
                }});
        HSSFSheet sheet= workbook.getSheetAt(0);
        sheet.shiftRows(0,sheet.getLastRowNum(),1);
        sheet.createRow(0);
        HSSFRow secondRow = sheet.getRow(1);
        HSSFRow row = sheet.getRow(0);

        Map map = new HashMap();
        map.put(0,"总库");
        map.put(1,"仓库");
        map.put(2,"门店");
        map.put(3,"总库(效期"+inData.getValidityPeriod()+")");
        map.put(4,"仓库(效期"+inData.getValidityPeriod()+")");
        map.put(5,"门店(效期"+inData.getValidityPeriod()+")");
        int cellcount = sheet.getRow(1).getLastCellNum();
        for(int i=0;i<cellcount;i++){
            row.createCell(i);
        }
        int cellIndex = 0;
        if(inData.getIsCheckedPeriod()>0){
            cellIndex=18;
        }else{
            cellIndex=9;
        }
            for(int i =0 ;i<cellIndex/3;i++){
                CellRangeAddress region = new CellRangeAddress(0, 0, cellcount-cellIndex+i*3, (cellcount-cellIndex-1)+(i+1)*3);
                sheet.addMergedRegion(region);
                HSSFCell cell = row.getCell(cellcount-cellIndex+i*3);
                cell.setCellValue((String)map.get(i));
            }
            for(int i =0 ;i<cellcount-cellIndex;i++){
                String value = secondRow.getCell(i).getStringCellValue();
                CellRangeAddress region = new CellRangeAddress(0, 1, i, i);
                sheet.addMergedRegion(region);
                HSSFCell cell = row.getCell(i);
                cell.setCellValue(value);
            }
        Result uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = "报表数据" + "-" + CommonUtil.getyyyyMMdd()+ CommonUtil.getHHmmss() + ".xls";
            uploadResult = cosUtils.uploadFile(bos, fileName);
            bos.flush();
        } catch (IOException e) {
            throw new BusinessException("导出文件失败！");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                throw new BusinessException("关闭流异常！");
            }
        }
        return uploadResult;
    }

    private List<List<Object>> convertList(List list, String[] cols,boolean isSum) {
        List sumList = new ArrayList();
        for(int i=0 ;i< list.size();i++){
            List rowList = new ArrayList();
            Class<?> clazz = list.get(i).getClass();
            for(String colName: cols){
                if("reportOrder".equals(colName)){
                    if(i == list.size()-1&&isSum){
                        rowList.add("合计：");
                    }else{
                        rowList.add(i+1+"");
                    }
                    continue;
                }
                String methodName = "get"+ colName.substring(0,1).toUpperCase()+colName.substring(1);
                Method method = null;
                try {
                    method = clazz.getDeclaredMethod(methodName);
                    Object value = method.invoke(list.get(i));
                    if(value==null){
                        rowList.add("");
                    }else if(value instanceof  BigDecimal){
                        rowList.add(((BigDecimal) value).setScale(4,BigDecimal.ROUND_HALF_UP));
                    }else{
                        rowList.add(value);
                    }
                } catch (Exception e) {
                    throw new BusinessException("转换Excel数据时异常！");
                }
            }
            sumList.add(rowList);
        }
        return sumList;
    }
}

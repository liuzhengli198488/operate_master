package com.gys.service.Impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.data.*;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.GaiaSdPayDayreportD;
import com.gys.entity.GaiaSdPayDayreportH;
import com.gys.entity.InData;
import com.gys.entity.data.InventoryInquiry.InventoryStore;
import com.gys.entity.data.payDayReport.PayDayReportDetailInData;
import com.gys.entity.data.payDayReport.PayDayReportDetailOutData;
import com.gys.entity.data.payDayReport.PayDayReportInData;
import com.gys.mapper.*;
import com.gys.report.entity.GaiaStoreCategoryType;
import com.gys.service.SaleReportService;
import com.gys.util.BigDecimalUtil;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaleReportServiceImpl implements SaleReportService {
    @Resource
    private SaleReportMapper saleReportMapper;
    @Resource
    private AppReportMapper appReportMapper;
    @Autowired
    private SaleReportService saleReportService;
    @Resource
    private GaiaStoreDataMapper gaiaStoreDataMapper;
    @Resource
    CosUtils cosUtils;
    @Resource
    private GaiaSdPayDayreportDMapper payDayreportDMapper;
    @Resource
    private GaiaSdPayDayreportHMapper payDayreportHMapper;

    @Override
    public NoSaleOutData selectNosaleReportList(SaleReportInData inData) {
        NoSaleOutData result = new NoSaleOutData();
        List<NoSaleItemOutData> itemList = saleReportMapper.selectNosaleReportList(inData);
        NoSaleTotalOutData totalOutData = new NoSaleTotalOutData();
        totalOutData.setGssdQty(BigDecimal.ZERO);
        totalOutData.setCostAmt(BigDecimal.ZERO);
        totalOutData.setStockQty(BigDecimal.ZERO);
        for (NoSaleItemOutData item : itemList) {
            if (ObjectUtil.isEmpty(item.getGssdQty())){
                item.setGssdQty(BigDecimal.ZERO);
            }
            totalOutData.setGssdQty(totalOutData.getGssdQty().add(item.getGssdQty()));
            if (ObjectUtil.isEmpty(item.getCostAmt())){
                item.setCostAmt(BigDecimal.ZERO);
            }
            totalOutData.setCostAmt(totalOutData.getCostAmt().add(item.getCostAmt()));
            if (ObjectUtil.isEmpty(item.getStockQty())){
                item.setStockQty(BigDecimal.ZERO);
            }
            totalOutData.setStockQty(totalOutData.getStockQty().add(item.getStockQty()));
        }
        if (itemList.size() > 0) {
            //????????????????????????0?????????
            List<NoSaleItemOutData> items = itemList.stream()
                    .filter(detail -> detail.getCostAmt().compareTo(BigDecimal.ZERO) != 0
                            || detail.getGssdQty().compareTo(BigDecimal.ZERO) != 0
                            || detail.getStockQty().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
            result.setItemOutData(items);
            result.setTotalOutData(totalOutData);
        }
        return result;
    }

    @Override
    public List<MedicalInsuranceOutData> selectMedicalInsuranceList(MedicalInsuranceInData inData) {
        if(ObjectUtil.isEmpty(inData.getStoCode())){
            throw new BusinessException("???????????????");
        }
        if (StringUtils.isEmpty(inData.getStartDate())){
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isEmpty(inData.getEndDate())){
            throw new BusinessException("?????????????????????");
        }

        String nowStr = CommonUtil.getyyyyMMdd();
        inData.setStartDate(inData.getStartDate() + "01");
        if(nowStr.equals(inData.getStartDate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            inData.setStartDate(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 0);
            inData.setEndDate(sdf.format(cal.getTime()));
        }else{
            if(nowStr.substring(0,6).equals(inData.getEndDate())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, 0);
                inData.setEndDate(sdf.format(cal.getTime()));
            }else{
                Map<String, String> startAndEndDate = appReportMapper.selectDateTime(Integer.valueOf(inData.getEndDate().substring(0,4)),Integer.valueOf(inData.getEndDate().substring(4)),null);
                inData.setEndDate(CommonUtil.parseWebDate(startAndEndDate.get("maxDate")));
            }
        }
        List<MaterialTypeQtyOutData> stockQtyList = saleReportMapper.selectStockQty(inData);//????????????????????????
        List<MedicalInsuranceOutData> medicalInsuranceOutDataList = saleReportMapper.selectMedicalInsuranceList(inData);
        String[] inputType = {"CD"};//??????
        inData.setMatType(inputType);
        List<MaterialTypeQtyOutData> inputOutDataList = saleReportMapper.selectMaterialQtyByType(inData);
        String[] callInType = {"PD","ND"};//??????
        inData.setMatType(callInType);
        List<MaterialTypeQtyOutData> callInOutDataList = saleReportMapper.selectMaterialQtyByType(inData);
        String[] callOutType = {"TD","MD"};//??????
        inData.setMatType(callOutType);
        List<MaterialTypeQtyOutData> callOutDataList = saleReportMapper.selectMaterialQtyByType(inData);
        String[] returnGoodsType = {"GD"};//??????
        inData.setMatType(returnGoodsType);
        List<MaterialTypeQtyOutData> returnGoodsOutDataList = saleReportMapper.selectMaterialQtyByType(inData);
        String[] profitLossType = {"BD","ZD","DD"};//??????
        inData.setMatType(profitLossType);
        List<MaterialTypeQtyOutData> profitLossOutDataList = saleReportMapper.selectMaterialQtyByType(inData);
        for (MedicalInsuranceOutData outData : medicalInsuranceOutDataList) {
            outData.setInventoryDate(inData.getEndDate()+"235959");
            String proCode = outData.getProCode();
            outData.setStockQty(BigDecimal.ZERO);
            for (MaterialTypeQtyOutData stockOutData : stockQtyList) {
                if (stockOutData.getProCode().equals(proCode)){
                    outData.setStockQty(stockOutData.getTotalQty());
                }
            }
            outData.setInputQty(BigDecimal.ZERO);
            for (MaterialTypeQtyOutData inputOutData : inputOutDataList) {
                if (inputOutData.getProCode().equals(proCode)){
                    outData.setInputQty(inputOutData.getTotalQty());
                }
            }
            outData.setCallInQty(BigDecimal.ZERO);
            for (MaterialTypeQtyOutData callInOutData : callInOutDataList) {
                if (callInOutData.getProCode().equals(proCode)){
                    outData.setCallInQty(callInOutData.getTotalQty());
                }
            }
            outData.setCallOutQty(BigDecimal.ZERO);
            for (MaterialTypeQtyOutData callOutData : callOutDataList) {
                if (callOutData.getProCode().equals(proCode)){
                    outData.setCallOutQty(callOutData.getTotalQty());
                }
            }
            outData.setReturnGoodsQty(BigDecimal.ZERO);
            for (MaterialTypeQtyOutData returnGoodsOutData : returnGoodsOutDataList) {
                if (returnGoodsOutData.getProCode().equals(proCode)){
                    outData.setReturnGoodsQty(returnGoodsOutData.getTotalQty());
                }
            }
            for (MaterialTypeQtyOutData profitLossOutData : profitLossOutDataList) {
                if (profitLossOutData.getProCode().equals(proCode)){
                    if (ObjectUtil.isEmpty(outData.getSalePayQty())){
                        outData.setSalePayQty(BigDecimal.ZERO);
                    }
                    outData.setSalePayQty(profitLossOutData.getTotalQty().add(outData.getSalePayQty()));
                }
            }
            if (ObjectUtil.isEmpty(outData.getSalePayQty())){
                outData.setSalePayQty(BigDecimal.ZERO);
            }
            if (ObjectUtil.isEmpty(outData.getCardQty())){
                outData.setCardQty(BigDecimal.ZERO);
            }
        }
        //????????????????????????0?????????
        List<MedicalInsuranceOutData> items = medicalInsuranceOutDataList.stream()
                .filter(detail -> detail.getCallInQty().compareTo(BigDecimal.ZERO) != 0
                        || detail.getCallOutQty().compareTo(BigDecimal.ZERO) != 0
                        || detail.getCardQty().compareTo(BigDecimal.ZERO) != 0
                        || detail.getSalePayQty().compareTo(BigDecimal.ZERO) != 0
                        || detail.getStockQty().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
        //??????????????????0?????????
//        List<MedicalInsuranceOutData> result =  items.stream()
//                .filter(detail -> detail.getStockQty().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
        return items;
    }

    private String[] headList = {
            "????????????",
            "????????????",
            "????????????",
            "?????????",
            "????????????",
            "????????????",
            "????????????",
            "?????????",
            "????????????",
            "??????????????????????????????",
            "??????????????????????????????",
            "????????????????????????????????????",
            "????????????????????????????????????",
            "??????????????????????????????",
            "??????????????????????????????",
            "????????????????????????",
            "????????????"
    };

    @Override
    public Result exportMedicalInsurance(MedicalInsuranceInData inData, String fileName) {
        List<MedicalInsuranceOutData> outDataList = saleReportService.selectMedicalInsuranceList(inData);
        try {
            List<List<Object>> dataList = new ArrayList<>();
            for (MedicalInsuranceOutData data : outDataList) {
                // ?????????
                List<Object> item = new ArrayList<>();
                dataList.add(item);
                // ????????????
                item.add(data.getInventoryDate());
                // ????????????
                item.add(data.getProCode());
                // ????????????
                item.add(data.getProName());
                // ?????????
                item.add(data.getProCommonName());
                //????????????
                item.add(data.getProDepict());
                // ??????
                item.add(data.getFactoryName());
                // ??????
                item.add(data.getProSpecs());
                // ?????????
                item.add(data.getChangeRatio());
                // ????????????
                item.add(data.getMaterial());
                // ??????????????????????????????
                item.add(data.getInputQty());
                // ??????????????????????????????
                item.add(data.getCallInQty());
                // ????????????????????????????????????
                item.add(data.getCardQty());
                // ????????????????????????????????????
                item.add(data.getSalePayQty());
                // ??????????????????????????????
                item.add(data.getCallOutQty());
                // ??????????????????????????????
                item.add(data.getReturnGoodsQty());
                // ????????????????????????
                item.add(data.getStockQty());
                // ????????????
                item.add(data.getProMedId());
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HSSFWorkbook workbook = ExcelUtils.exportExcel2(
                    new ArrayList<String[]>() {{
                        add(headList);
                    }},
                    new ArrayList<List<List<Object>>>() {{
                        add(dataList);
                    }},
                    new ArrayList<String>() {{
                        add("?????????????????????");
                    }});
            workbook.write(bos);
            Result result = cosUtils.uploadFile(bos, fileName);
            bos.flush();
            bos.close();
            return result;
        } catch (IOException e) {
            throw new BusinessException("?????????????????????");
        }
    }

//    private static String[][] convertToArr(List<MedicalInsuranceOutData> storageInfoList){
//        String[][] content =new String[storageInfoList.size()][11];
//        for(int i =0; i < storageInfoList.size(); i++){
//            MedicalInsuranceOutData info = storageInfoList.get(i);
//            //??????bean????????????16???
//            content[i][0]= info.getInventoryDate();
//            content[i][1]= info.getProCode();
//            content[i][2]= info.getProName();
//            content[i][3]= info.getProCommonName();
//            content[i][4]= info.getFactoryName();
//            content[i][5]= info.getProSpecs();
//            content[i][6]= info.getChangeRatio();
//            content[i][7]= info.getMaterial();
//            content[i][8]= info.getInputQty().toString();
//            content[i][9]= info.getCallInQty().toString();
//            content[i][10]= info.getCardQty().toString();
//            content[i][11]= info.getSalePayQty().toString();
//            content[i][12]= info.getCallOutQty().toString();
//            content[i][13]= info.getReturnGoodsQty().toString();
//            content[i][14]= info.getStockQty().toString();
//            content[i][15]= info.getProMedId();
//        }
//        return content;
//
//    }
//
//    private static List<String> getTitleList(){
//        List<String> list = new ArrayList<String>();
//        list.add("????????????");
//        list.add("????????????");
//        list.add("????????????");
//        list.add("?????????");
//        list.add("????????????");
//        list.add("????????????");
//        list.add("?????????");
//        list.add("????????????");
//        list.add("??????????????????????????????");
//        list.add("??????????????????????????????");
//        list.add("????????????????????????????????????");
//        list.add("????????????????????????????????????");
//        list.add("??????????????????????????????");
//        list.add("??????????????????????????????");
//        list.add("????????????????????????");
//        list.add("????????????");
//        return list;
//    }

    @Override
    public PageInfo selectNosaleReportListNew(SaleReportInData inData) {

//        if ((ObjectUtil.isNotEmpty(inData.getStoArr()) || inData.getStoArr().length>0)  && ObjectUtil.isNotEmpty(inData.getDcCode())){
//            throw new BusinessException("??????????????????????????????????????????");
//        }
        String type = null;

        //?????????
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

        if(ObjectUtil.isNotEmpty(inData.getClassArr())){
          inData.setClassArrs(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        List<Map<String,Object>> itemList = new ArrayList<>();
        List<InventoryStore> inventoryStore = null;
        //?????????????????????????????? ????????????????????????:????????????????????????
        if (ObjectUtil.isEmpty(inData.getStoArr()) && ObjectUtil.isNotEmpty(inData.getDcCode())) {
            type = "1";
            inventoryStore = this.gaiaStoreDataMapper.getInventoryStore(new InData(inData.getClientId(),inData.getStoArr(),type));
            if(ObjectUtil.isNotEmpty(inventoryStore)){
                inData.setInventoryStore(inventoryStore);
//                inData.setDcCode(inData.getDcCode());
                itemList = saleReportMapper.selectNosaleReportListByDc(inData);
            }
        }else {
            List<String> arr = new ArrayList<>();
            InData inData1 = new InData();

            if (ObjectUtil.isNotEmpty(inData.getStoArr()) && ObjectUtil.isEmpty(inData.getDcCode())) {
                inData1.setStoArr(inData.getStoArr());
                type = "2";
            } else if (ObjectUtil.isNotEmpty(inData.getStoArr()) && ObjectUtil.isNotEmpty(inData.getDcCode())) {
                inData1.setStoArr(inData.getStoArr());
                inData1.setDcCode(inData.getDcCode());
            }
            inData1.setGssgId(inData.getGssgId());
            inData1.setGssgIds(inData.getGssgIds());
            inData1.setStoAttribute(inData.getStoAttribute());
            inData1.setStoAttributes(inData.getStoAttributes());
            inData1.setStoGssgType(inData.getStoGssgType());
            inData1.setStoGssgTypes(inData.getStoGssgTypes());
            inData1.setStoIfDtp(inData.getStoIfDtp());
            inData1.setStoIfDtps(inData.getStoIfDtps());
            inData1.setStoIfMedical(inData.getStoIfMedical());
            inData1.setStoIfMedicals(inData.getStoIfMedicals());
            inData1.setClient(inData.getClientId());
            inData1.setType(type);
            inventoryStore = this.gaiaStoreDataMapper.getInventoryStore(inData1);
            if(ObjectUtil.isNotEmpty(inventoryStore)){
                inData.setInventoryStore(inventoryStore);
                itemList = saleReportMapper.selectNosaleReportListByStore(inData);
            }
        }


        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(itemList)) {
//     // ????????????????????????
//            //???????????????????????????
            Map<String,BigDecimal> mapSum = new HashMap<>();
            mapSum.put("qtySum",BigDecimal.ZERO);
            mapSum.put("costAmt",BigDecimal.ZERO);
            mapSum.put("gssdQty",BigDecimal.ZERO);

            for (InventoryStore paytppe : inventoryStore){
                mapSum.put(paytppe.getColumnCode(),BigDecimal.ZERO);
            }
            for(Map<String,Object> outMap:itemList){
                //??????????????????
                mapSum = BigDecimalUtil.addWeightMap(mapSum,outMap);
            }

//
            pageInfo = new PageInfo(itemList,mapSum);

        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<Map<String, Object>> selectNosaleReportListNewExcel(SaleReportInData inData) {
        if ((ObjectUtil.isNotEmpty(inData.getStoArr()) || inData.getStoArr().length>0)  && ObjectUtil.isNotEmpty(inData.getDcCode())){
            throw new BusinessException("??????????????????????????????????????????");
        }
        if (StringUtils.isNotEmpty(inData.getProCode())) {
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        List<Map<String,Object>> itemList = null;
        List<InventoryStore> inventoryStore = null;
        //?????????????????????????????? ????????????????????????
        if(ObjectUtil.isNotEmpty(inData.getDcCode())){
            inventoryStore = this.gaiaStoreDataMapper.getInventoryStore(new InData(inData.getClientId(),inData.getStoArr(),"1"));
            if(ObjectUtil.isNotEmpty(inventoryStore)){
                inData.setInventoryStore(inventoryStore);
                itemList = saleReportMapper.selectNosaleReportListByDc(inData);
            }

        }else {
            inventoryStore = this.gaiaStoreDataMapper.getInventoryStore(new InData(inData.getClientId(),inData.getStoArr(),"2"));
            if(ObjectUtil.isNotEmpty(inventoryStore)){
                inData.setInventoryStore(inventoryStore);
                itemList = saleReportMapper.selectNosaleReportListByStore(inData);
            }
        }
    return itemList;
    }

    @Override
    public List<GaiaSdPayDayreportH> selectPayDayReport(PayDayReportInData inData) {
        if (StringUtils.isEmpty(inData.getStartDate()) || StringUtils.isEmpty(inData.getEndDate())){
            throw new BusinessException("??????????????????????????????????????????");
        }
        return payDayreportHMapper.selectPayDayReport(inData);
    }

    @Override
    public List<PayDayReportDetailOutData> selectPayDayReportDetail(PayDayReportDetailInData inData) {
        if (StringUtils.isEmpty(inData.getVoucherId())){
            throw new BusinessException("???????????????????????????");
        }
        return payDayreportDMapper.selectPayDayReportDetail(inData);
    }
}

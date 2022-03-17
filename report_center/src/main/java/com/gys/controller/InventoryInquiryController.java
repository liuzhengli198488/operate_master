package com.gys.controller;

import cn.hutool.core.util.StrUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.InData;
import com.gys.entity.data.InventoryInquiry.*;
import com.gys.report.entity.GaiaStoreCategoryType;
import com.gys.report.entity.InventoryInquiryByClientAndBatchNoOutData;
import com.gys.report.entity.InventoryInquiryByClientAndSiteOutData;
import com.gys.service.InventoryInquiryService;
import com.gys.service.StoreDataService;
import com.gys.util.CosUtils;
import com.gys.util.ExcelUtils;
import com.gys.util.UtilMessage;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping({"/inventoryInquiry"})
@Slf4j
@Api(tags = "库存管理")
public class InventoryInquiryController extends BaseController {
    @Resource
    private InventoryInquiryService inventoryInquiryService;
    @Resource
    private StoreDataService storeDataService;
    @Resource
    public CosUtils cosUtils;

    @ApiOperation(value = "获取库存门店")
    @PostMapping({"/getInventoryStore"})
    public JsonResult getInventoryStore(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(storeDataService.getInventoryStore(inData), "提示：获取数据成功！");
    }

    @Log("加盟商下库存商品查询")
    @ApiOperation(value = "加盟商下库存商品查询")
    @PostMapping({"/inventoryInquiryByRow"})
    public JsonResult inventoryInquiryListByClient(HttpServletRequest request, @RequestBody InventoryInquiryInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        PageInfo pageInfo = inventoryInquiryService.inventoryInquiryByRow(inData);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @Log("加盟商下库存商品导出")
    @ApiOperation(value = "加盟商下库存商品导出")
    @PostMapping({"/inventoryInquiryByRowExecl"})
    public Result inventoryInquiryByRowExecl(HttpServletRequest request, HttpServletResponse response,
                                             @RequestBody InventoryInquiryInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        //匹配值
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
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
        inData.setClientId(userInfo.getClient());
        List<LinkedHashMap<String, Object>> outData = inventoryInquiryService.inventoryInquiryByRowExecl(inData);

        if (outData.size() > 0) {
            InData inData1 = new InData(inData.getClientId());
            BeanUtils.copyProperties(inData, inData1);
            //动态查询的门店头
            List<InventoryStore> inventoryStores = storeDataService.getInventoryStore(inData1);

//            //表格的头部标签，与你查出来的数据对应即可
            ArrayList<String> headers = new ArrayList<>(Arrays.asList("商品编码", "国际条形码", "商品名称", "商品通用名称", "生产厂家", "产地", "剂型", "单位", "规格", "单价", "最新进价", "库存汇总"));
            ArrayList<String> columnNames = new ArrayList<>(Arrays.asList("gssmProId", "proBarcode", "gssmProName", "proCommonName", "factory", "origin", "dosageForm", "unit", "format", "priceNormal", "poPrice", "qtySum"));
//            //动态的查询门店
            inventoryStores.forEach((item) -> {
                headers.add(item.getStoName());
                columnNames.add(item.getColumnCode());
            });
            headers.addAll(Arrays.asList("批准文号", "销售等级", "商品大类编码", "商品中类编码", "商品分类编码", "是否医保", "医保类型", "医保编码", "自定义1", "自定义2"));
            columnNames.addAll(Arrays.asList("approvalNum", "saleClass", "bigClass", "midClass", "proClass", "ifMed", "yblx", "medProdctCode", "zdy1", "zdy2"));

            return ExcelUtils.exportExcel("地点库存表", "地点库存表", headers, columnNames, outData, "yyyy-MM-dd HH:mm:ss", response);
        } else {
            return null;
        }


    }

    @Log("加盟商下库存商品导出")
    @ApiOperation(value = "加盟商下库存商品导出")
    @PostMapping({"/inventoryInquiryByRowCSV"})
    public Result InBoundCountForDayExportring(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody InventoryInquiryInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        //匹配值
        if (inData.getStoGssgType() != null) {
            List<GaiaStoreCategoryType> stoGssgTypes = new ArrayList<>();
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypes.add(gaiaStoreCategoryType);
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
        List<LinkedHashMap<String, Object>> outData = inventoryInquiryService.inventoryInquiryByRowExecl(inData);
        if (!ObjectUtils.isEmpty(outData)) {
            InData inData1 = new InData(inData.getClientId());
            BeanUtils.copyProperties(inData, inData1);
            //动态查询的门店头
            List<InventoryStore> inventoryStores = storeDataService.getInventoryStore(inData1);
            String fileName = "地点实时库存(新)导出";
            Map<String, String> titleMap = new LinkedHashMap<>(2);
            titleMap.put("gssmProId", "商品编码");
            titleMap.put("proBarcode", "国际条形码");
            titleMap.put("gssmProName", "商品名称");
            titleMap.put("proCommonName", "商品通用名称");
            titleMap.put("factory", "生产厂家");
            titleMap.put("origin", "产地");
            titleMap.put("dosageForm", "剂型");
            titleMap.put("unit", "单位");
            titleMap.put("format", "规格");
            titleMap.put("priceNormal", "单价");
            titleMap.put("poPrice", "最新进价");
            titleMap.put("qtySum", "库存汇总");
            //动态的查询门店
            inventoryStores.forEach((item) -> {
                titleMap.put(item.getColumnCode(), item.getStoName());
            });
            titleMap.put("approvalNum", "批准文号");
            titleMap.put("saleClass", "销售等级");
            titleMap.put("bigClass", "商品大类编码");
            titleMap.put("midClass", "商品中类编码");
            titleMap.put("proClass", "商品分类编码");
            titleMap.put("ifMed", "是否医保");
            titleMap.put("yblx", "医保类型");
            titleMap.put("medProdctCode", "医保编码");
            titleMap.put("zdy1", "自定义1");
            titleMap.put("zdy2", "自定义2");

            //查统计
            PageInfo totalqry = inventoryInquiryService.inventoryInquiryByRow(inData);
            Map<String,Object> totalNum =new HashMap();
            if (totalqry.getListNum() != null && totalqry.getListNum() instanceof Map) {
                totalNum = (Map<String, Object>) totalqry.getListNum();
            }
            //塞进合计map
            if (totalNum != null && totalNum.size() != 0) {
                Map<String,Object> totalNumNew=totalNum;
                LinkedHashMap totalMap = new LinkedHashMap();
                totalMap.put("gssmProId", "合计");
                totalMap.put("proBarcode", "");
                totalMap.put("gssmProName", "");
                totalMap.put("proCommonName", "");
                totalMap.put("factory", "");
                totalMap.put("origin", "");
                totalMap.put("dosageForm", "");
                totalMap.put("unit", "");
                totalMap.put("format", "");
                totalMap.put("priceNormal", "");
                totalMap.put("poPrice", "");
                totalMap.put("qtySum", totalNum.get("qtySum"));
                //动态的查询门店
                inventoryStores.forEach((item) -> {
                    for (String key : totalNumNew.keySet()) {
                        if (key.equals(item.getColumnCode())) {
                            totalMap.put(item.getColumnCode(), totalNumNew.get(key));
                        }
                    }
                });
                totalMap.put("approvalNum", "");
                totalMap.put("saleClass", "");
                totalMap.put("bigClass", "");
                totalMap.put("midClass", "");
                totalMap.put("proClass", "");
                totalMap.put("ifMed", "");
                totalMap.put("yblx", "");
                totalMap.put("medProdctCode", "");
                totalMap.put("zdy1", "");
                totalMap.put("zdy2", "");
                outData.add(totalMap);
            }
            // 导出
            // byte数据
            CsvFileInfo csvInfo = CsvClient.getCsvByteByMap(outData, fileName, titleMap);
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
                bos.flush();
                bos.close();
            }
            return result;

        } else {
            return Result.error("提示：没有查询到数据,请修改查询条件!");
        }
    }

    @ApiOperation(value = "库存商品查询", response = EndingInventoryOutData.class)
    @PostMapping({"/selectEndingInventory"})
    public JsonResult selectEndingInventory(HttpServletRequest request, @Valid @RequestBody EndingInventoryInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(inventoryInquiryService.selectEndingInventory(inData), "提示：获取数据成功！");
    }

    @Log("期末库存商品导出")
    @ApiOperation(value = "期末库存商品导出")
    @PostMapping({"/selectEndingInventoryCSV"})
    public Result selectEndingInventoryCSV(HttpServletRequest request, @RequestBody EndingInventoryInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());

        List<EndingInventoryOutData> outData = inventoryInquiryService.selectEndingInventoryCSV(inData);
        String fileName = "期末库存商品导出";

        if (!ObjectUtils.isEmpty(outData)) {
            CsvFileInfo csvInfo = null;
            // 导出
            // byte数据
            if (UtilMessage.ZERO.equals(inData.getType())) {
                csvInfo = CsvClient.getCsvByte(outData, fileName, null);
            } else if (UtilMessage.ONE.equals(inData.getType())) {
                csvInfo = CsvClient.getCsvByte(outData, fileName, Collections.singletonList((short) 1));
            } else {
                return Result.error("导出失败");
            }

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
                bos.flush();
                bos.close();
            }
            return result;

        } else {
            throw new BusinessException("提示：没有查询到数据,请修改查询条件!");
        }
    }

    @ApiOperation(value = "效期商品汇总查询", response = EffectiveGoodsOutData.class)
    @PostMapping({"/selectEffectiveGoods"})
    public JsonResult selectEffectiveGoods(HttpServletRequest request, @Valid @RequestBody EffectiveGoodsInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(inventoryInquiryService.selectEffectiveGoods(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "库存商品查询", response = InventoryInquiryByClientAndSiteOutData.class)
    @PostMapping({"/inventoryInquiryListByStoreAndDc"})
    public JsonResult inventoryInquiryListByStore(HttpServletRequest request, @Valid @RequestBody InventoryInquiryInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(inventoryInquiryService.inventoryInquiryListByStoreAndDc(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "地点实时库存导出", response = InventoryInquiryByClientAndSiteOutData.class)
    @PostMapping({"/inventoryQryByStoreAndDcExport"})
    public void inventoryInquiryListByStoreExport(HttpServletRequest request,HttpServletResponse response,@Valid @RequestBody InventoryInquiryInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        try {
            inventoryInquiryService.inventoryInquiryListByStoreExport(inData,response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "加盟商下库存商品查询", response = InventoryInquiryByClientAndBatchNoOutData.class)
    @PostMapping({"/inventoryInquiryListByClient"})
    public JsonResult inventoryInquiryListByClient1(HttpServletRequest request, @Valid @RequestBody InventoryInquiryInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        return JsonResult.success(inventoryInquiryService.inventoryInquiryListByClient(inData), "提示：获取数据成功！");
    }

    @PostMapping({"/querySalesInfo"})
    public Result querySalesInfo(HttpServletRequest request) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        InventoryInquiryInData inData = new InventoryInquiryInData();
        inData.setClientId(userInfo.getClient());
        return inventoryInquiryService.querySalesInfo(inData);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.mapper.GaiaSdReplenishComparedDMapper;
import com.gys.report.common.constant.CommonConstant;
import com.gys.report.entity.GetReplenishInData;
import com.gys.report.service.ReplenishWebService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.ExcelUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplenishWebServiceImpl implements ReplenishWebService {
    private static final Logger log = LoggerFactory.getLogger(ReplenishWebServiceImpl.class);
    @Autowired
    private GaiaSdReplenishComparedDMapper gaiaSdReplenishComparedDMapper;
    @Autowired
    private CosUtils cosUtils;

    public ReplenishWebServiceImpl() {
    }

    public HashMap<String, Object> listDifferentReplenish(GetReplenishInData inData, GetLoginOutData userInfo) {
        HashMap<String, Object> result = new HashMap();
        List<HashMap<String, Object>> differentReplenish = this.gaiaSdReplenishComparedDMapper.listDifferentReplenish(inData);
        HashMap<String, BigDecimal> statistics = new HashMap();
        BigDecimal addTotal = BigDecimal.ZERO;
        BigDecimal deleteTotal = BigDecimal.ZERO;
        BigDecimal modifyTotal = BigDecimal.ZERO;
        Set<String> stoSet = new HashSet();
        BigDecimal orginalItemsTotal = BigDecimal.ZERO;
        Set<String> createDateSet = new HashSet();
        Iterator var12 = differentReplenish.iterator();

        while(var12.hasNext()) {
            HashMap different = (HashMap)var12.next();
            BigDecimal add = new BigDecimal(different.get("add").toString());
            BigDecimal delete = new BigDecimal(different.get("delete").toString());
            BigDecimal modify = new BigDecimal(different.get("modify").toString());
            BigDecimal orginalItems = new BigDecimal(different.get("orginalItems").toString());
            addTotal = addTotal.add(add);
            deleteTotal = deleteTotal.add(delete);
            modifyTotal = modifyTotal.add(modify);
            orginalItemsTotal = orginalItemsTotal.add(orginalItems);
            stoSet.add((String)different.get("stoCode"));
            createDateSet.add((String)different.get("checkDate"));
        }

        statistics.put("add", addTotal);
        statistics.put("delete", deleteTotal);
        statistics.put("modify", modifyTotal);
        statistics.put("orginalItems", orginalItemsTotal);
        statistics.put("stoCode", BigDecimal.valueOf((long)stoSet.size()));
        statistics.put("creDate", BigDecimal.valueOf((long)createDateSet.size()));
        statistics.put("voucherId", BigDecimal.valueOf((long)differentReplenish.size()));
        result.put("DifferentReplenishData", differentReplenish);
        result.put("statistics", statistics);
        return result;
    }

    public HashMap<String, Object> listDifferentReplenishDetail(GetReplenishInData inData, GetLoginOutData userInfo) {
        HashMap<String, Object> res = this.gaiaSdReplenishComparedDMapper.getDifferentReplenish(inData);
        List<HashMap<String, Object>> hashMaps = this.gaiaSdReplenishComparedDMapper.listDifferentReplenishDetail(inData);
        Set<String> proIdSet = new HashSet();
        BigDecimal proposeQtyTotal = BigDecimal.ZERO;
        BigDecimal needQtyTotal = BigDecimal.ZERO;
        BigDecimal diffQtyTotal = BigDecimal.ZERO;
        BigDecimal storeQtyTotal = BigDecimal.ZERO;
        Iterator var10 = hashMaps.iterator();

        while(var10.hasNext()) {
            HashMap<String, Object> hashMap = (HashMap)var10.next();
            if ((new BigDecimal(hashMap.get("needQty").toString())).compareTo(BigDecimal.ZERO) == 0) {
                hashMap.put("status", "删除");
            }

            BigDecimal diffQty = new BigDecimal(hashMap.get("diffQty").toString());
            BigDecimal proposeQty = new BigDecimal(hashMap.get("proposeQty").toString());
            BigDecimal needQty = new BigDecimal(hashMap.get("needQty").toString());
            BigDecimal storeQty = new BigDecimal(ObjectUtil.isNotEmpty(hashMap.get("storeQty").toString()) ? hashMap.get("storeQty").toString() : "0");
            diffQtyTotal = diffQtyTotal.add(diffQty);
            proposeQtyTotal = proposeQtyTotal.add(proposeQty);
            needQtyTotal = needQtyTotal.add(needQty);
            storeQtyTotal = storeQtyTotal.add(storeQty);
            proIdSet.add((String)hashMap.get("proId"));
        }

        HashMap<String, BigDecimal> statistics = new HashMap();
        statistics.put("proId", BigDecimal.valueOf((long)proIdSet.size()));
        statistics.put("diffQty", diffQtyTotal);
        statistics.put("proposeQty", proposeQtyTotal);
        statistics.put("needQty", needQtyTotal);
        statistics.put("storeQty", storeQtyTotal);
        res.put("details", hashMaps);
        res.put("statistics", statistics);
        return res;
    }

    public Result exportData(GetReplenishInData inData, GetLoginOutData userInfo) {
        List<HashMap<String, Object>> differentReplenish = this.gaiaSdReplenishComparedDMapper.listDifferentReplenish(inData);
        final List<List<Object>> dataList = new ArrayList();
        Iterator var5 = differentReplenish.iterator();

        while(true) {
            HashMap different;
            List hashMaps;
            do {
                if (!var5.hasNext()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    HSSFWorkbook workbook = ExcelUtils.exportExcel2(new ArrayList<String[]>() {
                        {
                            this.add(CommonConstant.PRODUCT_REPLENISHMENT_DIFFERENCES_EXPORT_EXCEL_HEAD);
                        }
                    }, new ArrayList<List<List<Object>>>() {
                        {
                            this.add(dataList);
                        }
                    }, new ArrayList<String>() {
                        {
                            this.add("门店补货对比差异");
                        }
                    });
                    hashMaps = null;

                    Result uploadResult;
                    try {
                        workbook.write(bos);
                        String fileName = "门店补货对比差异-" + CommonUtil.getyyyyMMdd() + ".xls";
                        uploadResult = this.cosUtils.uploadFile(bos, fileName);
                        bos.flush();
                    } catch (IOException var21) {
                        log.error("导出文件失败:{}", var21.getMessage(), var21);
                        throw new BusinessException("导出文件失败！");
                    } finally {
                        try {
                            bos.close();
                        } catch (IOException var20) {
                            log.error("关闭流异常:{}", var20.getMessage(), var20);
                            throw new BusinessException("关闭流异常！");
                        }
                    }

                    return uploadResult;
                }

                different = (HashMap)var5.next();
                inData.setGsrhVoucherId((String)different.get("voucherId"));
                hashMaps = this.gaiaSdReplenishComparedDMapper.listDifferentReplenishDetail(inData);
            } while(hashMaps.size() <= 0);

            Integer index = 1;
            Iterator var9 = hashMaps.iterator();

            while(var9.hasNext()) {
                HashMap<String, Object> hashMap = (HashMap)var9.next();
                List<Object> line = new ArrayList();
                line.add(index);
                index = index + 1;
                line.add(different.get("stoCode"));
                line.add(different.get("stoName"));
                line.add(different.get("creDate"));
                line.add(different.get("creTime"));
                line.add(different.get("creName"));
                line.add(different.get("checkDate"));
                line.add(different.get("checkTime"));
                line.add(different.get("checkName"));
                line.add(different.get("voucherId"));
                line.add(hashMap.get("proId"));
                line.add(hashMap.get("proName"));
                line.add(hashMap.get("proSpecs"));
                line.add(hashMap.get("unit"));
                line.add(hashMap.get("factory"));
                line.add(hashMap.get("proPrice"));
                line.add(hashMap.get("proposeQty"));
                line.add(hashMap.get("needQty"));
                line.add(hashMap.get("diffQty"));
                line.add(hashMap.get("status"));
                dataList.add(line);
            }
        }
    }
}

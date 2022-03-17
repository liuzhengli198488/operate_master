package com.gys.util;

import cn.hutool.core.util.StrUtil;
import com.gys.common.enums.StoreAttributeEnum;
import com.gys.common.enums.StoreDTPEnum;
import com.gys.common.enums.StoreMedicalEnum;
import com.gys.common.enums.StoreTaxClassEnum;
import com.gys.report.entity.GaiaStoreCategoryType;
import com.gys.report.entity.StoreClassInData;
import com.gys.report.entity.StoreClassOutData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @desc:  门店分类列赋值
 * @author: ZhangChi
 * @createTime: 2021/12/20 16:47
 */
public class StoreClassSetValueUtil {
    //设置门店分类列的值
    public static void setValue(StoreClassInData inData, StoreClassOutData outData, List<GaiaStoreCategoryType> storeCategoryByClient){
        //匹配值
        Set<String> stoGssgTypeSet = new HashSet<>();
        boolean noChooseFlag = true;
        if (inData.getStoGssgType() != null) {
            for (String s : inData.getStoGssgType().split(StrUtil.COMMA)) {
                String[] str = s.split(StrUtil.UNDERLINE);
                GaiaStoreCategoryType gaiaStoreCategoryType = new GaiaStoreCategoryType();
                gaiaStoreCategoryType.setGssgType(str[0]);
                gaiaStoreCategoryType.setGssgId(str[1]);
                stoGssgTypeSet.add(str[0]);
            }
            noChooseFlag = false;
        }
        String stoAttribute = inData.getStoAttribute();
        if (stoAttribute != null) {
            noChooseFlag = false;
            inData.setStoAttributes(Arrays.asList(stoAttribute.split(StrUtil.COMMA)));
        }
        String stoIfMedical = inData.getStoIfMedical();
        if (stoIfMedical != null) {
            noChooseFlag = false;
            inData.setStoIfMedicals(Arrays.asList(stoIfMedical.split(StrUtil.COMMA)));
        }
        String stoTaxClass = inData.getStoTaxClass();
        if (stoTaxClass != null) {
            noChooseFlag = false;
            inData.setStoTaxClasss(Arrays.asList(stoTaxClass.split(StrUtil.COMMA)));
        }
        String stoIfDtp = inData.getStoIfDtp();
        if (stoIfDtp != null) {
            noChooseFlag = false;
            inData.setStoIfDtps(Arrays.asList(stoIfDtp.split(StrUtil.COMMA)));
        }


        //转换
        if (outData.getStoAttribute() != null || noChooseFlag){
            outData.setStoAttribute(StoreAttributeEnum.getName(outData.getStoAttribute()));
        }
        if (outData.getStoIfMedical() != null || noChooseFlag){
            outData.setStoIfMedical(StoreMedicalEnum.getName(outData.getStoIfMedical()));
        }
        if (outData.getStoIfDtp() != null || noChooseFlag){
            outData.setStoIfDtp(StoreDTPEnum.getName(outData.getStoIfDtp()));
        }
        if (outData.getStoTaxClass() != null || noChooseFlag){
            outData.setStoTaxClass(StoreTaxClassEnum.getName(outData.getStoTaxClass()));
        }
        List<GaiaStoreCategoryType> collect = storeCategoryByClient.stream().filter(t -> t.getGssgBrId().equals(outData.getStoCode())).collect(Collectors.toList());
        Set<String> tmpStoGssgTypeSet = new HashSet<>(stoGssgTypeSet);
        for (GaiaStoreCategoryType storeCategoryType : collect){
            if (noChooseFlag){
                if ("DX0001".equals(storeCategoryType.getGssgType())){
                    outData.setShopType(storeCategoryType.getGssgIdName());
                }else if ("DX0002".equals(storeCategoryType.getGssgType())){
                    outData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                }else if ("DX0003".equals(storeCategoryType.getGssgType())){
                    outData.setDirectManaged(storeCategoryType.getGssgIdName());
                }else if ("DX0004".equals(storeCategoryType.getGssgType())) {
                    outData.setManagementArea(storeCategoryType.getGssgIdName());
                }
            }else {
                if (!stoGssgTypeSet.isEmpty()){
                    if (tmpStoGssgTypeSet.contains("DX0001") && "DX0001".equals(storeCategoryType.getGssgType())) {
                        outData.setShopType(storeCategoryType.getGssgIdName());
                        tmpStoGssgTypeSet.remove("DX0001");
                    } else if (tmpStoGssgTypeSet.contains("DX0002") && "DX0002".equals(storeCategoryType.getGssgType())) {
                        outData.setStoreEfficiencyLevel(storeCategoryType.getGssgIdName());
                        tmpStoGssgTypeSet.remove("DX0002");
                    } else if (tmpStoGssgTypeSet.contains("DX0003") && "DX0003".equals(storeCategoryType.getGssgType())) {
                        outData.setDirectManaged(storeCategoryType.getGssgIdName());
                        tmpStoGssgTypeSet.remove("DX0003");
                    } else if (tmpStoGssgTypeSet.contains("DX0004") && "DX0004".equals(storeCategoryType.getGssgType())) {
                        outData.setManagementArea(storeCategoryType.getGssgIdName());
                        tmpStoGssgTypeSet.remove("DX0004");
                    }
                }
            }
        }
    }
}

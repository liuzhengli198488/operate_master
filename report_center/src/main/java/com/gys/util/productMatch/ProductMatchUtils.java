package com.gys.util.productMatch;

import cn.hutool.core.util.ObjectUtil;
import com.gys.entity.GaiaProductMatch;
import com.gys.entity.ProductInfo;
import com.gys.entity.ProductMatchOutData;
import com.gys.entity.ProductMatchStatus;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductMatchUtils {

    //多商品匹配列表
    public static List<ProductMatchOutData> execMatchProductList(GaiaProductMatch dr, List<ProductInfo> dtStand) {
        if (dr != null && StringUtils.isEmpty(dr.getProBarcode())) {
            dr.setProBarcode("");
        }
        //对比69码
        List<ProductMatchOutData> result = new ArrayList<>();
        List<ProductInfo> list = new ArrayList<>();
        Iterator<ProductInfo> iter = dtStand.iterator();
        while (iter.hasNext()) {
            ProductInfo a = iter.next();
            if (StringUtils.isEmpty(a.getProBarcode())) {
                a.setProBarcode("");
            }
            if (StringUtils.isEmpty(a.getProBarcode2())) {
                a.setProBarcode2("");
            }
            if (dr.getProBarcode().equals(a.getProBarcode()) || dr.getProBarcode().equals(a.getProBarcode2())) {
                list.add(a);
            }
        }
        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
            for (ProductInfo drItem : list) {
                ProductMatchOutData item = new ProductMatchOutData();
                item.setProBarCode(dr.getProBarcode());
                item.setProName(dr.getProName());
                item.setProSpecs(dr.getProSpecs());
                item.setProCode(dr.getProCode());
                item.setProRegisterNo(dr.getProRegisterNo());
                int lastMatchValue = 0;
                //准批文号
                String pzwh1 = StringUtils.isNotEmpty(drItem.getProRegisterNo()) ? drItem.getProRegisterNo() : "";
                String pzwh2 = StringUtils.isNotEmpty(dr.getProRegisterNo()) ? dr.getProRegisterNo() : "";
                pzwh1 = pzwh1.length() > 8 ? pzwh1.substring(pzwh1.length() - 9).toLowerCase() : pzwh1;
                pzwh2 = pzwh2.length() > 8 ? pzwh2.substring(pzwh2.length() - 9).toLowerCase() : pzwh2;

                String tym1 = drItem.getProCommonName();
                String tym2 = dr.getProName();

                String gg1 = drItem.getProSpecs();
                String gg2 = dr.getProSpecs();

                String cj1 = StringUtils.isNotEmpty(drItem.getFactoryName()) ? drItem.getFactoryName() : "";
                String cj2 = StringUtils.isNotEmpty(dr.getProFactoryName()) ? dr.getProFactoryName() : "";

                boolean pzwhMatch = pzwh1.equals(pzwh2);
                boolean tymMatch = tym2.toUpperCase().contains(tym1.toUpperCase());
                boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;

                int matchValue = 30 + (pzwhMatch ? 25 : 0) + (tymMatch ? 25 : 0) + (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                if (matchValue > lastMatchValue) {
                    //封装返回信息
                    item.setMatchDegree(matchValue + "");
                    item.setGssdProCode(drItem.getProCode());
                    item.setGssdProName(drItem.getProCommonName());
                    item.setGssdProBarCode(drItem.getProBarcode());
                    item.setGssdProFactoryName(drItem.getFactoryName());
                    item.setGssdProSpecs(drItem.getProSpecs());
                    item.setGssdProRegisterNo(drItem.getProRegisterNo());
                    item.setProNameFlag(tymMatch ? "Y" : "N");
                    item.setProBarCodeFlag("Y");
                    item.setProSpecsFlag(ggMatch ? "Y" : "N");
                    item.setProFactoryNameFlag(cjMatch ? "Y" : "N");
                    item.setProRegisterNoFlag(pzwhMatch ? "Y" : "N");
                    result.add(item);
                }
            }
        }
        //对比批准文号
        List<ProductInfo> drRegister = new ArrayList<>();
        Iterator<ProductInfo> iterRegister = dtStand.iterator();
        while (iterRegister.hasNext()) {
            ProductInfo a = iterRegister.next();
            if (StringUtils.isEmpty(a.getProBarcode())) {
                a.setProBarcode("");
            }
            if (StringUtils.isEmpty(a.getProBarcode2())) {
                a.setProBarcode2("");
            }
            String re = StringUtils.isNotEmpty(a.getProRegisterNo()) ? a.getProRegisterNo() : "";
            a.setProRegisterNo(re);
            if (!(dr.getProBarcode().equals(a.getProBarcode()))
                    && !(dr.getProBarcode().equals(a.getProBarcode2()))
                    && dr.getProBarcode().equals(re)) {
                drRegister.add(a);
            }
        }
        if (ObjectUtil.isNotEmpty(drRegister) && drRegister.size() > 0) {
            if (drRegister.size() > 0) {
                int lastMatchValue = 0;
                for (ProductInfo drItem : drRegister) {
                    ProductMatchOutData item = new ProductMatchOutData();
                    item.setProBarCode(dr.getProBarcode());
                    item.setProName(dr.getProName());
                    item.setProSpecs(dr.getProSpecs());
                    item.setProCode(dr.getProCode());
                    item.setProRegisterNo(dr.getProRegisterNo());
                    String tym1 = drItem.getProCommonName();
                    String tym2 = dr.getProName();

                    String gg1 = drItem.getProSpecs();
                    String gg2 = dr.getProSpecs();

                    String cj1 = StringUtils.isNotEmpty(drItem.getFactoryName()) ? drItem.getFactoryName() : "";
                    String cj2 = StringUtils.isNotEmpty(dr.getProFactoryName()) ? dr.getProFactoryName() : "";

                    boolean tymMatch = tym2.toUpperCase().contains(tym1.toUpperCase());
                    boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                    boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;

                    int matchValue = (tymMatch ? 40 : 0) + (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                    if (matchValue > lastMatchValue) {
                        lastMatchValue = matchValue;
                        //封装返回信息
                        item.setMatchDegree(matchValue + "");
                        item.setGssdProCode(drItem.getProCode());
                        item.setGssdProName(drItem.getProCommonName());
                        item.setGssdProBarCode(drItem.getProBarcode());
                        item.setGssdProFactoryName(drItem.getFactoryName());
                        item.setGssdProSpecs(drItem.getProSpecs());
                        item.setGssdProRegisterNo(drItem.getProRegisterNo());
                        item.setProNameFlag(tymMatch ? "Y" : "N");
                        item.setProBarCodeFlag("N");
                        item.setProSpecsFlag(ggMatch ? "Y" : "N");
                        item.setProFactoryNameFlag(cjMatch ? "Y" : "N");
                        item.setProRegisterNoFlag("Y");
                        result.add(item);
                    }
                }
            }
            //对比通用名
            List<ProductInfo> drCommName = new ArrayList<>();
            Iterator<ProductInfo> iterCommonName = dtStand.iterator();
            while (iterCommonName.hasNext()) {
                ProductInfo a = iterCommonName.next();
                if (StringUtils.isEmpty(a.getProBarcode())) {
                    a.setProBarcode("");
                }
                if (StringUtils.isEmpty(a.getProBarcode2())) {
                    a.setProBarcode2("");
                }
                String re = StringUtils.isNotEmpty(a.getProRegisterNo()) ? a.getProRegisterNo() : "";
                a.setProRegisterNo(re);
                if (!(dr.getProBarcode().equals(a.getProBarcode()))
                        && !(dr.getProBarcode().equals(a.getProBarcode2()))
                        && !(dr.getProBarcode().equals(a.getProRegisterNo()))
                        && dr.getProBarcode().equals(a.getProCommonName())) {
                    drCommName.add(a);
                }
            }
            if (drCommName.size() > 0) {
                int lastMatchValue = 0;
                for (ProductInfo drItem : drCommName) {
                    ProductMatchOutData item = new ProductMatchOutData();
                    item.setProBarCode(dr.getProBarcode());
                    item.setProName(dr.getProName());
                    item.setProSpecs(dr.getProSpecs());
                    item.setProCode(dr.getProCode());
                    item.setProRegisterNo(dr.getProRegisterNo());
                    String tym1 = drItem.getProCommonName();
                    String tym2 = dr.getProName();

                    String gg1 = drItem.getProSpecs();
                    String gg2 = dr.getProSpecs();

                    String cj1 = StringUtils.isNotEmpty(drItem.getFactoryName()) ? drItem.getFactoryName() : "";
                    String cj2 = StringUtils.isNotEmpty(dr.getProFactoryName()) ? dr.getProFactoryName() : "";

                    boolean tymMatch = tym2.toUpperCase().contains(tym1.toUpperCase());
                    boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                    boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;
                    int matchValue = (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0) + (tymMatch ? 40 : 0);

                    if (matchValue > lastMatchValue) {
                        lastMatchValue = matchValue;
                        //封装返回信息
                        item.setMatchDegree(matchValue + "");
                        item.setGssdProCode(drItem.getProCode());
                        item.setGssdProName(drItem.getProCommonName());
                        item.setGssdProBarCode(drItem.getProBarcode());
                        item.setGssdProFactoryName(drItem.getFactoryName());
                        item.setGssdProSpecs(drItem.getProSpecs());
                        item.setGssdProRegisterNo(drItem.getProRegisterNo());
                        item.setProNameFlag(tymMatch ? "Y" : "N");
                        item.setProBarCodeFlag("N");
                        item.setProSpecsFlag(ggMatch ? "Y" : "N");
                        item.setProFactoryNameFlag(cjMatch ? "Y" : "N");
                        item.setProRegisterNoFlag("N");
                        result.add(item);
                    }
                }
            }
        }
        if (ObjectUtil.isNotEmpty(result) && result.size() > 0) {
            result = result.stream().filter(s -> !s.equals(dr.getMatchProCode())).sorted(Comparator.comparing(ProductMatchOutData::getMatchDegree))
                    .collect(Collectors.toList());
            if (result.size() > 5) {
                result = result.subList(0, 5);
            }
        }
        return result;
    }

    //单商品匹配
    public static ProductMatchStatus execMatchProcess(GaiaProductMatch dr, List<ProductInfo> dtStand) {
        ProductMatchStatus result = new ProductMatchStatus();
        //todo 进行数据比对。
        //比对69码
        if (StringUtils.isEmpty(dr.getProBarcode())) {
            dr.setProBarcode("");
        }
        List<ProductInfo> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(dr.getProBarcode())) {
            Iterator<ProductInfo> iter = dtStand.iterator();
            while (iter.hasNext()) {
                ProductInfo a = iter.next();
                if (StringUtils.isEmpty(a.getProBarcode())) {
                    a.setProBarcode("");
                }
                if (StringUtils.isEmpty(a.getProBarcode2())) {
                    a.setProBarcode2("");
                }
                if (dr.getProBarcode().equals(a.getProBarcode()) || dr.getProBarcode().equals(a.getProBarcode2())) {
                    list.add(a);
                }
            }
        }
        result.setBarCode(false);
//        DataRow[] drBarcode = dtStand.stream.(string.Format("商品条码1='{0}' or 商品条码2='{0}'", dr["商品条码"]));
        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
            result.setBarCode(true);
            result.setMatchedCode(list.get(0).getProCode());
            int lastMatchValue = 0;
            for (ProductInfo drItem : list) {
//                String pzwh1 = Convert.ToString(drItem["批准文号"]);
//                String pzwh2 = Convert.ToString(dr["批准文号"]);
//                pzwh1 = pzwh1.Length > 8 ? pzwh1.Substring(pzwh1.Length - 9).ToUpper() : pzwh1;
//                pzwh2 = pzwh2.Length > 8 ? pzwh2.Substring(pzwh2.Length - 9).ToUpper() : pzwh2;
                //准批文号
                String pzwh1 = StringUtils.isNotEmpty(drItem.getProRegisterNo()) ? drItem.getProRegisterNo() : "";
                String pzwh2 = StringUtils.isNotEmpty(dr.getProRegisterNo()) ? dr.getProRegisterNo() : "";
                pzwh1 = pzwh1.length() > 8 ? pzwh1.substring(pzwh1.length() - 9).toLowerCase() : pzwh1;
                pzwh2 = pzwh2.length() > 8 ? pzwh2.substring(pzwh2.length() - 9).toLowerCase() : pzwh2;

                String tym1 = drItem.getProCommonName();
                String tym2 = dr.getProName();

                String gg1 = drItem.getProSpecs();
                String gg2 = dr.getProSpecs();

                String cj1 = StringUtils.isNotEmpty(drItem.getFactoryName()) ? drItem.getFactoryName() : "";
                String cj2 = StringUtils.isNotEmpty(dr.getProFactoryName()) ? dr.getProFactoryName() : "";

                boolean pzwhMatch = pzwh1.equals(pzwh2);
                boolean tymMatch = tym2 == null ? false : tym2.toUpperCase().contains(tym1.toUpperCase());
                boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
//                boolean cjMatch = cj1.toUpperCase().contains(cj2.toUpperCase());
                boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;

                int matchValue = 30 + (pzwhMatch ? 25 : 0) + (tymMatch ? 25 : 0) + (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                if (matchValue > lastMatchValue) {
                    lastMatchValue = matchValue;
                    result.setRegisteredNumber(pzwhMatch);
                    result.setCommName(tymMatch);
                    result.setSpec(ggMatch);
                    result.setFactory(cjMatch);
                    result.setMatchedCode(drItem.getProCode());
                    result.setMatchValue(matchValue + "");
                }
            }
        }
        if (!(result.isBarCode())) {
            result.setBarCode(false);
            result.setRegisteredNumber(false);
//            result.setMatchedCode("");
            //批准文号
            //通用名（包含）
            //规格，算法匹配
            //厂家（包含）
            List<ProductInfo> drRegister = new ArrayList<>();
            Iterator<ProductInfo> iterRegister = dtStand.iterator();
            //准批文号
            String pzwh2 = StringUtils.isNotEmpty(dr.getProRegisterNo()) ? dr.getProRegisterNo() : "";
            while (iterRegister.hasNext()) {
                ProductInfo a = iterRegister.next();
                String pzwh1 = StringUtils.isNotEmpty(a.getProRegisterNo()) ? a.getProRegisterNo() : "";
                if (pzwh2.equals(pzwh1)) {
                    drRegister.add(a);
                }
            }
            if (drRegister.size() > 0) {
                result.setRegisteredNumber(true);
                result.setMatchedCode(drRegister.get(0).getProCode());
                int lastMatchValue = 0;
                for (ProductInfo drItem : drRegister) {
                    String tym1 = drItem.getProCommonName();
                    String tym2 = dr.getProName();

                    String gg1 = drItem.getProSpecs();
                    String gg2 = dr.getProSpecs();

                    String cj1 = StringUtils.isNotEmpty(drItem.getFactoryName()) ? drItem.getFactoryName() : "";
                    String cj2 = StringUtils.isNotEmpty(dr.getProFactoryName()) ? dr.getProFactoryName() : "";

                    boolean tymMatch = tym2.toUpperCase().contains(tym1.toUpperCase());
                    boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                    boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;
                    int matchValue = (tymMatch ? 40 : 0) + (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);
                    if (matchValue > lastMatchValue) {
                        lastMatchValue = matchValue;
                        result.setCommName(tymMatch);
                        result.setSpec(ggMatch);
                        result.setFactory(cjMatch);
                        result.setMatchedCode(drItem.getProCode());
                        result.setMatchValue(matchValue+"");
                    }
                }
            } else {
                List<ProductInfo> drCommName = new ArrayList<>();
                Iterator<ProductInfo> iterCommonName = dtStand.iterator();
                String tym2 = StringUtils.isNotEmpty(dr.getProName()) ? dr.getProName() : "";
                while (iterCommonName.hasNext()) {
                    ProductInfo a = iterCommonName.next();
                    String tym1 = StringUtils.isNotEmpty(a.getProCommonName()) ? a.getProCommonName() : "";
                    if (tym2.equals(tym1)) {
                        drCommName.add(a);
                    }
                }
                if (drCommName.size() > 0) {
                    result.setCommName(true);
                    result.setMatchedCode(drCommName.get(0).getProCode());
                    int lastMatchValue = 0;
                    for (ProductInfo drItem : drCommName) {
                        String gg1 = drItem.getProSpecs();
                        String gg2 = dr.getProSpecs();

                        String cj1 = StringUtils.isNotEmpty(drItem.getFactoryName()) ? drItem.getFactoryName() : "";
                        String cj2 = StringUtils.isNotEmpty(dr.getProFactoryName()) ? dr.getProFactoryName() : "";

                        boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                        boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;
                        int matchValue = (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                        if (matchValue > lastMatchValue) {
                            lastMatchValue = matchValue;
                            result.setSpec(ggMatch);
                            result.setFactory(cjMatch);
                            result.setMatchedCode(drItem.getProCode());
                            result.setMatchValue(matchValue+"");
                        }
                    }
                }
            }

        }
        return result;
    }

    public static ProductMatchStatus execProProcess(GaiaProductMatch dr, List<ProductInfo> dtStand) {
        ProductMatchStatus result = new ProductMatchStatus();
        //todo 进行数据比对。
        //比对69码
        List<ProductInfo> list = new ArrayList<>();
        Iterator<ProductInfo> iter = dtStand.iterator();
        while (iter.hasNext()) {
            ProductInfo a = iter.next();
            if (dr.getProBarcode().equals(a.getProBarcode()) || dr.getProBarcode().equals(a.getProBarcode2())) {
                list.add(a);
            }
        }
//        List<ProductInfo> list = dtStand.stream().filter(f -> f.getProBarcode().equals(dr.getProBarcode()) || f.getProBarcode2().equals(dr.getProBarcode())).collect(Collectors.toList());
//        DataRow[] drBarcode = dtStand.stream.(string.Format("商品条码1='{0}' or 商品条码2='{0}'", dr["商品条码"]));
        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
            result.setBarCode(true);
            result.setMatchedCode(list.get(0).getProCode());
            int lastMatchValue = 0;
            for (ProductInfo drItem : list) {
//                String pzwh1 = Convert.ToString(drItem["批准文号"]);
//                String pzwh2 = Convert.ToString(dr["批准文号"]);
//                pzwh1 = pzwh1.Length > 8 ? pzwh1.Substring(pzwh1.Length - 9).ToUpper() : pzwh1;
//                pzwh2 = pzwh2.Length > 8 ? pzwh2.Substring(pzwh2.Length - 9).ToUpper() : pzwh2;
                //准批文号
                String pzwh1 = drItem.getProRegisterNo();
                String pzwh2 = dr.getProRegisterNo();
                pzwh1 = pzwh1.length() > 8 ? pzwh1.substring(pzwh1.length() - 9).toLowerCase() : pzwh1;
                pzwh2 = pzwh2.length() > 8 ? pzwh2.substring(pzwh2.length() - 9).toLowerCase() : pzwh2;

                String tym1 = drItem.getProCommonName();
                String tym2 = dr.getProName();

                String gg1 = drItem.getProSpecs();
                String gg2 = dr.getProSpecs();

                String cj1 = drItem.getFactoryName();
                String cj2 = dr.getProFactoryName();

                boolean pzwhMatch = pzwh1.equals(pzwh2);
                boolean tymMatch = tym2.toUpperCase().contains(tym1.toUpperCase());
                boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;

                int matchValue = 30 + (pzwhMatch ? 25 : 0) + (tymMatch ? 25 : 0) + (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                if (matchValue > lastMatchValue) {
                    lastMatchValue = matchValue;
                    result.setRegisteredNumber(pzwhMatch);
                    result.setCommName(tymMatch);
                    result.setSpec(ggMatch);
                    result.setFactory(cjMatch);
                    result.setMatchedCode(drItem.getProCode());
                    result.setMatchValue(matchValue + "");
                }
            }
        }
        if (!((result.isBarCode() && result.isRegisteredNumber()) || (result.isBarCode() && result.isCommName()))) {
            result.setBarCode(false);
            result.setRegisteredNumber(false);
            result.setMatchedCode("");
            //批准文号
            //通用名（包含）
            //规格，算法匹配
            //厂家（包含）
            List<ProductInfo> drRegister = new ArrayList<>();
            Iterator<ProductInfo> iterRegister = dtStand.iterator();
            while (iterRegister.hasNext()) {
                ProductInfo a = iterRegister.next();
                if (dr.getProBarcode().equals(a.getProRegisterNo())) {
                    drRegister.add(a);
                }
            }
            if (drRegister.size() > 0) {
                result.setRegisteredNumber(true);
                result.setMatchedCode(drRegister.get(0).getProCode());
                int lastMatchValue = 0;
                for (ProductInfo drItem : drRegister) {
                    String tym1 = drItem.getProCommonName();
                    String tym2 = dr.getProName();

                    String gg1 = drItem.getProSpecs();
                    String gg2 = dr.getProSpecs();

                    String cj1 = drItem.getFactoryName();
                    String cj2 = dr.getProFactoryName();

                    boolean tymMatch = tym2.toUpperCase().contains(tym1.toUpperCase());
                    boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                    boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;

                    int matchValue = (tymMatch ? 40 : 0) + (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                    if (matchValue > lastMatchValue) {
                        lastMatchValue = matchValue;
                        result.setCommName(tymMatch);
                        result.setSpec(ggMatch);
                        result.setFactory(cjMatch);
                        result.setMatchedCode(drItem.getProCode());
                        result.setMatchValue(matchValue + "");
                    }
                }
            } else {
                List<ProductInfo> drCommName = new ArrayList<>();
                Iterator<ProductInfo> iterCommonName = dtStand.iterator();
                while (iterCommonName.hasNext()) {
                    ProductInfo a = iterCommonName.next();
                    if (dr.getProBarcode().equals(a.getProCommonName())) {
                        drCommName.add(a);
                    }
                }
                if (drCommName.size() > 0) {
                    result.setCommName(true);
                    result.setMatchedCode(drCommName.get(0).getProCode());
                    int lastMatchValue = 0;
                    for (ProductInfo drItem : drCommName) {
                        String gg1 = drItem.getProSpecs();
                        String gg2 = dr.getProSpecs();

                        String cj1 = drItem.getFactoryName();
                        String cj2 = dr.getProFactoryName();

                        boolean ggMatch = ProductMatchUtil.isMatchSpec(gg1, gg2);
                        boolean cjMatch = StringUtils.isNotEmpty(cj1) && StringUtils.isNotEmpty(cj2) ? cj1.toUpperCase().contains(cj2.toUpperCase()) : false;
                        int matchValue = (ggMatch ? 10 : 0) + (cjMatch ? 10 : 0);

                        if (matchValue > lastMatchValue) {
                            lastMatchValue = matchValue;
                            result.setSpec(ggMatch);
                            result.setFactory(cjMatch);
                            result.setMatchedCode(drItem.getProCode());
                            result.setMatchValue(matchValue + "");
                        }
                    }
                }
            }

        }
        return result;
    }
}

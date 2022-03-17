package com.gys.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.response.Result;
import com.gys.entity.*;
import com.gys.entity.data.productMatch.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ProductMatchService {
    Result importProductMatchList(HttpServletRequest request,GetLoginOutData userInfo,MultipartFile file,String clientId,String[] stoCodes,String tel);

    Map<String,Integer> ratioCount(MatchProductInData inData);

    List<MatchProductOutData> selectProMatchList(MatchProductInData inData);

    void sureProduct(ProductSureInData inData);

    void modifyProBasicInfo(ProBasicInfo inData);

    List<ProductClass> productClassList();

    List<ProductCompClass> productComponentList();

    List<Map<String,String>> getStoListByClient(StoInData inData);

    List<ProductMatchOutData> otherProductList(MatchProductInData inData);

    MatchSchedule productMatchSchedule(MatchProductInData inData);

    List<PictureMessage> imageQuery(MatchProductInData inData);

    Result exportData(ProductSureInData inData);

    GaiaProductBasic queryProductBasicById(MatchProductInData inData);

    void addProductBasic(ProductBasicInData inData);

    List<Map<String,String>> selectClientList(StoInData inData);

    Result importKuCun(HttpServletRequest request, MultipartFile file,GetLoginOutData userInfo);

    Map<String,Object> selectMergeProductList(MatchProductInData inData);

    List<LinkedHashMap<String,String>> selectNoMergeProductList(MatchProductInData inData);

    Result exportMergeProductList(MatchProductInData inData);

    Result exportNoMergeProductList(MatchProductInData inData);

    String selectNextMatchBatch(String clientId);

    String selectStoreByCode(String clientId,String stoCode);

    String selectNextCode(String clientId);

    void addProMatchRecord(ProductMatchZ productMatchZ);

    String selectMatchBatch(String clientId,String matchCode);

    void updateProMatchRecord(ProductMatchZ productMatchZ);

    void batchAddProductMatch(List<GaiaProductMatch> list);

    List<ProductInfo> selectProductBasicList(GaiaProductMatch inData);

    void addProductMatch(GaiaProductMatch inData);

    String selectClientByCode(String clientId);

    List<StoreInfo> selectStoreAndDcCodes(String client);

    void confirmProductBasic(ConfirmProductBasicDTO confirmProductBasicDTO);

}


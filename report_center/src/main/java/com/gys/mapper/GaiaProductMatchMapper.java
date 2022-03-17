package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaProductMatch;
import com.gys.entity.ProductInfo;
import com.gys.entity.data.productMatch.*;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface GaiaProductMatchMapper extends BaseMapper<GaiaProductMatch> {
    List<ProductInfo> selectProductBasicList(GaiaProductMatch inData);

    String selectStoreByCode(@Param("client") String clientId, @Param("stoCode") String stoCode);

    String selectClientByCode(@Param("client") String clientId);

    String selectNextCode(@Param("client") String clientId);

    void addProMatchRecord(ProductMatchZ productMatchZ);

    void updateProMatchRecord(ProductMatchZ productMatchZ);

    int selectProMatchCount(MatchProductInData inData);

    List<MatchProductOutData> selectProMatchList(MatchProductInData inData);

    List<MatchProductOutData>selectProMatchStoGroup(MatchProductInData inData);

    void sureProduct(List<ProductSureMsg> list);

    void modifyMatchClass(ProBasicInfo inData);

    void modifyProBasicInfo(ProBasicInfo inData);

    List<ProductClass> selectProductClassList();

    List<ProductCompClass> selectProductComponentList();

    List<Map<String,String>> getStoListByClient(@Param("client") String clientId, @Param("stoName") String stoName);

    GaiaProductMatch selectProductMatch(MatchProductInData inData);

    MatchSchedule productMatchSchedule(MatchProductInData inData);

    List<PictureMessage> imageQuery(@Param("clientId") String clientId,@Param("stoCode") List<String> stoCode,@Param("proCode") String proCode,@Param("wmClient") String wmClient);

    void updateProductMatch(ProductSureMsg inData);

    void updateTpCollect(@Param("clientId") String clientId,@Param("stoCode") String stoCode,@Param("proCode") String proCode,@Param("proSelfCode") String proSelfCode,@Param("wmClient") String wmClient);

    String selectProPym(@Param("proName") String proName);

    void batchAddProductMatch(List<GaiaProductMatch> list);

    void addProductMatch(GaiaProductMatch inData);

    String selectNextMatchBatch(@Param("clientId") String clientId);

    String selectMatchBatch(@Param("clientId") String clientId,@Param("matchCode") String matchCode);

    List<Map<String,String>> selectProCodeByClient(@Param("clientId") String clientId);

    List<Map<String,String>> selectProCodeByStore(@Param("clientId") String clientId,@Param("stoCodes") String[] stoCodes);

    List<LinkedHashMap<String,String>> selectMatchProCode(MatchProductInData inData);

    List<LinkedHashMap<String,String>> selectNoMatchProCode(MatchProductInData inData);

    int selectAllRatioCount(@Param("clientId") String clientId,@Param("matchBatch") String matchBatch);

    int selectUnratioCount(@Param("clientId") String clientId,@Param("matchBatch") String matchBatch);
}
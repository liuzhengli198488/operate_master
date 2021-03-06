package com.gys.service.Impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.exception.BusinessException;
import com.gys.common.redis.RedisManager;
import com.gys.common.response.Result;
import com.gys.entity.*;
import com.gys.entity.data.productMatch.*;
import com.gys.mapper.*;
import com.gys.service.ProductMatchService;
import com.gys.util.*;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import com.gys.util.productMatch.ProductMatchUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductMatchServiceImpl implements ProductMatchService {
    @Resource
    private GaiaProductMatchMapper productMatchMapper;
    @Resource
    private GaiaProductBasicMapper productBasicMapper;
    @Resource
    private GaiaProductBusinessMapper productBusinessMapper;
    @Resource
    private GaiaStoreDataMapper storeDataMapper;
    @Autowired
    private CosUtils cosUtils;
    @Autowired
    private ProductCheckKucunImportMapper productCheckKucunImportMapper;

    @Autowired
    private RedisManager redisManager;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result importProductMatchList(HttpServletRequest request, GetLoginOutData userInfo, MultipartFile file, String clientId, String[] stoCodes, String tel) {
        if (StrUtil.isBlank(tel)) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if (!PhoneFormatCheckUtils.isPhoneLegal(tel)) {
            throw new BusinessException("???????????????????????????");
        }

        Result result = new Result();
        try {
            String rootPath = request.getSession().getServletContext().getRealPath("/");
            String fileName = file.getOriginalFilename();
            String path = rootPath + fileName;
//            File newFile = new File(path);
//            //????????????????????????
//            if(!newFile.exists()) {
//                newFile.mkdirs();//???????????????????????????????????????
//            }
//            //????????????????????????
//            file.transferTo(newFile);
            // ???????????????
            String url = cosUtils.urlAuth(path);
            String type = url.substring(url.lastIndexOf(".") + 1, url.indexOf("?"));
            InputStream in = file.getInputStream();
            Workbook wk = null;
            if ("xls".equals(type)) {
                //???????????????
                wk = new HSSFWorkbook(in);
            } else if ("xlsx".equals(type)) {
                wk = new XSSFWorkbook(in);
            } else {
                log.info("???excel??????");
                throw new BusinessException("??????????????????");
            }
//            if(ObjectUtil.isNotEmpty(stoCodes)){
//                List<String> stoMsg = productMatchMapper.selectStoreByCode(clientId,stoCodes);
//                if (ObjectUtil.isNotEmpty(stoMsg)){
//                    String resultString = "";
//                    for(int j=0;j<=stoMsg.size()-1;j++){
//                        if(j < stoMsg.size()-1){
//                            resultString += stoMsg.get(j) + ",";
//                        }else{
//                            resultString += stoMsg.get(j);
//                        }
//                    }
//                    throw new BusinessException("???????????????????????????,?????????????????????");
//                }
//            }else {
//
//            }
//            if (fileName.indexOf("xlsx") > 0){
            //??????????????????Excel???????????????????????????????????????
            //???????????????Sheet???
            Sheet sheet = wk.getSheetAt(0);
            //?????????
            ImportProductInfo model = new ImportProductInfo();
            List<ImportProductInfo> importProductInfoList = ExcelManage.readFromExcel(sheet, model);
            //??????????????????
            String matchBatch = productMatchMapper.selectNextMatchBatch(clientId);
            if (ObjectUtil.isNotEmpty(importProductInfoList) && importProductInfoList.size() > 0) {
                //?????????????????????????????????????????????
                List<GaiaProductMatch> list = new ArrayList<>();
//                List<GaiaProductBusiness> productBusinessList = new ArrayList<>();
                //???????????????????????????
                if (ObjectUtil.isNotEmpty(stoCodes)) {
                    for (int i = 0; i < stoCodes.length; i++) {
                        String matchCode = productMatchMapper.selectStoreByCode(clientId, stoCodes[i]);
                        //????????????????????????
                        ProductMatchZ productMatchZ = new ProductMatchZ();
                        productMatchZ.setClient(clientId);
                        productMatchZ.setStoCode(stoCodes[i]);
                        productMatchZ.setMatchType("1");
                        productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                        productMatchZ.setMatchCreater(userInfo.getUserId());
                        if (stoCodes.length == 1) {
                            productMatchZ.setMatchDataCheckTouchTel(tel);
                            productMatchZ.setMatchDataCheckFlag("3");//????????????????????????3?????????????????????????????????????????????????????????
                        }
                        if (StringUtils.isEmpty(matchCode)) {//??????????????????
                            matchCode = productMatchMapper.selectNextCode(clientId);
                            productMatchZ.setMatchCode(matchCode);
                            productMatchZ.setMatchBatch(matchBatch);
                            productMatchMapper.addProMatchRecord(productMatchZ);
                        } else {//??????????????????
                            matchBatch = productMatchMapper.selectMatchBatch(clientId, matchCode);
                            productMatchZ.setMatchCode(matchCode);
                            productMatchZ.setMatchBatch(matchBatch);
                            productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
                            productMatchZ.setMatchUpdater(userInfo.getUserId());
                            productMatchMapper.updateProMatchRecord(productMatchZ);
                        }
                        for (ImportProductInfo productInfo : importProductInfoList) {
                            GaiaProductMatch productMatch = new GaiaProductMatch();
                            BeanUtils.copyProperties(productInfo, productMatch);
                            productMatch.setProName(productInfo.getProCommonname());
                            if (StringUtils.isEmpty(productInfo.getProCommonname())) {
                                productMatch.setProName(productInfo.getProName());
                            }
//                            List<ProductInfo> productInfoList = productMatchMapper.selectProductBasicList(productMatch);//?????????
                            productMatch.setClient(clientId);
                            productMatch.setStoCode(stoCodes[i]);
                            productMatch.setMatchCode(matchCode);
                            productMatch.setMatchBatch(productMatchZ.getMatchBatch());
//                            productMatch.setSite(clientId + "-" + stoCodes[i]);
                            //?????????????????????
//                            ProductMatchStatus productMatchStatus = ProductMatchUtils.execMatchProcess(productMatch, productInfoList);
//                            if (ObjectUtil.isEmpty(productMatchStatus.getMatchValue())) {
//                                productMatchStatus.setMatchValue("0");
//                            }
//                            if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(BigDecimal.ZERO) == 0) {
//                                productMatch.setMatchDegree(productMatchStatus.getMatchValue());
//                                productMatch.setMatchType("0");
//                                productMatch.setProMatchStatus("0");
//                                productMatch.setMatchProCode("99999999");
//                            } else if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(new BigDecimal(100)) == 0) {
//                                productMatch.setMatchDegree(productMatchStatus.getMatchValue());
//                                productMatch.setMatchType("2");
//                                productMatch.setProMatchStatus("1");
//                                productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
//                            } else {
//                                productMatch.setMatchDegree(productMatchStatus.getMatchValue());
//                                productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
//                                productMatch.setMatchType("1");
//                                productMatch.setProMatchStatus("1");
//                            }
//                            productMatch.setMatchCreateDate(CommonUtil.getyyyyMMdd());
//                            productMatch.setMatchCreateTime(CommonUtil.getHHmmss());
                            list.add(productMatch);
                            //?????????????????????????????????
//                                StoreInfo s = new StoreInfo();
//                                s.setClientId(clientId);
//                                s.setProSite(stoCodes[i]);
//                                GaiaProductBusiness productBusiness = this.saveProBusinissInfo(productMatch, s);
//                                productBusinessList.add(productBusiness);
                        }
                        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
                            //?????????????????????????????????
                            productMatchMapper.batchAddProductMatch(list);
                        }
                    }
//                    //?????????????????????????????????
//                    if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
//                        String matchCode = productMatchMapper.selectClientByCode(clientId);
//                        ProductMatchZ productMatchZ = new ProductMatchZ();
//                        productMatchZ.setClient(clientId);
//                        productMatchZ.setMatchCode(matchCode);
//                        productMatchZ.setStoCode("");
//                        productMatchZ.setMatchType("0");
//                        productMatchZ.setMatchDataCheckTouchTel(tel);
//                        productMatchZ.setMatchDataCheckFlag("3");//????????????????????????3?????????????????????????????????????????????????????????
//                        productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
//                        productMatchZ.setMatchCreater(userInfo.getUserId());
//                        if (StringUtils.isNotEmpty(matchCode)) {//??????????????????
//                            productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
//                            productMatchZ.setMatchUpdater(userInfo.getUserId());
//                            productMatchZ.setMatchCode(matchCode);
//                            productMatchMapper.updateProMatchRecord(productMatchZ);
//                        } else {
//                            //????????????????????????
//                            matchCode = productMatchMapper.selectNextCode(clientId);
//                            productMatchZ.setMatchCode(matchCode);
//                            productMatchMapper.addProMatchRecord(productMatchZ);
//                        }
//                        for (GaiaProductMatch a :list) {
//                            a.setMatchCode(matchCode);
//                            a.setStoCode("");
//                        }
//                        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
//                            //?????????????????????????????????
//                            productMatchMapper.batchAddProductMatch(list);
//                        }
//                    }
                } else {
                    String matchCode = productMatchMapper.selectClientByCode(clientId);
                    ProductMatchZ productMatchZ = new ProductMatchZ();
                    productMatchZ.setClient(clientId);
                    productMatchZ.setMatchCode(matchCode);
                    productMatchZ.setStoCode("");
                    productMatchZ.setMatchType("0");
                    productMatchZ.setMatchDataCheckTouchTel(tel);
                    productMatchZ.setMatchDataCheckFlag("3");//????????????????????????3?????????????????????????????????????????????????????????
                    productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                    productMatchZ.setMatchCreater(userInfo.getUserId());
                    if (StringUtils.isNotEmpty(matchCode)) {//??????????????????
                        matchBatch = productMatchMapper.selectMatchBatch(clientId, matchCode);
                        productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
                        productMatchZ.setMatchUpdater(userInfo.getUserId());
                        productMatchZ.setMatchCode(matchCode);
                        productMatchZ.setMatchBatch(matchBatch);
                        productMatchMapper.updateProMatchRecord(productMatchZ);
                    } else {
                        //????????????????????????
                        matchCode = productMatchMapper.selectNextCode(clientId);
                        productMatchZ.setMatchCode(matchCode);
                        productMatchZ.setMatchBatch(matchBatch);
                        productMatchMapper.addProMatchRecord(productMatchZ);
                    }
                    for (ImportProductInfo productInfo : importProductInfoList) {
                        GaiaProductMatch productMatch = new GaiaProductMatch();
                        BeanUtils.copyProperties(productInfo, productMatch);
                        productMatch.setProName(productInfo.getProCommonname());
//                        List<ProductInfo> productInfoList = productMatchMapper.selectProductBasicList(productMatch);//?????????
                        productMatch.setClient(clientId);
                        productMatch.setStoCode("");//???????????????????????????????????????
                        productMatch.setMatchCode(matchCode);
                        productMatch.setMatchBatch(productMatchZ.getMatchBatch());
//                        productMatch.setSite(clientId);
                        //?????????????????????
//                        ProductMatchStatus productMatchStatus = ProductMatchUtils.execMatchProcess(productMatch, productInfoList);
//                        if (ObjectUtil.isEmpty(productMatchStatus.getMatchValue())) {
//                            productMatchStatus.setMatchValue("0");
//                        }
//                        if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(BigDecimal.ZERO) == 0) {
//                            productMatch.setMatchDegree(productMatchStatus.getMatchValue());
//                            productMatch.setMatchType("0");
//                            productMatch.setProMatchStatus("0");
//                            productMatch.setMatchProCode("99999999");
//                        } else if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(new BigDecimal(100)) == 0) {
//                            productMatch.setMatchDegree(productMatchStatus.getMatchValue());
//                            productMatch.setMatchType("2");
//                            productMatch.setProMatchStatus("1");
//                            productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
//                        } else {
//                            productMatch.setMatchDegree(productMatchStatus.getMatchValue());
//                            productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
//                            productMatch.setMatchType("1");
//                            productMatch.setProMatchStatus("2");
//                        }
                        //?????????????????????????????????
                        List<StoreInfo> storeList = storeDataMapper.selectStoreAndDcCodes(productMatch.getClient());
                        if (ObjectUtil.isNotEmpty(storeList) && storeList.size() > 0) {
//                            for (StoreInfo s : storeList) {
//                        ?????????????????????????????????????????????
//                                Example example = new Example(GaiaProductBusiness.class);
//                                example.createCriteria().andEqualTo("client", s.getClientId()).andEqualTo("proSite", s.getProSite()).andEqualTo("proSelfCode", productMatch.getProCode());
//                                GaiaProductBusiness gaiaProductBusiness = this.productBusinessMapper.selectOneByExample(example);
//                                if (ObjectUtil.isNotEmpty(gaiaProductBusiness)) {
//                                    throw new BusinessException("???????????????");
//                                } else {
//                                    this.saveProBusinissInfo(productMatch, s);
//                                }
//                                GaiaProductBusiness productBusiness = this.saveProBusinissInfo(productMatch, s);
//                                productBusinessList.add(productBusiness);
//                            }
                            productMatch.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                            productMatch.setMatchCreateTime(CommonUtil.getHHmmss());
                            log.info("?????????" + productMatch.getProBarcode().length());
                            list.add(productMatch);
                        } else {
                            throw new BusinessException("????????????????????????");
                        }
                        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
                            //?????????????????????????????????
                            productMatchMapper.batchAddProductMatch(list);
                        }
                    }
                }
//                if (ObjectUtil.isNotEmpty(productBusinessList) && productBusinessList.size() > 0) {
//                    productBusinessMapper.batchInsertProduct(productBusinessList);
//                }
                result.setCode("0");
                result.setMessage("???????????????");
            }
            in.close();
            wk.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Integer> ratioCount(MatchProductInData inData) {
        Map<String, Integer> result = new HashMap<>();
        int unratioCount = productMatchMapper.selectUnratioCount(inData.getClientId(), inData.getMatchBatch());
        int allRatioCount = productMatchMapper.selectAllRatioCount(inData.getClientId(), inData.getMatchBatch());
        result.put("unratioCount", unratioCount);
        result.put("allRatioCount", allRatioCount);
        return result;
    }

    @Override
    public List<MatchProductOutData> selectProMatchList(MatchProductInData inData) {
        // ???????????? 0-????????????1-???????????????2-????????????
        if (StringUtils.equals(inData.getMatchType(), "1")) {
            if (StringUtils.isNotEmpty(inData.getMinMatchDegree())) {
                if (Integer.valueOf(inData.getMinMatchDegree()) <= 0) {
                    throw new BusinessException("???????????????????????????");
                }
            }
            if (StringUtils.isNotEmpty(inData.getMaxMatchDegree())) {
                if (Integer.valueOf(inData.getMaxMatchDegree()) >= 100) {
                    throw new BusinessException("???????????????????????????");
                }
            }

        } else {
            inData.setMinMatchDegree("");
            inData.setMaxMatchDegree("");
        }
        List<MatchProductOutData> list = new ArrayList<>();
        Map<String, MatchProductOutData> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(inData.getStoCodes())) {
            inData.setType("1");
            List<MatchProductOutData> stoGroupList = productMatchMapper.selectProMatchStoGroup(inData);
            map = stoGroupList.stream().collect(Collectors.toMap(o -> o.getClientId() + "-" + o.getMatchBatch() + "-" + o.getProCode(), x -> x, (a, pcb) -> pcb));
        } else {
            inData.setType("0");
        }
        int count = productMatchMapper.selectProMatchCount(inData);
        if (StringUtils.isNotEmpty(inData.getDecimationRatio())) {
            int pageNum = 0;
            BigDecimal a = new BigDecimal(count).multiply(new BigDecimal(inData.getDecimationRatio()).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP)).setScale(0, BigDecimal.ROUND_HALF_UP);
            pageNum = a.intValue();
            inData.setPageNum(pageNum);
        }
        //????????????????????????
//        if (StringUtils.isEmpty(inData.getMatchStatus())){
//            inData.setSetSelect("1");
//        }
        list = productMatchMapper.selectProMatchList(inData);
        if ("1".equals(inData.getType())) {
            for (MatchProductOutData a : list) {
                String str = a.getClientId() + "-" + a.getMatchBatch() + "-" + a.getProCode();
                MatchProductOutData b = map.get(str);
                a.setStoCode(b.getStoCode());
                a.setStoName(b.getStoName());
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sureProduct(ProductSureInData inData) {
        List<ProductSureMsg> list = new ArrayList<>();
//        if (ObjectUtil.isNotEmpty(inData.getStoCodes())) {
        for (Map<String, String> m : inData.getItemList()) {
            ProductSureMsg msg = new ProductSureMsg();
            msg.setClientId(inData.getClientId());
            msg.setMatchBatch(m.get("matchBatch"));
            msg.setProCode(m.get("proCodeArr").split("-")[0]);
            msg.setProCodeG(m.get("proCodeArr").split("-")[1]);
            msg.setUpdateClient(inData.getUpdateClient());
            msg.setUpdateUser(inData.getUpdateUser());
            msg.setUpdateTime(inData.getUpdateTime());
//                //??????????????????????????????
//                Example example1 = new Example(GaiaProductBasic.class);
//                example1.createCriteria().andEqualTo("proCode", msg.getProCodeG());
//                GaiaProductBasic productBasic = this.productBasicMapper.selectOneByExample(example1);
//                //???????????????????????????
//                GaiaProductBusiness productBusiness = new GaiaProductBusiness();
//                BeanUtils.copyProperties(productBasic, productBusiness);
//                Example example = new Example(GaiaProductBusiness.class);
//                example.createCriteria().andEqualTo("proCode", msg.getProCode()).andEqualTo("proSite", msg.getStoCode()).andEqualTo("client", msg.getClientId());
//                productBusinessMapper.updateByExampleSelective(productBusiness, example);
            list.add(msg);
        }
        if (ObjectUtil.isNotEmpty(list)) {
            productMatchMapper.sureProduct(list);
        }
        //??????????????????????????????

//        } else {
//            for (Map<String, String> m : inData.getItemList()) {
//                ProductSureMsg msg = new ProductSureMsg();
//                msg.setClientId(inData.getClientId());
//                msg.setStoCode("");
//                msg.setProCode(m.get("proCodeArr").split("-")[0]);
//                msg.setProCodeG(m.get("proCodeArr").split("-")[1]);
//                list.add(msg);
////                Example example1 = new Example(GaiaProductBasic.class);
////                example1.createCriteria().andEqualTo("proCode", msg.getProCodeG());
////                GaiaProductBasic productBasic = this.productBasicMapper.selectOneByExample(example1);
////                List<StoreInfo> storeList = storeDataMapper.selectStoreAndDcCodes(inData.getClientId());
////                if (ObjectUtil.isNotEmpty(storeList) && storeList.size() > 0) {
////                    for (StoreInfo s : storeList) {
////                        GaiaProductBusiness productBusiness = new GaiaProductBusiness();
////                        BeanUtils.copyProperties(productBasic, productBusiness);
////                        Example example = new Example(GaiaProductBusiness.class);
////                        example.createCriteria().andEqualTo("proCode", msg.getProCode()).andEqualTo("proSite", s.getProSite()).andEqualTo("client", s.getClientId());
////                        productBusinessMapper.updateByExampleSelective(productBusiness, example);
////                    }
////                }
//            }
//            if (ObjectUtil.isNotEmpty(list)) {
//                productMatchMapper.sureProduct(list);
//            }
//        }
    }

    @Override
    public void modifyProBasicInfo(ProBasicInfo inData) {
        //???????????????99999999???,????????????????????????????????????????????????GAIA_PRODUCT_BASIC??????
//        if(ObjectUtil.isNotEmpty(inData.getStoCodes())){
//            inData.setIsClient("Y");
//        }
        //??????????????????????????????????????????????????????
        productMatchMapper.modifyMatchClass(inData);
        if (!"99999999".equals(inData.getProCodeG())) {
            productMatchMapper.modifyProBasicInfo(inData);
        }
    }

    @Override
    public List<ProductClass> productClassList() {
        return productMatchMapper.selectProductClassList();
    }

    @Override
    public List<ProductCompClass> productComponentList() {
        return productMatchMapper.selectProductComponentList();
    }

    @Override
    public List<Map<String, String>> getStoListByClient(StoInData inData) {
        return productMatchMapper.getStoListByClient(inData.getClientId(), inData.getStoName());
    }

    @Override
    public List<ProductMatchOutData> otherProductList(MatchProductInData inData) {
        if (ObjectUtil.isNotEmpty(inData.getStoCode())) {
            inData.setType("1");
        } else {
            inData.setType("0");
        }
        GaiaProductMatch productMatch = productMatchMapper.selectProductMatch(inData);
        List<ProductInfo> productInfoList = productMatchMapper.selectProductBasicList(productMatch);
        List<ProductMatchOutData> list = ProductMatchUtils.execMatchProductList(productMatch, productInfoList);
        return list;
    }

    @Override
    public MatchSchedule productMatchSchedule(MatchProductInData inData) {
        if (ObjectUtil.isNotEmpty(inData.getStoCodes()) && inData.getStoCodes().length > 0) {
            inData.setType("1");
        } else {
            inData.setType("0");
        }
        return productMatchMapper.productMatchSchedule(inData);
    }

    @Override
    public List<PictureMessage> imageQuery(MatchProductInData inData) {
        String wmClient = "0";
        List<String> stoCode = new ArrayList<>();
        if (StringUtils.isEmpty(inData.getStoCode())) {
            inData.setStoCode("");
            wmClient = "1";
        } else {
            String[] stoCodeArr = inData.getStoCode().split(",");
            stoCode = Arrays.asList(stoCodeArr);
        }
        return productMatchMapper.imageQuery(inData.getClientId(), stoCode, inData.getProCode(), wmClient);
    }

    public GaiaProductBusiness saveProBusinissInfo(GaiaProductMatch productMatch, StoreInfo s) {
        Example example1 = new Example(GaiaProductBasic.class);
        example1.createCriteria().andEqualTo("proCode", productMatch.getMatchProCode());
        GaiaProductBasic productBasic = this.productBasicMapper.selectOneByExample(example1);
        GaiaProductBusiness productBusiness = new GaiaProductBusiness();
        BeanUtils.copyProperties(productBasic, productBusiness);
        productBusiness.setClient(s.getClientId());
        productBusiness.setProSite(s.getProSite());
        productBusiness.setProSelfCode(productMatch.getProCode());
        productBusiness.setProName(productMatch.getProName());
//        productBusiness.setProSpecs(productMatch.getProSpecs());
//        productBusiness.setProRegisterNo(productMatch.getProRegisterNo());
//        productBusiness.setProBarcode(productMatch.getProBarcode());
//        productBusiness.setProSite(productMatch.getStoCode());
        if (StringUtils.isNotEmpty(productBasic.getProStorageCondition())) {
            productBusiness.setProStorageCondition(productBasic.getProStorageCondition());
        } else {
            productBusiness.setProStorageCondition("1");
        }
        if (StringUtils.isNotEmpty(productBasic.getProStorageArea())) {
            productBusiness.setProStorageArea(productBasic.getProStorageArea());
        } else {
            productBusiness.setProStorageArea("14");
        }
//        productBusinessMapper.insert(productBusiness);
        return productBusiness;
    }

    @Override
    public Result exportData(ProductSureInData inData) {
        List<ProductSureMsg> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(inData.getStoCodes())) {
            for (Map<String, String> m : inData.getItemList()) {
                ProductSureMsg msg = new ProductSureMsg();
                msg.setClientId(inData.getClientId());
                msg.setStoCode(m.get("stoCode"));
                if (m.get("proCodeArr").split("-").length > 1) {
                    msg.setProCode(m.get("proCodeArr").split("-")[0]);
                    msg.setProCodeG(m.get("proCodeArr").split("-")[1]);
                } else {
                    msg.setProCode(m.get("proCodeArr").split("-")[0]);
                    msg.setProCodeG("");
                }
                list.add(msg);
            }
        } else {
            for (Map<String, String> m : inData.getItemList()) {
                ProductSureMsg msg = new ProductSureMsg();
                msg.setClientId(inData.getClientId());
                msg.setStoCode("");
                if (m.get("proCodeArr").split("-").length > 1) {
                    msg.setProCode(m.get("proCodeArr").split("-")[0]);
                    msg.setProCodeG(m.get("proCodeArr").split("-")[1]);
                } else {
                    msg.setProCode(m.get("proCodeArr").split("-")[0]);
                    msg.setProCodeG("");
                }
                list.add(msg);
            }
        }
        //??????????????????
        List<List<Object>> dataList = new ArrayList<>();
        Integer index = 1;
        for (ProductSureMsg msg : list) {
            List<Object> line = new ArrayList<>();
//            //??????
//            line.add(index);
//            index ++;
            //??????????????????
            line.add(msg.getProCode());
            //??????????????????
            line.add(msg.getProCodeG());

            dataList.add(line);
        }
        String[] header = {"??????????????????", "????????????"};
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = ExcelUtils.exportExcel3(
                new ArrayList<String[]>() {{
                    add(header);
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(dataList);
                }},
                new ArrayList<String>() {{
                    add("????????????????????????");
                }});
        Result uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = inData.getClientId() + "-" + CommonUtil.getyyyyMMdd() + ".xlsx";
            uploadResult = cosUtils.uploadFile(bos, fileName);
            bos.flush();
        } catch (IOException e) {
            log.error("??????????????????:", e);
            throw new BusinessException("?????????????????????");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("???????????????:", e);
                throw new BusinessException("??????????????????");
            }
        }
        return uploadResult;
    }

    @Override
    public GaiaProductBasic queryProductBasicById(MatchProductInData inData) {
        return productBasicMapper.queryProductBasicById(inData.getProCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProductBasic(ProductBasicInData inData) {
        GaiaProductBasic productBasic = new GaiaProductBasic();
//        GaiaProductBusiness productBusiness = new GaiaProductBusiness();
        BeanUtils.copyProperties(inData, productBasic);
//        if (StringUtils.isNotEmpty(inData.getMatchProCode())) {
//
//        }
//        BeanUtils.copyProperties(inData, productBusiness);
        //????????????
        if (StringUtils.isNotEmpty(inData.getProName())) {
            productBasic.setProDepict(inData.getProCommonname() + "(" + inData.getProName() + ")");
//            productBusiness.setProDepict(inData.getProCommonname() + "(" + inData.getProName() + ")");
        } else {
            productBasic.setProDepict(inData.getProCommonname());
//            productBusiness.setProDepict(inData.getProCommonname());
        }
        String pym = productMatchMapper.selectProPym(productBasic.getProDepict());
        productBasic.setProPym(pym);
//        productBusiness.setProPym(pym);
        //???????????????????????????
        if (StringUtils.isNotEmpty(productBasic.getProCode())) {
            productBasicMapper.modifyProductBasicById(productBasic);
        } else {//??????????????????
            String proCode = productBasicMapper.selectMaxProcode();
            productBasic.setProCode(proCode);
            productBasicMapper.insert(productBasic);
        }
        //????????????????????????
        ProductSureMsg msg = new ProductSureMsg();
        msg.setProCodeG(productBasic.getProCode());
        msg.setProCode(inData.getMatchProCode());
        msg.setClientId(inData.getClientId());
        msg.setUpdateClient(inData.getUpdateClient());
        msg.setUpdateUser(inData.getUpdateUser());
        msg.setUpdateTime(inData.getUpdateTime());

        /** add by jinwencheng on 2022-01-07 17:57:38 ??????matchCode?????? **/
        msg.setMatchCode(inData.getMatchCode());
        /** add end **/

        if (StringUtils.isNotEmpty(inData.getStoCode())) {
            msg.setStoCode(inData.getStoCode());
            //?????????????????????????????????
//            productBusiness.setClient(inData.getClientId());
//            productBusiness.setProSite(inData.getStoCode());
//            productBusiness.setProSelfCode(inData.getMatchProCode());
//            Example example = new Example(GaiaProductBusiness.class);
//            example.createCriteria().andEqualTo("proCode", inData.getProCode()).andEqualTo("proSite", productBusiness.getProSite()).andEqualTo("client", productBusiness.getClient());
//            productBusinessMapper.updateByExampleSelective(productBusiness, example);
            //??????????????????
            productMatchMapper.updateTpCollect(inData.getClientId(), inData.getStoCode(), inData.getProCode(), inData.getMatchProCode(), "0");
        } else {
//            List<StoreInfo> storeList = storeDataMapper.selectStoreAndDcCodes(inData.getClientId());
//            productBusiness.setClient(inData.getClientId());
//            productBusiness.setProSelfCode(inData.getMatchProCode());
//            if (ObjectUtil.isNotEmpty(storeList) && storeList.size() > 0) {
//                for (int i = 0; i < storeList.size(); i++) {
//                    productBusiness.setProSite(storeList.get(i).getProSite());
//                    Example example = new Example(GaiaProductBusiness.class);
//                    example.createCriteria().andEqualTo("proCode", inData.getProCode()).andEqualTo("proSite", productBusiness.getProSite()).andEqualTo("client", productBusiness.getClient());
//                    productBusinessMapper.updateByExampleSelective(productBusiness, example);
//                }
//            }
            productMatchMapper.updateTpCollect(inData.getClientId(), "", inData.getProCode(), inData.getMatchProCode(), "1");
        }
        productMatchMapper.updateProductMatch(msg);
    }

    @Override
    public List<Map<String, String>> selectClientList(StoInData inData) {
        return storeDataMapper.selectClientList(inData.getContent());
    }

    @Override
    public Result importKuCun(HttpServletRequest request, MultipartFile file, GetLoginOutData userInfo) {
        Result result = new Result();
        try {
            String rootPath = request.getSession().getServletContext().getRealPath("/");
            String fileName = file.getOriginalFilename();
            String path = rootPath + fileName;
            // ???????????????
            String url = cosUtils.urlAuth(path);
            String type = url.substring(url.lastIndexOf(".") + 1, url.indexOf("?"));
            InputStream in = file.getInputStream();
            Workbook wk = null;
            if ("xls".equals(type)) {
                //???????????????
                wk = new HSSFWorkbook(in);
            } else if ("xlsx".equals(type)) {
                wk = new XSSFWorkbook(in);
            } else {
                log.info("???excel??????");
                throw new BusinessException("??????????????????");
            }

            //??????????????????Excel???????????????????????????????????????
            //???????????????Sheet???
            Sheet sheet = wk.getSheetAt(0);
            //?????????
            ImportProductKuCunInfo model = new ImportProductKuCunInfo();
            List<ImportProductKuCunInfo> importProductKuCunInfoList = ExcelManage.readFromExcelExtends(sheet, model, constractImportKuCunTitle());
            if (ObjectUtil.isNotEmpty(importProductKuCunInfoList) && importProductKuCunInfoList.size() > 0) {
                //????????????????????????
                String client = importProductKuCunInfoList.get(0).getClient();
                Example example = new Example(ProductCheckKucunImport.class);
                Example.Criteria criteria = example.createCriteria()
                        .andEqualTo("client", client);
                productCheckKucunImportMapper.deleteByExample(example);
                //?????????????????????????????????????????????
                List<ProductCheckKucunImport> list = new ArrayList<>();
                for (ImportProductKuCunInfo info : importProductKuCunInfoList) {
                    ProductCheckKucunImport db = new ProductCheckKucunImport();
                    BeanUtils.copyProperties(info, db);
                    db.setStoCode(info.getSite());
                    db.setStoName(info.getSiteName());
                    list.add(db);
                }

                //????????????????????????????????????????????????????????????????????????????????????????????????
                Map<String, ProCodeKuCunInfo> proCodeKuCunInfoMap = makeProductCodeExtendsInfo(list);
                if (CollectionUtil.isNotEmpty(proCodeKuCunInfoMap)) {
                    Map<String, ProCodeKuCunInfo> proCodeKuCunInfoMapFinal = new HashMap<>();
                    //??????????????????????????????????????????????????????
                    List<StoreInfo> storeInfos = storeDataMapper.selectStores(client);
                    if (CollectionUtil.isNotEmpty(storeInfos)) {
                        for (String stoCode : proCodeKuCunInfoMap.keySet()) {
                            for (StoreInfo storeInfo : storeInfos) {
                                if (storeInfo.getProSite().equals(proCodeKuCunInfoMap.get(stoCode).getMaxNumSite())) {
                                    proCodeKuCunInfoMapFinal.put(stoCode, proCodeKuCunInfoMap.get(stoCode));
                                }
                            }
                        }
                    }
                    String key = "ProImport" + "_" + client;
                    redisManager.set(key, new Gson().toJson(proCodeKuCunInfoMapFinal), 10 * 24 * 60 * 60L);
                }
                int insertCount = 1;
                int insertPerSize = 2000;
                if (CollectionUtil.isNotEmpty(list)) {
                    if (insertPerSize >= list.size()) {
                        List<ProductCheckKucunImport> perDbList = new ArrayList<>();
                        list.forEach(x -> {
                            perDbList.add(x);
                        });
                        productCheckKucunImportMapper.insertList(perDbList);
                    } else {
                        List<ProductCheckKucunImport> perDbList = new ArrayList<>();
                        while (insertCount * insertPerSize <= list.size()) {
                            perDbList.clear();
                            for (int i = (insertCount - 1) * insertPerSize; i < insertCount * insertPerSize; i++) {
                                if (StrUtil.isBlank(list.get(i).getClient())) {
                                    System.out.println(list.get(i));
                                }
                                perDbList.add(list.get(i));
                            }
                            insertCount++;
                            productCheckKucunImportMapper.insertList(perDbList);
                        }
                        List<ProductCheckKucunImport> leftDbList = new ArrayList<>();
                        if ((insertCount - 1) * insertPerSize < list.size() && insertCount * insertPerSize > list.size()) {
                            for (int i = (insertCount - 1) * insertPerSize; i < list.size(); i++) {
                                leftDbList.add(list.get(i));
                            }
                            productCheckKucunImportMapper.insertList(leftDbList);
                        }
                    }


                }
                result.setCode("0");
                result.setMessage("???????????????");
            }
            in.close();
            wk.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Object> selectMergeProductList(MatchProductInData inData) {
        Map<String, Object> result = new HashMap<>();
        LinkedHashMap<String, String> t = new LinkedHashMap<>();
        t.put("matchProCode", "??????????????????");
        t.put("proCommonName", "??????????????????");
        t.put("proSpecs", "??????");
        t.put("factoryName", "????????????");
        t.put("proBarcode", "????????????1");
        t.put("proBarcode2", "????????????2");
        t.put("proRegisterNo", "????????????");
        t.put("proClassName", "????????????");
        t.put("proCompClassName", "????????????");
        List<LinkedHashMap<String, String>> proInfoList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(inData.getStoCodes()) && inData.getStoCodes().length > 0) {
            inData.setType("1");
            List<Map<String, String>> sortProduct = productMatchMapper.selectProCodeByStore(inData.getClientId(), inData.getStoCodes());
            proInfoList = productMatchMapper.selectMatchProCode(inData);
            for (int k = 0; k < proInfoList.size(); k++) {
                Map<String, String> item = proInfoList.get(k);
                for (int i = 0; i < sortProduct.size(); i++) {
                    String proCodes = sortProduct.get(i).get("num");
                    String[] proArr = proCodes.split(";");
                    if (ObjectUtil.isNotEmpty(proArr) && proArr.length > 0) {
                        for (int j = 0; j < proArr.length; j++) {
                            String keyName = sortProduct.get(i).get("stoCode") + "-" + (j + 1);
                            t.put(keyName, sortProduct.get(i).get("stoCode") + sortProduct.get(i).get("stoName") + "????????????" + (j + 1));
                            if (sortProduct.get(i).get("ydProCode").equals(item.get("matchProCode"))) {
                                log.info(keyName + "-" + proArr[j]);
                                item.put(keyName, proArr[j]);
                            }
                        }
                    }
                }
            }
        } else {
            inData.setType("0");
            List<Map<String, String>> sortProduct = productMatchMapper.selectProCodeByClient(inData.getClientId());
            proInfoList = productMatchMapper.selectMatchProCode(inData);
            for (int k = 0; k < proInfoList.size(); k++) {
                Map<String, String> item = proInfoList.get(k);
                for (int i = 0; i < sortProduct.size(); i++) {
                    String proCodes = sortProduct.get(i).get("num");
                    String[] proArr = proCodes.split(";");
                    if (ObjectUtil.isNotEmpty(proArr) && proArr.length > 0) {
                        for (int j = 0; j < proArr.length; j++) {
                            String keyName = sortProduct.get(i).get("clientId") + "-" + (j + 1);
                            t.put(keyName, sortProduct.get(i).get("clientId") + "????????????" + (j + 1));
                            if (sortProduct.get(i).get("ydProCode").equals(item.get("matchProCode"))) {
                                log.info(keyName + "-" + proArr[j]);
                                item.put(keyName, proArr[j]);
                            }
                        }
                    }
                }
            }
        }
        LinkedList<Map<String, String>> title = new LinkedList<>();
        for (String k : t.keySet()) {
            Map<String, String> item = new HashMap<>();
            item.put("title", k);
            item.put("titleName", t.get(k));
            title.add(item);
        }
        result.put("title", title);
        result.put("item", proInfoList);
        return result;
    }

    @Override
    public List<LinkedHashMap<String, String>> selectNoMergeProductList(MatchProductInData inData) {
        if (ObjectUtil.isNotEmpty(inData.getStoCodes())) {
            inData.setType("1");
        } else {
            inData.setType("0");
        }
        return productMatchMapper.selectNoMatchProCode(inData);
    }

    @Override
    public Result exportMergeProductList(MatchProductInData inData) {
        Result result = null;
        Map<String, String> t = new LinkedHashMap<>();
        t.put("matchProCode", "??????????????????");
        t.put("proCommonName", "??????????????????");
        t.put("proSpecs", "??????");
        t.put("factoryName", "????????????");
        t.put("proBarcode", "????????????1");
        t.put("proBarcode2", "????????????2");
        t.put("proRegisterNo", "????????????");
        t.put("proClassName", "????????????");
        t.put("proCompClassName", "????????????");
        List<LinkedHashMap<String, String>> proInfoList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(inData.getStoCodes()) && inData.getStoCodes().length > 0) {
            inData.setType("1");
            List<Map<String, String>> sortProduct = productMatchMapper.selectProCodeByStore(inData.getClientId(), inData.getStoCodes());
            proInfoList = productMatchMapper.selectMatchProCode(inData);
            for (int k = 0; k < proInfoList.size(); k++) {
                Map<String, String> item = proInfoList.get(k);
                for (int i = 0; i < sortProduct.size(); i++) {
                    String proCodes = sortProduct.get(i).get("num");
                    String[] proArr = proCodes.split(";");
                    if (ObjectUtil.isNotEmpty(proArr) && proArr.length > 0) {
                        for (int j = 0; j < proArr.length; j++) {
                            String keyName = sortProduct.get(i).get("stoCode") + "-" + (j + 1);
                            t.put(keyName, sortProduct.get(i).get("stoCode") + sortProduct.get(i).get("stoName") + "????????????" + (j + 1));
                            if (sortProduct.get(i).get("ydProCode").equals(item.get("matchProCode"))) {
                                log.info(keyName + "-" + proArr[j]);
                                item.put(keyName, proArr[j]);
                            }
                        }
                    }
                }
            }
        } else {
            inData.setType("0");
            List<Map<String, String>> sortProduct = productMatchMapper.selectProCodeByClient(inData.getClientId());
            proInfoList = productMatchMapper.selectMatchProCode(inData);
            for (int k = 0; k < proInfoList.size(); k++) {
                Map<String, String> item = proInfoList.get(k);
                for (int i = 0; i < sortProduct.size(); i++) {
                    String proCodes = sortProduct.get(i).get("num");
                    String[] proArr = proCodes.split(";");
                    if (ObjectUtil.isNotEmpty(proArr) && proArr.length > 0) {
                        for (int j = 0; j < proArr.length; j++) {
                            String keyName = sortProduct.get(i).get("clientId") + "-" + (j + 1);
                            t.put(keyName, sortProduct.get(i).get("clientId") + "????????????" + (j + 1));
                            if (sortProduct.get(i).get("ydProCode").equals(item.get("matchProCode"))) {
                                log.info(keyName + "-" + proArr[j]);
                                item.put(keyName, proArr[j]);
                            }
                        }
                    }
                }
            }
        }
        for (String k : t.keySet()) {
            for (Map<String, String> p : proInfoList) {
                boolean a = true;
                if (p.containsKey(k)) {
                    a = false;
                }
                if (a) {
                    p.put(k, "");
                }
            }
        }
        CsvFileInfo csvInfo = CsvClient.getCsvByteByMap(proInfoList, "???????????????????????????", t);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
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
        } catch (IOException e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    public Result exportNoMergeProductList(MatchProductInData inData) {
        Result result = null;
        Map<String, String> t = new LinkedHashMap<>();
        t.put("matchProCode", "??????????????????");
        t.put("proCommonName", "??????????????????");
        t.put("proSpecs", "??????");
        t.put("factoryName", "????????????");
        t.put("proBarcode", "????????????1");
        t.put("proBarcode2", "????????????2");
        t.put("proRegisterNo", "????????????");
        t.put("proClassName", "????????????");
        t.put("proCompClassName", "????????????");
        t.put("proCode", "??????????????????");
        if (ObjectUtil.isNotEmpty(inData.getStoCodes())) {
            inData.setType("1");
            t.put("stoName", "??????");
        } else {
            inData.setType("0");
        }
        List<LinkedHashMap<String, String>> proInfo = productMatchMapper.selectNoMatchProCode(inData);
        CsvFileInfo csvInfo = CsvClient.getCsvByteByMap(proInfo, "???????????????????????????", t);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
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
        } catch (IOException e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    public String selectNextMatchBatch(String clientId) {
        return productMatchMapper.selectNextMatchBatch(clientId);
    }

    @Override
    public String selectStoreByCode(String clientId, String stoCode) {
        return productMatchMapper.selectStoreByCode(clientId, stoCode);
    }

    @Override
    public String selectNextCode(String clientId) {
        return productMatchMapper.selectNextCode(clientId);
    }

    @Override
    public void addProMatchRecord(ProductMatchZ productMatchZ) {
        productMatchMapper.addProMatchRecord(productMatchZ);
    }

    @Override
    public String selectMatchBatch(String clientId, String matchCode) {
        return productMatchMapper.selectMatchBatch(clientId, matchCode);
    }

    @Override
    public void updateProMatchRecord(ProductMatchZ productMatchZ) {
        productMatchMapper.updateProMatchRecord(productMatchZ);
    }

    @Override
    public void batchAddProductMatch(List<GaiaProductMatch> list) {
        productMatchMapper.batchAddProductMatch(list);
    }

    @Override
    public List<ProductInfo> selectProductBasicList(GaiaProductMatch inData) {
        return productMatchMapper.selectProductBasicList(inData);
    }

    @Override
    public void addProductMatch(GaiaProductMatch inData) {
        productMatchMapper.addProductMatch(inData);
    }

    @Override
    public String selectClientByCode(String clientId) {
        return productMatchMapper.selectClientByCode(clientId);
    }

    @Override
    public List<StoreInfo> selectStoreAndDcCodes(String client) {
        return storeDataMapper.selectStoreAndDcCodes(client);
    }


    //?????????????????????Map
    public static Map<String, String> constractImportKuCunTitle() {
        Map<String, String> result = new HashMap<>();
        result.put("?????????ID", "client");
        result.put("??????", "site");
        result.put("??????", "siteName");
        result.put("????????????", "proCode");
        result.put("???????????????", "proBarcode");
        result.put("????????????", "proName");
        result.put("??????", "kuCunNum");
        return result;
    }


    private Map<String, ProCodeKuCunInfo> makeProductCodeExtendsInfo(List<ProductCheckKucunImport> productCheckKucunImports) {
        Map<String, ProCodeKuCunInfo> resMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(productCheckKucunImports)) {
            for (ProductCheckKucunImport kucunImport : productCheckKucunImports) {
                String proCode = kucunImport.getProCode();
                if (resMap.get(proCode) == null) {
                    ProCodeKuCunInfo proCodeKuCunInfo = new ProCodeKuCunInfo();
                    List<String> sites = new ArrayList<>();
                    sites.add(kucunImport.getStoCode());
                    proCodeKuCunInfo.setProMaxNum(StrUtil.isBlank(kucunImport.getKuCunNum()) ? 0 : Integer.parseInt(kucunImport.getKuCunNum()));
                    proCodeKuCunInfo.setMaxNumSite(kucunImport.getStoCode());
                    proCodeKuCunInfo.setProCoce(proCode);
                    proCodeKuCunInfo.setSites(sites);
                    resMap.put(kucunImport.getProCode(), proCodeKuCunInfo);
                } else {
                    ProCodeKuCunInfo proCodeKuCunInfo = resMap.get(proCode);
                    List<String> sites = proCodeKuCunInfo.getSites();
                    if (!sites.contains(kucunImport.getStoCode())) {
                        sites.add(kucunImport.getStoCode());
                        proCodeKuCunInfo.setSites(sites);
                    }
                    Integer importNum = StrUtil.isBlank(kucunImport.getKuCunNum()) ? 0 : Integer.parseInt(kucunImport.getKuCunNum());
                    if (importNum > proCodeKuCunInfo.getProMaxNum()) {
                        proCodeKuCunInfo.setProMaxNum(importNum);
                        proCodeKuCunInfo.setMaxNumSite(kucunImport.getStoCode());
                    }
                    proCodeKuCunInfo.setProCoce(proCode);
                    resMap.put(kucunImport.getProCode(), proCodeKuCunInfo);
                }

            }
        }
        return resMap;
    }

    @Override
    public void confirmProductBasic(ConfirmProductBasicDTO confirmProductBasicDTO) {
        //????????????????????????
        ProductSureMsg msg = new ProductSureMsg();
        msg.setProCodeG(confirmProductBasicDTO.getProCode());
        msg.setProCode(confirmProductBasicDTO.getMatchProCode());
        msg.setClientId(confirmProductBasicDTO.getClientId());
        msg.setMatchCode(confirmProductBasicDTO.getMatchCode());
        msg.setUpdateClient(confirmProductBasicDTO.getUpdateClient());
        msg.setUpdateUser(confirmProductBasicDTO.getUpdateUser());
        msg.setUpdateTime(confirmProductBasicDTO.getUpdateTime());
        if (StringUtils.isNotEmpty(confirmProductBasicDTO.getStoCode())) {
            String[] stoCodes = confirmProductBasicDTO.getStoCode().split(",");
            for (int i = 0; i < stoCodes.length; i ++) {
                msg.setStoCode(stoCodes[i]);
                productMatchMapper.updateProductMatch(msg);
            }
        } else {
            msg.setStoCode(confirmProductBasicDTO.getStoCode());
            productMatchMapper.updateProductMatch(msg);
        }
    }

    @Data
    class ProCodeKuCunInfo {
        private String proCoce;

        private List<String> sites;

        private String maxNumSite;

        private Integer proMaxNum;
    }

}

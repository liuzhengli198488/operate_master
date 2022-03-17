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
            throw new BusinessException("商品主数据校验负责人电话不能为空");
        }
        if (!PhoneFormatCheckUtils.isPhoneLegal(tel)) {
            throw new BusinessException("请输入合法的手机号");
        }

        Result result = new Result();
        try {
            String rootPath = request.getSession().getServletContext().getRealPath("/");
            String fileName = file.getOriginalFilename();
            String path = rootPath + fileName;
//            File newFile = new File(path);
//            //判断文件是否存在
//            if(!newFile.exists()) {
//                newFile.mkdirs();//不存在的话，就开辟一个空间
//            }
//            //将上传的文件存储
//            file.transferTo(newFile);
            // 文件全路径
            String url = cosUtils.urlAuth(path);
            String type = url.substring(url.lastIndexOf(".") + 1, url.indexOf("?"));
            InputStream in = file.getInputStream();
            Workbook wk = null;
            if ("xls".equals(type)) {
                //文件流对象
                wk = new HSSFWorkbook(in);
            } else if ("xlsx".equals(type)) {
                wk = new XSSFWorkbook(in);
            } else {
                log.info("非excel文件");
                throw new BusinessException("文件格式有误");
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
//                    throw new BusinessException("已存在相关门店数据,请重新选择门店");
//                }
//            }else {
//
//            }
//            if (fileName.indexOf("xlsx") > 0){
            //导入已存在的Excel文件，获得只读的工作薄对象
            //获取第一张Sheet表
            Sheet sheet = wk.getSheetAt(0);
            //取数据
            ImportProductInfo model = new ImportProductInfo();
            List<ImportProductInfo> importProductInfoList = ExcelManage.readFromExcel(sheet, model);
            //导入批次查询
            String matchBatch = productMatchMapper.selectNextMatchBatch(clientId);
            if (ObjectUtil.isNotEmpty(importProductInfoList) && importProductInfoList.size() > 0) {
                //商品匹配并新增到商品匹配明细表
                List<GaiaProductMatch> list = new ArrayList<>();
//                List<GaiaProductBusiness> productBusinessList = new ArrayList<>();
                //判断入参是否有门店
                if (ObjectUtil.isNotEmpty(stoCodes)) {
                    for (int i = 0; i < stoCodes.length; i++) {
                        String matchCode = productMatchMapper.selectStoreByCode(clientId, stoCodes[i]);
                        //添加记录到主表中
                        ProductMatchZ productMatchZ = new ProductMatchZ();
                        productMatchZ.setClient(clientId);
                        productMatchZ.setStoCode(stoCodes[i]);
                        productMatchZ.setMatchType("1");
                        productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                        productMatchZ.setMatchCreater(userInfo.getUserId());
                        if (stoCodes.length == 1) {
                            productMatchZ.setMatchDataCheckTouchTel(tel);
                            productMatchZ.setMatchDataCheckFlag("3");//初始化时初始化为3表示只是导入了数据，核验人员无任何操作
                        }
                        if (StringUtils.isEmpty(matchCode)) {//新增匹配数据
                            matchCode = productMatchMapper.selectNextCode(clientId);
                            productMatchZ.setMatchCode(matchCode);
                            productMatchZ.setMatchBatch(matchBatch);
                            productMatchMapper.addProMatchRecord(productMatchZ);
                        } else {//修改匹配数据
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
//                            List<ProductInfo> productInfoList = productMatchMapper.selectProductBasicList(productMatch);//需优化
                            productMatch.setClient(clientId);
                            productMatch.setStoCode(stoCodes[i]);
                            productMatch.setMatchCode(matchCode);
                            productMatch.setMatchBatch(productMatchZ.getMatchBatch());
//                            productMatch.setSite(clientId + "-" + stoCodes[i]);
                            //获取商品匹配度
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
                            //添加数据到商品主数据中
//                                StoreInfo s = new StoreInfo();
//                                s.setClientId(clientId);
//                                s.setProSite(stoCodes[i]);
//                                GaiaProductBusiness productBusiness = this.saveProBusinissInfo(productMatch, s);
//                                productBusinessList.add(productBusiness);
                        }
                        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
                            //门店批量新增商品明细表
                            productMatchMapper.batchAddProductMatch(list);
                        }
                    }
//                    //数据补充入加盟商记录下
//                    if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
//                        String matchCode = productMatchMapper.selectClientByCode(clientId);
//                        ProductMatchZ productMatchZ = new ProductMatchZ();
//                        productMatchZ.setClient(clientId);
//                        productMatchZ.setMatchCode(matchCode);
//                        productMatchZ.setStoCode("");
//                        productMatchZ.setMatchType("0");
//                        productMatchZ.setMatchDataCheckTouchTel(tel);
//                        productMatchZ.setMatchDataCheckFlag("3");//初始化时初始化为3表示只是导入了数据，核验人员无任何操作
//                        productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
//                        productMatchZ.setMatchCreater(userInfo.getUserId());
//                        if (StringUtils.isNotEmpty(matchCode)) {//修改主表记录
//                            productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
//                            productMatchZ.setMatchUpdater(userInfo.getUserId());
//                            productMatchZ.setMatchCode(matchCode);
//                            productMatchMapper.updateProMatchRecord(productMatchZ);
//                        } else {
//                            //添加记录到主表中
//                            matchCode = productMatchMapper.selectNextCode(clientId);
//                            productMatchZ.setMatchCode(matchCode);
//                            productMatchMapper.addProMatchRecord(productMatchZ);
//                        }
//                        for (GaiaProductMatch a :list) {
//                            a.setMatchCode(matchCode);
//                            a.setStoCode("");
//                        }
//                        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
//                            //门店批量新增商品明细表
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
                    productMatchZ.setMatchDataCheckFlag("3");//初始化时初始化为3表示只是导入了数据，核验人员无任何操作
                    productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                    productMatchZ.setMatchCreater(userInfo.getUserId());
                    if (StringUtils.isNotEmpty(matchCode)) {//修改主表记录
                        matchBatch = productMatchMapper.selectMatchBatch(clientId, matchCode);
                        productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
                        productMatchZ.setMatchUpdater(userInfo.getUserId());
                        productMatchZ.setMatchCode(matchCode);
                        productMatchZ.setMatchBatch(matchBatch);
                        productMatchMapper.updateProMatchRecord(productMatchZ);
                    } else {
                        //添加记录到主表中
                        matchCode = productMatchMapper.selectNextCode(clientId);
                        productMatchZ.setMatchCode(matchCode);
                        productMatchZ.setMatchBatch(matchBatch);
                        productMatchMapper.addProMatchRecord(productMatchZ);
                    }
                    for (ImportProductInfo productInfo : importProductInfoList) {
                        GaiaProductMatch productMatch = new GaiaProductMatch();
                        BeanUtils.copyProperties(productInfo, productMatch);
                        productMatch.setProName(productInfo.getProCommonname());
//                        List<ProductInfo> productInfoList = productMatchMapper.selectProductBasicList(productMatch);//需优化
                        productMatch.setClient(clientId);
                        productMatch.setStoCode("");//未选择门店时不填入门店编码
                        productMatch.setMatchCode(matchCode);
                        productMatch.setMatchBatch(productMatchZ.getMatchBatch());
//                        productMatch.setSite(clientId);
                        //获取商品匹配度
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
                        //获取加盟商下门店及仓库
                        List<StoreInfo> storeList = storeDataMapper.selectStoreAndDcCodes(productMatch.getClient());
                        if (ObjectUtil.isNotEmpty(storeList) && storeList.size() > 0) {
//                            for (StoreInfo s : storeList) {
//                        判断商品主数据表中是否有同商品
//                                Example example = new Example(GaiaProductBusiness.class);
//                                example.createCriteria().andEqualTo("client", s.getClientId()).andEqualTo("proSite", s.getProSite()).andEqualTo("proSelfCode", productMatch.getProCode());
//                                GaiaProductBusiness gaiaProductBusiness = this.productBusinessMapper.selectOneByExample(example);
//                                if (ObjectUtil.isNotEmpty(gaiaProductBusiness)) {
//                                    throw new BusinessException("商品已存在");
//                                } else {
//                                    this.saveProBusinissInfo(productMatch, s);
//                                }
//                                GaiaProductBusiness productBusiness = this.saveProBusinissInfo(productMatch, s);
//                                productBusinessList.add(productBusiness);
//                            }
                            productMatch.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                            productMatch.setMatchCreateTime(CommonUtil.getHHmmss());
                            log.info("长度：" + productMatch.getProBarcode().length());
                            list.add(productMatch);
                        } else {
                            throw new BusinessException("请先维护门店信息");
                        }
                        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
                            //门店批量新增商品明细表
                            productMatchMapper.batchAddProductMatch(list);
                        }
                    }
                }
//                if (ObjectUtil.isNotEmpty(productBusinessList) && productBusinessList.size() > 0) {
//                    productBusinessMapper.batchInsertProduct(productBusinessList);
//                }
                result.setCode("0");
                result.setMessage("上传成功！");
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
        // 匹配类型 0-未匹配，1-部分匹配，2-完全匹配
        if (StringUtils.equals(inData.getMatchType(), "1")) {
            if (StringUtils.isNotEmpty(inData.getMinMatchDegree())) {
                if (Integer.valueOf(inData.getMinMatchDegree()) <= 0) {
                    throw new BusinessException("请输入正确的匹配度");
                }
            }
            if (StringUtils.isNotEmpty(inData.getMaxMatchDegree())) {
                if (Integer.valueOf(inData.getMaxMatchDegree()) >= 100) {
                    throw new BusinessException("请输入正确的匹配度");
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
        //设置过滤手动匹配
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
//                //查询药德库中商品信息
//                Example example1 = new Example(GaiaProductBasic.class);
//                example1.createCriteria().andEqualTo("proCode", msg.getProCodeG());
//                GaiaProductBasic productBasic = this.productBasicMapper.selectOneByExample(example1);
//                //更新商品业务表信息
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
        //加盟商下相同商品确认

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
        //药德编码是99999999时,修改商品分类与成分分类不同步更新GAIA_PRODUCT_BASIC表。
//        if(ObjectUtil.isNotEmpty(inData.getStoCodes())){
//            inData.setIsClient("Y");
//        }
        //更新匹配表中的商品分类、商品成分分类
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
        //组装导出内容
        List<List<Object>> dataList = new ArrayList<>();
        Integer index = 1;
        for (ProductSureMsg msg : list) {
            List<Object> line = new ArrayList<>();
//            //序号
//            line.add(index);
//            index ++;
            //用户商品编码
            line.add(msg.getProCode());
            //药德商品编码
            line.add(msg.getProCodeG());

            dataList.add(line);
        }
        String[] header = {"用户商品编码", "总库编码"};
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = ExcelUtils.exportExcel3(
                new ArrayList<String[]>() {{
                    add(header);
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(dataList);
                }},
                new ArrayList<String>() {{
                    add("采集任务商品编码");
                }});
        Result uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = inData.getClientId() + "-" + CommonUtil.getyyyyMMdd() + ".xlsx";
            uploadResult = cosUtils.uploadFile(bos, fileName);
            bos.flush();
        } catch (IOException e) {
            log.error("导出文件失败:", e);
            throw new BusinessException("导出文件失败！");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭流异常:", e);
                throw new BusinessException("关闭流异常！");
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
        //商品描述
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
        //存在商品编码则更新
        if (StringUtils.isNotEmpty(productBasic.getProCode())) {
            productBasicMapper.modifyProductBasicById(productBasic);
        } else {//不存在则新增
            String proCode = productBasicMapper.selectMaxProcode();
            productBasic.setProCode(proCode);
            productBasicMapper.insert(productBasic);
        }
        //商品匹配操作回填
        ProductSureMsg msg = new ProductSureMsg();
        msg.setProCodeG(productBasic.getProCode());
        msg.setProCode(inData.getMatchProCode());
        msg.setClientId(inData.getClientId());
        msg.setUpdateClient(inData.getUpdateClient());
        msg.setUpdateUser(inData.getUpdateUser());
        msg.setUpdateTime(inData.getUpdateTime());

        /** add by jinwencheng on 2022-01-07 17:57:38 添加matchCode属性 **/
        msg.setMatchCode(inData.getMatchCode());
        /** add end **/

        if (StringUtils.isNotEmpty(inData.getStoCode())) {
            msg.setStoCode(inData.getStoCode());
            //如果存在门店编码则更新
//            productBusiness.setClient(inData.getClientId());
//            productBusiness.setProSite(inData.getStoCode());
//            productBusiness.setProSelfCode(inData.getMatchProCode());
//            Example example = new Example(GaiaProductBusiness.class);
//            example.createCriteria().andEqualTo("proCode", inData.getProCode()).andEqualTo("proSite", productBusiness.getProSite()).andEqualTo("client", productBusiness.getClient());
//            productBusinessMapper.updateByExampleSelective(productBusiness, example);
            //图片采集赋码
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
            // 文件全路径
            String url = cosUtils.urlAuth(path);
            String type = url.substring(url.lastIndexOf(".") + 1, url.indexOf("?"));
            InputStream in = file.getInputStream();
            Workbook wk = null;
            if ("xls".equals(type)) {
                //文件流对象
                wk = new HSSFWorkbook(in);
            } else if ("xlsx".equals(type)) {
                wk = new XSSFWorkbook(in);
            } else {
                log.info("非excel文件");
                throw new BusinessException("文件格式有误");
            }

            //导入已存在的Excel文件，获得只读的工作薄对象
            //获取第一张Sheet表
            Sheet sheet = wk.getSheetAt(0);
            //取数据
            ImportProductKuCunInfo model = new ImportProductKuCunInfo();
            List<ImportProductKuCunInfo> importProductKuCunInfoList = ExcelManage.readFromExcelExtends(sheet, model, constractImportKuCunTitle());
            if (ObjectUtil.isNotEmpty(importProductKuCunInfoList) && importProductKuCunInfoList.size() > 0) {
                //删除该供应商数据
                String client = importProductKuCunInfoList.get(0).getClient();
                Example example = new Example(ProductCheckKucunImport.class);
                Example.Criteria criteria = example.createCriteria()
                        .andEqualTo("client", client);
                productCheckKucunImportMapper.deleteByExample(example);
                //商品匹配并新增到商品匹配明细表
                List<ProductCheckKucunImport> list = new ArrayList<>();
                for (ImportProductKuCunInfo info : importProductKuCunInfoList) {
                    ProductCheckKucunImport db = new ProductCheckKucunImport();
                    BeanUtils.copyProperties(info, db);
                    db.setStoCode(info.getSite());
                    db.setStoName(info.getSiteName());
                    list.add(db);
                }

                //此处进行预处理，将处理为加盟商店铺级别库存汇总数据，用于后期使用
                Map<String, ProCodeKuCunInfo> proCodeKuCunInfoMap = makeProductCodeExtendsInfo(list);
                if (CollectionUtil.isNotEmpty(proCodeKuCunInfoMap)) {
                    Map<String, ProCodeKuCunInfo> proCodeKuCunInfoMapFinal = new HashMap<>();
                    //此处需求处需要处理，过滤掉非门店的点
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
                result.setMessage("上传成功！");
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
        t.put("matchProCode", "药德商品编码");
        t.put("proCommonName", "药德通用名称");
        t.put("proSpecs", "规格");
        t.put("factoryName", "生产厂家");
        t.put("proBarcode", "国际条码1");
        t.put("proBarcode2", "国际条码2");
        t.put("proRegisterNo", "批准文号");
        t.put("proClassName", "商品分类");
        t.put("proCompClassName", "成分分类");
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
                            t.put(keyName, sortProduct.get(i).get("stoCode") + sortProduct.get(i).get("stoName") + "商品编码" + (j + 1));
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
                            t.put(keyName, sortProduct.get(i).get("clientId") + "商品编码" + (j + 1));
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
        t.put("matchProCode", "药德商品编码");
        t.put("proCommonName", "药德通用名称");
        t.put("proSpecs", "规格");
        t.put("factoryName", "生产厂家");
        t.put("proBarcode", "国际条码1");
        t.put("proBarcode2", "国际条码2");
        t.put("proRegisterNo", "批准文号");
        t.put("proClassName", "商品分类");
        t.put("proCompClassName", "成分分类");
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
                            t.put(keyName, sortProduct.get(i).get("stoCode") + sortProduct.get(i).get("stoName") + "商品编码" + (j + 1));
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
                            t.put(keyName, sortProduct.get(i).get("clientId") + "商品编码" + (j + 1));
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
        CsvFileInfo csvInfo = CsvClient.getCsvByteByMap(proInfoList, "已匹配商品合并信息", t);
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
        t.put("matchProCode", "药德商品编码");
        t.put("proCommonName", "药德通用名称");
        t.put("proSpecs", "规格");
        t.put("factoryName", "生产厂家");
        t.put("proBarcode", "国际条码1");
        t.put("proBarcode2", "国际条码2");
        t.put("proRegisterNo", "批准文号");
        t.put("proClassName", "商品分类");
        t.put("proCompClassName", "成分分类");
        t.put("proCode", "用户商品编码");
        if (ObjectUtil.isNotEmpty(inData.getStoCodes())) {
            inData.setType("1");
            t.put("stoName", "门店");
        } else {
            inData.setType("0");
        }
        List<LinkedHashMap<String, String>> proInfo = productMatchMapper.selectNoMatchProCode(inData);
        CsvFileInfo csvInfo = CsvClient.getCsvByteByMap(proInfo, "未匹配商品合并信息", t);
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


    //表头中文对应的Map
    public static Map<String, String> constractImportKuCunTitle() {
        Map<String, String> result = new HashMap<>();
        result.put("加盟商ID", "client");
        result.put("店号", "site");
        result.put("地点", "siteName");
        result.put("商品编码", "proCode");
        result.put("国际条形码", "proBarcode");
        result.put("商品名称", "proName");
        result.put("库存", "kuCunNum");
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
        //商品匹配操作回填
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

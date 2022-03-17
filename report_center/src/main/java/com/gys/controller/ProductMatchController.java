package com.gys.controller;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.*;
import com.gys.entity.data.productMatch.*;
import com.gys.service.ProductMatchService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.ExcelManage;
import com.gys.util.PhoneFormatCheckUtils;
import com.gys.util.productMatch.ProductMatchUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@Api(tags = "商品匹配操作")
@RequestMapping("/productMatch")
@Slf4j
public class ProductMatchController extends BaseController {
    @Resource
    private ProductMatchService productMatchService;
    @Autowired
    private CosUtils cosUtils;

    @ApiOperation(value = "匹配商品导入")
    @PostMapping({"importProductMatchListOld"})
    public Result getImportExcelDetailList(HttpServletRequest request,@RequestParam("file") MultipartFile file,
                                           @RequestParam("clientId") String clientId,
                                           @RequestParam("stoCodes") String[] stoCodes,
                                           @RequestParam("tel") String tel) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return productMatchService.importProductMatchList(request,userInfo,file,clientId,stoCodes,tel);
    }

    @ApiOperation(value = "匹配商品导入")
    @PostMapping({"importProductMatchList"})
    public Result getImportExcelDetailListNew(HttpServletRequest request,@RequestParam("file") MultipartFile file,
                                           @RequestParam("clientId") String clientId,
                                           @RequestParam("stoCodes") String[] stoCodes,
                                           @RequestParam("tel") String tel) {
        GetLoginOutData userInfo = this.getLoginUser(request);
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
            //获取第一张Sheet表
            Sheet sheet = wk.getSheetAt(0);
            //取数据
            ImportProductInfo model = new ImportProductInfo();
            List<ImportProductInfo> importProductInfoList = ExcelManage.readFromExcel(sheet, model);
            //导入批次查询
            String matchBatch = productMatchService.selectNextMatchBatch(clientId);
            if (ObjectUtil.isNotEmpty(importProductInfoList) && importProductInfoList.size() > 0) {
                //商品匹配并新增到商品匹配明细表
                List<GaiaProductMatch> list = new ArrayList<>();
//                List<GaiaProductBusiness> productBusinessList = new ArrayList<>();
                //判断入参是否有门店
                if (ObjectUtil.isNotEmpty(stoCodes)) {
                    for (int i = 0; i < stoCodes.length; i++) {
                        String matchCode = productMatchService.selectStoreByCode(clientId, stoCodes[i]);
                        //添加记录到主表中
                        ProductMatchZ productMatchZ = new ProductMatchZ();
                        productMatchZ.setClient(clientId);
                        productMatchZ.setStoCode(stoCodes[i]);
                        productMatchZ.setMatchType("1");
                        productMatchZ.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                        productMatchZ.setMatchCreater(userInfo.getUserId());
                        if(stoCodes.length==1){
                            productMatchZ.setMatchDataCheckTouchTel(tel);
                            productMatchZ.setMatchDataCheckFlag("3");//初始化时初始化为3表示只是导入了数据，核验人员无任何操作
                        }
                        if (StringUtils.isEmpty(matchCode)) {//新增匹配数据
                            matchCode = productMatchService.selectNextCode(clientId);
                            productMatchZ.setMatchCode(matchCode);
                            productMatchZ.setMatchBatch(matchBatch);
                            productMatchService.addProMatchRecord(productMatchZ);
                        } else {//修改匹配数据
                            matchBatch = productMatchService.selectMatchBatch(clientId,matchCode);
                            productMatchZ.setMatchCode(matchCode);
                            productMatchZ.setMatchBatch(matchBatch);
                            productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
                            productMatchZ.setMatchUpdater(userInfo.getUserId());
                            productMatchService.updateProMatchRecord(productMatchZ);
                        }
                        for (ImportProductInfo productInfo : importProductInfoList) {
                            GaiaProductMatch productMatch = new GaiaProductMatch();
                            BeanUtils.copyProperties(productInfo, productMatch);
                            productMatch.setProName(productInfo.getProCommonname());
                            if (StringUtils.isEmpty(productInfo.getProCommonname())) {
                                productMatch.setProName(productInfo.getProName());
                            }
                            productMatch.setClient(clientId);
                            productMatch.setStoCode(stoCodes[i]);
                            productMatch.setMatchCode(matchCode);
                            productMatch.setMatchBatch(productMatchZ.getMatchBatch());
                            productMatch.setProMatchStatus("5"); //初始导入数据状态
                            productMatch.setMatchType("0");
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
                            productMatchService.batchAddProductMatch(list);
                        }
                        Map<String,String> map = new HashMap<>();
                        map.put("clientId",productMatchZ.getClient());
                        map.put("matchBatch",productMatchZ.getMatchBatch());
                        result.setData(map);
                        //异步处理耗时任务
                        CompletableFuture.runAsync(() -> {
                            try {
//                                List<GaiaProductMatch> ratiolist = new ArrayList<>();
                                for (ImportProductInfo productInfo : importProductInfoList) {
                                    GaiaProductMatch productMatch = new GaiaProductMatch();
                                    BeanUtils.copyProperties(productInfo, productMatch);
                                    productMatch.setProName(productInfo.getProCommonname());
                                    if (StringUtils.isEmpty(productInfo.getProCommonname())) {
                                        productMatch.setProName(productInfo.getProName());
                                    }
                                    productMatch.setClient(clientId);
                                    productMatch.setStoCode(productMatchZ.getStoCode());
                                    productMatch.setMatchCode(productMatchZ.getMatchCode());
                                    productMatch.setMatchBatch(productMatchZ.getMatchBatch());
//                            productMatch.setSite(clientId + "-" + stoCodes[i]);
                                    List<ProductInfo> productInfoList = productMatchService.selectProductBasicList(productMatch);//需优化
                                    //获取商品匹配度
                                    ProductMatchStatus productMatchStatus = ProductMatchUtils.execMatchProcess(productMatch, productInfoList);
                                    if (ObjectUtil.isEmpty(productMatchStatus.getMatchValue())) {
                                        productMatchStatus.setMatchValue("0");
                                    }
                                    if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(BigDecimal.ZERO) == 0) {
                                        productMatch.setMatchDegree(productMatchStatus.getMatchValue());
                                        productMatch.setMatchType("0");
                                        productMatch.setProMatchStatus("0");
                                        productMatch.setMatchProCode("99999999");
                                    } else if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(new BigDecimal(100)) == 0) {
                                        productMatch.setMatchDegree(productMatchStatus.getMatchValue());
                                        productMatch.setMatchType("2");
                                        productMatch.setProMatchStatus("1");
                                        productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
                                    } else {
                                        productMatch.setMatchDegree(productMatchStatus.getMatchValue());
                                        productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
                                        productMatch.setMatchType("1");
                                        productMatch.setProMatchStatus("1");
                                    }
                                    productMatch.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                                    productMatch.setMatchCreateTime(CommonUtil.getHHmmss());
                                    productMatchService.addProductMatch(productMatch);
                                }
//                                if (ObjectUtil.isNotEmpty(ratiolist) && ratiolist.size() > 0) {
//                                    //门店批量新增商品明细表
//                                    productMatchMapper.batchAddProductMatch(ratiolist);
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("run end ...");
                        });
                    }
                } else {
                    String matchCode = productMatchService.selectClientByCode(clientId);
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
                        matchBatch = productMatchService.selectMatchBatch(clientId,matchCode);
                        productMatchZ.setMatchUpdateDate(CommonUtil.getyyyyMMdd());
                        productMatchZ.setMatchUpdater(userInfo.getUserId());
                        productMatchZ.setMatchCode(matchCode);
                        productMatchZ.setMatchBatch(matchBatch);
                        productMatchService.updateProMatchRecord(productMatchZ);
                    } else {
                        //添加记录到主表中
                        matchCode = productMatchService.selectNextCode(clientId);
                        productMatchZ.setMatchCode(matchCode);
                        productMatchZ.setMatchBatch(matchBatch);
                        productMatchService.addProMatchRecord(productMatchZ);
                    }
                    for (ImportProductInfo productInfo : importProductInfoList) {
                        GaiaProductMatch productMatch = new GaiaProductMatch();
                        BeanUtils.copyProperties(productInfo, productMatch);
                        productMatch.setProName(productInfo.getProCommonname());
                        productMatch.setClient(clientId);
                        productMatch.setStoCode("");//未选择门店时不填入门店编码
                        productMatch.setMatchCode(matchCode);
                        productMatch.setMatchBatch(productMatchZ.getMatchBatch());
                        productMatch.setProMatchStatus("5");
                        productMatch.setMatchType("0");
//                        productMatch.setMatchCreateDate(CommonUtil.getyyyyMMdd());
//                        productMatch.setMatchCreateTime(CommonUtil.getHHmmss());
                        list.add(productMatch);
                        //获取加盟商下门店及仓库
                    }
                    List<StoreInfo> storeList = productMatchService.selectStoreAndDcCodes(productMatchZ.getClient());
                    if (ObjectUtil.isNotEmpty(storeList) && storeList.size() > 0) {
                        log.info("长度："+ productMatchZ.getMatchBatch());
                    } else {
                        throw new BusinessException("请先维护门店信息");
                    }
                    if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
                        //门店批量新增商品明细表
                        productMatchService.batchAddProductMatch(list);
                    }
                    Map<String,String> map = new HashMap<>();
                    map.put("clientId",productMatchZ.getClient());
                    map.put("matchBatch",productMatchZ.getMatchBatch());
                    result.setData(map);
                    //异步处理耗时任务
                    CompletableFuture.runAsync(() -> {
                        try {
//                            List<GaiaProductMatch> ratiolist = new ArrayList<>();
                            for (ImportProductInfo productInfo : importProductInfoList) {
                                GaiaProductMatch productMatch = new GaiaProductMatch();
                                BeanUtils.copyProperties(productInfo, productMatch);
                                productMatch.setProName(productInfo.getProCommonname());
                                List<ProductInfo> productInfoList = productMatchService.selectProductBasicList(productMatch);//需优化
                                productMatch.setClient(productMatchZ.getClient());
                                productMatch.setStoCode("");//未选择门店时不填入门店编码
                                productMatch.setMatchCode(productMatchZ.getMatchCode());
                                productMatch.setMatchBatch(productMatchZ.getMatchBatch());
                                //获取商品匹配度
                                ProductMatchStatus productMatchStatus = ProductMatchUtils.execMatchProcess(productMatch, productInfoList);
                                if (ObjectUtil.isEmpty(productMatchStatus.getMatchValue())) {
                                    productMatchStatus.setMatchValue("0");
                                }
                                if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(BigDecimal.ZERO) == 0) {
                                    productMatch.setMatchDegree(productMatchStatus.getMatchValue());
                                    productMatch.setMatchType("0");
                                    productMatch.setProMatchStatus("0");
                                    productMatch.setMatchProCode("99999999");
                                } else if (new BigDecimal(productMatchStatus.getMatchValue()).compareTo(new BigDecimal(100)) == 0) {
                                    productMatch.setMatchDegree(productMatchStatus.getMatchValue());
                                    productMatch.setMatchType("2");
                                    productMatch.setProMatchStatus("1");
                                    productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
                                } else {
                                    productMatch.setMatchDegree(productMatchStatus.getMatchValue());
                                    productMatch.setMatchProCode(productMatchStatus.getMatchedCode());
                                    productMatch.setMatchType("1");
                                    productMatch.setProMatchStatus("2");
                                }
                                productMatch.setMatchCreateDate(CommonUtil.getyyyyMMdd());
                                productMatch.setMatchCreateTime(CommonUtil.getHHmmss());
                                productMatchService.addProductMatch(productMatch);
//                                ratiolist.add(productMatch);
                                //获取加盟商下门店及仓库
                            }
//                            if (ObjectUtil.isNotEmpty(ratiolist) && ratiolist.size() > 0) {
//                                //门店批量新增商品明细表
//                                productMatchMapper.batchAddProductMatch(ratiolist);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("run end ...");
                    });
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
//        return productMatchService.importProductMatchListNew(request,userInfo,file,clientId,stoCodes,tel);
    }

    @ApiOperation(value = "查询匹配商品进度")
    @PostMapping({"selectRatioSchedule"})
    public JsonResult selectRatioSchedule(HttpServletRequest request, @RequestBody MatchProductInData inData) {
//        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(productMatchService.ratioCount(inData),"提示：获取数据成功！");
    }

    @ApiOperation(value = "库存导入")
    @PostMapping({"importKuCun"})
    public Result importKuCun(HttpServletRequest request,@RequestParam("file") MultipartFile file) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return productMatchService.importKuCun(request,file,userInfo);
    }

    @ApiOperation(value = "匹配商品查询")
    @PostMapping({"productMatchList"})
    public JsonResult productMatchList(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        return JsonResult.success(productMatchService.selectProMatchList(inData),"提示：获取数据成功！");
    }

    //商品匹配确认
    @ApiOperation(value = "商品匹配确认")
    @PostMapping({"sureProduct"})
    public JsonResult sureProduct(HttpServletRequest request, @RequestBody ProductSureInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setUpdateClient(userInfo.getClient());
        inData.setUpdateUser(userInfo.getUserId());
        inData.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)));
        productMatchService.sureProduct(inData);
        return JsonResult.success("","提示：操作成功！");
    }

    //商品信息修改
    @ApiOperation(value = "商品信息修改")
    @PostMapping({"productModify"})
    public JsonResult productModify(HttpServletRequest request, @RequestBody ProBasicInfo inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setUpdateClient(userInfo.getClient());
        inData.setUpdateUser(userInfo.getUserId());
        inData.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)));
        productMatchService.modifyProBasicInfo(inData);
        return JsonResult.success("","提示：操作成功！");
    }

    //商品分类列表
    @ApiOperation(value = "商品分类列表")
    @PostMapping({"productClassList"})
    public JsonResult productClassList(HttpServletRequest request) {
        return JsonResult.success(productMatchService.productClassList(),"提示：操作成功！");
    }

    //商品成分分类列表
    @ApiOperation(value = "商品成分分类列表")
    @PostMapping({"productComponentList"})
    public JsonResult productComponentList(HttpServletRequest request) {
        return JsonResult.success(productMatchService.productComponentList(),"提示：操作成功！");
    }

    //门店列表查询
    @ApiOperation(value = "门店列表查询")
    @PostMapping({"getStoListByClient"})
    public JsonResult getStoListByClient(HttpServletRequest request, @RequestBody StoInData inData) {
        return JsonResult.success(productMatchService.getStoListByClient(inData),"提示：操作成功！");
    }

    //匹配其他商品列表
    @ApiOperation(value = "匹配其他商品列表")
    @PostMapping({"otherProductList"})
    public JsonResult otherProductList(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return JsonResult.success(productMatchService.otherProductList(inData),"提示：操作成功！");
    }

    //进度看板页面
    @ApiOperation(value = "进度看板页面")
    @PostMapping({"matchSchedule"})
    public JsonResult matchSchedule(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return JsonResult.success(productMatchService.productMatchSchedule(inData),"提示：操作成功！");
    }

    //已应用图片查询
    @ApiOperation(value = "已应用图片查询")
    @PostMapping({"imageQuery"})
    public JsonResult imageQuery(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return JsonResult.success(productMatchService.imageQuery(inData),"提示：操作成功！");
    }

    //商品采集导出选中商品
    @ApiOperation(value = "商品采集导出选中商品")
    @PostMapping({"excelCollectPicProduct"})
    public Result exportData(HttpServletRequest request, @RequestBody ProductSureInData inData) {
        return productMatchService.exportData(inData);
    }

    //根据药德商品编码查询商品
    @ApiOperation(value = "根据药德商品编码查询商品")
    @PostMapping({"queryProductBasicById"})
    public JsonResult queryProductBasicById(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return JsonResult.success(productMatchService.queryProductBasicById(inData),"提示：获取信息成功！");
    }

    //商品新增赋码
    @ApiOperation(value = "商品新增赋码")
    @PostMapping({"addProductBasic"})
    public JsonResult addProductBasic(HttpServletRequest request, @RequestBody ProductBasicInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setUpdateClient(userInfo.getClient());
        inData.setUpdateUser(userInfo.getUserId());
        inData.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)));
        productMatchService.addProductBasic(inData);
        return JsonResult.success("","提示：获取信息成功！");
    }

    //商品新增赋码
    @ApiOperation(value = "客户列表查询")
    @PostMapping({"selectClientList"})
    public JsonResult selectClientList(HttpServletRequest request, @RequestBody StoInData inData) {
        return JsonResult.success(productMatchService.selectClientList(inData),"提示：获取信息成功！");
    }

    //商品信息合并未匹配查询
    @ApiOperation(value = "商品信息合并已匹配查询")
    @PostMapping({"selectMergeProductList"})
    public JsonResult selectMergeProductList(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return JsonResult.success(productMatchService.selectMergeProductList(inData),"提示：获取信息成功！");
    }

    //商品信息合并未匹配查询
    @ApiOperation(value = "商品信息合并未匹配查询")
    @PostMapping({"selectNoMergeProductList"})
    public JsonResult selectNoMergeProductList(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return JsonResult.success(productMatchService.selectNoMergeProductList(inData),"提示：获取信息成功！");
    }

    //商品信息合并已匹配导出
    @ApiOperation(value = "商品信息合并已匹配导出")
    @PostMapping({"exportMergeProductList"})
    public Result exportMergeProductList(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return productMatchService.exportMergeProductList(inData);
    }

    //商品信息合并已匹配导出
    @ApiOperation(value = "商品信息合未匹配导出")
    @PostMapping({"exportNoMergeProductList"})
    public Result exportNoMergeProductList(HttpServletRequest request, @RequestBody MatchProductInData inData) {
        return productMatchService.exportNoMergeProductList(inData);
    }

    /** add by jinwencheng on 2021-12-31 14:23:05 新增商品匹配前搜索类似商品进行确认 **/
    @ApiOperation(value = "确认新增赋码")
    @PostMapping({"confirmProductBasic"})
    public JsonResult confirmProductBasic(HttpServletRequest request, @RequestBody ConfirmProductBasicDTO confirmProductBasicDTO) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        confirmProductBasicDTO.setUpdateClient(userInfo.getClient());
        confirmProductBasicDTO.setUpdateUser(userInfo.getUserId());
        confirmProductBasicDTO.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)));
        productMatchService.confirmProductBasic(confirmProductBasicDTO);
        return JsonResult.success("","提示：确认成功！");
    }
    /** add end **/

}

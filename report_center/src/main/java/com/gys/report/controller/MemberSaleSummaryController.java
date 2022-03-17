//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.controller;

import cn.hutool.core.util.ObjectUtil;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.report.entity.MemberSalesInData;
import com.gys.report.entity.MemberSalesOutData;
import com.gys.report.service.MemberSaleSummaryService;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
        tags = {"会员销售汇总查询"}
)
@RestController
@RequestMapping({"/memberSaleSummary/"})
public class MemberSaleSummaryController extends BaseController {
    @Resource
    public CosUtils cosUtils;
    @Autowired
    private MemberSaleSummaryService memberSaleSummaryService;

    public MemberSaleSummaryController() {
    }

    @ApiOperation(
            value = "会员消费汇总表",
            response = MemberSalesOutData.class
    )
    @PostMapping({"selectMemberList"})
    public JsonResult selectMemberList(HttpServletRequest request, @RequestBody MemberSalesInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.memberSaleSummaryService.selectMemberCollectList(inData), "提示：提取数据成功！");
    }

    @ApiOperation("会员消费汇总导出")
    @PostMapping({"selectMemberListCSV"})
    public Result selectMemberListCSV(HttpServletRequest request, @RequestBody MemberSalesInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        new ArrayList();
        new ArrayList();
        String fileName = "";
        Map<String, String> titleMap = new LinkedHashMap(2);
        Result result = null;
        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("开始日期不能为空！");
        } else if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        } else {
            CsvFileInfo fileInfo;
            ByteArrayOutputStream bos;
            if ("0".equals(inData.getState())) {
                List<LinkedHashMap<String, Object>> outData = this.memberSaleSummaryService.selectMemberListCSV(inData);
                if (outData.size() > 0) {
                    fileName = "会员消费汇总表导出";
                    titleMap.put("cardStore", "办卡门店");
                    titleMap.put("hykNo", "会员卡号");
                    titleMap.put("hykName", "会员名称");
                    titleMap.put("mobile", "手机号码");
                    titleMap.put("sex", "性别");
                    titleMap.put("totalSelledDays", "期间天数");
                    titleMap.put("selledDays", "消费天数");
                    titleMap.put("gssdnormalAmt", "应收金额");
                    titleMap.put("ssAmount", "实收金额");
                    titleMap.put("allCostAmt", "含税成本额");
                    titleMap.put("grossProfitAmt", "毛利额");
                    titleMap.put("grossProfitRate", "毛利率");
                    titleMap.put("tradedTime", "消费次数");
                    titleMap.put("dailyPayAmt", "消费单价");
                    titleMap.put("discountAmt", "折扣金额");
                    titleMap.put("discountRate", "折扣率");
                    titleMap.put("dailyPayCount", "消费频次");
                    titleMap.put("proAvgCount", "客品次");
                    titleMap.put("billAvgPrice", "品单价");
                    for(Map<String, Object> map : outData){
                        if(ObjectUtil.isNotNull(map.get("discountRate"))){
                            map.put("discountRate",map.get("discountRate").toString().concat("%"));
                        }else{
                            map.put("discountRate","0%");
                        }
                    }
                    fileInfo = CsvClient.getCsvByteByMap(outData, fileName, titleMap);
                    bos = new ByteArrayOutputStream();

                    try {
                        bos.write(fileInfo.getFileContent());
                        result = this.cosUtils.uploadFile(bos, fileInfo.getFileName());
                    } catch (FileNotFoundException var26) {
                        var26.printStackTrace();
                    } catch (IOException var27) {
                        var27.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }

                    return result;
                } else {
                    throw new BusinessException("导出失败");
                }
            } else if ("1".equals(inData.getState())) {
                List<LinkedHashMap<String, Object>> outDataStore = this.memberSaleSummaryService.selectMemberStoListCSV(inData);
                if (outDataStore.size() > 0) {
                    fileName = "会员消费汇总表（门店）导出";
                    titleMap.put("cardStore", "办卡门店");
                    titleMap.put("hykNo", "会员卡号");
                    titleMap.put("hykName", "会员名称");
                    titleMap.put("mobile", "手机号码");
                    titleMap.put("sex", "性别");
                    titleMap.put("stoCode", "门店编码");
                    titleMap.put("saleStore", "门店名称");
                    titleMap.put("totalSelledDays", "期间天数");
                    titleMap.put("selledDays", "消费天数");
                    titleMap.put("gssdnormalAmt", "应收金额");
                    titleMap.put("ssAmount", "实收金额");
                    titleMap.put("allCostAmt", "含税成本额");
                    titleMap.put("grossProfitAmt", "毛利额");
                    titleMap.put("grossProfitRate", "毛利率");
                    titleMap.put("tradedTime", "消费次数");
                    titleMap.put("dailyPayAmt", "消费单价");
                    titleMap.put("discountAmt", "折扣金额");
                    titleMap.put("discountRate", "折扣率");
                    titleMap.put("dailyPayCount", "消费频次");
                    titleMap.put("proAvgCount", "客品次");
                    titleMap.put("billAvgPrice", "品单价");
                    for(Map<String, Object> map : outDataStore){
                        if(ObjectUtil.isNotNull(map.get("discountRate"))){
                            map.put("discountRate",map.get("discountRate").toString().concat("%"));
                        }else{
                            map.put("discountRate","0%");
                        }
                    }
                    fileInfo = CsvClient.getCsvByteByMap(outDataStore, fileName, titleMap);
                    bos = new ByteArrayOutputStream();

                    try {
                        bos.write(fileInfo.getFileContent());
                        result = this.cosUtils.uploadFile(bos, fileInfo.getFileName());
                    } catch (FileNotFoundException var29) {
                        var29.printStackTrace();
                    } catch (IOException var30) {
                        var30.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }

                    return result;
                } else {
                    throw new BusinessException("导出失败");
                }
            } else {
                return result;
            }
        }
    }

    @ApiOperation("会员消费商品明细导出")
    @PostMapping({"selectMemberProductListCSV"})
    public Result selectMemberProductListCSV(HttpServletRequest request, @RequestBody MemberSalesInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        new ArrayList();
        new ArrayList();
        String fileName = "";
        Map<String, String> titleMap = new LinkedHashMap(2);
        Result result = null;
        if (ObjectUtil.isNotEmpty(inData.getClassArr())) {
            inData.setProductClass(CommonUtil.twoDimensionToOneDimensionArrar(inData.getClassArr()));
        }

        if (ObjectUtil.isNotEmpty(inData.getProCode())) {
            inData.setProductCode(inData.getProCode().split(","));
        }

        if (ObjectUtil.isEmpty(inData.getStartDate())) {
            throw new BusinessException("开始日期不能为空！");
        } else if (ObjectUtil.isEmpty(inData.getEndDate())) {
            throw new BusinessException("结束日期不能为空！");
        } else {
            CsvFileInfo fileInfo;
            ByteArrayOutputStream bos;
            if ("0".equals(inData.getState())) {
                List<LinkedHashMap<String, Object>> outData = this.memberSaleSummaryService.selectMemberProCSV(inData);
                if (outData.size() > 0) {
                    fileName = "会员消费商品明细导出";
                    titleMap.put("cardStore", "办卡门店");
                    titleMap.put("hykNo", "会员卡号");
                    titleMap.put("hykName", "会员名称");
                    titleMap.put("mobile", "手机号码");
                    titleMap.put("sex", "性别");
                    titleMap.put("proCode", "商品编码");
                    titleMap.put("productName", "商品名称");
                    titleMap.put("count", "商品数量");
                    titleMap.put("gssdnormalAmt", "应收金额");
                    titleMap.put("ssAmount", "实收金额");
                    titleMap.put("discountAmt", "折扣金额");
                    titleMap.put("discountRate", "折扣率");
                    titleMap.put("allCostAmt", "含税成本额");
                    titleMap.put("grossProfitAmt", "毛利额");
                    titleMap.put("grossProfitRate", "毛利率");
                    titleMap.put("bigClass", "商品大类");
                    titleMap.put("midClass", "商品中类");
                    titleMap.put("smallClass", "商品小类");
                    titleMap.put("saleClass", "销售等级");
                    titleMap.put("proPosition", "商品定位");
                    titleMap.put("proClass", "商品自分类");
                    titleMap.put("zdy1", "自定义1");
                    titleMap.put("zdy2", "自定义2");
                    titleMap.put("zdy3", "自定义3");
                    titleMap.put("zdy4", "自定义4");
                    titleMap.put("zdy5", "自定义5");
                    fileInfo = CsvClient.getCsvByteByMap(outData, fileName, titleMap);
                    bos = new ByteArrayOutputStream();

                    try {
                        bos.write(fileInfo.getFileContent());
                        result = this.cosUtils.uploadFile(bos, fileInfo.getFileName());
                    } catch (FileNotFoundException var26) {
                        var26.printStackTrace();
                    } catch (IOException var27) {
                        var27.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }

                    return result;
                } else {
                    throw new BusinessException("导出失败");
                }
            } else if ("1".equals(inData.getState())) {
                List<LinkedHashMap<String, Object>> outDataStore = this.memberSaleSummaryService.selectMemberProStoCSV(inData);
                if (outDataStore.size() > 0) {
                    fileName = "会员消费商品明细导出";
                    titleMap.put("cardStore", "办卡门店");
                    titleMap.put("hykNo", "会员卡号");
                    titleMap.put("hykName", "会员名称");
                    titleMap.put("mobile", "手机号码");
                    titleMap.put("sex", "性别");
                    titleMap.put("stoCode", "门店编码");
                    titleMap.put("saleStore", "门店名称");
                    titleMap.put("proCode", "商品编码");
                    titleMap.put("productName", "商品名称");
                    titleMap.put("count", "商品数量");
                    titleMap.put("gssdnormalAmt", "应收金额");
                    titleMap.put("ssAmount", "实收金额");
                    titleMap.put("discountAmt", "折扣金额");
                    titleMap.put("discountRate", "折扣率");
                    titleMap.put("allCostAmt", "含税成本额");
                    titleMap.put("grossProfitAmt", "毛利额");
                    titleMap.put("grossProfitRate", "毛利率");
                    titleMap.put("bigClass", "商品大类");
                    titleMap.put("midClass", "商品中类");
                    titleMap.put("smallClass", "商品小类");
                    titleMap.put("saleClass", "销售等级");
                    titleMap.put("proPosition", "商品定位");
                    titleMap.put("proClass", "商品自分类");
                    titleMap.put("zdy1", "自定义1");
                    titleMap.put("zdy2", "自定义2");
                    titleMap.put("zdy3", "自定义3");
                    titleMap.put("zdy4", "自定义4");
                    titleMap.put("zdy5", "自定义5");
                    fileInfo = CsvClient.getCsvByteByMap(outDataStore, fileName, titleMap);
                    bos = new ByteArrayOutputStream();

                    try {
                        bos.write(fileInfo.getFileContent());
                        result = this.cosUtils.uploadFile(bos, fileInfo.getFileName());
                    } catch (FileNotFoundException var29) {
                        var29.printStackTrace();
                    } catch (IOException var30) {
                        var30.printStackTrace();
                    } finally {
                        bos.flush();
                        bos.close();
                    }

                    return result;
                } else {
                    throw new BusinessException("导出失败");
                }
            } else {
                return result;
            }
        }
    }

    @ApiOperation(
            value = "会员消费明细表",
            response = MemberSalesOutData.class
    )
    @PostMapping({"selectMemberDetailsList"})
    public JsonResult selectMemberDetailsList(HttpServletRequest request, @RequestBody MemberSalesInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        //inData.setClient("10000005");
        return JsonResult.success(this.memberSaleSummaryService.selectMenberDetailList(inData), "提示：提取数据成功！");
    }
}

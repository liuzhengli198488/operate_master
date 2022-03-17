package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.entity.data.salesSummary.PersonSalesInData;
import com.gys.entity.data.salesSummary.PersonSalesOutDataTotal;
import com.gys.entity.renhe.RenHePersonSales;
import com.gys.entity.renhe.StoreProductSaleSummaryInData;
import com.gys.entity.renhe.StoreSaleSummaryInData;
import com.gys.report.entity.StoreProductSaleStoreOutData;
import com.gys.report.entity.StoreSaleDateInData;
import com.gys.service.RenHeSaleReportService;
import com.gys.util.CosUtils;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "门店销售管理-山西仁和专供")
@RequestMapping({"/storeSaleReport"})
public class StoreSaleOptimizationController extends BaseController {

    @Autowired
    private RenHeSaleReportService renHeSaleReportService;
    @Resource
    public CosUtils cosUtils;

    @ApiOperation(value = "门店销售汇总查询")
    @PostMapping("/storeSaleSummary")
    public JsonResult selectStoreSaleSummaryByBrId(HttpServletRequest request, @Valid @RequestBody StoreSaleSummaryInData summaryInData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        summaryInData.setClient(userInfo.getClient());
        return JsonResult.success(renHeSaleReportService.selectStoreSaleSummaryByBrId(summaryInData), "返回成功");
    }

    @ApiOperation(value = "门店日期销售查询")
    @PostMapping({"/storeSaleByDate"})
    public JsonResult selectStoreSaleByDate(HttpServletRequest request, @RequestBody StoreSaleDateInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(renHeSaleReportService.selectStoreSaleByDate(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "日期销售汇总查询")
    @PostMapping("/storeSaleSummaryByDate")
    public JsonResult selectSalesSummaryByDate(HttpServletRequest request, @Valid @RequestBody com.gys.entity.data.salesSummary.StoreSaleDateInData summaryData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        summaryData.setClient(userInfo.getClient());
        return JsonResult.success(renHeSaleReportService.selectStoreSalesSummaryByDate(summaryData), "返回成功");
    }

    @ApiOperation(value = "门店商品销售汇总查询", response = StoreProductSaleStoreOutData.class)
    @PostMapping({"/selectStoreProductSaleSummary"})
    public JsonResult selectStoreProductSaleSummary(HttpServletRequest request, @RequestBody StoreProductSaleSummaryInData data) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(renHeSaleReportService.selectStoreProductSaleSummary(data), "提示：获取数据成功！");
    }

    @ApiOperation(value = "人员销售汇总查询")
    @PostMapping("/selectPersonSales")
    public JsonResult selectPersonSales(HttpServletRequest request, @RequestBody PersonSalesInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(renHeSaleReportService.selectPersonSales(inData), "返回成功");
    }

    @ApiOperation(value = "人员销售汇总导出")
    @PostMapping("/exportPersonSales")
    public Result exportPersonSales(HttpServletRequest request, @RequestBody PersonSalesInData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        PageInfo pageInfo = renHeSaleReportService.selectPersonSales(inData);
        List<RenHePersonSales> outData = pageInfo.getList();
        RenHePersonSales total = new RenHePersonSales();
        PersonSalesOutDataTotal outTotal = (PersonSalesOutDataTotal) pageInfo.getListNum();
        BeanUtils.copyProperties(pageInfo.getListNum(), total);
        total.setUserCode("总计");
        total.setDiscountRate(new BigDecimal(outTotal.getDiscountRate().replace("%", "")));
        total.setMemberSaleRate(new BigDecimal(outTotal.getMemberSaleRate().replace("%", "")));
        total.setGrossProfitRate(new BigDecimal(outTotal.getGrossProfitRate().replace("%", "")));
        total.setCtmGrossProfitRate(new BigDecimal(outTotal.getCtmGrossProfitRate().replace("%", "")));
        outData.add(total);
        String fileName = "员工销售汇总导出";

        CsvFileInfo csvInfo = null;
        // 导出a
        // byte数据
        if (StringUtils.isNotEmpty(inData.getNotSto())) {
            if ("Y".equals(inData.getNotSto())) {
                csvInfo = CsvClient.getCsvByte(outData, fileName, null);
            } else {
                csvInfo = CsvClient.getCsvByte(outData, fileName, Collections.singletonList((short) 1));
            }
        } else {
            return Result.error("导出失败");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Result result = null;
        try {
            bos.write(csvInfo.getFileContent());
            result = cosUtils.uploadFile(bos, csvInfo.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bos.flush();
            bos.close();
        }
        return result;
    }
}

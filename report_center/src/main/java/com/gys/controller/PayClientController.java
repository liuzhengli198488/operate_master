package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.PayClientDetail;
import com.gys.entity.PayClientInData;
import com.gys.entity.PayClientOutData;
import com.gys.entity.data.salesSummary.PersonSalesInData;
import com.gys.entity.data.salesSummary.PersonSalesOutData;
import com.gys.service.PayClientService;
import com.gys.util.CosUtils;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
@Api(tags = "用户付款查询")
@RequestMapping("/payClient")
public class PayClientController extends BaseController {
    @Autowired
    private PayClientService payClientService;
    @Resource
    public CosUtils cosUtils;

    @ApiOperation(value = "用户付款记录查询",response = PayClientOutData.class)
    @PostMapping({"selectPayClientList"})
    public JsonResult selectPayClient(@RequestBody PayClientInData inData) {
//        GetLoginOutData userInfo = getLoginUser(request);
//        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return JsonResult.success(this.payClientService.selectPayClientList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "用户付款明细查询",response = PayClientDetail.class)
    @PostMapping({"selectPayClientDetail"})
    public JsonResult selectPayClientDetail(@RequestBody PayClientInData inData) {
//        GetLoginOutData userInfo = getLoginUser(request);
//        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return JsonResult.success(this.payClientService.selectPayClientDetail(inData), "提示：获取数据成功！");
    }

    @Log("用户付款导出")
    @ApiOperation(value = "用户付款导出")
    @PostMapping({"/selectPayClientByCSV"})
    public Result InBoundCountForDayExportring(@RequestBody PayClientInData inData) throws IOException {
//        GetLoginOutData userInfo = this.getLoginUser(request);
//        inData.setClient(userInfo.getClient());
        List<PayClientOutData> outData = new ArrayList<>();
        List<PayClientDetail> itemOutData = new ArrayList<>();
        String fileName = "";
        Result result = null;
        if ("0".equals(inData.getIsShowDetail())) {
            outData = payClientService.selectPayClientListByCSV(inData);
            fileName = "用户付款记录";
        }else if("1".equals(inData.getIsShowDetail())){
            itemOutData = payClientService.selectPayClientDetailByCSV(inData);
            fileName = "用户付款明细";
        }
        if ("0".equals(inData.getIsShowDetail())) {
            if (outData.size() > 0) {
                CsvFileInfo csvInfo = CsvClient.getCsvByte(outData, fileName, Collections.singletonList((short) 1));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
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
            } else {
                return Result.error("导出失败");
            }
        }else if("1".equals(inData.getIsShowDetail())){
            if (itemOutData.size() > 0) {
                // 导出
                // byte数据
                CsvFileInfo csvInfo = CsvClient.getCsvByte(itemOutData, fileName, Collections.singletonList((short) 1));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
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
            } else {
                return Result.error("导出失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "用户列表查询（模糊匹配）")
    @PostMapping({"selectClientList"})
    public JsonResult selectClientList(@RequestBody PayClientInData inData) {
//        GetLoginOutData userInfo = getLoginUser(request);
//        inData.setClientId(userInfo.getClient());
//        inData.setClientId("10000005");
        return JsonResult.success(this.payClientService.selectClientList(inData.getClientId()), "提示：获取数据成功！");
    }
}

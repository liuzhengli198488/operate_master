package com.gys.controller;


import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.log.Log;
import com.gys.common.response.Result;
import com.gys.entity.InData;
import com.gys.entity.data.member.ActivateMemberCardOutput;
import com.gys.service.MemberService;
import com.gys.util.CosUtils;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
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
import java.util.List;


@RestController
@RequestMapping("/member")
public class MemberController extends BaseController {
    @Autowired
    private MemberService memberService;

    @Resource
    public CosUtils cosUtils;

    @PostMapping({"getActivateMemberCard"})
    public JsonResult getActivateMemberCard(HttpServletRequest request, @RequestBody InData inData) {
        GetLoginOutData userInfo = getLoginUser(request);
        inData.setClient(userInfo.getClient());
        return JsonResult.success(this.memberService.getActivateMemberCard(inData), "提示：获取数据成功！");
    }

    @Log("预备会员激活统计导出")
    @PostMapping({"/getActivateMemberCardCSV"})
    public Result getActivateMemberCardCSV(HttpServletRequest request, @RequestBody InData inData) throws IOException {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        List<ActivateMemberCardOutput> outData = memberService.getActivateMemberCardCSV(inData);
        String fileName = "预备会员激活统计导出";
        if (outData.size() > 0) {
            CsvFileInfo csvInfo =null;
            // 导出
            // byte数据
            csvInfo = CsvClient.getCsvByte(outData,fileName,null);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result result = null;
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
            return result;
        } else {
            throw new BusinessException("提示：没有查询到数据,请修改查询条件!");
        }
    }

}

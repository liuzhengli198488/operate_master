package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.data.DownLoadInData;
import com.gys.entity.data.ViewPictureInData;
import com.gys.entity.data.YaoJianInData;
import com.gys.service.GaiaWmsViewYaojianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/5 17:57
 */
@Slf4j
@RestController
@RequestMapping("/drugTest")
public class GaiaWmsViewYaojianController extends BaseController {
    @Resource
    private GaiaWmsViewYaojianService yaojianService;

    /**
     * 查询
     */
    @PostMapping("/getList")
    public JsonResult report(HttpServletRequest request, @RequestBody YaoJianInData inData) {
        GetLoginOutData user = getLoginUser(request);
        inData.setClient(user.getClient());
        return JsonResult.success(yaojianService.getList(inData), "查询成功");
    }

    /**
     * 查看
     */
    @PostMapping("/viewPicture")
    public JsonResult viewPicture(HttpServletRequest request, @RequestBody ViewPictureInData inData){
        GetLoginOutData user = getLoginUser(request);
        inData.setClient(user.getClient());
        return JsonResult.success(yaojianService.viewPicture(inData), "查询成功");
    }

    /**
     *打印
     */
    @PostMapping("/print")
    public JsonResult print(HttpServletRequest request, @RequestBody List<ViewPictureInData> inDataList) {
        GetLoginOutData user = getLoginUser(request);
        String client = user.getClient();
        return JsonResult.success(yaojianService.print(inDataList,client), "查询成功");
    }


    /**
     * 下载
     */
    @PostMapping("/downLoad")
    public void downLoad(HttpServletResponse response, HttpServletRequest request, @RequestBody List<DownLoadInData> inDataList){
        GetLoginOutData user = getLoginUser(request);
        String client = user.getClient();
        yaojianService.downLoad(response,inDataList,client);
    }

}

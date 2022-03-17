package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.entity.GaiaSdMessage;
import com.gys.entity.LoginMessageInData;
import com.gys.service.GaiaSdMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "登录消息提醒")
@RestController
@RequestMapping({"/loginMessage/"})
public class LoginMessageController extends BaseController {
    @Resource
    private GaiaSdMessageService gaiaSdMessageService;

    @ApiOperation(value = "消息提醒列表" ,response = GaiaSdMessage.class)
    @PostMapping({"listmessage"})
    public JsonResult list(HttpServletRequest request, @RequestBody LoginMessageInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClientId(userInfo.getClient());
        inData.setUserId(userInfo.getUserId());
        inData.setStoCode(userInfo.getDepId());
        return JsonResult.success(this.gaiaSdMessageService.messageList(inData), "提示：获取数据成功！");
    }

    @ApiOperation(value = "点击消息更新状态")
    @PostMapping({"modifyMessage"})
    public JsonResult modifyMessage(HttpServletRequest request, @RequestBody GaiaSdMessage inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setGsmPlatForm("WEB");
        this.gaiaSdMessageService.updateMessage(userInfo,inData);
        return JsonResult.success("", "提示：更新成功！");
    }
}

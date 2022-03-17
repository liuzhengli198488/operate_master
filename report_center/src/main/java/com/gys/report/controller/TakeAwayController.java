//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.report.entity.WebOrderDataDto;
import com.gys.report.entity.WebOrderDetailDataDto;
import com.gys.report.entity.WebOrderQueryBean;
import com.gys.report.service.TakeAwayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(
        tags = {"外卖订单"},
        description = "/takeAway"
)
@Controller
@RequestMapping({"/takeAway/"})
public class TakeAwayController extends BaseController {
    @Resource
    private TakeAwayService takeAwayService;

    public TakeAwayController() {
    }

    @ApiOperation(
            value = "O2O订单汇总查询-web",
            response = WebOrderDataDto.class
    )
    @ResponseBody
    @PostMapping({"orderQuery"})
    public JsonResult orderQuery(HttpServletRequest request, @RequestBody WebOrderQueryBean bean) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        bean.setClient(userInfo.getClient());
        return this.takeAwayService.orderQuery(bean);
    }

    @ApiOperation("O2O订单查询结果-导出")
    @PostMapping({"orderQueryOutput"})
    public void orderQueryOutput(HttpServletRequest request, HttpServletResponse response, @RequestBody WebOrderQueryBean bean) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        bean.setClient(userInfo.getClient());
        this.takeAwayService.orderQueryOutput(bean, request, response);
    }

    @ApiOperation(
            value = "O2O订单明细查询-web",
            response = WebOrderDetailDataDto.class
    )
    @ResponseBody
    @PostMapping({"orderDetailQuery"})
    public JsonResult orderDetailQuery(HttpServletRequest request, @RequestBody WebOrderQueryBean bean) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        bean.setClient(userInfo.getClient());
        return this.takeAwayService.orderDetailQuery(bean);
    }

    @ApiOperation("O2O订单明细结果-导出")
    @PostMapping({"orderDetailQueryOutput"})
    public void orderDetailQueryOutput(HttpServletRequest request, HttpServletResponse response, @RequestBody WebOrderQueryBean bean) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        bean.setClient(userInfo.getClient());
        this.takeAwayService.orderDetailQueryOutput(bean, request, response);
    }
}

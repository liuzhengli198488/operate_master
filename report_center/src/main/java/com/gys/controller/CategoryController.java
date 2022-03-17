package com.gys.controller;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.log.Log;
import com.gys.entity.data.Category.CategoryExecuteResponse;
import com.gys.entity.data.Category.CategoryStatisticResponse;
import com.gys.entity.data.Category.StoreTimeRequest;
import com.gys.entity.data.Category.SupplierCostStatisticResponse;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyBySalespersonInData;
import com.gys.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/4 11:23
 */

@ApiOperation("品类相关报表")
@RestController
@RequestMapping({"/category/"})
public class CategoryController extends BaseController {
    @Resource
    private CategoryService categoryService;

    @ApiOperation( value= "上周（月）品类执行情况",response = CategoryExecuteResponse.class)
    @Log("上周（月）品类执行情况")
    @PostMapping("categoryExecuteData")
    public JsonResult categoryExecuteData(HttpServletRequest request, @RequestBody StoreTimeRequest data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(categoryService.categoryExecuteData(data),"提示：获取数据成功");
    }

    @ApiOperation( value= "上周（月）引进、淘汰，调价品类情况数据汇总报表",response = CategoryStatisticResponse.class)
    @Log("上周（月）引进、淘汰，调价品类情况数据汇总报表")
    @PostMapping("categoryStatisticData")
    public JsonResult categoryStatisticData(HttpServletRequest request, @RequestBody StoreTimeRequest data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(categoryService.categoryStatisticData(data),"提示：获取数据成功");
    }

    @ApiOperation( value= "TOP10进货成本额供应商列表（周/月）",response = SupplierCostStatisticResponse.class)
    @Log("TOP10进货成本额供应商列表（周/月）")
    @PostMapping("supplierCostStatisticData")
    public JsonResult supplierCostStatisticData(HttpServletRequest request, @RequestBody StoreTimeRequest data){
        GetLoginOutData userInfo = this.getLoginUser(request);
        data.setClient(userInfo.getClient());
        return JsonResult.success(categoryService.supplierCostStatisticData(data),"提示：获取数据成功");
    }

}

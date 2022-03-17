package com.gys.controller;

import com.gys.common.data.JsonResult;
import com.gys.entity.ReplenishDiffSumInData;
import com.gys.entity.ReplenishDiffSumOutData;
import com.gys.service.ReplenishDiffSumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 *  @author SunJiaNan
 *  @version 1.0
 *  @date 2021/11/25 10:13
 *  @description 门店补货公式汇总报表
 */
@Api(tags = "门店补货公式汇总报表")
@RestController
@RequestMapping({"/replenish/web/"})
public class ReplenishDiffSumController {

    @Resource
    private ReplenishDiffSumService replenishDiffSumService;

    @ApiOperation(value = "门店补货公式差异汇总查询")
    @PostMapping({"getReplenishDiffSumList"})
    public JsonResult getReplenishDiffSumList(@RequestBody ReplenishDiffSumInData inData) {
        return replenishDiffSumService.getReplenishDiffSumList(inData);
    }


    @ApiOperation(value = "门店补货公式差异汇总-导出")
    @PostMapping({"exportReplenishDiffSum"})
    public JsonResult exportReplenishDiffSum(@RequestBody List<ReplenishDiffSumOutData> inData) {
        return replenishDiffSumService.exportReplenishDiffSum(inData);
    }

}

package com.gys.service;

import com.gys.common.data.JsonResult;
import com.gys.entity.ReplenishDiffSumInData;
import com.gys.entity.ReplenishDiffSumOutData;

import java.util.List;

/**
 *  @author SunJiaNan
 *  @version 1.0
 *  @date 2021/11/25 10:13
 *  @description 门店补货公式汇总报表
 */
public interface ReplenishDiffSumService {

    JsonResult getReplenishDiffSumList(ReplenishDiffSumInData inData);

    JsonResult exportReplenishDiffSum(List<ReplenishDiffSumOutData> replenishDiffSumList);
}

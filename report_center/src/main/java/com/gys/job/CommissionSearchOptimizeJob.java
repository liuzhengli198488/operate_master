package com.gys.job;

import com.alibaba.fastjson.JSONObject;
import com.gys.report.entity.SaveUserCommissionSummaryDetailInData;
import com.gys.service.IUserCommissionSummaryDetailService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CommissionSearchOptimizeJob {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private IUserCommissionSummaryDetailService userCommissionSummaryDetailService;

    /**
     * 统计每天员工销售提成
     */
    @XxlJob("commissionSearchOptimize")
    public void commissionSearchOptimize() {
        log.info("<定时任务><统计每天员工销售提成><任务调用成功>");
        String summaryDetail = XxlJobHelper.getJobParam();
        //json->SaveUserCommissionSummaryDetailInData
        SaveUserCommissionSummaryDetailInData inData = JSONObject.parseObject(summaryDetail, SaveUserCommissionSummaryDetailInData.class);
        threadPoolTaskExecutor.execute(() -> userCommissionSummaryDetailService.saveUserCommissionSummaryDetail(inData));
        XxlJobHelper.handleSuccess("调用成功");
    }
}

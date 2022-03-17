package com.gys.job;

import com.gys.service.GaiaXhlJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/9/1 14:10
 */
@Slf4j
@Component
public class DropRateJob {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private GaiaXhlJobService gaiaXhlJobService;

    /**
     * 统计下货率报表
     */
    @XxlJob("countStoreXhlByDate")
    public void countStoreXhlByDate() {
        log.info("<定时任务><统计下货率><任务调用成功>");
        String tjDate = XxlJobHelper.getJobParam();
        threadPoolTaskExecutor.execute(() -> gaiaXhlJobService.execXhlTask(tjDate));
        XxlJobHelper.handleSuccess("调用成功");
    }

    @XxlJob("countStoreXhlByWeek")
    public void countStoreXhlByWeek() {
        log.info("<定时任务><统计下货率><任务调用成功>");
        threadPoolTaskExecutor.execute(() -> gaiaXhlJobService.execWeekTask());
        XxlJobHelper.handleSuccess("调用成功");
    }
}

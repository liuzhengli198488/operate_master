package com.gys.job;

import com.gys.service.MaterialMonitorJobService;
import com.gys.util.DateUtil;
import com.gys.util.ValidateUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * @Author jiht
 * @Description 物料凭证监控
 * @Date 2022/2/21 13:21
 * @Param
 * @Return
 **/
@Slf4j
@Component
public class MaterialMonitorJob {

    @Autowired
    private MaterialMonitorJobService materialMonitorJobService;

    @XxlJob("materialMonitorJob")
    public ReturnT<String> materialMonitor() {
        XxlJobHelper.log("===============[物料凭证监控]定时任务开始===============");
        String param = XxlJobHelper.getJobParam();

        String batchDate = (param == null || StringUtils.isEmpty(param)) ? DateUtil.formatDate(new Date(), "yyyyMMdd") : param;
        log.info("[物料凭证监控]批处理日期：{}", batchDate);
        if (ValidateUtil.isEmpty(batchDate)) {
            log.info("=========[物料凭证监控]批处理时间获取异常=========");
            return null;
        }

        log.info("[物料凭证监控]物料凭证监控建议数据提取");
        materialMonitorJobService.saveMonitorData(batchDate);

        XxlJobHelper.log("===============[物料凭证监控]定时任务结束===============");
        return ReturnT.SUCCESS;
    }

}

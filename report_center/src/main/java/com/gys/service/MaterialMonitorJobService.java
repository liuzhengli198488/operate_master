package com.gys.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * @Author jiht
 * @Description
 * @Date 2022/2/21 13:55
 * @Param
 * @Return
 **/
public interface MaterialMonitorJobService {

    /**
     * @Author jiht
     * @Description 数据提取
     * @Date 2022/2/21 13:54
     * @Param [batchDate]
     * @Return void
     **/
    @Transactional(rollbackFor = Exception.class)
    void saveMonitorData(String batchDate);

}

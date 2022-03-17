package com.gys.service;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/9/1 14:17
 */
public interface GaiaXhlJobService {

    void execXhlTask(String tjDate);

    void execWeekTask();

    /**
     * 修改品项平均下货率
     */
    void update(String startDate,String endDate);
}

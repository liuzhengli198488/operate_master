package com.gys.service;

import com.gys.report.entity.SaveUserCommissionSummaryDetailInData;

/**
 * <p>
 * 用户提成明细 服务类
 * </p>
 *
 * @author yifan.wang
 * @since 2022-02-11
 */
public interface IUserCommissionSummaryDetailService {

    /**
     * 定时计算昨日提成数据
     * @param inData 请求参数
     */
    void saveUserCommissionSummaryDetail(SaveUserCommissionSummaryDetailInData inData);

}

package com.gys.service;

import com.gys.common.enums.SerialCodeTypeEnum;

public interface CommonService {

    /**
     * 获取加盟商维度流水号
     * @param client 加盟商
     * @param serialCodeTypeEnum 流水号类型
     * @return 流水号
     */
    String getSerialCode(String client, SerialCodeTypeEnum serialCodeTypeEnum);

    /**
     * 获取门店维度流水号
     * @param client  加盟商
     * @param stoCode 门店编码
     * @param serialCodeTypeEnum 流水号类型
     * @return 流水号
     */
    String getSdSerialCode(String client, String stoCode, SerialCodeTypeEnum serialCodeTypeEnum);
}

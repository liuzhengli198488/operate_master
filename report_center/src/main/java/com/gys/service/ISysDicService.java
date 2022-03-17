package com.gys.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.entity.SysDic;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统码表，加盟商 门店编号可以不填写，不填写情况下表示全局级别的码表 服务类
 * </p>
 *
 * @author flynn
 * @since 2022-01-29
 */
public interface ISysDicService {

    List<SysDic> getSysDicListByTypeCode(String client, String stoCode, String typeCode);
}

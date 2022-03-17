package com.gys.util;

import com.gys.common.exception.BusinessException;
import com.gys.mapper.GaiaGlobalDataMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author ：liuzhiwen.
 * @Date ：Created in 14:04 2021/8/25
 * @Description：
 * @Modified By：liuzhiwen.
 * @Version:
 */
@Component
public class ExportStatusUtil {

    private static GaiaGlobalDataMapper gaiaGlobalDataMapper;

    @Autowired
    public void setGaiaGlobalDataMapper(GaiaGlobalDataMapper gaiaGlobalDataMapper) {
        this.gaiaGlobalDataMapper = gaiaGlobalDataMapper;
    }

    public static void checkExportAuthority(String client, String stoCode) {
        String globalType = gaiaGlobalDataMapper.globalTypeReport(client, stoCode, "5");
        if (StringUtils.isEmpty(globalType) || "0".equals(globalType)) {
            throw new BusinessException("该门店未配置导出权限！");
        }
    }
}

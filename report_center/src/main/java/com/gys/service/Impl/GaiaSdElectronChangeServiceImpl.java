package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.common.data.SdElectronChangeInData;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.entity.GaiaSdElectronChange;
import com.gys.mapper.GaiaSdElectronChangeMapper;
import com.gys.service.GaiaSdElectronChangeService;
import com.gys.util.CosUtils;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2022/1/7 15:57
 */
@Slf4j
@Service
public class GaiaSdElectronChangeServiceImpl implements GaiaSdElectronChangeService {
    @Resource
    private GaiaSdElectronChangeMapper sdElectronChangeMapper;
    @Resource
    public CosUtils cosUtils;

    @Override
    public PageInfo<GaiaSdElectronChange> getElectronChange(SdElectronChangeInData inData) {
        PageInfo pageInfo;
        if (ObjectUtil.isNotNull(inData.getPageNum()) && ObjectUtil.isNotNull(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        }
        List<GaiaSdElectronChange> outData = sdElectronChangeMapper.getElectronChange(inData);
        if (CollectionUtils.isEmpty(outData)){
            pageInfo = new PageInfo(new ArrayList());
        }else {
            pageInfo = new PageInfo(outData);
        }
        return pageInfo;
    }

    @Override
    public Result exportCSV(SdElectronChangeInData inData) {
        Result result;
        List<GaiaSdElectronChange> list = sdElectronChangeMapper.getElectronChange(inData);
        String name = "会员送券与用券导出";
        if (list.size() > 0){
            CsvFileInfo csvFileInfo = CsvClient.getCsvByte(list,name, Collections.singletonList((short) 1));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                assert csvFileInfo != null;
                outputStream.write(csvFileInfo.getFileContent());
                result = cosUtils.uploadFile(outputStream, csvFileInfo.getFileName());
            } catch (IOException e){
                log.error("导出文件失败:{}",e.getMessage(), e);
                throw new BusinessException("导出文件失败！");
            } finally {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.error("关闭流异常：{}",e.getMessage(),e);
                    throw new BusinessException("关闭流异常！");
                }
            }
            return result;
        }else{
            throw new BusinessException("提示：数据为空！");
        }
    }
}

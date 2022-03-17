package com.gys.service.Impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.data.MaterialDocumentInData;
import com.gys.common.data.MaterialDocumentOutData;
import com.gys.common.data.PageInfo;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyBySalespersonOutData;
import com.gys.entity.data.MonthPushMoney.MonthPushMoneyBySalespersonOutTotal;
import com.gys.mapper.MaterialDocumentMapper;
import com.gys.service.MaterialDocumentService;
import com.gys.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class MaterialDocumentServiceImpl implements MaterialDocumentService {

    @Autowired
    private MaterialDocumentMapper materialDocumentMapper;

    @Override
    public PageInfo<List<MaterialDocumentOutData>> noLogList(MaterialDocumentInData inData) {
        List<MaterialDocumentOutData> outData = new ArrayList<>();
        //处理 单号查询条件
        if (!StrUtil.isBlankOrUndefined(inData.getMatDnId())) {
            String[] matDnIdList = inData.getMatDnId().split(",");
            inData.setMatDnIdList(matDnIdList);
        }
        if (!StrUtil.isBlankOrUndefined(inData.getMatPoId())) {
            String[] matPoIdList = inData.getMatPoId().split(",");
            inData.setMatDnIdList(matPoIdList);
        }
        if ("1".equals(inData.getSuccessFlag())) {
            //所有成功的凭证
            outData = materialDocumentMapper.listSuccessDocument(inData);
        } else {
            switch (StrUtil.isBlank(inData.getType())?"":inData.getType()) {
                case "CD":
                    outData = materialDocumentMapper.listErrorForCDOrCX(inData);
                    break;
                case "CX":
                    outData = materialDocumentMapper.listErrorForCDOrCX(inData);
                    break;
                case "PD":
                    outData = materialDocumentMapper.listErrorForPDOrPXOrXD(inData);
                    break;
                case "PX":
                    outData = materialDocumentMapper.listErrorForPDOrPXOrXD(inData);
                    break;
                case "XD":
                    outData = materialDocumentMapper.listErrorForPDOrPXOrXD(inData);
                    break;
                case "TD":
                    outData = materialDocumentMapper.listErrorForTD(inData);
                    break;
                case "GD":
                    outData = materialDocumentMapper.listErrorForGD(inData);
                    break;
                case "LS":
                    outData = materialDocumentMapper.listErrorForLSOrLX(inData);
                    break;
                case "LX":
                    outData = materialDocumentMapper.listErrorForLSOrLX(inData);
                    break;
                default:
                    //默认 已调用接口成功，未生成凭证
                    outData = materialDocumentMapper.noLogList(inData);
            }
        }


        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            pageInfo = new PageInfo(outData);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public List<HashMap<String, Object>> listClient(MaterialDocumentInData inData) {
        return materialDocumentMapper.listClient();
    }

    @Override
    public List<HashMap<String, Object>> listClientDCAndStorage(MaterialDocumentInData inData) {
        return materialDocumentMapper.listClientDCAndStorage(inData);
    }
}

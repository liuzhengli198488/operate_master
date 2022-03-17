package com.gys.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.GaiaResource;
import com.gys.entity.GaiaSdMessage;
import com.gys.entity.LoginMessageInData;
import com.gys.mapper.GaiaSdMessageMapper;
import com.gys.service.GaiaSdMessageService;
import com.gys.util.CommonUtil;
import com.gys.util.UtilMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GaiaSdMessageServiceImpl implements GaiaSdMessageService {

    @Resource
    private GaiaSdMessageMapper gaiaSdMessageMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMessage(GetLoginOutData userInfo, GaiaSdMessage inData) {
        SimpleDateFormat format = new SimpleDateFormat(UtilMessage.HHMMSS);
        inData.setClient(userInfo.getClient());
//        inData.setGsmId(userInfo.getDepId());
        inData.setGsmCheckEmp(userInfo.getUserId());
        inData.setGsmCheckDate(DateUtil.format(new Date(), UtilMessage.YYYYMMDD));
//        inData.setGsmCheckTime(format.format(new Date()));
        inData.setGsmCheckTime(CommonUtil.getHHmmss());
        inData.setGsmFlag(UtilMessage.Y);
        GaiaSdMessage message = gaiaSdMessageMapper.getUnique(inData);
        if (message != null && ("GAIA_MM_010309".equals(message.getGsmType()) || "GAIA_MM_010311".equals(message.getGsmType())||"GAIA_WF_0103".equals(message.getGsmType()))) {
            inData.setGsmDeleteFlag("1");
            //新品消息/周新品消息是否阅读
            try {
                editReadFlag(message);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("<新品消息/周新品消息是否阅读><保存失败>message:{}", message);
            }

        }
        this.gaiaSdMessageMapper.updateByPrimaryKeySelective(inData);

    }

    @Async
    void editReadFlag(GaiaSdMessage message) {
        if ("GAIA_MM_010309".equals(message.getGsmType()) && "Y".equals(message.getGsmBusinessVoucherId())) {
            //新品消息
            Set<String> proCodeSet = Arrays.stream(message.getGsmWarningDay().split(",")).collect(Collectors.toSet());
            proCodeSet.forEach(proCode -> {
                gaiaSdMessageMapper.updateReadFlag(message.getClient(), proCode);
            });
        } else if ("GAIA_MM_010309".equals(message.getGsmType()) && !("Y".equals(message.getGsmBusinessVoucherId()))) {
            //周新品消息
            Set<String> weekProCodeSet = Arrays.stream(message.getGsmWarningDay().split(",")).collect(Collectors.toSet());
            weekProCodeSet.forEach(weekProCode -> {
                gaiaSdMessageMapper.updateWeekReadFlag(message.getClient(), weekProCode);
            });
        }
        log.info("<新品消息/周新品消息是否阅读><保存成功>");
    }

    @Override
    public PageInfo<GaiaSdMessage> messageList(LoginMessageInData inData) {
        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(inData.getPageNum()) && ObjectUtil.isNotEmpty(inData.getPageSize())) {
            PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
            List<GaiaSdMessage> outData = new ArrayList<>();
            //门店消息
            List<GaiaSdMessage> stoOutData = gaiaSdMessageMapper.listMessage(inData.getClientId(), inData.getStoCode(), "WEB");
            if (ObjectUtil.isNotEmpty(stoOutData) && stoOutData.size() > 0) {
                outData.addAll(stoOutData);
            }
            //仓库消息
            List<GaiaSdMessage> dcOutData = gaiaSdMessageMapper.listMessageDc(inData.getClientId(), inData.getStoCode(), "WEB");
            if (ObjectUtil.isNotEmpty(dcOutData) && dcOutData.size() > 0) {
                outData.addAll(dcOutData);
            }
            //商品消息
            List<GaiaSdMessage> proOutData = gaiaSdMessageMapper.listMessage(inData.getClientId(), "product", "WEB");
            if (ObjectUtil.isNotEmpty(proOutData) && proOutData.size() > 0) {
                outData.addAll(proOutData);
            }
            //证照消息
            List<GaiaSdMessage> zzOutData = gaiaSdMessageMapper.listMessageZz(inData.getClientId(),"WEB");
            outData=outData.stream().filter(out->!"GAIA_SD_0222".equals(out.getGsmType())).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(zzOutData) && zzOutData.size() > 0) {
                outData.addAll(zzOutData);
            }
            List<GaiaResource> authConfigDataList = gaiaSdMessageMapper.selectAuthConfig(inData.getClientId(), inData.getUserId());
            List<GaiaSdMessage> result = new ArrayList<>();
            for (GaiaResource au : authConfigDataList) {
                for (GaiaSdMessage data : outData) {
                    if (data.getGsmType().equals(au.getId())) {
                        result.add(data);
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(result)) {
                pageInfo = new PageInfo(result);
            } else {
                pageInfo = new PageInfo();
            }
        }
        return pageInfo;
    }

    @Transactional
    @Override
    public void addMessage(GaiaSdMessage gaiaSdMessage) {
        try {
            gaiaSdMessageMapper.insertSelective(gaiaSdMessage);
        } catch (Exception e) {
            throw new BusinessException("新增消息异常", e);
        }
    }
}

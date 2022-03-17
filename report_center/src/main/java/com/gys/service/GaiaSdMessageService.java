package com.gys.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.entity.GaiaSdMessage;
import com.gys.entity.LoginMessageInData;


public interface GaiaSdMessageService {


    /**
     * 修改
     * @param userInfo
     * @param inData
     */
    void updateMessage(GetLoginOutData userInfo, GaiaSdMessage inData);

    PageInfo<GaiaSdMessage> messageList(LoginMessageInData inData);

    void addMessage(GaiaSdMessage gaiaSdMessage);

}

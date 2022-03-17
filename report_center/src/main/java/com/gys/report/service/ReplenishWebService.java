//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.response.Result;
import com.gys.report.entity.GetReplenishInData;
import java.util.HashMap;

public interface ReplenishWebService {
    HashMap<String, Object> listDifferentReplenish(GetReplenishInData inData, GetLoginOutData userInfo);

    HashMap<String, Object> listDifferentReplenishDetail(GetReplenishInData inData, GetLoginOutData userInfo);

    Result exportData(GetReplenishInData inData, GetLoginOutData userInfo);
}

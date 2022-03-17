package com.gys.report.service;

import com.gys.report.entity.GetPayInData;
import com.gys.report.entity.GetPayTypeOutData;

import java.util.List;
import java.util.Map;

public interface PayService {

    List<GetPayTypeOutData> payTypeListByClient(GetPayInData inData);
}

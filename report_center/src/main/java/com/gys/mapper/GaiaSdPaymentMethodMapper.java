package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.GaiaSdPaymentMethod;
import com.gys.report.entity.GetPayInData;
import com.gys.report.entity.GetPayTypeOutData;

import java.util.List;

public interface GaiaSdPaymentMethodMapper extends BaseMapper<GaiaSdPaymentMethod> {

    List<GetPayTypeOutData> payTypeListByClient(GetPayInData inData);

}

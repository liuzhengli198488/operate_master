//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.GaiaTOrderInfo;
import com.gys.report.entity.WebOrderDataDto;
import com.gys.report.entity.WebOrderDetailDataDto;
import com.gys.report.entity.WebOrderQueryBean;
import java.util.List;

public interface GaiaTOrderInfoMapper extends BaseMapper<GaiaTOrderInfo> {
    List<WebOrderDataDto> orderQuery(WebOrderQueryBean bean);

    List<WebOrderDetailDataDto> orderDetailQuery(WebOrderQueryBean bean);
}

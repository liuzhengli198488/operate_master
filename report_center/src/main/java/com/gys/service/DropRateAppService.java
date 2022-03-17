package com.gys.service;

import com.gys.controller.app.dto.DistributeGoodsDto;
import com.gys.controller.app.vo.DayDropRateVO;
import com.gys.controller.app.vo.DropRateVO;
import com.gys.controller.app.vo.MonthDropRateVO;
import com.gys.controller.app.vo.WeekDropRateVO;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/29 23:41
 */
public interface DropRateAppService {

    DropRateVO lastTimeData(String client, DistributeGoodsDto dto);

    DayDropRateVO dayInfo(String client, DistributeGoodsDto dto);

    WeekDropRateVO weekInfo(String client, DistributeGoodsDto dto);

    MonthDropRateVO monthInfo(String client, DistributeGoodsDto dto);

    void updateViewSource(String client, Integer type);
}

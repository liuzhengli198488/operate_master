package com.gys.controller.app;

import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.enums.ViewSourceEnum;
import com.gys.controller.app.dto.DistributeGoodsDto;
import com.gys.controller.app.vo.DayDropRateVO;
import com.gys.controller.app.vo.DropRateVO;
import com.gys.controller.app.vo.MonthDropRateVO;
import com.gys.controller.app.vo.WeekDropRateVO;
import com.gys.service.DropRateAppService;
import com.gys.service.GaiaXhlJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_MESSAGE;

/**
 * @desc: 下货率模块
 * @author: Ryan
 * @createTime: 2021/8/29 00:09
 */
@Slf4j
@RestController
@RequestMapping("app/dropRate")
public class DropRateAppController extends BaseController {
    @Resource
    private DropRateAppService dropRateAppService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 下货率信息【上次】
     */
    @PostMapping("dayInfo")
    public JsonResult<DropRateVO> lastTimeData( HttpServletRequest request,@RequestBody  DistributeGoodsDto dto) {
        DropRateVO dropRateVO;
        try {
            GetLoginOutData loginUser = getLoginUser(request);
            // 增加判空
            if (loginUser == null || StringUtils.isBlank(loginUser.getClient())) {
                return JsonResult.error(ERROR_MESSAGE);
            }
            if (dto == null || dto.getTag() == null) {
                DistributeGoodsDto goodsDto = new DistributeGoodsDto();
                goodsDto.setTag(0);
                dropRateVO = dropRateAppService.lastTimeData(loginUser.getClient(), goodsDto);
            } else {
                dropRateVO = dropRateAppService.lastTimeData(loginUser.getClient(), dto);
            }
            //异步更新查看标记
            threadPoolTaskExecutor.execute(() -> dropRateAppService.updateViewSource(loginUser.getClient(), ViewSourceEnum.APP.type));
        } catch (Exception e) {
            log.error("<下货率><日下货率><统计异常：{}>", e.getMessage(), e);
            dropRateVO =  new DropRateVO();
        }
        return JsonResult.success(dropRateVO, "查询成功");
    }

    /**
     * 下货率趋势【最近一周】  主动铺货
     */
    @PostMapping("weekInfo")
    public JsonResult<DayDropRateVO> dayInfo(HttpServletRequest request,@RequestBody  DistributeGoodsDto dto) {
        DayDropRateVO dayDropRateVO;
        try {
            GetLoginOutData loginUser = getLoginUser(request);
            if(dto==null||dto.getTag()==null){
                DistributeGoodsDto goodsDto=new DistributeGoodsDto();
                goodsDto.setTag(0);
                dayDropRateVO = dropRateAppService.dayInfo(loginUser.getClient(),goodsDto);
            }else {
                dayDropRateVO = dropRateAppService.dayInfo(loginUser.getClient(),dto);
            }
        } catch (Exception e) {
            log.error("<下货率><周下货率><统计异常：{}>", e.getMessage(), e);
            dayDropRateVO = new DayDropRateVO();
        }
        return JsonResult.success(dayDropRateVO, "查询成功");
    }

    /**
     * 最终下货率情况【前七周】
     */
    @PostMapping("monthInfo")
    public JsonResult<WeekDropRateVO> weekInfo(HttpServletRequest request, @RequestBody DistributeGoodsDto dto) {
        WeekDropRateVO weekDropRateVO;
        try {
            GetLoginOutData loginUser = getLoginUser(request);
            if (dto == null || dto.getTag() == null) {
                DistributeGoodsDto goodsDto = new DistributeGoodsDto();
                goodsDto.setTag(0);
                weekDropRateVO = dropRateAppService.weekInfo(loginUser.getClient(), goodsDto);
            } else {
                weekDropRateVO = dropRateAppService.weekInfo(loginUser.getClient(), dto);
            }
        } catch (Exception e) {
            log.error("<下货率><月下货率><统计异常：{}>", e.getMessage(), e);
            weekDropRateVO = new WeekDropRateVO();
        }
        return JsonResult.success(weekDropRateVO, "查询成功");
    }

    /**
     * 最终下货率情况【当年】
     */
    @PostMapping("yearInfo")
    public JsonResult<MonthDropRateVO> monthInfo( HttpServletRequest request, @RequestBody DistributeGoodsDto dto) {
        MonthDropRateVO monthDropRateVO;
        try {
            GetLoginOutData loginUser = getLoginUser(request);
            if(dto==null||dto.getTag()==null){
                DistributeGoodsDto goodsDto=new DistributeGoodsDto();
                goodsDto.setTag(0);
                monthDropRateVO = dropRateAppService.monthInfo(loginUser.getClient(),goodsDto);
            }else {
                monthDropRateVO = dropRateAppService.monthInfo(loginUser.getClient(),dto);
            }
        } catch (Exception e) {
            log.error("<下货率><年下货率><统计异常：{}>", e.getMessage(), e);
            monthDropRateVO = new MonthDropRateVO();
        }
        return JsonResult.success(monthDropRateVO, "查询成功");
    }

    @Resource
    private GaiaXhlJobService gaiaXhlJobService;

    @PostMapping("execJob")
    public JsonResult execJob(@RequestParam("date") String date) {
        gaiaXhlJobService.execXhlTask(date);
        return JsonResult.success(null, "成功");
    }
    @PostMapping("update")
    public JsonResult update(String startDate,String endDate){
        gaiaXhlJobService.update(startDate,endDate);
        return JsonResult.success(null, "成功");
    }
}

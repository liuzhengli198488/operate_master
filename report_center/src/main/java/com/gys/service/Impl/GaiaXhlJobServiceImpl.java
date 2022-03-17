package com.gys.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.util.StringUtil;
import com.gys.entity.GaiaXhlH;
import com.gys.mapper.GaiaXhlDMapper;
import com.gys.mapper.GaiaXhlHMapper;
import com.gys.service.DropRateService;
import com.gys.service.GaiaXhlHService;
import com.gys.service.GaiaXhlJobService;
import com.gys.util.CommonUtil;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/9/1 14:19
 */
@Slf4j
@Service("gaiaXhlJobService")
public class GaiaXhlJobServiceImpl implements GaiaXhlJobService {

    @Resource
    private GaiaXhlDMapper gaiaXhlDMapper;
    @Resource
    private GaiaXhlHMapper gaiaXhlHMapper;
    @Resource
    private GaiaXhlHService gaiaXhlHService;
    @Resource
    private DropRateService dropRateService;
    @Override
    public void execXhlTask(String tjDate) {
        //获取加盟商列表
        List<String> clientList = gaiaXhlDMapper.getClientList();
        if (clientList == null || clientList.size() == 0) {
            return;
        }
        Date date = StringUtil.isEmpty(tjDate) ? DateUtil.addDay(new Date(), -1) : DateUtil.transformDate(tjDate, "yyyy-MM-dd");
        //获取行业平均下货率（随机值）
        Map<String, BigDecimal> industryAverage = dropRateService.getIndustryAverage(null);
        for (String client : clientList) {
            try {
                gaiaXhlHService.saveStoreXhl(client, date, industryAverage);
            } catch (Exception e) {
                log.error("<定时任务><统计下货率><执行异常：{}>", e.getMessage(), e);
            }
        }
    }

    @Override
    public void execWeekTask() {
        Date currentDate = new Date();
        Date before2 = DateUtil.addDay(currentDate, -2);
        Date before3 = DateUtil.addDay(currentDate, -3);
        Date before4 = DateUtil.addDay(currentDate, -4);
        Date before5 = DateUtil.addDay(currentDate, -5);
        Date before6 = DateUtil.addDay(currentDate, -6);
        Date before7 = DateUtil.addDay(currentDate, -7);
        String[] dateArray = new String[]{DateUtil.formatDate(before2, "yyyy-MM-dd"), DateUtil.formatDate(before3, "yyyy-MM-dd"),
                DateUtil.formatDate(before4, "yyyy-MM-dd"), DateUtil.formatDate(before5, "yyyy-MM-dd"),
                DateUtil.formatDate(before6, "yyyy-MM-dd"), DateUtil.formatDate(before7, "yyyy-MM-dd")};
        for (String date : dateArray) {
            try {
                execXhlTask(date);
            } catch (Exception e) {
                log.error("<定时任务><统计下货率><补充任务>", e);
            }
        }
    }

    /**
     * 修改品项平均下货率
     */
    @Override
    public void update(String startDate,String endDate) {
        //获取
        Example example = new Example(GaiaXhlH.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andBetween("tjDate", com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(startDate, "yyyy-MM-dd"), "yyyy-MM-dd 00:00:00"), com.gys.util.DateUtil.formatDate(com.gys.util.DateUtil.transformDate(endDate, "yyyy-MM-dd"), "yyyy-MM-dd 23:59:59"));
        List<GaiaXhlH> dbList = this.gaiaXhlHMapper.selectByExample(example);
        if(CollUtil.isNotEmpty(dbList)){
            Map<Date, List<GaiaXhlH>> map = dbList.stream().collect(Collectors.groupingBy(s -> s.getTjDate()));
            for (Map.Entry<Date, List<GaiaXhlH>> stringListEntry : map.entrySet()) {
                Map<String, BigDecimal> industryAverage = dropRateService.getIndustryAverage(null);
                List<GaiaXhlH> value = stringListEntry.getValue();
                if(CollUtil.isNotEmpty(value)){
                    for (GaiaXhlH gaiaXhlH : value) {
                        if (Objects.equals(gaiaXhlH.getTjType(), 1)) {
                            gaiaXhlH.setIndustryAverage(industryAverage.get("billingIndustryAverage"));
                        } else if (Objects.equals(gaiaXhlH.getTjType(), 2)) {
                            gaiaXhlH.setIndustryAverage(industryAverage.get("depotIndustryAverage"));
                        } else if (Objects.equals(gaiaXhlH.getTjType(), 3)) {
                            gaiaXhlH.setIndustryAverage(industryAverage.get("finalIndustryAverage"));
                        }
                        gaiaXhlHMapper.update(gaiaXhlH);
                    }
                }
            }
        }
    }
}

package com.gys.service.Impl;

import com.gys.entity.data.Category.CategoryExecuteResponse;
import com.gys.entity.data.Category.CategoryStatisticResponse;
import com.gys.entity.data.Category.StoreTimeRequest;
import com.gys.entity.data.Category.SupplierCostStatisticResponse;
import com.gys.mapper.AppReportMapper;
import com.gys.mapper.CategoryMapper;
import com.gys.service.CategoryService;
import com.gys.util.DateUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/4 13:39
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private AppReportMapper appReportMapper;
    @Resource
    private CategoryMapper categoryMapper;

    @Override
   public CategoryExecuteResponse categoryExecuteData(StoreTimeRequest request){
        CategoryExecuteResponse response=new CategoryExecuteResponse();
        request =getDateRange(request);
        if(request==null){return null;}
        response=categoryMapper.categoryExecuteData(request);
        if(response!=null){
            response.setDiffAdjust(response.getRecommendAdjust().subtract(response.getActualAdjust()));
            response.setDiffOut(response.getRecommendOut().subtract(response.getActualOut()));
            response.setDiffAdImport(response.getRecommendImport().subtract(response.getActualImport()));
        }else{
            response=new CategoryExecuteResponse();
        }
        return response;
    }

    @Override
    public List<CategoryStatisticResponse> categoryStatisticData(StoreTimeRequest request){
        List<CategoryStatisticResponse> data=new ArrayList<>();
        request =getDateRange(request);
        if(request==null){return null;}
        List<CategoryStatisticResponse> firstList=categoryMapper.categoryStatisticData(request);
        if(request.getWeekOrMonth()==1){
            request.setEndDate(DateUtil.formatDate(DateUtil.addDay(request.getStartDate(),"yyyy-MM-dd",-1),"yyyy-MM-dd"));
            request.setStartDate(DateUtil.formatDate(DateUtil.addMonth(request.getStartDate(),"yyyy-MM-dd",-1),"yyyy-MM-dd"));
        }else{
            request.setStartDate(DateUtil.formatDate(DateUtil.addDay(request.getStartDate(),"yyyy-MM-dd",-7),"yyyy-MM-dd"));
            request.setEndDate(DateUtil.formatDate(DateUtil.addDay(request.getEndDate(),"yyyy-MM-dd",-7),"yyyy-MM-dd"));
        }
        List<CategoryStatisticResponse> secondList=categoryMapper.categoryStatisticData(request);

        for (int i = 1; i<4; i++){
            CategoryStatisticResponse item=new CategoryStatisticResponse();
            item.setType(i);
            List<CategoryStatisticResponse>  first=firstList==null?null:firstList.stream().filter(s-> s.getType().equals(item.getType())).collect(Collectors.toList());
            if (first!=null&&first.stream().count()>0){
                item.setSalesAmount(new BigDecimal(first.get(0).getSalesAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                item.setOrderCount(first.get(0).getOrderCount());
                item.setProfitAmount(new BigDecimal(first.get(0).getProfitAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
            }else{
                item.setSalesAmount(BigDecimal.ZERO.toString());
                item.setOrderCount(0);
                item.setProfitAmount(BigDecimal.ZERO.toString());
            }
            List<CategoryStatisticResponse>  second=secondList==null?null:secondList.stream().filter(s-> s.getType().equals(item.getType())).collect(Collectors.toList());
            if (second!=null&&second.stream().count()>0){
                //环比：（本期数－上期数）/上期数×100%
                BigDecimal salesAmountA=new BigDecimal(item.getSalesAmount());
                BigDecimal salesAmountB=new BigDecimal(second.get(0).getSalesAmount());
                item.setSalesAmount(salesAmountA.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                item.setSalesAmountCompare(((salesAmountA.subtract(salesAmountB)).divide(salesAmountB).multiply(new BigDecimal(100))).intValue());
                item.setOrderCountCompare(((item.getOrderCount()-second.get(0).getOrderCount())/(second.get(0).getOrderCount())*100));
                BigDecimal profitAmountA=new BigDecimal(item.getProfitAmount());
                BigDecimal profitAmountB=new BigDecimal(second.get(0).getProfitAmount());
                item.setProfitAmount(profitAmountA.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                item.setProfitAmountCompare(((profitAmountA.subtract(profitAmountB)).divide(profitAmountB).multiply(new BigDecimal(100))).intValue());
             }else{
                item.setSalesAmountCompare(item.getSalesAmount().equals("0")?0: 100);
                item.setOrderCountCompare(item.getOrderCount()==0?0: 100);
                item.setProfitAmountCompare(item.getProfitAmount().equals("0")?0:100);
            }
            data.add(item);
        }

        return data;
    }

    @Override
    public List<SupplierCostStatisticResponse> supplierCostStatisticData(StoreTimeRequest request){
        List<SupplierCostStatisticResponse> data=new ArrayList<>();
        request =getDateRange(request);
        if(request==null){return null;}
        data=categoryMapper.supplierCostStatisticData(request);
        for(SupplierCostStatisticResponse item : data){
            BigDecimal bd=new BigDecimal(item.getCost());
            item.setCost(bd.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        }
        return data;
    }

    private StoreTimeRequest getDateRange(StoreTimeRequest request) {
        if (request.getChooseType() == 3) {
            if (request.getYear() == 0||request.getWeekOrMonth()==0) {
                return null;
            }
            Map<String, String> result = appReportMapper.selectDateTime(request.getYear(), request.getWeekOrMonth() == 1 ? request.getNumber() : 0, request.getWeekOrMonth() == 2 ? request.getNumber() : 0);
            if(result==null){
                return null;
            }
            request.setStartDate(result.get("minDate"));
            request.setEndDate(result.get("maxDate"));
        }else if(request.getChooseType()==2){
            if(request.getWeekOrMonth()==1) {
                Date date = new Date();
                request.setStartDate(DateUtil.formatDate(new Date(date.getYear(), date.getMonth() - 1, 1), "yyyy-MM-dd"));
                request.setEndDate(DateUtil.formatDate(DateUtil.addDay(DateUtil.addMonth(request.getStartDate(), "yyyy-MM-dd", 1), -1), "yyyy-MM-dd"));
            }else{
                Date date = new Date();
                int day=date.getDay()==0?7:date.getDay();
                request.setEndDate(DateUtil.formatDate(DateUtil.addDay(date,0-day),"yyyy-MM-dd"));
                request.setStartDate(DateUtil.formatDate(DateUtil.addDay(date,0-(day+7)),"yyyy-MM-dd"));
            }
        }else if(request.getChooseType()==1){
            if(request.getWeekOrMonth()==1) {
                Date date = new Date();
                request.setStartDate(DateUtil.formatDate(new Date(date.getYear(), date.getMonth() , 1), "yyyy-MM-dd"));
                request.setEndDate(DateUtil.formatDate(DateUtil.addDay(DateUtil.addMonth(request.getStartDate(), "yyyy-MM-dd", 1), -1), "yyyy-MM-dd"));
            }else{
                Date date = new Date();
                int day=date.getDay()==0?7:date.getDay();
                request.setEndDate(DateUtil.formatDate(date,"yyyy-MM-dd"));
                request.setStartDate(DateUtil.formatDate(DateUtil.addDay(date,0-day+1),"yyyy-MM-dd"));
            }
        }else if(4 == request.getChooseType()){
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);//美国是以周日为每周的第一天 现把周一设成第一天
            int year = calendar.get(Calendar.YEAR) - 1;
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            Map<String, String> startAndEndDate = appReportMapper.selectDateTime(year,0,week);
            request.setEndDate(startAndEndDate.get("minDate"));
            request.setStartDate(startAndEndDate.get("maxDate"));
        }
        return request;
    }
}

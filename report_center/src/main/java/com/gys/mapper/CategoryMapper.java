package com.gys.mapper;

import com.gys.entity.data.Category.CategoryExecuteResponse;
import com.gys.entity.data.Category.CategoryStatisticResponse;
import com.gys.entity.data.Category.StoreTimeRequest;
import com.gys.entity.data.Category.SupplierCostStatisticResponse;

import java.util.List;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/5 14:16
 */
public interface CategoryMapper {
    /**
     * 查询品类执行情况
     * @param request
     * @return
     */
    CategoryExecuteResponse categoryExecuteData(StoreTimeRequest request);

    /**
     * 实际品类情况数据汇总
     * @param request
     * @return
     */
    List<CategoryStatisticResponse> categoryStatisticData(StoreTimeRequest request);

    /**
     * TOP10进货成本额供应商列表
     * @param request
     * @return
     */
    List<SupplierCostStatisticResponse> supplierCostStatisticData(StoreTimeRequest request);
}

package com.gys.service;

import com.gys.entity.data.Category.CategoryExecuteResponse;
import com.gys.entity.data.Category.CategoryStatisticResponse;
import com.gys.entity.data.Category.StoreTimeRequest;
import com.gys.entity.data.Category.SupplierCostStatisticResponse;

import java.util.List;
import java.util.Map;

/**
 * @author li_haixia@gov-info.cn
 * @date 2021/3/4 13:39
 */
public interface CategoryService {
    /**
     * 时间段内品类执行情况
     * @param request
     * @return
     */
    CategoryExecuteResponse categoryExecuteData(StoreTimeRequest request);

    /**
     * 时间段内引进、淘汰，调价品类情况数据汇总报表
     * @param request
     * @return
     */
    List<CategoryStatisticResponse> categoryStatisticData(StoreTimeRequest request);

    /**
     * TOP10进货成本额供应商列表（周/月）
     * @param request
     * @return
     */
    List<SupplierCostStatisticResponse> supplierCostStatisticData(StoreTimeRequest request);
}

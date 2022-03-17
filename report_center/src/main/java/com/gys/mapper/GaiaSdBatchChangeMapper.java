package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface GaiaSdBatchChangeMapper extends BaseMapper<GaiaSdBatchChange> {
    /**
     * 进销存查询
     * @param inData
     * @return
     */
    List<InvoicingOutData> getInventoryList(InvoicingInData inData);

    List<InvoicingOutData> getBrIdInventoryList(InvoicingInData inData);
    List<ProductSalesBySupplierOutData> getProductSalesBySupplier(ProductSalesBySupplierInData inData);

    List<String> getClientInventoryList(@Param("clientId") String clientId);
}

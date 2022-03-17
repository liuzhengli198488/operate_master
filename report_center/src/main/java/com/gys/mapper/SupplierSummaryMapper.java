package com.gys.mapper;

import com.gys.entity.SupplierSaleMan;
import com.gys.report.entity.ProductSalesBySupplierInData;
import com.gys.report.entity.ProductSalesBySupplierOutData;
import com.gys.report.entity.ProductSalesBySupplierWithSaleManOutData;
import com.gys.report.entity.SupplierSummaryInData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierSummaryMapper {

    // 根据加盟商【供应商】查询业务员列表
    List<SupplierSaleMan> querySaleManList( @Param("client")String client , @Param("supplierCode") String supplierCode);

    // 根据业务员查询
    List<ProductSalesBySupplierWithSaleManOutData> getProductSalesBySupplier(SupplierSummaryInData inData);

    List<String> querySaleManCodeList(@Param("client")String client , @Param("supplierCode") String supplierCode);
}

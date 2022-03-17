package com.gys.mapper;

import com.gys.common.data.*;
import com.gys.entity.data.productSaleAnalyse.ProductSaleAnalyseInData;
import com.gys.entity.data.productSaleAnalyse.StoreInfoOutData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SaleReportMapper {
    List<NoSaleItemOutData> selectNosaleReportList(SaleReportInData inData);

    List<MedicalInsuranceOutData> selectMedicalInsuranceList(MedicalInsuranceInData inData);

    List<MaterialTypeQtyOutData>selectMaterialQtyByType(MedicalInsuranceInData inData);

    List<MaterialTypeQtyOutData> selectStockQty(MedicalInsuranceInData inData);

    List<Map<String,Object>> selectNosaleReportListByStore(SaleReportInData inData);

    List<Map<String, Object>> selectNosaleReportListByDc(SaleReportInData inData);

    List<StoreInfoOutData>selectStoreInfoBySale(ProductSaleAnalyseInData inData);

    List<StoreInfoOutData> selectStoNumByClient(ProductSaleAnalyseInData inData);

    List<Map<String,String>> selectStoreType();
}

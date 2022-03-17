package com.gys.mapper;

import com.gys.common.data.ProductStockQueryInData;
import com.gys.common.data.ProductStockQueryOutData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductStockQueryMapper {

    List<ProductStockQueryOutData> searchProBasicInfo(@Param("inData") ProductStockQueryInData inData);

    ProductStockQueryOutData queryProBasicInfo(@Param("inData") ProductStockQueryInData inData);

    List<ProductStockQueryOutData> queryStoreInfo(@Param("inData") ProductStockQueryInData inData);

}

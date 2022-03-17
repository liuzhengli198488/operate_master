package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.GaiaSdSaleH;
import com.gys.report.entity.UpdateInvoicedInData;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface GaiaSdSaleHMapper extends BaseMapper<GaiaSdSaleH> {

    /**
     * 标记开票
     *
     * @param data 加盟商 门店编码 订单号 日期
     * @return Integer
     */
    Integer updateInvoicedByBillNo(List<UpdateInvoicedInData> data);

}

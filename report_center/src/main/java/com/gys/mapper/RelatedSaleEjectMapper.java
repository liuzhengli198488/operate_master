package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.RelatedSaleEject;
import com.gys.report.entity.RelatedSaleEjectInData;
import com.gys.report.entity.RelatedSaleEjectRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * <p>
 * 关联销售弹出率统计表 Mapper 接口
 * </p>
 *
 * @author flynn
 * @since 2021-08-18
 */
@Mapper
public interface RelatedSaleEjectMapper extends BaseMapper<RelatedSaleEject> {
    List<RelatedSaleEjectRes> selectTclGroupByUserId(RelatedSaleEjectInData relatedSaleEjectInData);
}

package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.common.data.CommonVo;
import com.gys.entity.TichengProplanBasic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 单品提成方案主表v5版 Mapper 接口
 * </p>
 *
 * @author flynn
 * @since 2021-11-11
 */
@Mapper
public interface TichengProplanBasicMapper extends BaseMapper<TichengProplanBasic> {

    /**
     * 获取用户名
     *
     * @return List<Map < String, String>>
     */
    List<Map<String, String>> selectUserName(@Param("client") String client);

    List<CommonVo> selectStoreByStoreCodes(@Param(value = "stoArr") List<String> stoArr, @Param(value = "client") String client);

}

package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.SysDic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 系统码表，加盟商 门店编号可以不填写，不填写情况下表示全局级别的码表 Mapper 接口
 * </p>
 *
 * @author flynn
 * @since 2022-01-29
 */
@Mapper
public interface SysDicMapper extends BaseMapper<SysDic> {
}

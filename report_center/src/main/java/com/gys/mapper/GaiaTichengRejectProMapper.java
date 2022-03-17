package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengRejectPro;
import com.gys.entity.GaiaTichengSaleplanZ;

import java.util.List;

public interface GaiaTichengRejectProMapper extends BaseMapper<GaiaTichengRejectPro> {

    List<String> selectPro(GaiaTichengSaleplanZ tc);

}
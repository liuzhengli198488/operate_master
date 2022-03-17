package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaXhlD;
import com.gys.entity.data.XhlDCond;

import java.util.List;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/30 23:31
 */
public interface GaiaXhlDMapper extends BaseMapper<GaiaXhlD> {

    GaiaXhlD getById(Long id);

    int add(GaiaXhlD gaiaXhlD);

    int update(GaiaXhlD gaiaXhlD);

    List<GaiaXhlD> findList(GaiaXhlD cond);

    List<GaiaXhlD> getStoreXhlByDate(XhlDCond cond);

    List<String> getClientList();

    GaiaXhlD getUnique(GaiaXhlD cond);

    GaiaXhlD countXhl(XhlDCond cond);

    List<GaiaXhlD> getStoreXhlByDateWithOutShopGoods(XhlDCond cond);
}

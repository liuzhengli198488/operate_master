package com.gys.mapper;


import com.gys.common.base.BaseMapper;
import com.gys.entity.AreaOutData;
import com.gys.entity.GaiaFranchisee;
import com.gys.entity.ReplenishDiffSumInData;
import com.gys.entity.ReplenishDiffSumOutData;
import com.gys.report.entity.GaiaSdReplenishComparedD;
import com.gys.report.entity.GetReplenishInData;

import java.util.HashMap;
import java.util.List;

public interface GaiaSdReplenishComparedDMapper extends BaseMapper<GaiaSdReplenishComparedD> {

    List<AreaOutData> getAreaList();

    List<GaiaFranchisee> getAllClient();

    List<ReplenishDiffSumOutData> getReplenishDiffSumList(ReplenishDiffSumInData inData);

    List<HashMap<String, Object>> listDifferentReplenish(GetReplenishInData inData);

    List<HashMap<String, Object>> listDifferentReplenishDetail(GetReplenishInData inData);

    HashMap<String, Object> getDifferentReplenish(GetReplenishInData inData);
}

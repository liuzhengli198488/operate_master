package com.gys.service;

import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.response.Result;
import com.gys.common.vo.GaiaInternalQualityReviewVo;
import com.gys.entity.GaiaInternalQualityReview;
import com.gys.entity.data.GaiaInternalQualityReviewInData;

import java.util.List;

/**
 * @author csm
 * @date 12/31/2021 - 5:41 PM
 */
public interface GaiaInternalQualityReviewServcie {

    void addItem(GetLoginOutData loginUser, GaiaInternalQualityReviewInData inData);


    void updateItem(GetLoginOutData loginUser, List<GaiaInternalQualityReviewInData> inDatas);

    void delItem(GetLoginOutData loginUser, List<GaiaInternalQualityReview> inDatas);

    PageInfo<GaiaInternalQualityReviewVo> getItem(GaiaInternalQualityReviewInData inData);

    List<GaiaInternalQualityReview> getExcelItem(GaiaInternalQualityReviewInData inData);

    Result exportExcel(GaiaInternalQualityReviewInData inData);
}

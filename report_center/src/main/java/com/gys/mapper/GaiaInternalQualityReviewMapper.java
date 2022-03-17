package com.gys.mapper;

import com.gys.entity.GaiaInternalQualityReview;
import com.gys.entity.data.GaiaInternalQualityReviewInData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaInternalQualityReviewMapper {


    int insertSelective(GaiaInternalQualityReview record);

    GaiaInternalQualityReview selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GaiaInternalQualityReview record);


    List<GaiaInternalQualityReview> getItem(@Param("inData") GaiaInternalQualityReviewInData inData);
}
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.service;

import com.gys.common.data.PageInfo;
import com.gys.report.entity.MemberSalesInData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface MemberSaleSummaryService {
    Map<String, Object> selectMemberCollectList(MemberSalesInData inData);

    PageInfo selectMenberDetailList(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberListCSV(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberStoListCSV(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberProCSV(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberProStoCSV(MemberSalesInData inData);
}

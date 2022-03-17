//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.report.entity.GaiaSdMemberBasic;
import com.gys.report.entity.GetMemberCardReportInData;
import com.gys.report.entity.GetMemberCardReportOutData;
import com.gys.report.entity.MemberSalesInData;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GaiaSdMemberBasicMapper extends BaseMapper<GaiaSdMemberBasic> {
    List<GetMemberCardReportOutData> queryReport(GetMemberCardReportInData inData);

    List<LinkedHashMap<String, Object>> selectMemberList(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberStoreList(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberPro(MemberSalesInData inData);

    List<LinkedHashMap<String, Object>> selectMemberProSto(MemberSalesInData inData);
}

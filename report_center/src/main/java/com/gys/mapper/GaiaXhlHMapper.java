package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaViewWmsTjXhl;
import com.gys.entity.GaiaXhlH;
import com.gys.entity.TjXhl;
import com.gys.entity.ViewProduct;
import com.gys.entity.data.dropdata.GetClientListDTO;
import com.gys.entity.data.dropdata.GetHistoryPostedUnloadRateTotalDTO;
import com.gys.entity.data.dropdata.SearchRangeDTO;
import com.gys.entity.data.xhl.dto.ReportInfoDto;
import com.gys.entity.data.xhl.dto.ReportSummaryDto;
import com.gys.entity.data.xhl.vo.ReportInfoSummaryVo;
import com.gys.entity.data.xhl.vo.ReportInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/8/30 23:16
 */
public interface GaiaXhlHMapper extends BaseMapper<GaiaXhlH> {

    GaiaXhlH getById(Long id);

    int add(GaiaXhlH gaiaXhlH);

    int update(GaiaXhlH gaiaXhlH);

    GaiaXhlH getUnique(GaiaXhlH cond);

    List<GaiaXhlH> findList(GaiaXhlH cond);

    List<GaiaXhlH> getLastTimeData(String client);

    List<GaiaXhlH> getWeekRate(Map<String, Object> params);

    List<GaiaXhlH> getWeekAverageRate(Map<String, Object> params);

    List<GaiaXhlH> getBefore7WeekRate(Map<String, Object> weekParams);

    List<GaiaXhlH> getCurrentYearRate(Map<String, Object> monthParams);

    List<GaiaXhlH> selectDate(@Param("client") String client);

    List<GaiaViewWmsTjXhl> selectHistoryList(List<String> clientId,
                                             List<String> dcId,
                                             String replenishStyle,
                                             SearchRangeDTO pleaseOrderDate,
                                             SearchRangeDTO deliveryOrderDate,
                                             SearchRangeDTO postingDate,
                                             SearchRangeDTO customerNo,
                                             String commodityCategory,
                                             String commodityCondition,
                                             String client,
                                             String site);

    GetHistoryPostedUnloadRateTotalDTO selectHistoryTotal(List<String> clientId,
                                                          List<String> dcId,
                                                          String replenishStyle,
                                                          SearchRangeDTO pleaseOrderDate,
                                                          SearchRangeDTO deliveryOrderDate,
                                                          SearchRangeDTO postingDate,
                                                          SearchRangeDTO customerNo,
                                                          String commodityCategory,
                                                          String commodityCondition,
                                                          String client,
                                                          String site);

    Integer selectTotalNumber(List<String> clientId,
                              List<String> dcId,
                              String replenishStyle,
                              SearchRangeDTO pleaseOrderDate,
                              SearchRangeDTO deliveryOrderDate,
                              SearchRangeDTO postingDate,
                              SearchRangeDTO customerNo,
                              String commodityCategory,
                              String commodityCondition,
                              String client,
                              String site);

    Integer selectIsPostTotal(List<String> clientId,
                              List<String> dcId,
                              String replenishStyle,
                              SearchRangeDTO pleaseOrderDate,
                              SearchRangeDTO deliveryOrderDate,
                              SearchRangeDTO postingDate,
                              SearchRangeDTO customerNo,
                              String commodityCategory,
                              String commodityCondition,
                              String client,
                              String site);

    //已过账且配送单原数量大于零行数合计
    Integer selectisPostDeliveryTotal(List<String> clientId,
                                      List<String> dcId,
                                      String replenishStyle,
                                      SearchRangeDTO pleaseOrderDate,
                                      SearchRangeDTO deliveryOrderDate,
                                      SearchRangeDTO postingDate,
                                      SearchRangeDTO customerNo,
                                      String commodityCategory,
                                      String commodityCondition,
                                      String client,
                                      String site);

    //配送过账数量大于零的行数
    Integer selectIsPostNumberTotal(List<String> clientId,
                                    List<String> dcId,
                                    String replenishStyle,
                                    SearchRangeDTO pleaseOrderDate,
                                    SearchRangeDTO deliveryOrderDate,
                                    SearchRangeDTO postingDate,
                                    SearchRangeDTO customerNo,
                                    String commodityCategory,
                                    String commodityCondition,
                                    String client,
                                    String site);

    List<String> selectDcData(String client);

    List<GetClientListDTO> getClientList(@Param("clientName")String clientName);

    List<String> getReplenishStyle();

    List<GaiaViewWmsTjXhl> selectListByOpenOrderDate(String selDate);

    void del(@Param("client")String client,@Param("proSite") String proSite,@Param("selDate") String selDate);

    void save1(@Param("tjXhl")TjXhl tjXhl);

    //获取商品模糊查询
    List<ViewProduct> selectProList(@Param("productInfo")String productInfo);

    Long selectContList(List<String> clientId, List<String> dcId, String replenishStyle, SearchRangeDTO pleaseOrderDate, SearchRangeDTO deliveryOrderDate, SearchRangeDTO postingDate, String customerNo, String commodityCategory, String commodityCondition, String client, String dcCode);

    List<ReportInfoVo> getListDropList(ReportInfoDto dto);

    List<ReportInfoSummaryVo> findXhlh(ReportSummaryDto dto);

    List<GaiaXhlH> getSummaryList(ReportSummaryDto dto);

    List<GaiaXhlH> getSummaryListLess(ReportSummaryDto dto);
}

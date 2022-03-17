package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.gys.common.data.EmpSaleDetailResVo;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.entity.data.MonthPushMoney.EmpSaleDetailInData;
import com.gys.entity.data.salesSummary.UserCommissionSummaryDetail;
import com.gys.mapper.UserCommissionSummaryDetailMapper;
import com.gys.report.entity.SaveUserCommissionSummaryDetailInData;
import com.gys.service.IUserCommissionSummaryDetailService;
import com.gys.service.TichengPlanZService;
import com.gys.util.BigDecimalUtil;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 用户提成明细 服务实现类
 * </p>
 *
 * @author yifan.wang
 * @since 2022-02-11
 */
@Service
@Slf4j
public class UserCommissionSummaryDetailServiceImpl implements IUserCommissionSummaryDetailService {
    @Autowired
    private UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper;

    @Resource
    private TichengPlanZService tichengPlanZService;

    /**
     * 定时计算昨日提成数据
     *
     * @param inData 请求参数
     */
    @Override
    public void saveUserCommissionSummaryDetail(SaveUserCommissionSummaryDetailInData inData) {
        List<String> delClients = new ArrayList<>();
        String delClient = "";
        try {
            //加盟商
            List<String> clients = inData.getClients();
            if (StrUtil.isBlank(inData.getStartDate()) || StrUtil.isBlank(inData.getEndDate())) {
                //昨天
                String yesterday = DateUtil.getFormatyyyyMMdd(LocalDate.now().minusDays(1));
                inData.setStartDate(yesterday);
                inData.setEndDate(yesterday);
            }
            //DateUtil.format(DateUtil.parse(inData.getGsphBeginDate(), "yyyy-MM-dd"), "yyyyMMdd")
            inData.setStartDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(inData.getStartDate(), "yyyyMMdd"), "yyyy-MM-dd"));
            inData.setEndDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(inData.getEndDate(), "yyyyMMdd"), "yyyy-MM-dd"));
            List<String> dateList = DateUtil.getDateList(inData.getStartDate(), inData.getEndDate());
            if (CollUtil.isEmpty(clients)) {
                //获取所有的加盟商
                List<Map<String, Object>> clientList = this.userCommissionSummaryDetailMapper.getClientList();
                if (CollUtil.isNotEmpty(clientList)) {
                    inData.setClients(clientList.stream().map(client -> Convert.toStr(client.get("client"))).collect(Collectors.toList()));
                }
            }
            if (CollUtil.isEmpty(clients) || CollUtil.isEmpty(dateList)) {
                return;
            }
            EmpSaleDetailInData detail = new EmpSaleDetailInData();
            detail.setType(inData.getType());
            for (String date : dateList) {
                detail.setStartDate(date);
                detail.setEndDate(date);
                clients.forEach(client -> {
                    GetLoginOutData userInfo = new GetLoginOutData();
                    userInfo.setClient(client);
                    detail.setClient(client);
                    PageInfo pageInfo = tichengPlanZService.selectEmpSaleDetailList(detail, userInfo);
                    List<EmpSaleDetailResVo> resVoList = new ArrayList<>();
                    if (Objects.nonNull(pageInfo) && CollUtil.isNotEmpty(pageInfo.getList())) {
                        resVoList.addAll((List<EmpSaleDetailResVo>) pageInfo.getList());
                    }
                    if (CollUtil.isNotEmpty(resVoList)) {
                        List<UserCommissionSummaryDetail> detailList = new ArrayList<>();
                        List<List<EmpSaleDetailResVo>> partition = Lists.partition(resVoList, 1000);
                        if (CollUtil.isNotEmpty(partition)) {
                            partition.forEach(part -> {
                                //封装实体
                                List<UserCommissionSummaryDetail> details = setUserCommissionSummaryDetail(part,client);
                                this.userCommissionSummaryDetailMapper.insertList(details);
                            });
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.info("<定时任务><统计每天员工销售提成><执行异常：{delClient}>", delClient);
            //log.info("程序异常,删除加盟商数据{delClient}", delClient);
            delClients.add(delClient);
            e.printStackTrace();
        } finally {
            //删除加盟商下的数据
            if (CollUtil.isNotEmpty(delClients)) {
                log.info("<定时任务><开始删除失败的员工销售提成加盟商><执行开始：{delClients}>", JSONObject.toJSONString(delClients));
                List<List<String>> lists = Lists.partition(delClients, 500);
                if (CollUtil.isNotEmpty(lists)) {
                    for (List<String> clients : lists) {
                        this.userCommissionSummaryDetailMapper.deleteClietnts(clients);
                    }
                }
            }
        }
    }

    /**
     * 封装提成实体
     *
     * @param saleDetailResVos
     * @return List<UserCommissionSummaryDetail>
     */
    private List<UserCommissionSummaryDetail> setUserCommissionSummaryDetail(List<EmpSaleDetailResVo> saleDetailResVos,String client) {
        return saleDetailResVos.stream().map(res -> {
            UserCommissionSummaryDetail detail = new UserCommissionSummaryDetail();
            //加盟商
            detail.setClient(client);
            //方案id
            detail.setPlanId(res.getPlanId());
            //方案名称
            detail.setPlanName(res.getPlanName());
            //子方案id
            detail.setSubPlanId(res.getCPlanId());
            //子方案名称
            detail.setSubPlanName(res.getCPlanName());
            //方案开始时间
            detail.setPlanStartDate(res.getStartDate());
            //方案结束时间
            detail.setPlanEndDate(res.getEndDate());
            //提成类型（1：销售提成，2：单品提成）
            detail.setCommissionType(Integer.valueOf(res.getType()));
            //营业员工号
            detail.setSalerId(res.getSalerId());
            //营业员名称
            detail.setSalerName(res.getSalerName());
            //门店编码
            detail.setStoCode(res.getStoCode());
            //门店名称
            detail.setStoName(res.getStoName());
            //销售日期
            detail.setSaleDate(cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(res.getSaleDate(), "yyyy-MM-dd"),"yyyyMMdd"));
            //销售单号
            detail.setBillNo(res.getGssdBillNo());
            //商品编码
            detail.setProId(res.getProSelfCode());
            //商品名称
            detail.setProName(res.getProName());
            //通用名称
            detail.setProCommonname(res.getProCommonName());
            //规格
            detail.setProSpecs(res.getProSpecs());
            //量单位
            detail.setProUnit(res.getProUnit());
            //生产企业代码
            detail.setProFactoryCode(null);
            //生产企业
            detail.setProFactoryName(res.getFactoryName());
            //商品批号
            detail.setBatchNo(res.getBatBatchNo());
            //商品有效期
            detail.setValidDate(res.getGssdValidDate());
            //数量
            detail.setQty(res.getQyt());
            //参考零售价
            detail.setProLsj(BigDecimalUtil.toBigDecimal(res.getProLsj()));
            //成本额
            detail.setCostAmt(BigDecimalUtil.toBigDecimal(res.getCostAmt()));
            //应收金额
            detail.setYsAmt(BigDecimalUtil.toBigDecimal(res.getYsAmt()));
            //实收金额
            detail.setAmt(BigDecimalUtil.toBigDecimal(res.getAmt()));
            //毛利额
            detail.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(res.getGrossProfitAmt()));
            //毛利率
            detail.setGrossProfitRate(BigDecimalUtil.toBigDecimal(res.getGrossProfitRate()));
            //折扣金额
            detail.setZkAmt(BigDecimalUtil.toBigDecimal(res.getZkAmt()));
            //折扣率
            detail.setZkRate(BigDecimalUtil.toBigDecimal(res.getZkl()));
            //提成金额
            detail.setCommissionAmt(BigDecimalUtil.toBigDecimal(res.getTiTotal()));
            //提成销售占比
            detail.setCommissionSalesRatio(BigDecimalUtil.toBigDecimal(res.getDeductionWageAmtRate()));
            //提成毛利占比
            detail.setCommissionGrossProfitRatio(BigDecimalUtil.toBigDecimal(res.getDeductionWageGrossProfitRate()));
            //BigDecimalUtil.toBigDecimal()
            //收银工号
            detail.setEmpId(res.getGsshEmpId());
            //收银员姓名
            detail.setEmpName(res.getGsshEmpName());
            //医生编号
            detail.setDoctorId(res.getDoctorId());
            //医生姓名
            detail.setDoctorName(res.getDoctorName());
            //商品分类
            detail.setProClass(res.getProClassCode());
            //商品分类名称
            detail.setProClassName(res.getProClassCode());
            //商品大类
            detail.setProBigClassName(res.getProBigClassCode());
            //商品中类
            detail.setProMidClassName(res.getProMidClassCode());
            //销售级别
            detail.setProSlaeClass(res.getProSlaeClass());
            //商品定位
            detail.setProPosition(res.getProPosition());
            //业务员编码
            detail.setBatSupplierCode(res.getBatSupplierName());
            //供应商编码
            detail.setBatSupplierCode(res.getBatSupplierCode());
            //供应商名称
            detail.setSupName(res.getBatSupplierName());
            //创建时间
            detail.setCreateDate(LocalDateTime.now().minusDays(1));
            //更新时间
            detail.setUpdateDate(LocalDateTime.now().minusDays(1));
            return detail;
        }).collect(Collectors.toList());
    }
}

package com.gys.service.Impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.data.consignment.dto.RecommendedDocumentsDto;
import com.gys.entity.data.consignment.dto.StoreDto;
import com.gys.entity.data.consignment.dto.StoreRecommendedSaleDto;
import com.gys.entity.data.consignment.vo.*;
import com.gys.mapper.GaiaSdSaleDMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.report.common.constant.CommonConstant;
import com.gys.report.entity.GetPayInData;
import com.gys.report.entity.GetPayTypeOutData;
import com.gys.report.service.PayService;
import com.gys.service.ConsignmentService;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: tzh
 * @Date: 2021/12/7 10:37
 * @Description: ConsignmentServiceImpl
 * @Version 1.0.0  推荐销售
 */
@Service
@Slf4j
public class ConsignmentServiceImpl implements ConsignmentService {
    @Resource
    private GaiaStoreDataMapper gaiaStoreDataMapper;
    @Resource
    private GaiaSdSaleDMapper gaiaSdSaleDMapper;
    @Resource
    private PayService payService;

    @Override
    public PageInfo getStoreRecommendReport(StoreRecommendedSaleDto dto) {
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(dto.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        dto.setFlag(flag);
      //  StoreReport storeReport=new StoreReport();
        PageInfo pageInfo;
        List<StoreReportVo> outData = gaiaSdSaleDMapper.getRecommendedSalesDetail(dto);
        if (ObjectUtil.isNotEmpty(outData)) {
            StoreReportTotalVo totalVo= gaiaSdSaleDMapper.getRecommendedSalesDetailTotal(dto);
            pageInfo = new PageInfo(outData, totalVo);
        } else {
            pageInfo = new PageInfo();
        }
       // storeReport.setReportTotalVo(totalVo);
       // storeReport.setReportVos(outData);
        return pageInfo;
    }

    @Override
    public PageInfo getStoreUnRecommendReport(StoreRecommendedSaleDto dto) {
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(dto.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        dto.setFlag(flag);
        PageInfo pageInfo;
        //StoreReport storeReport=new StoreReport();
        List<StoreReportVo> outData = gaiaSdSaleDMapper.getUnRecommendedSalesDetail(dto);
        if (ObjectUtil.isNotEmpty(outData)) {
            StoreReportTotalVo totalVo= gaiaSdSaleDMapper.getUnRecommendedSalesDetailTotal(dto);
            pageInfo = new PageInfo(outData, totalVo);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo ;
    }

    @Override
    public void exportStoreReport(StoreRecommendedSaleDto dto, HttpServletResponse response) {
        String flag = gaiaStoreDataMapper.selectStoPriceComparison(dto.getClientId(), null, CommonConstant.GSSP_ID_IMPRO_DETAIL);
        dto.setFlag(flag);
        if(dto.getTag()==0){
            //reportVoList= gaiaSdSaleDMapper.getRecommendedSalesDetail(dto);
            CsvFileInfo fileInfo = new CsvFileInfo(new byte[0], 0, "推荐未完成门店报表");
            List<StoreReportVo> list = new ArrayList<>();
            AtomicInteger i = new AtomicInteger(1);
            gaiaSdSaleDMapper.getUnRecommendedSalesDetail(dto,resultContext -> {
                StoreReportVo outData = resultContext.getResultObject();
                if(outData.getIncludeTaxSale()==null){
                    outData.setIncludeTaxSale(BigDecimal.ZERO);
                }
                if(outData.getGrossProfitMargin()==null){
                    outData.setGrossProfitMargin(BigDecimal.ZERO);
                }
                if(outData.getGrossProfitRate()==null){
                    outData.setGrossProfitRate(BigDecimal.ZERO.toPlainString());
                }
                outData.setIndex(i.get());
                i.getAndIncrement();
                if (StringUtils.isEmpty(outData.getDiscountRate())){
                    outData.setDiscountRate("0");
                }
                outData.setDiscountRate(new BigDecimal(outData.getDiscountRate()).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                list.add(outData);
                if (list.size() == CsvClient.BATCH_SIZE) {
                    CsvClient.handle(list, fileInfo);
                }
            });

            CsvClient.endHandle(response,list,fileInfo,()->{
                if (ObjectUtil.isEmpty(fileInfo.getFileContent())){
                    throw new BusinessException("导出数据不能为空！");
                }
                StoreReportTotalVo outTotal = gaiaSdSaleDMapper.getUnRecommendedSalesDetailTotal(dto);
                BigDecimal includetaxsale=BigDecimal.ZERO;
                if( outTotal.getIncludeTaxSale()!=null){
                    includetaxsale  = outTotal.getIncludeTaxSale().compareTo(BigDecimal.ZERO)>0?  outTotal.getIncludeTaxSale():BigDecimal.ZERO;
                }
                BigDecimal grossprofitmargin=BigDecimal.ZERO;
                if(outTotal.getGrossProfitMargin()!=null){
                    grossprofitmargin  = outTotal.getGrossProfitMargin().compareTo(BigDecimal.ZERO)>0?outTotal.getGrossProfitMargin():BigDecimal.ZERO;
                }
                outTotal.setGrossProfitRate("0%");
                byte[] bytes = ("\r\n合计,,,,,,,,,,,,,,,,,,,,,,,," + outTotal.getQty() + "," + outTotal.getAmountReceivable() + "," + outTotal.getAmt() + "," +includetaxsale+ ","+grossprofitmargin+ ","+outTotal.getGrossProfitRate()+","+ outTotal.getDiscount() + "," + outTotal.getDiscountRate()).getBytes(StandardCharsets.UTF_8);
                byte[] all = ArrayUtils.addAll(fileInfo.getFileContent(), bytes);
                fileInfo.setFileContent(all);
                fileInfo.setFileSize(all.length);
            });

        }else {
            CsvFileInfo fileInfo = new CsvFileInfo(new byte[0], 0, "推荐完成门店报表");
            List<StoreReportVo> list = new ArrayList<>();
            AtomicInteger i = new AtomicInteger(1);
            gaiaSdSaleDMapper.getRecommendedSalesDetail(dto,resultContext -> {
                StoreReportVo outData = resultContext.getResultObject();
                outData.setIndex(i.get());
                i.getAndIncrement();
                if (StringUtils.isEmpty(outData.getDiscountRate())){
                    outData.setDiscountRate("0");
                }
                outData.setDiscountRate(new BigDecimal(outData.getDiscountRate()).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
                list.add(outData);
                if (list.size() == CsvClient.BATCH_SIZE) {
                    CsvClient.handle(list, fileInfo);
                }
            });

            CsvClient.endHandle(response,list,fileInfo,()->{
                if (ObjectUtil.isEmpty(fileInfo.getFileContent())){
                    throw new BusinessException("导出数据不能为空！");
                }
                StoreReportTotalVo     outTotal = gaiaSdSaleDMapper.getRecommendedSalesDetailTotal(dto);
                byte[] bytes = ("\r\n合计,,,,,,,,,,,,,,,,,,,,,,,," + outTotal.getQty() + "," + outTotal.getAmountReceivable() + "," + outTotal.getAmt() + "," +outTotal.getIncludeTaxSale()+ ","+outTotal.getGrossProfitMargin()+ ","+outTotal.getGrossProfitRate()+","+ outTotal.getDiscount() + "," + outTotal.getDiscountRate()).getBytes(StandardCharsets.UTF_8);
                byte[] all = ArrayUtils.addAll(fileInfo.getFileContent(), bytes);
                fileInfo.setFileContent(all);
                fileInfo.setFileSize(all.length);
            });
        }



    }

    @Override
    public StoreVoList getAllStores(StoreDto dto) {
        // 查询推荐成功的
       // List<StoreVo>  recommendedList= gaiaSdSaleDMapper.getRecommendedStores(dto);
       // List<StoreVo>  saleList= gaiaSdSaleDMapper.getSaleStores(dto);
        // 统一处理
        List<StoreVo> list=  gaiaSdSaleDMapper.getAllStores(dto);
        StoreVoList voList=new StoreVoList();
        voList.setRecommendList(list);
        voList.setSaleList(list);
        return voList;
    }

    @Override
    public PageInfo getRecommendedDocuments(RecommendedDocumentsDto data) {
        if (ObjectUtil.isNotNull(data.getPageNum()) && ObjectUtil.isNotNull(data.getPageSize())) {
            PageHelper.startPage(data.getPageNum(), data.getPageSize());
        }
        if(StrUtil.isNotBlank(data.getStatDatePart())){
            data.setStatDatePart(data.getStatDatePart()+"00");
        }
        if(StrUtil.isNotBlank(data.getEndDatePart())){
            data.setEndDatePart(data.getEndDatePart()+"59");
        }
        GetPayInData pay = new GetPayInData();
        pay.setClientId(data.getClient());
        pay.setType("1");
//        pay.setPayName(inData.getPayName());

        //获取支付类型
        List<GetPayTypeOutData> payTypeOutData = payService.payTypeListByClient(pay);
        if (payTypeOutData != null && payTypeOutData.size() > 0) {
            data.setPayTypeOutData(payTypeOutData);
        }
        // 推荐单据
        if(StringUtils.isNotBlank(data.getBillNo())){
            data.setBillNos(Arrays.asList(data.getBillNo().split(",")));
        }
        List<RecommendedDocumentsVo> outData = this.gaiaSdSaleDMapper.getRecommendedDocuments(data);
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            for (RecommendedDocumentsVo item : outData) {
                if(StrUtil.isNotBlank(Convert.toStr(item.getDatePart()))){
                    String hm =  cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.parse(Convert.toStr(item.getDatePart()), "HHmmss"), "HH:mm:ss");
                    item.setDatePart(hm);
                }
            }
            // 集合列的数据汇总
            RecommendedDocumentsVo mapTotal = this.gaiaSdSaleDMapper.getRecommendedDocumentsTotal(data);
            mapTotal.setAmountReceivable(mapTotal.getAmountReceivable().setScale(2, RoundingMode.HALF_UP));
            mapTotal.setAmt(mapTotal.getAmt().setScale(2,RoundingMode.HALF_UP));
            mapTotal.setGrossProfitMargin(mapTotal.getGrossProfitMargin().setScale(2,RoundingMode.HALF_UP));
            //mapTotal.setGrossProfitRateStr(mapTotal.getGrossProfitRate().toPlainString()+"%");
            mapTotal.setMovPrices(mapTotal.getMovPrices().setScale(2,RoundingMode.HALF_UP));
            mapTotal.setGrossProfitRate(mapTotal.getGrossProfitRate()+"%");
          // s1000;s2000;s2002;s2004;s3001;s3002;s3003;s3004;s4000;s6001;s7007;s9006;
            if(mapTotal.getS1000()==null){
                mapTotal.setS1000(BigDecimal.ZERO);
            }
            if(mapTotal.getS2000()==null){
                mapTotal.setS2000(BigDecimal.ZERO);
            }
            if(mapTotal.getS2002()==null){
                mapTotal.setS2002(BigDecimal.ZERO);
            }
            if(mapTotal.getS2004()==null){
                mapTotal.setS2004(BigDecimal.ZERO);
            }
            if(mapTotal.getS3001()==null){
                mapTotal.setS3001(BigDecimal.ZERO);
            }
            if(mapTotal.getS3002()==null){
                mapTotal.setS3002(BigDecimal.ZERO);
            }
            if(mapTotal.getS3003()==null){
                mapTotal.setS3003(BigDecimal.ZERO);
            }
            if(mapTotal.getS3004()==null){
                mapTotal.setS3004(BigDecimal.ZERO);
            }
            if(mapTotal.getS4000()==null){
                mapTotal.setS4000(BigDecimal.ZERO);
            }
            if(mapTotal.getS6001()==null){
                mapTotal.setS6001(BigDecimal.ZERO);
            }
            if(mapTotal.getS7007()==null){
                mapTotal.setS7007(BigDecimal.ZERO);
            }
            if(mapTotal.getS9006()==null){
                mapTotal.setS9006(BigDecimal.ZERO);
            }
            pageInfo = new PageInfo(outData, mapTotal);
        } else {
            pageInfo = new PageInfo();
        }

        return pageInfo;
    }

    public static void main(String[] args) {
        BigDecimal a= new BigDecimal("0");
        BigDecimal bigDecimal = a.setScale(2, RoundingMode.HALF_UP);
        System.out.println(bigDecimal);
    }
}

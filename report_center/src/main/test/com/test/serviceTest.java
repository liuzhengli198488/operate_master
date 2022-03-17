package com.test;
import com.alibaba.fastjson.JSON;
import com.gys.GysReportApplication;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.entity.GaiaSdMessage;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.vo.ApprovalVo;
import com.gys.entity.data.consignment.dto.StoreDto;
import com.gys.entity.data.consignment.dto.StoreRecommendedSaleDto;
import com.gys.entity.data.consignment.vo.StoreVoList;
import com.gys.entity.data.xhl.dto.QueryDto;
import com.gys.entity.data.xhl.dto.ReplenishDto;
import com.gys.entity.data.xhl.dto.ReportInfoDto;
import com.gys.entity.data.xhl.vo.ClientBaseInfoVo;
import com.gys.entity.data.xhl.vo.ReplenishVo;
import com.gys.entity.data.xhl.vo.StoreData;
import com.gys.mapper.GaiaSdMessageMapper;
import com.gys.mapper.GaiaSdReplenishHMapper;
import com.gys.service.ApprovalService;
import com.gys.service.ConsignmentService;
import com.gys.service.DropRateService;
import com.gys.service.GaiaSdMessageService;
import com.gys.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GysReportApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class serviceTest {

    @Resource
    private GaiaSdMessageService gaiaSdMessageService;
    @Resource
    private GaiaSdMessageMapper gaiaSdMessageMapper;
    @Resource
    private DropRateService dropRateService;
    private ConsignmentService consignmentService;
    private ApprovalService approvalService;
    @Resource
    private GaiaSdReplenishHMapper gaiaSdReplenishHMapper;
    @Test
    public void updateMessage() {
        GaiaSdMessage sdMessage = new GaiaSdMessage();
        sdMessage.setGsmVoucherId("GSMC2021000037");
        GetLoginOutData userInfo = new GetLoginOutData();
        userInfo.setClient("10000013");
        gaiaSdMessageService.updateMessage(userInfo, sdMessage);
    }

    @Test
    public void SqlTest() {
        gaiaSdMessageMapper.updateReadFlag("10000013","A88");
        gaiaSdMessageMapper.updateWeekReadFlag("10000013","A88");
    }

    @Test
    public void xhl() {
        ReportInfoDto dto=new ReportInfoDto();
        List<String> strings =new ArrayList<>();
        strings.add("10000005");
       // strings.add("10000003");
        dto.setClientIds(strings);
        List<String> strings2 =new ArrayList<>();
        //strings2.add("10002");
        //strings2.add("10007");
        dto.setStoreIds(strings2);
        dto.setBeginDate(DateUtil.addDayString(new Date(),-110));
        dto.setEndDate(DateUtil.getFullDate());
       // List<ReportInfoVo> listDropList = dropRateService.getListDropList(dto);
        //System.out.println(JSON.toJSONString(listDropList));
    }

    @Test
    public void xhl2() {
        List<String> strings=new ArrayList<>();
        strings.add("10000005");
        strings.add("10000003");
        List<StoreData> stores = dropRateService.getStores(strings);
        System.out.println(JSON.toJSONString(stores));
    }
        @Test
    public void Test3() {
        StoreRecommendedSaleDto dto =new StoreRecommendedSaleDto();
        dto.setClientId("10000042");
        dto.setQueryStartDate("20211201");
        List<String> strings =new ArrayList<>();
        strings.add("10001");
        dto.setBrIds(strings);
       // dto.setSaleIds(strings);
        dto.setQueryEndDate("20211208");
        PageInfo report = consignmentService.getStoreRecommendReport(dto);
        // List<StoreReportVo> storeReport = consignmentService.getStoreRecommendReport(dto);
       //  List<StoreReportVo> storeReport1 = consignmentService.getStoreUnRecommendReport(dto);
        System.out.println(report);
    }

    @Test
    public void Test1() {
        /*StoreDto storeDto=new StoreDto();
        storeDto.setClient("10000042");*/
        /*StoreDto storeDto=new StoreDto();
        StoreVoList allStores = consignmentService.getClientBaseInfo(storeDto);*/
        QueryDto queryDto=new QueryDto();
        queryDto.setPageNum(1);
        queryDto.setPageSize(10);
        com.github.pagehelper.PageInfo<ClientBaseInfoVo> clientBaseInfo = dropRateService.getClientBaseInfo(queryDto);
        System.out.println(clientBaseInfo);
    }
    @Test
    public void test() {
        ApprovalDto approvalDto =new ApprovalDto();
       // approvalDto.setProPym("amxl");
       // approvalDto.setClient("10000013");
        approvalDto.setDeliveryNumber("XD2021000001");
        approvalDto.setPageNum(1);
        approvalDto.setPageSize(10);
        com.github.pagehelper.PageInfo<ApprovalVo> allList = approvalService.getAllList(approvalDto);
        System.out.println(allList);
    }

    @Test
    public void test1() {
        StoreDto storeDto=new StoreDto();
        storeDto.setClient("10000005");
        StoreVoList allStores = consignmentService.getAllStores(storeDto);
        System.out.println(allStores);
    }
}

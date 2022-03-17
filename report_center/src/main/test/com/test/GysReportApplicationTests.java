package com.test;

import cn.hutool.system.UserInfo;
import com.gys.GysReportApplication;
import com.gys.common.data.GetLoginOutData;
import com.gys.entity.GaiaSdMessage;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.vo.ApprovalVo;
import com.gys.mapper.GaiaSdMessageMapper;
import com.gys.service.ApprovalService;
import com.gys.service.GaiaSdMessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GysReportApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GysReportApplicationTests {

    @Resource
    private GaiaSdMessageService gaiaSdMessageService;
    @Resource
    private GaiaSdMessageMapper gaiaSdMessageMapper;

    @Resource
    private ApprovalService approvalService;

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
    public void getApprovals() {
        ApprovalDto approvalDto=new ApprovalDto();
        approvalDto.setDeliveryNumber("XD2021000089");
       // List<ApprovalVo> listBydeliveryNumber = approvalService.getListBydeliveryNumber(approvalDto);
       // System.out.println(listBydeliveryNumber);
    }
}

package com.gys.service.Impl;

import com.gys.common.exception.BusinessException;
import com.gys.mapper.MaterialMonitorMapper;
import com.gys.service.MaterialMonitorJobService;
import com.gys.util.DateUtil;
import com.gys.util.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Author jiht
 * @Description
 * @Date 2022/2/21 13:58
 * @Param
 * @Return
 **/
@Service
@Slf4j
public class MaterialMonitorJobServiceImpl implements MaterialMonitorJobService {

    @Autowired
    private MaterialMonitorMapper materialMonitorMapper;

    @Resource
    private MailUtil mailUtil;

    @Override
    public void saveMonitorData(String batchDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String yesterday = dateFormat.format(DateUtil.addDay(batchDate, "yyyyMMdd", -1));
        String monthBeginDay = yesterday.substring(0, 6) + "01";
        log.info("[物料凭证监控]job日期：{}，统计起期：{}，昨天日期：{}", batchDate, monthBeginDay, yesterday);

        // 查询加盟商数据
        List<String> clientList = materialMonitorMapper.queryAllClients();
        if (clientList == null || clientList.size() <= 0) {
            log.info("[物料凭证监控]批处理日期：{}，加盟商查询失败", batchDate);
            throw new BusinessException("加盟商查询失败");
        }
        // 数据删除
        clearData(batchDate);
        log.info("[物料凭证监控]job日期：{}，数据清除完成", batchDate);


        // 循环提取加盟商数据
        for (String client : clientList) {
            dealOneClient(client, batchDate, monthBeginDay, yesterday);
        }
        log.info("[物料凭证监控]job日期：{}，加盟商数据提取完成", batchDate);


        // 发送预警邮件
        if (!sendMonitorMail(monthBeginDay, yesterday, batchDate)) {
            throw new BusinessException("邮件发送失败");
        }
        log.info("[物料凭证监控]job日期：{}，邮件发送完成", batchDate);
    }

    /**
     * @Author jiht
     * @Description 提取一个加盟商数据
     * @Date 2022/2/22 15:39
     * @Param []
     * @Return void
     **/
    private void dealOneClient(String client, String batchDate, String monthBeginDay, String yesterday) {
        // 获取物料凭证数据
        materialMonitorMapper.insertMonitorMaterialDoc(client, monthBeginDay, batchDate);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取物料凭证数据", batchDate, client);
        // 获取物料凭证LOG表数据
        materialMonitorMapper.insertMonitorMaterialLog(client, monthBeginDay, batchDate);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取物料凭证LOG表数据", batchDate, client);
        // 获取虚拟品商品数据
        materialMonitorMapper.insertMonitorProductXnp(client, monthBeginDay, batchDate);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取虚拟品商品数据", batchDate, client);

        // 获取业务数据 GD-退供应商
        materialMonitorMapper.insertMonitorServiceData_GD(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 GD-退供应商", batchDate, client);
        // 获取业务数据 XD-销售 PD-配送 ND-互调配送
        materialMonitorMapper.insertMonitorServiceData_XDPDND(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 XD-销售 PD-配送 ND-互调配送", batchDate, client);
        // 获取业务数据 CD-仓库验收
        materialMonitorMapper.insertMonitorServiceData_CDdc(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 CD-仓库验收", batchDate, client);
        // 获取业务数据 CD-门店验收
        materialMonitorMapper.insertMonitorServiceData_CDsd(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 CD-门店验收", batchDate, client);
        // 获取业务数据 ED-销退 TD-退库 MD-互调退库
        materialMonitorMapper.insertMonitorServiceData_EDTDMD(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 ED-销退 TD-退库 MD-互调退库", batchDate, client);
        // 获取业务数据 ZD-仓库自用
        materialMonitorMapper.insertMonitorServiceData_ZD(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 ZD-仓库自用", batchDate, client);
        // 获取业务数据 ZD-门店自用
        materialMonitorMapper.insertMonitorServiceData_ZDsd(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 ZD-门店自用", batchDate, client);
        // 获取业务数据 BD-报损出库
        materialMonitorMapper.insertMonitorServiceData_BDdc(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 BD-报损出库", batchDate, client);
        // 获取业务数据 BD-门店报损
        materialMonitorMapper.insertMonitorServiceData_BDsd(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 BD-门店报损", batchDate, client);
        // 获取业务数据 LS-零售
        materialMonitorMapper.insertMonitorServiceData_LS(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 LS-零售", batchDate, client);
        // 获取业务数据 SY-仓库损溢
        materialMonitorMapper.insertMonitorServiceData_SYdc(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 SY-仓库损溢", batchDate, client);
        // 获取业务数据 SY-门店损溢
        materialMonitorMapper.insertMonitorServiceData_SYsd(client, monthBeginDay, yesterday);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，获取业务数据 SY-门店损溢", batchDate, client);

        // 差异结果生成
        materialMonitorMapper.insertMonitorMaterialDiff(client, batchDate);
        log.info("[物料凭证监控]job日期：{}，加盟商：{}，差异结果生成", batchDate, client);
    }

    /**
     * @Author jiht
     * @Description 数据删除
     * @Date 2022/2/21 17:06
     * @Param [batchDate]
     * @Return void
     **/
    private void clearData(String batchDate) {
        materialMonitorMapper.clearMonitorMaterialDoc();
        materialMonitorMapper.clearMonitorMaterialLog();
        materialMonitorMapper.clearMonitorProductXnp();
        materialMonitorMapper.clearMonitorServiceData();
        materialMonitorMapper.clearMonitorMaterialDiff(batchDate);
    }

    /**
     * @Author jiht
     * @Description 发送监控邮件
     * @Date 2022/2/22 15:24
     * @Param [monthBeginDay, yesterday, batchDate]
     * @Return boolean
     **/
    private boolean sendMonitorMail(String monthBeginDay, String yesterday, String batchDate) {
        List<String> mailList = materialMonitorMapper.queryMails();
        if (mailList == null || mailList.size() <= 0) {
            log.info("[物料凭证监控]批处理日期：{}，收件人查询失败", batchDate);
            throw new BusinessException("收件人查询失败");
        }
        // 收件人
        String[] receiveMails = mailList.toArray(new String[mailList.size()]);
        // 主题
        String subject = "业务单据与物料凭证监控";
        // 正文
        String content = createMailContent(monthBeginDay, yesterday, batchDate);
        log.info("[物料凭证监控]邮件正文：{}", content);

        Boolean result = mailUtil.sendMail(receiveMails, subject, content);
        log.info("[物料凭证监控]邮件发送结果：{}", content);

        return result;
    }

    /**
     * @Author jiht
     * @Description 组装邮件正文
     * @Date 2022/2/22 14:00
     * @Param [batchDate]
     * @Return java.lang.String
     **/
    public String createMailContent(String monthBeginDay, String yesterday, String batchDate) {
        int diffCount = materialMonitorMapper.countMaterailDiff(batchDate);
        // 无差异时直接返回
        if (diffCount <= 0) {
            return Integer.parseInt(monthBeginDay.substring(4, 6)) + "月1日至" + Integer.parseInt(yesterday.substring(4, 6)) + "月" + Integer.parseInt(yesterday.substring(6, 8)) + "日，业务单据与物料凭证检查无差异。";
        }
        // 查询数据
        List<Map<String, String>> contentList = materialMonitorMapper.queryMailInfo(batchDate);
        // 组装html邮件
        StringBuilder contentBuilder = new StringBuilder("");
        contentBuilder.append("<p>																																");
        contentBuilder.append("<p class=\"MsoNormal\" style=\"text-align:justify;font-family:Calibri;font-size:10.5pt;\">                                           ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-size:16px;font-family:&quot;\">" + Integer.parseInt(monthBeginDay.substring(4, 6)) + "月1日至" + Integer.parseInt(yesterday.substring(4, 6)) + "月" + Integer.parseInt(yesterday.substring(6, 8)) + "日，业务单据与物料凭证检查结果如下：</span>                                               ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("<p>                                                                                                                              ");
        contentBuilder.append("<table class=\"MsoTableGrid\" border=\"1\" cellspacing=\"0\" style=\"border:none;\">                                                     ");
        contentBuilder.append("<tbody>                                                                                                                          ");
        contentBuilder.append("<tr>                                                                                                                             ");
        contentBuilder.append("<td width=\"180\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">                                                          ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:&quot;font-size:10.5pt;\">&nbsp;</span>                                                                  ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</td>                                                                                                                            ");
        contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">                                                          ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:&quot;font-size:14px;\">未调用凭证</span><span style=\"font-family:Calibri;font-size:10.5000pt;\"></span>       ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</td>                                                                                                                            ");
        contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">                                                          ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:&quot;font-size:14px;\">调用失败</span><span style=\"font-family:Calibri;font-size:10.5000pt;\"></span>        ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</td>                                                                                                                            ");
        contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">                                                          ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:&quot;font-size:14px;\">数据不符</span><span style=\"font-family:Calibri;font-size:10.5000pt;\"></span>        ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</td>                                                                                                                            ");
        contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">                                                          ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:&quot;font-size:14px;\">单据不全</span><span style=\"font-family:Calibri;font-size:10.5000pt;\"></span>        ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</td>                                                                                                                            ");
        contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">                                                          ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:&quot;font-size:14px;\">总计</span><span style=\"font-family:Calibri;font-size:10.5000pt;\"></span>          ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</td>                                                                                                                            ");
        contentBuilder.append("</tr>                                                                                                                            ");

        contentList.stream().forEach(o -> {
            contentBuilder.append("<tr>                                                                                                                             ");
            contentBuilder.append("<td width=\"180\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">" + o.get("name") + "</td>                                               ");
            contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">" + o.get("notInvoked") + "</td>                                                  ");
            contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">" + o.get("callFailed") + "</td>                                                  ");
            contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">" + o.get("dataDiscrepancy") + "</td>                                                  ");
            contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">" + o.get("incompleteDocuments") + "</td>                                                  ");
            contentBuilder.append("<td width=\"110\" valign=\"top\" style=\"border:0.5000pt solid windowtext;\">" + o.get("total") + "</td>                                                  ");
            contentBuilder.append("</tr>                                                                                                                            ");
        });

        contentBuilder.append("                                                                                                                                 ");
        contentBuilder.append("</tbody>                                                                                                                         ");
        contentBuilder.append("</table>                                                                                                                         ");
        contentBuilder.append("<span style=\"font-family:Microsoft YaHei;\"></span>                                                                               ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("<p class=\"MsoNormal\">                                                                                                            ");
        contentBuilder.append("<span style=\"font-family:Microsoft YaHei;\">&nbsp;</span>                                                                         ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("</p>                                                                                                                             ");
        contentBuilder.append("<p>                                                                                                                              ");
        contentBuilder.append("<br />                                                                                                                           ");
        contentBuilder.append("</p>                                                                                                                             ");

        return contentBuilder.toString();
    }
}
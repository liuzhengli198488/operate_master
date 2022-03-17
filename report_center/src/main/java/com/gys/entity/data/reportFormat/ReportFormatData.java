package com.gys.entity.data.reportFormat;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "GAIA_TICHENG_PROPLAN_PRO_N")
@Data
public class ReportFormatData {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private Long id;
    /*
    加盟商
     */
    @Column(name = "CLIENT")
    private String client;
    /*
    用户ID
     */
    @Column(name = "USER_ID")
    private String userId;
    /*
    路由ID
    */
    @Column(name = "RESOURCE_ID")
    private String resourceId;
    /*
    页签名称
     */
    @Column(name = "TAB_NAME")
    private String tabName;
    /*
    格式明细
     */
    @Column(name = "FORMAT_DETAIL")
    private String formatDetail;
    /*
   创建日期
    */
    @Column(name = "CREATE_DATE")
    private String createDate;
    /*
   更新日期
    */
    @Column(name = "UPDATE_DATE")
    private String updateDate;
}

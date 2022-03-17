package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;


@Data
@Table(name = "GAIA_STORE_DATA")
public class StoreOutData {
    @ApiModelProperty(value = "加盟商")
    private String client;

    @ApiModelProperty(value = "门店编码")
    private String stoCode;

    @ApiModelProperty(value = "门店名称")
    private String stoName;

    @ApiModelProperty(value = "助记码")
    private String stoPym;

    @ApiModelProperty(value = "门店简称")
    private String stoShortName;

    @ApiModelProperty(value = "门店属性")
    private String stoAttribute;

    @ApiModelProperty(value = "配送方式")
    private String stoDeliveryMode;

    @ApiModelProperty(value = "关联门店")
    private String stoRelationStore;

    @ApiModelProperty(value = "门店状态")
    private String stoStatus;

    @ApiModelProperty(value = "经营面积")
    private String stoArea;

    @ApiModelProperty(value = "开业日期")
    private String stoOpenDate;

    @ApiModelProperty(value = "关店日期")
    private String stoCloseDate;

    @ApiModelProperty(value = "详细地址")
    private String stoAdd;

    @ApiModelProperty(value = "省")
    private String stoProvince;

    @ApiModelProperty(value = "城市")
    private String stoCity;

    @ApiModelProperty(value = "区/县")
    private String stoDistrict;

    @ApiModelProperty(value = "是否医保店")
    private String stoIfMedicalcare;

    @ApiModelProperty(value = "DTP")
    private String stoIfDtp;

    @ApiModelProperty(value = "税分类")
    private String stoTaxClass;

    @ApiModelProperty(value = "委托配送公司")
    private String stoDeliveryCompany;

    @ApiModelProperty(value = "连锁总部")
    private String stoChainHead;

    @ApiModelProperty(value = "纳税主体")
    private String stoTaxSubject;

    @ApiModelProperty(value = "税率")
    private String stoTaxRate;

    @ApiModelProperty(value = "统一社会信用代码")
    private String stoNo;

    @ApiModelProperty(value = "法人")
    private String stoLegalPerson;

    @ApiModelProperty(value = "质量负责人")
    private String stoQua;

    @ApiModelProperty(value = "创建日期")
    private String stoCreDate;

    @ApiModelProperty(value = "创建时间")
    private String stoCreTime;

    @ApiModelProperty(value = "创建人账号")
    private String stoCreId;

    @ApiModelProperty(value = "修改日期")
    private String stoModiDate;

    @ApiModelProperty(value = "修改时间")
    private String stoModiTime;

    @ApiModelProperty(value = "修改人账号")
    private String stoModiId;

    @ApiModelProperty(value = "LOGO地址")
    private String stoLogo;

    @ApiModelProperty(value = "门店组编码")
    private String stogCode;

    @ApiModelProperty(value = "店长")
    private String stoLeader;

    @ApiModelProperty(value = "配送中心")
    private String stoDcCode;

    @ApiModelProperty(value = "店长编码")
    private String stoLeaderCode;

    @ApiModelProperty(value = "模糊匹配字段（门店名称、助记码、门店编码、门店简称）")
    private String nameOrCode;

    private static final long serialVersionUID = 1L;
}

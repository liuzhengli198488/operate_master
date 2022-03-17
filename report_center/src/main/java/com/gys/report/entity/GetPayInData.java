package com.gys.report.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GetPayInData {
    @ApiModelProperty(value = "加盟商")
    private String clientId;
    private String brId;
    private String billNo;
    private BigDecimal amount;
    private BigDecimal zlAmount;
    private BigDecimal dkAmount;
    private String count;
    private String payCode;
    private String storeCode;
    private String cardType;
    @ApiModelProperty(value = "类型")
    private String type;
    private BigDecimal dzqAmount;
    private String cardNo;
    private String jfdxPoint;
    private BigDecimal jfdxAmount;
    private String dyqNo;
    private BigDecimal dyqAmount;
    private String dyqReason;
    private BigDecimal czkAmount;
    private String czkNo;
    private String userId;
    private String integralAddSet;
    private String ybCardNo;
    private BigDecimal ybAmount;
    private List<GetDzqInData> dzqList; //暂未使用
    private List<GetPayTypeInData> payTypeList;
    private List<ElectronDetailOutData> selectElectronList; //电子券列表
    private List<GaiaSdRechargeCard> selectRechargeCardList; //储值卡列表

    //订单信息对象
    private GaiaSdSaleH gaiaSdSaleH;
    private List<GaiaSdSaleD> gaiaSdSaleDList;
    private List<List<SelectRecipelDrugsOutData>> selectDataIndexList;
    private List<GiftPromOutData> giftPromOutDataList;
    private String memberId;//会员唯一标识
    private String[] payName;

}

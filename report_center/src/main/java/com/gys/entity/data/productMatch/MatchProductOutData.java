package com.gys.entity.data.productMatch;

import lombok.Data;

@Data
public class MatchProductOutData {
    private String clientId;//客户编码
    private String matchCode;//匹配编码
    private String clientName;//客户名
    private String stoCode;//门店编码
    private String stoName;//门店名
    private String matchBatch;//匹配批次
    private String matchStatus;//匹配状态
    private String matchName;//匹配状态名
    private Integer matchDegreen;//匹配度
    private String proCode;//用户编码
    private String proCommonName;//商品名称
    private String proSpecs;//规格
    private String factoryName;//生产厂家
    private String proBarcode;//国际条形码
    private String proRegisterNo;//批准文号
    private String proCodeG;//商品编码（G）
    private String proCommonNameG;//商品名称（G）
    private String proSpecsG;//规格（G）
    private String factoryNameG;//生产厂家（G）
    private String proBarcodeG;//国际条形码（G）
    private String proBarcode2G;//国际条形码2（G）
    private String proRegisterNoG;//批准文号（G）
    private String isProSpecs;//规格是否匹配  Y 是 N 否
    private String isFactoryName;//生产厂家是否匹配 Y 是 N否
    private String isProBarcode;//国际条码是否匹配 Y 是 N否
    private String isProRegisterNo;//批准文号是否匹配 Y 是 N否
    private String proClassG;
    private String proClassNameG;
    private String proCompClassG;
    private String proCompClassNameG;
    private Integer picCount;
    private String matchDate;//商品导入时间
    private String updater; //更新人
    private String updateTime; // 更新时间
}

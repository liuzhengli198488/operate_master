package com.gys.entity.data.productMatch;

import lombok.Data;

@Data
public class PictureMessage {
    //采集日期
    private String acquisitionDate;
    //地点
    private String site;
    //第几次上传
    private String uploadNumber;
    //图片序号
    private String serialNumber;
    //图片地址
    private String pictureAddress;
    //图片id
    private String id;
}

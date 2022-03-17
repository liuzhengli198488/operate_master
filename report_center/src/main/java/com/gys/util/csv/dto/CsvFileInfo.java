package com.gys.util.csv.dto;

import lombok.Data;

import java.util.Base64;

/**
 * @Description 上传fastDfs的文件信息
 * @Author huxinxin
 * @Date 2021/4/28 14:17
 * @Version 1.0.0
 **/
@Data
public class CsvFileInfo {
    /**
     * 文件流
     */
    private byte[] fileContent;

    /**
     * base64文件流
     */
    private String fileContentEncode;

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 文件名
     */
    private String fileName;

    public CsvFileInfo(byte[] fileContent, long fileSize, String fileName) {
        this.fileContent = fileContent;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.fileContentEncode = Base64.getEncoder().encodeToString(fileContent);
    }

}

package com.gys.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author: libb
 * @date: 2020.04.09
 */
@Data
@Component
@ConfigurationProperties(prefix = "tencent")
public class Tencent {
    /**
     *
     */
    private String cosSecretId;
    /**
     *
     */
    private String cosSecretKey;
    /**
     *
     */
    private String cosRegion;
    /**
     *
     */
    private String cosBucket;
    /**
     *
     */
    private String importPaht;
    /**
     *
     */
    private String exportPath;
    /**
     *
     */
    private String cosUrl;

    /**
     * 发票pdf路径
     */
    private String invoicePath;

    public String getCosUrl() {
        return MessageFormat.format(this.cosUrl, this.cosBucket);
    }
}


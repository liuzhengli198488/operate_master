package com.gys;

import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@EnableSwagger2Doc
@SpringBootApplication
@MapperScan({"com.gys.mapper"})
@EnableCaching
@EnableEurekaClient
@EnableFeignClients
public class GysReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(GysReportApplication.class, args);
        log.info("<启动服务><启动成功><服务启动成功>");
    }
}

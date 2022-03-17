package com.gys.common.config;

import com.spring4all.swagger.EnableSwagger2Doc;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2Doc
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("第一个接口服务页面").apiInfo(apiInfo()).forCodeGeneration(true)
                .select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.gys.controller"))
                .paths(PathSelectors.regex("^.*(?<!error)$"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("report后端API")
                .description("UAT测试地址：")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }


    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeys = new ArrayList<>();
        apiKeys.add(new ApiKey("X-Token", "X-Token", "header"));
        return apiKeys;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences=new ArrayList<>();
        securityReferences.add(new SecurityReference("X-Token", authorizationScopes));
        return securityReferences;
    }

//    @Bean
//    public Docket openRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("第二个接口服务页面")
//                .genericModelSubstitutes(DeferredResult.class)
//                .apiInfo(openapiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.gys.appController"))
////     .apis(predicate)
//                .paths(PathSelectors.any())
//                .build();
//    }

//    private ApiInfo openapiInfo() {
//        return new ApiInfoBuilder()
//                .title("operation移动端API")
//                .description("UAT测试地址：")
//                .termsOfServiceUrl("")
//                .version("1.0")
//                .build();
//    }
}

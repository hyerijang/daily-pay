package com.hyerijang.dailypay.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 설정
 */
@Configuration
public class SwaggerOpenAPIConfig {


    // API info 등록
    @Bean
    public OpenAPI dailyPayAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Daily Pay API")
                .description("Daily Pay의 API 명세서입니다.")
                .version("v1.0.0"));
    }

    //OpenAPI 그룹 설정
    @Bean
    public GroupedOpenApi group1Config() {
        return GroupedOpenApi.builder()
            .group("v1-definition")
            .pathsToMatch("/api/v1/**")
            .build();
    }


}
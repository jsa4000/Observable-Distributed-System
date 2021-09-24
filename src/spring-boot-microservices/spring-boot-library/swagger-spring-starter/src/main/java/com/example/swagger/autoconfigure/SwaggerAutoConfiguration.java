package com.example.swagger.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerAutoConfiguration {

    @Bean
    public Docket api(ApiInfo apiInfo) {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example"))
                //.paths(PathSelectors.ant("/api/**"))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title( "Booking API" )
                .description( "Microservice API to manage bookings" )
                .version( "1.0.0" )
                .build();
    }

}
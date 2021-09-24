package com.example.webflux.autoconfigure;

import com.example.webflux.handler.WebfluxRestControllerAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class WebfluxErrorHandlerAutoConfiguration {

    @Bean
    @Primary
    public WebfluxRestControllerAdvice restControllerAdvice() {
        return new WebfluxRestControllerAdvice();
    }

}

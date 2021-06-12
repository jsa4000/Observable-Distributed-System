package com.example.logging.autoconfigure;

import com.example.logging.aop.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingAspectAutoConfiguration {

    @Bean
    public LoggingAspect loggingAspect() { return new LoggingAspect(); }
}

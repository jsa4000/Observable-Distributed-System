package com.group.ecommerce.catalog.config;


import com.group.ecommerce.catalog.aop.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfig {

    @Bean
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}

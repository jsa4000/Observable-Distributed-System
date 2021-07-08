package com.example.core.autoconfigure;

import com.example.core.converter.OffsetDateTimeReadConverter;
import com.example.core.converter.OffsetDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;

@Configuration
public class ConvertersAutoConfiguration {

    @Bean
    public OffsetDateTimeWriteConverter offsetDateTimeWriteConverter() {
        return new OffsetDateTimeWriteConverter();
    }

    @Bean
    public OffsetDateTimeReadConverter offsetDateTimeReadConverter() {
        return new OffsetDateTimeReadConverter(ZoneOffset.UTC);
    }

}

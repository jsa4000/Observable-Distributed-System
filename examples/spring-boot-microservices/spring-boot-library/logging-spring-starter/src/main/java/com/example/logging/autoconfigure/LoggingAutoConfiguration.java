package com.example.logging.autoconfigure;

import com.example.core.factory.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:logging.yaml", factory = YamlPropertySourceFactory.class, encoding = "UTF-8")
public class LoggingAutoConfiguration {

}

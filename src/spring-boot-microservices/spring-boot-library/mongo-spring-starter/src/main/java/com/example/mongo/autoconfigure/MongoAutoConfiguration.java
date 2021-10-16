package com.example.mongo.autoconfigure;

import com.example.core.factory.YamlPropertySourceFactory;
import com.example.mongo.listener.ContextIndicesListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

@Configuration
@PropertySource(value = "classpath:mongo.yaml", factory = YamlPropertySourceFactory.class, encoding = "UTF-8")
public class MongoAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "spring.data.mongodb.auto-index-creation", havingValue = "false")
    public ContextIndicesListener ContextIndicesListener(MongoTemplate mongoTemplate) {
        return new ContextIndicesListener(mongoTemplate);
    }

    @Bean
    @Primary
    public MongoCustomConversions customConversions(List<Converter> converters) {
        return new MongoCustomConversions(converters);
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

}

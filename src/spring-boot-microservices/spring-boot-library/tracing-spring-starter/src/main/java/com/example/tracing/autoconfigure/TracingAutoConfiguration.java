package com.example.tracing.autoconfigure;

import com.example.core.factory.YamlPropertySourceFactory;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.client.TracingExchangeFilterFunction;
import io.opentracing.contrib.spring.web.client.WebClientSpanDecorator;
import io.opentracing.contrib.spring.web.webfilter.TracingWebFilter;
import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

@Configuration
@PropertySource(value = "classpath:tracing.yaml", factory = YamlPropertySourceFactory.class, encoding = "UTF-8")
public class TracingAutoConfiguration {

    @Bean
    public TracingWebFilter tracingWebFilter(Tracer tracer) {
        return new TracingWebFilter(tracer, Integer.MIN_VALUE, Pattern.compile(""), Collections.emptyList(),
                Arrays.asList(new WebFluxSpanDecorator.StandardTags(), new WebFluxSpanDecorator.WebFluxTags()));
    }

    @Bean
    public WebClient webClient(Tracer tracer) {
        return WebClient.builder()
                .filter(new TracingExchangeFilterFunction(tracer, Collections.singletonList(new WebClientSpanDecorator.StandardTags())))
                .build();
    }

}
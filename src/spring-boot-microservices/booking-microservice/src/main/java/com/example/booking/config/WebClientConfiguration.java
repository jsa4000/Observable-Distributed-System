package com.example.booking.config;

import com.example.booking.clients.ApiClient;
import com.example.booking.clients.car.VehicleApi;
import com.example.booking.config.properties.WebClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Bean("webClientCarProperties")
    @ConfigurationProperties(prefix = "web.clients.car")
    public WebClientProperties clientProperties() {
        return new WebClientProperties();
    }

    @Bean
    public VehicleApi vehicleApi(@Qualifier("webClientCarProperties") WebClientProperties clientProperties) {
        ApiClient client = new ApiClient();
        VehicleApi api = new VehicleApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }
}

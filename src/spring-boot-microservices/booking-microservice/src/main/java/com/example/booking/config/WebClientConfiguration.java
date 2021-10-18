package com.example.booking.config;

import com.example.booking.clients.ApiClient;
import com.example.booking.clients.car.VehicleControllerApi;
import com.example.booking.clients.flight.FlightControllerApi;
import com.example.booking.clients.hotel.HotelControllerApi;
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
    public WebClientProperties carClientProperties() {return new WebClientProperties();}

    @Bean("webClientFlightProperties")
    @ConfigurationProperties(prefix = "web.clients.flight")
    public WebClientProperties flightClientProperties() {
        return new WebClientProperties();
    }

    @Bean("webClientHotelProperties")
    @ConfigurationProperties(prefix = "web.clients.hotel")
    public WebClientProperties hotelClientProperties() {
        return new WebClientProperties();
    }

    @Bean
    public VehicleControllerApi vehicleApi(@Qualifier("webClientCarProperties") WebClientProperties clientProperties) {
        ApiClient client = new ApiClient();
        VehicleControllerApi api = new VehicleControllerApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }

    @Bean
    public FlightControllerApi flightApi(@Qualifier("webClientFlightProperties") WebClientProperties clientProperties) {
        ApiClient client = new ApiClient();
        FlightControllerApi api = new FlightControllerApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }

    @Bean
    public HotelControllerApi hotelApi(@Qualifier("webClientHotelProperties") WebClientProperties clientProperties) {
        ApiClient client = new ApiClient();
        HotelControllerApi api = new HotelControllerApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }
}

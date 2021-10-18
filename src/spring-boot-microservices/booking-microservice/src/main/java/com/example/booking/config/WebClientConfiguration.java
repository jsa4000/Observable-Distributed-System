package com.example.booking.config;

import com.example.booking.clients.ApiClient;
import com.example.booking.clients.RFC3339DateFormat;
import com.example.booking.clients.car.VehicleControllerApi;
import com.example.booking.clients.flight.FlightControllerApi;
import com.example.booking.clients.hotel.HotelControllerApi;
import com.example.booking.config.properties.WebClientProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.DateFormat;
import java.util.TimeZone;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Bean("webClientDateFormat")
    public DateFormat dateFormat() {
        DateFormat dateFormat = new RFC3339DateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    @Bean("webClientObjectMapper")
    public ObjectMapper objectMapper(DateFormat dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNullableModule jnm = new JsonNullableModule();
        mapper.registerModule(jnm);
        return mapper;
    }

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
    public VehicleControllerApi vehicleApi(@Qualifier("webClientCarProperties") WebClientProperties clientProperties,
                                           @Qualifier("webClientObjectMapper") ObjectMapper mapper,
                                           @Qualifier("webClientDateFormat") DateFormat dateFormat,
                                           WebClient webClient) {
        ApiClient client = new ApiClient(webClient,mapper,dateFormat);
        VehicleControllerApi api = new VehicleControllerApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }

    @Bean
    public FlightControllerApi flightApi(@Qualifier("webClientFlightProperties") WebClientProperties clientProperties,
                                         @Qualifier("webClientObjectMapper") ObjectMapper mapper,
                                         @Qualifier("webClientDateFormat") DateFormat dateFormat,
                                         WebClient webClient){
        ApiClient client = new ApiClient(webClient,mapper,dateFormat);
        FlightControllerApi api = new FlightControllerApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }

    @Bean
    public HotelControllerApi hotelApi(@Qualifier("webClientHotelProperties") WebClientProperties clientProperties,
                                       @Qualifier("webClientObjectMapper") ObjectMapper mapper,
                                       @Qualifier("webClientDateFormat") DateFormat dateFormat,
                                       WebClient webClient) {
        ApiClient client = new ApiClient(webClient,mapper,dateFormat);
        HotelControllerApi api = new HotelControllerApi(client);
        client.setBasePath(clientProperties.getUrl());
        return api;
    }
}

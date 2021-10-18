package com.example.booking.service.impl;

import com.example.booking.clients.flight.FlightControllerApi;
import com.example.booking.clients.flight.model.FlightDto;
import com.example.booking.exception.FlightNotFoundException;
import com.example.booking.exception.FlightServiceException;
import com.example.booking.service.FlightService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightControllerApi flightApi;

    @Override
    @Timed("com.example.booking.service")
    public Mono<FlightDto> save(FlightDto flight) {
        return flightApi.saveFlight(flight)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.booking.service")
    public Mono<FlightDto> findById(String id) {
        return flightApi.findFlightById(id)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof WebClientResponseException) {
            if (((WebClientResponseException) ex).getStatusCode() == HttpStatus.NOT_FOUND) {
                return new FlightNotFoundException(ex.getMessage());
            }
            return new FlightServiceException(ex.getMessage());
        }
        return new FlightServiceException(ex.getMessage());
    }

}

package com.example.flight.controller;

import com.example.flight.controller.dto.FlightDto;
import com.example.flight.exception.FlightNotFoundException;
import com.example.flight.mapper.FlightMapper;
import com.example.flight.service.FlightService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FlightController implements FlightsApi{

    private final FlightService flightService;

    private final FlightMapper flightMapper;

    @Override
    @Timed("com.example.flight.controller")
    public Mono<ResponseEntity<Void>> deleteFlight(String id, ServerWebExchange exchange) {
        return flightService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.flight.controller")
    public Mono<ResponseEntity<Flux<FlightDto>>> findAllFlights(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(flightService.findAll().map(flightMapper::fromDomain)
                .onErrorMap(this::handleError)));
    }

    @Override
    @Timed("com.example.flight.controller")
    public Mono<ResponseEntity<FlightDto>> findFlightById(String id, ServerWebExchange exchange) {
        return flightService.findById(id)
                .map(flightMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.flight.controller")
    public Mono<ResponseEntity<FlightDto>> saveFlight(FlightDto FlightDto, ServerWebExchange exchange) {
        return flightService.save(flightMapper.toDomain(FlightDto))
                .map(flightMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof FlightNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}

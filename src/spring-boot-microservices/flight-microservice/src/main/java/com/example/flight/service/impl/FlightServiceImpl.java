package com.example.flight.service.impl;

import com.example.flight.domain.Flight;
import com.example.flight.exception.FlightNotFoundException;
import com.example.flight.exception.FlightServiceException;
import com.example.flight.repositories.ReactiveFlightRepository;
import com.example.flight.service.FlightService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    public final ReactiveFlightRepository flightRepository;

    @Override
    @Timed("com.example.flight.service")
    public Mono<Flight> save(Flight flight) {
        return flightRepository.findById(flight.getId())
                        .flatMap(x -> flightRepository.save(flight))
                        .switchIfEmpty(Mono.defer(() -> flightRepository.save(flight)))
                        .flatMap(x -> flightRepository.findById(flight.getId()))
                .onErrorMap(FlightServiceException::new);
    }

    @Override
    @Timed("com.example.flight.service")
    public Mono<Void> delete(String id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new FlightNotFoundException()))
                .flatMap(x -> flightRepository.deleteById(id)
                        .onErrorMap(FlightServiceException::new));
    }

    @Override
    @Timed("com.example.flight.service")
    public Flux<Flight> findAll() {
        return flightRepository.findAll()
                .onErrorMap(FlightServiceException::new);
    }

    @Override
    @Timed("com.example.flight.service")
    public Mono<Flight> findById(String id) {
        return flightRepository.findById(id)
                .onErrorMap(FlightServiceException::new)
                .switchIfEmpty(Mono.error(new FlightNotFoundException()));
    }

}

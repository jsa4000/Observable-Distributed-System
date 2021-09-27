package com.example.flight.service;

import com.example.flight.domain.Flight;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {

    /**
     * Create or Update a hotel
     *
     * @param flight
     * @return
     */
    Mono<Flight> save(Flight flight);

    /**
     * Delete an existing hotel
     *
     * @param id
     * @return
     */
    Mono<Void> delete(String id);

    /**
     * Retrieve all hotel
     *
     * @return
     */
    Flux<Flight> findAll();

    /**
     * Find hotel by Id
     *
     * @param id
     * @return
     */
     Mono<Flight> findById(String id);
}

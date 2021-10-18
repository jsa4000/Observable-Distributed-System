package com.example.booking.service;

import com.example.booking.clients.flight.model.FlightDto;
import reactor.core.publisher.Mono;

public interface FlightService {

    /**
     * Create or Update a flight
     *
     * @param vehicle
     * @return
     */
    Mono<FlightDto> save(FlightDto flight);

    /**
     * Find flight by Id
     *
     * @param id
     * @return
     */
     Mono<FlightDto> findById(String id);

}

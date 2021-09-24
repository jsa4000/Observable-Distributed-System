package com.example.booking.service;

import com.example.booking.clients.car.model.VehicleDto;
import reactor.core.publisher.Mono;

public interface VehicleService {

    /**
     * Create or Update a Vehicle
     *
     * @param vehicle
     * @return
     */
    Mono<VehicleDto> save(VehicleDto vehicle);

    /**
     * Find vehicle by Id
     *
     * @param id
     * @return
     */
     Mono<VehicleDto> findById(String id);

}

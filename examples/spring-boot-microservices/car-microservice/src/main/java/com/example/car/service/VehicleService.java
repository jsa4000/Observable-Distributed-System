package com.example.car.service;

import com.example.car.domain.Vehicle;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VehicleService {

    /**
     * Create or Update a vehicle
     *
     * @param vehicle
     * @return
     */
    Mono<Vehicle> save(Vehicle vehicle);

    /**
     * Delete an existing Vehicle
     *
     * @param id
     * @return
     */
    Mono<Void> delete(String id);

    /**
     * Retrieve all vehicle
     *
     * @return
     */
    Flux<Vehicle> findAll();

    /**
     * Find vehicle by Id
     *
     * @param id
     * @return
     */
     Mono<Vehicle> findById(String id);
}

package com.example.hotel.service;

import com.example.hotel.domain.Hotel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HotelService {

    /**
     * Create or Update a vehicle
     *
     * @param vehicle
     * @return
     */
    Mono<Hotel> save(Hotel vehicle);

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
    Flux<Hotel> findAll();

    /**
     * Find vehicle by Id
     *
     * @param id
     * @return
     */
     Mono<Hotel> findById(String id);
}

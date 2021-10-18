package com.example.booking.service;

import com.example.booking.clients.hotel.model.HotelDto;
import reactor.core.publisher.Mono;

public interface HotelService {

    /**
     * Create or Update a Hotel
     *
     * @param vehicle
     * @return
     */
    Mono<HotelDto> save(HotelDto vehicle);

    /**
     * Find hotel by Id
     *
     * @param id
     * @return
     */
     Mono<HotelDto> findById(String id);

}

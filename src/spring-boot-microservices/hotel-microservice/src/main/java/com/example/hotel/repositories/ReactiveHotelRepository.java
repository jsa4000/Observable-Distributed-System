package com.example.hotel.repositories;

import com.example.hotel.domain.Hotel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveHotelRepository extends ReactiveCrudRepository<Hotel, String> {
}

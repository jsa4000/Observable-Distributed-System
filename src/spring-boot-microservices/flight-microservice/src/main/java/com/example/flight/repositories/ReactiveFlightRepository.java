package com.example.flight.repositories;

import com.example.flight.domain.Flight;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveFlightRepository extends ReactiveCrudRepository<Flight, String> {
}

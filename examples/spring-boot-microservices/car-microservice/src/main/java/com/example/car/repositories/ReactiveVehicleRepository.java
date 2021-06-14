package com.example.car.repositories;

import com.example.car.domain.Vehicle;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveVehicleRepository extends ReactiveCrudRepository<Vehicle, String> {
}

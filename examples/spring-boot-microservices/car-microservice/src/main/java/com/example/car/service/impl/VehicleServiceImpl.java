package com.example.car.service.impl;

import com.example.car.domain.Vehicle;
import com.example.car.exception.VehicleNotFoundException;
import com.example.car.exception.VehicleServiceException;
import com.example.car.repositories.ReactiveVehicleRepository;
import com.example.car.service.VehicleService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    public final ReactiveVehicleRepository vehicleRepository;

    @Override
    @Timed("com.example.car.service")
    public Mono<Vehicle> save(Vehicle vehicle) {
        return vehicleRepository.findById(vehicle.getId())
                        .flatMap(x -> vehicleRepository.save(vehicle))
                        .switchIfEmpty(Mono.defer(() -> vehicleRepository.save(vehicle)))
                        .flatMap(x -> vehicleRepository.findById(vehicle.getId()))
                .onErrorMap(VehicleServiceException::new);
    }

    @Override
    @Timed("com.example.car.service")
    public Mono<Void> delete(String id) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException()))
                .flatMap(x -> vehicleRepository.deleteById(id)
                        .onErrorMap(VehicleServiceException::new));
    }

    @Override
    @Timed("com.example.car.service")
    public Flux<Vehicle> findAll() {
        return vehicleRepository.findAll()
                .onErrorMap(VehicleServiceException::new);
    }

    @Override
    @Timed("com.example.car.service")
    public Mono<Vehicle> findById(String id) {
        return vehicleRepository.findById(id)
                .onErrorMap(VehicleServiceException::new)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException()));
    }

}

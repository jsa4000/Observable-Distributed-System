package com.example.car.controller;

import com.example.car.domain.Vehicle;
import com.example.car.exception.VehicleNotFoundException;
import com.example.car.service.VehicleService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/vehicle", headers = "Accept=application/json")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Vehicle>> save(@RequestBody Vehicle vehicle) {
        return vehicleService.save(vehicle)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @DeleteMapping("/{id}")
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Void>> deleteVehicle(@PathVariable("id") String id) {
        return vehicleService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @GetMapping("/{id}")
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Vehicle>> findVehicleById(@PathVariable("id") String id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @GetMapping
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Flux<Vehicle>>> findAllVehicles() {
        return Mono.just(ResponseEntity.ok(vehicleService.findAll()
                .onErrorMap(this::handleError)));
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof VehicleNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}

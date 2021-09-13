package com.example.car.controller;

import com.example.car.exception.VehicleNotFoundException;
import com.example.car.mapper.VehicleMapper;
import com.example.car.service.VehicleService;
import com.example.controller.VehicleApi;
import com.example.controller.dto.VehicleDto;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VehicleController implements VehicleApi {

    private final VehicleService vehicleService;

    private final VehicleMapper vehicleMapper;

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Void>> deleteVehicle(String id, ServerWebExchange exchange) {
        return vehicleService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Flux<VehicleDto>>> findAllVehicles(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(vehicleService.findAll().map(vehicleMapper::fromDomain)
                .onErrorMap(this::handleError)));
    }

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<VehicleDto>> findVehicleById(String id, ServerWebExchange exchange) {
        return vehicleService.findById(id)
                .map(vehicleMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<VehicleDto>> saveVehicle(VehicleDto vehicleDto, ServerWebExchange exchange) {
        return vehicleService.save(vehicleMapper.toDomain(vehicleDto))
                .map(vehicleMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof VehicleNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}

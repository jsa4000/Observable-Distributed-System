package com.example.booking.service.impl;

import com.example.booking.clients.car.VehicleApi;
import com.example.booking.clients.car.model.VehicleDto;
import com.example.booking.exception.VehicleServiceException;
import com.example.booking.service.VehicleService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleApi vehicleApi;

    @Override
    @Timed("com.example.booking.service")
    public Mono<VehicleDto> save(VehicleDto vehicle) {
        return vehicleApi.saveVehicle(vehicle)
                .onErrorMap(VehicleServiceException::new);
    }

    @Override
    @Timed("com.example.booking.service")
    public Mono<VehicleDto> findById(String id) {
        return vehicleApi.findVehicleById(id)
                .onErrorMap(VehicleServiceException::new);
    }

}

package com.example.booking.service.impl;

import com.example.booking.clients.car.VehicleControllerApi;
import com.example.booking.clients.car.model.VehicleDto;
import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.VehicleNotFoundException;
import com.example.booking.exception.VehicleServiceException;
import com.example.booking.service.VehicleService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleControllerApi vehicleApi;

    @Override
    @Timed("com.example.booking.service")
    public Mono<VehicleDto> save(VehicleDto vehicle) {
        return vehicleApi.saveVehicle(vehicle)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.booking.service")
    public Mono<VehicleDto> findById(String id) {
        return vehicleApi.findVehicleById(id)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof WebClientResponseException) {
            if (((WebClientResponseException) ex).getStatusCode() == HttpStatus.NOT_FOUND) {
                return new VehicleNotFoundException(ex.getMessage());
            }
            return new VehicleServiceException(ex.getMessage());
        }
        return new VehicleServiceException(ex.getMessage());
    }

}

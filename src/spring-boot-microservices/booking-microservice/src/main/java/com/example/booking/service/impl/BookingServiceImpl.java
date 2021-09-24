package com.example.booking.service.impl;

import com.example.booking.clients.car.model.VehicleDto;
import com.example.booking.domain.Booking;
import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.BookingServiceException;
import com.example.booking.repositories.ReactiveBookingRepository;
import com.example.booking.service.BookingService;
import com.example.booking.service.VehicleService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ReactiveBookingRepository bookingRepository;

    private final VehicleService vehicleService;

    @Override
    @Timed("com.example.booking.service")
    public Mono<Booking> save(Booking booking) {

        Mono<VehicleDto> vehicle = vehicleService.findById(booking.getVehicleId());
        Mono<VehicleDto> vehicle2 = vehicleService.findById(booking.getVehicleId());

        return Mono.zip(vehicle, vehicle2)
                .flatMap(data -> bookingRepository.findById(booking.getId())
                        .flatMap(x -> bookingRepository.save(booking))
                        .switchIfEmpty(Mono.defer(() -> bookingRepository.save(booking)))
                        .flatMap(x -> bookingRepository.findById(booking.getId()))
                        .onErrorMap(BookingServiceException::new));
    }

    @Override
    @Timed("com.example.booking.service")
    public Mono<Void> delete(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingNotFoundException()))
                .flatMap(x -> bookingRepository.deleteById(id)
                        .onErrorMap(BookingServiceException::new));
    }

    @Override
    @Timed("com.example.booking.service")
    public Flux<Booking> findAll() {
        return bookingRepository.findAll()
                .onErrorMap(BookingServiceException::new);
    }

    @Override
    @Timed("com.example.booking.service")
    public Mono<Booking> findById(String id) {
        return bookingRepository.findById(id)
                .onErrorMap(BookingServiceException::new)
                .switchIfEmpty(Mono.error(new BookingNotFoundException()));
    }

}

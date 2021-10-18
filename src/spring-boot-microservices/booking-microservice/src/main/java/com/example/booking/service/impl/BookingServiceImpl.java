package com.example.booking.service.impl;

import com.example.booking.domain.Booking;
import com.example.booking.exception.*;
import com.example.booking.repositories.ReactiveBookingRepository;
import com.example.booking.service.BookingService;
import com.example.booking.service.FlightService;
import com.example.booking.service.HotelService;
import com.example.booking.service.VehicleService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ReactiveBookingRepository bookingRepository;

    private final VehicleService vehicleService;

    private final FlightService flightService;

    private final HotelService hotelService;

    @Override
    @Timed("com.example.booking.service")
    public Mono<Booking> save(Booking booking) {

        List<Mono<?>> publishers = new ArrayList<>();

        if (!ObjectUtils.isEmpty(booking.getVehicleId()))
            publishers.add(vehicleService.findById(booking.getVehicleId())
                    .onErrorMap(VehicleNotFoundException::new));

        if (!ObjectUtils.isEmpty(booking.getFlightId()))
            publishers.add(flightService.findById(booking.getFlightId())
                    .onErrorMap(FlightNotFoundException::new));

        if (!ObjectUtils.isEmpty(booking.getHotelId()))
            publishers.add(hotelService.findById(booking.getHotelId())
                    .onErrorMap(HotelNotFoundException::new));

        return Mono.zip(publishers, objects -> Arrays.stream(objects).collect(Collectors.toList()))
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

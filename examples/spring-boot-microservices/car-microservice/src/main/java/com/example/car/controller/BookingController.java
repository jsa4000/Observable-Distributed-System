package com.example.car.controller;

import com.example.car.domain.Booking;
import com.example.car.exception.BookingNotFoundException;
import com.example.car.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/booking", headers = "Accept=application/json")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Mono<ResponseEntity<Booking>> save(@RequestBody Booking booking) {
        return bookingService.save(booking)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBooking(@PathVariable("id") String id) {
        return bookingService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Booking>> findBookingById(@PathVariable("id") String id) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Booking>>> findAllBookings() {
        return Mono.just(ResponseEntity.ok(bookingService.findAll()
                .onErrorMap(this::handleError)));
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof BookingNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}

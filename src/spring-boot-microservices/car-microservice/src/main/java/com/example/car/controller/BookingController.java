package com.example.car.controller;

import com.example.car.exception.BookingNotFoundException;
import com.example.car.mapper.BookingMapper;
import com.example.car.service.BookingService;
import com.example.controller.BookingsApi;
import com.example.controller.dto.BookingDto;
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
public class BookingController implements BookingsApi {

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Void>> deleteBooking(String id, ServerWebExchange exchange) {
        return bookingService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<Flux<BookingDto>>> findAllBookings(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(bookingService.findAll().map(bookingMapper::fromDomain)
                .onErrorMap(this::handleError)));
    }

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<BookingDto>> findBookingById(String id, ServerWebExchange exchange) {
        return bookingService.findById(id)
                .map(bookingMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.car.controller")
    public Mono<ResponseEntity<BookingDto>> saveBooking(BookingDto bookingDto, ServerWebExchange exchange) {
        return bookingService.save(bookingMapper.toDomain(bookingDto))
                .map(bookingMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof BookingNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}

package com.example.booking.controller;

import com.example.booking.controller.dto.BookingDto;
import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.VehicleNotFoundException;
import com.example.booking.mapper.BookingMapper;
import com.example.booking.service.BookingService;
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
    @Timed("com.example.booking.controller")
    public Mono<ResponseEntity<Void>> deleteBooking(String id, ServerWebExchange exchange) {
        return bookingService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.booking.controller")
    public Mono<ResponseEntity<Flux<BookingDto>>> findAllBookings(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(bookingService.findAll().map(bookingMapper::fromDomain)
                .onErrorMap(this::handleError)));
    }

    @Override
    @Timed("com.example.booking.controller")
    public Mono<ResponseEntity<BookingDto>> findBookingById(String id, ServerWebExchange exchange) {
        return bookingService.findById(id)
                .map(bookingMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.booking.controller")
    public Mono<ResponseEntity<BookingDto>> saveBooking(BookingDto bookingDto, ServerWebExchange exchange) {
        return bookingService.save(bookingMapper.toDomain(bookingDto))
                .map(bookingMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof BookingNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        else if (ex instanceof VehicleNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}

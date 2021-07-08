package com.example.booking.service.impl;

import com.example.booking.domain.Booking;
import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.BookingServiceException;
import com.example.booking.repositories.BookingRepository;
import com.example.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;

    @Override
    public Mono<Booking> submit(Booking booking) throws Throwable {
        // TODO: Check if there is a Lock related to the booking to be submitted
        return Mono.just(Booking.builder().build())
                .onErrorMap(BookingServiceException::new);
    }

    @Override
    public Mono<Void> delete(String id) {
        return bookingRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookingNotFoundException()))
                .flatMap(x -> bookingRepository.deleteById(id)
                        .onErrorMap(BookingServiceException::new));
    }

    @Override
    public Flux<Booking> findAll() {
        return bookingRepository.findAll()
                .onErrorMap(BookingServiceException::new);
    }

    @Override
    public Mono<Booking> findById(String id) {
        return bookingRepository.findById(id)
                .onErrorMap(BookingServiceException::new)
                .switchIfEmpty(Mono.error(new BookingNotFoundException()));
    }

}

package com.example.booking.service.impl;

import com.example.booking.clients.hotel.HotelControllerApi;
import com.example.booking.clients.hotel.model.HotelDto;
import com.example.booking.exception.HotelNotFoundException;
import com.example.booking.exception.HotelServiceException;
import com.example.booking.service.HotelService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelControllerApi hotelApi;

    @Override
    @Timed("com.example.booking.service")
    public Mono<HotelDto> save(HotelDto hotel) {
        return hotelApi.saveHotel(hotel)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.booking.service")
    public Mono<HotelDto> findById(String id) {
        return hotelApi.findHotelById(id)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof WebClientResponseException) {
            if (((WebClientResponseException) ex).getStatusCode() == HttpStatus.NOT_FOUND) {
                return new HotelNotFoundException(ex.getMessage());
            }
            return new HotelServiceException(ex.getMessage());
        }
        return new HotelServiceException(ex.getMessage());
    }

}

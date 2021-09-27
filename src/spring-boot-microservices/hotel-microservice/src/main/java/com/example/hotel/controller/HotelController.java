package com.example.hotel.controller;

import com.example.hotel.controller.dto.HotelDto;
import com.example.hotel.exception.HotelNotFoundException;
import com.example.hotel.mapper.HotelMapper;
import com.example.hotel.service.HotelService;
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
public class HotelController implements HotelsApi {

    private final HotelService hotelService;

    private final HotelMapper hotelMapper;

    @Override
    @Timed("com.example.hotel.controller")
    public Mono<ResponseEntity<Void>> deleteHotel(String id, ServerWebExchange exchange) {
        return hotelService.delete(id)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.hotel.controller")
    public Mono<ResponseEntity<Flux<HotelDto>>> findAllHotels(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(hotelService.findAll().map(hotelMapper::fromDomain)
                .onErrorMap(this::handleError)));
    }

    @Override
    @Timed("com.example.hotel.controller")
    public Mono<ResponseEntity<HotelDto>> findHotelById(String id, ServerWebExchange exchange) {
        return hotelService.findById(id)
                .map(hotelMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    @Override
    @Timed("com.example.hotel.controller")
    public Mono<ResponseEntity<HotelDto>> saveHotel(HotelDto hotelDto, ServerWebExchange exchange) {
        return hotelService.save(hotelMapper.toDomain(hotelDto))
                .map(hotelMapper::fromDomain)
                .map(ResponseEntity::ok)
                .onErrorMap(this::handleError);
    }

    private Throwable handleError(Throwable ex) {
        if (ex instanceof HotelNotFoundException)
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}

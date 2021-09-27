package com.example.hotel.service.impl;

import com.example.hotel.domain.Hotel;
import com.example.hotel.exception.HotelNotFoundException;
import com.example.hotel.exception.HotelServiceException;
import com.example.hotel.repositories.ReactiveHotelRepository;
import com.example.hotel.service.HotelService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    public final ReactiveHotelRepository hotelRepository;

    @Override
    @Timed("com.example.hotel.service")
    public Mono<Hotel> save(Hotel hotel) {
        return hotelRepository.findById(hotel.getId())
                        .flatMap(x -> hotelRepository.save(hotel))
                        .switchIfEmpty(Mono.defer(() -> hotelRepository.save(hotel)))
                        .flatMap(x -> hotelRepository.findById(hotel.getId()))
                .onErrorMap(HotelServiceException::new);
    }

    @Override
    @Timed("com.example.hotel.service")
    public Mono<Void> delete(String id) {
        return hotelRepository.findById(id)
                .switchIfEmpty(Mono.error(new HotelNotFoundException()))
                .flatMap(x -> hotelRepository.deleteById(id)
                        .onErrorMap(HotelServiceException::new));
    }

    @Override
    @Timed("com.example.hotel.service")
    public Flux<Hotel> findAll() {
        return hotelRepository.findAll()
                .onErrorMap(HotelServiceException::new);
    }

    @Override
    @Timed("com.example.hotel.service")
    public Mono<Hotel> findById(String id) {
        return hotelRepository.findById(id)
                .onErrorMap(HotelServiceException::new)
                .switchIfEmpty(Mono.error(new HotelNotFoundException()));
    }

}

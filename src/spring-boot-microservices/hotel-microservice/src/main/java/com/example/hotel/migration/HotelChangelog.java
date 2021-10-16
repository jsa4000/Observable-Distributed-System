package com.example.hotel.migration;

import com.example.hotel.domain.Hotel;
import com.example.hotel.repositories.ReactiveHotelRepository;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@ChangeLog(order = "2")
public class HotelChangelog {

    private static final int INITIAL_HOTELS = 100;

    private static final Faker faker = new Faker();

    @ChangeSet(id = "20210614120001-hotels-data-initialization", order = "001", author = "Anonymous")
    public void changeSet01(ReactiveHotelRepository hotelRepository) {
        java.lang.reflect.Proxy.getInvocationHandler(hotelRepository);

        Flux<Hotel> hotelFlux = Flux.range(0, INITIAL_HOTELS)
                .map(HotelChangelog::getVehicle);
        
        hotelRepository.saveAll(hotelFlux)
                .doOnNext(x -> log.trace("Saved hotel with id {}", x.getId()))
                .subscribe();
    }

    private static Hotel getVehicle(int index) {
        return Hotel.builder()
                .id(Integer.toString(index))
                .name(faker.address().streetAddress())
                .address(faker.address().fullAddress())
                .postalCode(faker.address().zipCode())
                .city(faker.address().cityName())
                .country(faker.address().country())
                .rooms(faker.number().numberBetween(1, 8))
                .build();
    }

}
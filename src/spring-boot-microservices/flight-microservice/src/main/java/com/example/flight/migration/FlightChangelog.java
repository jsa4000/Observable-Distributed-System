package com.example.flight.migration;

import com.example.flight.domain.Flight;
import com.example.flight.repositories.ReactiveFlightRepository;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;

@Slf4j
@ChangeLog(order = "2")
public class FlightChangelog {

    private static final int INITIAL_FLIGHTS = 100;

    private static final Faker faker = new Faker();

    @ChangeSet(id = "20210614120001-flights-data-initialization", order = "001", author = "Anonymous")
    public void changeSet01(ReactiveFlightRepository flightRepository) {
        java.lang.reflect.Proxy.getInvocationHandler(flightRepository);

        Flux<Flight> flightFlux = Flux.range(0, INITIAL_FLIGHTS)
                .map(FlightChangelog::getFlight);
        
        flightRepository.saveAll(flightFlux)
                .doOnNext(x -> log.trace("Saved flight with id {}", x.getId()))
                .subscribe();
    }

    private static Flight getFlight(int index) {
        return Flight.builder()
                .id(Integer.toString(index))
                .number(faker.number().numberBetween(10000,90000))
                .source(faker.address().cityName())
                .destination(faker.address().cityName())
                .departure(OffsetDateTime.now())
                .arrival(OffsetDateTime.now().plusMinutes(faker.number().numberBetween(0,1440)))
                .build();
    }

}
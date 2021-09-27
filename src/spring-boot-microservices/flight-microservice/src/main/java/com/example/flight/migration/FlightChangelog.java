package com.example.flight.migration;

import com.example.flight.domain.Flight;
import com.example.flight.repositories.ReactiveFlightRepository;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.Random;

@Slf4j
@ChangeLog(order = "2")
public class FlightChangelog {

    private static final int INITIAL_FLIGHTS = 100;

    private static final Faker faker = new Faker();

    @ChangeSet(id = "20210614120001-flights-data-initialization", order = "001", author = "Anonymous")
    public void changeSet01(ReactiveFlightRepository flightRepository) {
        java.lang.reflect.Proxy.getInvocationHandler(flightRepository);

        Flux<Flight> flightFlux = Flux.range(INITIAL_FLIGHTS, INITIAL_FLIGHTS)
                .map(FlightChangelog::getFlight);
        
        flightRepository.saveAll(flightFlux)
                .doOnNext(x -> log.trace("Saved flight with id {}", x.getId()))
                .subscribe();
    }

    private static Flight getFlight(int index) {
        return Flight.builder()
                .id(Integer.toString(index))
                .from(faker.name().name())
                .to(faker.company().name())
                .fromDate(OffsetDateTime.now())
                .toDate(OffsetDateTime.now().plusDays(faker.number().numberBetween(0,30)))
                .build();
    }

}
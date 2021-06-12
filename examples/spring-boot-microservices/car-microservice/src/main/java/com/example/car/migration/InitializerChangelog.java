package com.example.car.migration;

import com.example.car.domain.Booking;
import com.example.car.repositories.BookingRepository;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@Slf4j
@ChangeLog(order = "1")
public class InitializerChangelog {

    private static final Faker faker = new Faker();

    @ChangeSet(id = "20200627161800-data-initialize", order = "001", author = "Anonymous")
    public void dataInitializer01(BookingRepository BookingRepository) {
        java.lang.reflect.Proxy.getInvocationHandler(BookingRepository);

        Flux<Booking> BookingsFlux = Flux.fromStream(Stream.of(
                Booking.builder().id("1").resourceId("001").active(false).build(),
                Booking.builder().id("2").resourceId("002").active(false).build(),
                Booking.builder().id("3").resourceId("003").active(false).build(),
                Booking.builder().id("4").resourceId("001").active(false).build(),
                Booking.builder().id("5").resourceId("004").active(false).build()
        ));
        BookingRepository.saveAll(BookingsFlux)
                .doOnNext(x -> log.trace("Saved booking with id {}", x.getId()))
                .subscribe();
    }

}
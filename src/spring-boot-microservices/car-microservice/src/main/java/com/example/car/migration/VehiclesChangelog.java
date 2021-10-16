package com.example.car.migration;

import com.example.car.domain.Vehicle;
import com.example.car.repositories.ReactiveVehicleRepository;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@ChangeLog(order = "2")
public class VehiclesChangelog {

    private static final int INITIAL_VEHICLES = 100;

    private static final Faker faker = new Faker();

    @ChangeSet(id = "20210614120001-vehicles-data-initialization", order = "001", author = "Anonymous")
    public void changeSet01(ReactiveVehicleRepository vehicleRepository) {
        java.lang.reflect.Proxy.getInvocationHandler(vehicleRepository);

        Flux<Vehicle> vehicleFlux = Flux.range(0, INITIAL_VEHICLES)
                .map(VehiclesChangelog::getVehicle);
        
        vehicleRepository.saveAll(vehicleFlux)
                .doOnNext(x -> log.trace("Saved vehicle with id {}", x.getId()))
                .subscribe();
    }

    private static Vehicle getVehicle(int index) {
        return Vehicle.builder()
                .id(Integer.toString(index))
                .model(faker.name().name())
                .brand(faker.company().name())
                .color(faker.color().name())
                .tankCapacity(faker.number().randomDouble(2, 0, 1000))
                .fuel(faker.name().name())
                .seats(faker.number().numberBetween(2, 16))
                .year(Integer.toString(faker.number().numberBetween(1991, 2021)))
                .build();
    }

}
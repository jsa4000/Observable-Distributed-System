package com.example.car.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("vehicles")
public class Vehicle {

    @Id
    private String id;
    private String model;
    private String brand;
    private double engineCapacity;
    private String fuel;
    private int seats;
    private String color;
    private String year;
    private int kms;

}

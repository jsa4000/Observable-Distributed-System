package com.example.car.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("vehicles")
public class Vehicle {

    @Id @NotNull @NotEmpty //@Max(64)
    private String id;

    //@Max(256)
    private String brand;

    //@Max(256)
    private String model;

    //@Max(32)
    private String color;

    //@Max(16)
    private String year;

    //@Max(16)
    private String fuel;

    //@Max(9999)
    private Double tankCapacity;

    //@Max(Integer.MAX_VALUE)
    private Integer kms;

    //@Max(16)
    private Integer seats;






}

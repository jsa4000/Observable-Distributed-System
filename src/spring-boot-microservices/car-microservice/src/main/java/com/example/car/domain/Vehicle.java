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

    @Id @NotNull @NotEmpty @Size(max = 64)
    private String id;

    @NotNull @NotEmpty @Size(max = 128)
    private String brand;

    @NotNull @NotEmpty @Size(max = 128)
    private String model;

    @NotNull @NotEmpty @Size(max = 32)
    private String color;

    @NotNull @NotEmpty @Size(max = 4)
    private String year;

    @NotNull @NotEmpty @Size(max = 64)
    private String fuel;

    @Max(9999)
    private Double tankCapacity;

    @Max(Integer.MAX_VALUE)
    private Integer kms;

    @Max(16)
    private Integer seats;






}

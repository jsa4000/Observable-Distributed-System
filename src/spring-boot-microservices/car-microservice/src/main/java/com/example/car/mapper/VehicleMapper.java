package com.example.car.mapper;

import com.example.car.controller.dto.VehicleDto;
import com.example.car.domain.Vehicle;
import org.mapstruct.Mapper;

@Mapper
public interface VehicleMapper {

    Vehicle toDomain(VehicleDto object);

    VehicleDto fromDomain(Vehicle object);

}

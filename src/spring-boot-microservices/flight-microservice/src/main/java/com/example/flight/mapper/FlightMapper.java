package com.example.flight.mapper;

import com.example.flight.controller.dto.FlightDto;
import com.example.flight.domain.Flight;
import org.mapstruct.Mapper;

@Mapper
public interface FlightMapper {

    Flight toDomain(FlightDto object);

    FlightDto fromDomain(Flight object);

}

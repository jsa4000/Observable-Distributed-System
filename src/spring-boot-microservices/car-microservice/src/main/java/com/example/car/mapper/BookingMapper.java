package com.example.car.mapper;

import com.example.car.controller.dto.BookingDto;
import com.example.car.domain.Booking;
import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {

    Booking toDomain(BookingDto object);

    BookingDto fromDomain(Booking object);

}

package com.example.car.mapper;

import com.example.car.domain.Booking;
import com.example.controller.dto.BookingDto;
import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {

    Booking toDomain(BookingDto object);

    BookingDto fromDomain(Booking object);

}

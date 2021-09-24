package com.example.booking.mapper;

import com.example.booking.controller.dto.BookingDto;
import com.example.booking.domain.Booking;
import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {

    Booking toDomain(BookingDto object);

    BookingDto fromDomain(Booking object);

}

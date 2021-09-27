package com.example.hotel.mapper;

import com.example.hotel.controller.dto.HotelDto;
import com.example.hotel.domain.Hotel;
import org.mapstruct.Mapper;

@Mapper
public interface HotelMapper {

    Hotel toDomain(HotelDto object);

    HotelDto fromDomain(Hotel object);

}

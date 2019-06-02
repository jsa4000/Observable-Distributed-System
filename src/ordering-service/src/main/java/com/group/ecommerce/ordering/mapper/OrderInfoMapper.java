package com.group.ecommerce.ordering.mapper;

import com.group.ecommerce.ordering.domain.OrderInfo;
import com.group.ecommerce.ordering.web.api.model.CreateOrderDto;
import com.group.ecommerce.ordering.web.api.model.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderInfoMapper {

    @Mapping(target = "id", ignore = true)
    OrderInfo toDomain(CreateOrderDto order);

    OrderDto fromDomain(OrderInfo order);

}

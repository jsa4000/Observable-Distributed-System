package com.group.ecommerce.order.mapper;

import com.group.ecommerce.order.domain.OrderInfo;
import com.group.ecommerce.order.web.api.model.CreateOrderDto;
import com.group.ecommerce.order.web.api.model.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderInfoMapper {

    @Mapping(target = "id", ignore = true)
    OrderInfo toDomain(CreateOrderDto order);

    OrderDto fromDomain(OrderInfo order);

}

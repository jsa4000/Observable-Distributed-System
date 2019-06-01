package com.group.ecommerce.catalog.mapper;

import com.group.ecommerce.catalog.domain.ProductInfo;
import com.group.ecommerce.catalog.web.api.model.CreateProductDto;
import com.group.ecommerce.catalog.web.api.model.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductInfoMapper {

    @Mapping(target = "id", ignore = true)
    ProductInfo toDomain(CreateProductDto order);

    ProductDto fromDomain(ProductInfo order);

}

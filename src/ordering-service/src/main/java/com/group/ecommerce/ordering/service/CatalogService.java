package com.group.ecommerce.ordering.service;

import com.group.ecommerce.ordering.web.catalog.model.ProductDto;

import java.util.Optional;

public interface CatalogService {

    /**
     * Find Product By Id
     *
     * @param id of the product
     * @return product information
     */
    Optional<ProductDto> findProductById(Long id);

}

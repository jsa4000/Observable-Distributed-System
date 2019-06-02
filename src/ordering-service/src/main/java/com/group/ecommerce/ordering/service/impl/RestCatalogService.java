package com.group.ecommerce.ordering.service.impl;

import com.group.ecommerce.ordering.service.CatalogService;
import com.group.ecommerce.ordering.web.catalog.handler.ProductsApi;
import com.group.ecommerce.ordering.web.catalog.model.ProductDto;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Service
public class RestCatalogService implements CatalogService {

    private final ProductsApi productsApi;


    public RestCatalogService(ProductsApi productsApi) {
        this.productsApi = productsApi;
    }

    @Timed(value = "group.ecommerce.order.service")
    public Optional<ProductDto> findProductById(Long id)  {
        try {
            ProductDto productDto = productsApi.findProductById(id);
            return Optional.of(productDto);
        } catch (RestClientException ex) {
            return Optional.empty();
        }
    }

}

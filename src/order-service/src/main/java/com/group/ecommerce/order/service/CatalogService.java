package com.group.ecommerce.order.service;

import com.group.ecommerce.order.web.catalog.handler.ProductsApi;
import com.group.ecommerce.order.web.catalog.model.ProductDto;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Service
public class CatalogService {

    private final ProductsApi productsApi;


    public CatalogService(ProductsApi productsApi) {
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

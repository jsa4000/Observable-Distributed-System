package com.group.ecommerce.order.config;

import com.group.ecommerce.order.web.catalog.ApiClient;
import com.group.ecommerce.order.web.catalog.handler.ProductsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CatalogConfig {

    @Bean
    public ProductsApi productsApi() {
        return new ProductsApi(apiClient(null,null));
    }

    @Bean
    public ApiClient apiClient(@Value("${services.catalog.endpoint:}") String basePath,
                               RestTemplate restTemplate) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }
}

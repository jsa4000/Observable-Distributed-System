package com.group.ecommerce.catalog.service;

import com.group.ecommerce.catalog.domain.ProductInfo;
import com.group.ecommerce.catalog.exception.ProductNotFoundException;
import com.group.ecommerce.catalog.mapper.ProductInfoMapper;
import com.group.ecommerce.catalog.repository.ProductInfoRepository;
import com.group.ecommerce.catalog.web.api.ProductsApiDelegate;
import com.group.ecommerce.catalog.web.api.model.CreateProductDto;
import com.group.ecommerce.catalog.web.api.model.PageProductsDto;
import com.group.ecommerce.catalog.web.api.model.ProductDto;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService implements ProductsApiDelegate{

    private final ProductInfoRepository productRepository;

    private final ProductInfoMapper productInfoMapper;

    public ProductService(ProductInfoRepository productRepository, ProductInfoMapper productInfoMapper) {
        this.productRepository = productRepository;
        this.productInfoMapper = productInfoMapper;
    }

    @Override
    @Timed(value = "group.ecommerce.product.service")
    public ResponseEntity<ProductDto> addProduct(CreateProductDto newProduct) {
        ProductInfo product = productRepository.save(productInfoMapper.toDomain(newProduct));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productInfoMapper.fromDomain(product));
    }

    @Override
    @Timed(value = "group.ecommerce.product.service")
    public ResponseEntity<Void> deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
        }
        catch (Exception ex) {
            throw new ProductNotFoundException(String.format("Product with id %s could not be found", id));
        }
        return ResponseEntity.ok(null);
    }

    @Override
    @Timed(value = "group.ecommerce.product.service")
    public ResponseEntity<ProductDto> findProductById(Long id) {
        Optional<ProductInfo> product = productRepository.findById(id);

        if (!product.isPresent()) {
            throw new ProductNotFoundException(String.format("Product with id %s could not be found", id));
        }

        return ResponseEntity.ok(productInfoMapper.fromDomain(product.get()));
    }

    @Override
    @Timed(value = "group.ecommerce.product.service")
    public ResponseEntity<PageProductsDto> findProducts(Integer pageNumber, Integer pageSize) throws Exception {
        Pageable pages = PageRequest.of(pageNumber, pageSize, Sort.by("name").descending());

        Page<ProductInfo> page = productRepository.findAll(pages);
        List<ProductDto> items = page
                .getContent().stream()
                .map(productInfoMapper::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageProductsDto()
                .results(items)
                .pageNumber(pageNumber)
                .pageSize(items.size())
                .totalPages(page.getTotalPages()));
    }

}

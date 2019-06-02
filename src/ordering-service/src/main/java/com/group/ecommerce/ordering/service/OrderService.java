package com.group.ecommerce.ordering.service;

import com.group.ecommerce.ordering.domain.OrderInfo;
import com.group.ecommerce.ordering.exception.OrderNotFoundException;
import com.group.ecommerce.ordering.exception.ProductNotFoundException;
import com.group.ecommerce.ordering.mapper.OrderInfoMapper;
import com.group.ecommerce.ordering.repository.OrderInfoRepository;
import com.group.ecommerce.ordering.web.api.OrdersApiDelegate;
import com.group.ecommerce.ordering.web.api.model.CreateOrderDto;
import com.group.ecommerce.ordering.web.api.model.OrderDto;
import com.group.ecommerce.ordering.web.api.model.PageOrdersDto;
import com.group.ecommerce.ordering.web.catalog.model.ProductDto;
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
public class OrderService implements OrdersApiDelegate{

    private final OrderInfoRepository orderRepository;

    private final CatalogService catalogService;

    private final OrderInfoMapper orderInfoMapper;

    public OrderService(OrderInfoRepository orderRepository,
                        OrderInfoMapper orderInfoMapper,
                        CatalogService catalogService) {
        this.orderRepository = orderRepository;
        this.orderInfoMapper = orderInfoMapper;
        this.catalogService = catalogService;
    }

    @Override
    @Timed(value = "group.ecommerce.order.service")
    public ResponseEntity<OrderDto> addOrder(CreateOrderDto newOrder) {
        Optional<ProductDto> product = catalogService.findProductById(newOrder.getProductId());
        if (!product.isPresent()) {
            throw new ProductNotFoundException(String.format("Product with id %s could not be found",
                    newOrder.getProductId()));
        }

        OrderInfo order = orderRepository.save(orderInfoMapper.toDomain(newOrder));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderInfoMapper.fromDomain(order));
    }

    @Override
    @Timed(value = "group.ecommerce.order.service")
    public ResponseEntity<Void> deleteOrder(Long id) {
        try {
            orderRepository.deleteById(id);
        }
        catch (Exception ex) {
            throw new OrderNotFoundException(String.format("Order with id %s could not be found", id));
        }
        return ResponseEntity.ok(null);
    }

    @Override
    @Timed(value = "group.ecommerce.order.service")
    public ResponseEntity<OrderDto> findOrderById(Long id) {
        Optional<OrderInfo> order = orderRepository.findById(id);

        if (!order.isPresent()) {
            throw new OrderNotFoundException(String.format("Order with id %s could not be found", id));
        }

        return ResponseEntity.ok(orderInfoMapper.fromDomain(order.get()));
    }

    @Override
    @Timed(value = "group.ecommerce.order.service")
    public ResponseEntity<PageOrdersDto> findOrders(Integer pageNumber, Integer pageSize) throws Exception {
        Pageable pages = PageRequest.of(pageNumber, pageSize, Sort.by("orderDate")
                .descending().and(Sort.by("reportDate").descending()));

        Page<OrderInfo> page = orderRepository.findAll(pages);
        List<OrderDto> items = page
                .getContent().stream()
                .map(orderInfoMapper::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageOrdersDto()
                .results(items)
                .pageNumber(pageNumber)
                .pageSize(items.size())
                .totalPages(page.getTotalPages()));
    }

    @Override
    @Timed(value = "group.ecommerce.order.service")
    public ResponseEntity<PageOrdersDto> findAllOrdersByClientId(Long id, Integer pageNumber, Integer pageSize) throws Exception {
        Pageable pages = PageRequest.of(pageNumber, pageSize, Sort.by("orderDate")
                .descending().and(Sort.by("reportDate").descending()));

        Page<OrderInfo> page = orderRepository.findAllByClientId(id, pages);
        List<OrderDto> items = page
                .getContent().stream()
                .map(orderInfoMapper::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new PageOrdersDto()
                .results(items)
                .pageNumber(pageNumber)
                .pageSize(items.size())
                .totalPages(page.getTotalPages()));
    }

}

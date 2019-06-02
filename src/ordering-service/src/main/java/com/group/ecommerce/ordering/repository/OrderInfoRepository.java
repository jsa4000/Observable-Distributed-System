package com.group.ecommerce.ordering.repository;

import com.group.ecommerce.ordering.domain.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {

    /**
     * Find all orders by client id
     *
     * @param clientId
     * @param var1
     * @return
     */
    Page<OrderInfo> findAllByClientId(Long clientId, Pageable var1);

}

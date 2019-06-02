package com.group.ecommerce.catalog.repository;

import com.group.ecommerce.catalog.domain.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {
}

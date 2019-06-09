package com.group.ecommerce.catalog.domain;

import com.spring.core.jpa.converter.OffsetDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * A product.
 */
@Entity
@Data
@Builder
@Table(name = "product_info")
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @NotNull
    @Size(max = 512)
    @Column(name = "description", length = 256, nullable = false)
    private String description;

    @NotNull
    @Column(name = "entry_date", nullable = false)
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime entryDate;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Size(max = 256)
    @Column(name = "provider", length = 256, nullable = false)
    private String provider;

}


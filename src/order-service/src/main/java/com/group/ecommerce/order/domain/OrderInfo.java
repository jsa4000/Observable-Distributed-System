package com.group.ecommerce.order.domain;

import com.group.ecommerce.order.converter.OffsetDateTimeConverter;
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
 * A order.
 */
@Entity
@Data
@Builder
@Table(name = "order_info",
        indexes = {
                @Index(name = "idx_order_iid", columnList = "invoice_id"),
                @Index(name = "idx_order_cid", columnList = "client_id") })
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @NotNull
    @Column(name = "order_date", nullable = false)
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime orderDate;

    @NotNull
    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    @Column(name = "total_cost_without_vap")
    private Double totalCostWithoutVap;

    @NotNull
    @Size(max = 256)
    @Column(name = "agency", length = 256, nullable = false)
    private String agency;

    @NotNull
    @Size(max = 256)
    @Column(name = "method", length = 256, nullable = false)
    private String method;

    @NotNull
    @Size(max = 256)
    @Column(name = "provider", length = 256, nullable = false)
    private String provider;

    @Column(name = "report_date")
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime reportDate;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private Long clientId;

}


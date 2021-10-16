package com.example.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("bookings")
@CompoundIndexes({
        @CompoundIndex(name = "resource_active", def = "{'resourceId' : 1, 'active': 1}")
})
public class Booking {

    @Id @NotNull @NotEmpty @Max(64)
    String id;

    @Indexed @NotNull @NotEmpty @Max(64)
    String clientId;

    OffsetDateTime fromDate;

    OffsetDateTime toDate;

    @Max(64)
    String vehicleId;

    @Max(64)
    String flightId;

    @Max(64)
    String hotelId;

    OffsetDateTime createdAt;
    Boolean active;

}

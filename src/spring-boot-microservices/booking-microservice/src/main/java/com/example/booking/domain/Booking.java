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
import javax.validation.constraints.Size;
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

    @Id @NotNull @NotEmpty @Size(max = 64)
    String id;

    @Indexed @NotNull @NotEmpty @Size(max = 64)
    String clientId;

    OffsetDateTime fromDate;

    OffsetDateTime toDate;

    @Size(max = 64)
    String vehicleId;

    @Size(max = 64)
    String flightId;

    @Size(max = 64)
    String hotelId;

    OffsetDateTime createdAt;

    Boolean active;

}

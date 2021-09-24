package com.example.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @Id
    String id;
    String clientId;
    String vehicleId;
    OffsetDateTime fromDate;
    OffsetDateTime createdAt;
    Boolean active;

}

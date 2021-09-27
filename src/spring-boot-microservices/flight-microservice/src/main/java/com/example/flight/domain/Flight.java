package com.example.flight.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("flights")
public class Flight {

    @Id
    private String id;
    private String from;
    private String to;
    private OffsetDateTime fromDate;
    private OffsetDateTime toDate;

}

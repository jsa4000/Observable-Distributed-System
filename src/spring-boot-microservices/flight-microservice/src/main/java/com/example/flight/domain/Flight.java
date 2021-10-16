package com.example.flight.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
@Document("flights")
public class Flight {

    @Id @NotNull @NotEmpty @Max(64)
    private String id;

    @Indexed @NotNull @NotEmpty @Max(Integer.MAX_VALUE)
    private Integer number;

    private String source;
    private String destination;
    private OffsetDateTime departure;
    private OffsetDateTime arrival;
    private Integer seats;

}

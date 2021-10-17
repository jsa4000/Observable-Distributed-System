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
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("flights")
public class Flight {

    @Id @NotNull @NotEmpty @Size(max = 64)
    private String id;

    @Indexed @NotNull @Max(Integer.MAX_VALUE)
    private Integer number;

    @NotNull @NotEmpty @Size(max = 256)
    private String source;

    @NotNull @NotEmpty @Size(max = 256)
    private String destination;

    @NotNull
    private OffsetDateTime departure;

    @NotNull
    private OffsetDateTime arrival;

    @Max(9999)
    private Integer seats;

}

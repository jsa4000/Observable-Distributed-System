package com.example.hotel.domain;

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("hotels")
public class Hotel {

    @Id @NotNull @NotEmpty @Max(64)
    private String id;

    @Indexed @NotNull @NotEmpty @Max(64)
    private String name;
    private String address;
    private String postalCode;
    private String city;
    private String country;
    private Integer rooms;

}

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
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("hotels")
public class Hotel {

    @Id @NotNull @NotEmpty @Size(max = 64)
    private String id;

    @Indexed @NotNull @NotEmpty @Size(max = 256)
    private String name;

    @NotNull @NotEmpty @Size(max = 256)
    private String address;

    @Size(max = 32)
    private String postalCode;

    @NotNull @NotEmpty @Size(max = 256)
    private String city;

    @NotNull @NotEmpty @Size(max = 128)
    private String country;

    @NotNull @Max(99999)
    private Integer rooms;

}

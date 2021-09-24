package com.example.booking.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebClientProperties {

    public static final String DEFAULT_WEB_CLIENT_PROPERTIES = "web.clients.default";

    /**
     * URL
     */
    @Builder.Default
    private String url = "http://localhost:8080";

    /**
     * Trusted
     */
    @Builder.Default
    private boolean trusted = false;

}


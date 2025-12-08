package com.example.api_gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeycloakTokenResponse {
    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private int expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;
}

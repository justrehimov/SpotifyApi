package com.spotify.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("access_token")
    private String accessToken;
}

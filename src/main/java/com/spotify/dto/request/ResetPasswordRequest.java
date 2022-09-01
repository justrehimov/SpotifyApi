package com.spotify.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @JsonProperty("new_password")
    private String newPassword;
    @JsonProperty("confirm_password")
    private String confirmPassword;
}

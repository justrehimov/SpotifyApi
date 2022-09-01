package com.spotify.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordRequest {
    @JsonProperty("new_password")
    @NotNull(message = "New password can't be empty")
    @NotBlank(message = "New password can't be empty")
    @Size(min = 6, message = "Password size can't be less than 6 characters")
    private String newPassword;

    @NotNull(message = "Confirm password can't be empty")
    @NotBlank(message = "Confirm password can't be empty")
    @Size(min = 6, message = "Password size can't be less than 6 characters")
    @JsonProperty("confirm_password")
    private String confirmPassword;
}

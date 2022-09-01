package com.spotify.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserUpdateRequest {
    @NotNull(message = "Email can't be empty")
    @NotBlank(message = "Email can't be empty")
    @Size(min = 6, message = "Email can't be less than 6 characters")
    private String email;

    @NotNull(message = "Username password can't be empty")
    @NotBlank(message = "Username password can't be empty")
    @Size(min = 2, message = "Username size can't be less than 2 characters")
    private String username;

}

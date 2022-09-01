package com.spotify.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String username;
    private String password;
}

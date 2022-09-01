package com.spotify.service;

import com.spotify.dto.request.UserUpdateRequest;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;
import com.spotify.entity.User;

public interface UserService {
    User getByEmail(String email);

    User getByUsername(String username);

    User getById(Long id);

    ResponseModel<UserResponse> update(UserUpdateRequest userRequest, Long id);

    ResponseModel<UserResponse> get(Long id);
}

package com.spotify.service;

import com.spotify.dto.request.LoginRequest;
import com.spotify.dto.request.ResetPasswordRequest;
import com.spotify.dto.request.UserRequest;
import com.spotify.dto.response.LoginResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;

public interface AuthService {
    ResponseModel<UserResponse> signUp(UserRequest userRequest);

    ResponseModel<LoginResponse> login(LoginRequest loginRequest);

    ResponseModel<UserResponse> confirm(String token);

    ResponseModel<UserResponse> forgotPassword(String email);

    ResponseModel<UserResponse> resetPassword(ResetPasswordRequest resetPasswordRequest, String token);
}

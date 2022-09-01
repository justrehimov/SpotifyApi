package com.spotify.controller;

import com.spotify.dto.request.LoginRequest;
import com.spotify.dto.request.ResetPasswordRequest;
import com.spotify.dto.request.UserRequest;
import com.spotify.dto.response.LoginResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;
import com.spotify.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseModel<UserResponse> signUp(@Valid @RequestBody UserRequest userRequest){
        return authService.signUp(userRequest);
    }

    @PostMapping("/login")
    public ResponseModel<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @PostMapping("/confirm/{token}")
    public ResponseModel<UserResponse> confirm(@PathVariable("token") String token){
        return authService.confirm(token);
    }

    @PostMapping("/forgot-password")
    public ResponseModel<UserResponse> forgotPassword(@RequestParam("email") String email){
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password/{token}")
    public ResponseModel<UserResponse> resetPassword(@PathVariable("token") String token,
                                                     @Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        return authService.resetPassword(resetPasswordRequest, token);
    }
}

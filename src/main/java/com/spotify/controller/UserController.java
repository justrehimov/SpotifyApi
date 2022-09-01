package com.spotify.controller;

import com.spotify.dto.request.UserRequest;
import com.spotify.dto.request.UserUpdateRequest;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;
import com.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseModel<UserResponse> update(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        return userService.update(userUpdateRequest, id);
    }

    @GetMapping("/{id}")
    public ResponseModel<UserResponse> get(@PathVariable("id") Long id){
        return userService.get(id);
    }

}

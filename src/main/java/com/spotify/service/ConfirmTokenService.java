package com.spotify.service;

import com.spotify.entity.ConfirmToken;

public interface ConfirmTokenService {
    ConfirmToken getByToken(String token);

    boolean isValidToken(String token);

}

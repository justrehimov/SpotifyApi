package com.spotify.service.impl;

import com.spotify.entity.ConfirmToken;
import com.spotify.exception.SpotifyException;
import com.spotify.exception.StatusMessage;
import com.spotify.repo.ConfirmTokenRepo;
import com.spotify.service.ConfirmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmTokenServiceImpl implements ConfirmTokenService {

    private final ConfirmTokenRepo confirmTokenRepo;

    @Override
    public ConfirmToken getByToken(String token) {
        return confirmTokenRepo.findByToken(token)
                .orElseThrow(()->new SpotifyException(StatusMessage.TOKEN_NOT_FOUND));
    }

    @Override
    public boolean isValidToken(String token) {
        try{
            ConfirmToken confirmToken = getByToken(token);
            if(confirmToken.getCreatedAt().toInstant().plusSeconds(600).isAfter(confirmToken.getExpiredAt().toInstant())){
                return false;
            }
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}

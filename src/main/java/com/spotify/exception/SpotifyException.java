package com.spotify.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SpotifyException extends RuntimeException{
    private String message;

    public SpotifyException(String message){
        super(message);
        this.message = message;
    }
}

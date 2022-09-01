package com.spotify.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseModel<T> {
    private T result;
    private boolean error;
    private String message;
}

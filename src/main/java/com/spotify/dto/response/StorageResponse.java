package com.spotify.dto.response;

import lombok.Data;

@Data
public class StorageResponse {
    private Long id;

    private String fileName;

    private String extension;

    private String url;

    private Long size;
}

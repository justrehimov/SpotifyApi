package com.spotify.dto.response;

import lombok.Data;

@Data
public class MusicResponse {
    private Long id;
    private String name;
    private String artistName;
    private Long storageMusicId;
    private Long storageImageId;
    private Long userId;
}

package com.spotify.dto.request;

import lombok.Data;

@Data
public class MusicRequest {

    private String name;
    private String aristName;
    private Long storageMusicId;
    private Long storageImageId;
    private Long userId;
}

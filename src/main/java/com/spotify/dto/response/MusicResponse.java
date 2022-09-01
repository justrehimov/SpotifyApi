package com.spotify.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MusicResponse {
    private Long id;
    private String name;

    @JsonProperty("artist_name")
    private String artistName;

    @JsonProperty("storage_music_id")
    private Long storageMusicId;

    @JsonProperty("storage_image_id")
    private Long storageImageId;

    @JsonProperty("user_id")
    private Long userId;
}

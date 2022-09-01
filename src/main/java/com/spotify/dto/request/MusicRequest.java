package com.spotify.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MusicRequest {

    @NotNull(message = "Music name can't be empty")
    @NotBlank(message = "Music name can't be empty")
    private String name;

    @NotNull(message = "Artist name can't be empty")
    @NotBlank(message = "Artist name can't be empty")
    @JsonProperty("artist_name")
    private String artistName;

    @NotNull(message = "Music storage can't be empty")
    @JsonProperty("storage_music_id")
    private Long storageMusicId;

    @NotNull(message = "Music image storage can't be empty")
    @JsonProperty("storage_image_id")
    private Long storageImageId;

    @NotNull(message = "Music user can't be empty")
    @JsonProperty("user_id")
    private Long userId;
}

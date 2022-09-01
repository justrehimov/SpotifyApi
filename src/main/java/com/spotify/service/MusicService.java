package com.spotify.service;

import com.spotify.dto.request.MusicRequest;
import com.spotify.dto.response.MusicResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.entity.Music;

import java.util.List;

public interface MusicService {
    ResponseModel<MusicResponse> save(MusicRequest musicRequest);

    Music getById(Long id);
    ResponseModel<MusicResponse> get(Long id);

    ResponseModel<List<MusicResponse>> list(String filter);
}

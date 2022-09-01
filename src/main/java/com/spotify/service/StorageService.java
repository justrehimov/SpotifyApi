package com.spotify.service;

import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.StorageResponse;
import com.spotify.entity.Storage;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface StorageService {
    Storage getById(Long storageMusicId);

    ResponseModel<StorageResponse> upload(MultipartFile multipartFile);

    ResponseModel<StorageResponse> get(Long id);

    ResponseModel<StorageResponse> delete(Long id);

}

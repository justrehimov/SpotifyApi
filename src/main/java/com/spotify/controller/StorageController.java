package com.spotify.controller;

import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.StorageResponse;
import com.spotify.entity.Storage;
import com.spotify.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseModel<StorageResponse> upload(@RequestPart("file") MultipartFile multipartFile){
        return storageService.upload(multipartFile);
    }

    @GetMapping("/{id}")
    public ResponseModel<StorageResponse> get(@PathVariable("id") Long id){
        return storageService.get(id);
    }

    @SneakyThrows
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id){
        Storage storage = storageService.getById(id);
        byte[] bytes = FileUtils.readFileToByteArray(new File(storage.getLocalPath()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storage.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+storage.getFileName()+"\"")
                .body(new ByteArrayResource(bytes));
    }

    @DeleteMapping("/{id}")
    public ResponseModel<StorageResponse> delete(@PathVariable("id") Long id){
        return storageService.delete(id);
    }
}

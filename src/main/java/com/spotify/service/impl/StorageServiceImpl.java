package com.spotify.service.impl;

import com.spotify.dto.response.MusicResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.StorageResponse;
import com.spotify.dto.response.UserResponse;
import com.spotify.entity.ConfirmToken;
import com.spotify.entity.Storage;
import com.spotify.entity.User;
import com.spotify.exception.SpotifyException;
import com.spotify.exception.StatusMessage;
import com.spotify.repo.StorageRepo;
import com.spotify.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final StorageRepo storageRepo;
    private final ModelMapper modelMapper;

    @Value("${file.local.path}")
    private String localPath;

    @Value("${app.domain}")
    private String domain;

    @Override
    public Storage getById(Long id) {
        return storageRepo.findById(id)
                .orElseThrow(()->new SpotifyException(StatusMessage.STORAGE_NOT_FOUND));
    }

    @Override
    @Transactional
    public ResponseModel<StorageResponse> upload(MultipartFile multipartFile) {
        try{
            if(multipartFile.isEmpty()){
                return ResponseModel.<StorageResponse>builder()
                        .message(StatusMessage.FILE_IS_EMPTY)
                        .error(true)
                        .build();
            }
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        Long size = multipartFile.getSize();
        String uniqueFileName = UUID.randomUUID()+"."+extension;
        String localPath = moveFolder(multipartFile, uniqueFileName);
        Storage storage = new Storage();
        storage.setExtension(extension);
        storage.setSize(size);
        storage.setLocalPath(localPath);
        storage.setContentType(multipartFile.getContentType());
        storage.setFileName(uniqueFileName);
        storage.setUniqueName(uniqueFileName);
        Storage savedStorage = storageRepo.save(storage);
        String downloadUrl = domain + "/storage/download/"+savedStorage.getId();
        savedStorage.setUrl(downloadUrl);
        if(savedStorage.getContentType().contains("image")){
            cropImageToSquare(localPath, localPath, extension);
        }
        StorageResponse storageResponse = modelMapper.map(savedStorage, StorageResponse.class);
        return ResponseModel.<StorageResponse>builder()
                .result(storageResponse)
                .message(StatusMessage.SUCCESS)
                .error(false)
                .build();

        }catch (SpotifyException ex){
            log.error("Error ", ex);
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            log.error("Error ", ex);
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            log.error("Error ", ex);
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }


    @Override
    public ResponseModel<StorageResponse> get(Long id){
        try{

            Storage storage = getById(id);

            StorageResponse storageResponse = modelMapper.map(storage, StorageResponse.class);
            return ResponseModel.<StorageResponse>builder()
                    .result(storageResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();

        }catch (SpotifyException ex){
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseModel<StorageResponse> delete(Long id) {
        try{

            Storage storage = getById(id);

            StorageResponse storageResponse = modelMapper.map(storage, StorageResponse.class);

            File file = new File(storage.getLocalPath());
            if(file.exists()){
                file.delete();
            }
            storageRepo.delete(storage);
            return ResponseModel.<StorageResponse>builder()
                    .result(storageResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();

        }catch (SpotifyException ex){
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<StorageResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    private String moveFolder(MultipartFile multipartFile, String uniqueName) throws IOException {
        byte[] bytes = multipartFile.getBytes();
        File folder = new File("src/main/resources" + localPath);
        if(!folder.exists()){
            folder.mkdir();
        }
        File file = new File("src/main/resources"+localPath+"/" + uniqueName);
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
        return "src/main/resources"+localPath+"/" + uniqueName;
    }


    public static void cropImageToSquare(String originalFilePath, String scopedFilePath, String extension) {
        try {
            File sourceFile = new File(originalFilePath);
            BufferedImage originalImage = ImageIO.read(sourceFile);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            int x, y, cropWidth, cropHeight;
            if (width > height) {
                x = (width - height) / 2;
                y = 0;
                cropWidth = cropHeight = height;
            } else {
                x = 0;
                y = (height - width) / 2;
                cropWidth = cropHeight = width;
            }

            BufferedImage resizedImage = Scalr.crop(originalImage, x, y, cropWidth, cropHeight);

            File resizedFile = new File(scopedFilePath);
            ImageIO.write(resizedImage, extension, resizedFile);

            originalImage.flush();
            resizedImage.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

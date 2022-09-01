package com.spotify.service.impl;

import com.spotify.dao.MusicDao;
import com.spotify.dto.request.MusicRequest;
import com.spotify.dto.response.MusicResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.dto.response.UserResponse;
import com.spotify.entity.ConfirmToken;
import com.spotify.entity.Music;
import com.spotify.entity.Storage;
import com.spotify.entity.User;
import com.spotify.exception.SpotifyException;
import com.spotify.exception.StatusMessage;
import com.spotify.repo.MusicRepo;
import com.spotify.service.MusicService;
import com.spotify.service.StorageService;
import com.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicServiceImpl implements MusicService {
    private final UserService userService;
    private final MusicRepo musicRepo;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    private final MusicDao musicDao;
    @Override
    @Transactional
    public ResponseModel<MusicResponse> save(MusicRequest musicRequest) {
        try{
            Music music = modelMapper.map(musicRequest, Music.class);
            Storage storageMusic = storageService.getById(musicRequest.getStorageMusicId());
            Storage storageImage = storageService.getById(musicRequest.getStorageImageId());
            User user = userService.getById(musicRequest.getUserId());
            music.setMusic(storageMusic);
            music.setImage(storageImage);
            music.setUser(user);
            Music savedMusic = musicRepo.save(music);
            MusicResponse musicResponse = modelMapper.map(savedMusic, MusicResponse.class);
            musicResponse.setStorageMusicId(storageMusic.getId());
            musicResponse.setStorageImageId(storageImage.getId());
            musicResponse.setUserId(user.getId());

            return ResponseModel.<MusicResponse>builder()
                    .result(musicResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();
        }catch (SpotifyException ex){
            log.error("Error ", ex);
            return ResponseModel.<MusicResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            log.error("Error ", ex);
            return ResponseModel.<MusicResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            log.error("Error ", ex);
            return ResponseModel.<MusicResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    @Override
    public Music getById(Long id) {
        return musicRepo.findById(id)
                .orElseThrow(()->new SpotifyException(StatusMessage.MUSIC_NOT_FOUND));
    }

    @Override
    public ResponseModel<MusicResponse> get(Long id) {
        try{
            Music music = getById(id);
            MusicResponse musicResponse = modelMapper.map(music, MusicResponse.class);
            musicResponse.setStorageMusicId(music.getMusic().getId());
            musicResponse.setStorageImageId(music.getImage().getId());
            musicResponse.setUserId(music.getUser().getId());

            return ResponseModel.<MusicResponse>builder()
                    .result(musicResponse)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();
        }catch (SpotifyException ex){
            return ResponseModel.<MusicResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<MusicResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<MusicResponse>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }

    @Override
    public ResponseModel<List<MusicResponse>> list(String filter) {
        try{
            List<Music> musicList = musicDao.list(filter);
            List<MusicResponse> musicResponseList = musicList.stream().map(music->{
                MusicResponse musicResponse = modelMapper.map(music, MusicResponse.class);
                musicResponse.setStorageMusicId(music.getMusic().getId());
                musicResponse.setStorageImageId(music.getImage().getId());
                musicResponse.setUserId(music.getUser().getId());
                return musicResponse;
            }).collect(Collectors.toList());

            if(musicList.isEmpty()){
                return ResponseModel.<List<MusicResponse>>builder()
                        .result(new ArrayList<>())
                        .message(StatusMessage.MUSIC_NOT_FOUND)
                        .error(false)
                        .build();
            }

            return ResponseModel.<List<MusicResponse>>builder()
                    .result(musicResponseList)
                    .message(StatusMessage.SUCCESS)
                    .error(false)
                    .build();
        }catch (SpotifyException ex){
            return ResponseModel.<List<MusicResponse>>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (RuntimeException ex){
            return ResponseModel.<List<MusicResponse>>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }catch (Exception ex){
            return ResponseModel.<List<MusicResponse>>builder()
                    .message(ex.getMessage())
                    .error(true)
                    .build();
        }
    }
}

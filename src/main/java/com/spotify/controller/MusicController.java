package com.spotify.controller;

import com.spotify.dto.request.MusicRequest;
import com.spotify.dto.response.MusicResponse;
import com.spotify.dto.response.ResponseModel;
import com.spotify.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/music")
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    public ResponseModel<MusicResponse> save(@Valid @RequestBody MusicRequest musicRequest){
        return musicService.save(musicRequest);
    }

    @GetMapping("/{id}")
    public ResponseModel<MusicResponse> get(@PathVariable("id") Long id){
        return musicService.get(id);
    }

    @GetMapping("/list")
    public ResponseModel<List<MusicResponse>> list(@RequestParam(value = "filter", required = false) String filter){
        return musicService.list(filter);
    }


}

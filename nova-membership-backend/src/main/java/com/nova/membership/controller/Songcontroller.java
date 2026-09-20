package com.nova.membership.controller;

import com.nova.membership.dto.ApiResponse;
import com.nova.membership.dto.SongResponse;
import com.nova.membership.entity.Song;
import com.nova.membership.service.SongService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
public class SongController {

    private final SongService service;

    public SongController(SongService service) {
        this.service = service;
    }

    /** Public: song list, optionally for one playlist (?category=calm). */
    @GetMapping("/api/songs")
    public ApiResponse list(@RequestParam(required = false) String category) {
        return ApiResponse.ok("Songs.", service.list(category));
    }

    /**
     * Public: streams the audio. Returning a Resource lets Spring answer "Range" requests
     * automatically (HTTP 206), which is what the browser's <audio> element needs for seeking.
     */
    @GetMapping("/api/songs/{id}/stream")
    public ResponseEntity<Resource> stream(@PathVariable Long id) {
        Song song = service.getSong(id);
        byte[] data = service.getAudio(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(song.getContentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic())
                .body(new ByteArrayResource(data));
    }

    /** Admin only (see SecurityConfig): upload one song. */
    @PostMapping(value = "/api/admin/songs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> upload(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam String category,
            @RequestParam("file") MultipartFile file) {
        SongResponse saved = service.upload(title, artist, category, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Song uploaded.", saved));
    }

    /** Admin only: delete a song. */
    @DeleteMapping("/api/admin/songs/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok("Song deleted.", null);
    }
}
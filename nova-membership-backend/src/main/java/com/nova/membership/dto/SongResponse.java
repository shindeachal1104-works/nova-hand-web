package com.nova.membership.dto;

import com.nova.membership.entity.Song;

public record SongResponse(Long id, String title, String artist, String category,
                           long sizeBytes, String contentType, String streamUrl) {

    public static SongResponse from(Song s) {
        return new SongResponse(s.getId(), s.getTitle(), s.getArtist(), s.getCategory(),
                s.getSizeBytes(), s.getContentType(), "/api/songs/" + s.getId() + "/stream");
    }
}
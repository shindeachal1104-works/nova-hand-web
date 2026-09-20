package com.nova.membership.entity;

import jakarta.persistence.*;

/** The raw audio file of a song, stored in the database (table song_audio, LONGBLOB). */
@Entity
@Table(name = "song_audio")
public class SongAudio {

    /** Same value as songs.id (one audio row per song). */
    @Id
    @Column(name = "song_id")
    private Long songId;

    @Lob
    @Column(name = "data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] data;

    protected SongAudio() {}

    public SongAudio(Long songId, byte[] data) {
        this.songId = songId;
        this.data = data;
    }

    public Long getSongId() { return songId; }
    public byte[] getData() { return data; }
}
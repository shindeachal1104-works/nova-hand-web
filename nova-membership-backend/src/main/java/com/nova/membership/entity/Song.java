package com.nova.membership.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Song metadata. The audio bytes live in {@link SongAudio} (separate table) so that listing
 * songs never loads the (large) audio data.
 */
@Entity
@Table(name = "songs",
        uniqueConstraints = @UniqueConstraint(name = "uk_song_title_artist", columnNames = {"title", "artist"}))
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String artist;

    /** Playlist key used by the frontend, e.g. "calm" or "rap". */
    @Column(nullable = false, length = 40)
    private String category;

    @Column(name = "content_type", nullable = false, length = 60)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getArtist() { return artist; }
    public void setArtist(String v) { this.artist = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getContentType() { return contentType; }
    public void setContentType(String v) { this.contentType = v; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long v) { this.sizeBytes = v; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String v) { this.originalFilename = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
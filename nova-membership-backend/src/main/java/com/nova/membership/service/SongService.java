package com.nova.membership.service;

import com.nova.membership.dto.SongResponse;
import com.nova.membership.entity.Song;
import com.nova.membership.entity.SongAudio;
import com.nova.membership.repository.SongAudioRepository;
import com.nova.membership.repository.SongRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Stores songs (metadata + audio bytes) in the database and serves them back. */
@Service
public class SongService {

    private static final long MAX_BYTES = 20L * 1024 * 1024;

    /** Allowed file extension -> MIME type we serve it with (we do not trust the browser's type). */
    private static final Map<String, String> TYPES = Map.of(
            "mp3", "audio/mpeg",
            "m4a", "audio/mp4",
            "aac", "audio/aac",
            "ogg", "audio/ogg",
            "wav", "audio/wav",
            "webm", "audio/webm");

    private final SongRepository songs;
    private final SongAudioRepository audio;

    public SongService(SongRepository songs, SongAudioRepository audio) {
        this.songs = songs;
        this.audio = audio;
    }

    public List<SongResponse> list(String category) {
        List<Song> found = (category == null || category.isBlank())
                ? songs.findAllByOrderByCategoryAscTitleAsc()
                : songs.findByCategoryIgnoreCaseOrderByTitleAsc(category.trim());
        return found.stream().map(SongResponse::from).toList();
    }

    @Transactional
    public SongResponse upload(String title, String artist, String category, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Choose an audio file to upload.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Song file must be 20 MB or smaller.");
        }

        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        String mime = ext == null ? null : TYPES.get(ext.toLowerCase(Locale.ROOT));
        if (mime == null) {
            throw new IllegalArgumentException("Only MP3, M4A, AAC, OGG, WAV or WEBM audio files are allowed.");
        }

        String cat = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);
        if (!cat.matches("^[a-z0-9-]{2,40}$")) {
            throw new IllegalArgumentException("Category must be 2-40 letters, digits or dashes (for example: calm, rap).");
        }

        String cleanTitle = clean(title);
        if (cleanTitle.isEmpty()) {
            cleanTitle = titleFromFilename(original);
        }
        if (cleanTitle.isEmpty() || cleanTitle.length() > 200) {
            throw new IllegalArgumentException("Song title is required (max 200 characters).");
        }
        String cleanArtist = clean(artist);
        if (cleanArtist.isEmpty()) {
            cleanArtist = "Unknown artist";
        }
        if (cleanArtist.length() > 200) {
            throw new IllegalArgumentException("Artist must be 200 characters or fewer.");
        }

        if (songs.existsByTitleIgnoreCaseAndArtistIgnoreCase(cleanTitle, cleanArtist)) {
            throw new IllegalArgumentException("\"" + cleanTitle + "\" by " + cleanArtist + " is already uploaded.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read the uploaded file.", e);
        }

        Song s = new Song();
        s.setTitle(cleanTitle);
        s.setArtist(cleanArtist);
        s.setCategory(cat);
        s.setContentType(mime);
        s.setSizeBytes(bytes.length);
        s.setOriginalFilename(original != null && original.length() > 255 ? original.substring(0, 255) : original);
        Song saved = songs.save(s);

        audio.save(new SongAudio(saved.getId(), bytes));
        return SongResponse.from(saved);
    }

    public Song getSong(Long id) {
        return songs.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found."));
    }

    public byte[] getAudio(Long id) {
        return audio.findById(id)
                .map(SongAudio::getData)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song audio not found."));
    }

    @Transactional
    public void delete(Long id) {
        if (!songs.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found.");
        }
        audio.deleteById(id);
        songs.deleteById(id);
    }

    private static String clean(String v) {
        return v == null ? "" : v.trim().replaceAll("\\s+", " ");
    }

    /** "Tum_Hi_Ho.mp3" -> "Tum Hi Ho" */
    private static String titleFromFilename(String filename) {
        if (filename == null) return "";
        String base = StringUtils.stripFilenameExtension(filename);
        return clean(base.replace('_', ' '));
    }
}
package com.nova.membership.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    public FileStorageService(@Value("${app.upload-dir:uploads/profile-photos}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** Returns the public URL path (e.g. /uploads/profile-photos/abc.jpg) or null if no photo was sent. */
    public String storeProfilePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Profile photo must be 5 MB or smaller.");
        }
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String type = file.getContentType();
        if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase())
                || type == null || !ALLOWED_TYPES.contains(type.toLowerCase())) {
            throw new IllegalArgumentException("Only JPG, PNG and WEBP profile photos are allowed.");
        }
        try (InputStream in = file.getInputStream()) {
            Files.createDirectories(root);
            String filename = UUID.randomUUID() + "." + ext.toLowerCase();
            Files.copy(in, root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/profile-photos/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("Could not save profile photo.", e);
        }
    }
}

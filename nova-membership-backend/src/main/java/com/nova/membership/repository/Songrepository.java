package com.nova.membership.repository;

import com.nova.membership.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findAllByOrderByCategoryAscTitleAsc();
    List<Song> findByCategoryIgnoreCaseOrderByTitleAsc(String category);
    boolean existsByTitleIgnoreCaseAndArtistIgnoreCase(String title, String artist);
}
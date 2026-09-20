package com.nova.membership.repository;

import com.nova.membership.entity.SongAudio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongAudioRepository extends JpaRepository<SongAudio, Long> {
}
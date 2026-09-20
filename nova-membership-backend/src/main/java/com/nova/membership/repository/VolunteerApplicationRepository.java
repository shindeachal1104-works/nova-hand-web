package com.nova.membership.repository;

import com.nova.membership.entity.VolunteerApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication, Long> {
    Optional<VolunteerApplication> findByMobile(String mobile);
    List<VolunteerApplication> findAllByOrderByCreatedAtDesc();
    List<VolunteerApplication> findByStatusOrderByCreatedAtDesc(String status);
}
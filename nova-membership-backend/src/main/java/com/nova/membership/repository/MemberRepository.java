package com.nova.membership.repository;

import com.nova.membership.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<Member> findByEmailIgnoreCase(String email);
    Optional<Member> findByMobileNumber(String mobileNumber);
}

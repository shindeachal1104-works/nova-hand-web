package com.nova.membership.dto;

import com.nova.membership.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberResponse(
        Long id, String fullName, LocalDate dateOfBirth, String mobileNumber,
        String email, String city, String schoolCollege, String className,
        String stream, String board, String areasOfInterest, String gender,
        String district, String profilePhotoUrl, String role, String status,
        LocalDateTime createdAt) {

    public static MemberResponse from(Member m) {
        return new MemberResponse(m.getId(), m.getFullName(), m.getDateOfBirth(),
                m.getMobileNumber(), m.getEmail(), m.getCity(), m.getSchoolCollege(),
                m.getClassName(), m.getStream(), m.getBoard(), m.getAreasOfInterest(),
                m.getGender(), m.getDistrict(), m.getProfilePhotoUrl(),
                m.getRole(), m.getStatus(), m.getCreatedAt());
    }
}

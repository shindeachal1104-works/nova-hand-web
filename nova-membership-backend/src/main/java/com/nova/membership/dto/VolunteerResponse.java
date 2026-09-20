package com.nova.membership.dto;

import com.nova.membership.entity.VolunteerApplication;

import java.time.LocalDateTime;

/** Full record - only ever returned to admins. */
public record VolunteerResponse(Long id, String name, String mobile, Integer age, String city,
                                String occupation, String interests, String reason,
                                String availability, String volunteerType, String source,
                                String referredBy, String status, LocalDateTime createdAt) {

    public static VolunteerResponse from(VolunteerApplication v) {
        return new VolunteerResponse(v.getId(), v.getName(), v.getMobile(), v.getAge(), v.getCity(),
                v.getOccupation(), v.getInterests(), v.getReason(), v.getAvailability(),
                v.getVolunteerType(), v.getSource(), v.getReferredBy(), v.getStatus(), v.getCreatedAt());
    }
}
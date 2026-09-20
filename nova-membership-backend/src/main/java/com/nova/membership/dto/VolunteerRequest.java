package com.nova.membership.dto;

import jakarta.validation.constraints.*;

/**
 * Public volunteer application. Only name + mobile are mandatory here because the
 * "share with 5 friends" flow sends just those two; the full form is validated in the frontend.
 */
public record VolunteerRequest(
        @NotBlank(message = "Full name is required.")
        @Size(min = 2, max = 120, message = "Name must be 2 to 120 characters.")
        String name,

        @NotBlank(message = "Mobile number is required.")
        @Pattern(regexp = "^\\+?[0-9 ()\\-]{10,20}$", message = "Enter a valid mobile number.")
        String mobile,

        @Min(value = 10, message = "Enter a valid age.")
        @Max(value = 100, message = "Enter a valid age.")
        Integer age,

        @Size(max = 100, message = "City must be 100 characters or fewer.") String city,
        @Size(max = 200, message = "Education / occupation must be 200 characters or fewer.") String occupation,
        @Size(max = 500, message = "Interests must be 500 characters or fewer.") String interests,
        @Size(max = 1000, message = "Reason must be 1000 characters or fewer.") String reason,
        @Size(max = 80, message = "Availability must be 80 characters or fewer.") String availability,
        @Size(max = 80, message = "Volunteer type must be 80 characters or fewer.") String volunteerType,
        @Size(max = 40, message = "Source must be 40 characters or fewer.") String source,
        @Size(max = 20, message = "Referral code must be 20 characters or fewer.") String referredBy) {
}
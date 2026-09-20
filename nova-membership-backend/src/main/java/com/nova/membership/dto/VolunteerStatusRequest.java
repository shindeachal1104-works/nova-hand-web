package com.nova.membership.dto;

import jakarta.validation.constraints.NotBlank;

public record VolunteerStatusRequest(
        @NotBlank(message = "Status is required.") String status) {
}
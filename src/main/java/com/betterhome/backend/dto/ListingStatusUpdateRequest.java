package com.betterhome.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ListingStatusUpdateRequest(
        @NotBlank(message = "Status is required.")
        String status,
        String rejectionReason
) {
}

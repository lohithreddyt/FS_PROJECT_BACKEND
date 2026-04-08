package com.betterhome.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ListingRequest(
        @NotBlank(message = "Location is required.")
        String location,
        @NotBlank(message = "Property type is required.")
        String type,
        @NotBlank(message = "Area is required.")
        String area,
        @NotBlank(message = "Budget is required.")
        String budget,
        String age,
        @Size(max = 500, message = "Concerns must be 500 characters or fewer.")
        String concerns,
        String image
) {
}

package com.betterhome.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RecommendationRequest(
        @NotBlank(message = "Category is required.")
        String category,
        @NotBlank(message = "Title is required.")
        String title,
        @NotBlank(message = "Description is required.")
        String description,
        @NotBlank(message = "ROI is required.")
        String roi,
        @NotBlank(message = "Cost is required.")
        String cost,
        String actionText
) {
}

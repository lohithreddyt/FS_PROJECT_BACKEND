package com.betterhome.backend.dto;

public record RecommendationResponse(
        Long id,
        String category,
        String title,
        String description,
        String roi,
        String cost,
        String actionText
) {
}

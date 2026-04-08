package com.betterhome.backend.dto;

import java.util.List;

public record AnalyticsResponse(
        long totalListings,
        long pendingListings,
        long activeListings,
        long rejectedListings,
        long totalRecommendations,
        List<AnalyticsItem> topCities,
        List<AnalyticsItem> topCategories
) {
    public record AnalyticsItem(String label, long value) {
    }
}

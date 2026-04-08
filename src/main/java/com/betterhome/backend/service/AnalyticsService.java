package com.betterhome.backend.service;

import com.betterhome.backend.dto.AnalyticsResponse;
import com.betterhome.backend.model.ListingStatus;
import com.betterhome.backend.model.PropertyListing;
import com.betterhome.backend.repository.PropertyListingRepository;
import com.betterhome.backend.repository.RecommendationRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final PropertyListingRepository listingRepository;
    private final RecommendationRepository recommendationRepository;
    private final SessionService sessionService;

    public AnalyticsService(
            PropertyListingRepository listingRepository,
            RecommendationRepository recommendationRepository,
            SessionService sessionService
    ) {
        this.listingRepository = listingRepository;
        this.recommendationRepository = recommendationRepository;
        this.sessionService = sessionService;
    }

    public AnalyticsResponse getAnalytics(HttpSession session) {
        sessionService.requireAdmin(session);
        List<PropertyListing> listings = listingRepository.findAll();

        return new AnalyticsResponse(
                listings.size(),
                listings.stream().filter(l -> l.getStatus() == ListingStatus.PENDING).count(),
                listings.stream().filter(l -> l.getStatus() == ListingStatus.ACTIVE).count(),
                listings.stream().filter(l -> l.getStatus() == ListingStatus.REJECTED).count(),
                recommendationRepository.count(),
                topItems(listings, PropertyListing::getLocation),
                topItems(listings, PropertyListing::getType)
        );
    }

    private List<AnalyticsResponse.AnalyticsItem> topItems(List<PropertyListing> listings, Function<PropertyListing, String> mapper) {
        Map<String, Long> counts = listings.stream()
                .map(mapper)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> new AnalyticsResponse.AnalyticsItem(entry.getKey(), entry.getValue()))
                .toList();
    }
}

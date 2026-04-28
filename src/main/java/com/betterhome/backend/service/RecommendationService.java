package com.betterhome.backend.service;

import com.betterhome.backend.dto.RecommendationRequest;
import com.betterhome.backend.dto.RecommendationResponse;
import com.betterhome.backend.model.Recommendation;
import com.betterhome.backend.repository.RecommendationRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SessionService sessionService;

    public RecommendationService(RecommendationRepository recommendationRepository, SessionService sessionService) {
        this.recommendationRepository = recommendationRepository;
        this.sessionService = sessionService;
    }

    public List<RecommendationResponse> getRecommendations() {
        return recommendationRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RecommendationResponse createRecommendation(RecommendationRequest request, HttpSession session) {
        sessionService.requireAdmin(session);
        Recommendation recommendation = recommendationRepository.save(new Recommendation(
                request.category().trim(),
                request.title().trim(),
                request.description().trim(),
                request.roi().trim(),
                request.cost().trim(),
                request.actionText() == null ? "" : request.actionText().trim()
        ));
        return toResponse(recommendation);
    }

    public void deleteRecommendation(Long id, HttpSession session) {
        sessionService.requireAdmin(session);
        recommendationRepository.deleteById(id);
    }

    private RecommendationResponse toResponse(Recommendation recommendation) {
        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getCategory(),
                recommendation.getTitle(),
                recommendation.getDescription(),
                recommendation.getRoi(),
                recommendation.getCost(),
                recommendation.getActionText()
        );
    }
}

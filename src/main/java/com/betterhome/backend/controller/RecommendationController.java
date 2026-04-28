package com.betterhome.backend.controller;

import com.betterhome.backend.dto.RecommendationRequest;
import com.betterhome.backend.dto.RecommendationResponse;
import com.betterhome.backend.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<RecommendationResponse> getRecommendations() {
        return recommendationService.getRecommendations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecommendationResponse createRecommendation(@Valid @RequestBody RecommendationRequest request, HttpSession session) {
        return recommendationService.createRecommendation(request, session);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecommendation(@PathVariable Long id, HttpSession session) {
        recommendationService.deleteRecommendation(id, session);
    }
}

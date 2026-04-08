package com.betterhome.backend.controller;

import com.betterhome.backend.dto.AnalyticsResponse;
import com.betterhome.backend.service.AnalyticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public AnalyticsResponse getAnalytics(HttpSession session) {
        return analyticsService.getAnalytics(session);
    }
}

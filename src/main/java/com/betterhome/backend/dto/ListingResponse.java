package com.betterhome.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record ListingResponse(
        Long id,
        String ownerName,
        String ownerEmail,
        String location,
        String type,
        String area,
        String budget,
        String age,
        String concerns,
        String image,
        Integer score,
        String status,
        LocalDate submittedAt,
        String rejectionReason,
        List<String> recommendations
) {
}

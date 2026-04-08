package com.betterhome.backend.service;

import com.betterhome.backend.dto.ListingRequest;
import com.betterhome.backend.dto.ListingResponse;
import com.betterhome.backend.dto.ListingStatusUpdateRequest;
import com.betterhome.backend.exception.AppException;
import com.betterhome.backend.model.AppUser;
import com.betterhome.backend.model.ListingStatus;
import com.betterhome.backend.model.PropertyListing;
import com.betterhome.backend.model.Recommendation;
import com.betterhome.backend.model.Role;
import com.betterhome.backend.repository.PropertyListingRepository;
import com.betterhome.backend.repository.RecommendationRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ListingService {

    private final PropertyListingRepository listingRepository;
    private final RecommendationRepository recommendationRepository;
    private final SessionService sessionService;

    public ListingService(
            PropertyListingRepository listingRepository,
            RecommendationRepository recommendationRepository,
            SessionService sessionService
    ) {
        this.listingRepository = listingRepository;
        this.recommendationRepository = recommendationRepository;
        this.sessionService = sessionService;
    }

    public List<ListingResponse> getListings(HttpSession session) {
        AppUser user = sessionService.requireUser(session);
        List<PropertyListing> listings = user.getRole() == Role.ADMIN
                ? listingRepository.findAll().stream().sorted(Comparator.comparing(PropertyListing::getSubmittedAt).reversed()).toList()
                : listingRepository.findByOwnerIdOrderBySubmittedAtDesc(user.getId());
        return listings.stream().map(this::toResponse).toList();
    }

    public ListingResponse createListing(ListingRequest request, HttpSession session) {
        AppUser user = sessionService.requireUser(session);
        if (user.getRole() != Role.USER) {
            throw new AppException(HttpStatus.FORBIDDEN, "Only homeowners can submit properties.");
        }

        PropertyListing listing = new PropertyListing(
                user,
                user.getName(),
                request.location().trim(),
                request.type().trim(),
                normalizeArea(request.area()),
                request.budget().trim(),
                blankToNull(request.age()),
                blankToNull(request.concerns()),
                blankToNull(request.image()),
                ThreadLocalRandom.current().nextInt(45, 91),
                ListingStatus.PENDING,
                LocalDate.now(),
                null
        );
        listing.setRecommendations(recommendationRepository.findAll().stream().limit(3).map(Recommendation::getTitle).toList());

        return toResponse(listingRepository.save(listing));
    }

    public ListingResponse updateStatus(Long id, ListingStatusUpdateRequest request, HttpSession session) {
        sessionService.requireAdmin(session);
        PropertyListing listing = listingRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Listing not found."));

        ListingStatus nextStatus = parseStatus(request.status());
        if (nextStatus == ListingStatus.REJECTED && blankToNull(request.rejectionReason()) == null) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Rejection reason is required.");
        }

        listing.setStatus(nextStatus);
        listing.setRejectionReason(nextStatus == ListingStatus.REJECTED ? request.rejectionReason().trim() : null);
        return toResponse(listingRepository.save(listing));
    }

    public void deleteListing(Long id, HttpSession session) {
        sessionService.requireAdmin(session);
        if (!listingRepository.existsById(id)) {
            throw new AppException(HttpStatus.NOT_FOUND, "Listing not found.");
        }
        listingRepository.deleteById(id);
    }

    private ListingStatus parseStatus(String raw) {
        try {
            return ListingStatus.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid listing status.");
        }
    }

    private String normalizeArea(String area) {
        String trimmed = area.trim();
        return trimmed.toLowerCase(Locale.ROOT).contains("sqft") ? trimmed : trimmed + " sqft";
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private ListingResponse toResponse(PropertyListing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getOwnerName(),
                listing.getOwner().getEmail(),
                listing.getLocation(),
                listing.getType(),
                listing.getArea(),
                listing.getBudget(),
                listing.getAge(),
                listing.getConcerns(),
                listing.getImage(),
                listing.getScore(),
                listing.getStatus().name().toLowerCase(),
                listing.getSubmittedAt(),
                listing.getRejectionReason(),
                listing.getRecommendations()
        );
    }
}

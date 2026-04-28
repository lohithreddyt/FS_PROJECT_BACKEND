package com.betterhome.backend.controller;

import com.betterhome.backend.dto.ListingRequest;
import com.betterhome.backend.dto.ListingResponse;
import com.betterhome.backend.dto.ListingStatusUpdateRequest;
import com.betterhome.backend.service.ListingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public List<ListingResponse> getListings(HttpSession session) {
        return listingService.getListings(session);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(@Valid @RequestBody ListingRequest request, HttpSession session) {
        return listingService.createListing(request, session);
    }

    @PatchMapping("/{id}/status")
    public ListingResponse updateStatus(@PathVariable Long id, @Valid @RequestBody ListingStatusUpdateRequest request, HttpSession session) {
        return listingService.updateStatus(id, request, session);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteListing(@PathVariable Long id, HttpSession session) {
        listingService.deleteListing(id, session);
    }
}

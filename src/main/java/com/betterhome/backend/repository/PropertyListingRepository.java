package com.betterhome.backend.repository;

import com.betterhome.backend.model.PropertyListing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyListingRepository extends JpaRepository<PropertyListing, Long> {
    List<PropertyListing> findByOwnerIdOrderBySubmittedAtDesc(Long ownerId);
}

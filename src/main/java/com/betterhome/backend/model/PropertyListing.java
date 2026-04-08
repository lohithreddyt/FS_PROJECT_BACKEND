package com.betterhome.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "property_listing")
public class PropertyListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FIX: prevent 500 error (lazy loading + JSON issue)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private AppUser owner;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String budget;

    private String age;

    @Column(length = 500)
    private String concerns;

    // ✅ MySQL compatible (LONGTEXT)
    @Lob
    private String image;

    @Column(nullable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status;

    @Column(nullable = false)
    private LocalDate submittedAt;

    private String rejectionReason;

    @ElementCollection
    @CollectionTable(
            name = "listing_recommendations",
            joinColumns = @JoinColumn(name = "listing_id")
    )
    @Column(name = "recommendation_title")
    private List<String> recommendations = new ArrayList<>();

    // ✅ Default constructor
    protected PropertyListing() {}

    // ✅ Constructor
    public PropertyListing(
            AppUser owner,
            String ownerName,
            String location,
            String type,
            String area,
            String budget,
            String age,
            String concerns,
            String image,
            Integer score,
            ListingStatus status,
            LocalDate submittedAt,
            String rejectionReason
    ) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.location = location;
        this.type = type;
        this.area = area;
        this.budget = budget;
        this.age = age;
        this.concerns = concerns;
        this.image = image;
        this.score = score;
        this.status = status;
        this.submittedAt = submittedAt;
        this.rejectionReason = rejectionReason;
    }

    // ✅ Getters
    public Long getId() { return id; }
    public AppUser getOwner() { return owner; }
    public String getOwnerName() { return ownerName; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getArea() { return area; }
    public String getBudget() { return budget; }
    public String getAge() { return age; }
    public String getConcerns() { return concerns; }
    public String getImage() { return image; }
    public Integer getScore() { return score; }
    public ListingStatus getStatus() { return status; }
    public LocalDate getSubmittedAt() { return submittedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public List<String> getRecommendations() { return recommendations; }

    // ✅ Setters
    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}	
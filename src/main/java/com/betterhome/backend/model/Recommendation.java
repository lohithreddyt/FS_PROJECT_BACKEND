package com.betterhome.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String roi;

    @Column(nullable = false)
    private String cost;

    @Column(length = 1000)
    private String actionText;

    protected Recommendation() {
    }

    public Recommendation(String category, String title, String description, String roi, String cost, String actionText) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.roi = roi;
        this.cost = cost;
        this.actionText = actionText;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRoi() {
        return roi;
    }

    public String getCost() {
        return cost;
    }

    public String getActionText() {
        return actionText;
    }
}
